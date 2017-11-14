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

    @Transactional
    public void updateAll(List<Map<String, Object>> results) {

        String stringValue;

        for (Map<String, Object> record: results) {

            stringValue = ExcelDocument.getCellStringValue(record.get("ID"));
            AssetInfo assetInfo = assetInfoDao.findByUid(stringValue);
            if (assetInfo == null) {
                continue;
            }

            String title = DbResourcesUtil.getInstance().getAsResourceBundle().getString("preventiveMaintenanceAnalysis_deviceName");
            if (StringUtils.isEmpty(title)) {
                continue;
            }
            if (record.containsKey(title)) {
                stringValue = ExcelDocument.getCellStringValue(record.get(title));
                assetInfo.setName(stringValue);
            }

            assetInfoDao.save(assetInfo);


        }
    }



}
