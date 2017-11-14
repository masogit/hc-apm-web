/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author 212579464
 */
public class ExcelDocument {

    public static enum ExcelType {
        XLS, XLSX
    }

    ExcelType documentType;

    Workbook workBook;

    public static ExcelType parseTypeByFileName(String path) {
        if (path.endsWith(".xls")) {
            return ExcelType.XLS;
        } else if (path.endsWith(".xlsx")) {
            return ExcelType.XLSX;
        } else {
            return null;
        }
    }

    public ExcelDocument(String path) throws IOException {
        this(new FileInputStream(path), ExcelDocument.parseTypeByFileName(path));
    }

    public ExcelDocument(InputStream input, ExcelType type) throws IOException {
        this.documentType = type;
        if (type.equals(ExcelType.XLS)) {
            workBook = new HSSFWorkbook(input);
        } else if (type.equals(ExcelType.XLSX)) {
            workBook = new XSSFWorkbook(input);
        }

        input.close();
    }

    public Integer getLastRowNum(String sheetName) {
        Sheet sheet = workBook.getSheet(sheetName);
        return sheet == null ? 0 : workBook.getSheet(sheetName).getLastRowNum();
    }

    public String[] getKeyRow(String sheetName, int keyRowNum) {
        Sheet sheet = workBook.getSheet(sheetName);
        return getKeyRow(sheet, keyRowNum);
    }

    public String[] getKeyRow(int sheetIndex, int keyRowNum) {
        Sheet sheet = workBook.getSheetAt(sheetIndex);
        return getKeyRow(sheet, keyRowNum);
    }

    public String[] getKeyRow(Sheet sheet, int keyRowNum) {
        Row keyRow = sheet.getRow(keyRowNum - 1);
        String[] result = new String[keyRow.getLastCellNum()];
        int firstCellNum = keyRow.getFirstCellNum();
        int lastCellNum = keyRow.getLastCellNum();
        for (int columnIndex = firstCellNum; columnIndex < lastCellNum; columnIndex++) {
            result[columnIndex] = keyRow.getCell(columnIndex).getStringCellValue();
        }
        return result;
    }

    public Map<String, Object> getDataRowMap(String sheetName, String[] rowKey, Integer dataRowNum) {
        Sheet sheet = workBook.getSheet(sheetName);
        Row dataRow = sheet.getRow(dataRowNum - 1);
        Map<String, Object> dataMap = new LinkedHashMap();
        
        if(dataRow==null){
            return dataMap;
        }
        
        for (int cellIndex = 0; cellIndex < rowKey.length; cellIndex++) {
            Cell cell = dataRow.getCell(cellIndex);
            if (cell == null) {
                continue;
            }
            Object v = getCellValue(cell);
            String rawValue = getRawValue(cell);
            ValueWrapper wrapper = new ValueWrapper(v, rawValue);
            dataMap.put(rowKey[cellIndex], wrapper);
        }
        return dataMap;
    }

    public List<Map<String, Object>> getAllDataRows(int sheetIndex, int keyRowNum, int startDataRowNum) {

        Sheet sheet = workBook.getSheetAt(sheetIndex);
        return getAllDataRows(sheet, keyRowNum, startDataRowNum);

    }

    public List<Map<String, Object>> getAllDataRows(Sheet sheet, int keyRowNum, int startDataRowNum) {

        String[] colKeys = getKeyRow(sheet, keyRowNum);
        int lastRowNum = sheet.getLastRowNum();
        List<Map<String, Object>> results = new ArrayList<>(lastRowNum);

        while (true) {
            Row dataRow = sheet.getRow(startDataRowNum - 1);
            if (dataRow == null) {
                break;
            }

            Map<String, Object> dataMap = new LinkedHashMap();
            for (int cellIndex = 0; cellIndex < colKeys.length; cellIndex++) {
                Cell cell = dataRow.getCell(cellIndex);
                if (cell == null) {
                    continue;
                }
                Object v = getCellValue(cell);
                String rawValue = getRawValue(cell);
                ValueWrapper wrapper = new ValueWrapper(v, rawValue);

                dataMap.put(colKeys[cellIndex], wrapper);
            }

            results.add(dataMap);

            ++startDataRowNum;
        }

        return results;

    }

    public Map<String, Object> getDataRowMap(String sheetName, Integer keyRowNum, Integer dataRowNum) {
        String[] rowKey = getKeyRow(sheetName, keyRowNum);
        return getDataRowMap(sheetName, rowKey, dataRowNum);
    }

    public Map<String, Object> getDataRowMap(Integer keyRowNum, Integer dataRowNum) {
        String sheetName = workBook.getSheetName(0);
        return getDataRowMap(sheetName, keyRowNum, dataRowNum);
    }

