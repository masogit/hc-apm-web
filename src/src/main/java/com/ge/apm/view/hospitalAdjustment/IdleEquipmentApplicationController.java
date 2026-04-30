package com.ge.apm.view.hospitalAdjustment;

import com.ge.apm.dao.IdleEquipmentApplicationRepository;
import com.ge.apm.domain.IdleEquipmentApplication;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserRole;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.uaa.OrgTreeController;
import com.ge.apm.view.wo.WorkOrderMgrController;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.cons.ApmSystemRole;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;

import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionListener;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class IdleEquipmentApplicationController extends GenericCRUDUUIDController<IdleEquipmentApplication> {

    protected static final Logger log = LoggerFactory.getLogger(WorkOrderMgrController.class);

    private IdleEquipmentApplicationRepository idleEquipmentApplicationRepository;

    private UserAccount currentUserAccount;

    private UaaService uaaService;
    @Getter
    @Setter
    private String siteFor;
    @Getter
    @Setter
    private List<OrgInfo> orgList;

    private OrgTreeController orgTreeController;


 private TreeNode[] selectedAccessOrgNodes = new TreeNode[0];


    private List<OrgInfo> clinicalDeptList;

    private ConfigCacheService cacheService;
    //选定科室过滤
    private String filterClinicalDeptOrg;
    private String filterAssetDeptOrg;

    private Set<String> clinicalDeptOrgUidSet;
    //选定的院区
    private OrgInfo selectSite;

    private String fileterOrgName;

    @Getter
    @Setter
    private String filterAssetGroup;
    @Getter
    @Setter
    private Integer filterStatus;

    private OrgInfoTreeService orgInfoTreeService;

    @Getter
    @Setter
    private Map<String, Object> filterObject;

    @Override
    protected void init() {
        idleEquipmentApplicationRepository = WebUtil.getBean(IdleEquipmentApplicationRepository.class);

        currentUserAccount = UserContextService.getCurrentUserAccount();

        orgTreeController = WebUtil.getBean(OrgTreeController.class);

        uaaService = WebUtil.getBean(UaaService.class);
        cacheService = WebUtil.getBean(ConfigCacheService.class);
        selectSite = cacheService.findOrgByUid(currentUserAccount.getSiteUID());

        orgList = uaaService.getUserAccessibleSiteList(currentUserAccount.getUid());

        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        loadClinicalDeptList();
        filterObject = new HashMap<>();
    }

    @Override
    protected GenericRepositoryUUID<IdleEquipmentApplication> getDAO() {
        return idleEquipmentApplicationRepository;
    }


    @Override
    public Page<IdleEquipmentApplication> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }

        if (ObjectUtil.isNotEmpty(selectedAccessOrgNodes)) {
            List<String> selectedSiteList = ListUtil.newArrayList();
            for (TreeNode selectedAccessOrgNode : selectedAccessOrgNodes) {
                OrgInfo orgInfo =(OrgInfo) selectedAccessOrgNode.getData();
                selectedSiteList.add(orgInfo.getUid());
            }
            this.updateSearchFilter(new SearchFilter("stockSiteUid", SearchFilter.Operator.IN, selectedSiteList));
        }else {
            this.removeFilterOnField("stockSiteUid");
        }

        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(clinicalDeptOrgUidSet)){
            this.updateSearchFilter(new SearchFilter("stockDeptUid", SearchFilter.Operator.IN, clinicalDeptOrgUidSet));

        }


//        List<UserRole> userRoleList = currentUserAccount.getUserRoleList();
//        boolean existence = userRoleList.stream().anyMatch(ite -> ite.getRole().getName().equals(ApmSystemRole.HospitalAdjustmentAdmin.name()) || ite.getRole().getName().equals(ApmSystemRole.InstitutionAdjustmentAdmin.name()));
//        if(!existence){
//            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
//        }

        Set<String> functionList = UserContextService.getCurrentUserFunction();
        if (!functionList.contains(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_APPLICATION.name()) && !functionList.contains(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_ACCEPTANCE.name())) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }


        this.updateSearchFilter(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, currentUserAccount.getTenantUID()));

