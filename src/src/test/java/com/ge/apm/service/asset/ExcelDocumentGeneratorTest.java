package com.ge.apm.service.asset;

import com.ge.apm.service.utils.ExcelDocument;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ExcelDocumentGeneratorTest {
    @Test
    public void getTestDoc_Test_Header_Titles() throws Exception {

        ExcelDocument excelDoc = ExcelDocumentGenerator.getTestDoc();
        String[] titles = excelDoc.getKeyRow(AssetFileImportService.SHEET_ASSET, AssetFileImportService.EXCEL_ASSET_HEADER_ROW);

        assertEquals(ExcelDocumentGenerator.headerTitles.length, titles.length);

        for (int i = 0; i < titles.length; i++) {
            assertEquals(ExcelDocumentGenerator.headerTitles[i], titles[i]);
        }

    }

    @Test
    public void getTestDoc_Test_Data() throws Exception {

        ExcelDocument excelDoc = ExcelDocumentGenerator.getTestDoc();
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

}