    public List<Map<String, Object>> getDataMaps(String sheetName, Integer keyRowNum, Integer firstData, Integer lastData) {
        List<Map<String, Object>> res = new ArrayList();
        for (int row = firstData - 1; row <= lastData - 1; row++) {
            Map<String, Object> dataMap = getDataRowMap(sheetName, keyRowNum, row + 1);
            if (null != dataMap) {
                res.add(dataMap);
            }
        }
        return res;
    }

    private Object getCellValue(Cell cell) {
        CellType type = cell.getCellTypeEnum();
        switch (type) {
            case BLANK:
                return null;
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:
                return HSSFDateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cell.getNumericCellValue();
            case STRING:
                return cell.getStringCellValue();
            case FORMULA:
                return null;
            default:
                return null;
        }
    }

    private String getRawValue(Cell cell) {
        CellType type = cell.getCellTypeEnum();
        switch (type) {
            case BLANK:
                return null;
            case BOOLEAN:
                return null;
            case NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    return null;
                }

                if (cell instanceof XSSFCell) {
                    return ((XSSFCell)cell).getRawValue();
                }
                if (cell instanceof HSSFCell) {
                    cell.setCellType(CellType.STRING);
                    return cell.getStringCellValue();
                }
                return null;
            case STRING:
                return null;
            case FORMULA:
                return null;
            default:
                return null;
        }
    }

    static public String getCellStringValue(Object value) {
        ValueWrapper valueWrapper = null;
        if (value instanceof ValueWrapper) {
            valueWrapper = (ValueWrapper) value;
            value = valueWrapper.value;
        }

        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return ((String) value).trim();
        }
        if (value instanceof Integer) {
            return valueWrapper.rawValue;
        }
        if (value instanceof Double) {
            return valueWrapper.rawValue;
        }
        return value.toString();
    }

    static public Integer getCellIntegerValue(Object value) {

        ValueWrapper valueWrapper = null;
        if (value instanceof ValueWrapper) {
            valueWrapper = (ValueWrapper) value;
            value = valueWrapper.value;
        }

        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return ((String) value).isEmpty() ? null : Integer.parseInt((String) value);
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Double) {
            return ((Double) value).intValue();
        }
        return null;
    }

    static public Double getCellDoubleValue(Object value) {

        ValueWrapper valueWrapper = null;
        if (value instanceof ValueWrapper) {
            valueWrapper = (ValueWrapper) value;
            value = valueWrapper.value;
        }

        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return ((String) value).isEmpty() ? null : Double.parseDouble((String) value);
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Integer) {
            return Double.valueOf((Integer) value);
        }
        return null;
    }

    public Object getCellValue(String sheetName, Integer dataRowNum, Integer cellnum) {
        return workBook.getSheet(sheetName).getRow(dataRowNum - 1).getCell(cellnum - 1);
    }

    public void writeRowData(String sheetName, String[] rowKey, Integer dataRowNum, Map<String, Object> data) {
        Sheet sheet = workBook.getSheet(sheetName);
        //Row dataRow = sheet.getRow(dataRowNum - 1);
        Row dataRow = sheet.createRow(dataRowNum - 1);

        for (int index = 0; index < rowKey.length; index++) {
            String key = rowKey[index];
            if (key == null || key.isEmpty()) {
                continue;
            }
            Object value = data.get(key);
            if (null == dataRow.getCell(index)) {
                dataRow.createCell(index);
            }
            writeCellData(dataRow.getCell(index), value);
        }
    }

    public void writeRowData(String sheetName, Integer keyRowNum, Integer dataRowNum, Map<String, Object> data) {
        String[] rowKey = getKeyRow(sheetName, keyRowNum);
        writeRowData(sheetName, rowKey, dataRowNum, data);
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
            cell.setCellValue((Date) data);
        }
        if (data instanceof Integer) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Integer) data);
        }
        if (data instanceof Double) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Double) data);
        }

    }

    public void clearRowsData(String sheetName, Integer startRow, Integer endRow) {
        Sheet sheet = workBook.getSheet(sheetName);
        for (int i = endRow; i >= startRow; i--) {
            Row dataRow = sheet.getRow(i - 1);
            if (dataRow != null) {
                sheet.removeRow(dataRow);
            }
        }
    }

    public InputStream getStream() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.workBook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static class ValueWrapper {
        public Object value;
        public String rawValue;

        public ValueWrapper(Object value, String rawValue) {
            this.value = value;
            this.rawValue = rawValue;
        }
    }

    public void release() {
        if (workBook != null) {
            try {
                workBook.close();
            } catch (IOException e) {
                //nothing
            }
            workBook = null;
        }
    }

}
