package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.aop.SystemEventAuditAopAspect;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.AuditBoTypeEnum;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.ge.apm.service.asset.AssetInfoBulkEditUtils.*;

@Service
public class AssetInfoBulkEditService {
    private Logger logger = LoggerFactory.getLogger(AssetInfoBulkEditService.class);
    @Autowired
    private AssetInfoRepository assetInfoDao;

    @Autowired
    private AssetInfoMapper assetInfoMapper;

    @Autowired
    private QrCodeLibRepository qrCodeLibDao;

    @Autowired
    private ServiceRequestRepository serviceRequestDao;

    @Autowired
    private V2_WorkOrderRepository workOrderDao;

    @Autowired
    private V2_PmOrderRepository pmOrderDao;

    @Autowired
    private RelRuleAssetRepository relRuleAssetDao;

    @Autowired
    private SharedAssetRequestRepository sharedAssetRequestRepository;

    @Autowired
    private InventoryOrderDetailAssetInfoRepository inventoryOrderDetailAssetInfoRepository;

    @Autowired
    SharedAssetReqDetailRepository sharedAssetReqDetailRepository;

    @Autowired
    private NurseSiteMaintainConfigItemRepository nurseSiteMaintainConfigItemRepository;

    @Autowired
    private NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;

    @Autowired
    private AssetTagRuleRepository assetTagRuleRepository;

    @Autowired
    private AssetHistoryService assetHistoryService;

    @Autowired
    private SystemEventAuditAopAspect systemEventAuditUtil;

    @Transactional(rollbackFor = Exception.class)
    public void commitBulkEditSite(List<AssetInfo> assets, AssetInfo targetAsset, List<String> selectedColumns) {
        commitCorrectiveMaintenanceBulkEdit(assets, targetAsset);
        commitPreventativeMaintenanceBulkEdit(assets, targetAsset);
        commitQrCode(assets, targetAsset);
        commitPmRule(assets, targetAsset);
        commitAssetTagRule(assets);
        commitNurseSiteConfig(assets);
        commitSharedAssetReq(assets);
        //make sure qrcode site update before asset site update, due to qrcode update depend on asset site
        commitSiteForAssetItself(assets, targetAsset);
    }

    private void commitSharedAssetReq(List<AssetInfo> assets) {
        if(CollectionUtils.isEmpty(assets)) return;
        Set<String> collect = assets.stream().map(AssetInfo::getUid).collect(Collectors.toSet());
        sharedAssetRequestRepository.updateStateCompletedByAssetUid(collect);
    }


