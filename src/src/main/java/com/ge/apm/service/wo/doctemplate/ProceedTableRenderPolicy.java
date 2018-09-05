package com.ge.apm.service.wo.doctemplate;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;
import java.util.Map;

public class ProceedTableRenderPolicy extends AbstractWoTableRenderPolicy {
    public void render(XWPFTable table, Object o) {

        List<Map<String, Object>> parts = (List) o;

        if (parts != null) {
            for (Map<String, Object> part : parts) {
                XWPFTableRow row = addRow(table);
                addCell(row, extractValue(part, "步骤名称"));
                addCell(row, extractValue(part, "责任人"));
                addCell(row, extractValue(part, "开始时间"));
                addCell(row, extractValue(part, "结束时间"));
                addCell(row, extractValue(part, "处理描述"));
            }
        }
        table.removeRow(1);
    }
}
