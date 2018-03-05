package com.ge.apm.view.uaa;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.utils.TreeNodeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.dao.BiomedGroupUserRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserOrgInfo;
import com.ge.apm.domain.UserRole;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PasswordUtil;
import com.ge.apm.view.sysutil.UserContextService;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.web.WebUtil;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.BiomedGroupUser;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.WorkflowConfig;
import org.primefaces.event.NodeUnselectEvent;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;

@ManagedBean
@ViewScoped
public class UserAccountController extends JpaCRUDController<UserAccount> {

    /**
     *
     */
    private static final long serialVersionUID = -1L;

    public static final String DEFAULT_USER_ROLE = "临床科室人员";

    UserAccountRepository dao = null;
    UaaService uaaService = WebUtil.getBean(UaaService.class);

    private String tenantUid;

    private TenantInfo selectedTenant;

    private OrgInfo selectedOrg = null;

    private OrgInfoRepository orgInfoDao;

    private OrgInfo selectedUserOrg;

    private UserCommonService userCommonService;

    @Override
    protected void init() {
        this.searchFilters = new ArrayList<SearchFilter>();
        filterBySite = false;

        uaaService = WebUtil.getBean(UaaService.class);
        dao = WebUtil.getBean(UserAccountRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        userCommonService = WebUtil.getBean(UserCommonService.class);

        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        selectedOrg = orgInfoDao.getByUid(loginUser.getOrgInfoUID());
        setTenantUid(loginUser.getTenantUID());

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
        selectedTenant = tenantDao.findByuid(tenantUid);
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.getChildren().addAll(uaaService.getOrgFullTreeByTenant(tenantUid).getChildren());
        TreeNode selectedUserOrg = TreeNodeUtil.findNodeFromTree(orgTree, selectedOrg);
        if (null != selectedUserOrg) {
            selectedUserOrg.setSelected(true);
            TreeNodeUtil.expandParentsNodes(selectedUserOrg);
        }

//        TreeNodeUtil.expandTreeNode(selectedUserOrg);
//        selectedOrg = null;
    }

    public TenantInfo getSelectedTenant() {
        return selectedTenant;
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

        return dao.findBySearchFilter(this.searchFilters, pageRequest);
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

        allRoles = uaaService.getSysRoleNames();

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
    public void onAfterDeleteObject(UserAccount object, boolean isOk) {
        if (isOk) {
            uaaService.clearUserScope(object.getUid());
        }
    }

    @Override
    public void onAfterUpdateObject(UserAccount object, boolean isOk) {
        if (isOk && this.crudActionName.equals("Edit")) {
            uaaService.clearUserScope(object.getUid());
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

        selected.setIsActive(false);
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

        selected.setIsActive(true);
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

    private void readSelectedOrgTree(TreeNode node, List<TreeNode> selectedTreeNodeList) {
        if (node.isSelected()) {
            if (node.getParent() != null && node.getParent().isSelected()) {
                return;
            } else {
                selectedTreeNodeList.add(node);
            }
        } else {
            for (TreeNode item : node.getChildren()) {
                readSelectedOrgTree(item, selectedTreeNodeList);
            }
        }
    }

    @Transactional
    private void updateUserScope() {
        UserOrgInfoRepository userOrgDao = WebUtil.getBean(UserOrgInfoRepository.class);
        List<UserOrgInfo> orgList = userOrgDao.findByUserUid(selected.getUid());
        userOrgDao.delete(orgList);
        List<TreeNode> selectedOrgList = new ArrayList();
        readSelectedOrgTree(accessOrgTree, selectedOrgList);
        List<UserOrgInfo> userOrgList = new ArrayList();
        for (TreeNode node : selectedOrgList) {
            OrgInfo org = (OrgInfo) node.getData();
            UserOrgInfo userOrg = new UserOrgInfo();
            userOrg.setOrgType(org.getOrgType());
            userOrg.setOrgUid(org.getUid());
            userOrg.setUserUid(selected.getUid());
            userOrgList.add(userOrg);
        }

        userOrgDao.save(userOrgList);
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

}
