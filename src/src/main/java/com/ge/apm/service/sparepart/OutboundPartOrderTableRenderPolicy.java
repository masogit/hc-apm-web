package com.ge.apm.service.sparepart;

import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;
import java.util.Map;

public class OutboundPartOrderTableRenderPolicy extends AbstractWoTableRenderPolicy {
    public void render(XWPFTable table, Object o) {

        if (o instanceof List) {
            List<Map<String, Object>> parts = (List) o;
            for (Map<String, Object> part : parts) {
                XWPFTableRow row = addRow(table);
                addCell(row, extractValue(part, "index"));
                addCell(row, extractValue(part, "name"));
                addCell(row, extractValue(part, "model"));
                addCell(row, extractValue(part, "manufacturer"));
                addCell(row, extractValue(part, "unitPrice"));
                addCell(row, extractValue(part, "quantity"));
                addCell(row, extractValue(part, "unit"));
                addCell(row, extractValue(part, "totalPrice"));
            }
        }
        table.removeRow(1);
    }
}
