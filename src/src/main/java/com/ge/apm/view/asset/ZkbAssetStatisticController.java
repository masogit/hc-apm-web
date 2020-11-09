package com.ge.apm.view.asset;

import com.ge.apm.domain.ZkbAssetStatistic;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbAssetStatisticService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author 503202755 2020/10/13
 */
@ManagedBean
@ViewScoped
public class ZkbAssetStatisticController extends GenericCRUDUUIDController<ZkbAssetStatistic> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbAssetStatisticController.class);

    /**
     * 智康宝历史订单service
     */
    private ZkbAssetStatisticService zkbAssetStatisticService;

    /**
     * 列表
     */
    private List<ZkbAssetStatistic> zkbAssetStatisticList;

    /**
     * 租户UID
     */
    private String tenantUid;

    /**
     * SystemID/厂商报修ID
     */
    private String systemId;

    /**
     * 二维码个数
     */
    private String qrCodeCount;

    /**
     * 二维码状态
     */
    private String qrCodeCountState;

    /**
     * 导出文件
     */
    private StreamedContent exportFile;

    @Override
    protected void init() {
        super.init();
        zkbAssetStatisticService = WebUtil.getBean(ZkbAssetStatisticService.class);
        loadData();
    }

    /**
     * 根据页面条件查询
     */
    public void loadData() {
        zkbAssetStatisticList=this.zkbAssetStatisticService.loadAllAsset(this.tenantUid,this.systemId,this.qrCodeCount,this.qrCodeCountState);
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    @Override
    protected GenericRepositoryUUID<ZkbAssetStatistic> getDAO() {
        return null;
    }

    public List<ZkbAssetStatistic> getZkbAssetStatisticList() {
        return zkbAssetStatisticList;
    }

    public void setZkbAssetStatisticList(List<ZkbAssetStatistic> zkbAssetStatisticList) {
        this.zkbAssetStatisticList = zkbAssetStatisticList;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    /**
     * 数据导出
     */
    public void exportAll(){
        List<ZkbAssetStatistic> allZkbAssetStatisticList = this.zkbAssetStatisticService.loadAllAsset(null,null,null,null);
        List<Map<String, Object>> zkbAssetStatisticMapList = zkbAssetStatisticService.createZkbAssetStatisticData(allZkbAssetStatisticList);
        try {
            UserContextService ucs = WebUtil.getBean(UserContextService.class);
            String fileName =  ucs.isLocale() == true ? ZkbAssetStatisticService.EXCEL_TEMPLATE_NAME:ZkbAssetStatisticService.EXCEL_TEMPLATE_EN_NAME;
            String path = ZkbAssetStatisticService.DIRECOTORY_PATH+fileName;
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(path);
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbAssetStatisticService.exportData(doc,zkbAssetStatisticMapList);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", fileName);
        } catch (IOException ex) {
            LOGGER.info(ex.getMessage());
        }
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    public String getQrCodeCount() {
        return qrCodeCount;
    }

    public void setQrCodeCount(String qrCodeCount) {
        this.qrCodeCount = qrCodeCount;
    }

    public String getQrCodeCountState() {
        return qrCodeCountState;
    }

    public void setQrCodeCountState(String qrCodeCountState) {
        this.qrCodeCountState = qrCodeCountState;
    }
}
