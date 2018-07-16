package com.ge.apm.service.utils.report;

import com.deepoove.poi.XWPFTemplate;
import com.ge.apm.domain.bo.MaintenanceTemplate;
import com.ge.apm.domain.pure.TableSchema;
import com.ge.apm.service.utils.SchemaResource;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.fail;

public class MaintenanceTemplateTest extends SchemaResource {

    @Test
    public void testReportTemplate() {
        XWPFTemplate template = null;
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("template/maintenance-template.docx");
                OutputStream fout = new FileOutputStream("target/maintenance-report-template.docx");
        ) {
            MaintenanceTemplate.Builder builder = new MaintenanceTemplate.Builder();
            TableSchema schema;
            schema = getSchema("reports/component.json");
            builder.safetyReport(schema);
            schema = getSchema("reports/defect1.json");
            builder.performanceReport(schema);

            template = XWPFTemplate.compile(is).render(builder.build().data());
            template.write(fout);
            fout.flush();
        } catch (IOException e) {
            fail("maintenance template generation failed");
        } finally {
            if (template != null) {
                try {
                    template.close();
                } catch (IOException e) {
                    fail("maintenance template close failed");
                }
            }
        }
    }

    @Test
    public void testEmptyPerformanceReportTemplate() {
        XWPFTemplate template = null;
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("template/maintenance-template.docx");
                OutputStream fout = new FileOutputStream("target/maintenance-report-empty-template.docx");
        ) {
            MaintenanceTemplate.Builder builder = new MaintenanceTemplate.Builder();

            template = XWPFTemplate.compile(is).render(builder.build().data());
            template.write(fout);
            fout.flush();
        } catch (IOException e) {
            fail("maintenance template generation failed");
        } finally {
            if (template != null) {
                try {
                    template.close();
                } catch (IOException e) {
                    fail("maintenance template close failed");
                }
            }
        }
    }
}
