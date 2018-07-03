package com.ge.apm.domain.bo.render;

import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;
import java.util.Map;

import static com.ge.apm.domain.bo.MaintenanceTemplate.CheckListInfo.*;

/**
 * Render Policy for Check List.
 */
public class CheckListTableRenderPolicy extends AbstractWoTableRenderPolicy {

    @Override
    public void render(XWPFTable table, Object data) {
        List<Map<String, Object>> checkListInfo = (List) data;

        for (Map<String, Object> cl : checkListInfo) {
            XWPFTableRow row = addRow(table);
            addCell(row, extractValue(cl, NAME.toString()));
            addCell(row, extractValue(cl, PERIOD.toString()));
            addCell(row, extractValue(cl, CONTENT.toString()));
            addCell(row, extractValue(cl, RESULT.toString()));
        }
        table.removeRow(1);
    }
}
