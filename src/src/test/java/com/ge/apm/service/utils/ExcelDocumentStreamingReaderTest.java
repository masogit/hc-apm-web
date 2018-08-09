package com.ge.apm.service.utils;

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

        ExcelDocumentStreamingReader doc = new ExcelDocumentStreamingReader(inputStream, 0, 1);
        String[] columnTitles = doc.columnTitles;

        assertEquals(2, doc.getWorkBook().getNumberOfSheets());

        assertArrayEquals(ExcelDocumentGenerator.headerTitles, columnTitles);

    }

    @Test
    public void getFirstDataRow() throws IOException {

        InputStream inputStream = ExcelDocumentGenerator.getTestDocByInputStream(10);

        ExcelDocumentStreamingReader doc = new ExcelDocumentStreamingReader(inputStream, 0, 1);

        Iterator<Map<String, Cell>> iter = doc.iterator();
        Map<String, Cell> firstRowData = iter.next();
        System.out.println(doc.getStringValue(firstRowData.get("设备编号")));

    }
}