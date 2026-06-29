package com.ge.apm.view.pm;

import com.ge.apm.dao.ConfigItemValueRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.dao.mapper.PmOrderMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
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
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class V2PmOrderUnifiedController extends GenericCRUDUUIDController<V2_PmOrder> implements ServerEventInterface {

    protected static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(V2PmOrderUnifiedController.class);

    private V2PmOrderController v2PmOrderController;
    private V2PmOrderMvsController v2PmOrderMvsController;
    private UserContextService userContextService;
    private PmOrderMapper pmOrderMapper;
    private ConfigService configService;
    private ConfigCacheService configCacheService;
    private UaaService uaaService;
    private OrgInfoTreeService orgInfoTreeService;
    private ConfigItemValueRepository configItemValueRepository;
    private V2_PmOrderRepository dao;
    private UserAccount loginUser;
    private List<AssetTypeGroup> assetTypeGroupList;
    @Getter
    @Setter
    List<V2_PmOrder> bySearchFilter = null;
    @Getter @Setter
    private String siteUidFilter;
    @Setter @Getter
    private Set<String> clinicalDeptOrgUidSet;
    @Getter @Setter
    private Integer repairTypeInt;
    @Getter @Setter
    private String filterPmRuleLevel;
    @Getter @Setter
    private List<OrgInfo> orgList;
    @Getter @Setter
    private String fileterOrgName;
    private List<OrgInfo> clinicalDeptList;
    private OrgInfo selectSite;
    @Getter @Setter
    private String stepFileter;
    @Getter @Setter
    private String plannedCompletionTimeFormat = null;
    @Getter @Setter
    private String actualCompletionTimeFormat = null;
    @Getter @Setter
    private String pageType;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(V2_PmOrderRepository.class);
        pmOrderMapper = WebUtil.getBean(PmOrderMapper.class);
        userContextService =  WebUtil.getBean(UserContextService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        configService =  WebUtil.getBean(ConfigService.class);
        configCacheService =  WebUtil.getBean(ConfigCacheService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        v2PmOrderController = WebUtil.getBean(V2PmOrderController.class);
        v2PmOrderMvsController = WebUtil.getBean(V2PmOrderMvsController.class);
        loginUser = UserContextService.getCurrentUserAccount();
        assetTypeGroupList = loginUser.getAssetTypeGroupList();
        orgList = new ArrayList<>();
        loadClinicalDeptList();
        getPageTypeFromConfig();
    }

    public void getPageTypeFromConfig() {
        String  key= "PmOrderDetailViewType";
        pageType = configItemValueRepository.findByKeyAndSiteForUseParent(loginUser.getSiteUID(), loginUser.getHospitalUID(), loginUser.getInstitutionUID(), loginUser.getTenantUID(), key);

        if(StrUtil.isEmpty(pageType)){
            pageType = "current";
        }
    }

    @Override
    protected Page<V2_PmOrder> loadData(PageRequest pageRequest) {
        Page<V2_PmOrder> result;
        if (loginUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(assetTypeGroupList)) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }

        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }

        Map<String, Object> params = initializeParams();
        // 处理搜索过滤器
        processSearchFilters(params);
        List<V2_PmOrder> v2PmOrderList = pmOrderMapper.queryPmOrderList(params);
        long totalCount = pmOrderMapper.queryPmOrderCount(params);
        result= new PageImpl<V2_PmOrder>(v2PmOrderList, pageRequest, totalCount);
        bySearchFilter = result.getContent();
        return result;
    }

    public void onFilterPo(FilterEvent event){
        super.onFilter(event);
        Map<String, Object> filters = event.getFilters();
    }

    // 初始化参数
    private Map<String, Object> initializeParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("userUid", loginUser.getUid());
        params.put("tenantUid", loginUser.getTenantUID());
        if (!org.springframework.util.CollectionUtils.isEmpty(clinicalDeptOrgUidSet)) {
            params.put("assetInfoClinicalDeptUID", clinicalDeptOrgUidSet.stream().toArray(String[]::new));
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(siteUidFilter)) {
            params.put("assetInfoSiteUID", siteUidFilter);
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
                    params.put("currentStepName1", searchFilter.value);
                    break;
                case "intExtType":
                    params.put("intExtType", Integer.valueOf(searchFilter.value.toString()));
                default:
                    break;
            }
        }
    }


    public boolean getPmRule(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        WorkflowConfig workflowConfig = configService.findConfigBySite(userAccount.getSiteUID());
        return workflowConfig != null && workflowConfig.getPmRuleLevelFour();
    }

    public String getSiteOrgName(String siteUID) {
        OrgInfo org = configCacheService.findOrgByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }

    public List<OrgInfo> LoadSiteNameList() {
        orgList = uaaService.getUserAccessibleSiteList(loginUser.getUid());
        if (!org.springframework.util.CollectionUtils.isEmpty(orgList)) {
            orgList = orgList.stream().filter(item -> 3 == item.getOrgType()).collect(Collectors.toList());
        } else {
            orgList = Collections.emptyList();
        }
        return orgList;
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
                        })
                        .collect(Collectors.toList());
            }

            if (null != selectSite && !clinicalDeptList.isEmpty()) {
                clinicalDeptList = clinicalDeptList.stream()
                        .filter(item -> com.alibaba.druid.util.StringUtils.equals(item.getSiteUID(), selectSite.getSiteUID())
                                || com.alibaba.druid.util.StringUtils.equals(item.getHospitalUID(), selectSite.getSiteUID())
                                || com.alibaba.druid.util.StringUtils.equals(item.getInstitutionUID(), selectSite.getSiteUID())
                                || com.alibaba.druid.util.StringUtils.equals(item.getTenantUID(), selectSite.getSiteUID()))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.error("loadClinicalDeptList occur exception", e);
            throw e;
        }
    }

    public OrgInfo getOrgInfoByUid(String orgUid) {
        return configCacheService.findOrgByUid(orgUid);
    }

    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
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

    public void onSelectOrderByUnified(SelectEvent event) throws IOException {
        selected = (V2_PmOrder) event.getObject();
        if("Insite".equals(selected.getIssueType())){
            String url = "actionName=View&selectedid="
                    + selected.getId() + "&assetUid=" + selected.getAssetInfo().getUid()
                    + "&queryIndex=0"
                    ;
            url = "pmOrderViewNew.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("url", url);
        }else if("MVS".equals(selected.getIssueType())){
            String url = "actionName=View&selectedid="
                    + selected.getId() + "&assetUid=" + selected.getAssetInfo().getUid()
                    + "&queryIndex=0"
                    ;
            url = "mvs/pmMvsOrderViewNew.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("url", url);
        }
    }

    public void onSelectOrderByUnifiedForCurrent(SelectEvent event){

        selected = (V2_PmOrder) event.getObject();
        v2PmOrderController.clearSelected();
        v2PmOrderMvsController.clearSelected();
        if("Insite".equals(selected.getIssueType())){
            v2PmOrderController.setSelected( selected);
            v2PmOrderController.onSelectPmOrder();
        }else if("MVS".equals(selected.getIssueType())){
            v2PmOrderMvsController.setSelected(selected);
            v2PmOrderMvsController.onSelectPmOrder();
        }
    }


    @Override
    protected GenericRepositoryUUID<V2_PmOrder> getDAO() {
        return dao;
    }
}
