package com.ge.apm.domain.pure;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetFileImportBaseService;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;

import java.util.*;
import java.util.stream.Collectors;

public class AssetInfoFormatter {

    private AssetInfo assetInfo;
    private List<OrgInfo> sites;
    private AssetFileImportBaseService importService;
    private AssetInfoRepository assetDao;
    private SupplierRepository supplierDao;
    private UserAccountRepository userDao;
    private SysAuthGroupRepository authGroupDao;
    private int siteId;
    private static Map<String, String> messageCacheMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(AssetInfoFormatter.class);

    public AssetInfoFormatter(AssetInfo assetInfo, List<OrgInfo> sites,
                              AssetFileImportBaseService importService,
                              AssetInfoRepository assetDao,
                              OrgInfoRepository orgDao,
                              SupplierRepository supplierDao , UserAccountRepository userDao, SysAuthGroupRepository authGroupDao) {
        this.assetInfo = assetInfo;
        this.sites = sites;
        this.importService = importService;
        this.assetDao = assetDao;
        this.supplierDao = supplierDao;
        this.userDao = userDao;
        this.siteId = UserContextService.getCurrentUserAccount().getSiteId();
        this.authGroupDao = authGroupDao;
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

    public Integer getNetworkingStatus() {
        return assetInfo.getNetworkingStatus();
    }

    public String getNetworkingStatusAsString() {
        I18nMessage msg = I18nMessageService.getMessageUsingDefaultConfigAsLowestPriority("networkingStatus",String.valueOf(assetInfo.getNetworkingStatus()), siteId);
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
        if (orgInfo == null || orgInfo.getOrgType() != 3) return null;

        return orgInfo.getName();
    }

    public String getHospitalName() {

        OrgInfo orgInfo = findDeptOrg(this.getHospitalUID());
        if (orgInfo == null || orgInfo.getOrgType() != 2) return null;

        return orgInfo.getName();
    }

    public String getInstitutionName() {

        OrgInfo orgInfo = findDeptOrg(this.getInstitutionUID());
        if (orgInfo == null || orgInfo.getOrgType() != 1) return null;

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

    public String getAssetQualityAsString() {

        if (assetInfo.getAssetQuality() == null) return null;

        String value = findMatchValue(assetInfo.getAssetQuality(), importService.assetQualityList);

        return value;
    }

    public String getAssetClassAsString() {

        if (assetInfo.getAssetClass() == null) return null;

        String value = findMatchValue(assetInfo.getAssetClass(), importService.assetClassList);

        return value;
    }

    public String getSpare01AsString() {

        if (assetInfo.getSpare01() == null) return null;

//        String value = findMatchTreeValue(assetInfo.getSpare01(), importService.spare01List);
        String value = findMatchKeyValue(assetInfo.getSpare01(), importService.spare01List);

        return value;
    }
    public String getSpare02AsString() {

        if (assetInfo.getSpare02() == null) return null;

//        String value = findMatchTreeValue(assetInfo.getSpare01(), importService.spare01List);
        String value = findMatchKeyValue(assetInfo.getSpare02(), importService.spare02List);

        return value;
    }
    public String getSpare03AsString() {

        if (assetInfo.getSpare03() == null) return null;

//        String value = findMatchTreeValue(assetInfo.getSpare01(), importService.spare01List);
        String value = findMatchKeyValue(assetInfo.getSpare03(), importService.spare03List);

        return value;
    }
    public String getSpare04AsString() {

        if (assetInfo.getSpare04() == null) return null;

//        String value = findMatchTreeValue(assetInfo.getSpare01(), importService.spare01List);
        String value = findMatchKeyValue(assetInfo.getSpare04(), importService.spare04List);

        return value;
    }
    public String getSpare05AsString() {

        if (assetInfo.getSpare05() == null) return null;

//        String value = findMatchTreeValue(assetInfo.getSpare01(), importService.spare01List);
        String value = findMatchKeyValue(assetInfo.getSpare05(), importService.spare05List);

        return value;
    }

    private String findMatchKeyValue(String spare01, List<I18nMessage> spare01List) {

        String msgKey = String.valueOf(spare01);
        for (I18nMessage msg: spare01List) {
            if (msg.getMsgKey().equals(msgKey)) {
                return msg.getMsgKey()+" "+msg.getValueZh();
            }
        }

        return null;

    }

    private String findMatchTreeValue(String spare01, List<I18nMessage> spare01List) {
        List<I18nMessage> collect = spare01List.stream().filter(item -> spare01.startsWith(item.getMsgKey())).sorted((a,b)-> a.getMsgKey().length()+b.getMsgKey().length()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(collect)){
            return null;
        }
        String collect1 = collect.stream().map(I18nMessage::getValueZh).collect(Collectors.joining("/"));
        return collect1;
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

    public Date getLastInventoryDate() {
        return assetInfo.getLastInventoryDate();
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

    public String getSystemNum1() { return assetInfo.getSystemNum1(); }

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

    public String getParentAssetName(Map parentAssets) {
        if (assetInfo.getParentAssetId() == null) {
            return null;
        }

        //AssetInfo parentAsset = assetDao.getById(assetInfo.getParentAssetId());
        AssetInfo parentAsset = (AssetInfo) parentAssets.get(assetInfo.getParentAssetId());
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

    public Integer getUsageCount() {
        return assetInfo.getUsageCount();
    }

    public Integer getRatingUsageCount() {
        return assetInfo.getRatingUsageCount();
    }

    public String getEpc() {
        return assetInfo.getEpc();
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

    private String joinTags(Map<String,List<String>> assetMap) {
        //List<String> assetTags = this.importService.assetTagsMapper.getTagsByAsset(assetInfo.getUid());
        List<String> assetTags = assetMap.get(assetInfo.getUid());
        if (assetTags==null || assetTags.isEmpty()) {
            return null;
        }

        //use space as delimiter
        String joinedAssetTags = String.join(" ", assetTags);
        return joinedAssetTags;
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

    public String getSupplierTel(Map supliers, Integer supplierId){
        if(supplierId != null){
            return  (String) supliers.get(supplierId);
        }
        return null;
    }

    public Map<String, Object> buildExcelColumnKeyValueMap(boolean crossSite,Map<String,List<String>> assetMap,Map parentAssets,Map supliers, Map encodedToAssetTypeGroupPath,Map listSysAuthGroupNamePathInHierarchyMap ) {

        Map<String, Object> map = new HashMap<>();

        // key "name" is used by a lot of modules, the definition is wrong, so use a new key name to redefine
        map.put(getMessage("EquipmentName"), this.getName());

        map.put(getMessage("departNum"), this.getDepartNum());
        map.put(getMessage("financingNum"), this.getFinancingNum());
        map.put(getMessage("aliasName"), this.getAliasName());

        map.put(getMessage("assetOwnerName"), this.getAssetOwnerName());
        map.put(getMessage("assetOwnerTel"), assetInfo.getAssetOwnerTel());
        map.put(getMessage("assetOwnerName2"), this.getAssetOwnerName2());
        map.put(getMessage("assetOwnerTel2"), assetInfo.getAssetOwnerTel2());
        map.put(getMessage("assetOwnerName3"), this.getAssetOwnerName3());
        map.put(getMessage("assetOwnerTel3"), assetInfo.getAssetOwnerTel3());

        map.put(getMessage("pmOwnerName"), this.getPmOwnerName());
        map.put(getMessage("pmOwnerTel"), assetInfo.getPmOwnerTel());
        map.put(getMessage("measuringOwner"), assetInfo.getMeasuringOwnerName());

        if (crossSite) {
            map.put(getMessage("Hospital"), this.getHospitalName());
            map.put(getMessage("Institution"), this.getInstitutionName());
        }

        map.put(getMessage("hospitalId"), this.getSiteName());
        map.put(getMessage("clinicalDeptName"), this.getClinicalDeptName());
        map.put(getMessage("clinicalOwnerName"),  this.getClinicalOwnerName());
        map.put(getMessage("clinicalOwnerTel"), assetInfo.getClinicalOwnerTel());
        map.put(getMessage("assetDeptName"), this.getAssetDeptName());
        map.put(getMessage("manufacture"), this.getManufacture());
        map.put(getMessage("manufactureTel"), this.getSupplierTel(supliers, Optional.ofNullable(assetInfo.getManufactureId()).map(Integer::parseInt).orElse(null)));
        map.put(getMessage("makerShortName"), this.getMakerShortName());
        map.put(getMessage("deviceName"), this.getDeviceName());
        map.put(getMessage("registrationNo"), this.getRegistrationNo());
        map.put(getMessage("vendor"), this.getVendor());
        map.put(getMessage("vendorTel"), this.getSupplierTel(supliers, assetInfo.getSupplierId()));
        map.put(getMessage("parentAsset"), this.getParentAssetName(parentAssets));

        map.put(getMessage("functionGroup"), this.getFunctionGroupAsString());
        map.put(getMessage("assetCode68"), this.getAssetCode68AsString());

        map.put(getMessage("assetGroup"),this.getAssetGroupAsString());

        map.put(getMessage("functionGrade"),  this.getFunctionGrade());
        map.put(getMessage("repairForm"), this.getRepairFormAsString());
        map.put(getMessage("functionType"), this.getFunctionType());
        map.put(getMessage("serialNum"), this.getSerialNum());
        map.put(getMessage("systemId"), this.getSystemId());
        map.put(getMessage("barcode"), this.getBarcode());
        map.put(getMessage("nationalStandardCode"), this.getNationalStandardCode());
        map.put(getMessage("modalityId"), this.getModalityId());

        map.put(getMessage("purchasePrice"), this.getPurchasePrice());
        map.put(getMessage("salvageValue"), this.getSalvageValue());
        map.put(getMessage("lifecycle"), this.getLifecycleInYear());
        map.put(getMessage("depreciationMethod"), this.getDepreciationMethodAsString());

        map.put(getMessage("manufactDate"),this.getManufactDate());
        map.put(getMessage("purchaseDate"),  this.getPurchaseDate());
        map.put(getMessage("arriveDate"), this.getArriveDate());
        map.put(getMessage("installDate"), this.getInstallDate());
        map.put(getMessage("factoryWarrantyDate"),this.getFactoryWarrantyDate());
        map.put(getMessage("warrantyDate"), this.getWarrantyDate());
        map.put(getMessage("terminateDate"), this.getTerminateDate());
        map.put(getMessage("lastPmDate"), this.getLastPmDate());
        map.put(getMessage("lastMeteringDate"), this.getLastMeteringDate());
        map.put(getMessage("lastInventoryDate"), this.getLastInventoryDate());
        map.put(getMessage("lastQaDate"), this.getLastQaDate());

        map.put(getMessage("maitanance"), this.getMaitanance());
        map.put(getMessage("maitananceTel"), this.getSupplierTel(supliers, Optional.ofNullable(assetInfo.getMaitananceId()).map(Integer::parseInt).orElse(null)));
        map.put(getMessage("qrCode"), this.getQrCode());
        map.put(getMessage("locationCode"), this.getLocationCode());
        map.put(getMessage("locationName"), this.getLocationName());
        map.put(getMessage("comments"), this.getComments());

        map.put(getMessage("assetUsage"),this.getAssetUsageAsString());
        map.put(getMessage("capitalSource"), this.getCapitalSourceAsString());
        map.put(getMessage("riskGrade"), this.getRiskGradeAsString());
        map.put(getMessage("measureGrade"), this.getMeasureGradeAsString());
        map.put(getMessage("measuringItemCategory"), this.getMeasuringItemCategoryAsString());
        map.put(getMessage("emergencyEquipment"),  this.getEmergencyEquipmentAsString());
        map.put(getMessage("importEquipment"), this.getImportEquipmentAsString());
        map.put(getMessage("status"), this.getStatusAsString());
        map.put(getMessage("assetClass"), this.getAssetClassAsString());
        map.put(getMessage("accountingCategory"), this.getAccountingCategoryAsString());
        map.put(getMessage("isAssetPMed"), assetInfo.getIsPm() == null || assetInfo.getIsPm() ? getMessage("true") : getMessage("false"));
        map.put(getMessage("isFixed"), assetInfo.getIsFixed() == null || assetInfo.getIsFixed() ? getMessage("true") : getMessage("false"));
        map.put(getMessage("isUseRegistration"), assetInfo.getIsUseRegistration() == null || assetInfo.getIsUseRegistration() ? getMessage("true") : getMessage("false"));
        map.put(getMessage("isAssetChecked"), assetInfo.getIsChecked() ==  null || assetInfo.getIsChecked() ? getMessage("true") : getMessage("false"));

        map.put(getMessage("assetValueCategory"), this.getAssetValueCategoryName());
        map.put(getMessage("deviceStatus"), this.getDeviceStatusAsString());
        map.put(getMessage("networkingStatus"), this.getNetworkingStatusAsString());
        map.put(getMessage("capitalSource2"), this.getCapitalSource2AsString());
        map.put(getMessage("isWarrantyNotice"), this.getWarrantyAsString());
        map.put(getMessage("assetTags"), this.joinTags(assetMap));
        map.put(getMessage("UCMID"), assetInfo.getUCMID());

        //fields not used by default
        map.put(getMessage("createdDate"), assetInfo.getCreatedDate());
        map.put(getMessage("assetGroupSecondary"), this.getAssetGroupSecondayName());
        map.put(getMessage("subjectProject"), assetInfo.getSubjectProject());
        map.put(getMessage("acceptDate"), assetInfo.getAcceptDate());
        map.put(getMessage("warrantyMonth"), assetInfo.getWarrantyMonth());
        map.put(getMessage("proprietaryClass"), this.getProprietaryClassAsString());

        map.put(getMessage("medicalApparatusClassification") + "编码", assetInfo.getMedicalApparatusClassification());
        map.put(getMessage("medicalApparatusClassification"),listSysAuthGroupNamePathInHierarchyMap.get(assetInfo.getMedicalApparatusClassification()));

        map.put(getMessage("createdBy"), this.selectCreatedBy(assetInfo.getCreatedBy()));
        map.put(getMessage("mainDirectory"), assetInfo.getMainDirectory());
        map.put(getMessage("secondDirectory"), assetInfo.getSecondDirectory());
        map.put(getMessage("UUID"), this.getUid());
        map.put(getMessage("rfId"), assetInfo.getRfId());
        map.put(getMessage("rfGroup"), assetInfo.getRfGroup());
        map.put(getMessage("powerOnThreshold"), assetInfo.getPowerOnThreshold());
        map.put(getMessage("assetQuality"), this.getAssetQualityAsString());
        map.put(getMessage("currentId"), assetInfo.getCurrentId());
        map.put(getMessage("assetUsedLifecycle"), assetInfo.getAssetUsedLifecycle());
        map.put(getMessage("assetLifecycleDeadline"), assetInfo.getAssetLifecycleDeadline());
        map.put(getMessage("MVSContractStartDate"), assetInfo.getMvsContractStartDate());
        map.put(getMessage("MVSContractEndDate"), assetInfo.getMvsContractEndDate());
        map.put(getMessage("assetQuantity"), assetInfo.getAssetQuantity());
        map.put(getMessage("assetTypeGroup"), encodedToAssetTypeGroupPath.get(assetInfo.getAssetType()));
        map.put(getMessage("assetTypeGroupEncoded"), assetInfo.getAssetType());
        map.put(getMessage("usageCount"), assetInfo.getUsageCount());
        map.put(getMessage("ratingUsageCount"), assetInfo.getRatingUsageCount());
        map.put(getMessage("epc"), assetInfo.getEpc());
        return map;

    }

    public Map<String, Object> buildExcelColumnKeyValueMap1(Map<String, String> userMap, boolean crossSite,Map<String,List<String>> assetMap,Map parentAssets,Map supliers, Map encodedToAssetTypeGroupPath,Map listSysAuthGroupNamePathInHierarchyMap) {

        Map<String, Object> map = new HashMap<>();

        // key "name" is used by a lot of modules, the definition is wrong, so use a new key name to redefine
        map.put(getMessage("EquipmentName"), this.getName());

        map.put(getMessage("departNum"), this.getDepartNum());
        map.put(getMessage("financingNum"), this.getFinancingNum());
        map.put(getMessage("aliasName"), this.getAliasName());

        map.put(getMessage("assetOwnerName"), this.getAssetOwnerName());
        map.put(getMessage("assetOwnerTel"), assetInfo.getAssetOwnerTel());
        map.put(getMessage("assetOwnerName2"), this.getAssetOwnerName2());
        map.put(getMessage("assetOwnerTel2"), assetInfo.getAssetOwnerTel2());
        map.put(getMessage("assetOwnerName3"), this.getAssetOwnerName3());
        map.put(getMessage("assetOwnerTel3"), assetInfo.getAssetOwnerTel3());

        map.put(getMessage("pmOwnerName"), this.getPmOwnerName());
        map.put(getMessage("pmOwnerTel"), assetInfo.getPmOwnerTel());
        map.put(getMessage("patrolInspectionOwnerName"), assetInfo.getPatrolInspectionOwnerName());
        map.put(getMessage("patrolInspectionOwnerTel"), assetInfo.getPatrolInspectionOwnerTel());
        map.put(getMessage("measuringOwner"), assetInfo.getMeasuringOwnerName());

        if (crossSite) {
            map.put(getMessage("Hospital"), this.getHospitalName());
            map.put(getMessage("Institution"), this.getInstitutionName());
        }

        map.put(getMessage("hospitalId"), this.getSiteName());
        map.put(getMessage("clinicalDeptName"), this.getClinicalDeptName());
        map.put(getMessage("clinicalOwnerName"),  this.getClinicalOwnerName());
        map.put(getMessage("clinicalOwnerTel"), assetInfo.getClinicalOwnerTel());
        map.put(getMessage("assetDeptName"), this.getAssetDeptName());
        map.put(getMessage("manufacture"), this.getManufacture());
        map.put(getMessage("manufactureTel"), this.getSupplierTel(supliers, Optional.ofNullable(assetInfo.getManufactureId()).map(Integer::parseInt).orElse(null)));
        map.put(getMessage("makerShortName"), this.getMakerShortName());
        map.put(getMessage("deviceName"), this.getDeviceName());
        map.put(getMessage("registrationNo"), this.getRegistrationNo());
        map.put(getMessage("vendor"), this.getVendor());
        map.put(getMessage("vendorTel"), this.getSupplierTel(supliers, assetInfo.getSupplierId()));
        map.put(getMessage("parentAsset"), this.getParentAssetName(parentAssets));

        map.put(getMessage("functionGroup"), this.getFunctionGroupAsString());
        map.put(getMessage("assetCode68"), this.getAssetCode68AsString());

        map.put(getMessage("assetGroup"),this.getAssetGroupAsString());

        map.put(getMessage("functionGrade"),  this.getFunctionGrade());
        map.put(getMessage("repairForm"), this.getRepairFormAsString());
        map.put(getMessage("functionType"), this.getFunctionType());
        map.put(getMessage("serialNum"), this.getSerialNum());
        map.put(getMessage("systemId"), this.getSystemId());
        map.put(getMessage("systemNum1"), this.getSystemNum1());
        map.put(getMessage("systemNum2"), this.getSystemNum2());
        map.put(getMessage("systemNum3"), this.getSystemNum3());
        map.put(getMessage("systemNum4"), this.getSystemNum4());
        map.put(getMessage("systemNum5"), this.getSystemNum5());
        map.put(getMessage("barcode"), this.getBarcode());
        map.put(getMessage("nationalStandardCode"), this.getNationalStandardCode());
        map.put(getMessage("modalityId"), this.getModalityId());

        map.put(getMessage("purchasePrice"), this.getPurchasePrice());
        map.put(getMessage("salvageValue"), this.getSalvageValue());
        map.put(getMessage("lifecycle"), this.getLifecycleInYear());
        map.put(getMessage("depreciationMethod"), this.getDepreciationMethodAsString());

        map.put(getMessage("manufactDate"),this.getManufactDate());
        map.put(getMessage("purchaseDate"),  this.getPurchaseDate());
        map.put(getMessage("arriveDate"), this.getArriveDate());
        map.put(getMessage("installDate"), this.getInstallDate());
        map.put(getMessage("factoryWarrantyDate"),this.getFactoryWarrantyDate());
        map.put(getMessage("warrantyDate"), this.getWarrantyDate());
        map.put(getMessage("terminateDate"), this.getTerminateDate());
        map.put(getMessage("lastPmDate"), this.getLastPmDate());
        map.put(getMessage("lastMeteringDate"), this.getLastMeteringDate());
        map.put(getMessage("lastInventoryDate"), this.getLastInventoryDate());
        map.put(getMessage("lastQaDate"), this.getLastQaDate());

        map.put(getMessage("maitanance"), this.getMaitanance());
        map.put(getMessage("maitananceTel"), this.getSupplierTel(supliers, Optional.ofNullable(assetInfo.getMaitananceId()).map(Integer::parseInt).orElse(null)));
        map.put(getMessage("qrCode"), this.getQrCode());
        map.put(getMessage("locationCode"), this.getLocationCode());
        map.put(getMessage("locationName"), this.getLocationName());
        map.put(getMessage("comments"), this.getComments());

        map.put(getMessage("assetUsage"),this.getAssetUsageAsString());
        map.put(getMessage("capitalSource"), this.getCapitalSourceAsString());
        map.put(getMessage("riskGrade"), this.getRiskGradeAsString());
        map.put(getMessage("measureGrade"), this.getMeasureGradeAsString());
        map.put(getMessage("measuringItemCategory"), this.getMeasuringItemCategoryAsString());
        map.put(getMessage("emergencyEquipment"),  this.getEmergencyEquipmentAsString());
        map.put(getMessage("importEquipment"), this.getImportEquipmentAsString());
        map.put(getMessage("status"), this.getStatusAsString());
        map.put(getMessage("assetClass"), this.getAssetClassAsString());
        map.put(getMessage("accountingCategory"), this.getAccountingCategoryAsString());
        map.put(getMessage("isAssetPMed"), assetInfo.getIsPm() == null || assetInfo.getIsPm() ? getMessage("true") : getMessage("false"));
        map.put(getMessage("isFixed"), assetInfo.getIsFixed() == null || assetInfo.getIsFixed() ? getMessage("true") : getMessage("false"));
        map.put(getMessage("isUseRegistration"), assetInfo.getIsUseRegistration() == null || assetInfo.getIsUseRegistration() ? getMessage("true") : getMessage("false"));
        map.put(getMessage("isAssetChecked"), assetInfo.getIsChecked() ==  null || assetInfo.getIsChecked() ? getMessage("true") : getMessage("false"));

        map.put(getMessage("assetValueCategory"), this.getAssetValueCategoryName());
        map.put(getMessage("deviceStatus"), this.getDeviceStatusAsString());
        map.put(getMessage("networkingStatus"), this.getNetworkingStatusAsString());
        map.put(getMessage("capitalSource2"), this.getCapitalSource2AsString());
        map.put(getMessage("isWarrantyNotice"), this.getWarrantyAsString());
        map.put(getMessage("assetTags"), this.joinTags(assetMap));
        map.put(getMessage("UCMID"), assetInfo.getUCMID());

        //fields not used by default
        map.put(getMessage("createdDate"), assetInfo.getCreatedDate());
        map.put(getMessage("assetGroupSecondary"), this.getAssetGroupSecondayName());
        map.put(getMessage("subjectProject"), assetInfo.getSubjectProject());
        map.put(getMessage("acceptDate"), assetInfo.getAcceptDate());
        map.put(getMessage("warrantyMonth"), assetInfo.getWarrantyMonth());
        map.put(getMessage("proprietaryClass"), this.getProprietaryClassAsString());

        map.put(getMessage("medicalApparatusClassification") + "编码", assetInfo.getMedicalApparatusClassification());
        map.put(getMessage("medicalApparatusClassification"),listSysAuthGroupNamePathInHierarchyMap.get(assetInfo.getMedicalApparatusClassification()));

        map.put(getMessage("createdBy"), assetInfo.getCreatedBy());
        map.put(getMessage("mainDirectory"), assetInfo.getMainDirectory());
        map.put(getMessage("secondDirectory"), assetInfo.getSecondDirectory());
        map.put(getMessage("UUID"), this.getUid());
        map.put(getMessage("rfId"), assetInfo.getRfId());
        map.put(getMessage("rfGroup"), assetInfo.getRfGroup());
        map.put(getMessage("powerOnThreshold"), assetInfo.getPowerOnThreshold());
        map.put(getMessage("assetQuality"), this.getAssetQualityAsString());
        map.put(getMessage("currentId"), assetInfo.getCurrentId());
        map.put(getMessage("assetUsedLifecycle"), assetInfo.getAssetUsedLifecycle());
        map.put(getMessage("assetLifecycleDeadline"), assetInfo.getAssetLifecycleDeadline());
        map.put(getMessage("MVSContractStartDate"), assetInfo.getMvsContractStartDate());
        map.put(getMessage("MVSContractEndDate"), assetInfo.getMvsContractEndDate());
        map.put(getMessage("assetQuantity"), assetInfo.getAssetQuantity());
        map.put(getMessage("assetTypeGroup"), encodedToAssetTypeGroupPath.get(assetInfo.getAssetType()));
        map.put(getMessage("assetTypeGroupEncoded"), assetInfo.getAssetType());

        map.put(getMessage("spare01"), this.getSpare01AsString());
        map.put(getMessage("spare02"), this.getSpare02AsString());
        map.put(getMessage("spare03"), this.getSpare03AsString());
        map.put(getMessage("spare04"), this.getSpare04AsString());
        map.put(getMessage("spare05"), this.getSpare05AsString());
        map.put(getMessage("spare06"), assetInfo.getSpare06());
        map.put(getMessage("spare07"), assetInfo.getSpare07());
        map.put(getMessage("spare08"), assetInfo.getSpare08());
        map.put(getMessage("spare09"), assetInfo.getSpare09());
        map.put(getMessage("spare10"), assetInfo.getSpare10());

        map.put(getMessage("UCMID"), assetInfo.getUCMID());
        map.put(getMessage("productFamily"), assetInfo.getProductFamily());
        map.put(getMessage("productSegment"), this.getProductSegmentAsString());
        map.put(getMessage("warrantyStatus"), assetInfo.getWarrantyStatus());
        map.put(getMessage("usageCount"), assetInfo.getUsageCount());
        map.put(getMessage("ratingUsageCount"), assetInfo.getRatingUsageCount());
        map.put(getMessage("epc"), assetInfo.getEpc());
        return map;

    }

    public String getProductSegmentAsString() {
        if (assetInfo.getProductSegment() == null) return null;
        return findMatchValue(assetInfo.getProductSegment(), importService.productSegmentList);
    }

    private String getMedicalApparatusClassificationName(String medicalApparatusClassification) {
        if(StringUtils.isEmpty(medicalApparatusClassification)){
            return "";
        }

        String connectedCode = authGroupDao.listSysAuthGroupNamePathInHierarchyByClassificationCode(medicalApparatusClassification);

        return connectedCode;
    }

    private String getMessage(String key) {
        return WebUtil.getMessage(key);
    }

    public String selectCreatedBy(String loginName) {
        UserAccount user = userDao.getByLoginName(loginName);
        if(user != null){
            return user.getName();
        }
        return null;
    }



}
