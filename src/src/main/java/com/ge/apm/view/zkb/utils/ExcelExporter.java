package com.ge.apm.view.zkb.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExcelExporter {

    public static void exportToExcel(OutputStream out, List<Map<String, Object>> data, String sheetName) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        // ✅ 创建日期格式
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd"));

        XSSFSheet sheet = workbook.createSheet(sheetName);

        // 写表头
        if (!data.isEmpty()) {
            Row headerRow = sheet.createRow(0);
            int colIdx = 0;
            for (String key : data.get(0).keySet()) {
                headerRow.createCell(colIdx++).setCellValue(key);
            }

            // 写数据
            int rowIdx = 1;
            for (Map<String, Object> rowData : data) {
                Row row = sheet.createRow(rowIdx++);
                colIdx = 0;
                for (Object value : rowData.values()) {
                    Cell cell = row.createCell(colIdx++);

                    if (value == null) {
                        cell.setCellType(CellType.BLANK);

                    } else if (value instanceof Date) {
                        // ✅ 特殊处理：Date 类型
                        cell.setCellValue((Date) value);
                        cell.setCellStyle(dateStyle);

                    } else if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());

                    } else {
                        cell.setCellValue(value.toString());
                    }
                }
            }
        }

        // ✅ 自动调整列宽（提升用户体验）
        for (int i = 0; i < 20; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(out);
        workbook.close();
    }
}
