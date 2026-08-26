package com.ge.apm.view.pm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.ConfigItemValueRepository;
import com.ge.apm.dao.PmOrderRuleRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class V2PmOrderManuallyConfirmController extends GenericCRUDUUIDController<V2_PmOrder> implements ServerEventInterface {

    protected static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(V2PmOrderManuallyConfirmController.class);
    private static final int MAX_EXPORT_SELECTION = 500;

    private V2_PmOrderRepository dao;
    private ConfigCacheService configCacheService;
    private UserContextService userContextService;
    private ConfigService configService;
    private OrgInfoTreeService orgInfoTreeService;
    private UaaService uaaService;
    private ConfigItemValueRepository configItemValueRepository;
    private PmOrderRuleRepository pmOrderRuleRepository;

    List<SearchFilter> filterArrayList = new ArrayList<>();
    @Setter @Getter
    Page<V2_PmOrder> result;
    private UserAccount loginUser;
    @Setter @Getter
    private List<AssetTypeGroup> assetTypeGroupList;
    private List<V2_PmOrder> currentPagePmOrders;
    @Setter @Getter
    private Boolean isSelectedCurrentPage = false;
    @Setter @Getter
    private Map<String, Boolean> selectedMap;
    List<String> pmIdList = new ArrayList<String>();
    @Setter @Getter
    private String filterPmRuleLevel;
    @Getter @Setter
    private String fileterOrgName;
    @Getter @Setter
    private List<OrgInfo> clinicalDeptList;
    @Getter @Setter
    private String fileterAssetDeptOrgName;
    @Setter @Getter
    private Set<String> assetDeptOrgUidSet;
    @Setter @Getter
    private String plannedCompletionTimeFormat = null;
    @Setter @Getter
    private String actualCompletionTimeFormat = null;
    @Getter @Setter
    private TreeNode[] selectedAccessOrgNodes;
    @Getter @Setter
    private OrgInfo selectSite;
    @Setter @Getter
    private String filterClinicalDeptOrg;
    @Setter @Getter
    private String filterAssetDeptOrg;
    @Setter @Getter
    private Set<String> clinicalDeptOrgUidSet;
    @Getter @Setter
    private boolean showLocationNameFlag;
    @Getter @Setter
    private String siteUidFilter;
    @Setter @Getter
    private Object  ruleNameObj;
    @Setter @Getter
    private Object  periodObj;
    @Setter @Getter
    List<Map<String, String>> customMap = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(V2_PmOrderRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        userContextService = WebUtil.getBean(UserContextService.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        configService = WebUtil.getBean(ConfigService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);

        this.selectedMap = new HashMap();
        String configItemValue = configItemValueRepository.findByKeyAndSiteForUseParent(loginUser.getSiteUID(),loginUser.getHospitalUID(), loginUser.getInstitutionUID(), loginUser.getTenantUID(), "showLocationNameFlag");
        showLocationNameFlag =  StrUtil.isNotEmpty(configItemValue) && "true".equals(configItemValue);
        loadClinicalDeptList();

        String value = configItemValueRepository.findByKeyAndSiteForUseParent(
                loginUser.getSiteUID(), loginUser.getHospitalUID(),
                loginUser.getInstitutionUID(), loginUser.getTenantUID(),
                "pmOrederManuallyConfirmFlag");
        if(StringUtils.isNotBlank(value)){
            customMap = JSON.parseObject(value, new TypeReference<List<Map<String,String>>>(){});
        }
    }

    public Boolean showDisabled(Integer ruleLevel){
        if(ruleLevel == null){
            return true;
        }
        for(Map<String, String> map : customMap){
            if(map.get("value").equals(ruleLevel.toString())){
                return false;
            }
        }
        return true;
    }

    public void pick(String itemId){
        pmIdList = new ArrayList<>();
        selectedMap.forEach((k, v) -> {
            if (v == true)
                pmIdList.add(k);
        });
        if (pmIdList.size() > MAX_EXPORT_SELECTION) {
            WebUtil.addErrorMessage("最多只能勾选" + MAX_EXPORT_SELECTION + "条数据进行导出");
        }
    }

    @Override
    protected Page<V2_PmOrder> loadData(PageRequest pageRequest) {
        selected = null;
        if (loginUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(assetTypeGroupList)) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }

        this.setSearchFilter();

        if (!userContextService.hasRole("AssetHead") && !userContextService.isPmDispatcher()) {
            filterArrayList.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            filterArrayList.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        }
        filterArrayList.add(new SearchFilter("currentStepName", SearchFilter.Operator.EQ, "已关单"));
        filterArrayList.add(new SearchFilter("isFinished", SearchFilter.Operator.EQ, true));

        Sort sort = returnSort(pageRequest);
        pageRequest = new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),sort);
        result = dao.findBySearchFilter(this.filterArrayList, pageRequest);
        currentPagePmOrders = result.getContent();
        this.isSelectedCurrentPage = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        result.getContent().forEach(item->pmIdList.add(item.getId()));

        return result;

    }


    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        this.updateSearchFilter(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID(), "1*"));
        filterArrayList.clear();
        if (ObjectUtil.isNotEmpty(selectedAccessOrgNodes)) {
            List<String> selectedSiteList = ListUtil.newArrayList();
            for (TreeNode selectedAccessOrgNode : selectedAccessOrgNodes) {
                OrgInfo orgInfo =(OrgInfo) selectedAccessOrgNode.getData();
                selectedSiteList.add(orgInfo.getUid());
            }
            filterArrayList.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, selectedSiteList, "1*3+"));
            filterArrayList.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, selectedSiteList, "1*3+"));
            filterArrayList.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, selectedSiteList, "1*3+"));
            filterArrayList.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, selectedSiteList, "1*3+"));
        }
        updatePMRuleFilterChange();
        searchFilters.removeAll(searchFilters.stream().filter(item -> "siteUID".equals(item.fieldName)).collect(Collectors.toList()));
        if (StringUtils.isNotEmpty(siteUidFilter)) {
            searchFilters.add((new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUidFilter, "1*")));
        }
        if (clinicalDeptOrgUidSet != null && !clinicalDeptOrgUidSet.isEmpty()) {
            filterArrayList.add((new SearchFilter("clinicalDeptId", SearchFilter.Operator.IN, new ArrayList<>(clinicalDeptOrgUidSet), "1*")));
        }
        if (assetDeptOrgUidSet != null && !assetDeptOrgUidSet.isEmpty()) {
            filterArrayList.add((new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, new ArrayList<>(assetDeptOrgUidSet), "1*")));
        }
        if(this.hasFilterOnField("confirmFlag")){
            List<SearchFilter> ss = searchFilters.stream().filter(item -> "confirmFlag".equals(item.fieldName)).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(ss)){
                SearchFilter searchFilter = ss.get(0);
                if(searchFilter.value.equals("Y")){
                    filterArrayList.add(new SearchFilter("confirmFlag", SearchFilter.Operator.EQ, "Y"));
                }else {
                    filterArrayList.add(new SearchFilter("confirmFlag", SearchFilter.Operator.EQ, "N", "1*4+"));
                    filterArrayList.add(new SearchFilter("confirmFlag", SearchFilter.Operator.IsNull, "", "1*4+"));
                }
            }
            searchFilters.removeAll(searchFilters.stream().filter(item -> "confirmFlag".equals(item.fieldName)).collect(Collectors.toList()));
        }

        filterArrayList.addAll(searchFilters);
        if (!this.hasFilterOnField("assetInfo.siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            filterArrayList.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filterArrayList.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filterArrayList.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filterArrayList.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filterArrayList.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));//所属科室
            filterArrayList.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));//使用科室
        }

        // 资产分类权限
        if (loginUser.isEnableAssetTypeGroup() && !CollectionUtils.isEmpty(assetTypeGroupList)) {
            List<String> encodedList = assetTypeGroupList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toList());
            filterArrayList.add(new SearchFilter("assetInfo.assetType", SearchFilter.Operator.IN, new ArrayList<>(encodedList), "1*"));
        }
    }

    public void updatePMRuleFilterChange(){
        if(ruleNameObj != null || periodObj != null) {
            Set<String> pmRuleUids = getPMRuleUids(ruleNameObj, periodObj);
            if (CollectionUtils.isEmpty(pmRuleUids)) {
                pmRuleUids.add("-1");
            }
            filterArrayList.add(new SearchFilter("pmRuleUid", SearchFilter.Operator.IN, pmRuleUids));
        }
    }

    private Set<String> getPMRuleUids(Object ruleNameObj,Object periodObj){
        String ruleName = "";
        Integer period = null;
        if(ruleNameObj != null){
            ruleName = String.valueOf(ruleNameObj);
        }
        if(periodObj != null){
            period = Integer.parseInt(String.valueOf(periodObj));
        }
        List<SearchFilter> pmRuleFilter = new ArrayList<>();
        if(StringUtils.isNotEmpty(ruleName)) {
            pmRuleFilter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, ruleName));
        }
        if(period != null) {
            pmRuleFilter.add(new SearchFilter("period", SearchFilter.Operator.EQ, period));
        }
        List<PmOrderRule> pmOrderRules = pmOrderRuleRepository.findBySearchFilter(pmRuleFilter);
        return pmOrderRules.stream().map(PmOrderRule::getUid).collect(Collectors.toSet());
    }

    protected Sort returnSort(PageRequest pageRequest){
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.DESC,"planTime"));
        orders.add(new Sort.Order(Sort.Direction.DESC,"pmNum"));
        Sort.Order orderFor = pageRequest.getSort().getOrderFor("planTime");
        //默认是计划完成时间按降序排,如果选择升续操作 就会加入下面的column
        if(orderFor!=null) {
            if (orderFor.getDirection().equals(Sort.Direction.ASC)) {
                orders = new ArrayList<Sort.Order>();
                orders.add(new Sort.Order(Sort.Direction.ASC, "planTime"));
                orders.add(new Sort.Order(Sort.Direction.ASC, "pmNum"));
            }
        }else{
            //如果选择除计划完成时间的其它列 进行排序
            orders =  acqListSortOrder(pageRequest);
        }
        Sort sort = new Sort(orders);
        return sort;
    }

    private  List<Sort.Order> acqListSortOrder(PageRequest pageRequest){
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        Sort sort1 = pageRequest.getSort();
        String filterName = sort1.toString().substring(0,sort1.toString().indexOf(":"));
        Sort.Order orderFor =  pageRequest.getSort().getOrderFor(filterName);
        if(orderFor!=null) {
            if (orderFor.getDirection().equals(Sort.Direction.ASC)) {
                orders = new ArrayList<Sort.Order>();
                orders.add(new Sort.Order(Sort.Direction.ASC, filterName));
            }else{
                orders = new ArrayList<Sort.Order>();
                orders.add(new Sort.Order(Sort.Direction.DESC, filterName));
            }
        }
        return orders;
    }

    public void onSelectAllOfPage() {
        // isSelectedCurrentPage 已由 PrimeFaces 在 Update Model 阶段设置为 header checkbox 的提交值
        // true=用户要全选, false=用户要取消全选
        if (Boolean.TRUE.equals(isSelectedCurrentPage)) {
            // 全选当前页，检查加上当前页未选中的数量是否会超过500
            long currentSelectedCount = selectedMap.values().stream().filter(v -> v).count();
            long currentPageUnselected = currentPagePmOrders.stream().filter(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId())).count();
            if (currentSelectedCount + currentPageUnselected > MAX_EXPORT_SELECTION) {
                WebUtil.addErrorMessage("最多只能勾选" + MAX_EXPORT_SELECTION + "条数据，当前已勾选" + currentSelectedCount + "条，本页将选择" + (MAX_EXPORT_SELECTION - currentSelectedCount) + "条");
                // 只选择允许数量内的条目
                long remaining = MAX_EXPORT_SELECTION - currentSelectedCount;
                for (V2_PmOrder item : currentPagePmOrders) {
                    if (!selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId())) {
                        if (remaining > 0) {
                            selectedMap.put(item.getId(), true);
                            remaining--;
                        } else {
                            selectedMap.put(item.getId(), false);
                        }
                    }
                }
                // 因为可能没有全部选中，重新计算 isSelectedCurrentPage
                isSelectedCurrentPage = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
            } else {
                for (V2_PmOrder item : currentPagePmOrders) {
                    selectedMap.put(item.getId(), true);
                }
                isSelectedCurrentPage = true;
            }
        } else {
            // 取消全选当前页（仅影响当前页，不影响其他页的勾选）
            for (V2_PmOrder item : currentPagePmOrders) {
                selectedMap.put(item.getId(), false);
            }
            isSelectedCurrentPage = false;
        }
        pmIdList = new ArrayList<>();
        selectedMap.forEach((k, v) -> {
            if (v == true)
                pmIdList.add(k);
        });
    }

    public String getSiteOrgName(String siteUID) {
        OrgInfo org = configCacheService.findOrgByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }

    public boolean getPmRule(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        WorkflowConfig workflowConfig = configService.findConfigBySite(userAccount.getSiteUID());
        return workflowConfig != null && workflowConfig.getPmRuleLevelFour();
    }

    public OrgInfo getOrgInfoByUid(String orgUid) {
        return configCacheService.findOrgByUid(orgUid);
    }

    public void onAssetDeptNameFilterChange(AjaxBehaviorEvent event) {
        selectedMap.clear();
        if (event instanceof SelectEvent) {
            fileterAssetDeptOrgName = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(fileterAssetDeptOrgName) && !clinicalDeptList.stream().anyMatch(item -> fileterAssetDeptOrgName.equals(item.getName()))) {
            fileterAssetDeptOrgName = null;
        }

        if (fileterAssetDeptOrgName != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(fileterAssetDeptOrgName)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            assetDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            assetDeptOrgUidSet = Collections.emptySet();
        }
    }

    public String getPeriodUnit(String periodUnit){
        if (StringUtils.isNotEmpty(periodUnit)) {
            switch (periodUnit) {
                case "y":
                    return "年";
                case "m":
                    return "月";
                case "w":
                    return "周";
                case "d":
                    return "日";
                default:
                    break;
            }
        }
        return null;
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
            if (!org.springframework.util.CollectionUtils.isEmpty(deptList)) {
                clinicalDeptList = deptList.stream()
                        .filter(item ->
                        {
                            if (item == null) {
                                LOGGER.info("null");
                            }
                            return item.getOrgType() != null && item.getOrgType() == 4;
                        }).collect(Collectors.toList());
            }

            if (null != selectSite && !clinicalDeptList.isEmpty()) {
                clinicalDeptList = clinicalDeptList.stream()
                        .filter(item -> com.alibaba.druid.util.StringUtils.equals(item.getSiteUID(), selectSite.getSiteUID())
                                || com.alibaba.druid.util.StringUtils.equals(item.getHospitalUID(), selectSite.getSiteUID())
                                || com.alibaba.druid.util.StringUtils.equals(item.getInstitutionUID(), selectSite.getSiteUID())
                                || com.alibaba.druid.util.StringUtils.equals(item.getTenantUID(), selectSite.getSiteUID()))
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

    public void confirmPm(String id){
        V2_PmOrder pmOrder = dao.findById(id);
        pmOrder.setConfirmFlag("Y");
        dao.save(pmOrder);
    }

    @Override
    protected GenericRepositoryUUID<V2_PmOrder> getDAO() {
        return dao;
    }
}
