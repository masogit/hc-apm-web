package com.ge.apm.service.utils;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class MicroServiceUtilTest {


    @InjectMocks
    MicroServiceUtil util;

    @Test
    public void docxToPdf() throws IOException {
        ReflectionTestUtils.setField(util, "template", new RestTemplate());
        ReflectionTestUtils.setField(util, "docxToPdfUrl", "http://apm-dev.hcdigital.com.cn/docxtopdf");
        byte[] bytes = util.docxToPdf(FileUtils.readFileToByteArray(new File("d:\\123.docx")));
        FileUtils.writeByteArrayToFile(new File("d:\\" + System.currentTimeMillis() + ".pdf"), bytes);
    }
}