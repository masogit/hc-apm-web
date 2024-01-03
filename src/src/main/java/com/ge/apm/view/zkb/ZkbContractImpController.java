package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbContractImportService;
import com.ge.apm.service.zkb.ZkbContractService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author : 503189362
 * @date : 2020/4/30
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbContractImpController extends GenericCRUDUUIDController<ZkbContract>  {
    private ZkbContractRepository dao;
    private UploadedFile file;
    private List<Map<String, Object>> importContractMapList;
    private List<Map<String, Object>> impContractCourseMapList;
    private List<Map<String, Object>> impContractAssetMapList;
    private List<Map<String, Object>> impContractCatMapList;
    private ZkbContractImportService zkbContractImportService;
    private List<Map<String, Object>> errorContractList = new ArrayList<>();
    private List<Map<String, Object>> errorContractCourseList = new ArrayList<>();
    private List<Map<String, Object>> errorContractAssetList = new ArrayList<>();
    private List<Map<String, Object>> errorContractCatList = new ArrayList<>();
    private UserAccount currentUser;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbContractRepository.class);
        zkbContractImportService = WebUtil.getBean(ZkbContractImportService.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;

        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            int maxRowNum = ZkbContractImportService.EXCEL_CONTRACT_HEADER_ROW + 5000;
            if(doc.exceedMaxRowNum(ZkbContractImportService.SHEET_SERVICE_CONTRACT, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel"));
                return;
            }
            impContractOp(doc);
            impContractCourseOp(doc);
            impContractAssetOp(doc);
            impContractCatOp(doc);
            if(!errorContractList.isEmpty() || !errorContractCourseList.isEmpty() || !errorContractAssetList.isEmpty() || !errorContractCatList.isEmpty()){
                int failCount = errorContractList.size() + errorContractCourseList.size() + errorContractAssetList.size() + errorContractCatList.size();
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, WebUtil.getMessage("ImportCompletedNoteFailureRecord"), WebUtil.getMessage("ThereAreATotalOf")+failCount+WebUtil.getMessage("RecordsOfFailure"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            }else {
                WebUtil.addSuccessMessage(WebUtil.getMessage("ImportComplete"));
            }
        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            Logger.getLogger(ZkbContractService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage());
            Logger.getLogger(ZkbContractService.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (doc != null) {
                doc.release();
            }
        }
    }

    public void impContractOp(ExcelDocument doc){
        try {
            importContractMapList = zkbContractImportService.getContractMapFromTemplate(doc);
            if(importContractMapList != null && !importContractMapList.isEmpty()){
                zkbContractImportService.importData(importContractMapList,currentUser);
                errorContractList = zkbContractImportService.filterErrorRecords(importContractMapList);
                if(errorContractList ==null) errorContractList = new ArrayList<>();
            }else {
                errorContractList = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void impContractCourseOp(ExcelDocument doc){
        try {
            impContractCourseMapList = zkbContractImportService.getContractCourseMapFromTemplate(doc);
            if(impContractCourseMapList != null && !impContractCourseMapList.isEmpty()){
                zkbContractImportService.importCourseData(impContractCourseMapList);
                errorContractCourseList = zkbContractImportService.filterCourseErrorRecords(impContractCourseMapList);
                if(errorContractCourseList == null){
                    errorContractCourseList = new ArrayList<>();
                }
            }else{
                errorContractCourseList = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void impContractAssetOp(ExcelDocument doc){
        try {
            impContractAssetMapList = zkbContractImportService.getContractAssetMapFromTemplate(doc);
            if(impContractAssetMapList != null && !impContractAssetMapList.isEmpty()){
                zkbContractImportService.importAssetData(impContractAssetMapList,currentUser);
                errorContractAssetList = zkbContractImportService.filterErrorRecords(impContractAssetMapList);
                if(errorContractAssetList == null){
                    errorContractAssetList = new ArrayList<>();
                }
            }else{
                errorContractAssetList = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void impContractCatOp(ExcelDocument doc){
        try {
            impContractCatMapList = zkbContractImportService.getContractCatMapFromTemplate(doc);
            if(impContractCatMapList != null && !impContractCatMapList.isEmpty()){
                zkbContractImportService.importCatData(impContractCatMapList,currentUser);
                errorContractCatList = zkbContractImportService.filterErrorRecords(impContractCatMapList);
                if(errorContractCatList == null){
                    errorContractCatList = new ArrayList<>();
                }
            }else{
                errorContractCatList = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getTemplateName(){
        UserContextService ucs = WebUtil.getBean(UserContextService.class);
        // return ucs.isLocale() == true ? "excel/APM_ZKB_Contract_imp_Template.xlsx":"excel/APM_ZKB_Contract_imp_Template.xlsx";
        return "excel/APM_ZKB_Contract_imp_Template.xlsx";
    }

    private StreamedContent exportFile;

    public void downloadImportFailureData() {
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/zkb/contract/contractImport/APM_Contract_Service_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean hasFailData = zkbContractImportService.exportFailData(doc, errorContractList);
            if(hasFailData != null && hasFailData){
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "contractFailedData.xlsx");
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    protected GenericRepositoryUUID<ZkbContract> getDAO() {
        return dao;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<Map<String, Object>> getImportContractMapList() {
        return importContractMapList;
    }

    public void setImportContractMapList(List<Map<String, Object>> importContractMapList) {
        this.importContractMapList = importContractMapList;
    }

    public ZkbContractImportService getZkbContractImportService() {
        return zkbContractImportService;
    }

    public void setZkbContractImportService(ZkbContractImportService zkbContractImportService) {
        this.zkbContractImportService = zkbContractImportService;
    }

    public List<Map<String, Object>> getErrorContractList() {
        return errorContractList;
    }

    public void setErrorContractList(List<Map<String, Object>> errorContractList) {
        this.errorContractList = errorContractList;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public List<Map<String, Object>> getImpContractCourseMapList() {
        return impContractCourseMapList;
    }

    public void setImpContractCourseMapList(List<Map<String, Object>> impContractCourseMapList) {
        this.impContractCourseMapList = impContractCourseMapList;
    }

    public List<Map<String, Object>> getImpContractAssetMapList() {
        return impContractAssetMapList;
    }

    public void setImpContractAssetMapList(List<Map<String, Object>> impContractAssetMapList) {
        this.impContractAssetMapList = impContractAssetMapList;
    }

    public List<Map<String, Object>> getErrorContractCourseList() {
        return errorContractCourseList;
    }

    public void setErrorContractCourseList(List<Map<String, Object>> errorContractCourseList) {
        this.errorContractCourseList = errorContractCourseList;
    }

    public List<Map<String, Object>> getErrorContractAssetList() {
        return errorContractAssetList;
    }

    public void setErrorContractAssetList(List<Map<String, Object>> errorContractAssetList) {
        this.errorContractAssetList = errorContractAssetList;
    }

    public List<Map<String, Object>> getImpContractCatMapList() {
        return impContractCatMapList;
    }

    public void setImpContractCatMapList(List<Map<String, Object>> impContractCatMapList) {
        this.impContractCatMapList = impContractCatMapList;
    }

    public List<Map<String, Object>> getErrorContractCatList() {
        return errorContractCatList;
    }

    public void setErrorContractCatList(List<Map<String, Object>> errorContractCatList) {
        this.errorContractCatList = errorContractCatList;
    }
}
