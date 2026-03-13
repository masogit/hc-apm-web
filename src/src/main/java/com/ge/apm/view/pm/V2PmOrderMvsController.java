package com.ge.apm.view.pm;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.MvsPmOrderMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.exception.BusinessException;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.pm.PmFileExportTemplate;
import com.ge.apm.service.pm.PmOrderSteps;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.FileScanService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.ExportFields;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class V2PmOrderMvsController extends GenericCRUDUUIDController<V2_PmOrder> implements ServerEventInterface {
    protected static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(V2PmOrderMvsController.class);
    private UserContextService userContextService;

    @Getter @Setter
    private FileScanService fileScanService;

    private List<String> pmFieldsExport= new ArrayList<>();
    @Setter @Getter
    List<UnstructuredDataCollection> reportList;
    private String messageTip;

    //选定的院区
    private OrgInfo selectSite;
    private List<OrgInfo> clinicalDeptList;
    private List<OrgInfo> clinicalDeptListMVS;
    private OrgInfoTreeService orgInfoTreeService;
    @Setter @Getter
    private Set<String> clinicalDeptOrgUidSetMVS;
    private RelRuleAssetRepository relRuleAssetRepository;
    private List<UnstructuredDataCollection> unDataCollection;
    private UnstructuredDataCollectionRepository reportDao;
    private List<UnstructuredData> pmCheckItemList;// 每条检查项记录
     private ConfigItemValueRepository configItemValueRepository;
    @Getter @Setter
    private String fileterOrgNameMVS;
     private V2_PmOrderRepository dao;
    private V2_PmOrderService pmOrderService;
     private String assetUid;
    private Integer ruleLevel;
    private UaaService uaaService;
    private Integer queryIndex;
    private AssetInfo selectedAsset;
    private Integer currentStatus = 1;
     private String stepFileter;
    private Integer repairTypeInt;
    private List<V2_WorkOrder_Detail> accessoryList;
    private V2_WorkOrder_Detail itemDetail;
    private V2_WorkOrderCoworker coWorkerDetail;
    private List<V2_BlobObject> picList;
    private List<V2_BlobObject> attachListEhome;
    private List<V2_BlobObject> attachListEhomeFile;
    private List<V2_BlobObject> attachListEhomeImg;

    private List<AssetTypeGroup> assetTypeGroupList;
    private PmReportService pmReportService;
    private FileDownloadService fileDownloadService;

    private List<V2_BlobObject> audioList;
    private BlobObjectRepository blobDao;
    private String pmOrderId;
    private Map<String, Boolean> selectedMap;
    private TableDefinitionRepository tableDao;
    private UnstructuredDataRepository unstructuredDataDao;
    HashMap<String, UnstructuredData> hmCheck;
    private Boolean isSelectedCurrentPage = false;
    private List<V2_PmOrder> currentPagePmOrders;
    private String pmRuleUid;
    private UserAccount loginUser;
    private TenantInfoRepository tenantInfoRepository;
    Boolean isInsulate;
    private String queryAssetUid;
    private String orderId;
    private ConfigService configService;
    private String plannedCompletionTimeFormat = null;
    private String actualCompletionTimeFormat = null;
    @Setter@Getter
    private boolean hasConfigMultiLevelApproval = false;
    private String filterPmRuleLevel;
    @Getter @Setter
    private String siteUidFilterMVS;
    @Getter @Setter
    private List<OrgInfo> orgListMVS;
    @Getter @Setter
    List<V2_PmOrder> bySearchFilterMVS = null;
    @Setter@Getter
    private String pageType ;

    private MvsPmOrderMapper mvsPmOrderMapper;

    private ConfigCacheService configCacheService;
    private AssetInfoRepository assetInfoRepository;

    public String getActualCompletionTimeFormat() {
        return actualCompletionTimeFormat;
    }

    public void setActualCompletionTimeFormat(String actualCompletionTimeFormat) {
        this.actualCompletionTimeFormat = actualCompletionTimeFormat;
    }

    public String getPlannedCompletionTimeFormat() {
        return plannedCompletionTimeFormat;
    }

    public void setPlannedCompletionTimeFormat(String plannedCompletionTimeFormat) {
        this.plannedCompletionTimeFormat = plannedCompletionTimeFormat;
    }

    @Getter @Setter
    private StreamedContent excelFile;
    private BlobObjectRepository blobObjectRepository;

    @Getter @Setter
    private String requestAssetUid;
    public String getSiteOrgName(String siteUID) {
        OrgInfo org = configCacheService.findOrgByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }

    public boolean exportButtonShow(){
        if (queryIndex==10 || queryIndex  == 20){
            return false;
        }else{
            return !isChecked();
        }
    }
    public void exportExcel() throws IOException{
        PmFileExportTemplate template = new PmFileExportTemplate();
        List<V2_PmOrder> pmOrderList = getPmListByselectedId();
        if(pmOrderList ==null && queryIndex!=10){
            WebUtil.addErrorMessage(WebUtil.getMessage("SelectExportFiles"));
            RequestContext.getCurrentInstance().addCallbackParam("", true);
            return;
        }
        template.exportPm(pmOrderList,pmFieldsExport,queryIndex);
        excelFile = new DefaultStreamedContent(template.getInputStream(), "application/.ms-excel", "PmOrder_Export.xlsx");
    }

    public void exportExcel1() throws IOException{
        PmFileExportTemplate template = new PmFileExportTemplate();
        List<V2_PmOrder> pmOrderList= getPmListByselectedId();
        if(pmOrderList ==null && queryIndex!=10){
            WebUtil.addErrorMessage(WebUtil.getMessage("SelectExportFiles"));
            RequestContext.getCurrentInstance().addCallbackParam("", true);
            return;
        }
        template.writePm(pmOrderList,queryIndex);
        excelFile = new DefaultStreamedContent(template.getInputStream(), "application/.ms-excel", "PmOrder_Export.xlsx");
    }

    private List<V2_PmOrder> getExportList() {
        List<V2_PmOrder> pmList=null;
        if(pmIdList.size()!=0 && queryIndex!=10 ) {
            pmList = dao.getBatchPmOrderList(pmIdList);
        }else{
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            pmList = dao.getPmOrderListAll(orgUidList);
        }
        return pmList;
    }


    public String checkValue(Object obj){
        if(obj != null){
            return obj.toString();
        }else {
            return "";
        }
    }

    List<String> pmIdList = new ArrayList<String>();
    private  List<V2_PmOrder> getPmListByselectedId(){
//        Map<String, Object> params = new HashMap<>();
        if(pmIdList.size()==0) {
            return null;
        }
//        params.put("userUid", loginUser.getUid());
//        params.put("tenantUid", loginUser.getTenantUID());

        Map<String, Object> params = initializeParams();
        if(queryIndex!=10){
            params.put("pmIdList", pmIdList.toArray());
        }
        // 处理搜索过滤器
        processSearchFilters(params);
        // 根据查询索引设置参数
        setParamsByQueryIndex(params);

        List<V2_PmOrder> pmList = mvsPmOrderMapper.loadData(params);
        return pmList;
    }
    public void pick(String itemId){
        pmIdList = new ArrayList<>();
        selectedMap.forEach((k, v) -> {
            if (v == true)
                pmIdList.add(k);

        });
    }
    @Override
    public void onFilter(FilterEvent event){
        super.onFilter(event);
        Map<String, Object> filters = event.getFilters();
        selectedMap.clear();
    }

    public boolean getPmRule(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        WorkflowConfig workflowConfig = configService.findConfigBySite(userAccount.getSiteUID());
        return workflowConfig != null && workflowConfig.getPmRuleLevelFour();
    }

    public boolean isChecked() {
        Map<String, Boolean> map = this.selectedMap;
        sameOpHashmap = map;
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (entry.getValue()) {
                return true;
            }
        }
        return false;
    }


    public List<ServiceRequestMsg> getServiceRequestMessageList(String pmOrderId) {
        return selected == null ? null : pmOrderService.getServiceRequestMessageList(pmOrderId);
    }

    public Map<String,Boolean> sameOpHashmap = new HashMap<String,Boolean>();

    public String getTableDefName(String templateId) {
        TableDefinition tb = tableDao.findById(templateId);
        if (tb != null) {
            return tb.getName();
        } else {
            return String.format(WebUtil.getMessage("SchemaIdUnknowReport"), templateId);
        }
    }

    UnstructuredData untest;

    public void hmCheckSet(String key) throws Exception {
        //临时用hmCheck1  因为hmCheck的value 是String ?
        untest = (UnstructuredData) hmCheck.get(key);
    }

    public OrgInfo getOrgInfoByUid(String orgUid) {
        return configCacheService.findOrgByUid(orgUid);
    }

    public List<PmOrderSteps> aquirePmOrderStep() {
        return Optional.ofNullable(selected).map(V2_PmOrder::getId).map(pmOrderService::getPmOrderStep).orElse(ListUtil.empty());
    }

    protected void init() {
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        userContextService =  WebUtil.getBean(UserContextService.class);
        pmReportService = WebUtil.getBean(PmReportService.class);
        mvsPmOrderMapper = WebUtil.getBean(MvsPmOrderMapper.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        fileScanService = WebUtil.getBean(FileScanService.class);
        tableDao = WebUtil.getBean(TableDefinitionRepository.class);
        unstructuredDataDao = WebUtil.getBean(UnstructuredDataRepository.class);
        relRuleAssetRepository = WebUtil.getBean(RelRuleAssetRepository.class);
        reportDao = WebUtil.getBean(UnstructuredDataCollectionRepository.class);
        dao = WebUtil.getBean(V2_PmOrderRepository.class);
        pmOrderService = WebUtil.getBean(V2_PmOrderService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        configService = WebUtil.getBean(ConfigService.class);
        this.selectedMap = new HashMap();
        blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        // 用户可见资产分类
        assetTypeGroupList = loginUser.getAssetTypeGroupList();
        orgListMVS = new ArrayList<>();
        //如果是院长等管理层登录进来queryIndex默认是8即当前就显示待审核
        String query = WebUtil.getRequestParameter("query");
        String enCodeParameters = WebUtil.getRequestParameter("str");
        if ("View".equals((String) UrlEncryptController.getValueFromMap(enCodeParameters, "actionName"))) {
            selected = dao.findById((String) UrlEncryptController.getValueFromMap(enCodeParameters, "selectedid"));
            if(selected.getAssetInfo() ==null){
                AssetInfo assetInfo = assetInfoRepository.findByUid((String) UrlEncryptController.getValueFromMap(enCodeParameters, "assetUid"));
                selected.setAssetInfo(assetInfo);
            }
            onSelectPmOrder();
        }else{
            if (!Strings.isNullOrEmpty(enCodeParameters)) {
                requestAssetUid = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "assetUID");
                queryAssetUid = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "queryAssetUID");
                orderId = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "orderId");
                if (null != UrlEncryptController.getValueFromMap(enCodeParameters, "query")) {
                    query = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "query");
                }
            }
            if (Strings.isNullOrEmpty(query)) {
                queryIndex = -1;
            } else {
                queryIndex = Integer.valueOf(query);
                searchList(queryIndex);
            }
            for (ExportFields exs: ExportFields.values()){
                pmFieldsExport.add(exs.getChinese());
            }
            queryIndex = Strings.isNullOrEmpty(query) ? 10  : Integer.valueOf(query);
            this.filterBySite = false;
            this.filterByHospital = false;
            hmCheck = new HashMap<String, UnstructuredData>();
            isInsulate = tenantInfoRepository.findByuid(loginUser.getTenantUID()).getInsulate();
            isInsulate = isInsulate == null ? false : isInsulate;
            loadClinicalDeptList();
            getPageTypeFromConfig();
        }
    }

    public void getPageTypeFromConfig() {
        String  key= "PmOrderDetailViewType";
        pageType = configItemValueRepository.findByKeyAndSiteForUseParent(loginUser.getSiteUID(), loginUser.getHospitalUID(), loginUser.getInstitutionUID(), loginUser.getTenantUID(), key);

        if(StrUtil.isEmpty(pageType)){
            pageType = "current";
        }
    }


    @Override
    protected GenericRepositoryUUID<V2_PmOrder> getDAO() {
        return dao;
    }
    @Override
    protected Page<V2_PmOrder> loadData(PageRequest pageRequest) {
        this.selected = null;
        Page<V2_PmOrder> result;
        if (loginUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(assetTypeGroupList)) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }
        if(!userContextService.accessContainsFunctionMenu("MAINTAIN_ORDER_TAKE")
                &&!userContextService.accessContainsFunctionMenu("MAINTAIN_ORDER_VIEW_ALL")){
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        Map<String, Object> params = initializeParams();
        // 处理搜索过滤器
        processSearchFilters(params);
        // 根据查询索引设置参数
        setParamsByQueryIndex(params);
        List<V2_PmOrder> v2PmOrderList = mvsPmOrderMapper.loadData(params);
        result= new PageImpl<V2_PmOrder>(this.pageNum(v2PmOrderList, pageRequest), pageRequest, v2PmOrderList.size());
        currentPagePmOrders = result.getContent();
        bySearchFilterMVS = result.getContent();
        result.getContent().forEach(item->pmIdList.add(item.getId()));
        this.isSelectedCurrentPage = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        if(selectedMap.size() > 0){
            selectedMap = new HashMap();
        }
        loadMVSClinicalDeptList();
        return result;
    }
    // 根据查询索引设置参数
    private void setParamsByQueryIndex(Map<String, Object> params) {
        if(queryIndex == 20){
            String[] strArray = new String[]{WebUtil.getMessage("Canceled")};
            params.put("currentStepName",strArray);
        }
        if(queryIndex==100){
            if (!Strings.isNullOrEmpty(queryAssetUid)) {
                params.put("queryAssetUid",queryAssetUid);
            }
            if (!Strings.isNullOrEmpty(orderId)) {
                params.put("orderId",orderId);
            }
        }
        if(queryIndex==10){
            pmIdList = new ArrayList<>();
            //loadMVSClinicalDeptList();
        } else if (queryIndex == 0) {
            String[] strArray = new String[]{"已计划","进行中"};
            params.put("currentStepName",strArray);
            params.put("isFinished",false);
        }  else if (queryIndex == 7) {
            String[] strArray = new String[]{"已关单"};
            params.put("currentStepName",strArray);
        }
    }

    /**
     * Check whether the target is selected.
     *
     * @return true if the mantenance report item has been selected, false otherwise.
     */
    public boolean isTargetSelected() {
        return !selectedMap.isEmpty() && selectedMap.values().contains(true);
    }
    private StreamedContent maintenanceReport;
    /**
     * Getter method for maintenance report.
     *
     * @return {@link StreamedContent} object that contains maintenance report.
     */
    public StreamedContent getMaintenanceReport() {
        return maintenanceReport;
    }

    /**
     * Setter for maintenance report.
     *
     * @param maintenanceReport {@link StreamedContent} object.
     */
    public void setMaintenanceReport(StreamedContent maintenanceReport) {
        this.maintenanceReport = maintenanceReport;
    }

    /**
     * Generate maintenance report.
     */
    public void doExportMaintenanceReport() {
        maintenanceReport = null;
        try {
            maintenanceReport = pmReportService.generate(selectedMap, isInsulate, loginUser);
        } catch (BusinessException e) {
            logger.error("downLoad pm_word BusinessException ",e);
            WebUtil.addErrorMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("downLoad pm_word Exception",e);
            Logger.getLogger(V2PmOrderController.class.getName()).log(Level.WARNING, "Error happens while generate maintenance report", e);
            WebUtil.addErrorMessage(WebUtil.getMessage("FailedToGenerateTheMaintenanceReport"));
        }
    }

    // 处理搜索过滤器
    private void processSearchFilters(Map<String, Object> params) {
        for (SearchFilter searchFilter : searchFilters) {
            String fieldName = searchFilter.fieldName;
            switch (fieldName) {
                case "assetInfo.name":
                    params.put("assetName", searchFilter.value);
                    break;
                case "assetInfo.serialNum":
                    params.put("assetSerialNum", searchFilter.value);
                    break;
                case "assetInfo.departNum":
                    params.put("departNum", searchFilter.value);
                    break;
                case "assetInfo.functionType":
                    params.put("functionType", searchFilter.value);
                    break;
                case "ruleLevel":
                    params.put("ruleLevel", searchFilter.value);
                    break;
                case "pmType":
                    params.put("pmType", searchFilter.value);
                    break;
                case "pmNum":
                    params.put("pmNum", searchFilter.value);
                    break;
                case "planTime":
                    if (searchFilter.operator.toString().equals("GTE")){
                        params.put("planTimeGTE", searchFilter.value);
                    }
                    if (searchFilter.operator.toString().equals("LTE")){
                        params.put("planTimeLTE", searchFilter.value);
                    }
                    break;
                case "endTime":
                    if (searchFilter.operator.toString().equals("GTE")){
                        params.put("endTimeGTE", searchFilter.value);
                    }
                    if (searchFilter.operator.toString().equals("LTE")){
                        params.put("endTimeLTE", searchFilter.value);
                    }
                    break;
                case "mainSub":
                    params.put("mainSub", searchFilter.value);
                    break;
                case "ruleName":
                    params.put("ruleName", searchFilter.value);
                    break;
                case "period":
                    params.put("period", Integer.valueOf(searchFilter.value.toString()));
                    break;
                case "periodUnit":
                    params.put("periodUnit", searchFilter.value);
                    break;
                case "creatorName":
                    params.put("creatorName", searchFilter.value);
                    break;
                case "ownerName":
                    params.put("ownerName", searchFilter.value);
                    break;
                case "currentStepName":
                    params.put("currentStepName1", searchFilter.value);
                    break;
                case "intExtType":
                    params.put("intExtType", Integer.valueOf(searchFilter.value.toString()));
                default:
                    break;
            }
        }
    }
    private List<V2_PmOrder> pageNum(List<V2_PmOrder> v2PmOrderList,PageRequest pageRequest){
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize() - 1;
        int numStart = pageNumber * pageSize;
        numStart = numStart + pageNumber;
        int numEnd = numStart + pageSize + 1;
        int size = v2PmOrderList.size();
        if(size > numEnd){
            v2PmOrderList = v2PmOrderList.subList(numStart ,numEnd);
        }else {
            v2PmOrderList = v2PmOrderList.subList(numStart ,size);
        }
        return v2PmOrderList;
    }

    // 初始化参数
    private Map<String, Object> initializeParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("userUid", loginUser.getUid());
        params.put("tenantUid", loginUser.getTenantUID());
        if (!org.springframework.util.CollectionUtils.isEmpty(clinicalDeptOrgUidSetMVS)) {
            params.put("assetInfoClinicalDeptUID", clinicalDeptOrgUidSetMVS.stream().toArray(String[]::new));
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(siteUidFilterMVS)) {
            params.put("assetInfoSiteUID", siteUidFilterMVS);
        }
        if (loginUser.isEnableAssetTypeGroup() && !CollectionUtils.isEmpty(assetTypeGroupList)) {
            List<String> encodedList = assetTypeGroupList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toList());
            params.put("assetInfoAssetType", encodedList.stream().toArray(String[]::new));
        }
        return params;
    }

    public String getFilterPmRuleLevel() {
        return filterPmRuleLevel;
    }

    public void setFilterPmRuleLevel(String filterPmRuleLevel) {
        this.filterPmRuleLevel = filterPmRuleLevel;
    }

    public void updateQueryIndex(int query){
        queryIndex = query;
        selected = null;
    }

    public StreamedContent getExcelFile() {
        return excelFile;
    }

    public void onSelectOrderRedirect() throws IOException {
        String url = "actionName=View&selectedid="
                + selected.getId() + "&assetUid=" + selected.getAssetInfo().getUid()
                + "&queryIndex=" + queryIndex
                ;
        url = "mvs/pmMvsOrderViewNew.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public void onSelectPmOrder() {
        attachListEhomeImg = new ArrayList<>();
        attachListEhomeFile = new ArrayList<>();
        accessoryList = getWorkOrderDetail(selected.getId());
        picList = blobDao.findByBoUuid(selected.getId());
        attachListEhome = blobDao.findByBoUuidAndBoType(selected.getId(),"PmOrderEHome");
        if(!org.springframework.util.CollectionUtils.isEmpty(attachListEhome)){
            //Set<String> objectIds = attachListEhome.stream().map(V2_BlobObject::getObjectStorageId).collect(Collectors.toSet());
            //List<FileInfoResult> fileInfoResults = fileDownloadService.getFileInfoForEhome(objectIds);
            List<String> imageExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
            if (attachListEhome == null) {
                attachListEhome = new ArrayList<>();
            }
            List<V2_BlobObject> imageFiles = attachListEhome.stream()
                    .filter(result -> result != null && result.getObjectName() != null)
                    .filter(result -> imageExtensions.stream()
                            .anyMatch(ext -> result.getObjectName().toLowerCase().endsWith(ext)))
                    .collect(Collectors.toList());
            if(!org.springframework.util.CollectionUtils.isEmpty(imageFiles)){
                attachListEhomeImg = imageFiles.stream()
                        .filter(result -> result.getObjectStorageId() != null)
                        .map(result -> {
                            V2_BlobObject blobObject = new V2_BlobObject();
                            blobObject.setObjectStorageId(result.getObjectStorageId());
                            blobObject.setObjectName(result.getObjectName());
                            blobObject.setBoType("IMG");
                            return blobObject;
                        })
                        .collect(Collectors.toList());
            }
            List<V2_BlobObject> otherFiles = attachListEhome.stream()
                    .filter(result -> result != null && result.getObjectName() != null)
                    .filter(result -> imageExtensions.stream()
                            .noneMatch(ext -> result.getObjectName().toLowerCase().endsWith(ext)))
                    .collect(Collectors.toList());
            if(!org.springframework.util.CollectionUtils.isEmpty(otherFiles)){
                attachListEhomeFile = otherFiles.stream()
                        .filter(result -> result.getObjectStorageId() != null)
                        .map(result -> {
                            V2_BlobObject blobObject = new V2_BlobObject();
                            blobObject.setObjectStorageId(result.getObjectStorageId());
                            blobObject.setObjectName(result.getObjectName());
                            return blobObject;
                        })
                        .collect(Collectors.toList());
            }
            attachListEhome.clear();
            attachListEhome.addAll(attachListEhomeImg);
            attachListEhome.addAll(attachListEhomeFile);
        }
        reportList = pmOrderService.getReportList(selected.getId());
        loadHmCheck(selected);
     }

    public List<V2_WorkOrder_Detail> getWorkOrderDetail(String woId) {
        return pmOrderService.getWorkOrderDetails(woId);
    }

    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return pmOrderService.getWorkOrderCoworker(woId);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetailON() {
        return Optional.ofNullable(selected).map(V2_PmOrder::getId).map(pmOrderService::getWorkOrderDetails).orElse(ListUtil.empty());
    }

    public List<V2_WorkOrderCoworker> getWorkOrderCoworkerON(String woId) {
        return Optional.ofNullable(selected).map(V2_PmOrder::getId).map(pmOrderService::getWorkOrderCoworker).orElse(ListUtil.empty());
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (V2_PmOrder item : currentPagePmOrders) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
        pmIdList = new ArrayList<>();
        selectedMap.forEach((k, v) -> {
            if (v == true)
                pmIdList.add(k);

        });
    }

    public void searchList(Integer buttonIndex) {
        pmIdList = new ArrayList<String>();
        this.selectedMap = new HashedMap<>();
        queryIndex = buttonIndex;
        this.searchFilters = null;
        this.stepFileter = null;
        cancel();
    }

    public void cancel() {
        selectedAsset = null;
         ruleLevel = null;
        picList = null;
        assetUid = null;
        attachListEhome = null;
        attachListEhomeImg = null;
        attachListEhomeFile = null;
    }





    private void loadClinicalDeptList() {
        try {
            clinicalDeptList = Collections.emptyList();
            List<OrgInfo> deptList = uaaService.getAllAccessibleClinicalDeptList();
            if (!org.springframework.util.CollectionUtils.isEmpty(deptList)) {
                clinicalDeptList = deptList.stream()
                        .filter(item ->
                        {
                            if (item == null) {
                                LOGGER.info("null");
                            }
                            return item.getOrgType() != null && item.getOrgType() == 4;
                        })
                        .collect(Collectors.toList());
            }

            if (null != selectSite && !clinicalDeptList.isEmpty()) {
                clinicalDeptList = clinicalDeptList.stream()
                        .filter(item -> com.alibaba.druid.util.StringUtils.equals(item.getSiteUID(), selectSite.getSiteUID())
                                || com.alibaba.druid.util.StringUtils.equals(item.getHospitalUID(), selectSite.getSiteUID())
                                || com.alibaba.druid.util.StringUtils.equals(item.getInstitutionUID(), selectSite.getSiteUID())
                                || com.alibaba.druid.util.StringUtils.equals(item.getTenantUID(), selectSite.getSiteUID()))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.error("loadClinicalDeptList occur exception", e);
            throw e;
        }
    }


    public List<String> autoCompleteDeptNameMVS(String query) {
        if (clinicalDeptListMVS == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptListMVS.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }


    public void onClinicalDeptNameFilterChangeMVS(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            fileterOrgNameMVS = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(fileterOrgNameMVS) && !clinicalDeptListMVS.stream().anyMatch(item -> fileterOrgNameMVS.equals(item.getName()))) {
            fileterOrgNameMVS = null;
        }

        if (fileterOrgNameMVS != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptListMVS.stream().filter(item -> item.getName().equals(fileterOrgNameMVS)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptListMVS);
            clinicalDeptOrgUidSetMVS = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSetMVS = Collections.emptySet();
        }
    }



    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }
        assetUid = asset.getUid();
        this.selectedAsset = asset;
        currentStatus = asset.getStatus();
        if (currentStatus == 1) {
            currentStatus = 3;
        }
        autoSetPmRuleUid(assetUid,ruleLevel);
    }

    private void autoSetPmRuleUid(String assetUid, Integer ruleLevel) {
        if(StringUtils.isNotEmpty(assetUid)&&ruleLevel != null){
            RelRuleAsset relRuleAsset = relRuleAssetRepository.findByAssetUid(assetUid);
            List<String> ruleUids = new ArrayList<>();
            if(relRuleAsset != null) {
                switch (ruleLevel) {
                    case 1:
                        if (relRuleAsset.getRuleUID1() != null)
                            ruleUids = Arrays.asList(StringUtils.split(relRuleAsset.getRuleUID1(), "|"));
                        break;
                    case 2:
                        if (relRuleAsset.getRuleUID2() != null)
                            ruleUids = Arrays.asList(StringUtils.split(relRuleAsset.getRuleUID2(), "|"));
                        break;
                    case 3:
                        if (relRuleAsset.getRuleUID() != null)
                            ruleUids = Arrays.asList(StringUtils.split(relRuleAsset.getRuleUID(), "|"));
                        break;
                    case 4:
                        if (relRuleAsset.getRuleUID4() != null)
                            ruleUids = Arrays.asList(StringUtils.split(relRuleAsset.getRuleUID4(), "|"));
                        break;
                    default:
                        LOGGER.warn("保养等级不符合要求，其值为{}", ruleLevel);
                }
            }
            if(ruleUids.size() == 1){
                pmRuleUid = ruleUids.get(0);
            }else{
                pmRuleUid = null;
            }

        }
    }


    public  List<UnstructuredData> getOrderCheckList(V2_PmOrder v2PmOrder){
        UnstructuredDataCollection unsD = reportDao.findCKByUId(v2PmOrder.getId());
        if(unsD==null){
            return Collections.emptyList();
        }
        return unstructuredDataDao.findByCollectionId(unsD.getId());
    }

    //hmCheck 的key是每一个检查项的uid, value是unstructrued_data的key
    public void loadHmCheck(V2_PmOrder v2PmOrder) {
        hmCheck = new   HashMap<>();
        //pmCheckItemList:检查项
        pmCheckItemList = getOrderCheckList(v2PmOrder);
        unDataCollection = reportDao.findCKByBoUuid(v2PmOrder.getId(), "pm-checklist-report");//为0说明该工单的规则还没有绑定 通过和不通过
        if (ObjectUtil.isNotEmpty(unDataCollection)) {
            for (UnstructuredData data : pmCheckItemList) {
                hmCheck.put(data.getKey(), data);
            }
        }
    }

    public List<OrgInfo> LoadSiteNameListMVS() {
        orgListMVS = uaaService.getUserAccessibleSiteList(loginUser.getUid());
        if (!org.springframework.util.CollectionUtils.isEmpty(orgListMVS)) {
                orgListMVS = orgListMVS.stream().filter(item -> 3 == item.getOrgType()).collect(Collectors.toList());
        } else {
            orgListMVS = Collections.emptyList();
        }
        return orgListMVS;
    }

