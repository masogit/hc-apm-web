/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 *
 * @author 212579464
 */
public class ExcelDocument implements AutoCloseable{

    @Override
    public void close() {
        if (workBook != null) {
            try {
                workBook.close();
            } catch (IOException e) {
                //nothing
            }
            workBook = null;
        }
    }

    public static enum ExcelType {
        XLS, XLSX
    }

    ExcelType documentType;

    Workbook workBook;

    final CellStyle DEFAULT_CELL_STYLE_DATE;
    final CellStyle DEFAULT_CELL_STYLE_DECIMAL;

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

        CreationHelper createHelper = workBook.getCreationHelper();

        DEFAULT_CELL_STYLE_DATE = workBook.createCellStyle();
        DEFAULT_CELL_STYLE_DATE.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy"));

        DEFAULT_CELL_STYLE_DECIMAL = workBook.createCellStyle();
        DEFAULT_CELL_STYLE_DECIMAL.setDataFormat(createHelper.createDataFormat().getFormat("0.00"));

    }

    public int getLastRowNum(String sheetName, int startRowNum) {
        Sheet sheet = workBook.getSheet(sheetName);
        if (sheet == null) {
            return 0;
        }

        int rowNum = startRowNum;
        Row row;
        while ((row = sheet.getRow(rowNum)) != null) {
            Cell cell = row.getCell(0);
            if (cell == null || StringUtils.isEmpty(cell.getStringCellValue())) {
                break;
            }

            ++rowNum;
        }

        return rowNum - 1;
    }

    public boolean exceedMaxRowNum(String sheetName, int maxRowNum) {
        Sheet sheet = workBook.getSheet(sheetName);

        Row row = sheet.getRow(maxRowNum);

        if (row == null) {
            return false;
        }

        Cell cell = row.getCell(0);
        if (cell == null || StringUtils.isEmpty(cell.getStringCellValue())) {
            return false;
        }

        return true;

    }

    public String[] getKeyRow(String sheetName, int keyRowNum) {
        Sheet sheet = workBook.getSheet(sheetName);
        return getKeyRow(sheet, keyRowNum);
    }
    
    public String[] getKeyRow(String sheetName, int keyRowNum,int maxColumn) {
        Sheet sheet = workBook.getSheet(sheetName);
        return getKeyRow(sheet, keyRowNum,maxColumn);
    }

    public String[] getKeyRow(int sheetIndex, int keyRowNum) {
        Sheet sheet = workBook.getSheetAt(sheetIndex);
        return getKeyRow(sheet, keyRowNum);
    }
    
    public void mergeCell(String sheetName,int firstRow, int lastRow, int firstCol, int lastCol){
    	Sheet sheet = workBook.getSheet(sheetName);
    	if(firstRow==lastRow && firstCol==lastCol){
    		return;
    	}
    	sheet.addMergedRegion(new CellRangeAddress(firstRow-1,lastRow-1,firstCol-1,lastCol-1));
    }

    public static String[] getKeyRow(Sheet sheet, int keyRowNum) {
        Row keyRow = sheet.getRow(keyRowNum - 1);

        List<String> resultList = new ArrayList<>();

        int firstCellNum = keyRow.getFirstCellNum();
        int lastCellNum = keyRow.getLastCellNum();
        for (int columnIndex = firstCellNum; columnIndex < lastCellNum; columnIndex++) {
            Cell cell = keyRow.getCell(columnIndex);
            if (cell == null) break;
            resultList.add(cell.getStringCellValue());
        }
        return resultList.toArray(new String[]{});
    }
    
    public static String[] getKeyRow(Sheet sheet, int keyRowNum,int lastCellNum) {
        Row keyRow = sheet.getRow(keyRowNum - 1);

        List<String> resultList = new ArrayList<>();

        int firstCellNum = keyRow.getFirstCellNum();
        for (int columnIndex = firstCellNum; columnIndex < lastCellNum; columnIndex++) {
            Cell cell = keyRow.getCell(columnIndex);
            if(cell!=null){
            	resultList.add(cell.getStringCellValue());
            }
            else{
            	resultList.add("");
            }
        }
        return resultList.toArray(new String[]{});
    }

    public Map<String, String> getStringDataRowMap(String sheetName, int keyRowNum, Integer dataRowNum) {
        String[] rowKey = getKeyRow(sheetName, keyRowNum);
        Sheet sheet = workBook.getSheet(sheetName);
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

    public void writeRowData(String sheetName, String[] rowKey, Integer dataRowNum, Map<String, Object> data) {

        for (int index = 0; index < rowKey.length; index++) {
            String key = rowKey[index];
            if (key == null || key.isEmpty()) {
                continue;
            }
            Object value = data.get(key);

            writeCellData(sheetName, dataRowNum, index + 1, value);
        }
    }
    public void writeRowData(String sheetName, String[] rowKey, Integer dataRowNum, String newKey,Object value) {

        for (int index = 0; index < rowKey.length; index++) {
            String key = rowKey[index];
            if (key == null || key.isEmpty()) {
                continue;
            }
            if(key.equals(newKey)){
            	writeCellData(sheetName, dataRowNum, index + 1, value);
            	break;
            }
        }
    }

    public void writeRowData(int sheetIndex, String[] rowKey, Integer dataRowNum, Map<String, Object> data) {

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

    public void writeRowData(String sheetName, Integer keyRowNum, Integer dataRowNum, Map<String, Object> data) {
        String[] rowKey = getKeyRow(sheetName, keyRowNum);
        writeRowData(sheetName, rowKey, dataRowNum, data);
    }
    
    public void writeRowData(String sheetName, Integer keyRowNum, Integer dataRowNum, String key,Object value) {
        String[] rowKey = getKeyRow(sheetName, keyRowNum);
        writeRowData(sheetName, rowKey, dataRowNum, key,value);
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

    public void writeCellData(String sheetName, int rowNum, int colNum, Object value) {
        Sheet sheet = workBook.getSheet(sheetName);
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
    
    public int addPicture(InputStream input,int formatType) throws IOException{
    	byte[]b=IOUtils.toByteArray(input);
    	int pictureIndex=workBook.addPicture(b, formatType);
    	input.close();
    	return pictureIndex;
    }
    
    public void drawPicture(int pictureIndex,String sheetName,int row,int col,double scaleX,double scaleY){
    	Sheet sheet=workBook.getSheet(sheetName);
    	Drawing drawing = sheet.createDrawingPatriarch();
    	CreationHelper helper = workBook.getCreationHelper();
    	ClientAnchor anchor = helper.createClientAnchor();
    	anchor.setCol1(col-1);
        anchor.setRow1(row-1);
        Picture pict = drawing.createPicture(anchor, pictureIndex);
        pict.resize(scaleX,scaleY);
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

    public void writeCellFormula(String sheetName, int rowNum, int colNum, String formula) {
        Sheet sheet = workBook.getSheet(sheetName);
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

    public void removeRow(String sheetName, int rowNum) {
        Sheet sheet = workBook.getSheet(sheetName);
        Row dataRow = sheet.getRow(rowNum - 1);
        if (dataRow != null) {
            sheet.removeRow(dataRow);
        }

    }

    public void clearRow(int rowNum) {
        Sheet sheet = workBook.getSheetAt(0);
        clearRow(sheet, rowNum);
    }

    public void clearRow(String sheetName, int rowNum) {
        Sheet sheet = workBook.getSheet(sheetName);
        clearRow(sheet, rowNum);
    }

    public void clearRow(int sheetIndex, int rowNum) {
        Sheet sheet = workBook.getSheetAt(sheetIndex);
        clearRow(sheet, rowNum);
    }

    private void clearRow(Sheet sheet, int rowNum) {
        Row dataRow = sheet.getRow(rowNum - 1);
        if (dataRow != null) {
            int firstCellNum = dataRow.getFirstCellNum();
            int lastCellNum = dataRow.getLastCellNum();
            for (int col = firstCellNum; col < lastCellNum; col++) {
                Cell cell = dataRow.getCell(col);
                if (cell.getCellTypeEnum() != CellType.BLANK) {
                    cell.setCellType(CellType.BLANK);
                }
            }
        }
    }

    public void calculateFormula() {

        if (documentType.equals(ExcelType.XLS)) {
            HSSFFormulaEvaluator.evaluateAllFormulaCells(workBook);
        } else if (documentType.equals(ExcelType.XLSX)) {
            XSSFFormulaEvaluator.evaluateAllFormulaCells(workBook);
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
        close();
    }

    public Workbook getWorkBook() {
        return workBook;
    }
}
