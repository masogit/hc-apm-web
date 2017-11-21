/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetSeriaNumConfigRepository;
import com.ge.apm.service.asset.AssetFileImportService;
import com.ge.apm.service.utils.ExcelDocument;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetFileImportController {

    private UploadedFile file;

    private Map<String, Map<String, Object>> importOrgMap;
    private Map<String, Map<String, Object>> importAssetMap;

    private AssetFileImportService aiService;
    private AssetSeriaNumConfigRepository assetSeriaNumConfigRepository;

    private Boolean isImported = false;
    private Boolean hasExportData = false;
    
    private Boolean canbeImported = false;
    private Boolean autoGenerateQrcode = true;
    private Boolean autoGenerateFinancingNum;
    private Boolean isGeneratePmOrder;

    private StreamedContent exportFile;

    @PostConstruct
    protected void init() {
        aiService = WebUtil.getBean(AssetFileImportService.class);
        assetSeriaNumConfigRepository = WebUtil.getBean(AssetSeriaNumConfigRepository.class);

        String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();
        Long configuredCount = assetSeriaNumConfigRepository.countAssetSeriaNumConfigByTenantUID(tenantUID);

        autoGenerateFinancingNum = configuredCount > 0L;

    }

    public void uploadDataFile(FileUploadEvent event) {
        try {
            this.file = event.getFile();
            ExcelDocument doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            if(!aiService.checkDocVersion(doc)){
                WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--模版版本不正确");
                return;
            }
            importOrgMap = aiService.getOrgInfoMapFromTemplate(doc);
            importAssetMap = aiService.getAssetMapFromTemplate(doc);
            
            canbeImported = aiService.checkData(importOrgMap,importAssetMap);

        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            Logger.getLogger(AssetFileImportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage());
            Logger.getLogger(AssetFileImportController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void importData() {
        if (null != importAssetMap) {
            aiService.importData(importOrgMap, importAssetMap, autoGenerateQrcode, autoGenerateFinancingNum,isGeneratePmOrder);
            isImported = true;

            try {
                InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/assetImport/APM_Asset_Template.xlsx");
//                InputStream stream = this.file.getInputstream();
                ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
                hasExportData = aiService.exportFailData(doc, importOrgMap, importAssetMap);
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "failedData.xlsx");
            } catch (IOException ex) {
                Logger.getLogger(AssetFileImportController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void cancel() {
        this.file = null;

    }
    
    public Integer getAssetErrorCounts(){
        if(null!=importAssetMap && !importAssetMap.isEmpty()){
           return Integer.parseInt(String.valueOf(importAssetMap.entrySet().stream().filter(item -> item.getValue().get("status").equals(AssetFileImportService.ImportStatus.CheckFail)).count()));
        }else{
            return 0;
        }
    }
    
    public List<String> getAssetErrorList(){
        if(null == importAssetMap) {
            return null;
        }
        List<String> errors = importAssetMap.entrySet().stream().filter(item -> item.getValue().get("status").equals(AssetFileImportService.ImportStatus.CheckFail))
                .map(item -> item.getValue().get("errmsg").toString()).collect(Collectors.toList());
        return errors;
    }
    
    public Integer getOrgErrorCounts(){
        if(null!=importOrgMap &&!importOrgMap.isEmpty()){
           return Integer.parseInt(String.valueOf(importOrgMap.entrySet().stream().filter(item -> item.getValue().get("status").equals(AssetFileImportService.ImportStatus.CheckFail)).count()));
        }else{
            return 0;
        }
    }

    //Getter and Setter
    public Map<String, Map<String, Object>> getImportOrgMap() {
        return importOrgMap;
    }

    public Map<String, Map<String, Object>> getImportAssetMap() {
        return importAssetMap;
    }

    public Boolean getIsImported() {
        return isImported;
    }

    public Boolean getHasExportData() {
        return hasExportData;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public Boolean getCanbeImported() {
        return canbeImported;
    }

    public Boolean getAutoGenerateQrcode() {
        return autoGenerateQrcode;
    }

    public void setAutoGenerateQrcode(Boolean autoGenerateQrcode) {
        this.autoGenerateQrcode = autoGenerateQrcode;
    }

    public Boolean getAutoGenerateFinancingNum() {
        return autoGenerateFinancingNum;
    }

    public void setAutoGenerateFinancingNum(Boolean autoGenerateFinancingNum) {
        this.autoGenerateFinancingNum = autoGenerateFinancingNum;
    }

    public Boolean getIsGeneratePmOrder() {
        return isGeneratePmOrder;
    }

    public void setIsGeneratePmOrder(Boolean isGeneratePmOrder) {
        this.isGeneratePmOrder = isGeneratePmOrder;
    }
    
}
