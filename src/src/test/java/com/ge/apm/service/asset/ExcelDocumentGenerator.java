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

    public static ExcelDocument getTestDoc() {

        ExcelDocument excelDoc = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet assetSheet = workbook.createSheet(AssetFileImportService.SHEET_ASSET);
            XSSFRow headerRow = assetSheet.createRow(AssetFileImportService.EXCEL_ASSET_HEADER_ROW - 1);
            buildHeaderRow(headerRow);
            workbook.write(baos);
            byte[] bytes = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

            excelDoc = new ExcelDocument(bais, ExcelDocument.ExcelType.XLSX);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return excelDoc;

    }

    private static void buildHeaderRow(XSSFRow headerRow) {

        int columnIndex = 0;
        for (String title: headerTitles) {
            headerRow.createCell(columnIndex++).setCellValue(title);
        }

    }

    private static void buildOneLineDataRow(Sheet sheet) {

//        Row headerRow = sheet.getRow(AssetFileImportService.EXCEL_ASSET_HEADER_ROW);

        String[] headers = ExcelDocument.getKeyRow(sheet, AssetFileImportService.EXCEL_ASSET_HEADER_ROW);

        Row dataRow = sheet.createRow(AssetFileImportService.EXCEL_ASSET_DATA_ROW);

        int columnIndex = 0;
        dataRow.createCell(columnIndex++).setCellValue("SBBH0001");
        dataRow.createCell(columnIndex++).setCellValue("Test Asset");
        dataRow.createCell(columnIndex++).setCellValue("其他类");
        dataRow.createCell(columnIndex++).setCellValue("资产编号");
        dataRow.createCell(columnIndex++).setCellValue("二维码");

    }
}
