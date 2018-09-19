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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Quan Tang
 */

@Service
public class AssetFileDynamicTemplate extends AssetFileImportBaseService {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileDynamicTemplate.class);

    private static final String SHEET_DYNAMIC_LIST_NAME = "动态列表";
    private static final String SHEET_STATIC_LIST_NAME = "静态列表";
    private static final String SHEET_ASSET_GROUP_SECONDAY_LEVEL_NAME = "设备分类2级";

    public static String[] columnTitles = {
            "设备编号", "设备名称", "设备别名", "维修负责人", "维修负责人2", "维修负责人3", "院区", "所属科室", "使用科室", "制造商", "品牌/制造商简称",
            "设备标准名称", "注册证号", "供应商", "医疗器械分类", "医疗器械子分类", "设备分类", "设备分类2级", "主设备", "功能等级", "维修类型", "设备型号", "制造商序列号",
            "SystemID/厂商报修ID", "2010国家编码", "资产编号", "条码", "IT系统编号", "采购价格", "最终残值", "折旧算法", "使用年限",
            "出厂日期", "采购日期", "到货日期", "启用日期", "质保日期", "维保截止日期", "报废日期", "上次保养日期", "上次计量日期", "上次巡检日期",
            "维修商", "维修商电话", "二维码", "安装位置编码", "安装位置", "使用责任人", "备注",
            "用途分类", "资金来源", "风险等级", "计量等级", "计量项目类别", "是否急救设备", "是否进口设备", "当前状态",
            "是否PM", "是否盘点", "资产价值分类", "UUID"
    };

    public static int MAX_FORMULA_ROWS = 5000;

    public static Map<String, String> columnFormulas;

    static
    {
        columnFormulas = new HashMap<>();
//        columnFormulas.put("所属科室", "=OrgList");
//        columnFormulas.put("使用科室", "=OrgList");
        columnFormulas.put("医疗器械分类", null);
        columnFormulas.put("医疗器械子分类", null);
        columnFormulas.put("设备分类", null);
        columnFormulas.put("设备分类2级", "=OFFSET(INDIRECT(\"动态列表!$A$\" & MATCH(OFFSET(INDIRECT(ADDRESS(ROW(), COLUMN())),0,-1),动态列表!A:A,0)),0,0,1,INDIRECT(\"动态列表!$B$\" & MATCH(OFFSET(INDIRECT(ADDRESS(ROW(), COLUMN())),0,-1),动态列表!A:A,0)))");

        columnFormulas.put("功能等级", null);
        columnFormulas.put("维修类型", null);
        columnFormulas.put("折旧算法", null);

        columnFormulas.put("用途分类", null);
        columnFormulas.put("资金来源", null);
        columnFormulas.put("风险等级", null);
        columnFormulas.put("计量等级", null);
        columnFormulas.put("计量项目类别", null);
        columnFormulas.put("是否急救设备", null);
        columnFormulas.put("是否进口设备", null);
        columnFormulas.put("当前状态", null);
        columnFormulas.put("是否PM", null);
        columnFormulas.put("是否盘点", null);
        columnFormulas.put("资产价值分类", null);
    }

    SXSSFWorkbook workBook;

    SXSSFSheet assetSheet;

    SXSSFSheet dynamicListSheet;
    SXSSFSheet staticListSheet;

    public AssetFileDynamicTemplate() throws IOException {
        InputStream inputStream = AssetFileDynamicTemplate.class.getClassLoader().getResourceAsStream("asset/template/APM_Asset_Dynamic_Template.xlsx");
        workBook = new SXSSFWorkbook(new XSSFWorkbook(inputStream));

        assetSheet = workBook.getSheet(SHEET_ASSET);
        if (assetSheet == null) {
            assetSheet = workBook.createSheet(SHEET_ASSET);
        }

        dynamicListSheet = workBook.getSheet(SHEET_DYNAMIC_LIST_NAME);
        if (dynamicListSheet == null) {
            dynamicListSheet = workBook.createSheet(SHEET_DYNAMIC_LIST_NAME);
        }

        staticListSheet = workBook.getSheet(SHEET_STATIC_LIST_NAME);
        if (staticListSheet == null) {
            staticListSheet = workBook.createSheet(SHEET_STATIC_LIST_NAME);
        }

        logger.info("Dynamic template instance created!");

    }

    public void buildMetaData() {

        createDynamicList();

        createStaticList();

        createAssetGroupSecondaryLevel();

        createFormulaForExistingDropdown("所属科室", "'Org List'!$A$5:$A$100");
        createFormulaForExistingDropdown( "使用科室", "'Org List'!$A$5:$A$100");

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

            List<I18nMessage> msgList = i18nDao.getByMsgTypeAndSiteId(msgType, siteId);

            List<String> msgs = msgList.stream().map(I18nMessage::getValueZh).collect(Collectors.toList());

            createDropdownOnlyWithListCount(sheet, rowNum, title, msgs, true);

            ++rowNum;
        }

