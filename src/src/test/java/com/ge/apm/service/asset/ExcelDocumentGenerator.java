package com.ge.apm.service.asset;

import com.ge.apm.service.utils.ExcelDocument;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ExcelDocumentGenerator {

    public static String[] headerTitles = {
            "设备编号", "设备名称", "设备别名", "维修负责人", "维修负责人2", "维修负责人3", "院区", "使用科室", "制造商", "品牌/制造商简称",
            "设备标准名称", "注册证号", "供应商", "医疗器械分类", "医疗器械子分类", "设备分类", "功能等级", "维修类型", "设备型号", "制造商序列号",
            "SystemID/厂商报修ID", "2010国家编码", "资产编号", "条码", "IT系统编号", "采购价格", "最终残值", "折旧算法", "使用年限",
            "出厂日期", "采购日期", "到货日期", "启用日期", "质保日期", "维保截止日期", "报废日期", "上次保养日期", "上次计量日期", "上次巡检日期",
            "维修商", "维修商电话", "二维码", "安装位置编码", "安装位置", "使用责任人", "备注",
            "用途分类", "资金来源", "风险等级", "计量等级", "计量项目类别", "是否急救设备", "是否进口设备", "当前状态",
            "是否PM", "是否盘点", "UUID"
    };

    public static String[] defaultRowData = {
            "设备编号", "设备名称", "设备别名", "维修负责人", "维修负责人2", "维修负责人3", "院区", "使用科室", "制造商", "品牌/制造商简称",
            "设备标准名称", "注册证号", "供应商", "医疗器械分类", "医疗器械子分类", "设备分类", "功能等级", "维修类型", "设备型号", "制造商序列号",
            "SystemID/厂商报修ID", "2010国家编码", "资产编号", "条码", "IT系统编号", "采购价格", "最终残值", "折旧算法", "使用年限",
            "出厂日期", "采购日期", "到货日期", "启用日期", "质保日期", "维保截止日期", "报废日期", "上次保养日期", "上次计量日期", "上次巡检日期",
            "维修商", "维修商电话", "二维码", "安装位置编码", "安装位置", "使用责任人", "备注",
            "用途分类", "资金来源", "风险等级", "计量等级", "计量项目类别", "是否急救设备", "是否进口设备", "当前状态",
            "是否PM", "是否盘点", "UUID"
    };

    public static String orgHeaderTitle = "部门名称";

    public static String[] defaultOrgRowData = {
            "内科",
            "外科"
    };

    public static ExcelDocument getDefaultTestDoc() {

        ExcelDocument excelDoc = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Workbook workbook = new XSSFWorkbook();

            //build asset list sheet
            Sheet assetSheet = workbook.createSheet(AssetFileImportService.SHEET_ASSET);
            buildHeaderRow(assetSheet);
            buildOneLineDataRow(assetSheet);

            //build clinical department list sheet
            Sheet orgSheet = workbook.createSheet(AssetFileImportService.SHEET_ORG);
            buildClinicalDeptSheet(orgSheet);

            workbook.write(baos);
            byte[] bytes = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

            excelDoc = new ExcelDocument(bais, ExcelDocument.ExcelType.XLSX);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return excelDoc;

    }

    public static ExcelDocument getTestDoc(int rowCount) {

        ExcelDocument excelDoc = null;

        try {

            Workbook workbook = new XSSFWorkbook();

            //build asset list sheet
            Sheet assetSheet = workbook.createSheet(AssetFileImportService.SHEET_ASSET);
            buildHeaderRow(assetSheet);
            buildDataRow(assetSheet, rowCount);

            //build clinical department list sheet
            Sheet orgSheet = workbook.createSheet(AssetFileImportService.SHEET_ORG);
            buildClinicalDeptSheet(orgSheet);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            byte[] bytes = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

            excelDoc = new ExcelDocument(bais, ExcelDocument.ExcelType.XLSX);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return excelDoc;

    }

    private static void buildClinicalDeptSheet(Sheet sheet) {

        Row headerRow = sheet.createRow(AssetFileImportService.EXCEL_ORG_HEADER_ROW - 1);
        headerRow.createCell(0).setCellValue(orgHeaderTitle);

        for (int i = 0; i < defaultOrgRowData.length; i++) {
            sheet.createRow(AssetFileImportService.EXCEL_ORG_DATA_ROW - 1 + i).createCell(0).setCellValue(defaultOrgRowData[i]);
        }

    }

    private static void buildHeaderRow(Sheet sheet) {

        Row headerRow = sheet.createRow(AssetFileImportService.EXCEL_ASSET_HEADER_ROW - 1);

        int columnIndex = 0;
        for (String title: headerTitles) {
            headerRow.createCell(columnIndex++).setCellValue(title);
        }

    }

    private static void buildOneLineDataRow(Sheet sheet) {

        Row dataRow = sheet.createRow(AssetFileImportService.EXCEL_ASSET_DATA_ROW - 1);

        for (int i = 0; i < headerTitles.length; i ++) {
            applyCellData(dataRow, headerTitles[i], defaultRowData[i]);
        }

    }

    private static void buildDataRow(Sheet sheet, int rowCount) {


        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Row dataRow = sheet.createRow(AssetFileImportService.EXCEL_ASSET_DATA_ROW - 1 + rowIndex);

            applyCellData(dataRow, "设备编号", String.format("SBBH%07d", rowIndex + 1));
            applyCellData(dataRow, "设备名称", "测试名称");
            applyCellData(dataRow, "设备分类", "其他类");
            applyCellData(dataRow, "资产编号", null);
            applyCellData(dataRow, "二维码", null);

        }


    }

    private static void applyCellData(Row dataRow, String columnTitle, String columnValue) {


        for (int i = 0; i < headerTitles.length; i ++) {

            if (columnTitle.equals(headerTitles[i])) {
                dataRow.createCell(i).setCellValue(columnValue);

                return;
            }
        }
    }
}
