/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.budget;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SystemTemplate;
import com.ge.apm.service.utils.ExcelDocumentStreamingWriter;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;
import webapp.framework.web.WebUtil;

import javax.faces.context.FacesContext;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lyj
 */
public class BudgetFileDynamicTemplate  {
    private static final Logger logger = LoggerFactory.getLogger(BudgetFileDynamicTemplate.class);

    public static final int BUDGET_FORMULA_DEFAULT_MAX_ROWS = 20000;

    public static final String SHEET_BUDGET = "Budget List";
    public static final String SHEET_ORG = "Org List";
    public static final int EXCEL_ASSET_HEADER_ROW = 6;
    public static final int EXCEL_ASSET_DATA_ROW = 7;

    public static final int EXCEL_ORG_HEADER_ROW = 4;
    public static final int EXCEL_ORG_DATA_ROW = 2;

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    public static Map<String, String> columnFormulas;

    SXSSFWorkbook workBook;

    SXSSFSheet budgetSheet;
    SXSSFSheet orgSheet;


    public BudgetFileDynamicTemplate() throws IOException {
        WebApplicationContext webAppContext = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
        webAppContext.getAutowireCapableBeanFactory().autowireBean(this);
//        String fileName = "Budget_Import_Template.xlsx";
//        InputStream inputStream = BudgetFileDynamicTemplate.class.getClassLoader().getResourceAsStream("template/" + fileName);
        FileDownloadService fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        InputStream inputStream = fileDownloadService.getTemplateFileInputStream(SystemTemplateEnum.BUDGET_IMPORT.getCode(), SystemTemplateEnum.BUDGET_IMPORT.getFileType());
        workBook = new SXSSFWorkbook(new XSSFWorkbook(inputStream));

        budgetSheet = workBook.getSheet(SHEET_BUDGET);
        if (budgetSheet == null) {
            budgetSheet = workBook.createSheet(SHEET_BUDGET);
        }
        budgetSheet.setDisplayGridlines(true);

        orgSheet = workBook.getSheet(SHEET_ORG);
        if (orgSheet == null) {
            orgSheet = workBook.createSheet(SHEET_ORG);
        }
        orgSheet.setDisplayGridlines(true);
    }



    private void writeClinicalDeptList(List<OrgInfo> clinicalDeptList) {

        ExcelDocumentStreamingWriter writer = new ExcelDocumentStreamingWriter(workBook, orgSheet.getSheetName(), null);

        int startRow = EXCEL_ORG_DATA_ROW;
        for (OrgInfo orgInfo: clinicalDeptList) {

            writer.writeRowData(startRow, new String[]{orgInfo.getName()});

            ++startRow;
        }
        writer.writeRowData(startRow, new String[]{"#"});

        orgSheet.setSelected(false);

    }


    public void exportSampleAsset(List<OrgInfo> clinicalDeptList) throws IOException {
        writeClinicalDeptList(clinicalDeptList);
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
}
