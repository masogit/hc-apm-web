package com.ge.apm.view.wo;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.WorkOrderReport;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.wo.WorkOrderStatisticsService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 212605082 on 2017/12/12.
 */
@ManagedBean
@ViewScoped
public class WorkOrderStatisticsController {

    private UserAccount currentUser;

    private OrgInfoRepository orgDao;

    private Date startTime;
    private Date endTime;

    private UaaService uaaService;
    private WorkOrderStatisticsService wosServic;

    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;

    private String statisticsType;

    private List<WorkOrderReport> worList;

    @PostConstruct
    protected void init() {

        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        wosServic = WebUtil.getBean(WorkOrderStatisticsService.class);

        currentUser = UserContextService.getCurrentUserAccount();
        statisticsType="depart";

        initOrgTree();

    }

    public void previewData(){
        worList = wosServic.createData(selectedOrg, startTime, endTime, statisticsType);
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

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = (OrgInfo) node.getData();
        }
    }

    public String calculateRate(Long sourceCount){
        DecimalFormat df = new DecimalFormat("######0");
        Long allCloseCount = 0l;
        String rateStr = " ";

        for (WorkOrderReport wor: worList) {
            allCloseCount += wor.getCloseCount();
        }

        if(allCloseCount != 0){
            rateStr = df.format((sourceCount.doubleValue()/allCloseCount) * 100) + "%";
        }

        return rateStr;
    }

    public String calculateCaseloadRate(Long sourceCaseload){

        DecimalFormat df = new DecimalFormat("######0");
        Long allTotalManHour = 0l;
        String rateCaseloadStr = " ";

        for (WorkOrderReport wor: worList) {
            allTotalManHour += wor.getTotalManHour();
        }

        if(allTotalManHour != 0){
            rateCaseloadStr = df.format((sourceCaseload.doubleValue()/allTotalManHour) * 100) + "%";
        }

        return rateCaseloadStr;
    }

    public String calculateAVGDate(Long spendTime, Long closeCount){
        DecimalFormat df = new DecimalFormat("######0.0");

        if(closeCount != 0){
            return df.format(spendTime.doubleValue()/(closeCount * 24)) + "天";
        }else{
            return "0天";
        }
    }

    public String getAllorgName(Integer orgID){

        OrgInfo tempOrgInfo = orgDao.findById(orgID);
        String deptName = tempOrgInfo.getName();
        String siteName = orgDao.getByUid(tempOrgInfo.getSiteUID()).getName();

        return siteName + "/" + deptName;

    }

    public UserAccount getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserAccount currentUser) {
        this.currentUser = currentUser;
    }

    public OrgInfoRepository getOrgDao() {
        return orgDao;
    }

    public void setOrgDao(OrgInfoRepository orgDao) {
        this.orgDao = orgDao;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public UaaService getUaaService() {
        return uaaService;
    }

    public void setUaaService(UaaService uaaService) {
        this.uaaService = uaaService;
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

    public String getStatisticsType() {
        return statisticsType;
    }

    public void setStatisticsType(String statisticsType) {
        this.statisticsType = statisticsType;
    }

    public List<WorkOrderReport> getWorList() {
        return worList;
    }

    public void setWorList(List<WorkOrderReport> worList) {
        this.worList = worList;
    }

}
