package com.ge.apm.service.wo;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.WorkOrderReportMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.WorkOrderExportPojo;
import com.ge.apm.service.constant.WorkOrderConstant;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 212605082 on 2017/7/25.
 */
@Component
@Scope("prototype")
public class WorkOrderExportService {

    private static final String SHEET_WORKORDER = "WorkOrder List";
    private static final String SHEET_PARTS = "Parts List";
    private static final String I18N_INT_EXT_TYPE = "intExtType";
    private static final String I18N_REPAIR_TYPE = "repairType";
    private static final String I18N_CURRENT_STEP_ = "woSteps";
    private static final String I18N_REPAIR_RESULT = "repairResult";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Logger log = LoggerFactory.getLogger(WorkOrderExportService.class);
    @Autowired
    ServiceRequestRepository serviceRequestDao;
    @Autowired
    WorkOrderDetailRepository workOrderDetailDao;
    @Autowired
    AssetInfoRepository assetInfoDao;
    @Autowired
    AssetFaultTypeRepository assetFaultTypeDao;
    @Autowired
    I18nMessageRepository i18nMessageDao;
    @Autowired
    V2_WorkOrderCoworkerRepository workOrderCoworkerDao;
    @Autowired
    UserAccountRepository userAccountDao;
    @Autowired
    private TenantInfoRepository tenantInfoRepository;
    @Autowired
    private AuditGroupConfigurationDetailRepository auditGroupConfigurationDetailRepository;
    @Autowired
    private OrgInfoTreeService orgInfoTreeService;
    @Autowired
    private WorkOrderReportMapper worDao;
    @Autowired
    private ServiceRequestApiService serviceRequestApiService;
    @Autowired
    private MultiLevelApprovalRepository multiLevelApprovalRepository;
    @Autowired
    private V2_WorkOrderService woService;
    @Autowired
    private BlobObjectRepository blobObjectRepository;

    public List<Integer> getVisableMvAssetIds(OrgInfo siteOrg,OrgInfo clinicalDepte){
        String token = UserContextService.getAccessToken();
        if(clinicalDepte!=null) {
            return serviceRequestApiService.getVisableMvsAssetSystemIdByClinaicalDept(token,clinicalDepte.getUid());
        } else if(siteOrg!=null) {
            return serviceRequestApiService.getVisableMvsAssetSystemIdBySite(token,siteOrg.getUid());
        } else {
            return serviceRequestApiService.getVisableMvsAssetSystemId(token);
        }
    }

