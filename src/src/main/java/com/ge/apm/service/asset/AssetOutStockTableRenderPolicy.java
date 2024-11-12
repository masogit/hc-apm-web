package com.ge.apm.service.asset;

import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.*;

import java.util.List;
import java.util.Map;

public class AssetOutStockTableRenderPolicy extends AbstractWoTableRenderPolicy {

    private String stockType = "outStock";

    public AssetOutStockTableRenderPolicy() {

    }

    public AssetOutStockTableRenderPolicy(String stockType) {
        this.stockType = stockType;
    }

    public void render(XWPFTable table, Object o) {
        if (o instanceof List) {
            List<Map<String, Object>> arrivalItemList = (List) o;
            int rowIndex = 1;
            for (Map<String, Object> arrivalItem : arrivalItemList) {
                XWPFTableRow row = addRow(table, ++rowIndex);
                addCell(row, extractValue(arrivalItem, "type"));
                addCell(row, extractValue(arrivalItem, "assetName"));
                addCell(row, extractValue(arrivalItem, "functionType"));
                addCell(row, extractValue(arrivalItem, "unit"));
                addCell(row, extractValue(arrivalItem, "qty"));
                addCell(row, extractValue(arrivalItem, "purchasePrice"));
                addCell(row, extractValue(arrivalItem, "total"));
                if ("outStock".equals(stockType)) {
                    addCell(row, extractValue(arrivalItem, "vendor"));
                }
            }
        }
        table.removeRow(1);
    }

    protected void addCell(XWPFTableRow row, String textValue) {
        XWPFTableCell xwpfTableCell = row.addNewTableCell();
        xwpfTableCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        XWPFParagraph paragraph = xwpfTableCell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("DengXian");
        run.setFontSize(11);
        run.setText(textValue);
    }
}
