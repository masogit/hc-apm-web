package com.ge.apm.view.uaa;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.AssetTypeGroupDto;
import com.ge.apm.service.api.UserApiService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.orginfo.OrgInfoClinicalDeptService;
import com.ge.apm.service.security.UserDetailsServiceImpl;
import com.ge.apm.service.uaa.*;
import com.ge.apm.service.utils.*;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmSystemRole;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import webapp.framework.context.LocaleHolder;
import webapp.framework.dao.BySearchFilterSpecification;
import webapp.framework.dao.SearchFilter;
import webapp.framework.dao.extend.BySearchFilterSpecificationExtend;
import webapp.framework.util.SessionCache;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class UserAccountController extends JpaCRUDController<UserAccount> {

    /**
     *
     */
    private static final long serialVersionUID = -1L;
    public static final String IT_ADMIN_ROLE = "后台运营管理";
    public static final String DEFAULT_USER_ROLE = "临床科室人员";
    public static final String SYSTEM_ACTIVE_ROLE = "系统激活专用";
    public static final String SYSTEM_ACTIVE = "ActivationPerson";

    private static final Integer LX_USER_TYPE = -1; // lixinag usertype
    private boolean isShowButton = true;

    AssetTypeGroupMapper assetTypeGroupMapper;
    UserOrgInfoRepository userOrgInfoRepository;
    UserAccountRepository dao = null;
    UserAccountMapper userAccountMapper;
    UserTagsMapper userTagsMapper;
    UserTagsRepository userTagsDao;
    UaaService uaaService = WebUtil.getBean(UaaService.class);
    UserDetailsService userDetailsService;

    private String tenantUid;

    private TenantInfo selectedTenant;

    private OrgInfo selectedOrg = null;

    private OrgInfoRepository orgInfoDao;

    private OrgInfo selectedUserOrg;

    private Boolean oldIsDisable;

    private UserCommonService userCommonService;

    private UserAccount loginUser;

    private TenantInfoRepository tenantInfoRepository;

    private MultiLevelApprovalConfigRepository multiLevelApprovalConfigRepository;

    private JsonTemplateRepository jsonTemplateRepository;

    private String protocol;

    private String previousOrgUid;  //used in update

    private List<SysRoleVO> roleListVO;

    private List<PermissionGroup> permissionGroups;

    private UserAccountService userAccountService;

    private UserApiService userApi;

    private UserAccountExportService userAccountExportService;

    private StreamedContent exportFile;

    private SysRoleRepository sysRoleRepository;

    private SupplierUserRepository supplierUserRepository;

    private UserAccountHistoryService userAccountHistoryService;

    private CustomRoleRepository customRoleRepository;
    private ZkbIbCrossUserStatusRepository zkbIbCrossUserStatusRepository;
    private UserRoleRepository userRoleRepository;
    private List<OrgInfo> deptList;

    private String userRoleType;

    private List<SysRoleVO> roles;

    private String retrieval;//查询树的关键字

    private List<String> treeDeleteUrgOidList = new ArrayList<>();//存储要删除的orgUids

    private List<OrgInfo> orgInfoTableData46 = new ArrayList<>();//存放页面表格的数据；

    private List<String> unselectInstitutions = new ArrayList<>();

    private List<String> unselectUidss = new ArrayList<>();

    private boolean userAccountEditFlag = true;

    private static org.slf4j.Logger log = LoggerFactory.getLogger(UserAccountController.class);

    TreeNode deleteTreeNode = new DefaultTreeNode("root", null, null);
    boolean deleteDisabled = true;
    boolean selectAllButton = false;
    private OrgInfo orgInfo46 = new OrgInfo();//初始化GE46号节点
    private String originalTelephone = null;//原始
    private Integer originaSelectId;
    private boolean isITAdmin=false;
    private List<OrgInfo> affiliationDepartmentGroup = new ArrayList<>();
    private List<String> childNodeUid=new ArrayList<>();

    private FileDownloadService fileDownloadService;

    public boolean isPropagateSelectionUp() {
        return propagateSelectionUp;
    }

    public void setPropagateSelectionUp(boolean propagateSelectionUp) {
        this.propagateSelectionUp = propagateSelectionUp;
    }

    private boolean propagateSelectionUp = true;


    public Integer getOriginaSelectId() {
        return originaSelectId;
    }

    public void setOriginaSelectId(Integer originaSelectId) {
        this.originaSelectId = originaSelectId;
    }

    public String getOriginalTelephone() {
        return originalTelephone;
    }

    public void setOriginalTelephone(String originalTelephone) {
        this.originalTelephone = originalTelephone;
    }

    public boolean isDeleteDisabled() {
        return deleteDisabled;
    }

    public boolean isUserAccountEditFlag() {
        return userAccountEditFlag;
    }

    public void setUserAccountEditFlag(boolean userAccountEditFlag) {
        this.userAccountEditFlag = userAccountEditFlag;
    }

    public List<String> getUnselectInstitutions() {
        return unselectInstitutions;
    }

    public void setUnselectInstitutions(List<String> unselectInstitutions) {
        this.unselectInstitutions = unselectInstitutions;
    }

    public List<OrgInfo> getOrgInfoTableData46() {
        return orgInfoTableData46;
    }

    public void setOrgInfoTableData46(List<OrgInfo> orgInfoTableData46) {
        this.orgInfoTableData46 = orgInfoTableData46;
    }

    public List<String> getTreeDeleteUrgOidList() {
        return treeDeleteUrgOidList;
    }

    public void setTreeDeleteUrgOidList(List<String> treeDeleteUrgOidList) {
        this.treeDeleteUrgOidList = treeDeleteUrgOidList;
    }

    public String getRetrieval() {
        return retrieval;
    }

    public void setRetrieval(String retrieval) {
        this.retrieval = retrieval;
    }

    private Boolean confirmDestroy = false;

    private AssetTypeGroupService assetTypeGroupService;

    private UserAssetTypeGroupMapper userAssetTypeGroupMapper;

    private UserAssetGroupRepository userAssetGroupDao;
    @Override
    protected void init() {
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);

        this.searchFilters = new ArrayList<>();
        filterBySite = false;
        userOrgInfoRepository = WebUtil.getBean(UserOrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        dao = WebUtil.getBean(UserAccountRepository.class);
        userAccountMapper = WebUtil.getBean(UserAccountMapper.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        userCommonService = WebUtil.getBean(UserCommonService.class);
        userAccountService = WebUtil.getBean(UserAccountService.class);
        userApi = WebUtil.getBean(UserApiService.class);
        userTagsDao = WebUtil.getBean(UserTagsRepository.class);
        userTagsMapper = WebUtil.getBean(UserTagsMapper.class);
        multiLevelApprovalConfigRepository = WebUtil.getBean(MultiLevelApprovalConfigRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        selectedOrg = orgInfoDao.getByUid(loginUser.getOrgInfoUID());
        sysRoleRepository = WebUtil.getBean(SysRoleRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        jsonTemplateRepository = WebUtil.getBean(JsonTemplateRepository.class);
        protocol = jsonTemplateRepository.findByType(3);
        userAccountExportService = WebUtil.getBean(UserAccountExportService.class);
        customRoleRepository = WebUtil.getBean(CustomRoleRepository.class);
        userDetailsService = WebUtil.getBean(UserDetailsServiceImpl.class);
        zkbIbCrossUserStatusRepository = WebUtil.getBean(ZkbIbCrossUserStatusRepository.class);
        userRoleRepository = WebUtil.getBean(UserRoleRepository.class);
        supplierUserRepository = WebUtil.getBean(SupplierUserRepository.class);
        userAccountHistoryService = WebUtil.getBean(UserAccountHistoryService.class);
        assetTypeGroupService= WebUtil.getBean(AssetTypeGroupService.class);

        userAssetTypeGroupMapper = WebUtil.getBean(UserAssetTypeGroupMapper.class);
        userAssetGroupDao = WebUtil.getBean(UserAssetGroupRepository.class);
        assetTypeGroupMapper = WebUtil.getBean(AssetTypeGroupMapper.class);
        setITAdmin();
        if (UserContextService.isSuperAdminWithPartnerAccount()) {
            //if current user belongs to test tenant, then use the first tenant in non test tenant list
            List<TenantInfo> tenantInfos = tenantInfoRepository.findAllByTestStatusIsNullOrTestStatusNotOrderByName(-1);
            setTenantUid(tenantInfos.get(0).getUid());
        } else{
            setTenantUid(loginUser.getTenantUID());
            setITAdminOrgTree();
            updateRoleList();
        }
        //判断是否是理想用户
        if (LX_USER_TYPE.equals(loginUser.getType())) {
            this.isShowButton = Boolean.FALSE;
        } else {
            this.isShowButton = Boolean.TRUE;
        }

    }
    private void setITAdminOrgTree(){
        searchFilters.clear();
        selectedOrg = null;
        selected = null;
        selectedTenant = tenantInfoRepository.findByuid(loginUser.getTenantUID());
        isEnableAssetTypeGroup = selectedTenant.getFeatureList().contains("CONFIG_ASSET_TYPE_GROUP");
        deptList = uaaService.getFullOrgListBySiteId(selectedTenant.getId());
        String orgInfoUID = loginUser.getOrgInfoUID();
        affiliationDepartmentGroup= orgInfoDao.getHospitalChildrenByUids(Arrays.asList(orgInfoUID));
        childNodeUid=affiliationDepartmentGroup.stream().map(OrgInfo::getUid).collect(Collectors.toList());
        orgTree=uaaService.getOrgTreeNodes(orgInfoUID,affiliationDepartmentGroup);
    }


    private void setITAdmin(){
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getRequestURI();
        if("/geapm/portal/uaa/userAccount/ListMain.xhtml".equals(encodeStr)){
            isITAdmin= Boolean.TRUE;
        }
    }
    public boolean isLocale() {
        return "zh".equalsIgnoreCase(LocaleHolder.getLocale().toString());
    }

    private void updateRoleList() {
        roles = new ArrayList<>();
        List<SysRole> sysRoles = Lists.newArrayList(sysRoleRepository.findAll());
        List<SysRoleVO> collect = sysRoles.stream().sorted(Comparator.comparingInt(SysRole::getId)).map(item -> {
            SysRoleVO sysRoleVO = new SysRoleVO();
            sysRoleVO.setId(item.getId());
            sysRoleVO.setName(item.getName());
            sysRoleVO.setRoleType(0);
            sysRoleVO.setRoleDescEn(item.getRoleDescEn());
            sysRoleVO.setRoleDesc(item.getRoleDesc());
            return sysRoleVO;
        }).collect(Collectors.toList());
        roles.addAll(collect);
        if (selectedTenant != null) {
            if (selectedTenant.getInsulate() == null || !selectedTenant.getInsulate()) {
                roles.removeIf(i -> "LocalITAdmin".equals(i.getName()));
            }
        }
        List<CustomRole> customRoles = customRoleRepository.findByTenantUID(loginUser.getTenantUID());
        List<SysRoleVO> customRolesCollect = customRoles.stream().sorted(Comparator.comparingInt(CustomRole::getId)).map(item -> {
            SysRoleVO sysRoleVO = new SysRoleVO();
            sysRoleVO.setId(Math.negateExact(item.getId()));
            sysRoleVO.setName(item.getName());
            sysRoleVO.setRoleType(1);
            String roleDesc = item.getRoleDesc();
            if(StringUtils.isEmpty(roleDesc)){
                roleDesc = item.getName();
            }
            sysRoleVO.setRoleDesc(roleDesc);
            sysRoleVO.setRoleDescEn(roleDesc);
            return sysRoleVO;
        }).collect(Collectors.toList());
        roles.addAll(customRolesCollect);
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
        assetTypeGroupList = assetTypeGroupMapper.findDtoByTenantUid(tenantUid);
        assetTypeGroupMap = assetTypeGroupList.stream().distinct().collect(Collectors.toMap(AssetTypeGroupDto::getUid, t -> t));
        if (selectedTenant != null && selectedTenant.getUid().equals(tenantUid)) return;

        searchFilters.clear();
        selectedOrg = null;
        selected = null;
        selectedTenant = tenantInfoRepository.findByuid(tenantUid);
        isEnableAssetTypeGroup = selectedTenant.getFeatureList().contains("CONFIG_ASSET_TYPE_GROUP");

        deptList = uaaService.getFullOrgListBySiteId(selectedTenant.getId());
        if (selectedTenant != null && selectedTenant.getId() != 46) { // skip building tenant org tree for apm ib user
            rebuildOrgTree();
            TreeNode selectedOrgNode = TreeNodeUtil.findNodeFromTree(orgTree, selectedOrg);
            if (null != selectedOrgNode) {
                selectedOrgNode.setSelected(true);
                TreeNodeUtil.expandParentsNodes(selectedOrgNode);
            }
        } else {
            orgTree = null;
        }
        updateRoleList();
    }

    // TreeNodeName Filter
    private String treeNodeNameFilter;

    public String getTreeNodeNameFilter() {
        return treeNodeNameFilter;
    }

    public void setTreeNodeNameFilter(String treeNodeNameFilter) {
        this.treeNodeNameFilter = treeNodeNameFilter;
    }

    public void rebuildOrgTree() {
        //判断登录用户是否是LocalITAdmin(本地后台运营管理)
        if (UserContextService.isOnlyLocalITAdmin()) {
            orgTree = uaaService.getOrgTreeBySite(loginUser.getSiteUID());
        } else {
            orgTree = uaaService.buildOrgTree(tenantUid, "", -1, -1);
        }
    }

    public void filterTreeNode() {
        if (selectedTenant != null && selectedTenant.getId() == 46) {
            List<OrgInfo> siteList = uaaService.getUserAccessibleSiteListWithClinicalDept(loginUser.getUid(), treeNodeNameFilter, selectedTenant.getId());
            OrgInfo tenant = orgInfoDao.findByUid(selectedTenant.getUid());
            siteList.add(tenant);
            orgTree = uaaService.getOrgTreeNodes(selectedTenant.getUid(), siteList);
            uaaService.expendedTreeNode(orgTree, treeNodeNameFilter, -1, -1);
        } else {
            uaaService.filterTreeNode(orgTree, treeNodeNameFilter, -1, -1);
        }
    }

    public String getOrgFullNameByUid(String orgUid) {
        OrgInfo org = orgInfoDao.getByUid(orgUid);
        return org == null ? "" : getOrgFullName(org);
    }

    public String getOrgFullName(OrgInfo org) {
        if (null == org || org.getId() == -1) {
            return null;
        }
        String name = org.getName();
        while (org.getParentOrg() != null) {
            org = org.getParentOrg();
            name = org.getName() + "/" + name;
        }

        return name;
    }

    public TenantInfo getSelectedTenant() {
        return selectedTenant;
    }

    @Override
    protected UserAccountRepository getDAO() {
        return dao;
    }

    List<SearchFilter> searchFilterList;

    private void generateListSearchFilter() {
        searchFilterList = new ArrayList<>(searchFilters);
        if(isITAdmin){
            itAdminFilter();
        }else{
            superAdminFilter();
        }
        boolean nameFilter = false;
        Object nameValue = "";
        for (SearchFilter filter : searchFilterList) {
            if (filter.fieldName.equals("name")) {
                nameFilter = true;
                nameValue = filter.value;
                filter.conditionPath = "1*2*3+";
            }
        }
        if (nameFilter) {
            nameValue = ((String) nameValue).toLowerCase();
            searchFilterList.add(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, nameValue, "1*2*3+"));
        }
    }
    private void itAdminFilter(){
        if (selectedOrg == null) {
            searchFilterList.add(new SearchFilter("orgInfoUID", SearchFilter.Operator.IN,childNodeUid));
        }else{
            String uid = selectedOrg.getUid();
            Set<String> child=new HashSet<>();
            getSubOrgInfo(uid,child);
            searchFilterList.add(new SearchFilter("orgInfoUID", SearchFilter.Operator.IN, child));
        }
//        if(!UserContextService.checkRole(ApmSystemRole.ITAdmin.name())){
//            List<String> customRolesUser = customRoleRepository.findCustomRolesUser(loginUser.getTenantUID());
//            if(CollectionUtils.isEmpty(customRolesUser)){
//                customRolesUser.add("-1");
//            }
//            searchFilterList.add(new SearchFilter("uid", SearchFilter.Operator.IN,customRolesUser));
//        }
    }
    private void getSubOrgInfo(String uid,Set<String> result){
        result.add(uid);
        affiliationDepartmentGroup.stream()
                .filter(item -> uid.equals(item.getParentUID()))
                .forEach(item->{
                    getSubOrgInfo(item.getUid(),result);
                });
    }
    private void superAdminFilter() {
        if (selectedOrg == null) {
            searchFilterList.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, this.tenantUid));
        } else {
            if (selectedOrg.getId() == -1) {
                searchFilterList.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, this.tenantUid));
            } else {
                searchFilterList.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedOrg.getUid(), "1*2+"));
                searchFilterList.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getUid(), "1*2+"));
                searchFilterList.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getUid(), "1*2+"));
                searchFilterList.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedOrg.getUid(), "1*2+"));
                if (selectedOrg.getOrgType() == 4) {
                    //clinical dept may include child clinical dept
                    OrgInfoClinicalDeptService clinicalDeptService = WebUtil.getBean(OrgInfoClinicalDeptService.class);
                    List<OrgInfo> allClinicals = clinicalDeptService.collectChildClinicalDept(selectedOrg);
                    List<String> clinicalUids = allClinicals.stream().map(OrgInfo::getUid).collect(Collectors.toList());
                    searchFilterList.add(new SearchFilter("orgInfoUID", SearchFilter.Operator.IN, clinicalUids, "1*2+"));

                }
            }
        }
    }

    @Override
    protected Page<UserAccount> loadData(PageRequest pageRequest) {
        generateListSearchFilter();
        Boolean isDisable = false;
        for (SearchFilter searchFilter : searchFilterList) {
            if ("isDisable".equals(searchFilter.fieldName)) {
                isDisable = (Boolean) searchFilter.value;
                break;
            }
        }
        searchFilterList.removeIf(e -> e.fieldName.equals("isDisable"));
        searchFilterList.add(new SearchFilter("isDisable", SearchFilter.Operator.EQ, isDisable, "1*3+"));
        if (!isDisable) {
            searchFilterList.add(new SearchFilter("isDisable", SearchFilter.Operator.IsNull, null, "1*3+"));
        }
        searchFilterList.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false, "1*4+"));
        searchFilterList.add(new SearchFilter("isDeleted", SearchFilter.Operator.IsNull, null, "1*4+"));
        if (ObjectUtil.isNotEmpty(assetTypeUserUidSet)) {
            searchFilterList.add(new SearchFilter("uid", SearchFilter.Operator.IN, assetTypeUserUidSet, "1*"));
        }
        Page<UserAccount> pageUserAccount = getPageUserAccount(this.searchFilterList, pageRequest);
        for (UserAccount user : pageUserAccount) {
            List<OrgInfo> orgListByUid = this.deptList.stream().filter(item -> item.getId().equals(user.getOrgInfoId())).collect(Collectors.toList());
            if (!orgListByUid.isEmpty()) {
                user.setOrgInfo(orgListByUid.get(0));
            }
        }
        return pageUserAccount;
    }
    private Page<UserAccount> getPageUserAccount(final List<SearchFilter> filterFilter, Pageable pageable){
        if (filterFilter == null)
            return dao.findAll(pageable);
        else {
            Specification<UserAccount> spec = BySearchFilterSpecificationExtend.bySearchFilter(filterFilter, UserAccount.class,true);
            return dao.findAll(spec, pageable);
        }
    }

    @Override
    public String getErrorMessageForDuplicateKey(UserAccount userAccount) {
        return String.format(WebUtil.getMessage("DuplicateLoginName"), userAccount.getLoginName());
    }

    @Override
    public String getKeyFieldNameValue(UserAccount obj) {
        return WebUtil.getMessage("login_name") + "=" + obj.getLoginName();
    }

    @Override
    public void setSelected(UserAccount selectedObj) {
        if (selectedObj == null) {
            return;
        }

        this.selected = selectedObj;
        originalTelephone = this.selected.getTelephone();
        originaSelectId = this.selected.getId();
        //fetch tags before display
        List<String> existingTags = userTagsMapper.getTagsByUser(this.selected.getUid());
        this.selected.setTags(existingTags);

    }

    public String Escape(int type) {
        if (1 == type) {
            return "集团";
        } else if (2 == type) {
            return "医院";
        } else if (3 == type) {
            return "院区";
        } else if (4 == type) {
            return "科室";
        } else {
            return "顶级";
        }
    }

    private List<String> allRoles;
    private List<String> assignedRoles;
    private List<String> assignedPermissionGroupUidList;

    public List<String> getAllRoles() {
        return allRoles;
    }

    public void setAllRoles(List<String> allRoles) {
        this.allRoles = allRoles;
    }

    public List<String> getAssignedRoles() {
        return assignedRoles;
    }

    public void setAssignedRoles(List<String> assignedRoles) {
        this.assignedRoles = assignedRoles;
    }

    public void prepareRoleData() {
        if (selected == null) {
            return;
        }
        selected = uaaService.loadUserWithUserRoles(selected.getId());
        roleListVO = new ArrayList<>();//
        assignedRoles = new ArrayList<>();
//        permissionGroups = uaaService.getPermissionGroups(selected.getTenantUID());
        assignedPermissionGroupUidList = uaaService.getPermissionGroupsByUserUid(selected.getUid());
        if (loginUser.getType() != null && loginUser.getType() == UserAccount.LI_XIANG) {
            SysRoleVO sysRoleVO = getVOBySysRoleName(SYSTEM_ACTIVE);
            if (sysRoleVO == null) {
                logger.error("cann't find system role by ActivationPerson");
                return;
            }
            roleListVO.add(sysRoleVO);
            assignedRoles.add(sysRoleVO.getRoleDesc());
        } else {
            roleListVO = uaaService.getSysRoleVO(selected.getTenantUID());

            if (uaaService.isOpenNewMenuRoles(selected.getTenantUID())) {
                Set<String> tenantSysRoles = uaaService.getSysRoleListByTenantUid(selected.getTenantUID());
                Set<String> tenantCustomRoles = uaaService.getTenantCustomRoles(selected.getTenantUID()).stream().map(CustomRole::getName).collect(Collectors.toSet());
                roleListVO = roleListVO.stream().filter(role -> tenantSysRoles.contains(role.getName()) || tenantCustomRoles.contains(role.getName())).collect(Collectors.toList());
            }
            List<CustomRole> customRoles = customRoleRepository.findCustomRolesByUserId(selected.getId());
            if (CollectionUtils.isNotEmpty(customRoles)) {
                for (CustomRole customRole : customRoles) {
                    assignedRoles.add(customRole.getName());
                }
            }
            List<UserRole> sysRoleList = uaaService.getSystemRoles(selected.getId());
            for (UserRole userRole : sysRoleList) {
                assignedRoles.add(userRole.getRole().getRoleDesc());
            }
        }
        prepareUserScope();


        /*TenantInfo ti = tenantInfoRepository.findByuid(selected.getTenantUID());

        //判断登录用户是否是LocalITAdmin(本地后台运营管理)
        if(UserContextService.isOnlyLocalITAdmin()){
            List<String> disableRoleList = new ArrayList<>();
            disableRoleList.add("ITAdmin");
            disableRoleList.add("ActivationPerson");
            //判断租户是否隔离，租户隔离时移除LocalITAdmin
            if(ti.getInsulate() == null || !ti.getInsulate()){
                disableRoleList.add("LocalITAdmin");
            }
            allRoles = uaaService.getSysRoleNamesByDisableList(disableRoleList);
        }else{
            //判断租户是否隔离，租户隔离时移除LocalITAdmin
            if(ti.getInsulate() == null || !ti.getInsulate()){
                List<String> disableRoleList = new ArrayList<>();
                disableRoleList.add("LocalITAdmin");
                allRoles = uaaService.getSysRoleNamesByDisableList(disableRoleList);
            }else{
                allRoles = uaaService.getSysRoleNames();
            }

        }*/
    }

    public SysRoleVO getVOBySysRoleName(String sysRoleName) {
        SysRole sr = sysRoleRepository.getByName(sysRoleName);
        if (sr != null) {
            SysRoleVO srVO = new SysRoleVO();
            srVO.setId(sr.getId());
            srVO.setName(sr.getName());
            srVO.setRoleDesc(sr.getRoleDesc());
            srVO.setRoleType(0);
            return srVO;
        }
        return null;
    }

    //根据orgInfoUid删除对应权限 并且动态更新树形结构。
    public void deleteOrgInfoTable(String orgInfoUid) {
        //1.先找到参数中的位置
        getTreeNodes46(accessOrgTree, orgInfoUid);
        setTreeNodes46(deleteTreeNode, false);
        if (unselectInstitutions.size() == 0) {//如果不在树中间，则只遍历删除当前节点及子节点。
            unselectUidss.add(orgInfoUid);
        }
        while (deleteTreeNode.getParent() != null) {
            deleteTreeNode.getParent().setSelected(false);
            selectedAccessOrgNodesCopy.add(deleteTreeNode.getParent());//放入需要去除的node
            deleteTreeNode = deleteTreeNode.getParent();
        }
        List<TreeNode> nowSelectNode2 = Arrays.asList(selectedAccessOrgNodes);//当前选中的node转换为list方便去掉重复的。
        List<TreeNode> nowSelectNode = new ArrayList<TreeNode>(nowSelectNode2);
        Iterator<TreeNode> it = nowSelectNode.iterator();
        while (it.hasNext()) {
            TreeNode m = it.next();
            if (selectedAccessOrgNodesCopy.contains(m)) {
                it.remove();
            }
        }
        selectedAccessOrgNodes = nowSelectNode.toArray(new TreeNode[0]);
        List<String> orgList = orgInfoDao.getOrgInfoUidsByparentUid(orgInfoUid);
        Iterator<OrgInfo> it2 = orgInfoTableData46.iterator();
        while (it2.hasNext()) {
            OrgInfo m = it2.next();
            if (orgList.contains(m.getUid())) {
                it2.remove();
            }
        }

        Set<OrgInfo> selectedOrgList = new HashSet<>();
        readSelectedOrgTree46(accessOrgTree, selectedOrgList);
        addMissingChildClinical(accessOrgTree, selectedOrgList);
        System.out.println(deleteTreeNode);


    }

    private void getTreeNodes46(TreeNode root, String orgInfoUid) {
        OrgInfo orgInfo = (OrgInfo) root.getData();
        if (root.getParent() != null && orgInfo.getUid().equals(orgInfoUid)) {
            deleteTreeNode = root;
            unselectInstitutions.add(orgInfo.getInstitutionUID());
        } else {
            for (TreeNode item : root.getChildren()) {
                getTreeNodes46(item, orgInfoUid);
            }
        }
    }

    public void saveRoles() {
        if (selected == null) {
            return;
        }
        //如果租户不是院区隔离时，修改用户角色时保证有LocalITAdmin角色的用户，LocalITAdmin角色不丢失
        TenantInfo ti = tenantInfoRepository.findByuid(selected.getTenantUID());
        if ((ti.getInsulate() == null || !ti.getInsulate())) {
            List<UserRole> tempUserRole = selected.getUserRoleList().stream().filter(item -> item.getRole().getName().equals("LocalITAdmin")).collect(Collectors.toList());
            if (!tempUserRole.isEmpty()) {
                this.assignedRoles.add(tempUserRole.get(0).getRole().getRoleDesc());
            }
        }

        //如果登录用户角色是LocalITAdmin，修改用户角色保证有ITAdmin角色的用户，ITAdmin角色不丢失
        if (UserContextService.isOnlyLocalITAdmin()) {
            List<UserRole> tempUserRole = selected.getUserRoleList().stream().filter(item -> item.getRole().getName().equals("ITAdmin")).collect(Collectors.toList());
            if (!tempUserRole.isEmpty()) {
                this.assignedRoles.add(tempUserRole.get(0).getRole().getRoleDesc());
            }
        }

        if (ti.getType() != null && ti.getType().equals(3)) {
            try {
                uaaService.addZkbIbCrossUserStatus(selected, assignedRoles);
                if (ti.getTestStatus() != null && ti.getTestStatus() != -1) {
                    uaaService.addZkbFeSupplierAdminUser(selected, assignedRoles);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        uaaService.setUserRoles(false, selected, assignedRoles);
//        uaaService.setUserPermissionGroup(selected, assignedPermissionGroupUidList);
        updateUserScope();
        userApi.resetUser(selected.getLoginName());
        selected.getUserRoleList().clear();
        userAssetTypeUpdate();
        selected = null;
        WebUtil.addSuccessMessage(WebUtil.getMessage("UserRole") + WebUtil.getMessage("Updated"));
        treeDeleteUrgOidList = new ArrayList();//初始化操作
        orgInfoTableData46 = new ArrayList();//初始化操作
        unselectInstitutions = new ArrayList();
        unselectUidss = new ArrayList();
        deleteTreeNode = new DefaultTreeNode("root", null, null);
        selectedAccessOrgNodesCopy = new ArrayList<TreeNode>();
        deleteDisabled = true;
        selectAllButton = false;
        accessOrgTree = new DefaultTreeNode("root", null, null);
        assetTypeGroupList = assetTypeGroupMapper.findDtoByTenantUid(tenantUid);
        assetTypeGroupMap = assetTypeGroupList.stream().distinct().collect(Collectors.toMap(AssetTypeGroupDto::getUid, t -> t));

    }

    private void userAssetTypeUpdate() {
        Set<AssetTypeGroup> selectedSysAuthGroups = gatherUserAuthGroup();
        updateUserAuthGroup(selected, selectedSysAuthGroups);

    }
    public Set<AssetTypeGroup> gatherUserAuthGroup() {
        Set<AssetTypeGroup> selectedSysAuthGroups = new HashSet<>();
        readSelectedNodesInAssetTypeGroupTree(assetTypeGroupTree, selectedSysAuthGroups);

        return selectedSysAuthGroups;
    }
    private void readSelectedNodesInAssetTypeGroupTree(TreeNode node, Set<AssetTypeGroup> selectedTreeNodeList) {
        if (node.isSelected()) {
            if (node.getParent() != null && node.getParent().isSelected()) {
                return;
            } else {
                selectedTreeNodeList.add((AssetTypeGroup)node.getData());
            }
        } else {
            for (TreeNode item : node.getChildren()) {
                readSelectedNodesInAssetTypeGroupTree(item, selectedTreeNodeList);
            }
        }
    }
    private void updateUserAuthGroup(UserAccount userAccount, Set<AssetTypeGroup> selectedSysAuthGroups) {
        userAssetTypeGroupMapper.removeAuthGroupByUser(userAccount.getUid());


        selectedSysAuthGroups.stream().forEach(item -> {
            UserAssetGroup userAuthGroup = new UserAssetGroup();
            userAuthGroup.setUserUid(userAccount.getUid());
            userAuthGroup.setAssetGroupUid(item.getUid());
//            userAuthGroup.setClassificationPath(item.getClassificationPath());
            userAssetGroupDao.save(userAuthGroup);
        });
        if (loginUser.getLoginName().equals(userAccount.getLoginName())) {
            // 如果修改的用户为当前登录用户，重新设置当前登录用户的资产分类
            UserContextService userContextService = WebUtil.getBean(UserContextService.class);
            userContextService.setAssetTypeGroup();
        }
    }


    public OrgInfo getSelectedOrg() {
        return selectedOrg;
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
        if (selectedNode == null) {
            return;
        }
        this.selectedNode = selectedNode;
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        this.searchFilters.clear();
        selected = null;
        TreeNode node = event.getTreeNode();
        //node.setExpanded(!node.isExpanded());
        selectedOrg = (OrgInfo) node.getData();
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.selectedUserOrg = null;
    }

    @Override
    public void onAfterNewObject(UserAccount userAccount, boolean isOk) {
        if (isOk && "Create".equals(this.crudActionName)) {
            updateUserRole(loginUser.getType());
        }
/*        if (isOk && this.crudActionName.equals("Create")) {
            List<String> role = new ArrayList<String>();
            role.add(DEFAULT_USER_ROLE);
            uaaService.setUserRoles(selected, role);
            uaaService.setDefaultScope(selected);

            //绑定新建用户和该用户所在层级的以下层级
            userOrgInfoList=new ArrayList<UserOrgInfo>();
            accessOrgTree = uaaService.getOrgTreeBySite(selected.getSiteUID());
            List<OrgInfo> orgList = uaaService.getOrgListByUser(selected.getUid());
            setSelectedOrgTree(accessOrgTree, orgList);
            prePareForScopeForNewUser(selected,accessOrgTree);
            userOrgInfoList= userOrgInfoList.stream().filter(item-> !item.getOrgUid().equalsIgnoreCase(selected.getOrgInfoUID())).collect(Collectors.toList());
            userOrgInfoRepository.save(userOrgInfoList);
        }*/
    }

    @Override
    public void onBeforeDeleteObject(UserAccount object) {
        uaaService.clearUserScope(object.getUid());
    }

    @Override
    public void onAfterUpdateObject(UserAccount object, boolean isOk) {
        if (isOk && this.crudActionName.equals("Edit")) {
            //YHAAO-4354 keep user scope on edit
            //if org change, delete old user org
            if (!object.getOrgInfoUID().equals(previousOrgUid)) {
//                uaaService.clearUserScope(object.getUid());
//                uaaService.clearUserOrgAfterOrgUpdate(object.getUid());
                uaaService.setDefaultScope(object);
            }
        }
    }

    public void deleteSelectedUser() {
        if (this.selected.getSiteUID() != null && !userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), this.selected.getSiteUID())) {
            return;
        }

        if (isMultiLevelAuditor() || isAuditor()) {
            return;
        }
        if (userAssociationVerification(selected)) {
            return;
        }
        if (canDelete()) {
            String oldLoginName = selected.getLoginName();//Zkb获取旧的LoginName，以用于插入Zkb穿越用户状态表
            selected.setIsDeleted(true);
            selected.setLoginName(selected.getLoginName() + "-" + ZipUtil.makeRandomPassword(5));
            selected.setLastModifiedBy(loginUser.getLoginName());
            selected.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
            selected.setWeChatId(null);
            selected.setTelephone(null);
            userAccountHistoryService.save(UserAccount.HistoryAction.DELETE.name(), selected);
            dao.save(selected);
            supplierUserRepository.deleteUserWxApp(selected.getId());
            WebUtil.addSuccessMessage(WebUtil.getMessage("Deleted"));

            TenantInfo ti = tenantInfoRepository.findByuid(selected.getTenantUID());
            if(ti.getType() != null && ti.getType().equals(3)){
                try{
                    uaaService.deleteZkbIbCrossUserStatus(selected, oldLoginName);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            userAssetGroupDao.deleteByUserUid(selected.getUid());

            /* 物理删除
            log.info("start delete user, uid: {}",this.selected.getUid());
            WebUtil.addSuccessMessage(WebUtil.getMessage("Deleted"));
            userAccountService.deleteUserAccount(this.selected);
            userApi.removeUser(selected.getLoginName());*/
        }
    }

    private boolean userAssociationVerification(UserAccount selected) {
        List<String> errorMsg = userAccountService.userAssociationVerification(selected);
        if (CollectionUtils.isNotEmpty(errorMsg)) {
            for (String s : errorMsg) {
                WebUtil.addErrorMessage(s);
            }
            log.info("删除用户失败，msg:{}", JSONObject.toJSONString(errorMsg));
            return true;
        }
        return false;
    }

    private boolean isMultiLevelAuditor() {
        MultiLevelApprovalConfigMapper multiLevelApprovalConfigMapper = WebUtil.getBean(MultiLevelApprovalConfigMapper.class);
        boolean isMultiLevelAuditor = multiLevelApprovalConfigMapper.countApprovalItemIncludeUserUid(selected.getUid()) > 0;
        if (isMultiLevelAuditor) {
            WebUtil.addErrorMessage("该用户为多级审核人，不能操作！");
        }
        return isMultiLevelAuditor;
    }

    private boolean isAuditor() {
        AuditGroupConfigurationMapper auditGroupConfigurationMapper = WebUtil.getBean(AuditGroupConfigurationMapper.class);
        boolean isAuditor = auditGroupConfigurationMapper.countGroupMemberItemIncludeUserUid(selected.getUid()) > 0;
        if (isAuditor) {
            WebUtil.addErrorMessage("该用户为审核人员，不能操作！");
        }
        return isAuditor;
    }

    private boolean canDelete() {
        Integer id = selected.getId();

        WorkflowConfigMapper wfDao = WebUtil.getBean(WorkflowConfigMapper.class);
        Wrapper wrapper = new EntityWrapper<WorkflowConfig>();
        wrapper.eq("dispatch_user_id", id);
        wrapper.or().eq("dispatch_user_id2", id);
        if (wfDao.selectCount(wrapper) > 0) {
            WebUtil.addErrorMessage(WebUtil.getMessage("TheUserIsAssignerAndCannotBeDeleted"));
            return false;
        }

        AssetInfoMapper assetDao = WebUtil.getBean(AssetInfoMapper.class);
        wrapper = new EntityWrapper<AssetInfo>();
        wrapper.eq("is_deleted", false);
        wrapper.andNew();
        wrapper.eq("asset_owner_id", id);
        wrapper.or().eq("asset_owner_id2", id);
        wrapper.or().eq("asset_owner_id3", id);
        wrapper.or().eq("clinical_owner_id", id);
        if (assetDao.selectCount(wrapper) > 0) {
            WebUtil.addErrorMessage(WebUtil.getMessage("TheUserIsEquipmentOwnerAndCannotBeDeleted"));
            return false;
        }

        BiomedGroupUserMapper groupDao = WebUtil.getBean(BiomedGroupUserMapper.class);
        wrapper = new EntityWrapper<BiomedGroupUser>();
        wrapper.eq("user_id", id);
        if (groupDao.selectCount(wrapper) > 0) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ListedInTheGroupOfMintenanceEngineersCannotBeDeleted"));
            return false;
        }

        V2_WorkOrderMapper woDao = WebUtil.getBean(V2_WorkOrderMapper.class);
        wrapper = new EntityWrapper<V2_WorkOrder>();
        wrapper.eq("current_person_id", id);
        wrapper.eq("status", 1);
        if (woDao.selectCount(wrapper) > 0) {
            WebUtil.addErrorMessage(WebUtil.getMessage("TheUserHasAnOnGoingWorkOrderAndCannotBeDeleted"));
            return false;
        }

        return true;
    }

    private boolean isDisable() {
        List<SearchFilter> checkFilters = new ArrayList<>();
        checkFilters.add(new SearchFilter("dispatchUserId", SearchFilter.Operator.EQ, selected.getId(), "1+"));
        checkFilters.add(new SearchFilter("dispatchUserId2", SearchFilter.Operator.EQ, selected.getId(), "1+"));
        WorkflowConfigRepository wfDao = WebUtil.getBean(WorkflowConfigRepository.class);
        List<WorkflowConfig> cfgList = wfDao.findBySearchFilter(checkFilters);
        if (cfgList != null && !cfgList.isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("TheUserIsAssignerAndCannotBeDisable"));
            return false;
        }
        checkFilters.clear();
        checkFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false, "1*"));
        checkFilters.add(new SearchFilter("assetOwnerId", SearchFilter.Operator.EQ, selected.getId(), "1*2+"));
        checkFilters.add(new SearchFilter("assetOwnerId2", SearchFilter.Operator.EQ, selected.getId(), "1*2+"));
        checkFilters.add(new SearchFilter("assetOwnerId3", SearchFilter.Operator.EQ, selected.getId(), "1*2+"));
        checkFilters.add(new SearchFilter("clinicalOwnerId", SearchFilter.Operator.EQ, selected.getId(), "1*2+"));
        AssetInfoRepository assetDao = WebUtil.getBean(AssetInfoRepository.class);
        List<AssetInfo> assetList = assetDao.findBySearchFilter(checkFilters);
        if (assetList != null && !assetList.isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("TheUserIsEquipmentOwnerAndCannotBeDisable"));
            return false;
        }
        checkFilters.clear();
        checkFilters.add(new SearchFilter("userId", SearchFilter.Operator.EQ, selected.getId()));
        BiomedGroupUserRepository groupDao = WebUtil.getBean(BiomedGroupUserRepository.class);
        List<BiomedGroupUser> groupList = groupDao.findBySearchFilter(checkFilters);
        if (groupList != null && !groupList.isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ListedInTheGroupOfMintenanceEngineersCannotBeDisable"));
            return false;
        }

        checkFilters.clear();
        checkFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, selected.getId()));
        checkFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
        V2_WorkOrderRepository woDao = WebUtil.getBean(V2_WorkOrderRepository.class);
        List<V2_WorkOrder> woList = woDao.findBySearchFilter(checkFilters);
        if (woList != null && !woList.isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("TheUserHasAnOnGoingWorkOrderAndCannotBeDisable"));
            return false;
        }

        return true;
    }

    @Override
    public void onBeforeSave(UserAccount user) {
        //remove space charactors.
        String loginName = user.getLoginName();
        if (loginName == null) {
            return;
        }
        loginName = loginName.replace(" ", "");
        user.setLoginName(loginName);
        if (selectedUserOrg != null) {
            //keep old org uid to use in update
            previousOrgUid = user.getOrgInfoUID();
            user.setOrgInfoId(selectedUserOrg.getId());
            user.setOrgInfoUID(selectedUserOrg.getUid());
            user.setSiteId(selectedUserOrg.getSiteId());
            //user.setHospitalId(selectedUserOrg.getHospitalId());
            user.setHospitalId(orgInfoDao.getByUid(selectedUserOrg.getSiteUID()).getId());
            user.setTenantUID(selectedUserOrg.getTenantUID());
            user.setInstitutionUID(selectedUserOrg.getInstitutionUID());
            user.setHospitalUID(selectedUserOrg.getHospitalUID());
            user.setSiteUID(selectedUserOrg.getSiteUID());
            user.setOrgLevel(selectedUserOrg.getOrgLevel());
        }
        if (!user.getIsActive()) {
            TenantInfo tenant = tenantInfoRepository.findByuid(user.getTenantUID());
            if (tenant.getIsEnabled()) {
                user.setIsActive(true);
                user.setActiveTime(new Date());
            } else {
                user.setIsActive(false);
            }
        }
        if (user.getIsLocked()) {
            //lockAccount();
            selected.setIsLocked(true);
            selected.setLockTime(TimeUtil.timeNow().toDate());
        } else {
            //unlockAccount();
            selected.setIsLocked(false);
            selected.setPasswordErrorCount(0);
        }
    }


    @Override
    @Transactional
    public void save() {
        if (selectedUserOrg != null && !userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), selectedUserOrg.getSiteUID())) {
            return;
        }

        Boolean newIsDisable = Boolean.TRUE.equals(selected.getIsDisable());
        if (!newIsDisable.equals(oldIsDisable) && (isMultiLevelAuditor() || isAuditor() || !isDisable())) {
            return;
        }
        if (!StringUtils.isEmpty(selected.getPlainPassword())) {
            String checkResult = PasswordUtil.checkPwd(selected.getPlainPassword());
            if (!StringUtils.isEmpty(checkResult)) {
                WebUtil.addErrorMessage(checkResult);
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("validationFailed", true);
                return;
            }
        }

        if (crudActionName.equals("Edit") && Boolean.TRUE.equals(selected.getIsDisable())) {
            if (userAssociationVerification(selected)) {
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("validationFailed", true);
                return;
            }
        }
        try {
            //input new password in create or edit page, so update
            if (!StringUtils.isEmpty(selected.getPlainPassword())) {
                selected.entryptPassword();

            } else {
                //if new user, use tenant initial password or system default password.
                if (null == selected.getId()) {
                    if (StringUtils.isEmpty(selectedTenant.getInitialPassword())) {
                        selected.setPlainPassword(PasswordUtil.DEFAULT_PWD);
                        selected.entryptPassword();
                    } else {
                        selected.setPassword(selectedTenant.getInitialPassword());
                        selected.setPwdSalt(selectedTenant.getInitialPasswordSalt());
                    }
                }
            }
            boolean flag = userAccountService.checkUser(selected);
            if (!flag) {
                selected.setTelephone(originalTelephone);
                userAccountEditFlag = false;
                return;
            }
            isRepeatedAfter();
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger(UserAccountController.class.getName()).log(Level.SEVERE, null, e);
        }


    }

    private void isRepeatedAfter() {
        userAccountEditFlag = true;
        super.save();
        if (Boolean.TRUE.equals(selected.getIsLocked()) || Boolean.TRUE.equals(selected.getIsDisable())) {
            userApi.removeUser(selected.getLoginName());
        } else {
            userApi.resetUser(selected.getLoginName());
        }
        log.info("save/update user to user_account successful, id: {}", selected.getId());

        saveTags();
        log.info("add new tags to user_tags successful, uid: {}", selected.getUid());

//        if (this.crudActionName.equals("Create")) {
//            updateUserRole(loginUser.getType());
//        }
    }

    private void saveTags() {
        List<String> existingTags = userTagsMapper.getTagsByUser(this.selected.getUid());
        List<String> currentTags = this.selected.getTags();

        List<String> newTags = new ArrayList<>(currentTags);
        newTags.removeAll(existingTags);

        List<String> removeTags = new ArrayList<>(existingTags);
        removeTags.removeAll(currentTags);

        //add new tags
        newTags.stream().distinct().forEach(item -> {
            UserTags tags = new UserTags();
            tags.setUserUid(this.selected.getUid());
            tags.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
            tags.setTag(item);
            userTagsDao.save(tags);
        });

        //remove useless tags
        removeTags.stream().distinct().forEach(item -> userTagsMapper.removeTag(this.selected.getUid(), item));
    }

    //    @Transactional
    public void updateUserRole(Integer creatorUserType) {
        List<String> role = new ArrayList<>();
        if (creatorUserType != null && creatorUserType.equals(LX_USER_TYPE)) {
            role.add(SYSTEM_ACTIVE_ROLE);
        } else {
            Set<String> roles = uaaService.getSysRoleListByTenantUid(selected.getTenantUID());
            if (roles.contains(ApmSystemRole.ClinicalStaff.name())) {
                role.add(DEFAULT_USER_ROLE);
            } else {
                role.add(IT_ADMIN_ROLE);
            }
        }
        uaaService.setUserRoles(true, selected, role);
        uaaService.setDefaultScope(selected);

        //绑定新建用户和该用户所在层级的以下层级
/*        if (46 != selected.getSiteId()) {
            userOrgInfoList = new ArrayList<>();
            accessOrgTree = uaaService.getOrgTreeBySite(selected.getSiteUID());
            List<OrgInfo> orgList = uaaService.getOrgListByUser(selected.getUid());
            setSelectedOrgTree(accessOrgTree, orgList);
            prePareForScopeForNewUser(selected, accessOrgTree);
            userOrgInfoList = userOrgInfoList.stream().filter(item -> !item.getOrgUid().equalsIgnoreCase(selected.getOrgInfoUID())).collect(Collectors.toList());
            userOrgInfoRepository.save(userOrgInfoList);
        }*/

    }

    public void resetPassword() {
        if (this.selected.getSiteUID() != null) {
            //判断操作权限
            if (!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), this.selected.getSiteUID())) {
                return;
            }
        }
        if (StringUtils.isEmpty(selectedTenant.getInitialPassword())) {
            selected.setPlainPassword(PasswordUtil.DEFAULT_PWD);
            try {
                selected.entryptPassword();
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(UserAccountController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            selected.setPassword(selectedTenant.getInitialPassword());
            selected.setPwdSalt(selectedTenant.getInitialPasswordSalt());
        }

        userAccountHistoryService.save(UserAccount.HistoryAction.RESET_PASSWORD.name(), selected);
        dao.save(selected);
        WebUtil.addSuccessMessage(WebUtil.getMessage("ResetSuccess"));
    }

    public void lockAccount() {
        if (this.selected.getSiteUID() != null) {
            //判断操作权限
            if (!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), this.selected.getSiteUID())) {
                return;
            }
        }
        //selected.setIsActive(false);
        selected.setIsLocked(true);
        selected.setLockTime(TimeUtil.timeNow().toDate());
        dao.save(selected);
    }

    public void unlockAccount() {
        if (this.selected.getSiteUID() != null) {
            //判断操作权限
            if (!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), this.selected.getSiteUID())) {
                return;
            }
        }
        //selected.setIsActive(true);
        selected.setIsLocked(false);
        selected.setPasswordErrorCount(0);
        dao.save(selected);
    }

    public void markDeleted() {
        selected.setIsDeleted(true);
        dao.save(selected);
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        selectedUserOrg = orgInfoDao.getByUid(selected.getOrgInfoUID());
        oldIsDisable = selected.getIsDisable() != null && selected.getIsDisable();
        filterTreeNodeByUid();
        if (selected.getId() != null) {
            List<String> existingTags = userTagsMapper.getTagsByUser(selected.getUid());
            this.selected.setTags(existingTags);
        }
    }

    public OrgInfo getSelectedUserOrg() {
        return selectedUserOrg;
    }

    public void onSelectOrgForUser(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedUserOrg = (OrgInfo) node.getData();
    }

    private TreeNode accessOrgTree;
    private TreeNode[] selectedAccessOrgNodes;
    private List<TreeNode> selectedAccessOrgNodesCopy = new ArrayList<TreeNode>();

    private void prepareUserScope() {
        List<OrgInfo> orgList = uaaService.getOrgListByUser(selected.getUid());
        if (46 == selected.getSiteId()) {
            //查询权限结合 给表格填充数据。
            orgInfoTableData46 = orgList;//给表格数据赋值。
            accessOrgTree = new DefaultTreeNode("root", null, null);
            retrieval = null;

        } else {
            retrieval = null;
            propagateSelectionUp = true;
            accessOrgTree = uaaService.getOrgTreeBySite(selected.getSiteUID());
            //List<OrgInfo> orgList = uaaService.getOrgListByUser(selected.getUid());
            setSelectedOrgTree(accessOrgTree, orgList);
            TreeNodeUtil.expandTreeNode(accessOrgTree);
        }
        loadAssetTypeGroup();
    }

    List<UserOrgInfo> userOrgInfoList;

    public void prePareForScopeForNewUser(UserAccount ua, TreeNode node) {
        if (node.getChildren().size() == 0) {
            return;
        }
        for (TreeNode item : node.getChildren()) {
            if (item.isSelected()) {
                OrgInfo orgInfo = (OrgInfo) item.getData();
                UserOrgInfo userOrgInfo = new UserOrgInfo();
                userOrgInfo.setOrgUid(orgInfo.getUid());
                userOrgInfo.setUserUid(ua.getUid());
                userOrgInfo.setOrgType(orgInfo.getOrgType());
                userOrgInfoList.add(userOrgInfo);
            }
            prePareForScopeForNewUser(ua, item);
        }
    }

    private void setSelectedOrgTree(TreeNode node, List<OrgInfo> accessiableOrgList) {
        OrgInfo org = (OrgInfo) node.getData();
        if (accessiableOrgList.contains(org)) {
            node.setSelected(true);
        } else if (node.getParent() != null && node.getParent().isSelected()) {
            node.setSelected(true);
        }
        for (TreeNode item : node.getChildren()) {
            setSelectedOrgTree(item, accessiableOrgList);
        }
    }

    private void readSelectedOrgTree(TreeNode node, Set<OrgInfo> selectedTreeNodeList) {
        if (node.isSelected()) {
            if (node.getParent() != null && node.getParent().isSelected()) {
                return;
            } else {
                selectedTreeNodeList.add((OrgInfo) node.getData());
            }
        } else {
            for (TreeNode item : node.getChildren()) {
                readSelectedOrgTree(item, selectedTreeNodeList);
            }
        }
    }

    private void readSelectedOrgTree46(TreeNode node, Set<OrgInfo> selectedTreeNodeList) {
        if (node.isSelected() && !"0".equals(node.getRowKey())) {
            if (node.getParent() != null) {
                OrgInfo orgInfo = (OrgInfo) node.getData();
                selectedTreeNodeList.add(orgInfo);
                treeDeleteUrgOidList.add(orgInfo.getUid());
            } else {
                return;
            }
        } else {
            for (TreeNode item : node.getChildren()) {
                readSelectedOrgTree46(item, selectedTreeNodeList);
            }
        }
    }

    public void updateUserScope() {

//        Set<OrgInfo> selectedOrgList = new HashSet<>(uaaService.getOrgListByPermissionGroup(ObjectUtil.isNotEmpty(assignedPermissionGroupUidList) ? assignedPermissionGroupUidList : ListUtil.toList("-1")));
        Set<OrgInfo> selectedOrgList = new HashSet<>();
        if (46 == selected.getSiteId()) {
            //only add parent if all children is selected
            readSelectedOrgTree46(accessOrgTree, selectedOrgList);
            //if site is not selected, which means not all clinical are selected. then we add all the selected clinical.
            addMissingChildClinical(accessOrgTree, selectedOrgList);
        } else {
            //only add parent if all children is selected
            readSelectedOrgTree(accessOrgTree, selectedOrgList);
            //if site is not selected, which means not all clinical are selected. then we add all the selected clinical.
            addMissingChildClinical(accessOrgTree, selectedOrgList);
        }

        userAccountService.updateUserOrg(selected, selectedOrgList, treeDeleteUrgOidList, unselectInstitutions, unselectUidss, selectAllButton, orgInfo46, orgInfoTableData46);
    }


    /**
     * 大科室下面可以有小科室，如果选择了把小科室加进来，这样不影响已有的应用
     *
     * @param node
     * @param selectedTreeNodeList
     */
    private void addMissingChildClinical(TreeNode node, Set<OrgInfo> selectedTreeNodeList) {

        OrgInfo data = (OrgInfo) node.getData();
        if (data == null) {
            //root
            for (TreeNode item : node.getChildren()) {
                addMissingChildClinical(item, selectedTreeNodeList);
            }

        } else if (data.getOrgType() < 4) {
            if (!node.isSelected()) {
                for (TreeNode item : node.getChildren()) {
                    addMissingChildClinical(item, selectedTreeNodeList);
                }
            }
        } else if (data.getOrgType() == 5 || data.getOrgType() == 4) {
            //if site is selected, will not traverse here
            if (node.isSelected()) {
                selectedTreeNodeList.add(data);
            }

            //大科室下面可能还有大科室
            for (TreeNode item : node.getChildren()) {
                addMissingChildClinical(item, selectedTreeNodeList);
            }

        }

    }

    public void onSelectAccessTreeNode46(NodeSelectEvent event) {
        if (orgInfoTableData46.size() == 1 && "GE后台运维".equals(orgInfoTableData46.get(0).getName())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", "如要继续操作，请先删除顶级权限！"));
            return;
        }
        setTreeNodes(event.getTreeNode(), true);
        updateOrgInfoTable("1", null);
    }

    private void updateOrgInfoTable(String type, OrgInfo unOrgInfo) {
        //1.先生成对应的关系列表
        Set<OrgInfo> selectedOrgList = new HashSet<>();
        readSelectedOrgTree46(accessOrgTree, selectedOrgList);
        addMissingChildClinical(accessOrgTree, selectedOrgList);
        List<OrgInfo> orginfos = new ArrayList(selectedOrgList);
        //if("2".equals(type)){
        //    orgInfoTableData46 = orginfos;
        //    return;
        //}
        //2.遍历关系列表与orgInfoTable表。如果发现是属于同一个集团的就把表中的字段删掉然后添加上新生成的关系
        Set<String> eqInstitutionUids = new HashSet<String>();//存放相等InstitutionUID的集合，以后统一删除与添加
        for (int i = 0; i < orgInfoTableData46.size(); i++) {//遍历当前表格，如果 institution_uid一样则删掉，换上新的institution_uid的数据
            for (int j = 0; j < orginfos.size(); j++) {
                if (orgInfoTableData46.get(i).getInstitutionUID().equals(orginfos.get(j).getInstitutionUID())) {
                    eqInstitutionUids.add(orgInfoTableData46.get(i).getInstitutionUID());//如果相等则放入集合
                }
            }
        }
        //找到有相同的InstitutionUID以后orgInfoTableData46先删除有此id的数据，再添加selectedOrgList 有此id的数据
        Iterator<OrgInfo> it = orgInfoTableData46.iterator();
        while (it.hasNext()) {
            OrgInfo orgInfo = it.next();
            if (eqInstitutionUids.contains(orgInfo.getInstitutionUID())) {
                it.remove();
            }
        }
        Iterator<OrgInfo> it2 = orgInfoTableData46.iterator();
        if ("2".equals(type)) {
            while (it2.hasNext()) {
                OrgInfo orgInfo = it2.next();
                if (unOrgInfo.getInstitutionUID().equals(orgInfo.getInstitutionUID())) {
                    it2.remove();
                }
            }
        }
        orgInfoTableData46.addAll(orginfos);
    }

    public void filterNode() {
        if (retrieval == null) {
            retrieval = "";
        }
        accessOrgTree = uaaService.getOrgTreeBySite46(selected.getSiteUID(), retrieval);
        List<OrgInfo> orgList = uaaService.getOrgListByUser(selected.getUid());
        setSelectedOrgTree(accessOrgTree, orgList);
        TreeNodeUtil.expandTreeNode(accessOrgTree);
        accessOrgTree.getChildren().get(0).setSelectable(false);
        deleteDisabled = false;
    }

    public void getFilteredNodes() {
        if (StringUtils.isEmpty(retrieval)) {
            propagateSelectionUp = true;
        } else {
            if (!accessOrgTree.getChildren().get(0).isSelected()) {
                propagateSelectionUp = false;
            }
        }
        uaaService.filterTreeNode(accessOrgTree, retrieval, -1, -1);
    }

    //全选按钮 （只保存GE后台运维的uid）(页面上表格也只显示GE后台运维的iD)
    public void selectAll() {
        selectAllButton = true;//1.标为全选
        //2.获取GE后台运维的节点，更新表格，删除按钮置灰
        //deleteDisabled = true;//删除按钮置灰
        orgInfoTableData46.clear();
        OrgInfo orgInfo = (OrgInfo) accessOrgTree.getChildren().get(0).getData();
        orgInfoTableData46.add(orgInfo);//放入GE后台运维节点
        orgInfo46 = orgInfo;//全选的时候只记录顶级用户

    }

    public void onSelectAccessTreeNode(NodeSelectEvent event) {
        setTreeNodes(event.getTreeNode(), true);
    }

    public void onUnselectAccessTreeNode(NodeUnselectEvent event) {
        TreeNode node = event.getTreeNode();
        setTreeNodes(node, false);
        while (node.getParent() != null) {
            node.getParent().setSelected(false);
            node = node.getParent();
        }
    }

    private void setTreeNodes(TreeNode root, Boolean isSelected) {
        root.setSelected(isSelected);
        for (TreeNode item : root.getChildren()) {
            setTreeNodes(item, isSelected);
        }
    }

    public void onUnselectAccessTreeNode46(NodeUnselectEvent event) {
        if (orgInfoTableData46.size() == 1 && "GE后台运维".equals(orgInfoTableData46.get(0).getName())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", "如要继续操作，请先删除顶级权限！"));
            return;
        }
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        unselectInstitutions.add(orgInfo.getInstitutionUID());
        setTreeNodes46(node, false);
        while (node.getParent() != null) {
            node.getParent().setSelected(false);

            node = node.getParent();
        }
        updateOrgInfoTable("2", orgInfo);
    }

    private void setTreeNodes46(TreeNode root, Boolean isSelected) {
        root.setSelected(isSelected);
        selectedAccessOrgNodesCopy.add(root);//放入需要去除的node
        for (TreeNode item : root.getChildren()) {
            setTreeNodes46(item, isSelected);
        }
    }

    public TreeNode getAccessOrgTree() {
        return accessOrgTree;
    }

    public void setAccessOrgTree(TreeNode accessOrgTree) {
        this.accessOrgTree = accessOrgTree;
    }

    public TreeNode[] getSelectedAccessOrgNodes() {
        return selectedAccessOrgNodes;
    }

    public void setSelectedAccessOrgNodes(TreeNode[] selectedAccessOrgNodes) {
        this.selectedAccessOrgNodes = selectedAccessOrgNodes;
    }

    public void roleChangeListener(ValueChangeEvent e) {
        if (e.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
            e.setPhaseId(PhaseId.INVOKE_APPLICATION);
            e.queue();
            return;
        }

        List<String> newValues = (List<String>) e.getNewValue();
        List<String> oldValues = (List<String>) e.getOldValue();
        List<String> newlyCheckedRoles = (List<String>) CollectionUtils.subtract(newValues, oldValues);

        if (newlyCheckedRoles.size() > 0) {
            //new role checked
            String checkedRole = newlyCheckedRoles.get(0);
            if (uaaService.isTemporaryAccount(checkedRole)) {
                if (newValues.size() > 1) {
                    //not only guest account checked
                    this.assignedRoles.clear();
                    this.assignedRoles.add(checkedRole);
                    WebUtil.addSuccessMessage(WebUtil.getMessage("GuestAccountCheckedWarning"));
                }

            } else {
                String tempRoleName = uaaService.getTemporaryAccountIfAlreadyChecked((oldValues));

                if (tempRoleName != null && this.assignedRoles.contains(tempRoleName)) {
                    this.assignedRoles.remove(tempRoleName);
                    WebUtil.addSuccessMessage(WebUtil.getMessage("GuestAccountCheckedWarning"));
                }
            }
        }

    }

    private Boolean agree = false;

    private List<String> items;

    @Transactional
    public void confirmActivation() {
        if (loginUser.getIsActive()) {
            WebUtil.addSuccessMessage(WebUtil.getMessage("ActivateSuccess"));
            return;
        }
        loginUser.setIsActive(true);
        loginUser.setActiveTime(new Date());
        dao.save(loginUser);
        if (hasAuthority()) {
            TenantInfo tenant = tenantInfoRepository.findByuid(loginUser.getTenantUID());
            tenant.setIsEnabled(true);
            tenant.setActiveTime(new Date());
            tenant.setActiveUserId(loginUser.getId());
            tenant.setActiveType(1);
            tenantInfoRepository.save(tenant);
            dao.updateMembersStatus(loginUser.getTenantUID());
            WebUtil.addSuccessMessage(WebUtil.getMessage("ActivateSuccess"));
        } else {
            logger.info("not active person !");
        }

    }

    @Transactional
    public void activeTenant() {
        TenantInfo tenant = tenantInfoRepository.findByuid(loginUser.getTenantUID());
        tenant.setIsEnabled(true);
        tenant.setActiveTime(new Date());
        tenant.setActiveUserId(loginUser.getId());
        tenant.setActiveType(1);
        tenantInfoRepository.save(tenant);
        dao.updateMembersStatus(loginUser.getTenantUID());
        WebUtil.addSuccessMessage(WebUtil.getMessage("ActivateSuccess"));
    }

    public boolean needActive() {
        TenantInfo tenant = tenantInfoRepository.findByuid(loginUser.getTenantUID());
        if (tenant != null && tenant.getIsEnabled() == false) {
            return false;
        }
        return true;
    }

    public boolean hasAuthority() {
        if (loginUser.getUserRoleNames().contains("系统激活专用")) {
            return true;
        }
        TenantInfo tenant = tenantInfoRepository.findByuid(loginUser.getTenantUID());
        if (StringUtils.hasText(tenant.getActiveUsers())) {
            String[] userIds = tenant.getActiveUsers().split(",");
            return Arrays.stream(userIds).anyMatch(String.valueOf(loginUser.getId())::equals);
        }
        return false;
    }

    public void logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
            HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        WebUtil.redirectTo("/login.xhtml");
    }

    public void updateAgree() {
        if (CollectionUtils.isNotEmpty(items)) {
            String str = items.get(0);
            agree = Integer.parseInt(str) == 1 ? true : false;
        } else {
            agree = false;
        }
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public Boolean getAgree() {
        return agree;
    }

    public void setAgree(Boolean agree) {
        this.agree = agree;
    }

    public List<SysRoleVO> getRoleListVO() {
        return roleListVO;
    }

    public void setRoleListVO(List<SysRoleVO> roleListVO) {
        this.roleListVO = roleListVO;
    }

    public void loginAs() {
        if (this.selected == null) return;

        //call microServices to get Access Token...
        try {
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
            MicroServiceUtil msUtil = WebUtil.getBean(MicroServiceUtil.class);

            String accessToken = msUtil.getAuthenticateByUserPasswordEncrypted(selected.getLoginName(), selected.getPassword(), request.getHeader("User-Agent"));
            request.getSession().setAttribute("ACCESS_TOKEN", accessToken);

            //make sure microservice works before apm web
            UserContextService.setLoginUser(this.selected);

            attemptSwitchUserInSpringSecurityContext(this.selected);

        } catch (Exception e) {
            Logger.getLogger(UserAccountController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void attemptSwitchUserInSpringSecurityContext(UserAccount loginAsUser) {
        UserDetails targetUser = userDetailsService.loadUserByUsername(loginAsUser.getLoginName());

        Collection<? extends GrantedAuthority> auths = targetUser.getAuthorities();
        UsernamePasswordAuthenticationToken targetUserRequest = new UsernamePasswordAuthenticationToken(targetUser, targetUser.getPassword(), auths);
        SecurityContextHolder.getContext().setAuthentication(targetUserRequest);
    }

    public void exportAll() {
        generateListSearchFilter();
        List<UserAccount> listUserAccount = getUserList(this.searchFilterList);
        if (listUserAccount == null || listUserAccount.size() == 0) {
            WebUtil.addErrorMessage(WebUtil.getMessage("NoData"));
            return;
        }
        userAccountExportService.supplementUserAccount(listUserAccount);
        downloadFile(userAccountExportService.parseUserAccountToMap(listUserAccount));
    }
    private List<UserAccount> getUserList(final List<SearchFilter> filterFilter){
        if(filterFilter == null){
            return (List<UserAccount>) dao.findAll();
        }else{
            Specification<UserAccount> spec = BySearchFilterSpecificationExtend.bySearchFilter(filterFilter, UserAccount.class,true);
            return dao.findAll(spec);
        }


    }
    private void downloadFile(List<Map<String, Object>> data) {
        try {
//            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/uaa/userAccount/APM_UserAccount_Template.xlsx");
            InputStream stream = fileDownloadService.getTemplateFileInputStream(SystemTemplateEnum.USER_ACCOUNT_EXPORT.getCode(), SystemTemplateEnum.USER_ACCOUNT_EXPORT.getFileType());
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean exportFlag = userAccountExportService.exportData(doc, data);
            if (exportFlag) {
                InputStream streamWorkBook = doc.getStream();
                streamWorkBook = userAccountExportService.processorTemplateFile(streamWorkBook);
                exportFile = new DefaultStreamedContent(streamWorkBook, "application/vnd.ms-excel", "UserAccount.xlsx");
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    public String getCurrentUserRoleNames(Integer userId) {
        List<SysRole> sysRoles = getSystemRolesByUserId(userId);
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isNotEmpty(sysRoles)) {
            for (SysRole sysRole : sysRoles) {
                sb.append(sysRole.getRoleDesc()).append(" ");
            }
        }
        List<CustomRole> customRoles = getCustomRoleByUserId(userId);
        if (CollectionUtils.isNotEmpty(customRoles)) {
            for (CustomRole customRole : customRoles) {
                sb.append(customRole.getName()).append(" ");
            }
        }
        return sb.toString();
    }

    public List<SysRole> getSystemRolesByUserId(Integer userId) {
        return sysRoleRepository.getSysRoles(userId);
    }

    public List<CustomRole> getCustomRoleByUserId(Integer userId) {
        return customRoleRepository.findCustomRolesByUserId(userId);
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public List<String> autoCompleteDeptName(String query) {
        if (deptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.deptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    private String filterClinicalDeptOrg = null;

    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            filterClinicalDeptOrg = (String) ((SelectEvent) event).getObject();
        }
        if (filterClinicalDeptOrg == null) return;

        List<OrgInfo> orgListByName = this.deptList.stream().filter(item -> item.getName().equals(filterClinicalDeptOrg)).collect(Collectors.toList());
        if (orgListByName.size() < 1)
            selectedUserOrg = null;
        else {
            selectedUserOrg = orgListByName.get(0);
        }
    }

    public String getSelectedOrgName() {
        if (selectedUserOrg == null) return "";

        return selectedUserOrg.getName();
    }

    public void setSelectedOrgName(String orgName) {
        //ignore the input orgName, just provide a Setter for UI input Component
    }

    public String getUserRoleType() {
        return userRoleType;
    }

    public void setUserRoleType(String userRoleType) {
        this.userRoleType = userRoleType;
    }

    public List<SysRoleVO> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRoleVO> roles) {
        this.roles = roles;
    }

    public boolean isShowButton() {
        return isShowButton;
    }

    public void setShowButton(boolean showButton) {
        isShowButton = showButton;
    }

    public List<PermissionGroup> getPermissionGroups() {
        return permissionGroups;
    }

    public void setPermissionGroups(List<PermissionGroup> permissionGroups) {
        this.permissionGroups = permissionGroups;
    }

    public List<String> getAssignedPermissionGroupUidList() {
        return assignedPermissionGroupUidList;
    }

    public void setAssignedPermissionGroupUidList(List<String> assignedPermissionGroupUidList) {
        this.assignedPermissionGroupUidList = assignedPermissionGroupUidList;
    }

    public List<String> autoCompleteTags(String query) {

        List<String> tags = userTagsMapper.getTagsByTenant(UserContextService.getCurrentUserAccount().getTenantUID());

        if (StringUtils.isEmpty(query)) {
            return tags;
        }

        tags = tags.stream().filter(tag -> tag.contains(query)).collect(Collectors.toList());

        if (tags.isEmpty()) {
            tags.add(query);
        }

        return tags;
    }

    //判断租户是否是46号租户 是返回true 否返回false
    public boolean judgeTheUser() {
        if (null == selected || null == selected.getSiteId()) {
            return false;
        }
        return 46 == selected.getSiteId();
    }

    public boolean getIsSelect46Account() {
        return selectedTenant != null && selectedTenant.getId() == 46;
    }

    public void filterTreeNodeByUid() {
        if (selectedTenant != null && selectedTenant.getId() == 46 && selectedUserOrg != null && !StringUtils.isEmpty(selectedUserOrg.getInstitutionUID())) {
            List<OrgInfo> siteList = uaaService.getUserAccessibleSiteListByUid(selectedUserOrg.getInstitutionUID());
            OrgInfo tenant = orgInfoDao.findByUid(selectedTenant.getUid());
            siteList.add(tenant);
            orgTree = uaaService.getOrgTreeNodes(selectedTenant.getUid(), siteList);
            uaaService.expendedTreeNode(orgTree, "ALL", -1, -1);
        }
    }

    public void destroyTenant() {
        this.dao.deleteTenantUser(loginUser.getTenantUID());
        TenantInfo tenantInfo = tenantInfoRepository.findByuid(loginUser.getTenantUID());
        tenantInfo.setActiveType(3);
        tenantInfoRepository.save(tenantInfo);
        final List<UserAccount> tenantUsers = dao.findByTenantUID(loginUser.getTenantUID());
        if (!CollectionUtils.isEmpty(tenantUsers)) {
            tenantUsers.forEach(u -> {
                userApi.removeUser(u.getLoginName());
                SessionCache.destroyedSession(u);
            });
        }
        logout();
    }

    public Boolean enableDestroyTenant() {
        TenantInfo tenantInfo = tenantInfoRepository.findByuid(loginUser.getTenantUID());
        return Objects.equals(2, tenantInfo.getActiveType());
    }

    public Boolean getConfirmDestroy() {
        return confirmDestroy;
    }

    public void setConfirmDestroy(Boolean confirmDestroy) {
        this.confirmDestroy = confirmDestroy;
    }


    private TreeNode assetTypeGroupTree;
    private TreeNode[] selectedAssetTypeGroupNodes;


    public TreeNode getAssetTypeGroupTree() {
        return assetTypeGroupTree;
    }

    public TreeNode[] getSelectedAssetTypeGroupNodes() {
        return selectedAssetTypeGroupNodes;
    }

    public void setSelectedAssetTypeGroupNodes(TreeNode[] selectedAssetTypeGroupNodes) {
        this.selectedAssetTypeGroupNodes = selectedAssetTypeGroupNodes;
    }

    public void onSelectAssetTypeGroupTreeNode(NodeSelectEvent event) {
        setTreeNodes(event.getTreeNode(), true);
    }



    public void onUnselectAssetTypeGroupTreeNode(NodeUnselectEvent event) {
        TreeNode node = event.getTreeNode();
        setTreeNodes(node, false);
        while (node.getParent() != null) {
            node.getParent().setSelected(false);
            node = node.getParent();
        }
    }

    private void loadAssetTypeGroup() {
        assetTypeGroupTree = assetTypeGroupService.buildAssetTypeGroupInTree();

        List<String> userAssetGroups = userAssetTypeGroupMapper.findAssetGroupByUser(this.selected.getUid());

        setSelectedSysAuthGroupTree(assetTypeGroupTree, userAssetGroups);

    }
    private void setSelectedSysAuthGroupTree(TreeNode node, List<String> userAuthGroupUids) {
        if (node.getData() instanceof AssetTypeGroup) {
            AssetTypeGroup assetTypeGroup = (AssetTypeGroup) node.getData();
            if (userAuthGroupUids.contains(assetTypeGroup.getUid())) {
                node.setSelected(true);
            } else if (node.getParent() != null && node.getParent().isSelected()) {
                node.setSelected(true);
            }
        }

        for (TreeNode item : node.getChildren()) {
            setSelectedSysAuthGroupTree(item, userAuthGroupUids);
        }
    }

    @Getter @Setter
    private Boolean isEnableAssetTypeGroup = false;
    @Getter @Setter
    private String filterAssetTypeGroup;
    @Getter @Setter
    private List<AssetTypeGroupDto> assetTypeGroupList;
    @Getter @Setter
    private Map<String, AssetTypeGroupDto> assetTypeGroupMap;
    @Getter @Setter
    private Set<String> assetTypeUserUidSet;
    @Getter @Setter
    private Map<String, AssetTypeGroup> currentPageAssetTypeGroupMap;

    public List<String> autoCompleteAssetTypeGroupName(String query) {
        if (assetTypeGroupList == null) {
            return Collections.emptyList();
        }
        List<AssetTypeGroupDto> res = this.assetTypeGroupList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query))
                .filter(item -> ObjectUtil.isEmpty(item.getParentUid()))
                .collect(Collectors.toList());
        return res.stream().map(AssetTypeGroupDto::getName).distinct().collect(Collectors.toList());
    }

    public void onAssetTypeGroupNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterAssetTypeGroup = (String) ((SelectEvent) event).getObject();
        }


        if (!Strings.isNullOrEmpty(filterAssetTypeGroup) && !assetTypeGroupList.stream().anyMatch(item -> filterAssetTypeGroup.equals(item.getName()))) {
            filterAssetTypeGroup = null;
        }
        if (filterAssetTypeGroup != null) {
            List<AssetTypeGroupDto> assetTypeGroupsMatchingByName = this.assetTypeGroupList.stream().filter(item -> item.getName().equals(filterAssetTypeGroup)).collect(Collectors.toList());
            List<AssetTypeGroupDto> matchedList = assetTypeGroupService.collectChildAssetTypeGroupInExstingList1(assetTypeGroupsMatchingByName, assetTypeGroupList);
            assetTypeUserUidSet = matchedList.stream().map(AssetTypeGroupDto::getUserUid).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
            if (ObjectUtil.isEmpty(assetTypeUserUidSet)) {
                assetTypeUserUidSet = Collections.singleton("-1");
            }
        } else {
            assetTypeUserUidSet = Collections.emptySet();
        }
    }

    public String convertAssetTypeGroupEncodedToName(String uid) {
        List<AssetTypeGroupDto> userGroups = assetTypeGroupList.stream().filter(t -> uid.equals(t.getUserUid())).collect(Collectors.toList());
        Set<String> nameList = new HashSet<>();
        for (AssetTypeGroupDto assetTypeGroupDto : userGroups) {
            nameList.add(findParent(assetTypeGroupDto).getName());
        }
        return nameList.stream().collect(Collectors.joining(" "));
    }

    private AssetTypeGroupDto findParent(AssetTypeGroupDto assetTypeGroupDto) {
        if (ObjectUtil.isNotEmpty(assetTypeGroupDto.getParentUid())) {
            assetTypeGroupDto = findParent(assetTypeGroupMap.get(assetTypeGroupDto.getParentUid()));
        }
        return assetTypeGroupDto;
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        Optional<SearchFilter> first = searchFilters.stream().filter(item -> "userRoleList.roleId".equals(item.fieldName)).findFirst();
        if(first.isPresent()){
            SearchFilter searchFilter = first.get();
            Object value = searchFilter.value;
            if(value instanceof String){
                 Integer roleId =  Integer.valueOf((String) value);
                 if(roleId > 0){
                     addFilter("userRoleList.roleType",SearchFilter.Operator.EQ.name(),0);
                 }else{
                     searchFilter.value = Math.negateExact(roleId);
                     addFilter("userRoleList.roleType",SearchFilter.Operator.EQ.name(),1);
                 }
            }

        }
    }
}
