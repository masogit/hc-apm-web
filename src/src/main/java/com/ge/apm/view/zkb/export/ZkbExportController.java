package com.ge.apm.view.zkb.export;

import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbOrderHistory;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbExportOrderService;
import com.ge.apm.service.zkb.ZkbOrderHistoryService;
import com.ge.apm.service.zkb.ZkbPartOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.zkb.ZkbPartOrderController;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
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
import java.util.logging.Level;


/**
 * 一键导出
 */
@ManagedBean
@ViewScoped
public class ZkbExportController extends GenericCRUDUUIDController {

    /**
     * 代理商
     */
    private List<TenantInfo> tenantInfoList;

    /**
     * FE 订单
     */
    private ZkbPartOrderService zkbPartOrderService;

    /**
     * 导出
     */
    private ZkbExportOrderService zkbExportOrderService;

    /**
     * 导出的文件
     */
    private StreamedContent exportFile;

    /**
     * 租户UID
     */
    private String tenantUid;

    /**
     * 判断用户admin service
     */
    private UserContextService userContextService;

    /**
     * 智康宝历史订单dao
     */
    private ZkbOrderHistoryService zkbOrderHistoryService;

    /**
     * 当前登录用户信息service
     */
    private UserAccount currentUser;

    /**
     * 导出excel路径
     */
    private static final String excelPath = "/portal/zkb/export/Order_History_And_Part_Order_Export.xlsx";

    @Override
    protected void init() {
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        zkbExportOrderService = WebUtil.getBean(ZkbExportOrderService.class);
        tenantInfoList = zkbPartOrderService.getSourceTenantInfoList();
        userContextService = WebUtil.getBean(UserContextService.class);
        zkbOrderHistoryService = WebUtil.getBean(ZkbOrderHistoryService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        if(!(userContextService.isGeAdmin()||
                userContextService.isZkbFeUserConfig("FourHundredsTeam_Probe")
                ||userContextService.isZkbFeUserConfig("FourHundredsTeam")))
        {
            this.tenantUid=currentUser.getTenantUID();
        }
        super.init();
    }

    /**
     * 导出需要
     */
    public void export()
    {
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(excelPath);
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            List<Map<String, Object>> fePartOrderList = this.zkbExportOrderService.getZkbFePartOrder(tenantUid);
            List<Map<String, Object>> partOrderList = this.zkbExportOrderService.getZkbPartOrder(tenantUid);
            List<ZkbOrderHistory> zkbOrderHistoryList = zkbOrderHistoryService.findByTenantUid(tenantUid);
            List<Map<String, Object>> orderHistoryMapList = zkbOrderHistoryService.createExportOrderHistoryData(zkbOrderHistoryList,this.userContextService);
            zkbExportOrderService.exportData(doc, fePartOrderList,partOrderList,orderHistoryMapList);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "Order_History_And_Part_Order_Export.xlsx");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ZkbPartOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    protected GenericRepositoryUUID getDAO() {
        return null;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }
}










