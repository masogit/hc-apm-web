/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 *
 * @author Quan Tang
 */
public class ExcelDocumentStreamingReader implements Closeable {

    Workbook workBook;

    public ExcelDocumentStreamingReader(String path) throws IOException {
        this(new FileInputStream(path));
    }

    public ExcelDocumentStreamingReader(InputStream input) {
        workBook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(input);
    }

    public String[] getKeyRow(int sheetIndex, int keyRowNum) {
        Sheet sheet = workBook.getSheetAt(sheetIndex);

        int currentRow = 0;

        List<String> resultList = new ArrayList<>();
        for (Row row : sheet) {
            if (currentRow == keyRowNum -1) {
                for (Cell cell : row) {
                    resultList.add(cell.getStringCellValue());
                }

                break;
            }


            ++currentRow;
        }

        return resultList.toArray(new String[]{});
    }

    public Map<String, String> getStringDataRowMap(int sheetIndex, int keyRowNum, Integer dataRowNum) {
        String[] rowKey = getKeyRow(sheetIndex, keyRowNum);
        Sheet sheet = workBook.getSheetAt(sheetIndex);
        Row dataRow = sheet.getRow(dataRowNum - 1);
        Map<String, String> dataMap = new LinkedHashMap();
        if (dataRow == null) {
            return dataMap;
        }
        for (int cellIndex = 0; cellIndex < rowKey.length; cellIndex++) {
            Cell cell = dataRow.getCell(cellIndex);
            if (cell == null) {
                continue;
            }
            switch (cell.getCellTypeEnum()) {
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        dataMap.put(rowKey[cellIndex], TimeUtils.getStrDate(date, "yyyy-MM-dd HH:mm:ss"));
                    } else {
                        dataMap.put(rowKey[cellIndex], new DecimalFormat("###################.#####").format(cell.getNumericCellValue()));
                    }
                    break;
                case BOOLEAN:
                    dataMap.put(rowKey[cellIndex], (String.valueOf(cell.getBooleanCellValue())));
                    break;
                case STRING:
                case BLANK:
                    dataMap.put(rowKey[cellIndex], cell.getStringCellValue());
                    break;
                case FORMULA:
                default:
                    dataMap.put(rowKey[cellIndex], "");
            }
        }
        return dataMap;
    }

    public Map<String, Object> getDataRowMap(String sheetName, String[] rowKey, Integer dataRowNum) {
        Sheet sheet = workBook.getSheet(sheetName);
        Row dataRow = sheet.getRow(dataRowNum - 1);
        Map<String, Object> dataMap = new LinkedHashMap();

        if (dataRow == null) {
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
        String[] colKeys = getKeyRow(sheetIndex, keyRowNum);
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

//    public Map<String, Object> getDataRowMap(int sheetIndex, Integer keyRowNum, Integer dataRowNum) {
//        String[] rowKey = getKeyRow(sheetIndex, keyRowNum);
//        return getDataRowMap(sheetIndex, rowKey, dataRowNum);
//    }
//
//    public Map<String, Object> getDataRowMap(Integer keyRowNum, Integer dataRowNum) {
//        String sheetName = workBook.getSheetName(0);
//        return getDataRowMap(sheetName, keyRowNum, dataRowNum);
//    }

//    public List<Map<String, Object>> getDataMaps(String sheetName, Integer keyRowNum, Integer firstData, Integer lastData) {
//        List<Map<String, Object>> res = new ArrayList();
//        for (int row = firstData - 1; row <= lastData - 1; row++) {
//            Map<String, Object> dataMap = getDataRowMap(sheetName, keyRowNum, row + 1);
//            if (null != dataMap) {
//                res.add(dataMap);
//            }
//        }
//        return res;
//    }

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
                    return ((XSSFCell) cell).getRawValue();
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

    static public Date getCellDateValue(Object value) {
        ValueWrapper valueWrapper = null;
        if (value instanceof ValueWrapper) {
            valueWrapper = (ValueWrapper) value;
            value = valueWrapper.value;
        }

        if (value == null) {
            return null;
        }

        if (value instanceof Date) {
            return (Date) value;
        }

        if (value instanceof String) {
            String strDate = ((String) value).trim();
            if (StringUtils.isEmpty(strDate)) {
                return null;
            }

            Date res;
            if (strDate.length() == 10) {
                String dateFormat = TimeUtils.getDateFormat(strDate);
                if (dateFormat != null) {
                    res = TimeUtils.getDateFromStr(strDate, dateFormat);
                    if (res != null) {
                        return res;
                    }
                }
                res = TimeUtils.getDateFromStr(strDate, "MM/dd/yyyy");
                if (res != null) {
                    return res;
                }
                res = TimeUtils.getDateFromStr(strDate, "dd/MM/yyyy");
                if (res != null) {
                    return res;
                }
                res = TimeUtils.getDateFromStr(strDate, "yyyy/MM/dd");
                if (res != null) {
                    return res;
                }
                res = TimeUtils.getDateFromStr(strDate, "yyyy-MM-dd");
                if (res != null) {
                    return res;
                }
            } else if (strDate.length() == 8) {
                res = TimeUtils.getDateFromStr(strDate, "MM/dd/yy");
                if (res != null) {
                    return res;
                }
                res = TimeUtils.getDateFromStr(strDate, "dd/MM/yy");
                if (res != null) {
                    return res;
                }
            }

            return null;
        }

        return null;
    }

    public Object getCellValue(String sheetName, Integer dataRowNum, Integer cellnum) {
        return workBook.getSheet(sheetName).getRow(dataRowNum - 1).getCell(cellnum - 1);
    }

    public InputStream getStream() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.workBook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
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

    public static class ValueWrapper {

        public Object value;
        public String rawValue;

        public ValueWrapper(Object value, String rawValue) {
            this.value = value;
            this.rawValue = rawValue;
        }
    }

    public Workbook getWorkBook() {
        return workBook;
    }
}
