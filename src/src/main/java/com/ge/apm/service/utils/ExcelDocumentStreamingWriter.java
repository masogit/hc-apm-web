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
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 *
 * @author Quan Tang
 */
public class ExcelDocumentStreamingWriter implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(ExcelDocumentStreamingWriter.class);

//    XSSFWorkbook xssfWorkbook;
    XSSFWorkbook workBook;

    final CellStyle DEFAULT_CELL_STYLE_DATE;
    final CellStyle DEFAULT_CELL_STYLE_DECIMAL;

    List<SheetMetaData> metaData;
    Map<Integer, SheetMetaData> sheetMetaDataMap = new HashMap<>();

    public ExcelDocumentStreamingWriter(String path, List<SheetMetaData> metaData) throws IOException {
        this(new FileInputStream(path), metaData);
    }

    public ExcelDocumentStreamingWriter(InputStream input, List<SheetMetaData> metaData) throws IOException {
//        this.xssfWorkbook = new XSSFWorkbook(input);

//        this.workBook = new SXSSFWorkbook(xssfWorkbook);
        this.workBook = new XSSFWorkbook(input);
//        formatAsTable();

        CreationHelper createHelper = workBook.getCreationHelper();

        DEFAULT_CELL_STYLE_DATE = workBook.createCellStyle();
        DEFAULT_CELL_STYLE_DATE.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy"));

        DEFAULT_CELL_STYLE_DECIMAL = workBook.createCellStyle();
        DEFAULT_CELL_STYLE_DECIMAL.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));

        this.metaData = metaData;

        this.metaData.stream().forEach(sheetMetaData -> {
            sheetMetaDataMap.put(sheetMetaData.sheetIndex, sheetMetaData);

            Sheet sheet = workBook.getSheetAt(sheetMetaData.sheetIndex);
//            sheetMetaData.iterator = sheet.rowIterator();

            sheet.getLastRowNum();

            Iterator<Row> iterator = sheet.rowIterator();
            while (iterator.hasNext()) {
                Row row = iterator.next();
//            logger.info("row： {}", row.getRowNum());

                if (row.getRowNum() == sheetMetaData.keyRowNum - 1) {
                    sheetMetaData.columnTitles = getColumnTitles(row);

                    break;
                }

            }
        });

//        getMetaData();
    }

    private void getMetaData() {

        this.metaData.stream().forEach(sheetMetaData -> {

            Iterator<Row> iterator = sheetMetaData.iterator;

            while (iterator.hasNext()) {
                Row row = iterator.next();
//            logger.info("row： {}", row.getRowNum());

                if (row.getRowNum() == sheetMetaData.keyRowNum - 1) {
                    sheetMetaData.columnTitles = getColumnTitles(row);

                    break;
                }

            }
        });

    }

    private String[] getColumnTitles(Row row) {
        List<String> resultList = new ArrayList<>();
        for (Cell cell : row) {
            resultList.add(cell.getStringCellValue());
        }

        String[] columnTitles = resultList.toArray(new String[]{});

        return columnTitles;
    }

    public String[] getColumnTitles(int sheetIndex) {

        SheetMetaData sheetMetaData = sheetMetaDataMap.get(sheetIndex);
        if (sheetMetaData ==  null) return null;

        return sheetMetaData.columnTitles;
    }


    public void writeRowData(int sheetIndex, String[] rowKey, int dataRowNum, Map<String, Object> data) {

        Sheet sheet = workBook.getSheetAt(sheetIndex);
        for (int index = 0; index < rowKey.length; index++) {
            String key = rowKey[index];
            if (key == null || key.isEmpty()) {
                continue;
            }
            Object value = data.get(key);

            writeCellData(sheet, dataRowNum, index + 1, value);
        }
    }

    public void removeRow(int sheetIndex, int dataRowNum) {
        Sheet sheet = workBook.getSheetAt(sheetIndex);
        Row row = sheet.getRow(dataRowNum - 1);
        sheet.removeRow(row);

    }
    public void writeRowData(int sheetIndex, int dataRowNum, List<Object> data) {

        Sheet sheet = workBook.getSheetAt(sheetIndex);
        for (int index = 0; index < data.size(); index++) {
            Object value = data.get(index);

            writeCellData(sheet, dataRowNum, index + 1, value);
        }
    }

    public void writeCellData(Sheet sheet, int rowNum, int colNum, Object value) {
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
        if (data instanceof String) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue((String) data);
        }
        if (data instanceof Date) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellStyle(DEFAULT_CELL_STYLE_DATE);

            cell.setCellValue((Date) data);
        }
        if (data instanceof Integer) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Integer) data);
        }
        if (data instanceof Double) {
            cell.setCellType(CellType.NUMERIC);
//            cell.setCellStyle(DEFAULT_CELL_STYLE_DECIMAL);

            cell.setCellValue((Double) data);
        }

    }

    public void formatAsTable() {
        XSSFTable my_table = workBook.getSheetAt(0).createTable();
        CTTable cttable = my_table.getCTTable();
//        AutoFilter autoFilter = new XSSFAutoFilter();
//        cttable.setAutoFilter(autoFilter);
        CTTableStyleInfo tableStyleInfo = cttable.addNewTableStyleInfo();
        tableStyleInfo.setName("TableStyleMedium9");
        tableStyleInfo.setShowColumnStripes(false);
        tableStyleInfo.setShowRowStripes(true);

        AreaReference areaReference = new AreaReference(
                new CellReference(6, 0),
                new CellReference(10, 0),
                SpreadsheetVersion.EXCEL97);

        cttable.setRef(areaReference.formatAsString());
        cttable.setDisplayName("Asset List");
        cttable.setName("Asset List");
        cttable.setId(1L);

        CTTableColumns columns = cttable.addNewTableColumns();
        columns.setCount(1);

        CTTableColumn column = columns.addNewTableColumn();
        column.setName("Column 1");
        column.setId(1);


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

    public static class SheetMetaData {
        private int sheetIndex;
        private int keyRowNum;

        SheetMetaData(int sheetIndex, int keyRowNum) {
            this.sheetIndex = sheetIndex;
            this.keyRowNum = keyRowNum;
        }

        private Sheet sheet;

        private String[] columnTitles;

        private Iterator<Row> iterator;



    }

}