    @Transactional
    public void commitBulkEdit(List<AssetInfo> assets, AssetInfo targetAsset, List<String> selectedColumns) {

        for (AssetInfo item : assets) {
            if (selectedColumns.contains("serialNum")) {
                item.setSerialNum(targetAsset.getSerialNum());
            }
            if (selectedColumns.contains("name")) {
                item.setName(targetAsset.getName());
            }
            if (selectedColumns.contains("aliasName")) {
                item.setAliasName(targetAsset.getAliasName());
            }
            if (selectedColumns.contains("assetGroup")) {
                item.setAssetGroup(targetAsset.getAssetGroup());
            }
            if (selectedColumns.contains("assetOwnerName")) {
                item.setAssetOwnerName(targetAsset.getAssetOwnerName());
                item.setAssetOwnerId(targetAsset.getAssetOwnerId());
                item.setAssetOwnerTel(targetAsset.getAssetOwnerTel());
            }
            if (selectedColumns.contains("assetOwnerTel")) {
                item.setAssetOwnerTel(targetAsset.getAssetOwnerTel());
            }
            if (selectedColumns.contains("assetOwnerName2")) {
                item.setAssetOwnerName2(targetAsset.getAssetOwnerName2());
                item.setAssetOwnerId2(targetAsset.getAssetOwnerId2());
                item.setAssetOwnerTel2(targetAsset.getAssetOwnerTel2());
            }
            if (selectedColumns.contains("assetOwnerTel2")) {
                item.setAssetOwnerTel2(targetAsset.getAssetOwnerTel2());
            }
            if (selectedColumns.contains("assetOwnerName3")) {
                item.setAssetOwnerId3(targetAsset.getAssetOwnerId3());
                item.setAssetOwnerName3(targetAsset.getAssetOwnerName3());
                item.setAssetOwnerTel3(targetAsset.getAssetOwnerTel3());
            }
            if (selectedColumns.contains("assetOwnerTel3")) {
                item.setAssetOwnerTel3(targetAsset.getAssetOwnerTel3());
            }
            if (selectedColumns.contains("pmOwnerName")) {
                item.setPmOwnerName(targetAsset.getPmOwnerName());
                item.setPmOwnerId(targetAsset.getPmOwnerId());
                item.setPmOwnerTel(targetAsset.getPmOwnerTel());
            }
            if (selectedColumns.contains("pmOwnerTel")) {
                item.setPmOwnerTel(targetAsset.getPmOwnerTel());
            }
            if (selectedColumns.contains("clinicalOwnerName")) {
                item.setClinicalOwnerName(targetAsset.getClinicalOwnerName());
                item.setClinicalOwnerId(targetAsset.getClinicalOwnerId());
                item.setClinicalOwnerTel(targetAsset.getClinicalOwnerTel());
            }
            if (selectedColumns.contains("clinicalOwnerTel")) {
                item.setClinicalOwnerTel(targetAsset.getClinicalOwnerTel());
            }
            if (selectedColumns.contains("manufacture")) {
                item.setManufacture(targetAsset.getManufacture());
                item.setManufactureId(targetAsset.getManufactureId());
                item.setManufactureTel(targetAsset.getManufactureTel());
            }
            if (selectedColumns.contains("manufactureTel")) {
                item.setManufactureTel(targetAsset.getManufactureTel());
            }
            if (selectedColumns.contains("makerShortName")) {
                item.setMakerShortName(targetAsset.getMakerShortName());
            }
            if (selectedColumns.contains("vendor")) {
                item.setVendor(targetAsset.getVendor());
                item.setSupplierId(targetAsset.getSupplierId());
                item.setVendorTel(targetAsset.getVendorTel());
            }
            if (selectedColumns.contains("vendorTel")) {
                item.setVendorTel(targetAsset.getVendorTel());
            }
            if (selectedColumns.contains("maitanance")) {
                item.setMaitanance(targetAsset.getMaitanance());
                item.setMaitananceId(targetAsset.getMaitananceId());
                item.setMaitananceTel(targetAsset.getMaitananceTel());
            }
            if (selectedColumns.contains("maitananceTel")) {
                item.setMaitananceTel(targetAsset.getMaitananceTel());
            }
            if (selectedColumns.contains("nationalStandardCode")) {
                item.setNationalStandardCode(targetAsset.getNationalStandardCode());
            }
            if (selectedColumns.contains("assetDeptName")) {
                item.setAssetDeptId(targetAsset.getAssetDeptId());
                item.setAssetDeptUID(targetAsset.getAssetDeptUID());
            }
            if (selectedColumns.contains("assetValueCategory")) {
                item.setAssetValueCategory(targetAsset.getAssetValueCategory());
            }
            if (selectedColumns.contains("capitalSource2")) {
                item.setCapitalSource2(targetAsset.getCapitalSource2());
            }
            if (selectedColumns.contains("deviceName")) {
                item.setDeviceName(targetAsset.getDeviceName());
            }
            if (selectedColumns.contains("systemId")) {
                item.setSystemId(targetAsset.getSystemId());
            }
            if (selectedColumns.contains("financingNum")) {
                item.setFinancingNum(targetAsset.getFinancingNum());
            }
            if (selectedColumns.contains("departNum")) {
                item.setDepartNum(targetAsset.getDepartNum());
            }
            if (selectedColumns.contains("barcode")) {
                item.setBarcode(targetAsset.getBarcode());
            }
            if (selectedColumns.contains("registrationNo")) {
                item.setRegistrationNo(targetAsset.getRegistrationNo());
            }
            if (selectedColumns.contains("modalityId")) {
                item.setModalityId(targetAsset.getModalityId());
            }
            if (selectedColumns.contains("qrCode")) {
                item.setQrCode(targetAsset.getQrCode());
            }
            if (selectedColumns.contains("functionType")) {
                item.setFunctionType(targetAsset.getFunctionType());
            }
            if (selectedColumns.contains("functionGroup")) {
                item.setFunctionGroup(targetAsset.getFunctionGroup());
            }
            if (selectedColumns.contains("assetCode68")) {
                item.setAssetCode68(targetAsset.getAssetCode68());
            }
            if (selectedColumns.contains("functionGrade")) {
                item.setFunctionGrade(targetAsset.getFunctionGrade());
            }
            if (selectedColumns.contains("repairForm")) {
                item.setRepairForm(targetAsset.getRepairForm());
            }
            if (selectedColumns.contains("locationCode")) {
                item.setLocationCode(targetAsset.getLocationCode());
            }
            if (selectedColumns.contains("locationName")) {
                item.setLocationName(targetAsset.getLocationName());
            }
            if (selectedColumns.contains("status")) {
                item.setStatus(targetAsset.getStatus());
            }
            if (selectedColumns.contains("deviceStatus")) {
                item.setDeviceStatus(targetAsset.getDeviceStatus());
            }
            if (selectedColumns.contains("isAssetPMed")) {
                item.setIsPm(targetAsset.getIsPm());
            }
            if (selectedColumns.contains("isAssetChecked")) {
                item.setIsChecked(targetAsset.getIsChecked());
            }
            if (selectedColumns.contains("comments")) {
                item.setComments(targetAsset.getComments());
            }
            if (selectedColumns.contains("assetUsage")) {
                item.setAssetUsage(targetAsset.getAssetUsage());
            }
            if (selectedColumns.contains("capitalSource")) {
                item.setCapitalSource(targetAsset.getCapitalSource());
            }
            if (selectedColumns.contains("measureGrade")) {
                item.setMeasureGrade(targetAsset.getMeasureGrade());
            }
            if (selectedColumns.contains("riskGrade")) {
                item.setRiskGrade(targetAsset.getRiskGrade());
            }
            if (selectedColumns.contains("emergencyEquipment")) {
                item.setEmergencyEquipment(targetAsset.getEmergencyEquipment());
            }
            if (selectedColumns.contains("importEquipment")) {
                item.setImportEquipment(targetAsset.getImportEquipment());
            }
            if (selectedColumns.contains("measuringItemCategory")) {
                item.setMeasuringItemCategory(targetAsset.getMeasuringItemCategory());
            }
            if (selectedColumns.contains("accountingCategory")) {
                item.setAccountingCategory(targetAsset.getAccountingCategory());
            }
            if (selectedColumns.contains("manufactDate")) {
                item.setManufactDate(targetAsset.getManufactDate());
            }
            if (selectedColumns.contains("purchaseDate")) {
                item.setPurchaseDate(targetAsset.getPurchaseDate());
            }
            if (selectedColumns.contains("arriveDate")) {
                item.setArriveDate(targetAsset.getArriveDate());
            }
            if (selectedColumns.contains("installDate")) {
                item.setInstallDate(targetAsset.getInstallDate());
            }
            if (selectedColumns.contains("factoryWarrantyDate")) {
                item.setFactoryWarrantyDate(targetAsset.getFactoryWarrantyDate());
            }
            if (selectedColumns.contains("warrantyDate")) {
                item.setWarrantyDate(targetAsset.getWarrantyDate());
            }
            if (selectedColumns.contains("terminateDate")) {
                item.setTerminateDate(targetAsset.getTerminateDate());
            }
            if (selectedColumns.contains("isWarrantyNotice")) {
                item.setWarranty(targetAsset.getWarranty());
            }
            if (selectedColumns.contains("purchasePrice")) {
                item.setPurchasePrice(targetAsset.getPurchasePrice());
            }
            if (selectedColumns.contains("salvageValue")) {
                item.setSalvageValue(targetAsset.getSalvageValue());
            }
            if (selectedColumns.contains("lifecycle")) {
                item.setLifecycle(targetAsset.getLifecycle());
            }
            if (selectedColumns.contains("depreciationLifecycle")) {
                item.setDepreciationLifecycle(targetAsset.getDepreciationLifecycle());
            }
            if (selectedColumns.contains("depreciationMethod")) {
                item.setDepreciationMethod(targetAsset.getDepreciationMethod());
            }
        }

        assetInfoDao.save(assets);
    }


