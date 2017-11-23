package com.ge.apm.service.asset;

import com.ge.apm.service.utils.ExcelDocument;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExcelDocumentGenerator {

    public static String[] headerTitles = {
            "设备编号",
            "设备名称",
            "设备分类",
            "资产编号",
            "二维码"
    };

    public static String[] defaultRowData = {
            "SBBH0001",
            "TEST Asset",
            "其他类",
            "ZCBH0001",
            "1111111111111111"
    };

    public static ExcelDocument getTestDoc() {

        ExcelDocument excelDoc = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet assetSheet = workbook.createSheet(AssetFileImportService.SHEET_ASSET);
            buildHeaderRow(assetSheet);
            buildOneLineDataRow(assetSheet);

            workbook.write(baos);
            byte[] bytes = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

            excelDoc = new ExcelDocument(bais, ExcelDocument.ExcelType.XLSX);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return excelDoc;

    }

    private static void buildHeaderRow(Sheet sheet) {

        Row headerRow = sheet.createRow(AssetFileImportService.EXCEL_ASSET_HEADER_ROW - 1);

        int columnIndex = 0;
        for (String title: headerTitles) {
            headerRow.createCell(columnIndex++).setCellValue(title);
        }

    }

    private static void buildOneLineDataRow(Sheet sheet) {

        Row dataRow = sheet.createRow(AssetFileImportService.EXCEL_ASSET_DATA_ROW - 1);

        int columnIndex = 0;
        for (String title: defaultRowData) {
            dataRow.createCell(columnIndex++).setCellValue(title);
        }

    }

    private static void applyCellData(Row dataRow, String columnTitle, String columnValue) {


        for (int i = 0; i < headerTitles.length; i ++) {

            if (columnTitle.equals(headerTitles[i])) {
                dataRow.createCell(i).setCellValue(columnValue);

                return;
            }
        }
    }
}
