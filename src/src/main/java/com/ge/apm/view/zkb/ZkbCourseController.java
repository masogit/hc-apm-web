package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbCourseRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbCourse;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbCourseImExportService;
import com.ge.apm.service.zkb.ZkbCourseScheduleService;
import com.ge.apm.service.zkb.ZkbCourseService;
import com.ge.apm.view.sysutil.UserContextService;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@ManagedBean
@ViewScoped
public class ZkbCourseController extends GenericCRUDUUIDController<ZkbCourse> {

    private ZkbCourseService zkbCourseServic;

    private ZkbCourseScheduleService zkbCourseScheduleService;

    private UserContextService userContextService;

    private ZkbCourseImExportService zkbCourseImExportService;

    private UserAccount currentUser;

    private String selectionTenantInfo;

    private Page<ZkbCourse> zkbCoursePage;

    private String actionName;

    private StreamedContent exportFile;
    private UploadedFile file;
    private List<ZkbCourse> importFailedDataList;

    private ZkbCourseRepository zkbCourseRepository;

    @Override
    protected void init() {
        super.init();
        zkbCourseServic = WebUtil.getBean(ZkbCourseService.class);
        zkbCourseScheduleService = WebUtil.getBean(ZkbCourseScheduleService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        zkbCourseImExportService = WebUtil.getBean(ZkbCourseImExportService.class);
        zkbCourseRepository=WebUtil.getBean(ZkbCourseRepository.class);

        if(userContextService.isZkbUser()) {
            this.selectionTenantInfo = currentUser.getTenantUID();
        }
    }

    @Override
    protected Page<ZkbCourse> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        if(this.selectionTenantInfo != null){
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectionTenantInfo));
        }

        zkbCoursePage = zkbCourseServic.findBySearchFilter(searchFilters, pageRequest);

        return zkbCoursePage;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.actionName = "Create";
        this.selected.setTenantUID(this.selectionTenantInfo);
        this.selected.setCreatedBy(this.currentUser.getLoginName());
        this.selected.setCreatedDate(new Date());
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.actionName = "Edit";
        this.selected.setLastModifiedBy(this.currentUser.getLoginName());
        this.selected.setLastModifiedDate(new Date());

    }


    public String saveCourse() {
        if (zkbCourseServic.isCourseExist(this.selected)){
            WebUtil.addErrorMessage("课程ID已存在");
            return "";
        }
        super.save();
        this.cancel();
        return "List?faces-redirect=true";
    }

    public String deleteCourse(){
        if(zkbCourseServic.isContractUsed(this.selected)){
            WebUtil.addErrorMessage("该课程在合同已使用，不能删除! ");
            return "";
        }
        if(zkbCourseScheduleService.isCourseUsed(this.selected)){
            WebUtil.addErrorMessage("该课程在课程计划已使用，不能删除! ");
            return "";
        }
        super.delete();
        this.cancel();
        return "List?faces-redirect=true";
    }

    @Override
    public void delete() {
        super.delete();
        this.cancel();
    }

    public void cancel() {
        this.selected = null;
    }


    public void exportAll(){
        List<Map<String, Object>> zkbCourseDataList = zkbCourseImExportService.createZkbCourseData( this.zkbCourseRepository.find());
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/zkb/course/APM_ZKB_Course_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbCourseImExportService.exportData(doc, zkbCourseDataList);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "ZKBCourseData.xlsx");
        } catch (IOException ex) {
            Logger.getLogger(ZkbCourseController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {

        this.file = event.getFile();
        importFailedDataList = zkbCourseImExportService.handleExcel(file, currentUser);
        if(importFailedDataList.size()==0){
            WebUtil.addSuccessMessage("导入完成！");
        }
        else{
            WebUtil.addErrorMessage("导入失败！请修改错误数据");
        }

    }


    @Override
    protected GenericRepositoryUUID<ZkbCourse> getDAO() {
        return zkbCourseServic.getCourseDao();
    }

    public String getSelectionTenantInfo() {
        return selectionTenantInfo;
    }

    public void setSelectionTenantInfo(String selectionTenantInfo) {
        this.selectionTenantInfo = selectionTenantInfo;
    }

    public Page<ZkbCourse> getZkbCoursePage() {
        return zkbCoursePage;
    }

    public void setZkbCoursePage(Page<ZkbCourse> zkbCoursePage) {
        this.zkbCoursePage = zkbCoursePage;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<ZkbCourse> getImportFailedDataList() {
        return importFailedDataList;
    }

    public void setImportFailedDataList(List<ZkbCourse> importFailedDataList) {
        this.importFailedDataList = importFailedDataList;
    }
}
