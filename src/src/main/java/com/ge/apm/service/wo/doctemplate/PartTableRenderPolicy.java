package com.ge.apm.service.wo.doctemplate;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PartTableRenderPolicy extends AbstractWoTableRenderPolicy {
    public void render(XWPFTable table, Object o) {
        if (o instanceof List) {
            List<String> tableHeader = handlerTableHeader(table, o);
            List<Map<String, Object>> parts = (List) o;
            for (Map<String, Object> part : parts) {
                XWPFTableRow row = addRow(table);
                handleRow(row,part,tableHeader);
//                addCell(row, extractValue(part, "备件名"));
//                addCell(row, extractValue(part, "数量"));
//                addCell(row, extractValue(part, "合同价格"));
//                addCell(row, extractValue(part, "类型"));
//                addCell(row, extractValue(part, "序列号"));
//                addCell(row, extractValue(part, "质保期"));
//                addCell(row, extractValue(part, "到货"));
            }
        }
        table.removeRow(1);
    }
    private void handleRow(XWPFTableRow row,Map<String, Object> part,List<String> tableHeader) {
        for (String s : tableHeader) {
            addCell(row, extractValue(part, s));
        }
    }
    private List<String> handlerTableHeader(XWPFTable table, Object o){
        XWPFTableRow row = table.getRow(0);
        List<XWPFTableCell> tableCells = row.getTableCells();
        return tableCells.stream().map(XWPFTableCell::getText).collect(Collectors.toList());
    }

    @Override
    protected String extractValue(Map part, String key) {
        try {
            Map<String, Object> partValue = part;
            Object obj = partValue.computeIfAbsent(key,item->{
                Optional<Object> first = partValue.keySet().stream().filter(k -> item.contains(k)|| k.contains(item) ).map(k->part.get(k)).filter(Objects::nonNull).findFirst();
                if(first.isPresent()){
                   return first.get();
                }else{
                    return null;
                }
            });
            if (obj == null) {
                return "";
            }
            return String.valueOf(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
