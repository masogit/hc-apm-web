package com.ge.apm.service.wo.doctemplate;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.PictureRenderData;
import com.ge.apm.dao.ServiceRequestMsgRepository;
import com.ge.apm.dao.V2_WorkOrderCoworkerRepository;
import com.ge.apm.dao.V2_WorkOrderStepRepository;
import com.ge.apm.domain.ServiceRequestMsg;
import com.ge.apm.domain.V2_WorkOrderCoworker;
import com.ge.apm.domain.V2_WorkOrder_Step;
import com.ge.apm.service.asset.BlobFileService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class WorkOrderDocxRenderService {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WorkOrderDocxRenderService.class);

    private static final PictureRenderData EMPTY_PICTURE = new PictureRenderData(0, 0, ".jpg", new byte[0]);



    @Autowired
    private BlobFileService blobService;

    @Autowired
    private ServiceRequestMsgRepository serviceRequestMsgRepository;

    @Autowired
    private V2_WorkOrderStepRepository v2WorkOrderStepDao;

    @Autowired
    V2_WorkOrderCoworkerRepository workOrderCoworkerDao;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private void copyKeyMapping(Map<String,String> COPY_KEY_MAPPING) {
        COPY_KEY_MAPPING.put("device_name", WebUtil.getMessage("EquipmentName_xlsx"));
        COPY_KEY_MAPPING.put("device_type", WebUtil.getMessage("EquipmentModel_xlsx"));
        COPY_KEY_MAPPING.put("device_serial", WebUtil.getMessage("EquipmentSerialNumber_xlsx"));
        COPY_KEY_MAPPING.put("device_qr", WebUtil.getMessage("QRCode_xlsx"));
        COPY_KEY_MAPPING.put("device_department", WebUtil.getMessage("Department"));
        COPY_KEY_MAPPING.put("device_oem", WebUtil.getMessage("manufacture"));
        COPY_KEY_MAPPING.put("device_number", WebUtil.getMessage("EquipmentID_xlsx"));
        COPY_KEY_MAPPING.put("device_location", WebUtil.getMessage("CurrentLocation_xlsx"));

        COPY_KEY_MAPPING.put("issue_number", WebUtil.getMessage("srNum_xlsx"));
        COPY_KEY_MAPPING.put("issue_reporter", WebUtil.getMessage("requestorId"));
        COPY_KEY_MAPPING.put("issue_reporter_phone", WebUtil.getMessage("RequestorTelephone_xlsx"));
        COPY_KEY_MAPPING.put("issue_report_time", WebUtil.getMessage("RequestTime_xlsx"));
        COPY_KEY_MAPPING.put("issue_report_status", WebUtil.getMessage("RequestStatus_xlsx"));
        COPY_KEY_MAPPING.put("issue_device_stop_time", WebUtil.getMessage("DownTime_xlsx"));
        COPY_KEY_MAPPING.put("issue_desc", WebUtil.getMessage("FaultDescription_xlsx"));

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

        /*COPY_KEY_MAPPING.put("fix_co1", "协作者1");
        COPY_KEY_MAPPING.put("fix_co2", "协作者2");
        COPY_KEY_MAPPING.put("fix_cohour1", "工时1");
        COPY_KEY_MAPPING.put("fix_cohour2", "工时2");*/

        COPY_KEY_MAPPING.put("fix_man_hour", WebUtil.getMessage("MaintenanceEngineerManhours_xlsx"));

        COPY_KEY_MAPPING.put("part_table", "PartInformation");
        //COPY_KEY_MAPPING.put("proceed_table", "维修流程");
    }

    public byte[] renderDocx(Map<String, Object> map, byte[] templateBytes) throws IOException {
        Map<String, Object> datas = mapMyMap(map);
        byte[] bytes;
        XWPFTemplate template = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             InputStream inputStream = new ByteArrayInputStream(templateBytes)) {
            template = XWPFTemplate.compile(inputStream);
            template.registerPolicy("part_table", new PartTableRenderPolicy());
            template.registerPolicy("proceed_table", new ProceedTableRenderPolicy());
            template.registerPolicy("audit_table", new AuditTableRenderPolicy());
            template.render(datas);
            template.write(out);
            bytes = out.toByteArray();
        } finally {
            try {
                if (template != null) {
                    template.close();
                }
            } catch (Exception ignore) {

            }
        }
        return bytes;
    }

    private Map<String, Object> mapMyMap(Map<String, Object> source) {
        Map<String, Object> target = new HashMap<>();

        mapESign(source, target, "signature", "signature_title", "审核人签名： ");
        mapESign(source, target, "ack_signature", "ack_signature_title", "验收人签名： ");

        putWorkOrderStep(target, (String) source.get("woId"));
        putServiceRequestMessage(target, (String) source.get("woId"));
        putWorkOrderCoworker(target, (String) source.get("woId"));
        Map<String, String> COPY_KEY_MAPPING = new HashMap<>();
        copyKeyMapping(COPY_KEY_MAPPING);
        for (Map.Entry<String, String> entry : COPY_KEY_MAPPING.entrySet()) {
            target.put(entry.getKey(), source.get(entry.getValue()));
        }

        return target;
    }

    private void mapESign(Map<String, Object> source, Map<String, Object> target, String key, String eSignTitleKey, String eSignTitleValue) {
        byte[] blob = getBlob(source, key);
        if (blob != null) {
            target.put(key, new PictureRenderData(100, 75, "mock.png", blob));
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
}