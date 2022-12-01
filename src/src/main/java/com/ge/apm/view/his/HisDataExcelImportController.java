/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.his;

import com.ge.apm.service.his.load.HisDataExcelImportService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class HisDataExcelImportController {
    private static final Logger logger = LoggerFactory.getLogger(HisDataExcelImportController.class);

    @Setter @Getter
    private UploadedFile file;

    private HisDataExcelImportService hisDataExcelImportService;


    @PostConstruct
    protected void init() {
        hisDataExcelImportService = WebUtil.getBean(HisDataExcelImportService.class);
    }

    public void uploadDataFile(FileUploadEvent event) {

        this.file = event.getFile();

        try (ExcelDocument excelDocument = new ExcelDocument(file.getInputstream(), ExcelDocument.ExcelType.XLS)) {
            int result = hisDataExcelImportService.importExcel(file.getFileName(), excelDocument, UserContextService.getCurrentUserAccount());

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "导入成功", "导入" + result + "条数据");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void importData() {

        try (ExcelDocument excelDocument = new ExcelDocument(file.getInputstream(), ExcelDocument.ExcelType.XLS)) {
            hisDataExcelImportService.importExcel(file.getFileName(), excelDocument, UserContextService.getCurrentUserAccount());

        } catch (IOException e) {
            logger.error(e.getMessage(), e);

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

    }

}
