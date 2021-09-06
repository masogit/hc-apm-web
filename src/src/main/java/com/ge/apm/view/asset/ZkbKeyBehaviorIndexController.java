package com.ge.apm.view.asset;

import com.ge.apm.dao.ZkbKeyBehaviorIndexRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.domain.ZkbKeyBehaviorIndex;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbKeyBehaviorIndexService;
import com.ge.apm.service.zkb.ZkbPartOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.zkb.ZkbPartOrderController;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.chart.LineChartModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.util.DateUtil;
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
 * KPI
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
     * 要生成KPI数据的月份
     */
    private Date kpiDate;

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

    public static final String dateFormat = "yyyyMMddHHmmss";

    /**
     * kpi新增
     */
    public static final String kpi_add = "KpiAdd";

    /**
     * kpi修改
     */
    public static final String kpi_modify = "KpiModify";

    private Map<String,Map<String,Integer>> rankMap = null;

    private String isZkbadmin =ZkbKeyBehaviorIndexService.yes;

    private List<TenantInfo> tenantInfoList;

    private List<TenantInfo> selectTenantInfoList;

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
            isZkbadmin=ZkbKeyBehaviorIndexService.no;
        }
        this.rankMap = this.zkbKeyBehaviorIndexService.initRankingMap();
        this.tenantInfoList=this.zkbKeyBehaviorIndexService.getSourceTenantInfoList(currentUser.getLoginName());
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
        this.zkbKeyBehaviorIndexList = this.zkbKeyBehaviorIndexService.loadZkbKeyBehaviorIndex(this.tenantUID,this.beginningTime,this.endingTime,this.rankMap,this.isZkbadmin);
    }

    /**
     * 生成报表
     */
    public void generateCharts()
    {
        Date date = new Date();
        Date beginningTimeTemp = this.beginningTime;
        Date endingTimeTimeTemp = this.endingTime;
        if(beginningTimeTemp==null)
        {
            beginningTimeTemp=this.getYearOfFirstDay(date);
        }
        if(endingTimeTimeTemp==null)
        {
            endingTimeTimeTemp=this.zkbKeyBehaviorIndexService.getLastDay(date);
        }
        this.lineModel = this.zkbKeyBehaviorIndexService.generateCharts(tenantUID,beginningTimeTemp,endingTimeTimeTemp,this.isZkbadmin);
    }

    /**
     * 导出租户信息
     */
    public void exportTenantInfo()
    {
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(ZkbKeyBehaviorIndexService.excelTenant);
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbKeyBehaviorIndexService.exportTenantData(doc,this.tenantInfoList);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "Generate_KPI_DATA.xlsx");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ZkbPartOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 生效
     */
    public void effect()
    {
        this.zkbKeyBehaviorIndexService.effect(this.beginningTime,this.tenantUID,currentUser,this.zkbKeyBehaviorIndexList);
        WebUtil.addSuccessMessage("生效成功！");
    }

    /**
     * 获取供应商名称
     * @return
     */
    public String getTenantName()
    {
        StringBuffer name = new StringBuffer("");
        if(CollectionUtils.isEmpty(this.selectTenantInfoList))
            return name.toString();
        for(TenantInfo tenantInfo:selectTenantInfoList)
        {
            name.append(tenantInfo.getName()).append("；");
        }
        return name.substring(0,name.length()-1);
    }

    /**
     * 生成报表
     */
    public void generateKpiData()
    {
        if(CollectionUtils.isEmpty(selectTenantInfoList))
        {
            WebUtil.addErrorMessage("请选择代理商");
            return;
        }
        for(TenantInfo item:selectTenantInfoList) {
            this.zkbKeyBehaviorIndexService.generateSingleKpiData(item,this.kpiDate);
        }
        WebUtil.addSuccessMessage("生成成功");
    }

    private Date getYearOfFirstDay(final Date date)
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

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        ExcelDocument doc = null;
        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            int maxRowNum = ZkbKeyBehaviorIndexService.EXCEL_KBI_HEADER_ROW + ZkbKeyBehaviorIndexService.EXCEL_KBI_IMPORT_MAX_ROW;
            if(doc.checkMaxRowNumIsFull(ZkbKeyBehaviorIndexService.excelSheetNameOfTenant, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel10000"));
                return;
            }
            List<Map<String, Object>> importTenantList = this.zkbKeyBehaviorIndexService.getTenantMapFromTemplate(doc);
            this.zkbKeyBehaviorIndexService.importTenantData(importTenantList);
            errorKbiList = this.zkbKeyBehaviorIndexService.filterErrorRecords(importTenantList);
            if(!errorKbiList.isEmpty()){
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


    /**
     * 计算得分
     */
    public void calculateScore()
    {
        this.zkbKeyBehaviorIndexService.calculateScore(this.selected,this.selected.getTenantUID(),this.zkbKeyBehaviorIndexService.getLastDay(this.selected.getKbiDate()),this.zkbKeyBehaviorIndexService.getMonthOfFirstDay(this.selected.getKbiDate()));
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
     * 跳转导入页面
     * @return
     */
    public String reGenerateDate() {
        return "GenerateKpiData?faces-redirect=true";
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
        String fieldThanAnotherFieldErro = WebUtil.getMessage("zkbKeyBehaviorIndex_fieldThanAnotherFieldErro");
        boolean ok = true;
        if(!this.compareDate())
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyBehaviorIndex_cannotChangeError"));
            return;
        }
        if(ZkbKeyBehaviorIndexService.totalFullScore!=this.selected.getTotalFullScore())
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyBehaviorIndex_totalFullScoreError"));
            ok=false;
        }
        if(this.selected.getTotalScore()>ZkbKeyBehaviorIndexService.totalFullScore)
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyBehaviorIndex_totalScoreError"));
            ok=false;
        }
        if(this.selected.getSatisfactoryDegreeScore()>this.selected.getSatisfactoryDegreeFullScore())
        {
            String satisfactoryDegreeFullScore = WebUtil.getMessage("zkbKeyBehaviorIndex_satisfactoryDegreeFullScore");
            String satisfactoryDegreeScore = WebUtil.getMessage("zkbKeyBehaviorIndex_satisfactoryDegreeScore");
            WebUtil.addErrorMessage(satisfactoryDegreeScore+fieldThanAnotherFieldErro+satisfactoryDegreeFullScore);
            ok=false;
        }
        if(this.selected.getComplianceScore()>this.selected.getComplianceFullScore())
        {
            String complianceFullScore = WebUtil.getMessage("zkbKeyBehaviorIndex_complianceFullScore");
            String complianceScore = WebUtil.getMessage("zkbKeyBehaviorIndex_complianceScore");
            WebUtil.addErrorMessage(complianceScore+fieldThanAnotherFieldErro+complianceFullScore);
            ok=false;
        }
        if(this.selected.getEngineerScore()>this.selected.getEngineerFullScore())
        {
            String engineerFullScore = WebUtil.getMessage("zkbKeyBehaviorIndex_engineerFullScore");
            String engineerScore = WebUtil.getMessage("zkbKeyBehaviorIndex_engineerScore");
            WebUtil.addErrorMessage(engineerScore+fieldThanAnotherFieldErro+engineerFullScore);
            ok=false;
        }
        if(!ok)
        {
            return;
        }
        super.save();
        if(ZkbKeyBehaviorIndexService.yes.equals(this.selected.getEffect()))
        {
            this.zkbKeyBehaviorIndexService.changeScore(this.selected,kpi_modify,this.currentUser);
        }
        this.selected = null;
        this.rankMap = this.zkbKeyBehaviorIndexService.initRankingMap();
        this.zkbKeyBehaviorIndexService.rankZkbKeyBehaviorIndexList(this.zkbKeyBehaviorIndexList,this.rankMap);
    }

    public boolean compareDate()
    {
        Calendar cd = Calendar.getInstance();
        cd.setTime(this.selected.getKbiDate());
        cd.add(Calendar.MONTH,1);
        Date date = new Date();
        if( DateUtil.isSameMonth(date,cd.getTime()))
        {
            return true;
        }
        return false;
    }

    public void cancelSelectTenantInfoList()
    {
        this.selectTenantInfoList= null;
    }

    public void cancel() {
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

    public Date getKpiDate() {
        return kpiDate;
    }

    public void setKpiDate(Date kpiDate) {
        this.kpiDate = kpiDate;
    }

    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }

    public List<TenantInfo> getSelectTenantInfoList() {
        return selectTenantInfoList;
    }

    public void setSelectTenantInfoList(List<TenantInfo> selectTenantInfoList) {
        this.selectTenantInfoList = selectTenantInfoList;
    }

}
