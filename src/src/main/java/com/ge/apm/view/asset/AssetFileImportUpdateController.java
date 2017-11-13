/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetSeriaNumConfigRepository;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.AssetFileImportService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.jsf.FacesContextUtils;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetFileImportUpdateController {

    private UploadedFile file;

    private Map<String, Map<String, Object>> importOrgMap;
    private Map<String, Map<String, Object>> importAssetMap;

    @Autowired
    private AssetFileImportService aiService;

    @Autowired
    private AssetSeriaNumConfigRepository assetSeriaNumConfigRepository;

    private Boolean isImported = false;
    private Boolean hasExportData = false;
    
    private Boolean canbeImported = false;
    private Boolean autoGenerateQrcode = true;
    private Boolean autoGenerateFinancingNum;

    private StreamedContent exportFile;

    @PostConstruct
    protected void init() {
        FacesContextUtils
                .getRequiredWebApplicationContext(FacesContext.getCurrentInstance())
                .getAutowireCapableBeanFactory().autowireBean(this);

        aiService = WebUtil.getBean(AssetFileImportService.class);
        assetSeriaNumConfigRepository = WebUtil.getBean(AssetSeriaNumConfigRepository.class);

        String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();


    }

    public void uploadDataFile(FileUploadEvent event) {
        try {
            this.file = event.getFile();
            ExcelDocument doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            if(!aiService.checkDocVersion(doc)){
                WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--模版版本不正确");
                return;
            }

        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            Logger.getLogger(AssetFileImportUpdateController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage());
            Logger.getLogger(AssetFileImportUpdateController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        this.file = event.getFile();

    }



 }
