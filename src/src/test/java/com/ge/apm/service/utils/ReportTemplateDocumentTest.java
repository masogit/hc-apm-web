package com.ge.apm.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.apm.domain.pure.TableSchema;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

public class ReportTemplateDocumentTest {

    private TableSchema schema;

    @Before
    public void init() {
        URL path = this.getClass().getClassLoader().getResource("sample-report.json");
        // read json file
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            schema = objectMapper.readValue(path, TableSchema.class);
        } catch (IOException e) {
            schema = null;
        }
    }

    @Test
    public void testWriteExcel() {
        assertNotNull(schema);
        try (FileOutputStream out = new FileOutputStream("report-template.xlsx");
             Workbook wb = new XSSFWorkbook()
        ) {
            ReportTemplateDocument report = new ReportTemplateDocument(wb, schema, "Template");
            report.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
