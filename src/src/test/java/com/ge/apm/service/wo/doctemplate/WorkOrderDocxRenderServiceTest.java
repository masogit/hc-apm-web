package com.ge.apm.service.wo.doctemplate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
public class WorkOrderDocxRenderServiceTest {

    private WorkOrderDocxRenderService workOrderDocxRenderService = new WorkOrderDocxRenderService();

    private static final String[] KEYS_PARTS = {"备件名", "单价", "数量", "类型", "序列号", "质保期", "到货"};
    private static final String[] KEYS_PROC = {"步骤名称", "责任人", "开始时间", "结束时间", "处理描述"};
    private static final String[] KEYS_AUDIT = {"sendTime", "senderUserName", "message"};

    private static byte[] template;

    static {
        try (FileInputStream input = new FileInputStream("src\\main\\webapp\\portal\\wo\\woExportTemplate\\template.docx")) {
            template = IOUtils.toByteArray(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRenderEmptyFile() throws IOException {
        Map<String, Object> map = new HashMap<>();
        byte[] outputBytes = workOrderDocxRenderService.renderDocx(map, template);
        assertTrue(outputBytes.length > 0);
//        write(outputBytes, "test_wo_render_empty");
    }

    @Test
    public void testRenderFullData() throws IOException {
        Map<String, Object> map = mockFullMap();
        byte[] outputBytes = workOrderDocxRenderService.renderDocx(map, template);
        assertTrue(outputBytes.length > 0);
//        write(outputBytes, "test_wo_render_fulldata");
    }

    private Map<String, Object> mockFullMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("设备名称", "设备名称");
        map.put("设备型号", "设备型号");
        map.put("设备序列号", "设备序列号");
        map.put("二维码", "二维码");
        map.put("科室", "科室");
        map.put("制造商", "制造商");
        map.put("设备编号", "设备编号");
        map.put("当前位置", "当前位置");

        map.put("报修单号", "报修单号");
        map.put("报修人", "报修人");
        map.put("联系电话", "联系电话");
        map.put("报修时间", "报修时间");
        map.put("报修状态", "报修状态");
        map.put("停机时间", "停机时间");
        map.put("故障描述", "故障描述");

        map.put("工单状态", "工单状态");
        map.put("维修工时", "维修工时");
        map.put("维修总费用", "维修总费用");
        map.put("其他费用", "其他费用");
        map.put("外修费用", "外修费用");
        map.put("签到时间", "签到时间");
        map.put("工单类型", "工单类型");
        map.put("故障类型", "故障类型");

        map.put("当前责任人", "当前责任人");
        map.put("原因描述", "原因描述");
        map.put("解决方案", "解决方案");
        map.put("交付前检测", "交付前检测");

        map.put("协作者1", "协作者1");
        map.put("协作者2", "协作者2");
        map.put("工时1", "工时1");
        map.put("工时2", "工时2");

        map.put("工时", "工时");

        map.put("备件信息", mockListMaps(KEYS_PARTS));
        map.put("维修流程", mockListMaps(KEYS_PROC));
        map.put("serviceRequestMessages", mockListMaps(KEYS_AUDIT));

        try {
            map.put("signature", FileUtils.readFileToByteArray(new File("src\\test\\resources\\sign\\verify.png")));
            map.put("ack_signature", FileUtils.readFileToByteArray(new File("src\\test\\resources\\sign\\acceptance.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private List<Map<String, Object>> mockListMaps(String[] keys) {
        List<Map<String, Object>> parts = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Map<String, Object> map = new HashMap<>();
            for (String key : keys) {
                map.put(key, key + i);
            }
            parts.add(map);
        }
        return parts;
    }

    private void write(byte[] outputBytes, String fileName) throws IOException {
        FileUtils.writeByteArrayToFile(new File(fileName + ".docx"), outputBytes);
    }
}