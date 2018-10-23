/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.pure.AssetInfoFormatter;
import com.ge.apm.service.utils.ExcelDocumentStreamingWriter;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

import javax.faces.context.FacesContext;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Quan Tang
 */
public class AssetFileDynamicTemplate extends AssetFileImportBaseService {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileDynamicTemplate.class);

    private static final String SHEET_DYNAMIC_LIST_NAME = "动态列表";
    private static final String SHEET_STATIC_LIST_NAME = "静态列表";
    private static final String SHEET_ASSET_GROUP_SECONDAY_LEVEL_NAME = "设备分类2级";
    private static final String SHEET_FUNCTION_TYPE_SECONDAY_LEVEL_NAME = "医疗器械子分类";
    public static final int ASSET_FORMULA_DEFAULT_MAX_ROWS = 20000;

    public static String[] columnTitles = {
            "设备编号", "设备名称", "设备别名", "维修负责人", "维修负责人2", "维修负责人3", "保养负责人", "所属院区", "所属科室", "使用科室", "制造商", "品牌/制造商简称",
            "设备标准名称", "注册证号", "供应商", "医疗器械分类", "医疗器械子分类", "设备分类", "设备分类2级", "主设备", "功能等级", "维修类型", "设备型号", "制造商序列号",
            "SystemID/厂商报修ID", "2010国家编码", "资产编号", "条码", "IT系统编号", "采购价格", "最终残值", "折旧算法", "使用年限",
            "出厂日期", "采购日期", "到货日期", "启用日期", "质保日期", "维保截止日期", "报废日期", "上次保养日期", "上次计量日期", "上次巡检日期",
            "维修商", "维修商电话", "二维码", "安装位置编码", "安装位置", "使用责任人", "备注",
            "用途分类", "资金来源", "风险等级", "计量等级", "计量项目类别", "核算类别", "是否急救设备", "是否进口设备", "当前状态",
            "是否PM", "是否盘点", "资产价值分类", "资产状态", "资金来源2", "是否对维保到期提醒", "UUID"
    };

    public static int MAX_FORMULA_ROWS = 5000;

    public static Map<String, String> columnFormulas;

    static
    {
        //we need to use formula for 2nd level dependant dropdown list, name range define is ok for others
        columnFormulas = new HashMap<>();
        columnFormulas.put("医疗器械子分类", "=OFFSET(INDIRECT(\"医疗器械子分类!$C$\" & MATCH(OFFSET(INDIRECT(ADDRESS(ROW(), COLUMN())),0,-1),医疗器械子分类!A:A,0)),0,0,1,INDIRECT(\"医疗器械子分类!$B$\" & MATCH(OFFSET(INDIRECT(ADDRESS(ROW(), COLUMN())),0,-1),医疗器械子分类!A:A,0)))");
        columnFormulas.put("设备分类2级", "=OFFSET(INDIRECT(\"设备分类2级!$C$\" & MATCH(OFFSET(INDIRECT(ADDRESS(ROW(), COLUMN())),0,-1),设备分类2级!A:A,0)),0,0,1,INDIRECT(\"设备分类2级!$B$\" & MATCH(OFFSET(INDIRECT(ADDRESS(ROW(), COLUMN())),0,-1),设备分类2级!A:A,0)))");

    }

    SXSSFWorkbook workBook;

    SXSSFSheet assetSheet;
    SXSSFSheet orgSheet;

    SXSSFSheet dynamicListSheet;
    SXSSFSheet staticListSheet;

    int siteId;

    public AssetFileDynamicTemplate(int siteId) throws IOException {
        siteId = siteId;

        WebApplicationContext webAppContext = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());

        webAppContext.getAutowireCapableBeanFactory().autowireBean(this);

        InputStream inputStream = AssetFileDynamicTemplate.class.getClassLoader().getResourceAsStream("template/APM_Asset_Dynamic_Template.xlsx");
        workBook = new SXSSFWorkbook(new XSSFWorkbook(inputStream));

        assetSheet = workBook.getSheet(SHEET_ASSET);
        if (assetSheet == null) {
            assetSheet = workBook.createSheet(SHEET_ASSET);
        }

        orgSheet = workBook.getSheet(SHEET_ORG);
        if (orgSheet == null) {
            orgSheet = workBook.createSheet(SHEET_ORG);
        }

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

        createFormulaForExistingDropdown("所属科室", "OFFSET('Org List'!$A$5,0,0,COUNTA('Org List'!$A:$A) - 2, 1)");
        createFormulaForExistingDropdown( "使用科室", "OFFSET('Org List'!$A$5,0,0,COUNTA('Org List'!$A:$A) - 2, 1)");
    }

    private void createDynamicList() {

        Row row = dynamicListSheet.createRow(0);
        row.createCell(0).setCellValue("列表名称");
        row.createCell(1).setCellValue("列表");

        createDropdownWithNamedRange(1, "设备分类", assetGroup, false);
        createDropdownWithNamedRange(2, "医疗器械分类", assetFunctionTypes, true);
        createDropdownWithNamedRange(3, "折旧算法", depreciationMethod, true);
        createDropdownWithNamedRange(4, "用途分类", assetUsageList, true);
        createDropdownWithNamedRange(5, "资金来源", capitalSourceList, true);
        createDropdownWithNamedRange(6, "计量等级", measureGradeList, true);
        createDropdownWithNamedRange(7, "风险等级", riskGradeList, true);
        createDropdownWithNamedRange(8, "是否急救设备", emergencyEquipmentList, true);
        createDropdownWithNamedRange(9, "是否进口设备", importEquipmentList, true);
        createDropdownWithNamedRange(10, "核算类别", accountingCategoryList, true);
        createDropdownWithNamedRange(11, "计量项目类别", measuringItemCategoryList, true);
        createDropdownWithNamedRange(12, "维修类型", repairFormList, true);
        createDropdownWithNamedRange(13, "当前状态", assetStatusList, false);
        createDropdownWithNamedRange(14, "资产价值分类", assetValueCategoryList, true);

        dynamicListSheet.setSelected(false);
    }

    private void createStaticList() {
        Row row = staticListSheet.createRow(0);
        row.createCell(0).setCellValue("列表名称");
        row.createCell(1).setCellValue("列表");

        createDropdownWithStaticList(1, "功能等级", true, "1", "2", "3");
        createDropdownWithStaticList(2, "是否PM", true, "是", "否");
        createDropdownWithStaticList(3, "是否盘点", true, "是", "否");

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
        row.createCell(0).setCellValue("列表名称");
        row.createCell(1).setCellValue("列表项个数");
        row.createCell(2).setCellValue("列表项");

        int rowNum = 1;
        for (I18nMessage msg: assetGroup) {
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
        row.createCell(0).setCellValue("列表名称");
        row.createCell(1).setCellValue("列表项个数");
        row.createCell(2).setCellValue("列表项");

        int rowNum = 1;
        for (I18nMessage msg: assetFunctionTypes) {
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

        for (String msg: msgList) {
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

        for (String msg: msgList) {
            row.createCell(columnIndex++, CellType.STRING).setCellValue(msg);
        }

        if (optional) {
            // # mark is used to delete the column in database record.
            row.createCell(columnIndex++, CellType.STRING).setCellValue("#");
        }

    }

    private void createDropdownWithNamedRange(int rowNum, String name, List<I18nMessage> msgList, boolean optional) {

        List<String> msgs = msgList.stream().map(I18nMessage::getValueZh).collect(Collectors.toList());

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
        String normalizedName = fieldName.replaceAll("[\\（\\）]", "_") + "列表";
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

    public void writeAsset(List<AssetInfo> assetList, List<OrgInfo> sites, List<String> exportFields) {

        ExcelDocumentStreamingWriter writer = new ExcelDocumentStreamingWriter(workBook, assetSheet.getSheetName(), exportFields);

        int maxRows = assetList.size() + EXCEL_ASSET_HEADER_ROW > ASSET_FORMULA_DEFAULT_MAX_ROWS ? assetList.size() + EXCEL_ASSET_HEADER_ROW : ASSET_FORMULA_DEFAULT_MAX_ROWS;
        //write sum formula
        writeSummaryFormula(writer, exportFields, maxRows);

        String[] columnTitles = exportFields.toArray(new String[] {});

        //create asset table
//        createAssetTable(columnTitles);

        writer.writeRowData(EXCEL_ASSET_HEADER_ROW, columnTitles);

        int startRow = EXCEL_ASSET_DATA_ROW;
        for (AssetInfo assetInfo: assetList) {
            AssetInfoFormatter formatter = new AssetInfoFormatter(assetInfo,  sites ,this, assetDao, orgDao, i18nDao, siteId);
            Map<String, Object> rowMap = formatter.buildExcelColumnKeyValueMap(true);

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

    public void writeSampleAsset(List<String> exportFields) {

        ExcelDocumentStreamingWriter writer = new ExcelDocumentStreamingWriter(workBook, assetSheet.getSheetName(), exportFields);

        int maxRows = ASSET_FORMULA_DEFAULT_MAX_ROWS;
        //write sum formula
        writeSummaryFormula(writer, exportFields, maxRows);

        String[] columnTitles = exportFields.toArray(new String[] {});

        //create asset table
//        createAssetTable(columnTitles);

        writer.writeRowData(EXCEL_ASSET_HEADER_ROW, columnTitles);

        int startRow = EXCEL_ASSET_DATA_ROW;
        writer.writeRowData(startRow, buildSampleAsset());

        workBook.setForceFormulaRecalculation(true);
        XSSFFormulaEvaluator.evaluateAllFormulaCells(workBook);

        assetSheet.setSelected(true);

    }

    public void writeClinicalDeptList(List<OrgInfo> clinicalDeptList) {

        ExcelDocumentStreamingWriter writer = new ExcelDocumentStreamingWriter(workBook, orgSheet.getSheetName(), null);

        int startRow = EXCEL_ORG_DATA_ROW;
        for (OrgInfo orgInfo: clinicalDeptList) {

            writer.writeRowData(startRow, new String[]{orgInfo.getName()});

            ++startRow;
        }
        writer.writeRowData(startRow, new String[]{"#"});

        orgSheet.setSelected(false);

    }

    public void exportAsset(List<AssetInfo> assetList, List<OrgInfo> sites, List<OrgInfo> clinicalDeptList, List<String> exportFields) throws IOException {
        logger.info("export asset meta data");
        writeMetaData(exportFields);
        logger.info("export clinical dept list");
        writeClinicalDeptList(clinicalDeptList);
        logger.info("export asset list");
        writeAsset(assetList, sites, exportFields);
    }

    public void exportSampleAsset(List<OrgInfo> clinicalDeptList, List<String> exportFields) throws IOException {
        writeMetaData(exportFields);
        writeClinicalDeptList(clinicalDeptList);
        writeSampleAsset(exportFields);
    }

    public InputStream getInputStream() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.workBook.write(out);
        this.workBook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    public void writeToFile(File file) throws IOException {

        FileOutputStream fos = new FileOutputStream(file);
        workBook.write(fos);
        workBook.close();
        fos.close();
    }

    private void writeSummaryFormula(ExcelDocumentStreamingWriter writer, List<String> exportFields, int maxRows) {

        int nameIndex = exportFields.indexOf("设备名称");
        int purchasePriceIndex = exportFields.indexOf("采购价格");

        String nameColLetter = CellReference.convertNumToColString(nameIndex);
        String purchasePriceLetter = CellReference.convertNumToColString(purchasePriceIndex);

        String countFormula = String.format("COUNTA('Asset List'!$%s$7:$%s$%d)", nameColLetter, nameColLetter, maxRows);

        String sumFormula = String.format("SUM('Asset List'!$%s$7:$%s$%d)", purchasePriceLetter, purchasePriceLetter, maxRows);

        writer.writeCellFormula(5, 1, countFormula);
        writer.writeCellFormula(5, 2, sumFormula);

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

    public Map<String, Object> buildSampleAsset() {

        Map<String, Object> map = new HashMap<>();

        map.put("设备名称", "XXXXXX");
        map.put("设备编号", "SBBH0001");
        map.put("设备别名", "XXXX");
        map.put("维修负责人", "张三");
        map.put("维修负责人2", "李四");
        map.put("所属科室", "放射科");
        map.put("制造商", "XXX医疗器械有限公司");
        map.put("供应商", "XX医疗设备有限公司");

        map.put("医疗器械分类", "6801基础外科手术器械");
        map.put("医疗器械子分类", "医用缝合针(不带线)");

        String data = assetGroup.isEmpty() ? "其他类" : assetGroup.get(0).getValueZh();
        map.put("设备分类", data);

        map.put("设备型号", "XXX");
        map.put("制造商序列号", "123456");

        map.put("采购价格", 100.0);
        map.put("最终残值", 0.0);
        map.put("使用年限", null);
        map.put("折旧算法", "平均年限");

        map.put("用途分类", "治疗类");
        map.put("资金来源", "医疗");
        map.put("风险等级", "二级");
        map.put("计量等级", "B类计量设备");
        map.put("计量项目类别", "磁共振类");
        map.put("是否急救设备", "急救设备");
        map.put("是否进口设备", "进口");
        map.put("当前状态", "正常");

        return map;

    }

}
