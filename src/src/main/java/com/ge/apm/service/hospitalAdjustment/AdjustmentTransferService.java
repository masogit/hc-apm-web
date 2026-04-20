package com.ge.apm.service.hospitalAdjustment;

import com.alibaba.fastjson.JSON;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.TextRenderData;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.approval.WxMessageService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.core.message.MSGEvent;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.utils.date.DatePattern;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.deepoove.poi.data.style.Style;

import java.io.IOException;
import java.util.*;



@Service
public class AdjustmentTransferService {
    public static final Logger log = LoggerFactory.getLogger(AdjustmentTransferService.class);

    @Autowired
    private IdleEquipmentStockRepository stockRepository;
    @Autowired
    private AdjustmentTransferApplicationRepository dao;

    @Autowired
    private AssetInfoRepository assetInfoRepository;
    @Autowired
    private OrgInfoRepository orgInfoRepository;

    @Autowired
    private CustomRoleRepository customRoleRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private WxMessageService wxMessageService;
    @Autowired
    private SystemTemplateService systemTemplateService;

    @Autowired
    private BlobFileService blobFileService;

    @Autowired
    private MqUtils mqUtils;
    @Transactional
    public void cancel(AdjustmentTransferApplication selected, UserAccount currentUserAccount){
//        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
//        if(byUid.getStatus()!=1 && !selected.getApplicationUid().equals(currentUserAccount.getUid())){
//            WebUtil.addWarnMessage("无权操作,请检查订单状态");
//            return;
//        }
        selected.setStatus(3);
        selected.setUpdateTime(new Date());
        selected.setUpdatedBy(currentUserAccount.getUid());
        IdleEquipmentStock idleEquipmentStock = stockRepository.findByUid(selected.getStockUid());
        idleEquipmentStock.setBorrowHospitalUid(null);
        idleEquipmentStock.setBorrowSiteUid(null);
        idleEquipmentStock.setBorrowDeptUid(null);
        idleEquipmentStock.setStatus(1);
        idleEquipmentStock.setLastModifiedDate(new Date());
        idleEquipmentStock.setLastModifiedBy(currentUserAccount.getUid());
        stockRepository.save(idleEquipmentStock);
        dao.save(selected);
    }

    @Transactional
    public void   confirmReceipt(AdjustmentTransferApplication selected,UserAccount currentUserAccount) {
//        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
//        if(byUid.getStatus()!=4  && !selected.getApplicationUid().equals(currentUserAccount.getUid())){
//            WebUtil.addWarnMessage("无权操作,请检查订单状态");
//            return;
//        }

        selected.setStatus(5);
        selected.setUpdateTime(new Date());
        selected.setUpdatedBy(currentUserAccount.getUid());
        selected.setDeliveryUid(currentUserAccount.getUid());
        selected.setDeliveryDate(new Date());
        IdleEquipmentStock idleEquipmentStock = stockRepository.findByUid(selected.getStockUid());
        OrgInfo orgInfo = orgInfoRepository.getByUid(selected.getBorrowDeptUid());

        String assetUid = idleEquipmentStock.getAssetUid();
        AssetInfo assetInfo = assetInfoRepository.getByUid(assetUid);
        assetInfo.setClinicalDeptUID(selected.getBorrowDeptUid());
        assetInfo.setClinicalDeptName(orgInfo.getName());
        assetInfo.setAssetDeptId(orgInfo.getId());
        assetInfo.setAssetDeptUID(orgInfo.getUid());
        assetInfo.setSiteUID(selected.getBorrowSiteUid());
        assetInfo.setHospitalUID(orgInfo.getHospitalUID());

        idleEquipmentStock.setStatus(3);
        idleEquipmentStock.setLastModifiedDate(new Date());
        idleEquipmentStock.setLastModifiedBy(currentUserAccount.getUid());
        stockRepository.save(idleEquipmentStock);
        assetInfoRepository.save(assetInfo);

        MqContext mqContext = new MqContext();
        mqContext.setClassName("AdjustmentTransferService");
        mqContext.setServiceName("hc-apm-web");
        mqContext.setMethodName("confirmReceipt");
        mqContext.setDescribe("院区间调剂借用确认到货");

        mqUtils.sendAssetToMQ(assetInfo, currentUserAccount, mqContext);
        dao.save(selected);
    }


