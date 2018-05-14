package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.*;
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
import java.util.stream.Collectors;

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

    //选定的院区
    private OrgInfo selectSite;

    //选定科室过滤
    private OrgInfo fileterOrg;

    //复选的设备
    private Map<String, Boolean> selectedMap;

    //搜索字串
    private String oneSearchStr;

    private boolean terminate = false;

    private boolean isSelectedCurrentPage = false;

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
        terminate = Boolean.valueOf(WebUtil.getRequestParameter("terminate"));

        importService = WebUtil.getBean(AssetFileImportService.class);

        this.filterBySite = false;
        this.selectedMap = new HashMap();

    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
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
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            onSelectSiteTreeNode(null);
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

        if (null != fileterOrg) {
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, fileterOrg.getUid(), "1*"));
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
            onClinicalDeptFilter(null);
        }
    }

    public void onClinicalDeptFilter(NodeSelectEvent event) {
        this.selectedMap.clear();
        if (null != event && null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            OrgInfo deptOrg = (OrgInfo) event.getTreeNode().getData();
            if (deptOrg.getOrgType() == 4) {
                fileterOrg = deptOrg;
                return;
            }
        }
        fileterOrg = null;
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
        url = "Detail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public void onItemSelected() {
        //TODO remove this ?
//        isSelectedCurrentPage = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
    }

    public void onSelectAllOfPage() {
        for (AssetInfo item : currentPageAssets) {
            selectedMap.put(item.getUid(), this.isSelectedCurrentPage);
        }
    }

    public String getEditPage(String assetId, String assetName) {
        String operation = "Create.xhtml?actionName=Edit&selectedid=" + assetId
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

        //非设备科的人导出的时候，导出部分数据
        boolean allColumns = false;
        if (userContextService.hasRole("AssetHead") || userContextService.hasRole("AssetStaff")) {
            allColumns = true;
        }

        Pageable pageable = new PageRequest(0, 100000000, lastPageSort);

        List<AssetInfo> assetList = dao.findBySearchFilter(this.searchFilters, pageable).getContent();

        List<OrgInfo> orgList = orgDao.findByParentUID(UserContextService.getCurrentUserAccount().getSiteUID());
        orgList = orgList.stream().filter(orgInfo -> orgInfo.getOrgType() == 4).collect(Collectors.toList());

        List<String> assetUids = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toList());
        if (!assetUids.isEmpty()) {
            assetList = assetList.stream().filter(assetInfo -> {
                Boolean exists = this.selectedMap.get(assetInfo.getUid());
                if (exists == null) {
                    return false;
                }
                return exists.booleanValue();
            }).collect(Collectors.toList());
        }

        ExcelDocument doc = importService.exportAsset(assetList, orgList, allColumns);

        DefaultStreamedContent streamedContent = new DefaultStreamedContent(doc.getStream());
        streamedContent.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        streamedContent.setName("AssetInfo_export.xlsx");

        excelFile = streamedContent;
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
    }

    public boolean isChecked() {
//        Map<String, Boolean> map = this.selectedMap;
//        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
//            if (entry.getValue()) {
//                return true;
//            }
//        }
//        return false;
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

    public OrgInfo getFileterOrg() {
        return fileterOrg;
    }

    public void setFileterOrg(OrgInfo fileterOrg) {
        this.fileterOrg = fileterOrg;
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

    public boolean getIsSelectedCurrentPage() {
        if (null == currentPageAssets) {
            return false;
        } else {
            this.isSelectedCurrentPage = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
            return isSelectedCurrentPage;
        }
    }

    public void setIsSelectedCurrentPage(boolean isSelectedCurrentPage) {
        this.isSelectedCurrentPage = isSelectedCurrentPage;
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

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public Boolean needAssetApproval(String siteUid) {
        return acService.needAssetApproval(siteUid);
    }

}
