package com.ge.apm.view;

import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 503310146
 */
@ManagedBean
@ViewScoped
@Data
@EqualsAndHashCode(callSuper = false)
public class PermissionGroupController extends GenericCRUDUUIDController<PermissionGroup> {

    private static final long serialVersionUID = -1523958787545937126L;

    private PermissionGroupRepository dao;
    private UaaService uaaService;
    private UserOrgInfoRepository userOrgDao;
    private OrgInfoRepository orgInfoDao;
    private UserAccountRepository userDao;
    private PermissionGroupOrgRepository permissionGroupOrgRepository;

    private TreeNode accessOrgTree;
    private TreeNode[] selectedAccessOrgNodes;

    private UserAccount loginUser;

    @Override
    protected PermissionGroupRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(PermissionGroupRepository.class);
        userOrgDao = WebUtil.getBean(UserOrgInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        permissionGroupOrgRepository = WebUtil.getBean(PermissionGroupOrgRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
    }

    @Override
    protected Page<PermissionGroup> loadData(PageRequest pageRequest) {
        searchFilters = ObjectUtil.isEmpty(searchFilters) ? ListUtil.toList() : searchFilters;
        UserAccount user = UserContextService.getCurrentUserAccount();
        boolean createdByBoolean = false;
        String value = null;
        for (SearchFilter searchFilter : searchFilters) {
            if ("createdBy".equals(searchFilter.fieldName)) {
                createdByBoolean = true;
                value = (String) searchFilter.value;
            }
        }
        if (createdByBoolean) {
            searchFilters.removeIf(e -> e.fieldName.equals("createdBy"));
            List<UserAccount> userAccountList = userDao.findByTenantUIDAndNameList(user.getTenantUID(), String.valueOf(value));
            List<String> loginNamList = new ArrayList<>();
            if (!userAccountList.isEmpty()) {
                for (UserAccount userAccount : userAccountList) {
                    loginNamList.add(userAccount.getLoginName());
                }
            }
            searchFilters.add(new SearchFilter("createdBy", SearchFilter.Operator.IN, loginNamList));
        }
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, loginUser.getSiteUID(), "1*2+"));
        searchFilters.add(new SearchFilter("institutionUid", SearchFilter.Operator.EQ, loginUser.getSiteUID(), "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUid", SearchFilter.Operator.EQ, loginUser.getSiteUID(), "1*2+"));
        searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.EQ, loginUser.getSiteUID(), "1*2+"));
        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public void save() {
        if ("Create".equals(crudActionName)) {
            selected.setTenantUid(loginUser.getTenantUID());
            selected.setInstitutionUid(loginUser.getInstitutionUID());
            selected.setHospitalUid(loginUser.getHospitalUID());
            selected.setSiteUid(loginUser.getSiteUID());
            selected.setCreatedBy(loginUser.getLoginName());
        } else {
            updateUserScope();
        }
        dao.save(selected);
    }

    public void preparePermissionData() {
        accessOrgTree = uaaService.getOrgTreeBySite(loginUser.getTenantUID());
        List<OrgInfo> orgList = ListUtil.toList();
        if (ObjectUtil.isNotEmpty(selected.getUid())) {
            orgList = uaaService.getOrgListByPermissionGroup(ListUtil.toList(selected.getUid()));
        }
        setSelectedOrgTree(accessOrgTree, orgList);
        TreeNodeUtil.expandTreeNode(accessOrgTree);
    }

    public String selectCreatedBy(String loginName) {
        UserAccount user = userDao.getByLoginName(loginName);
        if (user != null) {
            return user.getName();
        }
        return null;
    }

    public boolean isCreateAction() {
        return "Create".equals(crudActionName);
    }

    public void updateUserScope() {
        Set<OrgInfo> selectedOrgList = new HashSet<>();
        //only add parent if all children is selected
        readSelectedOrgTree(accessOrgTree, selectedOrgList);
        //if site is not selected, which means not all clinical are selected. then we add all the selected clinical.
        addMissingChildClinical(accessOrgTree, selectedOrgList);

        permissionGroupOrgRepository.deleteByPermissionGroupUid(selected.getUid());
        List<PermissionGroupOrg> permissionGroupOrgList = new ArrayList<>();
        for (OrgInfo selectedOrg : selectedOrgList) {
            PermissionGroupOrg userOrg = new PermissionGroupOrg();
            userOrg.setOrgType(selectedOrg.getOrgType());
            userOrg.setOrgUid(selectedOrg.getUid());
            userOrg.setPermissionGroupUid(selected.getUid());
            permissionGroupOrgList.add(userOrg);
        }
        permissionGroupOrgRepository.save(permissionGroupOrgList);

    }

    private void readSelectedOrgTree(TreeNode node, Set<OrgInfo> selectedTreeNodeList) {
        if (node.isSelected()) {
            if (!node.getParent().isSelected()) {
                selectedTreeNodeList.add((OrgInfo) node.getData());
            }
        } else {
            for (TreeNode item : node.getChildren()) {
                readSelectedOrgTree(item, selectedTreeNodeList);
            }
        }
    }

    /**
     * 大科室下面可以有小科室，如果选择了把小科室加进来，这样不影响已有的应用
     *
     * @param node                 可访问的组织书
     * @param selectedTreeNodeList 选中的组织列表
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
        } else if (Objects.equals(data.getOrgType(), 5) || Objects.equals(data.getOrgType(), 4)) {
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

    private void setSelectedOrgTree(TreeNode node, List<OrgInfo> accessibleOrgList) {
        OrgInfo org = (OrgInfo) node.getData();
        boolean selectedNode = accessibleOrgList.contains(org) || (node.getParent() != null && node.getParent().isSelected());
        if (selectedNode) {
            node.setSelected(true);
        }
        for (TreeNode item : node.getChildren()) {
            setSelectedOrgTree(item, accessibleOrgList);
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

}
