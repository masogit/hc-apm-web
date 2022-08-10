package com.ge.apm.view;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
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
    SharedAssetSwapIndexRepository sharedAssetSwapIndexDao;
    private WorkflowConfigRepository wfcDao;
    private UaaService uaaService;
    private OrgInfoRepository orgInfoRepository;
    private String oneSearchStr;

    private String filterSatId;

    //是否启用无人值守 true：启动
    private boolean whethertoenableunattended;

    public boolean isWhethertoenableunattended() {
        return whethertoenableunattended;
    }

    public void setWhethertoenableunattended(boolean whethertoenableunattended) {
        this.whethertoenableunattended = whethertoenableunattended;
    }

    //调剂编号
    private String swapIndex;

    public String getSwapIndex() {
        return swapIndex;
    }

    public void setSwapIndex(String swapIndex) {
        this.swapIndex = swapIndex;
    }

    private Map<String, Boolean> selectedMap;

    private List<AssetInfo> currentPageAssets;

    private Boolean isSelectedCurrentPage = false;

    private String batchSharedAssetTypeUid;

    List<String> deptids;

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
        wfcDao = WebUtil.getBean(WorkflowConfigRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        OrgInfo selectSite = userContextService.getSelectSite();
        if(selectSite != null){
            deptids = wfcDao.findDeptForSharedAsset(selectSite.getUid());
            WorkflowConfig workflowConfig = wfcDao.findFirstBySiteUID(selectSite.getUid());
            whethertoenableunattended=workflowConfig.getWhethertoenableunattended();
        }else{
            UserAccount user = UserContextService.getCurrentUserAccount();
            deptids = wfcDao.findDeptForSharedAsset(user.getSiteUID());
            WorkflowConfig workflowConfig = wfcDao.findFirstBySiteUID(user.getSiteUID());
            if(workflowConfig.getWhethertoenableunattended() == null || workflowConfig.getWhethertoenableunattended() == false){
                whethertoenableunattended = false;
            }else {
                whethertoenableunattended = true;
            }
        }

        sharedAssetSwapIndexDao = WebUtil.getBean(SharedAssetSwapIndexRepository.class);
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
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        OrgInfo selectSite = userContextService.getSelectSite();
        if(selectSite != null){
            deptids = wfcDao.findDeptForSharedAsset(selectSite.getUid());
        }else{
            UserAccount user = UserContextService.getCurrentUserAccount();
            deptids = wfcDao.findDeptForSharedAsset(user.getSiteUID());
        }
        List<SharedAssetSwapIndex> sharedAssetSwapIndexList = new ArrayList<>();
        //1、使用科室：调剂中心  所属科室：空
        updateSearchFilter(new SearchFilter("sharedAssetTypeUid", SearchFilter.Operator.IsNull,null));
        updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN,deptids));
