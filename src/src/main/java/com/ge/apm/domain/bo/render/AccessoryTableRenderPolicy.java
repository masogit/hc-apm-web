package com.ge.apm.domain.bo.render;

import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.ge.apm.domain.bo.MaintenanceTemplate.AccessoryInfo.*;

/**
 * Render Policy for Accessory table.
 */
public class AccessoryTableRenderPolicy extends AbstractWoTableRenderPolicy {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

    private String extractDate(Map<String, Object> part, String key) {
        Object obj = part.get(key);
        if (obj == null) {
            return "";
        }
        Date d = (Date) obj;
        return DATE_TIME_FORMATTER.print(d.getTime());
    }

    private String extractArrived(Map<String, Object> part, String key) {
        Object obj = part.get(key);
        if (obj == null) {
            return "";
        }
        Boolean b = (Boolean) obj;
        return b ? "已到货" : "未到货";
    }

    @Override
    public void render(XWPFTable table, Object data) {
        List<Map<String, Object>> content = (List) data;

        for (Map<String, Object> item : content) {
            XWPFTableRow row = addRow(table);
            addCell(row, extractValue(item, NAME.toString()));
            addCell(row, extractValue(item, QUANTITY.toString()));
            addCell(row, extractValue(item, PRICE.toString()));
            addCell(row, extractValue(item, TYPE.toString()));
            addCell(row, extractValue(item, SERIAL_NUM.toString()));
            addCell(row, extractDate(item, WARRANTY_DATE.toString()));
            addCell(row, extractArrived(item, ARRIVED.toString()));
        }
        table.removeRow(1);
    }
}
