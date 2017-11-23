package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.mock.FacesContextTestExecutionListener;
import com.ge.apm.service.utils.ExcelDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"/config/applicationContext.xml"})
@TestExecutionListeners(listeners = {FacesContextTestExecutionListener.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class AssetFileImportServiceTest {

    private static boolean initialized = false;

    @Autowired
    AssetFileImportService service;

    @Autowired
    AssetInfoRepository assetInfoDao;

    @Autowired
    OrgInfoRepository orgInfoDao;

    @Autowired
    PlatformTransactionManager transactionManager;

    TransactionTemplate transactionTemplate;

    ExcelDocument excelDocument;

    @Before
    public void setUp() throws Exception {

        this.transactionTemplate = new TransactionTemplate(transactionManager);

        assetInfoDao.deleteAll();
        orgInfoDao.deleteAll();

        if (!initialized) {
            init();
            initialized = true;
        }

    }

    public void init() {
        List<OrgInfo> orgInfoList = (List<OrgInfo>) orgInfoDao.findAll();
    }

    @After
    public void tearDown() throws Exception {
        assetInfoDao.deleteAll();
        orgInfoDao.deleteAll();
    }

    @Ignore
    @Test
    public void getOrgInfoMapFromTemplate() throws Exception {
        ExcelDocument excelDoc = ExcelDocumentGenerator.getTestDoc();
        Map<String, Map<String, Object>> map = service.getOrgInfoMapFromTemplate(excelDoc);


    }

    @Ignore
    @Test
    public void getAssetMapFromTemplate() throws Exception {
    }

    @Test
    public void importData() throws Exception {

//        service.importData();
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