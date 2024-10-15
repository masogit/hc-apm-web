package com.ge.apm.service.asset;

import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;
import java.util.Map;

public class DepartmentConvertRenderPolicy extends AbstractWoTableRenderPolicy {

    public void render(XWPFTable table, Object o) {

        if (o instanceof List) {
            List<Map<String, Object>> list = (List) o;
            for (Map<String, Object> assetDepartment : list) {
                XWPFTableRow row = addRow(table);
                addCell(row, extractValue(assetDepartment, "approvalNumber"));
                addCell(row, extractValue(assetDepartment, "businessNumber"));
                addCell(row, extractValue(assetDepartment, "businessType"));
                addCell(row, extractValue(assetDepartment, "approvalLevel"));
                addCell(row, extractValue(assetDepartment, "actualApproverName"));
                addCell(row, extractValue(assetDepartment, "approvalNodeStatus"));
                addCell(row, extractValue(assetDepartment, "actualApproverDate"));
            }
        }
        table.removeRow(1);

    }
}
