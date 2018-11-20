/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
    public List<I18nMessage> assetValueCategoryList = FieldValueMessageController.getFieldValueList("assetValueCategory", siteId);
    public List<I18nMessage> capitalSource2List = FieldValueMessageController.getFieldValueList("capitalSource2", siteId);
    public List<I18nMessage> deviceStatusList = FieldValueMessageController.getFieldValueList("deviceStatus", siteId);

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

    @Autowired
    protected I18nMessageRepository i18nDao;

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


    public void importData(List<Map<String, Object>> importOrgMap, List<Map<String, Object>> importAssetMap,
                           Boolean autoGenerateQrcode, Boolean isGeneratePmOrder) {

        LocalDateTime startTime = LocalDateTime.now();

        importOrgMap.forEach(item -> {
            OrgInfo newOrgInfo = (OrgInfo) item.get("org");
            OrgInfo orgInfo = orgDao.findFirstBySiteUIDAndNameAndOrgType(selectedOrgToImport.getUid(), newOrgInfo.getName(), 4);

            if (orgInfo == null) {
                supportingService.importOrgIfNeeded(selectedOrgToImport, newOrgInfo.getName());
            }

            item.put("status", orgInfo == null ? ImportStatus.Created : ImportStatus.Exist);
        });

        int row = 0;
        try {

            for (Map<String, Object> item: importAssetMap) {
                if (row % 100 == 0) {
                    logger.info("imported {} rows", row);
                }

                ++row;
                importOneAsset(item, autoGenerateQrcode);
            }

        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }


        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());

    }

    @Transactional
    protected void importOneAsset(Map<String, Object> item, boolean autoGenerateQrcode) {

        AssetInfo newAsset = (AssetInfo) item.get("asset");

        //if site uid not match, ignore update
        if (newAsset.getUid() != null) {
            if (!selectedOrgToImport.getUid().equals(newAsset.getSiteUID())) {
                return ;
            }

        }

        AssetRow.clearErrMessage(item);

        item.put("status", StringUtils.isEmpty(newAsset.getUid()) ? ImportStatus.Created : ImportStatus.Exist);

        //check depart num to avoid duplicate when update
        boolean isDuplicate = supportingService.isAssetDepartNumDuplicate(selectedOrgToImport, newAsset);
        if (isDuplicate) {
            item.put("status", ImportStatus.Failure);
            AssetRow.addErrMessage(item, "错误: 设备编号重复");
            return;
        }

        String orgName = newAsset.getClinicalDeptName();
        if (StringUtils.isEmpty(orgName)) {
            item.put("status", ImportStatus.Failure);
            AssetRow.addErrMessage(item, "错误: 科室为空");
            return;
        } else {
            OrgInfo orgInfo = supportingService.findOrgOnly(selectedOrgToImport, orgName);
            if (orgInfo != null) {
                //new child clinical will reach here
                newAsset.setClinicalDeptId(orgInfo.getId());
                newAsset.setClinicalDeptUID(orgInfo.getUid());
            } else {
                newAsset.setClinicalDeptName(null);
            }

        }

        supportingService.setAssetDeptId(newAsset, selectedOrgToImport, item);

        List<UserAccount> userAccounts;
        //for AssetOwner
        if (newAsset.getAssetOwnerName() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName") + "未设置");
        } else {

            userAccounts = userDao.findByTenantUIDAndName(tenantUID, newAsset.getAssetOwnerName());
            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName") + newAsset.getAssetOwnerName() + "无此用户/无法关联");
                newAsset.setAssetOwnerName(null);
            } else {
                newAsset.setAssetOwnerId(userAccounts.get(0).getId());
                newAsset.setAssetOwnerTel(userAccounts.get(0).getTelephone());
            }
        }

        //for AssetOwner2
        if (newAsset.getAssetOwnerName2() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName2") + "未设置");
        } else {
            userAccounts = userDao.findByTenantUIDAndName(tenantUID, newAsset.getAssetOwnerName2());
            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName2") + newAsset.getAssetOwnerName2() + "无此用户/无法关联");
                newAsset.setAssetOwnerName2(null);
            } else {
                newAsset.setAssetOwnerId2(userAccounts.get(0).getId());
                newAsset.setAssetOwnerTel2(userAccounts.get(0).getTelephone());
            }

        }

        //for AssetOwner3
        if (newAsset.getAssetOwnerName3() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("assetOwnerName3") + "未设置");
        } else {
            userAccounts = userDao.findByTenantUIDAndName(tenantUID, newAsset.getAssetOwnerName3());
            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("assetOwnerName3") + newAsset.getAssetOwnerName3() + "无此用户/无法关联");
                newAsset.setAssetOwnerName3(null);
            } else {
                newAsset.setAssetOwnerId3(userAccounts.get(0).getId());
                newAsset.setAssetOwnerTel3(userAccounts.get(0).getTelephone());
            }

        }

        //for ClinicalOwner
        if (newAsset.getClinicalOwnerName() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("ClinicalOwnerName") + "未设置");
        } else {
            userAccounts = userDao.findByTenantUIDAndName(tenantUID, newAsset.getClinicalOwnerName());
            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("ClinicalOwnerName") + newAsset.getClinicalOwnerName() + "无此用户/无法关联");
                newAsset.setClinicalOwnerName(null);
            } else {
                newAsset.setClinicalOwnerId(userAccounts.get(0).getId());
                newAsset.setClinicalOwnerTel(userAccounts.get(0).getTelephone());
            }

        }

        //for Pm OwnerName
        if (newAsset.getPmOwnerName() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("pmOwnerName") + "未设置");
        } else {
            userAccounts = userDao.findByTenantUIDAndName(tenantUID, newAsset.getPmOwnerName());
            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("pmOwnerName") + newAsset.getPmOwnerName() + "无此用户/无法关联");
                newAsset.setPmOwnerName(null);
            } else {
                newAsset.setPmOwnerId(userAccounts.get(0).getId());
                newAsset.setPmOwnerTel(userAccounts.get(0).getTelephone());
            }

        }

        Supplier supplier;
        //导入供应商，并修正ID
        if (!StringUtils.isEmpty(newAsset.getVendor())) {
            supplier = supportingService.importSupplierIfNeeded(selectedOrgToImport, newAsset.getVendor(), newAsset.getVendorTel());
            newAsset.setSupplierId(supplier.getId());
        } else {
            newAsset.setSupplierId(null);
        }

        ////导入维修商，并修正ID
        if (!StringUtils.isEmpty(newAsset.getMaitanance())) {
            supplier = supportingService.importMaintainancerIfNeeded(selectedOrgToImport, newAsset.getMaitanance(), newAsset.getMaitananceTel());
            newAsset.setMaitananceId(supplier.getId().toString());
        } else {
            newAsset.setMaitananceId(null);
        }

        ////导入制造商，并修正ID
        if (!StringUtils.isEmpty(newAsset.getManufacture())) {
            supplier = supportingService.importManufacturerIfNeeded(selectedOrgToImport, newAsset.getManufacture());
            newAsset.setManufactureId(supplier.getId().toString());
        } else {
            newAsset.setManufactureId(null);
        }

        //auto generate financing num
        String financingNum = newAsset.getFinancingNum();
        String nationalStandardCode = newAsset.getNationalStandardCode();

        if (supportingService.needGenerateFinancingNum(workflowConfig, newAsset)) {

            //修改的时候不需要生成资产编号
            financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(
                    null,
                    nationalStandardCode,
                    newAsset.getSiteUID(),
                    null,
                    financingNum
            );

            if (!financingNum.startsWith("error:")) {
                newAsset.setFinancingNum(financingNum);
            } else {
                item.put("status", ImportStatus.Failure);
                AssetRow.addErrMessage(item, financingNum);
                return;
            }

        }


        supportingService.releaseOldQRCodeInUse(selectedOrgToImport, item);

        //create qr code if needed, make new qrcode in used.
        if (StringUtils.isEmpty(newAsset.getQrCode())) {
            if (autoGenerateQrcode) {
                QrCodeLib newCode = qrCodeService.createOneQrCode(selectedOrgToImport, 3);
                newAsset.setQrCode(newCode.getQrCode());
            }
        } else {

            boolean changed = supportingService.isQrCodeNewOrChanged(newAsset, item);

            if (changed) {
                QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(newAsset.getQrCode(),newAsset.getSiteUID());
                if (qrCodeLib == null) {
                    qrCodeService.createSpecifiedQrCode(newAsset.getQrCode(), selectedOrgToImport, 3);

                } else {
                    if (qrCodeLib.getStatus() == 1) {//已发行
                        qrCodeLib.setStatus(3);//已建档
                        qrcodeDao.save(qrCodeLib);
                    } else {
                        item.put("status", ImportStatus.Failure);
                        AssetRow.addErrMessage(item, "错误: 该二维码已使用！");
                        return;
                    }

                }
            }

        }

        //if import status failure, already returned, will not reach here.
        assetDao.save(newAsset);
        assetDepreciationService.saveAssetDerpeciation(newAsset);

    }

    public Boolean checkData(List<Map<String, Object>> importOrgMap, List<Map<String, Object>> importAssetMap) {

        LocalDateTime startTime = LocalDateTime.now();

        HashSet<Boolean> result = new HashSet<>();
        importOrgMap.forEach(item -> {
            OrgInfo newOrgInfo = (OrgInfo) item.get("org");

            //parent uid is not working for child clinical dept, so we use site id
            OrgInfo orgInfo = orgDao.findFirstBySiteUIDAndNameAndOrgType(selectedOrgToImport.getUid(), newOrgInfo.getName(), 4);

            item.put("status", orgInfo == null ? ImportStatus.New : ImportStatus.Exist);
        });

        int row = 0;
        for (Map<String, Object> item: importAssetMap) {
            ++row;

            AssetInfo newAsset = (AssetInfo) item.get("asset");

            if (!StringUtils.isEmpty(newAsset.getUid())) {
                //检查是否输入非法UUID
                if (!tenantUID.equals(newAsset.getTenantUID())) {
                    item.put("status", ImportStatus.CheckFail);
                    result.add(Boolean.FALSE);
                    AssetRow.addErrMessage(item, String.format("第%d行错误: 非法UUID！", row));
                    continue;
                }
            }

            if (!StringUtils.isEmpty(newAsset.getQrCode())) {
                if (supportingService.isQrCodeNewOrChanged(newAsset, item)) {
                    boolean isAvailable = qrCodeService.isQrcodeAvailableInStatus(newAsset.getQrCode(), selectedOrgToImport.getUid(), 1);
                    if (!isAvailable) {
                        item.put("status", ImportStatus.CheckFail);
                        result.add(Boolean.FALSE);
                        AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage("InvalidQRCode") + newAsset.getQrCode());
                        continue;
                    }
                }
            }

            if (null == newAsset.getAssetGroup()) {
                item.put("status", ImportStatus.CheckFail);
                result.add(Boolean.FALSE);
                AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage("assetGroup") + WebUtil.getMessage("ValidationRequire"));
            }

            if (StringUtils.isEmpty(newAsset.getDepartNum()) && StringUtils.isEmpty(newAsset.getUid())) {
                item.put("status", ImportStatus.CheckFail);
                result.add(Boolean.FALSE);
                AssetRow.addErrMessage(item, "错误: 设备编号是必须的");
                continue;
            }

            String orgName = newAsset.getClinicalDeptName();
            if (StringUtils.isEmpty(orgName)) {
                item.put("status", ImportStatus.CheckFail);
                AssetRow.addErrMessage(item, "错误: 所属科室为空");
            } else {

                //if child clinical dept not found, we don't create. we only create parent clinical dept in later steps.
                if (!supportingService.isShortName(orgName)) {
                    OrgInfo orgInfo = orgDao.findFirstBySiteUIDAndNameAndOrgType(selectedOrgToImport.getUid(), orgName, 4);
                    if (orgInfo == null) {
                        item.put("status", ImportStatus.CheckFail);
                        result.add(Boolean.FALSE);
                        AssetRow.addErrMessage(item, "错误: 所属子科室没有找到");
                    }
                }

            }

            //check depart num to avoid duplicate when update
            boolean isDuplicate = supportingService.isAssetDepartNumDuplicate(selectedOrgToImport, newAsset);
            if (isDuplicate) {
                item.put("status", ImportStatus.CheckFail);
                result.add(Boolean.FALSE);
                AssetRow.addErrMessage(item, "错误: 设备编号重复");
            }

            //配置了三码合一和设备编号不为空，则检查，Jira: YHAAO-1594
            if ((AssetFileImportSupportingService.ASSET_CODE_MERGE_THREE.equals(assetCodeConfig) || AssetFileImportSupportingService.ASSET_CODE_MERGE_TWO.equals(assetCodeConfig)) && !StringUtils.isEmpty(newAsset.getDepartNum())) {
                if (newAsset.getDepartNum().length() > 16) {
                    item.put("status", ImportStatus.CheckFail);
                    result.add(Boolean.FALSE);
                    AssetRow.addErrMessage(item, "错误: 设备编号不能超过16位");
                    continue;
                }

                if (!newAsset.getDepartNum().equals(newAsset.getFinancingNum()) && !StringUtils.isEmpty(newAsset.getFinancingNum())) {
                    item.put("status", ImportStatus.CheckFail);
                    result.add(Boolean.FALSE);
                    AssetRow.addErrMessage(item, "错误: 资产编码与设备编码不相同不允许导入");
                } else {
                    newAsset.setFinancingNum(newAsset.getDepartNum());

                    if (AssetFileImportSupportingService.ASSET_CODE_MERGE_THREE.equals(assetCodeConfig)) {
                        newAsset.setQrCode(newAsset.getDepartNum());
                    }

                }


            }


        }

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for checkData: {} seconds", duration.getSeconds());

        return result.isEmpty();
    }

}
