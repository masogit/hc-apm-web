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

        String stringValue;
        Integer intValue;

        int row = 1;
        for (Map<String, Object> record: results) {

            stringValue = ExcelDocument.getCellStringValue(record.get("ID"));
            AssetInfo assetInfo = assetInfoDao.findByUid(stringValue);
            if (assetInfo == null) {
                throw new Exception(String.format("第{0}条记录没有找到", row));
            }

            Object columnValue = getColumnValue(record, "preventiveMaintenanceAnalysis_deviceName");
            stringValue = ExcelDocument.getCellStringValue(columnValue);
            assetInfo.setName(stringValue);

            columnValue = getColumnValue(record, "functionType");
            stringValue = ExcelDocument.getCellStringValue(columnValue);
            assetInfo.setFunctionType(stringValue);

            columnValue = getColumnValue(record, "serialNum");
            stringValue = ExcelDocument.getCellStringValue(columnValue);
            assetInfo.setSerialNum(stringValue);

            columnValue = getColumnValue(record, "departNum");
            stringValue = ExcelDocument.getCellStringValue(columnValue);
            assetInfo.setDepartNum(stringValue);

            columnValue = getColumnValue(record, "assetGroup");
            stringValue = ExcelDocument.getCellStringValue(columnValue);
            intValue = stringValue == null ? null : findSelectionKey(this.assetGroup, stringValue);
            assetInfo.setAssetGroup(intValue);

            columnValue = getColumnValue(record, "nationalStandardCode");
            stringValue = ExcelDocument.getCellStringValue(columnValue);
            assetInfo.setNationalStandardCode(stringValue);


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


    private Integer findSelectionKey(List<I18nMessage> messages, String title) {

        for (I18nMessage message: messages) {
            if (message.getValue().equals(title)) {
                return Integer.valueOf(message.getMsgKey());
            }
        }

        return null;

    }

}
