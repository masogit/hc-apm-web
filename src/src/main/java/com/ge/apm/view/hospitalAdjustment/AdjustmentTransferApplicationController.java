package com.ge.apm.view.hospitalAdjustment;

import com.ge.apm.dao.AdjustmentTransferApplicationRepository;
import com.ge.apm.dao.IdleEquipmentApplicationRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
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
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AdjustmentTransferApplicationController extends GenericCRUDUUIDController<AdjustmentTransferApplication> implements Serializable, ServerEventInterface {

    protected static final Logger log = LoggerFactory.getLogger(AdjustmentTransferApplicationController.class);

    private AdjustmentTransferApplicationRepository adjustmentTransferApplicationRepository;

    private OrgInfoRepository orgInfoRepository;
    private UserAccount currentUserAccount;
    private OrgTreeController orgTreeController;
    private ConfigCacheService cacheService;
    private UaaService uaaService;
    @Getter
    @Setter
    private List<String> visibleScope;
    @Getter
    @Setter
    private List<OrgInfo> orgList;
    @Getter
    @Setter
    private int buttonType = 1;  //1借出工单   2  借入工单
    @Getter
    @Setter
    private String filterAssetGroup;
    @Getter
    @Setter
    private Integer filterStatus;


    @Getter
    @Setter
    private Map<String, Object> filterObject;




    @Getter @Setter
    private TreeNode[] selectedAccessOrgNodes;

    private List<OrgInfo> clinicalDeptList;


    //选定科室过滤
    private String filterClinicalDeptOrg;
    private String filterAssetDeptOrg;

    private Set<String> clinicalDeptOrgUidSet;
    //选定的院区
    private OrgInfo selectSite;

    private String fileterOrgName;
    private OrgInfoTreeService orgInfoTreeService;
    private   OrgInfoRepository orgInfoDao;
    @Override
    protected void init() {
        adjustmentTransferApplicationRepository = WebUtil.getBean(AdjustmentTransferApplicationRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        cacheService = WebUtil.getBean(ConfigCacheService.class);
        currentUserAccount = UserContextService.getCurrentUserAccount();
        visibleScope = orgInfoRepository.getUserDeptScope(currentUserAccount.getUid());
        orgTreeController = WebUtil.getBean(OrgTreeController.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);

        Set<String> functionList = UserContextService.getCurrentUserFunction();
        if (functionList.contains(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_STOCK.name()) ) {
            orgList = orgInfoDao.getOrgInfosByParentId(currentUserAccount.getInstitutionUID());
            selectSite = cacheService.findOrgByUid(currentUserAccount.getInstitutionUID());

        }else{
            orgList = uaaService.getUserAccessibleSiteList(currentUserAccount.getUid());
            selectSite = cacheService.findOrgByUid(currentUserAccount.getSiteUID());

        }
        filterObject = new HashMap<>();
        loadClinicalDeptList();
    }

    @Override
    protected GenericRepositoryUUID<AdjustmentTransferApplication> getDAO() {
        return adjustmentTransferApplicationRepository;
    }


    @Override
    public Page<AdjustmentTransferApplication> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        Set<String> functionList = UserContextService.getCurrentUserFunction();
        if (!functionList.contains(ApmFunction.HOSPITAL_ADJUSTMENT_WORKBENCH.name()) && !functionList.contains(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_ADJUSTMENT_ACCEPTANCE.name())) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }

        List<String> selectedSiteList = ListUtil.newArrayList();
        if (ObjectUtil.isNotEmpty(selectedAccessOrgNodes)) {

            for (TreeNode selectedAccessOrgNode : selectedAccessOrgNodes) {
                OrgInfo orgInfo =(OrgInfo) selectedAccessOrgNode.getData();
                selectedSiteList.add(orgInfo.getUid());
            }
            this.updateSearchFilter(new SearchFilter("stockSiteUid", SearchFilter.Operator.IN, selectedSiteList));
        }else {
            this.removeFilterOnField("stockSiteUid");
        }
        this.updateSearchFilter(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, currentUserAccount.getTenantUID()));
        if (functionList.contains(ApmFunction.HOSPITAL_ADJUSTMENT_WORKBENCH.name())) {

            if (buttonType == 1) {
                if(org.apache.commons.collections.CollectionUtils.isNotEmpty(selectedSiteList)){
                    List<String> intersection = selectedSiteList.stream()
                            .filter(visibleScope::contains)
                            .collect(Collectors.toList());

                    this.updateSearchFilter(new SearchFilter("stockSiteUid", SearchFilter.Operator.IN, intersection, "1*2+"));
                    this.updateSearchFilter(new SearchFilter("stockDeptUid", SearchFilter.Operator.IN, intersection, "1*2+"));
                }else{
                    this.updateSearchFilter(new SearchFilter("stockSiteUid", SearchFilter.Operator.IN, visibleScope, "1*2+"));
                    this.updateSearchFilter(new SearchFilter("stockDeptUid", SearchFilter.Operator.IN, visibleScope, "1*2+"));
                }

                this.updateSearchFilter(new SearchFilter("status", SearchFilter.Operator.EQ, 6));
//                this.removeFilterOnField("borrowSiteUid");
//                this.removeFilterOnField("borrowDeptUid");
            } else {
                this.updateSearchFilter(new SearchFilter("borrowSiteUid", SearchFilter.Operator.IN, visibleScope, "1*2+"));
                this.updateSearchFilter(new SearchFilter("borrowDeptUid", SearchFilter.Operator.IN, visibleScope, "1*2+"));
//                this.removeFilterOnField("stockSiteUid");
//                this.removeFilterOnField("stockDeptUid");
//                this.removeFilterOnField("status");
            }
        }
        Page<AdjustmentTransferApplication> bySearchFilter = adjustmentTransferApplicationRepository.findBySearchFilter(searchFilters, pageRequest);
        return bySearchFilter;

    }



    public  void buttonTypeChange(int  buttonType){
        searchFilters = new ArrayList<>();
        this.buttonType=buttonType;
    }

    public void onSelectAsset() throws IOException {
        if (null == selected) {
            return;
        }

        String url = "actionName=View&selectedid="
                + selected.getUid();
        url = "AdjustmentTransferApplicationDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

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


            List<OrgInfo> deptList =new ArrayList<>();

            Set<String> functionList = UserContextService.getCurrentUserFunction();
            if (functionList.contains(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_STOCK.name()) ) {
                deptList= uaaService.getBySiteUidDeptList(currentUserAccount.getInstitutionUID());
            }else{
                deptList = uaaService.getAllAccessibleClinicalDeptList();
            }

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




    public String getSiteOrgName(String siteUID) {
        OrgInfo org = cacheService.findOrgByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }

    //1待审批  2  审批未通过 3 已取消  4 待交付  5  已交付 6 已完成 7 已退回
    public String statusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 1:
                return "待审批";
            case 2:
                return "审批未通过";
            case 3:
                return "已取消";
            case 4:
                return "待交付";
            case 5:
                return "已交付";
            case 6:
                return "已完成";
            case 7:
                return "退回待审批";
            case 8:
                return "已退回";
            case 9:
                return "退回审批不通过";
            default:
                return "";
        }
    }


    public List<OrgInfo> getOrgList() {
        return orgList;
    }

    public void setOrgList(List<OrgInfo> orgList) {
        this.orgList = orgList;
    }

    public int getButtonType() {
        return buttonType;
    }

    public void setButtonType(int buttonType) {
        this.buttonType = buttonType;
    }

    public String getFilterAssetGroup() {
        return filterAssetGroup;
    }

    public void setFilterAssetGroup(String filterAssetGroup) {
        this.filterAssetGroup = filterAssetGroup;
    }

    public Integer getFilterStatus() {
        return filterStatus;
    }

    public void setFilterStatus(Integer filterStatus) {
        this.filterStatus = filterStatus;
    }

    public Map<String, Object> getFilterObject() {
        return filterObject;
    }

    public void setFilterObject(Map<String, Object> filterObject) {
        this.filterObject = filterObject;
    }

    public TreeNode[] getSelectedAccessOrgNodes() {
        return selectedAccessOrgNodes;
    }

    public void setSelectedAccessOrgNodes(TreeNode[] selectedAccessOrgNodes) {
        this.selectedAccessOrgNodes = selectedAccessOrgNodes;
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
}
