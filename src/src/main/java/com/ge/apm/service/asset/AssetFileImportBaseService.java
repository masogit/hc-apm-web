/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.AssetInfoFormatter;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.CustomConfigConstant;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
public class AssetFileImportBaseService {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileImportBaseService.class);

    public static final String EXCEL_ASSET_TEMPLATE_VERSION = "v2.4";

    public static final String SHEET_ASSET = "Asset List";
    public static final String SHEET_ORG = "Org List";
    public static final int EXCEL_ASSET_HEADER_ROW = 6;
    public static final int EXCEL_ASSET_DATA_ROW = 7;

    public static final int EXCEL_ORG_HEADER_ROW = 4;
    public static final int EXCEL_ORG_DATA_ROW = 5;

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    protected Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
    protected String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();
    public List<I18nMessage> assetFunctionTypes = FieldValueMessageController.getFieldValueList("assetFunctionType", siteId);
    public List<I18nMessage> assetGroup = FieldValueMessageController.getFieldValueList("assetGroup", siteId);
    public List<I18nMessage> depreciationMethod = FieldValueMessageController.getFieldValueList("depreciationMethodList", siteId);
    public List<I18nMessage> assetUsageList = FieldValueMessageController.getFieldValueList("assetUsage", siteId);
    public List<I18nMessage> capitalSourceList = FieldValueMessageController.getFieldValueList("capitalSource", siteId);
    public List<I18nMessage> measureGradeList = FieldValueMessageController.getFieldValueList("measureGrade", siteId);
    public List<I18nMessage> riskGradeList = FieldValueMessageController.getFieldValueList("riskGrade", siteId);
    public List<I18nMessage> emergencyEquipmentList = FieldValueMessageController.getFieldValueList("emergencyEquipment", siteId);
    public List<I18nMessage> importEquipmentList = FieldValueMessageController.getFieldValueList("importEquipment", siteId);
    public List<I18nMessage> accountingCategoryList = FieldValueMessageController.getFieldValueList("accountingCategory", siteId);
    public List<I18nMessage> measuringItemCategoryList = FieldValueMessageController.getFieldValueList("measuringItemCategory", siteId);
    public List<I18nMessage> repairFormList = FieldValueMessageController.getFieldValueList("repairForm", siteId);
    public List<I18nMessage> assetStatusList = FieldValueMessageController.getFieldValueList("assetStatus", siteId);

    @Autowired
    protected AssetInfoRepository assetDao;

    @Autowired
    protected OrgInfoRepository orgDao;

    @Autowired
    protected UserAccountRepository userDao;

    @Autowired
    protected QrCodeLibRepository qrcodeDao;

    @Autowired
    protected QrCodeService qrCodeService;

    @Autowired
    protected AssetFinacingNumGenerateService assetFinacingNumGenerateService;

    @Autowired
    protected ConfigService configService;

    @Autowired
    protected AssetFileImportSupportingService supportingService;

    @Autowired
    protected AssetDepreciationService assetDepreciationService;

    protected OrgInfo selectedOrgToImport;

    protected String assetCodeConfig;

    protected WorkflowConfig workflowConfig;

    protected boolean isNeedAudit;



    public void setSelectedOrgToImport(OrgInfo selectedOrgToImport) {
        this.selectedOrgToImport = selectedOrgToImport;

        workflowConfig = configService.findConfigBySite(selectedOrgToImport.getSiteUID());

        if (workflowConfig != null) {
            assetCodeConfig = workflowConfig.getAssetCodeConfig();
        }

        isNeedAudit = configService.isAssetNeedAudit(selectedOrgToImport.getSiteUID());

    }

}
