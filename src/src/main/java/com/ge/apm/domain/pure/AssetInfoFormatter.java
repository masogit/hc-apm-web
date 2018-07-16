package com.ge.apm.domain.pure;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.Brand;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.service.asset.AssetFileImportService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetInfoFormatter {

    private AssetInfo assetInfo;
    private List<OrgInfo> sites;
    private AssetFileImportService importService;

    public AssetInfoFormatter(AssetInfo assetInfo, List<OrgInfo> sites, AssetFileImportService importService) {
        this.assetInfo = assetInfo;
        this.sites = sites;
        this.importService = importService;
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
        for (OrgInfo orgInfo: sites) {
            if (this.getSiteUID().equals(orgInfo.getUid())) {
                return orgInfo.getName();
            }
        }

        return null;
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
        return assetInfo.getClinicalDeptName();
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

    private String findMatchValue(Integer item, List<I18nMessage> msgList) {

        String msgKey = String.valueOf(item);
        for (I18nMessage msg: msgList) {
            if (msg.getMsgKey().equals(msgKey)) {
                return msg.getValueZh();
            }
        }

        return null;
    }

    public Map<String, Object> buildExcelColumnKeyValueMap(boolean allColumns) {

        Map<String, Object> map = new HashMap<>();

        map.put("设备名称", this.getName());
        map.put("设备编号", this.getDepartNum());
        map.put("资产编号", this.getFinancingNum());
        map.put("设备别名", this.getAliasName());
        map.put("维修负责人", this.getAssetOwnerName());
        map.put("维修负责人2", this.getAssetOwnerName2());
        map.put("维修负责人3", this.getAssetOwnerName3());
        map.put("院区", this.getSiteName());
        map.put("使用科室", this.getClinicalDeptName());
        map.put("制造商", this.getManufacture());
        map.put("品牌/制造商简称", this.getMakerShortName());
        map.put("设备标准名称", this.getDeviceName());
        map.put("注册证号", this.getRegistrationNo());
        map.put("供应商", this.getVendor());

        map.put("医疗器械分类", this.getFunctionGroupAsString());
        map.put("医疗器械子分类", this.getAssetCode68AsString());

        map.put("设备分类", this.getAssetGroupAsString());

        map.put("功能等级", this.getFunctionGrade());
        map.put("维修类型", this.getRepairFormAsString());
        map.put("设备型号", this.getFunctionType());
        map.put("制造商序列号", this.getSerialNum());
        map.put("SystemID/厂商报修ID", this.getSystemId());
        map.put("条码", this.getBarcode());
        map.put("2010国家编码", this.getNationalStandardCode());
        map.put("IT系统编号", this.getModalityId());

        if (allColumns) {
            map.put("采购价格", this.getPurchasePrice());
            map.put("最终残值", this.getSalvageValue());
            map.put("使用年限", this.getLifecycleInYear());
            map.put("折旧算法", this.getDepreciationMethodAsString());
        } else {
            map.put("采购价格", null);
            map.put("最终残值", null);
            map.put("使用年限", null);
            map.put("折旧算法", null);
        }

        map.put("出厂日期", this.getManufactDate());
        map.put("采购日期", this.getPurchaseDate());
        map.put("到货日期", this.getArriveDate());
        map.put("启用日期", this.getInstallDate());
        map.put("质保日期", this.getFactoryWarrantyDate());
        map.put("维保截止日期", this.getWarrantyDate());
        map.put("报废日期", this.getTerminateDate());
        map.put("上次保养日期", this.getLastPmDate());
        map.put("上次计量日期", this.getLastMeteringDate());
        map.put("上次巡检日期", this.getLastQaDate());

        map.put("维修商", this.getMaitanance());
        map.put("维修商电话", this.getMaitananceTel());
        map.put("二维码", this.getQrCode());
        map.put("安装位置编码", this.getLocationCode());
        map.put("安装位置", this.getLocationName());
        map.put("使用责任人", this.getClinicalOwnerName());
        map.put("备注", this.getComments());


        map.put("用途分类", this.getAssetUsageAsString());
        map.put("资金来源", this.getCapitalSourceAsString());
        map.put("风险等级", this.getRiskGradeAsString());
        map.put("计量等级", this.getMeasureGradeAsString());
        map.put("计量项目类别", this.getMeasuringItemCategoryAsString());
        map.put("是否急救设备", this.getEmergencyEquipmentAsString());
        map.put("是否进口设备", this.getImportEquipmentAsString());
        map.put("当前状态", this.getStatusAsString());
        map.put("核算类别", this.getAccountingCategoryAsString());
        map.put("是否PM", assetInfo.getIsPm() == null || assetInfo.getIsPm() ? "是" : "否");
        map.put("是否盘点", assetInfo.getIsChecked() ==  null || assetInfo.getIsChecked() ? "是" : "否");

        map.put("UUID", this.getUid());

        return map;

    }
}
