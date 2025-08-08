package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbTrainCourseQuotaRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbContractImportService;
import com.ge.apm.service.zkb.ZkbContractService;
import com.ge.apm.service.zkb.ZkbTrainCourseQuotaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
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
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class ZkbTrainCourseQuotaController extends GenericCRUDUUIDController<ZkbTrainCourseQuota> {

    private ZkbTrainCourseQuotaRepository dao;

    private UserContextService userContextService;

    private UserAccount currentUser;

    private ZkbTrainCourseQuotaService zkbTrainCourseQuotaService;

    private String actionName;

    @Getter@Setter
    private ZkbTrainCourse selectedCourse;

    @Getter@Setter
    private List<ZkbTrainCourse> zkbTrainCourseList = new ArrayList<>();
    @Getter@Setter
    private List<Map<String, Object>> importMapList;
    @Getter@Setter
    private List<Map<String, Object>> errorImportList = new ArrayList<>();
    private UploadedFile file;


    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbTrainCourseQuotaRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        zkbTrainCourseQuotaService = WebUtil.getBean(ZkbTrainCourseQuotaService.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected Page<ZkbTrainCourseQuota> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        if(userContextService.isZkbUser()){
            searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
        }

        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    public TenantInfo getTenantInfo(String tenantInfoUID){
        return zkbTrainCourseQuotaService.getTenantInfo(tenantInfoUID);
    }

    public List<TenantInfo> getSourceTenantInfoList(){
        return zkbTrainCourseQuotaService.getSourceTenantInfoList();
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.actionName = "Create";
        selected = new ZkbTrainCourseQuota();
        zkbTrainCourseList = zkbTrainCourseQuotaService.queryTrainCourse();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.actionName = "Edit";
        zkbTrainCourseList = zkbTrainCourseQuotaService.queryTrainCourse();
        selectedCourse = selected.getZkbTrainCourse();
    }

    public List<ZkbTrainCourse> autoCompleteCourse(String query){
        List<ZkbTrainCourse> res = zkbTrainCourseList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)).collect(Collectors.toList());

        return res;
    }

    public void saveData(){
        zkbTrainCourseQuotaService.saveData(selected, selectedCourse, currentUser);
        WebUtil.addSuccessMessage("保存成功！");
    }

    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;

        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            int maxRowNum = ZkbTrainCourseQuotaService.EXCEL_CONTRACT_HEADER_ROW + 5000;
            if(doc.exceedMaxRowNum(ZkbTrainCourseQuotaService.SHEET_NAME, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel"));
                return;
            }
            impQuotaOp(doc);
            if(!errorImportList.isEmpty() ){
                int failCount = errorImportList.size() ;
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


    public void impQuotaOp(ExcelDocument doc){
        try {
            importMapList = zkbTrainCourseQuotaService.getMapFromTemplate(doc);
            if(importMapList != null && !importMapList.isEmpty()){
                zkbTrainCourseQuotaService.importData(importMapList,currentUser);
                errorImportList = zkbTrainCourseQuotaService.filterErrorRecords(importMapList);
                if(errorImportList ==null) errorImportList = new ArrayList<>();
            }else {
                errorImportList = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Setter@Getter
    private StreamedContent exportFile;
    public void downloadImportFailureData() {
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/zkb/contract/contractImport/APM_Contract_Service_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean hasFailData = zkbTrainCourseQuotaService.exportFailData(doc, errorImportList);
            if(hasFailData != null && hasFailData){
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "contractFailedData.xlsx");
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public String getTemplateName(){
        UserContextService ucs = WebUtil.getBean(UserContextService.class);
        return "APM_ZKB_Train_Course_Quota_Template.xlsx";
    }

    @Override
    protected GenericRepositoryUUID<ZkbTrainCourseQuota> getDAO() {
        return dao;
    }
}
