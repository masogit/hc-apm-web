/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetTypeGroupRepository;
import com.ge.apm.dao.SysAuthGroupRepository;
import com.ge.apm.dao.SystemTemplateValueRepository;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.AssetTagsMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.AssetInfoFormatter;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.ExcelDocumentStreamingWriter;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.BaseFormulaEvaluator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;
import webapp.framework.context.LocaleHolder;
import webapp.framework.web.WebUtil;

import javax.faces.context.FacesContext;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Quan Tang
 */
public class AssetFileDynamicTemplate extends AssetFileImportBaseService {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileDynamicTemplate.class);

    private static final String SHEET_DYNAMIC_LIST_NAME = WebUtil.getMessage("DynamicList");
    private static final String SHEET_STATIC_LIST_NAME = WebUtil.getMessage("StaticList");
    private static final String SHEET_ASSET_GROUP_SECONDAY_LEVEL_NAME = WebUtil.getMessage("EquipmentClassificationClassII");
    private static final String SHEET_FUNCTION_TYPE_SECONDAY_LEVEL_NAME = "医疗器械子分类";
    public static final int ASSET_FORMULA_DEFAULT_MAX_ROWS = 20000;

//    public static String[] columnTitles = {
//            "设备编号", "设备名称", "设备别名", "维修负责人", "维修负责人2", "维修负责人3", "保养负责人", "所属院区", "所属科室", "使用科室", "制造商", "品牌/制造商简称",
//            "设备标准名称", "注册证号", "供应商", "医疗器械分类", "医疗器械子分类", "设备分类", "设备分类2级", "主设备", "功能等级", "维修类型", "设备型号", "制造商序列号",
//            "SystemID/厂商报修ID", "2010国家编码", "资产编号", "条码", "IT系统编号", "采购价格", "最终残值", "折旧算法", "使用年限",
//            "出厂日期", "采购日期", "到货日期", "启用日期", "质保截止日期", "维保截止日期", "报废日期", "上次保养日期", "上次计量日期", "上次巡检日期", "验收日期", "质保时间（月）",
//            "维修商", "维修商电话", "二维码", "安装位置编码", "安装位置", "使用责任人", "备注",
//            "用途分类", "资金来源", "风险等级", "计量等级", "计量项目类别", "核算类别", "是否急救设备", "是否进口设备", "当前状态",
//            "是否PM", "是否盘点", "资产价值分类", "资产状态", "资金来源2", "是否对维保到期提醒", "所属课题项目", "UUID"
//    };

    public static int MAX_FORMULA_ROWS = 5000;

    public static Map<String, String> columnFormulas;

    static {
        //we need to use formula for 2nd level dependant dropdown list, name range define is ok for others
        columnFormulas = new HashMap<>();
        columnFormulas.put("医疗器械子分类(旧)", "=OFFSET(INDIRECT(\"医疗器械子分类!$C$\" & MATCH(OFFSET(INDIRECT(ADDRESS(ROW(), COLUMN())),0,-1),医疗器械子分类!A:A,0)),0,0,1,INDIRECT(\"医疗器械子分类!$B$\" & MATCH(OFFSET(INDIRECT(ADDRESS(ROW(), COLUMN())),0,-1),医疗器械子分类!A:A,0)))");
        columnFormulas.put("设备分类2级", "=OFFSET(INDIRECT(\"设备分类2级!$C$\" & MATCH(OFFSET(INDIRECT(ADDRESS(ROW(), COLUMN())),0,-1),设备分类2级!A:A,0)),0,0,1,INDIRECT(\"设备分类2级!$B$\" & MATCH(OFFSET(INDIRECT(ADDRESS(ROW(), COLUMN())),0,-1),设备分类2级!A:A,0)))");

    }

    SXSSFWorkbook workBook;
    XSSFWorkbook workBookRead;

    boolean isExistenceHead=false;


    List<String> assetSheetHead;

    SXSSFSheet assetSheet;
    XSSFSheet assetSheetXs;
    SXSSFSheet orgSheet;
    SXSSFSheet tcmSheet;

    SXSSFSheet dynamicListSheet;
    SXSSFSheet staticListSheet;

    //if cross site is true clinical dept is useless.
    boolean crossSite;
    @Autowired
    public AssetTagsMapper assetTagsMapper;

    @Autowired
    AssetInfoMapper assetInfoMapper;

    @Autowired
    private AssetTypeGroupRepository assetTypeGroupRepository;

    @Autowired
    private SysAuthGroupRepository sysAuthGroupRepository;


    @Autowired
    private MicroServiceUtil microServiceUtil;

    @Autowired
    private SystemTemplateValueRepository systemTemplateValueRepository;
//    int siteId;

    public AssetFileDynamicTemplate(boolean crossSite) throws IOException {
//        this.siteId = siteId;

        this.crossSite = crossSite;

        WebApplicationContext webAppContext = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());

        webAppContext.getAutowireCapableBeanFactory().autowireBean(this);

        UserContextService userContextService = WebUtil.getBean(UserContextService.class);

        String templateType;
        String fileName = "APM_Asset_Dynamic_Template.xlsx";
        if (userContextService.isCountyArea()) {
            if (userContextService.isLocale()) {
                fileName = "APM_Asset_Dynamic_CountryArea_Template.xlsx";
                templateType = SystemTemplateEnum.ASSET_COUNTRYAREA_TEMPLATE.getCode();
            } else {
                fileName = "APM_Asset_Dynamic_CountryArea_Template_en.xlsx";
                templateType = SystemTemplateEnum.ASSET_COUNTRYAREA_TEMPLATE_EN.getCode();
            }
        } else {
            if (userContextService.isLocale()) {
                fileName = "APM_Asset_Dynamic_Template.xlsx";
                templateType = SystemTemplateEnum.ASSET_TEMPLATE.getCode();
            } else {
                fileName = "APM_Asset_Dynamic_Template_en.xlsx";
                templateType = SystemTemplateEnum.ASSET_TEMPLATE_EN.getCode();
            }
        }

        UserAccount user = UserContextService.getCurrentUserAccount();
        String storageId= systemTemplateValueRepository.findByKeyAndSiteForUseParent(user.getSiteUID(),user.getHospitalUID(),user.getInstitutionUID(),user.getTenantUID(),templateType,"excel");
        byte[] templateBytes = microServiceUtil.downloadSingleFileByte(storageId);
        InputStream inputStream = new ByteArrayInputStream(templateBytes);
