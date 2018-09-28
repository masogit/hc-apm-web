package com.ge.apm.view.asset;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.ContractItemImportService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by 212605082 on 2018/9/18.
 */
@ManagedBean
@ViewScoped
public class ContractItemImportController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ContractItemImportController.class);

    private UploadedFile file;

    private List<Map<String, Object>> importContractItemMapList;

    private UaaService uaaService;
    private ContractItemImportService contractItemImportService;

    private StreamedContent exportFile;

    private String contractType;

    List<Map<String, Object>> errorContractItemList;


    @PostConstruct
    protected void init() {
        contractItemImportService = WebUtil.getBean(ContractItemImportService.class);
        uaaService = WebUtil.getBean(UaaService.class);
    }

    public void uploadDataFile(FileUploadEvent event) {

        ExcelDocument doc = null;
        this.file = event.getFile();
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        try {

            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));

            int maxRowNum = contractItemImportService.EXCEL_CONTRACTITEM_HEADER_ROW + 5000;
            if(doc.exceedMaxRowNum(contractItemImportService.SHEET_CONTRACTITEM, maxRowNum)){
                WebUtil.addErrorMessage("导入Excel中数据行数最多5000条，请分批导入！");
                return;
            }

            importContractItemMapList = contractItemImportService.getContractItemMapFromTemplate(doc, contractType);

            contractItemImportService.importData(importContractItemMapList, contractType, currentUserAccount.getTenantUID());

            errorContractItemList = contractItemImportService.filterErrorRecords(importContractItemMapList);

            WebUtil.addSuccessMessage("导入完成");

        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            Logger.getLogger(ContractItemImportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage());
            Logger.getLogger(ContractItemImportController.class.getName()).log(Level.SEVERE, null, e);
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

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public List<Map<String, Object>> getErrorContractItemList() {
        return errorContractItemList;
    }

    public void setErrorContractItemList(List<Map<String, Object>> errorContractItemList) {
        this.errorContractItemList = errorContractItemList;
    }
}