    public List<Map<String, Object>> createPreviewWorkOrderData(OrgInfo selectedOrg, Date startTime, Date endTime, Date closedStartTime, Date closedEndTime, List<Integer> statusList,OrgInfo clinicalDept,UserAccount currentOwner, List<OrgInfo> clinicalDeptList, String filterParts, String filterTotalPrice,Map<String,String> filterMap,String currentStep, Integer assetInfoId){
        List<Map<String, Object>> resultList = new ArrayList<>();

        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();

        if (selectedOrg != null && selectedOrg.getOrgLevel() == 0) {
            SearchFilter tenantFilter = new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(tenantFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 1) {
            SearchFilter institutionFilter = new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(institutionFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 2) {
            SearchFilter hospitalFilter = new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(hospitalFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 3) {
            SearchFilter siteFilter = new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(siteFilter);
        }

        if (selectedOrg != null) {
            SearchFilter siteFilter = new SearchFilter("orgUID", SearchFilter.Operator.IN, selectedOrg.getUid());
            searchFilters.add(siteFilter);
        }

        if(clinicalDept !=null){
            List<OrgInfo> orgSearchList = new ArrayList<>();
            orgSearchList.add(clinicalDept);

            Set<OrgInfo> tempOrgSearchSet = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgSearchList, clinicalDeptList);
            List<String> clinicalDeptUIDList = new ArrayList<>();
            for (OrgInfo oi : tempOrgSearchSet) {
                clinicalDeptUIDList.add(oi.getUid());
            }
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, clinicalDeptUIDList));
        }
        if(currentOwner !=null) {
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, currentOwner.getId()));
        }

        ZoneId zone = ZoneId.systemDefault();

        if (startTime != null) {
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.GTE, startTime));
        }
        if (endTime != null) {
            LocalDate endDate = LocalDateTime.ofInstant(endTime.toInstant(), zone).toLocalDate();
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.LT, Date.from(endDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant())));
        }
        if (closedStartTime != null) {
            searchFilters.add(new SearchFilter("closeTime", SearchFilter.Operator.GTE, closedStartTime));
        }
        if (closedEndTime != null) {
            LocalDate closeEndDate = LocalDateTime.ofInstant(closedEndTime.toInstant(), zone).toLocalDate();
            searchFilters.add(new SearchFilter("closeTime", SearchFilter.Operator.LT, Date.from(closeEndDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant())));
        }
        if (statusList != null && !statusList.isEmpty()) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.IN, statusList));
        }
        if(filterParts != null){
            searchFilters.add(new SearchFilter("filterParts", SearchFilter.Operator.EQ, filterParts));
        }
        if(filterTotalPrice != null){
            searchFilters.add(new SearchFilter("filterTotalPrice", SearchFilter.Operator.EQ, filterTotalPrice));
        }
        if(assetInfoId != null){
            searchFilters.add(new SearchFilter("assetInfoId", SearchFilter.Operator.EQ, assetInfoId));
        }
        List<Integer> assetIds = getVisableMvAssetIds(selectedOrg,clinicalDept);
        List<WorkOrderExportPojo> workOrderList = worDao.workOrderExport(searchFilters);
        if(assetIds.size()>0) {
            List<Integer> orderAssetIds = workOrderList.stream().map(WorkOrderExportPojo::getAssetId).collect(Collectors.toList());
            List<Integer> mvsAssetIds = assetIds.stream().filter(assetId -> !orderAssetIds.contains(assetId)).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetIds", SearchFilter.Operator.IN, mvsAssetIds));
            workOrderList.addAll(worDao.workOrderExportForMVS(searchFilters));
        }
        workOrderList = workOrderList.stream().filter(workOrderExportPojo -> {
            workOrderExportPojo.setMVSWorkOrder(assetIds.contains(workOrderExportPojo.getAssetId()));
            boolean isReturn = true;
            if(filterMap.get("srNum") != null && filterMap.get("srNum").length() > 0) {   //报修单号
                if(workOrderExportPojo.getSrNum()!=null) {
                    isReturn = workOrderExportPojo.getSrNum().contains(filterMap.get("srNum").trim());
                } else {
                    isReturn = false;
                }
            }
            if(isReturn && filterMap.get("assetDepartNum")!=null && filterMap.get("assetDepartNum").length()>0) {  //设备编号
                if(workOrderExportPojo.getAssetDepartNum()!=null) {
                    isReturn = workOrderExportPojo.getAssetDepartNum().contains(filterMap.get("assetDepartNum").trim());
                } else {
                    isReturn = false;
                }
            }
            if(isReturn  && filterMap.get("assetName")!=null && filterMap.get("assetName").length()>0) {    //设备名称
                if(workOrderExportPojo.getAssetName()!=null) {
                    isReturn = workOrderExportPojo.getAssetName().contains(filterMap.get("assetName").trim());
                } else {
                    isReturn = false;
                }
            }
            if(isReturn && filterMap.get("assetFunctionType")!=null && filterMap.get("assetFunctionType").length()>0) {  //设备型号
                if(workOrderExportPojo.getAssetFunctionType()!=null) {
                    isReturn = workOrderExportPojo.getAssetFunctionType().contains(filterMap.get("assetFunctionType").trim());
                } else {
                    isReturn = false;
                }
            }
            if(isReturn  && filterMap.get("requestorName")!=null && filterMap.get("requestorName").length()>0) {  //报修人
                if(workOrderExportPojo.getRequestorName()!=null && filterMap.get("requestorName")!=null) {
                    isReturn = workOrderExportPojo.getRequestorName().contains(filterMap.get("requestorName").trim());
                } else {
                    isReturn = false;
                }
            }
            if(isReturn && filterMap.get("requestReason")!=null && filterMap.get("requestReason").length()>0) {  //故障描述
                if(workOrderExportPojo.getRequestReason()!=null) {
                    isReturn = workOrderExportPojo.getRequestReason().contains(filterMap.get("requestReason").trim());
                } else {
                    isReturn = false;
                }
            }
            if(isReturn && currentStep!=null) {  //当前步骤
                if(workOrderExportPojo.getCurrentStepId()!=null) {
                    isReturn = Integer.parseInt(currentStep)==workOrderExportPojo.getCurrentStepId();
                } else {
                    isReturn = false;
                }
            }
            return isReturn;
        }).collect(Collectors.toList());

        for(WorkOrderExportPojo workOrderExportPojo : workOrderList){
            Map<String, Object> workOrderMap = new HashedMap();

            workOrderMap.put("woId", workOrderExportPojo.getId());
            workOrderMap.put(WebUtil.getMessage("srNum_xlsx"), workOrderExportPojo.getSrNum() != null ? workOrderExportPojo.getSrNum() : "");
            workOrderMap.put(WebUtil.getMessage("EquipmentID_xlsx"), workOrderExportPojo.getAssetDepartNum());
            //workOrderMap.put("工单状态", workOrderExportPojo.getStatusName());
            workOrderMap.put(WebUtil.getMessage("WorkorderStatus_xlsx"), workOrderExportPojo.getStatus() != null ? FieldValueMessageController.fieldValue("status", workOrderExportPojo.getStatus().toString()) : "");
            workOrderMap.put(WebUtil.getMessage("AssetID_xlsx"), workOrderExportPojo.getAssetFinancingNum());
            workOrderMap.put(WebUtil.getMessage("EquipmentSerialNumber_xlsx"), workOrderExportPojo.getAssetSerialNum());
            workOrderMap.put(WebUtil.getMessage("EquipmentName_xlsx"), workOrderExportPojo.getAssetName());
            workOrderMap.put(WebUtil.getMessage("Site"), workOrderExportPojo.getHospitalName());
            workOrderMap.put(WebUtil.getMessage("Department"), workOrderExportPojo.getAssetClinicalDeptName());
            workOrderMap.put(WebUtil.getMessage("manufacture"), workOrderExportPojo.getAssetManufacture());
            workOrderMap.put(WebUtil.getMessage("brand"), workOrderExportPojo.getAssetMakerShortName());
            workOrderMap.put(WebUtil.getMessage("MaintenanceSubStatus"), getSubStatus(workOrderExportPojo.getSubStatus()));
            workOrderMap.put(WebUtil.getMessage("CurrentOwner_xlsx"), workOrderExportPojo.getCurrentPersonName());
            //workOrderMap.put("当前步骤", workOrderExportPojo.getCurrentStepName());
            Integer currentStepId = workOrderExportPojo.getCurrentStepId();
            UserContextService userContextService = WebUtil.getBean(UserContextService.class);
            if(userContextService.hasCurrentTenantName("")){
                if (currentStepId == null) {
                    workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"), "");
                }else if(currentStepId == 7 || currentStepId == 9){//云阜用户 已关单
                    currentStepId = 9;
                    workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"),  FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, currentStepId.toString()));
                }else if(currentStepId == 2 || currentStepId == 3){//云阜用户 待接单
                    currentStepId = 3;
                    workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"),  FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, currentStepId.toString()));
                }else{
                    workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"),  FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, currentStepId.toString()));
                }
            }else {
                workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"), currentStepId != null ? FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, currentStepId.toString()) : "");
            }

            workOrderMap.put("是否MVS工单",workOrderExportPojo.getMVSWorkOrder()? "是" : "否");
            workOrderMap.put(WebUtil.getMessage("requestorId"), workOrderExportPojo.getRequestorName());
            workOrderMap.put(WebUtil.getMessage("RequestTime_xlsx"), formatDate(workOrderExportPojo.getRequestTime()));
            workOrderMap.put(WebUtil.getMessage("FaultDescription_xlsx"), workOrderExportPojo.getRequestReason());
            workOrderMap.put(WebUtil.getMessage("dispatchUserName"), workOrderExportPojo.getSendStepOwnerName());
            workOrderMap.put(WebUtil.getMessage("AssignTime_xlsx"), formatDate(workOrderExportPojo.getSendStepStartTime()));
            //workOrderMap.put("工单类型", workOrderExportPojo.getIntExtTypeName());
            workOrderMap.put(WebUtil.getMessage("WorkorderType_xlsx"), workOrderExportPojo.getIntExtType() != null ? FieldValueMessageController.fieldValue(I18N_INT_EXT_TYPE, workOrderExportPojo.getIntExtType().toString()) : "");
            //workOrderMap.put("维修类型", workOrderExportPojo.getRepairTypeName());
            workOrderMap.put(WebUtil.getMessage("RepairType_xlsx"), workOrderExportPojo.getRepairType() != null ? FieldValueMessageController.fieldValue(I18N_REPAIR_TYPE, workOrderExportPojo.getRepairType().toString()) : "");
            workOrderMap.put(WebUtil.getMessage("RepairResult"), workOrderExportPojo.getRepairResult() != null ? FieldValueMessageController.fieldValue(I18N_REPAIR_RESULT, workOrderExportPojo.getRepairResult().toString()) : "");
            workOrderMap.put(WebUtil.getMessage("Acceptedby_xlsx"), workOrderExportPojo.getAcceptStepOwnerName());
            workOrderMap.put(WebUtil.getMessage("AcceptTime_xlsx"), formatDate(workOrderExportPojo.getAcceptStepStartTime()));
            workOrderMap.put(WebUtil.getMessage("RepairCompleteTime_xlsx"), formatDate(workOrderExportPojo.getWorkStepEndTime()));
            workOrderMap.put(WebUtil.getMessage("CheckInTime_xlsx"), formatDate(workOrderExportPojo.getCheckinTime()));
            workOrderMap.put(WebUtil.getMessage("MaintenanceEngineerManhours_xlsx"), workOrderExportPojo.getManHours());
            workOrderMap.put(WebUtil.getMessage("TotalManhours_xlsx"), workOrderExportPojo.getTotalManHour());
            workOrderMap.put(WebUtil.getMessage("RepairPart_xlsx"), workOrderExportPojo.getParts());
            workOrderMap.put(WebUtil.getMessage("TotalPartPrice_xlsx"), workOrderExportPojo.getPartsTotalPrice());
            workOrderMap.put(WebUtil.getMessage("OtherExpenses_xlsx"), workOrderExportPojo.getOtherExpense());
            workOrderMap.put(WebUtil.getMessage("ExternalExpenses_xlsx"), workOrderExportPojo.getExternalExpense());
            workOrderMap.put(WebUtil.getMessage("TotalRepairPrice_xlsx"), workOrderExportPojo.getTotalPrice());
            workOrderMap.put(WebUtil.getMessage("AcknowledgedBy_xlsx"), workOrderExportPojo.getEvaluateStepOwnerName());
            workOrderMap.put(WebUtil.getMessage("maintanenceValue_xlsx"), workOrderExportPojo.getMaintenanceValue());
            workOrderMap.put(WebUtil.getMessage("AcceptTime_xlsx2"), formatDate(workOrderExportPojo.getEvaluateStepEndTime()));
            workOrderMap.put(WebUtil.getMessage("Closedby_xlsx"), workOrderExportPojo.getCloseStepOwnerName());
            workOrderMap.put(WebUtil.getMessage("CloseTime_xlsx"), formatDate(workOrderExportPojo.getCloseStepEndTime()));
            workOrderMap.put(WebUtil.getMessage("Coworker"), workOrderExportPojo.getCoworker());
            workOrderMap.put(WebUtil.getMessage("CoworkerManhours_xlsx"), workOrderExportPojo.getCoworkManHour());
            workOrderMap.put(WebUtil.getMessage("FinalRating_xlsx"), workOrderExportPojo.getFeedbackRating());
            workOrderMap.put(WebUtil.getMessage("FaultType_xlsx"), workOrderExportPojo.getCaseTypeName());
            workOrderMap.put(WebUtil.getMessage("ReasonDescription_xlsx"), workOrderExportPojo.getPatProblems());
            workOrderMap.put(WebUtil.getMessage("patActions"), workOrderExportPojo.getPatActions());
            workOrderMap.put(WebUtil.getMessage("patTests"), workOrderExportPojo.getPatTests());
            workOrderMap.put(WebUtil.getMessage("Remark"), workOrderExportPojo.getComment());
            workOrderMap.put(WebUtil.getMessage("QRCode_xlsx"), workOrderExportPojo.getAssetInfoQrCode());
            //workOrderMap.put("设备分类", workOrderExportPojo.getAssetGroupName());
            workOrderMap.put(WebUtil.getMessage("EquipmentClassification_xlsx"), workOrderExportPojo.getAssetGroup() != null ? FieldValueMessageController.fieldValue("assetGroup", workOrderExportPojo.getAssetGroup().toString()) : "");
            /*workOrderMap.put("医疗器械分类", workOrderExportPojo.getAssetFunctionGroupName());
            workOrderMap.put("医疗器械子分类", workOrderExportPojo.getAssetCode68Name());*/
            workOrderMap.put(WebUtil.getMessage("EquipmentModel_xlsx"), workOrderExportPojo.getAssetFunctionType());
            workOrderMap.put(WebUtil.getMessage("NearestWorkDays_xlsx"), (workOrderExportPojo.getNearestWorkDay() == null || workOrderExportPojo.getNearestWorkDay() < 0) ? "" : workOrderExportPojo.getNearestWorkDay());
            workOrderMap.put(WebUtil.getMessage("DownTime_xlsx"), formatDate(workOrderExportPojo.getConfirmedDownTime()));
            workOrderMap.put(WebUtil.getMessage("RecoveryTime_xlsx"), formatDate(workOrderExportPojo.getConfirmedUpTime()));
            workOrderMap.put(WebUtil.getMessage("RequestorTelephone_xlsx"), workOrderExportPojo.getTelephone());
            workOrderMap.put(WebUtil.getMessage("CurrentLocation_xlsx"), workOrderExportPojo.getLocationName());
            //workOrderMap.put("报修状态", workOrderExportPojo.getAssetStatusName());
            workOrderMap.put(WebUtil.getMessage("RequestStatus_xlsx"), workOrderExportPojo.getAssetStatus() != null ? FieldValueMessageController.fieldValue("assetStatus", workOrderExportPojo.getAssetStatus().toString()) : "");

            workOrderMap.put("assetId",workOrderExportPojo.getAssetId());

            if (workOrderExportPojo.getAssetFunctionGroup() != null) {
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceClassification_xlsx"), workOrderExportPojo.getAssetFunctionGroup() != null ? FieldValueMessageController.fieldValue("assetFunctionType", workOrderExportPojo.getAssetFunctionGroup().toString()) : "");
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceSubclassification_xlsx"), workOrderExportPojo.getAssetCode68() != null ? FieldValueMessageController.fieldValue(workOrderExportPojo.getAssetFunctionGroup().toString(), workOrderExportPojo.getAssetCode68().toString()) : "");
            } else {
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceClassification_xlsx"), "");
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceSubclassification_xlsx"), "");
            }

            workOrderMap.put("siebleNumber", workOrderExportPojo.getSiebleNumber());
            workOrderMap.put("callCenterNumber", workOrderExportPojo.getCallCenterNumber());

            String attributesStr = "";
            if(!StringUtils.isEmpty(workOrderExportPojo.getAttributes())){
                JSONArray attributesJSONArray = JSONObject.parseArray(workOrderExportPojo.getAttributes());
                for(int i = 0 ; i < attributesJSONArray.size() ; i++){
                    JSONObject attributesObject = attributesJSONArray.getJSONObject(i);
                    Object label = attributesObject.get("label").toString();
                    Object value = attributesObject.get("value").toString();
                    attributesStr += label + ":" + value + ";";
                }
            }
            workOrderMap.put(WebUtil.getMessage("Attribute"), attributesStr);

            if(workOrderExportPojo.getSignature() != null){
                workOrderMap.put("signature", workOrderExportPojo.getSignature());
            }
            if(workOrderExportPojo.getAckSignature() != null){
                workOrderMap.put("ack_signature", workOrderExportPojo.getAckSignature());
            }
            if(workOrderExportPojo.getSrId()!=null) {
                workOrderMap.put("srId",workOrderExportPojo.getSrId());
            }
            resultList.add(workOrderMap);
        }
        return resultList;
    }

    public List<Map<String, Object>> supplementWorkOrder(List<Map<String, Object>> workOrderdata) {

        final List<String> woId = workOrderdata.stream()
                .map(i -> String.valueOf(i.get("woId"))).collect(Collectors.toList());

        final List<Integer> assetId = workOrderdata.stream()
                .map(i -> (Integer) i.get("assetId")).distinct().collect(Collectors.toList());

        final Map<Integer, AssetInfo> assetMap = assetInfoDao.findByIdIn(assetId).stream().collect(Collectors.toMap(AssetInfo::getId, i -> i));

        final List<MultiLevelApproval> multiLevelApprovals = multiLevelApprovalRepository.findByBusinessUidIn(woId);

        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        UserAccount userAccount = userContextService.getCurrentUserAccount();
        String tenantUID = userAccount.getTenantUID();
        TenantInfo tenantInfo = tenantInfoRepository.findByuid(tenantUID);

        Map<String,  List<V2_WorkOrder_Detail>> workOrderDetails= new HashMap<>();

        Map<String,String> partsSignatureMap = new HashMap<>();
        Map<String,String> medicalWorkerApproverSignatureMap = new HashMap<>();
//        if(WorkOrderConstant.DESIGNATED_USER.equals(tenantInfo.getName().trim())) {
            List<V2_WorkOrder_Detail> v2_workOrder_details = workOrderDetailDao.findByWoIdIn(woId);
            v2_workOrder_details.stream().forEach(i->{
                workOrderDetails.putIfAbsent(i.getWoId(), new ArrayList<>());
                workOrderDetails.get(i.getWoId()).add(i);

                if(!Strings.isNullOrEmpty(i.getPartsSignature())) {
                    partsSignatureMap.putIfAbsent(i.getWoId(), i.getPartsSignature());
                }
                if(!Strings.isNullOrEmpty(i.getMedicalWorkerApproverSignature())) {
                    medicalWorkerApproverSignatureMap.putIfAbsent(i.getWoId(), i.getMedicalWorkerApproverSignature());
                }
            });
//        }

        final List<AuditGroupConfigurationDetail> approverSignatureList = auditGroupConfigurationDetailRepository.findByBusinessUidInOrderByApproveNumDesc(woId);
        Map<String, List<AuditGroupConfigurationDetail>> approverSignatureObjMap = new HashMap<>();
        approverSignatureList.stream().forEach(i->{
            i.setApproveNum(Optional.ofNullable(i.getApproveNum()).orElse(0));
            approverSignatureObjMap.putIfAbsent(i.getBusinessUid(), new ArrayList<>());
            if(CollectionUtils.isEmpty(approverSignatureObjMap.get(i.getBusinessUid())) ||
                    approverSignatureObjMap.get(i.getBusinessUid()).stream().anyMatch(sign-> Objects.equals(sign.getApproveNum(), i.getApproveNum()))){
                approverSignatureObjMap.get(i.getBusinessUid()).add(i);
            }else if(approverSignatureObjMap.get(i.getBusinessUid()).stream().anyMatch(sign-> sign.getApproveNum() < i.getApproveNum())){
                approverSignatureObjMap.get(i.getBusinessUid()).clear();
                approverSignatureObjMap.get(i.getBusinessUid()).add(i);
            }
        });

        Map<String, List<MultiLevelApproval>> approvalsMap = new HashMap<>();
        multiLevelApprovals.stream().forEach(i->{
            approvalsMap.putIfAbsent(i.getBusinessUid(), new ArrayList<>());
            approvalsMap.get(i.getBusinessUid()).add(i);
        });

        workOrderdata.stream().forEach(m->{
            String woid = (String) m.get("woId");
            m.put("设备价格", Optional.ofNullable(assetMap.get((Integer) m.get("assetId"))).map(a-> a.getPurchasePrice()==null ? a.getPurchasePrice() : new BigDecimal(a.getPurchasePrice())).orElse(null));
            m.put("采购日期",  Optional.ofNullable(assetMap.get((Integer) m.get("assetId")))
                    .map(a->a.getPurchaseDate()).orElse(null));

            m.put("auditSignatureList",Optional.ofNullable(approvalsMap.get(woid))
                    .map(o-> o.stream().map(MultiLevelApproval::getAuditSignature).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList()))
                    .orElse(Collections.emptyList()));


            if(WorkOrderConstant.DESIGNATED_USER.equals(tenantInfo.getName().trim())) {
                m.put("partsSignature", partsSignatureMap.get(woid));
                m.put("medicalWorkerApproverSignature",medicalWorkerApproverSignatureMap.get(woid));
                m.put("approverSignatureList",Optional.ofNullable(approverSignatureObjMap.get(woid))
                        .map(o-> o.stream().map(AuditGroupConfigurationDetail::getApproverSignature).collect(Collectors.toList()))
                        .orElse(Collections.emptyList()));
            }else if(tenantInfo.getType() != null && tenantInfo.getType() == 6){//县域新增
                m.put(WebUtil.getMessage("siebleNumber"), m.get("siebleNumber"));
                m.put(WebUtil.getMessage("callCenterNumber"), m.get("callCenterNumber"));
            }
            m.put("PartInformation", getPartsDetailMap(Optional.ofNullable(workOrderDetails.get(woid)).orElse(Collections.emptyList()),
                    (String) m.get(WebUtil.getMessage("srNum_xlsx")),
                    (String) m.get(WebUtil.getMessage("EquipmentName_xlsx")),
                    (String) m.get(WebUtil.getMessage("EquipmentID_xlsx")) ));

            List<V2_BlobObject> pictureList = woService.getAttachByBoSubType((String) m.get("srId"), null); //微信端之前上传的图片没有 boSubType区分
            if(pictureList!=null) {
                List<V2_BlobObject> serviceRequestPicture = new ArrayList<>();  //报修图片
                List<V2_BlobObject> workOrderPicture = new ArrayList<>();    //维修图片
                List<V2_BlobObject> partsPicture = new ArrayList<>();        //备件图片，目前只有mobile端可以上传图片
                serviceRequestPicture = pictureList.stream().filter(item -> "ServiceRequest".equals(item.getBoType())).collect(Collectors.toList());
                pictureList = woService.getAttachByBoSubType((String) m.get("srId"), V2_BlobObject.BO_SUB_TYPE_IMG);
                workOrderPicture = pictureList.stream().filter(item -> "WorkOrder".equals(item.getBoType())).collect(Collectors.toList());
                Set<String> ids = workOrderDetailDao.findByWoId(woid).stream().map(p -> p.getId()).collect(Collectors.toSet());
                if(!ids.isEmpty()) partsPicture.addAll(blobObjectRepository.getPartPictures(ids));
                m.put("serviceRequestPicture",serviceRequestPicture.stream().map(v2_blobObject -> v2_blobObject.getObjectStorageId()).collect(Collectors.toList()));
                m.put("workOrderPicture",workOrderPicture.stream().map(v2_blobObject -> v2_blobObject.getObjectStorageId()).collect(Collectors.toList()));
                m.put("partsPicture",partsPicture.stream().map(v2_blobObject -> v2_blobObject.getObjectStorageId()).collect(Collectors.toList()));
            }
        });

        return workOrderdata;
    }

    /*
    @Deprecated
    public List<Map<String, Object>> createWorkOrderData(OrgInfo selectedOrg, Date startTime, Date endTime, Date closedStartTime, Date closedEndTime, List<Integer> statusList,OrgInfo clinicalDept,UserAccount currentOwner, List<OrgInfo> clinicalDeptList, String filterParts, String filterTotalPrice,Map<String,String> filterMap,String currentStep){
        List<Map<String, Object>> resultList = new ArrayList<>();

        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();

        if (selectedOrg != null && selectedOrg.getOrgLevel() == 0) {
            SearchFilter tenantFilter = new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(tenantFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 1) {
            SearchFilter institutionFilter = new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(institutionFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 2) {
            SearchFilter hospitalFilter = new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(hospitalFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 3) {
            SearchFilter siteFilter = new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(siteFilter);
        }

        if (selectedOrg != null) {
            SearchFilter siteFilter = new SearchFilter("orgUID", SearchFilter.Operator.IN, selectedOrg.getUid());
            searchFilters.add(siteFilter);
        }

        if(clinicalDept !=null){
            List<OrgInfo> orgSearchList = new ArrayList<>();
            orgSearchList.add(clinicalDept);

            Set<OrgInfo> tempOrgSearchSet = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgSearchList, clinicalDeptList);
            List<String> clinicalDeptUIDList = new ArrayList<>();
            for (OrgInfo oi : tempOrgSearchSet) {
                clinicalDeptUIDList.add(oi.getUid());
            }
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, clinicalDeptUIDList));
        }
        if(currentOwner !=null) {
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, currentOwner.getId()));
        }

        ZoneId zone = ZoneId.systemDefault();

        if (startTime != null) {
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.GTE, startTime));
        }
        if (endTime != null) {
            LocalDate endDate = LocalDateTime.ofInstant(endTime.toInstant(), zone).toLocalDate();
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.LT, Date.from(endDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant())));
        }
        if (closedStartTime != null) {
            searchFilters.add(new SearchFilter("closeTime", SearchFilter.Operator.GTE, closedStartTime));
        }
        if (closedEndTime != null) {
            LocalDate closeEndDate = LocalDateTime.ofInstant(closedEndTime.toInstant(), zone).toLocalDate();
            searchFilters.add(new SearchFilter("closeTime", SearchFilter.Operator.LT, Date.from(closeEndDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant())));
        }
        if (statusList != null && !statusList.isEmpty()) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.IN, statusList));
        }
        if(filterParts != null){
            searchFilters.add(new SearchFilter("filterParts", SearchFilter.Operator.EQ, filterParts));
        }
        if(filterTotalPrice != null){
            searchFilters.add(new SearchFilter("filterTotalPrice", SearchFilter.Operator.EQ, filterTotalPrice));
        }
        List<Integer> assetIds = getVisableMvAssetIds(selectedOrg,clinicalDept);
        List<WorkOrderExportPojo> workOrderList = worDao.workOrderExport(searchFilters);
        if(assetIds.size()>0) {
            searchFilters.add(new SearchFilter("assetIds", SearchFilter.Operator.IN, assetIds));
            workOrderList.addAll(worDao.workOrderExportForMVS(searchFilters));
        }
        workOrderList = workOrderList.stream().filter(workOrderExportPojo -> {
            if(assetIds.contains(workOrderExportPojo.getAssetId())) {
                workOrderExportPojo.setMVSWorkOrder(true);
            } else {
                workOrderExportPojo.setMVSWorkOrder(false);
            }
            boolean isReturn = true;
            if(isReturn && filterMap.get("srNum")!=null && filterMap.get("srNum").length()>0) {   //报修单号
                if(workOrderExportPojo.getSrNum()!=null) {
                    isReturn = workOrderExportPojo.getSrNum().contains(filterMap.get("srNum").trim());
                } else {
                    isReturn = false;
                }
            }
            if(isReturn && filterMap.get("assetDepartNum")!=null && filterMap.get("assetDepartNum").length()>0) {  //设备编号
                if(workOrderExportPojo.getAssetDepartNum()!=null) {
                    isReturn = workOrderExportPojo.getAssetDepartNum().contains(filterMap.get("assetDepartNum").trim());
                } else {
                    isReturn = false;
                }
            }
            if(isReturn  && filterMap.get("assetName")!=null && filterMap.get("assetName").length()>0) {    //设备名称
                if(workOrderExportPojo.getAssetName()!=null) {
                    isReturn = workOrderExportPojo.getAssetName().contains(filterMap.get("assetName").trim());
                } else {
                    isReturn = false;
                }
            }
            if(isReturn && filterMap.get("assetFunctionType")!=null && filterMap.get("assetFunctionType").length()>0) {  //设备型号
                if(workOrderExportPojo.getAssetFunctionType()!=null) {
                    isReturn = workOrderExportPojo.getAssetFunctionType().contains(filterMap.get("assetFunctionType").trim());
                } else {
                    isReturn = false;
                }
            }
            if(isReturn  && filterMap.get("requestorName")!=null && filterMap.get("requestorName").length()>0) {  //报修人
                if(workOrderExportPojo.getRequestorName()!=null && filterMap.get("requestorName")!=null) {
                    isReturn = workOrderExportPojo.getRequestorName().contains(filterMap.get("requestorName").trim());
                } else {
                    isReturn = false;
                }
            }
            if(isReturn && filterMap.get("requestReason")!=null && filterMap.get("requestReason").length()>0) {  //故障描述
                if(workOrderExportPojo.getRequestReason()!=null) {
                    isReturn = workOrderExportPojo.getRequestReason().contains(filterMap.get("requestReason").trim());
                } else {
                    isReturn = false;
                }
            }
            if(isReturn && currentStep!=null) {  //当前步骤
                if(workOrderExportPojo.getCurrentStepId()!=null) {
                    isReturn = Integer.parseInt(currentStep)==workOrderExportPojo.getCurrentStepId();
                } else {
                    isReturn = false;
                }
            }
            return isReturn;
        }).collect(Collectors.toList());

        for(WorkOrderExportPojo workOrderExportPojo : workOrderList){
            Map<String, Object> workOrderMap = new HashedMap();

            workOrderMap.put("woId", workOrderExportPojo.getId());
            workOrderMap.put(WebUtil.getMessage("srNum_xlsx"), workOrderExportPojo.getSrNum() != null ? workOrderExportPojo.getSrNum() : "");
            workOrderMap.put(WebUtil.getMessage("EquipmentID_xlsx"), workOrderExportPojo.getAssetDepartNum());
            //workOrderMap.put("工单状态", workOrderExportPojo.getStatusName());
            workOrderMap.put(WebUtil.getMessage("WorkorderStatus_xlsx"), workOrderExportPojo.getStatus() != null ? FieldValueMessageController.fieldValue("status", workOrderExportPojo.getStatus().toString()) : "");
            workOrderMap.put(WebUtil.getMessage("AssetID_xlsx"), workOrderExportPojo.getAssetFinancingNum());
            workOrderMap.put(WebUtil.getMessage("EquipmentSerialNumber_xlsx"), workOrderExportPojo.getAssetSerialNum());
            workOrderMap.put(WebUtil.getMessage("EquipmentName_xlsx"), workOrderExportPojo.getAssetName());
            workOrderMap.put(WebUtil.getMessage("Site"), workOrderExportPojo.getHospitalName());
            workOrderMap.put(WebUtil.getMessage("Department"), workOrderExportPojo.getAssetClinicalDeptName());
            workOrderMap.put(WebUtil.getMessage("manufacture"), workOrderExportPojo.getAssetManufacture());
            workOrderMap.put(WebUtil.getMessage("brand"), workOrderExportPojo.getAssetMakerShortName());
            workOrderMap.put(WebUtil.getMessage("MaintenanceSubStatus"), getSubStatus(workOrderExportPojo.getSubStatus()));
            workOrderMap.put(WebUtil.getMessage("CurrentOwner_xlsx"), workOrderExportPojo.getCurrentPersonName());
            //workOrderMap.put("当前步骤", workOrderExportPojo.getCurrentStepName());
            Integer currentStepId = workOrderExportPojo.getCurrentStepId();
            UserContextService userContextService = WebUtil.getBean(UserContextService.class);
            if(userContextService.hasCurrentTenantName("")){
                if (currentStepId == null) {
                    workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"), "");
                }else if(currentStepId == 7 || currentStepId == 9){//云阜用户 已关单
                    currentStepId = 9;
                    workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"),  FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, currentStepId.toString()));
                }else if(currentStepId == 2 || currentStepId == 3){//云阜用户 待接单
                    currentStepId = 3;
                    workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"),  FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, currentStepId.toString()));
                }else{
                    workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"),  FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, currentStepId.toString()));
                }
            }else {
                workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"), currentStepId != null ? FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, currentStepId.toString()) : "");
            }

            workOrderMap.put("是否MVS工单",workOrderExportPojo.getMVSWorkOrder()? "是" : "否");
            workOrderMap.put(WebUtil.getMessage("requestorId"), workOrderExportPojo.getRequestorName());
            workOrderMap.put(WebUtil.getMessage("RequestTime_xlsx"), formatDate(workOrderExportPojo.getRequestTime()));
            workOrderMap.put(WebUtil.getMessage("FaultDescription_xlsx"), workOrderExportPojo.getRequestReason());
            workOrderMap.put(WebUtil.getMessage("dispatchUserName"), workOrderExportPojo.getSendStepOwnerName());
            workOrderMap.put(WebUtil.getMessage("AssignTime_xlsx"), formatDate(workOrderExportPojo.getSendStepStartTime()));
            //workOrderMap.put("工单类型", workOrderExportPojo.getIntExtTypeName());
            workOrderMap.put(WebUtil.getMessage("WorkorderType_xlsx"), workOrderExportPojo.getIntExtType() != null ? FieldValueMessageController.fieldValue(I18N_INT_EXT_TYPE, workOrderExportPojo.getIntExtType().toString()) : "");
            //workOrderMap.put("维修类型", workOrderExportPojo.getRepairTypeName());
            workOrderMap.put(WebUtil.getMessage("RepairType_xlsx"), workOrderExportPojo.getRepairType() != null ? FieldValueMessageController.fieldValue(I18N_REPAIR_TYPE, workOrderExportPojo.getRepairType().toString()) : "");
            workOrderMap.put(WebUtil.getMessage("RepairResult"), workOrderExportPojo.getRepairResult() != null ? FieldValueMessageController.fieldValue(I18N_REPAIR_RESULT, workOrderExportPojo.getRepairResult().toString()) : "");
            workOrderMap.put(WebUtil.getMessage("Acceptedby_xlsx"), workOrderExportPojo.getAcceptStepOwnerName());
            workOrderMap.put(WebUtil.getMessage("AcceptTime_xlsx"), formatDate(workOrderExportPojo.getAcceptStepStartTime()));
            workOrderMap.put(WebUtil.getMessage("RepairCompleteTime_xlsx"), formatDate(workOrderExportPojo.getWorkStepEndTime()));
            workOrderMap.put(WebUtil.getMessage("CheckInTime_xlsx"), formatDate(workOrderExportPojo.getCheckinTime()));
            workOrderMap.put(WebUtil.getMessage("MaintenanceEngineerManhours_xlsx"), workOrderExportPojo.getManHours());
            workOrderMap.put(WebUtil.getMessage("TotalManhours_xlsx"), workOrderExportPojo.getTotalManHour());
            workOrderMap.put(WebUtil.getMessage("RepairPart_xlsx"), workOrderExportPojo.getParts());
            workOrderMap.put(WebUtil.getMessage("TotalPartPrice_xlsx"), workOrderExportPojo.getPartsTotalPrice());
            workOrderMap.put(WebUtil.getMessage("OtherExpenses_xlsx"), workOrderExportPojo.getOtherExpense());
            workOrderMap.put(WebUtil.getMessage("ExternalExpenses_xlsx"), workOrderExportPojo.getExternalExpense());
            workOrderMap.put(WebUtil.getMessage("TotalRepairPrice_xlsx"), workOrderExportPojo.getTotalPrice());
            workOrderMap.put(WebUtil.getMessage("AcknowledgedBy_xlsx"), workOrderExportPojo.getEvaluateStepOwnerName());
            workOrderMap.put(WebUtil.getMessage("maintanenceValue_xlsx"), workOrderExportPojo.getMaintenanceValue());
            workOrderMap.put(WebUtil.getMessage("AcceptTime_xlsx2"), formatDate(workOrderExportPojo.getEvaluateStepEndTime()));
            workOrderMap.put(WebUtil.getMessage("Closedby_xlsx"), workOrderExportPojo.getCloseStepOwnerName());
            workOrderMap.put(WebUtil.getMessage("CloseTime_xlsx"), formatDate(workOrderExportPojo.getCloseStepEndTime()));
            workOrderMap.put(WebUtil.getMessage("Coworker"), workOrderExportPojo.getCoworker());
            workOrderMap.put(WebUtil.getMessage("CoworkerManhours_xlsx"), workOrderExportPojo.getCoworkManHour());
            workOrderMap.put(WebUtil.getMessage("FinalRating_xlsx"), workOrderExportPojo.getFeedbackRating());
            workOrderMap.put(WebUtil.getMessage("FaultType_xlsx"), workOrderExportPojo.getCaseTypeName());
            workOrderMap.put(WebUtil.getMessage("ReasonDescription_xlsx"), workOrderExportPojo.getPatProblems());
            workOrderMap.put(WebUtil.getMessage("patActions"), workOrderExportPojo.getPatActions());
            workOrderMap.put(WebUtil.getMessage("patTests"), workOrderExportPojo.getPatTests());
            workOrderMap.put(WebUtil.getMessage("Remark"), workOrderExportPojo.getComment());
            workOrderMap.put(WebUtil.getMessage("QRCode_xlsx"), workOrderExportPojo.getAssetInfoQrCode());
            //workOrderMap.put("设备分类", workOrderExportPojo.getAssetGroupName());
            workOrderMap.put(WebUtil.getMessage("EquipmentClassification_xlsx"), workOrderExportPojo.getAssetGroup() != null ? FieldValueMessageController.fieldValue("assetGroup", workOrderExportPojo.getAssetGroup().toString()) : "");
            //workOrderMap.put("医疗器械分类", workOrderExportPojo.getAssetFunctionGroupName());
            //workOrderMap.put("医疗器械子分类", workOrderExportPojo.getAssetCode68Name());
            workOrderMap.put(WebUtil.getMessage("EquipmentModel_xlsx"), workOrderExportPojo.getAssetFunctionType());
            workOrderMap.put(WebUtil.getMessage("NearestWorkDays_xlsx"), (workOrderExportPojo.getNearestWorkDay() == null || workOrderExportPojo.getNearestWorkDay() < 0) ? "" : workOrderExportPojo.getNearestWorkDay());
            workOrderMap.put(WebUtil.getMessage("DownTime_xlsx"), formatDate(workOrderExportPojo.getConfirmedDownTime()));
            workOrderMap.put(WebUtil.getMessage("RecoveryTime_xlsx"), formatDate(workOrderExportPojo.getConfirmedUpTime()));
            workOrderMap.put(WebUtil.getMessage("RequestorTelephone_xlsx"), workOrderExportPojo.getTelephone());
            workOrderMap.put(WebUtil.getMessage("CurrentLocation_xlsx"), workOrderExportPojo.getLocationName());
            //workOrderMap.put("报修状态", workOrderExportPojo.getAssetStatusName());
            workOrderMap.put(WebUtil.getMessage("RequestStatus_xlsx"), workOrderExportPojo.getAssetStatus() != null ? FieldValueMessageController.fieldValue("assetStatus", workOrderExportPojo.getAssetStatus().toString()) : "");
            V2_WorkOrder v2_workOrder = v2WorkOrderDao.findById(workOrderExportPojo.getId());
            AssetInfo assetInfo = null;
            if(v2_workOrder!=null) {
                assetInfo = assetInfoDao.findById(v2_workOrder.getAssetId());
            }
                workOrderMap.put("设备价格", assetInfo !=null ? assetInfo.getPurchasePrice() : null);
                workOrderMap.put("采购日期", assetInfo !=null ? assetInfo.getPurchaseDate() : null);

            if (workOrderExportPojo.getAssetFunctionGroup() != null) {
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceClassification_xlsx"), workOrderExportPojo.getAssetFunctionGroup() != null ? FieldValueMessageController.fieldValue("assetFunctionType", workOrderExportPojo.getAssetFunctionGroup().toString()) : "");
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceSubclassification_xlsx"), workOrderExportPojo.getAssetCode68() != null ? FieldValueMessageController.fieldValue(workOrderExportPojo.getAssetFunctionGroup().toString(), workOrderExportPojo.getAssetCode68().toString()) : "");
            } else {
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceClassification_xlsx"), "");
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceSubclassification_xlsx"), "");
            }

//            String sendStepInfo = workOrderExportPojo.getSendStepInfo();
//            if(sendStepInfo != null){
//                String [] sendStepInfoArray = sendStepInfo.split("\\|:\\|");
//                workOrderMap.put("派工人", sendStepInfoArray.length >= 1 ? sendStepInfoArray[0] : "");
//                workOrderMap.put("派工时间", sendStepInfoArray.length >= 2 ? sendStepInfoArray[1] : "");
//            }else {
//                workOrderMap.put("派工人", "");
//                workOrderMap.put("派工时间", "");
//            }
//            String acceptStepInfo = workOrderExportPojo.getAcceptStepInfo();
//            if(acceptStepInfo != null){
//                String [] acceptStepInfoArray = acceptStepInfo.split("\\|:\\|");
//                workOrderMap.put("接单人", acceptStepInfoArray.length >= 1 ? acceptStepInfoArray[0] : "");
//                workOrderMap.put("接单时间", acceptStepInfoArray.length >= 2 ? acceptStepInfoArray[1] : "");
//            }else {
//                workOrderMap.put("接单人", "");
//                workOrderMap.put("接单时间", "");
//            }
//            String evaluateStepInfo = workOrderExportPojo.getEvaluateStepInfo();
//            if(evaluateStepInfo != null){
//                String [] evaluateStepInfoArray = evaluateStepInfo.split("\\|:\\|");
//                workOrderMap.put("验收人", evaluateStepInfoArray.length >= 1 ? evaluateStepInfoArray[0] : "");
//                workOrderMap.put("验收时间", evaluateStepInfoArray.length >= 2 ? evaluateStepInfoArray[1] : "");
//            }
//            String closeStepInfo = workOrderExportPojo.getCloseStepInfo();
//            if(closeStepInfo != null){
//                String [] closeStepInfoArray = closeStepInfo.split("\\|:\\|");
//                workOrderMap.put("关单人", closeStepInfoArray.length >=1 ? closeStepInfoArray[0] : "");
//                workOrderMap.put("关单时间", closeStepInfoArray.length >=2 ? closeStepInfoArray[1] : "");
//            }else {
//                workOrderMap.put("关单人", "");
//                workOrderMap.put("关单时间", "");
//            }

            String attributesStr = "";
            if(!StringUtils.isEmpty(workOrderExportPojo.getAttributes())){
                JSONArray attributesJSONArray = JSONObject.parseArray(workOrderExportPojo.getAttributes());
                for(int i = 0 ; i < attributesJSONArray.size() ; i++){
                    JSONObject attributesObject = attributesJSONArray.getJSONObject(i);
                    Object label = attributesObject.get("label").toString();
                    Object value = attributesObject.get("value").toString();
                    attributesStr += label + ":" + value + ";";
                }
            }
            workOrderMap.put(WebUtil.getMessage("Attribute"), attributesStr);

            if(workOrderExportPojo.getSignature() != null){
                workOrderMap.put("signature", workOrderExportPojo.getSignature());
            }
            if(workOrderExportPojo.getAckSignature() != null){
                workOrderMap.put("ack_signature", workOrderExportPojo.getAckSignature());
            }
            if(workOrderExportPojo.getId()!=null){
                List<MultiLevelApproval> multiLevelApprovals = multiLevelApprovalRepository.findByBusinessUid(workOrderExportPojo.getId());
                if (multiLevelApprovals!=null && !multiLevelApprovals.isEmpty()){
                    List<String> list = new ArrayList<>();
                    for (MultiLevelApproval multiLevelApproval:multiLevelApprovals){
                        String auditSignature = multiLevelApproval.getAuditSignature();
                        if(auditSignature!=null){
                            list.add(auditSignature);
                            log.info("audit_signature:{}", auditSignature);
                        }
                    }
                    log.info("auditSignatureList:{}",list.size());
                    workOrderMap.put("auditSignatureList",list );
                }
            }

            List<V2_BlobObject> pictureList = woService.getAttachByBoSubType(workOrderExportPojo.getSrId(), V2_BlobObject.BO_SUB_TYPE_IMG);
            if(pictureList!=null) {
                List<V2_BlobObject> serviceRequestPicture = new ArrayList<>();  //报修图片
                List<V2_BlobObject> workOrderPicture = new ArrayList<>();    //维修图片
                List<V2_BlobObject> partsPicture = new ArrayList<>();        //备件图片，目前只有mobile端可以上传图片
                serviceRequestPicture = pictureList.stream().filter(item -> "ServiceRequest".equals(item.getBoType())).collect(Collectors.toList());
                workOrderPicture = pictureList.stream().filter(item -> "WorkOrder".equals(item.getBoType())).collect(Collectors.toList());
                List<V2_WorkOrder_Detail> details = workOrderDetailDao.getByWoIdOrderByCreatedDateDesc(workOrderExportPojo.getId());
                if (details != null && !details.isEmpty()) {
                    for(V2_WorkOrder_Detail detail : details) {
                        partsPicture.addAll(detail.getAttachments());
                    }
                }
                workOrderMap.put("serviceRequestPicture",serviceRequestPicture.stream().map(v2_blobObject -> v2_blobObject.getObjectStorageId()).collect(Collectors.toList()));
                workOrderMap.put("workOrderPicture",workOrderPicture.stream().map(v2_blobObject -> v2_blobObject.getObjectStorageId()).collect(Collectors.toList()));
                workOrderMap.put("partsPicture",partsPicture.stream().map(v2_blobObject -> v2_blobObject.getObjectStorageId()).collect(Collectors.toList()));
            }

            if(workOrderExportPojo.getId()!=null){
                UserAccount userAccount = userContextService.getCurrentUserAccount();
                String tenantUID = userAccount.getTenantUID();
                TenantInfo tenantInfo = tenantInfoRepository.findByuid(tenantUID);
                if(WorkOrderConstant.DESIGNATED_USER.equals(tenantInfo.getName().trim())) {
                    List<V2_WorkOrder_Detail> v2_workOrder_details = workOrderDetailDao.getByWoIdOrderByCreatedDateDesc(workOrderExportPojo.getId());
                    if (v2_workOrder_details!=null && !v2_workOrder_details.isEmpty()){
                        V2_WorkOrder_Detail v2_workOrder_detail = v2_workOrder_details.get(0);
                        //添加备件签名
                        String partsSignature = v2_workOrder_detail.getPartsSignature();
                        log.info("partsSignature:{}",partsSignature);
                        if(partsSignature!=null){
                            workOrderMap.put("partsSignature",partsSignature );
                        }
                        //医工组签名
                        String medicalWorkerApproverSignature = v2_workOrder_detail.getMedicalWorkerApproverSignature();
                        log.info("medicalWorkerApproverSignature:{}",medicalWorkerApproverSignature);
                        if(medicalWorkerApproverSignature!=null){
                            workOrderMap.put("medicalWorkerApproverSignature",medicalWorkerApproverSignature );
                        }
                        //核心组签名
                        List<AuditGroupConfigurationDetail> auditGroupConfigurationDetails = auditGroupConfigurationDetailRepository.findByBusinessUidOrderByApproveNumDesc(workOrderExportPojo.getId());
                        if (auditGroupConfigurationDetails != null && !auditGroupConfigurationDetails.isEmpty()){
                            int num=0;
                            List<String> approverSignatureList = new ArrayList<>();
                            for (int i = 0; i <auditGroupConfigurationDetails.size() ; i++) {
                                AuditGroupConfigurationDetail auditGroupConfigurationDetail = auditGroupConfigurationDetails.get(i);
                                String approverSignature = auditGroupConfigurationDetail.getApproverSignature();
                               Integer approveNum = auditGroupConfigurationDetail.getApproveNum();
                               if (approveNum==null){
                                   approveNum=0;
                               }
                                if (i==0){
                                    num = approveNum;
                                    if(approverSignature!=null){
                                        approverSignatureList.add(approverSignature);
                                        log.info("核心组签名:"+ approverSignature);
                                    }
                                }else {
                                    if (num == approveNum){
                                        if(approverSignature!=null){
                                            approverSignatureList.add(approverSignature);
                                            log.info("核心组签名:"+ approverSignature);
                                        }
                                    }
                                }
                            }
                            log.info("approverSignatureList:{}",approverSignatureList.size());
                            workOrderMap.put("approverSignatureList",approverSignatureList );
                        }
                    }
                }else if(tenantInfo.getType() != null && tenantInfo.getType() == 6){//县域新增
                    workOrderMap.put(WebUtil.getMessage("siebleNumber"), workOrderExportPojo.getSiebleNumber());
                    workOrderMap.put(WebUtil.getMessage("callCenterNumber"), workOrderExportPojo.getCallCenterNumber());
                }
            }

            this.putPartsData(workOrderExportPojo ,workOrderMap);
            //this.putWorkOrderStep(workOrderExportPojo ,workOrderMap);

            resultList.add(workOrderMap);
        }

        return resultList;

    }
    */

    public List<Map<String, Object>> createPartsData(List<Map<String, Object>> workOrderList){

        List<Map<String, Object>> partList = new ArrayList<>();
        for (Map<String, Object> workOrderMap : workOrderList) {
            if(workOrderMap.containsKey("PartInformation")){
                partList.addAll((List<Map<String, Object>>)workOrderMap.get("PartInformation"));
            }
        }
        return partList;
    }

    private List<Map<String, Object>> getPartsDetailMap(List<V2_WorkOrder_Detail> partsDetails, String srnum, String AssetName,String AssetNum ){
        List<Map<String, Object>> parts = new ArrayList<>();
        for (V2_WorkOrder_Detail workOrderDetail : partsDetails) {
            Map<String, Object> part = new HashedMap();
            part.put(WebUtil.getMessage("srNum_xlsx"),srnum);
            part.put(WebUtil.getMessage("AssetName_xlsx"), AssetName);
            part.put(WebUtil.getMessage("EquipmentID_xlsx"), AssetNum);
            part.put(WebUtil.getMessage("PartName_xlsx"), workOrderDetail.getParts());
            part.put(WebUtil.getMessage("ContractPrice_xlsx"), workOrderDetail.getClosedPrice());
            part.put(WebUtil.getMessage("Quantity"), workOrderDetail.getApartsQuantity());
            part.put(WebUtil.getMessage("type"), workOrderDetail.getPartsType() != null ? FieldValueMessageController.fieldValue("partsType", workOrderDetail.getPartsType()) : "");
            part.put(WebUtil.getMessage("SerialNumber_xlsx"), workOrderDetail.getSerialNum());
            part.put(WebUtil.getMessage("WarrantyDate_xlsx"), workOrderDetail.getWarrantyDate());
            part.put(WebUtil.getMessage("ContractNumber_xlsx"), workOrderDetail.getContractNum());
            part.put(WebUtil.getMessage("InvoiceNumber_xlsx"), workOrderDetail.getInvoiceNum());
            part.put(WebUtil.getMessage("InvoiceTime_xlsx"), formatDate(workOrderDetail.getInvoiceTime()));
            part.put(WebUtil.getMessage("EstimatedPrice_xlsx"), workOrderDetail.getPartsPrice());
            part.put(WebUtil.getMessage("Vendor"), workOrderDetail.getSupplier());
            part.put(WebUtil.getMessage("Remark"), workOrderDetail.getComment());
            if (workOrderDetail.getArrived() == null) {
                part.put(WebUtil.getMessage("Arrival"), null);
            } else {
                String arrived = workOrderDetail.getArrived().equals(Boolean.TRUE) ? WebUtil.getMessage("Arrival") : WebUtil.getMessage("NonArrived");
                part.put(WebUtil.getMessage("Arrival"), arrived);
            }
            parts.add(part);
        }
        return parts;
    }