//        searchFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.IsNull,null));

        if (queryIndex == 1){
            //2、使用科室：任意科室  所属科室：调剂中心
            this.updateSearchFilter(new SearchFilter("sharedAssetTypeUid",SearchFilter.Operator.IsNotNull,null));
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID",SearchFilter.Operator.IN,deptids));
            this.updateSearchFilter(new SearchFilter("assetDeptUID",SearchFilter.Operator.IsNotNull,null));
        }

        if (filterSatId != null && !filterSatId.equals("")){
            updateSearchFilter(new SearchFilter("sharedAssetTypeUid",SearchFilter.Operator.EQ,filterSatId));
        }

        for(int i = 0 ; i < this.searchFilters.size() ; i++){
            String fieldName = this.searchFilters.get(i).fieldName;
            if(fieldName.equals("swapIndex")){
                sharedAssetSwapIndexList = sharedAssetSwapIndexDao.findBySwapIndexLike("%" + this.searchFilters.get(i).value.toString() + "%");
                List<String> assetUids = new ArrayList<>();
                sharedAssetSwapIndexList.stream().forEach(sharedAssetSwapIndex -> assetUids.add(sharedAssetSwapIndex.getAssetUid()));
                searchFilters.add(new SearchFilter("uid",SearchFilter.Operator.IN,assetUids));
                this.removeFilterOnField("swapIndex");
            }
        }



        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            Page<AssetInfo> res = dao.findBySearchFilter(this.searchFilters, pageRequest);
            currentPageAssets = res.getContent();
            SharedAssetSwapIndex index = null;

            if(queryIndex == 1){
                for(AssetInfo assetInfo : res.getContent()) {
                    index = sharedAssetSwapIndexDao.findByAssetUid(assetInfo.getUid());
                    assetInfo.setSharedAssetSwapIndex(index == null ?  null : index.getSwapIndex());
                    assetInfo.setSharedAssetSwapIndexId(index == null ?  null : index.getId());
                }
            }

            this.isSelectedCurrentPage = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
            for(AssetInfo assetInfo : res.getContent()) {
                for(SharedAssetSwapIndex sharedAssetSwapIndex : sharedAssetSwapIndexList ) {
                    if(sharedAssetSwapIndex.getAssetUid().equals(assetInfo.getUid())) {
                        assetInfo.setSharedAssetSwapIndex(sharedAssetSwapIndex.getSwapIndex());
                        assetInfo.setSharedAssetSwapIndexId(sharedAssetSwapIndex.getId());
                    }
                }
            }
            return res;
        }
    }

    private void setUserSearchFilters() {
        this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));

        if (!this.hasFilterOnField("siteUID")) {
            UserContextService bean = WebUtil.getBean(UserContextService.class);
            OrgInfo selectSite = bean.getSelectSite();
            List<String> orgUidList=new ArrayList<>();
            if(selectSite != null){
                orgUidList = orgInfoRepository.getOrgInfoUidsByparentUid(selectSite.getUid());
            }else{
                UserAccount user = UserContextService.getCurrentUserAccount();
                orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            }
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));

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
        UserContextService bean = WebUtil.getBean(UserContextService.class);
        OrgInfo selectSite = bean.getSelectSite();
        if(selectSite != null){
            satSearchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, Arrays.asList(selectSite.getUid())));
        }else{
            UserAccount user = bean.getLoginUser();
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            satSearchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            satSearchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            satSearchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            satSearchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }

        return swapDao.findBySearchFilter(satSearchFilters);
    }

    public void onSelectAllOfPage() {
        for (AssetInfo item : currentPageAssets) {
            if (isBorrow(item.getUid()))
                continue;
            selectedMap.put(item.getUid(), this.isSelectedCurrentPage);
        }
    }

    public void onItemSelected() {
        isSelectedCurrentPage = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
    }

    public void batchSave(){
        List<String> assetUids = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toList());

        if (deptids.size() > 0 && batchSharedAssetTypeUid != null){
            dao.batchUpdateAssetInfoSwapType(batchSharedAssetTypeUid,assetUids,deptids.get(0));
        }else{
            dao.batchUpdateAssetInfoSwapType(batchSharedAssetTypeUid,assetUids,null);
        }

        this.batchSharedAssetTypeUid = null;
        this.selectedMap.clear();
    }

    public void saveOne() {
        String ind = this.selected.getSharedAssetSwapIndex();
        String sharedAssetTypeUid = this.selected.getSharedAssetTypeUid();
        String msg = "调剂编号已存在";
        if(whethertoenableunattended){

            String nullMsg = "请填写设备名称和调剂编号";
            //点击行修改按钮 设备表和调剂编号表同时做修改
            if (this.selected.getSharedAssetTypeUid() != null && ind == null ||this.selected.getSharedAssetTypeUid() == null && ind != null ) {
                WebUtil.addWarnMessage(nullMsg);
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            } else {
                //未配置调剂名称
                if (queryIndex == -1) {
                    //查询数据库是否已存在调剂编号
                    SharedAssetSwapIndex entity = sharedAssetSwapIndexDao.findBySharedAssetTypeaAndSwapIndexAnd(ind , this.selected.getClinicalDeptUID());
                    if (entity == null) {
                        super.save();
                        this.selectedMap.clear();
                        SharedAssetSwapIndex sharedAssetSwapIndex = new SharedAssetSwapIndex();
                        sharedAssetSwapIndex.setSharedAssetType(this.selected.getSharedAssetTypeUid());
                        sharedAssetSwapIndex.setAssetUid(this.selected.getUid());
                        sharedAssetSwapIndex.setSwapIndex(ind);
                        sharedAssetSwapIndexDao.save(sharedAssetSwapIndex);
                        swapIndex = null;
                    } else {
                        WebUtil.addWarnMessage(msg);
                        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                    }
                } else {
                    if(StringUtils.isBlank(sharedAssetTypeUid)){
                        sharedAssetSwapIndexDao.deleteByAssetUid(selected.getUid());
                        super.save();
                       // WebUtil.addWarnMessage("设备调剂名称解绑成功");
                       // RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                    }else{
                        //已配置调剂名称
                        //查找除当前数据以外的数据
                        if (ind==null){
                            ind="";
                        }
                        SharedAssetSwapIndex entity = sharedAssetSwapIndexDao.findNotData(ind, this.selected.getUid() , this.selected.getClinicalDeptUID());
                        if (entity == null) {
                            super.save();
                            this.selectedMap.clear();
                            //查找当前数据;
                            SharedAssetSwapIndex sharedAssetSwapIndex = new SharedAssetSwapIndex();
                            if(this.selected.getSharedAssetSwapIndexId() != null){
                                SharedAssetSwapIndex swapIndexEntity = sharedAssetSwapIndexDao.findById(this.selected.getSharedAssetSwapIndexId());
                                sharedAssetSwapIndex.setId(swapIndexEntity.getId());
                            }
                            sharedAssetSwapIndex.setSharedAssetType(this.selected.getSharedAssetTypeUid());
                            sharedAssetSwapIndex.setAssetUid(this.selected.getUid());
                            sharedAssetSwapIndex.setSwapIndex(ind);
                            sharedAssetSwapIndexDao.save(sharedAssetSwapIndex);
                            swapIndex = null;
                        } else {
                            WebUtil.addWarnMessage(msg);
                            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                        }
                    }

                }
            }
        }else{
            super.save();
            this.selectedMap.clear();
        }


    }

    @Override
    public void onBeforeUpdateObject(AssetInfo object) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        if (deptids.size() > 0) {
//            selected.setClinicalDeptUID(orgIds.get(0));
            OrgInfo orgInfo = orgInfoRepository.findByUid(deptids.get(0));
            selected.setAssetDeptUID(deptids.get(0));
            selected.setAssetDeptId(orgInfo != null ? orgInfo.getId() : null);
        }
        if (selected.getSharedAssetTypeUid() == null){
            selected.setAssetDeptUID(null);
            selected.setAssetDeptId(null);
        }
    }

    public boolean isBorrow(String uid){
        AssetInfo a = dao.findByUid(uid);
        String sharedAssetTypeUid = a.getSharedAssetTypeUid();
        String clinicalDeptUID = a.getAssetDeptUID();

        return sharedAssetTypeUid != null && !deptids.contains(clinicalDeptUID);

    }

    public void prepareEditOne(String uid){
        selected = dao.findByUid(uid);
        if(queryIndex == 1){
            //通过asset_info中uid查找swap_index(调剂编号)
            SharedAssetSwapIndex sharedAssetSwapIndex = sharedAssetSwapIndexDao.findByAssetUid(uid);
            selected.setSharedAssetSwapIndex(sharedAssetSwapIndex == null ? "" : sharedAssetSwapIndex.getSwapIndex());
            selected.setSharedAssetSwapIndexId(sharedAssetSwapIndex == null ? "" : sharedAssetSwapIndex.getId());
        }
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
    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        UserContextService bean = WebUtil.getBean(UserContextService.class);
        removeFilterOnField("siteUID");
        removeFilterOnField("institutionUID");
        removeFilterOnField("hospitalUID");
        bean.onSelectSiteTreeNode(event);

    }
}
