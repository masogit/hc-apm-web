package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.cons.ModifySourceEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.*;
import java.lang.reflect.Field;
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
    private AssetInfoParentAssetService parentAssetService;
    private AssetHistoryService assetHistoryService;

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

    private String terminationCause;

    private List<AssetInfo> batchUpdateAssets;

    private UaaService uuaService;

    private StreamedContent assetTemplateFile;
    private AssetLifecyleService assetLifecyleService;

    private AssetInfoMapper assetInfoMapper;

    private FileDownloadService fileDownloadService;

    private SystemTemplateService systemTemplateService;
    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    private List<Field> fieldList = new ArrayList<>();



    @Override
    protected void init() {
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        systemTemplateService = WebUtil.getBean(SystemTemplateService.class);

        user = UserContextService.getCurrentUserAccount();
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        isInsulate = tenantInfoRepository.findByuid(user.getTenantUID()).getInsulate();
        isInsulate = isInsulate == null ? false : isInsulate;

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
        uuaService = WebUtil.getBean(UaaService.class);
        assetLifecyleService = WebUtil.getBean(AssetLifecyleService.class);
        parentAssetService = WebUtil.getBean(AssetInfoParentAssetService.class);
        assetHistoryService = WebUtil.getBean(AssetHistoryService.class);
        assetInfoMapper = WebUtil.getBean(AssetInfoMapper.class);

        this.filterBySite = false;
        this.selectedMap = new HashMap<>();
        this.selectedObject = new HashMap<>();
        loadCustomConfig();
        loadClinicalDeptList();
        fieldList = Arrays.stream(AssetInfo.class.getDeclaredFields()).collect(Collectors.toList());

    }

    private void loadCustomConfig() {
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        List<String> readFields = ccService.getReadAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        customedColumns = customConfig.getAssetsListPageFields().stream().filter(item -> readFields.contains(item)).collect(Collectors.toList());
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        lastPageSort = pageRequest.getSort(); //export excel use
//        if (lastPageSort.getOrderFor("lastModifiedDate")!=null) { // default sort desc
//            lastPageSort = new Sort(new Sort.Order(Sort.Direction.DESC,"lastModifiedDate"));
//            pageRequest = new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),lastPageSort);
//        }

        String orderBySql=buildOrderBy(lastPageSort);
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        setUserSearchFilters();
        selected = null;
//        Page<AssetInfo> res = dao.findBySearchFilter(this.searchFilters, pageRequest);

        HashMap<String, Object> param = new HashMap<>();
        covQueryCondition(searchFilters, param);
        param.put("page", pageRequest.getPageSize());
        param.put("offset", pageRequest.getPageNumber() * pageRequest.getPageSize());
        if(orderBySql!=null){
            param.put("orderBy", orderBySql);

        }
        param.put("tenantUid", user.getTenantUID());
        List<AssetInfo> list= assetInfoMapper.toBeTerminatedList(param);
        Long count= assetInfoMapper.toBeTerminatedListCount(param);
        PageImpl<AssetInfo> res = new PageImpl<>(list, pageRequest, count);
        currentPageAssets = res.getContent();
        addMissingClinicalDeptOrg(currentPageAssets);
        return res;
    }
    public boolean checkScrapConfit(){
        String assetScrapValue = userContextService.getConfigItemValue("assetScrap");
        String showAssetScrapNoValue = userContextService.getConfigItemValue("showAssetScrapNo");
        if(Boolean.TRUE.toString().equals(assetScrapValue) && Boolean.TRUE.toString().equals(showAssetScrapNoValue)){
            return true;

        }
        return false;
    }

    public boolean checkWarehouseSystemStatusEnabled() {
        String value = userContextService.getConfigItemValue("enableWarehouseSystemStatus");
        return Boolean.TRUE.toString().equals(value);
    }

    public void batchUpdateWarehouseSystemStatus() {
        List<String> assetUidList = selectedMap.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (assetUidList.isEmpty()) {
            WebUtil.addErrorMessage("请先选择记录");
            return;
        }
        List<AssetInfo> assetInfos = dao.findByUidIn(assetUidList);
        int count = 0;
        for (AssetInfo asset : assetInfos) {
            if (!asset.getIsValid()) {
                continue;
            }
            asset.setWarehouseSystemStatus(2);
            count++;
        }
        if (count == 0) {
            WebUtil.addErrorMessage("所选设备均已报废，无法修改");
            return;
        }
        dao.save(assetInfos);
        WebUtil.addSuccessMessage(WebUtil.getMessage("BatchUpdateWarehouseStatusSuccess"));
        this.selectedMap.clear();
    }
    private String buildOrderBy(Sort lastPageSort) {
        String sql=null;
        if(lastPageSort==null){
            sql="order by al.create_time desc nulls last";
        }else{
            String property = lastPageSort.iterator().next().getProperty();
            switch (property) {
                case "namePyCode":
                    sql="order by ai.name_py_code ";
                    break;
                case "functionType":
                    sql="order by ai.function_type ";
                    break;
                case "serialNum":
                    sql="order by ai.serial_num ";
                    break;
                case "departNum":
                    sql="order by ai.depart_num ";
                    break;
                case "assetGroup":
                    sql="order by ai.asset_group ";
                    break;
                case "assetGroupSecondary":
                    sql="order by ai.asset_group_secondary ";
                    break;
                case "manufacture":
                    sql="order by ai.manufacture ";
                    break;
                case "vendor":
                    sql="order by ai.vendor ";
                    break;
                case "subjectProject":
                    sql=" order by ai.subject_project ";
                    break;
                case "warrantyDate":
                    sql="order by ai.warranty_date ";
                    break;
                case "createdDate":
                    sql="order by ai.created_date ";
                    break;
                case "financingNum":
                    sql="order by ai.financing_num ";
                    break;
                case "qrCode":
                    sql="order by ai.qr_code ";
                    break;
                case "aliasName":
                    sql="order by ai.alias_name ";
                    break;
                case "assetOwnerName":
                    sql="order by ai.asset_owner_name ";
                    break;
                case "assetUsage":
                    sql="order by ai.asset_usage ";
                    break;
                case "capitalSource":
                    sql="order by ai.capital_source ";
                    break;
                case "capitalSource2":
                    sql="order by ai.capital_source2 ";
                    break;
                case "emergencyEquipment":
                    sql="order by ai.emergency_equipment ";
                    break;
                case "importEquipment":
                    sql="order by ai.import_equipment ";
                    break;
                case "makerShortName":
                    sql="order by ai.make_short_name ";
                    break;
                case "proprietaryClass":
                    sql="order by ai.proprietary_class ";
                    break;
                case "assetScrapId":
                    sql="order by sc.business_number ";
                    break;
                default:
                    sql="order by "+ StringUtil.camelToSnakeOptimized(property)+" ";
                    break;

            }
            if(sql!=null){
                if("ASC".equalsIgnoreCase(lastPageSort.iterator().next().getDirection().name())){
                    sql=sql+"asc";
                }else{
                    sql=sql+"desc";
                }
            }

        }


        return sql;
    }
    public void exportToBeTerminatedAppraisalFile(boolean singleFileImport) {
        try {
            toBeTerminatedAppraisalFile = null;
            List<String> selectedIds = selectedMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());
//            byte[] activeTemplateBytes = exportToBeTerminatedAppraisalService.getActiveTemplateBytes(isInsulate, user.getSiteUID(), user.getHospitalUID(), user.getInstitutionUID(), user.getTenantUID(),"ToBeTerminated");
            SystemTemplateEnum systemTemplateEnum=singleFileImport?SystemTemplateEnum.ASSET_TOBE_TERMINATED_SINGLE:SystemTemplateEnum.ASSET_TOBE_TERMINATED_MULTI;
            byte[] activeTemplateBytes = systemTemplateService.getTemplateFileByte(systemTemplateEnum, user);

            if(singleFileImport){
                toBeTerminatedAppraisalFile = exportToBeTerminatedAppraisalService.export(selectedIds, activeTemplateBytes);
            }else{
                toBeTerminatedAppraisalFile = exportToBeTerminatedAppraisalService.exportZip(selectedIds, activeTemplateBytes);

            }
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
            assetInfo.setTerminateDate(new Date());  //mobile端没有已报废的设备，先将鉴定的时间作为待报废资产的报废时间
            return assetInfo;
        }).collect(Collectors.toList());
        dao.save(updatedAssets);
        terminationCause = null;
    }

    @Transactional
    public void uploadDataFile(FileUploadEvent event) {
        try {
            this.uploadedFile = event.getFile();
            String fileName = uploadedFile.getFileName();
            if (!StringUtils.endsWithIgnoreCase(fileName, ".xlsx")) {
                uploadedFile = null;
                throw new IllegalStateException("Invalid file type: " + fileName);
            }

            UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
            String tenantUid = currentUserAccount.getTenantUID();
            List<OrgInfo> allAccessibleClinicalDeptList = uuaService.getAllAccessibleClinicalDeptList();
            Set<String> siteUids = allAccessibleClinicalDeptList.stream().map(i -> i.getSiteUID()).collect(Collectors.toSet());
            batchUpdateAssets = exportToBeTerminatedAppraisalService.getAssetInfo(uploadedFile.getInputstream(), tenantUid, siteUids);

            batchUpdateAssets.removeIf(i -> validateTermination(i));

        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("FileUploadFailed"));
        }
    }


    public void exportAssetExcelTemplate() {
//        InputStream resourceAsStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/info/APM_Asset_Termination_Template.xlsx");
        InputStream resourceAsStream =fileDownloadService.getTemplateFileInputStream(SystemTemplateEnum.BATCH_ASSET_TERMINATION.getCode(), SystemTemplateEnum.BATCH_ASSET_TERMINATION.getFileType());
        assetTemplateFile = new DefaultStreamedContent(resourceAsStream, "application/vnd.ms-excel", "template.xlsx");
    }

    public void batchUpdate() {
        batchUpdateAssets.forEach(i -> terminateAsset(i));
        batchUpdateAssets.clear();
        uploadedFile = null;
    }


    public void preEdit() {
        List<Map.Entry<String, Boolean>> selectedEntries = selectedMap.entrySet().stream().filter(i -> i.getValue()).collect(Collectors.toList());
        if (selectedEntries.size() == 1) {
            AssetInfo selectedAsset = selectedEntries.stream().map(i -> dao.getByUid(i.getKey())).findFirst().orElse(null);
            terminationCause = selectedAsset == null ? "" : selectedAsset.getTerminationCause();
        } else {
            terminationCause = "";
        }
    }


    private void addMissingClinicalDeptOrg(List<AssetInfo> currentPageAssets) {
        Set<String> orgUids = currentPageAssets.stream().filter(item -> item.getClinicalDeptUID() != null).map(item -> item.getClinicalDeptUID()).collect(Collectors.toSet());
        Set<String> orgUids2 = currentPageAssets.stream().filter(item -> item.getAssetDeptUID() != null).map(item -> item.getAssetDeptUID()).collect(Collectors.toSet());
        orgUids.addAll(orgUids2);
        if (orgUids.isEmpty()) {
            return;
        }
//        Set<String> uids = currentPageAssets.stream().filter(item -> item.getUid() != null).map(item -> item.getUid()).collect(Collectors.toSet());
//        if(!CollectionUtils.isEmpty(uids)){
//            List<AssetLifecycle> lifecycles= assetLifecyleService.selectByUidsAndStatus(uids, AssetConstant.ASSET_STATUS_TO_TERMINATED);
////        List<AssetLifecycle> lifecycles= assetLifecyleService.selectByAssetUidsAndStatusGroupAssetUid(uids,4);
//            Map<String, List<AssetLifecycle>> collect = lifecycles.stream().collect(Collectors.groupingBy(AssetLifecycle::getAssetUid));
//            currentPageAssets.forEach(item -> {
//                List<AssetLifecycle> assetLifecycles = collect.get(item.getUid());
//                if(!CollectionUtils.isEmpty(assetLifecycles)){
//                    List<AssetLifecycle> assetLifecycleSortDesc = assetLifecycles.stream().sorted(Comparator.comparing(AssetLifecycle::getModifyTime).reversed()).collect(Collectors.toList());
//                    item.setTerminateOperation(assetLifecycleSortDesc.get(0).getModifyBy());
//                    item.setTerminateOperationDate(assetLifecycleSortDesc.get(0).getModifyTime());
//                }
//
//            });
//        }
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
            /*this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));*/
            this.updateSearchFilter(new SearchFilter("oneSearchStr", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            /*this.updateSearchFilter(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptName", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));*/
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
                .stream().filter(item -> Objects.equals(item.getOrgType(), 4)).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }

    public void setOneSearchStr(String oneSearchStr) {
        this.oneSearchStr = oneSearchStr;
        if (StringUtils.isBlank(oneSearchStr)) {
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

    public void onSelectAsset() {
        String url = "actionName=View&selectedid="
                + selected.getUid().toString()
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
        insertCrossSiteColumns(exportFields);


        List<OrgInfo> orgList = orgDao.findBySiteUIDAndOrgType(UserContextService.getCurrentUserAccount().getSiteUID(), 4);

        List<OrgInfo> sites = orgDao.getByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());

        logger.info("load asset list to export");
        List<AssetInfo> assetList = getExportList();

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
        // 库房系统状态不在CustomConfigFields中，手动追加到导出列
        if (checkWarehouseSystemStatusEnabled()) {
            String warehouseStatusHeader = WebUtil.getMessage("warehouseSystemStatus");
            if (!exportFields.contains(warehouseStatusHeader)) {
                int uuidIndex = exportFields.indexOf("UUID");
                if (uuidIndex >= 0) {
                    exportFields.add(uuidIndex, warehouseStatusHeader);
                } else {
                    exportFields.add(warehouseStatusHeader);
                }
            }
        }
        template.exportAsset(assetList, sites, orgList, exportFields);

        logger.info("exported asset list to excel, start to get input stream");

        excelFile = new DefaultStreamedContent(template.getInputStream(), "application/vnd.ms-excel", "AssetInfo_export.xlsx");
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
        List<String> assetUidList = ListUtil.newArrayList("-1");
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (Boolean.FALSE.equals(entry.getValue())) {
                continue;
            }
            assetUidList.add(entry.getKey());
        }
        List<AssetInfo> assetInfos = dao.findByUidIn(assetUidList);
        List<AssetInfo> assetScrapList = assetInfos.stream().filter(t -> ObjectUtil.isNotEmpty(t.getAssetScrapId())).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(assetScrapList)) {
            String assetNames = StringUtils.join(assetScrapList.stream().map(AssetInfo::getName).collect(Collectors.toSet()), "、");
            WebUtil.addErrorMessage(assetNames + " 待多级审批通过，才能报废");
            return;
        }
        for (AssetInfo assetInfo : assetInfos) {
            terminateAsset(assetInfo);
        }
        List<FacesMessage> messageList = FacesContext.getCurrentInstance().getMessageList();
        long error = messageList.stream().filter(facesMessage -> facesMessage.getSeverity().toString().contains("ERROR")).count();
        String successMessage;
        if (error > 0) {
            successMessage = "其他设备批量报废成功";
        } else {
            successMessage = "设备批量报废成功";
        }
        WebUtil.addSuccessMessage(successMessage);
        this.selectedMap.clear();
    }

    public void terminateAsset(AssetInfo assetInfo) {
        if (assetInfo == null) {
            return;
        }

        logger.info("Terminate: " + assetInfo.getUid());
        if (validateTermination(assetInfo)) return;
        int originStatus = assetInfo.getStatus();
        AssetInfo oldAssetInfo = assetHistoryService.depCopy(assetInfo);
        assetInfo.setStatus(4);
        assetInfo.setIsValid(false);
        assetInfo.setWarehouseSystemStatus(3);
        assetInfo.setTerminateDate(new Date());
        assetHistoryService.saveAssetHistoryIfUpdated(oldAssetInfo,assetInfo,ModifySourceEnum.ASSET_SCRAP.getCode());
        dao.save(assetInfo);
//        assetLifecyleService.saveLifecycle(originStatus,assetInfo,UserContextService.getCurrentUserAccount());
        aiMgrService.updateNurseSiteMaintain(assetInfo.getUid());
    }

    public boolean validateTermination(AssetInfo assetInfo) {
        if (!Objects.equals(assetInfo.getStatus(), 4)) {
            WebUtil.addErrorMessage(String.format(WebUtil.getMessage("EquipmentCannotBeTerminated"), assetInfo.getName()));
            return true;
        }

        int workOrdersNum = dao.fetchCurrentWorkOrders(assetInfo.getId());
        if (workOrdersNum > 0) {
            WebUtil.addErrorMessage(String.format(WebUtil.getMessage("UnfinishedWorkorderCandBeTerminated"), ObjectUtil.isEmpty(assetInfo.getQrCode()) ? assetInfo.getDepartNum() : assetInfo.getQrCode()));
            return true;
        }

        if (aiMgrService.isInDispenceCenter(assetInfo)) {
            WebUtil.addErrorMessageWithParams(String.format(WebUtil.getMessage("SwappedInRentPleaseReturnItFirst"), assetInfo.getName(), assetInfo.getDepartNum()));
            return true;
        }
        return false;
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
        if (varWarrantyDateTo == null) {
            return warrantyFormatTime;
        } else {
            return "~" + DateUtil.format(varWarrantyDateTo, "yyyy/MM/dd");
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

    public StreamedContent getAssetTemplateFile() {
        return assetTemplateFile;
    }

    public void setAssetTemplateFile(StreamedContent assetTemplateFile) {
        this.assetTemplateFile = assetTemplateFile;
    }

    public String getParentAssetNameByAssetId(int assetId) {
        return parentAssetService.getParentAssetNameByAssetId(assetId);
    }



    public void covQueryCondition(List<SearchFilter> searchFilters, Map<String, Object> param) {
        for (SearchFilter searchFilter :searchFilters) {
            String fieldName = searchFilter.fieldName;
            switch (fieldName) {
                case "isDeleted":
                    param.put("isDeleted", searchFilter.value);
                    break;
                case "isValid":
                    param.put("isValid", searchFilter.value);
                    break;
                case "status":
                    param.put("status", searchFilter.value);
                    break;
                case "siteUID":
                    param.put("orgUidList", searchFilter.value);
                    break;
                case "oneSearchStr":
                    param.put("oneSearchStr", searchFilter.value);
                    break;
                case "assetDeptOrgUidSet":
                    param.put("assetDeptOrgUidSet", searchFilter.value);
                    break;
                case "clinicalDeptOrgUidSet":
                    param.put("clinicalDeptOrgUidSet", searchFilter.value);
                    break;
                case "selectSite":
                    param.put("selectSite", searchFilter.value);
                    break;
                case "mvsEquip":
                    param.put("mvsEquip", searchFilter.value);
                    break;
                case "qrCode":
                    param.put("qrCode", searchFilter.value);
                    break;
                case "name":
                    param.put("name", searchFilter.value);
                    break;
                case "assetGroup":
                    param.put("assetGroup", searchFilter.value);
                    break;
                case "functionType":
                    param.put("functionType", searchFilter.value);
                    break;
                case "financingNum":
                    param.put("financingNum", searchFilter.value);
                    break;
                case "departNum":
                    param.put("departNum", searchFilter.value);
                    break;
                case "makerShortName":
                    param.put("makerShortName", searchFilter.value);
                    break;
                case "vendor":
                    param.put("vendor", searchFilter.value);
                    break;
                case "usageStatus":
                    param.put("usageStatus",Integer.parseInt(searchFilter.value.toString()));
                    break;
                case "warehouseSystemStatus":
                    param.put("warehouseSystemStatus",Integer.parseInt(searchFilter.value.toString()));
                    break;
                case "terminationCause":
                    param.put("terminationCause", searchFilter.value);
                    break;
                case "workOrderId":
                    param.put("workOrderId", searchFilter.value);
                    break;
                case "terminateOperation":
                    param.put("terminateOperation", searchFilter.value);
                    break;
                default:
                Optional<Field> firstField = fieldList.stream().filter(item -> item.getName().equalsIgnoreCase(fieldName)).findFirst();
                if(firstField.isPresent()){
                    Field field = firstField.get();
                    String name = field.getName();
                    Class<?> type = field.getType();
                    if(name.equals("warrantyDate")){
                        String[] split = filterWarrantyDate.split("~");
                        if(ObjectUtil.isNotEmpty(split[0])){
                            param.put(name, DateUtil.parse(split[0], "yyyy/MM/dd"));
                            param.put(name+"1", DateUtil.parse(split[1], "yyyy/MM/dd"));
                        }else{
                            param.put(name, DateUtil.parse(split[1], "yyyy/MM/dd"));
                        }
                    }else if (type == Date.class){
                        String filterDate =(String) selectedObject.get(name);
                        String[] split = filterDate.split("~");
                        if(ObjectUtil.isNotEmpty(split[0])){
                            param.put(name, DateUtil.parse(split[0], "yyyy/MM/dd"));
                            param.put(name+"1", DateUtil.parse(split[1], "yyyy/MM/dd"));
                        }else{
                            param.put(name, DateUtil.parse(split[1], "yyyy/MM/dd"));
                        }
                    }else{
                        param.put(name,  searchFilter.value);
                    }

                }
                break;
            }
        }

        if (clinicalDeptOrgUidSet != null && !clinicalDeptOrgUidSet.isEmpty()) {
            param.put("clinicalDeptOrgUidSet", clinicalDeptOrgUidSet);
        }
        if (assetDeptOrgUidSet != null && !assetDeptOrgUidSet.isEmpty()) {
            param.put("assetDeptOrgUidSet", assetDeptOrgUidSet);
        }
        if (null != selectSite) {
            param.put("selectSite", selectSite.getUid());
        }
    }

}
