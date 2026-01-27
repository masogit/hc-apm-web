package com.ge.apm.view.asset;

import com.ge.apm.dao.mapper.ContractInfoMapper;
import com.ge.apm.domain.dto.SelectItemDto;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigItemService;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.service.uaa.AssetTypeGroupService;
import com.ge.apm.service.utils.*;
import com.ge.apm.view.preview.QRPreViewerBean;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateTime;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.pm.RelRuleAssetController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetInfoListController extends JpaCRUDController<AssetInfo> {

    private static final Logger logger = LoggerFactory.getLogger(AssetInfoListController.class);

    private static final long serialVersionUID = -1L;
    static Map<String,List<AssetInfo>> resultAssetMap = null;
    private AssetInfoRepository dao = null;
    private AssetInfoMgrService aiMgrService;
    private AssetCreateService acService;
    private UserContextService userContextService;
    private OrgInfoRepository orgDao;
    private UaaService uaaService;
    private CustomConfigService ccService;
    private OrgInfoTreeService orgInfoTreeService;
    private I18nMessageRepository i18nDao;
    private ConfigCacheService configCacheService;
    private AssetHistoryService assetHistoryService;
    private RelRuleAssetRepository relRuleAssetRepository;
    private UserRoleRepository userRoleRepository;
    private CustomRoleRepository customRoleRepository;
    private AssetTypeGroupRepository assetTypeGroupRepository;
    private SysAuthGroupRepository sysAuthGroupRepository;

    private AssetInfoService assetInfoService;
    private I18nMessageService i18nMessageService;
    private ContractInfoMapper contractInfoMapper;

    private QRPreViewerBean qrPreViewerBean;
    private ConfigItemService configItemService;

    //选定科室过滤
    private String filterClinicalDeptOrg;
    private String filterAssetDeptOrg;
    private String filterAccountingDeptOrgOrg;
    private Set<String> accountingDeptOrgUidSet;
    private Set<String> clinicalDeptOrgUidSet;
    private String filterSpare01;

    private Set<String> spare01Set;


    private String filterSpare02;

    private Set<String> spare02Set;


    private String filterSpare03;

    private Set<String> spare03Set;

    private String filterSpare04;

    private Set<String> spare04Set;
    private String filterSpare05;

    private Set<String> spare05Set;


    @Getter
    @Setter
    private List<String> selectDeptList;

    private TreeNode[] selectedAccessOrgNodes;
    @Getter
    @Setter
    private List<String> selectSiteDeptList;
    private Set<String> assetDeptOrgUidSet;
    //复选的设备
    @Getter
    @Setter
    private Map<String, Boolean> selectedMap;

    //搜索字串
    private String oneSearchStr;
    //是否保养
    @Getter @Setter
    private String isPmSearch;
    @Getter @Setter
    private Boolean isOrderByForParentAndChildren = false;
    @Getter @Setter
    private List<String> warrantyStatus= new ArrayList<>();

    //显示子设备，主设备，或者全部类型的设备
    @Getter
    private int queryType = 1;
    public void setQueryType(int queryType) {
        this.searchFilters.clear();
        this.queryType = queryType;
    }
    private List<String> relRuleAssetUids = new ArrayList<>();

    private boolean terminate = false;

    private Date varWarrantyDateTo = null;

    private String warrantyFormatTime = null;
    private String manufactDateFormatTime = null;
    private String installDateFormatTime = null;
    private String arriveDateFormatTime = null;
    private String factoryWarrantyDateFormatTime = null;
    private String lastInventoryDateFormatTime = null;
    private String factoryAssetLifecycleDeadlineFormatTime = null;
    @Getter @Setter
    private String mvsContractStartDate = null;
    @Getter @Setter
    private String mvsContractEndDate = null;

    private Boolean needShowAssetApproval;

    private StreamedContent excelFile;

    private StreamedContent zipFile;

    private String exportQrCodeTemplateId;

    private List<AssetInfo> currentPageAssets;

    private Sort lastPageSort; //export use

    private List<AssetInfo> selectedAssets;
    //custom config
    private CustomConfigObject customConfig;

    List<String> customedColumns;

    private List<OrgInfo> clinicalDeptList;

    private Integer assetGroup;
    private Integer assetClass;
    private Integer assetGroupSecondary;

    private List<I18nMessage> assetGroupSecondayList;

    //状态监控报表跳转参数
    private String filterAssetStatus = null;
    private String filterWarrantyDate = null;
    private Integer assetId;


    private Integer warrantyAlertDays = null;

    private Map<String, Object> selectedObject;
    private AssetLifecyleService assetLifecyleService;

    private SupplierRepository supplierDao;
    private UserAccountRepository userDao;
    private String oneQrCodeStatus;
    private boolean countyArea = false;
    private UserAccount user;
    //    private Map<String, UserAccount> createdByMap;
    private List<AssetInfo> resultList = new ArrayList<>();
    @Getter @Setter
    private Boolean showMasterEquipment;

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        long l = System.currentTimeMillis();
        logger.info("AssetInfoListController.init start time:{}",l);
        dao = WebUtil.getBean(AssetInfoRepository.class);
        aiMgrService = WebUtil.getBean(AssetInfoMgrService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        terminate = Boolean.parseBoolean(WebUtil.getRequestParameter("terminate"));
        supplierDao = WebUtil.getBean(SupplierRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        assetHistoryService = WebUtil.getBean(AssetHistoryService.class);
        assetLifecyleService = WebUtil.getBean(AssetLifecyleService.class);
        relRuleAssetRepository = WebUtil.getBean(RelRuleAssetRepository.class);
        userRoleRepository = WebUtil.getBean(UserRoleRepository.class);
        customRoleRepository = WebUtil.getBean(CustomRoleRepository.class);
        assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);
        sysAuthGroupRepository = WebUtil.getBean(SysAuthGroupRepository.class);
        assetTypeGroupService = WebUtil.getBean(AssetTypeGroupService.class);
        i18nMessageService = WebUtil.getBean(I18nMessageService.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        contractInfoMapper = WebUtil.getBean(ContractInfoMapper.class);
        qrPreViewerBean = WebUtil.getBean(QRPreViewerBean.class);
        configItemService = WebUtil.getBean(ConfigItemService.class);
        user = UserContextService.getCurrentUserAccount();
        showMasterEquipment = configItemService.getConfigItemByKey("showMasterEquipment", user);
        assetTypeGroupList = user.getAssetTypeGroupList();
        this.filterBySite = false;
        this.selectedMap = new HashMap();
        this.selectedObject = new HashMap();
        loadCustomConfig();
        loadClinicalDeptList();
        getRelRuleAssetUids();
        warrantyAlertDays = getWarrantyAlertDaysConfig();
        countyArea = userContextService.isCountyArea();
        logger.info("AssetInfoListController.init end time cost:{}ms",(System.currentTimeMillis()-l));
    }

    private Integer getWarrantyAlertDaysConfig(){
        WorkflowConfigRepository wfDao = WebUtil.getBean(WorkflowConfigRepository.class);
        List<WorkflowConfig> wfConfigList = wfDao.findByTenantUID(user.getTenantUID());

        WorkflowConfig alertSite=null, alertHospital=null, alertInstitution=null, alsertTenant=null;
        for(int i=0; i<wfConfigList.size(); i++){
            WorkflowConfig item = wfConfigList.get(i);

            if(user.getSiteUID().equals(item.getSiteUID()))
                alertSite = item;
            else if(user.getHospitalUID().equals(item.getSiteUID()))
                alertHospital = item;
            else if(user.getInstitutionUID().equals(item.getSiteUID()))
                alertInstitution = item;
            else if(user.getTenantUID().equals(item.getSiteUID()))
                alsertTenant = item;
        }

        if(alertSite!=null) return alertSite.getWarrantyRemindDays();
        if(alertHospital!=null) return alertHospital.getWarrantyRemindDays();
        if(alertInstitution!=null) return alertInstitution.getWarrantyRemindDays();
        if(alsertTenant!=null) return alsertTenant.getWarrantyRemindDays();

        return null;
    }

    public boolean getShowWarrantyAlert(){
        return warrantyAlertDays!=null;
    }

    private void loadCustomConfig() {
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        List<String> roles = UserContextService.getRoles();
        List<CustomRole> customRoles = userContextService.getListCustomRole(UserContextService.getCurrentUserAccount().getId());
        if (CollectionUtils.isNotEmpty(customRoles)) {
            roles.addAll(customRoles.stream().map(CustomRole::getName).collect(Collectors.toSet()));
        }
        List<String> readFields = ccService.getReadAssetFieldsByRoles(customConfig, roles);
        customedColumns = customConfig.getAssetsListPageFields().stream().filter(item -> readFields.contains(item)).collect(Collectors.toList());
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        long l = System.currentTimeMillis();
        logger.info("AssetInfoListController.loadData start time:{}",l);
        isOrderByForParentAndChildren = false;

        if (user.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(user.getAssetTypeGroupList())) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }
        setUserSearchFilters();
        Integer newLifecyle = null;
        for(SearchFilter searchFilter : searchFilters) {
            if("lifecycle".equals(searchFilter.fieldName)) {
                newLifecyle = Integer.parseInt(searchFilter.value.toString()) * 12;
            }
        }
        if(newLifecyle!=null) {
            this.updateSearchFilter(new SearchFilter("lifecycle", SearchFilter.Operator.EQ,newLifecyle));
        }
        selected = null;
        pageRequest = addSort(pageRequest);

        lastPageSort = pageRequest.getSort(); //export excel use
        if (this.hasFilterOnField("assetUsedLifecycle")) {
            int assetUsedLifecycle = Integer.parseInt(searchFilters.stream().filter(sf -> "assetUsedLifecycle".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("assetUsedLifecycle", SearchFilter.Operator.EQ, 0)).value.toString());
            DateTime parse = DateUtil.parse(String.valueOf(org.joda.time.LocalDate.now().plusYears(-assetUsedLifecycle)));
            searchFilters.add(new SearchFilter("installDate", SearchFilter.Operator.IsNotNull, null));
            searchFilters.add(new SearchFilter("lifecycle", SearchFilter.Operator.IsNotNull, null));
            searchFilters.add(new SearchFilter("installDate", SearchFilter.Operator.LTE, new Date(parse.getTime())));
            this.removeFilterOnField("assetUsedLifecycle");
        }
        searchFilters.removeAll(searchFilters.stream().filter(filter -> (filter.fieldName.equals("warrantyDate") || filter.fieldName.equals("factoryWarrantyDate")) && filter.conditionPath.startsWith("1*6")).collect(Collectors.toList()));
        if (ObjectUtil.isNotEmpty(warrantyStatus)) {
            Date now = DateUtil.beginOfDay(DateUtil.now()).toDate();
            Date date = DateUtil.offsetDay(now, warrantyAlertDays).toDate();
            if (warrantyStatus.contains("1")) {
                searchFilters.add(new SearchFilter("warrantyDate", SearchFilter.Operator.GT, now, "1*6+1*"));
                searchFilters.add(new SearchFilter("warrantyDate", SearchFilter.Operator.LTE, date, "1*6+1*"));
            }
            if (warrantyStatus.contains("2")) {
                searchFilters.add(new SearchFilter("warrantyDate", SearchFilter.Operator.LTE, now, "1*6+"));
            }
            if (warrantyStatus.contains("3")) {
                searchFilters.add(new SearchFilter("factoryWarrantyDate", SearchFilter.Operator.GT, now, "1*6+2*"));
                searchFilters.add(new SearchFilter("factoryWarrantyDate", SearchFilter.Operator.LTE, date, "1*6+2*"));
            }
            if (warrantyStatus.contains("4")) {
                searchFilters.add(new SearchFilter("factoryWarrantyDate", SearchFilter.Operator.LTE, now, "1*6+"));
            }
        }
        Page<AssetInfo> res = dao.findBySearchFilter(this.searchFilters, pageRequest);
        currentPageAssets = res.getContent();

        addMisingClinicalDeptOrg(currentPageAssets);
        setWarrantyAlerts(currentPageAssets);
        assetInfoService.setSupplier(currentPageAssets);
        assetInfoService.setWarrantyStatus(currentPageAssets);

        Set<String> encodes = currentPageAssets.stream().map(AssetInfo::getAssetType).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        List<AssetTypeGroup> currentPageAssetTypeGroupList = assetTypeGroupRepository.findByEncodedInAndTenantUid(encodes, user.getTenantUID());
        currentPageAssetTypeGroupMap = currentPageAssetTypeGroupList.stream().collect(Collectors.toMap(AssetTypeGroup::getEncoded, ast -> ast));
        logger.info("AssetInfoListController.loadData end time cost:{}ms",(System.currentTimeMillis()-l));
        return res;
    }

    private PageRequest addSort(PageRequest pageRequest) {
        Sort sort = pageRequest.getSort();
        JSONObject assetListSort = customConfig.getAssetListSort();
        Boolean namePyCodeSort = assetListSort.getBoolean("namePyCode");
        Boolean createdDateSort = assetListSort.getBoolean("createdDate");
        Boolean lastModifiedDateSort = assetListSort.getBoolean("lastModifiedDate");
        if (ObjectUtil.isNotEmpty(namePyCodeSort)) {
            Sort sortNamePyCode = new Sort(namePyCodeSort ? Sort.Direction.ASC : Sort.Direction.DESC, "namePyCode");
            sort = sort != null ? sort.and(sortNamePyCode) : sortNamePyCode;
        }
        if (ObjectUtil.isNotEmpty(createdDateSort)) {
            Sort sortCreatedDate = new Sort(createdDateSort ? Sort.Direction.ASC : Sort.Direction.DESC, "createdDate");
            sort = sort != null ? sort.and(sortCreatedDate) : sortCreatedDate;
        }
        if (ObjectUtil.isNotEmpty(lastModifiedDateSort)) {
            Sort sortLastModifiedDate = new Sort(lastModifiedDateSort ? Sort.Direction.ASC : Sort.Direction.DESC, "lastModifiedDate");
            sort = sort != null ? sort.and(sortLastModifiedDate) : sortLastModifiedDate;
        }
        Iterator<Sort.Order> orders = sort.iterator();
        boolean exists = false;
        while(orders.hasNext()){
            if(orders.next().getProperty().equals("id")){
                exists = true;
                break;
            }
        }
        if (!exists) {
            Sort sortWithId = new Sort(Sort.Direction.ASC, "id");
            sort = sort.and(sortWithId);
        }
        return new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
    }

    private void setWarrantyAlerts(List<AssetInfo> assets){
        assets.forEach(asset -> asset.setWarrantyExpireAlertDays(warrantyAlertDays));
    }

    private void addMisingClinicalDeptOrg(List<AssetInfo> currentPageAssets) {
        Set<String> orgUids = currentPageAssets.stream().filter(item -> item.getClinicalDeptUID() != null).map(AssetInfo::getClinicalDeptUID).collect(Collectors.toSet());
        Set<String> orgUids2 = currentPageAssets.stream().filter(item -> item.getAssetDeptUID() != null).map(AssetInfo::getAssetDeptUID).collect(Collectors.toSet());
        Set<String> orgUids3 = currentPageAssets.stream().filter(item -> item.getAccountingDeptUID() != null).map(AssetInfo::getAccountingDeptUID).collect(Collectors.toSet());
        orgUids.addAll(orgUids2);
        orgUids.addAll(orgUids3);

        if (orgUids.isEmpty()) {
            return;
        }

        List<OrgInfo> orgInfoList = orgDao.findByUids(orgUids);

        Map<String, OrgInfo> orgInfoMap = orgInfoList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> item));

        currentPageAssets.stream().forEach(item -> {
            if (item.getClinicalDeptUID() != null) {
                OrgInfo found = orgInfoMap.get((item.getClinicalDeptUID()));
                if (found != null) {
                    item.setClinicalDeptOrg(found);
                }

            }

            if (item.getAssetDeptUID() != null) {
                OrgInfo found = orgInfoMap.get((item.getAssetDeptUID()));
                if (found != null) {
                    item.setAssetDeptOrg(found);
                }

            }
            if (item.getAccountingDeptUID() != null) {
                OrgInfo found = orgInfoMap.get((item.getAccountingDeptUID()));
                if (found != null) {
                    item.setAccountingDeptOrg(found);
                }

            }
        });

    }

    public void setUserSearchFilters() {

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));
        boolean assetStatus = "-1".equals(filterAssetStatus);
        if(assetStatus){
            this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, true));
            this.removeFilterOnField("status");
        }else{
            this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        }
        if(isPmSearch!=null) {
            if(isPmSearch.equals("ypz")) {
                this.updateSearchFilter(new SearchFilter("isPm", SearchFilter.Operator.EQ, true));
                this.updateSearchFilter(new SearchFilter("uid",SearchFilter.Operator.IN,relRuleAssetUids));
            } else if(isPmSearch.equals("wpz")) {
                this.updateSearchFilter(new SearchFilter("isPm", SearchFilter.Operator.EQ, true));
                this.updateSearchFilter(new SearchFilter("uid",SearchFilter.Operator.NotIN,relRuleAssetUids));
            } else if(isPmSearch.equals("bpz")) {
                this.updateSearchFilter(new SearchFilter("isPm", SearchFilter.Operator.EQ, false));
            }
        } else {
            this.removeFilterOnField("isPm");
            this.removeFilterOnField("uid");
        }
        if(!assetStatus){
            if (terminate) {
                this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, false));
            } else {
                this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
            }
        }

        if (!UserContextService.isMVSRole()) {
            if (!this.hasFilterOnField("siteUID")) {
                List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            }
        }

        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptName", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("assetTags.tag", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }
        if(queryType == 1) //全部
            searchFilters.removeAll(searchFilters.stream().filter(item -> "parentAssetId".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        if(queryType == 2) //主设备
            this.updateSearchFilter(new SearchFilter("parentAssetId", SearchFilter.Operator.IsNull, null, "1*"));
        if(queryType == 3) //子设备
            this.updateSearchFilter(new SearchFilter("parentAssetId", SearchFilter.Operator.IsNotNull, null,"1*"));

        if (assetDeptOrgUidSet != null && !assetDeptOrgUidSet.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, new ArrayList<>(assetDeptOrgUidSet), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }

        if (clinicalDeptOrgUidSet != null && !clinicalDeptOrgUidSet.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, new ArrayList<>(clinicalDeptOrgUidSet), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }

        if (accountingDeptOrgUidSet != null && !accountingDeptOrgUidSet.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("accountingDeptUID", SearchFilter.Operator.IN, new ArrayList<>(accountingDeptOrgUidSet), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "accountingDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }

        if (CollectionUtils.isNotEmpty(selectSiteDeptList)) {
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.IN, selectSiteDeptList, "1*5+"));
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, selectSiteDeptList, "1*5+"));
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.IN, selectSiteDeptList, "1*5+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, selectSiteDeptList, "1*5+"));
//            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, selectSite.getUid(), "1*5+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        }

        Optional<SearchFilter> nameFilterOptional = this.getFilterOnField("name");
        if (nameFilterOptional.isPresent() && !nameFilterOptional.get().conditionPath.equals("1*3+")) {

            SearchFilter nameFilter = nameFilterOptional.get();
            nameFilter.conditionPath = "1*name+";
            this.updateSearchFilter(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, nameFilter.value, nameFilter.conditionPath));
        }

        if (UserContextService.isMVSRole()) {
            this.updateSearchFilter(new SearchFilter("mvsEquip", SearchFilter.Operator.EQ, 1));
        }

        if (assetTypeGroupEncodedSet != null && !assetTypeGroupEncodedSet.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("assetType", SearchFilter.Operator.IN, new ArrayList<>(assetTypeGroupEncodedSet), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetType".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (user.isEnableAssetTypeGroup()) {
            searchFilters.add(new SearchFilter("assetType", SearchFilter.Operator.IN, user.getAssetTypeGroupMap().keySet(), "1*"));
        }
        if (spare01Set != null && !spare01Set.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("spare01", SearchFilter.Operator.IN, new ArrayList<>(spare01Set), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "spare01".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }

        if (spare02Set != null && !spare02Set.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("spare02", SearchFilter.Operator.IN, new ArrayList<>(spare02Set), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "spare02".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (spare03Set != null && !spare03Set.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("spare03", SearchFilter.Operator.IN, new ArrayList<>(spare03Set), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "spare03".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (spare04Set != null && !spare04Set.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("spare04", SearchFilter.Operator.IN, new ArrayList<>(spare04Set), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "spare04".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (spare05Set != null && !spare05Set.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("spare05", SearchFilter.Operator.IN, new ArrayList<>(spare05Set), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "spare05".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        this.searchFilters.clear();
        loadClinicalDeptList();
        filterClinicalDeptOrg = null;
        filterAssetDeptOrg = null;
        assetDeptOrgUidSet = null;
        clinicalDeptOrgUidSet = null;
        assetGroup = null;
        assetGroupSecondary = null;
        filterAssetStatus = null;
        setTreeNodes(event.getTreeNode(), true);

    }


    public void onUnSelectSiteTreeNode(NodeUnselectEvent event) {
        this.searchFilters.clear();
        loadClinicalDeptList();
        filterClinicalDeptOrg = null;
        filterAssetDeptOrg = null;
        assetDeptOrgUidSet = null;
        clinicalDeptOrgUidSet = null;
        assetGroup = null;
        assetGroupSecondary = null;
        filterAssetStatus = null;

        TreeNode node = event.getTreeNode();
        setTreeNodes(node, false);
        while (node.getParent() != null) {
            node.getParent().setSelected(false);
            node = node.getParent();
        }
    }




    private void setTreeNodes(TreeNode root, Boolean isSelected) {
        root.setSelected(isSelected);
        for (TreeNode item : root.getChildren()) {
            setTreeNodes(item, isSelected);
        }
    }

    public void onSelectSiteTreeNode2(NodeSelectEvent event) {
        loadClinicalDeptList();
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (selectedAccessOrgNodes!=null && selectedAccessOrgNodes.length>0){
            Set<OrgInfo> orgInfos = new HashSet<>();
            selectSiteDeptList=new ArrayList<>();
            for (TreeNode selectedAccessOrgNode : selectedAccessOrgNodes) {
                OrgInfo orgInfo =(OrgInfo) selectedAccessOrgNode.getData();
               if(!orgInfo.getOrgType().equals(4)){
                   Set<OrgInfo> collect = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(orgInfo.getSiteUID()) || item.getHospitalUID().equals(orgInfo.getSiteUID())
                           || item.getInstitutionUID().equals(orgInfo.getSiteUID()) || item.getTenantUID().equals(orgInfo.getSiteUID())).collect(Collectors.toSet());
                   orgInfos.addAll(collect);
               }else{
                   orgInfos.add(orgInfo);
               }

                selectSiteDeptList.add(orgInfo.getUid());

            }
            clinicalDeptList=new ArrayList<>(orgInfos);
        }else{
            selectSiteDeptList=null;
        }
    }

    public void setOneSearchStr(String oneSearchStr) {
        this.oneSearchStr = oneSearchStr;
        if (StringUtils.isEmpty(oneSearchStr)) {
            this.removeFilterOnField("name");
            this.removeFilterOnField("namePyCode");
            this.removeFilterOnField("departNum");
            this.removeFilterOnField("financingNum");
            this.removeFilterOnField("qrCode");
            this.removeFilterOnField("serialNum");
            this.removeFilterOnField("manufacture");
            this.removeFilterOnField("clinicalDeptName");
            this.removeFilterOnField("assetTags.tag");
        }
    }

    public void onSelectAsset() throws IOException {
        if (null == selected) {
            return;
        }

        String url = "actionName=View&selectedid="
                + selected.getUid()
                + "&asset_name=" + selected.getName()
                + "&terminate=" + this.terminate;
        url = "AssetDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        for (AssetInfo item : currentPageAssets) {
            selectedMap.put(item.getUid(), !isSelectedAll);
        }
    }

    public String getEditPage(String assetUid, String assetName) {
        String operation = "AssetCreate.xhtml?actionName=Edit&selectedid=" + assetUid
                + "&asset_name=" + assetName
                + "&terminate=" + this.terminate;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public String getCopyPage(String assetUid, String assetName) {
        String operation = "AssetCreate.xhtml?actionName=Copy&selectedid=" + assetUid
                + "&asset_name=" + assetName
                + "&terminate=" + this.terminate;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public void deleteAsset(AssetInfo asset) {
        int originStatus = asset.getStatus();
        aiMgrService.deleteAsset(asset);
        if(Boolean.TRUE.equals(asset.getIsDeleted())){
            assetHistoryService.deleteAsset(asset);
        }
//        assetLifecyleService.saveLifecycle(originStatus,asset,UserContextService.getCurrentUserAccount());
    }

    public void cancleWarranty(String assetUid) {
        AssetInfo ai = dao.findByUid(assetUid);
        if (ai != null) {
            ai.setWarranty(false);
            dao.save(ai);
        }
    }
    public void deleteAssetForever(AssetInfo asset){

        try {
            aiMgrService.deleteAssetForever(asset);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void export(List<String> exportFields) throws IOException {
        List<OrgInfo> orgList = orgDao.findBySiteUIDAndOrgType(UserContextService.getCurrentUserAccount().getSiteUID(), 4);

        List<OrgInfo> sites = orgDao.getByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());

        logger.info("load asset list to export");

        List<AssetInfo> assetList = getExportList();

        logger.info("start to export asset list to excel");

        AssetFileDynamicTemplate template = new AssetFileDynamicTemplate(false);

        template.exportAsset(assetList, sites, orgList, exportFields);

        logger.info("exported asset list to excel, start to get input stream");

        excelFile = new DefaultStreamedContent(template.getInputStream(), "application/vnd.ms-excel", "AssetInfo_export.xlsx");

        logger.info("export file: AssetInfo_export.xlsx successful.");
    }

    public void export1(List<String> exportFields) throws IOException {
        logger.info("start to export asset list to excel");
        AssetFileDynamicTemplate template = new AssetFileDynamicTemplate(false);
        List<String> assetSheetHeads = template.getAssetSheetHead();
        if(CollectionUtils.isNotEmpty(assetSheetHeads)){
            List<String> finalExportFields = exportFields;
            exportFields= assetSheetHeads.stream().filter(finalExportFields::contains).collect(Collectors.toList());
            if(!exportFields.contains("UUID")){
                exportFields.add("UUID");
            }
        }

        writerAsset(template, exportFields);

        logger.info("exported asset list to excel, start to get input stream");

        excelFile = new DefaultStreamedContent(template.getInputStream(), "application/vnd.ms-excel", "AssetInfo_export.xlsx");

        logger.info("export file: AssetInfo_export.xlsx successful.");
    }

    public void writerAsset(AssetFileDynamicTemplate template, List<String> exportFields) {
        List<OrgInfo> orgList = orgDao.findBySiteUIDAndOrgType(UserContextService.getCurrentUserAccount().getSiteUID(), 4);

        List<OrgInfo> sites = orgDao.getByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());

        List<String> assetUids = selectedMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());
        int num = 5000;
        boolean siteFlag = exportFields.containsAll(ListUtil.newArrayList("医院", "集团"));
        Map encodedToAssetTypeGroupPath = new HashMap<>();;
        Map listSysAuthGroupNamePathInHierarchyMap = new HashMap<>();;
        List<Object> assetTypeGroupPaths = assetTypeGroupRepository.flatternHierarchy();
        encodedToAssetTypeGroupPath = new HashMap<>();
        for (Object obj : assetTypeGroupPaths) {
            Object[] obj1 = (Object[]) obj;
            String encoded = obj1[0].toString();
            String authGroupPaths = obj1[1].toString();
            encodedToAssetTypeGroupPath.put(encoded, authGroupPaths);
        }
        List<Object> listSysAuthGroupNamePathInHierarchy = sysAuthGroupRepository.listSysAuthGroupNamePathInHierarchy();
        listSysAuthGroupNamePathInHierarchyMap = new HashMap<>();
        for (Object obj : listSysAuthGroupNamePathInHierarchy) {
            Object[] obj1 = (Object[]) obj;
            String classificationCode = obj1[0].toString();
            String name = obj1[1].toString();
            listSysAuthGroupNamePathInHierarchyMap.put(classificationCode, name);
        }
        if (assetUids.isEmpty()) {
            if(CollectionUtils.isEmpty(searchFilters)){
                this.setUserSearchFilters();
            }
            int count = (int) dao.count(searchFilters);
            int cycles = count / num;
            ExcelDocumentStreamingWriter writer = template.initExcelWriter(count, exportFields, orgList);
            int startRow = AssetFileImportBaseService.EXCEL_ASSET_DATA_ROW;
            for (int i = 0; i <= cycles; i++) {
                PageRequest pageRequest = new PageRequest(i, num, lastPageSort);
                Page<AssetInfo> page = dao.findBySearchFilter(searchFilters, pageRequest);
                List<AssetInfo> assetList = page.getContent();
                if(Boolean.TRUE.equals(isOrderByForParentAndChildren)){
                    assetList =  orderByForParentAndChildren(assetList);
                }
                assetInfoService.setWarrantyStatus(currentPageAssets);
                startRow = template.writeAsset1(writer, assetList, sites, siteFlag, startRow,encodedToAssetTypeGroupPath,listSysAuthGroupNamePathInHierarchyMap);
            }
        } else {
            List<AssetInfo> assetList = dao.findByUidIn(assetUids, lastPageSort);
            if(Boolean.TRUE.equals(isOrderByForParentAndChildren)){
                assetList =  orderByForParentAndChildren(assetList);
            }
            assetInfoService.setWarrantyStatus(currentPageAssets);
            ExcelDocumentStreamingWriter writer = template.initExcelWriter(assetList.size(), exportFields, orgList);
            template.writeAsset1(writer, assetList, sites, siteFlag, AssetFileImportBaseService.EXCEL_ASSET_DATA_ROW,encodedToAssetTypeGroupPath,listSysAuthGroupNamePathInHierarchyMap);
        }
        template.setFormula();
    }

    public void exportExcel() throws IOException {
        export1(ccService.getLocalizedExportableAssetFieldsByRoles(customConfig, UserContextService.getRoles()));
    }

    public void exportAssetExcel() throws IOException {
        List<String> exportFields = ccService.getLocalizedExportableAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        insertCrossSiteColumns(exportFields);
        export1(exportFields);
    }

    private void insertCrossSiteColumns(List<String> exportFields) {
        String hospitalColumn = WebUtil.getMessage("hospitalId");
        int siteIndex = exportFields.indexOf(hospitalColumn);
        if (siteIndex == -1){
            return;
        }
        // 增加医院列
        exportFields.add(siteIndex, WebUtil.getMessage("Hospital"));
        // 增加集团列
        exportFields.add(exportFields.size(), WebUtil.getMessage("Institution"));
    }

    public void batchProcessToBeTerminated() {
        Map<String, Boolean> map = this.selectedMap;
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (!entry.getValue()) {
                continue;
            }
            String assetUid = entry.getKey();
            AssetInfo assetInfo = dao.getByUid(assetUid);
            AssetInfo oldAsset = assetHistoryService.depCopy(assetInfo);
            if (assetInfo == null) {
                continue;
            }

            if (aiMgrService.isInDispenceCenter(assetInfo)) {
                WebUtil.addErrorMessageWithParams(String.format(WebUtil.getMessage("SwappedInRentPleaseReturnItFirst"), assetInfo.getName(), assetInfo.getDepartNum()));
                continue;
            }
            int originStatus = assetInfo.getStatus();
            assetInfo.setStatus(4);
            dao.save(assetInfo);
            assetHistoryService.saveAssetHistoryIfUpdated(oldAsset,assetInfo);
            aiMgrService.updateNurseSiteMaintain(assetInfo.getUid());
//            assetLifecyleService.saveLifecycle(originStatus,assetInfo,UserContextService.getCurrentUserAccount());
        }
        this.selectedMap.clear();
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }

    public void getRelRuleAssetUids() {
        relRuleAssetUids = relRuleAssetRepository.findAssetUidsByTenantUID(userContextService.getLoginUser().getTenantUID());
    }

    public void setSelectedAssetsUids() {
        List<String> selectedAssetUids = new ArrayList<>();
        selectedMap.keySet().stream().forEach(assetUid -> { if(selectedMap.get(assetUid)) selectedAssetUids.add(assetUid); });
        RelRuleAssetController.assetInfoUids = selectedAssetUids;
    }

    public List<I18nMessage> getListForRelRule() {
        List<I18nMessage> result = new ArrayList<>();
        result.add(new I18nMessage(null,"wpz","未配置规则"));
        result.add(new I18nMessage(null,"ypz","已配置规则"));
        result.add(new I18nMessage(null,"bpz","不能配置规则"));
        return result;
    }

    public Boolean showIsPm() {
        return configCacheService.getTenantOptionConfigByUid(user.getTenantUID()).getShowIsPm();
    }

    public String getRelRuleStatusName(String uid,Boolean isPm) {
        String result = null;
        if(isPm==null || isPm == false) {
            result = "不能配置规则";
        } else {
            if(relRuleAssetUids.contains(uid)) {
                result = "已配置规则";
            } else {
                result = "未配置规则";
            }
        }
        return result;
    }

    public List<AssetInfo> getExportList() {
        List<String> assetUids = selectedMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());
        List<AssetInfo> resultList;
        if (assetUids.isEmpty()) {
            if(CollectionUtils.isEmpty(searchFilters)){
                this.setUserSearchFilters();
            }
            resultList = dao.findBySearchFilter(searchFilters, lastPageSort);
        } else {
            resultList = dao.findByUidIn(assetUids, lastPageSort);
        }
        if(isOrderByForParentAndChildren){
            resultList =  orderByForParentAndChildren(resultList);
        }
        return resultList;
    }

    private List<AssetInfo> orderByForParentAndChildren(List<AssetInfo> resultList) {
        if(CollectionUtils.isNotEmpty(resultList)) {
            resultList.stream().forEach(e -> {
                if (e.getParentAssetId() == null) {
                    e.setBranchIds("" + e.getId());
                } else {
                    e.setBranchIds("" + e.getParentAssetId() + e.getId());
                }
            });
            return resultList.stream().sorted(Comparator.comparing(AssetInfo::getBranchIds)).collect(Collectors.toList());
        }
        return resultList;
    }

    public void getAssetInfoBatchEdit() {
        List<String> assetUids = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toList());
        if (assetUids.isEmpty()) {
            if(CollectionUtils.isEmpty(searchFilters)){
                this.setUserSearchFilters();
            }
            resultList = dao.findBySearchFilter(searchFilters, lastPageSort);
        } else {
            resultList = dao.findByUidIn(assetUids, lastPageSort);
        }
    }
    public void prepareViewQrcode(){
        List<String> assetUids = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toList());
        List<AssetInfo> assetInfos = dao.findByUidIn(assetUids);
        List<String> collect = assetInfos.stream().filter(item->StringUtils.isNotEmpty(item.getQrCode())).map(item->item.getQrCode()+":"+item.getSiteUID()).collect(Collectors.toList());
        qrPreViewerBean.onServerEvent("",collect);
    }
    public void navigateTo(){
        String verificationCode = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int r = random.nextInt(10); //每次随机出一个数字（0-9）
            verificationCode = verificationCode + r;  //把每次随机出的数字拼在一起
        }
        resultAssetMap = new HashMap<>();

        resultAssetMap.put(verificationCode,resultList);
        WebUtil.navigateTo("AssetInfoBatchEdit.xhtml?faces-redirect=true&uidList= "+ verificationCode);
    }

    public String getParentAssetNameByAssetId(int assetId) {
        AssetInfo ai = dao.findById(assetId);
        if (ai.getParentAssetId() != null) {
            AssetInfo assetInfo = dao.getById(ai.getParentAssetId());
            if (assetInfo != null) {
                if (StringUtils.isEmpty(assetInfo.getFunctionType())) {
                    return assetInfo.getName();
                } else {
                    return assetInfo.getName() + "(" + assetInfo.getFunctionType() + ")";
                }
            }
        }
        return "";
    }

    public List<String> getSelectedUidList() {
        return selectedMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public List<QrCodeTemplate> getQrCodeTemplates() {
        QrCodeTemplateService qtService = WebUtil.getBean(QrCodeTemplateService.class);
        return qtService.getTemplateByUser(UserContextService.getCurrentUserAccount());
    }

    public void exportQrCode() throws Exception {
        List<AssetInfo> exportList = getExportList();
        Map<String, String> orgNamesMap = orgDao.findByUids(exportList.stream().map(AssetInfo::getSiteUID).distinct().collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(OrgInfo::getUid, OrgInfo::getName));
        QrCodeExportStream exportStream = new QrCodeExportStream(exportList.stream().filter(item -> item.getQrCode() != null && !item.getQrCode().isEmpty()).collect(Collectors.toList()), exportQrCodeTemplateId,orgNamesMap);
        zipFile = new DefaultStreamedContent(exportStream, "application/x-zip-compressed", "qrCodes.zip");
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    public void batchProcessToTerminated() {
        Map<String, Boolean> map = this.selectedMap;
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (!entry.getValue()) {
                continue;
            }

            String assetUid = entry.getKey();
            AssetInfo assetInfo = dao.getByUid(assetUid);
            if (assetInfo == null) {
                continue;
            }

            int workOrdersNum = dao.fetchCurrentWorkOrders(assetInfo.getId());
            if (workOrdersNum > 0) {
                WebUtil.addErrorMessage(String.format(WebUtil.getMessage("UnfinishedWorkorderCandBeTerminated"), assetInfo.getName()));
                continue;
            }

            if (aiMgrService.isInDispenceCenter(assetInfo)) {
                WebUtil.addErrorMessageWithParams(String.format(WebUtil.getMessage("SwappedInRentPleaseReturnItFirst"), assetInfo.getName(), assetInfo.getDepartNum()));
                continue;
            }

            assetInfo.setStatus(4);
            assetInfo.setIsValid(false);
            assetInfo.setTerminateDate(new Date());
            dao.save(assetInfo);
            aiMgrService.updateNurseSiteMaintain(assetInfo.getUid());
        }
        this.selectedMap.clear();
    }


    public boolean isShowColumn(String fieldName) {
        return customedColumns.contains(fieldName);
    }

    public String getSiteOrgName(String siteUID) {
        OrgInfo org = configCacheService.findOrgByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }

    public Date getVarWarrantyDateTo() {
        return varWarrantyDateTo;
    }

    public void setVarWarrantyDateTo(Date varWarrantyDateTo) {
        this.varWarrantyDateTo = varWarrantyDateTo;
    }

    public String getWarrantyFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (varWarrantyDateTo == null) {
            return warrantyFormatTime;
        } else {
            return "~" + sdf.format(varWarrantyDateTo);
        }
    }

    public void setWarrantyFormatTime(String warrantyFormatTime) {
        this.warrantyFormatTime = warrantyFormatTime;
    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .filter(distinctByOrgName(OrgInfo::getName)) //display unique clinical name only
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
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


    public List<String> autoCompleteSpare01Name(String query) {
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();

        List<I18nMessage> spare01List = FieldValueMessageController.getFieldValueList("spare01", siteId);

        if (spare01List == null) {
            return Collections.emptyList();
        }

        List<I18nMessage> res = spare01List.stream()
                .filter(item ->Strings.isNullOrEmpty(query) ||  item.getValueZh().contains(query))
                .collect(Collectors.toList());
        List<String> result =  res.stream().sorted(Comparator.comparing(I18nMessage::getMsgKey)).map(I18nMessage::getValueZh).collect(Collectors.toList());

        return result;
    }



    public List<String> autoCompleteSpare02Name(String query) {
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();

        List<I18nMessage> spare01List = FieldValueMessageController.getFieldValueList("spare02", siteId);

        if (spare01List == null) {
            return Collections.emptyList();
        }

        List<I18nMessage> res = spare01List.stream()
                .filter(item ->Strings.isNullOrEmpty(query) ||  item.getValueZh().contains(query))
                .collect(Collectors.toList());
        List<String> result =  res.stream().sorted(Comparator.comparing(I18nMessage::getMsgKey)).map(I18nMessage::getValueZh).collect(Collectors.toList());

        return result;
    }
    public List<String> autoCompleteSpare03Name(String query) {
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();

        List<I18nMessage> spare01List = FieldValueMessageController.getFieldValueList("spare03", siteId);

        if (spare01List == null) {
            return Collections.emptyList();
        }

        List<I18nMessage> res = spare01List.stream()
                .filter(item ->Strings.isNullOrEmpty(query) ||  item.getValueZh().contains(query))
                .collect(Collectors.toList());
        List<String> result =  res.stream().sorted(Comparator.comparing(I18nMessage::getMsgKey)).map(I18nMessage::getValueZh).collect(Collectors.toList());

        return result;
    }
    public List<String> autoCompleteSpare04Name(String query) {
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();

        List<I18nMessage> spare01List = FieldValueMessageController.getFieldValueList("spare04", siteId);

        if (spare01List == null) {
            return Collections.emptyList();
        }

        List<I18nMessage> res = spare01List.stream()
                .filter(item ->Strings.isNullOrEmpty(query) ||  item.getValueZh().contains(query))
                .collect(Collectors.toList());
        List<String> result =  res.stream().sorted(Comparator.comparing(I18nMessage::getMsgKey)).map(I18nMessage::getValueZh).collect(Collectors.toList());

        return result;
    }
    public List<String> autoCompleteSpare05Name(String query) {
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();

        List<I18nMessage> spare01List = FieldValueMessageController.getFieldValueList("spare05", siteId);

        if (spare01List == null) {
            return Collections.emptyList();
        }

        List<I18nMessage> res = spare01List.stream()
                .filter(item ->Strings.isNullOrEmpty(query) ||  item.getValueZh().contains(query))
                .collect(Collectors.toList());
        List<String> result =  res.stream().sorted(Comparator.comparing(I18nMessage::getMsgKey)).map(I18nMessage::getValueZh).collect(Collectors.toList());

        return result;
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

    public void onClinicalDeptNameFilterChange2( ) {
        if (CollectionUtils.isNotEmpty(selectDeptList)) {
            clinicalDeptOrgUidSet = new HashSet<>();
            for (String deptName : selectDeptList) {
               if( clinicalDeptList.stream().anyMatch(item -> deptName.equals(item.getName()))) {
                   List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(deptName)).collect(Collectors.toList());
                   Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
                   clinicalDeptOrgUidSet.addAll(matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet()));
               }
            }
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
        }

    }

    public void onAccountingDeptOrgNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterAccountingDeptOrgOrg = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(filterAccountingDeptOrgOrg) && !clinicalDeptList.stream().anyMatch(item -> filterAccountingDeptOrgOrg.equals(item.getName()))) {
            filterAccountingDeptOrgOrg = null;
        }

        if (filterAccountingDeptOrgOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterAccountingDeptOrgOrg)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            accountingDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            accountingDeptOrgUidSet = Collections.emptySet();
        }

    }
    public void onSpare01FilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterSpare01 = (String) ((SelectEvent) event).getObject();
        }
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();

        List<I18nMessage> spare01List = FieldValueMessageController.getFieldValueList("spare01", siteId);

        if (!Strings.isNullOrEmpty(filterSpare01) && !spare01List.stream().anyMatch(item -> filterSpare01.equals(item.getValueZh()))) {
            filterSpare01 = null;
        }

        if (filterSpare01 != null) {
            List<I18nMessage> i18nMessagesByName = spare01List.stream().filter(item -> item.getValueZh().equals(filterSpare01)).collect(Collectors.toList());

            Set<I18nMessage> matchedList = i18nMessageService.collectSpare01InExstingList(i18nMessagesByName, spare01List);
            spare01Set = matchedList.stream().map(I18nMessage::getMsgKey).collect(Collectors.toSet());
        } else {
            spare01Set = Collections.emptySet();
        }

    }



    public void onSpare02FilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterSpare02 = (String) ((SelectEvent) event).getObject();
        }
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();

        List<I18nMessage> spare01List = FieldValueMessageController.getFieldValueList("spare02", siteId);

        if (!Strings.isNullOrEmpty(filterSpare02) && !spare01List.stream().anyMatch(item -> filterSpare02.equals(item.getValueZh()))) {
            filterSpare02 = null;
        }

        if (filterSpare02 != null) {
            List<I18nMessage> i18nMessagesByName = spare01List.stream().filter(item -> item.getValueZh().equals(filterSpare02)).collect(Collectors.toList());

            Set<I18nMessage> matchedList = i18nMessageService.collectSpare01InExstingList(i18nMessagesByName, spare01List);
            spare02Set = matchedList.stream().map(I18nMessage::getMsgKey).collect(Collectors.toSet());
        } else {
            spare02Set = Collections.emptySet();
        }

    }


    public void onSpare03FilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterSpare03 = (String) ((SelectEvent) event).getObject();
        }
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();

        List<I18nMessage> spare01List = FieldValueMessageController.getFieldValueList("spare03", siteId);

        if (!Strings.isNullOrEmpty(filterSpare03) && !spare01List.stream().anyMatch(item -> filterSpare03.equals(item.getValueZh()))) {
            filterSpare03 = null;
        }

        if (filterSpare03 != null) {
            List<I18nMessage> i18nMessagesByName = spare01List.stream().filter(item -> item.getValueZh().equals(filterSpare03)).collect(Collectors.toList());

            Set<I18nMessage> matchedList = i18nMessageService.collectSpare01InExstingList(i18nMessagesByName, spare01List);
            spare03Set = matchedList.stream().map(I18nMessage::getMsgKey).collect(Collectors.toSet());
        } else {
            spare03Set = Collections.emptySet();
        }

    }
    public void onSpare04FilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterSpare04 = (String) ((SelectEvent) event).getObject();
        }
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();

        List<I18nMessage> spare01List = FieldValueMessageController.getFieldValueList("spare04", siteId);

        if (!Strings.isNullOrEmpty(filterSpare04) && !spare01List.stream().anyMatch(item -> filterSpare04.equals(item.getValueZh()))) {
            filterSpare04 = null;
        }

        if (filterSpare04 != null) {
            List<I18nMessage> i18nMessagesByName = spare01List.stream().filter(item -> item.getValueZh().equals(filterSpare04)).collect(Collectors.toList());

            Set<I18nMessage> matchedList = i18nMessageService.collectSpare01InExstingList(i18nMessagesByName, spare01List);
            spare04Set = matchedList.stream().map(I18nMessage::getMsgKey).collect(Collectors.toSet());
        } else {
            spare04Set = Collections.emptySet();
        }

    }

    public void onSpare05FilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterSpare05 = (String) ((SelectEvent) event).getObject();
        }
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();

        List<I18nMessage> spare01List = FieldValueMessageController.getFieldValueList("spare05", siteId);

        if (!Strings.isNullOrEmpty(filterSpare05) && !spare01List.stream().anyMatch(item -> filterSpare05.equals(item.getValueZh()))) {
            filterSpare05 = null;
        }

        if (filterSpare05 != null) {
            List<I18nMessage> i18nMessagesByName = spare01List.stream().filter(item -> item.getValueZh().equals(filterSpare05)).collect(Collectors.toList());

            Set<I18nMessage> matchedList = i18nMessageService.collectSpare01InExstingList(i18nMessagesByName, spare01List);
            spare05Set = matchedList.stream().map(I18nMessage::getMsgKey).collect(Collectors.toSet());
        } else {
            spare05Set = Collections.emptySet();
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

    public void assetGroupChangeListener() {

        if (assetGroup == null) {
            assetGroupSecondayList = Collections.emptyList();
        } else {
            assetGroupSecondayList = i18nDao.getByMsgTypeAndSiteId("assetGroupSecondary_" + String.valueOf(assetGroup.intValue()), userContextService.getLoginUser().getSiteId());
        }

    }

    public void assetClassChangeListener() {

        if (assetClass == null) {
            assetGroupSecondayList = Collections.emptyList();
        } else {
            assetGroupSecondayList = i18nDao.getByMsgTypeAndSiteId("assetClassSecondary_" + String.valueOf(assetGroup.intValue()), userContextService.getLoginUser().getSiteId());
        }

    }

    public void assetGroupSecondaryChangeListener() {

    }

    public OrgInfo getOrgInfoByUid(String uid) {
        if (Strings.isNullOrEmpty(uid)) {
            return null;
        }
        return configCacheService.findOrgByUid(uid);
    }

    //getter and setter
    public Boolean getNeedShowAssetApproval() {
        if (null == needShowAssetApproval) {
            needShowAssetApproval = acService.needShowAssetApprovalByUser(UserContextService.getCurrentUserAccount().getUid());
        }
        return needShowAssetApproval;
    }

    public void setAssetStatusFilter() {
        if (filterAssetStatus == null && filterWarrantyDate == null) {
            return;
        }
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        if (filterWarrantyDate != null && "1".equals(filterWarrantyDate)) {
            Date warrantyDate = new Date();
            //2个月内，则是保修到期时间在2个月后时间之前所有
            Calendar c = Calendar.getInstance();
            c.setTime(warrantyDate);
            c.add(Calendar.MONTH, 2);
            varWarrantyDateTo = c.getTime();
            searchFilters.add(new SearchFilter("warrantyDate", SearchFilter.Operator.LTE, varWarrantyDateTo));
            searchFilters.add(new SearchFilter("isWarranty", SearchFilter.Operator.EQ, true));
        }
    }

    /**
     *
     * @param authKey
     * @param roleName
     *        a - create
     *        d - delete
     *        m - modify
     *        s - search
     *        o - export
     *        i - import
     * @return
     */
    public boolean hasCustomRole(String authKey, String roleName){
        List<UserRole> userRoleList = userRoleRepository.findByUserIdAndRoleType(UserContextService.getUserID(), ApmConstant.CUSTOME_ROLE_TYPE);
        for (UserRole userRole : userRoleList) {
            String webAuthorities = customRoleRepository.findById(userRole.getRoleId()).getWebAuthorities();
            if(null != webAuthorities){
                JSONArray authJSONArr= JSONArray.parseArray(webAuthorities);
                for (int i = 0; i < authJSONArr.size(); i++) {
                    JSONObject ob = authJSONArr.getJSONObject(i);
                    if(authKey.equals(ob.get("k")) && (Boolean) ob.get(roleName)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String filterAssetTypeGroup;
    private List<AssetTypeGroup> assetTypeGroupList;
    private AssetTypeGroupService assetTypeGroupService;
    private Set<String> assetTypeGroupEncodedSet;
    private Map<String, AssetTypeGroup> currentPageAssetTypeGroupMap;

    public void onAssetTypeGroupNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterAssetTypeGroup = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(filterAssetTypeGroup) && !assetTypeGroupList.stream().anyMatch(item -> filterAssetTypeGroup.equals(item.getName()))) {
            filterAssetTypeGroup = null;
        }

        if (filterAssetTypeGroup != null) {
            List<AssetTypeGroup> assetTypeGroupsMatchingByName = this.assetTypeGroupList.stream().filter(item -> item.getName().equals(filterAssetTypeGroup)).collect(Collectors.toList());
            Set<AssetTypeGroup> matchedList = assetTypeGroupService.collectChildAssetTypeGroupInExstingList(assetTypeGroupsMatchingByName, assetTypeGroupList);
            assetTypeGroupEncodedSet = matchedList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toSet());
        } else {
            assetTypeGroupEncodedSet = Collections.emptySet();
        }
    }

    public List<String> autoCompleteAssetTypeGroupName(String query) {
        if (assetTypeGroupList == null) {
            return Collections.emptyList();
        }
        List<AssetTypeGroup> res = this.assetTypeGroupList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query))
                .collect(Collectors.toList());
        return res.stream().map(AssetTypeGroup::getName).distinct().collect(Collectors.toList());
    }

    public String convertAssetTypeGroupEncodedToName(String encoded) {
        if (null == encoded) {
            return null;
        }

        AssetTypeGroup assetTypeGroup = currentPageAssetTypeGroupMap.get(encoded);
        if (null == assetTypeGroup) {
            return null;
        }

        return assetTypeGroup.getDisplayName();
    }
    public String convertUidToName(String uid) {
        if (null == uid) {
            return null;
        }

        OrgInfo orgInfo = getOrgInfoByUid(uid);
        if (null == orgInfo) {
            return null;
        }

        return orgInfo.getName();
    }


    private TreeNode[] selectedWarrantyNodes; // 用于 tree 绑定
    public TreeNode getBuildTreeWarranty() {
        TreeNode root = new DefaultTreeNode("Root", null);
        root.setExpanded(true);

        // 添加4个叶子节点，对应原来的 selectItem
        new DefaultTreeNode("status", new SelectItemDto("维保临期", "1"), root);
        new DefaultTreeNode("status", new SelectItemDto("维保逾期", "2"), root);
        new DefaultTreeNode("status", new SelectItemDto("质保临期", "3"), root);
        new DefaultTreeNode("status", new SelectItemDto("质保逾期", "4"), root);

        return root;
    }
    public void onWarrantySelect(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        if (node.getData() instanceof SelectItemDto) {
            String value = ((SelectItemDto) node.getData()).getValue();
            if (!warrantyStatus.contains(value)) {
                warrantyStatus.add(value);
            }
        }
    }

    public void onWarrantyUnselect(NodeUnselectEvent event) {
        TreeNode node = event.getTreeNode();
        if (node.getData() instanceof SelectItemDto) {
            String value = ((SelectItemDto) node.getData()).getValue();
            warrantyStatus.remove(value);
        }
    }

    public TreeNode[] getSelectedWarrantyNodes() {
        return selectedWarrantyNodes;
    }

    public void setSelectedWarrantyNodes(TreeNode[] selectedWarrantyNodes) {
        this.selectedWarrantyNodes = selectedWarrantyNodes;
    }

    public String getFilterAssetTypeGroup() {
        return filterAssetTypeGroup;
    }

    public void setFilterAssetTypeGroup(String filterAssetTypeGroup) {
        this.filterAssetTypeGroup = filterAssetTypeGroup;
    }

    public String getFilterWarrantyDate() {
        return filterWarrantyDate;
    }

    public void setFilterWarrantyDate(String filterWarrantyDate) {
        this.filterWarrantyDate = filterWarrantyDate;
    }

    public String getFilterAssetStatus() {
        return filterAssetStatus;
    }

    public void setFilterAssetStatus(String filterAssetStatus) {
        this.filterAssetStatus = filterAssetStatus;
    }

    public StreamedContent getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(StreamedContent excelFile) {
        this.excelFile = excelFile;
    }

    public String getExportQrCodeTemplateId() {
        return exportQrCodeTemplateId;
    }

    public void setExportQrCodeTemplateId(String exportQrCodeTemplateId) {
        this.exportQrCodeTemplateId = exportQrCodeTemplateId;
    }

    public StreamedContent getZipFile() {
        return zipFile;
    }

    public void setZipFile(StreamedContent zipFile) {
        this.zipFile = zipFile;
    }

    public String getOneSearchStr() {
        return oneSearchStr;
    }

    public boolean getTerminate() {
        return terminate;
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public Boolean needAssetApproval(String siteUid) {
        return acService.needAssetApproval(siteUid);
    }

    public List<AssetInfo> getSelectedAssets() {
        return selectedAssets;
    }

    public void setSelectedAssets(List<AssetInfo> selectedAssets) {
        this.selectedAssets = selectedAssets;
    }

    public List<String> getCustomedColumns() {
        return customedColumns;
    }

    public String getFilterClinicalDeptOrg() {
        return filterClinicalDeptOrg;
    }

    public void setFilterClinicalDeptOrg(String filterClinicalDeptOrg) {
        this.filterClinicalDeptOrg = filterClinicalDeptOrg;
    }

    public String getFilterAssetDeptOrg() {
        return filterAssetDeptOrg;
    }

    public void setFilterAssetDeptOrg(String filterAssetDeptOrg) {
        this.filterAssetDeptOrg = filterAssetDeptOrg;
    }

    public static <T> Predicate<T> distinctByOrgName(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public Integer getAssetGroup() { return assetGroup; }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public Integer getAssetClass() { return assetClass; }

    public void setAssetClass(Integer assetClass) { this.assetClass = assetClass; }

    public List<I18nMessage> getAssetGroupSecondayList() {
        return assetGroupSecondayList;
    }

    public void setAssetGroupSecondayList(List<I18nMessage> assetGroupSecondayList) {
        this.assetGroupSecondayList = assetGroupSecondayList;
    }

    public Integer getAssetGroupSecondary() {
        return assetGroupSecondary;
    }

    public void setAssetGroupSecondary(Integer assetGroupSecondary) {
        this.assetGroupSecondary = assetGroupSecondary;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public Map<String, Object> getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Map<String, Object> selectedObject) {
        this.selectedObject = selectedObject;
    }

    public String getManufactDateFormatTime() {
        return manufactDateFormatTime;
    }

    public void setManufactDateFormatTime(String manufactDateFormatTime) {
        this.manufactDateFormatTime = manufactDateFormatTime;
    }

    public String getInstallDateFormatTime() {
        return installDateFormatTime;
    }

    public void setInstallDateFormatTime(String installDateFormatTime) {
        this.installDateFormatTime = installDateFormatTime;
    }

    public String getArriveDateFormatTime() {
        return arriveDateFormatTime;
    }

    public void setArriveDateFormatTime(String arriveDateFormatTime) {
        this.arriveDateFormatTime = arriveDateFormatTime;
    }

    public String getFactoryWarrantyDateFormatTime() {
        return factoryWarrantyDateFormatTime;
    }

    public void setFactoryWarrantyDateFormatTime(String factoryWarrantyDateFormatTime) {
        this.factoryWarrantyDateFormatTime = factoryWarrantyDateFormatTime;
    }

    public String getLastInventoryDateFormatTime() {
        return lastInventoryDateFormatTime;
    }

    public void setLastInventoryDateFormatTime(String lastInventoryDateFormatTime) {
        this.lastInventoryDateFormatTime = lastInventoryDateFormatTime;
    }

    public String getFactoryAssetLifecycleDeadlineFormatTime() {
        return factoryAssetLifecycleDeadlineFormatTime;
    }

    public void setFactoryAssetLifecycleDeadlineFormatTime(String factoryAssetLifecycleDeadlineFormatTime) {
        this.factoryAssetLifecycleDeadlineFormatTime = factoryAssetLifecycleDeadlineFormatTime;
    }

    public boolean getCountyArea() {
        return countyArea;
    }

    public void setCountyArea(boolean countyArea) {
        this.countyArea = countyArea;
    }

    public String getFilterSpare01() {
        return filterSpare01;
    }

    public void setFilterSpare01(String filterSpare01) {
        this.filterSpare01 = filterSpare01;
    }

    public Set<String> getSpare01Set() {
        return spare01Set;
    }

    public void setSpare01Set(Set<String> spare01Set) {
        this.spare01Set = spare01Set;
    }

    public String getFilterSpare02() {
        return filterSpare02;
    }

    public void setFilterSpare02(String filterSpare02) {
        this.filterSpare02 = filterSpare02;
    }

    public Set<String> getSpare02Set() {
        return spare02Set;
    }

    public void setSpare02Set(Set<String> spare02Set) {
        this.spare02Set = spare02Set;
    }

    public String getFilterSpare03() {
        return filterSpare03;
    }

    public void setFilterSpare03(String filterSpare03) {
        this.filterSpare03 = filterSpare03;
    }

    public Set<String> getSpare03Set() {
        return spare03Set;
    }

    public void setSpare03Set(Set<String> spare03Set) {
        this.spare03Set = spare03Set;
    }

    public String getFilterSpare04() {
        return filterSpare04;
    }

    public void setFilterSpare04(String filterSpare04) {
        this.filterSpare04 = filterSpare04;
    }

    public Set<String> getSpare04Set() {
        return spare04Set;
    }

    public void setSpare04Set(Set<String> spare04Set) {
        this.spare04Set = spare04Set;
    }

    public String getFilterSpare05() {
        return filterSpare05;
    }

    public void setFilterSpare05(String filterSpare05) {
        this.filterSpare05 = filterSpare05;
    }

    public Set<String> getSpare05Set() {
        return spare05Set;
    }

    public void setSpare05Set(Set<String> spare05Set) {
        this.spare05Set = spare05Set;
    }

    public List<AssetInfo> getResultList() {
        return resultList;
    }

    public String getFilterAccountingDeptOrgOrg() {
        return filterAccountingDeptOrgOrg;
    }

    public void setFilterAccountingDeptOrgOrg(String filterAccountingDeptOrgOrg) {
        this.filterAccountingDeptOrgOrg = filterAccountingDeptOrgOrg;
    }

    public TreeNode[] getSelectedAccessOrgNodes() {
        return selectedAccessOrgNodes;
    }

    public void setSelectedAccessOrgNodes(TreeNode[] selectedAccessOrgNodes) {
        this.selectedAccessOrgNodes = selectedAccessOrgNodes;
    }
}
