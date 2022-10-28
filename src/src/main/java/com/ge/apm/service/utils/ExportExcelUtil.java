package com.ge.apm.service.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.ReflectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class ExportExcelUtil {

    public static <T> byte[] ExportExcel(List<T> list, String[] titles, String[] column, int size) throws IOException {
        SXSSFWorkbook wb = new SXSSFWorkbook(size);
        Sheet sheet = wb.createSheet();
        Row row = sheet.createRow(0);
        //给单元格设置样式
        CellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 12);
        //设置字体加粗
        font.setBold(true);
        //给字体设置样式
        cellStyle.setFont(font);
        //设置单元格背景颜色
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        //设置单元格填充样式(使用纯色背景颜色填充)
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //设置表头
        for (int i = 0; i < titles.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(cellStyle);
            //设置列的宽度
            sheet.setColumnWidth(i, 200*50);
        }
        //填充表格
        for (int j = 0; j < list.size(); j++) {
            Row rowData = sheet.createRow(j + 1);
            Object t = list.get(j);
            for (int i = 0; i < column.length; i++) {
                Field field = ReflectionUtils.findField(t.getClass(), column[i]);
                field.setAccessible(true);
                Cell cell = rowData.createCell(i);
                try {
                    cell.setCellValue("" + field.get(t));
                } catch (IllegalAccessException e) {
                    cell.setCellValue("");
                }
            }
        }
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            wb.write(outStream);
            return outStream.toByteArray();
        } catch (IOException e) {
            return null;
        } finally {
            wb.close();
        }
    }
}

