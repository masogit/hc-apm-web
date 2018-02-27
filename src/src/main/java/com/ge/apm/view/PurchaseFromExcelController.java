package com.ge.apm.view;

import com.ge.apm.domain.AssetPurchaseList;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.PurchaseImportService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
pc
 */
@ManagedBean
@ViewScoped
public class PurchaseFromExcelController {

    private PurchaseImportService purchaseImportService;
    private UserAccount currentUser;
    private UploadedFile file;
    private List<AssetPurchaseList> purchaseList;

    @PostConstruct
    protected void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        purchaseImportService = WebUtil.getBean(PurchaseImportService.class);
      
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException, ParseException {
        FacesMessage message = new FacesMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
        this.file = event.getFile();
        purchaseList = purchaseImportService.handleExcel(file, currentUser);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<AssetPurchaseList> getPurchaseList() {
        return purchaseList;
    }

    public void setPurchaseList(List<AssetPurchaseList> purchaseList) {
        this.purchaseList = purchaseList;
    }
}
