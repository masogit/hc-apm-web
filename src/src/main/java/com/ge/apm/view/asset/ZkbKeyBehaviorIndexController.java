package com.ge.apm.view.asset;

import com.ge.apm.dao.ZkbKeyBehaviorIndexRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.domain.ZkbKeyBehaviorIndex;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbKeyBehaviorIndexService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.chart.LineChartModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author 503202755 2020/9/23
 */
@ManagedBean
@ViewScoped
public class ZkbKeyBehaviorIndexController extends GenericCRUDUUIDController<ZkbKeyBehaviorIndex> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbKeyBehaviorIndexController.class);

    /**
     * 智康宝KPI service
     */
    private ZkbKeyBehaviorIndexService zkbKeyBehaviorIndexService;

    /**
     * 智康宝KPI dao
     */
    private ZkbKeyBehaviorIndexRepository zkbKeyBehaviorIndexRepository;

    /**
     * 租户UID
     */
    private String tenantUID;

    /**
     * 开始时间
     */
    private Date beginningTime;
    /**
     *结束时间
     */
    private Date endingTime;

    /**
     * 列表
     */
    private List<ZkbKeyBehaviorIndex> zkbKeyBehaviorIndexList;


    /**
     * 导出文件
     */
    private StreamedContent exportFile;

    /**
     * 导入文件
     */
    private UploadedFile file;

    /**
     * 合同列表
     */
    private List<ZkbContract> zkbContractList;

    /**
     * 判断用户admin service
     */
    private UserContextService userContextService;
    /**
     * 当前登录用户信息service
     */
    private UserAccount currentUser;

    private static final String dateFormat = "yyyyMMddHHmmss";

    @Override
    protected void init() {
        super.init();
        zkbKeyBehaviorIndexService = WebUtil.getBean(ZkbKeyBehaviorIndexService.class);
        zkbKeyBehaviorIndexRepository = WebUtil.getBean(ZkbKeyBehaviorIndexRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        if(!userContextService.isZkbAdmin())
        {
            String tenantUID = currentUser.getTenantUID();
            if(StringUtils.isNotEmpty(tenantUID))
            {
                this.tenantUID=tenantUID;
            }else
            {
                this.tenantUID="-";
            }
        }
        loadData();
    }
    /**
     * 错误列表
     */
    private List<Map<String, Object>> errorKbiList;

    /**
     * 根据页面条件查询
     */
    public void loadData() {
        this.zkbKeyBehaviorIndexList = this.zkbKeyBehaviorIndexService.loadZkbKeyBehaviorIndex(this.tenantUID,this.beginningTime,this.endingTime);
    }

    /**
     * 生成报表
     */
    public void generateCharts()
    {
        Date date = new Date();
        if(this.beginningTime==null)
        {
            this.beginningTime=this.getFirstDay(date);
        }
        if(this.endingTime==null)
        {
            this.endingTime=this.getLastDay(date);
        }
        this.lineModel = this.zkbKeyBehaviorIndexService.generateCharts(tenantUID,beginningTime,endingTime);
    }


    private Date getLastDay(final Date date)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDay = format.format(cal.getTime());
        lastDay = lastDay+"235959";
        SimpleDateFormat formatter = new SimpleDateFormat(ZkbKeyBehaviorIndexController.dateFormat);
        Date date1 = null;
        try {
            date1 = formatter.parse(lastDay);
        } catch (ParseException e) {
            LOGGER.info(e.getMessage());
        }
        return date1;
    }

    private Date getFirstDay(final Date date)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int last = cal.getActualMinimum(Calendar.DAY_OF_YEAR);
        cal.set(Calendar.DAY_OF_YEAR, last);
        String lastDay = format.format(cal.getTime());
        lastDay = lastDay+"000000";
        SimpleDateFormat formatter = new SimpleDateFormat(ZkbKeyBehaviorIndexController.dateFormat);
        Date date1 = null;
        try {
            date1 = formatter.parse(lastDay);
        } catch (ParseException e) {
            LOGGER.info(e.getMessage());
        }
        return date1;
    }
    /**
     * 图表
     */
    private LineChartModel lineModel = new LineChartModel();

    /**
     * 数据导出
     */
    public void exportAll(){
        List<Map<String, Object>> keyBehaviorIndexMapList = zkbKeyBehaviorIndexService.createExportKeyBehaviorIndexData(this.zkbKeyBehaviorIndexList);
        List<Map<String, Object>> zkbContractMapList = zkbKeyBehaviorIndexService.createExportContractData(this.zkbKeyBehaviorIndexList);
        try {
            UserContextService ucs = WebUtil.getBean(UserContextService.class);
            String fileName =  ucs.isLocale() == true ? ZkbKeyBehaviorIndexService.EXCEL_TEMPLATE_NAME:ZkbKeyBehaviorIndexService.EXCEL_TEMPLATE_EN_NAME;
            String path = ZkbKeyBehaviorIndexService.DIRECOTORY_PATH+fileName;
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(path);
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbKeyBehaviorIndexService.exportData(doc,keyBehaviorIndexMapList,zkbContractMapList);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", fileName);
        } catch (IOException ex) {
            LOGGER.info(ex.getMessage());
        }
    }

    /**
     * 文件上传
     * @param event  文件流
     */
    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;
        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            int maxRowNum = ZkbKeyBehaviorIndexService.EXCEL_KBI_HEADER_ROW + ZkbKeyBehaviorIndexService.EXCEL_KBI_IMPORT_MAX_ROW;
            if(doc.exceedMaxRowNum(ZkbKeyBehaviorIndexService.SHEET_SERVICE_KEY_BEHAVIOR_INDEX, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel"));
                return;
            }
            List<Map<String, Object>> importKbiMapList = zkbKeyBehaviorIndexService.getKbiMapFromTemplate(doc);
            zkbKeyBehaviorIndexService.importData(importKbiMapList);
            errorKbiList = zkbKeyBehaviorIndexService.filterErrorRecords(importKbiMapList);
            if(!errorKbiList.isEmpty()){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, WebUtil.getMessage("ImportCompletedNoteFailureRecord"), WebUtil.getMessage("ThereAreATotalOf")+errorKbiList.size()+WebUtil.getMessage("RecordsOfFailure"));
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

    public LineChartModel getLineModel() {
        return lineModel;
    }

    public void setLineModel(LineChartModel lineModel) {
        this.lineModel = lineModel;
    }

    /**
     * 文件下载
     * @return
     */
    public String getTemplateName(){
        UserContextService ucs = WebUtil.getBean(UserContextService.class);
        return ucs.isLocale() == true? ZkbKeyBehaviorIndexService.EXCEL_TEMPLATE_NAME:ZkbKeyBehaviorIndexService.EXCEL_TEMPLATE_EN_NAME;
    }


    public void calculateScore()
    {
      this.zkbKeyBehaviorIndexService.calculateScore(this.selected,this.zkbContractList);
    }

    @Override
    protected GenericRepositoryUUID<ZkbKeyBehaviorIndex> getDAO() {
        return zkbKeyBehaviorIndexRepository;
    }

    public Date getBeginningTime() {
        return beginningTime;
    }

    public void setBeginningTime(Date beginningTime) {
        this.beginningTime = beginningTime;
    }

    public Date getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(Date endingTime) {
        this.endingTime = endingTime;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUid) {
        this.tenantUID = tenantUid;
    }

    public List<ZkbKeyBehaviorIndex> getZkbKeyBehaviorIndexList() {
        return zkbKeyBehaviorIndexList;
    }

    public void setZkbKeyBehaviorIndexList(List<ZkbKeyBehaviorIndex> zkbKeyBehaviorIndexList) {
        this.zkbKeyBehaviorIndexList = zkbKeyBehaviorIndexList;
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
    }

    /**
     * 跳转导入页面
     * @return
     */
    public String importAction() {
        return "KeyBehaviorIndexImport?faces-redirect=true";
    }

    /**
     * 根据租户UID获取合同列表
     * @return 合同列表
     */
    public List<ZkbContract> getZkbContractList()
    {
        this.zkbContractList = this.zkbKeyBehaviorIndexService.getZkbContractListByTenantUID(this.selected.getTenantUID());
        return zkbContractList;
    }

    @Override
    public void save() {
        if(ZkbKeyBehaviorIndexService.totalFullScore!=this.selected.getTotalFullScore())
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyBehaviorIndex_totalFullScoreError"));
            return;
        }
        if(this.selected.getTotalScore()>ZkbKeyBehaviorIndexService.totalFullScore)
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyBehaviorIndex_totalScoreError"));
            return;
        }
        super.save();
        this.selected = null;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public List<Map<String, Object>> getErrorKbiList() {
        return errorKbiList;
    }

    public void setErrorKbiList(List<Map<String, Object>> errorKbiList) {
        this.errorKbiList = errorKbiList;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
}
