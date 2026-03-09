package com.ge.apm.view.adverseEvent;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.adverseEvent.AdverseEventsService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.nurseSite.WordReportExportService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MessageUtil;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.TempFileUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AdverseEventManagementController extends AdverseEventBaseController {
    private Logger log = Logger.getLogger(AdverseEventManagementController.class.getName());
    /*接口类*/
    private AdverseEventsRepository adverseEventsRepository;
    private AssetInfoRepository assetInfoRepository;
    private OrgInfoRepository orgInfoRepository;
    private I18nMessageRepository i18nDao;
    private UserContextService userContextService;
    private OrgInfoTreeService orgInfoTreeService;
    private UaaService uaaService;
    private CustomConfigService ccService;
    private WordReportExportService exportService;
    private AdverseEventServiceClient adverseEventServiceClient;
    private ConfigCacheService configCacheService;
    private MultiLevelApprovalRepository multiLevelApprovalRepository;
    private SystemTemplateService systemTemplateService;
    /*属性类*/
    static PageRequest pageRequest = new PageRequest(0, 1000);
    @Getter@Setter
    private List<AssetInfo> assets;
    @Getter@Setter
    private OrgInfo selectSite; //选定的所属院区
    @Getter@Setter
    private AssetInfo assetSelected;
    @Getter@Setter
    private String filterClinicalDeptOrg; //选定的所属科室
    @Getter@Setter
    private Set<String> clinicalDeptOrgUidSet;
    @Getter@Setter
    private String findByStr; //设定搜索字符
    @Getter@Setter
    protected AdverseEvents selected;
    @Getter@Setter
    private Integer assetType = 1;
    @Getter@Setter
    private Integer assetGroup;
    @Getter@Setter
    private List<I18nMessage> assetGroupSecondaryList;
    @Getter@Setter
    private Map<String, Boolean> selectedMap;
    private boolean canExport;
    @Getter@Setter
    private Sort lastPageSort; //export use
    @Getter@Setter
    private List<AdverseEvents> currentPageAdverse;
    @Getter@Setter
    private List<OrgInfo> clinicalDeptList;
    @Getter@Setter
    private String functionType; //设备型号
    @Getter@Setter
    private String assetName; //设备名称 or 二维码
    @Setter
    private TreeNode deptOrgTree;
    @Getter@Setter
    private OrgInfo selectAdverseSite; //选定的所属院区


    private AdverseEventsService adverseEventsService;
    private boolean renderedSearchForm = true;

    private boolean readOnlyCreateForm = false;
    @Getter@Setter
    private String eventStatusFilter = "";
    @Getter@Setter
    private String queryStr = "";
    @Getter@Setter
    private StreamedContent exportPdf;
    @Getter@Setter
    private String[] selectedOptions;

    private ConfigItemValueRepository configItemValueRepository;

    private TenantInfoRepository tenantInfoRepository;

    String configItemValue;
    
    private UserAccount currentUser;

    @Getter@Setter
    private StreamedContent excelFile;

    @Setter
    @Getter
    private List<MultiLevelApproval> multiLevelApprovals;

    @Override
    protected void init() {
        /*接口类*/
        adverseEventsRepository = WebUtil.getBean(AdverseEventsRepository.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        exportService = WebUtil.getBean(WordReportExportService.class);
        adverseEventServiceClient = WebUtil.getBean(AdverseEventServiceClient.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        adverseEventsService = WebUtil.getBean(AdverseEventsService.class);
        multiLevelApprovalRepository = WebUtil.getBean(MultiLevelApprovalRepository.class);
        systemTemplateService = WebUtil.getBean(SystemTemplateService.class);

        /*属性类*/
        selectSite = userContextService.getLoginUserOrgInfo();
        if (selectSite.getOrgType() == 4) {
            selectSite = configCacheService.findOrgByUid(selectSite.getSiteUID());
        }
        selectAdverseSite = userContextService.getLoginUserOrgInfo();
        String selectedId = WebUtil.getRequestParameter("selectedId");
        this.selectedMap = new HashMap();
        injuryPerformance = new ArrayList<>();
        mechanicalFailure = new ArrayList<>();
        currentUser = UserContextService.getCurrentUserAccount();
        /*初始化方法*/
        //configItemValue = configItemMapper.findByKey("setAdverseEventProductType", currentUser.getSiteUID());
        configItemValue = configItemValueRepository.findByKeyAndSiteForUseParent(currentUser.getSiteUID(),currentUser.getHospitalUID(), currentUser.getInstitutionUID(), currentUser.getTenantUID(), "setAdverseEventProductType");
        configItemValue = StrUtil.isEmpty(configItemValue) ? "1" : configItemValue;
        loadAdverseEventById(selectedId);
        loadClinicalDeptList();
    }

    public void loadAdverseEventById(String id) {
        if (StringUtils.isEmpty(id)) {
            this.selected = new AdverseEvents();
            this.selected.setReporterName(currentUser.getName());
            this.selected.setReportDate(new Date());
            this.selected.setDepartment(selectSite.getUid());
            //发生地默认【境内】
            this.selected.setOccurredPlace("境内");
            //默认不良事件工单类型
            this.selected.setAdverseEventType("1");
            if(StrUtil.isNotEmpty(configItemValue) && "3".equals(configItemValue)){
                assetType = 2;
            }
            loadUsePlace();
        } else {
            selected = adverseEventServiceClient.getAdverseEventsById(UserContextService.getAccessToken(), id);
            if (selected.getStatus() != null && (selected.getStatus() == 1 || selected.getStatus() == 4)) {
                renderedSearchForm = true;
                readOnlyCreateForm = false;
            } else {
                renderedSearchForm = false;
                readOnlyCreateForm = true;
            }
            //设备类型赋值
            assetType = selected.getDevicesClass() == null ? 1 : selected.getDevicesClass();
            //事件原因分析由字符串转为数组
            if(StrUtil.isNotEmpty(selected.getEventCauseAnalyze())){
                selectedOptions = selected.getEventCauseAnalyze().split(",");
            }
            multiLevelApprovals = multiLevelApprovalRepository.findByBusinessUid(selected.getUid());
        }
    }

    public void loadUsePlace(){
        String usePlaceConfigValue = configItemValueRepository.findByKeyAndSiteForUseParent(currentUser.getSiteUID(),currentUser.getHospitalUID(), currentUser.getInstitutionUID(), currentUser.getTenantUID(), "enableInsertUsePlace");
        if(StrUtil.isNotEmpty(usePlaceConfigValue) && "true".equals(usePlaceConfigValue)){
            TenantInfo tenantInfo = tenantInfoRepository.findByuid(currentUser.getTenantUID());
            selected.setPlaceName(tenantInfo == null ? "" : tenantInfo.getName());
        }
    }

    public boolean assetTypeDisabledCheck(String type){
        if("1".equals(type)){
            if(configItemValue == null || StrUtil.isEmpty(configItemValue)){
                return false;
            }else if(AdverseEvents.ALL.equals(configItemValue)){
                return false;
            }else if(AdverseEvents.EQUIPMENT.equals(configItemValue)){
                return false;
            }else{
                return true;
            }
        }else{
            if(configItemValue == null || StrUtil.isEmpty(configItemValue)){
                return true;
            }else if(AdverseEvents.ALL.equals(configItemValue)){
                return false;
            }else if(AdverseEvents.CONSUMABLES.equals(configItemValue)){
                return false;
            }else{
                return true;
            }
        }
    }

    public void setUserSearchFilters() {
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
        this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        if (!ObjectUtils.isEmpty(selectSite)) {
            String[] fields = new String[]{"siteUID", "hospitalUID", "institutionUID"};
            Arrays.asList(fields).forEach(field -> this.updateSearchFilter(new SearchFilter(field, SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+")));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        }
        if (!StringUtils.isEmpty(findByStr)) {
            String[] fields = new String[]{"name", "assetGroup", "functionType"};
            Arrays.asList(fields).forEach(field -> {
                this.updateSearchFilter(new SearchFilter(field, SearchFilter.Operator.LIKE,  findByStr, "1*3+"));
                if ("assetGroup".equals(field)) {
                    if (!org.apache.commons.lang3.StringUtils.isNumeric(findByStr)) {
                        this.removeFilterOnField("assetGroup");
                    }
                }
            });
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*3+")).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(clinicalDeptOrgUidSet)) {
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, clinicalDeptOrgUidSet.stream().findFirst().get(), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (null != assetGroup) {
            this.updateSearchFilter(new SearchFilter("assetGroup", SearchFilter.Operator.EQ,  assetGroup, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetGroup".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (!StringUtils.isEmpty(functionType)) {
            this.updateSearchFilter(new SearchFilter("functionType", SearchFilter.Operator.LIKE,  functionType, "1*3+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "functionType".equals(item.fieldName) && item.conditionPath.equals("1*3+")).collect(Collectors.toList()));
        }
        if (!StringUtils.isEmpty(assetName)) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE,  assetName, "1*3+"));
            this.updateSearchFilter(new SearchFilter("qrCode", SearchFilter.Operator.LIKE,  assetName, "1*3+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "name".equals(item.fieldName) && item.conditionPath.equals("1*3+")).collect(Collectors.toList()));
            searchFilters.removeAll(searchFilters.stream().filter(item -> "qrCode".equals(item.fieldName) && item.conditionPath.equals("1*3+")).collect(Collectors.toList()));

        }
    }

    //获取设备列表
    @Override
    protected Page<AdverseEvents> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;
        if (!CollectionUtils.isEmpty(clinicalDeptOrgUidSet)) {
            searchFilters.add(new SearchFilter("department",SearchFilter.Operator.EQ, clinicalDeptOrgUidSet.iterator().next()));
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for(SearchFilter searchFilter : searchFilters){
            Map<String, Object> mm = new HashMap<>();
            mm.put("fieldName", searchFilter.fieldName);
            if("reportDate".equals(searchFilter.fieldName)){
                mm.put("valueDate", searchFilter.value);
            }else{
                mm.put("valueString", searchFilter.value);
            }
            mm.put("operator", searchFilter.operator.name());
            list.add(mm);
        }
        Page<AdverseEvents> pageResult = adverseEventServiceClient.getAdverseEventsById(UserContextService.getAccessToken(), pageRequest, eventStatusFilter, queryStr, "report", list);
        currentPageAdverse = pageResult.getContent();
        return pageResult;
    }

    public String reporterList(String uid) {
        return Optional.ofNullable(configCacheService.findOrgByUid(uid)).orElse(new OrgInfo()).getName();
    }

    public void setEventStatus(String status) {
        queryStr = "";
        selectedMap.clear();
        this.eventStatusFilter = status;
    }

    public void setQueryStr(String queryStr) {
        selectedMap.clear();
        this.queryStr = queryStr;
    }

    public void assetList() {
        /*if (StringUtils.isEmpty(assetName) && StringUtils.isEmpty(functionType) && assetGroup == null && CollectionUtils.isEmpty(clinicalDeptOrgUidSet)) {
            MessageUtil.outPutReturnFacesMessage("AdverseSearchRequire", FacesMessage.SEVERITY_INFO);
        }*/
        searchFilters = new ArrayList<>();
        setUserSearchFilters();
        Page<AssetInfo> page = assetInfoRepository.findBySearchFilter(this.searchFilters, pageRequest);
        assets = page.getContent();
        unSelect();
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            selectSite = (OrgInfo) event.getTreeNode().getData();
        }
        loadClinicalDeptList();
    }

    public void onAdverseSelectSiteTreeNode(NodeSelectEvent event) {
        if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            selectAdverseSite = (OrgInfo) event.getTreeNode().getData();
        }
        selected.setDepartment(selectAdverseSite.getUid());
    }

    public void onSelectAdverseEvent(Integer id) throws IOException {
        String url = "selectedId=" + id;
        url = "adverseEventCreate.xhtml?" + url;
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public TreeNode getDeptOrgTree() {
        return uaaService.getOrgAccessTreeByUser(UserContextService.getCurrentUserAccount());
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageAdverse.stream().anyMatch(item -> !selectedMap.containsKey(item.getWorkOrderNum()) || !selectedMap.get(item.getWorkOrderNum()));
        for (AdverseEvents item : currentPageAdverse) {
            selectedMap.put(item.getWorkOrderNum(), !isSelectedAll);
        }
    }

    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            filterClinicalDeptOrg = (String) ((SelectEvent) event).getObject();
        }
        if (!Strings.isNullOrEmpty(filterClinicalDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterClinicalDeptOrg.equals(item.getName()))) {
            filterClinicalDeptOrg = null;
        }
        if (filterClinicalDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterClinicalDeptOrg)).collect(Collectors.toList());
            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
        }
    }

    public void setOneSearchStr(String findByStr) {
        this.findByStr = findByStr;
        if (StringUtils.isEmpty(findByStr)) {
            this.removeFilterOnField("name");
            this.removeFilterOnField("assetGroup");
            this.removeFilterOnField("functionType");
        }
    }

    public void onSelect(SelectEvent event) {
        this.selected.setAssetName(assetSelected.getName());
        this.selected.setDepartment(assetSelected.getClinicalDeptUID());
        this.selected.setOriginPlace(assetSelected.getProductPlace());
        this.selected.setDeviceNum(assetSelected.getDepartNum());
        this.selected.setRegistrationNum(assetSelected.getRegistrationNo());
        this.selected.setProductName(assetSelected.getName());
        this.selected.setPreviouslyRegistrationReport(false);
        this.selected.setProductionDate(assetSelected.getManufactDate());
        this.selected.setModel(assetSelected.getFunctionType());
        this.selected.setProductNum(assetSelected.getSerialNum());
        //设备截止日期
        if(assetType == 1){
            //this.selected.setValidUntil(assetSelected.getAssetLifecycleDeadline());
            if(assetSelected.getManufactDate() != null && assetSelected.getLifecycle() != null){
                Date d = DateUtil.offsetMonth(assetSelected.getManufactDate(), assetSelected.getLifecycle());
                d = DateUtil.offsetDay(d, -1);
                this.selected.setValidUntil(d);
            }
        }
    }

    public void unSelect() {
        this.selected.setAssetName(null);
        this.selected.setDepartment(null);
        this.selected.setOriginPlace(null);
        this.selected.setDeviceNum(null);
        this.selected.setRegistrationNum(null);
        this.selected.setPreviouslyRegistrationReport(null);
        this.selected.setProductionDate(null);
        this.selected.setModel(null);
        this.selected.setProductName(null);
    }



    public void tempSaveAdverseEventCreate(Integer status) throws RuntimeException {
        if (status == 0) {
            selected.setStatus(0);
            readOnlyCreateForm = false;
            renderedSearchForm = true;
            return;
        }

        Integer oldStatus = selected.getStatus();
        processFile(selected);
        selected.setStatus(status);
        selected.setDevicesClass(assetType);

        //事件原因分析由数组转为字符串（可编辑状态下）
        if(status == 0 || status == 1 || status == 2){
            if(selectedOptions != null && selectedOptions.length > 0){
                String str4 = org.apache.commons.lang.StringUtils.join(selectedOptions, ",");
                selected.setEventCauseAnalyze(str4);
            }
        }

        try {
            AdverseEvents result = adverseEventServiceClient.tempSaveAdverseEvents(UserContextService.getAccessToken(), selected);
            if (!ObjectUtils.isEmpty(result)) {
                selected = result;
                if (status == 2 || status == 5) {
                    //提交后页面设置为只读并隐藏设备查询列表
                    readOnlyCreateForm = true;
                    renderedSearchForm = false;
                }
                injuryPerformance.clear();
                mechanicalFailure.clear();
                if(CollectionUtils.isEmpty(selected.getHarmPerformFile())){
                    selected.setHarmPerformFile(new ArrayList<>());
                }
                if(CollectionUtils.isEmpty(selected.getDeviceFaultPerformFile())){
                    selected.setDeviceFaultPerformFile(new ArrayList<>());
                }
                MessageUtil.outPutReturnFacesMessage("保存成功", FacesMessage.SEVERITY_INFO);
            }
        } catch (Exception e) {
            selected.setStatus(oldStatus);
            removeAdverseFile();
            String result = e.getMessage();
            if(result.indexOf(";") > -1){
                List<String> r = Arrays.asList(result.split(";"));
                for(String s : r){
                    MessageUtil.outPutReturnFacesMessage(s, FacesMessage.SEVERITY_INFO);
                }
            }else{
                MessageUtil.outPutReturnFacesMessage(e.getMessage(), FacesMessage.SEVERITY_INFO);
            }
        }
    }



    public void removeAdverseFile(){
        List<AdverseEventsFile> list = new ArrayList<>();
        if(selected.getHarmPerformFile() != null){
            for(AdverseEventsFile adverseEventsFile : selected.getHarmPerformFile()){
                if(StrUtil.isEmpty(adverseEventsFile.getAdverseEventsUid())){
                    list.add(adverseEventsFile);
                }
            }
            if (!CollectionUtils.isEmpty(list)) {
                selected.getHarmPerformFile().removeAll(list);
            }
        }

        list = new ArrayList<>();
        if(selected.getDeviceFaultPerformFile() != null){
            for(AdverseEventsFile adverseEventsFile : selected.getDeviceFaultPerformFile()){
                if(StrUtil.isEmpty(adverseEventsFile.getAdverseEventsUid())){
                    list.add(adverseEventsFile);
                }
            }
            if (!CollectionUtils.isEmpty(list)) {
                selected.getDeviceFaultPerformFile().removeAll(list);
            }
        }
    }

    public void downLoadPdf() {
        if (CollectionUtils.isEmpty(selectedMap)) {
            MessageUtil.outPutReturnFacesMessage("请勾选需要的工单", FacesMessage.SEVERITY_INFO);
            return;
        }
        try {
            selectedMap = selectedMap.entrySet().stream()
                    .filter(entry -> entry.getValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            TempFileUtil tempFileUtil = adverseEventServiceClient.downLoadPdf(UserContextService.getAccessToken(), selectedMap.keySet().toArray(new String[selectedMap.size()]));
            exportPdf = new DefaultStreamedContent((new ByteArrayInputStream(tempFileUtil.getContents())), tempFileUtil.getContentType(), tempFileUtil.getFileName());
        } catch (Exception e) {
            MessageUtil.outPutReturnFacesMessage(e.getMessage(), FacesMessage.SEVERITY_INFO);
        }
    }

    public void exportExcel(){
        List<SearchFilter> searchFilters = this.searchFilters;
        if (!CollectionUtils.isEmpty(clinicalDeptOrgUidSet)) {
            searchFilters.add(new SearchFilter("department",SearchFilter.Operator.EQ, clinicalDeptOrgUidSet.iterator().next()));
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for(SearchFilter searchFilter : searchFilters){
            Map<String, Object> mm = new HashMap<>();
            mm.put("fieldName", searchFilter.fieldName);
            if("reportDate".equals(searchFilter.fieldName)){
                mm.put("valueDate", searchFilter.value);
            }else{
                mm.put("valueString", searchFilter.value);
            }
            mm.put("operator", searchFilter.operator.name());
            list.add(mm);
        }
        List<AdverseEvents> adverseEventsList = adverseEventServiceClient.queryAdverseEvents(UserContextService.getAccessToken(),  eventStatusFilter, queryStr, "report",list);
        for(AdverseEvents adverseEvents : adverseEventsList){
            MultiLevelApproval recentApprove = multiLevelApprovalRepository.getRecentApprove(adverseEvents.getUid());
            if (recentApprove != null) {
                adverseEvents.setActualApproverName(recentApprove.getActualApproverName());
                adverseEvents.setActualApproverDate(recentApprove.getActualApproverDate());
                adverseEvents.setApprovalOpinion(recentApprove.getApprovalOpinion());
            }
        }
        InputStream inputStream = adverseEventsService.exportExcel(adverseEventsList);
        excelFile = new DefaultStreamedContent(inputStream, "application/vnd.ms-excel", "AdverseEvents_Export.xlsx");

    }


    public boolean getCanExport() {
        this.canExport = !selectedMap.values().stream().anyMatch(Boolean::booleanValue);
        return canExport;
    }







    public boolean getRenderedSearchForm() {
        return this.renderedSearchForm;
    }

    public boolean getReadOnlyCreateForm() {
        return this.readOnlyCreateForm;
    }

    public boolean isWithdrawnButton(){
        String createUserUid = selected.getCreateUserUID();
        String uid = currentUser.getUid();
        List<MultiLevelApproval> multiLevelApprovals = multiLevelApprovalRepository.findByBusinessTypeAndBusinessUidAndApprovalStatus(ApprovalConstant.BUSINESS_TYPE_ADVERSE_EVENTS, selected.getBusinessUid(),ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
        if(createUserUid.equals(uid)  &&  !CollectionUtils.isEmpty(multiLevelApprovals)  &&  multiLevelApprovals.size()==1 && multiLevelApprovals.get(0).getApprovalLevelNow()==1   && multiLevelApprovals.get(0).getInitiateApproverUid().equals(selected.getCreateUserUID()) ){
            return true;
        }
        return false;
    }

    public  void  adverseEventWithdrawn(){
        AdverseEvents result = adverseEventServiceClient.adverseEventWithdrawn(UserContextService.getAccessToken(), selected);

        if(result!=null && result.getStatus()==1){
            MessageUtil.outPutReturnFacesMessage("撤回成功", FacesMessage.SEVERITY_INFO);

        }


    }



    @Override
    protected GenericRepository<AdverseEvents> getDAO() {
        return adverseEventsRepository;
    }


}