    @Transactional
    public void   returnPass(AdjustmentTransferApplication selected,UserAccount currentUserAccount) {
//        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
//
//        if(byUid.getStatus()!=7  ||  !userContextService.accessContainsFunctionMenu(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_ADJUSTMENT_ACCEPTANCE.name())  ){
//            WebUtil.addWarnMessage("无权操作,请检查订单状态");
//            return;
//        }

        selected.setStatus(8);
        selected.setReturnApprovalDate(new Date());
        selected.setReturnApprovalUid(currentUserAccount.getUid());
        selected.setReturnApprovalResult(1);
        IdleEquipmentStock idleEquipmentStock = stockRepository.findByUid(selected.getStockUid());
        idleEquipmentStock.setStatus(4);
        idleEquipmentStock.setBorrowHospitalUid(null);
        idleEquipmentStock.setBorrowDeptUid(null);
        idleEquipmentStock.setBorrowSiteUid(null);
        idleEquipmentStock.setLastModifiedBy(currentUserAccount.getUid());
        idleEquipmentStock.setLastModifiedDate(new Date());
        OrgInfo orgInfo = orgInfoRepository.getByUid(selected.getStockDeptUid());
        AssetInfo assetInfo = assetInfoRepository.getByUid(idleEquipmentStock.getAssetUid());
        assetInfo.setClinicalDeptUID(selected.getStockDeptUid());
        assetInfo.setClinicalDeptName(orgInfo.getName());
        assetInfo.setAssetDeptUID(selected.getStockDeptUid());
        assetInfo.setAssetDeptId(orgInfo.getId());
        assetInfo.setSiteUID(selected.getStockSiteUid());
        assetInfo.setHospitalUID(orgInfo.getHospitalUID());
        assetInfoRepository.save(assetInfo);
        stockRepository.save(idleEquipmentStock);
        dao.save(selected);


        MqContext mqContext = new MqContext();
        mqContext.setClassName("AdjustmentTransferService");
        mqContext.setServiceName("hc-apm-web");
        mqContext.setMethodName("returnPass");
        mqContext.setDescribe("院区间调剂借用设备退回");
        mqUtils.sendAssetToMQ(assetInfo, currentUserAccount, mqContext);


    }

    @Transactional

    public void   returnRefuse(AdjustmentTransferApplication selected,UserAccount currentUserAccount) {
//        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
//
//        if(byUid.getStatus()!=7  ||  !userContextService.accessContainsFunctionMenu(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_ADJUSTMENT_ACCEPTANCE.name())  ){
//            WebUtil.addWarnMessage("无权操作,请检查订单状态");
//            return;
//        }
        selected.setStatus(9);

        selected.setReturnApprovalDate(new Date());
        selected.setReturnApprovalUid(currentUserAccount.getUid());
        selected.setReturnApprovalResult(2);

        selected.setUpdatedBy(currentUserAccount.getUid());
        selected.setUpdateTime(new Date());
        IdleEquipmentStock idleEquipmentStock = stockRepository.findByUid(selected.getStockUid());
        idleEquipmentStock.setStatus(1);
        idleEquipmentStock.setBorrowHospitalUid(null);
        idleEquipmentStock.setBorrowDeptUid(null);
        idleEquipmentStock.setBorrowSiteUid(null);
        idleEquipmentStock.setLastModifiedBy(currentUserAccount.getUid());
        idleEquipmentStock.setLastModifiedDate(new Date());
        OrgInfo orgInfo = orgInfoRepository.getByUid(selected.getStockDeptUid());
        AssetInfo assetInfo = assetInfoRepository.getByUid(idleEquipmentStock.getAssetUid());
        assetInfo.setClinicalDeptUID(selected.getStockDeptUid());
        assetInfo.setClinicalDeptName(orgInfo.getName());
        assetInfo.setAssetDeptUID(selected.getStockDeptUid());
        assetInfo.setAssetDeptId(orgInfo.getId());
        assetInfo.setSiteUID(selected.getStockSiteUid());
        assetInfo.setHospitalUID(orgInfo.getHospitalUID());
        assetInfoRepository.save(assetInfo);
        stockRepository.save(idleEquipmentStock);
        dao.save(selected);

        MqContext mqContext = new MqContext();
        mqContext.setClassName("AdjustmentTransferService");
        mqContext.setServiceName("hc-apm-web");
        mqContext.setMethodName("returnRefuse");
        mqContext.setDescribe("院区间调剂借用设备退回");
        mqUtils.sendAssetToMQ(assetInfo, currentUserAccount, mqContext);
    }

