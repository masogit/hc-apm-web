package com.ge.apm.view.zkb.fePartOrder;

import com.ge.apm.dao.ZkbFePartOrderRepository;
import com.ge.apm.dao.ZkbFePartOrderUserConfigRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbFePartOrder;
import com.ge.apm.domain.ZkbFePartOrderUserConfig;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.zkb.ZkbFePartOrderService;
import com.ge.apm.view.asset.ContractImportController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.zkb.ZkbPartOrderController;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author 503202755 2020/9/23
 */
@ManagedBean
@ViewScoped
public class ZkbFePartOrderController extends GenericCRUDUUIDController<ZkbFePartOrder> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbFePartOrderController.class);

    /**
     * 智康宝人工申请 service
     */
    private ZkbFePartOrderService zkbFePartOrderService;

    /**
     * 当前登录用户信息service
     */
    private UserAccount currentUser;

    /**
     *智康宝人工申请 reporsitory
     */
    private ZkbFePartOrderRepository zkbFePartOrderRepository;

    /**
     * 获取审批角色配置信息
     */
    private ZkbFePartOrderUserConfigRepository zkbFePartOrderUserConfigRepository;

    /**
     * 目录关系分页数据
     */
    private Page<ZkbFePartOrder> zkbFePartOrderPage;

    public final static String CREATE_ACTION = "Create";

    public final static String EDIT_ACTION="Edit";

    /**
     * 查询哪个选项卡状态
     */
    private String queryStatus = MY_WORK;

    /**
     * 我的待办
     */
    private static final String MY_WORK="myWork";

    /**
     * 全部
     */
    private static final String ALL="all";

    /**
     * 待上传探头说明函
     */
    private static final String waitingUpload = "waitingUpload";

    /**
     * 没权限查看
     */
    private static final String NO_STEP="noStep";

    /**
     * 当前用户登录名
     */
    private String loginName = "";

    /**
     * 当前用户tenantUid
     */
    private String tenantUID="";

    /**
     * 当前用户在config中的配置权限
     */
    private Map<String, String> configTypeMap = new HashMap<>();

    /**
     * 判断是否400团队
     */
    private Boolean fourHundredsTeam = false;

    private Boolean geAdmin = false;

    private List<String> orderTypeList = new ArrayList<>();

    /**
     * 需要上传探头说明函值
     */
    private static final String needUploadLetterValue = "t";

    /**
     * 没有上传探头说明函值
     */
    private static final String hasNotUploadLetterValue = "f";

    /**
     * 已经上传探头说明函值
     */
    public static final String hasUploadLetterValue = "t";

    /**
     * 导出excel路径
     */
    private static final String excelPath = "/portal/zkb/zkbFePartOrder/excel/APM_ZKB_FePartOrder_Exp_Template.xlsx";

    /**
     * 导出excel路径
     */
    private static final String excelReturnPath = "/portal/zkb/zkbFePartOrder/excel/APM_ZKB_FePartOrder_Batch_Return_Template.xlsx";

    private StreamedContent exportFile;

    /**
     * 上传的附件
     */
    private UploadedFile file;

    /**
     * 是否休假
     */
    private String isVacation;

    /**
     * 错误列表
     */
    private List<Map<String, Object>> errorZkbFePartList;

    /**
     * 当前用户称
     */
    private String userName;

    /**
     * 当前用户UID
     */
    private String userUid;

    Map<String,String> map = null;

    /**
     * 初始化数据
     */
    @Override
    protected void init() {
        super.init();
        zkbFePartOrderService = WebUtil.getBean(ZkbFePartOrderService.class);
        zkbFePartOrderRepository = WebUtil.getBean(ZkbFePartOrderRepository.class);
        zkbFePartOrderUserConfigRepository = WebUtil.getBean(ZkbFePartOrderUserConfigRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.loginName=currentUser.getLoginName();
        this.queryStatus = WebUtil.getRequestParameter("queryStatus");
        this.tenantUID=currentUser.getTenantUID();
        this.userName=currentUser.getName();
        this.userUid=currentUser.getUid();
        if(StringUtils.isEmpty(this.queryStatus)){
            this.queryStatus = MY_WORK;
        }
        configTypeMap=this.getMapConfigByLoginName();
        if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString()))
        {
            fourHundredsTeam=true;
        }else if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam_Probe.toString()))
        {
            fourHundredsTeam=true;
        }
        if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString()))
        {
            this.geAdmin=true;
        }
        orderTypeList.add(ZkbFePartOrder.OrderType.ManMade.toString());
        orderTypeList.add(ZkbFePartOrder.OrderType.SpareParts.toString());
    }


    /**
     * 列表加载
     * @param pageRequest
     * @return
     */
    @Override
    protected Page<ZkbFePartOrder> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = getSearchFilter(this.searchFilters);
        zkbFePartOrderPage=zkbFePartOrderRepository.findBySearchFilter(searchFilters, pageRequest);
        return zkbFePartOrderPage;
    }

    /**
     * 动态组装列表过滤条件
     * @param csearchFilters
     * @return
     */
    public List<SearchFilter> getSearchFilter(List<SearchFilter> csearchFilters) {
        List<SearchFilter> searchFilters = csearchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        if(StringUtils.isNotBlank(this.loginName)&&this.loginName.startsWith(ZkbFePartOrderService.testLoginNameStartWith))
        {
            searchFilters.add((new SearchFilter("testStatus", SearchFilter.Operator.EQ,ZkbFePartOrderService.testStatusTrueValue)));
        }else
            {
                searchFilters.add((new SearchFilter("testStatus", SearchFilter.Operator.EQ,ZkbFePartOrderService.testStatusFalseValue)));
            }
        //判断是不是我的待办选项卡
        if(MY_WORK.equals(this.queryStatus))
        {
            this.getSearchFilterByUserConfig(searchFilters);
        }else if(waitingUpload.equals(this.queryStatus))
        {
            this.getSearchFilterByUploadLetter(searchFilters);
        }else{
            this.getAllSearchFilterByUserConfig(searchFilters);
            if(StringUtils.isNotBlank(this.queryStatus)&&!ALL.equals(this.queryStatus))
            {
                searchFilters.add((new SearchFilter("currentStep", SearchFilter.Operator.EQ,queryStatus)));
            }
        }
        return searchFilters;
    }

    public boolean hasLetterViewRight()
    {
        return this.zkbFePartOrderService.hasLetterViewRight(configTypeMap);
    }

    /**
     * 根据配置信息动态组装SearchFilter过滤条件
     * @param searchFilters
     * @return
     */
    private void getSearchFilterByUserConfig(List<SearchFilter> searchFilters)
    {
        if(!(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString())
                ||configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString())
                ||configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam_Probe.toString())
                ||configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString())))
        {
            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ,NO_STEP));
            return;
        }
        if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString()))
        {
            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ,ZkbFePartOrder.workFlowSteps.WaitingApm.toString()));
            searchFilters.add(new SearchFilter("orderType", SearchFilter.Operator.IN,orderTypeList));
        }else if((configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam_Probe.toString())))
        {
            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ,ZkbFePartOrder.workFlowSteps.WaitingApm.toString()));
            searchFilters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ,ZkbFePartOrder.OrderType.Probe.toString()));
        }else if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString()))
        {
            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ,ZkbFePartOrder.workFlowSteps.WaitingAssignment.toString()));
            searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ,this.tenantUID));
        }else if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString()))
        {
            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ,ZkbFePartOrder.workFlowSteps.WaitingGEAdmin.toString()));
        }
    }

    /**
     *
     * @param searchFilters
     */
    private void getSearchFilterByUploadLetter(List<SearchFilter> searchFilters)
    {
        searchFilters.add(new SearchFilter("needLetterFlag", SearchFilter.Operator.EQ,needUploadLetterValue));
        searchFilters.add(new SearchFilter("hasUploadLetter", SearchFilter.Operator.EQ,hasNotUploadLetterValue));
        if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString()))
        {
            searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ,this.tenantUID));
            return;
        }
    }

    /**
     * 根据登录账户名获取当前账户名流程配置信息
     * @return
     */
    private Map<String, String> getMapConfigByLoginName()
    {
        Map<String, String> configTypeMap = new HashMap<>();
        List<ZkbFePartOrderUserConfig> list = this.zkbFePartOrderUserConfigRepository.findByLoginName(this.loginName);
        if(CollectionUtils.isEmpty(list))
        {
            return configTypeMap;
        }
        for(ZkbFePartOrderUserConfig item:list)
        {
            if(item==null) continue;
            if(configTypeMap.containsKey(item.getConfigType())) continue;
            configTypeMap.put(item.getConfigType(),item.getConfigType());
        }
        return configTypeMap;
    }

    /**
     * 根据配置信息动态组装SearchFilter过滤条件
     * @param searchFilters
     * @return
     */
    private void getAllSearchFilterByUserConfig(List<SearchFilter> searchFilters)
    {
        if(!(this.configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString())
                ||configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString())
                ||configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam_Probe.toString())
                ||configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString())))
        {
            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ,NO_STEP));
            return;
        }
        if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString()))
        {
            searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ,this.tenantUID));
        }
    }

    public void saveVacation(){
        List<ZkbFePartOrderUserConfig> zkbFePartOrderUserConfigList = this.zkbFePartOrderUserConfigRepository.findByLoginName(this.loginName);
        if(CollectionUtils.isEmpty(zkbFePartOrderUserConfigList))
        {
            return;
        }
        for(ZkbFePartOrderUserConfig c : zkbFePartOrderUserConfigList){
            c.setIsVacation(isVacation);
            this.zkbFePartOrderUserConfigRepository.save(c);
        }
    }

    public String getI18(String key,String orderType)
    {
        if(this.map==null)
        {
            this.map = this.zkbFePartOrderService.getI18ToMapEngKey(ZkbFePartOrderService.status);
        }
        if(ZkbFePartOrder.workFlowStatus.FourHundredsAccept.toString().equals(key)&&ZkbFePartOrder.OrderType.ManMade.toString().equals(orderType))
        {
            return "400已开单";
        }
        return this.map.get(key);
    }


    @Override
    protected GenericRepositoryUUID<ZkbFePartOrder> getDAO() {
        return this.zkbFePartOrderRepository;
    }

    /**
     * 跳转新增模式
     * @return
     */
    public String intoCreateModel() {
        return UrlEncryptController.encodeUrlParam("Edit?action="+ZkbFePartOrderController.CREATE_ACTION+"&id="+"&queryStatus="+queryStatus);
    }

    /**
     * 跳转编辑模式
     * @return
     */
    public String intoEditModel() {
        return UrlEncryptController.encodeUrlParam("Edit?action="+ZkbFePartOrderController.EDIT_ACTION+"&id="+this.selected.getId()+"&queryStatus="+queryStatus);
    }

    public String queryListByStatus(String status){
        queryStatus = status;
        return "List.xhtml?faces-redirect=true&queryStatus="+queryStatus;
    }

    /**
     * 跳转导入页面
     * @return
     */
    public String importAction() {
        return "Import?faces-redirect=true";
    }

    public void doubleClick(SelectEvent event){
        String str = "action="+ZkbFePartOrderController.EDIT_ACTION+"&id="+this.selected.getId()+"&queryStatus="+queryStatus;
        String url = "Edit.xhtml?str="+UrlParamUtil.encodeUrlQueryString(str);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public void exportAll(){
        List<SearchFilter> searchFilters = getSearchFilter(this.searchFilters);
        Sort sort = new Sort(Sort.Direction.DESC, "orderType").and(new Sort(Sort.Direction.DESC, "reportDate"));
        List<ZkbFePartOrder> zkbFePartOrderList =  this.zkbFePartOrderRepository.findBySearchFilter(searchFilters,sort);
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(excelPath);
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            HashMap<String,Object> dataMap = this.zkbFePartOrderService.createZkbFePartOrderData(zkbFePartOrderList);
            zkbFePartOrderService.exportData(doc, dataMap);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "ZkbPartOrderExcel_Export.xlsx");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ZkbPartOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        ExcelDocument doc = null;
        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            int maxRowNum = ZkbFePartOrderService.EXCEL_FE_PART_ORDER_HEADER_ROW + ZkbFePartOrderService.EXCEL_FE_PART_ORDER_IMPORT_MAX_ROW;
            if(doc.checkMaxRowNumIsFull(ZkbFePartOrderService.excelSheetNameOfProbeReturn, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel10000"));
                return;
            }
            if(doc.checkMaxRowNumIsFull(ZkbFePartOrderService.excelSheetNameOfSparePartReturn, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel10000"));
                return;
            }
            Map<String,String> i18Map = this.zkbFePartOrderService.getI18ToMap("zkbFePartOrder_zkbPoReturnType");
            List<Map<String, Object>> importSparePartList = this.zkbFePartOrderService.getZkbFePartOrderSparePartMapFromTemplate(doc,i18Map);
            List<Map<String, Object>> importProbeList = this.zkbFePartOrderService.getZkbFePartOrderProbeMapFromTemplate(doc,i18Map);
            this.zkbFePartOrderService.importData(importSparePartList,importProbeList,this.userUid,this.userName,this.tenantUID,this.currentUser);
            errorZkbFePartList = this.zkbFePartOrderService.filterErrorRecords(importSparePartList,importProbeList);
            if(!errorZkbFePartList.isEmpty()){
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
     * 导出需要
     */
    public void exportNeedReturn()
    {
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(excelReturnPath);
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbFePartOrderService.exportReturnData(doc);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "ZkbPartOrderExcel_Export.xlsx");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ZkbPartOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getQueryStatus() {
        return queryStatus;
    }

    public void setQueryStatus(String queryStatus) {
        this.queryStatus = queryStatus;
    }

    public Boolean getFourHundredsTeam() {
        return fourHundredsTeam;
    }

    public void setFourHundredsTeam(Boolean fourHundredsTeam) {
        fourHundredsTeam = fourHundredsTeam;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public String getIsVacation() {
        List<ZkbFePartOrderUserConfig> list = this.zkbFePartOrderUserConfigRepository.findByLoginName(this.loginName);
        if(CollectionUtils.isEmpty(list))
        {
            return "f";
        }
        for(ZkbFePartOrderUserConfig item:list)
        {
            if(item==null) continue;
            if(StringUtils.isBlank(item.getIsVacation()))
            {
                isVacation="f";
                continue;
            }
            isVacation=item.getIsVacation();

        }
        return isVacation;
    }

    public void setIsVacation(String isVacation) {
        this.isVacation = isVacation;
    }

    public List<Map<String, Object>> getErrorZkbFePartList() {
        return errorZkbFePartList;
    }

    public void setErrorZkbFePartList(List<Map<String, Object>> errorZkbFePartList) {
        this.errorZkbFePartList = errorZkbFePartList;
    }

    public Boolean getGeAdmin() {
        return geAdmin;
    }

    public void setGeAdmin(Boolean geAdmin) {
        this.geAdmin = geAdmin;
    }
}
