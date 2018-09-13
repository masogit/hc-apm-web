package com.ge.apm.service.utils;

import com.ge.apm.service.asset.AssetFileImportService;
import com.ge.apm.service.asset.ExcelDocumentGenerator;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ExcelDocumentStreamingReaderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getColumnTitleRow() throws IOException {

        InputStream inputStream = ExcelDocumentGenerator.getTestDocByInputStream(10);

        ExcelDocumentStreamingReader doc = new ExcelDocumentStreamingReader(inputStream, 0, AssetFileImportService.EXCEL_ASSET_HEADER_ROW, 2, 3);
        String[] columnTitles = doc.columnTitles;

        assertEquals(2, doc.getWorkBook().getNumberOfSheets());

        assertArrayEquals(ExcelDocumentGenerator.headerTitles, columnTitles);

    }

    @Test
    public void getFirstDataRow_First_Sheet() throws IOException {

        InputStream inputStream = ExcelDocumentGenerator.getTestDocByInputStream(10);

        ExcelDocumentStreamingReader doc = new ExcelDocumentStreamingReader(inputStream, 0, AssetFileImportService.EXCEL_ASSET_HEADER_ROW, 2, 3);

        Iterator<Map<String, ExcelDocument.ValueWrapper>> iter = doc.iterator();
        Map<String, ExcelDocument.ValueWrapper> firstRowData = iter.next();
        ExcelDocument.ValueWrapper wrapper = firstRowData.get("设备编号");
        System.out.println(wrapper.value);

    }

    @Test
    public void getFirstDataRow_Second_sheet() throws IOException {

        InputStream inputStream = ExcelDocumentGenerator.getTestDocByInputStream(10);

        ExcelDocumentStreamingReader doc = new ExcelDocumentStreamingReader(inputStream, 1, AssetFileImportService.EXCEL_ORG_HEADER_ROW, 2, 3);

        Iterator<Map<String, ExcelDocument.ValueWrapper>> iter = doc.iterator();
        Map<String, ExcelDocument.ValueWrapper> firstRowData = iter.next();
        ExcelDocument.ValueWrapper wrapper = firstRowData.get(ExcelDocumentGenerator.orgHeaderTitle);
        System.out.println(wrapper.value);

    }
}