/*
    private void putPartsData(WorkOrderExportPojo workOrderPojo, Map<String, Object> workOrderMap){

        List<V2_WorkOrder_Detail> workOrderDetailList = workOrderDetailDao.findByWoId(workOrderPojo.getId());
        List<Map<String, Object>> parts = new ArrayList<>();
        for (V2_WorkOrder_Detail workOrderDetail : workOrderDetailList) {
            Map<String, Object> part = new HashedMap();
            part.put(WebUtil.getMessage("srNum_xlsx"), workOrderPojo.getSrNum());
            part.put(WebUtil.getMessage("AssetName_xlsx"), workOrderPojo.getAssetName());
            part.put(WebUtil.getMessage("EquipmentID_xlsx"), workOrderPojo.getAssetDepartNum());
            part.put(WebUtil.getMessage("PartName_xlsx"), workOrderDetail.getParts());
            part.put(WebUtil.getMessage("ContractPrice_xlsx"), workOrderDetail.getClosedPrice());
            part.put(WebUtil.getMessage("Quantity"), workOrderDetail.getApartsQuantity());
            part.put(WebUtil.getMessage("type"), workOrderDetail.getPartsType() != null ? FieldValueMessageController.fieldValue("partsType", workOrderDetail.getPartsType()) : "");
            part.put(WebUtil.getMessage("SerialNumber_xlsx"), workOrderDetail.getSerialNum());
            part.put(WebUtil.getMessage("WarrantyDate_xlsx"), workOrderDetail.getWarrantyDate());
            part.put(WebUtil.getMessage("ContractNumber_xlsx"), workOrderDetail.getContractNum());
            part.put(WebUtil.getMessage("InvoiceNumber_xlsx"), workOrderDetail.getInvoiceNum());
            part.put(WebUtil.getMessage("InvoiceTime_xlsx"), formatDate(workOrderDetail.getInvoiceTime()));
            part.put(WebUtil.getMessage("EstimatedPrice_xlsx"), workOrderDetail.getPartsPrice());
            part.put(WebUtil.getMessage("Vendor"), workOrderDetail.getSupplier());
            part.put(WebUtil.getMessage("Remark"), workOrderDetail.getComment());
            if (workOrderDetail.getArrived() == null) {
                part.put(WebUtil.getMessage("Arrival"), null);
            } else {
                String arrived = workOrderDetail.getArrived().equals(Boolean.TRUE) ? WebUtil.getMessage("Arrival") : WebUtil.getMessage("NonArrived");
                part.put(WebUtil.getMessage("Arrival"), arrived);
            }
            parts.add(part);
        }

        workOrderMap.put("PartInformation", parts);
    }*/

   /* private void putWorkOrderStep(WorkOrderExportPojo workOrderPojo, Map<String, Object> workOrderMap){

        List<V2_WorkOrder_Step> steps = v2WorkOrderStepDao.findByWorkOrder(workOrderPojo.getId());
        List<Map<String, Object>> woSteps = new ArrayList<>();
        for (V2_WorkOrder_Step step : steps) {
            Map<String, Object> woStep = new HashedMap();
            woStep.put("步骤名称", step.getStepName());
            woStep.put("责任人", step.getOwnerName());
            woStep.put("开始时间", formatDate(step.getStartTime()));
            woStep.put("结束时间", formatDate(step.getEndTime()));
            woStep.put("处理描述", step.getComments());
            woSteps.add(woStep);
        }

        workOrderMap.put("维修流程", woSteps);
    }*/