//    public List<OrgInfo> LoadSiteNameListMVS() {
//        orgListMVS = Collections.emptyList();
//        if (CollectionUtils.isEmpty(orgListMVS)) {
//            if (!org.springframework.util.CollectionUtils.isEmpty(bySearchFilterMVS)) {
//                orgListMVS = bySearchFilterMVS.stream()
//                        .map(item -> Optional.ofNullable(item.getAssetInfo())
//                                .map(info -> Optional.ofNullable(info.getSiteUID())
//                                        .orElse(null))
//                                .orElse(null))
//                        .filter(Objects::nonNull) // 过滤掉所有 null 值
//                        .collect(Collectors.toSet()) // 转换为 Set 以去除重复项
//                        .stream()
//                        .map(val -> configCacheService.findOrgByUid(val))
//                        .collect(Collectors.toList());
//                if (org.springframework.util.CollectionUtils.isEmpty(clinicalDeptListMVS)) {
//                    orgListMVS = Collections.emptyList();
//                }
//            }
//            if (!org.springframework.util.CollectionUtils.isEmpty(orgListMVS)) {
//                orgListMVS = orgListMVS.stream().filter(item -> 3 == item.getOrgType()).collect(Collectors.toList());
//            }
//        }
//        return orgListMVS;
//    }


    private void loadMVSClinicalDeptList() {
//        try {
//            if (!org.springframework.util.CollectionUtils.isEmpty(bySearchFilterMVS)) {
//                clinicalDeptListMVS = bySearchFilterMVS.stream()
//                        .map(item -> Optional.ofNullable(item.getAssetInfo())
//                                .map(info -> Optional.ofNullable(info.getClinicalDeptUID())
//                                        .orElse(null))
//                                .orElse(null))
//                        .filter(Objects::nonNull) // 过滤掉所有 null 值
//                        .collect(Collectors.toSet()) // 转换为 Set 以去除重复项
//                        .stream()
//                        .map(val -> configCacheService.findOrgByUid(val))
//                        .collect(Collectors.toList());
//                if (org.springframework.util.CollectionUtils.isEmpty(clinicalDeptListMVS)) {
//                    clinicalDeptListMVS = Collections.emptyList();
//                }
//            }
//        } catch (Exception e) {
//            logger.info("暂无MVS列表");
//        }
        try {
            clinicalDeptListMVS = Collections.emptyList();
            List<OrgInfo> deptList = uaaService.getAllAccessibleClinicalDeptList();
            if (!org.springframework.util.CollectionUtils.isEmpty(deptList)) {
                clinicalDeptListMVS = deptList.stream()
                        .filter(item ->
                        {
                            if (item == null) {
                                LOGGER.info("null");
                            }
                            return item.getOrgType() != null && item.getOrgType() == 4;
                        })
                        .collect(Collectors.toList());
            }

            if (null != selectSite && !clinicalDeptListMVS.isEmpty()) {
                clinicalDeptListMVS = clinicalDeptListMVS.stream()
                        .filter(item -> org.apache.commons.lang.StringUtils.equals(item.getSiteUID(), selectSite.getSiteUID())
                                || org.apache.commons.lang.StringUtils.equals(item.getHospitalUID(), selectSite.getSiteUID())
                                || org.apache.commons.lang.StringUtils.equals(item.getInstitutionUID(), selectSite.getSiteUID())
                                || org.apache.commons.lang.StringUtils.equals(item.getTenantUID(), selectSite.getSiteUID()))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.error("loadClinicalDeptList occur exception", e);
            throw e;
        }
    }


    public V2_WorkOrder_Detail getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(V2_WorkOrder_Detail itemDetail) {
        this.itemDetail = itemDetail;
    }

    public V2_WorkOrderCoworker getCoWorkerDetail() {
        return coWorkerDetail;
    }

    public void setCoWorkerDetail(V2_WorkOrderCoworker coWorkerDetail) {
        this.coWorkerDetail = coWorkerDetail;
    }


    public String getStepFileter() {
        return stepFileter;
    }

    public void setStepFileter(String stepFileter) {
        this.stepFileter = stepFileter;
    }

    public List<V2_BlobObject> getPicList() {
        return picList;
    }

    public void setPicList(List<V2_BlobObject> picList) {
        this.picList = picList;
    }

    public List<V2_BlobObject> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<V2_BlobObject> audioList) {
        this.audioList = audioList;
    }

    public Integer getRuleLevel() {
        return ruleLevel;
    }

    public void setRuleLevel(Integer ruleLevel) {
        this.ruleLevel = ruleLevel;
    }

    public String getPmOrderId() {
        return pmOrderId;
    }

    public void setPmOrderId(String pmOrderId) {
        this.pmOrderId = pmOrderId;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public void setAccessoryList(List<V2_WorkOrder_Detail> accessoryList) {
        this.accessoryList = accessoryList;
    }

    public Boolean getIsSelectedCurrentPage() {
        return isSelectedCurrentPage;
    }

    public void setIsSelectedCurrentPage(Boolean selectedCurrentPage) {
        isSelectedCurrentPage = selectedCurrentPage;
    }

    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }


    public void setPmCheckItemList(List<UnstructuredData> pmCheckItemList) {
        this.pmCheckItemList = pmCheckItemList;
    }

    public List<UnstructuredData> getPmCheckItemList() {
        return pmCheckItemList;
    }

    public Integer getRepairTypeInt() {
        return repairTypeInt;
    }

    public void setRepairTypeInt(Integer repairTypeInt) {
        this.repairTypeInt = repairTypeInt;
    }


    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }

    public List<OrgInfo> getClinicalDeptList() {
        return clinicalDeptList;
    }

    public void setClinicalDeptList(List<OrgInfo> clinicalDeptList) {
        this.clinicalDeptList = clinicalDeptList;
    }

    public OrgInfoTreeService getOrgInfoTreeService() {
        return orgInfoTreeService;
    }

    public void setOrgInfoTreeService(OrgInfoTreeService orgInfoTreeService) {
        this.orgInfoTreeService = orgInfoTreeService;
    }


    public List<V2_BlobObject> getPartPictures(String id) {
        if(StringUtils.isNotEmpty(id)) {
            accessoryList = getWorkOrderDetail(id);
            Set<String> ids = accessoryList.stream().map(p -> p.getId()).collect(Collectors.toSet());
            if(!ids.isEmpty()) {
                return blobObjectRepository.getPartPictures(ids);
            }
            return null;
        } else {
            logger.warn("V2PmOrderController selected is null");
            return null;
        }
    }


    public String getPeriodUnit(String periodUnit){
        if (StringUtils.isNotEmpty(periodUnit)) {
            switch (periodUnit) {
                case "y":
                    return "年";
                case "m":
                    return "月";
                case "w":
                    return "周";
                case "d":
                    return "日";
                default:
                    break;
            }
        }
        return null;
    }
    /**
     * Generate maintenance report.
     */
    public void doExportWorkOrderReport() {
        maintenanceWorkReport = null;
        try {
            maintenanceWorkReport = pmReportService.generateEhome(selectedMap, loginUser);
        } catch (BusinessException e) {
            logger.error("downLoad pm_word BusinessException ",e);
            WebUtil.addErrorMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("downLoad pm_word Exception",e);
            java.util.logging.Logger.getLogger(V2PmOrderController.class.getName()).log(Level.WARNING, "Error happens while generate maintenance report", e);
            WebUtil.addErrorMessage(WebUtil.getMessage("FailedToGenerateTheMaintenanceReport"));
        }
    }
    public List<V2_BlobObject> getAttachListEhome() {
        return attachListEhome;
    }

    public void setAttachListEhome(List<V2_BlobObject> attachListEhome) {
        this.attachListEhome = attachListEhome;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }
    public boolean showLocationName(){
        String configItemValue = configItemValueRepository.findByKeyAndSiteForUseParent(loginUser.getSiteUID(),loginUser.getHospitalUID(), loginUser.getInstitutionUID(), loginUser.getTenantUID(), "showLocationNameFlag");
        if (StrUtil.isNotEmpty(configItemValue) && "true".equals(configItemValue)) {
            return true;
        }
        return false;
    }

    public List<V2_BlobObject> getAttachListEhomeImg() {
        return attachListEhomeImg;
    }

    public void setAttachListEhomeImg(List<V2_BlobObject> attachListEhomeImg) {
        this.attachListEhomeImg = attachListEhomeImg;
    }

    public List<V2_BlobObject> getAttachListEhomeFile() {
        return attachListEhomeFile;
    }

    public void setAttachListEhomeFile(List<V2_BlobObject> attachListEhomeFile) {
        this.attachListEhomeFile = attachListEhomeFile;
    }

    public String getMessageTip() {
        return  pmReportService.generateMessageTip(selectedMap);
    }
    private StreamedContent maintenanceWorkReport;

    public StreamedContent getMaintenanceWorkReport() {
        return maintenanceWorkReport;
    }

    public void setMaintenanceWorkReport(StreamedContent maintenanceWorkReport) {
        this.maintenanceWorkReport = maintenanceWorkReport;
    }

    public void setMessageTip(String messageTip) {
        this.messageTip = messageTip;
    }
}