//        Name namedCell = sheet.getWorkbook().getXSSFWorkbook().createName();
//        namedCell.setNameName("设备分类2级" + "列表");
//        namedCell.setRefersToFormula("INDIRECT(CONCATENATE(\"动态列表!$B$\", MATCH(OFFSET(INDIRECT(ADDRESS(ROW(), COLUMN())),0,-1),动态列表!A:A,0), \":$AZ$\", MATCH(OFFSET(INDIRECT(ADDRESS(ROW(), COLUMN())),0,-1),动态列表!A:A,0)))");

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
        String cellRange = String.format("$B$%s:$AZ$%s", String.valueOf(rowNum + 1), String.valueOf(rowNum + 1));
        namedCell.setRefersToFormula(dynamicListSheet.getSheetName() + "!" + cellRange);

    }

    private void createDropdownWithStaticList(int rowNum, String name, boolean optional, String... items) {

        createDropdownOnly(staticListSheet, rowNum, name, Arrays.asList(items), optional);

        Name namedCell = workBook.getXSSFWorkbook().createName();
        namedCell.setNameName(getRangeName(name));
        String cellRange = String.format("$B$%s:$AZ$%s", String.valueOf(rowNum + 1), String.valueOf(rowNum + 1));
        namedCell.setRefersToFormula(staticListSheet.getSheetName() + "!" + cellRange);
    }

    private String getRangeName(String fieldName) {
        String normalizedName = "_" + fieldName.replaceAll("[\\（\\）]", "_") + "列表";
        return normalizedName;
    }

    private void createFormulaForExistingDropdown(String columnName, String formula) {

        Name namedCell = workBook.getXSSFWorkbook().createName();
        namedCell.setNameName(getRangeName(columnName));
        namedCell.setRefersToFormula(formula);

    }

    public void writeAsset(List<AssetInfo> assetList, List<OrgInfo> sites, List<OrgInfo> clinicalDeptList, List<String> exportFields) {

        ExcelDocumentStreamingWriter writer = new ExcelDocumentStreamingWriter(workBook, assetSheet.getSheetName(), exportFields);

        String[] columnTitles = exportFields.toArray(new String[] {});
        writer.writeRowData(EXCEL_ASSET_HEADER_ROW, columnTitles);

        int startRow = EXCEL_ASSET_DATA_ROW;
        for (AssetInfo assetInfo: assetList) {
            AssetInfoFormatter formatter = new AssetInfoFormatter(assetInfo,  sites ,this, assetDao, orgDao, i18nDao);
            Map<String, Object> rowMap = formatter.buildExcelColumnKeyValueMap(true);

            writer.writeRowData(startRow, rowMap);

            ++startRow;
        }

        assetSheet.setSelected(true);

    }

    public void writeToFile(File file) throws IOException {

        FileOutputStream fos = new FileOutputStream(file);
        workBook.write(fos);
        workBook.close();
        fos.close();
    }

    public void applyFormula(List<String> exportFields) {

        assetSheet.getDataValidations().clear();

        exportFields.stream().forEach(field -> {
            applyCellDropdownIfPossible(exportFields, field);
        });

    }

    public void applyCellDropdownIfPossible(List<String> exportFields, String currentField) {
        String formula = null;

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

}
