package com.ge.apm.service.asset;

import com.ge.apm.service.utils.ExcelDocument;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExcelDocumentGeneratorTest {
    @Test
    public void getTestDoc() throws Exception {

        ExcelDocument excelDoc = ExcelDocumentGenerator.getTestDoc();
        String[] titles = excelDoc.getKeyRow(AssetFileImportService.SHEET_ASSET, AssetFileImportService.EXCEL_ASSET_HEADER_ROW);

        assertEquals(ExcelDocumentGenerator.headerTitles.length, titles.length);

        for (int i = 0; i < titles.length; i++) {
            assertEquals(ExcelDocumentGenerator.headerTitles[i], titles[i]);
        }

        fail("should pass!");
    }

}