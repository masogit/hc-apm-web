package com.ge.apm.service.utils;

import com.ge.apm.service.asset.AssetFileImportBaseService;
import com.ge.apm.service.asset.AssetFileImportService;
import com.ge.apm.service.asset.ExcelDocumentGenerator;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    @Test
    public void testFormula() throws IOException {

//        InputStream inputStream = ExcelDocumentGenerator.getTestDocByInputStream(10);

        InputStream inputStream = new FileInputStream(new File("./src/main/webapp/portal/asset/assetImport/APM_Asset_Template.xlsx"));
        SXSSFWorkbook workBook = new SXSSFWorkbook(new XSSFWorkbook(inputStream));

        ExcelDocumentStreamingWriter doc = new ExcelDocumentStreamingWriter(workBook, AssetFileImportBaseService.SHEET_ASSET, null);

        for (int i = 1; i < 5000; i++) {
            List<Object> rowData = new ArrayList<>();
            rowData.add("col1");
            doc.writeRowData(7 + i, rowData);
        }

        doc.writeToFile(new File("./temp.xlsx"));
    }
}