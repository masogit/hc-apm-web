package com.ge.apm.view.pm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.ConfigItemValueRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.dao.mapper.MvsPmOrderMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
public class V2PmOrderMvsManuallyConfirmController extends GenericCRUDUUIDController<V2_PmOrder> implements ServerEventInterface {
    protected static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(V2PmOrderMvsManuallyConfirmController.class);

    private MvsPmOrderMapper mvsPmOrderMapper;
    private V2_PmOrderRepository dao;
    private List<AssetTypeGroup> assetTypeGroupList;
    private ConfigService configService;
    private ConfigCacheService configCacheService;
    private OrgInfoTreeService orgInfoTreeService;
    private UserContextService userContextService;
    private ConfigItemValueRepository configItemValueRepository;
    private UserAccount loginUser;
    private UaaService uaaService;
    private Boolean isSelectedCurrentPage = false;
    private List<V2_PmOrder> currentPagePmOrders;
    @Getter @Setter
    private Map<String, Boolean> selectedMap;
    List<String> pmIdList = new ArrayList<String>();
    private List<OrgInfo> clinicalDeptListMVS;
    @Setter @Getter
    private Set<String> clinicalDeptOrgUidSetMVS;
    @Setter @Getter
    private Set<String> assetDeptOrgUidSetMVS;
    @Getter @Setter
    private String siteUidFilterMVS;
    @Getter @Setter
    private String filterPmRuleLevel;
    @Getter @Setter
    private String fileterOrgNameMVS;
    @Getter @Setter
    private String fileterAssetDeptOrgNameMVS;
    @Getter @Setter
    private String stepFileter;
    @Getter @Setter
    private List<OrgInfo> orgListMVS;
    @Getter @Setter
    private String plannedCompletionTimeFormat = null;
    @Getter @Setter
    private String actualCompletionTimeFormat = null;
    @Setter @Getter
    List<Map<String, String>> customMap = new ArrayList<>();


    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(V2_PmOrderRepository.class);
        mvsPmOrderMapper = WebUtil.getBean(MvsPmOrderMapper.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        loginUser = UserContextService.getCurrentUserAccount();
        configService = WebUtil.getBean(ConfigService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);

        orgListMVS = new ArrayList<>();

        assetTypeGroupList = loginUser.getAssetTypeGroupList();
        this.selectedMap = new HashMap();
        String value = configItemValueRepository.findByKeyAndSiteForUseParent(
                loginUser.getSiteUID(), loginUser.getHospitalUID(),
                loginUser.getInstitutionUID(), loginUser.getTenantUID(),
                "pmOrederManuallyConfirmFlag");
        if(StringUtils.isNotBlank(value)){
            customMap = JSON.parseObject(value, new TypeReference<List<Map<String,String>>>(){});
        }
    }

    @Override
    protected Page<V2_PmOrder> loadData(PageRequest pageRequest) {
        this.selected = null;
        Page<V2_PmOrder> result;
        if (loginUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(assetTypeGroupList)) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }
        if(!userContextService.accessContainsFunctionMenu("MAINTAIN_ORDER_TAKE")
                &&!userContextService.accessContainsFunctionMenu("MAINTAIN_ORDER_VIEW_ALL")){
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        Map<String, Object> params = initializeParams();
        // 处理搜索过滤器
        processSearchFilters(params);
        // 根据查询索引设置参数
        setParamsByQueryIndex(params);
        List<V2_PmOrder> v2PmOrderList = mvsPmOrderMapper.loadData(params);
        result= new PageImpl<V2_PmOrder>(this.pageNum(v2PmOrderList, pageRequest), pageRequest, v2PmOrderList.size());
        currentPagePmOrders = result.getContent();
        result.getContent().forEach(item->pmIdList.add(item.getId()));
        this.isSelectedCurrentPage = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        if(selectedMap.size() > 0){
            selectedMap = new HashMap();
        }
        loadMVSClinicalDeptList();
        return result;
    }
    private List<V2_PmOrder> pageNum(List<V2_PmOrder> v2PmOrderList,PageRequest pageRequest){
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize() - 1;
        int numStart = pageNumber * pageSize;
        numStart = numStart + pageNumber;
        int numEnd = numStart + pageSize + 1;
        int size = v2PmOrderList.size();
        if(size > numEnd){
            v2PmOrderList = v2PmOrderList.subList(numStart ,numEnd);
        }else {
            v2PmOrderList = v2PmOrderList.subList(numStart ,size);
        }
        return v2PmOrderList;
    }

