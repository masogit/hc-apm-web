/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 *
 * @author Quan Tang
 */
public class ExcelDocumentStreamingReader implements Closeable, Iterable<Map<String, Cell>> {
    private static final Logger logger = LoggerFactory.getLogger(ExcelDocumentStreamingReader.class);

    Workbook workBook;
    Sheet sheet;
    int keyRowNum;
    String[] columnTitles;
    Iterator<Row> iterator;

    public ExcelDocumentStreamingReader(String path, int sheetIndex, int keyRowNum) throws IOException {
        this(new FileInputStream(path), sheetIndex, keyRowNum);
    }

    public ExcelDocumentStreamingReader(InputStream input, int sheetIndex, int keyRowNum) {
        this.workBook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(input);
        this.sheet = workBook.getSheetAt(sheetIndex);
        this.keyRowNum = keyRowNum;
        this.iterator = sheet.rowIterator();

        this.columnTitles = getColumnTitles();


    }

    private String[] getColumnTitles() {

        int currentRow = 0;

        List<String> resultList = new ArrayList<>();
//        Iterator<Row> iterator = sheet.rowIterator();
        while (iterator.hasNext()) {
            Row row = iterator.next();
//            logger.info("row： {}", row.getRowNum());

            if (row.getRowNum() == keyRowNum - 1) {
                for (Cell cell : row) {
                    resultList.add(cell.getStringCellValue());
                }

                break;
            }

            ++currentRow;
        }

        String[] columnTitles = resultList.toArray(new String[]{});

        return columnTitles;
    }

    @Override
    public Iterator<Map<String, Cell>> iterator() {

//        int currentRow = 0;

//        Iterator<Row> iterator = sheet.rowIterator();
//        while (iterator.hasNext()) {
//            iterator.next();
//            if (currentRow == keyRowNum - 1) {
//
//                break;
//            }
//
//            ++currentRow;
//        }
        return new DataIterator(iterator);
    }

    public class DataIterator implements Iterator<Map<String, Cell>> {

        Iterator<Row> iterator;

        public DataIterator(Iterator<Row> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Map<String, Cell> next() {

            Map<String, Cell> map = new HashMap<>();

            Row row = iterator.next();

            int cellIndex = 0;
            for (Cell cell : row) {
                if (cellIndex >= columnTitles.length) {
                    break;
                }

                String columnTitle = columnTitles[cellIndex];
                map.put(columnTitle, cell);

                ++cellIndex;
            }
            return map;
        }
    }

    public String getStringValue(Cell cell) {
        CellType type = cell.getCellTypeEnum();
        switch (type) {
            case NUMERIC:
//                return String.valueOf(cell.getNumericCellValue());
                return ((XSSFCell) cell).getRawValue();
            case STRING:
                return cell.getStringCellValue();

            default:
                return null;
        }
    }

    public Integer getIntegerValue(Cell cell) {
        CellType type = cell.getCellTypeEnum();
        switch (type) {
            case NUMERIC:
                double numericCellValue = cell.getNumericCellValue();
                return Double.valueOf(numericCellValue).intValue();

            case STRING:
                return Integer.valueOf(cell.getStringCellValue());

            default:
                return null;
        }
    }

    public Double getDoubleValue(Cell cell) {
        CellType type = cell.getCellTypeEnum();
        switch (type) {
            case NUMERIC:
                double numericCellValue = cell.getNumericCellValue();
                return Double.valueOf(numericCellValue);

            case STRING:
                return Double.valueOf(cell.getStringCellValue());

            default:
                return null;
        }
    }

    public Date getDateValue(Cell cell) {
        CellType type = cell.getCellTypeEnum();
        switch (type) {
            case NUMERIC:
                return cell.getDateCellValue();
            case STRING:
                return formatDate(cell.getStringCellValue());

            default:
                return null;
        }
    }

    private Object getObjectValue(Cell cell) {
        CellType type = cell.getCellTypeEnum();
        switch (type) {
            case NUMERIC:
                return HSSFDateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cell.getNumericCellValue();
            case STRING:
                return cell.getStringCellValue();
            default:
                return null;
        }
    }

    private Date formatDate(String strDate) {
        if (strDate == null) return null;

        Date convertedDate = null;
        if (strDate.length() == 10) {
            String dateFormat = TimeUtils.getDateFormat(strDate);
            if (dateFormat != null) {
                convertedDate = TimeUtils.getDateFromStr(strDate, dateFormat);
                if (convertedDate != null) {
                    return convertedDate;
                }
            }
            convertedDate = TimeUtils.getDateFromStr(strDate, "MM/dd/yyyy");
            if (convertedDate != null) {
                return convertedDate;
            }
            convertedDate = TimeUtils.getDateFromStr(strDate, "dd/MM/yyyy");
            if (convertedDate != null) {
                return convertedDate;
            }
            convertedDate = TimeUtils.getDateFromStr(strDate, "yyyy/MM/dd");
            if (convertedDate != null) {
                return convertedDate;
            }
            convertedDate = TimeUtils.getDateFromStr(strDate, "yyyy-MM-dd");
            if (convertedDate != null) {
                return convertedDate;
            }
        } else if (strDate.length() == 8) {
            convertedDate = TimeUtils.getDateFromStr(strDate, "MM/dd/yy");
            if (convertedDate != null) {
                return convertedDate;
            }
            convertedDate = TimeUtils.getDateFromStr(strDate, "dd/MM/yy");
            if (convertedDate != null) {
                return convertedDate;
            }
        }

        return null;
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

    public Workbook getWorkBook() {
        return workBook;
    }
}
