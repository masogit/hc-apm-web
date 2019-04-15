package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetInfoToBeTerminatedListController extends JpaCRUDController<AssetInfo> {

    private static final Logger logger = LoggerFactory.getLogger(AssetInfoToBeTerminatedListController.class);

    private static final long serialVersionUID = -1L;
    private AssetInfoRepository dao = null;
    private AssetInfoMgrService aiMgrService;
    private AssetCreateService acService;
    private UserContextService userContextService;
    private OrgInfoRepository orgDao;
    private UaaService uaaService;
    private CustomConfigService ccService;
    private OrgInfoTreeService orgInfoTreeService;
    private I18nMessageRepository i18nDao;

    //选定的院区
    private OrgInfo selectSite;

    //选定科室过滤
    private String filterClinicalDeptOrg;
    private String filterAssetDeptOrg;

    private Set<String> clinicalDeptOrgUidSet;
    private Set<String> assetDeptOrgUidSet;
    //复选的设备
    private Map<String, Boolean> selectedMap;

    //搜索字串
    private String oneSearchStr;

    private Date varWarrantyDateTo = null;

    private String warrantyFormatTime = null;
    private String manufactDateFormatTime = null;
    private String installDateFormatTime = null;
    private String arriveDateFormatTime = null;
    private String factoryWarrantyDateFormatTime = null;

    private Boolean needShowAssetApproval;

    private StreamedContent excelFile;

    private StreamedContent toBeTerminatedAppraisalFile;

    private List<AssetInfo> currentPageAssets;

    private Sort lastPageSort; //export use

    private List<AssetInfo> selectedAssets;
    //custom config
    private CustomConfigObject customConfig;

    List<String> customedColumns;

    private List<OrgInfo> clinicalDeptList;

    private Integer assetGroup;
    private Integer assetGroupSecondary;

    private List<I18nMessage> assetGroupSecondayList;

    //状态监控报表跳转参数
    private String filterAssetStatus = null;
    private String filterWarrantyDate = null;
    private Integer assetId;

    private Map<String, Object> selectedObject;

    private ExportToBeTerminatedAppraisalService exportToBeTerminatedAppraisalService;

    private UserAccount user;
    private TenantInfoRepository tenantInfoRepository;
    private Boolean isInsulate;

    private UploadedFile uploadedFile;
    private BlobFileService blobFileService;
    private V2_BlobObject uploadedFileObject;

    private String terminationCause;

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        user = UserContextService.getCurrentUserAccount();
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        isInsulate = tenantInfoRepository.findByuid(user.getTenantUID()).getInsulate();
        isInsulate = isInsulate == null ? false : isInsulate;
        blobFileService = WebUtil.getBean(BlobFileService.class);

        dao = WebUtil.getBean(AssetInfoRepository.class);
        aiMgrService = WebUtil.getBean(AssetInfoMgrService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        exportToBeTerminatedAppraisalService = WebUtil.getBean(ExportToBeTerminatedAppraisalService.class);

        this.filterBySite = false;
        this.selectedMap = new HashMap();
        this.selectedObject = new HashMap();
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
        lastPageSort = pageRequest.getSort(); //export excel use
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        setUserSearchFilters();
        selected = null;
        Page<AssetInfo> res = dao.findBySearchFilter(this.searchFilters, pageRequest);
        currentPageAssets = res.getContent();

        addMisingClinicalDeptOrg(currentPageAssets);

        return res;
    }


    public StreamedContent getActiveTemplate() {
        try {
            byte[] activeTemplateBytes = exportToBeTerminatedAppraisalService.getActiveTemplateBytes(isInsulate, user.getSiteUID(), user.getHospitalUID(), user.getInstitutionUID(), user.getTenantUID());
            return new DefaultStreamedContent((new ByteArrayInputStream(activeTemplateBytes)), "application/msword", "template.docx");
        } catch (IOException e) {
            WebUtil.addErrorMessage("Fail to download template");
        }
        return null;
    }

    public void exportToBeTerminatedAppraisalFile() {
        try {
            List<String> selectedIds = selectedMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());
            byte[] activeTemplateBytes = exportToBeTerminatedAppraisalService.getActiveTemplateBytes(isInsulate, user.getSiteUID(), user.getHospitalUID(), user.getInstitutionUID(), user.getTenantUID());
            toBeTerminatedAppraisalFile = exportToBeTerminatedAppraisalService.export(selectedIds, activeTemplateBytes);
        } catch (IllegalStateException e) {
            WebUtil.addErrorMessage(e.getMessage());
        } catch (IOException e) {
            WebUtil.addErrorMessage("Fail to download report");
        }
    }

    public void saveTerminationCause() {
        List<AssetInfo> updatedAssets = selectedMap.entrySet().stream().filter(i -> i.getValue()).map(i -> {
            String assetUid = i.getKey();
            AssetInfo assetInfo = dao.getByUid(assetUid);
            assetInfo.setTerminationCause(terminationCause);
            return assetInfo;
        }).collect(Collectors.toList());
        dao.save(updatedAssets);
        terminationCause = null;
    }

    public void uploadDataFile(FileUploadEvent event) {
        try {
            this.uploadedFile = event.getFile();
            String fileName = uploadedFile.getFileName();
            if (!StringUtils.endsWithIgnoreCase(fileName, ".docx")) {
                throw new IllegalStateException("Invalid file type: " + uploadedFile.getFileName());
            }
            uploadedFileObject = blobFileService.uploadFile(uploadedFile);
            logger.info("File is uploaded as " + uploadedFileObject.getObjectStorageId());
        } catch (Exception e) {
            WebUtil.addErrorMessage("文件上传失败!");
        }
    }

    private void addMisingClinicalDeptOrg(List<AssetInfo> currentPageAssets) {
        Set<String> orgUids = currentPageAssets.stream().filter(item -> item.getClinicalDeptUID() != null).map(item -> item.getClinicalDeptUID()).collect(Collectors.toSet());
        Set<String> orgUids2 = currentPageAssets.stream().filter(item -> item.getAssetDeptUID() != null).map(item -> item.getAssetDeptUID()).collect(Collectors.toSet());
        orgUids.addAll(orgUids2);

        if (orgUids.isEmpty()) {
            return;
        }

        List<OrgInfo> orgInfoList = orgDao.findByUids(orgUids);

        Map<String, OrgInfo> orgInfoMap = orgInfoList.stream().collect(Collectors.toMap(item -> item.getUid(), item -> item));

        currentPageAssets.forEach(item -> {
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
        });

    }

    private void setUserSearchFilters() {
        this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        this.updateSearchFilter(new SearchFilter("status", SearchFilter.Operator.EQ, 4));

        if (!UserContextService.isMVSRole()) {
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
        }

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

        Optional<SearchFilter> nameFilterOptional = this.getFilterOnField("name");
        if (nameFilterOptional.isPresent() && !nameFilterOptional.get().conditionPath.equals("1*3+")) {

            SearchFilter nameFilter = nameFilterOptional.get();
            nameFilter.conditionPath = "1*name+";
            this.updateSearchFilter(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, nameFilter.value, nameFilter.conditionPath));
        }

        if (UserContextService.isMVSRole()) {
            this.updateSearchFilter(new SearchFilter("mvsEquip", SearchFilter.Operator.EQ, 1));
        }

    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {

        this.searchFilters.clear();

        if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            selectSite = (OrgInfo) event.getTreeNode().getData();
        } else {
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
        assetGroupSecondary = null;
        filterAssetStatus = null;

    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }

    public void setOneSearchStr(String oneSearchStr) {
        this.oneSearchStr = oneSearchStr;
        if (oneSearchStr.isEmpty()) {
            this.removeFilterOnField("name");
            this.removeFilterOnField("namePyCode");
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
                + "&asset_name=" + selected.getName();
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
                + "&asset_name=" + assetName;
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

        List<String> exportFields = ccService.getLocalizedExportableAssetFieldsByRoles(customConfig, UserContextService.getRoles());

        List<OrgInfo> orgList = orgDao.findBySiteUIDAndOrgType(UserContextService.getCurrentUserAccount().getSiteUID(), 4);

        List<OrgInfo> sites = orgDao.getByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());

        logger.info("load asset list to export");
        List<AssetInfo> assetList = getExportList();

        logger.info("start to export asset list to excel");

        AssetFileDynamicTemplate template = new AssetFileDynamicTemplate();
        template.exportAsset(assetList, sites, orgList, exportFields);

        logger.info("exported asset list to excel, start to get input stream");

        excelFile = new DefaultStreamedContent(template.getInputStream(), "application/vnd.ms-excel", "AssetInfo_export.xlsx");
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }

    public List<AssetInfo> getExportList() {
        List<String> assetUids = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toList());
        List<AssetInfo> resultList;
        if (assetUids.isEmpty()) {
            resultList = dao.findBySearchFilter(searchFilters, lastPageSort);
        } else {
            resultList = dao.findByUidIn(assetUids, lastPageSort);
        }

        return resultList;
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

            if (aiMgrService.isInDispenceCenter(assetInfo)) {
                WebUtil.addErrorMessageWithParams("操作失败，设备名称[%s]，设备编号[%s]为在借中的调剂设备，请先归还！", assetInfo.getName(), assetInfo.getDepartNum());
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

    public void assetGroupChangeListener() {

        if (assetGroup == null) {
            assetGroupSecondayList = Collections.emptyList();
        } else {
            assetGroupSecondayList = i18nDao.getByMsgTypeAndSiteId("assetGroupSecondary_" + String.valueOf(assetGroup.intValue()), userContextService.getLoginUser().getSiteId());
        }

    }

    public void assetGroupSecondaryChangeListener() {

    }

    public OrgInfo getOrgInfoByUid(String uid) {
        if (Strings.isNullOrEmpty(uid)) {
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

    public StreamedContent getToBeTerminatedAppraisalFile() {
        return toBeTerminatedAppraisalFile;
    }

    public void setToBeTerminatedAppraisalFile(StreamedContent toBeTerminatedAppraisalFile) {
        this.toBeTerminatedAppraisalFile = toBeTerminatedAppraisalFile;
    }

    public String getOneSearchStr() {
        return oneSearchStr;
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

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getTerminationCause() {
        return terminationCause;
    }

    public void setTerminationCause(String terminationCause) {
        this.terminationCause = terminationCause;
    }
}
