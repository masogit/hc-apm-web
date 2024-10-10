package com.ge.apm.service.asset;

import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;
import java.util.Map;

public class AssetOutStockTableRenderPolicy extends AbstractWoTableRenderPolicy {

    public void render(XWPFTable table, Object o) {

        if (o instanceof List) {
            List<Map<String, Object>> arrivalItemList = (List) o;
            for (Map<String, Object> arrivalItem : arrivalItemList) {
                XWPFTableRow row = addRow(table);
                addCell(row, extractValue(arrivalItem, "type"));
                addCell(row, extractValue(arrivalItem, "assetName"));
                addCell(row, extractValue(arrivalItem, "functionType"));
                addCell(row, extractValue(arrivalItem, "unit"));
                addCell(row, extractValue(arrivalItem, "qty"));
                addCell(row, extractValue(arrivalItem, "purchasePrice"));
                addCell(row, extractValue(arrivalItem, "total"));
                addCell(row, extractValue(arrivalItem, "vendor"));
            }
        }
        table.removeRow(1);

    }
}
