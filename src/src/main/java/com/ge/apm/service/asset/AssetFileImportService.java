/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
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
@Component
@Scope("prototype")
public class AssetFileImportService {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileImportService.class);

    public static final String EXCEL_ASSET_TEMPLATE_VERSION = "v2.3";

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
    private SupplierRepository supplierDao;
    @Autowired
    private QrCodeLibRepository qrcodeDao;
    @Autowired
    private AssetCreateService assetCreateService;
    @Autowired
    private QrCodeService qrCodeService;
    @Autowired
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;
    @Autowired
    ConfigService configService;

    private OrgInfo selectedOrgToImport;

    private String assetCodeConfig;
    private static final String ASSET_CODE_MERGE_THREE = "DFQ";
    private static final String ASSET_CODE_MERGE_TWO = "DF";

    private final String OLD_QRCODE_KEY = "old_qrcode";
    private final String NEW_FINANCE_NUM_KEY = "new_finance_num";
    private WorkflowConfig workflowConfig;

    public Map<String, Map<String, Object>> getOrgInfoMapFromTemplate(ExcelDocument doc) {

        Map<String, Map<String, Object>> result = new LinkedHashMap();
        int keyRow = 4;
        int dataRow = 5;

        for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_ORG, keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(SHEET_ORG, keyRow, ++dataRow)) {
            OrgInfo org = new OrgInfo();

            String orgName = ExcelDocument.getCellStringValue(rowMap.get("部门名称"));

            if (orgName == null || orgName.isEmpty() || orgName.equals(AssetRow.DELETE_MARK)) {
                break;
            }

            org.setName(orgName);
            org.setHospitalId(selectedOrgToImport.getId());
            org.setParentOrg(selectedOrgToImport);
            org.setSiteId(selectedOrgToImport.getSiteId());

            org.setParentUID(selectedOrgToImport.getUid());
            org.setHospitalUID(selectedOrgToImport.getHospitalUID());
            org.setTenantUID(selectedOrgToImport.getTenantUID());
            org.setInstitutionUID(selectedOrgToImport.getInstitutionUID());
            org.setSiteUID(selectedOrgToImport.getSiteUID());
            org.setOrgLevel(selectedOrgToImport.getOrgLevel());
            org.setOrgType(4);

            org.setLineNum(dataRow);

            Map<String, Object> map = new LinkedHashMap();
            map.put("org", org);
            map.put("status", ImportStatus.New);
            map.put("rowData", rowMap);
            result.put(orgName, map);
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

                boolean isNewAsset = true; //flag an asset is new or existing.

                AssetInfo asset = new AssetInfo();

                String uuid = ExcelDocument.getCellStringValue(rowMap.get("UUID"));
                if (!StringUtils.isEmpty(uuid)) {
                    asset.setUid(uuid);
                }

                String assetName = ExcelDocument.getCellStringValue(rowMap.get("设备名称"));
                if (StringUtils.isEmpty(assetName)) {
                    break; //return if go beyond the valid cell range
                }

                //keep the new finance num to prevent lose its data to use later
                String financingNum = ExcelDocument.getCellStringValue(rowMap.get("资产编号"));
                map.put(NEW_FINANCE_NUM_KEY, financingNum);

                //load asset if it is already existed in db, then update the record
                List<AssetInfo> assetList = getAssetInfos(asset);
                if (assetList != null && assetList.size() == 1) {
                    asset = assetList.get(0);
                    isNewAsset = false;
                    map.put(OLD_QRCODE_KEY, asset.getQrCode());
                }

                asset.setLineNum(dataRow);

                String inputQrcode = ExcelDocument.getCellStringValue(rowMap.get("二维码"));
                if (!StringUtils.isEmpty(inputQrcode)) {
                    asset.setQrCode(inputQrcode.replace("-", ""));
                }

                AssetRow assetRow = new AssetRow(asset, isNewAsset, rowMap);
                assetRow.setFieldStringValue("设备名称", "setName");
                assetRow.setFieldStringValue("设备编号", "setDepartNum");
                assetRow.setFieldStringValue("资产编号", "setFinancingNum");
                assetRow.setFieldStringValue("设备别名", "setAliasName");
                assetRow.setFieldStringValue("维修负责人", "setAssetOwnerName");
                assetRow.setFieldStringValue("维修负责人2", "setAssetOwnerName2");
                assetRow.setFieldStringValue("维修负责人3", "setAssetOwnerName3");
                assetRow.setFieldStringValue("使用科室", "setClinicalDeptName");
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
                assetRow.setFieldDropdownValue("维修类型", "setRepairForm", repairFormList, map, null);
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



                if (isNewAsset) {

                    //only new asset need to set this
                    asset.setUsageStatus(3); //已建档

                    boolean isNeedAudit = configService.isAssetNeedAudit(selectedOrgToImport.getSiteUID());
                    if(isNeedAudit){
                        asset.setUsageStatus(2); //待审核
                    }

                    asset.setSiteId(selectedOrgToImport.getSiteId());
                    asset.setHospitalId(selectedOrgToImport.getId());

                    asset.setTenantUID(selectedOrgToImport.getTenantUID());
                    asset.setHospitalUID(selectedOrgToImport.getHospitalUID());
                    asset.setInstitutionUID(selectedOrgToImport.getInstitutionUID());
                    asset.setSiteUID(selectedOrgToImport.getSiteUID());

                    asset.setIsValid(true);
                    asset.setDeviceStatus(0);
                    asset.setIsDeleted(Boolean.FALSE);

                    asset.setCreatedBy(UserContextService.getCurrentUserAccount().getLoginName());
                    asset.setCreatedDate(now);
                    asset.setLastModifiedBy(UserContextService.getCurrentUserAccount().getLoginName());
                    asset.setLastModifiedDate(now);

                } else {
                    cleanRelatedFieldsWhenUpdating(asset);

                    asset.setLastModifiedBy(UserContextService.getCurrentUserAccount().getLoginName());
                    asset.setLastModifiedDate(now);
                }

                map.put("asset", asset);
                map.put("status", isNewAsset ? ImportStatus.New : ImportStatus.Exist);
                map.put("rowData", rowMap);

                result.add(map);

            }catch(Exception e){
                throw new Exception("第"+dataRow+"行数据错误" + e.getMessage());
            }
        }

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getAssetMapFromTemplate: {} seconds", duration.getSeconds());

        return result;
    }

    private void cleanRelatedFieldsWhenUpdating(AssetInfo asset) {
        if (asset.getAssetOwnerName() == null) {
            asset.setAssetOwnerId(null);
            asset.setAssetOwnerTel(null);
        }
        if (asset.getAssetOwnerName2() == null) {
            asset.setAssetOwnerId2(null);
            asset.setAssetOwnerTel2(null);
        }
        if (asset.getAssetOwnerName3() == null) {
            asset.setAssetOwnerId3(null);
            asset.setAssetOwnerTel3(null);
        }
        if (asset.getClinicalOwnerName() == null) {
            asset.setClinicalOwnerId(null);
            asset.setClinicalOwnerTel(null);
        }
        if (asset.getClinicalDeptName() == null) {
            asset.setClinicalDeptId(null);
            asset.setClinicalDeptUID(null);
        }
        if (asset.getVendor() == null) {
            asset.setSupplierId(null);
        }

    }

    public void importData(Map<String, Map<String, Object>> importOrgMap, List<Map<String, Object>> importAssetMap,
                           Boolean autoGenerateQrcode, Boolean isGeneratePmOrder) {

        LocalDateTime startTime = LocalDateTime.now();

        importOrgMap.values().forEach(item -> {
            OrgInfo newOrgInfo = (OrgInfo) item.get("org");
            List<OrgInfo> orgs = getOrgInfoByName(newOrgInfo);
            if (null == orgs || orgs.isEmpty()) {
                orgDao.save((OrgInfo) item.get("org"));
                item.put("status", ImportStatus.Created);
            } else if (orgs.size() > 1) {
                item.put("status", ImportStatus.Failure);
                AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage("存在重名的") + WebUtil.getMessage("OrgInfo") + ":" + ((OrgInfo) item.get("org")).getName());
            } else {
                OrgInfo existingOrgInfo = orgs.get(0);
                existingOrgInfo.setLineNum(newOrgInfo.getLineNum());
                item.put("org", existingOrgInfo);
                item.put("status", ImportStatus.Exist);
            }
        });

        //release qrcode in use in order to exchange qrcode in two different asset
        releaseQRCodeInUse(importAssetMap);

        importAssetMap.forEach(item -> {
            AssetInfo newAsset = (AssetInfo) item.get("asset");

            //if site uid not match, ignore update
            if (newAsset.getUid() != null) {
                if (!selectedOrgToImport.getUid().equals(newAsset.getSiteUID())) {
                    return ;
                }

            }

            //check depart num to avoid duplicate when update
            boolean isDuplicate = isAssetDepartNumDuplicate(newAsset);
            if (isDuplicate) {
                item.put("status", ImportStatus.Failure);
                AssetRow.addErrMessage(item, "错误: 设备编号重复");
                return;
            }

            String orgName = newAsset.getClinicalDeptName();
            if (StringUtils.isEmpty(orgName)) {
                item.put("status", ImportStatus.Failure);
                AssetRow.addErrMessage(item, "警告:科室为空");
            } else if (importOrgMap.containsKey(orgName)) {
                OrgInfo clinicalDept = (OrgInfo) importOrgMap.get(orgName).get("org");
                if (clinicalDept.getId() == null || clinicalDept.getId() <= 0) {
                    item.put("status", ImportStatus.Failure);
                    AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("OrgInfo") + orgName + "无法关联");
                    newAsset.setClinicalDeptName(null);
                } else {
                    newAsset.setClinicalDeptName(orgName);
                    newAsset.setClinicalDeptId(clinicalDept.getId());
                    newAsset.setClinicalDeptUID(clinicalDept.getUid());
                }
            } else {
                item.put("status", ImportStatus.Failure);
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("OrgInfo") + orgName + "不在列表页中");
                newAsset.setClinicalDeptName(null);
            }

            //for AssetOwner
            List<UserAccount> userList = getUserInfosByName(newAsset.getAssetOwnerName());
            if (userList != null && userList.size() == 1) {
                UserAccount assetOwner = userList.get(0);
                newAsset.setAssetOwnerId(assetOwner.getId());
                newAsset.setAssetOwnerTel(assetOwner.getTelephone());
            } else {
                if (newAsset.getAssetOwnerName() == null) {
                    AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName") + "未设置");
                } else {
                    AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName") + newAsset.getAssetOwnerName() + "无此用户/无法关联");
                }
                newAsset.setAssetOwnerName(null);
            }
            //for AssetOwner2
            userList = getUserInfosByName(newAsset.getAssetOwnerName2());
            if (userList != null && userList.size() == 1) {
                UserAccount assetOwner = userList.get(0);
                newAsset.setAssetOwnerId2(assetOwner.getId());
                newAsset.setAssetOwnerTel2(assetOwner.getTelephone());
            } else {
                if (newAsset.getAssetOwnerName2() == null) {
                    AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName2") + "未设置");
                } else {
                    AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName2") + newAsset.getAssetOwnerName2() + "无此用户/无法关联");
                }
                newAsset.setAssetOwnerName2(null);
            }

            //for AssetOwner3
            userList = getUserInfosByName(newAsset.getAssetOwnerName3());
            if (userList != null && userList.size() == 1) {
                UserAccount assetOwner = userList.get(0);
                newAsset.setAssetOwnerId3(assetOwner.getId());
                newAsset.setAssetOwnerTel3(assetOwner.getTelephone());
            } else {
                if (newAsset.getAssetOwnerName3() == null) {
                    AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("assetOwnerName3") + "未设置");
                } else {
                    AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("assetOwnerName3") + newAsset.getAssetOwnerName3() + "无此用户/无法关联");
                }
                newAsset.setAssetOwnerName3(null);
            }

            //for ClinicalOwner
            userList = getUserInfosByName(newAsset.getClinicalOwnerName());
            if (userList != null && userList.size() == 1) {
                UserAccount clinicalOwner = userList.get(0);
                newAsset.setClinicalOwnerId(clinicalOwner.getId());
                newAsset.setClinicalOwnerTel(clinicalOwner.getTelephone());
            } else {
                if (newAsset.getClinicalOwnerName() == null) {
                    AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("ClinicalOwnerName") + "未设置");
                } else {
                    AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("ClinicalOwnerName") + newAsset.getClinicalOwnerName() + "无此用户/无法关联");
                }
                newAsset.setClinicalOwnerName(null);
            }

            if (newAsset.getVendor() != null && !newAsset.getVendor().isEmpty()) {
                List<Supplier> supplierList = getSupplierListByName(newAsset.getVendor());
                if (null == supplierList || supplierList.isEmpty()) {
                    Supplier newSupplier = new Supplier();
                    newSupplier.setSiteId(selectedOrgToImport.getSiteId());
                    newSupplier.setName(newAsset.getVendor());
                    newSupplier.setTenantUID(selectedOrgToImport.getTenantUID());
                    newSupplier.setHospitalUID(selectedOrgToImport.getHospitalUID());
                    newSupplier.setInstitutionUID(selectedOrgToImport.getInstitutionUID());
                    newSupplier.setSiteUID(selectedOrgToImport.getSiteUID());
                    supplierDao.save(newSupplier);
                    newAsset.setSupplierId(newSupplier.getId());
                    newAsset.setVendor(newAsset.getVendor());
                } else if (supplierList.size() == 1) {
                    Supplier supplier = supplierList.get(0);
                    newAsset.setSupplierId(supplier.getId());
                    newAsset.setVendor(supplier.getName());
                } else {
                    AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("Vendor") + (String) item.get("superlierName") + "无法关联");
                    newAsset.setVendor(null);
                }
            }

            //auto generate financing num
            String financingNum = newAsset.getFinancingNum();
            String nationalStandardCode = newAsset.getNationalStandardCode();

            if (needGenerateFinancingNum(newAsset)) {

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

            String qrCode = newAsset.getQrCode();

            List<AssetInfo> assetList = getAssetInfos(newAsset);
            if (null == assetList || assetList.isEmpty()) {

                if (ASSET_CODE_MERGE_THREE.equals(assetCodeConfig)) {
                    String qrCodeStatus = qrCodeService.isAvailableQrcode(qrCode, selectedOrgToImport.getUid());
                    if (!"Normal".equals(qrCodeStatus)) {
                        item.put("status", ImportStatus.Failure);
                        AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage(qrCodeStatus));
                    }

                    if(createAsset(newAsset,isGeneratePmOrder)){
                        item.put("status", ImportStatus.Created);
                    }else{
                        item.put("status", ImportStatus.Failure);
                    }

                } else {

                    if ((qrCode == null || qrCode.isEmpty()) && autoGenerateQrcode) {
                        QrCodeLib newCode = qrCodeService.createOneQrCode(selectedOrgToImport, 3);
                        newAsset.setQrCode(newCode.getQrCode());
                        if(createAsset(newAsset,isGeneratePmOrder)){
                            item.put("status", ImportStatus.Created);
                        }else{
                            item.put("status", ImportStatus.Failure);
                        }
                    } else if (isAvailableQrcode(qrCode, item)) {
                        if(createAsset(newAsset,isGeneratePmOrder)){
                            item.put("status", ImportStatus.Created);
                        }else{
                            item.put("status", ImportStatus.Failure);
                        }
                    } else {
                        item.put("status", ImportStatus.Failure);
//                    addErrMessage(item, WebUtil.getMessage("InvalidParameter") +WebUtil.getMessage("qrCode")+ qrCode);
                    }
                }


            } else if (assetList.size() > 1) {
                item.put("status", ImportStatus.Failure);
                AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage("存在重复的") + WebUtil.getMessage("AssetInfo") + newAsset.getName() + "/" + newAsset.getDepartNum() + "/" + newAsset.getFinancingNum());
            } else {

                if (StringUtils.isEmpty(qrCode)) {
                    if (autoGenerateQrcode) {
                        QrCodeLib newCode = qrCodeService.createOneQrCode(selectedOrgToImport, 3);
                        newAsset.setQrCode(newCode.getQrCode());
                    }

                } else {
                    String oldQrCode = (String)item.get(OLD_QRCODE_KEY);
                    if (!qrCode.equals(oldQrCode)) {

                        boolean qrCodeUpdated = qrCodeService.updateExistingQrCodeNotUsed(qrCode, selectedOrgToImport);
                        if (!qrCodeUpdated) {
                            item.put("status", ImportStatus.Failure);
                            AssetRow.addErrMessage(item, "错误: 无效二维码");
                            return;
                        }

                    }
                }

                item.put("status", ImportStatus.Exist);
                assetDao.save(newAsset);

            }

        });

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());

    }

    private Boolean isAvailableQrcode(String qrcode, Map<String, Object> item) {
        if (null == qrcode || qrcode.isEmpty()) {
            return true;
        }

        QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(qrcode,selectedOrgToImport.getUid());
        if (qrCodeLib == null) {
            AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage("InvalidQRCode") + qrcode);
            return false;
        }
        if (!qrCodeLib.getSiteUID().equals(selectedOrgToImport.getUid())) {
            AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage("WrongHospitalQRCode"));
            return false;
        }
        if (qrCodeLib.getStatus() != 1) {
            AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage("AlreadyUsingQrCode"));
            return false;
        }

        return true;
    }

    public Boolean exportFailData(ExcelDocument doc, Map<String, Map<String, Object>> importOrgMap, List<Map<String, Object>> importAssetMap) {
        Integer keyRowOrg = 4;
        Integer dataRowOrg = 5;
        Boolean hasFailData = false;
        doc.clearRowsData(SHEET_ORG, 5, 4 + importOrgMap.values().size());
        for (Map<String, Object> item : importOrgMap.values()) {
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

    @Transactional
    Boolean createAsset(AssetInfo asset, Boolean isGeneratePmOrder) {
        if(!assetCreateService.createAsset(asset,isGeneratePmOrder)){
            return false;
        }
        if (null != asset.getQrCode() && !asset.getQrCode().isEmpty()) {
            QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(asset.getQrCode(),asset.getSiteUID());
            if (qrCodeLib != null) {
                qrCodeLib.setStatus(3);
                qrcodeDao.save(qrCodeLib);
            }
        }
        return true;
    }

    private List<OrgInfo> getOrgInfoByName(OrgInfo org) {
        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedOrgToImport.getUid()));
        OrgInfoFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUID));
        OrgInfoFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, org.getName()));
        return orgDao.findBySearchFilter(OrgInfoFilters);
    }

    private List<AssetInfo> getAssetInfos(AssetInfo asset) {


        if (!StringUtils.isEmpty(asset.getUid())) {
            List<SearchFilter> assetFilters = new ArrayList<>();
            assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.EQ, asset.getUid()));
            assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUID));
            List<AssetInfo> tempList = assetDao.findBySearchFilter(assetFilters);
            return tempList;
        } else {
            return Collections.emptyList();

        }


    }

    private boolean isAssetDepartNumDuplicate(AssetInfo asset) {

        if (StringUtils.isEmpty(asset.getDepartNum())) {
            return false;
        }

        List<SearchFilter> assetFilters = new ArrayList<>();
        String orgUid = selectedOrgToImport.getUid();

        assetFilters.add(new SearchFilter("departNum", SearchFilter.Operator.EQ, asset.getDepartNum()));
        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, orgUid));

        if (null != asset.getUid()) {
            assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.NE, asset.getUid()));
        }

        List<AssetInfo> resultList = assetDao.findBySearchFilter(assetFilters);
        if (resultList.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    private List<UserAccount> getUserInfosByName(String userName) {
        List<SearchFilter> userFilters = new ArrayList<>();
//        userFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, orgUid));
        userFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUID));
        userFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, userName));
        return userDao.findBySearchFilter(userFilters);
    }

    private List<Supplier> getSupplierListByName(String supplierName) {
        List<SearchFilter> supplierFilters = new ArrayList<>();
        supplierFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUID));
        supplierFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, supplierName));
        return supplierDao.findBySearchFilter(supplierFilters);
    }

    public Boolean checkData(Map<String, Map<String, Object>> importOrgMap, List<Map<String, Object>> importAssetMap) {

        LocalDateTime startTime = LocalDateTime.now();

        HashSet<Boolean> result = new HashSet<>();
        importOrgMap.values().forEach(item -> {
            OrgInfo newOrgInfo = (OrgInfo) item.get("org");
            List<OrgInfo> orgs = getOrgInfoByName(newOrgInfo);
            if (null == orgs || orgs.isEmpty()) {
                item.put("status", ImportStatus.New);
            } else if (orgs.size() > 1) {
                item.put("status", ImportStatus.CheckFail);
                AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage("存在重复的") + WebUtil.getMessage("OrgIngo") + ((OrgInfo) item.get("org")).getName());
                result.add(Boolean.FALSE);
            } else {
                OrgInfo existingOrgInfo = orgs.get(0);
                existingOrgInfo.setLineNum(newOrgInfo.getLineNum());
                item.put("org", existingOrgInfo);
                item.put("status", ImportStatus.Exist);
            }
        });

        int row = 0;
        for (Map<String, Object> item: importAssetMap) {
            ++row;

            AssetInfo newAsset = (AssetInfo) item.get("asset");

            List<AssetInfo> assetList = getAssetInfos(newAsset);
            if (null == assetList || assetList.isEmpty()) {

                if (!StringUtils.isEmpty(newAsset.getUid())) {
                    //检查是否输入非法UUID
                    item.put("status", ImportStatus.CheckFail);
                    result.add(Boolean.FALSE);
                    AssetRow.addErrMessage(item, String.format("第%d行错误: 非法UUID！", row));

                } else {
                    String qrCode = newAsset.getQrCode();
                    if (isAvailableQrcode(qrCode, item)) {
                        item.put("status", ImportStatus.New);
                    } else {
                        item.put("status", ImportStatus.CheckFail);
                        result.add(Boolean.FALSE);
//                    addErrMessage(item, WebUtil.getMessage("InvalidParameter") +WebUtil.getMessage("qrCode")+ qrCode);
                    }
                }

            } else if (assetList.size() > 1) {
                item.put("status", ImportStatus.CheckFail);
                result.add(Boolean.FALSE);
                AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage("存在重复的") + WebUtil.getMessage("AssetInfo") + newAsset.getName() + "/" + newAsset.getDepartNum() + "/" + newAsset.getFinancingNum());
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

            //check depart num to avoid duplicate when update
            boolean isDuplicate = isAssetDepartNumDuplicate(newAsset);
            if (isDuplicate) {
                item.put("status", ImportStatus.CheckFail);
                result.add(Boolean.FALSE);
                AssetRow.addErrMessage(item, "错误: 设备编号重复");
            }

            //配置了三码合一和设备编号不为空，则检查，Jira: YHAAO-1594
            if ((ASSET_CODE_MERGE_THREE.equals(assetCodeConfig) || ASSET_CODE_MERGE_TWO.equals(assetCodeConfig)) && !StringUtils.isEmpty(newAsset.getDepartNum())) {
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

                    if (ASSET_CODE_MERGE_THREE.equals(assetCodeConfig)) {
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

    /**
     * export asset based on the asset import excel template
     * @param assetList
     * @return
     * @throws IOException
     */
    public ExcelDocument exportAsset(List<AssetInfo> assetList, List<OrgInfo> sites, List<OrgInfo> clinicalDeptList, List<String> exportFields) throws IOException {

        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/assetImport/APM_Asset_Template.xlsx");
        ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
        //fix template version
        doc.writeCellData(AssetFileImportService.SHEET_ASSET, 2, 3, AssetFileImportService.EXCEL_ASSET_TEMPLATE_VERSION);
        //remove fake data row
        doc.clearRow(AssetFileImportService.SHEET_ASSET, AssetFileImportService.EXCEL_ASSET_DATA_ROW);
        
        String[] columnKeys = doc.getKeyRow(AssetFileImportService.SHEET_ASSET, AssetFileImportService.EXCEL_ASSET_HEADER_ROW);
        List<String> fields = Arrays.asList(columnKeys).stream().filter(item-> !CustomConfigConstant.getExportFields().containsKey(item) || 
                exportFields.contains(CustomConfigConstant.getExportFields().get(item))).collect(Collectors.toList());
        doc.clearRow(AssetFileImportService.SHEET_ASSET, AssetFileImportService.EXCEL_ASSET_HEADER_ROW);
        for(int i=0;i<fields.size();i++){
            doc.writeCellData(AssetFileImportService.SHEET_ASSET, AssetFileImportService.EXCEL_ASSET_HEADER_ROW, i+1, fields.get(i));
        }
        
        columnKeys = doc.getKeyRow(AssetFileImportService.SHEET_ASSET, AssetFileImportService.EXCEL_ASSET_HEADER_ROW);

        int startRow = AssetFileImportService.EXCEL_ASSET_DATA_ROW;
        for (AssetInfo assetInfo: assetList) {
            AssetInfoFormatter formatter = new AssetInfoFormatter(assetInfo,  sites ,this);
            Map<String, Object> rowMap = formatter.buildExcelColumnKeyValueMap(true);

            doc.writeRowData(AssetFileImportService.SHEET_ASSET, columnKeys, startRow, rowMap);

            ++startRow;
        }

        String countFormula = String.format("COUNTA('Asset List'!$B$7:$B$%d)", assetList.size() + 6);

        String sumFormula = String.format("SUM('Asset List'!$Z$7:$Z$%d)", assetList.size() + 6);

        doc.writeCellFormula(AssetFileImportService.SHEET_ASSET, 5, 1, countFormula);
        doc.writeCellFormula(AssetFileImportService.SHEET_ASSET, 5, 2, sumFormula);
        doc.getWorkBook().setForceFormulaRecalculation(true);
        doc.calculateFormula();

        //export org info list
        for (startRow = AssetFileImportService.EXCEL_ORG_DATA_ROW; startRow < 20; startRow++) {
            doc.clearRow(AssetFileImportService.SHEET_ORG, startRow);
        }

        startRow = AssetFileImportService.EXCEL_ORG_DATA_ROW;
        for (OrgInfo orgInfo: clinicalDeptList) {
            doc.writeCellData(AssetFileImportService.SHEET_ORG, startRow, 1, orgInfo.getName());

            ++startRow;
        }
        doc.writeCellData(AssetFileImportService.SHEET_ORG, startRow, 1, "#");

        return doc;

    }

    private void releaseQRCodeInUse(List<Map<String, Object>> importAssetMap) {

        importAssetMap.forEach(item -> {
            AssetInfo newAsset = (AssetInfo) item.get("asset");

            //if site uid not match, ignore update
            if (newAsset.getUid() != null) {
                if (!selectedOrgToImport.getUid().equals(newAsset.getSiteUID())) {
                    return ;
                }

            }

            String oldQrCode = (String)item.get(OLD_QRCODE_KEY);
            if (StringUtils.isEmpty(oldQrCode)) return;
            if (oldQrCode.equals(newAsset.getQrCode())) return;  //qrCode not changed

            boolean releaseable = true;
            if (!StringUtils.isEmpty(newAsset.getQrCode())) {
                boolean exists = qrCodeService.isQrcodeAvailableInStatus(newAsset.getQrCode(), selectedOrgToImport.getUid(), 1);
                if (!exists) {
                    releaseable = false;
                }
            }

            if (releaseable) {
                qrCodeService.releaseQrCodeInUse(oldQrCode, selectedOrgToImport.getUid());
            }

        });

    }

    public void setSelectedOrgToImport(OrgInfo selectedOrgToImport) {
        this.selectedOrgToImport = selectedOrgToImport;

        workflowConfig = configService.findConfigBySite(selectedOrgToImport.getSiteUID());

        if (workflowConfig != null) {
            assetCodeConfig = workflowConfig.getAssetCodeConfig();
        }
    }

    private boolean needGenerateFinancingNum(AssetInfo assetInfo) {

        final int GENERATE_BY_NATIONAL_STANDARD_CODE = 1;
        final int GENERATE_BY_PREFIX_AND_SEQ = 2;

        if (workflowConfig == null) return false;

        assetCodeConfig = workflowConfig.getAssetCodeConfig();

        if (ASSET_CODE_MERGE_THREE.equals(assetCodeConfig) || ASSET_CODE_MERGE_TWO.equals(assetCodeConfig)) {
            return false;
        }

        if (workflowConfig != null && workflowConfig.getGenerateMethod() != null) {
            String financingNum = assetInfo.getFinancingNum();

            if (workflowConfig.getGenerateMethod() == GENERATE_BY_NATIONAL_STANDARD_CODE) {

                if (assetInfo.getUid() == null && !StringUtils.isEmpty(assetInfo.getNationalStandardCode()) && (StringUtils.isEmpty(financingNum) || financingNum.length() < 5)) {
                    return true;
                }

            } else if (workflowConfig.getGenerateMethod() == GENERATE_BY_PREFIX_AND_SEQ) {
                if (assetInfo.getUid() == null && (!StringUtils.isEmpty(financingNum) && financingNum.length() < 5)) {
                    return true;
                }
            }
        }

        return false;
    }

}
