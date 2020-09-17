package com.ge.apm.view.asset;

import com.ge.apm.dao.ZkbOrderHistoryRepository;
import com.ge.apm.domain.ZkbOrderHistory;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbOrderHistoryService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author 503202755 2020/9/3
 */
@ManagedBean
@ViewScoped
public class ZkbOrderHistoryController extends GenericCRUDUUIDController<ZkbOrderHistory> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbOrderHistoryController.class);

    /**
     * 智康宝历史订单service
     */
    private ZkbOrderHistoryService zkbOrderHistoryService;

    /**
     * 智康宝历史订单dao
     */
    private ZkbOrderHistoryRepository zkbOrderHistoryRepository;

    /**
     * 错误列表
     */
    private List<Map<String, Object>> errorOrderHistoryList;

    /**
     * 列表
     */
    private List<ZkbOrderHistory> zkbOrderHistoryList;

    /**
     * 数据类型
     * 1.DI、PCB
     * 2.US、Biomed
     * 3.LCS
     * 4.US探头
     */
    private String dataType;

    /**
     * 导出文件
     */
    private StreamedContent exportFile;

    /**
     * 合同开始时间
     */
    private Date contractBeginningTime;
    /**
     *合同结束时间
     */
    private Date contractEndingTime;

    /**
     * 搜索文本框
     */
    private String searchStr;

    /**
     *地区
     */
    private String region;
    /**
     *代理商
     */
    private String agent;
    /**
     *合同号
     */
    private String contractNo;
    /**
     *合同类型
     */
    private String contractType;

    @Override
    protected void init() {
        super.init();
        zkbOrderHistoryService = WebUtil.getBean(ZkbOrderHistoryService.class);
        zkbOrderHistoryRepository = WebUtil.getBean(ZkbOrderHistoryRepository.class);
        loadData();

    }
    /**
     * 导入文件
     */
    private UploadedFile file;

    /**
     * 文件上传
     * @param event  文件流
     */
    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;
        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            int maxRowNum = ZkbOrderHistoryService.EXCEL_ORDER_HISTORY_HEADER_ROW + ZkbOrderHistoryService.EXCEL_ORDER_HISTORY_IMPORT_MAX_ROW;
            if(doc.exceedMaxRowNum(ZkbOrderHistoryService.SHEET_SERVICE_ORDER_HISTORY, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel"));
                return;
            }
            List<Map<String, Object>> importDirectoryMapList = zkbOrderHistoryService.getOrderHistoryMapFromTemplate(doc);
            zkbOrderHistoryService.importData(importDirectoryMapList);
            errorOrderHistoryList = zkbOrderHistoryService.filterErrorRecords(importDirectoryMapList);
            if(!errorOrderHistoryList.isEmpty()){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, WebUtil.getMessage("ImportCompletedNoteFailureRecord"), WebUtil.getMessage("ThereAreATotalOf")+errorOrderHistoryList.size()+WebUtil.getMessage("RecordsOfFailure"));
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
     * 根据页面条件查询
     */
    public void loadData() {
        zkbOrderHistoryList=zkbOrderHistoryService.loadZkbOrderHistoryData(dataType,
                this.contractBeginningTime,
                this.contractEndingTime,
                this.region,
                this.agent,
                this.contractNo,
                this.contractType,
                this.searchStr);
    }

    /**
     * 文件下载
     * @return
     */
    public String getTemplateName(){
        UserContextService ucs = WebUtil.getBean(UserContextService.class);
        return ucs.isLocale() == true?ZkbOrderHistoryService.EXCEL_TEMPLATE_NAME:ZkbOrderHistoryService.EXCEL_TEMPLATE_EN_NAME;
    }

    /**
     * 跳转导入页面
     * @return
     */
    public String importAction() {
        return "OrderHistoryImport?faces-redirect=true";
    }

    @Override
    protected GenericRepositoryUUID<ZkbOrderHistory> getDAO() {
        return zkbOrderHistoryRepository;
    }

    public List<Map<String, Object>> getErrorOrderHistoryList() {
        return errorOrderHistoryList;
    }

    public void setErrorOrderHistoryList(List<Map<String, Object>> errorOrderHistoryList) {
        this.errorOrderHistoryList = errorOrderHistoryList;
    }

    public List<ZkbOrderHistory> getZkbOrderHistoryList() {
        return zkbOrderHistoryList;
    }

    public void setZkbOrderHistoryList(List<ZkbOrderHistory> zkbOrderHistoryList) {
        this.zkbOrderHistoryList = zkbOrderHistoryList;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * 数据导出
     */
    public void exportAll(){
        List<Map<String, Object>> orderHistoryMapList = zkbOrderHistoryService.createExportOrderHistoryData(this.zkbOrderHistoryList);
        try {
            UserContextService ucs = WebUtil.getBean(UserContextService.class);
            String fileName =  ucs.isLocale() == true ?ZkbOrderHistoryService.EXCEL_TEMPLATE_NAME:ZkbOrderHistoryService.EXCEL_TEMPLATE_EN_NAME;
            String path = ZkbOrderHistoryService.DIRECOTORY_PATH+fileName;
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(path);
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbOrderHistoryService.exportData(doc,orderHistoryMapList);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", fileName);
        } catch (IOException ex) {
            LOGGER.info(ex.getMessage());
        }
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    /**
     * 删除
     */
    @Override
    public void delete() {
        super.delete();
        this.loadData();
    }

    public Date getContractBeginningTime() {
        return contractBeginningTime;
    }

    public void setContractBeginningTime(Date contractBeginningTime) {
        this.contractBeginningTime = contractBeginningTime;
    }

    public Date getContractEndingTime() {
        return contractEndingTime;
    }

    public void setContractEndingTime(Date contractEndingTime) {
        this.contractEndingTime = contractEndingTime;
    }

    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }
}
