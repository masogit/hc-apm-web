package com.ge.apm.domain.bo.render;

import com.ge.healthcare.cn.common.utils.render.AbstractWoTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;
import java.util.Map;

import static com.ge.apm.domain.bo.MaintenanceTemplate.CoworkerHours.*;

/**
 * Render Policy for Maintenance Table.
 */
public class CoworkerHoursRenderPolicy extends AbstractWoTableRenderPolicy {

    @Override
    public void render(XWPFTable table, Object data) {
        List<Map<String, Object>> maintenanceInfo = (List) data;

        for (Map<String, Object> mi : maintenanceInfo) {
            XWPFTableRow row = addRow(table);
            addCell(row, extractValue(mi, COWORKER_USER_NAME.toString()));
            addCell(row, extractValue(mi, START_TIME.toString()));
            addCell(row, extractValue(mi, END_TIME.toString()));
            addCell(row, extractValue(mi, MAN_HOURS.toString()));
            addCell(row, extractValue(mi, REMARK.toString()));
        }
        table.removeRow(1);
    }
}
