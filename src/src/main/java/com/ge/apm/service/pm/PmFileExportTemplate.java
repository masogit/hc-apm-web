package com.ge.apm.service.pm;

import com.ge.apm.domain.V2_PmOrder;
import com.ge.apm.domain.pure.PmExportFormatter;
import com.ge.apm.service.asset.AssetFileImportBaseService;
import com.ge.apm.service.utils.ExcelDocumentStreamingWriter;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class PmFileExportTemplate extends AssetFileImportBaseService {
    public static final int ASSET_FORMULA_DEFAULT_MAX_ROWS = 20000;

    SXSSFWorkbook workBook;

    SXSSFSheet pmSheet;

    private static final Logger logger = LoggerFactory.getLogger(PmFileExportTemplate.class);
    public InputStream getInputStream() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.workBook.write(out);
        this.workBook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
    public PmFileExportTemplate() throws IOException {
        WebApplicationContext webAppContext = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());

        webAppContext.getAutowireCapableBeanFactory().autowireBean(this);
        InputStream inputStream = PmFileExportTemplate.class.getClassLoader().getResourceAsStream("template/PmOrder_export.xlsx");
        workBook = new SXSSFWorkbook(new XSSFWorkbook(inputStream));
        pmSheet = workBook.getSheet("Sheet1");
    }
    public void exportPm(List<V2_PmOrder> assetList, List<String> exportFields) throws IOException {
        logger.info("export asset list");
        writePm(assetList, exportFields);
    }

    public static final int EXCEL_PM_DATA_ROW = 2;
    public static final int EXCEL_PM_HEADER_ROW = 1;
    public void writePm(List<V2_PmOrder> pmList,List<String> exportFields) {
        ExcelDocumentStreamingWriter writer = new ExcelDocumentStreamingWriter(workBook, pmSheet.getSheetName(), exportFields);

        int maxRows = pmList.size() + EXCEL_PM_DATA_ROW > ASSET_FORMULA_DEFAULT_MAX_ROWS ? pmList.size() + EXCEL_PM_HEADER_ROW : ASSET_FORMULA_DEFAULT_MAX_ROWS;

        String[] columnTitles = exportFields.toArray(new String[] {});
        writer.writeRowData(EXCEL_PM_HEADER_ROW, columnTitles);
        int startRow = EXCEL_PM_DATA_ROW;

        for (V2_PmOrder v2PmOrder: pmList) {
            PmExportFormatter fm = new PmExportFormatter(v2PmOrder);
            Map<String, Object> rowMap = fm.buildExcelColumnKeyValueMap();
            writer.writeRowData(startRow, rowMap);
            if (startRow % 1000 == 0) {
                logger.info("export PM order rows {}", startRow);
            }
            ++startRow;

        }



    }


    private void writeSummaryFormula(ExcelDocumentStreamingWriter writer, List<String> exportFields, int maxRows) {

        int nameIndex = exportFields.indexOf("设备名称");
        int purchasePriceIndex = exportFields.indexOf("采购价格");

        if (nameIndex == -1 || purchasePriceIndex == -1) return;

        String nameColLetter = CellReference.convertNumToColString(nameIndex);
        String purchasePriceLetter = CellReference.convertNumToColString(purchasePriceIndex);
        String[] columnTitles = exportFields.toArray(new String[] {});

        String countFormula = String.format("COUNTA('Asset List'!$%s$7:$%s$%d)", nameColLetter, nameColLetter, maxRows);

        String sumFormula = String.format("SUM('Asset List'!$%s$7:$%s$%d)", purchasePriceLetter, purchasePriceLetter, maxRows);

        writer.writeCellFormula(5, 1, countFormula);
        writer.writeCellFormula(5, 2, sumFormula);

    }
   /* private void applyAssetDateValidation(List<String> exportFields) {

        pmSheet.getDataValidations().clear();

        exportFields.stream().forEach(field -> {
            applyCellDropdownIfPossible(exportFields, field);
        });

    }*/
   /* private void createStaticList() {
        Row row = staticListSheet.createRow(0);
        row.createCell(0).setCellValue("列表名称");
        row.createCell(1).setCellValue("列表");

        int rowNum = 1;
        createDropdownWithStaticList(rowNum++, "功能等级", true, "1", "2", "3");
        createDropdownWithStaticList(rowNum++, "是否PM", true, "是", "否");
        createDropdownWithStaticList(rowNum++, "是否盘点", true, "是", "否");
        createDropdownWithStaticList(rowNum++, "是否对维保到期提醒", true, "是", "否");

        staticListSheet.setSelected(false);
    }

    private void createDropdownWithStaticList(int rowNum, String name, boolean optional, String... items) {

        createDropdownOnly(staticListSheet, rowNum, name, Arrays.asList(items), optional);

        Name namedCell = workBook.getXSSFWorkbook().createName();
        namedCell.setNameName(getRangeName(name));
        String cellRange = getRangeFormulaWithHorizontalList(staticListSheet.getSheetName(), rowNum);
        namedCell.setRefersToFormula(cellRange);
    }*/
}
