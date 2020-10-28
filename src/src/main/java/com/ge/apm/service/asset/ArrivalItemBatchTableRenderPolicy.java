package com.ge.apm.service.asset;

import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;
import java.util.Map;

public class ArrivalItemBatchTableRenderPolicy extends AbstractWoTableRenderPolicy {

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
                addCell(row, extractValue(arrivalItem, "guarantee"));
                addCell(row, extractValue(arrivalItem, "annual_maintenance_times"));
                addCell(row, extractValue(arrivalItem, "manufact_num"));
                addCell(row, extractValue(arrivalItem, "hospital_num"));
            }
        }
        table.removeRow(1);

    }
}