/*
    public List<Map<String, Object>> createData(OrgInfo selectedOrg, Date startTime, Date endTime, Date closedStartTime, Date closedEndTime, List<Integer> statusList,OrgInfo clinicalDept,UserAccount currentOwner, List<OrgInfo> clinicalDeptList) {

        List<Map<String, Object>> resultList = new ArrayList<>();

        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();

        if (selectedOrg != null && selectedOrg.getOrgLevel() == 0) {
            SearchFilter tenantFilter = new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(tenantFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 1) {
            SearchFilter institutionFilter = new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(institutionFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 2) {
            SearchFilter hospitalFilter = new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(hospitalFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 3) {
            SearchFilter siteFilter = new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(siteFilter);
        }

        if(clinicalDept !=null){
            List<OrgInfo> orgSearchList = new ArrayList<>();
            orgSearchList.add(clinicalDept);

            Set<OrgInfo> tempOrgSearchSet = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgSearchList, clinicalDeptList);
            List<String> clinicalDeptUIDList = new ArrayList<>();
            for (OrgInfo oi : tempOrgSearchSet) {
                clinicalDeptUIDList.add(oi.getUid());
            }
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, clinicalDeptUIDList));
        }
        if(currentOwner !=null) {
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, currentOwner.getId()));
        }

        ZoneId zone = ZoneId.systemDefault();

        if (startTime != null) {
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.GTE, startTime));
        }
        if (endTime != null) {
            LocalDate endDate = LocalDateTime.ofInstant(endTime.toInstant(), zone).toLocalDate();
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.LT, Date.from(endDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant())));
        }
        if (closedStartTime != null) {
            searchFilters.add(new SearchFilter("closeTime", SearchFilter.Operator.GTE, closedStartTime));
        }
        if (closedEndTime != null) {
            LocalDate closeEndDate = LocalDateTime.ofInstant(closedEndTime.toInstant(), zone).toLocalDate();
            searchFilters.add(new SearchFilter("closeTime", SearchFilter.Operator.LT, Date.from(closeEndDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant())));
        }
        if (statusList != null && !statusList.isEmpty()) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.IN, statusList));
        }

        PageRequest pageRequest = this.buildPageRequest(0, 100000);

        Page<V2_WorkOrder> pageDate = v2WorkOrderDao.findBySearchFilter(searchFilters, pageRequest);

        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();

        for (V2_WorkOrder workOrder : pageDate.getContent()) {
            AssetInfo assetInfo = assetInfoDao.findById(workOrder.getAssetId());
            V2_ServiceRequest serviceRequest = serviceRequestDao.findOne(workOrder.getSrId());
            List<V2_WorkOrder_Detail> workOrderDetailList = workOrderDetailDao.findByWoId(workOrder.getId());
            V2_WorkOrder_Step sendStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 2);
            V2_WorkOrder_Step sendStep_2 = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 3);
            V2_WorkOrder_Step acceptStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeAsc(workOrder.getId(), 4);
            V2_WorkOrder_Step workStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 4);
            V2_WorkOrder_Step evaluateStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 5);
            V2_WorkOrder_Step closeStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 6);
            List<V2_WorkOrder> closeWorkOrderList = v2WorkOrderDao.getByAssetIdAndStatusOrderByCloseTimeDesc(workOrder.getAssetId(), 2);

            List<V2_WorkOrderCoworker> workOrderList = workOrderCoworkerDao.findByWoId(workOrder.getId());

            Map<String, Object> workOrderMap = new HashedMap();

            workOrderMap.put("woId", workOrder.getId());
            workOrderMap.put("报修单号", serviceRequest.getSrNum() != null ? serviceRequest.getSrNum() : "");
            workOrderMap.put("设备编号", assetInfo.getDepartNum());
            workOrderMap.put("工单状态", workOrder.getStatus() != null ? FieldValueMessageController.fieldValue("status", workOrder.getStatus().toString()) : "");
            workOrderMap.put("资产编号", assetInfo.getFinancingNum());
            workOrderMap.put("设备序列号", assetInfo.getSerialNum());
            workOrderMap.put("设备名称", serviceRequest.getAssetName());
            workOrderMap.put("院区", serviceRequest.getHospitalName());
            workOrderMap.put("科室", assetInfo.getClinicalDeptName());
            workOrderMap.put("制造商", assetInfo.getManufacture());
            workOrderMap.put("品牌", assetInfo.getMakerShortName());
            workOrderMap.put("当前责任人", workOrder.getCurrentPersonName());
            workOrderMap.put("当前步骤", workOrder.getCurrentStepId() != null ? FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, workOrder.getCurrentStepId().toString()) : "");
            workOrderMap.put("报修人", serviceRequest.getRequestorName());
            workOrderMap.put("报修时间", formatDate(serviceRequest.getRequestTime()));
            workOrderMap.put("故障描述", serviceRequest.getRequestReason());
            if (sendStep != null) {
                workOrderMap.put("派工人", sendStep.getOwnerName());
            } else {
                workOrderMap.put("派工人", "无");
            }
            if (sendStep_2 != null) {
                workOrderMap.put("派工时间", formatDate(sendStep_2.getStartTime()));
            }
            workOrderMap.put("工单类型", workOrder.getIntExtType() != null ? FieldValueMessageController.fieldValue(I18N_INT_EXT_TYPE, workOrder.getIntExtType().toString()) : "");
            workOrderMap.put("上门维修/送修", workOrder.getRepairType() != null ? FieldValueMessageController.fieldValue(I18N_REPAIR_TYPE, workOrder.getRepairType().toString()) : "");
            if (acceptStep != null) {
                workOrderMap.put("接单人", acceptStep.getOwnerName());
                workOrderMap.put("接单时间", formatDate(acceptStep.getStartTime()));
            }
            if (workStep != null) {
                workOrderMap.put("维修完成时间", formatDate(workStep.getEndTime()));
            }
            workOrderMap.put("签到时间", formatDate(workOrder.getCheckinTime()));
            workOrderMap.put("维修工程师工时", workOrder.getManHours());
            workOrderMap.put("维修总工时", workOrder.getTotalManHour());

            putBlob(workOrderMap, workOrder.getSignature(), "signature");
            putBlob(workOrderMap, workOrder.getAckSignature(), "ack_signature");

            String info = "";
            double infoTotalPrice = 0;
            List<Map<String, Object>> parts = new ArrayList<>();
            for (V2_WorkOrder_Detail workOrderDetail : workOrderDetailList) {
                if (workOrderDetail.getApproved() == null || workOrderDetail.getApproved()) {
                    Integer tempQuantity = workOrderDetail.getApartsQuantity() != null ? workOrderDetail.getApartsQuantity() : 0;
                    Double tempPrice = workOrderDetail.getClosedPrice() != null ? workOrderDetail.getClosedPrice() : 0;
                    String invoceTimeStr = formatDate(workOrderDetail.getInvoiceTime());
                    Double partsPrice = workOrderDetail.getPartsPrice() != null ? workOrderDetail.getPartsPrice() : 0;
                    String supplier = workOrderDetail.getSupplier() != null ? workOrderDetail.getSupplier() : "";

                    info += workOrderDetail.getParts() + ",数量:" + tempQuantity + ",合同单价:" + tempPrice + ",合同号:" + workOrderDetail.getContractNum() + ",发票号:" + workOrderDetail.getInvoiceNum() + ",开票日期:" + invoceTimeStr + ",预计价格:" + partsPrice + ",供应商:" + supplier + ",备注:" + workOrderDetail.getComment() + ";";
                    infoTotalPrice += tempQuantity * tempPrice;
                }

                Map<String, Object> part = new HashedMap();
                part.put("备件名", workOrderDetail.getParts());
                part.put("单价", workOrderDetail.getClosedPrice());
                part.put("数量", workOrderDetail.getApartsQuantity());
                part.put("类型", getPartType(workOrderDetail.getPartsType()));
                part.put("序列号", workOrderDetail.getSerialNum());
                part.put("质保期", workOrderDetail.getWarrantyDate());
                if (workOrderDetail.getArrived() == null) {
                    part.put("到货", null);
                } else {
                    String arrived = workOrderDetail.getArrived().equals(Boolean.TRUE) ? "到货" : "未到货";
                    part.put("到货", arrived);
                }
                parts.add(part);
            }

            workOrderMap.put("维修备件", info);
            workOrderMap.put("备件总费用", infoTotalPrice);
            workOrderMap.put("其他费用", workOrder.getOtherExpense());
            workOrderMap.put("外修费用", workOrder.getExternalExpense());
            workOrderMap.put("维修总费用", workOrder.getTotalPrice());
            if (evaluateStep != null && workOrder.getCurrentStepId() >= 6) {
                workOrderMap.put("验收人", evaluateStep.getOwnerName());
                workOrderMap.put("验收时间", formatDate(evaluateStep.getEndTime()));
            }
            if (closeStep != null) {
                workOrderMap.put("关单人", closeStep.getOwnerName());
                workOrderMap.put("关单时间", formatDate(closeStep.getEndTime()));
            }
            String coworkerStr = "";
            String coworkManHourStr = "";
            int i = 0;
            for (V2_WorkOrderCoworker woc : workOrderList) {
                coworkerStr += woc.getCowokerUserName() + ",";
                coworkManHourStr += woc.getManHours() + ",";

                i++;
                workOrderMap.put("工时" + String.valueOf(i), woc.getManHours());
                workOrderMap.put("协作者" + String.valueOf(i), woc.getCowokerUserName());
            }
            workOrderMap.put("协作者", coworkerStr);
            workOrderMap.put("协作者工时", coworkManHourStr);
            workOrderMap.put("最终评价", workOrder.getFeedbackRating());

            if (workOrder.getCaseType() != null) {
                workOrderMap.put("故障类型", assetFaultTypeDao.findById(workOrder.getCaseType()) != null ? assetFaultTypeDao.findById(workOrder.getCaseType()).getFaultName() : "");
            }

            workOrderMap.put("原因描述", workOrder.getPatProblems());
            workOrderMap.put("解决方案", workOrder.getPatActions());
            workOrderMap.put("交付前检测", workOrder.getPatTests());
            workOrderMap.put("备注", workOrder.getComment());
            workOrderMap.put("二维码", assetInfo.getQrCode());
            workOrderMap.put("设备分类", assetInfo.getAssetGroup() != null ? FieldValueMessageController.fieldValue("assetGroup", assetInfo.getAssetGroup().toString()) : "");
            if (assetInfo.getFunctionGroup() != null) {
                workOrderMap.put("医疗器械分类", assetInfo.getFunctionGroup() != null ? FieldValueMessageController.fieldValue("assetFunctionType", assetInfo.getFunctionGroup().toString()) : "");
                workOrderMap.put("医疗器械子分类", assetInfo.getAssetCode68() != null ? FieldValueMessageController.fieldValue(assetInfo.getFunctionGroup().toString(), assetInfo.getAssetCode68().toString()) : "");
            } else {
                workOrderMap.put("医疗器械分类", "");
                workOrderMap.put("医疗器械子分类", "");
            }
            workOrderMap.put("设备型号", assetInfo.getFunctionType());

            //计算距上次报修天数
            String nearestWorkDay = "";
            if (!closeWorkOrderList.isEmpty() && workOrder.getCreatedDate() != null) {
                closeWorkOrderList = closeWorkOrderList.stream().filter(item -> item.getCloseTime() != null && item.getCloseTime().before(workOrder.getCreatedDate())).collect(Collectors.toList());
                if (!closeWorkOrderList.isEmpty()) {
                    Date closeTime = closeWorkOrderList.get(0).getCloseTime();
                    int days = (int) ((workOrder.getCreatedDate().getTime() - closeTime.getTime()) / (1000 * 3600 * 24));
                    nearestWorkDay = String.valueOf(days);
                }
            }
            workOrderMap.put("距上次报修天数", nearestWorkDay);
            //workOrderMap.put("距上次报修天数", serviceRequest.getNearestSrDays());
            workOrderMap.put("停机时间", formatDate(serviceRequest.getConfirmedDownTime()));
            workOrderMap.put("恢复时间", formatDate(serviceRequest.getConfirmedUpTime()));

            //added columns for pdf export
            workOrderMap.put("联系电话", (userAccountDao.getById(serviceRequest.getRequestorId()) != null) ? userAccountDao.getById(serviceRequest.getRequestorId()).getTelephone() : null);
            workOrderMap.put("设备编号", assetInfo.getDepartNum());
            workOrderMap.put("当前位置", serviceRequest.getAssetLocation() == null ? assetInfo.getLocationName()
                    : serviceRequest.getAssetLocation());
            workOrderMap.put("报修状态", getPdfStatus(assetInfo.getStatus()));


            workOrderMap.put("备件信息", parts);
            workOrderMap.put("维修流程", null);
            workOrderMap.put("工时", workOrder.getManHours());
            workOrderMap.put("维修类型", workOrder.getRepairType() != null ? FieldValueMessageController.fieldValue(I18N_REPAIR_TYPE, workOrder.getRepairType().toString()) : "");

            List<V2_WorkOrder_Step> steps = v2WorkOrderStepDao.findByWorkOrder(workOrder.getId());
            List<Map<String, Object>> woSteps = new ArrayList<Map<String, Object>>();
            Map<String, Object> woStep;
            for (V2_WorkOrder_Step step : steps) {
                woStep = new HashedMap();
                woStep.put("步骤名称", step.getStepName());
                woStep.put("责任人", step.getOwnerName());
                woStep.put("开始时间", formatDate(step.getStartTime()));
                woStep.put("结束时间", formatDate(step.getEndTime()));
                woStep.put("处理描述", step.getComments());
                woSteps.add(woStep);
            }

            workOrderMap.put("维修流程", woSteps);

            resultList.add(workOrderMap);
        }

        return resultList;
    }*/
