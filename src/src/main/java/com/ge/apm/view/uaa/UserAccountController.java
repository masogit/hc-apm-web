package com.ge.apm.view.uaa;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BiomedGroupUserRepository;
import com.ge.apm.dao.JsonTemplateRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.BiomedGroupUser;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SysRoleVO;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserRole;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.uaa.UserAccountExportService;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.service.utils.PasswordUtil;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class UserAccountController extends JpaCRUDController<UserAccount> {

    /**
     *
     */
    private static final long serialVersionUID = -1L;

    public static final String DEFAULT_USER_ROLE = "临床科室人员";
    public static final int ACTIVATION_ROLE = 10;

    UserAccountRepository dao = null;
    UaaService uaaService = WebUtil.getBean(UaaService.class);

    private String tenantUid;

    private TenantInfo selectedTenant;

    private OrgInfo selectedOrg = null;

    private OrgInfoRepository orgInfoDao;

    private OrgInfo selectedUserOrg;

    private UserCommonService userCommonService;

    private UserAccount loginUser;

    private TenantInfoRepository tenantInfoRepository;

    private JsonTemplateRepository jsonTemplateRepository;

    private String protocol;

    private String previousOrgUid;  //used in update

    private List<SysRoleVO> roleListVO;

    private UserAccountService userAccountService;
    
    private UserAccountExportService userAccountExportService;
    
    private StreamedContent exportFile;

    @Override
    protected void init() {
        this.searchFilters = new ArrayList<SearchFilter>();
        filterBySite = false;

        uaaService = WebUtil.getBean(UaaService.class);
        dao = WebUtil.getBean(UserAccountRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        userCommonService = WebUtil.getBean(UserCommonService.class);
        userAccountService = WebUtil.getBean(UserAccountService.class);

        loginUser = UserContextService.getCurrentUserAccount();

        selectedOrg = orgInfoDao.getByUid(loginUser.getOrgInfoUID());
        setTenantUid(loginUser.getTenantUID());
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        jsonTemplateRepository = WebUtil.getBean(JsonTemplateRepository.class);
        protocol = jsonTemplateRepository.findByType(3);
        userAccountExportService= WebUtil.getBean(UserAccountExportService.class);
        /*
        orgTree = buildOrgTree(UserContextService.getSiteId(), loginUser.getOrgInfoId());
        try{
            DefaultTreeNode selectedNode = (DefaultTreeNode)orgTree.getData();
            this.selectedOrg = (OrgInfo) selectedNode.getData();
        }
        catch(Exception ex){
        }*/
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        searchFilters.clear();
        this.tenantUid = tenantUid;
        TenantInfoRepository tenantDao = WebUtil.getBean(TenantInfoRepository.class);
        TenantInfo tempTenantInfo = tenantDao.findByuid(loginUser.getTenantUID());
        //判断登陆用户是否是LocalITAdmin(本地后台运营管理)
        if(UserContextService.isOnlyLocalITAdmin()){
            selectedTenant = tempTenantInfo;
            orgTree = uaaService.getOrgTreeBySite(loginUser.getSiteUID());
        }else{
            selectedTenant = tenantDao.findByuid(tenantUid);
            OrgInfo rootOrg = new OrgInfo();
            rootOrg.setId(-1);
            rootOrg.setShortName(WebUtil.getMessage("AllHospitals"));
            rootOrg.setName(WebUtil.getMessage("AllHospitals"));
            orgTree = new DefaultTreeNode("root", null, null);
            TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
            rootNode.getChildren().addAll(uaaService.getOrgFullTreeByTenant(tenantUid).getChildren());
        }

        TreeNode selectedUserOrg = TreeNodeUtil.findNodeFromTree(orgTree, selectedOrg);
        if (null != selectedUserOrg) {
            selectedUserOrg.setSelected(true);
            TreeNodeUtil.expandParentsNodes(selectedUserOrg);
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

    @Override
    protected Page<UserAccount> loadData(PageRequest pageRequest) {
        //this.selected = null;
        if (selectedOrg == null) {
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, this.tenantUid));
        } else {
            if (selectedOrg.getId() == -1) {
                searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, this.tenantUid));
            } else {
                searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedOrg.getUid(), "1*2+"));
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getUid(), "1*2+"));
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getUid(), "1*2+"));
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedOrg.getUid(), "1*2+"));
                searchFilters.add(new SearchFilter("orgInfoUID", SearchFilter.Operator.EQ, selectedOrg.getUid(), "1*2+"));
            }
        }
        boolean nameFilter=false;
        Object nameValue="";
        for(SearchFilter filter: searchFilters){
        	if(filter.fieldName.equals("name")){
        		nameFilter=true;
        		nameValue=filter.value;
        		filter.conditionPath="1*2*3+";
        	}
        }
        if(nameFilter){
        	nameValue=((String)nameValue).toLowerCase();
        	searchFilters.add(new SearchFilter("namePyCode",SearchFilter.Operator.LIKE,nameValue,"1*2*3+"));
        }

        Page<UserAccount> pageUserAccount=dao.findBySearchFilter(this.searchFilters, pageRequest);
        for(UserAccount user: pageUserAccount){
        	user.setOrgInfo(orgInfoDao.getByUid(user.getOrgInfoUID()));
        }
        return pageUserAccount;
    }

    @Override
    public String getErrorMessageForDuplicateKey(UserAccount userAccount) {
        return String.format(WebUtil.getMessage("DuplicateLoginName"), userAccount.getLoginName());
    }

    @Override
    public String getKeyFieldNameValue(UserAccount obj) {
        return WebUtil.getMessage("login_name") + "=" + obj.getLoginName();
    }

    private List<String> allRoles;
    private List<String> assignedRoles;

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

        /*TenantInfo ti = tenantInfoRepository.findByuid(selected.getTenantUID());

        //判断登陆用户是否是LocalITAdmin(本地后台运营管理)
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

        roleListVO = uaaService.getSysRoleVO(selected.getTenantUID());

        assignedRoles = new ArrayList<String>();
        for (UserRole userRole : selected.getUserRoleList()) {
            assignedRoles.add(userRole.getRole().getRoleDesc());
        }
        prepareUserScope();
    }

    public void saveRoles() {
        if (selected == null) {
            return;
        }
        //如果租户不是院区隔离时，修改用户角色时保证有LocalITAdmin角色的用户，LocalITAdmin角色不丢失
        TenantInfo ti = tenantInfoRepository.findByuid(selected.getTenantUID());
        if((ti.getInsulate() == null || !ti.getInsulate())){
            List<UserRole> tempUserRole = selected.getUserRoleList().stream().filter(item -> item.getRole().getName().equals("LocalITAdmin")).collect(Collectors.toList());
            if(!tempUserRole.isEmpty()){
                this.assignedRoles.add(tempUserRole.get(0).getRole().getRoleDesc());
            }
        }

        //如果登陆用户角色是LocalITAdmin，修改用户角色保证有ITAdmin角色的用户，ITAdmin角色不丢失
        if(UserContextService.isOnlyLocalITAdmin()){
            List<UserRole> tempUserRole = selected.getUserRoleList().stream().filter(item -> item.getRole().getName().equals("ITAdmin")).collect(Collectors.toList());
            if(!tempUserRole.isEmpty()){
                this.assignedRoles.add(tempUserRole.get(0).getRole().getRoleDesc());
            }
        }

        uaaService.setUserRoles(selected, assignedRoles);
        updateUserScope();
        selected.getUserRoleList().clear();
        selected = null;
        WebUtil.addSuccessMessage(WebUtil.getMessage("UserRole") + WebUtil.getMessage("Updated"));
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

        selected.setPlainPassword(PasswordUtil.DEFAULT_PWD);
        try {
            selected.entryptPassword();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.selectedUserOrg = null;

    }

    @Override
    public void onAfterNewObject(UserAccount userAccount, boolean isOk) {
        if (isOk && this.crudActionName.equals("Create")) {
            List<String> role = new ArrayList<String>();
            role.add(DEFAULT_USER_ROLE);
            uaaService.setUserRoles(selected, role);
            uaaService.setDefaultScope(selected);
        }
    }

    @Override
    public void onBeforeDeleteObject(UserAccount object) {
        uaaService.clearUserScope(object.getUid());
    }

    @Override
    public void onAfterUpdateObject(UserAccount object, boolean isOk) {
        if (isOk && this.crudActionName.equals("Edit")) {
            uaaService.clearUserScope(object.getUid());

            //if org change, delete old user org
            if (!object.getOrgInfoUID().equals(previousOrgUid) ) {
                uaaService.clearUserOrgAfterOrgUpdate(object.getUid());
            }

            uaaService.setDefaultScope(object);

        }
    }

    public void deleteSelectedUser() {
        if (this.selected.getSiteUID() != null) {
            //判断操作权限
            if(!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), this.selected.getSiteUID())){
                return;
            }
        }

        if (canDelete()) {
            super.delete();
        }
    }

    private Boolean canDelete() {
        List<SearchFilter> checkFilters = new ArrayList<>();
        checkFilters.add(new SearchFilter("dispatchUserId", SearchFilter.Operator.EQ, selected.getId(), "1+"));
        checkFilters.add(new SearchFilter("dispatchUserId2", SearchFilter.Operator.EQ, selected.getId(), "1+"));
        WorkflowConfigRepository wfDao = WebUtil.getBean(WorkflowConfigRepository.class);
        List<WorkflowConfig> cfgList = wfDao.findBySearchFilter(checkFilters);
        if (cfgList != null && !cfgList.isEmpty()) {
            WebUtil.addErrorMessage("该用户是派工人,不能删除.");
            return false;
        }
        checkFilters.clear();
        checkFilters.add(new SearchFilter("assetOwnerId", SearchFilter.Operator.EQ, selected.getId(), "1+"));
        checkFilters.add(new SearchFilter("assetOwnerId2", SearchFilter.Operator.EQ, selected.getId(), "1+"));
        checkFilters.add(new SearchFilter("assetOwnerId3", SearchFilter.Operator.EQ, selected.getId(), "1+"));
        checkFilters.add(new SearchFilter("clinicalOwnerId", SearchFilter.Operator.EQ, selected.getId(), "1+"));
        AssetInfoRepository assetDao = WebUtil.getBean(AssetInfoRepository.class);
        List<AssetInfo> assetList = assetDao.findBySearchFilter(checkFilters);
        if (assetList != null && !assetList.isEmpty()) {
            WebUtil.addErrorMessage("该用户是设备责任人,不能删除.");
            return false;
        }
        checkFilters.clear();
        checkFilters.add(new SearchFilter("userId", SearchFilter.Operator.EQ, selected.getId()));
        BiomedGroupUserRepository groupDao = WebUtil.getBean(BiomedGroupUserRepository.class);
        List<BiomedGroupUser> groupList = groupDao.findBySearchFilter(checkFilters);
        if (groupList != null && !groupList.isEmpty()) {
            WebUtil.addErrorMessage("该用户在维修工程师群组中,不能删除.");
            return false;
        }

        checkFilters.clear();
        checkFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, selected.getId()));
        checkFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
        V2_WorkOrderRepository woDao = WebUtil.getBean(V2_WorkOrderRepository.class);
        List<V2_WorkOrder> woList = woDao.findBySearchFilter(checkFilters);
        if (woList != null && !woList.isEmpty()) {
            WebUtil.addErrorMessage("该用户有在修工单,不能删除.");
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
        if(!user.getIsActive()){
            TenantInfo tenant = tenantInfoRepository.findByuid(user.getTenantUID());
            if(tenant.getIsEnabled()){
                user.setIsActive(true);
                user.setActiveTime(new Date());
            }else{
                user.setIsActive(false);
            }
        }
        if(user.getIsLocked()){
            //lockAccount();
            selected.setIsLocked(true);
            selected.setLockTime(TimeUtil.timeNow().toDate());
        }else{
            //unlockAccount();
            selected.setIsLocked(false);
            selected.setPasswordErrorCount(0);
        }
    }

    @Override
    public void save() {
        if (selectedUserOrg != null) {
            //判断操作权限
            if(!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), selectedUserOrg.getSiteUID())){
                return;
            }
        }

        super.save();
    }

    public void resetPassword() {
        if (this.selected.getSiteUID() != null) {
            //判断操作权限
            if(!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), this.selected.getSiteUID())){
                return;
            }
        }

        selected.setPlainPassword(PasswordUtil.DEFAULT_PWD);
        try {
            selected.entryptPassword();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
        dao.save(selected);
    }

    public void lockAccount() {
        if (this.selected.getSiteUID() != null) {
            //判断操作权限
            if(!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), this.selected.getSiteUID())){
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
            if(!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), this.selected.getSiteUID())){
                return;
            }
        }
        //selected.setIsActive(true);
        selected.setIsLocked(false);
        selected.setPasswordErrorCount(0);
        dao.save(selected);
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        selectedUserOrg = orgInfoDao.getByUid(selected.getOrgInfoUID());
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

    private void prepareUserScope() {
        accessOrgTree = uaaService.getOrgTreeBySite(selected.getSiteUID());
        List<OrgInfo> orgList = uaaService.getOrgListByUser(selected.getUid());
        setSelectedOrgTree(accessOrgTree, orgList);
        TreeNodeUtil.expandTreeNode(accessOrgTree);
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
                selectedTreeNodeList.add((OrgInfo)node.getData());
            }
        } else {
            for (TreeNode item : node.getChildren()) {
                readSelectedOrgTree(item, selectedTreeNodeList);
            }
        }
    }

    public void updateUserScope() {

        Set<OrgInfo> selectedOrgList = new HashSet<>();

        //only add parent if all children is selected
        readSelectedOrgTree(accessOrgTree, selectedOrgList);

        //if site is not selected, which means not all clinical are selected. then we add all the selected clinical.
        addMissingChildClinical(accessOrgTree, selectedOrgList);

        userAccountService.updateUserOrg(selected.getUid(), selectedOrgList);

    }



    /**
     * 大科室下面可以有小科室，如果选择了把小科室加进来，这样不影响已有的应用
     * @param node
     * @param selectedTreeNodeList
     */
    private void addMissingChildClinical(TreeNode node, Set<OrgInfo> selectedTreeNodeList) {

        OrgInfo data = (OrgInfo)node.getData();
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
    public void confirmActivation(){
        if(loginUser.getIsActive()){
            //System.out.println("already active !");
            WebUtil.addSuccessMessage("激活成功！");
            return;
        }
        loginUser.setIsActive(true);
        loginUser.setActiveTime(new Date());
        dao.save(loginUser);
        List<UserRole> roles = loginUser.getUserRoleList();
        roles = roles.stream().filter(role -> role.getRoleId() == ACTIVATION_ROLE).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(roles)){
            TenantInfo tenant = tenantInfoRepository.findByuid(loginUser.getTenantUID());
            tenant.setIsEnabled(true);
            tenant.setActiveTime(new Date());
            tenant.setActiveUserId(loginUser.getId());
            tenant.setActiveType(1);
            tenantInfoRepository.save(tenant);
            dao.updateMembersStatus(loginUser.getTenantUID());
        }

        WebUtil.addSuccessMessage("激活成功！");
    }

    @Transactional
    public void activeTenant(){
        TenantInfo tenant = tenantInfoRepository.findByuid(loginUser.getTenantUID());
        tenant.setIsEnabled(true);
        tenant.setActiveTime(new Date());
        tenant.setActiveUserId(loginUser.getId());
        tenant.setActiveType(1);
        tenantInfoRepository.save(tenant);
        dao.updateMembersStatus(loginUser.getTenantUID());
        WebUtil.addSuccessMessage("激活成功！");
    }

    public boolean needActive(){
        TenantInfo tenant = tenantInfoRepository.findByuid(loginUser.getTenantUID());
        if(tenant != null && tenant.getIsEnabled() == false){
            return false;
        }
        return true;
    }

    public boolean hasAuthority(){
        return loginUser.getUserRoleNames().contains("系统激活专用") ? true : false;
    }

    public void logout(){
        /*String context = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        String logoutUrl = FacesContext().getExternalContext().getRequestContextPath()+ "/logout.xhtml?uid="+loginUser.getId();
        return logoutUrl;*/
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null){
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
            HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        WebUtil.redirectTo("/login.xhtml");
    }

    public void updateAgree(){
        if(CollectionUtils.isNotEmpty(items)){
            String str = items.get(0);
            agree = Integer.parseInt(str) == 1 ? true : false;
        }else{
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
        if(this.selected==null) return;

        UserContextService.setLoginUser(this.selected);

        //call microServices to get Access Token...
        try{
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
            MicroServiceUtil msUtil = WebUtil.getBean(MicroServiceUtil.class);

            String accessToken = msUtil.getAuthenticateByUserPasswordEncrypted(selected.getLoginName(), selected.getPassword());
            request.getSession().setAttribute("ACCESS_TOKEN", accessToken);
        }catch(Exception e){
            Logger.getLogger(UserAccountController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void exportAll(){
    	List<SearchFilter> listSearchFilter=new ArrayList<SearchFilter>();
    	if (selectedOrg == null) {
    		listSearchFilter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, this.tenantUid));
        } else {
            if (selectedOrg.getId() == -1) {
            	listSearchFilter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, this.tenantUid));
            } else {
            	listSearchFilter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedOrg.getUid(), "1*2+"));
            	listSearchFilter.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getUid(), "1*2+"));
            	listSearchFilter.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getUid(), "1*2+"));
            	listSearchFilter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedOrg.getUid(), "1*2+"));
            	listSearchFilter.add(new SearchFilter("orgInfoUID", SearchFilter.Operator.EQ, selectedOrg.getUid(), "1*2+"));
            }
        }
    	List<UserAccount> listUserAccount=dao.findBySearchFilter(listSearchFilter);
		if(listUserAccount==null || listUserAccount.size()==0){
			WebUtil.addErrorMessage("无数据");
			return;
		}
		userAccountExportService.supplementUserAccount(listUserAccount);
		downloadFile(userAccountExportService.parseUserAccountToMap(listUserAccount));
	}
    
    private void downloadFile(List<Map<String,Object>> data){
		try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/uaa/userAccount/APM_UserAccount_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean exportFlag = userAccountExportService.exportData(doc, data);
            if (exportFlag) {
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "UserAccount.xlsx");
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
	}

	public StreamedContent getExportFile() {
		return exportFile;
	}

	public void setExportFile(StreamedContent exportFile) {
		this.exportFile = exportFile;
	}
}
