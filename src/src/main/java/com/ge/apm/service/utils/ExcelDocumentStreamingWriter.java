/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Quan Tang
 */
public class ExcelDocumentStreamingWriter implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(ExcelDocumentStreamingWriter.class);

    SXSSFWorkbook workBook;

    SXSSFSheet sheet;

    String sheetName;

    List<String> columnTitles;

    final CellStyle DEFAULT_CELL_STYLE_DATE;
    final CellStyle DEFAULT_CELL_STYLE_DECIMAL;
    final String transferColumnStyleToDate = "出厂日期,采购日期,到货日期,启用日期,质保截止日期,维保截止日期";

    public ExcelDocumentStreamingWriter(SXSSFWorkbook workBook, String sheetName, List<String> columnTitles) {
        SXSSFSheet sheet = workBook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workBook.createSheet(sheetName);
        }

        this.workBook = workBook;
        this.sheetName = sheetName;
        this.sheet = sheet;
        this.columnTitles = columnTitles;

        CreationHelper createHelper = workBook.getCreationHelper();

        DEFAULT_CELL_STYLE_DATE = workBook.createCellStyle();
        DEFAULT_CELL_STYLE_DATE.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy"));

        DEFAULT_CELL_STYLE_DECIMAL = workBook.createCellStyle();
        DEFAULT_CELL_STYLE_DECIMAL.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));

        setDefaultColumnStyles();
    }

    private void setDefaultColumnStyles() {

        if (null == columnTitles || columnTitles.isEmpty()) {
            return;
        }

        for (int index = 0; index < columnTitles.size(); index++) {
            String columnTitle = columnTitles.get(index);
            if (columnTitle == null || columnTitle.isEmpty()) {
                continue;
            }

            if (columnTitle.toLowerCase().endsWith("date") || columnTitle.endsWith("日期")) {
                sheet.setDefaultColumnStyle(index, DEFAULT_CELL_STYLE_DATE);
            }
        }
    }

    public void writeRowData(int row, Map<String, Object> data) {

        for (int index = 0; index < columnTitles.size(); index++) {
            String columnTitle = columnTitles.get(index);
            if (columnTitle == null || columnTitle.isEmpty()) {
                continue;
            }
            Object value = data.get(columnTitle);
            if (value == null) continue;

            writeCellData(row, index + 1, value);
        }
    }

    public void writeRowData(int row, List<Object> data) {

        for (int index = 0; index < data.size(); index++) {
            Object value = data.get(index);

            writeCellData(row, index + 1, value);
        }
    }

    public void writeRowData(int row, String[] data) {

        for (int index = 0; index < data.length; index++) {
            Object value = data[index];

            writeCellData(row, index + 1, value);
        }
    }

    public void writeCellData(int rowNum, int colNum, Object value) {
        Row dataRow = sheet.getRow(rowNum - 1);
        if (dataRow == null) {
            dataRow = sheet.createRow(rowNum - 1);
        }

        Cell cell = dataRow.getCell(colNum - 1);
        if (null == cell) {
            cell = dataRow.createCell(colNum - 1);
        }

        writeCellData(cell, value);

    }

    public void writeCellData(Cell cell, Object data) {

        if (data == null) {
            return;
        }

        if(transferColumnStyleToDate.contains(data.toString())){
            int columnIndex = cell.getColumnIndex();
            sheet.setDefaultColumnStyle(columnIndex, DEFAULT_CELL_STYLE_DATE);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((String) data);
        }else if (data instanceof String) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue((String) data);

        } else if (data instanceof Date) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellStyle(DEFAULT_CELL_STYLE_DATE);
            cell.setCellValue((Date) data);

        } else if (data instanceof Integer) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Integer) data);

        } else if (data instanceof Long) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Long) data);

        } else if (data instanceof Double) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellStyle(DEFAULT_CELL_STYLE_DECIMAL);
            cell.setCellValue((Double) data);
        }

    }

    public void writeCellFormula(int rowNum, int colNum, String formula) {
        Row dataRow = sheet.getRow(rowNum - 1);
        if (dataRow == null) {
            dataRow = sheet.createRow(rowNum - 1);
        }

        Cell cell = dataRow.getCell(colNum - 1);
        if (null == cell) {
            cell = dataRow.createCell(colNum - 1);
        }

        cell.setCellFormula(formula);

    }

    public InputStream getStream() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.workBook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    public void writeToFile(File file) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.workBook.write(out);

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(out.toByteArray());
        fos.close();

    }

    @Override
    public void close()  {
        if (workBook != null) {
            try {
                workBook.close();
            } catch (IOException e) {
                //nothing
            }
            workBook = null;
        }
    }

    public Workbook getWorkBook() {
        return workBook;
    }

}
