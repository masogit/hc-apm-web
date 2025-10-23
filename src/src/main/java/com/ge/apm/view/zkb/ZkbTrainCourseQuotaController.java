package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbTrainCourseQuotaRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbContractService;
import com.ge.apm.service.zkb.ZkbTrainCourseQuotaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.zkb.utils.ExcelExporter;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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
    private String actionDetailName;

    @Getter@Setter
    private ZkbTrainCourse selectedCourse;

    @Getter@Setter
    private List<ZkbTrainCourse> zkbTrainCourseList = new ArrayList<>();
    @Getter@Setter
    private List<Map<String, Object>> importMapList;
    @Getter@Setter
    private List<Map<String, Object>> errorImportList = new ArrayList<>();
    private UploadedFile file;
    @Getter@Setter
    private ZkbTrainCourseQuotaDetail selectedCourseQuotaDetail;
    @Getter@Setter
    private List<ZkbTrainCourseQuotaDetail> zkbTrainCourseQuotaDetails = new ArrayList<>();

    private static final Map<String, String> COURSE_TYPE_DISPLAY = new HashMap<>();
    static {
        COURSE_TYPE_DISPLAY.put("normal", "普通课程");
        COURSE_TYPE_DISPLAY.put("advance", "进阶课程");
    }


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

    public void courseSelect(){
        selected.setZkbTrainCourse(selectedCourse);
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.actionName = "Create";
        selected = new ZkbTrainCourseQuota();
        zkbTrainCourseList = zkbTrainCourseQuotaService.queryTrainCourse();
        selectedCourse = null;
        zkbTrainCourseQuotaDetails = new ArrayList<>();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.actionName = "Edit";
        zkbTrainCourseList = zkbTrainCourseQuotaService.queryTrainCourse();
        selectedCourse = selected.getZkbTrainCourse();
        zkbTrainCourseQuotaDetails = zkbTrainCourseQuotaService.queryDetailByQuota(selected.getId());

    }

    public List<ZkbTrainCourse> autoCompleteCourse(String query){
        List<ZkbTrainCourse> res = zkbTrainCourseList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)).collect(Collectors.toList());

        return res;
    }

    public void saveData(){
        zkbTrainCourseQuotaService.saveData(selected, selectedCourse, currentUser, zkbTrainCourseQuotaDetails);
        WebUtil.addSuccessMessage("保存成功！");
        selected = null;
        selectedCourse = null;
        zkbTrainCourseQuotaDetails = new ArrayList<>();

    }

    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;

        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            int maxRowNum = ZkbTrainCourseQuotaService.EXCEL_CONTRACT_HEADER_ROW + 5000;
            if(doc.exceedMaxRowNum(0, maxRowNum)){
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

    public void delete(){
        zkbTrainCourseQuotaService.delete(selected);
        WebUtil.addSuccessMessage("删除成功！");
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

    public void prepareDetailCreate(){
        selectedCourseQuotaDetail = new ZkbTrainCourseQuotaDetail();
        selectedCourseQuotaDetail.setTotalQuota(1);
        selectedCourseQuotaDetail.setQuota(1);
        //临时赋予id
        selectedCourseQuotaDetail.setId(UUID.randomUUID().toString());
        actionDetailName = "Create";
    }

    public void prepareDetailEdit(){
        actionDetailName = "Edit";
    }

    public void saveDetail(){
        if("Create".equals(actionDetailName)){
            selectedCourseQuotaDetail.setCreatedBy(currentUser.getCreatedBy());
            selectedCourseQuotaDetail.setCreatedDate(new Date());
            zkbTrainCourseQuotaDetails.add(selectedCourseQuotaDetail);
        }else if("Edit".equals(actionDetailName)){
            selectedCourseQuotaDetail.setLastModifiedBy(currentUser.getCreatedBy());
            selectedCourseQuotaDetail.setLastModifiedDate(new Date());
        }
        //WebUtil.addSuccessMessage("保存成功！");
        calcQuota();
    }

    public void calcQuota(){
        int quota = 0;
        int totalQuota = 0;
        for(ZkbTrainCourseQuotaDetail detail :zkbTrainCourseQuotaDetails){
            quota += detail.getQuota();
            totalQuota += detail.getTotalQuota();
        }
        selected.setQuota(quota);
        selected.setTotalQuota(totalQuota);
    }

    public String getStatusName(int status){
        if(status == 0){
            return "未使用";
        }else if(status == 1){
            return "已使用";
        }else{
            return "";
        }
    }

    public void deleteDetail(){
        zkbTrainCourseQuotaDetails.remove(selectedCourseQuotaDetail);
        WebUtil.addSuccessMessage("删除成功！");
    }

    @Override
    protected GenericRepositoryUUID<ZkbTrainCourseQuota> getDAO() {
        return dao;
    }


    @Setter
    @Getter
    private StreamedContent exportFileWithExtraColumns;


    /**
     * 导出包含额外三列的 Excel 文件
     */
    public StreamedContent exportWithExtraColumns() {

        List<ZkbTrainCourseQuota> allData = zkbTrainCourseQuotaService.findAll();

        List<Map<String, Object>> exportData = new ArrayList<>();

        for (ZkbTrainCourseQuota quota : allData) {
            List<ZkbTrainCourseQuotaDetail> details = zkbTrainCourseQuotaService.queryDetailByQuota(quota.getId());

            if (details.isEmpty()) {
                Map<String, Object> row = new LinkedHashMap<>();
                populateQuotaFields(row, quota);
                row.put("UniqueID", null);
                row.put("总额", null);
                row.put("剩余名额", null);
                exportData.add(row);
            } else {
                for (ZkbTrainCourseQuotaDetail detail : details) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    populateQuotaFields(row, quota);
                    row.put("UniqueID", detail.getUniqueID());
                    row.put("已使用名额", detail.getTotalQuota() - detail.getQuota());
                    row.put("可用名额", detail.getQuota());
                    exportData.add(row);
                }
            }
        }


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ExcelExporter.exportToExcel(outputStream, exportData, "培训课程名额数据");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("导出失败", e);
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(outputStream.toByteArray());

        exportFileWithExtraColumns = new DefaultStreamedContent(bis, "application/vnd.ms-excel", "Zkb_Train_Course_Quota_With_Extra_Columns.xlsx");

        return exportFileWithExtraColumns;
    }

    private void populateQuotaFields(Map<String, Object> row, ZkbTrainCourseQuota quota) {
        row.put("租户", getTenantInfo(quota.getTenantUid()).getName());
        row.put("合同号", quota.getContractNo());
        row.put("合同开始日期", quota.getStartDate());
        row.put("合同结束日期", quota.getEndDate());
        row.put("Modality", quota.getModality());
        row.put("课程名称", quota.getZkbTrainCourse() != null ? quota.getZkbTrainCourse().getName() : "");
        row.put("课程类型", COURSE_TYPE_DISPLAY.get(quota.getZkbTrainCourse() != null ? quota.getZkbTrainCourse().getCourseType() : ""));
        row.put("总额", quota.getTotalQuota());
        row.put("名额", quota.getQuota());
    }

}
