package com.ge.apm.service.asset;

import com.ge.apm.service.utils.ExcelDocument;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ExcelDocumentGeneratorTest {
    @Test
    public void getTestDoc_Test_Header_Titles() throws Exception {

        ExcelDocument excelDoc = ExcelDocumentGenerator.getDefaultTestDoc();
        String[] titles = excelDoc.getKeyRow(AssetFileImportService.SHEET_ASSET, AssetFileImportService.EXCEL_ASSET_HEADER_ROW);

        assertEquals(ExcelDocumentGenerator.headerTitles.length, titles.length);

        for (int i = 0; i < titles.length; i++) {
            assertEquals(ExcelDocumentGenerator.headerTitles[i], titles[i]);
        }

    }

    @Test
    public void getTestDoc_Test_ORG_Titles() throws Exception {

        ExcelDocument excelDoc = ExcelDocumentGenerator.getDefaultTestDoc();
        String[] titles = excelDoc.getKeyRow(AssetFileImportService.SHEET_ORG, AssetFileImportService.EXCEL_ORG_HEADER_ROW);

        assertEquals(1, titles.length);

        String title = titles[0];

        List<Map<String, Object>> results =  excelDoc.getAllDataRows(1,
                AssetFileImportService.EXCEL_ORG_HEADER_ROW,
                AssetFileImportService.EXCEL_ORG_DATA_ROW);

        assertEquals(ExcelDocumentGenerator.defaultOrgRowData.length, results.size());

        for (int i = 0; i < results.size(); i++) {
            Map<String, Object> rowData = results.get(i);
            String expectedColumnValue = ExcelDocumentGenerator.defaultOrgRowData[i];
            String actualColumnValue = ExcelDocument.getCellStringValue(rowData.get(title));

            assertEquals(expectedColumnValue, actualColumnValue);
        }

    }

    @Test
    public void getTestDoc_Test_Data_One_Row() throws Exception {

        ExcelDocument excelDoc = ExcelDocumentGenerator.getDefaultTestDoc();
        List<Map<String, Object>> results =  excelDoc.getAllDataRows(0,
                                                                    AssetFileImportService.EXCEL_ASSET_HEADER_ROW,
                                                                    AssetFileImportService.EXCEL_ASSET_DATA_ROW);
        assertEquals(1, results.size());

        Map<String, Object> result = results.get(0);

        for (int i = 0; i < ExcelDocumentGenerator.headerTitles.length; i++) {
            String title = ExcelDocumentGenerator.headerTitles[i];
            String expectedColumnValue = ExcelDocumentGenerator.defaultRowData[i];

            String actualColumnValue = ExcelDocument.getCellStringValue(result.get(title));

            assertEquals(expectedColumnValue, actualColumnValue);
        }
    }

    @Test
    public void getTestDoc_Test_Data_Multiple_Row() throws Exception {

        int rowCount = 2;

        ExcelDocument excelDoc = ExcelDocumentGenerator.getTestDoc(rowCount);
        List<Map<String, Object>> results =  excelDoc.getAllDataRows(0,
                AssetFileImportService.EXCEL_ASSET_HEADER_ROW,
                AssetFileImportService.EXCEL_ASSET_DATA_ROW);

        assertEquals(2, results.size());

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Map<String, Object> result = results.get(rowIndex);

            String format = ExcelDocumentGenerator.headerTitles[0] + ": %s, " + ExcelDocumentGenerator.headerTitles[1] + ": %s";

            String title1 = ExcelDocument.getCellStringValue(result.get(ExcelDocumentGenerator.headerTitles[0]));
            String title2 = ExcelDocument.getCellStringValue(result.get(ExcelDocumentGenerator.headerTitles[1]));

            String row = String.format(format, title1, title2);
            System.out.println(row);

        }

    }

}