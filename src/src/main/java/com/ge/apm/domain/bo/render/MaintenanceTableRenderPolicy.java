package com.ge.apm.domain.bo.render;

import com.ge.healthcare.cn.common.utils.render.AbstractWoTableRenderPolicy;
import com.ge.healthcare.cn.common.utils.render.DynamicColumnTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ge.apm.domain.bo.MaintenanceTemplate.MaintenanceInfo.*;

/**
 * Render Policy for Maintenance Table.
 */
public class MaintenanceTableRenderPolicy extends AbstractWoTableRenderPolicy {

    @Override
    public void render(XWPFTable table, Object data) {
        List<Map<String, Object>> maintenanceInfo = (List) data;

        String[] dynamicParameter = DynamicColumnTableRenderPolicy.getDynamicParameter();
        if(dynamicParameter != null && dynamicParameter.length == 3){
            String parameter = dynamicParameter[1];
            String[] stepNames = parameter.split("[|,，、\\-]");
            List<String> stepNameList = Arrays.asList(stepNames);
            maintenanceInfo = maintenanceInfo.stream().filter(item->stepNameList.contains(item.get(STEP.toString()))).collect(Collectors.toList());
        }

        for (Map<String, Object> mi : maintenanceInfo) {
            XWPFTableRow row = addRow(table);
            addCell(row, extractValue(mi, STEP.toString()));
            addCell(row, extractValue(mi, OWNER.toString()));
            addCell(row, extractValue(mi, START_TIME.toString()));
            addCell(row, extractValue(mi, END_TIME.toString()));
        }
        table.removeRow(1);
    }
}
