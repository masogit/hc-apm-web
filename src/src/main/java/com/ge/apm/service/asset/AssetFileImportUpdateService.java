/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.poi.ss.usermodel.ExcelStyleDateFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.jsf.FacesContextUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.DbResourcesUtil;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import java.util.*;

/**
 * @author Quan Tang
 */
@Component
public class AssetFileImportUpdateService {

    private Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
    private String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();
    private String orgUid = UserContextService.getCurrentUserAccount().getSiteUID();
    private List<I18nMessage> assetFunctionTypes = FieldValueMessageController.getFieldValueList("assetFunctionType", siteId);
    private List<I18nMessage> assetGroup = FieldValueMessageController.getFieldValueList("assetGroup", siteId);
    private List<I18nMessage> depreciationMethod = FieldValueMessageController.getFieldValueList("depreciationMethodList", siteId);
    private List<I18nMessage> assetCategoryList = FieldValueMessageController.getFieldValueList("assetCategory", siteId);
    private List<I18nMessage> assetUsageList = FieldValueMessageController.getFieldValueList("assetUsage", siteId);
    private List<I18nMessage> capitalSourceList = FieldValueMessageController.getFieldValueList("capitalSource", siteId);
    private List<I18nMessage> measureGradeList = FieldValueMessageController.getFieldValueList("measureGrade", siteId);
    private List<I18nMessage> riskGradeList = FieldValueMessageController.getFieldValueList("riskGrade", siteId);
    private List<I18nMessage> emergencyEquipmentList = FieldValueMessageController.getFieldValueList("emergencyEquipment", siteId);
    private List<I18nMessage> importEquipmentList = FieldValueMessageController.getFieldValueList("importEquipment", siteId);
    private List<I18nMessage> usageStatusList = FieldValueMessageController.getFieldValueList("usageStatus", siteId);
    private List<I18nMessage> accountingCategoryList = FieldValueMessageController.getFieldValueList("accountingCategory", siteId);
    private List<I18nMessage> measuringItemCategoryList = FieldValueMessageController.getFieldValueList("measuringItemCategory", siteId);

    @Autowired
    private AssetInfoRepository assetInfoDao;

    @Autowired
    private OrgInfoRepository orgInfoDao;

    @Autowired
    private UserAccountRepository userDao;

    @Autowired
    DbResourcesUtil dbResourcesUtil;

    @PostConstruct
    protected void init() {
        FacesContextUtils
                .getRequiredWebApplicationContext(FacesContext.getCurrentInstance())
                .getAutowireCapableBeanFactory().autowireBean(this);

    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAll(List<Map<String, Object>> results) throws Exception{

        //clear data like QR code to avoid unique key exception
        clearBeforeUpdateAll(results);

        String stringValue;
        Integer intValue;
        Date dateValue;
        String columnTitle = null;
//        Object columnValue = null;

        int row = 1;
        for (Map<String, Object> record: results) {

            stringValue = getColumnStringValue(record, "ID");
            AssetInfo assetInfo = assetInfoDao.findByUid(stringValue);
            if (assetInfo == null) {
                throw new Exception(String.format("第{0}条记录没有找到", row));
            }

            stringValue = getColumnStringValue(record, "preventiveMaintenanceAnalysis_deviceName");
            assetInfo.setName(stringValue);

            stringValue = getColumnStringValue(record, "functionType");
            assetInfo.setFunctionType(stringValue);

            stringValue = getColumnStringValue(record, "serialNum");
            assetInfo.setSerialNum(stringValue);

            stringValue = getColumnStringValue(record, "departNum");
            assetInfo.setDepartNum(stringValue);

            stringValue = getColumnStringValue(record, "assetGroup");
            intValue = stringValue == null ? null : findSelectionKey(this.assetGroup, stringValue);
            if (!StringUtils.isEmpty(stringValue) && null == intValue) {
                columnTitle = DbResourcesUtil.getInstance().getAsResourceBundle().getString("assetGroup");
                throw new Exception(String.format("第{0}条记录{1}没有找到", row, columnTitle));
            }
            assetInfo.setAssetGroup(intValue);

            stringValue = getColumnStringValue(record, "nationalStandardCode");
            assetInfo.setNationalStandardCode(stringValue);

//            stringValue = getColumnStringValue(record, "brand");
//            assetInfo.setBrand(stringValue);

            stringValue = getColumnStringValue(record, "manufacture");
            assetInfo.setManufacture(stringValue);

            stringValue = getColumnStringValue(record, "vendor");
            assetInfo.setVendor(stringValue);

            dateValue = getColumnDateValue(record, "warrantyDate");
            assetInfo.setWarrantyDate(dateValue);

            stringValue = getColumnStringValue(record, "aliasName");
            assetInfo.setAliasName(stringValue);

            stringValue = getColumnStringValue(record, "financingNum");
            assetInfo.setFinancingNum(stringValue);

            stringValue = getColumnStringValue(record, "QRCode");
            assetInfo.setQrCode(stringValue);




            assetInfoDao.save(assetInfo);

            ++row;

        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void clearBeforeUpdateAll(List<Map<String, Object>> results) throws Exception {

        String stringValue;

        int row = 1;
        //clear QR code first, otherwise throw unique key exception
        for (Map<String, Object> record : results) {

            stringValue = getColumnStringValue(record, "ID");
            AssetInfo assetInfo = assetInfoDao.findByUid(stringValue);
            if (assetInfo == null) {
                throw new Exception(String.format("第{0}条记录没有找到", row));
            }
            assetInfo.setQrCode(null);


            ++row;

            assetInfoDao.save(assetInfo);

        }

    }

    private Object getColumnValue(Map<String, Object> record, String columnKey) throws Exception {
        String columnTitle = DbResourcesUtil.getInstance().getAsResourceBundle().getString(columnKey);
        if (StringUtils.isEmpty(columnTitle)) {
            throw new Exception(String.format("列别名{0}没有找到相应的列名", columnKey));
        }

        if (!record.containsKey(columnTitle)) {
            throw new Exception(String.format("列名{0}不在数据中", columnTitle));
        }

        return record.get(columnTitle);
    }

    private String getColumnStringValue(Map<String, Object> record, String columnKey) throws Exception {
        Object obj = getColumnValue(record, columnKey);
        String columnValue = ExcelDocument.getCellStringValue(obj);

        return convertToNullIfEmtpy(columnValue);
    }

    private Date getColumnDateValue(Map<String, Object> record, String columnKey) throws Exception {
        Object obj = getColumnValue(record, columnKey);
        Date columnValue = ExcelDocument.getCellDateValue(obj);
        return columnValue;
    }


    private Integer findSelectionKey(List<I18nMessage> messages, String title) {

        for (I18nMessage message: messages) {
            if (message.getValue().equals(title)) {
                return Integer.valueOf(message.getMsgKey());
            }
        }

        return null;

    }

    private String convertToNullIfEmtpy(String input) {
        if (null == input) {
            return null;
        }
        if (StringUtils.isEmpty(input)) {
            return null;
        }
        return input.trim();
    }

}
