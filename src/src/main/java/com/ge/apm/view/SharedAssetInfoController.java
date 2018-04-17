package com.ge.apm.view;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.SharedAssetTypeRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SharedAssetType;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.data.FilterEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SharedAssetInfoController extends JpaCRUDController<AssetInfo> {

    AssetInfoRepository dao = null;
    private SharedAssetTypeRepository swapDao;
    private UaaService uaaService;

    private String oneSearchStr;

    private String filterSatId;

    private Map<String, Boolean> selectedMap;

    private List<AssetInfo> currentPageAssets;

    private Boolean isSelectedCurrentPage = false;

    private String batchSharedAssetTypeUid;

    private long queryIndex = -1;
    public long getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(long queryIndex) {
        this.queryIndex = queryIndex;
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        swapDao = WebUtil.getBean(SharedAssetTypeRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);

        this.selectedMap = new HashMap();

    }

    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {

        if (this.searchFilters == null){
            this.searchFilters = new ArrayList<>();
        }
        setUserSearchFilters();

        searchFilters.add(new SearchFilter("sharedAssetTypeUid", SearchFilter.Operator.IsNull,null));

        if (queryIndex == 1){
            this.updateSearchFilter(new SearchFilter("sharedAssetTypeUid",SearchFilter.Operator.IsNotNull,null));
        }else if (queryIndex == 2){
            this.removeFilterOnField("sharedAssetTypeUid");
        }

        if (filterSatId != null && !filterSatId.equals("")){
            searchFilters.add(new SearchFilter("sharedAssetTypeUid",SearchFilter.Operator.EQ,filterSatId));
        }

        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            Page<AssetInfo> res = dao.findBySearchFilter(this.searchFilters, pageRequest);
            currentPageAssets = res.getContent();
            this.isSelectedCurrentPage = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
            return res;
        }
    }

    private void setUserSearchFilters() {
        this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));

        if (!this.hasFilterOnField("siteUID")) {
            UserAccount user = UserContextService.getCurrentUserAccount();
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));

            this.selectedMap.clear();
        }

    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    public void searchList(Long queryIndex){
        this.queryIndex = queryIndex;
        this.searchFilters.clear();
        this.filterSatId = null;
        this.selectedMap.clear();
    }

    public String getOneSearchStr() {
        return oneSearchStr;
    }

    public void setOneSearchStr(String oneSearchStr) {
        this.oneSearchStr = oneSearchStr;
        if (oneSearchStr.isEmpty()) {
            this.removeFilterOnField("name");
            this.removeFilterOnField("departNum");
            this.removeFilterOnField("financingNum");
            this.removeFilterOnField("qrCode");
        }
    }

    public String getSwapName(String id){
        if (id == null || id.equals("")){
            return "";
        }
        SharedAssetType sat = swapDao.findById(id);
        return sat.getSwapName();
    }

    public List<SharedAssetType> allType(){
        ArrayList<SearchFilter> satSearchFilters = new ArrayList<>();
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        satSearchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        satSearchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        satSearchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        satSearchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));

        return swapDao.findBySearchFilter(satSearchFilters);
    }

    public void onSelectAllOfPage() {
        for (AssetInfo item : currentPageAssets) {
            selectedMap.put(item.getUid(), this.isSelectedCurrentPage);
        }
    }

    public void onItemSelected() {
        isSelectedCurrentPage = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
    }

    public void batchSave(){
        List<String> assetUids = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toList());
        dao.batchUpdateAssetInfoSwapType(batchSharedAssetTypeUid,assetUids);
        this.batchSharedAssetTypeUid = null;
        this.selectedMap.clear();
    }

    public void saveOne(){
        super.save();
        this.selectedMap.clear();
    }

    public void prepareEditOne(String uid){
        selected = dao.findByUid(uid);
    }

    public String getFilterSatId() {
        return filterSatId;
    }

    public void setFilterSatId(String filterSatId) {
        this.filterSatId = filterSatId;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public Boolean getIsSelectedCurrentPage() {
        return isSelectedCurrentPage;
    }

    public void setIsSelectedCurrentPage(Boolean isSelectedCurrentPage) {
        this.isSelectedCurrentPage = isSelectedCurrentPage;
    }

    public Boolean editFlag() {
        boolean anyMatch = selectedMap.values().stream().anyMatch(item -> item.booleanValue()==true);
        return anyMatch;
    }

    public String getBatchSharedAssetTypeUid() {
        return batchSharedAssetTypeUid;
    }

    public void setBatchSharedAssetTypeUid(String batchSharedAssetTypeUid) {
        this.batchSharedAssetTypeUid = batchSharedAssetTypeUid;
    }
}
