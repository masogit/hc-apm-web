package com.ge.apm.domain.pure;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetFileImportBaseService;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.web.WebUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetInfoFormatter {

    private AssetInfo assetInfo;
    private List<OrgInfo> sites;
    private AssetFileImportBaseService importService;
    private AssetInfoRepository assetDao;
//    private OrgInfoRepository orgDao;
//    private I18nMessageRepository i18nDao;
    private int siteId;

    public AssetInfoFormatter(AssetInfo assetInfo, List<OrgInfo> sites,
                              AssetFileImportBaseService importService,
                              AssetInfoRepository assetDao,
                              OrgInfoRepository orgDao) {
        this.assetInfo = assetInfo;
        this.sites = sites;
        this.importService = importService;
        this.assetDao = assetDao;
//        this.orgDao = orgDao;
//        this.i18nDao = i18nDao;
        this.siteId = UserContextService.getCurrentUserAccount().getSiteId();
    }

    public Integer getAssetUsage() {
        return assetInfo.getAssetUsage();
    }

    public String getAssetUsageAsString() {

        if (assetInfo.getAssetUsage() == null) return null;

        String value = findMatchValue(assetInfo.getAssetUsage(), importService.assetUsageList);

        return value;
    }

    public Integer getCapitalSource() {
        return assetInfo.getCapitalSource();
    }

    public String getCapitalSourceAsString() {

        if (assetInfo.getCapitalSource() == null) return null;

        String value = findMatchValue(assetInfo.getCapitalSource(), importService.capitalSourceList);

        return value;
    }

    public String getCapitalSource2AsString() {

        if (assetInfo.getCapitalSource2() == null) return null;

        I18nMessage msg = I18nMessageService.getMessageUsingDefaultConfigAsLowestPriority("capitalSource2", String.valueOf(assetInfo.getCapitalSource2()), siteId);

        if (msg != null) {
            return msg.getValueZh();
        }

        return null;
    }

    public Integer getRiskGrade() {
        return assetInfo.getRiskGrade();
    }

    public String getRiskGradeAsString() {

        if (assetInfo.getRiskGrade() == null) return null;

        String value = findMatchValue(assetInfo.getRiskGrade(), importService.riskGradeList);

        return value;
    }

    public Integer getMeasureGrade() {
        return assetInfo.getMeasureGrade();
    }

    public String getMeasureGradeAsString() {

        if (assetInfo.getMeasureGrade() == null) return null;

        String value = findMatchValue(assetInfo.getMeasureGrade(), importService.measureGradeList);

        return value;
    }

    public Integer getEmergencyEquipment() {
        return assetInfo.getEmergencyEquipment();
    }

    public String getEmergencyEquipmentAsString() {

        if (assetInfo.getEmergencyEquipment() == null) return null;

        String value = findMatchValue(assetInfo.getEmergencyEquipment(), importService.emergencyEquipmentList);

        return value;
    }

    public Integer getImportEquipment() {
        return assetInfo.getImportEquipment();
    }

    public String getImportEquipmentAsString() {

        if (assetInfo.getImportEquipment() == null) return null;

        String value = findMatchValue(assetInfo.getImportEquipment(), importService.importEquipmentList);

        return value;
    }

    public Integer getAssetOwnerId3() {
        return assetInfo.getAssetOwnerId3();
    }

    public String getAssetOwnerName3() {
        return assetInfo.getAssetOwnerName3();
    }

    public String getPmOwnerName() {
        return assetInfo.getPmOwnerName();
    }

    public String getAssetOwnerTel3() {
        return assetInfo.getAssetOwnerTel3();
    }

    public Integer getMeasuringItemCategory() {
        return assetInfo.getMeasuringItemCategory();
    }

    public String getMeasuringItemCategoryAsString() {

        if (assetInfo.getMeasuringItemCategory() == null) return null;

        String value = findMatchValue(assetInfo.getMeasuringItemCategory(), importService.measuringItemCategoryList);

        return value;
    }

    public Integer getUsageStatus() {
        return assetInfo.getUsageStatus();
    }

    public Integer getAccountingCategory() {
        return assetInfo.getAccountingCategory();
    }

    public String getAccountingCategoryAsString() {

        if (assetInfo.getAccountingCategory() == null) return null;

        String value = findMatchValue(assetInfo.getAccountingCategory(), importService.accountingCategoryList);

        return value;
    }

    public Integer getDeviceStatus() {
        return assetInfo.getDeviceStatus();
    }

    public String getDeviceStatusAsString() {
        if (assetInfo.getDeviceStatus() == null) return null;

        I18nMessage msg = I18nMessageService.getMessageUsingDefaultConfigAsLowestPriority("deviceStatus", String.valueOf(assetInfo.getDeviceStatus()), siteId);

        if (msg != null) {
            return msg.getValueZh();
        }

        return null;
    }

    public String getComments() {
        return assetInfo.getComments();
    }

    public String getTenantUID() {
        return assetInfo.getTenantUID();
    }

    public String getInstitutionUID() {
        return assetInfo.getInstitutionUID();
    }

    public String getHospitalUID() {
        return assetInfo.getHospitalUID();
    }

    public String getSiteUID() {
        return assetInfo.getSiteUID();
    }

    public String getSharedAssetTypeUid() {
        return assetInfo.getSharedAssetTypeUid();
    }

    public String getSwapTypeUid() {
        return assetInfo.getSwapTypeUid();
    }

    public String getSwapTypeState() {
        return assetInfo.getSwapTypeState();
    }

    public String getUid() {
        return assetInfo.getUid();
    }

    public Brand getBrand() {
        return assetInfo.getBrand();
    }

    public Integer getId() {
        return assetInfo.getId();
    }

    public int getSiteId() {
        return assetInfo.getSiteId();
    }

    public String getSiteName() {

        OrgInfo orgInfo = findDeptOrg(this.getSiteUID());
        if (orgInfo == null) return null;

        return orgInfo.getName();
    }

    public String getName() {
        return assetInfo.getName();
    }

    public String getAliasName() {
        return assetInfo.getAliasName();
    }

    public Integer getHospitalId() {
        return assetInfo.getHospitalId();
    }

    public OrgInfo getHospital() {
        return assetInfo.getHospital();
    }

    public Integer getFunctionGroup() {
        return assetInfo.getFunctionGroup();
    }

    public String getFunctionGroupAsString() {

        if (assetInfo.getFunctionGroup() == null) return null;

        String value = findMatchValue(assetInfo.getFunctionGroup(), importService.assetFunctionTypes);

        return value;

    }

    public String getFunctionType() {
        return assetInfo.getFunctionType();
    }

    public Integer getFunctionGrade() {
        return assetInfo.getFunctionGrade();
    }

    public String getManufacture() {
        return assetInfo.getManufacture();
    }

    public String getVendor() {
        return assetInfo.getVendor();
    }

    public String getMaitanance() {
        return assetInfo.getMaitanance();
    }

    public String getMaitananceTel() {
        return assetInfo.getMaitananceTel();
    }

    public String getNationalStandardCode() {
        return assetInfo.getNationalStandardCode();
    }

    public String getSerialNum() {
        return assetInfo.getSerialNum();
    }

    public String getDepartNum() {
        return assetInfo.getDepartNum();
    }

    public String getFinancingNum() {
        return assetInfo.getFinancingNum();
    }

    public String getBarcode() {
        return assetInfo.getBarcode();
    }

    public String getModalityId() {
        return assetInfo.getModalityId();
    }

    public String getLocationCode() {
        return assetInfo.getLocationCode();
    }

    public String getLocationName() {
        return assetInfo.getLocationName();
    }

    public Integer getClinicalDeptId() {
        return assetInfo.getClinicalDeptId();
    }

    public String getClinicalDeptName() {
        if (assetInfo.getClinicalDeptUID() == null) return null;

        OrgInfo orgInfo = findDeptOrg(assetInfo.getClinicalDeptUID());
        if (orgInfo == null) return null;

        return orgInfo.getName();
    }

    public Integer getAssetGroup() {
        return assetInfo.getAssetGroup();
    }

    public String getAssetGroupAsString() {
        if (assetInfo.getAssetGroup() == null) return null;

        String value = findMatchValue(assetInfo.getAssetGroup(), importService.assetGroup);

        return value;
    }

    public Integer getAssetDeptId() {
        return assetInfo.getAssetDeptId();
    }

    public String getAssetDeptUID() {
        return assetInfo.getAssetDeptUID();
    }

    public String getAssetDeptName() {
        if (assetInfo.getAssetDeptUID() == null) return null;
        OrgInfo orgInfo = findDeptOrg(assetInfo.getAssetDeptUID());
        if (orgInfo == null) return null;

        return orgInfo.getName();
    }

    public Integer getAssetOwnerId() {
        return assetInfo.getAssetOwnerId();
    }

    public String getAssetOwnerName() {
        return assetInfo.getAssetOwnerName();
    }

    public String getAssetOwnerTel() {
        return assetInfo.getAssetOwnerTel();
    }

    public boolean getIsValid() {
        return assetInfo.getIsValid();
    }

    public Integer getStatus() {
        return assetInfo.getStatus();
    }

    public String getStatusAsString() {

        if (assetInfo.getStatus() == null) return null;

        String value = findMatchValue(assetInfo.getStatus(), importService.assetStatusList);

        return value;
    }

    public Date getManufactDate() {
        return assetInfo.getManufactDate();
    }

    public Date getPurchaseDate() {
        return assetInfo.getPurchaseDate();
    }

    public Date getArriveDate() {
        return assetInfo.getArriveDate();
    }

    public Date getInstallDate() {
        return assetInfo.getInstallDate();
    }

    public Date getWarrantyDate() {
        return assetInfo.getWarrantyDate();
    }

    public Date getTerminateDate() {
        return assetInfo.getTerminateDate();
    }

    public Date getLastPmDate() {
        return assetInfo.getLastPmDate();
    }

    public Date getLastMeteringDate() {
        return assetInfo.getLastMeteringDate();
    }

    public Date getLastQaDate() {
        return assetInfo.getLastQaDate();
    }

    public Date getLastStockTakeDate() {
        return assetInfo.getLastStockTakeDate();
    }

    public Double getPurchasePrice() {
        return assetInfo.getPurchasePrice();
    }

    public Double getSalvageValue() {
        return assetInfo.getSalvageValue();
    }

    public Integer getLifecycle() {
        return assetInfo.getLifecycle();
    }

    public Integer getDepreciationMethod() {
        return assetInfo.getDepreciationMethod();
    }

    public String getDepreciationMethodAsString() {

        if (assetInfo.getDepreciationMethod() == null) return null;

        String value = findMatchValue(assetInfo.getDepreciationMethod(), importService.depreciationMethod);

        return value;
    }

    public Double getLifecycleInYear() {
        return assetInfo.getLifecycleInYear();
    }

    public String getRegistrationNo() {
        return assetInfo.getRegistrationNo();
    }

    public Date getFactoryWarrantyDate() {
        return assetInfo.getFactoryWarrantyDate();
    }

    public Integer getSupplierId() {
        return assetInfo.getSupplierId();
    }

    public String getQrCode() {
        return assetInfo.getQrCode();
    }

    public Integer getAssetOwnerId2() {
        return assetInfo.getAssetOwnerId2();
    }

    public String getAssetOwnerName2() {
        return assetInfo.getAssetOwnerName2();
    }

    public String getAssetOwnerTel2() {
        return assetInfo.getAssetOwnerTel2();
    }

    public Integer getFeUserId() {
        return assetInfo.getFeUserId();
    }

    public Integer getDispatchMode() {
        return assetInfo.getDispatchMode();
    }

    public Integer getClinicalOwnerId() {
        return assetInfo.getClinicalOwnerId();
    }

    public String getClinicalOwnerName() {
        return assetInfo.getClinicalOwnerName();
    }

    public String getClinicalOwnerTel() {
        return assetInfo.getClinicalOwnerTel();
    }

    public String getEamId() {
        return assetInfo.getEamId();
    }

    public String getSystemId() {
        return assetInfo.getSystemId();
    }

    public String getSystemNum1() {
        return assetInfo.getSystemNum1();
    }

    public String getSystemNum2() {
        return assetInfo.getSystemNum2();
    }

    public String getSystemNum3() {
        return assetInfo.getSystemNum3();
    }

    public String getSystemNum4() {
        return assetInfo.getSystemNum4();
    }

    public String getSystemNum5() {
        return assetInfo.getSystemNum5();
    }

    public Boolean getIsDeleted() {
        return assetInfo.getIsDeleted();
    }

    public Integer getParentAssetId() {
        return assetInfo.getParentAssetId();
    }

    public String getParentAssetName() {
        if (assetInfo.getParentAssetId() == null) {
            return null;
        }

        AssetInfo parentAsset = assetDao.getById(assetInfo.getParentAssetId());
        if (parentAsset == null) return null;

        String departNum = parentAsset.getDepartNum() == null ? "" : parentAsset.getDepartNum();
        String qrCode = parentAsset.getQrCode() == null ? "" : parentAsset.getQrCode();


        return parentAsset.getName()  + " / " + qrCode + " /" + departNum + " / " + parentAsset.getUid();
    }

    public Integer getInventoryStatus() {
        return assetInfo.getInventoryStatus();
    }

    public Integer getInventoryOwnerId() {
        return assetInfo.getInventoryOwnerId();
    }

    public Date getInventoryTime() {
        return assetInfo.getInventoryTime();
    }

    public Integer getAssetCode68() {
        return assetInfo.getAssetCode68();
    }

    public String getAssetCode68AsString() {
        if (assetInfo.getAssetCode68() == null) return null;

        if (assetInfo.getFunctionGroup() == null) return null;

        List<I18nMessage> msgList = FieldValueMessageController.getFieldValueList(String.valueOf(assetInfo.getFunctionGroup()), UserContextService.getCurrentUserAccount().getSiteId());

        String value = findMatchValue(assetInfo.getAssetCode68(), msgList);

        return value;

    }

    public String getClinicalDeptUID() {
        return assetInfo.getClinicalDeptUID();
    }

    public String getDeviceName() {
        return assetInfo.getDeviceName();
    }

    public String getMakerShortName() {
        return assetInfo.getMakerShortName();
    }

    public Integer getAssetCategory() {
        return assetInfo.getAssetCategory();
    }

    public Integer getMeteringCycle() {
        return assetInfo.getMeteringCycle();
    }

    public Date getNextMeteringDate() {
        return assetInfo.getNextMeteringDate();
    }

    public Boolean getWarranty() {
        return assetInfo.getWarranty();
    }

    public String getWarrantyAsString() {
        if (assetInfo.getWarranty() == null) return null;

        if (assetInfo.getWarranty()) {
            I18nMessage msg = I18nMessageService.getMessageByMsgKeyUsingDefaultConfigAsLowestPriority("True2Yes", siteId);

            if (msg != null) {
                return msg.getValueZh();
            }
        } else {
            I18nMessage msg = I18nMessageService.getMessageByMsgKeyUsingDefaultConfigAsLowestPriority("False2No", siteId);

            if (msg != null) {
                return msg.getValueZh();
            }
        }

        return null;
    }

    public String getMeteringNO() {
        return assetInfo.getMeteringNO();
    }

    public Integer getRepairForm() {
        return assetInfo.getRepairForm();
    }

    public String getRepairFormAsString() {

        if (assetInfo.getRepairForm() == null) return null;

        String value = findMatchValue(assetInfo.getRepairForm(), importService.repairFormList);

        return value;
    }

    public String getCreatedBy() {
        return assetInfo.getCreatedBy();
    }

    public Date getCreatedDate() {
        return assetInfo.getCreatedDate();
    }

    public String getLastModifiedBy() {
        return assetInfo.getLastModifiedBy();
    }

    public Date getLastModifiedDate() {
        return assetInfo.getLastModifiedDate();
    }

    public Integer getIbStatus() {
        return assetInfo.getIbStatus();
    }

    public Integer getGeDeviceType() {
        return assetInfo.getGeDeviceType();
    }

    public String getAssetGroupSecondayName() {
        if (assetInfo.getAssetGroup() == null || assetInfo.getAssetGroupSecondary() == null) return null;

        String msgType = "assetGroupSecondary_" + assetInfo.getAssetGroup();

        I18nMessage msg = I18nMessageService.getMessageUsingDefaultConfigAsLowestPriority(msgType, String.valueOf(assetInfo.getAssetGroupSecondary()), siteId);
        if (msg == null) return null;

        return msg.getValueZh();
    }

    public String getAssetValueCategoryName() {
        if (assetInfo.getAssetValueCategory() == null) return null;

        String value = findMatchValue(assetInfo.getAssetValueCategory(), importService.assetValueCategoryList);

        return value;
    }

    public String getProprietaryClassAsString() {

        if (assetInfo.getProprietaryClass() == null) return null;

        int proprietaryClass = Integer.parseInt(assetInfo.getProprietaryClass());

        String value = findMatchValue(proprietaryClass, importService.proprietaryClassList);

        return value;

    }

    private String findMatchValue(Integer item, List<I18nMessage> msgList) {

        String msgKey = String.valueOf(item);
        for (I18nMessage msg: msgList) {
            if (msg.getMsgKey().equals(msgKey)) {
                return msg.getValueZh();
            }
        }

        return null;
    }

    private String findMatchValue(String item, List<I18nMessage> msgList) {

        for (I18nMessage msg: msgList) {
            if (msg.getMsgKey().equals(item)) {
                return msg.getValueZh();
            }
        }

        return null;
    }

    private OrgInfo findDeptOrg(String orgUid) {
        if (sites == null || sites.isEmpty()) {
            return null;
        }

        for (OrgInfo orgInfo: sites) {
            if (orgInfo.getUid().equals(orgUid)) {
                return orgInfo;
            }
        }

        return null;
    }

    public Map<String, Object> buildExcelColumnKeyValueMap() {

        Map<String, Object> map = new HashMap<>();

        map.put(WebUtil.getMessage("EquipmentName"), this.getName());
        map.put(WebUtil.getMessage("EquipmentID"), this.getDepartNum());
        map.put(WebUtil.getMessage("financingNum"), this.getFinancingNum());
        map.put(WebUtil.getMessage("EquipmentAlias"), this.getAliasName());
        map.put(WebUtil.getMessage("RepairOwner"), this.getAssetOwnerName());
        map.put(WebUtil.getMessage("RepairOwner2"), this.getAssetOwnerName2());
        map.put(WebUtil.getMessage("RepairOwner3"), this.getAssetOwnerName3());
        map.put(WebUtil.getMessage("pmOwnerName"), this.getPmOwnerName());
        map.put(WebUtil.getMessage("Site"), this.getSiteName());
        map.put(WebUtil.getMessage("clinicalDeptId"), this.getClinicalDeptName());
        map.put(WebUtil.getMessage("AssetDepartment"), this.getAssetDeptName());
        map.put(WebUtil.getMessage("Manufacturer"), this.getManufacture());
        map.put(WebUtil.getMessage("BrandManufacturerShortName"), this.getMakerShortName());
        map.put(WebUtil.getMessage("EquipmentStandardName"), this.getDeviceName());
        map.put(WebUtil.getMessage("RegistrationNumber"), this.getRegistrationNo());
        map.put(WebUtil.getMessage("Vendor"), this.getVendor());
        map.put(WebUtil.getMessage("parentAsset"), this.getParentAssetName());

        map.put(WebUtil.getMessage("MedicalDeviceClassification"), this.getFunctionGroupAsString());
        map.put(WebUtil.getMessage("MedicalDeviceSubclassification"), this.getAssetCode68AsString());

        map.put(WebUtil.getMessage("EquipmentClassification"),this.getAssetGroupAsString());

        map.put(WebUtil.getMessage("FunctionGrade"),  this.getFunctionGrade());
        map.put(WebUtil.getMessage("RepairType"), this.getRepairFormAsString());
        map.put(WebUtil.getMessage("EquipmentModel"), this.getFunctionType());
        map.put(WebUtil.getMessage("ManufacturerSerialNumber"), this.getSerialNum());
        map.put(WebUtil.getMessage("SystemIDManufacturerRequestID"), this.getSystemId());
        map.put(WebUtil.getMessage("BarCode"), this.getBarcode());
        map.put(WebUtil.getMessage("2010NationalCode"), this.getNationalStandardCode());
        map.put(WebUtil.getMessage("ITSystemID"), this.getModalityId());

        map.put(WebUtil.getMessage("PurchasePrice"), this.getPurchasePrice());
        map.put(WebUtil.getMessage("FinalSalvageValue"), this.getSalvageValue());
        map.put(WebUtil.getMessage("LifeCycle"), this.getLifecycleInYear());
        map.put(WebUtil.getMessage("DepreciationMethod"), this.getDepreciationMethodAsString());


        map.put(WebUtil.getMessage("ManufactureDate"),this.getManufactDate());
        map.put(WebUtil.getMessage("PurchaseDate"),  this.getPurchaseDate());
        map.put(WebUtil.getMessage("ArrivalDate"), this.getArriveDate());
        map.put(WebUtil.getMessage("StartUsingDate"), this.getInstallDate());
        map.put(WebUtil.getMessage("WarrantyDate"),this.getFactoryWarrantyDate());
        map.put(WebUtil.getMessage("WarrantyExpirationDate"), this.getWarrantyDate());
        map.put(WebUtil.getMessage("TerminateDate"), this.getTerminateDate());
        map.put(WebUtil.getMessage("lastPmDate"), this.getLastPmDate());
        map.put(WebUtil.getMessage("LastMeasurementDate"), this.getLastMeteringDate());
        map.put(WebUtil.getMessage("LastInspectionDate"), this.getLastQaDate());

        map.put(WebUtil.getMessage("Maintainer"), this.getMaitanance());
        map.put(WebUtil.getMessage("MaintainerTelephone"), this.getMaitananceTel());
        map.put(WebUtil.getMessage("QRCode"), this.getQrCode());
        map.put(WebUtil.getMessage("InstallationLocationCode"), this.getLocationCode());
        map.put(WebUtil.getMessage("InstallationLocation"), this.getLocationName());
        map.put(WebUtil.getMessage("UseOwner"),  this.getClinicalOwnerName());
        map.put(WebUtil.getMessage("Remark"), this.getComments());


        map.put(WebUtil.getMessage("UsageClassification"),this.getAssetUsageAsString());
        map.put(WebUtil.getMessage("CapitalSource"), this.getCapitalSourceAsString());
        map.put(WebUtil.getMessage("RiskGrade"), this.getRiskGradeAsString());
        map.put(WebUtil.getMessage("MeasurementGrade"), this.getMeasureGradeAsString());
        map.put(WebUtil.getMessage("measuringItemCategory"), this.getMeasuringItemCategoryAsString());
        map.put(WebUtil.getMessage("IsEmergencyEquipment"),  this.getEmergencyEquipmentAsString());
        map.put(WebUtil.getMessage("IsImportEquipment"), this.getImportEquipmentAsString());
        map.put(WebUtil.getMessage("CurrentStatus"), this.getStatusAsString());
        map.put(WebUtil.getMessage("accountingCategory"), this.getAccountingCategoryAsString());
        map.put(WebUtil.getMessage("IsPM"), assetInfo.getIsPm() == null || assetInfo.getIsPm() ? WebUtil.getMessage("true") : WebUtil.getMessage("false"));
        map.put(WebUtil.getMessage("IsChecked2"), assetInfo.getIsChecked() ==  null || assetInfo.getIsChecked() ? WebUtil.getMessage("true") : WebUtil.getMessage("false"));
        map.put(WebUtil.getMessage("assetGroupSecondary"), this.getAssetGroupSecondayName());
        map.put(WebUtil.getMessage("assetValueCategory"), this.getAssetValueCategoryName());
        map.put(WebUtil.getMessage("deviceStatus"), this.getDeviceStatusAsString());
        map.put(WebUtil.getMessage("capitalSource2"), this.getCapitalSource2AsString());
        map.put(WebUtil.getMessage("isWarrantyNotice"), this.getWarrantyAsString());
        map.put(WebUtil.getMessage("subjectProject"), assetInfo.getSubjectProject());
        map.put(WebUtil.getMessage("warrantyMonth"), assetInfo.getWarrantyMonth());
        map.put(WebUtil.getMessage("acceptDate"), assetInfo.getAcceptDate());
        map.put(WebUtil.getMessage("createdDate"), assetInfo.getCreatedDate());

        map.put(WebUtil.getMessage("proprietaryClass"), this.getProprietaryClassAsString());

        map.put(WebUtil.getMessage("UUID"), this.getUid());

        return map;

    }
}
