package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbCourseRepository;
import com.ge.apm.dao.ZkbCourseScheduleRepository;
import com.ge.apm.dao.ZkbCourseTrainRepository;
import com.ge.apm.dao.ZkbCourseTrainUserConfigRepository;
import com.ge.apm.dao.mapper.ZknContractCourseScheduleMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbCourseScheduleService;
import com.ge.apm.service.zkb.ZkbFePartOrderService;
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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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
     * 获取培训信息
     */
    private ZkbCourseTrainRepository zkbCourseTrainRepository;

    /**
     * 当前登录用户信息service
     */
    private UserAccount currentUser;

    /**
     * false
     */
    public static final String f = "f";

    /**
     * true
     */
    public static final String t = "t";

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
     * 是否生效
     */
    private String valid;

    /**
     * 错误列表
     */
    private List<Map<String, Object>> errorZknCourseScheduleList;

    private static final String EXCEL_NAME = "APM_ZKB_Course_Schedule_Template.xlsx";

    private static final String Export_EXCEL_NAME = "APM_ZKB_Course_Schedule_Template_Export.xlsx";

    /**
     * 导出文件
     */
    private StreamedContent exportFile;

    private Boolean hasEditRight=false;

    private ZknContractCourseScheduleMapper zknContractCourseScheduleMapper;

    private ZkbFePartOrderService zkbFePartOrderService;

    private Map<String, Boolean> selectedMap = new HashMap<>();

    private Page<ZkbContractCourseSchedule> page;

    boolean testPerson = false;

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
        zkbFePartOrderService=WebUtil.getBean(ZkbFePartOrderService.class);
        zkbCourseTrainRepository=WebUtil.getBean(ZkbCourseTrainRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        List<ZkbCourseTrainUserConfig> zkbCourseTrainUserConfigList = this.zkbCourseTrainUserConfigRepository.findByLoginName(currentUser.getLoginName());
        if(CollectionUtils.isNotEmpty(zkbCourseTrainUserConfigList))
        {
            hasEditRight=true;
        }
        String loginName =currentUser.getLoginName();
        if(StringUtils.isNotBlank(loginName)&&loginName.startsWith(ZkbFePartOrderService.testLoginNameStartWith))
        {
            testPerson=true;
        }
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
        page = zkbCourseScheduleRepository.findBySearchFilter(searchFilters, pageRequest);
        return page;
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
            searchFilters.add(new SearchFilter("startDate",SearchFilter.Operator.GTE, startDate));
        }
        if(endDate!=null)
        {
            searchFilters.add(new SearchFilter("endDate", SearchFilter.Operator.LTE, endDate));
        }
        if(valid!=null)
        {
            searchFilters.add(new SearchFilter("valid", SearchFilter.Operator.EQ, valid));
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
     * 预创建
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.selected.setValid(f);

    }

    /**
     * 批量生效
     */
    public void batchValid()
    {
        if(this.selectedMap==null||this.selectedMap.isEmpty())
            return;
        StringBuffer ids = new StringBuffer();
        for (String key : selectedMap.keySet()) {
            if(!this.selectedMap.get(key))
                continue;
            ids=ids.append(key).append(",");
            ZkbContractCourseSchedule item = this.zkbCourseScheduleRepository.findById(key);
            item.setValid(t);
            item.setValidDate(new Date());
            this.zkbCourseScheduleRepository.save(item);
        }
        String idsStr = ids.toString();
        if(StringUtils.isBlank(idsStr))
            return;
        idsStr = idsStr.substring(0,idsStr.length()-1);
        this.zkbFePartOrderService.sendMessage(idsStr, GatewayURL.CONTRACT_COURSE_SCHEDULE_MESSAGE);
        WebUtil.addSuccessMessage("生效成功！");
        this.selectedMap.clear();
    }

    public void sendTestMessage()
    {
        this.zkbCourseScheduleService.sendMessageByTelephone(GatewayURL.CONTRACT_COURSE_SCHEDULE_SEND_MESSAGE_BY_TELEPHONE);
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
        if(this.selectedMap==null||this.selectedMap.isEmpty())
            return;
        List<String> ids = new ArrayList<>();
        for (String key : selectedMap.keySet()) {
            if(!this.selectedMap.get(key))
                continue;
            ids.add(key);
        }
        if(CollectionUtils.isEmpty(ids))
        {
            WebUtil.addErrorMessage("请选中需要删除的数据");
            return;
        }
        StringBuffer cnName = new StringBuffer();
        boolean canNotDelete = true;
        for(String id:ids)
        {
            List<ZkbCourseTrain> zkbCourseTrainList = this.zkbCourseTrainRepository.findByCourseScheduleId(id);
            if(CollectionUtils.isNotEmpty(zkbCourseTrainList))
            {
                ZkbContractCourseSchedule zbContractCourseSchedule = this.zkbCourseScheduleRepository.findById(id);
                cnName=cnName.append("《").append(zbContractCourseSchedule.getCname()).append("》,");
                canNotDelete=false;
            }
        }
        if(!canNotDelete)
        {
            cnName.deleteCharAt(cnName.length()-1);
            WebUtil.addErrorMessage(cnName.toString()+"已有用户提交报名记录，不能删除");
            return;
        }
        for(String id:ids)
        {
            this.zkbCourseScheduleRepository.delete(id);
        }
        this.cancel();
    }

    public boolean checkSelectMap()
    {
        boolean batchValid = false;
        if(this.selectedMap!=null&&!this.selectedMap.isEmpty())
        {
           for(String key:selectedMap.keySet())
           {
                if(selectedMap.get(key))
                {
                    batchValid=true;
                }
           }
        }
        return batchValid;
    }

    /**
     * 取消
     */
    public void cancel() {
        this.selected = null;
    }

    @Override
    public void save() {
        String id = this.selected.getId();
        List<ZkbCourseTrain> zkbCourseTrainList = this.zkbCourseTrainRepository.checkCourseTrain(id,ZkbCourseTrain.ParticipateStatusEnum.Y.toString());
        if(CollectionUtils.isNotEmpty(zkbCourseTrainList))
        {
            WebUtil.addErrorMessage("该课程已有用户报名，不能修改");
            return;
        }
        if(this.selected.getStartDate().compareTo(this.selected.getEndDate())>0)
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbContractCourseSchedule_startDate")+"不能大于"+WebUtil.getMessage("zkbContractCourseSchedule_endDate"));
            return;
        }
        if(this.selected.getTrainStartDate().compareTo(this.selected.getTrainEndDate())>0)
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbContractCourseSchedule_trainStartDate")+"不能大于"+WebUtil.getMessage("zkbContractCourseSchedule_trainEndDate"));
            return;
        }
        if(StringUtils.isNotBlank(this.selected.getValid())&&t.equals(this.selected.getValid()))
        {
            this.selected.setValidDate(new Date());
        }
        super.save();
        this.selected = null;
    }

    public void onSelectAllOfPage() {
        List<ZkbContractCourseSchedule> zkbContractCourseScheduleList = page.getContent();
        boolean isSelectedAll = !zkbContractCourseScheduleList.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (ZkbContractCourseSchedule item : zkbContractCourseScheduleList) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
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
                WebUtil.addErrorMessage("导入失败");
            }else {
                WebUtil.addSuccessMessage(WebUtil.getMessage("ImportComplete"));
            }
        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            java.util.logging.Logger.getLogger(ContractImportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError"));
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
            String path = ZkbCourseScheduleService.COURSE_SCHEDULE_PATH+Export_EXCEL_NAME;
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

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public boolean isTestPerson() {
        return testPerson;
    }

    public void setTestPerson(boolean testPerson) {
        this.testPerson = testPerson;
    }
}
