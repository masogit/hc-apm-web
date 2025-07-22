package com.ge.apm.view.wo.noAsset;

import com.alibaba.fastjson.JSONPath;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.constant.AssetConstant;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.wo.ServiceRequestNoAssetService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.budget.form.BudgetAssetSearchForm;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class ServiceRequestNoAssetController extends GenericCRUDUUIDController<V2_ServiceRequestNoAsset> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ServiceRequestNoAssetController.class);

    private BlobFileService blobService;
    private ServiceRequestNoAssetRepository dao;
    private OrgInfoRepository orgDao;
    private ConfigCacheService cacheService;
    private CustomConfigService ccService;
    private ConfigService configService;
    private UaaService uaaService;
    private UserContextService userContextService;
    private V2_WorkOrderService woService;
    private OrgInfoTreeService orgInfoTreeService;
    private ServiceRequestNoAssetService serviceRequestNoAssetService;
    private BlobObjectRepository blobObjectRepository;

    private V2_WorkOrderRepository woDao;
    private UserAccount loginUser;
    @Getter @Setter
    private Set<String> clinicalDeptOrgUidSet;
    @Getter @Setter
    private String fileterOrgName;
    @Getter@Setter
    private String applyTimeFormatTime = null;
    private List<OrgInfo> clinicalDeptList;
    private OrgInfo selectSite;

    @Getter @Setter
    private String siteUidFilter;
    @Getter @Setter
    private List<OrgInfo> orgList;
    @Getter@Setter
    List<V2_ServiceRequestNoAsset> bySearchFilterInternal = null;
    @Getter@Setter
    private List<V2_BlobObject> picList;
    @Getter@Setter
    private V2_ServiceRequestNoAsset newServiceRequest;
    @Getter @Setter
    private String searchDeptName;
    @Getter @Setter
    private OrgInfo selectedDept;
    @Getter  @Setter
    private TreeNode orgTree;
    @Getter  @Setter
    private OrgInfo org;

    @Getter @Setter
    private V2_ServiceRequest newAssetServiceRequest = new V2_ServiceRequest();
    @Getter @Setter
    private Boolean needConfirmTime = false;
    @Getter @Setter
    private Integer currentStatus = 1;
    @Getter @Setter
    private AssetInfo selectedAsset;
    @Getter @Setter
    private String locationName;

    @Getter@Setter
    private List<V2_BlobObject> picSrList = new ArrayList<>();
    private CustomConfig customConfig;
    private CustomConfigObject currentCustomConfig;
    @Getter@Setter
    private Integer queryIndex;
    @Getter@Setter
    private String applyTimeFrom;

    @Getter @Setter
    private V2_ServiceRequestNoAsset selectedSr;
    @Getter @Setter
    private Date esTime;
    @Getter @Setter
    private String assetStatus;
    @Getter @Setter
    private String cancelReason;
    @Getter @Setter
    private String comments;
    @Getter @Setter
    private AssetInfo selectAsset;

    @Getter  @Setter
    private BudgetAssetSearchForm assetSearchForm = new BudgetAssetSearchForm();
    @Getter  @Setter
    private List<AssetInfo> assetInfoList ;
    @Getter  @Setter
    private Map<String, Boolean> selectedMap = new HashMap<>();
    @Getter  @Setter
    private TreeNode orgTreeSearch;


    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ServiceRequestNoAssetRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        cacheService = WebUtil.getBean(ConfigCacheService.class);
        configService = WebUtil.getBean(ConfigService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        serviceRequestNoAssetService = WebUtil.getBean(ServiceRequestNoAssetService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        selectSite = cacheService.findOrgByUid(loginUser.getSiteUID());
        ccService = WebUtil.getBean(CustomConfigService.class);
        org = orgDao.findById(loginUser.getOrgInfoId());
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
        woDao = WebUtil.getBean(V2_WorkOrderRepository.class);

        customConfig = ccService.getCustomConfigBySite(selectSite.getUid());
        currentCustomConfig = ccService.getCurrentCustomConfig(loginUser.getSiteUID());
        loadClinicalDeptList();
        String query = WebUtil.getRequestParameter("query");
        String encodeStr = WebUtil.getRequestParameter("str");
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        if (selectedId != null) {
            selected = dao.findById(selectedId);
            picList = blobService.findBlobObjectsByBoUid(selectedId);
            currentStatus = selected.getAssetStatus();

        }
        if (Strings.isNullOrEmpty(query)) {
            //云南阜外由默认全部改为默认待办
            queryIndex = 0;
        } else {
            queryIndex = Integer.valueOf(query);
            searchList(queryIndex);
        }
    }

    public void searchList(Integer buttonIndex) {
        queryIndex = buttonIndex;
        this.searchFilters = null;
        cancel();
    }

    @Override
    protected Page<V2_ServiceRequestNoAsset> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        this.searchFiltersByQuery();
        Page<V2_ServiceRequestNoAsset> pageResult = dao.findBySearchFilter(this.searchFilters, pageRequest);
        bySearchFilterInternal = pageResult.getContent();
        return pageResult;
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }

        if (clinicalDeptOrgUidSet != null && !clinicalDeptOrgUidSet.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("deptUid", SearchFilter.Operator.IN, new ArrayList<>(clinicalDeptOrgUidSet), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "deptUid".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        searchFilters.removeAll(searchFilters.stream().filter(item -> "siteUID".equals(item.fieldName)).collect(Collectors.toList()));

        if (!this.hasFilterOnField("siteUID")) {
            if (userContextService.hasRole("AssetHead")) {
                List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).distinct().collect(Collectors.toList());
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            } else {
                List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            }
        }
    }

    public void searchFiltersByQuery() {
        if(queryIndex == 0){
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId()));
        }else if (queryIndex == 1) { //已报修
            searchFilters.add(new SearchFilter("requestorId", SearchFilter.Operator.EQ, loginUser.getId()));
        }else if (queryIndex == 2) { //待派工
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 2));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId()));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 4)));
        }else if (queryIndex == 3) { //待接单
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.IN, Arrays.asList(2, 3)));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 3), "1*3+4*"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.NE, loginUser.getId(), "1*3+4*A+"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId(), "1*3+4*A+B*"));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 2, "1*3+4*A+B*"));
            //去除派工模式4且待派工的单子
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId(), "1*3+5*"));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 3, "1*3+5*"));
            //当前责任人是自己且已经派工的单子
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId(), "1*2+6*"));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 3, "1*2+6*"));
        } if (queryIndex == 4) {
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 4));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId()));
        } if (queryIndex == 5) {
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 5));
            /*searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId()));*/
        }
    }

    public String getStepName(V2_ServiceRequestNoAsset serviceRequestNoAsset){
        Integer stepId =serviceRequestNoAsset.getCurrentStepId();
        switch(stepId){
            case 2:
                return "待派工";
            case 3:
                return "待接单";
            case 4:
                return "待关单";
            case 5:
                return "已关单";
            case 9:
                return "已取消";
            default:
                return "";
        }
    }

    public String getTurnWoFlag(V2_ServiceRequestNoAsset serviceRequestNoAsset){
        if(StrUtil.isEmpty(serviceRequestNoAsset.getSrId())){
            return "否";
        }else{
            return "是";
        }
    }

    private void loadClinicalDeptList() {
        try {
            clinicalDeptList = Collections.emptyList();
            List<OrgInfo> deptList = uaaService.getAllAccessibleClinicalDeptList();
            if (!CollectionUtils.isEmpty(deptList)) {
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

    public void onSelectRow() throws IOException {
        if (null == selected) {
            return;
        }

        String url = "actionName=View&selectedid="
                + selected.getId();
        url = "view.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());


        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            fileterOrgName = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(fileterOrgName) && !clinicalDeptList.stream().anyMatch(item -> fileterOrgName.equals(item.getName()))) {
            fileterOrgName = null;
        }

        if (fileterOrgName != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(fileterOrgName)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
        }
    }

    public List<OrgInfo> LoadSiteNameList() {
        if (CollectionUtils.isEmpty(orgList)) {
            if (!CollectionUtils.isEmpty(bySearchFilterInternal)) {
                orgList = bySearchFilterInternal.stream().map(item -> item.getSiteUID()).collect(Collectors.toSet()).stream()
                        .map(val -> cacheService.findOrgByUid(val)).collect(Collectors.toList());
            }
            if (!CollectionUtils.isEmpty(orgList)) {
                orgList = orgList.stream().filter(item -> 3 == item.getOrgType()).collect(Collectors.toList());
            }
        }
        return orgList;
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

    public void createServiceRequest() {
        CustomConfigObject currentCustomConfig = ccService.getCurrentCustomConfig(loginUser.getSiteUID());
        //从配置信息获取当前状态
        String strPriority =JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.group[type='orderFields'].priority.defaultValue[0]").toString();

        newServiceRequest = new V2_ServiceRequestNoAsset();
        picList = new ArrayList();
        newServiceRequest.setCasePriority(Integer.valueOf(strPriority));//紧急程度默认是设置为一般
        currentStatus = 1;
        newServiceRequest.setDeptUid(loginUser.getOrgInfoUID());
        newServiceRequest.setRequestorName(loginUser.getName());
        newServiceRequest.setRequestorPhone(loginUser.getTelephone());
        newServiceRequest.setRequestTime(new Date());
        OrgInfo orgInfo = orgDao.findByUid(loginUser.getOrgInfoUID());
        if (orgInfo.getOrgType() == 4) {
            newServiceRequest.setDeptName(orgInfo.getName());
            newServiceRequest.setDeptUid(orgInfo.getUid());
        }
        OrgInfo orgArea = getCourtyardAreaOrg(orgInfo);
        newServiceRequest.setSiteUID(orgArea.getUid());
        newServiceRequest.setSiteName(orgArea.getName());

        newServiceRequest.setSiteUID(loginUser.getSiteUID());

        TreeNode treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(loginUser.getOrgInfoUID(), loginUser.getUid(), 4);
        orgTree = initOrgTree(treeNodeEdit);
        selected = null;
    }

    public TreeNode initOrgTree(TreeNode treeNode) {
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        TreeNode orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.setSelectable(false);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        } else {
            // 用户在科室级别时, 可选择所在科室
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setSelectable(org.getOrgType() == 4);
            node.setRowKey("org_" + org.getId());
            rootNode.getChildren().add(node);
        }
        rootNode.setExpanded(true);
        return orgTree;
    }


    public OrgInfo getCourtyardAreaOrg(OrgInfo orgInfo) {
        if (orgInfo != null && orgInfo.getOrgLevel() == orgInfo.getOrgType()) {
            return orgInfo;
        }
        return getCourtyardAreaOrg(orgInfo.getParentOrg());
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

    public void handleFileUpload(FileUploadEvent event) {
        synchronized (picList){
            if(picList != null && picList.size() >= 5){
                WebUtil.addWarnMessage("上传图片数量不能超过5个！");
                return;
            }
            V2_BlobObject blobObject = blobService.uploadFile(event);
            if (null != blobObject) {
                picList.add(blobObject);
                blobObject.setBoType(V2_BlobObject.BO_TYPE_SR_NO_ASSET);
                blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
                blobService.saveBlobObject(blobObject);
            } else {
                WebUtil.addErrorMessage("fail to upload file");
            }
        }
    }

    public void handleFileUploadAssetSr(FileUploadEvent event) {
        synchronized (picSrList){
            if(picSrList != null && picSrList.size() >= 5){
                WebUtil.addWarnMessage("上传图片数量不能超过5个！");
                return;
            }
            V2_BlobObject blobObject = blobService.uploadFile(event);
            if (null != blobObject) {
                picSrList.add(blobObject);
                blobObject.setBoType(V2_BlobObject.BO_TYPE_ServiceRequest);
                blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
                blobService.saveBlobObject(blobObject);
            } else {
                WebUtil.addErrorMessage("fail to upload file");
            }
        }
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        selectedDept = orgInfo;
        this.newServiceRequest.setDeptName(selectedDept.getName());
        this.newServiceRequest.setDeptUid(selectedDept.getUid());
    }

    public void onStatusChange() {
        if (newServiceRequest == null) {
            return;
        }
        if(currentStatus==2){ //选择停机状态
            needConfirmTime=true;
        }
        if(currentStatus==3){//选择 限制功能使用
            needConfirmTime=false;
        }
    }

    public List<V2_ServiceRequestNoAssetStep> getSrStep(String id){
        return serviceRequestNoAssetService.queryStepBySrId(selected.getId());
    }

    public void createServiceRequestByNoAsset(V2_ServiceRequestNoAsset serviceRequestNoAsset) {

        CustomConfigObject currentCustomConfig = ccService.getCurrentCustomConfig(loginUser.getSiteUID());
        Object repairReportSendPlace=JSONPath.eval(currentCustomConfig, "mobilePage.pageWorkOrder.group[type='orderFields'].repairReportSendPlace.defaultValue[0]");
        String strAssetStatus =JSONPath.eval(currentCustomConfig,"$.mobilePage.pageWorkOrder.group[type='orderFields'].assetStatus.defaultValue[0]").toString();
        newAssetServiceRequest = new V2_ServiceRequest();
        picSrList = new ArrayList();
        currentStatus= Integer.valueOf(strAssetStatus);
        newAssetServiceRequest.setStatus(currentStatus);
        newAssetServiceRequest.setRequestorId(loginUser.getId());
        OrgInfo depOrg = orgDao.findByUid(serviceRequestNoAsset.getDeptUid());
        newAssetServiceRequest.setFromDeptId(depOrg.getId());
        newAssetServiceRequest.setRequestorName(serviceRequestNoAsset.getRequestorName());
        newAssetServiceRequest.setRequestorPhone(serviceRequestNoAsset.getRequestorPhone());
        newAssetServiceRequest.setRequestTime(serviceRequestNoAsset.getRequestTime());
        newAssetServiceRequest.setRequestReason(serviceRequestNoAsset.getRequestReason());

        picSrList.addAll(picList);

        if(repairReportSendPlace != null){
            newAssetServiceRequest.setRepairReportSendPlace(repairReportSendPlace.toString());
        }
        if(selectedAsset != null && userContextService.isCountyArea()){
            OrgInfo orgInfo = cacheService.findOrgByUid(selectedAsset.getSiteUID());
            newAssetServiceRequest.setCustomerName(orgInfo.getContactPerson());
            newAssetServiceRequest.setCustomerTel(orgInfo.getContactPhone());
            newAssetServiceRequest.setHospitalName(orgInfo.getName());//这里只需要开单页面显示，asset微服务会保存医院Uid和名称。
        }
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

        if(newAssetServiceRequest != null && userContextService.isCountyArea()){
            OrgInfo orgInfo = cacheService.findOrgByUid(asset.getSiteUID());
            newAssetServiceRequest.setCustomerName(orgInfo.getContactPerson());
            newAssetServiceRequest.setCustomerTel(orgInfo.getContactPhone());
            newAssetServiceRequest.setHospitalName(orgInfo.getName());
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

    public void saveServiceRequestAsset() {
        if(userContextService.isCountyArea()){
            String msg = null;
            if(newAssetServiceRequest.getSiebleNumber() != null && newAssetServiceRequest.getSiebleNumber().length() != 13){
                msg = WebUtil.getMessage("siebleNumber")+"长度必须填为13位";
            }
            if(newAssetServiceRequest.getCallCenterNumber() != null && newAssetServiceRequest.getCallCenterNumber().length() != 10){
                msg = WebUtil.getMessage("callCenterNumber")+"长度必须填为10位";
            }
            if(StringUtils.isNotBlank(msg)){
                WebUtil.addErrorMessage(msg);
                //前端弹窗不退出并提示错误信息
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                return;
            }
        }
        if (null == selectedAsset) {
            WebUtil.addErrorMessage(WebUtil.getMessage("NoEquipmentSelectedNoRepairTip"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
            return;
        }
        WorkflowConfig woc = configService.findConfigBySite(selectedAsset.getSiteUID());
        if (woc != null && woc.getIsWoSupplierAutoEnabled() !=null && woc.getIsWoSupplierAutoEnabled()) {
            this.saveServiceRequestSupplier();
        }
        if(StringUtils.isEmpty(newAssetServiceRequest.getRequestReason())){
            newAssetServiceRequest.setRequestReason(WebUtil.getMessage("EquipmentFault"));
        }

        if (selectedAsset.getStatus() == 4) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ToBeTerminatedCannotBeRequestedForRepairTip"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else if (Strings.isNullOrEmpty(selectedAsset.getClinicalDeptUID())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("NoAssetDepartmentNoRepaired"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else {
            selectedAsset.setStatus(currentStatus);
            newAssetServiceRequest.setAssetLocation(locationName);
            newAssetServiceRequest.setSrRepairSource(RepairSourceEnum.Accounts.name());
            woService.createServiceRequest(newAssetServiceRequest, selectedAsset, picSrList,null);
            cancel();
        }
    }

    public void saveServiceRequestSupplier() {
        if(StringUtils.isEmpty(newAssetServiceRequest.getRequestReason())){
            newAssetServiceRequest.setRequestReason(WebUtil.getMessage("EquipmentFault"));
        }
        if(selectedAsset.getSupplierNewUID() == null){
            WebUtil.addErrorMessage("设备未关联供应商,无法使用供应商派工");
            return;
        }
        Map<String, Object> map = woService.findSupplierFEByAssetUid(selectedAsset);
        List<UserAccount>  data = (List<UserAccount>)map.get("data");
        if(ObjectUtils.isEmpty(data)){
            WebUtil.addErrorMessage("设备未绑定供应商维修工程师");
            return;
        }
        int size = data.size();
        if(size>1){
            WebUtil.addErrorMessage("设备绑定工程师过多");
            return;
        }
        if (null == selectedAsset) {
            WebUtil.addErrorMessage(WebUtil.getMessage("NoEquipmentSelectedNoRepairTip"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
            return;
        }
        if (selectedAsset.getStatus() == 4) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ToBeTerminatedCannotBeRequestedForRepairTip"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else if (Strings.isNullOrEmpty(selectedAsset.getClinicalDeptUID())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("NoAssetDepartmentNoRepaired"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else {
            selectedAsset.setStatus(currentStatus);
            newAssetServiceRequest.setAssetLocation(locationName);
            newAssetServiceRequest.setSrRepairSource(RepairSourceEnum.Accounts.name());

            woService.createServiceRequest(newAssetServiceRequest, selectedAsset, picSrList,2);
            cancel();
        }
    }

    public void cancelAsset(){
        newAssetServiceRequest = null;
    }


    public void saveServiceRequest(){
        try{
            newServiceRequest.setAssetStatus(currentStatus);
            serviceRequestNoAssetService.saveServiceRequest(newServiceRequest, picList);
            WebUtil.addSuccessMessage("工单创建成功！");
        }catch (Exception ex){
            WebUtil.addErrorMessage("工单创建失败！");
        }
    }

    public void cancel(){
        newServiceRequest = null;
        selectAsset = null;
        comments = "";
    }

    public void prepareAct(String id) {
        selectedSr = dao.findById(id);
        needConfirmTime = false;
        Long time = System.currentTimeMillis();
        time += 120*1000*60;
        esTime = new Date(time);
    }

    public void prepareTurnWorkOrder(String id){
        prepareAct(id);
        TreeNode treeNodeSearch = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelectedForReturn(loginUser.getSiteUID(), null);
        orgTreeSearch = initOrgTree(treeNodeSearch);
        OrgInfo org = orgDao.findByUid(selectedSr.getDeptUid());
        assetSearchForm.setApplicationDeptId(selectedSr.getDeptUid());
        assetSearchForm.setDept(org);
        assetSearchForm.setDeptName(selectedSr.getDeptName());
    }
    public void onSearchTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        assetSearchForm.setDept(orgInfo);
        assetSearchForm.setApplicationDeptId(orgInfo.getUid());
        assetSearchForm.setDeptName(orgInfo.getName());
    }

    public void treeSearchCondition(){
        TreeNode treeNodeEdit = null;
        if(StrUtil.isEmpty(searchDeptName)){
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(loginUser.getOrgInfoUID(), loginUser.getUid(), 4);
        }else{
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(loginUser.getOrgInfoUID(), loginUser.getUid(), 4, searchDeptName);
        }
        orgTree = initOrgTree(treeNodeEdit);
    }

    public void cancelSrNoAsset(){
        serviceRequestNoAssetService.cancelSrNoAsset(selectedSr.getId(), cancelReason, assetStatus);
        cancelReason = null;
        cancel();
    }

    public List<UserAccount> getAssigneeUsers(){
        if(selectedSr != null){
            return serviceRequestNoAssetService.getAssigneeUsersBySite(selectedSr.getSiteUID());
        }
        return new ArrayList<>();
    }

    public void dispatchSrNoAsset() {
        serviceRequestNoAssetService.dispatchSrNoAsset(selectedSr);
        cancel();
    }

    public void acceptSrNoAsset(){
        Integer repairType = selectedSr.getRepairType();
        serviceRequestNoAssetService.acceptSrNoAsset(selectedSr, esTime, repairType, comments);
        cancel();
    }

    public void reassignSrNoAsset() {
        if(StringUtils.isEmpty(comments)){
            comments=WebUtil.getMessage("WorkRequirements");
        }
        serviceRequestNoAssetService.reassignSrNoAsset(selectedSr, selectedSr.getCurrentPersonId(), comments);
        cancel();
    }

    public void closeSrNoAsset() {
        serviceRequestNoAssetService.closeSrNoAsset(selectedSr, comments);
        cancel();
    }

    public void searchAsset(){
        if(StrUtil.isEmpty(assetSearchForm.getApplicationDeptId())){
            WebUtil.addErrorMessage("清输入申请科室！");
            return;
        }
        assetInfoList = serviceRequestNoAssetService.queryAssetByCondition(loginUser, assetSearchForm);
    }

    public void resetAssetSearch(){
        assetSearchForm = new BudgetAssetSearchForm();
    }

    public void saveSelectedAsset(){
        if(selectAsset == null){
            WebUtil.addErrorMessage("请选择设备！");
            return;
        }
        turnWorkOrder();
    }

    public void turnWorkOrder() {
        serviceRequestNoAssetService.turnWorkOrder(selectedSr, selectAsset.getUid());
        cancel();
    }

    public void workOrderView(String id){
        String url = "actionName=View&selectedid=" + id + "&queryIndex=0";
        url = "../../woMgr/workOrderList.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }


    @Override
    protected GenericRepositoryUUID<V2_ServiceRequestNoAsset> getDAO() {
        return dao;
    }
}
