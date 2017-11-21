package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.mock.FacesContextTestExecutionListener;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"/config/applicationContext.xml"})
@TestExecutionListeners(listeners = {FacesContextTestExecutionListener.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class AssetFileImportUpdateServiceTest {

    @Autowired
    AssetFileImportUpdateService service;

    @Autowired
    AssetInfoRepository assetInfoDao;

    @Autowired
    OrgInfoRepository orgInfoDao;

    @Autowired
    PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    private static final String ASSET_ID = "9999";
    private static final String ASSET_NAME = "CT";
    private static final int HOSPITAL_ID = 1;
    private static final int SITE_ID = 0;


    @Before
    public void setUp() throws Exception {

        this.transactionTemplate = new TransactionTemplate(transactionManager);

//        setupOrgInfo();
//        setupAssetInfo();

        List<OrgInfo> orgInfoList = (List<OrgInfo>) orgInfoDao.findAll();

        if (orgInfoList.isEmpty()) {
            transactionTemplate.execute(new TransactionCallback() {

                // the code in this method executes in a transactional context
                public Object doInTransaction(TransactionStatus status) {
                    OrgInfo orgInfo = orgInfoDao.findById(HOSPITAL_ID);
                    if (orgInfo == null) {
                        orgInfo = new OrgInfo();
//                    orgInfo.setId(HOSPITAL_ID);
                        orgInfo.setSiteId(SITE_ID);
                        orgInfo.setName("TEST ORG");
                        orgInfoDao.save(orgInfo);
                    }

                    return orgInfo;
                }
            });
        }

        orgInfoList = (List<OrgInfo>) orgInfoDao.findAll();


        AssetInfo assetInfo = assetInfoDao.findByUid(ASSET_ID);
        if (null == assetInfo) {
            assetInfo = new AssetInfo();
            assetInfo.setUid(ASSET_ID);
            assetInfo.setName(ASSET_NAME);
            assetInfo.setHospitalId(orgInfoList.get(0).getId());
            assetInfoDao.save(assetInfo);
        }


    }

    @Transactional()
    public void setupOrgInfo() {
        OrgInfo orgInfo = orgInfoDao.findById(HOSPITAL_ID);
        if (orgInfo == null) {
            orgInfo = new OrgInfo();
            orgInfo.setId(HOSPITAL_ID);
            orgInfo.setSiteId(SITE_ID);
            orgInfo.setName("TEST ORG");
            orgInfoDao.save(orgInfo);
        }
    }

    @Transactional
    public void setupAssetInfo() {
        AssetInfo assetInfo = assetInfoDao.findByUid(ASSET_ID);
        if (null == assetInfo) {
            assetInfo = new AssetInfo();
            assetInfo.setUid(ASSET_ID);
            assetInfo.setName(ASSET_NAME);
            assetInfo.setHospitalId(HOSPITAL_ID);
            assetInfoDao.save(assetInfo);
        }
    }

    @After
    public void tearDown() throws Exception {
//        AssetInfo assetInfo = assetInfoDao.findByUid(ASSET_ID);
        assetInfoDao.deleteAll();
        orgInfoDao.deleteAll();
    }

    @Ignore
    @Test
    public void updateAll() throws Exception {
    }

    @Ignore
    @Test
    public void clearBeforeUpdateAll() throws Exception {
    }

    @Test(expected = Exception.class)
    public void checkExcelData_ID_Column_Not_Exists() throws Exception {

        Map<String, Object> record = new HashMap<>();
        service.checkExcelRowData(record, 0);

    }

    @Test(expected = EntityNotFoundException.class)
    public void checkExcelData_ID_Value_Not_Exists() throws Exception {

        Map<String, Object> record = new HashMap<>();
        record.put("ID", "111");
        AssetInfo assetInfo = service.checkExcelRowData(record, 0);

    }


}