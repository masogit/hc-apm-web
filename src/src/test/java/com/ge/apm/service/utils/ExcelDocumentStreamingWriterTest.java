package com.ge.apm.service.utils;

import com.ge.apm.service.asset.AssetFileImportService;
import com.ge.apm.service.asset.ExcelDocumentGenerator;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExcelDocumentStreamingWriterTest {

    @Ignore
    @Test
    public void getColumnTitles() throws IOException {

//        InputStream inputStream = ExcelDocumentGenerator.getTestDocByInputStream(10);

        InputStream inputStream = new FileInputStream(new File("./src/main/webapp/portal/asset/assetImport/APM_Asset_Template.xlsx"));

        List<ExcelDocumentStreamingWriter.SheetMetaData> metaData = new ArrayList<>();

        ExcelDocumentStreamingWriter.SheetMetaData assetMetaData = new ExcelDocumentStreamingWriter.SheetMetaData(0, 6);
//        ExcelDocumentStreamingWriter.SheetMetaData orgMetaData = new ExcelDocumentStreamingWriter.SheetMetaData(1, 4);
        metaData.add(assetMetaData);
//        metaData.add(orgMetaData);

        ExcelDocumentStreamingWriter doc = new ExcelDocumentStreamingWriter(inputStream, metaData);
//        String[] columnTitles = doc.getColumnTitles(0);

//        assertEquals(2, doc.getWorkBook().getNumberOfSheets());

//        assertArrayEquals(ExcelDocumentGenerator.headerTitles, columnTitles);

        for (int i = 1; i < 5000; i++) {
            List<Object> rowData = new ArrayList<>();
            rowData.add("col1");
//            doc.removeRow(0, 7 + i);
            doc.writeRowData(0, 7 + i, rowData);
        }

        doc.writeToFile(new File("./temp.xlsx"));
    }

    @Test
    public void testFormula() throws IOException {

//        InputStream inputStream = ExcelDocumentGenerator.getTestDocByInputStream(10);

        InputStream inputStream = new FileInputStream(new File("./src/main/webapp/portal/asset/assetImport/APM_Asset_Template.xlsx"));

        List<ExcelDocumentStreamingWriter.SheetMetaData> metaData = new ArrayList<>();

        ExcelDocumentStreamingWriter.SheetMetaData assetMetaData = new ExcelDocumentStreamingWriter.SheetMetaData(0, 6);
//        ExcelDocumentStreamingWriter.SheetMetaData orgMetaData = new ExcelDocumentStreamingWriter.SheetMetaData(1, 4);
        metaData.add(assetMetaData);
//        metaData.add(orgMetaData);

        ExcelDocumentStreamingWriter doc = new ExcelDocumentStreamingWriter(inputStream, metaData);
//        String[] columnTitles = doc.getColumnTitles(0);

//        assertEquals(2, doc.getWorkBook().getNumberOfSheets());

//        assertArrayEquals(ExcelDocumentGenerator.headerTitles, columnTitles);

        doc.formatAsTable();
        for (int i = 1; i < 5000; i++) {
            List<Object> rowData = new ArrayList<>();
            rowData.add("col1");
//            doc.removeRow(0, 7 + i);
            doc.writeRowData(0, 7 + i, rowData);
        }

        doc.writeToFile(new File("./temp.xlsx"));
    }
}