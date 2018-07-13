package com.ge.apm.view.schedule;

import com.ge.apm.dao.*;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserSchedule;
import com.ge.apm.domain.UserScheduleStageConfig;
import com.ge.apm.service.schedule.UserScheduleService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.*;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private List<UserAccount> selectedUserList;
    private String batchType;

    private Date startTime_all;
    private Date endTime_all;
    private Date startTime_am;
    private Date endTime_am;
    private Date startTime_pm;
    private Date endTime_pm;


    @PostConstruct
    protected void init() {

        dao = WebUtil.getBean(UserScheduleRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        userScheduleStageConfigDao = WebUtil.getBean(UserScheduleStageConfigRepository.class);
        userScheduleService = WebUtil.getBean(UserScheduleService.class);

        currentUser = UserContextService.getCurrentUserAccount();

        event = new DefaultScheduleEvent();

        this.loadScheduleData();
        this.loadScheduleConfig();
        this.userList = loadUserList();
        this.scheduleDateStage = null;
        this.isDisableDelete = false;
        this.isDisableSave = false;
        this.isDisableScheduleDateStage = false;
        this.isDisableScheduleDate = false;

    }

    private void loadScheduleData(){
        eventModel = new DefaultScheduleModel();

        List<SearchFilter> searchFilters = new ArrayList<>();
        if(currentUser.getOrgLevel() == 0){
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
        }else if(currentUser.getOrgLevel() == 1){
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, currentUser.getInstitutionUID()));
        }else if(currentUser.getOrgLevel() == 2){
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, currentUser.getHospitalUID()));
        }else{
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID()));
        }
        List<UserSchedule> tempUserScheduleList = dao.findBySearchFilter(searchFilters);

        for (UserSchedule us : tempUserScheduleList) {

            DefaultScheduleEvent tempEvent = new DefaultScheduleEvent(this.getShowTitle(us.getScheduleDateStage()) + "---" + us.getUserAccount().getName(), us.getScheduleDate(), us.getScheduleDate(), true);
            tempEvent.setData(us);
            eventModel.addEvent(tempEvent);
        }
    }

    public void addSchedule(ActionEvent actionEvent) {

        UserSchedule tempUserSchedule = null;
        DefaultScheduleEvent tempEvent = null;

        if(event.getId() == null){
            //新增记录
            tempUserSchedule = new UserSchedule();
            tempUserSchedule.setTenantUID(currentUser.getTenantUID());
            tempUserSchedule.setInstitutionUID(currentUser.getInstitutionUID());
            tempUserSchedule.setHospitalUID(currentUser.getHospitalUID());
            tempUserSchedule.setSiteUID(currentUser.getSiteUID());
            tempUserSchedule.setType("W");
            tempUserSchedule.setScheduleDateStage(this.scheduleDateStage);
            tempUserSchedule.setUserUID(this.userUID);
            tempUserSchedule.setScheduleDate(this.scheduleDate);
            tempUserSchedule.setUserAccount(userAccountDao.getByUid(this.userUID));

            tempEvent = new DefaultScheduleEvent(this.getShowTitle(tempUserSchedule.getScheduleDateStage()) + "---" + tempUserSchedule.getUserAccount().getName(), this.scheduleDate, this.scheduleDate, true);
            tempEvent.setData(tempUserSchedule);

        }else{
            //更新记录
            if(event.getData() != null){
                tempUserSchedule = (UserSchedule)event.getData();

                tempUserSchedule.setScheduleDateStage(this.scheduleDateStage);
                tempUserSchedule.setUserUID(this.userUID);
                tempUserSchedule.setScheduleDate(event.getStartDate());
                tempUserSchedule.setUserAccount(userAccountDao.getByUid(this.userUID));

                tempEvent = new DefaultScheduleEvent(this.getShowTitle(tempUserSchedule.getScheduleDateStage()) + "---" + tempUserSchedule.getUserAccount().getName(), this.scheduleDate, this.scheduleDate, true);
                tempEvent.setId(event.getId());
                tempEvent.setData(tempUserSchedule);

            }
        }

        if(tempUserSchedule != null){
            if(userScheduleService.isValidateWorkSchedule(tempUserSchedule.getScheduleDate(), tempUserSchedule.getScheduleDateStage(), currentUser)){
                if(tempUserSchedule.getId() == null){
                    eventModel.addEvent(tempEvent);
                }else{
                    eventModel.updateEvent(tempEvent);
                }
                dao.save(tempUserSchedule);
            }else {
                WebUtil.addErrorMessage("和已存在记录冲突。");
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
            tempTime = "全天";
        }else if(UserScheduleService.SCHEDULE_STAGE_AM.equals(scheduleDateStage)){
            tempTime = "早班";
        }else if(UserScheduleService.SCHEDULE_STAGE_PM.equals(scheduleDateStage)){
            tempTime = "晚班";
        }
        return tempTime;
    }

    public List<UserAccount> loadUserList(){

        List<UserAccount> userAccountList = new ArrayList<>();
        if(currentUser.getOrgLevel() == 0){
            userAccountList = userAccountDao.findByTenantUIDAndStaffRole(currentUser.getTenantUID());
        }else if(currentUser.getOrgLevel() == 1){
            userAccountList = userAccountDao.findByInstitutionUIDAndStaffRole(currentUser.getInstitutionUID());
        }else if(currentUser.getOrgLevel() == 2){
            userAccountList = userAccountDao.findByHospitalUIDAndStaffRole(currentUser.getHospitalUID());
        }else if(currentUser.getOrgLevel() == 3){
            userAccountList = userAccountDao.findBySiteUIDAndStaffRole(currentUser.getSiteUID());
        }

        return userAccountList;
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();

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
    }

    public void onDateSelect(SelectEvent selectEvent) {
        DefaultScheduleEvent tempEvent = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject(), true);
        tempEvent.setData(null);
        event = tempEvent;

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

    private void loadScheduleConfig(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateStr = sdf.format(new Date());

        Map<String, UserScheduleStageConfig> usscMap = userScheduleService.loadScheduleStageConfig(currentUser);

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

    public String batchSaveWorkSchedule(){
        if(this.isValideDate()){
            userScheduleService.batchSaveWorkSchedule(selectedUserList, startBatchDate, endBatchDate, batchType, currentUser);

            return "workSchedule?faces-redirect=true";
        }else {
            return "";
        }
    }

    public void saveStageConfig(){

        userScheduleService.saveScheduleStageConfig(startTime_all,endTime_all,startTime_am,endTime_am,startTime_pm,endTime_pm,currentUser);

    }

    public boolean isValideDate(){
        Calendar calender = Calendar.getInstance();
        calender.setTime(new Date());
        calender.add(Calendar.DAY_OF_MONTH, -1);

        if (startBatchDate.before(calender.getTime())) {
            WebUtil.addErrorMessage("开始日期不能早于当前日期");
            return false;
        }
        if (endBatchDate.before(startBatchDate)) {
            WebUtil.addErrorMessage("结束日期不能早于开始日期");
            return false;
        }

        return true;
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
}
