/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.ge.apm.dao.AssetTypeGroupRepository;
import com.ge.apm.dao.ConfigItemValueRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigAssetField;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.cache.CacheService;
import com.ge.apm.service.common.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.ReorderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.DbMessageSource;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class CustomConfigController {
    private static final Logger logger = LoggerFactory.getLogger(CustomConfigController.class);

    private CustomConfigService ccService;
    private OrgInfoRepository orgDao;
    private UserCommonService userService;
    private ConfigService configService;
    private ConfigCacheService configCacheService;
    private UserContextService userContextService;
    private I18nMessageService i18nMessageService;
    private ConfigItemValueRepository configItemValueRepository;
    private AssetTypeGroupRepository assetTypeGroupRepository;
    private OrgInfo selectSite;
    private CustomConfigObject customConfigObject;
    private Map<String, Boolean> selectedAddFields;
    private CustomConfigAssetField currentEditField;
    private CustomConfig customConfig;
    private LinkedHashMap<String, String> allFields;
    private Map<String, String> configRoles;
    private List<String> addFieldsList;
    private String addFieldsType;
    private Map<String, String> configInfo = new HashMap();
    private PinYinComparator pinYinComparator;
    private Map<String,Boolean> importantFieldsCheckedStatus = new HashMap();
    private List<AssetTypeGroup> assetTypeGroups = new ArrayList<>();


    private OrgInfoRepository orgInfoDao;

    private CacheService cacheService;

    @Setter
    @Getter
    private String newName;

    @Setter
    @Getter
    private List<String> assetsImportantFieldsAll;

    @PostConstruct
    protected void init() {
        ccService = WebUtil.getBean(CustomConfigService.class);
        userService = WebUtil.getBean(UserCommonService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        configService = WebUtil.getBean(ConfigService.class);
        i18nMessageService = WebUtil.getBean(I18nMessageService.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        cacheService = WebUtil.getBean(CacheService.class);

        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        selectSite = orgDao.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        allFields = CustomConfigConstant.getAllFields();
        configRoles = ccService.getConfigRoles().stream().collect(Collectors.toMap(SysRole::getName, SysRole::getRoleDesc));
        if (userContextService.isOpenNewMenuRoles()) {
            List<CustomRole> customRoles = WebUtil.getBean(UaaService.class).getTenantCustomRoles(selectSite.getTenantUID());
            customRoles = customRoles.stream().filter(r -> StringUtils.isNotEmpty(r.getFunctionList()) && (r.getFunctionList().contains(ApmFunction.ASSET_INFO_READ.name()) || r.getFunctionList().contains("ASSET_MOBILE_SEARCH"))).collect(Collectors.toList());
            configRoles.putAll(customRoles.stream().collect(Collectors.toMap(CustomRole::getName, CustomRole::getName)));
        }
        selectedAddFields = new HashMap();
        pinYinComparator = new PinYinComparator();
        loadConfigForSite(selectSite.getUid());

        assetsImportantFieldsAll = CustomConfigConstant.getAssetsImportantFieldsAll();
        assetTypeGroups = assetTypeGroupRepository.listAssetTypeGroupInHierarchy(UserContextService.getCurrentUserAccount().getTenantUID());
        initImportantFieldsChecked();
    }

    private void loadConfigForSite(String siteUid) {
        customConfig = ccService.getCustomConfigBySite(siteUid);
        if (null == customConfig) {
            customConfigObject = ccService.getCurrentCustomConfig(siteUid);
        } else {
            customConfigObject = JSONObject.parseObject(customConfig.getConfig(), CustomConfigObject.class);
        }
        // 强制清空只读字段的 editRoles，防止 DB 中残留旧数据
        if (customConfigObject.getAssetFields() != null) {
            for (String readOnlyField : CustomConfigConstant.getReadOnlyFields()) {
                if (customConfigObject.getAssetFields().containsKey(readOnlyField)) {
                    customConfigObject.getAssetFields().get(readOnlyField).setEditRoles(Collections.emptyList());
                }
            }
        }
        JSONObject pageWorkOrder = customConfigObject.getMobileMaintainPage().getJSONObject("pageWorkOrder");
        JSONArray group = pageWorkOrder.getJSONArray("group");
        for (int i = 0; i < group.size(); i++) {
            JSONObject obj = group.getJSONObject(i);
            if ("assetInfo".equals(obj.get("type"))) {
                JSONObject externalQuotation = obj.getJSONObject("externalQuotation");
                if (externalQuotation == null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("enable", false);
                    obj.put("externalQuotation", jsonObject);
                }
            }
        }
        /**
         * 增加显示报修发送地字段
         */
        JSONObject pageWorkOrderMobilePage = customConfigObject.getMobilePage().getJSONObject("pageWorkOrder");
        JSONArray group2 = pageWorkOrderMobilePage.getJSONArray("group");
        for (int i = 0; i < group2.size(); i++) {
            JSONObject obj = group2.getJSONObject(i);
            if("orderFields".equals(obj.get("type"))){
                JSONObject repairReportSendPlace = obj.getJSONObject("repairReportSendPlace");
                if (repairReportSendPlace == null) {
                    JSONObject jsonObject = new JSONObject();
                    String configItemValue = userContextService.getConfigItemValue("enableAdverseEventSentinelMode");
                    jsonObject.put("enable", StringUtils.isEmpty(configItemValue) ? false : Boolean.valueOf(configItemValue));
                    jsonObject.put("defaultValue", "");
                    jsonObject.put("required", true);
                    obj.put("repairReportSendPlace", jsonObject);
                }
            }
        }
        updateConfigInfo();
    }

    public void prepareAddAssetField(String type) {
        addFieldsType = type;
        if (null == addFieldsList) {
            addFieldsList = new ArrayList();
        } else {
            addFieldsList.clear();
        }
        switch (type) {
            case "BasicFields":
                addFieldsList = customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !customConfigObject.getAssetsDetailpageBasicFields().contains(item)).collect(Collectors.toList());
                break;
            case "SupplierViewFields":
                addFieldsList = customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !customConfigObject.getSupplierViewFields().contains(item)).collect(Collectors.toList());
                break;
            case "SupplierEditFields":
                addFieldsList = customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !customConfigObject.getSupplierEditFields().contains(item)).collect(Collectors.toList());
                break;
            case "DetailFields":
                addFieldsList = customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !customConfigObject.getAssetsDetailpageDetailFields().contains(item)).collect(Collectors.toList());
                break;
            case "MobileFilterFields":
                addFieldsList = customConfigObject.defaultAssetsMobilepageFilterFields().stream().filter(item -> !customConfigObject.getAssetsMobilepageFilterFields().contains(item)).collect(Collectors.toList());
                addFieldsList = addFieldsList.stream().filter(item-> !"assetTypeGroup".equals(item) || userContextService.accessContainsFunctionMenu("CONFIG_ASSET_GROUP")).collect(Collectors.toList());
                break;
            case "MobileHeaderFields":
                addFieldsList = customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !customConfigObject.getAssetsMobilepageHeaderFields().contains(item)).collect(Collectors.toList());
                break;
            case "MobileBasicFields":
            case "MobileDetailFields":
                addFieldsList = customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !customConfigObject.getAssetsMobilepageDetailFields().contains(item)
                        && !customConfigObject.getAssetsMobilepageBasicFields().contains(item)).collect(Collectors.toList());
                break;
            case "MobileBuildAssetFields":
                addFieldsList = customConfigObject.getAllAssetsMobilepageBuildAssetFields();
                break;
            case "ListPageFields":
                addFieldsList = customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !customConfigObject.getAssetsListPageFields().contains(item)).collect(Collectors.toList());
                break;
            case "UsingFields":
                addFieldsList = allFields.keySet().stream().filter(item -> !customConfigObject.getAssetFields().containsKey(item)).collect(Collectors.toList());
                break;
            case "RemoveSuppplierViewFields":
                addFieldsList.addAll(customConfigObject.getSupplierViewFields());
                break;
            case "RemoveSupplierEditFields":
                addFieldsList.addAll(customConfigObject.getSupplierEditFields());
                break;
            case "RemoveBasicFields":
                addFieldsList.addAll(customConfigObject.getAssetsDetailpageBasicFields());
                break;
            case "RemoveDetailFields":
                addFieldsList.addAll(customConfigObject.getAssetsDetailpageDetailFields());
                break;
            case "EditpageBasicFields":
            case "EditpageCodesFields":
            case "EditpageStatusFields":
            case "EditpageCatalogFields":
            case "EditpageDateFields":
            case "EditpageFinicialFields":
                addFieldsList = customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !customConfigObject.getAssetsEditpageBasicFields().contains(item)
                        && !customConfigObject.getAssetsEditpageCodesFields().contains(item)
                        && !customConfigObject.getAssetsEditpageStatusFields().contains(item)
                        && !customConfigObject.getAssetsEditpageCatalogFields().contains(item)
                        && !customConfigObject.getAssetsEditpageDateFields().contains(item)
                        && !customConfigObject.getAssetsEditpageFinicialFields().contains(item)).collect(Collectors.toList());
                break;
            case "RemoveEditpageBasicFields":
                addFieldsList.addAll(customConfigObject.getAssetsEditpageBasicFields());
                break;
            case "RemoveEditpageCodesFields":
                addFieldsList.addAll(customConfigObject.getAssetsEditpageCodesFields());
                break;
            case "RemoveEditpageStatusFields":
                addFieldsList.addAll(customConfigObject.getAssetsEditpageStatusFields());
                break;
            case "RemoveEditpageCatalogFields":
                addFieldsList.addAll(customConfigObject.getAssetsEditpageCatalogFields());
                break;
            case "RemoveEditpageDateFields":
                addFieldsList.addAll(customConfigObject.getAssetsEditpageDateFields());
                break;
            case "RemoveEditpageFinicialFields":
                addFieldsList.addAll(customConfigObject.getAssetsEditpageFinicialFields());
                break;
            case "RemoveMobileBasicFields":
                addFieldsList.addAll(customConfigObject.getAssetsMobilepageBasicFields());
                break;
            case "RemoveMobileDetailFields":
                addFieldsList.addAll(customConfigObject.getAssetsMobilepageDetailFields());
                break;
            case "RemoveMobileHeaderFields":
                addFieldsList.addAll(customConfigObject.getAssetsMobilepageHeaderFields());
                break;
            case "RemoveMobileFilterFields":
                addFieldsList.addAll(customConfigObject.getAssetsMobilepageFilterFields());
                break;
            case "RemoveMobileBuildAssetFields":
                addFieldsList.addAll(customConfigObject.getAssetsMobilepageBuildAssetFields());
                break;
            case "MobileWorkOrderFieldsList":
                addFieldsList.addAll(customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !JSONPath.containsValue(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='assetInfo'].assetInfoFields", item)).collect(Collectors.toList()));
                break;
            case "WebWorkOrderFieldsList":
                addFieldsList.addAll(customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !JSONPath.containsValue(customConfigObject, "$.mobileMaintainPage.pageWorkOrder.group[type='assetInfo'].assetInfoFields", item)).collect(Collectors.toList()));
                break;
            case "WebMaintainFieldsList":
                addFieldsList.addAll(customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !JSONPath.containsValue(customConfigObject, "$.mobileMaintainPage.pageWorkOrder.group[type='assetInfo'].assetInfoFields", item)).collect(Collectors.toList()));
                break;
            case "RemoveMobileWorkOrderFieldsList":
                addFieldsList.addAll(((JSONArray) JSONPath.eval(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='assetInfo'].assetInfoFields")).toJavaList(String.class));
                break;
            case "RemoveWebWorkOrderFieldsList":
                addFieldsList.addAll(((JSONArray) JSONPath.eval(customConfigObject, "$.mobileMaintainPage.pageWorkOrder.group[type='assetInfo'].assetInfoFields")).toJavaList(String.class));
                break;
            case "assetAllocateFieldsList":
                addFieldsList=getAllAllocateFields().keySet().stream().filter(item->!customConfigObject.getAssetAllocateFields().contains(item)).collect(Collectors.toList());
                break;
            default:
                break;

        }

        addFieldsList.sort((String o1, String o2) -> {
            return pinYinComparator.compare(allFields.get(o1), allFields.get(o2));
        });

    }

    public String getAddFieldsUpdateString() {
        switch (addFieldsType) {
            case "BasicFields":
            case "RemoveBasicFields":
                return ":customConfigForm:tabView:BasicFieldsList";
            case "SupplierViewFields":
            case "RemoveSuppplierViewFields":
                return ":customConfigForm:tabView:SupplierViewFieldsList";
            case "SupplierEditFields":
            case "RemoveSupplierEditFields":
                return ":customConfigForm:tabView:SupplierEditFieldsList";
            case "MobileHeaderFields":
            case "RemoveMobileHeaderFields":
                return ":customConfigForm:tabView:MobileHeaderFieldsList";
            case "MobileBasicFields":
            case "RemoveMobileBasicFields":
                return ":customConfigForm:tabView:MobileBasicFieldsList";
            case "MobileDetailFields":
            case "RemoveMobileDetailFields":
                return ":customConfigForm:tabView:MobileDetailFieldsList";
            case "MobileFilterFields":
            case "RemoveMobileFilterFields":
                return ":customConfigForm:tabView:MobileFilterFieldsList";
            case "MobileBuildAssetFields":
            case "RemoveMobileBuildAssetFields":
                return ":customConfigForm:tabView:MobileBuildAssetFieldsList";
            case "DetailFields":
            case "RemoveDetailFields":
                return ":customConfigForm:tabView:DetailFieldsList";
            case "ListPageFields":
                return ":customConfigForm:tabView:assetListPageTable";
            case "UsingFields":
                return ":customConfigForm:tabView:assetFieldsTable";
            case "EditpageBasicFields":
            case "RemoveEditpageBasicFields":
                return "customConfigForm:tabView:EditpageBasicFields";
            case "EditpageCodesFields":
            case "RemoveEditpageCodesFields":
                return "customConfigForm:tabView:EditpageCodesFields";
            case "EditpageStatusFields":
            case "RemoveEditpageStatusFields":
                return "customConfigForm:tabView:EditpageStatusFields";
            case "EditpageCatalogFields":
            case "RemoveEditpageCatalogFields":
                return "customConfigForm:tabView:EditpageCatalogFields";
            case "EditpageDateFields":
            case "RemoveEditpageDateFields":
                return "customConfigForm:tabView:EditpageDateFields";
            case "EditpageFinicialFields":

            case "RemoveEditpageFinicialFields":
                return "customConfigForm:tabView:EditpageFinicialFields";
            case "MobileWorkOrderFieldsList":
            case "RemoveMobileWorkOrderFieldsList":
                return "customConfigForm:tabView:MobileWorkOrderFieldsList";
            case "WebWorkOrderFieldsList":

            case "RemoveWebWorkOrderFieldsList":
                return "customConfigForm:tabView:WebWorkOrderFieldsList";
            case "assetAllocateFieldsList":
                return "customConfigForm:tabView:assetAllocatePageTable";
            default:
                return "";
        }
    }

    public void addAssetFields() {
        switch (addFieldsType) {
            case "BasicFields":
                customConfigObject.getAssetsDetailpageBasicFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsDetailpageBasicFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "SupplierViewFields":
                customConfigObject.getSupplierViewFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getSupplierViewFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "SupplierEditFields":
                customConfigObject.getSupplierEditFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getSupplierEditFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "DetailFields":
                customConfigObject.getAssetsDetailpageDetailFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsDetailpageDetailFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "MobileHeaderFields":
                customConfigObject.getAssetsMobilepageHeaderFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsMobilepageHeaderFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "MobileBasicFields":
                customConfigObject.getAssetsMobilepageBasicFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsMobilepageBasicFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "MobileDetailFields":
                customConfigObject.getAssetsMobilepageDetailFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsMobilepageDetailFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "MobileFilterFields":
                customConfigObject.getAssetsMobilepageFilterFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsMobilepageFilterFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "MobileBuildAssetFields":
                customConfigObject.getAssetsMobilepageBuildAssetFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsMobilepageBuildAssetFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "ListPageFields":
                List<String> addFields = new ArrayList();
                addFields.addAll(customConfigObject.getAssetsListPageFields());
                addFields.addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsListPageFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                customConfigObject.setAssetsListPageFields(addFields);
                break;
            case "UsingFields":
                customConfigObject.getAssetFields().putAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetFields().containsKey(item.getKey()))
                        .collect(Collectors.toMap(itemField -> itemField.getKey(), itemField -> new CustomConfigAssetField(itemField.getKey(),CustomConfigConstant.getConfigRolesName(), CustomConfigConstant.getConfigEditRolesName()))));
                break;
            case "RemoveBasicFields":
                customConfigObject.getAssetsDetailpageBasicFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsDetailpageBasicFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveDetailFields":
                customConfigObject.getAssetsDetailpageDetailFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsDetailpageDetailFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveMobileHeaderFields":
                customConfigObject.getAssetsMobilepageHeaderFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsMobilepageHeaderFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveMobileBuildAssetFields":
                customConfigObject.getAssetsMobilepageBuildAssetFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsMobilepageBuildAssetFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveMobileBasicFields":
                customConfigObject.getAssetsMobilepageBasicFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsMobilepageBasicFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveMobileDetailFields":
                customConfigObject.getAssetsMobilepageDetailFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsMobilepageDetailFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveMobileFilterFields":
                customConfigObject.getAssetsMobilepageFilterFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsMobilepageFilterFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "EditpageBasicFields":
                customConfigObject.getAssetsEditpageBasicFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsEditpageBasicFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveEditpageBasicFields":
                customConfigObject.getAssetsEditpageBasicFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsEditpageBasicFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "EditpageCodesFields":
                customConfigObject.getAssetsEditpageCodesFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsEditpageCodesFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveEditpageCodesFields":
                customConfigObject.getAssetsEditpageCodesFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsEditpageCodesFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "EditpageStatusFields":
                customConfigObject.getAssetsEditpageStatusFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsEditpageStatusFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveEditpageStatusFields":
                customConfigObject.getAssetsEditpageStatusFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsEditpageStatusFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "EditpageCatalogFields":
                customConfigObject.getAssetsEditpageCatalogFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsEditpageCatalogFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveEditpageCatalogFields":
                customConfigObject.getAssetsEditpageCatalogFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsEditpageCatalogFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "EditpageDateFields":
                customConfigObject.getAssetsEditpageDateFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsEditpageDateFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveEditpageDateFields":
                customConfigObject.getAssetsEditpageDateFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsEditpageDateFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "EditpageFinicialFields":
                customConfigObject.getAssetsEditpageFinicialFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsEditpageFinicialFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "WebWorkOrderFieldsList":
                JSONPath.set(customConfigObject, "$.mobileMaintainPage.pageWorkOrder.group[type='assetInfo'].assetInfoFields", ((JSONArray) JSONPath.eval(customConfigObject, "$.mobileMaintainPage.pageWorkOrder.group[type='assetInfo'].assetInfoFields"))
                        .fluentAddAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()).map(Entry::getKey).collect(Collectors.toList())));
                break;
            case "RemoveEditpageFinicialFields":
                customConfigObject.getAssetsEditpageFinicialFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsEditpageFinicialFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
            case "MobileWorkOrderFieldsList":
                JSONPath.set(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='assetInfo'].assetInfoFields",
                        ((JSONArray) JSONPath.eval(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='assetInfo'].assetInfoFields"))
                                .fluentAddAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()).map(Entry::getKey).collect(Collectors.toList())));
                break;
            case "MobileWorkOrderFieldsListWeb":
                JSONPath.set(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='assetInfo'].assetInfoFields",
                        ((JSONArray) JSONPath.eval(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='assetInfo'].assetInfoFields"))
                                .fluentAddAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()).map(Entry::getKey).collect(Collectors.toList())));
                break;
            case "RemoveMobileWorkOrderFieldsList":
                JSONPath.set(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='assetInfo'].assetInfoFields",
                        ((JSONArray) JSONPath.eval(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='assetInfo'].assetInfoFields"))
                                .fluentRemoveAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()).map(Entry::getKey).collect(Collectors.toList())));
                break;
            case "RemoveWebWorkOrderFieldsList":
                JSONPath.set(customConfigObject, "$.mobileMaintainPage.pageWorkOrder.group[type='assetInfo'].assetInfoFields",
                        ((JSONArray) JSONPath.eval(customConfigObject, "$.mobileMaintainPage.pageWorkOrder.group[type='assetInfo'].assetInfoFields"))
                                .fluentRemoveAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()).map(Entry::getKey).collect(Collectors.toList())));
                break;
            case "ImportantFields":
                List<String> addImportantFields = new ArrayList();
                addImportantFields.addAll(customConfigObject.getAssetsImportantFields());
                addImportantFields.addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsImportantFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                customConfigObject.setAssetsImportantFields(addImportantFields);
                break;
            case "assetAllocateFieldsList":
                customConfigObject.getAssetAllocateFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetAllocateFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            default:
                break;

        }
        cancelAddFields();
    }

    public void cancelAddFields() {
        addFieldsType = "";
        addFieldsList.clear();
        selectedAddFields.clear();
    }

    //使用的字段
    public List<CustomConfigAssetField> getAssetFieldsList() {
        List<CustomConfigAssetField> res = customConfigObject.getAssetFields().entrySet().stream().map(Entry::getValue).collect(Collectors.toList());

        res.sort((CustomConfigAssetField o1, CustomConfigAssetField o2) -> {
            if (allFields.get(o1.getKey()) == null || allFields.get(o2.getKey()) == null) {
                logger.error("One of label of key not found!, key1: " + o1.getKey() + ", key2: " + o2.getKey());
            }
            return pinYinComparator.compare(allFields.get(o1.getKey()), allFields.get(o2.getKey()));
        });

        return res;
    }

    //角色名称
    public String getAssetFieldsRoles(List<String> roles) {
        StringBuilder res = new StringBuilder();
        roles.stream().forEach(item -> res.append(configRoles.get(item) == null ? "" : configRoles.get(item)).append(" "));
        return res.toString();
    }

    public void removeAssetField(String key) {
        ccService.removeAssetField(customConfigObject, key);
    }

    public String getCustomConfigInfomation() {
        if (customConfig == null) {
            return "当前院区无配置,保存后创建";
        }
        StringBuilder res = new StringBuilder();
        if (customConfig.getCreatedBy() != null) {
            res.append("创建人:").append(getUsernameFromUid(customConfig.getCreatedBy()));
            res.append(" 创建时间:").append(DateUtil.formatDate(customConfig.getCreatedDate(), "yyyy-MM-dd"));
        }
        if (customConfig.getLastModifiedBy() != null) {
            res.append("; 修改人:").append(getUsernameFromUid(customConfig.getLastModifiedBy()));
            res.append(" 修改时间:").append(DateUtil.formatDate(customConfig.getLastModifiedDate(), "yyyy-MM-dd"));
        }
        return res.toString();
    }

    private void updateConfigInfo() {
        if (null != customConfig) {
            configInfo.put("CreatedBy", getUsernameFromUid(customConfig.getCreatedBy()));
            configInfo.put("CreatedDate", customConfig.getCreatedDate() == null ? "" : DateUtil.formatDate(customConfig.getCreatedDate(), "yyyy-MM-dd"));
            configInfo.put("LastModifiedBy", getUsernameFromUid(customConfig.getLastModifiedBy()));
            configInfo.put("LastModifiedDate", customConfig.getLastModifiedDate() == null ? "" : DateUtil.formatDate(customConfig.getLastModifiedDate(), "yyyy-MM-dd"));
        }
    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        if (null != event) {
            if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
                selectSite = (OrgInfo) event.getTreeNode().getData();
            } else {
                selectSite = orgDao.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());
            }
            loadConfigForSite(selectSite.getUid());
        }
    }

    public void onListPageFieldsReorder(ReorderEvent event) {
    }

    public void saveChange() {
        ccService.saveConfig(selectSite, customConfigObject);
        customConfig = ccService.getCustomConfigBySite(selectSite.getSiteUID());
        WebUtil.addSuccessMessage("已保存");
        updateConfigInfo();

        List<String> siteOrgUidsByParentUid = orgInfoDao.getSiteOrgUidsByParentUid(selectSite.getSiteUID());
        cacheService.evictSiteConfigByUids(siteOrgUidsByParentUid);

    }

    public void deleteConfig() {
        ccService.removeConfig(selectSite);
        loadConfigForSite(selectSite.getUid());
        WebUtil.addSuccessMessage(WebUtil.getMessage("Deleted"));

        List<String> siteOrgUidsByParentUid = orgInfoDao.getSiteOrgUidsByParentUid(selectSite.getSiteUID());
        cacheService.evictSiteConfigByUids(siteOrgUidsByParentUid);
    }

    public boolean isSystemFields(String key) {
        return CustomConfigConstant.getSystemFields().contains(key);
    }


    public String getSystemShowName(){
         return   i18nMessageService.getSystemFieldName(CustomConfigFields.valueOf(currentEditField.getKey()));
    }

    public void resetField(){
        i18nMessageService.resetCustomFieldName(CustomConfigFields.valueOf(currentEditField.getKey()) , selectSite.getSiteId());
        allFields.put(currentEditField.getKey(), i18nMessageService.getSystemFieldName(CustomConfigFields.valueOf(currentEditField.getKey())));
        resetCache();
    }

    public void renameField(){
        allFields.put(currentEditField.getKey(), newName);
        i18nMessageService.changeCustomFieldName(CustomConfigFields.valueOf(currentEditField.getKey()) , selectSite.getSiteId(),newName);
        resetCache();
        newName = "";
    }
    private void resetCache(){
        DbMessageSource.reLoadMessages();
        configCacheService.cleanCacheNames();
    }
    public boolean isRenameFields(String key) {
        try {
            return CustomConfigConstant.getRenameFields().contains(CustomConfigFields.valueOf(key));
        }catch (IllegalArgumentException e){
            logger.error("CustomConfigFields is not key:{}",key);
            return  false;
        }catch (Exception e){
            logger.error("CustomConfigController isRenameFields key:{}, error :{}",key,e);
            return false;
        }
    }

    public boolean isReadOnlyFields(String key) {
        return CustomConfigConstant.getReadOnlyFields().contains(key);
    }

    public void removeListPageField(String key) {
        if (customConfigObject.getAssetsListPageFields().contains(key)) {
            customConfigObject.setAssetsListPageFields(customConfigObject.getAssetsListPageFields().stream().filter(item -> !Objects.equals(item, key)).collect(Collectors.toList()));
        }
    }
    public void removeAllocateListPageField(String key) {
        if (customConfigObject.getAssetAllocateFields().contains(key)) {
            customConfigObject.setAssetAllocateFields(customConfigObject.getAssetAllocateFields().stream().filter(item -> !Objects.equals(item, key)).collect(Collectors.toList()));
        }
    }
    public void prepareEditAssetField(String fieldKey) {
        currentEditField = customConfigObject.getAssetFields().get(fieldKey);
        this.newName =allFields.get(fieldKey);
    }

    public List<String> getRolesSelectItems() {
        return new ArrayList(configRoles.keySet());
    }

    public void moveItem(JSONObject groupItem, String oper) {
        JSONArray groups = ((JSONArray) JSONPath.eval(customConfigObject, "$.mobilePage.pageWorkOrder.group"));
        int index = groups.indexOf(groupItem);
        if ("up".equals(oper) && index >= 1) {
            groups.set(index, groups.get(index - 1));
            groups.set(index - 1, groupItem);
        }
        if ("down".equals(oper) && index < groups.size() - 1) {
            groups.set(index, groups.get(index + 1));
            groups.set(index + 1, groupItem);
        }
    }

    public void onCheckConfig(String opt) {
        switch (opt) {
            case "attachments.enable":
                if (JSONPath.containsValue(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='descriptions'].attachments.enable", Boolean.FALSE)) {
                    JSONPath.set(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='descriptions'].attachments.required", Boolean.FALSE);
                }
                break;
            case "voiceAttachment.enable":
                if (JSONPath.containsValue(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='descriptions'].voiceAttachment.enable", Boolean.FALSE)) {
                    JSONPath.set(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='descriptions'].voiceAttachment.required", Boolean.FALSE);
                    JSONPath.set(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='descriptions'].voiceOrReasonRequired", Boolean.FALSE);
                }
                break;
            case "requestReason.enable":
                if (JSONPath.containsValue(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='descriptions'].requestReason.enable", Boolean.FALSE)) {
                    JSONPath.set(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='descriptions'].requestReason.required", Boolean.FALSE);
                    JSONPath.set(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='descriptions'].voiceOrReasonRequired", Boolean.FALSE);
                }
                break;
            case "voiceAttachment.required":
            case "requestReason.required":
                if (JSONPath.containsValue(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='descriptions'].voiceAttachment.required", Boolean.TRUE)
                        || JSONPath.containsValue(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='descriptions'].requestReason.required", Boolean.TRUE)) {
                    JSONPath.set(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='descriptions'].voiceOrReasonRequired", Boolean.FALSE);
                }
                break;
            case "voiceOrReasonRequired" :
                if (JSONPath.containsValue(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='descriptions'].voiceOrReasonRequired", Boolean.TRUE)) {
                    JSONPath.set(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='descriptions'].requestReason.required", Boolean.FALSE);
                    JSONPath.set(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='descriptions'].voiceAttachment.required", Boolean.FALSE);
                }
            default:
        }
    }

    private void initImportantFieldsChecked() {
        List<String> assetsImportantFields = customConfigObject.getAssetsImportantFields();

        for (int i = 0; i < assetsImportantFieldsAll.size(); i++) {
            String fieldName = assetsImportantFieldsAll.get(i);
            if(assetsImportantFields.contains(fieldName)){
                importantFieldsCheckedStatus.put(fieldName,true);
            }else{
                importantFieldsCheckedStatus.put(fieldName,false);
            }
        }
    }

    //for 10.1 update
