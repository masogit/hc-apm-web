package com.ge.apm.view.hospitalAdjustment;

import com.ge.apm.dao.IdleEquipmentApplicationRepository;
import com.ge.apm.dao.IdleEquipmentStockRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
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
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class IdleEquipmentStockController extends GenericCRUDUUIDController<IdleEquipmentStock> {

    protected static final Logger log = LoggerFactory.getLogger(WorkOrderMgrController.class);

    private IdleEquipmentStockRepository idleEquipmentStockRepository;

    private UserAccount currentUserAccount;

    private UaaService uaaService;

    @Getter @Setter
    private List<OrgInfo> orgList;

    private OrgTreeController orgTreeController;

    @Getter @Setter
    private TreeNode[] selectedAccessOrgNodes;

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
    private Map<String,Object> filterObject;
    @Override
    protected void init() {
        idleEquipmentStockRepository = WebUtil.getBean(IdleEquipmentStockRepository.class);

        currentUserAccount = UserContextService.getCurrentUserAccount();

        orgTreeController = WebUtil.getBean(OrgTreeController.class);

        uaaService = WebUtil.getBean(UaaService.class);
        cacheService = WebUtil.getBean(ConfigCacheService.class);
        selectSite = cacheService.findOrgByUid(currentUserAccount.getSiteUID());

        orgList = uaaService.getUserAccessibleSiteList(currentUserAccount.getUid());

        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        loadClinicalDeptList();
        filterObject=new HashMap<>();
    }

    @Override
    protected GenericRepositoryUUID<IdleEquipmentStock> getDAO() {
        return idleEquipmentStockRepository;
    }

    /**
     *  获取我发起的审批的列表
     * @return
     */
    @Override
    public Page<IdleEquipmentStock> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
//        List<UserRole> userRoleList = currentUserAccount.getUserRoleList();
//        boolean existence = userRoleList.stream().anyMatch(ite -> ite.getRole().getName().equals(ApmSystemRole.HospitalAdjustmentAdmin.name()) || ite.getRole().getName().equals(ApmSystemRole.InstitutionAdjustmentAdmin.name()));
//        if(!existence){
//            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
//        }
//

        Set<String> functionList = UserContextService.getCurrentUserFunction();
        if (!functionList.contains(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_STOCK.name()) && !functionList.contains(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_STOCK_INSTITUTION.name())) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }


        this.updateSearchFilter(new SearchFilter("tenantUid", SearchFilter.Operator.EQ,currentUserAccount.getTenantUID()));

        Page<IdleEquipmentStock> bySearchFilter = idleEquipmentStockRepository.findBySearchFilter(searchFilters, pageRequest);
        return bySearchFilter;

    }



    public void onSelectAsset() throws IOException {
        if (null == selected) {
            return;
        }

        String url = "actionName=View&selectedid="
                + selected.getUid();
        url = "IdleEquipmentStockDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

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
        switch (status){
            case 1:
                return "可借用";
            case 2:
                return "申请中";
            case 3:
                return "已借用";
            case 4:
                return "不可借";
            default:
                return "";
        }
    }



}
