package com.ge.apm.view;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.secondment.SecondmentConfig;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.view.secondment.dept.SecondmentAssetSettingBatchController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.cons.ModifySourceEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;
import webapp.framework.web.mvc.GenericDataTableModel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SpecialAssetBatchController extends GenericCRUDController<AssetInfo> {

    private static final Logger log = LoggerFactory.getLogger(SpecialAssetBatchController.class);

    private UserAccountRepository userAccountRepository;
    private OrgInfoRepository orgInfoRepository;
    private AssetInfoRepository dao;
    private MqUtils mqUtils;
    private SpecialAssetInfoRepository specialAssetInfoRepository;

    private UserAccount userAccount;
    private List<String> visibleScope;
    private List<UserAccount> userAccountList;
    private List<AssetInfo> cacheDataTablePage;
    private List<AssetInfo> cacheDataTablePage2;
    private Map<String, Boolean> selectedAssetUidTable1;
    private Set<AssetInfo> selectedAssetsed1;
    private Boolean allSelected1 = Boolean.FALSE;

    private Map<String, Boolean> selectedAssetUidTable2;
    private Set<AssetInfo> selectedAssetsed2;
    private Boolean allSelected2 = Boolean.FALSE;
    private List<AssetInfo> selectAsset;
    private List<OrgInfo> cacheOrgList;
    private LazyDataModel<AssetInfo> lazyModel2;
    private WorkflowConfigRepository workflowConfigRepository;
    private boolean whetherUserOrgDispatchCenter = false;


    @Override
    protected void init() {
        dao= WebUtil.getBean(AssetInfoRepository.class);
        mqUtils= WebUtil.getBean(MqUtils.class);
        userAccountRepository= WebUtil.getBean(UserAccountRepository.class);
        orgInfoRepository= WebUtil.getBean(OrgInfoRepository.class);
        workflowConfigRepository= WebUtil.getBean(WorkflowConfigRepository.class);
        specialAssetInfoRepository= WebUtil.getBean(SpecialAssetInfoRepository.class);
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        initialization();
        initLazyModel2();

    }

    private void initLazyModel2() {
        lazyModel2 = new GenericDataTableModel<AssetInfo>() {
            @Override
            protected List<AssetInfo> load(PageRequest pageRequest) {
                int size = selectAsset.size();
                List<AssetInfo> result= new ArrayList<>();
                int pageNumber = pageRequest.getPageNumber();
                int pageSize = pageRequest.getPageSize();
                int start = pageNumber * pageSize;
                int end = (pageNumber+1) * pageSize;
                for (;start< end;start++){
                    if(start == size){
                        break;
                    }
                    result.add(selectAsset.get(start));
                }
                cacheDataTablePage2 = result;
                this.setRowCount(size);
                return result;
            }
        };
    }

    private void initialization(){
        userAccount = UserContextService.getCurrentUserAccount();
        visibleScope = orgInfoRepository.getUserDeptScope(userAccount.getUid());
        if(CollectionUtils.isNotEmpty(visibleScope)){
            filterAdjustmentCenter();
            if(CollectionUtils.isNotEmpty(visibleScope)){
                cacheOrgList = orgInfoRepository.findByUids(new HashSet<>(visibleScope));
            }else{
                cacheOrgList = new ArrayList<>();
            }
        }
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ,userAccount.getSiteUID()));
        searchFilters.clear();
        userAccountList = userAccountRepository.getByTenantUID(userAccount.getTenantUID());
        selectedAssetUidTable1= new HashMap<>();
        selectedAssetsed1 = new HashSet<>();
        allSelected1 = Boolean.FALSE;
        selectedAssetUidTable2= new HashMap<>();
        selectedAssetsed2 = new HashSet<>();
        allSelected2 = Boolean.FALSE;
        selectAsset = new ArrayList<>();
    }
    private void filterAdjustmentCenter(){
        String tenantUID = userAccount.getTenantUID();
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,tenantUID));
        searchFilters.add(new SearchFilter("deptForSharedAsset", SearchFilter.Operator.IsNotNull,tenantUID));
        List<WorkflowConfig> workflowConfigs = workflowConfigRepository.findBySearchFilter(searchFilters);
        if(CollectionUtils.isNotEmpty(workflowConfigs)){
            List<String> deptForSharedAsset = workflowConfigs.stream().map(WorkflowConfig::getDeptForSharedAsset).collect(Collectors.toList());
            visibleScope = visibleScope.stream().filter(item -> !deptForSharedAsset.contains(item)).collect(Collectors.toList());
            whetherUserOrgDispatchCenter = deptForSharedAsset.contains(userAccount.getOrgInfoUID());
        }
    }
    @Override
    public void onBeforeNewObject(AssetInfo object) {
        selectedAssetUidTable1.clear();
        selectedAssetsed1.clear();
        allSelected1 = Boolean.FALSE;
        selectedAssetUidTable2.clear();
        selectedAssetsed2.clear();
        allSelected2 = Boolean.FALSE;
        selectAsset.clear();
    }

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        addFilter();
        Page<AssetInfo> assetInfos = super.loadData(pageRequest);
        cacheDataTablePage = assetInfos.getContent();
        allSelected1 = Boolean.FALSE;
        return assetInfos;
    }

    private void addFilter(){

        List<String> scope=new ArrayList<>();
        if(CollectionUtils.isEmpty(visibleScope)){
            scope.add("-1");
        }else{
            scope.addAll(visibleScope);
        }
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,userAccount.getTenantUID()));
        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN,scope));
        searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ,false));
        searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ,true));
        searchFilters.add(new SearchFilter("isSpecialAsset", SearchFilter.Operator.NE,true));
        if(CollectionUtils.isNotEmpty(selectAsset)){
            List<String> collect = selectAsset.stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("uid", SearchFilter.Operator.NotIN,collect));
        }

    }

    @Override
    public void save() {
        try {
            if(CollectionUtils.isEmpty(selectAsset)){
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessage("请选择添加设备");
                return;
            }
            List<SpecialAssetInfo> specialAssetInfos = initializationSpecialAssetInfo(selectAsset, userAccount);
            specialAssetInfoRepository.save(specialAssetInfos);
            dao.save(selectAsset);
            for (AssetInfo assetInfo : selectAsset) {
                saveAssetHistory(assetInfo,"特种设备管理/批量设置");
            }
            WebUtil.addErrorMessage("批量设置特种设备成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private List<SpecialAssetInfo> initializationSpecialAssetInfo(List<AssetInfo> assetInfos,UserAccount loginUser){
     if (CollectionUtils.isEmpty(assetInfos)) return ListUtil.empty();
     return  assetInfos.stream().map(item->{
            item.setSpecialAsset(Boolean.TRUE);
            SpecialAssetInfo specialAssetInfo= new SpecialAssetInfo();
            specialAssetInfo.setAssetUid(item.getUid());
            specialAssetInfo.setAssetId(item.getId());
            specialAssetInfo.setCreatedByUid(loginUser.getUid());
            specialAssetInfo.setCreatedDate(new Date());
            specialAssetInfo.setTenantUID(item.getTenantUID());
            specialAssetInfo.setInstitutionUID(item.getInstitutionUID());
            specialAssetInfo.setHospitalUID(item.getHospitalUID());
            specialAssetInfo.setSiteUID(item.getSiteUID());
            return specialAssetInfo;
        }).collect(Collectors.toList());
    }
    @Override
    public void delete() {

    }
    public void saveAssetHistory(AssetInfo newAsset,String message) {
        MqContext mqContext = new MqContext();
        mqContext.setServiceName("hc-apm-web");
        mqContext.setClassName("SpecialAssetBatchController");
        mqContext.setMethodName("save");
        mqContext.setDescribe(message);
        mqContext.setModifySource(ModifySourceEnum.SPECIAL_ASSET.getCode());
        mqUtils.sendAssetToMQ(newAsset,userAccount,mqContext);
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
    }


    public void updateSelctedAssetList1(AjaxBehaviorEvent event) {
        SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
        AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
        if(box.isSelected()) {
            selectedAssetsed1.add(asset);
        } else {
            selectedAssetsed1.remove(asset);
        }
        allSelected1=cacheDataTablePage.stream().allMatch(item->selectedAssetUidTable1.get(item.getUid()));
    }
    public void updateSelctedAssetList2(AjaxBehaviorEvent event) {
        SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
        AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
        if(box.isSelected()) {
            selectedAssetsed2.add(asset);
        } else {
            selectedAssetsed2.remove(asset);
        }
        allSelected2=cacheDataTablePage2.stream().allMatch(item->selectedAssetUidTable2.get(item.getUid()));
    }
    public void addAssetsOfCurrentPage1(AjaxBehaviorEvent event) {
        log.debug("value of isAllSelected: {}", this.allSelected1);
        if(allSelected1) {
            cacheDataTablePage.stream().forEach(t -> {
                selectedAssetUidTable1.put(t.getUid(), Boolean.TRUE);
                selectedAssetsed1.add(t);
            });
        } else {
            cacheDataTablePage.stream().forEach(t -> {
                selectedAssetUidTable1.put(t.getUid(), Boolean.FALSE);
                selectedAssetsed1.remove(t);
            });
        }
        log.debug("size of selectedAssetUids: {}", selectedAssetUidTable1.size());
    }
    public void addAssetsOfCurrentPage2(AjaxBehaviorEvent event) {
        log.debug("value of allSelected2: {}", this.allSelected2);
        if(allSelected2) {
            cacheDataTablePage2.stream().forEach(t -> {
                selectedAssetUidTable2.put(t.getUid(), Boolean.TRUE);
                selectedAssetsed2.add(t);
            });
        } else {
            cacheDataTablePage2.stream().forEach(t -> {
                selectedAssetUidTable2.put(t.getUid(), Boolean.FALSE);
                selectedAssetsed2.remove(t);
            });
        }
        log.debug("size of selectedAssetsed2: {}", selectedAssetUidTable2.size());
    }
    public void addCheckedDevice(){
        for (AssetInfo assetInfo : selectedAssetsed1) {
            boolean b = selectAsset.stream().anyMatch(item -> item.getUid().equals(assetInfo.getUid()));
            if(!b){
                selectAsset.add(assetInfo);
            }
        }
        allSelected1 = Boolean.FALSE;
        selectedAssetsed1.clear();
        selectedAssetUidTable1.clear();
    }
    public void deleteCheckedDevice(){
        for (AssetInfo assetInfo : selectedAssetsed2) {
            boolean b = selectAsset.stream().anyMatch(item -> item.getUid().equals(assetInfo.getUid()));
            if(b){
                selectAsset.remove(assetInfo);
            }
        }
        allSelected2 = Boolean.FALSE;
        selectedAssetsed2.clear();
        selectedAssetUidTable2.clear();
    }
    public void clearAll(){
        selectAsset.clear();
        allSelected1 = Boolean.FALSE;
        selectedAssetsed1.clear();
        selectedAssetUidTable1.clear();
        allSelected2 = Boolean.FALSE;
        selectedAssetsed2.clear();
        selectedAssetUidTable2.clear();

    }

    public Map<String, Boolean> getSelectedAssetUidTable1() {
        return selectedAssetUidTable1;
    }

    public void setSelectedAssetUidTable1(Map<String, Boolean> selectedAssetUidTable1) {
        this.selectedAssetUidTable1 = selectedAssetUidTable1;
    }

    public Set<AssetInfo> getSelectedAssetsed1() {
        return selectedAssetsed1;
    }

    public void setSelectedAssetsed1(Set<AssetInfo> selectedAssetsed1) {
        this.selectedAssetsed1 = selectedAssetsed1;
    }

    public Boolean getAllSelected1() {
        return allSelected1;
    }

    public void setAllSelected1(Boolean allSelected1) {
        this.allSelected1 = allSelected1;
    }

    public Map<String, Boolean> getSelectedAssetUidTable2() {
        return selectedAssetUidTable2;
    }

    public void setSelectedAssetUidTable2(Map<String, Boolean> selectedAssetUidTable2) {
        this.selectedAssetUidTable2 = selectedAssetUidTable2;
    }

    public Set<AssetInfo> getSelectedAssetsed2() {
        return selectedAssetsed2;
    }

    public void setSelectedAssetsed2(Set<AssetInfo> selectedAssetsed2) {
        this.selectedAssetsed2 = selectedAssetsed2;
    }

    public Boolean getAllSelected2() {
        return allSelected2;
    }

    public void setAllSelected2(Boolean allSelected2) {
        this.allSelected2 = allSelected2;
    }

    public LazyDataModel<AssetInfo> getLazyModel2() {
        return lazyModel2;
    }

    public void setLazyModel2(LazyDataModel<AssetInfo> lazyModel2) {
        this.lazyModel2 = lazyModel2;
    }

    public List<OrgInfo> getCacheOrgList() {
        return cacheOrgList;
    }

    public void setCacheOrgList(List<OrgInfo> cacheOrgList) {
        this.cacheOrgList = cacheOrgList;
    }


}