    @Transactional
    public void refuse(AdjustmentTransferApplication selected,UserAccount userAccount) {
        selected.setStatus(2);
        selected.setUpdateTime(new Date());
        selected.setUpdatedBy(userAccount.getUid());
        selected.setApplicationActualApprovalUid(userAccount.getUid());

        IdleEquipmentStock idleEquipmentStock = stockRepository.findByUid(selected.getStockUid());

        idleEquipmentStock.setLastModifiedDate(new Date());
        idleEquipmentStock.setLastModifiedBy(userAccount.getUid());
        idleEquipmentStock.setStatus(1);
        idleEquipmentStock.setBorrowHospitalUid(null);
        idleEquipmentStock.setBorrowSiteUid(null);
        idleEquipmentStock.setBorrowDeptUid(null);
        stockRepository.save(idleEquipmentStock);
        dao.save(selected);
    }


    public void approvalMsg(AdjustmentTransferApplication adjustmentTransferApplication){
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("orderCode", adjustmentTransferApplication.getApplicationNumber());
        stringMap.put("content", "设备调剂申请已通过，请确认信息后线下交接");
        stringMap.put("date", DateUtil.formatDateTime(new Date()));

        List<String> byIdsAndHospitalUID = getAdjustmentTransferApproval(adjustmentTransferApplication,true,false);
        wxMessageService.sendWxMsgToMq(stringMap,null,MSGEvent.HOSPITAL_ADJUSTMENT_BORROW_APPROVAL_NOTICE,byIdsAndHospitalUID,null);
    }

    public List<String> getAdjustmentTransferApproval(AdjustmentTransferApplication adjustmentTransferApplication,boolean in,boolean out) {
        List<CustomRole> customRoles = customRoleRepository.findByTenantUidAndFunctionListContains(adjustmentTransferApplication.getTenantUid());
        List<Integer> tenantRoleIds = new ArrayList<>();

        for (CustomRole customRole : customRoles) {
            List<String> strings = JSON.parseArray(customRole.getFunctionList(), String.class);
            if(CollectionUtils.isNotEmpty(strings) && strings.contains(ApmFunction.HOSPITAL_ADJUSTMENT_WORKBENCH.name())){
                tenantRoleIds.add(customRole.getId());
            }
        }
        if(CollectionUtils.isEmpty(tenantRoleIds)){
            log.info("租户没有配置自定义调剂角色tenantUid:{}", adjustmentTransferApplication.getTenantUid());
        }
        List<Integer> userIds = userAccountRepository.findByCustomRoleId(tenantRoleIds);

        List<String> strings = new ArrayList<>();
        if(out){
            String stockHospitalUid = adjustmentTransferApplication.getStockHospitalUid();
            strings.add(stockHospitalUid);
        }
        if(in){
            String borrowHospitalUid = adjustmentTransferApplication.getBorrowHospitalUid();
            strings.add(borrowHospitalUid);
        }
        List<String> byIdsAndHospitalUID = userAccountRepository.getByIdsAndHospitalUID(userIds, strings);
        return byIdsAndHospitalUID;
    }


    public List<UserAccount>  getApprovalUser(String tenantUid,List<ApmFunction> functions){
        List<CustomRole> customRoles = customRoleRepository.findByTenantUidAndFunctionListContains(tenantUid);
        List<Integer> tenantRoleIds = new ArrayList<>();

        for (CustomRole customRole : customRoles) {
            List<String> strings = JSON.parseArray(customRole.getFunctionList(), String.class);
            for (ApmFunction function : functions) {
                if(CollectionUtils.isNotEmpty(strings) && strings.contains(function.name())){
                    tenantRoleIds.add(customRole.getId());
                }
            }
        }
        if(CollectionUtils.isEmpty(tenantRoleIds)){
            log.info("租户没有配置自定义调剂角色tenantUid:{}",tenantUid);
            return new ArrayList<>();
        }
        List<Integer> userIds = userAccountRepository.findByCustomRoleId(tenantRoleIds);

        return userAccountRepository.getByIds(new HashSet<>(userIds));
    }

