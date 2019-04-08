/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.QrCodeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class QrCodeImportController {

    private UploadedFile file;

    private String selectedSiteUID;

    private UaaService uaaService;

    private QrCodeService qrcodeService;

    private List<OrgInfo> orgInfoList;

    private Map<String, Map<String, Object>> importQrCodeMap;

    @PostConstruct
    protected void init() {
        uaaService = WebUtil.getBean(UaaService.class);
        qrcodeService = WebUtil.getBean(QrCodeService.class);
        
        selectedSiteUID=UserContextService.getCurrentUserAccount().getSiteUID();
    }

    public void cancelUpload() {
        this.file = null;
        importQrCodeMap=new HashMap();
    }

    public void finishUpload() {
        importQrCodeMap = qrcodeService.importQrCode(importQrCodeMap, orgInfoList.stream().filter(item -> Objects.equals(item.getUid(), selectedSiteUID)).findAny().get());

    }

    public void onSiteChange() {

    }

    public List<OrgInfo> getHospitalFileterList() {
        if (null == orgInfoList) {
            UserAccount user = UserContextService.getCurrentUserAccount();
            orgInfoList = uaaService.getUserAccessibleSiteList(user.getUid());
        }
        return orgInfoList;
    }

    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc=null;
        this.file = event.getFile();
        try {
            InputStream in = new ByteArrayInputStream(file.getContents());
            doc = new ExcelDocument(in, ExcelDocument.parseTypeByFileName(file.getFileName()));
            importQrCodeMap = qrcodeService.getQrCodeMapFromTemplate(doc);
        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            Logger.getLogger(CompareImportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--" + e.getMessage());
            Logger.getLogger(CompareImportController.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (doc != null) {
                doc.release();
            }
        }
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String getSelectedSiteUID() {
        return selectedSiteUID;
    }

    public void setSelectedSiteUID(String selectedSiteUID) {
        this.selectedSiteUID = selectedSiteUID;
    }

    public Map<String, Map<String, Object>> getImportQrCodeMap() {
        return importQrCodeMap;
    }

    public void setImportQrCodeMap(Map<String, Map<String, Object>> importQrCodeMap) {
        this.importQrCodeMap = importQrCodeMap;
    }

}
