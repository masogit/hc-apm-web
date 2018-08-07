package com.ge.apm.service.utils;

import com.ge.apm.service.asset.ExcelDocumentGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class ExcelDocumentStreamingReaderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getKeyRow() throws IOException {

        InputStream inputStream = ExcelDocumentGenerator.getTestDocByInputStream(10);

        ExcelDocumentStreamingReader doc = new ExcelDocumentStreamingReader(inputStream);
        String[] keyTitles = doc.getKeyRow(0, 6);

        assertEquals(2, doc.getWorkBook().getNumberOfSheets());

        assertArrayEquals(ExcelDocumentGenerator.headerTitles, keyTitles);


    }
}