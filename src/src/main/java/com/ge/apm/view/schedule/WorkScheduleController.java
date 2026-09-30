package com.ge.apm.view.schedule;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.UserScheduleDto;
import com.ge.apm.service.schedule.UserScheduleService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.SharedAssetConfigController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.SetUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 212605082 on 2018/5/8.
 */
@ManagedBean
@ViewScoped
public class WorkScheduleController{

    private UserScheduleRepository dao;
    private OrgInfoRepository orgInfoDao;
    private TenantInfoRepository tenantInfoDao;
    private UserAccountRepository userAccountDao;
    private UserScheduleStageConfigRepository userScheduleStageConfigDao;
    private UserAccount currentUser;

    private UserScheduleService userScheduleService;

    private ScheduleModel eventModel;
    private ScheduleEvent event;

    private String userScheduleID;
    private String userUID;
    private String scheduleDateStage;
    private Date scheduleDate;
    private List<UserAccount> userList;
    private boolean isDisableDelete;
    private boolean isDisableSave;
    private boolean isDisableScheduleDateStage;
    private boolean isDisableScheduleDate;

    private Date startBatchDate;
    private Date endBatchDate;
    private Date deleteStartBatchDate;
    private Date deleteEndBatchDate;
    private List<UserAccount> selectedUserList;
    private List<UserAccount> deleteSelectedUserList;
    private String batchType;
    private String[] selectedOptions2;
    private String[] shiftType;
    private Date startTime_all;
    private Date endTime_all;
    private Date startTime_am;
    private Date endTime_am;
    private Date startTime_pm;
    private Date endTime_pm;
    private UserSchedule.Type type;
    private String resultInfo;
    private OrgInfo selectSite;
    private List<OrgInfo> selectOrgInfo;
    private UserContextService userContextService;
    private Boolean isEdited=Boolean.FALSE;
    private List<UserAccount> editUserList;
    private SharedAssetConfigController sharedAssetConfigController;
    private List<UserAccount> tenantUserList;
    @PostConstruct
    protected void init() {

        dao = WebUtil.getBean(UserScheduleRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        userScheduleStageConfigDao = WebUtil.getBean(UserScheduleStageConfigRepository.class);
        userScheduleService = WebUtil.getBean(UserScheduleService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        sharedAssetConfigController = WebUtil.getBean(SharedAssetConfigController.class);
        currentUser = userContextService.getLoginUser();

        event = new DefaultScheduleEvent();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getRequestURI();
        if("/geapm/portal/schedule/SwapCenterDuty/workSchedule.xhtml".equals(encodeStr)){
            type=UserSchedule.Type.A;
        }else{
            type=UserSchedule.Type.W;
        }
        this.initSite();
        this.initTenantUserList();
        this.loadScheduleData();
        this.loadScheduleConfig();
        this.userList = loadUserList();
        this.scheduleDateStage = null;
        this.isDisableDelete = false;
        this.isDisableSave = false;
        this.isDisableScheduleDateStage = false;
        this.isDisableScheduleDate = false;
        shiftType=new String[]{"AM","PM","ALL"};
        this.resultInfo = "";


    }
    private void initSite(){
        List<SearchFilter> searchFilters = new ArrayList<>();
        SearchFilter orgLevel = new SearchFilter("orgType", SearchFilter.Operator.NE, 4);
        searchFilters.add(orgLevel);
        if(Objects.equals(currentUser.getOrgLevel(), 0)){
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
        }else if(Objects.equals(currentUser.getOrgLevel(), 1)){
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, currentUser.getInstitutionUID()));
        }else if(Objects.equals(currentUser.getOrgLevel(), 2)){
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, currentUser.getHospitalUID()));
        }else{
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID()));
        }
        selectOrgInfo = orgInfoDao.findBySearchFilter(searchFilters);
        OrgInfo selectSiteSchedule = userContextService.getSelectSiteSchedule();
        if(selectSiteSchedule == null){
            OrgInfo orgInfo =null;
            Optional<OrgInfo> first = selectOrgInfo.stream().sorted((item, item2) -> item2.getOrgType() - item.getOrgType()).findFirst();
            orgInfo=first.get();
            userContextService.setSelectSiteSchedule(orgInfo);
            selectSite=orgInfo;
        }else{
            selectSite=selectSiteSchedule;
        }
    }
    private void initTenantUserList(){
        if(CollectionUtils.isEmpty(tenantUserList)){
            tenantUserList = userAccountDao.findByTenantUID(selectSite.getTenantUID());
        }
    }
    private void loadScheduleData(){
            eventModel = new DefaultScheduleModel();

            List<SearchFilter> searchFilters = new ArrayList<>();
            searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, type.name()));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectSite.getUid()));

            List<UserScheduleDto> tempUserScheduleList = dao.findBySiteUidAndType(type.name(),selectSite.getUid());

            Map<String, UserAccount> userAccountMap =new HashMap<>();
            if(!CollectionUtils.isEmpty(tenantUserList)){
                userAccountMap = tenantUserList.stream().collect(Collectors.toMap(UserAccount::getUid, item -> item));
            }
            for (UserScheduleDto us : tempUserScheduleList) {
                UserSchedule schedule=new UserSchedule();
                BeanUtils.copyProperties(us,schedule);
                UserAccount userAccount = userAccountMap.get(us.getUserUID());
                schedule.setUserAccount(userAccount);
                DefaultScheduleEvent tempEvent = new DefaultScheduleEvent(this.getShowTitle(schedule.getScheduleDateStage()) + "---" + (userAccount == null?"":userAccount.getName()), schedule.getScheduleDate(), schedule.getScheduleDate(), true);
                tempEvent.setData(schedule);
                eventModel.addEvent(tempEvent);
            }
    }

    public void addSchedule(ActionEvent actionEvent) {

        UserSchedule tempUserSchedule = null;
        DefaultScheduleEvent tempEvent = null;

        if(event.getId() == null){

            //新增记录
            tempUserSchedule = new UserSchedule();
            tempUserSchedule.setTenantUID(selectSite.getTenantUID());
            tempUserSchedule.setInstitutionUID(selectSite.getInstitutionUID());
            tempUserSchedule.setHospitalUID(selectSite.getHospitalUID());
            tempUserSchedule.setSiteUID(selectSite.getSiteUID());
            tempUserSchedule.setType(type.name());
            tempUserSchedule.setScheduleDateStage(this.scheduleDateStage);
            tempUserSchedule.setUserUID(this.userUID);
            tempUserSchedule.setScheduleDate(this.scheduleDate);
            tempUserSchedule.setUserAccount(userAccountDao.getByUid(this.userUID));
            tempUserSchedule.setCreatedBy(currentUser.getLoginName());
            tempUserSchedule.setCreatedDate(new Date());
            tempUserSchedule.setLastModifiedBy(currentUser.getLoginName());
            tempUserSchedule.setLastModifiedDate(new Date());

            //JSF Schedule model
            tempEvent = new DefaultScheduleEvent(this.getShowTitle(tempUserSchedule.getScheduleDateStage()) + "---" + tempUserSchedule.getUserAccount().getName(), this.scheduleDate, this.scheduleDate, true);
            tempEvent.setData(tempUserSchedule);
            if(!userScheduleService.getUserVacationList(tempUserSchedule.getScheduleDate(),tempUserSchedule.getUserUID(),tempUserSchedule.getScheduleDateStage(), UserSchedule.AuditStatus.Approved.name()).isEmpty()){
                WebUtil.addErrorMessage(WebUtil.getMessage("ConfictingWithTheExistingRecords")+"（和休假冲突）");
                return;
            }
        }else{
            //更新记录
            if(event.getData() != null){
                tempUserSchedule = (UserSchedule)event.getData();

                tempUserSchedule.setScheduleDateStage(this.scheduleDateStage);
                tempUserSchedule.setUserUID(this.userUID);
                tempUserSchedule.setScheduleDate(event.getStartDate());
                tempUserSchedule.setUserAccount(userAccountDao.getByUid(this.userUID));
                tempUserSchedule.setLastModifiedBy(currentUser.getLoginName());
                tempUserSchedule.setLastModifiedDate(new Date());

                //JSF Schedule model
                tempEvent = new DefaultScheduleEvent(this.getShowTitle(tempUserSchedule.getScheduleDateStage()) + "---" + tempUserSchedule.getUserAccount().getName(), this.scheduleDate, this.scheduleDate, true);
                tempEvent.setId(event.getId());
                tempEvent.setData(tempUserSchedule);

            }
        }

        if(tempUserSchedule != null){
            if(userScheduleService.isValidateWorkScheduleAndUser(tempUserSchedule.getScheduleDate(), tempUserSchedule.getScheduleDateStage(), currentUser,userUID,type)){
                if(tempUserSchedule.getId() == null){
                    eventModel.addEvent(tempEvent);
                }else{
                    eventModel.updateEvent(tempEvent);
                }
                dao.save(tempUserSchedule);
            }else {
                WebUtil.addErrorMessage(WebUtil.getMessage("ConfictingWithTheExistingRecords"));
                //this.loadScheduleData();
                return;
            }

        }else{
            eventModel.updateEvent(new DefaultScheduleEvent(event.getTitle(), event.getStartDate(), event.getEndDate(), true));
        }

        event = new DefaultScheduleEvent();
    }

    public void deleteSchedule(){
        if(event.getId() != null){
            if(eventModel.deleteEvent(event)){
                dao.delete((UserSchedule)event.getData());
            }
        }
    }

    private String getShowTitle(String scheduleDateStage){
        String tempTime = "";
        if(UserScheduleService.SCHEDULE_STAGE_ALL.equals(scheduleDateStage)){
            tempTime = WebUtil.getMessage("AllDay");
        }else if(UserScheduleService.SCHEDULE_STAGE_AM.equals(scheduleDateStage)){
            tempTime = WebUtil.getMessage("MorningShift");
        }else if(UserScheduleService.SCHEDULE_STAGE_PM.equals(scheduleDateStage)){
            tempTime = WebUtil.getMessage("NightShift");
        }
        return tempTime;
    }

    public List<UserAccount> loadUserList(){
        OrgInfo selectSiteSchedule = userContextService.getSelectSiteSchedule();
        List<UserAccount> userAccountList = new ArrayList<>();
        if(Objects.equals(currentUser.getOrgLevel(), 0)){
            userAccountList = userAccountDao.findByTenantUIDAndStaffRole(currentUser.getTenantUID());
        }else if(Objects.equals(currentUser.getOrgLevel(), 1)){
            userAccountList = userAccountDao.findByInstitutionUIDAndStaffRole(currentUser.getInstitutionUID());
            userAccountList = userAccountList.stream()
                    .filter(item-> currentUser.getInstitutionUID().equals(item.getInstitutionUID()))
                    .filter(item-> {
                        if(selectSiteSchedule == null) return Boolean.TRUE;
                        String institutionUID = item.getInstitutionUID();
                        String hospitalUID = item.getHospitalUID();
                        String siteUID = item.getSiteUID();
                        if(siteUID.equals(selectSiteSchedule.getUid()) || siteUID.equals(hospitalUID) || siteUID.equals(institutionUID)){
                            return Boolean.TRUE;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }else if(Objects.equals(currentUser.getOrgLevel(), 2)){
            userAccountList = userAccountDao.findByHospitalUIDAndStaffRole(currentUser.getHospitalUID());
            userAccountList = userAccountList.stream()
                    .filter(item-> currentUser.getHospitalUID().equals(item.getHospitalUID()))
                    .filter(item-> {
                        if(selectSiteSchedule == null) return Boolean.TRUE;
                        String hospitalUID = item.getHospitalUID();
                        String siteUID = item.getSiteUID();
                        if(siteUID.equals(selectSiteSchedule.getUid()) || siteUID.equals(hospitalUID)){
                            return Boolean.TRUE;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }else if(Objects.equals(currentUser.getOrgLevel(), 3)){
            userAccountList = userAccountDao.findBySiteUIDAndStaffRole(currentUser.getSiteUID());
        }
        userAccountList=userAccountList.stream().filter(item-> item.getIsDeleted() == null?Boolean.TRUE:!item.getIsDeleted()).collect(Collectors.toList());
        return userAccountList;
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
        this.setEdited(Boolean.TRUE);
        this.userScheduleID = ((UserSchedule)event.getData()).getId();
        this.scheduleDate = ((UserSchedule)event.getData()).getScheduleDate();
        this.scheduleDateStage = ((UserSchedule)event.getData()).getScheduleDateStage();
        this.userUID = ((UserSchedule)event.getData()).getUserUID();
        this.isDisableScheduleDateStage = true;
        this.isDisableScheduleDate = true;

        if(userScheduleService.isBeforeTagetDate(((UserSchedule)event.getData()).getScheduleDate(), new Date())){
            this.isDisableDelete = true;
            this.isDisableSave = true;
        }else {
            this.isDisableDelete = false;
            this.isDisableSave = false;
        }
        List<UserAccount> userAccounts =new ArrayList<>();
        if(UserSchedule.Type.A == type){
            userAccounts=sharedAssetConfigController.loadUserList();
        }else{
            userAccounts=this.loadUserList();
        }
        boolean b = userAccounts.stream().anyMatch(item -> item.getUid().equals(this.userUID));
        if(!b){
            userAccounts.add(userAccountDao.getUsers(this.userUID));
        }
        this.setEditUserList(userAccounts);
    }

    public void onDateSelect(SelectEvent selectEvent) {
        DefaultScheduleEvent tempEvent = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject(), true);
        tempEvent.setData(null);
        event = tempEvent;
        this.setEdited(Boolean.FALSE);
        this.userScheduleID = null;
        this.scheduleDate = (Date) selectEvent.getObject();
        this.scheduleDateStage = null;
        this.userUID = null;
        this.isDisableDelete = true;
        this.isDisableScheduleDateStage = false;
        this.isDisableScheduleDate = false;

        if(userScheduleService.isBeforeTagetDate((Date) selectEvent.getObject(), new Date())){
            this.isDisableSave = true;
        }else {
            this.isDisableSave = false;
        }
    }

    public void onScheduleValidate(){
        if(userScheduleService.isBeforeTagetDate(this.scheduleDate, new Date()) || !userScheduleService.isValidateWorkSchedule(this.scheduleDate, this.scheduleDateStage, currentUser) || !userScheduleService.isValidateUser(this.scheduleDate, this.userUID, this.scheduleDateStage, this.userScheduleID)){
            this.isDisableSave = true;
        }else {
            this.isDisableSave = false;
        }
    }
    public void onScheduleValidateNew(){
        if(userScheduleService.isBeforeTagetDate(this.scheduleDate, new Date()) || !userScheduleService.isValidateWorkScheduleAndUser(this.scheduleDate, this.scheduleDateStage, currentUser,this.userUID,type)){
            this.isDisableSave = true;
        }else {
            this.isDisableSave = false;
        }
    }
    public void loadScheduleConfig(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateStr = sdf.format(new Date());

        Map<String, UserScheduleStageConfig> usscMap = userScheduleService.loadScheduleStageConfig(currentUser,type,selectSite);

        UserScheduleStageConfig ussc_all = usscMap.get(UserScheduleService.SCHEDULE_STAGE_ALL);
        UserScheduleStageConfig ussc_am = usscMap.get(UserScheduleService.SCHEDULE_STAGE_AM);
        UserScheduleStageConfig ussc_pm = usscMap.get(UserScheduleService.SCHEDULE_STAGE_PM);

        try {
            if(ussc_all != null){
                this.startTime_all = ussc_all.getStartTime() != null ? sdf.parse(currentDateStr.split(" ")[0] + " " + ussc_all.getStartTime()) : null;
                this.endTime_all = ussc_all.getEndTime() != null ? sdf.parse(currentDateStr.split(" ")[0] + " " + ussc_all.getEndTime()) : null;
            }else{
                this.startTime_all = sdf.parse(currentDateStr.split(" ")[0] + " 8:00:00");
                this.endTime_all = sdf.parse(currentDateStr.split(" ")[0] + " 8:00:00");
            }
            if(ussc_am != null){
                this.startTime_am = ussc_am.getStartTime() != null ? sdf.parse(currentDateStr.split(" ")[0] + " " + ussc_am.getStartTime()) : null;
                this.endTime_am = ussc_am.getEndTime() != null ? sdf.parse(currentDateStr.split(" ")[0] + " " + ussc_am.getEndTime()) : null;
            }else {
                this.startTime_am = sdf.parse(currentDateStr.split(" ")[0] + " 12:00:00");
                this.endTime_am = sdf.parse(currentDateStr.split(" ")[0] + " 13:30:00");
            }
            if(ussc_pm != null){
                this.startTime_pm = ussc_pm.getStartTime() != null ? sdf.parse(currentDateStr.split(" ")[0] + " " + ussc_pm.getStartTime()) : null;
                this.endTime_pm = ussc_pm.getEndTime() != null ? sdf.parse(currentDateStr.split(" ")[0] + " " + ussc_pm.getEndTime()) : null;
            }else {
                this.startTime_pm = sdf.parse(currentDateStr.split(" ")[0] + " 17:00:00");
                this.endTime_pm = sdf.parse(currentDateStr.split(" ")[0] + " 8:00:00");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    public void batchSaveWorkSchedule(){
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if(this.isValideDate(startBatchDate,endBatchDate)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            List<Integer> collect = Arrays.asList(selectedOptions2).stream().map(item -> Integer.valueOf(item)).collect(Collectors.toList());
            Map<String, List<UserSchedule>> resultMap = userScheduleService.batchSaveWorkSchedule(selectedUserList, startBatchDate, endBatchDate, shiftType, selectSite, type,collect);

            List<UserSchedule> successList = resultMap.get(UserScheduleService.BATCH_SUCCESS_LIST);//没有创建成功排班记录
            List<UserSchedule> failedList = resultMap.get(UserScheduleService.BATCH_FAILED_LIST);//没有创建成功排班记录

            for (UserSchedule us : failedList) {
                resultInfo += "[" + us.getUserAccount().getName() + sdf.format(us.getScheduleDate()) + this.getShowTitle(us.getScheduleDateStage()) + "]";
            }

            if("".equals(resultInfo)){
                requestContext.addCallbackParam("flag", "Success");
            }else{
                requestContext.addCallbackParam("flag", "PartialSuccess");
            }

        }else {
            requestContext.addCallbackParam("flag", "Failed");
        }

    }
    public void batchDeleteWorkSchedule(){
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if(this.isValideDate(deleteStartBatchDate,deleteEndBatchDate)){
            userScheduleService.batchDeleteWorkSchedule(deleteSelectedUserList,deleteStartBatchDate, deleteEndBatchDate,selectSite, type,Arrays.asList(shiftType));
            requestContext.addCallbackParam("flag", "Success");
        }else {
            requestContext.addCallbackParam("flag", "Failed");
        }
    }
    public String resultConfig(){
        return "workSchedule?faces-redirect=true";
    }

    public void saveStageConfig(){

        userScheduleService.saveScheduleStageConfig(startTime_all,endTime_all,startTime_am,endTime_am,startTime_pm,endTime_pm,selectSite,type);

    }

    public boolean isValideDate(Date startBatchDate,Date endBatchDate){
        Calendar calender = Calendar.getInstance();
        calender.setTime(new Date());
        calender.add(Calendar.DAY_OF_MONTH, -1);

        if (startBatchDate.before(calender.getTime())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("StartTimeCannotBeEarlierThanCurrentTime"));
            return false;
        }
        if (endBatchDate.before(startBatchDate)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("EndTimeCannotBeEarlierThanStartTime"));
            return false;
        }

        return true;
    }

    public String[] getShiftType() {
        return shiftType;
    }

    public void setShiftType(String[] shiftType) {
        this.shiftType = shiftType;
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getScheduleDateStage() {
        return scheduleDateStage;
    }

    public void setScheduleDateStage(String scheduleDateStage) {
        this.scheduleDateStage = scheduleDateStage;
    }

    public List<UserAccount> getUserList() {
        return userList;
    }

    public void setUserList(List<UserAccount> userList) {
        this.userList = userList;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Date getStartBatchDate() {
        return startBatchDate;
    }

    public void setStartBatchDate(Date startBatchDate) {
        this.startBatchDate = startBatchDate;
    }

    public Date getEndBatchDate() {
        return endBatchDate;
    }

    public void setEndBatchDate(Date endBatchDate) {
        this.endBatchDate = endBatchDate;
    }

    public boolean isDisableDelete() {
        return isDisableDelete;
    }

    public void setDisableDelete(boolean disableDelete) {
        isDisableDelete = disableDelete;
    }

    public boolean isDisableSave() {
        return isDisableSave;
    }

    public void setDisableSave(boolean disableSave) {
        isDisableSave = disableSave;
    }

    public boolean isDisableScheduleDateStage() {
        return isDisableScheduleDateStage;
    }

    public void setDisableScheduleDateStage(boolean disableScheduleDateStage) {
        isDisableScheduleDateStage = disableScheduleDateStage;
    }

    public boolean isDisableScheduleDate() {
        return isDisableScheduleDate;
    }

    public void setDisableScheduleDate(boolean disableScheduleDate) {
        isDisableScheduleDate = disableScheduleDate;
    }

    public List<UserAccount> getSelectedUserList() {
        return selectedUserList;
    }

    public void setSelectedUserList(List<UserAccount> selectedUserList) {
        this.selectedUserList = selectedUserList;
    }

    public String getBatchType() {
        return batchType;
    }

    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }

    public Date getStartTime_all() {
        return startTime_all;
    }

    public void setStartTime_all(Date startTime_all) {
        this.startTime_all = startTime_all;
    }

    public Date getEndTime_all() {
        return endTime_all;
    }

    public void setEndTime_all(Date endTime_all) {
        this.endTime_all = endTime_all;
    }

    public Date getStartTime_am() {
        return startTime_am;
    }

    public void setStartTime_am(Date startTime_am) {
        this.startTime_am = startTime_am;
    }

    public Date getEndTime_am() {
        return endTime_am;
    }

    public void setEndTime_am(Date endTime_am) {
        this.endTime_am = endTime_am;
    }

    public Date getStartTime_pm() {
        return startTime_pm;
    }

    public void setStartTime_pm(Date startTime_pm) {
        this.startTime_pm = startTime_pm;
    }

    public Date getEndTime_pm() {
        return endTime_pm;
    }

    public void setEndTime_pm(Date endTime_pm) {
        this.endTime_pm = endTime_pm;
    }

    public String getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(String resultInfo) {
        this.resultInfo = resultInfo;
    }

    public String[] getSelectedOptions2() {
        return selectedOptions2;
    }

    public void setSelectedOptions2(String[] selectedOptions2) {
        this.selectedOptions2 = selectedOptions2;
    }



    private final String[] all=new String[]{"0","1","2","3","4","5","6","7"};
    private String selectedStr;
    private boolean add=Boolean.TRUE;
    public void valueChangeListener(ValueChangeEvent event){
        String[] newValue =(String[]) event.getNewValue();
        String[] oldValue =(String[]) event.getOldValue();
       if(oldValue == null){
            selectedStr=newValue[0];
            add=Boolean.TRUE;
        }else {
            int newlength = newValue.length;
            int oldlength = oldValue.length;
            add=newlength > oldlength;
            if(newlength == 7 && add){
                selectedStr="0";
            }else{
                Set<String> set=new HashSet<String>(Arrays.asList(add?newValue:oldValue));
                for (String i: add?oldValue:newValue){
                    if(set.contains(i)){
                        set.remove(i);
                    }else{
                        set.add(i);
                    }
                }
                selectedStr=new ArrayList<>(set).get(0);
            }
         }
    }
    public void selectedOptions2Changed() {
           if(add){
               if("0".equals(selectedStr)){
                   selectedOptions2=all;
               }else{
                   List<String> collect = Arrays.asList(selectedOptions2).stream().filter(item -> !"0".equals(item)).collect(Collectors.toList());
                   selectedOptions2=collect.toArray(new String[collect.size()]);
               }
           }else{
               if("0".equals(selectedStr)){
                   selectedOptions2=null;
               }else{
                   List<String> collect = Arrays.asList(selectedOptions2).stream().filter(item -> !"0".equals(item)).collect(Collectors.toList());
                   selectedOptions2=collect.toArray(new String[collect.size()]);
               }
           }
    }

    public Date getDeleteStartBatchDate() {
        return deleteStartBatchDate;
    }

    public void setDeleteStartBatchDate(Date deleteStartBatchDate) {
        this.deleteStartBatchDate = deleteStartBatchDate;
    }

    public Date getDeleteEndBatchDate() {
        return deleteEndBatchDate;
    }

    public void setDeleteEndBatchDate(Date deleteEndBatchDate) {
        this.deleteEndBatchDate = deleteEndBatchDate;
    }

    public List<UserAccount> getDeleteSelectedUserList() {
        return deleteSelectedUserList;
    }

    public void setDeleteSelectedUserList(List<UserAccount> deleteSelectedUserList) {
        this.deleteSelectedUserList = deleteSelectedUserList;
    }
    public TreeNode getHospitalTree(){
        List<OrgInfo> orgList = selectOrgInfo;
        if(org.springframework.util.CollectionUtils.isEmpty(orgList)){
            return null;
        }
        List<OrgInfo> collect = orgList.stream().sorted((item, item2) ->item2.getOrgType()-item.getOrgType()).collect(Collectors.toList());
        OrgInfo orgInfo = collect.get(0);
        TreeNode rootNode = new DefaultTreeNode("org", null, null);
        Map<String, TreeNode> treeMap = orgList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> {
            TreeNode org = new DefaultTreeNode("org", item, null);
            if(!Objects.equals(item.getOrgType(), orgInfo.getOrgType())){
                org.setSelectable(false);
            }
            return org;
        }));
        for (OrgInfo org : orgList) {
            if (treeMap.containsKey(org.getParentUID())) {
                treeMap.get(org.getParentUID()).getChildren().add(treeMap.get(org.getUid()));
            } else {
                rootNode.getChildren().add(treeMap.get(org.getUid()));
            }
        }
        TreeNodeUtil.sortTreeNodeByName(rootNode);
        rootNode.setExpanded(true);
//        expandedByOrgType(rootNode, 2);
        return rootNode;
    }
    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }
    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        if(event == null ) return;
        OrgInfo orgInfo = (OrgInfo) event.getTreeNode().getData();
        userContextService.setSelectSiteSchedule(orgInfo);
        selectSite=userContextService.getSelectSiteSchedule();
        this.loadScheduleData();
        this.loadScheduleConfig();
    }

    public Boolean getEdited() {
        return isEdited;
    }

    public void setEdited(Boolean edited) {
        isEdited = edited;
    }

    public List<UserAccount> getEditUserList() {
        return editUserList;
    }

    public void setEditUserList(List<UserAccount> editUserList) {
        this.editUserList = editUserList;
    }
}