    public  byte[] download(AdjustmentTransferApplication selected ) throws IOException {

        AdjustmentTransferApplication adjustmentTransferApplication = dao.findByUid(selected.getUid());
        UserAccount user = UserContextService.getCurrentUserAccount();
        byte[] byteArray = systemTemplateService.getTemplateFileByte(SystemTemplateEnum.HOSPITAL_ADJUSTMENT_APPLICATION, user);
        HashMap<String, Object> datas= buildDatas(adjustmentTransferApplication);
        return WordExportServiceUtil.generate(datas, byteArray, null);
    }

    TextRenderData checked = new TextRenderData("R", new Style("Wingdings 2", 10.5));

    TextRenderData unchecked = new TextRenderData("\u00A3", new Style("Wingdings 2", 10.5));
    private HashMap<String, Object> buildDatas(AdjustmentTransferApplication adjustmentTransferApplication) {

        HashMap<String, Object> params = new HashMap<>();
        IdleEquipmentStock idleEquipmentStock = adjustmentTransferApplication.getIdleEquipmentStock();
        AssetInfo assetInfo = assetInfoRepository.findByUid(idleEquipmentStock.getAssetUid());

        params.put("ck_lead", "1".equals(adjustmentTransferApplication.getMedicalCommunityType()) ? checked : unchecked);
        params.put("ck_town", "2".equals(adjustmentTransferApplication.getMedicalCommunityType())  ? checked : unchecked);
        params.put("ck_center", "3".equals(adjustmentTransferApplication.getMedicalCommunityType())  ? checked : unchecked);
        params.put("ck_station", "4".equals(adjustmentTransferApplication.getMedicalCommunityType())  ? checked : unchecked);
        OrgInfo borrowDeptName = orgInfoRepository.getByUid(adjustmentTransferApplication.getBorrowDeptUid());
        params.put("unit_name",adjustmentTransferApplication.getUnitName());
        params.put("dept_name",borrowDeptName.getName());
        params.put("contact_person",adjustmentTransferApplication.getContactPerson());
        params.put("contact_phone",adjustmentTransferApplication.getContactPhone());
        params.put("application_date",DateUtil.format(adjustmentTransferApplication.getApplicationDate(), DatePattern.CHINESE_DATE_FORMAT));

        params.put("check_diagnosis", "DIAGNOSTIC_EQUIPMENT".equals(adjustmentTransferApplication.getDeviceType()) ? checked : unchecked);
        params.put("check_treatment", "THERAPEUTIC_EQUIPMENT".equals(adjustmentTransferApplication.getDeviceType())  ? checked : unchecked);
        params.put("check_assist", "AUXILIARY_MEDICAL_EQUIPMENT".equals(adjustmentTransferApplication.getDeviceType())  ? checked : unchecked);
        params.put("check_general", "GENERAL_MEDICAL_EQUIPMENT".equals(adjustmentTransferApplication.getDeviceType())  ? checked : unchecked);
        params.put("check_other", "OTHER".equals(adjustmentTransferApplication.getDeviceType())  ? checked : unchecked);
        params.put("other_device_type_note", adjustmentTransferApplication.getOtherDeviceTypeNote());

        params.put("asset_name",assetInfo.getName());
        params.put("core_technical_params",adjustmentTransferApplication.getCoreTechnicalParams());
        params.put("secondary_technical_params",adjustmentTransferApplication.getSecondaryTechnicalParams());
        params.put("estimated_usage_years",adjustmentTransferApplication.getEstimatedUsageYears());
        params.put("check_emergency", "EMERGENCY".equals(adjustmentTransferApplication.getDescriptionOfUse()) ? checked : unchecked);
        params.put("check_specialty", "SPECIALTY".equals(adjustmentTransferApplication.getDescriptionOfUse())  ? checked : unchecked);
        params.put("check_routine", "ROUTINE".equals(adjustmentTransferApplication.getDescriptionOfUse())  ? checked : unchecked);
        params.put("check_rescue", "RESCUE".equals(adjustmentTransferApplication.getDescriptionOfUse())  ? checked : unchecked);
        params.put("check_purpose_other", "OTHER".equals(adjustmentTransferApplication.getDescriptionOfUse())  ? checked : unchecked);
        params.put("other_description_of_use_note",adjustmentTransferApplication.getOtherDescriptionOfUseNote());

        params.put("ready", "READY".equals(adjustmentTransferApplication.getSiteConditionStatus()) ? checked : unchecked);
        params.put("needs_renovauion", "NEEDS_RENOVATION".equals(adjustmentTransferApplication.getSiteConditionStatus())  ? checked : unchecked);
        params.put("site_renovation_plan", adjustmentTransferApplication.getSiteRenovationPlan());

        params.put("satisfied", "SATISFIED".equals(adjustmentTransferApplication.getPowerSupplyStatus()) ? checked : unchecked);
        params.put("needs_capacity_increase", "NEEDS_CAPACITY_INCREASE".equals(adjustmentTransferApplication.getPowerSupplyStatus())  ? checked : unchecked);
        params.put("power_capacity_demand", adjustmentTransferApplication.getPowerCapacityDemand());

        params.put("staffed", "STAFFED".equals(adjustmentTransferApplication.getOperatorStatus()) ? checked : unchecked);
        params.put("needs_training", "NEEDS_TRAINING".equals(adjustmentTransferApplication.getOperatorStatus())  ? checked : unchecked);
        params.put("operator_qualification_note", adjustmentTransferApplication.getOperatorQualificationNote());
        params.put("operator_training_plan", adjustmentTransferApplication.getOperatorTrainingPlan());


        params.put("self_maintenance", "SELF_MAINTENANCE".equals(adjustmentTransferApplication.getMaintenanceCapability()) ? checked : unchecked);
        params.put("needs_tech_support", "NEEDS_TECH_SUPPORT".equals(adjustmentTransferApplication.getMaintenanceCapability())  ? checked : unchecked);



        params.put("matched", "matched".equals(adjustmentTransferApplication.getInitialScreeningResult()) ? checked : unchecked);
        params.put("unmatched", "unmatched".equals(adjustmentTransferApplication.getInitialScreeningResult())  ? checked : unchecked);
        params.put("screening_reason", adjustmentTransferApplication.getScreeningReason());
        params.put("none", "none".equals(adjustmentTransferApplication.getTechnicalAssessment()) ? checked : unchecked);
        params.put("needed", "needed".equals(adjustmentTransferApplication.getTechnicalAssessment())  ? checked : unchecked);

        if(adjustmentTransferApplication.getHandlerSignature()!=null){
            byte[] handlerSignature = getSignatureBytes(adjustmentTransferApplication.getHandlerSignature());
            params.put("handler_signature", new PictureRenderData(100, 70, PictureType.PNG, handlerSignature));

        }
        params.put("assessment_time",DateUtil.format(adjustmentTransferApplication.getAssessmentTime(), DatePattern.CHINESE_DATE_FORMAT));
        params.put("application_pending_approval_date",DateUtil.format(adjustmentTransferApplication.getApplicationPendingApprovalDate(), DatePattern.CHINESE_DATE_FORMAT));


        List<String> strings = JSON.parseArray(adjustmentTransferApplication.getAttachments(), String.class);
        if(CollectionUtils.isNotEmpty(strings)){
            params.put("DemandDocument", strings.contains("DemandDocument")? checked : unchecked);
            params.put("SitePowerProof", strings.contains("SitePowerProof")? checked : unchecked);
            params.put("OperatorQualification", strings.contains("OperatorQualification")? checked : unchecked);
            params.put("other", strings.contains("other")? checked : unchecked);
            if(strings.contains("other")){
                params.put("attachments_other_description", adjustmentTransferApplication.getAttachmentsOtherDescription());
            }
        }


        return params;
    }

    public byte[] getSignatureBytes(String storageId) {
        byte[] signatureBytes = null;

        try {
            signatureBytes = IOUtils.toByteArray(blobFileService.getBlobObjectByStorageId(storageId).getStream());
        } catch (IOException e) {
            log.info("Error happens while get signature bytes", e);
        }

        return signatureBytes;
    }


}