//        InputStream inputStream = AssetFileDynamicTemplate.class.getClassLoader().getResourceAsStream("template/" + fileName);
        workBookRead = new XSSFWorkbook(inputStream);

        assetSheetXs = workBookRead.getSheet(SHEET_ASSET);

        if (assetSheetXs != null && assetSheetXs.getRow(0) != null) {
            isExistenceHead=true;
        }
        if(isExistenceHead){
            if(CollectionUtils.isEmpty(assetSheetHead)){
                assetSheetHead=new ArrayList<>();
            }
            String[] keyRow = ExcelDocument.getKeyRow(assetSheetXs, 1);
            if(keyRow.length>0){
                for (String s : keyRow) {
                    assetSheetHead.add(s);
                }
            }
            workBookRead.removeSheetAt(workBookRead.getSheetIndex(SHEET_ASSET));
            workBookRead.createSheet(SHEET_ASSET);
            workBookRead.setSheetOrder(SHEET_ASSET, 0);
            int newIndex = workBookRead.getSheetIndex(SHEET_ASSET);
            workBookRead.setActiveSheet(newIndex);
            workBookRead.setSelectedTab(newIndex);
        }



        workBook = new SXSSFWorkbook(workBookRead);

        assetSheet = workBook.getSheet(SHEET_ASSET);
        if (assetSheet == null) {
            assetSheet = workBook.createSheet(SHEET_ASSET);
        }
        assetSheet.setDisplayGridlines(true);

        orgSheet = workBook.getSheet(SHEET_ORG);
        if (orgSheet == null) {
            orgSheet = workBook.createSheet(SHEET_ORG);
        }
        orgSheet.setDisplayGridlines(true);

        tcmSheet = workBook.getSheet(SHEET_TCM);
        if (tcmSheet == null) {
            tcmSheet = workBook.createSheet(SHEET_TCM);
        }
        tcmSheet.setDisplayGridlines(true);


        dynamicListSheet = workBook.getSheet(SHEET_DYNAMIC_LIST_NAME);
        if (dynamicListSheet == null) {
            dynamicListSheet = workBook.createSheet(SHEET_DYNAMIC_LIST_NAME);
        }
        int index = workBook.getSheetIndex(dynamicListSheet);
        workBook.setSheetHidden(index, true);

        staticListSheet = workBook.getSheet(SHEET_STATIC_LIST_NAME);
        if (staticListSheet == null) {
            staticListSheet = workBook.createSheet(SHEET_STATIC_LIST_NAME);
        }
        index = workBook.getSheetIndex(staticListSheet);
        workBook.setSheetHidden(index, true);
    }

    public void writeMetaData(List<String> exportFields) {

        loadMetaDataAndApplyNamedRange();

        applyAssetDateValidation(exportFields);

    }

    public void loadMetaDataAndApplyNamedRange() {

        createDynamicList();

        createStaticList();

        createAssetGroupSecondaryLevel();

        createFunctionTypeSecondaryLevel();

        if (!crossSite) {
            createFormulaForExistingDropdown(WebUtil.getMessage("clinicalDeptId"), "OFFSET('Org List'!$A$2,0,0,COUNTA('Org List'!$A:$A) - 2, 1)");
            createFormulaForExistingDropdown(WebUtil.getMessage("assetDeptId"), "OFFSET('Org List'!$A$2,0,0,COUNTA('Org List'!$A:$A) - 2, 1)");
            createFormulaForExistingDropdown(WebUtil.getMessage("spare01"), "OFFSET('TCM List'!$A$2,0,0,COUNTA('TCM List'!$A:$A) - 2, 1)");
        }

    }

    private void createDynamicList() {

        Row row = dynamicListSheet.createRow(0);
        row.createCell(0).setCellValue(WebUtil.getMessage("StaticList"));
        row.createCell(1).setCellValue(WebUtil.getMessage("List"));

        int rowNum = 1;
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("assetGroup"), assetGroup, false);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("MedicalDeviceClassification"), assetFunctionTypes, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("DepreciationMethod"), depreciationMethod, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("UsageClassification"), assetUsageList, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("capitalSource"), capitalSourceList, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("MeasurementGrade"), measureGradeList, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("riskGrade"), riskGradeList, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("emergencyEquipment"), emergencyEquipmentList, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("IsImportEquipment"), importEquipmentList, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("accountingCategory"), accountingCategoryList, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("measuringItemCategory"), measuringItemCategoryList, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("repairForm"), repairFormList, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("CurrentStatus"), assetStatusList, false);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("AssetValueCategory"), assetValueCategoryList, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("CapitalSourceTwo"), capitalSource2List, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("assetClass"), assetClassList, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("deviceStatus"), deviceStatusList, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("networkingStatus"), networkingStatusList, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("ProprietaryClass"), proprietaryClassList, true);
        createDropdownWithNamedRange(rowNum++, WebUtil.getMessage("productSegment"), productSegmentList, true);
        createDropdownWithKeyNamedRange(rowNum++, WebUtil.getMessage("spare02"), spare02List, true);
        createDropdownWithKeyNamedRange(rowNum++, WebUtil.getMessage("spare03"), spare03List, true);
        createDropdownWithKeyNamedRange(rowNum++, WebUtil.getMessage("spare04"), spare04List, true);
        createDropdownWithKeyNamedRange(rowNum++, WebUtil.getMessage("spare05"), spare05List, true);

        dynamicListSheet.setSelected(false);
    }

    private void createStaticList() {
        Row row = staticListSheet.createRow(0);
        row.createCell(0).setCellValue(WebUtil.getMessage("ListName"));
        row.createCell(1).setCellValue(WebUtil.getMessage("List"));

        int rowNum = 1;
        createDropdownWithStaticList(rowNum++, WebUtil.getMessage("FunctionGrade"), true, "1", "2", "3");
        createDropdownWithStaticList(rowNum++, WebUtil.getMessage("IsPM"), true, WebUtil.getMessage("true"), WebUtil.getMessage("false"));
        createDropdownWithStaticList(rowNum++, WebUtil.getMessage("isUseRegistration"), true, WebUtil.getMessage("true"), WebUtil.getMessage("false"));
        createDropdownWithStaticList(rowNum++, WebUtil.getMessage("isFixed"), true, WebUtil.getMessage("true"), WebUtil.getMessage("false"));
        createDropdownWithStaticList(rowNum++, WebUtil.getMessage("IsChecked"), true, WebUtil.getMessage("true"), WebUtil.getMessage("false"));
        createDropdownWithStaticList(rowNum++, WebUtil.getMessage("WarrantyExpirationReminder"), true, WebUtil.getMessage("true"), WebUtil.getMessage("false"));

        staticListSheet.setSelected(false);
    }

    private void createAssetGroupSecondaryLevel() {
        SXSSFSheet sheet = workBook.getSheet(SHEET_ASSET_GROUP_SECONDAY_LEVEL_NAME);
        if (sheet == null) {
            sheet = workBook.createSheet(SHEET_ASSET_GROUP_SECONDAY_LEVEL_NAME);
            int index = workBook.getSheetIndex(sheet);
            workBook.setSheetHidden(index, true);
        }

        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue(WebUtil.getMessage("ListName"));
        row.createCell(1).setCellValue(WebUtil.getMessage("NumberOfListItems"));
        row.createCell(2).setCellValue(WebUtil.getMessage("ListItem"));

        int rowNum = 1;
        for (I18nMessage msg : assetGroup) {
            String title = msg.getValueZh();
            String msgType = "assetGroupSecondary_" + msg.getMsgKey();

            List<I18nMessage> msgList = FieldValueMessageController.getFieldValueList(msgType, siteId);

            List<String> msgs = msgList.stream().map(I18nMessage::getValueZh).collect(Collectors.toList());

            createDropdownOnlyWithListCount(sheet, rowNum, title, msgs, true);

            ++rowNum;
        }

    }

    private void createFunctionTypeSecondaryLevel() {
        SXSSFSheet sheet = workBook.getSheet(SHEET_FUNCTION_TYPE_SECONDAY_LEVEL_NAME);
        if (sheet == null) {
            sheet = workBook.createSheet(SHEET_FUNCTION_TYPE_SECONDAY_LEVEL_NAME);
            int index = workBook.getSheetIndex(sheet);
            workBook.setSheetHidden(index, true);
        }

        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue(WebUtil.getMessage("ListName"));
        row.createCell(1).setCellValue(WebUtil.getMessage("NumberOfListItems"));
        row.createCell(2).setCellValue(WebUtil.getMessage("ListItem"));

        int rowNum = 1;
        for (I18nMessage msg : assetFunctionTypes) {
            String title = msg.getValueZh();
            String msgType = msg.getMsgKey();

            List<I18nMessage> msgList = FieldValueMessageController.getFieldValueList(msgType, siteId);

            List<String> msgs = msgList.stream().map(I18nMessage::getValueZh).collect(Collectors.toList());

            createDropdownOnlyWithListCount(sheet, rowNum, title, msgs, true);

            ++rowNum;
        }

    }

    private void createDropdownOnly(SXSSFSheet sheet, int rowNum, String name, List<String> msgList, boolean optional) {

        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(name);

        int columnIndex = 1;

        for (String msg : msgList) {
            row.createCell(columnIndex++, CellType.STRING).setCellValue(msg);
        }

        if (optional) {
            // # mark is used to delete the column in database record.
            row.createCell(columnIndex++, CellType.STRING).setCellValue("#");
        }

    }

    private void createDropdownOnlyWithListCount(SXSSFSheet sheet, int rowNum, String name, List<String> msgList, boolean optional) {

        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(name);

        int itemCount = optional ? msgList.size() + 1 : msgList.size();

        row.createCell(1).setCellValue(itemCount);

        int columnIndex = 2;

        for (String msg : msgList) {
            row.createCell(columnIndex++, CellType.STRING).setCellValue(msg);
        }

        if (optional) {
            // # mark is used to delete the column in database record.
            row.createCell(columnIndex++, CellType.STRING).setCellValue("#");
        }

    }

    private void createDropdownWithNamedRange(int rowNum, String name, List<I18nMessage> msgList, boolean optional) {

        List<String> msgs = null;
        if (LocaleHolder.getLocale().toString().equalsIgnoreCase("zh")) {
            msgs = msgList.stream().map(I18nMessage::getValueZh).collect(Collectors.toList());
        } else {
            msgs = msgList.stream().map(I18nMessage::getValueEn).collect(Collectors.toList());
        }

        createDropdownOnly(dynamicListSheet, rowNum, name, msgs, optional);

        Name namedCell = workBook.getXSSFWorkbook().createName();
        namedCell.setNameName(getRangeName(name));
        String cellRange = getRangeFormulaWithHorizontalList(dynamicListSheet.getSheetName(), rowNum);
        namedCell.setRefersToFormula(cellRange);

    }

    private void createDropdownWithKeyNamedRange(int rowNum, String name, List<I18nMessage> msgList, boolean optional) {

        List<String> msgs = new ArrayList<>(msgList.size());
        if (LocaleHolder.getLocale().toString().equalsIgnoreCase("zh")) {
            for (I18nMessage i18nMessage : msgList) {
                msgs.add(i18nMessage.getMsgKey() + " " + i18nMessage.getValueZh());
            }
        } else {
            for (I18nMessage i18nMessage : msgList) {
                msgs.add(i18nMessage.getMsgKey() + " " + i18nMessage.getValueEn());
            }
        }

        createDropdownOnly(dynamicListSheet, rowNum, name, msgs, optional);

        Name namedCell = workBook.getXSSFWorkbook().createName();
        namedCell.setNameName(getRangeName(name));
        String cellRange = getRangeFormulaWithHorizontalList(dynamicListSheet.getSheetName(), rowNum);
        namedCell.setRefersToFormula(cellRange);

    }


    private void createDropdownWithStaticList(int rowNum, String name, boolean optional, String... items) {

        createDropdownOnly(staticListSheet, rowNum, name, Arrays.asList(items), optional);

        Name namedCell = workBook.getXSSFWorkbook().createName();
        namedCell.setNameName(getRangeName(name));
        String cellRange = getRangeFormulaWithHorizontalList(staticListSheet.getSheetName(), rowNum);
        namedCell.setRefersToFormula(cellRange);
    }

    private String getRangeName(String fieldName) {
        String normalizedName = fieldName.replaceAll("[\\（\\）]", "_").replaceAll("[\\(\\)]", "_").replaceAll(" ", "_").replaceAll("\\?", "_") + WebUtil.getMessage("List");
        return normalizedName;
    }

    private String getRangeFormulaWithHorizontalList(String sheetName, int rowNum) {
        //example "OFFSET('Org List'!$A$5,0,0,COUNTA('Org List'!$A:$A) - 2, 1)";
        String cellRange = String.format("OFFSET('%s'!$B$%d, 0, 0, 1, COUNTA('%s'!$%d:$%d) - 1)", sheetName, rowNum + 1, sheetName, rowNum + 1, rowNum + 1);
        return cellRange;
    }

    private void createFormulaForExistingDropdown(String columnName, String formula) {

        Name namedCell = workBook.getXSSFWorkbook().createName();
        namedCell.setNameName(getRangeName(columnName));
        namedCell.setRefersToFormula(formula);

    }

    private void applyAssetDateValidation(List<String> exportFields) {

        assetSheet.getDataValidations().clear();

        exportFields.stream().forEach(field -> {
            applyCellDropdownIfPossible(exportFields, field);
        });

    }

    public void applyCellDropdownIfPossible(List<String> exportFields, String currentField) {

        if (null == currentField) {
            return;
        }
        String normalizedName = getRangeName(currentField);
        Name name = workBook.getXSSFWorkbook().getName(normalizedName);
        String formulaList;
        if (name != null) {
            formulaList = "=" + name.getNameName();
        } else {
            //if we cannot define this formula in name, then use formula
            formulaList = columnFormulas.get(currentField);
        }

        if (formulaList == null) return;

        int columnIndex = exportFields.indexOf(currentField);

        XSSFDataValidationHelper helper = new XSSFDataValidationHelper(workBook.getXSSFWorkbook().getSheet(assetSheet.getSheetName()));

        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) helper.createFormulaListConstraint(formulaList);

        CellRangeAddressList addressList = new CellRangeAddressList(EXCEL_ASSET_DATA_ROW - 1, EXCEL_ASSET_DATA_ROW + MAX_FORMULA_ROWS, columnIndex, columnIndex);

        XSSFDataValidation dataValidation = (XSSFDataValidation) helper.createValidation(dvConstraint, addressList);

        dataValidation.setShowErrorBox(true);
        dataValidation.setSuppressDropDownArrow(true);
        dataValidation.setShowPromptBox(true);
        dataValidation.setEmptyCellAllowed(false);

        assetSheet.addValidationData(dataValidation);

    }

    public void writeAsset(List<AssetInfo> assetList, List<OrgInfo> sites, List<String> exportFields,Map encodedToAssetTypeGroupPath,Map listSysAuthGroupNamePathInHierarchyMap) {
        boolean flag = this.crossSite;
        if (!flag && exportFields.containsAll(ListUtil.newArrayList("医院", "集团"))) {
            flag = true;
        }

        ExcelDocumentStreamingWriter writer = new ExcelDocumentStreamingWriter(workBook, assetSheet.getSheetName(), exportFields);

        int maxRows = assetList.size() + EXCEL_ASSET_HEADER_ROW > ASSET_FORMULA_DEFAULT_MAX_ROWS ? assetList.size() + EXCEL_ASSET_HEADER_ROW : ASSET_FORMULA_DEFAULT_MAX_ROWS;
        //write sum formula
        writeSummaryFormula(writer, exportFields, maxRows);

        String[] columnTitles = exportFields.toArray(new String[]{});

        writer.writeRowData(EXCEL_ASSET_HEADER_ROW, columnTitles);

        int startRow = EXCEL_ASSET_DATA_ROW;

        GetAllElement getAllElement = new GetAllElement(assetList).invoke();
        Map<String, List<String>> assetMap = getAllElement.getAssetMap();
        Map parentAssets = getAllElement.getParentAssets();
        Map supliers = getAllElement.getSupliers();
       /* Map encodedToAssetTypeGroupPath = getAllElement.getEncodedToAssetTypeGroupPath();
        Map listSysAuthGroupNamePathInHierarchyMap = getAllElement.getListSysAuthGroupNamePathInHierarchyMap();
*/
        for (AssetInfo assetInfo : assetList) {
            assetInfo.setAssetUsedLifecycle(AssetInfoService.getAssetUsedLifecycle(assetInfo.getInstallDate(), assetInfo.getLifecycle()));
            AssetInfoFormatter formatter = new AssetInfoFormatter(assetInfo, sites, this, assetDao, orgDao, supplierDao, userDao, authGroupDao);
            Map<String, Object> rowMap = formatter.buildExcelColumnKeyValueMap1(MapUtil.newHashMap(),flag, assetMap, parentAssets, supliers, encodedToAssetTypeGroupPath,listSysAuthGroupNamePathInHierarchyMap);

            writer.writeRowData(startRow, rowMap);

            if (startRow % 1000 == 0) {
                logger.info("export asset rows {}", startRow);
            }


            ++startRow;
        }

        workBook.setForceFormulaRecalculation(true);
        XSSFFormulaEvaluator.evaluateAllFormulaCells(workBook);

        assetSheet.setSelected(true);

    }

    public ExcelDocumentStreamingWriter initExcelWriter(int size, List<String> exportFields, List<OrgInfo> clinicalDeptList) {
        logger.info("export asset meta data");
        writeMetaData(exportFields);
        if (!crossSite) {
            logger.info("export clinical dept list");
            writeClinicalDeptList(clinicalDeptList);
        }
        writeI18nMessage(spare01List);

        ExcelDocumentStreamingWriter writer = new ExcelDocumentStreamingWriter(workBook, assetSheet.getSheetName(), exportFields);

        int maxRows = Math.max(size + EXCEL_ASSET_HEADER_ROW, ASSET_FORMULA_DEFAULT_MAX_ROWS);
        //write sum formula
        writeSummaryFormula(writer, exportFields, maxRows);

        String[] columnTitles = exportFields.toArray(new String[]{});

        writer.writeRowData(EXCEL_ASSET_HEADER_ROW, columnTitles, true);
        return writer;
    }

    public int writeAsset1(ExcelDocumentStreamingWriter writer, List<AssetInfo> assetList, List<OrgInfo> sites, boolean siteFlag, int startRow,Map encodedToAssetTypeGroupPath,Map listSysAuthGroupNamePathInHierarchyMap) {
        boolean flag = this.crossSite;
        if (!flag && siteFlag) {
            flag = true;
        }

        GetAllElement getAllElement = new GetAllElement(assetList).invoke();
        Map<String, List<String>> assetMap = getAllElement.getAssetMap();
        Map parentAssets = getAllElement.getParentAssets();
        Map supliers = getAllElement.getSupliers();
    /*    Map encodedToAssetTypeGroupPath = getAllElement.getEncodedToAssetTypeGroupPath();
        Map listSysAuthGroupNamePathInHierarchyMap = getAllElement.getListSysAuthGroupNamePathInHierarchyMap();*/
//        Set<String> loginNames = assetList.stream().map(AssetInfo::getCreatedBy).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, String> userMap = MapUtil.newHashMap();
//        if (ObjectUtil.isNotEmpty(loginNames)) {
//            List<Map<String, String>> userAccounts = userAccountMapper.findNameByLoginNames(loginNames);
//            userMap = userAccounts.stream().collect(Collectors.toMap(map -> map.get("loginname"), map -> map.get("name")));
//        }
        for (AssetInfo assetInfo : assetList) {
            assetInfo.setAssetUsedLifecycle(AssetInfoService.getAssetUsedLifecycle(assetInfo.getInstallDate(), assetInfo.getLifecycle()));
            AssetInfoFormatter formatter = new AssetInfoFormatter(assetInfo, sites, this, assetDao, orgDao, supplierDao, userDao, authGroupDao);
            Map<String, Object> rowMap = formatter.buildExcelColumnKeyValueMap1(userMap, flag, assetMap, parentAssets, supliers, encodedToAssetTypeGroupPath,listSysAuthGroupNamePathInHierarchyMap);

            writer.writeRowData(startRow, rowMap);

            if (startRow % 5000 == 0) {
                logger.info("export asset rows {}", startRow);
            }
            ++startRow;
        }
        return startRow;
    }

    public void setFormula() {
        workBook.setForceFormulaRecalculation(true);
        BaseFormulaEvaluator.evaluateAllFormulaCells(workBook);
        assetSheet.setSelected(true);
    }

    public void writeSampleAsset(List<String> exportFields, List<OrgInfo> clinicalDeptList) {

        ExcelDocumentStreamingWriter writer = new ExcelDocumentStreamingWriter(workBook, assetSheet.getSheetName(), exportFields);

        int maxRows = ASSET_FORMULA_DEFAULT_MAX_ROWS;
        //write sum formula
        writeSummaryFormula(writer, exportFields, maxRows);

        String[] columnTitles = exportFields.toArray(new String[]{});

        //create asset table
//        createAssetTable(columnTitles);

        writer.writeRowData(EXCEL_ASSET_HEADER_ROW, columnTitles, true);

        int startRow = EXCEL_ASSET_DATA_ROW;
        writer.writeRowData(startRow, buildSampleAsset(clinicalDeptList));

        workBook.setForceFormulaRecalculation(true);
        XSSFFormulaEvaluator.evaluateAllFormulaCells(workBook);

        assetSheet.setSelected(false);

    }

    private void writeClinicalDeptList(List<OrgInfo> clinicalDeptList) {

        ExcelDocumentStreamingWriter writer = new ExcelDocumentStreamingWriter(workBook, orgSheet.getSheetName(), null);

        int startRow = EXCEL_ORG_DATA_ROW;
        for (OrgInfo orgInfo : clinicalDeptList) {

            writer.writeRowData(startRow, new String[]{orgInfo.getName()});

            ++startRow;
        }
        writer.writeRowData(startRow, new String[]{"#"});

        orgSheet.setSelected(false);

    }

    private void writeI18nMessage(List<I18nMessage> messages) {

        ExcelDocumentStreamingWriter writer = new ExcelDocumentStreamingWriter(workBook, tcmSheet.getSheetName(), null);

        int startRow = EXCEL_ORG_DATA_ROW;
        for (I18nMessage i18nMessage : messages) {

            writer.writeRowData(startRow, new String[]{i18nMessage.getMsgKey() + " " + i18nMessage.getValue()});

            ++startRow;
        }
        writer.writeRowData(startRow, new String[]{"#"});

        tcmSheet.setSelected(false);

    }

    public void exportAsset(List<AssetInfo> assetList, List<OrgInfo> sites, List<OrgInfo> clinicalDeptList, List<String> exportFields) throws IOException {
        logger.info("export asset meta data");
        writeMetaData(exportFields);

        if (!crossSite) {
            logger.info("export clinical dept list");
            writeClinicalDeptList(clinicalDeptList);
        }
        writeI18nMessage(spare01List);
        Map encodedToAssetTypeGroupPath = new HashMap<>();;
        Map listSysAuthGroupNamePathInHierarchyMap = new HashMap<>();;
        List<Object> assetTypeGroupPaths = assetTypeGroupRepository.flatternHierarchy();
        encodedToAssetTypeGroupPath = new HashMap<>();
        for (Object obj : assetTypeGroupPaths) {
            Object[] obj1 = (Object[]) obj;
            String encoded = obj1[0].toString();
            String authGroupPaths = obj1[1].toString();
            encodedToAssetTypeGroupPath.put(encoded, authGroupPaths);
        }
        List<Object> listSysAuthGroupNamePathInHierarchy = sysAuthGroupRepository.listSysAuthGroupNamePathInHierarchy();
        listSysAuthGroupNamePathInHierarchyMap = new HashMap<>();
        for (Object obj : listSysAuthGroupNamePathInHierarchy) {
            Object[] obj1 = (Object[]) obj;
            String classificationCode = obj1[0].toString();
            String name = obj1[1].toString();
            listSysAuthGroupNamePathInHierarchyMap.put(classificationCode, name);
        }
        logger.info("export asset list");
        writeAsset(assetList, sites, exportFields,encodedToAssetTypeGroupPath,listSysAuthGroupNamePathInHierarchyMap);
    }

    public void exportSampleAsset(List<OrgInfo> clinicalDeptList, List<String> exportFields) throws IOException {
        writeMetaData(exportFields);

        if (!crossSite) {
            writeClinicalDeptList(clinicalDeptList);
        }

        writeSampleAsset(exportFields, clinicalDeptList);
    }

    public void exportSampleAsset(List<OrgInfo> clinicalDeptList, List<String> exportFields, List<I18nMessage> tcm) throws IOException {
        writeI18nMessage(tcm);
    }

    public InputStream getInputStream() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.workBook.write(out);
        this.workBook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    public void writeToFile(File file) throws IOException {

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workBook.write(fos);
            workBook.close();
        }
    }

    private void writeSummaryFormula(ExcelDocumentStreamingWriter writer, List<String> exportFields, int maxRows) {

        int nameIndex = exportFields.indexOf(WebUtil.getMessage("EquipmentName"));
        int purchasePriceIndex = exportFields.indexOf(WebUtil.getMessage("PurchasePrice"));

        if (nameIndex == -1 || purchasePriceIndex == -1) return;

        String nameColLetter = CellReference.convertNumToColString(nameIndex);
        String purchasePriceLetter = CellReference.convertNumToColString(purchasePriceIndex);

        String countFormula = String.format("COUNTA('Asset List'!$%s$7:$%s$%d)", nameColLetter, nameColLetter, maxRows);

        String sumFormula = String.format("SUM('Asset List'!$%s$7:$%s$%d)", purchasePriceLetter, purchasePriceLetter, maxRows);

      /*  writer.writeCellFormula(5, 1, countFormula);
        writer.writeCellFormula(5, 2, sumFormula);*/

    }

    public void createAssetTable(String[] columnTitles) {

        XSSFTable assetSheetTable = assetSheet.getWorkbook().getXSSFWorkbook().getSheet(assetSheet.getSheetName()).createTable();
        CTTable cttable = assetSheetTable.getCTTable();
        CTAutoFilter autoFilter = cttable.addNewAutoFilter();
        cttable.setAutoFilter(autoFilter);

        AreaReference areaReference = new AreaReference(
                new CellReference(EXCEL_ASSET_HEADER_ROW - 1, 0),
                new CellReference(EXCEL_ASSET_HEADER_ROW - 1 + MAX_FORMULA_ROWS, columnTitles.length - 1),
                SpreadsheetVersion.EXCEL2007);

        cttable.setDisplayName("Asset List");
        cttable.setName("AssetList");
        cttable.setId(1L);
        cttable.setRef(areaReference.formatAsString());
        cttable.setTotalsRowShown(false);
        cttable.setTotalsRowCount(MAX_FORMULA_ROWS);

        CTTableStyleInfo tableStyleInfo = cttable.addNewTableStyleInfo();
        tableStyleInfo.setName("TableStyleMedium1");
        tableStyleInfo.setShowColumnStripes(false);
        tableStyleInfo.setShowRowStripes(true);

        CTTableColumns columns = cttable.addNewTableColumns();
        columns.setCount(columnTitles.length);

        for (int i = 1; i <= columnTitles.length; i++) {
            CTTableColumn column = columns.addNewTableColumn();
            column.setId(i);
            column.setName(columnTitles[i - 1]);

        }


    }

    public Map<String, Object> buildSampleAsset(List<OrgInfo> clinicalDeptList) {

        Map<String, Object> map = new HashMap<>();

        map.put(WebUtil.getMessage("EquipmentName"), "XXXXXX");
        map.put(WebUtil.getMessage("EquipmentID"), "SBBH0001");
        map.put(WebUtil.getMessage("EquipmentAlias"), "XXXX");
        map.put(WebUtil.getMessage("assetOwnerId"), "张三");
        map.put(WebUtil.getMessage("RepairOwner2"), "李四");
        map.put(WebUtil.getMessage("clinicalDeptId"), clinicalDeptList.size() == 0 ? "" : clinicalDeptList.get(0).getName());
        map.put(WebUtil.getMessage("Manufacturer"), "XXX医疗器械有限公司");
        map.put(WebUtil.getMessage("Vendor"), "XX医疗设备有限公司");

        map.put(WebUtil.getMessage("MedicalDeviceClassification"), "6801基础外科手术器械");
        map.put(WebUtil.getMessage("MedicalDeviceSubclassification"), "医用缝合针(不带线)");

        String data = assetGroup.isEmpty() ? "其他类" : assetGroup.get(0).getValueZh();
        map.put(WebUtil.getMessage("assetGroup"), data);

        map.put(WebUtil.getMessage("EquipmentModel"), "XXX");
        map.put(WebUtil.getMessage("ManufacturerSerialNumber"), "123456");

        map.put(WebUtil.getMessage("PurchasePrice"), 100.0);
        map.put(WebUtil.getMessage("salvageValue"), 0.0);
        map.put(WebUtil.getMessage("LifeCycle"), 10);
        map.put(WebUtil.getMessage("depreciationMethod"), "平均年限");

        map.put(WebUtil.getMessage("UsageClassification"), "治疗类");
        map.put(WebUtil.getMessage("capitalSource"), "医疗");
        map.put(WebUtil.getMessage("riskGrade"), CollectionUtils.isEmpty(riskGradeList) ? "二级" : riskGradeList.get(0).getValueZh());
        map.put(WebUtil.getMessage("MeasurementGrade"), "B类计量设备");

        List<I18nMessage> measuringItemCategory = I18nMessageService.getMessagesByType("measuringItemCategory", -1);
        map.put(WebUtil.getMessage("measuringItemCategory"), measuringItemCategory.isEmpty() ? "" : measuringItemCategory.get(0).getValueZh());

        map.put(WebUtil.getMessage("emergencyEquipment"), "急救设备");
        map.put(WebUtil.getMessage("IsImportEquipment"), "进口");
        map.put(WebUtil.getMessage("CurrentStatus"), "正常");
        map.put(WebUtil.getMessage("PMOwner"), "张三");
        map.put(WebUtil.getMessage("patrolInspectionOwnerName"), "张三");
        map.put(WebUtil.getMessage("hospitalId"), UserContextService.getCurrentUserAccount().getSiteName());
        map.put(WebUtil.getMessage("UseOwner"), "张三");
        map.put(WebUtil.getMessage("BrandManufacturerShortName"), "XXX医疗器械有限公司");
        map.put(WebUtil.getMessage("EquipmentStandardName"), "XXX");
        map.put(WebUtil.getMessage("RegistrationNumber"), "国械备XXX号");
        map.put(WebUtil.getMessage("functionGrade"), "1");//功能等级
        map.put(WebUtil.getMessage("repairForm"), CollectionUtils.isEmpty(repairFormList) ? "" : repairFormList.get(0).getValueZh());//维修类型
        map.put(WebUtil.getMessage("systemId"), "0824211XX343");//SystemID/厂商报修ID
        map.put(WebUtil.getMessage("nationalStandardCode"), "20109XX");//设备国标码
        map.put(WebUtil.getMessage("AssetID"), "200868560XXX");//资产编号
        map.put(WebUtil.getMessage("BarCode"), "123456XXX");//条码
        map.put(WebUtil.getMessage("ITSystemID"), "30700238");//院内IT系统编号
        map.put(WebUtil.getMessage("manufactDate"), new Date(124, Calendar.JANUARY, 15));//出厂日期
        map.put(WebUtil.getMessage("purchaseDate"), new Date(124, Calendar.FEBRUARY, 16));//采后日期
        map.put(WebUtil.getMessage("arriveDate"), new Date(124, Calendar.MARCH, 17));//到货日期
        map.put(WebUtil.getMessage("StartUsingDate"), new Date(124, Calendar.APRIL, 18));//启用日期
        map.put(WebUtil.getMessage("Maintainer"), "XXX设备有限公司");//维修商
        map.put(WebUtil.getMessage("InstallationLocation"), "住院部X层");//安装位置
        map.put(WebUtil.getMessage("InstallationLocationNotes"), "XXX科室");//位置备注1(科室)
        map.put(WebUtil.getMessage("capitalSource2"), CollectionUtils.isEmpty(capitalSource2List) ? "" : capitalSource2List.get(0).getValueZh());//资金来源2
        map.put(WebUtil.getMessage("IsPM"), "是");//是否pm
        map.put(WebUtil.getMessage("isFixed"), "是");//是否固定资产
        map.put(WebUtil.getMessage("IsChecked"), "是");//是否盘点
        map.put(WebUtil.getMessage("assetValueCategory"), CollectionUtils.isEmpty(assetValueCategoryList) ? "" : assetValueCategoryList.get(0).getValueZh());//资产价值分布
        map.put(WebUtil.getMessage("deviceStatus"), CollectionUtils.isEmpty(deviceStatusList) ? "" : deviceStatusList.get(0).getValueZh());//资产状态
        map.put(WebUtil.getMessage("isWarrantyNotice"), "是");//资产状态
        map.put(WebUtil.getMessage("rfGroup"), "20422200XX");//射频码分组
        map.put(WebUtil.getMessage("rfId"), "123");//射频码
        assetTypeGroupList = assetTypeGroupRepository.listAssetTypeGroupInHierarchyByUser(UserContextService.getCurrentUserAccount().getUid());
        map.put(WebUtil.getMessage("assetTypeGroupEncoded"), CollectionUtils.isEmpty(assetTypeGroupList) ? "" : assetTypeGroupList.get(0).getEncoded());//设备已使用年限
        map.put(WebUtil.getMessage("AccountingCategory"), CollectionUtils.isEmpty(accountingCategoryList) ? "" : accountingCategoryList.get(0).getValueZh());//核算级别
        map.put("医疗器械分类编码", "01");//医疗器械分类编码
        map.put(WebUtil.getMessage("measuringOwner"), "张三");//计量负责人
        String siteUid = UserContextService.getCurrentUserAccount().getSiteUID();
        String hospitalUid = UserContextService.getCurrentUserAccount().getHospitalUID();
        String institutionUid = UserContextService.getCurrentUserAccount().getInstitutionUID();
        OrgInfo site = null;
        OrgInfo hospital = null;
        OrgInfo institution = null;
        if (Objects.nonNull(siteUid)) {
            site = orgDao.getByUid(siteUid);
        }
        if (Objects.nonNull(hospitalUid)) {
            hospital = orgDao.getByUid(hospitalUid);
        }
        if (Objects.nonNull(institutionUid)) {
            institution = orgDao.getByUid(institutionUid);
        }
        map.put(WebUtil.getMessage("hospitalId"), Objects.isNull(site) ? "" : site.getName());
        map.put(WebUtil.getMessage("Hospital"), Objects.isNull(hospital) ? "" : hospital.getName());
        map.put(WebUtil.getMessage("Institution"), Objects.isNull(institution) ? "" : institution.getName());
        return map;

    }


    private class GetAllElement {
        private List<AssetInfo> assetList;
        private Map<String, List<String>> assetMap;
        private Map parentAssets;
        private Map supliers;
        private List<Object> assetTypeGroupPaths;
        private List<Object> listSysAuthGroupNamePathInHierarchy;
        private Map<String, String> encodedToAssetTypeGroupPath;
        private Map<String, String> listSysAuthGroupNamePathInHierarchyMap;
        private Map<String, String> spare01;

        public GetAllElement(List<AssetInfo> assetList) {
            this.assetList = assetList;
        }

        public Map<String, List<String>> getAssetMap() {
            return assetMap;
        }

        public Map getParentAssets() {
            return parentAssets;
        }

        public Map getSupliers() {
            return supliers;
        }

        public Map getEncodedToAssetTypeGroupPath() {
            return encodedToAssetTypeGroupPath;
        }

        public Map<String, String> getListSysAuthGroupNamePathInHierarchyMap() {
            return listSysAuthGroupNamePathInHierarchyMap;
        }

        public void setListSysAuthGroupNamePathInHierarchyMap(Map<String, String> listSysAuthGroupNamePathInHierarchyMap) {
            this.listSysAuthGroupNamePathInHierarchyMap = listSysAuthGroupNamePathInHierarchyMap;
        }

        public GetAllElement invoke() {
            List<String> uids = new ArrayList<>();
            Set<Integer> parendAssetIds = new HashSet<>();//父id集合
            Set<Integer> suplierIds = new HashSet<>();//供应商id集合
            for (int i = 0; i < assetList.size(); i++) {
                uids.add(assetList.get(i).getUid());
                if (null != assetList.get(i).getParentAssetId()) {
                    parendAssetIds.add(assetList.get(i).getParentAssetId());//获取parentAssetId准备获取parent节点的信息
                }
                if (null != assetList.get(i).getSupplierId()) {
                    suplierIds.add(assetList.get(i).getSupplierId());
                }
                if (null != assetList.get(i).getMaitananceId()) {
                    suplierIds.add(Integer.parseInt(assetList.get(i).getMaitananceId()));
                }
                if (null != assetList.get(i).getManufactureId()) {
                    suplierIds.add(Integer.parseInt(assetList.get(i).getManufactureId()));
                }
            }

            //每次查5000条
            List<HashMap> map = new ArrayList<>();
            int size = uids.size();
            int num = size / 5000;
            int cycles = size % 5000;
            for (int i = 0; i < num; i++) {
                map.addAll(assetTagsMapper.getTagsByAssetMap(uids.subList(i * 5000, (i + 1) * 5000)));
            }
            if (cycles > 0) {
                map.addAll(assetTagsMapper.getTagsByAssetMap(uids.subList(num * 5000, size)));
            }

            assetMap = new HashMap<>();

            for (int i = 0; i < map.size(); i++) {
                //判断assetMap中有没有当前的key
                List<String> assetTagNow = assetMap.get(map.get(i).get("asset_uid"));//尝试从当前assetMap中获取asset_uid对应的数组
                if (null == assetTagNow) {//如果没有塞过值则放入新值
                    List<String> assetTags = new ArrayList();
                    assetTags.add((String) map.get(i).get("tag"));
                    assetMap.put((String) map.get(i).get("asset_uid"), assetTags);
                } else {//不为空的情况直接取出来往后面加
                    List<String> assetTagOld = assetMap.get(map.get(i).get("asset_uid"));
                    assetTagOld.add((String) map.get(i).get("tag"));
                    assetMap.put((String) map.get(i).get("asset_uid"), assetTagOld);
                }
            }

            List<Integer> parendAssetIdList = new ArrayList<>(parendAssetIds);
            List<AssetInfo> parentAssetMap = new ArrayList();
            if (parendAssetIdList.size() == 0) {
                logger.info("====================export asset  data parentAssetMap.size is 0 ");
            } else {
                parentAssetMap = assetInfoMapper.getAssetMap(parendAssetIdList);
            }
            List<Integer> suplierIdList = new ArrayList<>(suplierIds);

            List<Supplier> suplierList = Collections.emptyList();
            if (suplierIdList.size() > 0) {
                suplierList = assetInfoMapper.getSupliersBySuplierIds(suplierIdList);
            }

            parentAssets = new HashMap();
            for (int i = 0; i < parentAssetMap.size(); i++) {
                parentAssets.put(parentAssetMap.get(i).getId(), parentAssetMap.get(i));
            }

            supliers = new HashMap();
            for (int i = 0; i < suplierList.size(); i++) {
                supliers.put(suplierList.get(i).getId(), suplierList.get(i).getTel());
            }

            /*assetTypeGroupPaths = assetTypeGroupRepository.flatternHierarchy();
            encodedToAssetTypeGroupPath = new HashMap<>();
            for (Object obj : assetTypeGroupPaths) {
                Object[] obj1 = (Object[]) obj;
                String encoded = obj1[0].toString();
                String authGroupPaths = obj1[1].toString();
                encodedToAssetTypeGroupPath.put(encoded, authGroupPaths);
            }
            listSysAuthGroupNamePathInHierarchy = sysAuthGroupRepository.listSysAuthGroupNamePathInHierarchy();
            listSysAuthGroupNamePathInHierarchyMap = new HashMap<>();
            for (Object obj : listSysAuthGroupNamePathInHierarchy) {
                Object[] obj1 = (Object[]) obj;
                String classificationCode = obj1[0].toString();
                String name = obj1[1].toString();
                listSysAuthGroupNamePathInHierarchyMap.put(classificationCode, name);
            }*/
            return this;
        }
    }

    public List<String> getAssetSheetHead(){



        return assetSheetHead;
    }
}
