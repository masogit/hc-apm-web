package com.ge.apm.view.uaa;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.orginfo.BuildManagementService;
import com.ge.apm.service.orginfo.OrgInfoClinicalDeptService;
import com.ge.apm.service.orginfo.OrgInfoService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.MessageUtil;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class BuildManagementController extends JpaCRUDController<OrgInfo> {

    private OrgInfoRepository dao = null;
    private UaaService uaaService;
    private AssetInfoRepository assetInfoRepository;
    private OrgInfoService orgInfoService;
    private OrgInfoClinicalDeptService clinicalDeptService;
    private UserOrgInfoRepository userOrgDao;
    private UserContextService userContextService;
    private BlobFileService blobObjectService;
    private UserCommonService userCommonService;
    private TenantInfoRepository tenantInfoRepository;
    private BuildManagementRepository buildManagementRepository;
    private BuildManagementService buildManagementService;

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
    private TenantInfo selectedTenant;
    @Getter @Setter
    private BuildManagement selectBuild;
    @Getter @Setter
    private Integer chooseType;
    @Getter @Setter
    private OrgInfo selectSite;
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
        userOrgDao = WebUtil.getBean(UserOrgInfoRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        blobObjectService = WebUtil.getBean(BlobFileService.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        buildManagementRepository = WebUtil.getBean(BuildManagementRepository.class);
        buildManagementService = WebUtil.getBean(BuildManagementService.class);
        findPerson();
        loginUser = UserContextService.getCurrentUserAccount();
        setTenantId(loginUser.getSiteId());
        currentOrg = dao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        countyArea = userContextService.isCountyArea();
        //buildOrdTree();
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
        if (this.selected != null && Objects.equals(this.selected.getOrgType(), 4)) {//类型是科室
            if (this.selected.getParentOrg() != null) {
                userList = userDao.findBySiteUID(this.selected.getSiteUID());
            }
        }
        return userList;
    }

    private void componentFloor(TreeNode orgTree) {
        orgTree.getChildren().forEach(val -> {
            BuildManagement buildManagement = (BuildManagement) val.getData();
            List<BuildManagement> buildFloorList = Optional.ofNullable(buildManagementRepository.findByParent(buildManagement.getId())).orElse(new ArrayList<>());
            buildFloorList.forEach(item -> val.getChildren().add(new DefaultTreeNode("org", item, null)));
            componentRoom(val);
        });
    }

    private void componentRoom(TreeNode orgTree) {
        orgTree.getChildren().forEach(val -> {
            BuildManagement buildManagement = (BuildManagement) val.getData();
            List<BuildManagement> buildFloorList = Optional.ofNullable(buildManagementRepository.findByParent(buildManagement.getId())).orElse(new ArrayList<>());
            buildFloorList.forEach(item -> val.getChildren().add(new DefaultTreeNode("org", item, null)));
        });
    }

    private void componentBuild(TreeNode orgTree) {
        if (orgTree.getData() != null) {
            OrgInfo org = (OrgInfo) orgTree.getData();
            if (Objects.equals(org.getOrgType(), 3)) {
                if (Objects.equals(org.getOrgLevel(), 3)) {
                    orgTree.getChildren().clear();
                    //添加楼宇
                    List<BuildManagement> buildManagementList = buildManagementRepository.findBySiteUID(org.getUid());
                    if (!CollectionUtils.isEmpty(buildManagementList)) {
                        buildManagementList.forEach(val -> orgTree.getChildren().add(new DefaultTreeNode("org", val, null)));
                        componentFloor(orgTree);
                    }
                } else {
                    getChildren(orgTree);
                }
            } else {
                getChildren(orgTree);
            }
        }
    }

    private void getChildren(TreeNode orgTree) {
        ArrayList<TreeNode> treeNodes = new ArrayList<>();
        orgTree.getChildren().stream().forEach(item -> {
            OrgInfo org = (OrgInfo) item.getData();
            if(Objects.equals(org.getOrgType(), 4)){
                treeNodes.add(item);
                return;
            }
            componentBuild(item);
            if (!CollectionUtils.isEmpty(item.getChildren())) {
                item.getChildren().stream().forEach(child1 -> {
                    if (!(child1.getData() instanceof BuildManagement)) {
                        componentBuild(child1);
                    }
                });
            }
        });
        orgTree.getChildren().removeAll(treeNodes);
    }

    public void buildOrdTree() {
        if (selectedTenant!=null&&Objects.equals(selectedTenant.getId(),46)) {
            orgTree = null;
            return;
        }
        TreeNode tenantOrgTree = uaaService.getOrgFullTreeByTenant(selectedTenant == null ? UserContextService.getCurrentUserAccount().getTenantUID() : selectedTenant.getUid());
        TreeNodeUtil.sortTreeNodeByName(tenantOrgTree);
        //递归查询院区楼栋并清空科室
        getChildren(tenantOrgTree);
        orgTree = tenantOrgTree;
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
        if (selectedTenant!=null&&Objects.equals(selectedTenant.getId(),46)) {
            List<OrgInfo> siteList = uaaService.getUserAccessibleSiteListWithClinicalDept(loginUser.getUid(), retrieval, selectedTenant.getId());
            OrgInfo tenant = dao.findByUid(selectedTenant.getUid());
            siteList.add(tenant);
            orgTree = uaaService.getOrgTreeNodes(selectedTenant.getUid(), siteList);
            expendedTreeNode(orgTree, retrieval, -1, -1);
        }else{
            filterTreeNode(orgTree, retrieval, -1, -1);
        }
    }
    public void filterTreeNode(TreeNode node, String filterKeyword, int maxOrgType, int maxOrgLevel){
        // filter tree node by setting node type to "hidden" for invisible nodes and  "org" for visible nodes
        if(filterKeyword==null) filterKeyword = "";
        boolean isKeywordEmpty = "".equals(filterKeyword);

        ArrayList<TreeNode> nodeList = new ArrayList<>();
        doFilterTreeNode(nodeList, node, filterKeyword, maxOrgType, maxOrgLevel);
        nodeList.forEach(item->{
            item.setType("org");
            if(!isKeywordEmpty) item.setExpanded(true); // Auto expand tree nodes
        });
    }
    private void doFilterTreeNode(ArrayList<TreeNode> nodeList, TreeNode node, String filterKeyword, int maxOrgType, int maxOrgLevel){
        node.getChildren().forEach(item->{
            doFilterTreeNode(nodeList, item, filterKeyword, maxOrgType, maxOrgLevel);
            item.setType("hidden");
            Object object = item.getData();
            OrgInfo org=new OrgInfo();
            BeanUtils.copyProperties(object,org);
            if(!org.getName().contains(filterKeyword) && (org.getNamePyCode()==null?true:!org.getNamePyCode().contains(filterKeyword))) return;
            nodeList.add(item);
            TreeNode pNode = node;
            while(pNode!=null){
                nodeList.add(pNode);
                pNode = pNode.getParent();
            }
        });
    }
    public void expendedTreeNode(TreeNode node, String filterKeyword, int maxOrgType, int maxOrgLevel){
        // filter tree node by setting node type to "hidden" for invisible nodes and  "org" for visible nodes
        if(filterKeyword==null) filterKeyword = "";
        boolean isKeywordEmpty = "".equals(filterKeyword);

        ArrayList<TreeNode> nodeList = new ArrayList<>();
        doExpendedTreeNode(nodeList, node, filterKeyword, maxOrgType, maxOrgLevel);
        nodeList.forEach(item->{
            item.setType("org");
            if(!isKeywordEmpty) item.setExpanded(true); // Auto expand tree nodes
        });
    }

    private void doExpendedTreeNode(ArrayList<TreeNode> nodeList, TreeNode node, String filterKeyword, int maxOrgType, int maxOrgLevel){
        node.getChildren().forEach(item->{
            doExpendedTreeNode(nodeList, item, filterKeyword, maxOrgType, maxOrgLevel);

            item.setType("hidden");
            Object object = item.getData();
            OrgInfo org=new OrgInfo();
            BeanUtils.copyProperties(object,org);

            //if(!org.getName().contains(filterKeyword) && !org.getNamePyCode().contains(filterKeyword)) return;
//            if(maxOrgType >=0 && org.getOrgType()>maxOrgType) return;
//            if(maxOrgLevel >=0 && org.getOrgLevel()>maxOrgLevel) return;

            nodeList.add(item);
            TreeNode pNode = node;
            while(pNode!=null){
                nodeList.add(pNode);
                pNode = pNode.getParent();
            }
        });
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

    public void prepareCreate(int buildType) {
        if (selectedNode == null) {
            MessageUtil.outPutReturnFacesMessage("请选择一列", FacesMessage.SEVERITY_INFO);
            return;
        }
        BuildManagement buildManagement = new BuildManagement();
        isShow = false;
        orgName = null;
        orgNameEn = null;
        address = null;
        contactPerson = null;
        contactPhone = null;
        buildManagement.setUid(UUID.randomUUID().toString().replace("-", ""));
        buildManagement.setTenantUID(selectedTenant.getUid());
        buildManagement.setBuildType(buildType);
        buildManagement.setSiteId(tenantId);
        switch (buildType) {
            case 4:
                if (selectedNode.getData() instanceof  OrgInfo) {
                    OrgInfo org = (OrgInfo) selectedNode.getData();
                    if (!Objects.equals(org.getOrgType(), 3)) {
                        MessageUtil.outPutReturnFacesMessage("请选择院区", FacesMessage.SEVERITY_INFO);
                        return;
                    }
                    selectSite = org;
                    buildManagement.setInstitutionUID(org.getUid());
                    buildManagement.setHospitalUID(org.getUid());
                    buildManagement.setSiteUID(org.getUid());
                    selectBuild = buildManagement;
                } else {
                    MessageUtil.outPutReturnFacesMessage("请选择院区", FacesMessage.SEVERITY_INFO);
                    return;
                }
                break;
            case 5:
                if (selectedNode.getData() instanceof  BuildManagement) {
                    BuildManagement building = (BuildManagement) selectedNode.getData();
                    if (building.getBuildType() != 4) {
                        MessageUtil.outPutReturnFacesMessage("请选择楼栋", FacesMessage.SEVERITY_INFO);
                        return;
                    }
                    buildManagement.setInstitutionUID(building.getUid());
                    buildManagement.setHospitalUID(building.getUid());
                    buildManagement.setParentBuild(building);
                    selectBuild = buildManagement;
                } else {
                    MessageUtil.outPutReturnFacesMessage("请选择楼栋", FacesMessage.SEVERITY_INFO);
                    return;
                }
                break;
            case 6:
                if (selectedNode.getData() instanceof  BuildManagement) {
                    BuildManagement floor = (BuildManagement) selectedNode.getData();
                    if (floor.getBuildType() != 5) {
                        MessageUtil.outPutReturnFacesMessage("请选择楼层", FacesMessage.SEVERITY_INFO);
                        return;
                    }
                    buildManagement.setInstitutionUID(floor.getUid());
                    buildManagement.setHospitalUID(floor.getUid());
                    buildManagement.setParentBuild(floor);
                    selectBuild = buildManagement;
                } else {
                    MessageUtil.outPutReturnFacesMessage("请选择楼层", FacesMessage.SEVERITY_INFO);
                    return;
                }
                break;
        }
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
                if (!Objects.equals(selected.getId(), orgInfo.getId())) {
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

    public void saveSelectedOrg() {
        selectBuild.setShortName(selectBuild.getName());
        buildManagementRepository.save(selectBuild);
        selectBuild = null;
        buildOrdTree();
        orgName = null;
        orgNameEn = null;
        hospitalUcmid = null;
        address = null;
        contactPerson = null;
        contactPhone = null;
    }

    public void buildOrdTree46AfterSave(String institutionUID) {
        List<OrgInfo> siteList = uaaService.getUserAccessibleSiteListByUid(institutionUID);
        OrgInfo tenant = dao.findByUid(selectedTenant.getUid());
        siteList.add(tenant);
        orgTree = uaaService.getOrgTreeNodes(selectedTenant.getUid(), siteList);
        uaaService.expendedTreeNode(orgTree, "all", -1, -1);
    }

    public void deleteSelectedOrg() {
        failureType=null;
        if (canDelete()) {
            int tenantId = selected.getSiteId();
            String institutionUID = selected.getInstitutionUID();
            super.delete();
            if(tenantId==46){
                buildOrdTree46AfterSave(institutionUID);
            }else{
                buildOrdTree();
            }
        }
    }
    public void deleteSelectedBuild() {
        failureType=null;
        if (!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), selectSite)) {
            return ;
        }
        if(selectedNode==null || selectedNode.getData()==null){
            return;
        }
        if (selectedNode.getData() instanceof  BuildManagement) {
            BuildManagement buildManagement = (BuildManagement) selectedNode.getData();
            List<AssetInfo> list;
            List<BuildManagement> deleteIds=new ArrayList<>();
            if(buildManagement.getBuildType()==6){
                 list= assetInfoRepository.findByLocationCode(buildManagement.getNamePyCode());
                deleteIds.add(buildManagement);
            }else{
                List<BuildManagement>  buildManagementList= buildManagementRepository.findByTree(buildManagement.getId());
                List<String> collect = buildManagementList.stream().filter(item->item.getBuildType()==6).map(BuildManagement::getNamePyCode).collect(Collectors.toList());
                list= assetInfoRepository.findByLocationCodeIn(collect);
                deleteIds.addAll(buildManagementList);
            }
           if(CollectionUtils.isNotEmpty(list)){
               WebUtil.addErrorMessage("选中记录已关联设备，请将设备安装位置解绑后再删除！");
               return;
           }
            buildManagementRepository.delete(deleteIds);
            buildOrdTree();
        }else{
            WebUtil.addErrorMessage("请选择楼宇层级");
        }
    }

    private Boolean canDelete() {
        //判断是否有权限(只能操作当前登录用户级别以下的和自己相同siteuid的数据)
        if (!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), selected)) {
            return false;
        }

        boolean canDeleteable;
        if (Objects.equals(selected.getOrgType(), 4)) {
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
            WebUtil.addErrorMessage("该组织下有用户存在,不能删除.");
            return false;
        }

        //检查是否有用户选择了该组织作为可见组织。
        checkFilters.clear();
        checkFilters.add(new SearchFilter("orgUid", SearchFilter.Operator.EQ, selected.getUid(), "1+"));

        UserOrgInfoRepository userOrgDao = WebUtil.getBean(UserOrgInfoRepository.class);
        List<UserOrgInfo> userOrgInfoList = userOrgDao.findBySearchFilter(checkFilters);
        if (!userOrgInfoList.isEmpty()) {
            failureType=2;
            WebUtil.addErrorMessage("该组织下有用户存在,不能删除.");
            return false;
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
        checkFilters.add(new SearchFilter("orgInfoUID", SearchFilter.Operator.IN, clinicalsDeptUids, "1+"));

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
        if (node.getData() instanceof OrgInfo) {
            selected = (OrgInfo) node.getData();
            selectSite = selected;
            preName = selected.getShortName();
            orgName = selected.getShortName();
            orgNameEn = selected.getNameEn();
            hospitalUcmid = selected.getHospitalUcmid();
            status = selected.getStatus();
            address = selected.getAddress();
            contactPerson = selected.getContactPerson();
            contactPhone = selected.getContactPhone();
            if (selected != null) {
                isShow = Objects.equals(selected.getOrgType(), 2);
                acShow = Objects.equals(selected.getOrgType(), 4);
            }
            selectBuild=null;
        }
        if (node.getData() instanceof BuildManagement) {
            selectBuild = (BuildManagement) node.getData();
            if (selectBuild.getBuildType() == 4) {
                selectSite = dao.findByUid(selectBuild.getSiteUID());
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

    public boolean getIsShjyDept() {
        //判断是否是九院科室
        if (selected.getSiteId().equals(SHANGHAIJIUYUAN_SITEID)&&selected.getOrgType().equals(4)){
            return true;
        }
        return false;
    }
    public boolean getIsDelete() {

        if(selectBuild!=null){
            return true;
        }
        return false;
    }

    public List<TenantInfo> getTenantList() {
        List<TenantInfo> tenantInfos;
        if (UserContextService.isSuperAdminWithPartnerAccount()) {
            tenantInfos = tenantInfoRepository.findAllByTestStatusIsNullOrTestStatusNotOrderByName(-1);
        } else {
            tenantInfos = tenantInfoRepository.getAllOrderByName();
        }

        return tenantInfos;

    }
    private StreamedContent excelFile;
    public void exportExcel() throws IOException {
//        ExcelDocument doc = buildManagementService.exportOrg();
        InputStream doc1 = buildManagementService.exportBuildManagement(orgTree);
        excelFile = new DefaultStreamedContent(doc1, "application/vnd.ms-excel", "APM_BuildManagement_Export.xlsx");
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

    public boolean getIsCreateInstitutionDisabled() {
        return true;
    }

    public boolean getIsCreateHospitalDisabled() {
        return (selected == null) || (selected.getOrgType() == null) || !Objects.equals(selected.getOrgType(), 1) || selected.getId() == null;
    }

    public boolean getIsCreateSiteDisabled() {
        return (selected == null) || (selected.getOrgType() == null) || !Objects.equals(selected.getOrgType(), 2) || selected.getId() == null;
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

    public StreamedContent getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(StreamedContent excelFile) {
        this.excelFile = excelFile;
    }
}
