package com.ge.apm.view.uaa;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.orginfo.OrgInfoClinicalDeptService;
import com.ge.apm.service.orginfo.OrgInfoService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.tree.TreeDragDropInfo;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class OrgInfoController extends JpaCRUDController<OrgInfo> {

    OrgInfoRepository dao = null;
    UaaService uaaService;
    private TenantInfo selectedTenant;
    private AssetInfoRepository assetInfoRepository;
    private OrgInfoService orgInfoService;
    private OrgInfoClinicalDeptService clinicalDeptService;
    private UserOrgInfoRepository userOrgDao;
    private UserContextService userContextService;
    private BlobFileService blobObjectService;
    private boolean acShow = false;
    private boolean isShow = false;
    private boolean isShjyDept = false;
    private boolean countyArea = false;

    private String orgName;

    private String orgNameEn;
    private String retrieval;
    private UserAccount loginUser;
    private String hospitalUcmid;
    private Integer status;
    private String  address;
    private String  contactPerson;
    private String  contactPhone;
    private Integer failureType;
    private String orgCode;
    private String gradeNature;

    private String region;
    private UserCommonService userCommonService;
    private ConfigCacheService configCacheService;

    private OrgInfo currentOrg;

    private static final Integer SHANGHAIJIUYUAN_SITEID = 112;

    @Override
    protected void init() {
        filterBySite = false;
        dao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        userCommonService = WebUtil.getBean(UserCommonService.class);
        orgInfoService = WebUtil.getBean(OrgInfoService.class);
        clinicalDeptService = WebUtil.getBean(OrgInfoClinicalDeptService.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        userOrgDao = WebUtil.getBean(UserOrgInfoRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        blobObjectService = WebUtil.getBean(BlobFileService.class);
        findPerson();
        loginUser = UserContextService.getCurrentUserAccount();
        setTenantId(loginUser.getSiteId());
        currentOrg = dao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        countyArea = userContextService.isCountyArea();
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
        List<UserAccount> userList = new ArrayList<>();
        if (this.selected != null && this.selected.getOrgType() == 4) {//类型是科室
            if (this.selected.getParentOrg() != null) {
                userList = userDao.findBySiteUID(this.selected.getSiteUID());
            }
        }
        return userList;
    }

    public void buildOrdTree() {
        if (selectedTenant!=null&&selectedTenant.getId()==46) {
            orgTree = null;
            return;
        }
        //判断登录用户是否是LocalITAdmin(本地后台运营管理)
        if (UserContextService.isOnlyLocalITAdmin()) {
            orgTree = uaaService.getOrgTreeBySite(loginUser.getSiteUID());
        } else {
            orgTree = uaaService.getOrgFullTreeByTenant(selectedTenant == null ? UserContextService.getCurrentUserAccount().getTenantUID() : selectedTenant.getUid());
        }
        //TreeNodeUtil.sortTreeNodeByName(orgTree);

        TreeNode currentOrgNode = TreeNodeUtil.findNodeFromTree(orgTree, currentOrg);

        if (null != currentOrgNode) {
            TreeNodeUtil.expandParentsNodes(currentOrgNode);
        }

        TreeNode selectedOrgNode = TreeNodeUtil.findNodeFromTree(orgTree, selected);
        if (null != selectedOrgNode) {
            TreeNodeUtil.expandParentsNodes(selectedOrgNode);
        }
    }

    public void filterNode() {
        if (selectedTenant!=null&&selectedTenant.getId()==46) {
            List<OrgInfo> siteList = uaaService.getUserAccessibleSiteListWithCondition(retrieval, selectedTenant.getId());
            OrgInfo tenant = dao.findByUid(selectedTenant.getUid());
            siteList.add(tenant);
            orgTree = uaaService.getOrgTreeNodes(selectedTenant.getUid(), siteList);
            uaaService.expendedTreeNode(orgTree, retrieval, -1, -1);
        }else{
            uaaService.filterTreeNode(orgTree, retrieval, -1, -1);
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
        isShow = false;
        orgName = null;
        orgNameEn = null;
        address = null;
        contactPerson = null;
        contactPhone = null;
        orgCode = null;

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
        isShow = true;
        orgName = null;
        orgNameEn = null;
        hospitalUcmid = null;
        address = null;
        contactPerson = null;
        contactPhone = null;
        orgCode=null;

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
        isShow = false;
        orgName = null;
        orgNameEn = null;
        address = null;
        contactPerson = null;
        contactPhone = null;
        orgCode = null;

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
        isShow = false;
        orgName = null;
        orgNameEn = null;
        address = null;
        contactPerson = null;
        contactPhone = null;
        orgCode = null;
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

    public boolean checkHospitalUcmid() {
        if (StringUtils.isEmpty(hospitalUcmid)) {
            return false;
        }
        OrgInfo orgInfo = dao
                .findFirstByTenantUIDAndHospitalUcmid(selected.getTenantUID(), hospitalUcmid);
        if (orgInfo != null&&!hospitalUcmid.equals(selected.getHospitalUcmid())) {
            WebUtil.addErrorMessage(hospitalUcmid + "已经存在");
            return true;
        }
        return false;
    }

    public boolean checkOrgCode() {
        if (StringUtils.isEmpty(orgCode)) {
            return false;
        }
        OrgInfo orgInfo = dao
                .findFirstByTenantUIDAndOrgCode(selected.getTenantUID(), orgCode);
        if (orgInfo != null&&!orgCode.equals(selected.getOrgCode())) {
            WebUtil.addErrorMessage(orgCode + "已经存在");
            return true;
        }
        return false;
    }

    public boolean checkGradeNature() {
        if (StringUtils.isEmpty(gradeNature)) {
            return false;
        }
        OrgInfo orgInfo = dao
                .findFirstByTenantUIDAndOrgCode(selected.getTenantUID(), orgCode);
        if (orgInfo != null&&!orgCode.equals(selected.getOrgCode())) {
            WebUtil.addErrorMessage(orgCode + "已经存在");
            return true;
        }
        return false;
    }

    public void saveSelectedOrg() {
        //判断是否有权限(只能操作当前登录用户级别以下的和自己相同siteuid的数据)
        if (!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), selected)) {
            return;
        }

        //重复增加不被允许
        boolean isDuplicate = checkName();
        if (isDuplicate) return;
        if (selected.getOrgType() == 2 && checkHospitalUcmid()) {
            return;
        }
//        if (selected.getOrgType() == 2 && checkOrgCode()) {
//            return;
//        }
        if (checkOrgCode()) {
            return;
        }
        boolean isNew = selected.getId() == null;

        selected.setShortName(orgName);
        String name = orgInfoService.buildFullPathOrgName(selected);
        selected.setName(name);
        selected.setNameEn(orgNameEn);
        selected.setHospitalUcmid(hospitalUcmid);
        selected.setOrgCode(orgCode);
        selected.setStatus(status);
        selected.setAddress(address);
        selected.setContactPerson(contactPerson);
        selected.setContactPhone(contactPhone);
        selected.setGradeNature(gradeNature);
        selected.setRegion(region);
        //如果更新的或者添加的org没有重名,就保存或更新
        super.save();
        configCacheService.cleanOrgInfoCacheByUid(selected.getUid());

        if (isNew) {
            //如果是增加小科室，并且它的父科室已经属于用户，则把小科室的权限可见性也加进去。
            if (selected.getOrgType() == 4 && selected.getParentOrg().getOrgType() == 4) {

                List<String> userUids = userOrgDao.findUserUidListByOrgUid(selected.getParentOrg().getUid());
                for (String userUid : userUids) {
                    UserOrgInfo found = userOrgDao.findFirstByUserUidAndOrgUid(userUid, selected.getUid());
                    if (found == null) {
                        found = new UserOrgInfo();
                        found.setUserUid(userUid);
                        found.setOrgUid(selected.getUid());
                        found.setOrgType(4);
                        userOrgDao.save(found);
                        configCacheService.cleanOrgInfoCacheByUid(found.getOrgUid());
                    }
                }
            }
        } else {
            if (selected.getOrgType() == 4) {
                updateClinicalDeptOrgName(Objects.requireNonNull(TreeNodeUtil.findNodeFromTree(orgTree, selected)));
            }

        }

        initWorkflowConfig();
        if(selected.getSiteId()==46){
            buildOrdTree46AfterSave(selected.getInstitutionUID());
        }else{
            buildOrdTree();
        }
        orgName = null;
        orgNameEn = null;
        hospitalUcmid = null;
        address = null;
        contactPerson = null;
        contactPhone = null;
        orgCode = null;
        gradeNature = null;
        region = null;
    }

    public void buildOrdTree46AfterSave(String institutionUID) {
        List<OrgInfo> siteList = uaaService.getUserAccessibleSiteListByUid(institutionUID);
        OrgInfo tenant = dao.findByUid(selectedTenant.getUid());
        siteList.add(tenant);
        orgTree = uaaService.getOrgTreeNodes(selectedTenant.getUid(), siteList);
        uaaService.expendedTreeNode(orgTree, "all", -1, -1);
    }

    private void updateClinicalDeptOrgName(TreeNode parentClinicalDeptNode) {

        OrgInfo parentClinicalDeptOrg = (OrgInfo) parentClinicalDeptNode.getData();
        dao.save(parentClinicalDeptOrg);
        configCacheService.cleanOrgInfoCacheByUid(parentClinicalDeptOrg.getUid());
        assetInfoRepository.updateClinicalName(parentClinicalDeptOrg.getName(), parentClinicalDeptOrg.getUid());

        for (TreeNode child : parentClinicalDeptNode.getChildren()) {
            OrgInfo childClinicalDeptOrg = (OrgInfo) child.getData();
            childClinicalDeptOrg.setName(parentClinicalDeptOrg.getName() + "-" + childClinicalDeptOrg.getShortName());
            updateClinicalDeptOrgName(child);
        }

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
        failureType=null;
        if (canDelete()) {
            int tenantId = selected.getSiteId();
            String institutionUID = selected.getInstitutionUID();
            configCacheService.cleanOrgInfoCacheByUid(selected.getUid());
            super.delete();
            if(tenantId==46){
                buildOrdTree46AfterSave(institutionUID);
            }else{
                buildOrdTree();
            }
        }
    }

    private Boolean canDelete() {
        //判断是否有权限(只能操作当前登录用户级别以下的和自己相同siteuid的数据)
        if (!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), selected)) {
            return false;
        }

        boolean canDeleteable;
        if (selected.getOrgType() == 4) {
            canDeleteable = canDeleteClinicalDept();
        } else {
            canDeleteable = canDeleteOrgExceptClinicalDept();
        }

        return canDeleteable;
    }

    private boolean canDeleteOrgExceptClinicalDept() {
        List<SearchFilter> checkFilters = new ArrayList<>();

        //检查是否有用户属于这个组织
        checkFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("orgInfoUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        List<UserAccount> userList = userDao.findBySearchFilter(checkFilters);
        if (userList != null && !userList.isEmpty()) {
            boolean b = userList.stream().anyMatch(item -> item.getIsDeleted() == null || !item.getIsDeleted());
            if(b){
                WebUtil.addErrorMessage("该组织下有用户存在,不能删除.");
                return false;
            }
        }

        //检查是否有用户选择了该组织作为可见组织。
        checkFilters.clear();
        checkFilters.add(new SearchFilter("orgUid", SearchFilter.Operator.EQ, selected.getUid(), "1+"));

        UserOrgInfoRepository userOrgDao = WebUtil.getBean(UserOrgInfoRepository.class);
        List<UserOrgInfo> userOrgInfoList = userOrgDao.findBySearchFilter(checkFilters);
        if (!userOrgInfoList.isEmpty()) {
            List<String> collect = userOrgInfoList.stream().map(UserOrgInfo::getUserUid).collect(Collectors.toList());
            List<UserAccount> userAccounts = userDao.findByUidIn(collect);
            if(CollectionUtils.isNotEmpty(userAccounts)){
                boolean b = userAccounts.stream().anyMatch(item -> item.getIsDeleted() == null || !item.getIsDeleted());
                if(b){
                    failureType=2;
                    WebUtil.addErrorMessage("该组织下有用户存在,不能删除.");
                    return false;
                }
            }
        }

        //检查该组织是否已经有资产属于该组织。
        checkFilters.clear();
        checkFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false, "1*"));
        checkFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, selected.getUid(), "1*2+"));
        checkFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.EQ, selected.getUid(), "1*2+"));
        checkFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selected.getUid(), "1*2+"));
        checkFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selected.getUid(), "1*2+"));
        checkFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selected.getUid(), "1*2+"));
        checkFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selected.getUid(), "1*2+"));

        AssetInfoRepository assetDao = WebUtil.getBean(AssetInfoRepository.class);
        List<AssetInfo> assetList = assetDao.findBySearchFilter(checkFilters);
        if (assetList != null && !assetList.isEmpty()) {
            WebUtil.addErrorMessage("该组织下有资产存在,不能删除.");
            return false;
        }

        //检查该组织下是否有二维码
        checkFilters.clear();
        checkFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
        checkFilters.add(new SearchFilter("orgId", SearchFilter.Operator.EQ, selected.getId(), "1+"));

        QrCodeLibRepository qrcodeLibDao = WebUtil.getBean(QrCodeLibRepository.class);
        List<QrCodeLib> qrcodeList = qrcodeLibDao.findBySearchFilter(checkFilters);
        if (qrcodeList != null && !qrcodeList.isEmpty()) {
            WebUtil.addErrorMessage("该组织下有二维码存在,不能删除.");
            failureType=1;
            return false;
        }

        return true;

    }

    private boolean canDeleteClinicalDept() {

        List<OrgInfo> clinicalsDeptOrg = clinicalDeptService.collectChildClinicalDept(selected);

        List<String> clinicalsDeptUids = clinicalsDeptOrg.stream().map(OrgInfo::getUid).collect(Collectors.toList());

        List<SearchFilter> checkFilters = new ArrayList<>();

        //检查是否有用户属于这个组织
        checkFilters.add(new SearchFilter("orgInfoUID", SearchFilter.Operator.IN, clinicalsDeptUids, "1*"));
        checkFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false, "1*2+"));
        checkFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.IsNull, null, "1*2+"));

        List<UserAccount> userList = userDao.findBySearchFilter(checkFilters);
        if (userList != null && !userList.isEmpty()) {
            WebUtil.addErrorMessage("该组织下有用户存在,不能删除.");
            return false;
        }

        //检查该组织是否已经有资产属于该组织。
        checkFilters.clear();
        checkFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false, "1*"));
        checkFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, clinicalsDeptUids, "1*2+"));
        checkFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, clinicalsDeptUids, "1*2+"));


        AssetInfoRepository assetDao = WebUtil.getBean(AssetInfoRepository.class);
        List<AssetInfo> assetList = assetDao.findBySearchFilter(checkFilters);
        if (assetList != null && !assetList.isEmpty()) {
            WebUtil.addErrorMessage("该组织下有资产存在,不能删除.");
            return false;
        }

        return true;
    }
    public void deleteFailureCode(){
        try {
            List<SearchFilter> checkFilters = new ArrayList<>();
             if(failureType == 1){
                 checkFilters.clear();
                checkFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
                checkFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
                checkFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
                checkFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selected.getUid(), "1+"));
                checkFilters.add(new SearchFilter("orgId", SearchFilter.Operator.EQ, selected.getId(), "1+"));
                QrCodeLibRepository qrcodeLibDao = WebUtil.getBean(QrCodeLibRepository.class);
                List<QrCodeLib> qrcodeList = qrcodeLibDao.findBySearchFilter(checkFilters);
                for (QrCodeLib qrCodeLib : qrcodeList) {
                    qrcodeLibDao.deleteQrCodeLib(qrCodeLib.getUid());
                    List<V2_BlobObject> blobObjectsList = blobObjectService.findBlobObjectsByBoUid(qrCodeLib.getUid());
                    blobObjectsList.forEach(item -> {
                        try {
                            blobObjectService.deleteBlobFile(item);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

                failureType=null;
                WebUtil.addErrorMessage("该组织下二维码已删除");
            }else if(failureType == 2){
                UserOrgInfoRepository userOrgDao = WebUtil.getBean(UserOrgInfoRepository.class);
                userOrgDao.deleteByOrgUId(selected.getUid());
                 failureType=null;
                 WebUtil.addErrorMessage("该组织下用户权限关联已解除");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Integer type=failureType;
            failureType=null;
            String msg="";
            if(type == 1){
                msg="删除组织下二维码失败";
            }else if(type == 2){
                msg="解除组织下用户关联失败";
            }
            WebUtil.addErrorMessage(msg);
        }
    }
    private String preName;

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selected = (OrgInfo) node.getData();
        preName = selected.getShortName();
        orgName = selected.getShortName();
        orgNameEn = selected.getNameEn();
        hospitalUcmid = selected.getHospitalUcmid();
        orgCode = selected.getOrgCode();
        status = selected.getStatus();
        address = selected.getAddress();
        contactPerson = selected.getContactPerson();
        contactPhone = selected.getContactPhone();
        gradeNature = selected.getGradeNature();
        region = selected.getRegion();
        if (selected != null) {
            isShow = selected.getOrgType() == 2;
            acShow = selected.getOrgType() == 4;
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

    public boolean getIsShjyDept() {
        //判断是否是九院科室
        if (selected.getSiteId().equals(SHANGHAIJIUYUAN_SITEID)&&selected.getOrgType().equals(4)){
            return true;
        }
        return false;
    }

    public boolean  onDrop(TreeDragDropInfo info){
        OrgInfo drapNode = (OrgInfo) info.getDragNode().getData();
        OrgInfo dropNode = (OrgInfo) info.getDropNode().getData();
        if(!drapNode.getParentUID().equals(dropNode.getUid())){
            return false;
        }
        //计算最后位置 正 -1 反
        int dropIndex = info.getDropIndex();
        if(drapNode.getOrgIndex() != null){
            if(drapNode.getOrgIndex()  < dropIndex){
                dropIndex--;
            }
        }

        List<OrgInfo> directChildren = uaaService.getDirectChildrenFromTree(orgTree, dropNode.getUid());
        directChildren.removeIf(child -> child.getUid().equals(drapNode.getUid()));

        List<OrgInfo> changes = new ArrayList<>();
        int index = 0;
        for(OrgInfo orgInfo : directChildren){
            if(index == dropIndex){
                drapNode.setOrgIndex(index);
                changes.add(drapNode);
                index++;
            }

            orgInfo.setOrgIndex(index);
            changes.add(orgInfo);
            index++;
        }

        if(CollectionUtils.isNotEmpty(changes)){
            dao.save(changes);
            buildOrdTree();
        }

        return true;
    }

    public boolean getAcShow() {
        return acShow;
    }

    public void setAcShow(boolean acShow) {
        this.acShow = acShow;
    }

    public boolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }

    public String getHospitalUcmid() {
        return hospitalUcmid;
    }

    public void setHospitalUcmid(String hospitalUcmid) {
        this.hospitalUcmid = hospitalUcmid;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public String getGradeNature() {
        return gradeNature;
    }

    public void setGradeNature(String gradeNature) {
        this.gradeNature = gradeNature;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
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
        return (selected == null) || (selected.getOrgType() == null) || selected.getId() == null || orgInfoService.getClinicalLevel(selected) >= 3;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean isShjyDept() {
        return isShjyDept;
    }

    public void setShjyDept(boolean shjyDept) {
        isShjyDept = shjyDept;
    }

    public boolean getCountyArea() {
        return countyArea;
    }

    public void setCountyArea(boolean countyArea) {
        countyArea = countyArea;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Integer getFailureType() {
        return failureType;
    }

    public void setFailureType(Integer failureType) {
        this.failureType = failureType;
    }


}
