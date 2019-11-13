/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

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
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.CustomConfigConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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
    OrgInfoRepository orgDao;
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

    public CustomConfigObject getCurrentCustomConfig(String siteUid) {
        OrgInfo org = orgDao.findByUid(siteUid);
        if (configService.isInsulateTenant(org.getTenantUID())) {
            CustomConfig res = getCustomConfigBySite(siteUid);
            return res == null ? getDefalutCustomConfigObject() : JSONObject.parseObject(res.getConfig(), CustomConfigObject.class);
        }
        List<CustomConfig> configList = customConfigDao.findBySiteUIDIn(Arrays.asList(org.getSiteUID(), org.getHospitalUID(), org.getInstitutionUID(), org.getTenantUID()));
        if (null == configList || configList.isEmpty()) {
            return getDefalutCustomConfigObject();
        }
        CustomConfig config = configList.stream().filter(item -> org.getSiteUID().equals(item.getSiteUID())).findAny()
                .orElse(configList.stream().filter(item -> org.getHospitalUID().equals(item.getSiteUID())).findAny()
                        .orElse(configList.stream().filter(item -> org.getInstitutionUID().equals(item.getSiteUID())).findAny()
                                .orElse(configList.stream().filter(item -> org.getTenantUID().equals(item.getSiteUID())).findAny().orElse(null))));
        return config == null ? getDefalutCustomConfigObject() : JSONObject.parseObject(config.getConfig(), CustomConfigObject.class);

    }

    private CustomConfigObject getDefalutCustomConfigObject() {
        CustomConfigObject   defaultConfig = new CustomConfigObject();
        return defaultConfig;
    }

    public void saveConfig(OrgInfo selectSite, CustomConfigObject customConfigObject) {
        CustomConfig config = getCustomConfigBySite(selectSite.getSiteUID());
        if (config == null) {
            config = new CustomConfig();
            config.setConfig(JSONObject.toJSONString(customConfigObject));
            config.setTenantUID(selectSite.getTenantUID());
            config.setInstitutionUID(selectSite.getInstitutionUID());
            config.setHospitalUID(selectSite.getHospitalUID());
            config.setSiteUID(selectSite.getSiteUID());
            config.setCreatedDate(new Date());
            config.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
        } else {
            config.setConfig(JSONObject.toJSONString(customConfigObject));
            config.setLastModifiedBy(UserContextService.getCurrentUserAccount().getUid());
            config.setLastModifiedDate(new Date());
        }
        customConfigDao.save(config);
    }

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
        //uuid is required to export, but not in custom config fields, so include it here.
        exportFields.add("UUID");

        List<String> localizedExportFields = CustomConfigConstant.getExportFields().entrySet().stream()
                .filter(item -> exportFields.contains(item.getValue()))
                .map(Entry::getKey)
                .collect(Collectors.toList());

        return localizedExportFields;
    }

    public void removeAssetField(CustomConfigObject customConfigObject, String key) {

        if (CustomConfigConstant.getSystemFields().contains(key)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("SystemRequiredFieldsCannotBeDeleted"));
            return;
        }
        if (customConfigObject.getAssetFields().containsKey(key)) {
            customConfigObject.getAssetFields().remove(key);
        }
        if (customConfigObject.getAssetsListPageFields().contains(key)) {
            customConfigObject.setAssetsListPageFields(customConfigObject.getAssetsListPageFields().stream().filter(item-> !Objects.equals(item, key)).collect(Collectors.toList()));
//            customConfigObject.getAssetsListPageFields().remove(key);
        }
        if (customConfigObject.getAssetsDetailpageBasicFields().contains(key)) {
            customConfigObject.getAssetsDetailpageBasicFields().remove(key);
        }
        if (customConfigObject.getAssetsDetailpageDetailFields().contains(key)) {
            customConfigObject.getAssetsDetailpageDetailFields().remove(key);
        }
        if (customConfigObject.getAssetsEditpageBasicFields().contains(key)) {
            customConfigObject.getAssetsEditpageBasicFields().remove(key);
        }
        if (customConfigObject.getAssetsEditpageCatalogFields().contains(key)) {
            customConfigObject.getAssetsEditpageCatalogFields().remove(key);
        }
        if (customConfigObject.getAssetsEditpageCodesFields().contains(key)) {
            customConfigObject.getAssetsEditpageCodesFields().remove(key);
        }
        if (customConfigObject.getAssetsEditpageFinicialFields().contains(key)) {
            customConfigObject.getAssetsEditpageFinicialFields().remove(key);
        }
        if (customConfigObject.getAssetsEditpageStatusFields().contains(key)) {
            customConfigObject.getAssetsEditpageStatusFields().remove(key);
        }
        if (customConfigObject.getAssetsEditpageDateFields().contains(key)) {
            customConfigObject.getAssetsEditpageDateFields().remove(key);
        }
        if (customConfigObject.getAssetsMobilepageBasicFields().contains(key)) {
            customConfigObject.getAssetsMobilepageBasicFields().remove(key);
        }
        if (customConfigObject.getAssetsMobilepageDetailFields().contains(key)) {
            customConfigObject.getAssetsMobilepageDetailFields().remove(key);
        }
        if (customConfigObject.getAssetsMobilepageHeaderFields().contains(key)) {
            customConfigObject.getAssetsMobilepageHeaderFields().remove(key);
        }
        if (JSONPath.containsValue(customConfigObject.getMobilePage(), "$.pageWorkOrder.group[type='assetInfo'].assetInfoFields",key)) {
            JSONPath.set(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='assetInfo'].assetInfoFields",
                        ((JSONArray)JSONPath.eval(customConfigObject, "$.mobilePage.pageWorkOrder.group[type='assetInfo'].assetInfoFields")).fluentRemove(key));
        }
    }

}
