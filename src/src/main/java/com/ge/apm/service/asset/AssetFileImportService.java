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
@Service
@Scope("prototype")
public class AssetFileImportService extends AssetFileImportBaseService {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileImportService.class);

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

    private Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
    private String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();
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
    private AssetInfoRepository assetDao;

    @Autowired
    private OrgInfoRepository orgDao;

    @Autowired
    private UserAccountRepository userDao;

    @Autowired
    private QrCodeLibRepository qrcodeDao;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;

    @Autowired
    ConfigService configService;

    @Autowired
    private AssetFileImportSupportingService supportingService;

    @Autowired
    private AssetDepreciationService assetDepreciationService;

    private OrgInfo selectedOrgToImport;

    private String assetCodeConfig;

    private WorkflowConfig workflowConfig;

    private boolean isNeedAudit;

    public List<Map<String, Object>> getOrgInfoMapFromTemplate(ExcelDocument doc) {

        List<Map<String, Object>> result = new ArrayList<>();
        int keyRow = 4;
        int dataRow = 5;

        for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_ORG, keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(SHEET_ORG, keyRow, ++dataRow)) {
            OrgInfo org = new OrgInfo();

            String orgName = ExcelDocument.getCellStringValue(rowMap.get("部门名称"));

            if (orgName == null || orgName.isEmpty() || orgName.equals(AssetRow.DELETE_MARK)) {
                break;
            }
            org.setShortName(orgName);
//            org.setName(orgName);
            org.setLineNum(dataRow);

            Map<String, Object> map = new LinkedHashMap();
            map.put("org", org);
            map.put("status", ImportStatus.New);
            map.put("rowData", rowMap);
            result.add(map);
        }
        return result;
    }

    public List<Map<String, Object>> getAssetMapFromTemplate(ExcelDocument doc) throws Exception {

        LocalDateTime startTime = LocalDateTime.now();

        Date now = new Date();

        List<Map<String, Object>> result = new ArrayList<>();
        int keyRow = 6;
        int dataRow = 7;

        for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_ASSET, keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(SHEET_ASSET, keyRow, ++dataRow)) {
            try{
                Map<String, Object> map = new LinkedHashMap();

                AssetInfo asset;

                String uuid = ExcelDocument.getCellStringValue(rowMap.get("UUID"));
                if (!StringUtils.isEmpty(uuid)) {
                    asset = assetDao.findByUid(uuid);
                    if (asset == null || !asset.getTenantUID().equals(selectedOrgToImport.getTenantUID())) throw new Exception("无效UUID");

                    //save old data to compare later
                    map.put(AssetFileImportSupportingService.OLD_QRCODE_KEY, asset.getQrCode());
                } else {
                    asset = new AssetInfo();
                }

                asset.setLineNum(dataRow);

                String assetName = ExcelDocument.getCellStringValue(rowMap.get("设备名称"));
                if (StringUtils.isEmpty(assetName)) {
                    break; //return if go beyond the valid cell range
                }

                String inputQrcode = ExcelDocument.getCellStringValue(rowMap.get("二维码"));
                if (!StringUtils.isEmpty(inputQrcode) && !inputQrcode.equals(asset.getQrCode())) {
                    asset.setQrCode(inputQrcode.replace("-", ""));
                }

                AssetRow assetRow = new AssetRow(asset, rowMap);
                assetRow.setFieldStringValue("设备名称", "setName");
                assetRow.setFieldStringValue("设备编号", "setDepartNum");
                assetRow.setFieldStringValue("资产编号", "setFinancingNum");
                assetRow.setFieldStringValue("设备别名", "setAliasName");
                assetRow.setFieldStringValue("维修负责人", "setAssetOwnerName");
                assetRow.setFieldStringValue("维修负责人2", "setAssetOwnerName2");
                assetRow.setFieldStringValue("维修负责人3", "setAssetOwnerName3");
                assetRow.setFieldStringValue("所属科室", "setClinicalDeptName");
                assetRow.setFieldStringValue("制造商", "setManufacture");
                assetRow.setFieldStringValue("品牌/制造商简称", "setMakerShortName");
                assetRow.setFieldStringValue("设备标准名称", "setDeviceName");
                assetRow.setFieldStringValue("注册证号", "setRegistrationNo");
                assetRow.setFieldStringValue("供应商", "setVendor");

                assetRow.setFieldDropdownValue("医疗器械分类", "setFunctionGroup", assetFunctionTypes, map, null);
                if (asset.getFunctionGroup() != null) {
                    List<I18nMessage> msgList = FieldValueMessageController.getFieldValueList(String.valueOf(asset.getFunctionGroup()), siteId);
                    assetRow.setFieldDropdownValue("医疗器械子分类", "setAssetCode68", msgList, map, null);
                } else {
                    //only update need to clear old data
                    asset.setAssetCode68(null);
                }

                assetRow.setFieldDropdownValue("设备分类", "setAssetGroup", assetGroup, map, null);

                assetRow.setFieldIntegerValue("功能等级", "setFunctionGrade");
                assetRow.setFieldDropdownValue("维修类型", "setRepairForm", repairFormList, map, StringUtils.isEmpty(asset.getUid()) ? 1 : null); //新增资产默认内修
                assetRow.setFieldStringValue("设备型号", "setFunctionType");
                assetRow.setFieldStringValue("制造商序列号", "setSerialNum");
                assetRow.setFieldStringValue("SystemID/厂商报修ID", "setSystemId");
                assetRow.setFieldStringValue("条码", "setBarcode");
                assetRow.setFieldStringValue("2010国家编码", "setNationalStandardCode");
                assetRow.setFieldStringValue("IT系统编号", "setModalityId");
                assetRow.setFieldDoubleValue("采购价格", "setPurchasePrice");
                assetRow.setFieldDoubleValue("最终残值", "setSalvageValue");
                assetRow.setFieldDoubleValue("使用年限", "setLifecycleInYear");

                assetRow.setFieldDropdownValue("折旧算法", "setDepreciationMethod", depreciationMethod, map, null);

                assetRow.setFieldDateValue("出厂日期", "setManufactDate");
                assetRow.setFieldDateValue("采购日期", "setPurchaseDate");
                assetRow.setFieldDateValue("到货日期", "setArriveDate");
                assetRow.setFieldDateValue("启用日期", "setInstallDate");
                assetRow.setFieldDateValue("质保日期", "setFactoryWarrantyDate");
                assetRow.setFieldDateValue("维保截止日期", "setWarrantyDate");
                assetRow.setFieldDateValue("报废日期", "setTerminateDate");
                assetRow.setFieldDateValue("上次保养日期", "setLastPmDate");
                assetRow.setFieldDateValue("上次计量日期", "setLastMeteringDate");
                assetRow.setFieldDateValue("上次巡检日期", "setLastQaDate");

                assetRow.setFieldStringValue("维修商", "setMaitanance");
                assetRow.setFieldStringValue("维修商电话", "setMaitananceTel");
                assetRow.setFieldStringValue("安装位置编码", "setLocationCode");
                assetRow.setFieldStringValue("安装位置", "setLocationName");
                assetRow.setFieldStringValue("使用责任人", "setClinicalOwnerName");
                assetRow.setFieldStringValue("备注", "setComments");

                assetRow.setFieldDropdownValue("用途分类", "setAssetUsage", assetUsageList, map, null);
                assetRow.setFieldDropdownValue("资金来源", "setCapitalSource", capitalSourceList, map, null);
                assetRow.setFieldDropdownValue("风险等级", "setRiskGrade", riskGradeList, map, null);
                assetRow.setFieldDropdownValue("计量等级", "setMeasureGrade", measureGradeList, map, null);
                assetRow.setFieldDropdownValue("计量项目类别", "setMeasuringItemCategory", measuringItemCategoryList, map, null);
                assetRow.setFieldDropdownValue("是否急救设备", "setEmergencyEquipment", emergencyEquipmentList, map, null);
                assetRow.setFieldDropdownValue("是否进口设备", "setImportEquipment", importEquipmentList, map, null);
                assetRow.setFieldDropdownValue("当前状态", "setStatus", assetStatusList, map, 1);
                assetRow.setFieldDropdownValue("核算类别", "setAccountingCategory", accountingCategoryList, map, null);
                assetRow.setFieldBooleanValue("是否PM", "setIsPm", true);
                assetRow.setFieldBooleanValue("是否盘点", "setIsChecked", true);



                if (StringUtils.isEmpty(asset.getUid())) {

                    //only new asset need to set this
                    asset.setUsageStatus(3); //已建档

                    if(isNeedAudit){
                        asset.setUsageStatus(2); //待审核
                    }

                } else {
                    supportingService.cleanRelatedFieldsWhenUpdating(asset);
                }

                supportingService.markDefautValue(selectedOrgToImport, asset, now);

                map.put("asset", asset);
                map.put("status", StringUtils.isEmpty(asset.getUid()) ? ImportStatus.New : ImportStatus.Exist);
                map.put("rowData", rowMap);

                result.add(map);

            }catch(Exception e){
                throw new Exception("第"+dataRow+"行数据错误:" + e.getMessage());
            }
        }

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getAssetMapFromTemplate: {} seconds", duration.getSeconds());

        return result;
    }


    public void importData(List<Map<String, Object>> importOrgMap, List<Map<String, Object>> importAssetMap,
                           Boolean autoGenerateQrcode, Boolean isGeneratePmOrder) {

        LocalDateTime startTime = LocalDateTime.now();

        importOrgMap.forEach(item -> {
            OrgInfo newOrgInfo = (OrgInfo) item.get("org");
            OrgInfo orgInfo = orgDao.findFirstByParentUIDAndName(selectedOrgToImport.getUid(), newOrgInfo.getShortName());

            if (orgInfo == null) {
                supportingService.importOrgIfNeeded(selectedOrgToImport, newOrgInfo.getShortName());
            }

            item.put("status", orgInfo == null ? ImportStatus.Created : ImportStatus.Exist);
        });

        int row = 0;
        try {

            for (Map<String, Object> item: importAssetMap) {
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
            OrgInfo orgInfo = supportingService.importOrgIfNeeded(selectedOrgToImport, orgName);
            newAsset.setClinicalDeptId(orgInfo.getId());
            newAsset.setClinicalDeptUID(orgInfo.getUid());
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

    public Boolean exportFailData(ExcelDocument doc, List<Map<String, Object>> importOrgMap, List<Map<String, Object>> importAssetMap) {
        Integer keyRowOrg = 4;
        Integer dataRowOrg = 5;
        Boolean hasFailData = false;
        doc.clearRowsData(SHEET_ORG, 5, 4 + importOrgMap.size());
        for (Map<String, Object> item : importOrgMap) {
            doc.writeRowData(SHEET_ORG, keyRowOrg, dataRowOrg++, (Map<String, Object>) item.get("rowData"));
            hasFailData = item.get("status").equals(ImportStatus.Failure);
        }

        Integer keyRowAsset = 6;
        Integer dataRowAsset = 7;
        doc.clearRowsData(SHEET_ASSET, 7, 6 + importAssetMap.size());
        for (Map<String, Object> item : importAssetMap) {
            if (item.get("status").equals(ImportStatus.Failure)) {
                doc.writeRowData(SHEET_ASSET, keyRowAsset, dataRowAsset++, (Map<String, Object>) item.get("rowData"));
                hasFailData = true;
            }
        }

        return hasFailData;
    }

    public Boolean checkData(List<Map<String, Object>> importOrgMap, List<Map<String, Object>> importAssetMap) {

        LocalDateTime startTime = LocalDateTime.now();

        HashSet<Boolean> result = new HashSet<>();
        importOrgMap.forEach(item -> {
            OrgInfo newOrgInfo = (OrgInfo) item.get("org");

            OrgInfo orgInfo = orgDao.findFirstByParentUIDAndName(selectedOrgToImport.getUid(), newOrgInfo.getName());

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

    public boolean checkDocVersion(ExcelDocument doc) {
        String inputVersion = ExcelDocument.getCellStringValue(doc.getCellValue(SHEET_ASSET, 2, 3));
        return EXCEL_ASSET_TEMPLATE_VERSION.equalsIgnoreCase(inputVersion);
    }

    public void setSelectedOrgToImport(OrgInfo selectedOrgToImport) {
        this.selectedOrgToImport = selectedOrgToImport;

        workflowConfig = configService.findConfigBySite(selectedOrgToImport.getSiteUID());

        if (workflowConfig != null) {
            assetCodeConfig = workflowConfig.getAssetCodeConfig();
        }

        isNeedAudit = configService.isAssetNeedAudit(selectedOrgToImport.getSiteUID());

    }

}