    public Boolean isAllParrentAssetAndChildAssetTransfer(List<String> parrentAssetUids, List<AssetInfo> assets) {

        List<Boolean> isAllTransferResult = new ArrayList<>();

        for (AssetInfo item : assets) {
            List<AssetInfo> assetInfos = null;
            if (null == item.getParentAssetId()) {
                assetInfos = assetInfoDao.findByParentAssetIdOrId(item.getId(), item.getId());
            } else {
                assetInfos = assetInfoDao.findByParentAssetIdOrId(item.getParentAssetId(), item.getParentAssetId());
            }
            List<String> assetUids = assetInfos.stream().map(AssetInfo::getUid).collect(Collectors.toList());
            if (parrentAssetUids.containsAll(assetUids)) {
                isAllTransferResult.add(true);
            } else {
                isAllTransferResult.add(false);
            }
        }

        for (Boolean result : isAllTransferResult) {
            if (result == false) {
                return false;
            }
        }
        return true;
    }

    private void commitSiteForAssetItself(List<AssetInfo> assets, AssetInfo targetAsset) {
        List<String> parrentAssetUids = assets.stream().map(AssetInfo::getUid).filter(item -> item != null).collect(Collectors.toList());
        Boolean isAllTransfer = isAllParrentAssetAndChildAssetTransfer(parrentAssetUids, assets);

        for (AssetInfo item : assets) {
            AssetInfo oldAssetInfo = assetHistoryService.depCopy(item);
            item.setSiteUID(targetAsset.getSiteOrg().getSiteUID());
            item.setHospitalUID(targetAsset.getSiteOrg().getHospitalUID());
            item.setInstitutionUID(targetAsset.getSiteOrg().getInstitutionUID());
            item.setTenantUID(targetAsset.getSiteOrg().getTenantUID());

            item.setClinicalDeptId(targetAsset.getClinicalDeptOrg().getId());
            item.setClinicalDeptUID(targetAsset.getClinicalDeptOrg().getUid());
            item.setClinicalDeptName(targetAsset.getClinicalDeptOrg().getName());

            item.setHospitalId(targetAsset.getSiteOrg().getId());

            //clear asset dept
            item.setAssetDeptId(null);
            item.setAssetDeptUID(null);

            //设备调剂设置清除
            item.setSharedAssetTypeUid(null);
            
            assetHistoryService.bathDepartmentConvert(item);
            if (!isAllTransfer) {
                item.setParentAssetId(null);
                List<AssetInfo> childAssetInfos = assetInfoDao.findChildAssets(item.getId());
                if (childAssetInfos.size() > 0) {
                    childAssetInfos.stream().forEach(childs -> childs.setParentAssetId(null));
                    assetInfoDao.save(childAssetInfos);
                }
            }
        }

        assetInfoDao.save(assets);

    }

