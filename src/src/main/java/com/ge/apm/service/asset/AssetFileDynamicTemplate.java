/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.pure.AssetInfoFormatter;
import com.ge.apm.service.utils.ExcelDocumentStreamingWriter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Quan Tang
 */

@Service
public class AssetFileDynamicTemplate extends AssetFileImportBaseService {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileDynamicTemplate.class);

//    Workbook workBook;

//    public AssetFileDynamicTemplate(Workbook workBook) {
//        this.workBook = workBook;
//    }
//
//    public AssetFileDynamicTemplate(String path) throws IOException {
//        this(new FileInputStream(path));
//    }
//
//    public AssetFileDynamicTemplate(InputStream input) throws IOException {
//        this.workBook = new SXSSFWorkbook(new XSSFWorkbook(input));
////        this.workBook = new XSSFWorkbook(input);
//    }

    public void buildMetaData(InputStream input) throws IOException {
        SXSSFWorkbook workBook = new SXSSFWorkbook(new XSSFWorkbook(input));
        buildMetaData(workBook);
    }

    public void buildMetaData(SXSSFWorkbook workBook) {


    }

    public void writeAsset(SXSSFWorkbook workBook, List<AssetInfo> assetList, List<OrgInfo> sites, List<OrgInfo> clinicalDeptList, List<String> exportFields) {

        Sheet sheet = workBook.getSheet(SHEET_ASSET);

        ExcelDocumentStreamingWriter writer = new ExcelDocumentStreamingWriter(workBook, SHEET_ASSET, exportFields);

        String[] columnTitles = exportFields.toArray(new String[] {});
        writer.writeRowData(EXCEL_ASSET_HEADER_ROW, columnTitles);

        int startRow = EXCEL_ASSET_DATA_ROW;
        for (AssetInfo assetInfo: assetList) {
            AssetInfoFormatter formatter = new AssetInfoFormatter(assetInfo,  sites ,this, assetDao, orgDao, i18nDao);
            Map<String, Object> rowMap = formatter.buildExcelColumnKeyValueMap(true);

            writer.writeRowData(startRow, rowMap);

            ++startRow;
        }


    }

}