/*
    private void putBlob(Map<String, Object> workOrderMap, String storageId, String key) {
        if (storageId != null) {
            workOrderMap.put(key, key);
        }
    }*/

    private String formatDate(Date date) {
        return date != null ? sdf.format(date) : "";
    }

    private String getPartType(String type) {
        String partType = null;

        if (type != null) {
            switch (type) {
                case "1":
                    partType = "库房领用";
                    break;
                case "2":
                    partType = "医工采购";
                    break;
                case "3":
                    partType = "外修采购";
                    break;
                case "4":
                    partType = "报废设备拆卸";
                    break;
                default:
                    break;
            }
        }
        return partType;
    }

    private String getSubStatus(Integer status) {
        String subStatus = null;

        if (status != null) {
            switch (status) {
                case 1:
                    subStatus = "等待备件";
                    break;
                case 2:
                    subStatus = "返厂维修-有备机";
                    break;
                case 3:
                    subStatus = "返厂维修-无备机";
                    break;
                default:
                    break;
            }
        }
        return subStatus;
    }
/*
    private String getPdfStatus(Integer status) {
        String assetStatus = null;

        if (status != null) {
            switch (status) {
                case 1:
                    assetStatus = "正常";
                    break;
                case 2:
                    assetStatus = "停机";
                    break;
                case 3:
                    assetStatus = "限制功能使用";
                    break;
                case 4:
                    assetStatus = "待报废";
                    break;
                case 5:
                    assetStatus = "停用";
                    break;
                default:
                    break;
            }
        }

        return assetStatus;
    }*/

    public Boolean exportData(ExcelDocument doc, List<Map<String, Object>> workOrderData, List<Map<String, Object>> partsData) {

        Integer keyRow_workOrder = 1;
        Integer dataRow_workOrder = 2;

        for (Map<String, Object> item : workOrderData) {
            doc.writeRowData(SHEET_WORKORDER, keyRow_workOrder, dataRow_workOrder++, item);
        }

        Integer keyRow_parts = 1;
        Integer dataRow_parts = 2;
        for (Map<String, Object> item : partsData) {
            doc.writeRowData(SHEET_PARTS, keyRow_parts, dataRow_parts++, item);
        }

        return Boolean.TRUE;
    }
/*

    //构建PageRequest
    private PageRequest buildPageRequest(Integer pageNumber, int pagzSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
        return new PageRequest(pageNumber, pagzSize, sort);
    }
*/


}
