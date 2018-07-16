package com.ge.apm.service.wo.doctemplate;

import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.*;

import java.util.Map;

public abstract class AbstractWoTableRenderPolicy extends DynamicTableRenderPolicy {
    protected String extractValue(Map<String, Object> part, String key) {
        Object obj = part.get(key);
        if (obj == null) {
            return "";
        }
        return String.valueOf(obj);
    }

    protected XWPFTableRow addRow(XWPFTable table) {
        XWPFTableRow row = table.insertNewTableRow(table.getNumberOfRows());
        row.setHeight(300);
        row.setCantSplitRow(false);
        row.setRepeatHeader(true);
        return row;
    }

    protected void addCell(XWPFTableRow row, String textValue) {
        XWPFTableCell xwpfTableCell = row.addNewTableCell();
        xwpfTableCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        XWPFParagraph paragraph = xwpfTableCell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Calibri");
        run.setFontSize(9);
        run.setText(textValue);
    }
}
