package com.ge.apm.view.uaa;

import com.ge.apm.dao.*;

import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.domain.*;
import com.ge.apm.service.orginfo.OrgInfoService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.service.common.UserCommonService;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.dao.DataIntegrityViolationException;
import webapp.framework.web.WebUtil;
import org.primefaces.util.TreeUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.service.UserContext;

@ManagedBean
@ViewScoped
public class OrgInfoController extends JpaCRUDController<OrgInfo> {

    OrgInfoRepository dao = null;
    UaaService uaaService;
    private TenantInfo selectedTenant;
    UserAccount userAccount;
    private AssetInfoRepository assetInfoRepository;
    private OrgInfoService orgInfoService;
    private UserOrgInfoRepository userOrgDao;

    private boolean acShow = false;

    private String orgName;
    private String orgNameEn;
    private String retrieval;
    private UserAccount loginUser;

    private UserCommonService userCommonService;

    private OrgInfo currentOrg;

    @Override
    protected void init() {
        filterBySite = false;
        dao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        userAccount = new UserAccount();
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        userCommonService = WebUtil.getBean(UserCommonService.class);
        orgInfoService = WebUtil.getBean(OrgInfoService.class);
        userOrgDao = WebUtil.getBean(UserOrgInfoRepository.class);

        findPerson();
        loginUser = UserContextService.getCurrentUserAccount();
        setTenantId(loginUser.getSiteId());
        currentOrg = dao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        buildOrdTree();
    }

    @Override
    protected OrgInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<OrgInfo> loadData(PageRequest pageRequest) {

        return dao.getBySiteId(pageRequest, tenantId);
    }

    private int tenantId;

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
        TenantInfoRepository siteDao = WebUtil.getBean(TenantInfoRepository.class);
        selectedTenant = siteDao.findById(tenantId);

        this.selected = null;
        buildOrdTree();
    }
    UserAccountRepository userDao;

    public TenantInfo getSelectedTenant() {
        return selectedTenant;
    }

    public List<UserAccount> findPerson() {
        List<UserAccount> userList = new ArrayList<UserAccount>();
        if (this.selected != null && this.selected.getOrgType() == 4) {//类型是科室
            if (this.selected.getParentOrg() != null) {
                userList = userDao.findBySiteUID(this.selected.getSiteUID());
            }
        }
        return userList;
    }

    public void buildOrdTree() {

        //判断登陆用户是否是LocalITAdmin(本地后台运营管理)
        if (UserContextService.isOnlyLocalITAdmin()) {
            orgTree = uaaService.getOrgTreeBySite(loginUser.getSiteUID());
        } else {
            orgTree = uaaService.getOrgFullTreeByTenant(selectedTenant == null ? UserContextService.getCurrentUserAccount().getTenantUID() : selectedTenant.getUid());
        }
        TreeNodeUtil.sortTreeNodeByName(orgTree);

        TreeNode currentOrgNode = TreeNodeUtil.findNodeFromTree(orgTree, currentOrg);

        if (null != currentOrgNode) {
            TreeNodeUtil.expandParentsNodes(currentOrgNode);
        }
        
        TreeNode selectedOrgNode = TreeNodeUtil.findNodeFromTree(orgTree, selected);
        if (null != selectedOrgNode) {
            TreeNodeUtil.expandParentsNodes(selectedOrgNode);
        }
        try {
//            this.selectedNode = (TreeNode)orgTree.getData();
            // this.selectedNode.setExpanded(true);
        } catch (Exception ex) {
        }
    }

    public void filterNode() {
        if (!StringUtils.isEmpty(retrieval)) {
            //判断登陆用户是否是LocalITAdmin(本地后台运营管理)
            if (UserContextService.isOnlyLocalITAdmin()) {
                orgTree = uaaService.getFilterOrgTreeByYuanQu(loginUser.getSiteUID(), retrieval);
            } else {
                orgTree = uaaService.getFilterOrgTreeByYuanQu(selectedTenant == null ? UserContextService.getCurrentUserAccount().getTenantUID() : selectedTenant.getUid(), retrieval);
            }

            TreeNodeUtil.expandTreeNode(orgTree);
        } else {//列出所有机构
            //orgTree = uaaService.getOrgFullTreeByTenant(selectedTenant==null?UserContextService.getCurrentUserAccount().getTenantUID():selectedTenant.getUid());
            this.buildOrdTree();
        }

    }

    public List<OrgInfo> getHospitalListBySiteId() {
        return uaaService.getHospitalListBySiteId(tenantId);
    }

    private TreeNode orgTree;

    public TreeNode getOrgTree() {
        return orgTree;
    }
    private TreeNode selectedNode;

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void prepareCreateInstitution() {
        orgName = null;
        orgNameEn = null;
        OrgInfo org = new OrgInfo();
        org.setSiteId(tenantId);

        //UIDs
        org.setUid(UUID.randomUUID().toString().replace("-", ""));
        org.setTenantUID(selectedTenant.getUid());
        org.setInstitutionUID(org.getUid());
        org.setHospitalUID(org.getUid());
        org.setSiteUID(org.getUid());

        org.setOrgLevel(1);
        org.setOrgType(1);

        org.setParentUID(selectedTenant.getUid());
        org.setParentOrg(dao.getByUid(selectedTenant.getUid()));
        this.selected = org;
    }

    public void prepareCreateHospital() {
        orgName = null;
        orgNameEn = null;
        if (selected == null) {
            return;
        }

        OrgInfo org = new OrgInfo();
        org.setParentOrg(selected);
        org.setParentUID(selected.getUid());

        org.setSiteId(selected.getSiteId());
        org.setHospitalId(selected.getId());

        //UIDs
        org.setUid(UUID.randomUUID().toString().replace("-", ""));
        org.setTenantUID(selectedTenant.getUid());
        org.setInstitutionUID(selected.getInstitutionUID());
        org.setHospitalUID(org.getUid());
        org.setSiteUID(org.getUid());

        org.setOrgLevel(2);
        org.setOrgType(2);
        this.selected = org;
    }

    public void prepareCreateSite() {
        orgName = null;
        orgNameEn = null;
        if (selected == null) {
            return;
        }

        OrgInfo org = new OrgInfo();
        org.setParentOrg(selected);
        org.setParentUID(selected.getUid());

        org.setSiteId(selected.getSiteId());
        org.setHospitalId(selected.getId());

        //UIDs
        org.setUid(UUID.randomUUID().toString().replace("-", ""));
        org.setTenantUID(selectedTenant.getUid());
        org.setInstitutionUID(selected.getInstitutionUID());
        org.setHospitalUID(selected.getHospitalUID());
        org.setSiteUID(org.getUid());

        org.setOrgLevel(3);
        org.setOrgType(3);
        this.selected = org;
    }

    public void prepareCreateDepartment() {
        orgName = null;
        orgNameEn = null;
        if (selected == null) {
            return;
        }

        OrgInfo org = new OrgInfo();
        org.setParentOrg(selected);
        org.setParentUID(selected.getUid());

        org.setSiteId(selected.getSiteId());
        org.setHospitalId(selected.getId());

        //UIDs
        org.setUid(UUID.randomUUID().toString().replace("-", ""));
        org.setTenantUID(selectedTenant.getUid());
        org.setInstitutionUID(selected.getInstitutionUID());
        org.setHospitalUID(selected.getHospitalUID());
        org.setSiteUID(selected.getSiteUID());

        org.setOrgLevel(selected.getOrgLevel());
        org.setOrgType(4);
        this.selected = org;
    }

