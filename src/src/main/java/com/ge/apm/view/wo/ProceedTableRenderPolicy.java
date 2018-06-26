package com.ge.apm.view.wo;

import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.*;

import java.util.List;
import java.util.Map;

public class ProceedTableRenderPolicy extends DynamicTableRenderPolicy {
    public void render(XWPFTable table, Object o) {

        List<Map<String, Object>> parts = (List) o;

        for (Map<String, Object> part : parts) {
            XWPFTableRow row = addRow(table);
            addCell(row, extractValue(part, "步骤名称"));
            addCell(row, extractValue(part, "责任人"));
            addCell(row, extractValue(part, "开始时间"));
            addCell(row, extractValue(part, "结束时间"));
            addCell(row, extractValue(part, "处理描述"));
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
