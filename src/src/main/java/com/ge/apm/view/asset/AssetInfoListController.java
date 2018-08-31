package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.ge.apm.service.utils.PinYinComparatorByName;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.event.SelectEvent;

@ManagedBean
@ViewScoped
public class AssetInfoListController extends JpaCRUDController<AssetInfo> {

    private static final long serialVersionUID = -1L;
    private AssetInfoRepository dao = null;
    private AssetInfoMgrService aiMgrService;
    private AssetCreateService acService;
    private UserContextService userContextService;
    private OrgInfoRepository orgDao;
    private AssetFileImportService importService;
    private QrCodeService qrCodeService;
    private UaaService uaaService;
    private CustomConfigService ccService;
    private OrgInfoTreeService orgInfoTreeService;
    private I18nMessageRepository i18nDao;

    //选定的院区
    private OrgInfo selectSite;

    //选定科室过滤
//    private String fileterOrgName;
//    private String fileterAssetDeptOrgName;
    private String filterClinicalDeptOrg;
    private String filterAssetDeptOrg;

    private Set<String> clinicalDeptOrgUidSet;
    private Set<String> assetDeptOrgUidSet;
    //复选的设备
    private Map<String, Boolean> selectedMap;

    //搜索字串
    private String oneSearchStr;

    private boolean terminate = false;

    private String filterWarrantyDate = null;

    private String filterAssetStatus = null;

    private Date varWarrantyDateTo = null;
    private String warrantyFormatTime = null;
    private Boolean needShowAssetApproval;

    private StreamedContent excelFile;

    private StreamedContent zipFile;

    private String exportQrCodeTemplateId;

    private List<AssetInfo> currentPageAssets;

    private Sort lastPageSort; //export use
    private PageRequest lastPageRequest;

    private List<AssetInfo> selectedAssets;
    //custom config
    private CustomConfigObject customConfig;

    List<String> customedColumns;

    private List<OrgInfo> clinicalDeptList;

    private Integer assetGroup;
    private Integer assetGroupSecondary;

    private List<I18nMessage> assetGroupSecondayList;

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        aiMgrService = WebUtil.getBean(AssetInfoMgrService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);

        terminate = Boolean.valueOf(WebUtil.getRequestParameter("terminate"));

        importService = WebUtil.getBean(AssetFileImportService.class);

        this.filterBySite = false;
        this.selectedMap = new HashMap();
        loadCustomConfig();
        loadClinicalDeptList();

    }

    private void loadCustomConfig() {
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        List<String> readFields = ccService.getReadAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        customedColumns = customConfig.getAssetsListPageFields().stream().filter(item -> readFields.contains(item)).collect(Collectors.toList());
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        lastPageRequest = pageRequest;
        lastPageSort = pageRequest.getSort(); //export excel use
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        setUserSearchFilters();
        selected = null;
        Page<AssetInfo> res = dao.findBySearchFilter(this.searchFilters, pageRequest);
        currentPageAssets = res.getContent();
        return res;
    }

    private void setUserSearchFilters() {
        this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        if (terminate) {
            this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, false));
        } else {
            this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        }

        if (!this.hasFilterOnField("siteUID")) {
            UserAccount user = UserContextService.getCurrentUserAccount();
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }

        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptName", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }

//        if (!Strings.isNullOrEmpty(fileterAssetDeptOrgName)) {
//            OrgInfo deptOrg = clinicalDeptList.stream().filter(item -> fileterAssetDeptOrgName.equals(item.getName())).findAny().get();
//            this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.EQ, deptOrg.getUid(), "1*"));
//        } else {
//            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
//        }

