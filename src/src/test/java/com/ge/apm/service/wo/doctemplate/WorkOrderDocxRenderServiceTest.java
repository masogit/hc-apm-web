package com.ge.apm.service.wo.doctemplate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
public class WorkOrderDocxRenderServiceTest {

    WorkOrderDocxRenderService workOrderDocxRenderService;

    @Test
    public void testRenderEmptyFile() throws IOException {
        try (FileInputStream input = new FileInputStream("src\\main\\webapp\\portal\\wo\\woExportTemplate\\template.docx")) {
            byte[] template = IOUtils.toByteArray(input);
            Map<String, Object> map = new HashMap<>();
            byte[] outputBytes = workOrderDocxRenderService.renderDocx(map, template);
            assertTrue(outputBytes.length > 0);
            //write(outputBytes);
        }
    }

    private void write(byte[] outputBytes) throws IOException {
        FileUtils.writeByteArrayToFile(new File("test.docx"), outputBytes);
    }
}