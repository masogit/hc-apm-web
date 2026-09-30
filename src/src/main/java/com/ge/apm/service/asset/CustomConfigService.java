/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.ge.apm.dao.CustomConfigRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SysRoleRepository;
import com.ge.apm.domain.CustomConfig;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.CustomConfigConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.view.sysutil.UserContextService;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Component
public class CustomConfigService {

    @Autowired
    CustomConfigRepository customConfigDao;
    @Autowired
    ConfigCacheService configCacheService;
    @Autowired
    ConfigService configService;
    @Autowired
    UaaService uaaService;
    @Autowired
    SysRoleRepository sysRoleDao;

//    private static CustomConfigObject defaultConfig;

    public List<SysRole> getConfigRoles() {
        return sysRoleDao.getByNameIn(CustomConfigConstant.getConfigRolesName());
    }

    public CustomConfig getCustomConfigBySite(String siteUID) {
        return customConfigDao.findBySiteUID(siteUID);
    }

    @Cacheable(cacheNames = "springCache", key = "'CustomConfigObject_SiteUid.'+#siteUid")
    public CustomConfigObject getCurrentCustomConfig(String siteUid) {
        OrgInfo org = configCacheService.findOrgByUid(siteUid);
//        if (configService.isInsulateTenant(org.getTenantUID())) {
//            CustomConfig res = getCustomConfigBySite(siteUid);
//            return res == null ? getDefalutCustomConfigObject() : JSONObject.parseObject(res.getConfig(), CustomConfigObject.class);
//        }
        List<CustomConfig> configList = customConfigDao.findBySiteUIDIn(Arrays.asList(org.getSiteUID(), org.getHospitalUID(), org.getInstitutionUID(), org.getTenantUID()));
        if (null == configList || configList.isEmpty()) {
            return getDefalutCustomConfigObject();
        }
        CustomConfig config = configList.stream().filter(item -> org.getSiteUID().equals(item.getSiteUID())).findAny()
                .orElse(configList.stream().filter(item -> org.getHospitalUID().equals(item.getSiteUID())).findAny()
                        .orElse(configList.stream().filter(item -> org.getInstitutionUID().equals(item.getSiteUID())).findAny()
                                .orElse(configList.stream().filter(item -> org.getTenantUID().equals(item.getSiteUID())).findAny().orElse(null))));
        CustomConfigObject result = config == null ? getDefalutCustomConfigObject() : JSONObject.parseObject(config.getConfig(), CustomConfigObject.class);
        // 强制清空只读字段的 editRoles，防止 DB 中残留旧数据
        if (result.getAssetFields() != null) {
            for (String readOnlyField : CustomConfigConstant.getReadOnlyFields()) {
                if (result.getAssetFields().containsKey(readOnlyField)) {
                    result.getAssetFields().get(readOnlyField).setEditRoles(Collections.emptyList());
                }
            }
        }
        return result;

    }

    @CacheEvict(cacheNames = "springCache", key = "'CustomConfigObject_SiteUid.'+#siteUid")
    public void  cleanCurrentCustomConfigCache (String siteUid){
    }

    private CustomConfigObject getDefalutCustomConfigObject() {
        CustomConfigObject   defaultConfig = new CustomConfigObject();
        return defaultConfig;
    }

    @CacheEvict(cacheNames = "springCache", key = "'CustomConfigObject_SiteUid.'+#selectSite.siteUID")
    public void saveConfig(OrgInfo selectSite, CustomConfigObject customConfigObject) {
        CustomConfig config = getCustomConfigBySite(selectSite.getSiteUID());
        if (config == null) {
            config = new CustomConfig();
            config.setConfig(JSON.toJSONString(customConfigObject));
            config.setTenantUID(selectSite.getTenantUID());
            config.setInstitutionUID(selectSite.getInstitutionUID());
            config.setHospitalUID(selectSite.getHospitalUID());
            config.setSiteUID(selectSite.getSiteUID());
            config.setCreatedDate(new Date());
            config.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
        } else {
            config.setConfig(JSON.toJSONString(customConfigObject));
            config.setLastModifiedBy(UserContextService.getCurrentUserAccount().getUid());
            config.setLastModifiedDate(new Date());
        }
        customConfigDao.save(config);
    }

    @CacheEvict(cacheNames = "springCache", key = "'CustomConfigObject_SiteUid.'+#selectSite.siteUID")
    public void removeConfig(OrgInfo selectSite) {
        CustomConfig config = getCustomConfigBySite(selectSite.getSiteUID());
        if (null != config) {
            customConfigDao.delete(config);
        }
    }

    public boolean hasAssetFieldPermissionByRole(CustomConfigObject config, String key, List<Object> userRoles) {

        return true;
    }

    public List<String> getReadAssetFieldsByRoles(CustomConfigObject config, List<String> userRoles) {
        List<String> configurationRule = getConfigurantionRules(userRoles);
        return config.getAssetFields().entrySet().stream()
                .filter(item -> item.getValue().getReadRoles().stream().anyMatch(itemRole -> configurationRule.contains(itemRole)))
                .map(Entry::getKey).collect(Collectors.toList());
    }

    public List<String> getEditAssetFieldsByRoles(CustomConfigObject config, List<String> userRoles) {
        List<String> configurationRule = getConfigurantionRules(userRoles);
        return config.getAssetFields().entrySet().stream()
                .filter(item -> item.getValue().getEditRoles().stream().anyMatch(itemRole -> configurationRule.contains(itemRole)))
                .map(Entry::getKey).collect(Collectors.toList());
    }

