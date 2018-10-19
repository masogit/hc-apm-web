package com.ge.apm.view.schedule;

import com.ge.apm.dao.UserScheduleRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserSchedule;
import com.ge.apm.service.schedule.UserScheduleService;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 212605082 on 2018/1/12.
 */
@ManagedBean
@ViewScoped
public class UserVacationScheduleController extends GenericCRUDUUIDController<UserSchedule> implements Serializable, ServerEventInterface {

    //休假
    private final String SCHEDULE_TYPE_VACATION = "V";
    //排班
    private final String SCHEDULE_TYPE_WORK = "W";

    private UserScheduleRepository dao;

    private UserAccount currentUser;

    private UserContextService userContextService;
    private UserScheduleService userScheduleService;

    private Date startDate;
    private Date endDate;
    private String scheduleDateStage;

    private String queryStr;

    private String auditInfo;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(UserScheduleRepository.class);
        userScheduleService = WebUtil.getBean(UserScheduleService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        currentUser = UserContextService.getCurrentUserAccount();

        queryStr = WebUtil.getRequestParameter("queryStr") != null ? WebUtil.getRequestParameter("queryStr") : UserSchedule.AuditStatus.PendingApproval.name();
    }

    @Override
    protected GenericRepositoryUUID<UserSchedule> getDAO() {
        return dao;
    }

    @Override
    protected Page<UserSchedule> loadData(PageRequest pageRequest) {

        if (this.searchFilters == null) {
            this.searchFilters = new ArrayList<>();
        }

        if (!this.hasFilterOnField("tenantUID")) {
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
        }

        if (!this.hasFilterOnField("auditStatus")) {
            if(!"All".equals(queryStr)){
                searchFilters.add(new SearchFilter("auditStatus", SearchFilter.Operator.EQ, queryStr));
            }
        }

        if(userContextService.hasRole("AssetHead")){
            if(currentUser.getOrgLevel() != 0){
                if(currentUser.getOrgLevel() == 1){
                    if (!this.hasFilterOnField("institutionUID")) {
                        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, currentUser.getInstitutionUID()));
                    }
                }else if(currentUser.getOrgLevel() == 2){
                    if (!this.hasFilterOnField("hospitalUID")) {
                        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, currentUser.getHospitalUID()));
                    }
                }else{
                    if (!this.hasFilterOnField("siteUID")) {
                        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID()));
                    }
                }
            }
        }else{
            if (!this.hasFilterOnField("userUID")) {
                searchFilters.add(new SearchFilter("userUID", SearchFilter.Operator.EQ, currentUser.getUid()));
            }
        }

        if (!this.hasFilterOnField("type")) {
            searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, SCHEDULE_TYPE_VACATION));
        }

        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    public String saveVacation(){

        if(!isValideDate()){
            return "";
        }

        List<LinkedHashMap<String, Object>> resultList = userScheduleService.saveVacation(startDate, endDate, scheduleDateStage);

        if(!resultList.isEmpty()){
            String msg = "";
            int i = 0;
            for (Map<String, Object> msgMap : resultList) {
                List assetInfoList = (List<AssetInfo>) msgMap.get("assetInfoList");
                if(i == 0){
                    if(assetInfoList != null && !assetInfoList.isEmpty()){
                        msg = ((Map)assetInfoList.get(0)).get("name") + "上只有你一个责任人或其他责任人都请假: ";
                    }else{
                        msg = "该日期已存在请假记录: ";
                    }
                }
                msg += msgMap.get("invalidDate") + ";";

                i++;
            }
            WebUtil.addErrorMessage(msg);
            return "";
        }

        return "userVacationList?faces-redirect=true";
    }

    public void approveVacation(boolean isApproved){
        if(isApproved){
            this.selected.setAuditStatus(UserSchedule.AuditStatus.Approved.name());
        }else {
            this.selected.setAuditStatus(UserSchedule.AuditStatus.NotApproved.name());
        }
        this.selected.setLastModifiedBy(currentUser.getLoginName());
        this.selected.setLastModifiedDate(new Date());
        userScheduleService.saveVacationSchedule(this.selected);
    }

    public void validateAudit(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.auditInfo = "";

        List<UserSchedule> tempUserScheduleList =  userScheduleService.getUserWorkList(this.selected.getUserAccount(), this.selected.getScheduleDate(), this.selected.getScheduleDateStage());
        for (UserSchedule us : tempUserScheduleList) {
            auditInfo += us.getUserAccount().getName() + " " + sdf.format(us.getScheduleDate()) + "有排班记录！";
            break;
        }
    }

    public boolean isValideDate(){
        Calendar calender = Calendar.getInstance();
        calender.setTime(new Date());
        calender.add(Calendar.DAY_OF_MONTH, -1);

        if (startDate.before(calender.getTime())) {
            WebUtil.addErrorMessage("开始日期不能早于当前日期");
            return false;
        }
        if (endDate.before(startDate)) {
            WebUtil.addErrorMessage("结束日期不能早于开始日期");
            return false;
        }

        return true;
    }

    public String getScheduleDateStageTitle(String scheduleDateStage){
        String title = "";
        if(UserSchedule.ScheduleDateStage.ALL.name().equals(scheduleDateStage)){
            title = "全天";
        }else if(UserSchedule.ScheduleDateStage.AM.name().equals(scheduleDateStage)){
            title = "上午";
        }else if(UserSchedule.ScheduleDateStage.PM.name().equals(scheduleDateStage)){
            title = "下午";
        }
        return title;
    }

    public String getAuditStatusTitle(String auditStatus){
        String title = "";
        if(UserSchedule.AuditStatus.PendingApproval.name().equals(auditStatus)){
            title = "待审批";
        }else if(UserSchedule.AuditStatus.Approved.name().equals(auditStatus)){
            title = "审批通过";
        }else if(UserSchedule.AuditStatus.NotApproved.name().equals(auditStatus)){
            title = "审批未通过";
        }
        return title;
    }

    @Override
    public void delete() {
        userScheduleService.deleteUserSchedule(this.selected);
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {}

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getQueryStr() {
        return queryStr;
    }

    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
    }

    public String getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(String auditInfo) {
        this.auditInfo = auditInfo;
    }

    public String getScheduleDateStage() {
        return scheduleDateStage;
    }

    public void setScheduleDateStage(String scheduleDateStage) {
        this.scheduleDateStage = scheduleDateStage;
    }
}
