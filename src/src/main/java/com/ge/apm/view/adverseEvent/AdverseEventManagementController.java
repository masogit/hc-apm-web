package com.ge.apm.view.adverseEvent;

import com.ge.apm.dao.AdverseEventsRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.nurseSite.WordReportExportService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MessageUtil;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.TempFileUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AdverseEventManagementController extends JpaCRUDController<AdverseEvents> {
    private Logger log = Logger.getLogger(AdverseEventManagementController.class.getName());
    /*接口类*/
    private AssetInfoRepository assetInfoRepository;
    private OrgInfoRepository orgInfoRepository;
    private I18nMessageRepository i18nDao;
    private UserContextService userContextService;
    private OrgInfoTreeService orgInfoTreeService;
    private UaaService uaaService;
    private CustomConfigService ccService;
    private WordReportExportService exportService;
    private AdverseEventsRepository adverseEventsRepository;
    private BlobFileService blobService;
    private AdverseEventServiceClient adverseEventServiceClient;
    private ConfigCacheService configCacheService;

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
    private Map<String, Boolean> selectedMap; //复选的设备
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
    @Getter@Setter
    private List<TempFileUtil> injuryPerformance;
    @Getter@Setter
    private List<TempFileUtil> mechanicalFailure;

    private boolean renderedSearchForm = true;

    private boolean readOnlyCreateForm = false;
    @Getter@Setter
    private String eventStatusFilter = "";
    @Getter@Setter
    private String queryStr = "";


    @Override
    protected void init() {
        /*接口类*/
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        exportService = WebUtil.getBean(WordReportExportService.class);
        adverseEventsRepository = WebUtil.getBean(AdverseEventsRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        adverseEventServiceClient = WebUtil.getBean(AdverseEventServiceClient.class);
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
        /*初始化方法*/
        loadAdverseEventById(selectedId);
        loadClinicalDeptList();
    }

    public void loadAdverseEventById(String id) {
        if (StringUtils.isEmpty(id)) {
            this.selected = new AdverseEvents();
            this.selected.setReporterName(UserContextService.getCurrentUserAccount().getName());
            this.selected.setReportDate(new Date());
            this.selected.setDepartment(selectSite.getUid());
        } else {
            selected = adverseEventServiceClient.getAdverseEventsById(UserContextService.getAccessToken(), id);
            if (selected.getStatus() != null && selected.getStatus() == 1) {
                renderedSearchForm = true;
                readOnlyCreateForm = false;
            } else {
                renderedSearchForm = false;
                readOnlyCreateForm = true;
            }
        }
    }

    public void setUserSearchFilters() {
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
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
        return adverseEventServiceClient.getAdverseEventsById(UserContextService.getAccessToken(), pageRequest, eventStatusFilter, queryStr, "report");
    }

    public String reporterList(String uid) {
        return Optional.ofNullable(configCacheService.findOrgByUid(uid)).orElse(new OrgInfo()).getName();
    }

    public void setEventStatus(String status) {
        queryStr = "";
        this.eventStatusFilter = status;
    }

    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
    }

    public void assetList() {
        if (StringUtils.isEmpty(assetName) && StringUtils.isEmpty(functionType) && assetGroup == null && CollectionUtils.isEmpty(clinicalDeptOrgUidSet)) {
            MessageUtil.outPutReturnFacesMessage("AdverseSearchRequire", FacesMessage.SEVERITY_INFO);
        }
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
        boolean isSelectedAll = !currentPageAdverse.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        for (AdverseEvents item : currentPageAdverse) {
            selectedMap.put(item.getUid(), !isSelectedAll);
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

    @Override
    protected GenericRepository<AdverseEvents> getDAO() {
        return adverseEventsRepository;
    }

    public void tempSaveAdverseEvent(Integer status) throws RuntimeException{
        if (status == 0) {
            selected.setStatus(0);
            readOnlyCreateForm = false;
            renderedSearchForm = true;
            return;
        }
        int oldStatus = selected.getStatus();
        processFile();
        selected.setStatus(status);
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
                MessageUtil.outPutReturnFacesMessage("保存成功", FacesMessage.SEVERITY_INFO);
            }
        } catch (Exception e) {
            selected.setStatus(oldStatus);
            MessageUtil.outPutReturnFacesMessage(e.getMessage(), FacesMessage.SEVERITY_INFO);
        }
    }

    private void processFile() {
        if (!CollectionUtils.isEmpty(injuryPerformance)) {
            List<AdverseEventsFile> harmPerformFile = CollectionUtils.isEmpty(selected.getHarmPerformFile())?new ArrayList<>():selected.getHarmPerformFile();
            injuryPerformance.forEach(v -> harmPerformFile.add(saveTempFile(v)));
            selected.setHarmPerformFile(harmPerformFile);
        }
        if (!CollectionUtils.isEmpty(mechanicalFailure)) {
            List<AdverseEventsFile> deviceFaultPerformFile = CollectionUtils.isEmpty(selected.getDeviceFaultPerformFile())?new ArrayList<>():selected.getDeviceFaultPerformFile();
            mechanicalFailure.forEach(v -> deviceFaultPerformFile.add(saveTempFile(v)));
            selected.setDeviceFaultPerformFile(deviceFaultPerformFile);
        }
    }


    public AdverseEventsFile saveTempFile(TempFileUtil tempFile) {
        V2_BlobObject blobObject = blobService.uploadFile(tempFile.getInputStream(), tempFile.getFileName());
        AdverseEventsFile file = new AdverseEventsFile();
        file.setCreatedDate(new Date());
        file.setObjectId(blobObject.getObjectStorageId());
        file.setObjectName(blobObject.getObjectName());
        file.setObjectType(blobObject.getObjectType());
        file.setObjectSize(blobObject.getObjectSize());
        return file;
    }

    public void tempInjuryPerformanceFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        injuryPerformance.add(TempFileUtil.builder()
                .fileName(file.getFileName())
                .contentType(file.getContentType())
                .contents(file.getContents())
                .build());
    }

    public void tempMechanicalFailureFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        mechanicalFailure.add(TempFileUtil.builder()
                .fileName(file.getFileName())
                .contentType(file.getContentType())
                .contents(file.getContents())
                .build());
    }

    public void removeTempHarmPerform(TempFileUtil file) {
        injuryPerformance.remove(file);
    }

    public void removeTempDeviceFaultPerform(TempFileUtil file) {
        mechanicalFailure.remove(file);
    }

    public void removeHarmPerformFile(AdverseEventsFile file) {
        selected.getHarmPerformFile().remove(file);
    }

    public void removeDeviceFaultPerformFile(AdverseEventsFile file) {
        selected.getDeviceFaultPerformFile().remove(file);
    }

    public boolean getRenderedSearchForm() {
        return this.renderedSearchForm;
    }

    public boolean getReadOnlyCreateForm() {
        return this.readOnlyCreateForm;
    }
}
