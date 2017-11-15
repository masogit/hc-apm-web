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
    private List<I18nMessage> functionGroupList = FieldValueMessageController.getFieldValueList("assetFunctionType", siteId);
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
    private List<I18nMessage> functionGradeList = FieldValueMessageController.getFieldValueList("assetFunctionGrade", siteId);

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

            stringValue = getColumnStringValue(record, "assetUsage");
            intValue = stringValue == null ? null : findSelectionKey(this.assetUsageList, stringValue);
            if (!StringUtils.isEmpty(stringValue) && null == intValue) {
                columnTitle = DbResourcesUtil.getInstance().getAsResourceBundle().getString("assetUsage");
                throw new Exception(String.format("第{0}条记录{1}没有找到", row, columnTitle));
            }
            assetInfo.setAssetUsage(intValue);

            stringValue = getColumnStringValue(record, "capitalSource");
            intValue = stringValue == null ? null : findSelectionKey(this.capitalSourceList, stringValue);
            if (!StringUtils.isEmpty(stringValue) && null == intValue) {
                columnTitle = DbResourcesUtil.getInstance().getAsResourceBundle().getString("capitalSource");
                throw new Exception(String.format("第{0}条记录{1}没有找到", row, columnTitle));
            }
            assetInfo.setCapitalSource(intValue);

            stringValue = getColumnStringValue(record, "measureGrade");
            intValue = stringValue == null ? null : findSelectionKey(this.measureGradeList, stringValue);
            if (!StringUtils.isEmpty(stringValue) && null == intValue) {
                columnTitle = DbResourcesUtil.getInstance().getAsResourceBundle().getString("measureGrade");
                throw new Exception(String.format("第{0}条记录{1}没有找到", row, columnTitle));
            }
            assetInfo.setMeasureGrade(intValue);

            stringValue = getColumnStringValue(record, "riskGrade");
            intValue = stringValue == null ? null : findSelectionKey(this.riskGradeList, stringValue);
            if (!StringUtils.isEmpty(stringValue) && null == intValue) {
                columnTitle = DbResourcesUtil.getInstance().getAsResourceBundle().getString("riskGrade");
                throw new Exception(String.format("第{0}条记录{1}没有找到", row, columnTitle));
            }
            assetInfo.setRiskGrade(intValue);

            stringValue = getColumnStringValue(record, "emergencyEquipment");
            intValue = stringValue == null ? null : findSelectionKey(this.emergencyEquipmentList, stringValue);
            if (!StringUtils.isEmpty(stringValue) && null == intValue) {
                columnTitle = DbResourcesUtil.getInstance().getAsResourceBundle().getString("emergencyEquipment");
                throw new Exception(String.format("第{0}条记录{1}没有找到", row, columnTitle));
            }
            assetInfo.setEmergencyEquipment(intValue);

            stringValue = getColumnStringValue(record, "importEquipment");
            intValue = stringValue == null ? null : findSelectionKey(this.importEquipmentList, stringValue);
            if (!StringUtils.isEmpty(stringValue) && null == intValue) {
                columnTitle = DbResourcesUtil.getInstance().getAsResourceBundle().getString("importEquipment");
                throw new Exception(String.format("第{0}条记录{1}没有找到", row, columnTitle));
            }
            assetInfo.setImportEquipment(intValue);

            stringValue = getColumnStringValue(record, "measuringItemCategory");
            intValue = stringValue == null ? null : findSelectionKey(this.measuringItemCategoryList, stringValue);
            if (!StringUtils.isEmpty(stringValue) && null == intValue) {
                columnTitle = DbResourcesUtil.getInstance().getAsResourceBundle().getString("measuringItemCategory");
                throw new Exception(String.format("第{0}条记录{1}没有找到", row, columnTitle));
            }
            assetInfo.setMeasuringItemCategory(intValue);

            stringValue = getColumnStringValue(record, "usageStatus");
            intValue = stringValue == null ? null : findSelectionKey(this.usageStatusList, stringValue);
            if (!StringUtils.isEmpty(stringValue) && null == intValue) {
                columnTitle = DbResourcesUtil.getInstance().getAsResourceBundle().getString("usageStatus");
                throw new Exception(String.format("第{0}条记录{1}没有找到", row, columnTitle));
            }
            assetInfo.setUsageStatus(intValue);

            stringValue = getColumnStringValue(record, "accountingCategory");
            intValue = stringValue == null ? null : findSelectionKey(this.accountingCategoryList, stringValue);
            if (!StringUtils.isEmpty(stringValue) && null == intValue) {
                columnTitle = DbResourcesUtil.getInstance().getAsResourceBundle().getString("accountingCategory");
                throw new Exception(String.format("第{0}条记录{1}没有找到", row, columnTitle));
            }
            assetInfo.setAccountingCategory(intValue);

            stringValue = getColumnStringValue(record, "systemId");
            assetInfo.setSystemId(stringValue);

            stringValue = getColumnStringValue(record, "functionGroup");
            intValue = stringValue == null ? null : findSelectionKey(this.functionGroupList, stringValue);
            if (!StringUtils.isEmpty(stringValue) && null == intValue) {
                columnTitle = DbResourcesUtil.getInstance().getAsResourceBundle().getString("functionGroup");
                throw new Exception(String.format("第{0}条记录{1}没有找到", row, columnTitle));
            }
            assetInfo.setFunctionGroup(intValue);

            stringValue = getColumnStringValue(record, "functionGrade");
            intValue = stringValue == null ? null : findSelectionKey(this.functionGradeList, stringValue);
            if (!StringUtils.isEmpty(stringValue) && null == intValue) {
                columnTitle = DbResourcesUtil.getInstance().getAsResourceBundle().getString("functionGrade");
                throw new Exception(String.format("第{0}条记录{1}没有找到", row, columnTitle));
            }
            assetInfo.setFunctionGrade(intValue);

            stringValue = getColumnStringValue(record, "maitanance");
            assetInfo.setMaitanance(stringValue);

            stringValue = getColumnStringValue(record, "maitananceTel");
            assetInfo.setMaitananceTel(stringValue);

            stringValue = getColumnStringValue(record, "financingNum");
            assetInfo.setFinancingNum(stringValue);

            stringValue = getColumnStringValue(record, "locationCode");
            assetInfo.setLocationCode(stringValue);

            stringValue = getColumnStringValue(record, "locationName");
            assetInfo.setLocationName(stringValue);

            stringValue = getColumnStringValue(record, "registrationNo");
            assetInfo.setRegistrationNo(stringValue);

            dateValue = getColumnDateValue(record, "manufactDate");
            assetInfo.setManufactDate(dateValue);

            dateValue = getColumnDateValue(record, "purchaseDate");
            assetInfo.setPurchaseDate(dateValue);

            dateValue = getColumnDateValue(record, "arriveDate");
            assetInfo.setArriveDate(dateValue);

            dateValue = getColumnDateValue(record, "installDate");
            assetInfo.setInstallDate(dateValue);

            dateValue = getColumnDateValue(record, "terminateDate");
            assetInfo.setTerminateDate(dateValue);

            dateValue = getColumnDateValue(record, "lastPmDate");
            assetInfo.setLastPmDate(dateValue);

            dateValue = getColumnDateValue(record, "lastMeteringDate");
            assetInfo.setLastMeteringDate(dateValue);

            dateValue = getColumnDateValue(record, "lastQaDate");
            assetInfo.setLastQaDate(dateValue);

            dateValue = getColumnDateValue(record, "factoryWarrantyDate");
            assetInfo.setFactoryWarrantyDate(dateValue);

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