    private Map<String, Object> initializeParams() {
        Map<String, Object> params = new HashMap<>();
       params.put("userUid", loginUser.getUid());
        params.put("tenantUid", loginUser.getTenantUID());
        if (!org.springframework.util.CollectionUtils.isEmpty(clinicalDeptOrgUidSetMVS)) {
            params.put("assetInfoClinicalDeptUID", clinicalDeptOrgUidSetMVS.stream().toArray(String[]::new));
        }
        if (!org.springframework.util.CollectionUtils.isEmpty(assetDeptOrgUidSetMVS)) {
            params.put("assetInfoAssetDeptUID", assetDeptOrgUidSetMVS.stream().toArray(String[]::new));
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(siteUidFilterMVS)) {
            params.put("assetInfoSiteUID", siteUidFilterMVS);
        }
        if (loginUser.isEnableAssetTypeGroup() && !CollectionUtils.isEmpty(assetTypeGroupList)) {
            List<String> encodedList = assetTypeGroupList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toList());
            params.put("assetInfoAssetType", encodedList.stream().toArray(String[]::new));
        }
        return params;
    }

    // 处理搜索过滤器
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
                case "ruleLevel":
                    params.put("ruleLevel", searchFilter.value);
                    break;
                case "pmType":
                    params.put("pmType", searchFilter.value);
                    break;
                case "pmNum":
                    params.put("pmNum", searchFilter.value);
                    break;
                case "planTime":
                    if (searchFilter.operator.toString().equals("GTE")){
                        params.put("planTimeGTE", searchFilter.value);
                    }
                    if (searchFilter.operator.toString().equals("LTE")){
                        params.put("planTimeLTE", searchFilter.value);
                    }
                    break;
                case "endTime":
                    if (searchFilter.operator.toString().equals("GTE")){
                        params.put("endTimeGTE", searchFilter.value);
                    }
                    if (searchFilter.operator.toString().equals("LTE")){
                        params.put("endTimeLTE", searchFilter.value);
                    }
                    break;
                case "mainSub":
                    params.put("mainSub", searchFilter.value);
                    break;
                case "ruleName":
                    params.put("ruleName", searchFilter.value);
                    break;
                case "period":
                    params.put("period", Integer.valueOf(searchFilter.value.toString()));
                    break;
                case "periodUnit":
                    params.put("periodUnit", searchFilter.value);
                    break;
                case "creatorName":
                    params.put("creatorName", searchFilter.value);
                    break;
                case "ownerName":
                    params.put("ownerName", searchFilter.value);
                    break;
                case "currentStepName":
                    params.put("currentStepName1", "已计划".equals(searchFilter.value) ? "进行中" : searchFilter.value);
                    break;
                case "intExtType":
                    params.put("intExtType", Integer.valueOf(searchFilter.value.toString()));
                case "confirmFlag":
                    params.put("confirmFlag", searchFilter.value);
                default:
                    break;
            }
        }
    }
    // 根据查询索引设置参数
    private void setParamsByQueryIndex(Map<String, Object> params) {
        String[] strArray = new String[]{"已关单"};
        params.put("currentStepName",strArray);
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

    private void loadMVSClinicalDeptList() {
        try {
            clinicalDeptListMVS = Collections.emptyList();
            List<OrgInfo> deptList = uaaService.getAllAccessibleClinicalDeptList();
            if (!org.springframework.util.CollectionUtils.isEmpty(deptList)) {
                clinicalDeptListMVS = deptList.stream()
                        .filter(item ->
                        {
                            if (item == null) {
                                LOGGER.info("null");
                            }
                            return item.getOrgType() != null && item.getOrgType() == 4;
                        })
                        .collect(Collectors.toList());
            }

        } catch (Exception e) {
            LOGGER.error("loadClinicalDeptList occur exception", e);
            throw e;
        }
    }

    public boolean getPmRule(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        WorkflowConfig workflowConfig = configService.findConfigBySite(userAccount.getSiteUID());
        return workflowConfig != null && workflowConfig.getPmRuleLevelFour();
    }

    public List<String> autoCompleteDeptNameMVS(String query) {
        if (clinicalDeptListMVS == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptListMVS.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public void onClinicalDeptNameFilterChangeMVS(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            fileterOrgNameMVS = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(fileterOrgNameMVS) && !clinicalDeptListMVS.stream().anyMatch(item -> fileterOrgNameMVS.equals(item.getName()))) {
            fileterOrgNameMVS = null;
        }

        if (fileterOrgNameMVS != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptListMVS.stream().filter(item -> item.getName().equals(fileterOrgNameMVS)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptListMVS);
            clinicalDeptOrgUidSetMVS = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSetMVS = Collections.emptySet();
        }
    }

    public OrgInfo getOrgInfoByUid(String orgUid) {
        return configCacheService.findOrgByUid(orgUid);
    }

    public List<OrgInfo> LoadSiteNameListMVS() {
        orgListMVS = uaaService.getUserAccessibleSiteList(loginUser.getUid());
        if (!org.springframework.util.CollectionUtils.isEmpty(orgListMVS)) {
            orgListMVS = orgListMVS.stream().filter(item -> 3 == item.getOrgType()).collect(Collectors.toList());
        } else {
            orgListMVS = Collections.emptyList();
        }
        return orgListMVS;
    }

    public String getSiteOrgName(String siteUID) {
        OrgInfo org = configCacheService.findOrgByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }


    public void onAssetDeptNameFilterChangeMVS(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            fileterAssetDeptOrgNameMVS = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(fileterAssetDeptOrgNameMVS) && !clinicalDeptListMVS.stream().anyMatch(item -> fileterAssetDeptOrgNameMVS.equals(item.getName()))) {
            fileterAssetDeptOrgNameMVS = null;
        }

        if (fileterAssetDeptOrgNameMVS != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptListMVS.stream().filter(item -> item.getName().equals(fileterAssetDeptOrgNameMVS)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptListMVS);
            assetDeptOrgUidSetMVS = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            assetDeptOrgUidSetMVS = Collections.emptySet();
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

    public boolean showLocationName(){
        String configItemValue = configItemValueRepository.findByKeyAndSiteForUseParent(loginUser.getSiteUID(),loginUser.getHospitalUID(), loginUser.getInstitutionUID(), loginUser.getTenantUID(), "showLocationNameFlag");
        if (StrUtil.isNotEmpty(configItemValue) && "true".equals(configItemValue)) {
            return true;
        }
        return false;
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
