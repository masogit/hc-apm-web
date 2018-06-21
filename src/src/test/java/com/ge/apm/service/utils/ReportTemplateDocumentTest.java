package com.ge.apm.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.apm.domain.pure.TableSchema;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Testcase for {@link ReportTemplateDocument}.
 *
 * @author 212697838
 */
public class ReportTemplateDocumentTest {

    @Test
    public void testExcelComponent() {
        TableSchema schema = getSchema("reports/component.json");

        assertNotNull(schema);
        try (FileOutputStream out = new FileOutputStream("target/report-component.xlsx");
             Workbook wb = new XSSFWorkbook()
        ) {
            write(wb, schema, out);
        } catch (IOException e) {
            fail("component failed");
        }
    }

    @Test
    public void testDefect1() {
        TableSchema schema = getSchema("reports/defect1.json");

        assertNotNull(schema);
        try (FileOutputStream out = new FileOutputStream("target/report-defect1.xlsx");
             Workbook wb = new XSSFWorkbook()
        ) {
            write(wb, schema, out);
        } catch (IOException e) {
            fail("defect#1 failed");
        }

    }

    private TableSchema getSchema(String name) {
        TableSchema schema;
        URL path = this.getClass().getClassLoader().getResource(name);
        // read json file
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            schema = objectMapper.readValue(path, TableSchema.class);
        } catch (IOException e) {
            schema = null;
        }
        return schema;
    }

    private void write(Workbook wb, TableSchema schema, OutputStream out) throws IOException {
        ReportTemplateDocument report = new ReportTemplateDocument(wb, schema, "Template");
        report.write(out);
    }
}
