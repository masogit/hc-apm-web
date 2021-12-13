package com.ge.apm.view.asset;

import com.ge.apm.dao.ZkbAssetQrCodeInfoRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbAssetQrCodeInfo;
import com.ge.apm.domain.ZkbAssetStatistic;
import com.ge.apm.domain.ZkbFePartOrder;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbAssetStatisticService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 503202755 2020/10/13
 */
@ManagedBean
@ViewScoped
public class ZkbAssetStatisticController extends GenericCRUDUUIDController<ZkbAssetQrCodeInfo> {

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
     * 医院名称
     */
    private String hospitalName;

    /**
     * 设备型号
     */
    private String functionType;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 二维码个数
     */
    private String qrCodeCount;

    /**
     * 二维码状态
     */
    private String qrCodeCountState;

    /**
     * 二维码状态
     */
    private String canNotPastQrCode;

    /**
     * 导出文件
     */
    private StreamedContent exportFile;
    /**
     * 当前登录用户信息service
     */
    private UserAccount currentUser;
    /**
     * 判断用户admin service
     */
    private UserContextService userContextService;

    private ZkbAssetQrCodeInfoRepository zkbAssetQrCodeInfoRepository;

    @Override
    protected void init() {
        super.init();
        zkbAssetStatisticService = WebUtil.getBean(ZkbAssetStatisticService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        zkbAssetQrCodeInfoRepository=WebUtil.getBean(ZkbAssetQrCodeInfoRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        if(!userContextService.isZkbAdmin())
        {
            String tenantUID = currentUser.getTenantUID();
            if(StringUtils.isNotEmpty(tenantUID))
            {
                this.tenantUid=tenantUID;
            }else
            {
                this.tenantUid="-";
            }
        }
//        loadData();
    }

    /**
     * 根据页面条件查询
     */
    public void loadData() {
        zkbAssetStatisticList=this.zkbAssetStatisticService.loadAllAsset(this.tenantUid,this.systemId,this.qrCodeCount,this.qrCodeCountState,this.contractNo,this.hospitalName,this.functionType,this.canNotPastQrCode);
    }

    /**
     * 列表加载
     *
     * @param pageRequest
     * @return
     */
    @Override
    protected Page<ZkbAssetQrCodeInfo> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = getSearchFilters();
        Page<ZkbAssetQrCodeInfo> ZkbAssetQrCodeInfo = zkbAssetQrCodeInfoRepository.findBySearchFilter(searchFilters, pageRequest);
        return ZkbAssetQrCodeInfo;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    @Override
    protected GenericRepositoryUUID<ZkbAssetQrCodeInfo> getDAO() {
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
        String tenantUid = null;
        if(!userContextService.isZkbAdmin())
        {
            tenantUid=this.tenantUid;
        }
        List<ZkbAssetStatistic> allZkbAssetStatisticList = this.zkbAssetStatisticService.loadAllAsset(tenantUid,null,null,null,null,null,null,null);
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

    @Override
    public void save() {
        if(selected.getReason() != null){
            selected.setReasonDate(new Date());
        }
        zkbAssetQrCodeInfoRepository.save(selected);
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

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getCanNotPastQrCode() {
        return canNotPastQrCode;
    }

    public void setCanNotPastQrCode(String canNotPastQrCode) {
        this.canNotPastQrCode = canNotPastQrCode;
    }

    /**
     * 重新生成二维码统计数据
     */
    public String regenerateZkbAssetQrCodeInfo(){
        zkbAssetStatisticService.regenerateZkbAssetQrCodeInfo(getSearchFilters());
        return "List.xhtml?faces-redirect=true";
    }

    public List<SearchFilter> getSearchFilters(){
        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
        if(tenantUid!=null)
        {
            SearchFilter dataTypeFilter = new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUid);
            searchFilters.add(dataTypeFilter);
        }
        if(systemId!=null)
        {
            SearchFilter systemIdFilter = new SearchFilter("systemId", SearchFilter.Operator.EQ, systemId);
            searchFilters.add(systemIdFilter);
        }
        if(contractNo!=null)
        {
            SearchFilter systemIdFilter = new SearchFilter("contractNo", SearchFilter.Operator.EQ, contractNo);
            searchFilters.add(systemIdFilter);
        }
        if(StringUtils.isNotBlank(hospitalName))
        {
            SearchFilter systemIdFilter = new SearchFilter("hospitalName", SearchFilter.Operator.EQ, hospitalName);
            searchFilters.add(systemIdFilter);
        }
        if(StringUtils.isNotBlank(functionType))
        {
            SearchFilter systemIdFilter = new SearchFilter("functionType", SearchFilter.Operator.EQ, functionType);
            searchFilters.add(systemIdFilter);
        }
        if(StringUtils.isNotBlank(qrCodeCountState))
        {
            SearchFilter systemIdFilter = new SearchFilter("qrCodeCountState", SearchFilter.Operator.EQ, qrCodeCountState);
            searchFilters.add(systemIdFilter);
        }
        if(StringUtils.isNotBlank(canNotPastQrCode))
        {
            SearchFilter systemIdFilter = new SearchFilter("canNotPastQrCode", SearchFilter.Operator.EQ, canNotPastQrCode);
            searchFilters.add(systemIdFilter);
        }
        return searchFilters;
    }
}