    public List<String> getExportAssetFieldsByRoles(CustomConfigObject config, List<String> userRoles) {
        List<String> configurationRule = getConfigurantionRules(userRoles);
        return config.getAssetFields().entrySet().stream()
                .filter(item -> item.getValue().getExportRoles().stream().anyMatch(itemRole -> configurationRule.contains(itemRole)))
                .map(Entry::getKey).collect(Collectors.toList());
    }

    private List<String> getConfigurantionRules(List<String> userRoles){
        List<String> resRule = new ArrayList();
        resRule.addAll(userRoles);
        if (!userRoles.contains("AssetStaff") && !userRoles.contains("ClinicalStaff") &&
                (userRoles.contains("NursingStaff") || userRoles.contains("ExternalStaff") || userRoles.contains("SharedAssetMgmt") || userRoles.contains("ITAdmin") || userRoles.contains("LocalITAdmin"))) {
            resRule.add("ClinicalStaff");
        }
        if (!userRoles.contains("AssetStaff") && !userRoles.contains("ClinicalStaff") &&
                (userRoles.contains(ApmConstant.MVS_ROLE_NAME_ADMIN) || userRoles.contains(ApmConstant.MVS_ROLE_NAME_FE))) {
            resRule.add("AssetStaff");
        }
        return resRule;
    }

    public List<String> getLocalizedExportableAssetFieldsByRoles(CustomConfigObject config, List<String> userRoles) {
        List<String> exportFields = getExportAssetFieldsByRoles(config, userRoles);
        exportFields.remove("vendorTel");
        exportFields.remove("manufactureTel");
        exportFields.remove("maitananceTel");

        removeDisabledExportFields(exportFields);
        if(exportFields.remove("medicalApparatusClassification")){
            exportFields.add("medicalApparatusClassificationCode");
            exportFields.add("medicalApparatusClassificationName");
        }
        orderByExportFields(exportFields);

        List<String> localizedExportFields = exportFields.stream()
                .map(item -> CustomConfigConstant.getExportOrImportFields().get(item)).collect(Collectors.toList());

        if (localizedExportFields.contains(WebUtil.getMessage("assetTypeGroup"))
                && !localizedExportFields.contains(WebUtil.getMessage("assetTypeGroupEncoded"))) {
            localizedExportFields.add(WebUtil.getMessage("assetTypeGroupEncoded"));
        }

        //uuid is required to export, but not in custom config fields, so include it here.
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        boolean zkbAccountBoolean = userContextService.isZkbAccount();
        if (zkbAccountBoolean){
            localizedExportFields.add(WebUtil.getMessage("mainDirectory"));
            localizedExportFields.add(WebUtil.getMessage("secondDirectory"));
        }
        if(Objects.equals(UserContextService.getCurrentUserAccount().getSiteId(), ApmConstant.IB_TENANT_ID)){
            localizedExportFields.add(WebUtil.getMessage("geDeviceType"));
        }
        localizedExportFields.add("UUID");

        return localizedExportFields;
    }

    private void removeDisabledExportFields(List<String> exportFields) {
        exportFields.removeAll(CustomConfigConstant.getAssetsDisabledExportFields());
    }

    private void orderByExportFields(List<String> exportFields) {
        List<String> exportFieldsOrder = CustomConfigConstant.getExportFieldsOrder();
        Collections.sort(exportFields, (o1, o2) -> {
            int index1 = exportFieldsOrder.indexOf(o1);
            int index2 = exportFieldsOrder.indexOf(o2);

            if (index1 == -1 && index2 == -1) {
                return 0;
            } else if (index1 == -1) {
                return 1;
            } else if (index2 == -1) {
                return -1;
            } else {
                return Integer.compare(index1, index2);
            }
        });

    }

    public void removeAssetField(CustomConfigObject customConfigObject, String key) {

        if (CustomConfigConstant.getSystemFields().contains(key)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("SystemRequiredFieldsCannotBeDeleted"));
            return;
        }
        customConfigObject.getAssetFields().remove(key);
        if (customConfigObject.getAssetsListPageFields().contains(key)) {
            customConfigObject.setAssetsListPageFields(customConfigObject.getAssetsListPageFields().stream().filter(item-> !Objects.equals(item, key)).collect(Collectors.toList()));
        }
        customConfigObject.getAssetsDetailpageBasicFields().remove(key);
        customConfigObject.getAssetsDetailpageDetailFields().remove(key);
        customConfigObject.getAssetsEditpageBasicFields().remove(key);
        customConfigObject.getAssetsEditpageCatalogFields().remove(key);
        customConfigObject.getAssetsEditpageCodesFields().remove(key);
        customConfigObject.getAssetsEditpageFinicialFields().remove(key);
        customConfigObject.getAssetsEditpageStatusFields().remove(key);
        customConfigObject.getAssetsEditpageDateFields().remove(key);
        customConfigObject.getAssetsMobilepageBasicFields().remove(key);
        customConfigObject.getAssetsMobilepageDetailFields().remove(key);
        customConfigObject.getAssetsMobilepageHeaderFields().remove(key);
        if (JSONPath.containsValue(customConfigObject.getMobilePage(), "$.pageWorkOrder.group[type='assetInfo'].assetInfoFields",key)) {
            JSONPath.set(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='assetInfo'].assetInfoFields",
                    ((JSONArray)JSONPath.eval(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='assetInfo'].assetInfoFields")).fluentRemove(key));
        }
    }

}
