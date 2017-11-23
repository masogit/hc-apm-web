package com.ge.apm.service.asset;

import com.ge.apm.service.utils.ExcelDocument;

import java.io.IOException;
import java.io.InputStream;

public class ExcelDocumentGenerator {

    public static ExcelDocument getTestDoc() {

        InputStream is = null;
        ExcelDocument excelDoc = null;
        try {
            excelDoc = new ExcelDocument(is, ExcelDocument.ExcelType.XLSX);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return excelDoc;
    }
}
