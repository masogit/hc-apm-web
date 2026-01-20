package com.ge.apm.service.wo.doctemplate;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.policy.RenderPolicy;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.asset.ArrivalItemBatchTableRenderPolicy;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.ConfigItemService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.utils.MultilApproveStatusMapping;
import com.ge.apm.service.wo.WorkOrderExportService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.render.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import webapp.framework.web.WebUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class WorkOrderDocxRenderService {

    private final Logger log = LoggerFactory.getLogger(WorkOrderExportService.class);
    private static final PictureRenderData EMPTY_PICTURE = new PictureRenderData(0, 0, PictureType.JPEG, new byte[0]);

    @Autowired
    V2_WorkOrderRepository v2_workOrderRepository;

    @Autowired
    private BlobFileService blobService;

    @Autowired
    private ServiceRequestMsgRepository serviceRequestMsgRepository;

    @Autowired
    private V2_WorkOrderStepRepository v2WorkOrderStepDao;

    @Autowired
    V2_WorkOrderCoworkerRepository workOrderCoworkerDao;

    @Autowired
    private MultiLevelApprovalRepository multiLevelApprovalRepository;

    @Autowired
    private V2_WorkOrderManHoursDetailRepository manHoursDetailRepository;

    @Autowired
    private ConfigItemService configItemService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DecimalFormat df = new DecimalFormat("#0.00");

    private void copyKeyMapping(Map<String,String> COPY_KEY_MAPPING) {
        COPY_KEY_MAPPING.put("device_name", WebUtil.getMessage("EquipmentName_xlsx"));
        COPY_KEY_MAPPING.put("device_type", WebUtil.getMessage("EquipmentModel_xlsx"));
        COPY_KEY_MAPPING.put("device_serial", WebUtil.getMessage("EquipmentSerialNumber_xlsx"));
        COPY_KEY_MAPPING.put("device_qr", WebUtil.getMessage("QRCode_xlsx"));
        COPY_KEY_MAPPING.put("device_department", WebUtil.getMessage("Department"));
        COPY_KEY_MAPPING.put("device_site", WebUtil.getMessage("Site"));
        COPY_KEY_MAPPING.put("device_oem", WebUtil.getMessage("manufacture"));
        COPY_KEY_MAPPING.put("device_number", WebUtil.getMessage("EquipmentID_xlsx"));
        COPY_KEY_MAPPING.put("device_location", WebUtil.getMessage("CurrentLocation_xlsx"));
        COPY_KEY_MAPPING.put("device_registration_no", WebUtil.getMessage("registrationNo"));
        COPY_KEY_MAPPING.put("device_group", WebUtil.getMessage("EquipmentClassification_xlsx"));
        //质保
        COPY_KEY_MAPPING.put("device_factory_warranty_date", WebUtil.getMessage("warrantyDate"));
        //维保
        COPY_KEY_MAPPING.put("device_warranty_date", WebUtil.getMessage("factoryWarrantyDate"));
        COPY_KEY_MAPPING.put("device_purchasePrice", "设备价格");
        COPY_KEY_MAPPING.put("device_purchaseDate", "采购日期");

        COPY_KEY_MAPPING.put("issue_number", WebUtil.getMessage("srNum_xlsx"));
        COPY_KEY_MAPPING.put("issue_reporter", WebUtil.getMessage("requestorId"));
        COPY_KEY_MAPPING.put("issue_reporter_phone", WebUtil.getMessage("RequestorTelephone_xlsx"));
        COPY_KEY_MAPPING.put("issue_report_time", WebUtil.getMessage("RequestTime_xlsx"));
        COPY_KEY_MAPPING.put("issue_report_status", WebUtil.getMessage("RequestStatus_xlsx"));
        COPY_KEY_MAPPING.put("issue_device_stop_time", WebUtil.getMessage("DownTime_xlsx"));
        COPY_KEY_MAPPING.put("issue_desc", WebUtil.getMessage("FaultDescription_xlsx"));

        COPY_KEY_MAPPING.put("order_comment", WebUtil.getMessage("LatestDevelopments"));
        COPY_KEY_MAPPING.put("order_usedPart", "拟更换配件");
        COPY_KEY_MAPPING.put("fix_org_name", WebUtil.getMessage("OuterRepairOrgName"));
        COPY_KEY_MAPPING.put("fix_status", WebUtil.getMessage("WorkorderStatus_xlsx"));
        COPY_KEY_MAPPING.put("fix_hours", WebUtil.getMessage("TotalManhours_xlsx"));
        COPY_KEY_MAPPING.put("fix_fee", WebUtil.getMessage("TotalRepairPrice_xlsx"));
        COPY_KEY_MAPPING.put("fix_other_fee", WebUtil.getMessage("OtherExpenses_xlsx"));
        COPY_KEY_MAPPING.put("fix_ext_fee", WebUtil.getMessage("ExternalExpenses_xlsx"));
        COPY_KEY_MAPPING.put("fix_sign_time", WebUtil.getMessage("CheckInTime_xlsx"));
        COPY_KEY_MAPPING.put("fix_type", WebUtil.getMessage("WorkorderType_xlsx"));
        COPY_KEY_MAPPING.put("fix_error_type", WebUtil.getMessage("FaultType_xlsx"));

        COPY_KEY_MAPPING.put("fix_owner", WebUtil.getMessage("CurrentOwner_xlsx"));
        COPY_KEY_MAPPING.put("fix_reason", WebUtil.getMessage("ReasonDescription_xlsx"));
        COPY_KEY_MAPPING.put("fix_solution", WebUtil.getMessage("patActions"));
        COPY_KEY_MAPPING.put("fix_validation", WebUtil.getMessage("TestbeforeDelivery_xlsx"));
        COPY_KEY_MAPPING.put("feedback_rating", WebUtil.getMessage("feedbackRating"));
        COPY_KEY_MAPPING.put("feedback_comment", "feedbackComment");
        COPY_KEY_MAPPING.put("close_time", WebUtil.getMessage("CloseTime_xlsx"));

        /*COPY_KEY_MAPPING.put("fix_co1", "协作者1");
        COPY_KEY_MAPPING.put("fix_co2", "协作者2");
        COPY_KEY_MAPPING.put("fix_cohour1", "工时1");
        COPY_KEY_MAPPING.put("fix_cohour2", "工时2");*/

        COPY_KEY_MAPPING.put("fix_man_hour", WebUtil.getMessage("MaintenanceEngineerManhours_xlsx"));
        COPY_KEY_MAPPING.put("work_order_type", WebUtil.getMessage("WorkOrderTypeNew_xlsx"));

        COPY_KEY_MAPPING.put("part_table", "PartInformation");
        COPY_KEY_MAPPING.put("parts_picture_table", "parts_picture_table");
        //COPY_KEY_MAPPING.put("proceed_table", "维修流程");

        COPY_KEY_MAPPING.put("main_sub", "main_sub");
        COPY_KEY_MAPPING.put("spare01", "spare01");
        COPY_KEY_MAPPING.put("spare02", "spare02");
        COPY_KEY_MAPPING.put("spare03", "spare03");
        COPY_KEY_MAPPING.put("spare04", "spare04");
        COPY_KEY_MAPPING.put("spare05", "spare05");
        COPY_KEY_MAPPING.put("spare06", "spare06");
        COPY_KEY_MAPPING.put("spare07", "spare07");
        COPY_KEY_MAPPING.put("spare08", "spare08");
        COPY_KEY_MAPPING.put("spare09", "spare09");
        COPY_KEY_MAPPING.put("spare10", "spare10");
        COPY_KEY_MAPPING.put("related_order_number","related_order_number");
        COPY_KEY_MAPPING.put("case_priority","case_priority");
        COPY_KEY_MAPPING.put("travel_time", WebUtil.getMessage("travelTime"));
    }

    public byte[] renderDocx(Map<String, Object> map, byte[] templateBytes) throws IOException {
        Map<String, Object> datas = mapMyMap(map);
//        byte[] bytes;
//        XWPFTemplate template = null;
//        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
//             InputStream inputStream = new ByteArrayInputStream(templateBytes)) {
////            template = XWPFTemplate.compile(inputStream);
////            template.registerPolicy("part_table", new PartTableRenderPolicy());
////            template.registerPolicy("proceed_table", new ProceedTableRenderPolicy());
////            template.registerPolicy("audit_table", new AuditTableRenderPolicy());
////            template.registerPolicy("work_hours_table", new WorkHourTableRenderPolicy());
////            template.registerPolicy("parts_picture_table", new PartsPictureTableRenderPolicy(blobService,2));
//            Configure config = Configure.newBuilder()
//                    .bind("part_table", new PartTableRenderPolicy())
//                    .bind("proceed_table", new ProceedTableRenderPolicy())
//                    .bind("audit_table", new AuditTableRenderPolicy())
//                    .bind("work_hours_table", new WorkHourTableRenderPolicy())
//                    .bind("parts_picture_table", new PartsPictureTableRenderPolicy(blobService, 2))
//                    .build();
//
//            template = XWPFTemplate.compile(inputStream, config);
//
//            template.render(datas);
//            template.write(out);
//            bytes = out.toByteArray();
//        } finally {
//            try {
//                if (template != null) {
//                    template.close();
//                }
//            } catch (Exception ignore) {
//                log.error("render docx error:{}",ignore.getMessage());
//            }
//        }

        Map<String, RenderPolicy> bindings = new HashMap<>();
        bindings.put("part_table", new PartTableRenderPolicy());
        bindings.put("proceed_table", new ProceedTableRenderPolicy());
        bindings.put("audit_table", new AuditTableRenderPolicy());
        bindings.put("work_hours_table", new WorkHourTableRenderPolicy());
        bindings.put("coworker_hours_table", new CoworkerHoursTable());
        bindings.put("parts_picture_table",  new PartsPictureTableRenderPolicy(blobService, 2));
        return  WordExportServiceUtil.generate(datas, templateBytes, bindings);

    }

    private Map<String, Object> mapMyMap(Map<String, Object> source) {
        Map<String, Object> target = new HashMap<>();

        mapESign(source, target, "signature", "signature_title", "审核人签名： ");
        mapESign(source, target, "ack_signature", "ack_signature_title", "验收人签名： ");
        int size=0;
//        if (source.containsKey("auditSignatureList")) {
//           List<String> auditSignatures= (List)source.get("auditSignatureList");
//            log.info("auditSignatures:{}",auditSignatures.size());
//           if (auditSignatures!=null && !auditSignatures.isEmpty()){
//                size = auditSignatures.size();
//               for (int i = 0; i < size; i++) {
//                   String storageId = auditSignatures.get(i);
//                   if (storageId !=null){
//                       log.info("storageId:{}",storageId);
//                       mapESignSignature(storageId, target,"auditSignatureList"+i, "audit_signature_title"+i, "备件审核签名： ");
//                   }
//               }
//           }
//        }
//        if(size<5){
//            for (int i = size; i < 5; i++) {
//                mapESignSignature(null, target,"auditSignatureList"+i, "audit_signature_title"+i, "备件审核签名： ");
//            }
//        }
//        size=0;
//        if (source.containsKey("auditOpinionList")) {
//            List<String> auditOpinionList= (List) source.get("auditOpinionList");
//            size = auditOpinionList.size();
//            for (int i = 0; i < size; i++) {
//                String opinion = auditOpinionList.get(i);
//                target.put("audit_opinion_title" + i, "备件审核意见： ");
//                target.put("auditOpinionList" + i, opinion);
//            }
//        }
//        if(size<5){
//            for (int i = size; i < 5; i++) {
//                target.put("audit_opinion_title" + i, "");
//                target.put("auditOpinionList" + i, "");
//            }
//        }
//        size=0;
//        if (source.containsKey("auditDateList")) {
//            List<String> auditDateList= (List) source.get("auditDateList");
//            size = auditDateList.size();
//            for (int i = 0; i < size; i++) {
//                String date = auditDateList.get(i);
//                target.put("audit_date_title" + i, "备件审核时间： ");
//                target.put("auditDateList" + i, date);
//            }
//        }
//        if(size<5){
//            for (int i = size; i < 5; i++) {
//                target.put("audit_date_title" + i, "");
//                target.put("auditDateList" + i, "");
//            }
//        }
        size=0;
        if (source.containsKey("serviceRequestPicture")) {
            List<String> serviceRequestPicture= (List)source.get("serviceRequestPicture");
            if (serviceRequestPicture!=null && !serviceRequestPicture.isEmpty()){
                size = serviceRequestPicture.size();
                for (int i = 0; i < size; i++) {
                    String storageId = serviceRequestPicture.get(i);
                    if (storageId !=null){
                        mapESignSignature(storageId, target,"serviceRequestPicture"+i, "service_request_picture_title"+i, "报修图片： ",260,260);
                    }
                }
            }
        }
        if(size<5){
            for (int i = size; i < 5; i++) {
                mapESignSignature(null, target,"serviceRequestPicture"+i, "service_request_picture_title"+i, "报修图片： ");
            }
        }
        size=0;
        if (source.containsKey("workOrderPicture")) {
            List<String> workOrderPicture= (List)source.get("workOrderPicture");
            if (workOrderPicture!=null && !workOrderPicture.isEmpty()){
                size = workOrderPicture.size();
                for (int i = 0; i < size; i++) {
                    String storageId = workOrderPicture.get(i);
                    if (storageId !=null){
                        mapESignSignature(storageId, target,"workOrderPicture"+i, "workorder_picture_title"+i, "维修图片： ",260,260);
                    }
                }
            }
        }
        if(size<5){
            for (int i = size; i < 5; i++) {
                mapESignSignature(null, target,"workOrderPicture"+i, "workorder_picture_title"+i, "维修图片： ");
            }
        }
        size=0;
        if (source.containsKey("partsPicture")) {
            List<String> partsPicture= (List)source.get("partsPicture");
            if (partsPicture!=null && !partsPicture.isEmpty()){
                size = partsPicture.size();
                for (int i = 0; i < size; i++) {
                    String storageId = partsPicture.get(i);
                    if (storageId !=null){
                        mapESignSignature(storageId, target,"partsPicture"+i, "parts_picture_title"+i, "备件图片： ",260,260);
                    }
                }
            }
        }
        if(size<5){
            for (int i = size; i < 5; i++) {
                mapESignSignature(null, target,"partsPicture"+i, "parts_picture_title"+i, "备件图片： ");
            }
        }

        mapESign(source, target, "partsSignature", "partsSignature_title", "新增备件签名： ");
        mapESign(source, target, "medicalWorkerApproverSignature", "medicalWorkerApproverSignature_title", "医工组长签名： ");
        int num=0;
        if (source.containsKey("approverSignatureList")) {
            List<String> approverSignatures= (List)source.get("approverSignatureList");
            log.info("approverSignatureList:{}",approverSignatures.size());
            if (approverSignatures!=null && !approverSignatures.isEmpty()){
                num = approverSignatures.size();
                for (int i = 0; i < num; i++) {
                    String storageId = approverSignatures.get(i);
                    if (storageId !=null){
                        log.info("核心组storageId:{}",storageId);
                        int serialNumber=i+1;
                        mapESignSignature(storageId, target,"approverSignatureList"+i, "approverSignatureList_title"+i, "核心组签名"+serialNumber+"： ");
                    }
                }
            }
        }
        if(num<9){
            for (int i = num; i < 9; i++) {
                mapESignSignature(null, target,"approverSignatureList"+i, "approverSignatureList_title"+i, "核心组签名"+i+"： ");
            }
        }
        putWorkOrderStep(target, (String) source.get("woId"));
        putServiceRequestMessage(target, (String) source.get("woId"));
        putWorkOrderCoworker(target, (String) source.get("woId"));
        putWorkHourList(target, (String) source.get("woId"));
        putCoworkerHoursList(target, (String) source.get("woId"));
        Map<String, String> COPY_KEY_MAPPING = new HashMap<>();
        copyKeyMapping(COPY_KEY_MAPPING);
        for (Map.Entry<String, String> entry : COPY_KEY_MAPPING.entrySet()) {
            target.put(entry.getKey(), source.get(entry.getValue()));
        }


        if (source.containsKey("approvalMap")) {
            HashMap<String, String> approvalMap= (HashMap<String, String>) source.get("approvalMap");
            target.putAll(approvalMap);
        }
        if (source.containsKey("approvalMapKey")) {
            List<String> approvalMapKey = (List<String>) source.get("approvalMapKey");
            for (String approvalKey : approvalMapKey) {
                if(target.get(approvalKey)!=null){
                    String storageId = (String) target.get(approvalKey);

                    byte[] blob = getBlobSignature( storageId);
                    if (blob != null) {
                        target.put(approvalKey, new PictureRenderData(100, 75, PictureType.PNG, blob));
                    } else {
                        target.put(approvalKey, EMPTY_PICTURE);
                    }
                }else{
                    target.put(approvalKey, EMPTY_PICTURE);
                }

            }


        }

        return target;
    }

    private void mapESign(Map<String, Object> source, Map<String, Object> target, String key, String eSignTitleKey, String eSignTitleValue) {
        byte[] blob = getBlob(source, key);
        if (blob != null) {
            target.put(key, new PictureRenderData(100, 75, PictureType.PNG, blob));
            target.put(eSignTitleKey, eSignTitleValue);
        } else {
            target.put(key, EMPTY_PICTURE);
            target.put(eSignTitleKey, "");
        }
    }
    private void mapESignSignature(String storageId,Map<String, Object> target, String key, String eSignTitleKey, String eSignTitleValue) {
        mapESignSignature(storageId,target,key,eSignTitleKey,eSignTitleValue,100,75);
    }
    private void mapESignSignature(String storageId,Map<String, Object> target, String key, String eSignTitleKey, String eSignTitleValue,int width,int height) {
        byte[] blob = getBlobSignature( storageId);
        if (blob != null) {
            target.put(key, new PictureRenderData(width, height,PictureType.PNG, blob));
            target.put(eSignTitleKey, eSignTitleValue);
        } else {
            target.put(key, EMPTY_PICTURE);
            target.put(eSignTitleKey, "");
        }
    }

    private byte[] getBlob(Map<String, Object> map, String key) {
        if (map.containsKey(key)) {
            String storageId = (String) map.get(key);
            if (StringUtils.isNotEmpty(storageId)) {
                try (InputStream stream = blobService.getBlobObjectByStorageId(storageId).getStream();) {
                    return IOUtils.toByteArray(stream);
                } catch (IOException ignore) {
                    //ignore
                }
            }
        }
        return null;
    }
    private byte[] getBlobSignature(String storageId) {
        if (StringUtils.isNotEmpty(storageId)) {
            try (InputStream stream = blobService.getBlobObjectByStorageId(storageId).getStream();) {
                return IOUtils.toByteArray(stream);
            } catch (IOException ignore) {
                //ignore
            }
        }
        return null;
    }

    private void putServiceRequestMessage(Map<String, Object> workOrderMap, String id) {
        List<ServiceRequestMsg> serviceRequestMsgs = serviceRequestMsgRepository.findBySrIdOrderBySendTime(id);
        List<Map<String, Object>> srMsgs = new ArrayList<>();
        for (ServiceRequestMsg srMessage : serviceRequestMsgs) {
            Map<String, Object> map = new HashedMap();
            map.put("sendTime", formatDate(srMessage.getSendTime()));
            map.put("senderUserName", srMessage.getSenderUserName());
            map.put("message", srMessage.getMessage());
            srMsgs.add(map);
        }
        List<MultiLevelApproval> approveList = multiLevelApprovalRepository.getByBusinessUidList(id);
        if (!CollectionUtils.isEmpty(approveList)) {
            approveList.forEach(v -> {
                Map<String, Object> map = new HashedMap();
                map.put("sendTime", formatDate(v.getActualApproverDate()));
                map.put("senderUserName", v.getActualApproverName());
                map.put("message", MultilApproveStatusMapping.getApprovalNodeStatus(v.getApprovalNodeStatus()) + (StringUtils.isNotEmpty(v.getApprovalOpinion())?v.getApprovalOpinion():""));
                srMsgs.add(map);
            });
        }
        workOrderMap.put("audit_table", srMsgs);
    }

    private void putWorkOrderStep(Map<String, Object> workOrderMap, String id){

        List<V2_WorkOrder_Step> steps = v2WorkOrderStepDao.findByWorkOrder(id);
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

        workOrderMap.put("proceed_table", woSteps);
    }

    private void putWorkHourList(Map<String, Object> workOrderMap, String id){

        List<V2_WorkOrderManHoursDetail> steps = manHoursDetailRepository.findByWoIdOrderByStartTime(id);
        List<Map<String, Object>> workHourList = new ArrayList<>();
        for (V2_WorkOrderManHoursDetail detail : steps) {
            Map<String, Object> workHour = new HashedMap();
            workHour.put("开始时间", formatDate(detail.getStartTime()));
            workHour.put("结束时间", formatDate(detail.getEndTime()));
            workHour.put("工时(小时)", formatDouble(detail.getHours()));
            workHour.put("备注", detail.getRemark());
            workHourList.add(workHour);
        }

        workOrderMap.put("work_hours_table", workHourList);
    }

    private void putCoworkerHoursList(Map<String, Object> workOrderMap, String id){
        V2_WorkOrder workOrder = v2_workOrderRepository.findById(id);
        if(workOrder.getCoworkerEntryType() == null || workOrder.getCoworkerEntryType() == 0){
            workOrderMap.put("coworker_hours_table", new ArrayList<>());
            return;
        }
        List<V2_WorkOrderCoworker> v2WorkOrderCoworkers = workOrderCoworkerDao.findByWoIdOrderByCreatedDateDesc(id);
        List<Map<String, Object>> workHourList = new ArrayList<>();
        for(V2_WorkOrderCoworker co : v2WorkOrderCoworkers){
            Map<String, Object> workHour = new HashedMap();
            workHour.put("协作者", co.getCowokerUserName());
            workHour.put("开始时间", formatDate(co.getStartTime()));
            workHour.put("结束时间", formatDate(co.getEndTime()));
            workHour.put("工时(小时)", formatDouble(co.getManHours()));
            workHour.put("备注", co.getRemark());
            workHourList.add(workHour);
        }
        workOrderMap.put("coworker_hours_table", workHourList);
    }

    private void putWorkOrderCoworker(Map<String, Object> workOrderMap, String id){
        List<V2_WorkOrderCoworker> workOrderList = workOrderCoworkerDao.findByWoId(id);

        int i = 0;
        for (V2_WorkOrderCoworker woc : workOrderList) {
            i++;
            workOrderMap.put("fix_cohour" + String.valueOf(i), woc.getManHours());
            workOrderMap.put("fix_co" + String.valueOf(i), woc.getCowokerUserName());
        }
    }

    private String formatDate(Date date) {
        return date != null ? sdf.format(date) : "";
    }

    private String formatDouble(Double num) {
        return num != null ? df.format(num) : "";
    }

    public String getConfigItemValue(String key) {
      return   configItemService.getConfigItemByKeyToString(key, UserContextService.getCurrentUserAccount());
    }
}