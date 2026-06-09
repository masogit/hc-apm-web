package com.ge.apm.view.wo;

import com.ge.apm.dao.ConfigItemValueRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.service.wo.WorkOrderUnifiedService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.uaa.OrgTreeController;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
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
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统一工单管理Controller - 合并内部工单和MVS工单
 * @author System Generated
 */
@ManagedBean
@ViewScoped
public class WorkOrderUnifiedController extends GenericCRUDUUIDController<V2_WorkOrder> implements Serializable {
    protected static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderUnifiedController.class);

    @Getter @Setter
    private Integer queryIndex;
    @Getter @Setter
    List<V2_WorkOrder> bySearchFilter = null;
    @Getter @Setter
    private String pageType;

    @Getter @Setter
    private V2_WorkOrder selected;

    @Getter @Setter
    private List<V2_WorkOrder> selectedList;

    @Getter @Setter
    private Map<String, Boolean> selectedBatchMap;

    @Getter @Setter
    private String stepFileter;

    @Getter @Setter
    private String fileterOrgName;
    @Getter @Setter
    private TreeNode[] selectedAccessOrgNodes;
    @Getter @Setter
    private TreeNode[] selectedAssetTypeNodes;
    @Getter @Setter
    private List<OrgInfo> clinicalDeptList;
    @Getter @Setter
    private List<OrgInfo> orgList;
    @Getter @Setter
    private Set<String> clinicalDeptOrgUidSet;
    @Getter @Setter
    private String siteUidFilter;
    @Getter @Setter
    private UserAccount loginUser;
    @Getter @Setter
    private String displayModeCasePriority;
    @Getter @Setter
    private String applyTimeFormatTime = null;
    @Getter @Setter
    private Date applyTimeFrom = null;

    private UaaService uaaService;
    private ConfigCacheService cacheService;
    private V2_WorkOrderRepository woDao;
    private WorkOrderUnifiedService workOrderUnifiedService;
    private UserContextService userContextService;
    private List<AssetTypeGroup> assetTypeGroupList;
    private OrgInfoTreeService orgInfoTreeService;
    private V2_WorkOrderService woService;
    private OrgTreeController orgTreeController;
    private ConfigItemValueRepository configItemValueRepository;
    WorkOrderMgrController workOrderMgrController;
    WorkOrderMgrMvsController  workOrderMgrMvsController;

    @Override
    protected GenericRepositoryUUID<V2_WorkOrder> getDAO() {
        return woDao;
    }

    @Override
    protected void init() {
        try {
            woDao = WebUtil.getBean(V2_WorkOrderRepository.class);
            workOrderUnifiedService = WebUtil.getBean(WorkOrderUnifiedService.class);
            userContextService = WebUtil.getBean(UserContextService.class);
            orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
            woService = WebUtil.getBean(V2_WorkOrderService.class);
            cacheService = WebUtil.getBean(ConfigCacheService.class);
            orgTreeController = WebUtil.getBean(OrgTreeController.class);
            uaaService = WebUtil.getBean(UaaService.class);
            configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
            workOrderMgrController = WebUtil.getBean(WorkOrderMgrController.class);
            workOrderMgrMvsController = WebUtil.getBean(WorkOrderMgrMvsController.class);
            loginUser = UserContextService.getCurrentUserAccount();

            assetTypeGroupList = loginUser.getAssetTypeGroupList();
            initLoadInternalSiteNameList();
            getPageTypeFromConfig();

        } catch (Exception e) {
            LOGGER.error("WorkOrderUnifiedController init error", e);
            throw e;
        }
    }
    public void getPageTypeFromConfig() {
        String  key= "WorkOrderDetailViewType";
        pageType = configItemValueRepository.findByKeyAndSiteForUseParent(loginUser.getSiteUID(), loginUser.getHospitalUID(), loginUser.getInstitutionUID(), loginUser.getTenantUID(), key);

        if(StrUtil.isEmpty(pageType)){
            pageType = "current";
        }
    }


    @Override
    protected Page<V2_WorkOrder> loadData(PageRequest pageRequest) {
        if(this.searchFilters == null){
            this.searchFilters = new ArrayList<>();
        }
        Map<String, Object> params = initializeParams();
        this.processSearchFilters(params);
        Page<V2_WorkOrder> page = workOrderUnifiedService.findUnifiedWorkOrders(pageRequest, params);
        bySearchFilter = page.getContent();

        return page;
    }

    private Map<String, Object> initializeParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("userUid", loginUser.getUid());
        params.put("tenantUid", loginUser.getTenantUID());
        if (!CollectionUtils.isEmpty(clinicalDeptOrgUidSet)) {
            params.put("assetInfoClinicalDeptUID", clinicalDeptOrgUidSet.stream().toArray(String[]::new));
        }
        if (StringUtils.isNotEmpty(siteUidFilter)) {
            params.put("assetInfoSiteUID", siteUidFilter);
        }
        if (loginUser.isEnableAssetTypeGroup() && !org.apache.commons.collections4.CollectionUtils.isEmpty(assetTypeGroupList)) {
            List<String> encodedList = assetTypeGroupList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toList());
            params.put("assetInfoAssetType", encodedList.stream().toArray(String[]::new));
        }
        if (ObjectUtil.isNotEmpty(selectedAccessOrgNodes)) {
            List<String> selectedSiteList = ListUtil.newArrayList();
            for (TreeNode selectedAccessOrgNode : selectedAccessOrgNodes) {
                OrgInfo orgInfo = (OrgInfo) selectedAccessOrgNode.getData();
                selectedSiteList.add(orgInfo.getUid());
            }
            params.put("siteUidList", selectedSiteList);
        }
        return params;
    }

    private void processSearchFilters(Map<String, Object> params) {
        for (SearchFilter searchFilter : searchFilters) {
            String fieldName = searchFilter.fieldName;
            switch (fieldName) {
                case "assetInfo.name":
                    params.put("assetName", searchFilter.value);
                    break;
                case "assetInfo.serialNum":
                    params.put("assetSerialNum", searchFilter.value);
                    break;
                case "assetInfo.departNum":
                    params.put("departNum", searchFilter.value);
                    break;
                case "assetInfo.functionType":
                    params.put("functionType", searchFilter.value);
                    break;
                case "serviceRequest.srNum":
                    params.put("srNum", searchFilter.value);
                    break;
                case "serviceRequest.casePriority":
                    params.put("casePriority", searchFilter.value);
                    break;
                case "siteUID":
                    params.put("siteUID", searchFilter.value);
                    break;
                case "locationName":
                    params.put("locationName", searchFilter.value);
                    break;
                case "assetInfo.clinicalDeptName":
                    params.put("clinicalDeptName", searchFilter.value);
                    break;
                case "intExtType":
                    params.put("intExtType", searchFilter.value);
                    break;
                case "serviceRequest.requestTime":
                    if(searchFilter.value != null){
                        if(searchFilter.operator.equals(SearchFilter.Operator.GTE)){
                            params.put("requestTimeGTE", searchFilter.value);
                        }else if (searchFilter.operator.equals(SearchFilter.Operator.LTE)){
                            params.put("requestTimeLTE", searchFilter.value);
                        }
                    }
                    break;
                case "mainSub":
                    params.put("mainSub", searchFilter.value);
                    break;
                case "serviceRequest.requestReason":
                    params.put("requestReason", searchFilter.value);
                    break;
                case "currentStepName":
                    params.put("currentStepName", searchFilter.value);
                    break;
                default:
                    break;
            }
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

    public void onFilterWo(FilterEvent event) {
        onFilterCustom(event, this::filters);
    }

    public boolean filters(String fieldName, String filterVal) {
        if ("serviceRequest.requestTime".equals(fieldName)) {
            processTimeFiled(filterVal, fieldName);
            return true;
        }
        if ("id".equals(fieldName)) {
            List<String> stepIds = new ArrayList<>();
            List<V2_WorkOrder_Step> steps = woService.getWorkStepsByStepId(4,loginUser.getTenantUID());
            if (!CollectionUtils.isEmpty(steps)) {
                Map<String, List<V2_WorkOrder_Step>> map = steps.stream().collect(Collectors.groupingBy(V2_WorkOrder_Step::getWoId));
                List<V2_WorkOrder_Step> stepList = map.values().stream().map(group -> group.stream()
                                .max(Comparator.comparing(V2_WorkOrder_Step::getStartTime))
                                .orElse(null))
                        .filter(val -> val!=null&&val.getOwnerName().contains(filterVal))
                        .collect(Collectors.toList());
                removeFilterOnField("id");
                stepIds = stepList.stream().map(V2_WorkOrder_Step::getWoId).collect(Collectors.toList());
            }
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.IN, stepIds));
            return true;
        }
        return false;
    }

    public String getSiteOrgName(String siteUID) {
        OrgInfo org = cacheService.findOrgByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }

    public String getStepImg(V2_WorkOrder v2_workOrder){
        Locale locale = LocaleContextHolder.getLocale();
        boolean en = locale.ENGLISH.equals(locale);
        Integer stepId =v2_workOrder.getCurrentStepId();
        int mode =v2_workOrder.getDispatchMode()!=null?v2_workOrder.getDispatchMode().intValue():0;
        switch(stepId){
            case 7: //待评价
                return en?"/images/icons/en_5.png":"/images/icons/5.png";
            case 6: //
                return en?"/images/icons/en_5.png":"/images/icons/5.png";
            case 5: //待验收
            {
                if( v2_workOrder.getV2_workOrder_detail_list().size()>0 && v2_workOrder.getApprovalDate()==null){//有备件 没有提交审核 则显审核图标显示为灰色的
                    return en?"/images/icons/en_6.png":"/images/icons/6.png";
                }
                if(v2_workOrder.getV2_workOrder_detail_list().size() ==0){
                    return en?"/images/icons/en_6.png":"/images/icons/6.png";
                }
                else{
                    return en?"/images/icons/en_5.png":"/images/icons/5.png";
                }
            }
            case 8: //待审核
                return en?"/images/icons/en_4.png":"/images/icons/4.png";
            case 4: //维修中
            case 10: //待送修
            case 11: //待返还
            {
                if(v2_workOrder.getApprovalDate()!=null){
                    return   en?"/images/icons/en_4.png":"/images/icons/4.png";
                }
                return  en?"/images/icons/en_3.png":"/images/icons/3.png";
            }

            case 3: //接单中
                return en?"/images/icons/en_2.png":"/images/icons/2.png";
            case 2:
                if(mode== 2 || mode ==3 ){//如果是抢单或抢单加手动模式
                    return en?"/images/icons/en_2.png":"/images/icons/2.png";
                }
            default:
                return en?"/images/icons/en_1.png":"/images/icons/1.png";
        }
    }

    public String getRequestReasonInShort(String reason) {
        if (reason == null) {
            return null;
        }
        if (reason.length() <= 20) {
            return reason;
        } else {
            return reason.substring(0, 19) + "...";
        }
    }

    public OrgInfo getOrgInfoByUid(String orgUid) {
        return cacheService.findOrgByUid(orgUid);
    }

    public TreeNode getSiteOrgTree() {
        return orgTreeController.getSiteOrgTree(orgList);
    }

    public void initLoadInternalSiteNameList() {
        orgList = uaaService.getUserAccessibleSiteList(loginUser.getUid());
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

    //选定的院区
    private OrgInfo selectSite;
    //选定科室过滤
    private String filterClinicalDeptOrg;
    private String filterAssetDeptOrg;
    private void loadClinicalDeptList() {
        try {
            clinicalDeptList = Collections.emptyList();
            List<OrgInfo> deptList = uaaService.getAllAccessibleClinicalDeptList();
            if (!CollectionUtils.isEmpty(deptList)) {
                clinicalDeptList = deptList.stream()
                        .filter(item ->
                        {
                            if (item == null) {
                                LOGGER.info("null");
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
            LOGGER.error("loadClinicalDeptList occur exception", e);
            throw e;
        }
    }

    private void setTreeNodes(TreeNode root, Boolean isSelected) {
        root.setSelected(isSelected);
        for (TreeNode item : root.getChildren()) {
            setTreeNodes(item, isSelected);
        }
    }


    public List<OrgInfo> LoadSiteNameList() {
        if (CollectionUtils.isEmpty(orgList)) {
            if (!CollectionUtils.isEmpty(bySearchFilter)) {
                orgList = bySearchFilter.stream()
                        .map(item -> Optional.ofNullable(item.getAssetInfo())
                                .map(AssetInfo::getSiteUID)
                                .orElse(null)) // 如果 getSiteUID() 为 null，则使用 null
                        .filter(Objects::nonNull) // 过滤掉所有 null 值
                        .collect(Collectors.toSet()) // 转换为 Set 以去除重复项
                        .stream()
                        .map(val -> cacheService.findOrgByUid(val))
                        .collect(Collectors.toList());
            }
            if (!CollectionUtils.isEmpty(orgList)) {
                orgList = orgList.stream().filter(item -> 3 == item.getOrgType()).collect(Collectors.toList());
            }
        }
        return orgList;
    }

    public void onSelectOrderByUnified(SelectEvent event) throws IOException {
        selected = (V2_WorkOrder) event.getObject();
        if("Insite".equals(selected.getIssueType())){
            String url = "actionName=View&selectedid="
                    + selected.getId() + "&assetUid=" + selected.getAssetInfo().getUid()
                    + "&queryIndex=0"
                    ;
            url = "workOrderList.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("url", url);
        }else if("MVS".equals(selected.getIssueType())){
            String url = "actionName=View&selectedid="
                    + selected.getId() + "&assetUid=" + selected.getAssetInfo().getUid()
                    + "&queryIndex=0"
                    ;
            url = "mvs/woMvsMgrViewNew.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("url", url);
        }
    }

    public void onSelectOrderByUnifiedForCurrent(SelectEvent event) throws IOException {
        selected = (V2_WorkOrder) event.getObject();
        //先清空
        workOrderMgrController.clearSelectedWorkOrder();
        workOrderMgrMvsController.clearSelectedWorkOrder();
        if("Insite".equals(selected.getIssueType())){
            workOrderMgrController.setSelected( selected);
            workOrderMgrController.onSelectOrderByUnified(event);
        }else if("MVS".equals(selected.getIssueType())){
            workOrderMgrMvsController.setSelected(selected);
            workOrderMgrMvsController.onSelectWorkOrder();
        }
    }



}