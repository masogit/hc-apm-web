package com.ge.apm.service.asset;


import com.ge.healthcare.cn.common.utils.render.AbstractWoTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.*;

import java.util.List;
import java.util.Map;

public class ArrivalItemBatchTableRenderPolicy extends AbstractWoTableRenderPolicy {

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
            List<Map<String, Object>> arrivalItemList = (List) o;
            for (Map<String, Object> arrivalItem : arrivalItemList) {
                XWPFTableRow row = addRow(table);
                addCell(row, extractValue(arrivalItem, "asset_name"));
                addCell(row, extractValue(arrivalItem, "brand"));
                addCell(row, extractValue(arrivalItem, "function_type"));
                addCell(row, extractValue(arrivalItem, "import_equipment"));
                addCell(row, extractValue(arrivalItem, "item_num"));
                addCell(row, extractValue(arrivalItem, "factory_warranty_date"));
                addCell(row, extractValue(arrivalItem, "annual_maintenance_times"));
                addCell(row, extractValue(arrivalItem, "manufact_num"));
                addCell(row, extractValue(arrivalItem, "hospital_num"));
                addCell(row, extractValue(arrivalItem, "location_name"));
            }
        }
        table.removeRow(1);

    }
}
