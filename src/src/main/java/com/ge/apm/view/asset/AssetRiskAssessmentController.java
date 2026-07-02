package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.asset.AssetInfoMgrService;
import com.ge.apm.service.asset.AssetLifecyleService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.nurseSite.WordReportExportService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.MessageUtil;
import com.ge.apm.view.asset.components.BatchRiskAssessment;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.google.common.base.Strings;
import lombok.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
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
import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetRiskAssessmentController extends JpaCRUDController<AssetInfo> {
    private Logger log = Logger.getLogger(AssetRiskAssessmentController.class.getName());

    /*接口类*/
    private AssetInfoRepository assetInfoRepository;
    private OrgInfoRepository orgInfoRepository;
    private I18nMessageRepository i18nDao;
    private UserContextService userContextService;
    private OrgInfoTreeService orgInfoTreeService;
    private AssetInfoMgrService aiMgrService;
    private AssetLifecyleService assetLifecyleService;
    private UaaService uaaService;
    private CustomConfigService ccService;
    private WordReportExportService exportService;

    /*属性类*/
    static PageRequest pageRequest = new PageRequest(0, 1000);
    @Getter@Setter
    private List<AssetInfo> assetInfoList;
    @Getter@Setter
    private DualListModel<AssetInfo> assets = new DualListModel<>(null, new ArrayList<>());
    @Getter@Setter
    private OrgInfo selectSite; //选定的所属院区
    @Getter@Setter
    private String filterClinicalDeptOrg; //选定的所属科室
    @Getter@Setter
    private String filterAssetDeptOrg; //选定的使用科室
    @Getter@Setter
    private Set<String> clinicalDeptOrgUidSet;
    @Getter@Setter
    private Set<String> assetDeptOrgUidSet;
    @Getter@Setter
    private String findByStr; //设定搜索字符
    @Getter@Setter
    protected AssetInfo selected;
    private boolean terminate = false;
    @Getter@Setter
    private Integer assetGroup;
    @Getter@Setter
    private List<I18nMessage> assetGroupSecondaryList;
    @Getter@Setter
    private Map<String, Boolean> selectedMap; //复选的设备
    @Getter@Setter
    private Sort lastPageSort; //export use
    @Getter@Setter
    private List<AssetInfo> currentPageAssets;
    @Getter@Setter
    private List<OrgInfo> clinicalDeptList;
    @Getter@Setter
    private StreamedContent wordFile;
    @Getter@Setter
    private StreamedContent zipFile;
    @Getter@Setter
    List<String> customedColumns;
    @Getter@Setter
    private CustomConfigObject customConfig;
    @Getter@Setter
    private String detectionCycle; //检测周期
    @Getter@Setter
    private String sumRiskScore; //风险总分
    @Getter@Setter
    private Set<String> maintenancePeriod; //保养周期
    @Getter@Setter
    private BatchRiskAssessment batchRiskAssessment;
    @Getter@Setter
    private String functionType; //设备型号
    @Getter@Setter
    private String assetName; //设备名称

    private SystemTemplateService systemTemplateService;

    @Override
    protected void init() {
        /*接口类*/
        systemTemplateService = WebUtil.getBean(SystemTemplateService.class);

        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        assetLifecyleService = WebUtil.getBean(AssetLifecyleService.class);
        aiMgrService = WebUtil.getBean(AssetInfoMgrService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        exportService = WebUtil.getBean(WordReportExportService.class);
        /*属性类*/
        terminate = Boolean.valueOf(WebUtil.getRequestParameter("terminate"));
        this.selectedMap = new HashMap();
        this.maintenancePeriod = new HashSet<>();
        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        if (!StringUtils.isEmpty(actionName) && "Edit".equals(actionName)) {
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            loadAssetInfo(selectedId);
        } else if ("Create".equals(actionName)) {
            batchRiskAssessment = BatchRiskAssessment.builder()
                    .accidentHistoryScore(0)
                    .clinicalFunctionScore(0)
                    .problemAvoidanceRateScore(0)
                    .specialRequirementScore(0)
                    .tangibleRiskScore(0)
                    .build();
            batchSumScore(batchRiskAssessment);
        }
        assets.setSource(new ArrayList<>());
        /*初始化方法*/
        loadClinicalDeptList();
        loadCustomConfig();
        loadMaintenancePeriod();
    }

    public void loadAssetInfo(String uid) {
        setSelected(assetInfoRepository.findByUid(uid));
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
        if (StringUtils.hasText(findByStr)) {
            String[] fields = new String[]{"name", "functionType"};
            Arrays.asList(fields).forEach(field -> {
                this.updateSearchFilter(new SearchFilter(field, SearchFilter.Operator.LIKE,  findByStr, "1*3+"));
            });
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*3+")).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(clinicalDeptOrgUidSet)) {
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, clinicalDeptOrgUidSet.stream().findFirst().get(), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(assetDeptOrgUidSet)) {
            this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.EQ, assetDeptOrgUidSet.stream().findFirst().get(), "1*2+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*2+")).collect(Collectors.toList()));
        }
    }

    public void editSearchFilters() {
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        if (!ObjectUtils.isEmpty(selectSite)) {
            String[] fields = new String[]{"siteUID", "hospitalUID", "institutionUID"};
            Arrays.asList(fields).forEach(field -> this.updateSearchFilter(new SearchFilter(field, SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+")));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(clinicalDeptOrgUidSet)) {
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, clinicalDeptOrgUidSet.stream().findFirst().get(), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(assetDeptOrgUidSet)) {
            this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.EQ, assetDeptOrgUidSet.stream().findFirst().get(), "1*2+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*2+")).collect(Collectors.toList()));
        }
        if (null != assetGroup) {
            this.updateSearchFilter(new SearchFilter("assetGroup", SearchFilter.Operator.EQ,  assetGroup, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetGroup".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (!StringUtils.isEmpty(functionType)) {
            this.updateSearchFilter(new SearchFilter("functionType", SearchFilter.Operator.LIKE,  functionType, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "functionType".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (!StringUtils.isEmpty(assetName)) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE,  assetName, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "name".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
    }

    //获取设备列表
    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        selected = null;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        setUserSearchFilters();
        searchFilters.add(new SearchFilter("isRiskAssessment", SearchFilter.Operator.EQ, Boolean.TRUE));
        Page<AssetInfo> res = assetInfoRepository.findBySearchFilter(this.searchFilters, pageRequest);
        lastPageSort = pageRequest.getSort(); //export excel use
        currentPageAssets = res.getContent();
        return res;
    }

    public DualListModel<AssetInfo> assetList() {
        if (StringUtils.isEmpty(assetName) && StringUtils.isEmpty(functionType) && assetGroup == null && CollectionUtils.isEmpty(clinicalDeptOrgUidSet) && CollectionUtils.isEmpty(assetDeptOrgUidSet)) {
            MessageUtil.outPutReturnFacesMessage("RiskAssessmentSearchRequire", FacesMessage.SEVERITY_INFO);
            return assets;
        }
        searchFilters = new ArrayList<>();
        editSearchFilters();
        Page<AssetInfo> page = assetInfoRepository.findBySearchFilter(this.searchFilters, pageRequest);
        List<AssetInfo> source = new ArrayList<>(page.getContent());
        source.removeAll(assets.getTarget());
        assets.setSource(source);
        return assets;
    }

    private void loadMaintenancePeriod() {
        String[] maintenancePeriods = new String[]{"每季度保养一次", "每半年保养一次", "每年保养一次"};
        maintenancePeriod = Arrays.stream(maintenancePeriods).map(String::valueOf).collect(Collectors.toSet());
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }
    private void loadCustomConfig() {
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        List<String> readFields = ccService.getReadAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        customedColumns = customConfig.getAssetsListPageFields().stream().filter(item -> readFields.contains(item)).collect(Collectors.toList());
    }


    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        if (this.searchFilters != null) {
            this.searchFilters.clear();
        }
        if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            selectSite = (OrgInfo) event.getTreeNode().getData();
        } else {
            selectSite = null;
        }
        if (selectSite.getUid() == null) {
            selectSite = null;
        }
        //如果是mvs role,选择了所有租户选项，则清空所选机构
        if (userContextService.getIsMVSRole() && selectSite != null && selectSite.getId() == -1) {
            selectSite = null;
        }
        loadClinicalDeptList();
        //table header dropdown needs to clear manually, table filters cleared by clear method above.
        filterClinicalDeptOrg = null;
        filterAssetDeptOrg = null;
        assetDeptOrgUidSet = null;
        clinicalDeptOrgUidSet = null;
        assetGroup = null;
    }

    public void onSelectAsset() throws IOException {
        if (null == selected) {
            return;
        }
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        for (AssetInfo item : currentPageAssets) {
            selectedMap.put(item.getUid(), !isSelectedAll);
        }
    }

    public String getEditPage(String assetUid, String assetName) {
        String operation = "assetRiskAssessmentEdit.xhtml?actionName=Edit&selectedid=" + assetUid
                + "&assetName=" + assetName
                + "&terminate=" + this.terminate;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public boolean getIsRiskAssessment(AssetInfo assetInfo) {
        return BooleanUtils.isTrue(assetInfo.getRiskAssessment());
    }

    public void assetGroupChangeListener() {
        if (assetGroup == null) {
            assetGroupSecondaryList = Collections.emptyList();
        } else {
            assetGroupSecondaryList = i18nDao.getByMsgTypeAndSiteId("assetGroupSecondary_" + String.valueOf(assetGroup.intValue()), userContextService.getLoginUser().getSiteId());
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

    public List<OrgInfo> autoCompleteDeptAsFilter(String query) {
        if (clinicalDeptList.size() == 0) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
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

    public void onAssetDeptNameFilterChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            filterAssetDeptOrg = (String) ((SelectEvent) event).getObject();
        }
        if (!Strings.isNullOrEmpty(filterAssetDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterAssetDeptOrg.equals(item.getName()))) {
            filterAssetDeptOrg = null;
        }
        if (filterAssetDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterAssetDeptOrg)).collect(Collectors.toList());
            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            assetDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            assetDeptOrgUidSet = Collections.emptySet();
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

    public void deleteAsset(AssetInfo assetInfo) {
        assetInfo.setRiskAssessment(Boolean.FALSE);
        assetInfo.setTangibleRiskScore(null);
        assetInfo.setProblemAvoidanceRateScore(null);
        assetInfo.setAccidentHistoryScore(null);
        assetInfo.setClinicalFunctionScore(null);
        assetInfo.setSpecialRequirementScore(null);
        assetInfo.setCheckCycle(null);
        assetInfo.setSumRiskScore(null);
        assetInfo.setDetectionCycle(null);
        assetInfo.setMaintenancePeriod(null);
        assetInfo.setAssessmentBy(null);
        assetInfo.setAssessmentDate(null);
        assetInfoRepository.save(assetInfo);

    }

    public void sumScore(AssetInfo assetInfo) {
        Integer sumScore = (assetInfo.getAccidentHistoryScore()==null?0:assetInfo.getAccidentHistoryScore())
                + (assetInfo.getClinicalFunctionScore()==null?0:assetInfo.getClinicalFunctionScore())
                + (assetInfo.getTangibleRiskScore()==null?0:assetInfo.getTangibleRiskScore())
                + (assetInfo.getSpecialRequirementScore()==null?0:assetInfo.getSpecialRequirementScore())
                + (assetInfo.getProblemAvoidanceRateScore()==null?0:assetInfo.getProblemAvoidanceRateScore());
        assetInfo.setSumRiskScore(sumScore + "分");
        if (sumScore >= 13) {
            assetInfo.setDetectionCycle("每半年检测一次"); //检测周期
            assetInfo.setMaintenancePeriod("每季度保养一次"); //保养周期
            assetInfo.setCheckCycle("每天");
        } else if (sumScore >= 9 && sumScore <= 12) {
            assetInfo.setDetectionCycle("每年检测一次"); //检测周期
            assetInfo.setMaintenancePeriod("每半年保养一次"); //保养周期
            assetInfo.setCheckCycle("每周");
        } else if (sumScore < 9) {
            assetInfo.setDetectionCycle("每两年检测一次"); //检测周期
            assetInfo.setMaintenancePeriod("每年保养一次"); //保养周期
            assetInfo.setCheckCycle("每月");
        }
        assetInfo.setRiskAssessment(Boolean.TRUE);
    }

    public void batchSumScore(BatchRiskAssessment assessment) {
        Integer sumScore = (assessment.getAccidentHistoryScore()==null?0:assessment.getAccidentHistoryScore())
                + (assessment.getClinicalFunctionScore()==null?0:assessment.getClinicalFunctionScore())
                + (assessment.getTangibleRiskScore()==null?0:assessment.getTangibleRiskScore())
                + (assessment.getSpecialRequirementScore()==null?0:assessment.getSpecialRequirementScore())
                + (assessment.getProblemAvoidanceRateScore()==null?0:assessment.getProblemAvoidanceRateScore());
        assessment.setSumRiskScore(sumScore + "分");
        if (sumScore >= 13) {
            assessment.setDetectionCycle("每半年检测一次"); //检测周期
            assessment.setMaintenancePeriod("每季度保养一次"); //保养周期
            assessment.setCheckCycle("每天");
        } else if (sumScore >= 9 && sumScore <= 12) {
            assessment.setDetectionCycle("每年检测一次"); //检测周期
            assessment.setMaintenancePeriod("每半年保养一次"); //保养周期
            assessment.setCheckCycle("每周");
        } else if (sumScore < 9 && sumScore > 0) {
            assessment.setDetectionCycle("每两年检测一次"); //检测周期
            assessment.setMaintenancePeriod("每年保养一次"); //保养周期
            assessment.setCheckCycle("每月");
        } else if (sumScore == 0) {
            assessment.setDetectionCycle(null); //检测周期
            assessment.setMaintenancePeriod(null); //保养周期
            assessment.setCheckCycle(null);
        }
    }

    public void onTransfer(TransferEvent event) {
        StringBuilder builder = new StringBuilder();
        for (Object item : event.getItems()) {
            builder.append(((AssetInfo) item).getName()).append("<br />");
        }
        MessageUtil.outPutReturnFacesMessage("ItemsTransferred", FacesMessage.SEVERITY_INFO, builder.toString());
    }

    public void saveAssetInfo() {
        selected.setAssessmentBy(userContextService.getLoginUser().getLoginName());
        selected.setAssessmentDate(new Date());
        assetInfoRepository.save(selected);
        MessageUtil.outPutReturnFacesMessage("SaveSuccess", FacesMessage.SEVERITY_INFO);
    }

    public void saveBatchRiskAssetInfo() {
        if (CollectionUtils.isEmpty(assets.getTarget())) {
            MessageUtil.outPutReturnFacesMessage("ChooseAssets", FacesMessage.SEVERITY_INFO);
            return;
        }
        if ("0分".equals(batchRiskAssessment.getSumRiskScore())) {
            MessageUtil.outPutReturnFacesMessage("NotAssessmentScore", FacesMessage.SEVERITY_INFO);
            return;
        }
        assets.getTarget().forEach(assetInfo -> {
            assetInfo.setRiskAssessment(Boolean.TRUE);
            assetInfo.setTangibleRiskScore(batchRiskAssessment.getTangibleRiskScore());
            assetInfo.setProblemAvoidanceRateScore(batchRiskAssessment.getProblemAvoidanceRateScore());
            assetInfo.setAccidentHistoryScore(batchRiskAssessment.getAccidentHistoryScore());
            assetInfo.setClinicalFunctionScore(batchRiskAssessment.getClinicalFunctionScore());
            assetInfo.setSpecialRequirementScore(batchRiskAssessment.getSpecialRequirementScore());
            assetInfo.setCheckCycle(batchRiskAssessment.getCheckCycle());
            assetInfo.setSumRiskScore(batchRiskAssessment.getSumRiskScore());
            assetInfo.setDetectionCycle(batchRiskAssessment.getDetectionCycle());
            assetInfo.setMaintenancePeriod(batchRiskAssessment.getMaintenancePeriod());
            assetInfo.setAssessmentBy(userContextService.getLoginUser().getLoginName());
            assetInfo.setAssessmentDate(new Date());
        });
        assetInfoRepository.save(assets.getTarget());
        MessageUtil.outPutReturnFacesMessage("SaveSuccess", FacesMessage.SEVERITY_INFO);
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }

    public boolean isShowColumn(String fieldName) {
        return customedColumns.contains(fieldName);
    }

    public void exportWord() throws IOException {
        List<AssetInfo> assetInfos = getExportList();
        Map<String, byte[]> expr = new HashMap<>();
        Map<String, Map<String, Object>> dataMaps = new HashMap<>();
        Map<String, AssetInfo> maps = assetInfos.stream().collect(Collectors.toMap(AssetInfo::getDepartNumAndName, Function.identity(), (key1, key2) -> key2));
        maps.forEach((key, val) -> {
            dataMaps.put(key + ".docx", buildWordTitle(val));
        });
//        FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/riskAssessment/assetRiskAssessment.docx")
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        byte[] templateFileByte = systemTemplateService.getTemplateFileByte(SystemTemplateEnum.ASSET_RISK_ASSESSMENT, currentUserAccount);

//        XWPFTemplate doc = XWPFTemplate.compile(inputStream);
        if (!CollectionUtils.isEmpty(assetInfos) && assetInfos.size() == 1) {
            dataMaps.forEach((key, val) -> {
                byte[] generate = WordExportServiceUtil.generate(val, templateFileByte, new HashMap<>());
                zipFile = new DefaultStreamedContent(new ByteArrayInputStream(generate), "application/docx", key);
//                try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
//                    doc.render(val);
//                    doc.write(outStream);
//                    zipFile = new DefaultStreamedContent(new ByteArrayInputStream(outStream.toByteArray()), "application/docx", key);
//                } catch (IOException e) {
//                    return;
//                }
            });
        } else if (!CollectionUtils.isEmpty(assetInfos) && assetInfos.size() > 1){
            dataMaps.forEach((key, val) -> {
                byte[] generate = WordExportServiceUtil.generate(val, templateFileByte, new HashMap<>());
                expr.put(key, generate);
//                doc.render(val);
//                try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()){
//                    doc.write(outStream);
//                    expr.put(key, outStream.toByteArray());
//                } catch (IOException e) {
//                    return;
//                }
            });
            byte[] result = exportService.docxZip(expr);
            if (result != null) {
                zipFile = new DefaultStreamedContent(new ByteArrayInputStream(result), "application/zip", "Report.zip");
            }
        }
    }

    private Map<String, Object> buildWordTitle(AssetInfo assetInfo) {
        Map<String, Object> map = new HashMap<>();
        map.put("hospitalName", assetInfo.getHospital().getName());
        map.put("assessmentDate",  DateFormatUtils.format(assetInfo.getAssessmentDate(), "yyyy-MM-dd"));
        map.put("name", assetInfo.getName());
        map.put("assessmentBy", assetInfo.getAssessmentBy());
        map.put("clinicalFunctionScore", assetInfo.getClinicalFunctionScore());
        map.put("tangibleRiskScore", assetInfo.getTangibleRiskScore());
        map.put("problemAvoidanceRateScore", assetInfo.getProblemAvoidanceRateScore());
        map.put("accidentHistoryScore", assetInfo.getAccidentHistoryScore());
        map.put("specialRequirementScore", assetInfo.getSpecialRequirementScore());
        map.put("sumRiskScore", assetInfo.getSumRiskScore());
        map.put("maintenancePeriod", assetInfo.getMaintenancePeriod());
        map.put("detectionCycle", assetInfo.getDetectionCycle());
        return map;
    }

    public List<AssetInfo> getExportList() {
        List<String> assetUids = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toList());
        List<AssetInfo> resultList;
        if (assetUids.isEmpty()) {
            resultList = assetInfoRepository.findBySearchFilter(searchFilters, lastPageSort);
        } else {
            resultList = assetInfoRepository.findByUidIn(assetUids, lastPageSort);
        }
        return resultList;
    }

    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return assetInfoRepository;
    }
}