//        if (!Strings.isNullOrEmpty(fileterOrgName)) {
//            this.updateSearchFilter(new SearchFilter("clinicalDeptName", SearchFilter.Operator.EQ, fileterOrgName, "1*"));
//        } else {
//            searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptName".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
//        }

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



        if (null != selectSite) {
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        }

    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        if (null != event) {
            if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
                selectSite = (OrgInfo) event.getTreeNode().getData();
            } else {
                selectSite = null;
            }
            loadClinicalDeptList();

            filterClinicalDeptOrg = null;
            filterAssetDeptOrg = null;
            assetDeptOrgUidSet = null;
            clinicalDeptOrgUidSet = null;
        }
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getUserAccessibleSiteListWithClinicalDept(UserContextService.getCurrentUserAccount().getUid())
                .stream().filter(item -> item.getOrgType() == 4 || item.getOrgType() == 5).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }

    public void setOneSearchStr(String oneSearchStr) {
        this.oneSearchStr = oneSearchStr;
        if (oneSearchStr.isEmpty()) {
            this.removeFilterOnField("name");
            this.removeFilterOnField("departNum");
            this.removeFilterOnField("financingNum");
            this.removeFilterOnField("qrCode");
            this.removeFilterOnField("serialNum");
            this.removeFilterOnField("manufacture");
            this.removeFilterOnField("clinicalDeptName");
        }
    }

    public void onSelectAsset() throws IOException {
        String url = "actionName=View&selectedid="
                + selected.getId().toString()
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

    public String getEditPage(String assetId, String assetName) {
        String operation = "AssetCreate.xhtml?actionName=Edit&selectedid=" + assetId
                + "&asset_name=" + assetName
                + "&terminate=" + this.terminate;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public void deleteAsset(AssetInfo asset) {
        aiMgrService.deleteAsset(asset);
    }

    public void cancleWarranty(String assetUid) {
        AssetInfo ai = dao.findByUid(assetUid);
        if (ai != null) {
            ai.setWarranty(false);
            dao.save(ai);
        }
    }

    public void exportExcel() throws IOException {

        List<String> exportFields = ccService.getExportAssetFieldsByRoles(customConfig, UserContextService.getRoles());

        //非设备科的人导出的时候，导出部分数据
        boolean allColumns = false;
        if (userContextService.hasRole("AssetHead") || userContextService.hasRole("AssetStaff")) {
            allColumns = true;
        }

        Pageable pageable = new PageRequest(0, 100000000, lastPageSort);

//        List<AssetInfo> assetList = dao.findBySearchFilter(this.searchFilters, pageable).getContent();
        List<OrgInfo> orgList = orgDao.findByParentUID(UserContextService.getCurrentUserAccount().getSiteUID());
        orgList = orgList.stream().filter(orgInfo -> orgInfo.getOrgType() == 4).collect(Collectors.toList());

        List<OrgInfo> sites = orgDao.getSitesByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());

        List<AssetInfo> assetList = getExportList();

        ExcelDocument doc = importService.exportAsset(assetList, sites, orgList, exportFields);

        excelFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "AssetInfo_export.xlsx");
    }

    public void batchProcessToBeTerminated() {
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
            assetInfo.setStatus(4);
            dao.save(assetInfo);
        }
        this.selectedMap.clear();
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }

    public List<AssetInfo> getExportList() {
        List<String> assetUids = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toList());
        return assetUids.isEmpty() ? dao.findBySearchFilter(searchFilters) : dao.findByUidIn(assetUids);
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

    public List<QrCodeTemplate> getQrCodeTemplates() {
        QrCodeTemplateService qtService = WebUtil.getBean(QrCodeTemplateService.class);
        return qtService.getTemplateByUser(UserContextService.getCurrentUserAccount());
    }

    public void exportQrCode() throws Exception {
        List<AssetInfo> exportList = getExportList();
        zipFile = qrCodeService.exportQrCodeListByAssets(exportList.stream().filter(item -> !Strings.isNullOrEmpty(item.getQrCode())).collect(Collectors.toList()), exportQrCodeTemplateId);
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
                WebUtil.addErrorMessage(String.format("设备名称[%s]存在未完成工单，不能报废！", assetInfo.getName()));
                continue;
            }
            assetInfo.setIsValid(false);
            assetInfo.setTerminateDate(new Date());
            dao.save(assetInfo);
        }
        this.selectedMap.clear();
    }

    public boolean isShowColumn(String fieldName) {
        return customedColumns.contains(fieldName);
    }

    public String getSiteOrgName(String siteUID) {
        OrgInfo org = orgDao.getByUid(siteUID);
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
                .filter(distinctByOrgName(OrgInfo::getName))  //display unique clinical name only
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

    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterClinicalDeptOrg = (String)((SelectEvent) event).getObject();
//        } else {
//            if (!Strings.isNullOrEmpty(filterClinicalDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterClinicalDeptOrg.equals(item.getName()))) {
//                filterClinicalDeptOrg = null;
//            }
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
            filterAssetDeptOrg = (String)((SelectEvent) event).getObject();
//        } else {
//            if (!Strings.isNullOrEmpty(filterAssetDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterAssetDeptOrg.equals(item.getName()))) {
//                filterAssetDeptOrg = null;
//            }
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

        assetGroupSecondary = null;

        this.removeFilterOnField("assetGroupSecondary");


        if (lastPageRequest != null) {
            loadData(lastPageRequest);
        }

    }
    
     public OrgInfo getOrgInfoByUid(String uid) {
        if(Strings.isNullOrEmpty(uid)){
            return null;
        }
        return orgDao.getByUid(uid);
    }

    //getter and setter
    public Boolean getNeedShowAssetApproval() {
        if (null == needShowAssetApproval) {
            needShowAssetApproval = acService.needShowAssetApprovalByUser(UserContextService.getCurrentUserAccount().getUid());
        }
        return needShowAssetApproval;
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
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

//    public String getFileterOrgName() {
//        return fileterOrgName;
//    }
//
//    public void setFileterOrgName(String fileterOrgName) {
//        this.fileterOrgName = fileterOrgName;
//    }

//    public String getFileterAssetDeptOrgName() {
//        return fileterAssetDeptOrgName;
//    }
//
//    public void setFileterAssetDeptOrgName(String filterAssetDeptOrgName) {
//        this.fileterAssetDeptOrgName = fileterAssetDeptOrgName;
//    }

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

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

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
}
