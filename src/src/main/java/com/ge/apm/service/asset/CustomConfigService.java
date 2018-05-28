/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.CustomConfigRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SysRoleRepository;
import com.ge.apm.domain.CustomConfig;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.UserRole;
import com.ge.apm.domain.pure.CustomConfigAssetField;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
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

    static public LinkedHashMap<String, String> getAllFields() {
        LinkedHashMap<String, String> res = new LinkedHashMap();
        res.put("serialNum", WebUtil.getMessage("serialNum"));
        res.put("departNum", WebUtil.getMessage("departNum"));
        res.put("name", WebUtil.getMessage("name"));
        res.put("aliasName", WebUtil.getMessage("aliasName"));
        res.put("assetGroup", WebUtil.getMessage("assetGroup"));
        res.put("assetOwnerName", WebUtil.getMessage("assetOwnerName"));
        res.put("assetOwnerTel", WebUtil.getMessage("assetOwnerTel"));
        res.put("assetOwnerName2", WebUtil.getMessage("assetOwnerName2"));
        res.put("assetOwnerTel2", WebUtil.getMessage("assetOwnerTel2"));
        res.put("assetOwnerName3", WebUtil.getMessage("assetOwnerName3"));
        res.put("assetOwnerTel3", WebUtil.getMessage("assetOwnerTel3"));
        res.put("siteUID", WebUtil.getMessage("hospitalId"));
        res.put("clinicalDeptName", WebUtil.getMessage("clinicalDeptName"));
        res.put("clinicalOwnerName", WebUtil.getMessage("clinicalOwnerName"));
        res.put("clinicalOwnerTel", WebUtil.getMessage("clinicalOwnerTel"));
        res.put("manufacture", WebUtil.getMessage("manufacture"));
        res.put("makerShortName", WebUtil.getMessage("makerShortName"));
        res.put("vendor", WebUtil.getMessage("vendor"));
        res.put("maitanance", WebUtil.getMessage("maitanance"));
        res.put("maitananceTel", WebUtil.getMessage("maitananceTel"));
        res.put("nationalStandardCode", WebUtil.getMessage("nationalStandardCode"));
        res.put("parentAssetId", WebUtil.getMessage("parentAsset"));
        res.put("deviceName", WebUtil.getMessage("deviceName"));
        res.put("systemId", WebUtil.getMessage("systemId"));
        res.put("financingNum", WebUtil.getMessage("financingNum"));
        res.put("barcode", WebUtil.getMessage("barcode"));
        res.put("registrationNo", WebUtil.getMessage("registrationNo"));
        res.put("modalityId", WebUtil.getMessage("modalityId"));
        res.put("qrCode", WebUtil.getMessage("qrCode"));
        res.put("functionType", WebUtil.getMessage("functionType"));
        res.put("functionGroup", WebUtil.getMessage("functionGroup"));
        res.put("assetCode68", WebUtil.getMessage("assetCode68"));
        res.put("functionGrade", WebUtil.getMessage("functionGrade"));
        res.put("repairForm", WebUtil.getMessage("repairForm"));
        res.put("locationCode", WebUtil.getMessage("locationCode"));
        res.put("locationName", WebUtil.getMessage("locationName"));
        res.put("status", WebUtil.getMessage("status"));
        res.put("deviceStatus", WebUtil.getMessage("deviceStatus"));
        res.put("comments", WebUtil.getMessage("comments"));
        res.put("assetUsage", WebUtil.getMessage("assetUsage"));
        res.put("capitalSource", WebUtil.getMessage("capitalSource"));
        res.put("capitalSource2", WebUtil.getMessage("capitalSource2"));
        res.put("measureGrade", WebUtil.getMessage("measureGrade"));
        res.put("riskGrade", WebUtil.getMessage("riskGrade"));
        res.put("emergencyEquipment", WebUtil.getMessage("emergencyEquipment"));
        res.put("importEquipment", WebUtil.getMessage("importEquipment"));
        res.put("measuringItemCategory", WebUtil.getMessage("measuringItemCategory"));
        res.put("accountingCategory", WebUtil.getMessage("accountingCategory"));
        res.put("manufactDate", WebUtil.getMessage("manufactDate"));
        res.put("purchaseDate", WebUtil.getMessage("purchaseDate"));
        res.put("arriveDate", WebUtil.getMessage("arriveDate"));
        res.put("installDate", WebUtil.getMessage("installDate"));
        res.put("factoryWarrantyDate", WebUtil.getMessage("factoryWarrantyDate"));
        res.put("warrantyDate", WebUtil.getMessage("warrantyDate"));
        res.put("terminateDate", WebUtil.getMessage("terminateDate"));
        res.put("purchasePrice", WebUtil.getMessage("purchasePrice"));
        res.put("salvageValue", WebUtil.getMessage("salvageValue"));
        res.put("lifecycle", WebUtil.getMessage("lifecycle"));
        res.put("depreciationMethod", WebUtil.getMessage("depreciationMethod"));
        res.put("usageStatus", WebUtil.getMessage("usageStatus"));
        res.put("isPm", WebUtil.getMessage("isAssetPMed"));
        res.put("isChecked", WebUtil.getMessage("isAssetChecked"));
        res.put("lastPmDate", WebUtil.getMessage("lastPmDate"));
        res.put("lastQaDate", WebUtil.getMessage("lastQaDate"));
        res.put("lastMeteringDate", WebUtil.getMessage("lastMeteringDate"));
        res.put("warranty", "是否对质保到期提醒");
        return res;
    }
    
    static public List<String> getSysTemFields(){
        return Arrays.asList("name","assetGroup");
    }
    
    public List<SysRole> getConfigRoles(){
        return sysRoleDao.getByNameIn(Arrays.asList("HospitalHead","AssetHead","DeptHead","AssetStaff","ClinicalStaff"));
    }

    public CustomConfig getCustomConfigBySite(String siteUID){
        return customConfigDao.findBySiteUID(siteUID);
    }
    
    public CustomConfigObject getCurrentCustomConfig(String siteUid) {
        OrgInfo org = orgDao.findByUid(siteUid);
        if (configService.isInsulateTenant(org.getTenantUID())) {
            CustomConfig res = getCustomConfigBySite(siteUid);
            return res == null ? getDefalutCustomConfigObject() : JSONObject.parseObject(res.getConfig(), CustomConfigObject.class);
        }
        List<CustomConfig> configList = customConfigDao.findBySiteUIDIn(Arrays.asList(org.getSiteUID(), org.getHospitalUID(), org.getInstitutionUID(), org.getTenantUID()));
        if(null==configList || configList.isEmpty()){
            return getDefalutCustomConfigObject();
        }
        CustomConfig config = configList.stream().filter(item->org.getSiteUID().equals(item.getSiteUID())).findAny()
                .orElse(configList.stream().filter(item->org.getHospitalUID().equals(item.getHospitalUID())).findAny()
                        .orElse(configList.stream().filter(item->org.getInstitutionUID().equals(item.getInstitutionUID())).findAny()
                                .orElse(configList.stream().filter(item->org.getTenantUID().equals(item.getTenantUID())).findAny().orElse(null))));
        return config==null?getDefalutCustomConfigObject():JSONObject.parseObject(config.getConfig(), CustomConfigObject.class);
        
    }

    private CustomConfigObject getDefalutCustomConfigObject() {
        CustomConfigObject object = new CustomConfigObject();
        object.setAssetFields(new HashMap());
        List<String> allRoles = getConfigRoles().stream().map(SysRole::getName).collect(Collectors.toList());
        getAllFields().entrySet().stream().forEach(item-> object.getAssetFields().put(item.getKey(), new CustomConfigAssetField(item.getKey(),allRoles)));
        getSysTemFields().forEach(item->{
            object.getAssetFields().get(item).setRequired(true);
        });
        List<String> listPageFields = new ArrayList();
        listPageFields.addAll(Arrays.asList("name","functionType","serialNum","departNum","assetGroup",
                "clinicalDeptName","manufacture","vendor","status","warrantyDate","financingNum","qrCode"));
        object.setAssetsListPageFields(listPageFields);
        object.setAssetsDetailpageBasicFields(Arrays.asList("serialNum","name","aliasName","assetGroup","assetOwnerName",
                "assetOwnerTel","assetOwnerName2","assetOwnerTel2","assetOwnerName3","assetOwnerTel3","siteUID",
                "clinicalDeptName","clinicalOwnerName","clinicalOwnerTel","manufacture","makerShortName","vendor",
                "maitanance","maitananceTel","nationalStandardCode"));
        object.setAssetsDetailpageDetailFields(Arrays.asList("parentAssetId","deviceName","systemId","financingNum","departNum",
                "barcode","registrationNo","modalityId","qrCode","functionType","functionGroup","assetCode68","functionGrade",
                "repairForm","locationCode","locationName","status","deviceStatus","isPm","isChecked","comments","assetUsage",
                "capitalSource","capitalSource2","measureGrade","riskGrade","emergencyEquipment","importEquipment","measuringItemCategory",
                "usageStatus","accountingCategory","manufactDate","purchaseDate","arriveDate","installDate","factoryWarrantyDate",
                "warrantyDate","terminateDate","purchasePrice","salvageValue","lifecycle","depreciationMethod","lastPmDate",
                "lastQaDate","lastMeteringDate","warranty"));
        
        object.setAssetsEditpageBasicFields(Arrays.asList("serialNum","name","aliasName","assetGroup","assetOwnerName",
                "assetOwnerTel","assetOwnerName2","assetOwnerTel2","assetOwnerName3","assetOwnerTel3","siteUID","clinicalDeptName",
                "clinicalOwnerName","clinicalOwnerTel","manufacture","makerShortName","vendor","maitanance","maitananceTel","nationalStandardCode"));
        object.setAssetsEditpageCodesFields(Arrays.asList("parentAssetId","deviceName","systemId","financingNum","departNum",
                "barcode","registrationNo","modalityId","qrCode"));
        object.setAssetsEditpageStatusFields(Arrays.asList("functionType","functionGroup","assetCode68","functionGrade","repairForm",
                "locationCode","locationName","status","deviceStatus","isPm","isChecked","comments"));
        object.setAssetsEditpageCatalogFields(Arrays.asList("assetUsage","capitalSource","capitalSource2","measureGrade","riskGrade",
                "emergencyEquipment","importEquipment","measuringItemCategory","accountingCategory"));
        object.setAssetsEditpageDateFields(Arrays.asList("manufactDate","purchaseDate","arriveDate","installDate","factoryWarrantyDate",
                "warrantyDate","terminateDate","warranty"));
        object.setAssetsEditpageFinicialFields(Arrays.asList("purchasePrice","salvageValue","lifecycle","depreciationMethod"));
        return object;
    }

    public void saveConfig(OrgInfo selectSite, CustomConfigObject customConfigObject) {
        CustomConfig config = getCustomConfigBySite(selectSite.getSiteUID());
        if(config==null){
            config = new CustomConfig();
            config.setConfig(JSONObject.toJSONString(customConfigObject));
            config.setTenantUID(selectSite.getTenantUID());
            config.setInstitutionUID(selectSite.getInstitutionUID());
            config.setHospitalUID(selectSite.getHospitalUID());
            config.setSiteUID(selectSite.getSiteUID());
            config.setCreatedDate(new Date());
            config.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
        }else{
            config.setConfig(JSONObject.toJSONString(customConfigObject));
            config.setLastModifiedBy(UserContextService.getCurrentUserAccount().getUid());
            config.setLastModifiedDate(new Date());
        }
        customConfigDao.save(config);
    }

    public void removeConfig(OrgInfo selectSite) {
        CustomConfig config = getCustomConfigBySite(selectSite.getSiteUID());
        if(null!=config){
            customConfigDao.delete(config);
        }
    }

    public boolean hasAssetFieldPermissionByRole(CustomConfigObject config, String key, List<Object> userRoles){
        
        return true;
    }
    
    public List<String> getReadAssetFieldsByRoles(CustomConfigObject config,List<String> userRoles){
       return config.getAssetFields().entrySet().stream()
               .filter(item-> item.getValue().getReadRoles().stream().anyMatch(itemRole -> userRoles.contains(itemRole)))
               .map(Entry::getKey).collect(Collectors.toList());
    }
    
    public List<String> getEditAssetFieldsByRoles(CustomConfigObject config,List<String> userRoles){
       return config.getAssetFields().entrySet().stream()
               .filter(item-> item.getValue().getEditRoles().stream().anyMatch(itemRole -> userRoles.contains(itemRole)))
               .map(Entry::getKey).collect(Collectors.toList());
    }

    public List<String> getExportAssetFieldsByRoles(CustomConfigObject config, List<String> userRoles) {
        return config.getAssetFields().entrySet().stream()
               .filter(item-> item.getValue().getExportRoles().stream().anyMatch(itemRole -> userRoles.contains(itemRole)))
               .map(Entry::getKey).collect(Collectors.toList());
    }

    public void removeAssetField(CustomConfigObject customConfigObject, String key) {
        
        if(getSysTemFields().contains(key)){
            WebUtil.addErrorMessage("系统必填字段,不能删除");
            return;
        }
        if (customConfigObject.getAssetFields().containsKey(key)) {
            customConfigObject.getAssetFields().remove(key);
        }
        if (customConfigObject.getAssetsListPageFields().contains(key)) {
            customConfigObject.getAssetsListPageFields().remove(key);
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
    }
    
}
