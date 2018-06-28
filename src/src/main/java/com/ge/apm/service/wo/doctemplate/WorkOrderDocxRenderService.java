package com.ge.apm.service.wo.doctemplate;

import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class WorkOrderDocxRenderService {

    static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WorkOrderDocxRenderService.class);

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

    private Map<String, Object> mapMyMap(Map<String, Object> map) {
        Map<String, Object> datas = new HashMap<>();
        datas.put("device_name", map.get("设备名称"));
        datas.put("device_type", map.get("设备类型"));
        datas.put("device_serial", map.get("设备序列号"));
        datas.put("device_qr", map.get("二维码"));
        datas.put("device_department", map.get("科室"));
        datas.put("device_oem", map.get("制造商"));
        datas.put("device_number", map.get("设备编号"));
        datas.put("device_location", map.get("当前位置"));

        datas.put("issue_number", map.get("报修单号"));
        datas.put("issue_reporter", map.get("报修人"));
        datas.put("issue_reporter_phone", map.get("联系电话"));
        datas.put("issue_report_time", map.get("报修时间"));
        datas.put("issue_report_status", map.get("报修状态"));
        datas.put("issue_device_stop_time", map.get("停机时间"));
        datas.put("issue_desc", map.get("故障描述"));

        datas.put("fix_status", map.get("工单状态"));
        datas.put("fix_hours", map.get("维修工时"));
        datas.put("fix_fee", map.get("维修总费用"));
        datas.put("fix_other_fee", map.get("其他费用"));
        datas.put("fix_ext_fee", map.get("外修费用"));
        datas.put("fix_sign_time", map.get("签到时间"));
        datas.put("fix_type", map.get("工单类型"));
        datas.put("fix_error_type", map.get("故障类型"));
        //todo
        datas.put("fix_owner", map.get("当前责任人"));
        datas.put("fix_reason", map.get("原因描述"));
        datas.put("fix_solution", map.get("解决方案"));
        datas.put("fix_validation", map.get("交付前检测"));

        datas.put("fix_co1", map.get("协作者1"));
        datas.put("fix_co2", map.get("协作者2"));
        datas.put("fix_cohour1", map.get("工时1"));
        datas.put("fix_cohour2", map.get("工时2"));

        datas.put("fix_man_hour", map.get("工时"));

        datas.put("part_table", map.get("备件信息"));
        datas.put("proceed_table", map.get("维修流程"));
        datas.put("audit_table", map.get("serviceRequestMessages"));

        return datas;
    }
}