    private void commitPmRule(List<AssetInfo> assets, AssetInfo targetAsset) {
        for (AssetInfo item : assets) {

            RelRuleAsset relRuleAsset = relRuleAssetDao.findByAssetUID(item.getUid());

            if (null == relRuleAsset) {
                continue;
            }

            relRuleAsset.setSiteUID(targetAsset.getSiteOrg().getSiteUID());
            relRuleAsset.setHospitalUID(targetAsset.getSiteOrg().getHospitalUID());
            relRuleAsset.setInstitutionUID(targetAsset.getSiteOrg().getInstitutionUID());
            relRuleAsset.setTenantUID(targetAsset.getSiteOrg().getTenantUID());

            relRuleAssetDao.save(relRuleAsset);
        }
    }

    /**
     * 维修 (step 没有site uid)
     * correctvie maintenance
     */
    private void commitCorrectiveMaintenanceBulkEdit(List<AssetInfo> assets, AssetInfo targetAsset) {
        for (AssetInfo assetInfo : assets) {
            List<V2_ServiceRequest> serviceRequests = serviceRequestDao.findByAssetId(assetInfo.getId());

            for (V2_ServiceRequest serviceRequest : serviceRequests) {
                List<V2_WorkOrder> workOrders = serviceRequest.getV2_workOrder_List();
                serviceRequest.setFromDeptId(targetAsset.getClinicalDeptOrg().getId());
                serviceRequest.setFromDeptUID(targetAsset.getClinicalDeptOrg().getUid());
                serviceRequest.setFromDeptName(targetAsset.getClinicalDeptOrg().getName());

                for (V2_WorkOrder workOrder : workOrders) {
                    List<V2_WorkOrder_Detail> details = workOrder.getV2_workOrder_detail_list();
                    List<V2_WorkOrderCoworker> coworkers = workOrder.getV2_workOrder_coworker_list();

                    updateWorkOrder(workOrder, targetAsset.getSiteOrg());
                    updateWorkOrderDetails(details, targetAsset.getSiteOrg());
                    updateWorkOrderCoworks(coworkers, targetAsset.getSiteOrg());
                }
                workOrderDao.save(workOrders);
            }

            updateServiceRequests(serviceRequests, targetAsset.getSiteOrg());

            serviceRequestDao.save(serviceRequests);
        }
    }

    /**
     * 保养 （附件没有site uid)
     * preventative maintenance
     */
    private void commitPreventativeMaintenanceBulkEdit(List<AssetInfo> assets, AssetInfo targetAsset) {
        for (AssetInfo assetInfo : assets) {
            List<V2_PmOrder> pmOrders = pmOrderDao.getByAssetUid(assetInfo.getUid());

            for (V2_PmOrder pmOrder : pmOrders) {

                List<V2_WorkOrder_Detail> details = pmOrder.getDetails();
                List<V2_WorkOrderCoworker> coworkers = pmOrder.getCoworkers();

                updatePMOrder(pmOrder, targetAsset.getSiteOrg(), targetAsset.getClinicalDeptOrg());
                updateWorkOrderDetails(details, targetAsset.getSiteOrg());
                updateWorkOrderCoworks(coworkers, targetAsset.getSiteOrg());
            }

            pmOrderDao.save(pmOrders);
        }

    }

