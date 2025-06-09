package com.ge.apm.service.wo.doctemplate;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;
import java.util.Map;

public class CoworkerHoursTable  extends AbstractWoTableRenderPolicy{
    @Override
    public void render(XWPFTable table, Object o) {
        if (o instanceof List) {
            List<Map<String, Object>> parts = (List) o;
            for (Map<String, Object> part : parts) {
                XWPFTableRow row = addRow(table);
                addCell(row, extractValue(part, "协作者"));
                addCell(row, extractValue(part, "开始时间"));
                addCell(row, extractValue(part, "结束时间"));
                addCell(row, extractValue(part, "工时(小时)"));
                addCell(row, extractValue(part, "备注"));
            }
        }
        table.removeRow(1);
    }

}
