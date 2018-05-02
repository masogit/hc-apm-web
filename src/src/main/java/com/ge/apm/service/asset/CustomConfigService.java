/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.CustomConfigRepository;
import com.ge.apm.domain.CustomConfig;
import com.ge.apm.domain.pure.CustomConfigObject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
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

    public LinkedHashMap<String, String> getAllFields() {
        LinkedHashMap<String, String> res= new LinkedHashMap();
        res.put("serialNum", WebUtil.getMessage("serialNum"));
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
        return res;
    }

    public CustomConfigObject getCustomConfigBySite(String siteUID) {
        CustomConfig res = customConfigDao.findBySiteUID(siteUID);
        return res==null? null:JSONObject.parseObject(res.getConfig(), CustomConfigObject.class);
    }
    
    public CustomConfigObject getCurrentCustomConfig(){
        return null;
    }
    
    private CustomConfigObject getDefalutCustomConfigObject(){
        CustomConfigObject object = new CustomConfigObject();
        List<String> defaultAssetListPageFields = new ArrayList();
        defaultAssetListPageFields.add("name");
        defaultAssetListPageFields.add("functionType");
        defaultAssetListPageFields.add("serialNum");
        defaultAssetListPageFields.add("departNum");
        defaultAssetListPageFields.add("assetGroup");
        defaultAssetListPageFields.add("clinicalDeptName");
        defaultAssetListPageFields.add("manufacture");
        defaultAssetListPageFields.add("vendor");
        defaultAssetListPageFields.add("status");
        defaultAssetListPageFields.add("warrantyDate");
        defaultAssetListPageFields.add("financingNum");
        defaultAssetListPageFields.add("qrCode");
        object.setAssetListPageFields(defaultAssetListPageFields);
        return object;
    }
}
