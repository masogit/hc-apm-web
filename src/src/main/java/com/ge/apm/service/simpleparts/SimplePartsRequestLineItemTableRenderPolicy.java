package com.ge.apm.service.simpleparts;

import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.*;

import java.util.List;
import java.util.Map;

public class SimplePartsRequestLineItemTableRenderPolicy extends AbstractWoTableRenderPolicy {

    protected void addCell(XWPFTableRow row, String textValue) {
        XWPFTableCell xwpfTableCell = row.addNewTableCell();
        xwpfTableCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        XWPFParagraph paragraph = xwpfTableCell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Microsoft YaHei");
        run.setFontSize(9);
        run.setText(textValue);
    }

    public void render(XWPFTable table, Object o) {
        if (o instanceof List) {
            List<Map<String, Object>> lineItemList = (List) o;
            for (Map<String, Object> lineItem : lineItemList) {
                XWPFTableRow row = addRow(table);
                addCell(row, extractValue(lineItem, "asset_name"));
                addCell(row, extractValue(lineItem, "request_code"));
                addCell(row, extractValue(lineItem, "work_order_no"));
                addCell(row, extractValue(lineItem, "transaction_qty"));
                addCell(row, extractValue(lineItem, "unit_price"));
                addCell(row, extractValue(lineItem, "total_prices"));
                addCell(row, extractValue(lineItem, "warehouse_name"));
                addCell(row, extractValue(lineItem, "transport_company"));
                addCell(row, extractValue(lineItem, "transport_no"));
                addCell(row, extractValue(lineItem, "address_info"));
            }
        }
        table.removeRow(1);
    }
}
