package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.mock.FacesContextTestExecutionListener;
import com.ge.apm.mock.FakeData;
import com.ge.apm.service.utils.ExcelDocument;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"/config/springmvc-parent.xml"})
@TestExecutionListeners(listeners = {FacesContextTestExecutionListener.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class AssetFileImportServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AssetFileImportServiceTest.class);
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");


    @Autowired
    AssetFileImportService service;

    @Autowired
    AssetInfoRepository assetInfoDao;

    @Autowired
    OrgInfoRepository orgInfoDao;

    @Autowired
    TenantInfoRepository tenantInfoDao;

    @Autowired
    PlatformTransactionManager transactionManager;

    TenantInfo tenantInfo;

    OrgInfo defaultImportSite;

    @Before
    public void setUp() throws Exception {
        List<TenantInfo> tenantInfoList = (List<TenantInfo>) tenantInfoDao.findAll();
        assertTrue(tenantInfoList.size() == 1);

        tenantInfo = tenantInfoList.get(0);

        defaultImportSite = orgInfoDao.findByTenantUIDAndName(tenantInfo.getUid(), FakeData.DEFAULT_SITE_NAME);

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

//    @Ignore
    @Test
    public void importData_performance_test() throws Exception {

        ExcelDocument excelDoc = ExcelDocumentGenerator.getTestDoc(5000);

        logger.info("start to import excel");
        long startTime = System.currentTimeMillis();

        service.setSelectedOrgToImport(defaultImportSite);

        Map<String, Map<String, Object>> orgMap = service.getOrgInfoMapFromTemplate(excelDoc);
        Map<String, Map<String, Object>> assetMap = service.getAssetMapFromTemplate(excelDoc);

        service.importData(orgMap, assetMap, false, false);

        long endTime = System.currentTimeMillis();
        logger.info("finish to import excel");

        logger.info("total time: {}", (endTime - startTime) / 1000);
    }

    @Ignore
    @Test
    public void importData_add_new_data() throws Exception {

        ExcelDocument excelDoc = ExcelDocumentGenerator.getTestDoc(0);
        Map<String, Object> rowData = buildDefaultExcelMap();

        excelDoc.writeRowData(AssetFileImportService.SHEET_ASSET,
                                AssetFileImportService.EXCEL_ASSET_HEADER_ROW,
                                AssetFileImportService.EXCEL_ASSET_DATA_ROW,
                                rowData
        );

        logger.info("start to import excel");
        long startTime = System.currentTimeMillis();

        Map<String, Map<String, Object>> orgMap = service.getOrgInfoMapFromTemplate(excelDoc);
        Map<String, Map<String, Object>> assetMap = service.getAssetMapFromTemplate(excelDoc);

        service.importData(orgMap, assetMap, false, false);

        long endTime = System.currentTimeMillis();
        logger.info("finish to import excel");

        logger.info("total time: {}", (endTime - startTime) / 1000);

        List<AssetInfo> assetInfoList = (List<AssetInfo>) assetInfoDao.findAll();
        AssetInfo assetInfo = assetInfoList.get(0);
        assertEquals("设备编号A", assetInfo.getDepartNum());
        assertEquals("设备名称A", assetInfo.getName());
        assertEquals("设备别名A", assetInfo.getAliasName());

        assertEquals("自动化测试人员", assetInfo.getAssetOwnerName());
        assertNotNull(assetInfo.getAssetOwnerId());
        assertEquals("tel-1", assetInfo.getAssetOwnerTel());
        assertEquals("自动化测试人员2", assetInfo.getAssetOwnerName2());
        assertNotNull(assetInfo.getAssetOwnerId2());
        assertEquals("tel-2", assetInfo.getAssetOwnerTel2());
        assertEquals("自动化测试人员3", assetInfo.getAssetOwnerName3());
        assertNotNull(assetInfo.getAssetOwnerId3());
        assertEquals("tel-3", assetInfo.getAssetOwnerTel3());

        assertEquals("内科", assetInfo.getClinicalDeptName());
        assertNotNull(assetInfo.getClinicalDeptId());
        assertNotNull(assetInfo.getClinicalDeptUID());

        assertEquals("制造商A", assetInfo.getManufacture());
        assertEquals("品牌/制造商简称A", assetInfo.getMakerShortName());
        assertEquals("设备标准名称A", assetInfo.getDeviceName());
        assertEquals("注册证号A", assetInfo.getRegistrationNo());
        assertEquals("供应商A", assetInfo.getVendor());

        assertEquals(6801, assetInfo.getFunctionGroup().intValue());
        assertEquals(1, assetInfo.getAssetCode68().intValue());

        assertEquals(1, assetInfo.getAssetGroup().intValue());

        assertEquals(1, assetInfo.getFunctionGrade().intValue());
        assertEquals("设备型号A", assetInfo.getFunctionType());
        assertEquals("制造商序列号A", assetInfo.getSerialNum());
        assertEquals("SystemIDA", assetInfo.getSystemId());
        assertEquals("2010国家编码A", assetInfo.getNationalStandardCode());
        assertEquals("资产编号A", assetInfo.getFinancingNum());
        assertEquals("条码A", assetInfo.getBarcode());
        assertEquals("IT系统编号A", assetInfo.getModalityId());
        assertEquals(1.5, assetInfo.getPurchasePrice(), 0.01);
        assertEquals(100.5, assetInfo.getSalvageValue(), 0.01);
        assertEquals(1, assetInfo.getDepreciationMethod().intValue());
        assertEquals(9.5, assetInfo.getLifecycleInYear().doubleValue(), 0.01);

        assertEquals("2017-12-01", df.format(assetInfo.getManufactDate()));
        assertEquals("2017-12-02", df.format(assetInfo.getPurchaseDate()));
        assertEquals("2017-12-03", df.format(assetInfo.getArriveDate()));
        assertEquals("2017-12-04", df.format(assetInfo.getInstallDate()));
        assertEquals("2017-12-05", df.format(assetInfo.getFactoryWarrantyDate()));
        assertEquals("2017-12-06", df.format(assetInfo.getWarrantyDate()));
        assertEquals("2017-12-07", df.format(assetInfo.getTerminateDate()));
        assertEquals("2017-12-08", df.format(assetInfo.getLastPmDate()));
        assertEquals("2017-12-09", df.format(assetInfo.getLastMeteringDate()));
        assertEquals("2017-12-10", df.format(assetInfo.getLastQaDate()));

        assertEquals("维护商A", assetInfo.getMaitanance());
        assertEquals("维护商电话A", assetInfo.getMaitananceTel());
//        assertEquals("二维码A", assetInfo.getQrCode());
        assertEquals("安装位置编码A", assetInfo.getLocationCode());
        assertEquals("安装位置A", assetInfo.getLocationName());
        assertEquals("自动化测试人员", assetInfo.getClinicalOwnerName());
        assertNotNull(assetInfo.getClinicalOwnerId());
        assertNotNull(assetInfo.getClinicalOwnerTel());
        assertEquals("备注A", assetInfo.getComments());

        assertEquals(1, assetInfo.getAssetUsage().intValue());
        assertEquals(1, assetInfo.getCapitalSource().intValue());
        assertEquals(1, assetInfo.getRiskGrade().intValue());
        assertEquals(1, assetInfo.getMeasureGrade().intValue());
        assertEquals(1, assetInfo.getMeasuringItemCategory().intValue());
        assertEquals(1, assetInfo.getEmergencyEquipment().intValue());
        assertEquals(1, assetInfo.getImportEquipment().intValue());
        assertEquals(1, assetInfo.getUsageStatus().intValue());



    }

    @Ignore
    @Test
    public void importData_update_data() throws Exception {
        //insert
        ExcelDocument excelDoc = ExcelDocumentGenerator.getTestDoc(0);
        Map<String, Object> rowData = buildDefaultExcelMap();

        excelDoc.writeRowData(AssetFileImportService.SHEET_ASSET,
                AssetFileImportService.EXCEL_ASSET_HEADER_ROW,
                AssetFileImportService.EXCEL_ASSET_DATA_ROW,
                rowData
        );

        Map<String, Map<String, Object>> orgMap = service.getOrgInfoMapFromTemplate(excelDoc);
        Map<String, Map<String, Object>> assetMap = service.getAssetMapFromTemplate(excelDoc);

        service.importData(orgMap, assetMap, false, false);

        List<AssetInfo> assetInfoList = (List<AssetInfo>) assetInfoDao.findAll();
        AssetInfo assetInfo = assetInfoList.get(0);
        assertEquals("制造商A", assetInfo.getManufacture());

        //update
        excelDoc = ExcelDocumentGenerator.getTestDoc(0);
        rowData.put("制造商", "制造商B");

        excelDoc.writeRowData(AssetFileImportService.SHEET_ASSET,
                AssetFileImportService.EXCEL_ASSET_HEADER_ROW,
                AssetFileImportService.EXCEL_ASSET_DATA_ROW,
                rowData
        );

        orgMap = service.getOrgInfoMapFromTemplate(excelDoc);
        assetMap = service.getAssetMapFromTemplate(excelDoc);

        service.importData(orgMap, assetMap, false, false);
        assetInfoList = (List<AssetInfo>) assetInfoDao.findAll();
        assetInfo = assetInfoList.get(0);

        assertEquals("制造商B", assetInfo.getManufacture());


    }

    @Ignore
    @Test
    public void importData_clean_data() throws Exception {
        //insert
        ExcelDocument excelDoc = ExcelDocumentGenerator.getTestDoc(0);
        Map<String, Object> rowData = buildDefaultExcelMap();

        excelDoc.writeRowData(AssetFileImportService.SHEET_ASSET,
                AssetFileImportService.EXCEL_ASSET_HEADER_ROW,
                AssetFileImportService.EXCEL_ASSET_DATA_ROW,
                rowData
        );

        Map<String, Map<String, Object>> orgMap = service.getOrgInfoMapFromTemplate(excelDoc);
        Map<String, Map<String, Object>> assetMap = service.getAssetMapFromTemplate(excelDoc);

        service.importData(orgMap, assetMap, false, false);

        List<AssetInfo> assetInfoList = (List<AssetInfo>) assetInfoDao.findAll();
        AssetInfo assetInfo = assetInfoList.get(0);
        assertEquals("制造商A", assetInfo.getManufacture());

        //update
        excelDoc = ExcelDocumentGenerator.getTestDoc(0);
        rowData.put("制造商", "#");

        excelDoc.writeRowData(AssetFileImportService.SHEET_ASSET,
                AssetFileImportService.EXCEL_ASSET_HEADER_ROW,
                AssetFileImportService.EXCEL_ASSET_DATA_ROW,
                rowData
        );

        orgMap = service.getOrgInfoMapFromTemplate(excelDoc);
        assetMap = service.getAssetMapFromTemplate(excelDoc);

        service.importData(orgMap, assetMap, false, false);
        assetInfoList = (List<AssetInfo>) assetInfoDao.findAll();
        assetInfo = assetInfoList.get(0);

        assertNull(assetInfo.getManufacture());


    }

    @Ignore
    @Test
    public void importData_keep_old_data() throws Exception {
        //insert
        ExcelDocument excelDoc = ExcelDocumentGenerator.getTestDoc(0);
        Map<String, Object> rowData = buildDefaultExcelMap();

        excelDoc.writeRowData(AssetFileImportService.SHEET_ASSET,
                AssetFileImportService.EXCEL_ASSET_HEADER_ROW,
                AssetFileImportService.EXCEL_ASSET_DATA_ROW,
                rowData
        );

        Map<String, Map<String, Object>> orgMap = service.getOrgInfoMapFromTemplate(excelDoc);
        Map<String, Map<String, Object>> assetMap = service.getAssetMapFromTemplate(excelDoc);

        service.importData(orgMap, assetMap, false, false);

        List<AssetInfo> assetInfoList = (List<AssetInfo>) assetInfoDao.findAll();
        AssetInfo assetInfo = assetInfoList.get(0);
        assertEquals("制造商A", assetInfo.getManufacture());

        //update
        excelDoc = ExcelDocumentGenerator.getTestDoc(0);
        rowData.put("制造商", null);

        excelDoc.writeRowData(AssetFileImportService.SHEET_ASSET,
                AssetFileImportService.EXCEL_ASSET_HEADER_ROW,
                AssetFileImportService.EXCEL_ASSET_DATA_ROW,
                rowData
        );

        orgMap = service.getOrgInfoMapFromTemplate(excelDoc);
        assetMap = service.getAssetMapFromTemplate(excelDoc);

        service.importData(orgMap, assetMap, false, false);
        assetInfoList = (List<AssetInfo>) assetInfoDao.findAll();
        assetInfo = assetInfoList.get(0);

        assertEquals("制造商A", assetInfo.getManufacture());


    }

    @Ignore
    @Test
    public void checkData() throws Exception {
    }

    @Ignore
    @Test
    public void checkDocVersion() throws Exception {
    }

    private void clearData() {
        assetInfoDao.deleteAll();
//        orgInfoDao.deleteAll();

    }

    private Map buildDefaultExcelMap() {
        Map<String, Object> rowData = new HashMap<>();
        rowData.put("设备编号", "设备编号A");
        rowData.put("设备名称", "设备名称A");
        rowData.put("设备别名", "设备别名A");
        rowData.put("维修负责人", "自动化测试人员");
        rowData.put("维修负责人2", "自动化测试人员2");
        rowData.put("维修负责人3", "自动化测试人员3");
        rowData.put("使用科室", "内科");
        rowData.put("制造商", "制造商A");
        rowData.put("品牌/制造商简称", "品牌/制造商简称A");
        rowData.put("设备标准名称", "设备标准名称A");
        rowData.put("注册证号", "注册证号A");
        rowData.put("供应商", "供应商A");
        rowData.put("医疗器械分类", "医疗器械分类A");
        rowData.put("医疗器械子分类", "医疗器械子分类A");
        rowData.put("设备分类", "设备分类A");
        rowData.put("功能等级", 1);
        rowData.put("设备型号", "设备型号A");
        rowData.put("制造商序列号", "制造商序列号A");
        rowData.put("SystemID/厂商报修ID", "SystemIDA");
        rowData.put("2010国家编码", "2010国家编码A");
        rowData.put("资产编号", "资产编号A");
        rowData.put("条码", "条码A");
        rowData.put("IT系统编号", "IT系统编号A");
        rowData.put("采购价格", 1.5);
        rowData.put("最终残值", 100.5);
        rowData.put("折旧算法", "折旧算法A");
        rowData.put("使用年限", 9.5);
        rowData.put("出厂日期", "2017-12-01");
        rowData.put("采购日期", "2017-12-02");
        rowData.put("到货日期", "2017-12-03");
        rowData.put("启用日期", "2017-12-04");
        rowData.put("质保日期", "2017-12-05");
        rowData.put("维保截止日期", "2017-12-06");
        rowData.put("报废日期", "2017-12-07");
        rowData.put("上次维护日期", "2017-12-08");
        rowData.put("上次计量日期", "2017-12-09");
        rowData.put("上次质检日期", "2017-12-10");
        rowData.put("维护商", "维护商A");
        rowData.put("维护商电话", "维护商电话A");
//        rowData.put("二维码", "二维码A");
        rowData.put("安装位置编码", "安装位置编码A");
        rowData.put("安装位置", "安装位置A");
        rowData.put("使用责任人", "自动化测试人员");
        rowData.put("备注", "备注A");
        rowData.put("用途分类", "用途分类A");
        rowData.put("资金来源", "资金来源A");
        rowData.put("风险等级", "风险等级A");
        rowData.put("计量等级", "计量等级A");
        rowData.put("计量项目类别", "计量项目类别A");
        rowData.put("是否急救设备", "是否急救设备A");
        rowData.put("是否进口设备", "是否进口设备A");
        rowData.put("使用状态", "使用状态A");

        return rowData;
    }

}