/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.service.asset.AssetFileImportService;
import com.ge.apm.service.asset.AssetFileImportUpdateService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.jsf.FacesContextUtils;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Quan Tang
 */
@ManagedBean
@RequestScoped
public class AssetFileImportUpdateController {

    Logger logger = LoggerFactory.getLogger(AssetFileImportUpdateController.class);

    private Map<String, Map<String, Object>> importAssetMap;

    @Autowired
    private AssetFileImportUpdateService assetFileUpdateService;

    @PostConstruct
    protected void init() {
        FacesContextUtils
                .getRequiredWebApplicationContext(FacesContext.getCurrentInstance())
                .getAutowireCapableBeanFactory().autowireBean(this);

    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            UploadedFile file = event.getFile();
            ExcelDocument doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));

            List<Map<String, Object>> results = doc.getAllDataRows(0, 1, 2);

            //clear data like QR code to avoid unique key exception
//            assetFileUpdateService.clearBeforeUpdateAll(results);

            assetFileUpdateService.updateAll(results);


        } catch (IOException e) {
            WebUtil.addErrorMessage("File can not be read!");
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--" +  e.getMessage());
            logger.error(e.getMessage(), e);
        }

    }



 }
