package com.ge.apm.view.asset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tiefaces.components.websheet.TieWebSheetBean;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class ExcelTemplateExport extends TieWebSheetBean {
    private static final Logger logger = LoggerFactory.getLogger(ExcelTemplateExport.class);
    private static final long serialVersionUID = 1L;
    public static final String EXCEL_TEMPLATE = "/portal/asset/assetImport/APM_Asset_TieFaces_Template.xlsx";


    @Autowired
    ServletContext servletContext;

    @Override
    public void initialLoad() {

    }

    public void loadTemplate(String excelTemplate, Map<String, Object> context) {


        InputStream stream = servletContext.getResourceAsStream(excelTemplate);
        loadWebSheet(stream, context);
    }

    public void loadTemplate(Map<String, Object> context) {

        loadTemplate(EXCEL_TEMPLATE, context);
    }

    public void exportExcel(Map<String, Object> context) {

        loadTemplate(context);

        //prepare export
        this.doExport();

    }
}