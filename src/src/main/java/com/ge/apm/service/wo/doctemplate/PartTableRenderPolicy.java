package com.ge.apm.service.wo.doctemplate;

import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.*;

import java.util.List;
import java.util.Map;

public class PartTableRenderPolicy extends DynamicTableRenderPolicy {
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

    private String extractValue(Map<String, Object> part, String key) {
        Object obj = part.get(key);
        if (obj == null) {
            return "";
        }
        return String.valueOf(obj);
    }

    XWPFTableRow addRow(XWPFTable table) {
        XWPFTableRow row = table.insertNewTableRow(table.getNumberOfRows());
        row.setHeight(300);
        row.setCantSplitRow(false);
        row.setRepeatHeader(true);
        return row;
    }

    void addCell(XWPFTableRow row, String textValue) {
        XWPFTableCell xwpfTableCell = row.addNewTableCell();
        xwpfTableCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        XWPFParagraph paragraph = xwpfTableCell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Calibri");
        run.setFontSize(9);
        run.setColor("1F497D");
        run.setText(textValue);
    }
}
