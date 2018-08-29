package com.ge.apm.view.uaa;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetCreateService;
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
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 212605082 on 2017/5/15.
 */
@ManagedBean
@ViewScoped
public class BiomedGroupController extends JpaCRUDController<BiomedGroup> {

    private BiomedGroupRepository dao = null;
    private I18nMessageRepository i18nDao = null;
    private TenantInfoRepository siteInfoDao = null;
    private OrgInfoRepository orgInfoDao = null;
    private TenantInfoRepository tenantInfoDao = null;

    private DualListModel<UserAccount> availableUserAccount;
    private List<UserAccount> sourceUserAccount;
    private List<UserAccount> targetUserAccount;

    private UserAccount queryUserAccount;

    private AssetCreateService acService;
    private BiomedGroupService biomedGroupService;

    private Integer siteId;
    private Integer hospitalId;

    private Integer siteIdFilter;
    private Integer hospitalIdFilter;

    private UserAccount currentUser;

    private UaaService uaaService;

    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;


    @Override
    protected void init() {
        dao = WebUtil.getBean(BiomedGroupRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        siteInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        biomedGroupService = WebUtil.getBean(BiomedGroupService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);

        targetUserAccount = new ArrayList<UserAccount>();

        currentUser = UserContextService.getCurrentUserAccount();

        this.siteIdFilter = currentUser.getSiteId();

        queryUserAccount = new UserAccount();
        queryUserAccount.setSiteId(currentUser.getSiteId());
        queryUserAccount.setTenantUID(currentUser.getTenantUID());

        if(currentUser.getOrgLevel() != 0) {
            if (currentUser.getOrgLevel() == 1) {
                queryUserAccount.setInstitutionUID(currentUser.getInstitutionUID());
            } else if (currentUser.getOrgLevel() == 2) {
                queryUserAccount.setHospitalUID(currentUser.getHospitalUID());
            } else {
                queryUserAccount.setSiteUID(currentUser.getSiteUID());
            }
        }

        this.getQueryUserAccountList();
        this.initOrgTree();
    }

    @Override
    protected BiomedGroupRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<BiomedGroup> loadData(PageRequest pageRequest) {

        selected = null;

        List<SearchFilter> searchFilters = this.searchFilters;
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));

        if(currentUser.getOrgLevel() != 0) {
            if (currentUser.getOrgLevel() == 1) {
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, currentUser.getInstitutionUID()));
            } else if (currentUser.getOrgLevel() == 2) {
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, currentUser.getHospitalUID()));
            } else {
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID()));
            }
        }

        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(searchFilters, pageRequest);
        }

    }

    @Override
    public List<BiomedGroup> getItemList() {
        return dao.find();
    }

    public String getTenantName(String tenantUid) {
        return tenantInfoDao.findByuid(tenantUid).getName();
    }
    public String getSiteName(String siteUid) {
        return orgInfoDao.getByUid(siteUid).getName();
    }

    public List<TenantInfo> getSiteList() {

        List<TenantInfo> siteList = siteInfoDao.find();

        return siteList;
    }

    public List<OrgInfo> getHospitalList() {

        List<OrgInfo> hospitalList = new ArrayList<>();
        if (siteId != null) {

            hospitalList = orgInfoDao.getHospitalBySiteId(Integer.valueOf(siteId));
        }

        return hospitalList;
    }

    public List<OrgInfo> getHospitalListFilter() {

        List<OrgInfo> hospitalList = new ArrayList<>();
        if (siteIdFilter != null) {

            hospitalList = orgInfoDao.getHospitalBySiteId(Integer.valueOf(siteIdFilter));
        }

        return hospitalList;
    }

    public void onSiteChange(){
        if(siteId == null){
            hospitalId = null;
        }
    }

    public void onSiteFilterChange(){
        if(siteIdFilter == null){
            hospitalIdFilter = null;
        }
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

    public void save (){
        super.save();
        biomedGroupService.saveBiomedGroup(this.getSelected(), targetUserAccount);
    }

    @Override
    public void onBeforeNewObject(BiomedGroup object) {
        object.setSiteId(currentUser.getSiteId());
        object.setHospitalId(currentUser.getHospitalId());
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());

        targetUserAccount = new ArrayList<UserAccount>();
        this.getQueryUserAccountList();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();

        targetUserAccount = biomedGroupService.getTargetUserAccount(this.getSelected());
        this.getQueryUserAccountList();
    }

    public void deleteBiomedGroup(){
        biomedGroupService.deleteBiomedGroup(this.getSelected());
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
            if(selectedOrg.getOrgLevel() == 1){
                queryUserAccount.setInstitutionUID(selectedOrg.getInstitutionUID());
            }else if(selectedOrg.getOrgLevel() == 2){
                queryUserAccount.setHospitalUID(selectedOrg.getHospitalUID());
            }else if(selectedOrg.getOrgLevel() == 3){
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
        //rootNode.getChildren().addAll(uaaService.getOrgFullTreeByTenant(loginUser.getTenantUID()).getChildren());
        rootNode.setExpanded(true);

    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getSiteIdFilter() {
        return siteIdFilter;
    }

    public void setSiteIdFilter(Integer siteIdFilter) {
        this.siteIdFilter = siteIdFilter;
    }

    public Integer getHospitalIdFilter() {
        return hospitalIdFilter;
    }

    public void setHospitalIdFilter(Integer hospitalIdFilter) {
        this.hospitalIdFilter = hospitalIdFilter;
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
}
