package com.ge.apm.view.pm;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.pm.RelRuleAssetService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.PageUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class RelRuleAssetManagerController extends GenericCRUDUUIDController<RelRuleAsset> {

    private static final long serialVersionUID = -1L;

    private RelRuleAssetRepository dao = null;
    private UserAccount user;
    private PmOrderRuleRepository pmOrderRuleRepository;
    private AssetInfoRepository assetInfoRepository;
    private String queryCondition;
    private OrgInfoTreeService orgInfoTreeService;
    private List<String> assetUids;
    private OrgInfoRepository orgInfoRepository;
    private RelRuleAssetService relRuleAssetService;
    private UaaService uaaService;
    private boolean deleteRule2 = false;
    private boolean deleteRule3 = false;
    private boolean deleteRule1 = false;
    private boolean deleteRule4 = false;
    private Map<String, Boolean> selectedMap;
    private List<RelRuleAsset> currentPageAssets;
    private List<OrgInfo> clinicalDeptList;
    private List<PmOrderRule> pmOrderRuleList;
    private List<RelRuleAsset> currentEditAssets;
    private Map<String, String> deleteRuleMap;
    private List<String> deleteRuleCache;
    private V2_PmOrderRepository pmOrderRepository;
    private List<AssetTypeGroup> assetTypeGroupList;
    //选定科室过滤
    @Getter
    @Setter
    private String filterClinicalDeptOrg;
    //选定使用科室过滤
    @Getter
    @Setter
    private String filterAssetDeptOrg;
    //选定过滤规则
    private Integer filterRuleLevel = null;
    @Getter
    @Setter
    private String filterRuleUIDInspectionName;
    private Integer filterRuleUIDInspectionLevel = null;
    private String filterRuleUIDInspectionRuleUid = null;
    @Getter
    @Setter
    private String filterRuleUID2Name;
    private Integer filterRuleUID2Level = null;
    private String filterRuleUID2RuleUid = null;
    @Getter
    @Setter
    private String filterRuleUIDName;
    private Integer filterRuleUIDLevel = null;
    private String filterRuleUIDRuleUid = null;
    @Getter
    @Setter
    private String filterRuleUID4Name;
    private Integer filterRuleUID4Level = null;
    private String filterRuleUID4RuleUid = null;
    private Set<String> clinicalDeptOrgUidSet;
    private Set<String> assetDeptOrgUidSet;

    @Getter @Setter
    private String filterSiteName;
    private List<OrgInfo> siteList;
    private Set<String> siteOrgUidSet;

    @Getter
    @Setter
    private StreamedContent excelFile;
    private ConfigService configService;
    @Getter
    @Setter
    private String startUsingDateFormatTime = null;
    @Getter
    @Setter
    private String startPurchaseDateFormatTime = null;
    @Getter
    @Setter
    private String startConfigurationDateFormatTime = null;
    @Getter
    @Setter
    private boolean pmLevelFour;
    @Getter
    @Setter
    private Map<String, PmOrderRule> ruleMap;
    private ConfigCacheService configCacheService;

    @Override
    protected void init() {
        this.filterByHospital = false;
        this.filterBySite = false;
        dao = WebUtil.getBean(RelRuleAssetRepository.class);
        user = UserContextService.getCurrentUserAccount();
        pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        relRuleAssetService = WebUtil.getBean(RelRuleAssetService.class);
        configService = WebUtil.getBean(ConfigService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        pmOrderRepository = WebUtil.getBean(V2_PmOrderRepository.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        ruleMap = new HashMap<>();
        this.selectedMap = new HashMap<>();
        this.currentPageAssets = new ArrayList<>();
        assetTypeGroupList = user.getAssetTypeGroupList();
        loadClinicalDeptList();
        loadSiteList();
        WorkflowConfig workflowConfig = configService.findConfigBySite(user.getSiteUID());
        pmLevelFour = workflowConfig != null && workflowConfig.getPmRuleLevelFour();
    }

    @Override
    protected RelRuleAssetRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<RelRuleAsset> loadData(PageRequest pageRequest) {
        ruleMap.clear();
        if (user.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(assetTypeGroupList)) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }
        List<SearchFilter> searchFilterList = new ArrayList<>();
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        if (filterRuleUIDInspectionLevel != null && StringUtils.isNotEmpty(filterRuleUIDInspectionRuleUid)) {
            searchFilterList.add(new SearchFilter("ruleUIDInspection", SearchFilter.Operator.EQ, filterRuleUIDInspectionRuleUid));
        }
        if (filterRuleUID2Level != null && StringUtils.isNotEmpty(filterRuleUID2RuleUid)) {
            searchFilterList.add(new SearchFilter("ruleUID2", SearchFilter.Operator.EQ, filterRuleUID2RuleUid));
        }
        if (filterRuleUIDLevel != null && StringUtils.isNotEmpty(filterRuleUIDRuleUid)) {
            searchFilterList.add(new SearchFilter("ruleUID", SearchFilter.Operator.EQ, filterRuleUIDRuleUid));
        }
        if (filterRuleUID4Level != null && StringUtils.isNotEmpty(filterRuleUID4RuleUid)) {
            String[] ruleUid4s = StringUtils.split(filterRuleUID4RuleUid, "|");
            searchFilterList.add(new SearchFilter("ruleUID4", SearchFilter.Operator.CONTAINS, Arrays.asList(ruleUid4s)));
        }
        if (user.isEnableAssetTypeGroup()) {
            searchFilterList.add(new SearchFilter("assetInfo.assetType", SearchFilter.Operator.IN, user.getAssetTypeGroupMap().keySet()));
        }
        if (ObjectUtil.isNotEmpty(clinicalDeptOrgUidSet)) {
            searchFilterList.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, clinicalDeptOrgUidSet));
        }
        if (ObjectUtil.isNotEmpty(assetDeptOrgUidSet)) {
            searchFilterList.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, assetDeptOrgUidSet));
        }
        if (ObjectUtil.isNotEmpty(siteOrgUidSet)) {
            searchFilterList.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, siteOrgUidSet));
        }
        searchFilterList.addAll(searchFilters);
        setSearchFilter(searchFilterList);
        if (StringUtils.isNotEmpty(queryCondition)) {
            searchFilterList.addAll(getAssetInfoByFilter());
        }

        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
        pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        Page<RelRuleAsset> relRuleAssetPage = dao.findBySearchFilter(searchFilterList, pageRequest);
        currentPageAssets = relRuleAssetPage.getContent();
        initInspectionRuleMap(currentPageAssets);
        return relRuleAssetPage;
    }

    public void setSearchFilter(List<SearchFilter> searchFilterList) {
        searchFilterList.addAll(getOrgFilter(user.getSiteUID()));
        searchFilterList.add(new SearchFilter("assetInfo.isDeleted", SearchFilter.Operator.EQ, false));
        searchFilterList.add(new SearchFilter("assetInfo.isValid", SearchFilter.Operator.EQ, true));
        searchFilterList.add(new SearchFilter("assetInfo.isPm", SearchFilter.Operator.EQ, true));
        searchFilterList.add(new SearchFilter("assetInfo.status", SearchFilter.Operator.IN, Lists.newArrayList(1, 2, 3)));
    }

    private void initInspectionRuleMap(List<RelRuleAsset> relRuleAssetList) {
        List<String> ruleUidList = ListUtil.newArrayList();
        for (RelRuleAsset relRuleAsset : relRuleAssetList) {
            addRuleUidList(relRuleAsset.getRuleUIDInspection(), ruleUidList);
            addRuleUidList(relRuleAsset.getRuleUID2(), ruleUidList);
            addRuleUidList(relRuleAsset.getRuleUID(), ruleUidList);
            addRuleUidList(relRuleAsset.getRuleUID4(), ruleUidList);
        }
        List<PmOrderRule> pmOrderRules = pmOrderRuleRepository.findByUidIn(ruleUidList);
        ruleMap = pmOrderRules.stream().collect(Collectors.toMap(PmOrderRule::getUid, t -> t));
    }

    private void addRuleUidList(String ruleUid, List<String> ruleUidList) {
        if (ObjectUtil.isNotEmpty(ruleUid)) {
            ruleUidList.addAll(Arrays.asList(StringUtils.split(ruleUid, '|')));
        }
    }

    public String getRuleName(String ruleUID) {
        List<PmOrderRule> pmOrderRules = ListUtil.newArrayList();
        for (String ruleUid : StringUtils.split(ruleUID, '|')) {
            if (ObjectUtil.isNotEmpty(ruleMap.get(ruleUid))) {
                pmOrderRules.add(ruleMap.get(ruleUid));
            }
        }
        String ruleName = "";
        if (CollectionUtils.isNotEmpty(pmOrderRules)) {
            ruleName = pmOrderRules.stream().map(PmOrderRule::getName).collect(Collectors.joining("|"));
        }
        return ruleName;
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> Objects.equals(item.getOrgType(), 4)).collect(Collectors.toList());
    }

    private void loadSiteList() {
        siteList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> Objects.equals(item.getOrgType(), 3)).collect(Collectors.toList());
    }

    public List<String> autoCompleteSiteName(String query) {
        if (siteList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.siteList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(OrgInfo::getName).distinct().collect(Collectors.toList());
    }

    public void onSiteNameFilterChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            filterSiteName = (String) ((SelectEvent) event).getObject();
        }
        if (!Strings.isNullOrEmpty(filterSiteName) && !siteList.stream().anyMatch(item -> filterSiteName.equals(item.getName()))) {
            filterSiteName = null;
        }
        if (filterSiteName != null) {
            List<OrgInfo> orgInfosMatchingByName = this.siteList.stream()
                    .filter(item -> item.getName().equals(filterSiteName)).collect(Collectors.toList());
            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, siteList);
            siteOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            siteOrgUidSet = Collections.emptySet();
        }
    }

    public List<String> autoLevelRulePMName(String query) {
        FacesContext context = FacesContext.getCurrentInstance();
        Integer level = Integer.valueOf(UIComponent.getCurrentComponent(context).getAttributes().get("level").toString() == null
                ? "0" : UIComponent.getCurrentComponent(context).getAttributes().get("level").toString());
        getRuleList();
        filterRuleLevel = level;
        switch (level) {
            case 1:
                filterRuleUIDInspectionLevel = level;
                break;
            case 2:
                filterRuleUID2Level = level;
                break;
            case 3:
                filterRuleUIDLevel = level;
                break;
            case 4:
                filterRuleUID4Level = level;
                break;
            default:
                break;
        }
        pmOrderRuleList = pmOrderRuleList.stream().filter(s -> s.getLevel().equals(level) && s.getName().contains(query)).collect(Collectors.toList());
        return pmOrderRuleList.stream().map(PmOrderRule::getName).distinct().collect(Collectors.toList());
    }

    public void getRuleList() {
        List<SearchFilter> filter = new ArrayList<>();
        filter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        filter.add(new SearchFilter("status", SearchFilter.Operator.IN, Lists.newArrayList(1, 2, 3)));
        pmOrderRuleList = pmOrderRuleRepository.findBySearchFilter(filter);
        pmOrderRuleList.sort((o1, o2) -> {
            try {
                String name1 = PinyinHelper.convertToPinyinString(o1.getName(), "", PinyinFormat.WITHOUT_TONE);
                String name2 = PinyinHelper.convertToPinyinString(o2.getName(), "", PinyinFormat.WITHOUT_TONE);
                return name1.compareTo(name2);
            } catch (PinyinException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    public void onLevelRulePMNameFilterChange(AjaxBehaviorEvent event) {
        String filterLevelRulePMName;
        if (event instanceof SelectEvent) {
            filterLevelRulePMName = (String) ((SelectEvent) event).getObject();
            if (StringUtils.isNotEmpty(filterLevelRulePMName)) {
                switch (filterRuleLevel) {
                    case 1:
                        filterRuleUIDInspectionName = filterLevelRulePMName;
                        filterRuleUIDInspectionRuleUid = this.pmOrderRuleList.stream().filter(item -> item.getName().equals(filterLevelRulePMName)).collect(Collectors.toList()).get(0).getUid();
                        filterRuleUIDInspectionLevel = filterRuleLevel;
                        break;
                    case 2:
                        filterRuleUID2Name = filterLevelRulePMName;
                        filterRuleUID2RuleUid = this.pmOrderRuleList.stream().filter(item -> item.getName().equals(filterLevelRulePMName)).collect(Collectors.toList()).get(0).getUid();
                        filterRuleUID2Level = filterRuleLevel;
                        break;
                    case 3:
                        filterRuleUIDName = filterLevelRulePMName;
                        filterRuleUIDRuleUid = this.pmOrderRuleList.stream().filter(item -> item.getName().equals(filterLevelRulePMName)).collect(Collectors.toList()).get(0).getUid();
                        filterRuleUIDLevel = filterRuleLevel;
                        break;
                    case 4:
                        filterRuleUID4Name = filterLevelRulePMName;
                        filterRuleUID4RuleUid = this.pmOrderRuleList.stream().filter(item -> item.getName().equals(filterLevelRulePMName)).collect(Collectors.toList()).get(0).getUid();
                        filterRuleUID4Level = filterRuleLevel;
                        break;
                    default:
                        break;
                }
            }
        } else {
            switch (filterRuleLevel) {
                case 1:
                    filterRuleUIDInspectionName = null;
                    filterRuleUIDInspectionLevel = null;
                    break;
                case 2:
                    filterRuleUID2Name = null;
                    filterRuleUID2Level = null;

                    break;
                case 3:
                    filterRuleUIDName = null;
                    filterRuleUIDLevel = null;
                    break;
                case 4:
                    filterRuleUID4Name = null;
                    filterRuleUID4Level = null;
                    break;
                default:
                    break;
            }
        }
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
        return res.stream().map(OrgInfo::getName).distinct().collect(Collectors.toList());
    }

    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterClinicalDeptOrg = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(filterClinicalDeptOrg) && clinicalDeptList.stream().noneMatch(item -> filterClinicalDeptOrg.equals(item.getName()))) {
            filterClinicalDeptOrg = null;
        }
        if (filterClinicalDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterClinicalDeptOrg)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
        }
    }

    public void onAssetDeptNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterAssetDeptOrg = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(filterAssetDeptOrg) && clinicalDeptList.stream().noneMatch(item -> filterAssetDeptOrg.equals(item.getName()))) {
            filterAssetDeptOrg = null;
        }
        if (filterAssetDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterAssetDeptOrg)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            assetDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            assetDeptOrgUidSet = Collections.emptySet();
        }
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = currentPageAssets.stream().noneMatch(item -> !selectedMap.containsKey(item.getAssetUID()) || !selectedMap.get(item.getAssetUID()));
        for (RelRuleAsset item : currentPageAssets) {
            selectedMap.put(item.getAssetUID(), !isSelectedAll);
        }
    }

    public void batchDelete() {
        Map<String, Boolean> map = this.selectedMap;
        List<String> deleteAssetUids = Lists.newArrayList();
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (Boolean.FALSE.equals(entry.getValue())) {
                continue;
            }
            deleteAssetUids.add(entry.getKey());
        }
        dao.deleteByAssetUids(deleteAssetUids);
        this.selectedMap.clear();
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }

    @Override
    public List<RelRuleAsset> getItemList() {
        return dao.find();
    }

    public List<SearchFilter> getAssetInfoByFilter() {
        List<SearchFilter> searchAssetFilters = ListUtil.newArrayList();
        if (StringUtils.isNotEmpty(queryCondition)) {
            searchAssetFilters.add(new SearchFilter("assetInfo.name", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
            searchAssetFilters.add(new SearchFilter("assetInfo.departNum", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
            searchAssetFilters.add(new SearchFilter("assetInfo.financingNum", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
            searchAssetFilters.add(new SearchFilter("assetInfo.qrCode", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
            searchAssetFilters.add(new SearchFilter("assetInfo.serialNum", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
            searchAssetFilters.add(new SearchFilter("assetInfo.manufacture", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
        }
        return searchAssetFilters;
    }

    /**
     * 导出已经配置了规则的设备
     */
    public void exportRelRuleAssets() {
        try {

            if (user.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(assetTypeGroupList)) {
                List<AssetInfoAndRelRule> assetInfoAndRelRules = new ArrayList<>();
                excelFile = relRuleAssetService.getExcelFile(assetInfoAndRelRules);
                return;
            }
            searchFilters.addAll(getOrgFilter(user.getSiteUID()));
            List<RelRuleAsset> relRuleAssets = dao.findBySearchFilter(this.searchFilters);
            List<String> collect = relRuleAssets.stream().map(RelRuleAsset::getAssetUID).collect(Collectors.toList());
            List<SearchFilter> assetSearchFilters = new ArrayList<>();
            assetSearchFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, collect));
            assetSearchFilters.addAll(getOrgFilter(user.getSiteUID()));
            assetSearchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
            assetSearchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
            assetSearchFilters.add(new SearchFilter("isPm", SearchFilter.Operator.EQ, true));
            assetSearchFilters.add(new SearchFilter("status", SearchFilter.Operator.IN, Lists.newArrayList(1, 2, 3)));
            addFilterAssetTypeGroup(assetSearchFilters);
            List<AssetInfo> assetInfos = assetInfoRepository.findBySearchFilter(assetSearchFilters);
            List<AssetInfoAndRelRule> assetInfoAndRelRules = relRuleAssetService.mergeAssetInfoAndRelRule(assetInfos, relRuleAssets);
            excelFile = relRuleAssetService.getExcelFile(assetInfoAndRelRules);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        searchFilters.removeAll(getOrgFilter(user.getSiteUID()));
    }

    /**
     * 导出所有设备
     */
    public void exportAllAssets() {
        try {
            if (user.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(assetTypeGroupList)) {
                List<AssetInfoAndRelRule> assetInfoAndRelRules = new ArrayList<>();
                excelFile = relRuleAssetService.getExcelFile(assetInfoAndRelRules);
                return;
            }
            List<AssetInfo> assetInfos = findAllAssets();
            List<RelRuleAsset> relRuleAssets = dao.findByAssetUids(assetInfos.stream().map(AssetInfo::getUid).collect(Collectors.toList()));
            List<AssetInfoAndRelRule> assetInfoAndRelRules = relRuleAssetService.mergeAssetInfoAndRelRule(assetInfos, relRuleAssets);
            excelFile = relRuleAssetService.getExcelFile(assetInfoAndRelRules);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private List<AssetInfo> findAllAssets() {
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        searchFilters.add(new SearchFilter("isPm", SearchFilter.Operator.EQ, true));
        searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        if (!UserContextService.isMVSRole()) {
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        } else {
            searchFilters.add(new SearchFilter("mvsEquip", SearchFilter.Operator.EQ, 1));
        }
        addFilterAssetTypeGroup(searchFilters);
        return assetInfoRepository.findBySearchFilter(searchFilters);
    }

    private void addFilterAssetTypeGroup(List<SearchFilter> searchFilters) {
        if (user != null && user.isEnableAssetTypeGroup() && CollectionUtils.isNotEmpty(assetTypeGroupList)) {
            List<String> encodedList = assetTypeGroupList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetType", SearchFilter.Operator.IN, new ArrayList<>(encodedList), "1*"));
        }
    }

    public List<SearchFilter> getOrgFilter(String siteUID) {
        List<SearchFilter> filter = new ArrayList<>();
        OrgInfo orgInfo = orgInfoRepository.getByUid(siteUID);
        if (Objects.equals(orgInfo.getOrgType(), 3)) {
            // 院区
            filter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
        } else if (Objects.equals(orgInfo.getOrgType(), 2)) {
            // 医院
            filter.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
        } else if (Objects.equals(orgInfo.getOrgType(), 1)) {
            // 集团
            filter.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
        } else {// 租户
            filter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
        }
        return filter;
    }

    public void setOneSearchStr() {
        queryCondition = null;
    }

    public String getQueryCondition() {
        return queryCondition;
    }

    public void setQueryCondition(String queryCondition) {
        this.queryCondition = queryCondition;
    }

    public List<String> getAssetUids() {
        return assetUids;
    }

    public void setAssetUids(List<String> assetUids) {
        this.assetUids = assetUids;
    }

    //@Transactional
    public void deleteRule() {
        if ((!deleteRule2) && (!deleteRule3) && (!deleteRule1) && (!deleteRule4)) {
            clearCache();
            return;
        }

        List<V2_PmOrder> pmOrder = getPmOrder();
        if (CollectionUtils.isNotEmpty(pmOrder)) {
            StringBuilder tip = new StringBuilder("删除失败!\r\n");
            for (V2_PmOrder v2_pmOrder : pmOrder) {
                String assetName = v2_pmOrder.getAssetName();
                assetName = StringUtils.isBlank(assetName) ? "" : assetName;
                String pmNum = v2_pmOrder.getPmNum();
                pmNum = StringUtils.isBlank(pmNum) ? "" : pmNum;
                tip.append("设备：").append(assetName).append(",保养工单：" + pmNum).append(" 未完成 \r\n");
            }
            WebUtil.addSuccessMessage(tip.toString());
            clearCache();
            return;
        }
        String alert = null;
        if (deleteRule2) {
            currentEditAssets.forEach(item -> item.setRuleUID2(null));
            alert = "二级规则";
        }
        if (deleteRule3) {
            if (StringUtils.isEmpty(alert)) {
                alert = "三级规则";
            } else {
                alert += "、三级规则";
            }
            currentEditAssets.forEach(item -> item.setRuleUID(null));
        }
        if (deleteRule1) {
            if (StringUtils.isEmpty(alert)) {
                alert = "巡检规则";
            } else {
                alert += "、巡检规则";
            }
            currentEditAssets.forEach(item -> item.setRuleUID1(null));
        }
        if (deleteRule4) {
            if (StringUtils.isEmpty(alert)) {
                alert = "质量控制规则";
            } else {
                alert += "、质量控制规则";
            }
            currentEditAssets.forEach(item -> item.setRuleUID4(null));
        }
        alert += "已成功删除！";
        List<RelRuleAsset> deleteRelRuleAsset = currentEditAssets.stream()
                .filter(item -> StringUtils.isBlank(item.getRuleUID())
                        && StringUtils.isBlank(item.getRuleUID1())
                        && StringUtils.isBlank(item.getRuleUID2())
                        && StringUtils.isBlank(item.getRuleUID4())).collect(Collectors.toList());

        List<RelRuleAsset> saveRelRuleAsset = currentEditAssets.stream()
                .filter(item -> StringUtils.isNotBlank(item.getRuleUID())
                        || StringUtils.isBlank(item.getRuleUID1())
                        || StringUtils.isBlank(item.getRuleUID2())
                        || StringUtils.isBlank(item.getRuleUID4())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(saveRelRuleAsset)) {
            dao.save(saveRelRuleAsset);
        }
        if (CollectionUtils.isNotEmpty(deleteRelRuleAsset)) {
            dao.delete(deleteRelRuleAsset);
        }
        clearCache();
        WebUtil.addSuccessMessage(alert);
    }

    public void clearCache() {
        deleteRule3 = false;
        deleteRule2 = false;
        deleteRule1 = false;
        deleteRule4 = false;
        currentEditAssets.clear();
        deleteRuleMap.clear();
        selectedMap.clear();
        deleteRuleCache.clear();
    }

    public List<V2_PmOrder> getPmOrder() {
        if (CollectionUtils.isEmpty(currentEditAssets) || CollectionUtils.isEmpty(deleteRuleCache)) {
            return ListUtil.empty();
        }
        List<String> assetUid = currentEditAssets.stream().map(RelRuleAsset::getAssetUID).collect(Collectors.toList());
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("assetUid", SearchFilter.Operator.IN, assetUid));
        searchFilters.add(new SearchFilter("pmRuleUid", SearchFilter.Operator.IN, deleteRuleCache));
        searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.EQ, false));
        return pmOrderRepository.findBySearchFilter(searchFilters);
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public List<RelRuleAsset> getCurrentPageAssets() {
        return currentPageAssets;
    }

    public void setCurrentPageAssets(List<RelRuleAsset> currentPageAssets) {
        this.currentPageAssets = currentPageAssets;
    }

    public void updateDeleteRule(Integer ruleNum) {
        switch (ruleNum) {
            case 1:  //巡检
                deleteRuleCache.add(deleteRuleMap.remove("rule1"));
                deleteRule1 = true;
                break;
            case 2: // 三级
                deleteRuleCache.add(deleteRuleMap.remove("rule2"));
                deleteRule2 = true;
                break;
            case 3:  // 三级
                deleteRuleCache.add(deleteRuleMap.remove("rule"));
                deleteRule3 = true;
                break;
            case 4: // 质量控制
                deleteRuleCache.add(deleteRuleMap.remove("rule4"));
                deleteRule4 = true;
                break;
            default:
                break;
        }
    }

    public void prepareEditRule() {
        Set<String> rule = new HashSet<>();
        Set<String> rule1 = new HashSet<>();
        Set<String> rule2 = new HashSet<>();
        Set<String> rule4 = new HashSet<>();
        Map<String, Boolean> selectedMap = this.selectedMap;
        Map<String, RelRuleAsset> relRuleAssetMap = currentPageAssets.stream().collect(Collectors.toMap(RelRuleAsset::getAssetUID, item -> item));
        currentEditAssets = selectedMap.entrySet().stream().filter(Map.Entry::getValue).map(item -> relRuleAssetMap.get(item.getKey())).collect(Collectors.toList());
        for (RelRuleAsset relRuleAsset : currentEditAssets) {
            String ruleUID = relRuleAsset.getRuleUID();
            String ruleUID1 = relRuleAsset.getRuleUID1();
            String ruleUID2 = relRuleAsset.getRuleUID2();
            String ruleUID4 = relRuleAsset.getRuleUID4();
            if (StringUtils.isNotBlank(ruleUID)) {
                rule.add(ruleUID);
            }
            if (StringUtils.isNotBlank(ruleUID1)) {
                rule1.add(ruleUID1);
            }
            if (StringUtils.isNotBlank(ruleUID2)) {
                rule2.add(ruleUID2);
            }
            if (StringUtils.isNotBlank(ruleUID4)) {
                rule4.add(ruleUID4);
            }
        }
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if (rule.size() > 1 || rule1.size() > 1 || rule2.size() > 1 || rule4.size() > 1) {
            WebUtil.addErrorMessage("资产保养规则不一致无法批量解除");
            requestContext.addCallbackParam("validationFailed", false);
        } else {
            requestContext.addCallbackParam("validationFailed", true);
            if (deleteRuleMap == null) {
                deleteRuleMap = new HashMap<>();
                deleteRuleCache = new ArrayList<>();
            }
            deleteRuleMap.put("rule", getRule(rule));
            deleteRuleMap.put("rule1", getRule(rule1));
            deleteRuleMap.put("rule2", getRule(rule2));
            deleteRuleMap.put("rule4", getRule(rule4));
            this.prepareEdit();
        }
    }

    private String getRule(Set<String> ruleSet) {
        String rule = null;
        Iterator<String> iterator = ruleSet.iterator();
        if (iterator.hasNext()) {
            rule = iterator.next();
        }
        return rule;
    }

    public String getSiteOrgName(String siteUID) {
        OrgInfo org = configCacheService.findOrgByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }

    public OrgInfo getOrgInfoByUid(String uid) {
        return configCacheService.findOrgByUid(uid);
    }

    public Map<String, String> getDeleteRuleMap() {
        return deleteRuleMap;
    }

    public void setDeleteRuleMap(Map<String, String> deleteRuleMap) {
        this.deleteRuleMap = deleteRuleMap;
    }

    public void setCurrentEditAssets(List<RelRuleAsset> currentEditAssets) {
        this.currentEditAssets = currentEditAssets;
    }

    public List<RelRuleAsset> getCurrentEditAssets() {
        return this.currentEditAssets;
    }
}