package com.ge.apm.service.wo.doctemplate;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;
import java.util.Map;

public class PartTableRenderPolicy extends AbstractWoTableRenderPolicy {
    public void render(XWPFTable table, Object o) {

        List<Map<String, Object>> parts = (List) o;

        for (Map<String, Object> part : parts) {
            XWPFTableRow row = addRow(table);
            addCell(row, extractValue(part, "备件名"));
            addCell(row, extractValue(part, "单价"));
            addCell(row, extractValue(part, "数量"));
            addCell(row, extractValue(part, "类型"));
            addCell(row, extractValue(part, "序列号"));
            addCell(row, extractValue(part, "质保期"));
            addCell(row, extractValue(part, "到货"));
        }
        table.removeRow(1);
    }
}
