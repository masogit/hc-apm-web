package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.mock.FacesContextTestExecutionListener;
import com.ge.apm.mock.FakeData;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"/config/springmvc-parent.xml"})
@TestExecutionListeners(listeners = {FacesContextTestExecutionListener.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class AssetFileImportServiceTest {

    Logger logger = LoggerFactory.getLogger(AssetFileImportServiceTest.class);

//    private static boolean initialized = false;

    @Autowired
    AssetFileImportService service;

    @Autowired
    AssetInfoRepository assetInfoDao;

    @Autowired
    OrgInfoRepository orgInfoDao;

    @Autowired
    PlatformTransactionManager transactionManager;

    TransactionTemplate transactionTemplate;

//    ExcelDocument excelDocument;

    private void clearData() {
        assetInfoDao.deleteAll();
//        orgInfoDao.deleteAll();

    }

    @Before
    public void setUp() throws Exception {

        this.transactionTemplate = new TransactionTemplate(transactionManager);

        clearData();
    }

    @After
    public void tearDown() throws Exception {
//        clearData();
    }

    @Ignore
    @Test
    public void getOrgInfoMapFromTemplate() throws Exception {
        ExcelDocument excelDoc = ExcelDocumentGenerator.getDefaultTestDoc();
        Map<String, Map<String, Object>> map = service.getOrgInfoMapFromTemplate(excelDoc);

    }

    @Ignore
    @Test
    public void getAssetMapFromTemplate() throws Exception {
    }

    @Ignore
    @Test
    public void importData_performance_test() throws Exception {

        ExcelDocument excelDoc = ExcelDocumentGenerator.getTestDoc(5000);

        logger.info("start to import excel");
        long startTime = System.currentTimeMillis();

        Map<String, Map<String, Object>> orgMap = service.getOrgInfoMapFromTemplate(excelDoc);
        Map<String, Map<String, Object>> assetMap = service.getAssetMapFromTemplate(excelDoc);

        service.importData(orgMap, assetMap, false, false, false);

        long endTime = System.currentTimeMillis();
        logger.info("finish to import excel");

        logger.info("total time: {}", (endTime - startTime) / 1000);
    }

    @Test
    public void importData_add_new_data() throws Exception {

        final String device_num = "SBBH0001";
        final String device_name = "XXXXXX";
        final String clinicalDeptName = "内科";
        final String device_category = "其他类";
        ExcelDocument excelDoc = ExcelDocumentGenerator.getTestDoc(0);
        Map<String, Object> rowData = new HashMap<>();
        rowData.put("设备编号", device_num);
        rowData.put("设备名称", device_name);
        rowData.put("使用科室", clinicalDeptName);
        rowData.put("设备分类", device_category);
        rowData.put("二维码", null);

        excelDoc.writeRowData(AssetFileImportService.SHEET_ASSET,
                                AssetFileImportService.EXCEL_ASSET_HEADER_ROW,
                                AssetFileImportService.EXCEL_ASSET_DATA_ROW,
                                rowData
        );

        logger.info("start to import excel");
        long startTime = System.currentTimeMillis();

        Map<String, Map<String, Object>> orgMap = service.getOrgInfoMapFromTemplate(excelDoc);
        Map<String, Map<String, Object>> assetMap = service.getAssetMapFromTemplate(excelDoc);

        service.importData(orgMap, assetMap, false, false, false);

        long endTime = System.currentTimeMillis();
        logger.info("finish to import excel");

        logger.info("total time: {}", (endTime - startTime) / 1000);

        List<AssetInfo> assetInfoList = (List<AssetInfo>) assetInfoDao.findAll();
        AssetInfo assetInfo = assetInfoList.get(0);
        assertEquals(device_num, assetInfo.getDepartNum());
        assertEquals(device_name, assetInfo.getName());

    }

    @Ignore
    @Test
    public void checkData() throws Exception {
    }

    @Ignore
    @Test
    public void checkDocVersion() throws Exception {
    }

}