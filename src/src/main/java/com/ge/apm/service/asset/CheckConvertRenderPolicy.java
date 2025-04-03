package com.ge.apm.service.asset;

import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckConvertRenderPolicy extends DynamicTableRenderPolicy {

    private List<String> checkItems=new ArrayList<>();

    public CheckConvertRenderPolicy(List<String> items){
        this.checkItems=items;
    }


    public void render(XWPFTable table, Object o) {

        if (o instanceof List) {
            List<Map<String, Object>> list = (List) o;

            for (Map<String, Object> map : list) {
                XWPFTableRow row = addRow(table);
                for (String checkItem : checkItems) {
                    addCell(row, extractValue(map, checkItem));
                }

            }

        }

        table.removeRow(2);

    }

    protected XWPFTableRow addRow(XWPFTable table) {
        return addRow(table, table.getNumberOfRows());
    }

    protected XWPFTableRow addRow(XWPFTable table, int n) {
        XWPFTableRow row = table.insertNewTableRow(n);
        row.setHeight(300);
        row.setCantSplitRow(false);
        row.setRepeatHeader(true);
        return row;
    }
    protected String extractValue(Map part, String key) {
        Object obj = part.get(key);
        if (obj == null) {
            return "";
        }
        return String.valueOf(obj);
    }
    protected void addCell(XWPFTableRow row, String textValue) {
        XWPFTableCell xwpfTableCell = row.addNewTableCell();
        xwpfTableCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        XWPFParagraph paragraph = xwpfTableCell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Calibri");
        run.setFontSize(11);
        run.setText(textValue);
    }



}
