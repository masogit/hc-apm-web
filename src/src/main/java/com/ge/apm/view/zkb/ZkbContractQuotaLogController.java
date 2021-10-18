package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbContractQuotaLogRepository;
import com.ge.apm.domain.ZkbContractQuotaLog;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbContractQuotaLogService;
import com.ge.apm.service.zkb.ZkbIbCrossService;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author : 503189362
 * @date : 2021/6/8
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbContractQuotaLogController  extends GenericCRUDUUIDController<ZkbContractQuotaLog> {

    private ZkbContractQuotaLogRepository zkbContractQuotaLogRepository;
    private ZkbContractQuotaLogService zkbContractQuotaLogService;
    private Page<ZkbContractQuotaLog> zkbContractQuotaLogPage;
    private ZkbIbCrossService zkbIbCrossService;

    @Override
    protected void init() {
        super.init();
        zkbContractQuotaLogRepository = WebUtil.getBean(ZkbContractQuotaLogRepository.class);
        zkbContractQuotaLogService = WebUtil.getBean(ZkbContractQuotaLogService.class);
        zkbIbCrossService = WebUtil.getBean(ZkbIbCrossService.class);
    }

    @Override
    protected Page<ZkbContractQuotaLog> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        zkbContractQuotaLogPage = zkbContractQuotaLogRepository.findBySearchFilter(searchFilters, pageRequest);
        return zkbContractQuotaLogPage;
    }
    private StreamedContent exportFile;
    public void exportAll(){
        List<SearchFilter> searchFilters = this.searchFilters;
        List<Map<String, Object>> zkbLogDataList = zkbContractQuotaLogService.createZkbEngineerData(searchFilters);
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/zkb/contractQuotaLog/APM_ZKB_Contract_quota_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbContractQuotaLogService.exportData(doc, zkbLogDataList);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "quotaLog.xlsx");
        } catch (IOException ex) {
            Logger.getLogger(ZkbEngineerController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void test(){
        zkbIbCrossService.handleIbCrossJob();
    }

    public void qRCodeJobTest(){

    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    @Override
    protected GenericRepositoryUUID<ZkbContractQuotaLog> getDAO() {
        return zkbContractQuotaLogRepository;
    }
}
