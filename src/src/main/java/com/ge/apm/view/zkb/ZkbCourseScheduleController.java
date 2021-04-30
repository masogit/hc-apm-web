package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbCourseRepository;
import com.ge.apm.dao.ZkbCourseScheduleRepository;
import com.ge.apm.dao.ZkbCourseTrainUserConfigRepository;
import com.ge.apm.dao.mapper.ZknContractCourseScheduleMapper;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbContractCourseSchedule;
import com.ge.apm.domain.ZkbCourse;
import com.ge.apm.domain.ZkbCourseTrainUserConfig;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbCourseScheduleService;
import com.ge.apm.view.asset.ContractImportController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


/**
 * 课程计划表Controller
 */
@ManagedBean
@ViewScoped
public class ZkbCourseScheduleController extends GenericCRUDUUIDController<ZkbContractCourseSchedule> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbCourseScheduleController.class);

    /**
     * 课程计划表service
     */
    private ZkbCourseScheduleService zkbCourseScheduleService;

    /**
     * 课程计划表repository
     */
    private ZkbCourseScheduleRepository zkbCourseScheduleRepository;

    /**
     * 获取所有目录信息
     */
    private ZkbCourseRepository zkbCourseRepository;

    /**
     * 权限
     */
    private ZkbCourseTrainUserConfigRepository zkbCourseTrainUserConfigRepository;

    /**
     * 当前登录用户信息service
     */
    private UserAccount currentUser;

    /**
     * false
     */
    private static final String f = "f";

    /**
     * true
     */
    private static final String t = "f";

    /**
     * 课程名称
     */
    private String cname;

    /**
     * modality
     */
    private String modality;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间
     */
    private Date endDate;


    private UploadedFile file;

    /**
     * 错误列表
     */
    private List<Map<String, Object>> errorZknCourseScheduleList;

    private static final String EXCEL_NAME = "APM_ZKB_Course_Schedule_Template.xlsx";

    /**
     * 导出文件
     */
    private StreamedContent exportFile;

    private Boolean hasEditRight=false;

    private ZknContractCourseScheduleMapper zknContractCourseScheduleMapper;

    /**
     * 初始化
     */
    @Override
    protected void init() {
        super.init();
        zkbCourseScheduleService = WebUtil.getBean(ZkbCourseScheduleService.class);
        zkbCourseScheduleRepository = WebUtil.getBean(ZkbCourseScheduleRepository.class);
        zkbCourseTrainUserConfigRepository=WebUtil.getBean(ZkbCourseTrainUserConfigRepository.class);
        zkbCourseRepository=WebUtil.getBean(ZkbCourseRepository.class);
        zknContractCourseScheduleMapper=WebUtil.getBean(ZknContractCourseScheduleMapper.class);
        currentUser = UserContextService.getCurrentUserAccount();
        List<ZkbCourseTrainUserConfig> zkbCourseTrainUserConfigList = this.zkbCourseTrainUserConfigRepository.findByLoginName(currentUser.getLoginName());
        if(CollectionUtils.isNotEmpty(zkbCourseTrainUserConfigList))
        {
            hasEditRight=true;
        }
        //zkbCourseScheduleRepository.fetchByTenantUid("0c4f91644a0f4b18993f4fc766cd4022");
        //zknContractCourseScheduleMapper.fetchByTenantUid("0c4f91644a0f4b18993f4fc766cd4022");
    }

    /**
     * 数据查询
     * @param pageRequest
     * @return
     */
    @Override
    protected Page<ZkbContractCourseSchedule> loadData(PageRequest pageRequest) {
        searchFilters= this.getSearchFilter();
        return zkbCourseScheduleRepository.findBySearchFilter(searchFilters, pageRequest);
    }

    /**
     * 动态组装列表过滤条件
     * @return
     */
    public List<SearchFilter> getSearchFilter() {
        List<SearchFilter> searchFilters = new ArrayList<>();
        if(StringUtils.isNotBlank(this.cname))
        {
            searchFilters.add(new SearchFilter("cname", SearchFilter.Operator.LIKE,this.cname));
        }
        if(StringUtils.isNotBlank(this.modality))
        {
            searchFilters.add(new SearchFilter("modality", SearchFilter.Operator.LIKE,this.modality));
        }
        if(startDate!=null)
        {
            searchFilters.add(new SearchFilter("startDate",SearchFilter.Operator.LTE, startDate));
        }
        if(endDate!=null)
        {
            searchFilters.add(new SearchFilter("endDate", SearchFilter.Operator.GTE, endDate));
        }
        return searchFilters;
    }


    /**
     * 跳转导入页面
     * @return
     */
    public String importAction() {
        return "Import?faces-redirect=true";
    }

    /**
     * 是否有编辑权限
     * @return
     */
    public Boolean hasEditRight()
    {
        if(this.selected!=null&&(f.equals(this.selected.getValid())||StringUtils.isBlank(this.selected.getValid()))) return true;
        return false;
    }


    /**
     * 预创建
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();

    }

    /**
     * 预编辑
     */
    @Override
    public void prepareEdit() {
        super.prepareEdit();
    }

    /**
     * 删除
     */
    @Override
    public void delete() {
        super.delete();
        this.cancel();
    }

    /**
     * 取消
     */
    public void cancel() {
        this.selected = null;
    }

    @Override
    public void save() {
        super.save();
        this.selected = null;
    }


    public void handleFileUpload(FileUploadEvent event) throws IOException {

        ExcelDocument doc = null;
        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            int maxRowNum = ZkbCourseScheduleService.EXCEL_COURSE_SCHEDULE_HEADER_ROW + ZkbCourseScheduleService.EXCEL_COURSE_SCHEDULE_IMPORT_MAX_ROW;
            if(doc.checkMaxRowNumIsFull(ZkbCourseScheduleService.SHEET_SERVICE_COURSE_SCHEDULE, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel10000"));
                return;
            }
            List<Map<String, Object>> importCourseScheduleMapList = zkbCourseScheduleService.getZkbCourseScheduleMapFromTemplate(doc);
            zkbCourseScheduleService.importData(importCourseScheduleMapList);
            errorZknCourseScheduleList = zkbCourseScheduleService.filterErrorRecords(importCourseScheduleMapList);
            if(!errorZknCourseScheduleList.isEmpty()){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, WebUtil.getMessage("ImportCompletedNoteFailureRecord"), WebUtil.getMessage("ThereAreATotalOf")+errorZknCourseScheduleList.size()+WebUtil.getMessage("RecordsOfFailure"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            }else {
                WebUtil.addSuccessMessage(WebUtil.getMessage("ImportComplete"));
            }
        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            java.util.logging.Logger.getLogger(ContractImportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage());
            java.util.logging.Logger.getLogger(ContractImportController.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (doc != null) {
                doc.release();
            }
        }

    }

    /**
     * 数据导出
     */
    public void exportAll(){
        List<ZkbContractCourseSchedule> zkbContractCourseSchedule = zkbCourseScheduleRepository.find();
        List<Map<String, Object>> orderHistoryMapList = zkbCourseScheduleService.createExportCourseScheduleData(zkbContractCourseSchedule);
        try {
            String path = ZkbCourseScheduleService.COURSE_SCHEDULE_PATH+EXCEL_NAME;
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(path);
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbCourseScheduleService.exportData(doc,orderHistoryMapList);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", EXCEL_NAME);
        } catch (IOException ex) {
            LOGGER.info(ex.getMessage());
        }
    }

    /**
     * 获取dao
     * @return
     */
    @Override
    protected GenericRepositoryUUID<ZkbContractCourseSchedule> getDAO() {
        return zkbCourseScheduleRepository;
    }

    public List<ZkbCourse> getAllZkbCourse()
    {
        return this.zkbCourseRepository.find();
    }

    public void changeCid()
    {
       ZkbCourse zkbCourse = this.zkbCourseRepository.findOne(this.selected.getZkbCourseId());
        this.selected.setCid(zkbCourse.getCid());
        this.selected.setCname(zkbCourse.getCname());
        this.selected.setModality(zkbCourse.getModality());
    }
    /**
     * 文件下载
     * @return
     */
    public String getTemplateName(){
        return EXCEL_NAME;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Map<String, Object>> getErrorZknCourseScheduleList() {
        return errorZknCourseScheduleList;
    }

    public void setErrorZknCourseScheduleList(List<Map<String, Object>> errorZknCourseScheduleList) {
        this.errorZknCourseScheduleList = errorZknCourseScheduleList;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public Boolean getHasEditRight() {
        return hasEditRight;
    }

    public void setHasEditRight(Boolean hasEditRight) {
        this.hasEditRight = hasEditRight;
    }
}