//        boolean HospitalAdjustmentAdmin = userRoleList.stream().anyMatch(ite -> ite.getRole().getName().equals(ApmSystemRole.HospitalAdjustmentAdmin.name()) );
//
//        if(HospitalAdjustmentAdmin){
//            this.updateSearchFilter(new SearchFilter("applicationUserUid", SearchFilter.Operator.EQ,currentUserAccount.getUid()));
//        }
        if (functionList.contains(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_APPLICATION.name())) {
            this.updateSearchFilter(new SearchFilter("applicationUserUid", SearchFilter.Operator.EQ, currentUserAccount.getUid()));
        }


//        boolean InstitutionAdjustmentAdmin = userRoleList.stream().anyMatch(ite -> ite.getRole().getName().equals(ApmSystemRole.InstitutionAdjustmentAdmin.name()) );
//
//        if(InstitutionAdjustmentAdmin){
//            if(!this.hasFilterOnField("status")){
//                this.updateSearchFilter(new SearchFilter("status", SearchFilter.Operator.IN,Arrays.asList(2,3,4)));
//            }
//        }


        if (functionList.contains(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_ACCEPTANCE.name())) {
            if (!this.hasFilterOnField("status")) {
                this.updateSearchFilter(new SearchFilter("status", SearchFilter.Operator.IN, Arrays.asList(2, 3, 4)));
            }
        }

        Page<IdleEquipmentApplication> bySearchFilter = idleEquipmentApplicationRepository.findBySearchFilter(searchFilters, pageRequest);
        return bySearchFilter;

    }


    public String getEditPage(String uid) {
        String operation = "adjustmentApplication.xhtml?actionName=Edit&selectedid=" + uid;
        return UrlEncryptController.encodeUrlParam(operation);
    }


    public String getCopyPage(String uid) {
        String operation = "adjustmentApplication.xhtml?actionName=Copy&selectedid=" + uid;
        return UrlEncryptController.encodeUrlParam(operation);
    }


    public void onSelectAsset() throws IOException {
        if (null == selected) {
            return;
        }

        String url = "actionName=View&selectedid="
                + selected.getUid();
        url = "IdleEquipmentApplicationDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public TreeNode getSiteOrgTree() {

        return orgTreeController.getSiteOrgTree(orgList);
    }


    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        this.searchFilters.clear();
        loadClinicalDeptList();
        filterClinicalDeptOrg = null;
        filterAssetDeptOrg = null;
        clinicalDeptOrgUidSet = null;
        setTreeNodes(event.getTreeNode(), true);
    }

    public void onUnSelectSiteTreeNode(NodeUnselectEvent event) {
        this.searchFilters.clear();
        loadClinicalDeptList();
        filterClinicalDeptOrg = null;
        filterAssetDeptOrg = null;
        clinicalDeptOrgUidSet = null;
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

    private void loadClinicalDeptList() {
        try {
            clinicalDeptList = Collections.emptyList();
            List<OrgInfo> deptList = uaaService.getAllAccessibleClinicalDeptList();
            if (!CollectionUtils.isEmpty(deptList)) {
                clinicalDeptList = deptList.stream()
                        .filter(item ->
                        {
                            if (item == null) {
                                log.info("null");
                            }
                            return item.getOrgType() != null && item.getOrgType() == 4;
                        })
                        .collect(Collectors.toList());
            }

            if (null != selectSite && !clinicalDeptList.isEmpty()) {
                clinicalDeptList = clinicalDeptList.stream()
                        .filter(item -> StringUtils.equals(item.getSiteUID(), selectSite.getSiteUID())
                                || StringUtils.equals(item.getHospitalUID(), selectSite.getSiteUID())
                                || StringUtils.equals(item.getInstitutionUID(), selectSite.getSiteUID())
                                || StringUtils.equals(item.getTenantUID(), selectSite.getSiteUID()))
                        .collect(Collectors.toList());
            }
            if (ObjectUtil.isNotEmpty(selectedAccessOrgNodes)) {
                List<String> selectedSiteUidList = Arrays.stream(selectedAccessOrgNodes).map(node -> (OrgInfo) node.getData()).map(OrgInfo::getSiteUID).collect(Collectors.toList());
                clinicalDeptList = clinicalDeptList.stream()
                        .filter(item -> selectedSiteUidList.contains(item.getSiteUID())
                                || selectedSiteUidList.contains(item.getHospitalUID())
                                || selectedSiteUidList.contains(item.getInstitutionUID())
                                || selectedSiteUidList.contains(item.getTenantUID()))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("loadClinicalDeptList occur exception", e);
            throw e;
        }
    }

    public String getSiteOrgName(String siteUID) {
        OrgInfo org = cacheService.findOrgByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }
    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            fileterOrgName = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(fileterOrgName) && !clinicalDeptList.stream().anyMatch(item -> fileterOrgName.equals(item.getName()))) {
            fileterOrgName = null;
        }

        if (fileterOrgName != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(fileterOrgName)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
        }
    }

    public String getInstallUrl(String uid) {
        return UrlEncryptController.encodeUrlParam("IdleEquipmentApplicationApproval.xhtml?actionName=Approval&selectedid=" + uid);


//        String url = "actionName=Approval&selectedid="
//                + uid;
//       return    "IdleEquipmentApplicationApproval.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
    }


    public String getSiteFor() {
        return siteFor;
    }

    public void setSiteFor(String siteFor) {
        this.siteFor = siteFor;
    }

    public List<OrgInfo> getOrgList() {
        return orgList;
    }

    public void setOrgList(List<OrgInfo> orgList) {
        this.orgList = orgList;
    }

    public List<OrgInfo> getClinicalDeptList() {
        return clinicalDeptList;
    }

    public void setClinicalDeptList(List<OrgInfo> clinicalDeptList) {
        this.clinicalDeptList = clinicalDeptList;
    }

    public String getFilterClinicalDeptOrg() {
        return filterClinicalDeptOrg;
    }

    public void setFilterClinicalDeptOrg(String filterClinicalDeptOrg) {
        this.filterClinicalDeptOrg = filterClinicalDeptOrg;
    }

    public String getFilterAssetDeptOrg() {
        return filterAssetDeptOrg;
    }

    public void setFilterAssetDeptOrg(String filterAssetDeptOrg) {
        this.filterAssetDeptOrg = filterAssetDeptOrg;
    }

    public Set<String> getClinicalDeptOrgUidSet() {
        return clinicalDeptOrgUidSet;
    }

    public void setClinicalDeptOrgUidSet(Set<String> clinicalDeptOrgUidSet) {
        this.clinicalDeptOrgUidSet = clinicalDeptOrgUidSet;
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }

    public String getFileterOrgName() {
        return fileterOrgName;
    }

    public void setFileterOrgName(String fileterOrgName) {
        this.fileterOrgName = fileterOrgName;
    }

    public String statusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 1:
                return "暂存中";
            case 2:
                return "待审批";
            case 3:
                return "已通过";
            case 4:
                return "已退回";
            case 5:
                return "已取消";
            default:
                return "";
        }
    }


    public void deleteIdleApplication(IdleEquipmentApplication application) {
        if (application != null && application.getUid() != null) {
            IdleEquipmentApplication byUid = idleEquipmentApplicationRepository.findByUid(application.getUid());
            if (byUid != null && byUid.getStatus() == 3) {
                WebUtil.addErrorMessage("闲置设备入库已经申请通过无法删除");
            }
            if (byUid != null && byUid.getStatus() != 3) {
                idleEquipmentApplicationRepository.delete(application.getUid());
                WebUtil.addSuccessMessage("闲置设备申请已删除");
            }

        }
    }

    public TreeNode[] getSelectedAccessOrgNodes() {
        return selectedAccessOrgNodes;
    }

    public void setSelectedAccessOrgNodes(TreeNode[] selectedAccessOrgNodes) {
        this.selectedAccessOrgNodes = selectedAccessOrgNodes;
    }
}
