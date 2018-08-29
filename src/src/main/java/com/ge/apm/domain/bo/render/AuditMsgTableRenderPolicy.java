package com.ge.apm.domain.bo.render;

import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;
import java.util.Map;

import static com.ge.apm.domain.bo.MaintenanceTemplate.AuditMsgInfo.*;

/**
 * Render Policy for Audit Message List.
 */
public class AuditMsgTableRenderPolicy extends AbstractWoTableRenderPolicy {
    @Override
    public void render(XWPFTable table, Object data) {
        List<Map<String, Object>> auditMsgInfo  = (List) data;

        for (Map<String, Object> item : auditMsgInfo) {
            XWPFTableRow row = addRow(table);
            addCell(row, extractValue(item, TIME.toString()));
            addCell(row, extractValue(item, OWNER.toString()));
            addCell(row, extractValue(item, INFO.toString()));
        }
        table.removeRow(1);
    }
}
