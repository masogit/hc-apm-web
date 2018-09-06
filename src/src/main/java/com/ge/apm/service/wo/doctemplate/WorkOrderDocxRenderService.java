package com.ge.apm.service.wo.doctemplate;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.PictureRenderData;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class WorkOrderDocxRenderService {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WorkOrderDocxRenderService.class);

    private static final PictureRenderData EMPTY_PICTURE = new PictureRenderData(0, 0, ".jpg", new byte[0]);

    static final Map<String, String> COPY_KEY_MAPPING = new HashMap<>();

    static {
        COPY_KEY_MAPPING.put("device_name", "设备名称");
        COPY_KEY_MAPPING.put("device_type", "设备型号");
        COPY_KEY_MAPPING.put("device_serial", "设备序列号");
        COPY_KEY_MAPPING.put("device_qr", "二维码");
        COPY_KEY_MAPPING.put("device_department", "科室");
        COPY_KEY_MAPPING.put("device_oem", "制造商");
        COPY_KEY_MAPPING.put("device_number", "设备编号");
        COPY_KEY_MAPPING.put("device_location", "当前位置");

        COPY_KEY_MAPPING.put("issue_number", "报修单号");
        COPY_KEY_MAPPING.put("issue_reporter", "报修人");
        COPY_KEY_MAPPING.put("issue_reporter_phone", "联系电话");
        COPY_KEY_MAPPING.put("issue_report_time", "报修时间");
        COPY_KEY_MAPPING.put("issue_report_status", "报修状态");
        COPY_KEY_MAPPING.put("issue_device_stop_time", "停机时间");
        COPY_KEY_MAPPING.put("issue_desc", "故障描述");

        COPY_KEY_MAPPING.put("fix_status", "工单状态");
        COPY_KEY_MAPPING.put("fix_hours", "维修工时");
        COPY_KEY_MAPPING.put("fix_fee", "维修总费用");
        COPY_KEY_MAPPING.put("fix_other_fee", "其他费用");
        COPY_KEY_MAPPING.put("fix_ext_fee", "外修费用");
        COPY_KEY_MAPPING.put("fix_sign_time", "签到时间");
        COPY_KEY_MAPPING.put("fix_type", "工单类型");
        COPY_KEY_MAPPING.put("fix_error_type", "故障类型");

        COPY_KEY_MAPPING.put("fix_owner", "当前责任人");
        COPY_KEY_MAPPING.put("fix_reason", "原因描述");
        COPY_KEY_MAPPING.put("fix_solution", "解决方案");
        COPY_KEY_MAPPING.put("fix_validation", "交付前检测");

        COPY_KEY_MAPPING.put("fix_co1", "协作者1");
        COPY_KEY_MAPPING.put("fix_co2", "协作者2");
        COPY_KEY_MAPPING.put("fix_cohour1", "工时1");
        COPY_KEY_MAPPING.put("fix_cohour2", "工时2");

        COPY_KEY_MAPPING.put("fix_man_hour", "工时");

        COPY_KEY_MAPPING.put("part_table", "备件信息");
        COPY_KEY_MAPPING.put("proceed_table", "维修流程");
        COPY_KEY_MAPPING.put("audit_table", "serviceRequestMessages");
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

        for (Map.Entry<String, String> entry : COPY_KEY_MAPPING.entrySet()) {
            target.put(entry.getKey(), source.get(entry.getValue()));
        }

        mapESign(source, target, "signature", "signature_title", "审核人签名： ");
        mapESign(source, target, "ack_signature", "ack_signature_title", "验收人签名： ");

        return target;
    }

    private void mapESign(Map<String, Object> source, Map<String, Object> target, String key, String eSignTitleKey, String eSignTitleValue) {
        if (source.containsKey(key)) {
            target.put(key, new PictureRenderData(100, 75, "mock.png", (byte[]) source.get(key)));
            target.put(eSignTitleKey, eSignTitleValue);
        } else {
            target.put(key, EMPTY_PICTURE);
            target.put(eSignTitleKey, "");
        }
    }
}