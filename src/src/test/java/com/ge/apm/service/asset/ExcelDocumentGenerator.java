package com.ge.apm.service.asset;

import com.ge.apm.service.utils.ExcelDocument;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

            Workbook workbook = new XSSFWorkbook();
            Sheet assetSheet = workbook.createSheet(AssetFileImportService.SHEET_ASSET);
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

    public static ExcelDocument getTestDoc(int rowCount) {

        ExcelDocument excelDoc = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Workbook workbook = new XSSFWorkbook();
            Sheet assetSheet = workbook.createSheet(AssetFileImportService.SHEET_ASSET);
            buildHeaderRow(assetSheet);
            buildDataRow(assetSheet, rowCount);

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

        for (int i = 0; i < headerTitles.length; i ++) {
            applyCellData(dataRow, headerTitles[i], defaultRowData[i]);
        }

    }

    private static void buildDataRow(Sheet sheet, int rowCount) {


        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Row dataRow = sheet.createRow(AssetFileImportService.EXCEL_ASSET_DATA_ROW - 1 + rowIndex);

            applyCellData(dataRow, "设备编号", String.format("SBBH%07d", rowIndex + 1));
            applyCellData(dataRow, "设备名称", "测试名称");
            applyCellData(dataRow, "设备分类", "其他类");
            applyCellData(dataRow, "资产编号", null);
            applyCellData(dataRow, "二维码", null);

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
