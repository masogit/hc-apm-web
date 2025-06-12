package com.ge.apm.service.asset;


import com.ge.healthcare.cn.common.utils.render.AbstractWoTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;
import java.util.Map;

public class ArrivalItemTableRenderPolicy extends AbstractWoTableRenderPolicy {

    public void render(XWPFTable table, Object o) {

        if (o instanceof List) {
            List<Map<String, Object>> arrivalItemList = (List) o;
            for (Map<String, Object> arrivalItem : arrivalItemList) {
                XWPFTableRow row = addRow(table);
                addCell(row, extractValue(arrivalItem, "asset_name"));
                addCell(row, extractValue(arrivalItem, "asset_group"));
                addCell(row, extractValue(arrivalItem, "function_type"));
                addCell(row, extractValue(arrivalItem, "depart_num"));
                addCell(row, extractValue(arrivalItem, "serial_num"));
                addCell(row, extractValue(arrivalItem, "work_hours"));
                addCell(row, extractValue(arrivalItem, "location_name"));
            }
        }
        table.removeRow(1);

    }
}
