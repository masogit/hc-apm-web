package com.ge.apm.service.wo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.WorkOrderReportMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.WorkOrderExportPojo;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.io.IOException;
import java.io.InputStream;
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

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    ServiceRequestRepository serviceRequestDao;
    @Autowired
    private V2_WorkOrderRepository v2WorkOrderDao;
    @Autowired
    private V2_WorkOrderStepRepository v2WorkOrderStepDao;
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
    private OrgInfoTreeService orgInfoTreeService;
    @Autowired
    private WorkOrderReportMapper worDao;

    public List<Map<String, Object>> createWorkOrderData(OrgInfo selectedOrg, Date startTime, Date endTime, Date closedStartTime, Date closedEndTime, List<Integer> statusList,OrgInfo clinicalDept,UserAccount currentOwner, List<OrgInfo> clinicalDeptList, String filterParts, String filterTotalPrice){
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

        List<WorkOrderExportPojo> workOrderList = worDao.workOrderExport(searchFilters);

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
            workOrderMap.put(WebUtil.getMessage("CurrentOwner_xlsx"), workOrderExportPojo.getCurrentPersonName());
            //workOrderMap.put("当前步骤", workOrderExportPojo.getCurrentStepName());
            workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"), workOrderExportPojo.getCurrentStepId() != null ? FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, workOrderExportPojo.getCurrentStepId().toString()) : "");
            workOrderMap.put(WebUtil.getMessage("requestorId"), workOrderExportPojo.getRequestorName());
            workOrderMap.put(WebUtil.getMessage("RequestTime_xlsx"), formatDate(workOrderExportPojo.getRequestTime()));
            workOrderMap.put(WebUtil.getMessage("FaultDescription_xlsx"), workOrderExportPojo.getRequestReason());
            workOrderMap.put(WebUtil.getMessage("dispatchUserName"), workOrderExportPojo.getSendStepOwnerName());
            workOrderMap.put(WebUtil.getMessage("AssignTime_xlsx"), formatDate(workOrderExportPojo.getSendStepStartTime()));
            //workOrderMap.put("工单类型", workOrderExportPojo.getIntExtTypeName());
            workOrderMap.put(WebUtil.getMessage("WorkorderType_xlsx"), workOrderExportPojo.getIntExtType() != null ? FieldValueMessageController.fieldValue(I18N_INT_EXT_TYPE, workOrderExportPojo.getIntExtType().toString()) : "");
            //workOrderMap.put("维修类型", workOrderExportPojo.getRepairTypeName());
            workOrderMap.put(WebUtil.getMessage("RepairType_xlsx"), workOrderExportPojo.getRepairType() != null ? FieldValueMessageController.fieldValue(I18N_REPAIR_TYPE, workOrderExportPojo.getRepairType().toString()) : "");
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
            workOrderMap.put(WebUtil.getMessage("maintanenceValue_xlsx"), workOrderExportPojo.getMaintanenceValue());
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

            if (workOrderExportPojo.getAssetFunctionGroup() != null) {
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceClassification_xlsx"), workOrderExportPojo.getAssetFunctionGroup() != null ? FieldValueMessageController.fieldValue("assetFunctionType", workOrderExportPojo.getAssetFunctionGroup().toString()) : "");
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceSubclassification_xlsx"), workOrderExportPojo.getAssetCode68() != null ? FieldValueMessageController.fieldValue(workOrderExportPojo.getAssetFunctionGroup().toString(), workOrderExportPojo.getAssetCode68().toString()) : "");
            } else {
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceClassification_xlsx"), "");
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceSubclassification_xlsx"), "");
            }

            /*String sendStepInfo = workOrderExportPojo.getSendStepInfo();
            if(sendStepInfo != null){
                String [] sendStepInfoArray = sendStepInfo.split("\\|:\\|");
                workOrderMap.put("派工人", sendStepInfoArray.length >= 1 ? sendStepInfoArray[0] : "");
                workOrderMap.put("派工时间", sendStepInfoArray.length >= 2 ? sendStepInfoArray[1] : "");
            }else {
                workOrderMap.put("派工人", "");
                workOrderMap.put("派工时间", "");
            }
            String acceptStepInfo = workOrderExportPojo.getAcceptStepInfo();
            if(acceptStepInfo != null){
                String [] acceptStepInfoArray = acceptStepInfo.split("\\|:\\|");
                workOrderMap.put("接单人", acceptStepInfoArray.length >= 1 ? acceptStepInfoArray[0] : "");
                workOrderMap.put("接单时间", acceptStepInfoArray.length >= 2 ? acceptStepInfoArray[1] : "");
            }else {
                workOrderMap.put("接单人", "");
                workOrderMap.put("接单时间", "");
            }
            String evaluateStepInfo = workOrderExportPojo.getEvaluateStepInfo();
            if(evaluateStepInfo != null){
                String [] evaluateStepInfoArray = evaluateStepInfo.split("\\|:\\|");
                workOrderMap.put("验收人", evaluateStepInfoArray.length >= 1 ? evaluateStepInfoArray[0] : "");
                workOrderMap.put("验收时间", evaluateStepInfoArray.length >= 2 ? evaluateStepInfoArray[1] : "");
            }
            String closeStepInfo = workOrderExportPojo.getCloseStepInfo();
            if(closeStepInfo != null){
                String [] closeStepInfoArray = closeStepInfo.split("\\|:\\|");
                workOrderMap.put("关单人", closeStepInfoArray.length >=1 ? closeStepInfoArray[0] : "");
                workOrderMap.put("关单时间", closeStepInfoArray.length >=2 ? closeStepInfoArray[1] : "");
            }else {
                workOrderMap.put("关单人", "");
                workOrderMap.put("关单时间", "");
            }*/

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

            this.putPartsData(workOrderExportPojo ,workOrderMap);
            //this.putWorkOrderStep(workOrderExportPojo ,workOrderMap);

            resultList.add(workOrderMap);
        }

        return resultList;

    }

    public List<Map<String, Object>> createPartsData(List<Map<String, Object>> workOrderList){

        List<Map<String, Object>> partList = new ArrayList<>();
        for (Map<String, Object> workOrderMap : workOrderList) {
            if(workOrderMap.containsKey("PartInformation")){
                partList.addAll((List<Map<String, Object>>)workOrderMap.get("PartInformation"));
            }
        }
        return partList;
    }

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
    }

    private void putWorkOrderStep(WorkOrderExportPojo workOrderPojo, Map<String, Object> workOrderMap){

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
    }

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
            workOrderMap.put("协助者", coworkerStr);
            workOrderMap.put("协助者工时", coworkManHourStr);
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
			/*if(serviceRequest.getStatus() == 2)
			{
				workOrderMap.put("停机时间", (serviceRequest.getConfirmedDownTime() != null ? serviceRequest.getConfirmedDownTime():null));
			}
			else
			{
				workOrderMap.put("停机时间", "未停机");
			}*/

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
    }

    private void putBlob(Map<String, Object> workOrderMap, String storageId, String key) {
        if (storageId != null) {
            workOrderMap.put(key, key);
        }
    }

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
    }

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

    //构建PageRequest
    private PageRequest buildPageRequest(Integer pageNumber, int pagzSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
        return new PageRequest(pageNumber, pagzSize, sort);
    }

}
