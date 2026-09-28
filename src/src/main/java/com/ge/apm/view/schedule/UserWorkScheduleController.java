package com.ge.apm.view.schedule;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserScheduleRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserSchedule;
import com.ge.apm.service.schedule.UserScheduleService;
import com.ge.apm.service.uaa.BiomedGroupService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by 212605082 on 2018/1/12.
 */
@ManagedBean
@ViewScoped
public class UserWorkScheduleController extends GenericCRUDUUIDController<UserSchedule> implements Serializable, ServerEventInterface {

    //休假
    private final String SCHEDULE_TYPE_VACATION = "V";
    //排班
    private final String SCHEDULE_TYPE_WORK = "W";

    private UserScheduleRepository dao;
    private OrgInfoRepository orgInfoDao;
    private TenantInfoRepository tenantInfoDao;
    private UserAccount currentUser;

    private DualListModel<UserAccount> availableUserAccount;
    private List<UserAccount> sourceUserAccount;
    private List<UserAccount> targetUserAccount;

    private UserAccount queryUserAccount;

    private UaaService uaaService;
    private BiomedGroupService biomedGroupService;
    private UserScheduleService userScheduleService;

    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;

    private Date startDate;
    private Date endDate;

    @Override
    protected void init() {
        super.init();

        dao = WebUtil.getBean(UserScheduleRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        biomedGroupService = WebUtil.getBean(BiomedGroupService.class);
        userScheduleService = WebUtil.getBean(UserScheduleService.class);

        currentUser = UserContextService.getCurrentUserAccount();

        targetUserAccount = new ArrayList<UserAccount>();
        queryUserAccount = new UserAccount();
        queryUserAccount.setTenantUID(currentUser.getTenantUID());

        if(Objects.equals(currentUser.getOrgLevel(), 1)){
            queryUserAccount.setInstitutionUID(currentUser.getInstitutionUID());
        }else if(Objects.equals(currentUser.getOrgLevel(), 2)){
            queryUserAccount.setHospitalUID(currentUser.getHospitalUID());
        }else{
            queryUserAccount.setSiteUID(currentUser.getSiteUID());
        }

        this.getQueryUserAccountList();
        this.initOrgTree();

    }

    @Override
    protected GenericRepositoryUUID<UserSchedule> getDAO() {
        return dao;
    }

    @Override
    protected Page<UserSchedule> loadData(PageRequest pageRequest) {
        this.selected = null;

        if (this.searchFilters == null) {
            this.searchFilters = new ArrayList<>();
        }

        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, SCHEDULE_TYPE_WORK));
        return dao.findBySearchFilter(this.searchFilters, pageRequest);

    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        startDate = null;
        endDate = null;
        targetUserAccount = new ArrayList<>();
        availableUserAccount = new DualListModel<>(sourceUserAccount, targetUserAccount);
    }

    @Override
    public void onBeforeNewObject(UserSchedule object) {
        super.onBeforeNewObject(object);

        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());
        object.setType(SCHEDULE_TYPE_WORK);

    }

    public String saveWork() {

        if(!isValideDate()){
            return "";
        }

        userScheduleService.saveWork(targetUserAccount, startDate, endDate, this.selected, false);
        this.selected=null;

        return "userWorkList?faces-redirect=true";

    }

    public String saveWorkBatch(){

        if(!isValideDate()){
            return "";
        }

        if(isValideDate()){
            userScheduleService.saveWork(targetUserAccount, startDate, endDate, this.selected, true);
            this.selected=null;
        }

        return "userWorkList?faces-redirect=true";
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

    public void prepareBatchCreate() throws InstantiationException, IllegalAccessException{
        this.prepareCreate();
    }

    @Override
    public void delete() {
        userScheduleService.deleteUserSchedule(this.selected);
    }

    public void getQueryUserAccountList() {
        sourceUserAccount = biomedGroupService.getSourceUserAccountList(queryUserAccount);
        targetUserAccount.forEach((item)->{
            if(sourceUserAccount.contains(item)){
                sourceUserAccount.remove(item);
            }
        });

        availableUserAccount = new DualListModel<>(sourceUserAccount, targetUserAccount);
    }

    public void onUserAccountTransfer(TransferEvent event) {

        for(Object item : event.getItems()) {
            if(event.isAdd()){
                targetUserAccount.add((UserAccount)item);
            }else{
                targetUserAccount.remove((UserAccount)item);
            }
        }
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        queryUserAccount.setInstitutionUID(null);
        queryUserAccount.setHospitalUID(null);
        queryUserAccount.setSiteUID(null);

        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = (OrgInfo) node.getData();
        }

        if(selectedOrg != null && selectedOrg.getOrgLevel() != null){
            if(Objects.equals(selectedOrg.getOrgLevel(), 1)){
                queryUserAccount.setInstitutionUID(selectedOrg.getInstitutionUID());
            }else if(Objects.equals(selectedOrg.getOrgLevel(), 2)){
                queryUserAccount.setHospitalUID(selectedOrg.getHospitalUID());
            }else if(Objects.equals(selectedOrg.getOrgLevel(), 3)){
                queryUserAccount.setSiteUID(selectedOrg.getSiteUID());
            }
        }

        this.getQueryUserAccountList();

    }

    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());

        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root",null,null);
        TreeNode rootNode = new DefaultTreeNode("org",rootOrg,orgTree);
        rootNode.getChildren().add(treeNode);
        rootNode.setExpanded(true);

    }

    public String getTenantName(String tenantUid) {
        return tenantInfoDao.findByuid(tenantUid).getName();
    }
    public String getSiteName(String siteUid) {
        return orgInfoDao.getByUid(siteUid).getName();
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public DualListModel<UserAccount> getAvailableUserAccount() {
        return availableUserAccount;
    }

    public void setAvailableUserAccount(DualListModel<UserAccount> availableUserAccount) {
        this.availableUserAccount = availableUserAccount;
    }

    public List<UserAccount> getSourceUserAccount() {
        return sourceUserAccount;
    }

    public void setSourceUserAccount(List<UserAccount> sourceUserAccount) {
        this.sourceUserAccount = sourceUserAccount;
    }

    public List<UserAccount> getTargetUserAccount() {
        return targetUserAccount;
    }

    public void setTargetUserAccount(List<UserAccount> targetUserAccount) {
        this.targetUserAccount = targetUserAccount;
    }

    public UserAccount getQueryUserAccount() {
        return queryUserAccount;
    }

    public void setQueryUserAccount(UserAccount queryUserAccount) {
        this.queryUserAccount = queryUserAccount;
    }

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
}