//    public void updateCustomFieldForArrivalRequired() {
//        com.ge.apm.dao.CustomConfigRepository customConfigDao = WebUtil.getBean(com.ge.apm.dao.CustomConfigRepository.class);
//        List<CustomConfig> allConfigList = customConfigDao.find();
//
//        allConfigList.stream().forEach(item -> {
//            CustomConfigObject configObject = JSONObject.parseObject(item.getConfig(), CustomConfigObject.class);
//
//            CustomConfigConstant.getSystemFields().forEach(fielditem -> {
//                configObject.getAssetFields().get(fielditem).setRequired(true);
//                configObject.getAssetFields().get(fielditem).setArrivalRequired(true);
//            });
//            CustomConfigConstant.getInstallRequiredFields().forEach(fielditem -> {
//                configObject.getAssetFields().get(fielditem).setArrivalRequired(true);
//            });
//            item.setConfig(JSONObject.toJSONString(configObject));
//        });
//        customConfigDao.save(allConfigList);
//        WebUtil.addSuccessMessage("updated all config:" + allConfigList.size());
//    }
    public Map<String, Boolean> getSelectedAddFields() {
        return selectedAddFields;
    }

    public void setSelectedAddFields(Map<String, Boolean> selectedAddFields) {
        this.selectedAddFields = selectedAddFields;
    }

    public CustomConfigObject getCustomConfigObject() {
        return customConfigObject;
    }

    public void setCustomConfigObject(CustomConfigObject customConfigObject) {
        this.customConfigObject = customConfigObject;
    }

    public CustomConfigAssetField getCurrentEditField() {
        return currentEditField;
    }

    public void setCurrentEditField(CustomConfigAssetField currentEditField) {
        this.currentEditField = currentEditField;
    }

    public CustomConfig getCustomConfig() {
        return customConfig;
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public Map<String, String> getConfigInfo() {
        return configInfo;
    }

    public LinkedHashMap<String, String> getAllFields() {
        return allFields;
    }

    public Map<String, String> getConfigRoles() {
        return configRoles;
    }

    public List<String> getAddFieldsList() {
        return addFieldsList;
    }

    public String getAddFieldsType() {
        return addFieldsType;
    }

    public Map<String, Boolean> getImportantFieldsCheckedStatus() {
        return importantFieldsCheckedStatus;
    }

    public void setImportantFieldsCheckedStatus(Map<String, Boolean> importantFieldsCheckedStatus) {
        this.importantFieldsCheckedStatus = importantFieldsCheckedStatus;
    }

    public List<AssetTypeGroup> getAssetTypeGroups() {
        return assetTypeGroups;
    }

    public boolean enableAssetTypeGroup() {
        return userContextService.getLoginUser().isEnableAssetTypeGroup();
    }

    public void removeAssetsImportantFields(String key) {
        if (customConfigObject.getAssetsImportantFields().contains(key)) {
            customConfigObject.setAssetsImportantFields(customConfigObject.getAssetsImportantFields().stream().filter(item -> !Objects.equals(item, key)).collect(Collectors.toList()));
        }else {
            if (assetsImportantFieldsAll.contains(key)) {
                List<String> assetsImportantFields = customConfigObject.getAssetsImportantFields();
                assetsImportantFields.add(key);
                customConfigObject.setAssetsImportantFields(assetsImportantFields);
            }
        }
    }

    private String getUsernameFromUid(String uid) {
        UserAccount userAccount  = userService.getUserByUid(uid);
        return userAccount == null ? "" : userAccount.getName();
    }

    public Boolean showSupplierViewAndEditFieldsTable() {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        TenantOptionConfig tenantOptionConfig = configCacheService.getTenantOptionConfigByUid(UserContextService.getCurrentUserAccount().getTenantUID());
        WorkflowConfig workflowConfig = configService.findConfigBySite(userAccount.getSiteUID());
        if(workflowConfig!=null && tenantOptionConfig!=null && workflowConfig.getActiveSupplier()!=null && tenantOptionConfig.getActiveSupplier()!=null && workflowConfig.getActiveSupplier() && tenantOptionConfig.getActiveSupplier()) return true;
        return false;
    }

    public LinkedHashMap<String, String> getAllAllocateFields() {
        return AllocateCustomConfigConstant.ASSET_ALLOCATE.getAllAllocateFields();
    }
    public boolean enabledWhethertoenableunattended(){
        String key="whetherToEnableUnattended";
        String byKeyAndSiteForUseParent = configItemValueRepository.findByKeyAndSiteForUseParent(selectSite.getSiteUID(),
                selectSite.getHospitalUID(),
                selectSite.getInstitutionUID(),
                selectSite.getTenantUID(), key);
        return Boolean.TRUE.toString().equalsIgnoreCase(byKeyAndSiteForUseParent);
    }

}
