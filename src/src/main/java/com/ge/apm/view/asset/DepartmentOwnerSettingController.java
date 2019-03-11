package com.ge.apm.view.asset;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.OwnerConfigRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.OwnerConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.OwnerConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 212605082 on 2019/2/25.
 */
@ManagedBean
@ViewScoped
public class DepartmentOwnerSettingController extends GenericCRUDUUIDController<OwnerConfig> {

    private OwnerConfigRepository dao;
    private UserCommonService userCommonService;
    private OwnerConfigService ownerConfigService;
    private OrgInfoRepository orgInfoDao;

    private List<UserAccount> userAccountList;

    private UserAccount woOwner;
    private UserAccount woOwner2;
    private UserAccount woOwner3;
    private UserAccount pmOwner;

    private UserAccount currentUser;

    private AssetInfoService assetInfoService;
    private UaaService uaaService;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;

    private OrgInfo deptOrg;
    private List<OrgInfo> clinicalDeptList;

    //选定科室过滤
    private OrgInfoTreeService orgInfoTreeService;
    private String filterClinicalDeptOrgName;
    private Set<String> clinicalDeptOrgUidSetFilter;
    private List<OrgInfo> filterClinicalDeptList;

    @Override
    protected void init() {

        dao = WebUtil.getBean(OwnerConfigRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        userCommonService = WebUtil.getBean(UserCommonService.class);
        ownerConfigService = WebUtil.getBean(OwnerConfigService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);

        userAccountList = userCommonService.getWorkerList(null,null);
        currentUser = UserContextService.getCurrentUserAccount();

        this.selectedOrg = orgInfoDao.getByUid(currentUser.getSiteUID());
        this.initOrgTree();

        this.loadClinicalDept();
        this.loadClinicalDeptFilter();

    }

    @Override
    protected OwnerConfigRepository getDAO(){
        return dao;
    }

    protected Page<OwnerConfig> loadData(PageRequest pageRequest) {

        if (this.searchFilters == null) {
            this.searchFilters = new ArrayList<>();
        }

        this.removeFilterOnField("tenantUID");
        this.removeFilterOnField("institutionUID");
        this.removeFilterOnField("hospitalUID");
        this.removeFilterOnField("siteUID");
        this.removeFilterOnField("deptUID");

        this.updateSearchFilter(new SearchFilter("ownerConfigType", SearchFilter.Operator.EQ, OwnerConfig.OwnerConfigType.Department.name()));
        if(this.selectedOrg.getOrgLevel() == 0){
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedOrg.getUid()));
        }
        if(this.selectedOrg.getOrgLevel() == 1){
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getUid()));
        }
        if(this.selectedOrg.getOrgLevel() == 2){
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getUid()));
        }
        if(this.selectedOrg.getOrgLevel() == 3){
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedOrg.getUid()));
        }

        if(this.clinicalDeptOrgUidSetFilter != null && !this.clinicalDeptOrgUidSetFilter.isEmpty()){
            this.updateSearchFilter(new SearchFilter("deptUID", SearchFilter.Operator.IN, new ArrayList<>(clinicalDeptOrgUidSetFilter)));
        }

        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    public void onSelectOwnerConfig(){
        this.deptOrg = this.selected.getDeptOrgInfo();
        this.woOwner = this.selected.getWoOwner();
        this.woOwner2 = this.selected.getWoOwner2();
        this.woOwner3 = this.selected.getWoOwner3();
        this.pmOwner = this.selected.getPmOwner();
    }

    public List<UserAccount> autoCompleteOwner(String query) {

        if (userAccountList == null) {
            return Collections.emptyList();
        }
        List<UserAccount> res = userAccountList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        //res.sort(new PinYinComparatorByName());
        return res;
    }

    public void loadOwnerConfig(){

        this.resetOwnerConfig();

        if(deptOrg != null){
            List<OwnerConfig> ownerConfigList = ownerConfigService.loadDepartmentOwnerConfig(deptOrg.getUid(), selectedOrg.getSiteUID());
            for(OwnerConfig ownerConfig : ownerConfigList){
                this.woOwner = ownerConfig.getWoOwner();
                this.woOwner2 = ownerConfig.getWoOwner2();
                this.woOwner3 = ownerConfig.getWoOwner3();
                this.pmOwner = ownerConfig.getPmOwner();
            }
        }
    }

    private void resetOwnerConfig(){
        this.woOwner = null;
        this.woOwner2 = null;
        this.woOwner3 = null;
        this.pmOwner = null;
    }

    public void createOwerConfig(){
        this.resetOwnerConfig();
        this.deptOrg = null;
        this.selected = new OwnerConfig();
    }

    public void saveChange() {

        if(woOwner == null && woOwner2 == null && woOwner3 == null && pmOwner == null){
            WebUtil.addErrorMessage("请设置维修负责人或保养负责人");
            return;
        }

        List<OwnerConfig> ownerConfigList = ownerConfigService.loadDepartmentOwnerConfig(deptOrg.getUid(), selectedOrg.getSiteUID());

        OwnerConfig ownerConfig = new OwnerConfig();

        for (OwnerConfig tempOwnerConfig : ownerConfigList) {
            ownerConfig.setId(tempOwnerConfig.getId());
        }
        ownerConfig.setTenantUID(selectedOrg.getTenantUID());
        ownerConfig.setInstitutionUID(selectedOrg.getInstitutionUID());
        ownerConfig.setHospitalUID(selectedOrg.getHospitalUID());
        ownerConfig.setSiteUID(selectedOrg.getSiteUID());

        ownerConfig.setWoOwnerUID(woOwner != null ? woOwner.getUid() : null);
        ownerConfig.setWoOwnerUID2(woOwner2 != null ? woOwner2.getUid() : null);
        ownerConfig.setWoOwnerUID3(woOwner3 != null ? woOwner3.getUid() : null);
        ownerConfig.setPmOwnerUID(pmOwner != null ? pmOwner.getUid() : null);

        ownerConfig.setOwnerConfigType(OwnerConfig.OwnerConfigType.Department.name());
        ownerConfig.setDeptUID(deptOrg != null ? deptOrg.getUid() : null);

        ownerConfig.setCreatedBy(currentUser.getLoginName());
        ownerConfig.setCreatedDate(new Date());
        ownerConfig.setLastModifiedBy(currentUser.getLoginName());
        ownerConfig.setLastModifiedDate(new Date());

        dao.save(ownerConfig);

        this.resetOwnerConfig();
        this.clinicalDeptOrgUidSetFilter = Collections.emptySet();
        this.filterClinicalDeptOrgName = null;
        WebUtil.addErrorMessage("保存成功");
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = (OrgInfo) node.getData();
        }

        this.loadClinicalDept();
        this.loadClinicalDeptFilter();
        this.resetOwnerConfig();
        this.deptOrg = null;
    }

    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());

        orgTree = new DefaultTreeNode("root", null, null);
        orgTree.getChildren().add(treeNode);
        orgTree.setExpanded(true);

        TreeNode selectedTempOrg = TreeNodeUtil.findNodeFromTree(orgTree, selectedOrg);
        if (null != selectedTempOrg) {
            selectedTempOrg.setSelected(true);
            TreeNodeUtil.expandParentsNodes(selectedTempOrg);
        }
    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    private void loadClinicalDept(){
        if(null == selectedOrg){
            clinicalDeptList = Collections.EMPTY_LIST;
        }else{
            clinicalDeptList = assetInfoService.getClinicalDeptList(selectedOrg.getSiteUID());
            if(null != deptOrg && !deptOrg.getSiteUID().equals(selectedOrg.getSiteUID()) ){
                deptOrg = null;
            }
        }
    }

    private void loadClinicalDeptFilter() {
        filterClinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (null != selectedOrg) {
            filterClinicalDeptList = filterClinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectedOrg.getSiteUID()) || item.getHospitalUID().equals(selectedOrg.getSiteUID())
                    || item.getInstitutionUID().equals(selectedOrg.getSiteUID()) || item.getTenantUID().equals(selectedOrg.getSiteUID())).collect(Collectors.toList());
        }
    }

    public List<String> autoCompleteDeptFilter(String query) {
        if (filterClinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.filterClinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public void onClinicalDeptFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterClinicalDeptOrgName = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(filterClinicalDeptOrgName) && !filterClinicalDeptList.stream().anyMatch(item -> filterClinicalDeptOrgName.equals(item.getName()))) {
            filterClinicalDeptOrgName = null;
        }

        if (filterClinicalDeptOrgName != null) {
            List<OrgInfo> orgInfosMatchingByName = this.filterClinicalDeptList.stream().filter(item -> item.getName().equals(filterClinicalDeptOrgName)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, filterClinicalDeptList);
            clinicalDeptOrgUidSetFilter = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSetFilter = Collections.emptySet();
        }

    }

    public void deleteOwerConfig(){
        dao.delete(this.selected);
        this.selected = null;
        WebUtil.addErrorMessage("删除成功");
    }

    public void onOwnerChange(SelectEvent event){

    }

    public UserAccount getWoOwner() {
        return woOwner;
    }

    public void setWoOwner(UserAccount woOwner) {
        this.woOwner = woOwner;
    }

    public UserAccount getWoOwner2() {
        return woOwner2;
    }

    public void setWoOwner2(UserAccount woOwner2) {
        this.woOwner2 = woOwner2;
    }

    public UserAccount getWoOwner3() {
        return woOwner3;
    }

    public void setWoOwner3(UserAccount woOwner3) {
        this.woOwner3 = woOwner3;
    }

    public UserAccount getPmOwner() {
        return pmOwner;
    }

    public void setPmOwner(UserAccount pmOwner) {
        this.pmOwner = pmOwner;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
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

    public OrgInfo getDeptOrg() {
        return deptOrg;
    }

    public void setDeptOrg(OrgInfo deptOrg) {
        this.deptOrg = deptOrg;
    }

    public String getFilterClinicalDeptOrgName() {
        return filterClinicalDeptOrgName;
    }

    public void setFilterClinicalDeptOrgName(String filterClinicalDeptOrgName) {
        this.filterClinicalDeptOrgName = filterClinicalDeptOrgName;
    }
}