    private void commitQrCode(List<AssetInfo> assets, AssetInfo targetAsset) {
        for (AssetInfo assetInfo : assets) {
            if (null == assetInfo.getQrCode()) {
                continue;
            }

            OrgInfo targetSite = targetAsset.getSiteOrg();

            QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(assetInfo.getQrCode(), assetInfo.getSiteUID());

            if (null == qrCodeLib) {
                continue;
            }
            qrCodeLib.setSiteUID(targetSite.getSiteUID());
            qrCodeLib.setHospitalUID(targetSite.getHospitalUID());
            qrCodeLib.setInstitutionUID(targetSite.getInstitutionUID());
            qrCodeLib.setTenantUID(targetSite.getTenantUID());

            qrCodeLibDao.save(qrCodeLib);
        }

    }

    public void commitNurseSiteConfig(List<AssetInfo> assets) {
        if (assets != null && !assets.isEmpty()) {
            List<String> uids = assets.stream().map(p -> p.getUid()).collect(Collectors.toList());
            List<NurseSiteMaintainConfigItem> assetinfos = nurseSiteMaintainConfigItemRepository.findByAssetUid(uids);
            if (assetinfos != null && !assetinfos.isEmpty()) {
                Set<String> ids = assetinfos.stream().map(p -> p.getId()).collect(Collectors.toSet());
                nurseSiteMaintainConfigItemRepository.deleteMaintainData(ids);
                List<Map<String, String>> eventData = assetinfos.stream().map(item -> {
                    Map<String, String> eventDataItem = new HashMap<>();
                    eventDataItem.put("assetUid", item.getAssetUid());
                    eventDataItem.put("configId", item.getParentId());
                    return eventDataItem;
                }).collect(Collectors.toList());
                SystemEventAudit systemEventAudit = systemEventAuditUtil.buildEventAudit(UserContextService.getCurrentUserAccount(), SystemEventAuditEnum.NURSE_SITE_MAINTAIN_CONFIG_ITEM_DELETE, JSONObject.toJSONString(uids), true, null, AuditBoTypeEnum.NURSE_SITE_MAINTAIN_CONFIG_ITEM.getBoType(), JSONObject.toJSONString(eventData));
                systemEventAuditUtil.sendSystemAuditMessage(systemEventAudit);
                Set<String> parentIds = assetinfos.stream().map(p -> p.getParentId()).collect(Collectors.toSet());
                List<NurseSiteMaintainConfigItem> items = nurseSiteMaintainConfigItemRepository.findByParentIds(parentIds);
                if (items != null && !items.isEmpty()) {
                    Set<String> temp = items.stream().map(u -> u.getParentId()).collect(Collectors.toSet());
                    parentIds.removeAll(temp);
                }
                /*if (parentIds != null && !parentIds.isEmpty()) {
                    nurseSiteMaintainConfigRepository.deleteMaintainConfigData(parentIds);
                }*/
            }
        }
    }

    public void commitAssetTagRule(List<AssetInfo> assets) {
        if (assets != null && !assets.isEmpty()) {
            List<Integer> uids = assets.stream().map(p -> p.getId()).collect(Collectors.toList());
            List<AssetTagRule> assetinfos = assetTagRuleRepository.findByAssetUid(uids);
            if (assetinfos != null && !assetinfos.isEmpty()) {
                Set<Integer> ids = assetinfos.stream().map(p -> p.getId()).collect(Collectors.toSet());
                assetTagRuleRepository.deleteAssetTagRuleData(ids);
            }
        }
    }

    public List<AssetInfo> findBathUpdateAssetInfos(Map<String, Object> param) {
        List<AssetInfo> assetInfos = assetInfoMapper.findByParam(param);
        bathUpdateAssetInfosMap = assetInfos.stream().collect(Collectors.toMap(AssetInfo::getId, asset -> asset));
        return assetInfoMapper.findByParam(param);
    }

    Map<Integer, AssetInfo> bathUpdateAssetInfosMap;

    public Map<Integer, AssetInfo> getBathUpdateAssetInfosMap() {
        if (bathUpdateAssetInfosMap == null) {
            return MapUtil.newHashMap();
        }
        return bathUpdateAssetInfosMap;
    }


}