//    private boolean exist(List<OrgInfo> orgTypeList) {
//        for (OrgInfo orgInfo : orgTypeList) {
//            if (orgInfo.getNameEn() != null) {
//                
//            };
//            
//            if (orgInfo.getName().equalsIgnoreCase(orgName) && orgNameEn.equalsIgnoreCase(orgInfo.getNameEn())) {
//                WebUtil.addErrorMessage(orgName + "已经存在");
//                return true;
//            }
//        }
//        return false;
//    }

    public boolean checkName() {

        OrgInfo orgInfo = dao.findFirstByParentUIDAndShortName(selected.getParentUID(), orgName);
        if (orgInfo != null) {
            if (selected.getId() == null) {
                //new
                WebUtil.addErrorMessage(orgName + "已经存在");
                return true;

            } else {
                //update
                if (selected.getId().intValue() != orgInfo.getId().intValue()) {
                    WebUtil.addErrorMessage(orgName + "已经存在");
                    return true;
                }
            }
        }

        return false;
    }

    public void saveSelectedOrg() {

        //判断是否有权限(只能操作当前登陆用户级别以下的和自己相同siteuid的数据)
        if (!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), selected)) {
            return;
        }

        //重复增加不被允许
        boolean isDuplicate = checkName();
        if (isDuplicate) return;

        boolean isNew = selected.getId() == null ? true : false;

        selected.setShortName(orgName);
        String name = orgInfoService.buildFullPathOrgName(selected);
        selected.setName(name);
        selected.setNameEn(orgNameEn);
        //如果更新的或者添加的org没有重名,就保存或更新
        super.save();

        if (isNew) {
            //如果是增加小科室，并且它的父科室已经属于用户，则把小科室的权限可见性也加进去。
            if (selected.getOrgType() == 4 && selected.getParentOrg().getOrgType() == 4) {

                List<String> userUids = userOrgDao.findUserUidListByOrgUid(selected.getParentOrg().getUid());
                for (String userUid: userUids) {
                    UserOrgInfo found = userOrgDao.findFirstByUserUidAndOrgUid(userUid, selected.getUid());
                    if (found == null) {
                        found = new UserOrgInfo();
                        found.setUserUid(userUid);
                        found.setOrgUid(selected.getUid());
                        found.setOrgType(4);
                        userOrgDao.save(found);
                    }
                }
            }
        } else {
            assetInfoRepository.updateClinicalName(name, selected.getUid());
        }

        initWorkflowConfig();
        buildOrdTree();
        orgName = null;
        orgNameEn = null;
    }

    public void initWorkflowConfig() {
        if (!selected.getHospitalId().equals(selected.getId())) {
            return;
        }

        WorkflowConfigRepository wfConfigDao = WebUtil.getBean(WorkflowConfigRepository.class);
        WorkflowConfig wfConfig = wfConfigDao.findById(selected.getHospitalId());
        if (wfConfig == null) {
            wfConfig = new WorkflowConfig();
            wfConfig.setHospitalId(selected.getHospitalId());
            wfConfig.setSiteId(selected.getSiteId());

            wfConfig.setDispatchMode(1);
            wfConfig.setTimeoutDispatch(30);
            wfConfig.setTimeoutAccept(30);
            wfConfig.setTimeoutRepair(300);  //5 hours
            wfConfig.setTimeoutClose(30);
            wfConfig.setOrderReopenTimeframe(7); //day
            wfConfig.setMaxMessageCount(2);

            wfConfigDao.save(wfConfig);
        }
    }

    public void deleteSelectedOrg() {
        if (canDelete()) {


            super.delete();
            buildOrdTree();
        }
    }

    private Boolean canDelete() {
        //判断是否有权限(只能操作当前登陆用户级别以下的和自己相同siteuid的数据)
        if (!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), selected)) {
            return false;
        }

        List<SearchFilter> checkFilters = new ArrayList<>();
        checkFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("orgInfoUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        List<UserAccount> userList = userDao.findBySearchFilter(checkFilters);
        if (userList != null && !userList.isEmpty()) {
            WebUtil.addErrorMessage("该组织下有用户存在,不能删除.");
            return false;
        }
        AssetInfoRepository assetDao = WebUtil.getBean(AssetInfoRepository.class);
        checkFilters.clear();
        checkFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        List<AssetInfo> assetList = assetDao.findBySearchFilter(checkFilters);
        if (assetList != null && !assetList.isEmpty()) {
            WebUtil.addErrorMessage("该组织下有资产存在,不能删除.");
            return false;
        }
        QrCodeLibRepository qrcodeLibDao = WebUtil.getBean(QrCodeLibRepository.class);
        checkFilters.clear();
        checkFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("orgId", SearchFilter.Operator.EQ, selected.getId(), "1+"));
        List<QrCodeLib> qrcodeList = qrcodeLibDao.findBySearchFilter(checkFilters);
        if (qrcodeList != null && !qrcodeList.isEmpty()) {
            WebUtil.addErrorMessage("该组织下有二维码存在,不能删除.");
            return false;
        }
        return true;
    }
    private String preName;

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selected = (OrgInfo) node.getData();
        preName = selected.getShortName();
        orgName = selected.getShortName();
        orgNameEn = selected.getNameEn();
        if (selected != null) {
            if (selected.getOrgType() == 4) {
                acShow = true;
            } else {
                acShow = false;
            }
        }
    }

    @Override
    public void onBeforeNewObject(OrgInfo object) {
        object.setHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
    }

    @Override
    protected boolean onPersistException(Exception ex, OrgInfo org) {
        if (ex.getClass().equals(DataIntegrityViolationException.class)) {
            DataIntegrityViolationException exFkey = (DataIntegrityViolationException) ex;
            if (exFkey.getRootCause().getMessage().toLowerCase().contains("foreign key constraint ")) {
                String errMessage = WebUtil.getMessage("ForeighKeyErrorWithOrgId");
                WebUtil.addErrorMessage(errMessage);
                return true;
            }
        }

        return false;
    }

    public boolean getAcShow() {
        return acShow;
    }

    public void setAcShow(boolean acShow) {
        this.acShow = acShow;
    }

    public boolean getIsCreateInstitutionDisabled() {
        return UserContextService.isOnlyLocalITAdmin();
    }

    public boolean getIsCreateHospitalDisabled() {
        return (selected == null) || (selected.getOrgType() == null) || (selected.getOrgType() != 1) || selected.getId() == null;
    }

    public boolean getIsCreateSiteDisabled() {
        return (selected == null) || (selected.getOrgType() == null) || (selected.getOrgType() != 2) || selected.getId() == null;
    }

    public boolean getIsCreateDepartmentDisabled() {
        return (selected == null) || (selected.getOrgType() == null) || selected.getId() == null || orgInfoService.getClinicalLevel(selected) >=3;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getPreName() {
        return preName;
    }

    public void setPreName(String preName) {
        this.preName = preName;
    }

    public String getOrgNameEn() {
        return orgNameEn;
    }

    public void setOrgNameEn(String orgNameEn) {
        this.orgNameEn = orgNameEn;
    }

    public String getRetrieval() {
        return retrieval;
    }

    public void setRetrieval(String retrieval) {
        this.retrieval = retrieval;
    }
}
