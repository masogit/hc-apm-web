package com.ge.apm.service.utils;

import com.deepoove.poi.XWPFTemplate;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class MicroServiceUtilTest {


    @InjectMocks
    MicroServiceUtil util;

    @Test
    public void docxToPdf() throws IOException {
        ReflectionTestUtils.setField(util, "template", new RestTemplate());
        ReflectionTestUtils.setField(util, "docxToPdfUrl", "http://apm-dev.hcdigital.com.cn/docxtopdf");
        byte[] content = FileUtils.readFileToByteArray(new File("D:\\pccExamineReportTemplate.docx"));
        HashMap<String, Object> datas = new HashMap<>();
        datas.put("hospitalName", "华山医院");
        datas.put("functionType", "华山医院");
        datas.put("lineStatus", "华山医院");
        byte[] docxBytes = this.renderDocx(datas, content);
        byte[] bytes = util.docxToPdf(docxBytes);
        FileUtils.writeByteArrayToFile(new File("d:\\" + System.currentTimeMillis() + ".pdf"), bytes);
    }

    private byte[] renderDocx(Map<String, Object> datas, byte[] templateBytes) throws IOException {
        byte[] bytes;
        XWPFTemplate template = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); InputStream inputStream = new ByteArrayInputStream(templateBytes)) {
            template = XWPFTemplate.compile(inputStream);
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
}