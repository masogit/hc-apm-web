package com.ge.apm.view.wo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.MvsWorkOrderMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.FileInfoResult;
import com.ge.apm.domain.exception.BusinessException;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.asset.AssetFaultTypeService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.constant.AssetConstant;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.pm.V2PmOrderController;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;



/**
 *
 * @author lyj
 */
@ManagedBean
@ViewScoped
public class WorkOrderMgrMvsController extends  GenericCRUDUUIDController<V2_WorkOrder> implements Serializable, ServerEventInterface {
    protected static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderMgrMvsController.class);
    @Setter@Getter
    private V2_WorkOrderRepository dao;
    @Getter @Setter
    private String fileterOrgNameMVS;
    private List<OrgInfo> clinicalDeptListMVS;
    private UserAccountRepository userDao;
    private V2_WorkOrderService woService;
    private UserContextService userContextService;
    private String locationName;
    private Integer queryIndex;
    private AssetFaultTypeService assetFaultTypeService;
    private List<AssetTypeGroup> assetTypeGroupList;

    private String messageTip;

    private Map<String, Boolean> selectedMap;

    @Setter @Getter
    private Set<String> clinicalDeptOrgUidSetMVS;
    private BlobObjectRepository blobObjectRepository;
    private V2_ServiceRequest newServiceRequest;
    private ConfigService configService;
    private MvsAssetInfoRepository mvsAssetInfoRepository;
    private V2_WorkOrder selectedWorkOrder;
    private List<V2_WorkOrder> selectedWorkOrderList;
    private AssetInfo selectedAsset;
    private Integer currentStatus = 1;
     private Boolean needConfirmTime = false;
    private String stepFileter;
    //for details form
    private List<V2_WorkOrder_Detail> accessoryList;

    private List<MultiLevelApproval> approveList;

    private Date confirmedDownTime;

    private List<V2_BlobObject> picList;
    private List<V2_BlobObject> audioList;
    private List<V2_BlobObject> attachList;

    private List<V2_BlobObject> attachListEhme;
    private List<V2_BlobObject> attachListEhmeFile;
    private List<V2_BlobObject> attachListEhmeImg;


    private String requestAssetUid;
    private String queryAssetUid;
    private ConfigCacheService cacheService;
    private OrgInfoTreeService orgInfoTreeService;
    private Map<String, Integer> woStatus = new HashMap<String, Integer>();
    private FileDownloadService fileDownloadService;

    private UaaService uaaService;

    private String displayModeCasePriority;

    public List<V2_BlobObject> getAttachListEhmeFile() {
        return attachListEhmeFile;
    }

    public void setAttachListEhmeFile(List<V2_BlobObject> attachListEhmeFile) {
        this.attachListEhmeFile = attachListEhmeFile;
    }

    public List<V2_BlobObject> getAttachListEhmeImg() {
        return attachListEhmeImg;
    }

    public void setAttachListEhmeImg(List<V2_BlobObject> attachListEhmeImg) {
        this.attachListEhmeImg = attachListEhmeImg;
    }

    private OrgInfo selectSite;
    private CustomConfigObject currentCustomConfig;
    private CustomConfigService ccService;
    private CustomConfig customConfig;
     private MultiLevelApprovalService multiLevelApprovalService;
    private MultiLevelApprovalRepository multiLevelApprovalRepository;
     @Getter @Setter
    private String siteUidFilterMVS;
    @Getter @Setter
    private List<OrgInfo> orgListMVS;
    @Getter @Setter
    List<V2_WorkOrder> bySearchFilterMVS = null;
    private UserAccount loginUser;
    @Setter@Getter
    private String pageType ;

    private ConfigItemValueRepository configItemValueRepository;

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public String getMessageTip() {
        return  woService.generateMessageTip(selectedMap);
    }

    public void setMessageTip(String messageTip) {
        this.messageTip = messageTip;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    List<String> woIdList = new ArrayList<String>();
    public boolean isTargetSelected() {
        return !selectedMap.isEmpty() && selectedMap.values().contains(true);
    }
    public void pick(String itemId){
        woIdList = new ArrayList<>();
        selectedMap.forEach((k, v) -> {
            if (v == true)
                woIdList.add(k);

        });
    }
    public void onSelectAllOfPage() {
        boolean isSelectedAll = !bySearchFilterMVS.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (V2_WorkOrder item : bySearchFilterMVS) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
        woIdList = new ArrayList<>();
        selectedMap.forEach((k, v) -> {
            if (v == true)
                woIdList.add(k);
        });
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
    public void doExportWorkOrderReport() {
        maintenanceReport = null;
        try {
            maintenanceReport = woService.generate(selectedMap, loginUser);
        } catch (BusinessException e) {
            logger.error("downLoad pm_word BusinessException ",e);
            WebUtil.addErrorMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("downLoad pm_word Exception",e);
            java.util.logging.Logger.getLogger(V2PmOrderController.class.getName()).log(Level.WARNING, "Error happens while generate maintenance report", e);
            WebUtil.addErrorMessage(WebUtil.getMessage("FailedToGenerateTheMaintenanceReport"));
        }
    }

    @Override
    protected void init() {
        pageType = "0";
        try {
            fileDownloadService = WebUtil.getBean(FileDownloadService.class);
            ccService = WebUtil.getBean(CustomConfigService.class);
            blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
            loginUser = UserContextService.getCurrentUserAccount();
            currentCustomConfig = ccService.getCurrentCustomConfig(loginUser.getSiteUID());
             mvsAssetInfoRepository = WebUtil.getBean(MvsAssetInfoRepository.class);
            dao = WebUtil.getBean(V2_WorkOrderRepository.class);
            userDao = WebUtil.getBean(UserAccountRepository.class);
            woService = WebUtil.getBean(V2_WorkOrderService.class);
            userContextService = WebUtil.getBean(UserContextService.class);
            cacheService = WebUtil.getBean(ConfigCacheService.class);
            configService = WebUtil.getBean(ConfigService.class);
             orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
              multiLevelApprovalService = WebUtil.getBean(MultiLevelApprovalService.class);
            multiLevelApprovalRepository = WebUtil.getBean(MultiLevelApprovalRepository.class);
            mvsWorkOrderMapper = WebUtil.getBean(MvsWorkOrderMapper.class);
            configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
            selectSite = cacheService.findOrgByUid(loginUser.getSiteUID());
            customConfig = ccService.getCustomConfigBySite(selectSite.getUid());
            assetFaultTypeService = WebUtil.getBean(AssetFaultTypeService.class);
            uaaService = WebUtil.getBean(UaaService.class);
            assetTypeGroupList = loginUser.getAssetTypeGroupList();
            ConfigItemValue displayModeCasePriorityValue = configItemValueRepository.findFirstByKeyAndSiteUid("displayModeCasePriority", loginUser.getSiteUID());
            displayModeCasePriority = Optional.ofNullable(displayModeCasePriorityValue).map(ConfigItemValue::getValue).orElse("key");
            String query = WebUtil.getRequestParameter("query");
            String enCodeParameters = WebUtil.getRequestParameter("str");
            String queryAssetUID_href = WebUtil.getRequestParameter("queryAssetUID");
            if ("View".equals((String) UrlEncryptController.getValueFromMap(enCodeParameters, "actionName"))) {
                queryIndex = Integer.valueOf(UrlEncryptController.getValueFromMap(enCodeParameters, "queryIndex").toString());
                selected = dao.findById((String) UrlEncryptController.getValueFromMap(enCodeParameters, "selectedid"));
                onSelectWorkOrder();
            } else {
                if (!Strings.isNullOrEmpty(queryAssetUID_href)) {
                    queryAssetUid = queryAssetUID_href;
                }
                if (!Strings.isNullOrEmpty(enCodeParameters)) {
                    requestAssetUid = (String) UrlEncryptController
                            .getValueFromMap(enCodeParameters, "assetUID");
                    queryAssetUid = (String) UrlEncryptController
                            .getValueFromMap(enCodeParameters, "queryAssetUID");
                    if (null != UrlEncryptController.getValueFromMap(enCodeParameters, "query")) {
                        query = (String) UrlEncryptController
                                .getValueFromMap(enCodeParameters, "query");
                    }
                }
                queryIndex = Strings.isNullOrEmpty(query) ? 4  : Integer.valueOf(query);
                searchList(queryIndex);
                this.filterBySite = false;
                this.filterByHospital = false;
                List<I18nMessage> ix2 = FieldValueMessageController
                        .getFieldValueListByMsgType("woSteps");
                ix2.stream().forEach(
                        item -> woStatus.put(item.getValueZh(), Integer.parseInt(item.getMsgKey())));
                getPageTypeFromConfig();
            }

        } catch (Exception e) {
            LOGGER.error("init occur exception", e);
            throw e;
        }
    }

    public void getPageTypeFromConfig() {
        String  key= "WorkOrderDetailViewType";
        pageType = configItemValueRepository.findByKeyAndSiteForUseParent(loginUser.getSiteUID(), loginUser.getHospitalUID(), loginUser.getInstitutionUID(), loginUser.getTenantUID(), key);

        if(StrUtil.isEmpty(pageType)){
            pageType = "current";
        }
    }


    public String getStepImg(V2_WorkOrder v2_workOrder){
        Integer stepId =v2_workOrder.getCurrentStepId();
        int mode =v2_workOrder.getDispatchMode()!=null?v2_workOrder.getDispatchMode().intValue():0;
        switch(stepId){
            case 7: //待评价
                return "icons/5.png";
            case 6: //
                return "icons/5.png";
            case 5: //待验收
            {
                if( v2_workOrder.getV2_workOrder_detail_list().size()>0 && v2_workOrder.getApprovalDate()==null){//有备件 没有提交审核 则显审核图标显示为灰色的
                    return "icons/6.png";
                }
                if(v2_workOrder.getV2_workOrder_detail_list().size() ==0){
                    return "icons/6.png";
                }
                else{
                    return "icons/5.png";
                }
            }
            case 8: //待审核
                return "icons/4.png";
            case 4: //维修中
            case 10: //待送修
            case 11: //待返还
            {
                if(v2_workOrder.getApprovalDate()!=null){
                    return  "icons/4.png";
                }
                return "icons/3.png";
            }

            case 3: //接单中
                return "icons/2.png";
            case 2:
                if(mode== 2 || mode ==3 ){//如果是抢单或抢单加手动模式
                    return "icons/2.png";
                }
            default:
                return "icons/1.png";
        }
    }
    public String getStepName(V2_WorkOrder v2_workOrder){
        Integer stepId =v2_workOrder.getCurrentStepId();
        switch(stepId){
            case 2:
            case 3:
                return "待接单";
            case 4:
                return "维修中";
            case 7:
            case 9:
                return "已关单";
            default:
                return "";
        }
    }

    public List<String> autoCompleteDeptNameMVS(String query) {
        if (clinicalDeptListMVS == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res =  convertStreamToForLoop(query);
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public List<OrgInfo> convertStreamToForLoop(String query) {
        List<OrgInfo> res = new ArrayList<>();
        if (clinicalDeptListMVS == null) {
            return res;
        }

        for (OrgInfo item : clinicalDeptListMVS) {
            if (item == null) {
                continue;
            }
            boolean condition = StringUtils.isBlank(query) || item.getName().contains(query)
                    || (item.getNamePyCode() != null && item.getNamePyCode().contains(query.toLowerCase()));
            if (condition) {
                res.add(item);
            }
        }
        return res;
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


    public void updateQueryIndex(int query){
        orgListMVS = null;
        queryIndex = query;
        selected = null;
    }

    @Override
    protected GenericRepositoryUUID<V2_WorkOrder> getDAO() {
        return dao;
    }
    private MvsWorkOrderMapper mvsWorkOrderMapper;

    @Override
    protected Page<V2_WorkOrder> loadData(PageRequest pageRequest) {
        Page<V2_WorkOrder> pageResult = null;
        this.selected = null;
        try {
            // 检查是否需要返回空页
            if (loginUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(loginUser.getAssetTypeGroupList())) {
                return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
            }
            // 初始化搜索过滤器
            if (this.searchFilters == null) {
                searchFilters = new ArrayList();
            }
            // 初始化参数
            Map<String, Object> params = initializeParams();
            // 处理搜索过滤器
            processSearchFilters(params);

            // 根据查询索引设置参数
            setParamsByQueryIndex(params);
            List<V2_WorkOrder> workOrderList = mvsWorkOrderMapper.loadData(params);
            pageResult = new PageImpl<V2_WorkOrder>(this.pageNum(workOrderList, pageRequest), pageRequest, workOrderList.size());
            bySearchFilterMVS = pageResult.getContent();
            selectedMap = new HashMap();
            loadMVSClinicalDeptList();
            needTwoSubStatusBatch(pageResult.getContent());
        } catch (Exception e) {
            LOGGER.error("load data occur exception,{}", e.getMessage());
            throw e;
        }
        return pageResult;
    }
    // 处理搜索过滤器
    private void processSearchFilters(Map<String, Object> params) {
        for (SearchFilter searchFilter : searchFilters) {
            String fieldName = searchFilter.fieldName;
            switch (fieldName) {
                case "mvsIbMapping.assetInfo.name":
                    params.put("assetName", searchFilter.value);
                    break;
                case "mvsIbMapping.assetInfo.serialNum":
                    params.put("assetSerialNum", searchFilter.value);
                    break;
                case "mvsIbMapping.assetInfo.departNum":
                    params.put("departNum", searchFilter.value);
                    break;
                case "mvsIbMapping.assetInfo.functionType":
                    params.put("functionType", searchFilter.value);
                    break;
                case "serviceRequest.srNum":
                    params.put("srNum", searchFilter.value);
                    break;
                case "serviceRequest.casePriority":
                    params.put("casePriority", searchFilter.value);
                    break;
                case "mvsIbMapping.assetInfo.locationName":
                    params.put("locationName", searchFilter.value);
                    break;
                case "mainSub":
                    params.put("mainSub", searchFilter.value);
                    break;
                default:
                    break;
            }
        }
    }
    // 根据查询索引设置参数
    private void setParamsByQueryIndex(Map<String, Object> params) {
        if (queryIndex == 4) { //维修中
            params.put("status", 1);
            params.put("currentStepId",Arrays.asList(4).toArray());
        } else if (queryIndex == 8) { //已关单
            params.put("status", 2);
        } else if (queryIndex == 9) { //已取消
            params.put("status", 3);
        } else if (queryIndex == 11) { //全部维修单
            if (stepFileter != null) {
                Integer fillterStepId = woStatus.get(stepFileter);
                if (fillterStepId > 6) {
                    if (userContextService.hasCurrentTenantName("")) {
                        params.put("currentStepId", Arrays.asList(7, 9).toArray());
                    } else if(fillterStepId==10 ||fillterStepId==11){
                        params.put("currentStepId", Arrays.asList(fillterStepId).toArray());
                    }else {
                        params.put("currentStepId", Arrays.asList(7).toArray());
                    }
                } else {
                    if (fillterStepId == 3 && userContextService.hasCurrentTenantName("")) {
                        params.put("currentStepId", Arrays.asList(2, 3).toArray());
                    } else {
                        params.put("currentStepId", Arrays.asList(fillterStepId).toArray());
                    }
                }
            }
        } else if (queryIndex == 12) { //指定设备queryAssetUid 工单 设备详细页面跳转
            if (!Strings.isNullOrEmpty(queryAssetUid)) {
                params.put("queryAssetUid", queryAssetUid);
            }
        } else if (queryIndex == 13) {
            params.put("status", 1);
            params.put("currentStepId", Arrays.asList(10).toArray());
        } else if (queryIndex == 14) {
            params.put("status", 1);
            params.put("currentStepId", Arrays.asList(11).toArray());
        }
    }
    // 初始化参数
    private Map<String, Object> initializeParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("userUid", loginUser.getUid());
        params.put("tenantUid", loginUser.getTenantUID());
        if (!CollectionUtils.isEmpty(clinicalDeptOrgUidSetMVS)) {
            params.put("assetInfoClinicalDeptUID", clinicalDeptOrgUidSetMVS.stream().toArray(String[]::new));
        }
        if (StringUtils.isNotEmpty(siteUidFilterMVS)) {
            params.put("assetInfoSiteUID", siteUidFilterMVS);
        }
        if (loginUser.isEnableAssetTypeGroup() && !org.apache.commons.collections4.CollectionUtils.isEmpty(assetTypeGroupList)) {
            List<String> encodedList = assetTypeGroupList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toList());
            params.put("assetInfoAssetType", encodedList.stream().toArray(String[]::new));
        }
        return params;
    }



    private List<V2_WorkOrder> pageNum(List<V2_WorkOrder> v2_workOrderList,PageRequest pageRequest){
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize() - 1;
        int numStart = pageNumber * pageSize;
        numStart = numStart + pageNumber;
        int numEnd = numStart + pageSize + 1;
        int size = v2_workOrderList.size();
        if(size > numEnd){
            v2_workOrderList = v2_workOrderList.subList(numStart ,numEnd);
        }else {
            v2_workOrderList = v2_workOrderList.subList(numStart ,size);
        }
        return v2_workOrderList;
    }

    public void searchList(Integer buttonIndex) {
        queryIndex = buttonIndex;
        this.searchFilters = null;
        this.stepFileter = null;
        selected = null;
        newServiceRequest = null;
        selectedWorkOrder = null;
        confirmedDownTime = null;
        selectedWorkOrderList = null;
        picList = null;
        attachList = new ArrayList<>();
        attachListEhme = new ArrayList<>();
        attachListEhmeFile = new ArrayList<>();
        attachListEhmeImg = new ArrayList<>();
        selectedAsset = null;
        locationName = null;
    }


    public void onSelectOrderRedirect() throws IOException {

        onSelectWorkOrder();
        String url = "actionName=View&selectedid="
                + selected.getId()
                + "&queryIndex=" + queryIndex
                ;
        url = "mvs/woMvsMgrViewNew.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }
    public void onSelectWorkOrder() {
        attachListEhmeFile = new ArrayList<>();
        attachListEhmeImg = new ArrayList<>();
        newServiceRequest = null;
        currentStatus = selected.getAssetInfo().getStatus();
        needConfirmTime = false;
        selectedWorkOrderList = dao.findBySrId(selected.getServiceRequest().getId());
        audioList = woService.getPicListBySrUid(selected.getSrId(), 2, null);
        picList = woService.getPicListBySrUid(selected.getSrId(), 1, V2_BlobObject.BO_SUB_TYPE_IMG);
        attachList = woService.getPicListBySrUid(selected.getSrId(), 1, V2_BlobObject.BO_SUB_TYPE_DOCS);
        attachListEhme = blobObjectRepository.findByBoUuidAndBoType(selected.getId(),  V2_BlobObject.BO_SUB_TYPE_EHOME);
        if(!CollectionUtils.isEmpty(attachListEhme)){
            Set<String> objectIds = attachListEhme.stream().map(V2_BlobObject::getObjectStorageId).collect(Collectors.toSet());
            List<FileInfoResult> fileInfoResults = fileDownloadService.getFileInfoForEhome(objectIds);
            List<String> imageExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
            if (fileInfoResults == null) {
                fileInfoResults = new ArrayList<>();
            }
            List<FileInfoResult> imageFiles = fileInfoResults.stream()
                    .filter(result -> result != null && result.getObjectName() != null)
                    .filter(result -> imageExtensions.stream()
                            .anyMatch(ext -> result.getObjectName().toLowerCase().endsWith(ext)))
                    .collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(imageFiles)){
                attachListEhmeImg = imageFiles.stream()
                        .filter(result -> result.getObjectId() != null)
                        .map(result -> {
                            V2_BlobObject blobObject = new V2_BlobObject();
                            blobObject.setObjectStorageId(result.getObjectId());
                            blobObject.setObjectName(result.getObjectName());
                            return blobObject;
                        })
                        .collect(Collectors.toList());
            }
            List<FileInfoResult> otherFiles = fileInfoResults.stream()
                    .filter(result -> result != null && result.getObjectName() != null)
                    .filter(result -> imageExtensions.stream()
                            .noneMatch(ext -> result.getObjectName().toLowerCase().endsWith(ext)))
                    .collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(otherFiles)){
                attachListEhmeFile = otherFiles.stream()
                        .filter(result -> result.getObjectId() != null)
                        .map(result -> {
                            V2_BlobObject blobObject = new V2_BlobObject();
                            blobObject.setObjectStorageId(result.getObjectId());
                            blobObject.setObjectName(result.getObjectName());
                            return blobObject;
                        })
                        .collect(Collectors.toList());
            }
        }
    }

    public List<V2_WorkOrder_Step> getWOrkOrderStep(String woId) {
        return woService.getWorkOrderSteps(woId);
    }
    public String getRepairResponer(String woId,Integer step){
        List <V2_WorkOrder_Step >vs=null;
        vs =woService.getWorkOrderDetailResponsor(woId,step);
        if(vs !=null && vs.size()>0){
            return vs.get(0).getOwnerName();
        }else {
            return "";
        }
    }

    public V2_WorkOrder_Step getRepairPerson(String woId, Integer step){
        List<V2_WorkOrder_Step> v2WorkOrderStep = woService.getMaxStartTimeWorkOrderStepByWoIdAndStepId(woId, step);
        return CollectionUtils.isEmpty(v2WorkOrderStep)?null:v2WorkOrderStep.get(0);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetail(String woId) {
        //获取多级备件审批节点详情
        try {
            approveList = multiLevelApprovalRepository.getByBusinessUidList(woId);
            if (!CollectionUtils.isEmpty(approveList)) {
                MultiLevelApprovalConfig config = multiLevelApprovalService.getApproveWorkflowConfig(approveList.get(0).getWorkflowConfigApproveUid());
                List<ApprovalNode> approvalNodes = JSON.parseObject(config.getApprovalItem(), new TypeReference<List<ApprovalNode>>(){});
                boolean isOwner = isOwner();
                approveList.forEach(v -> {
                    ApprovalNode node = approvalNodes.get(v.getApprovalLevel() - 1);
                    if (isOwner) {
                        switch (v.getApprovalNodeStatus()) {
                            case 0:
                            case 1:
                                v.setActualApproverName(userDao.findByUidIn(Arrays.stream(v.getApprovalUid().split(",")).filter(c -> StringUtils.isNotEmpty(c)).collect(Collectors.toList())).stream().map(UserAccount::getName).collect(Collectors.joining(",")));
                                break;
                        }
                    } else {
                        v.setActualApproverName(node.getNodeName());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return woService.getWorkOrderDetails(woId);
    }

    public boolean isOwner() {
        if (null != selected.getRepairPersonId() && selected.getRepairPersonId().compareTo(loginUser.getId()) == 0) {
            return true;
        }
        return false;
    }

    public List<MultiLevelApproval> getApproveList() {
        return approveList;
    }

    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return woService.getWorkOrderCoworker(woId);
    }

    public List<ServiceRequestMsg> getServiceRequestMessageList(String woId) {
        return selected == null ? null : woService.getServiceRequestMessageList(woId);
    }

    public void updateDownTime() {
        woService.updateDownTime(selected, confirmedDownTime);
        needConfirmTime = false;
        this.selected.getServiceRequest().setConfirmedDownTime(confirmedDownTime);
    }

    public boolean showLocationName(){
        String configItemValue = configItemValueRepository.findByKeyAndSiteForUseParent(loginUser.getSiteUID(),loginUser.getHospitalUID(), loginUser.getInstitutionUID(), loginUser.getTenantUID(), "showLocationNameFlag");
        if (StrUtil.isNotEmpty(configItemValue) && "true".equals(configItemValue)) {
            return true;
        }
        return false;
    }

    private void onSelectedAsset(AssetInfo asset) {
        String lName ="";
        if(StringUtils.isNotEmpty(asset.getAssetDeptUID())){
            OrgInfo org = cacheService.findOrgByUid(asset.getAssetDeptUID());
            lName= org.getName();
        }else{
            lName= asset.getClinicalDeptName();
        }
        asset.setLocationName(lName);
        locationName = lName;

        if(newServiceRequest != null && userContextService.isCountyArea()){
            OrgInfo orgInfo = cacheService.findOrgByUid(asset.getSiteUID());
            newServiceRequest.setCustomerName(orgInfo.getContactPerson());
            newServiceRequest.setCustomerTel(orgInfo.getContactPhone());
            newServiceRequest.setHospitalName(orgInfo.getName());
        }
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }
        onSelectedAsset(asset);
        this.selectedAsset = asset;
        currentStatus = asset.getStatus();

        if(customConfig ==null){
            if (currentStatus == AssetConstant.ASSET_STATUS_NORMAL
                    || currentStatus == AssetConstant.ASSET_STATUS_TO_TERMINATED
                    || currentStatus == AssetConstant.ASSET_STATUS_STOP) {
                currentStatus = AssetConstant.ASSET_STATUS_ILL;
            }
            needConfirmTime = false;
        }else{ //如果有自定义配置
            String strAssetStatus =JSONPath.eval(currentCustomConfig,"$.mobilePage.pageWorkOrder.group[type='orderFields'].assetStatus.defaultValue[0]").toString();
            currentStatus= Integer.valueOf(strAssetStatus);
            if(currentStatus == AssetConstant.ASSET_STATUS_ILL) //限制功能使用
                needConfirmTime=false;
            if(currentStatus == AssetConstant.ASSET_STATUS_DOWN)//停机
                needConfirmTime=true;
        }
        if (this.newServiceRequest == null) {
            return;
        }

    }

    public void onStatusChange() {
        if (selectedWorkOrder != null) {
            selectedAsset = selectedWorkOrder.getAssetInfo();
        }

        if (selectedAsset == null && selected == null) {
            return;
        }
        if(currentStatus==2){ //选择停机状态
            needConfirmTime=true;
        }
        if(currentStatus==3 || currentStatus==1){//选择 限制功能使用 或可正常使用
            needConfirmTime=false;
        }

    }

    public List<V2_BlobObject> getPartPictures(String id){
        if(StringUtils.isNotEmpty(id)) {
            accessoryList = getWorkOrderDetail(id);
            Set<String> ids = accessoryList.stream().map(p -> p.getId()).collect(Collectors.toSet());
            if(!ids.isEmpty()){
                return blobObjectRepository.getPartPictures(ids);
            }
            return  null;
        } else {
            logger.warn("V2WorkOrderController selected is null");
            return null;
        }
    }

    public OrgInfo getOrgInfoByUid(String orgUid) {
        return cacheService.findOrgByUid(orgUid);
    }

    public String getRequestReasonInShort(String reason) {
        if (reason == null) {
            return null;
        }
        if (reason.length() <= 20) {
            return reason;
        } else {
            return reason.substring(0, 19) + "...";
        }
    }

    public List<V2_BlobObject> getPicListByBoType(String boType) {
        if (picList != null) {
            List<V2_BlobObject> collect = picList.stream().filter(item -> boType.equals(item.getBoType())).collect
                    (Collectors.toList());
            return collect;
        } else {
            return null;
        }
    }

    //getter and setter
    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }

    public V2_WorkOrder getSelectedWorkOrder() {
        return selectedWorkOrder;
    }

    public void setSelectedWorkOrder(V2_WorkOrder selectedWorkOrder) {
        this.selectedWorkOrder = selectedWorkOrder;
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

    public Boolean getNeedConfirmTime() {
        return needConfirmTime;
    }

    public void setNeedConfirmTime(Boolean needConfirmTime) {
        this.needConfirmTime = needConfirmTime;
    }

    public V2_ServiceRequest getNewServiceRequest() {
        return newServiceRequest;
    }

    public void setNewServiceRequest(V2_ServiceRequest newServiceRequest) {
        this.newServiceRequest = newServiceRequest;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Date getConfirmedDownTime() {
        return confirmedDownTime;
    }

    public void setConfirmedDownTime(Date confirmedDownTime) {
        this.confirmedDownTime = confirmedDownTime;
    }

    public List<V2_WorkOrder> getSelectedWorkOrderList() {
        return selectedWorkOrderList;
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


    public String getRequestAssetUid() {
        return requestAssetUid;
    }

    public void setRequestAssetUid(String requestAssetUid) {
        this.requestAssetUid = requestAssetUid;
    }

    public List<V2_BlobObject> getAttachListEhme() {
        return attachListEhme;
    }

    public void setAttachListEhme(List<V2_BlobObject> attachListEhme) {
        this.attachListEhme = attachListEhme;
    }

    public List<V2_BlobObject> getAttachList() {
        return attachList;
    }

    public void setAttachList(List<V2_BlobObject> attachList) {
        this.attachList = attachList;
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }


    public String getFaultTypeName(Integer id) {
        return assetFaultTypeService.getFaultName(id);
    }
    public MvsAssetInfo getMvsContract(String assetUid){
        MvsAssetInfo mvsAssetInfo = mvsAssetInfoRepository.findById(assetUid);
        if(mvsAssetInfo==null){
            return new MvsAssetInfo();
        }else{
            return mvsAssetInfo;
        }
    }

    public List<OrgInfo> LoadSiteNameListMVS() {
        if (CollectionUtils.isEmpty(orgListMVS)) {
            if (!CollectionUtils.isEmpty(bySearchFilterMVS)) {
                orgListMVS = bySearchFilterMVS.stream()
                        .map(item -> Optional.ofNullable(item.getAssetInfo())
                                .map(AssetInfo::getSiteUID)
                                .orElse(null)) // 如果 getSiteUID() 为 null，则使用 null
                        .filter(Objects::nonNull) // 过滤掉所有 null 值
                        .collect(Collectors.toSet()) // 转换为 Set 以去除重复项
                        .stream()
                        .map(val -> cacheService.findOrgByUid(val))
                        .collect(Collectors.toList());
            }
            if (!CollectionUtils.isEmpty(orgListMVS)) {
                orgListMVS = orgListMVS.stream().filter(item -> 3 == item.getOrgType()).collect(Collectors.toList());
            }
        }
        return orgListMVS;
    }


    public String getSiteOrgName(String siteUID) {
        OrgInfo org = cacheService.findOrgByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }

    private void loadMVSClinicalDeptList() {
//        try {
//            logger.info("loadMVSClinicalDeptList.mvs {} "+CollectionUtils.isEmpty(bySearchFilterMVS));
//            if (!org.springframework.util.CollectionUtils.isEmpty(bySearchFilterMVS)) {
//                logger.info("loadMVSClinicalDeptList.size {} "+bySearchFilterMVS.size());
//                clinicalDeptListMVS = bySearchFilterMVS.stream()
//                        .map(item -> Optional.ofNullable(item.getAssetInfo())
//                                .map(AssetInfo::getClinicalDeptUID)
//                                .orElse(null)) // 如果 getClinicalDeptUID() 为 null，则使用 null
//                        .filter(Objects::nonNull) // 过滤掉所有 null 值
//                        .collect(Collectors.toSet()) // 转换为 Set 以去除重复项
//                        .stream()
//                        .map(val -> cacheService.findOrgByUid(val))
//                        .collect(Collectors.toList());
//                logger.info("loadMVSClinicalDeptList.clinicalDeptListMVS {} "+JSONObject.toJSONString(clinicalDeptListMVS));
//
//            }
//        } catch (Exception e) {
//            logger.info("暂无MVS列表");
//        }

        try {
            clinicalDeptListMVS = Collections.emptyList();
            List<OrgInfo> deptList = uaaService.getAllAccessibleClinicalDeptList();
            if (!CollectionUtils.isEmpty(deptList)) {
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
                        .filter(item -> StringUtils.equals(item.getSiteUID(), selectSite.getSiteUID())
                                || StringUtils.equals(item.getHospitalUID(), selectSite.getSiteUID())
                                || StringUtils.equals(item.getInstitutionUID(), selectSite.getSiteUID())
                                || StringUtils.equals(item.getTenantUID(), selectSite.getSiteUID()))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.error("loadClinicalDeptList occur exception", e);
            throw e;
        }
    }

    public void needTwoSubStatusBatch(List<V2_WorkOrder> workOrders){
        workOrders.forEach(workOrder -> {
            V2_WorkOrder_Step workOrder_step = new V2_WorkOrder_Step();
            try {
                workOrder_step = isConfigNeedTwoSubBySiteUid(workOrder.getAssetInfo().getSiteUID()) && (workOrder.getRepairType() == 2 || workOrder.getRepairType() == 3) && workOrder.getCurrentStepId() == 10 ? getRepairPerson(workOrder.getId(), 10):getRepairPerson(workOrder.getId(), 4);
            } catch (Exception e) {
                logger.info("workOrder.info:"+JSONObject.toJSONString(workOrder)+";");
                logger.error("needTwoSubStatusBatch", e);
            }
            workOrder.setRepairName(ObjectUtils.isEmpty(workOrder_step)?"":workOrder_step.getOwnerName());
            workOrder.setRepairPersonId(ObjectUtils.isEmpty(workOrder_step)?null:workOrder_step.getOwnerId());
        });
    }

    public Boolean isConfigNeedTwoSubBySiteUid(String siteUid) {
        WorkflowConfig woc = configService.findConfigBySite(siteUid);
        return woc != null&&woc.getEnableSendRepair()!=null&&woc.getEnableSendRepair();
    }

    public void onFilterWo(FilterEvent event) {
        onFilterCustom(event, this::filters);
    }

    public boolean filters(String fieldName, String filterVal) {
        if ("id".equals(fieldName)) {
            List<String> stepIds = new ArrayList<>();
            List<V2_WorkOrder_Step> steps = woService.getWorkStepsByStepId(4,loginUser.getTenantUID());
            if (!CollectionUtils.isEmpty(steps)) {
                Map<String, List<V2_WorkOrder_Step>> map = steps.stream().collect(Collectors.groupingBy(V2_WorkOrder_Step::getWoId));
                List<V2_WorkOrder_Step> stepList = map.values().stream().map(group -> group.stream()
                                .max(Comparator.comparing(V2_WorkOrder_Step::getStartTime))
                                .orElse(null))
                        .filter(val -> val!=null&&val.getOwnerName().contains(filterVal))
                        .collect(Collectors.toList());
                removeFilterOnField("id");
                stepIds = stepList.stream().map(V2_WorkOrder_Step::getWoId).collect(Collectors.toList());
            }
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.IN, stepIds));
            return true;
        }
        return false;
    }

    public String getDisplayModeCasePriority() {
        return displayModeCasePriority;
    }

    public void setDisplayModeCasePriority(String displayModeCasePriority) {
        this.displayModeCasePriority = displayModeCasePriority;
    }
}

