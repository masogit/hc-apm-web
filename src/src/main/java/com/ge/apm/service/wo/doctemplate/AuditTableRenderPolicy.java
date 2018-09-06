package com.ge.apm.service.wo.doctemplate;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;
import java.util.Map;

public class AuditTableRenderPolicy extends AbstractWoTableRenderPolicy {
    public void render(XWPFTable table, Object o) {

        if (o instanceof List) {
            List<Map<String, Object>> parts = (List) o;
            for (Map<String, Object> part : parts) {
                XWPFTableRow row = addRow(table);
                addCell(row, extractValue(part, "sendTime"));
                addCell(row, extractValue(part, "senderUserName"));
                addCell(row, extractValue(part, "message"));
            }
        }
        table.removeRow(1);

    }
}
