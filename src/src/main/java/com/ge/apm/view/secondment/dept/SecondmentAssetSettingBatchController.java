package com.ge.apm.view.secondment.dept;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.secondment.SecondmentConfig;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.cons.ModifySourceEnum;
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
public class SecondmentAssetSettingBatchController extends GenericCRUDController<AssetInfo>   {

    private static final Logger log = LoggerFactory.getLogger(SecondmentAssetSettingBatchController.class);

    private UserAccountRepository userAccountRepository;
    private SecondmentConfigRepository secondmentConfigRepository;
    private OrgInfoRepository orgInfoRepository;
    private AssetInfoRepository dao;
    private MqUtils mqUtils;

    private UserAccount userAccount;
    private List<String> visibleScope;
    private List<SecondmentConfig> cacheConfigList;
    private Map<String,I18nMessage> cacheChargingType;
    private List<UserAccount> userAccountList;
    private SecondmentConfig secondmentConfig ;
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
    private boolean chooseDept = Boolean.FALSE;
    private LazyDataModel<AssetInfo> lazyModel2;
    private WorkflowConfigRepository workflowConfigRepository;
    private boolean whetherUserOrgDispatchCenter = false;


    @Override
    protected void init() {
        dao= WebUtil.getBean(AssetInfoRepository.class);
        mqUtils= WebUtil.getBean(MqUtils.class);
        secondmentConfigRepository= WebUtil.getBean(SecondmentConfigRepository.class);
        userAccountRepository= WebUtil.getBean(UserAccountRepository.class);
        orgInfoRepository= WebUtil.getBean(OrgInfoRepository.class);
        workflowConfigRepository= WebUtil.getBean(WorkflowConfigRepository.class);
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        initialization();
        initChooseDept();
        initLazyModel2();

    }
    private  void initChooseDept(){
        String orgInfoUID = userAccount.getOrgInfoUID();
        String site = userAccount.getSiteUID();
        chooseDept =orgInfoUID.equals(site);
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
        cacheConfigList = secondmentConfigRepository.findBySearchFilter(searchFilters);
        searchFilters.clear();
        List<I18nMessage> chargingType = FieldValueMessageController.getFieldValueList("chargingType");
        cacheChargingType = chargingType.stream().collect(Collectors.toMap(I18nMessage::getMsgKey, item -> item));
        userAccountList = userAccountRepository.getByTenantUID(userAccount.getTenantUID());
        selectedAssetUidTable1= new HashMap<>();
        selectedAssetsed1 = new HashSet<>();
        allSelected1 = Boolean.FALSE;
        selectedAssetUidTable2= new HashMap<>();
        selectedAssetsed2 = new HashSet<>();
        allSelected2 = Boolean.FALSE;

        selectAsset = new ArrayList<>();
        secondmentConfig = new SecondmentConfig();
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
        secondmentConfig = new SecondmentConfig();

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
        if(chooseDept){
            if(CollectionUtils.isEmpty(visibleScope)){
                scope.add("-1");
            }else{
                scope.addAll(visibleScope);
            }
        }else{
            if(whetherUserOrgDispatchCenter){  // 当前用户所属科室时调剂中心科室
                scope.add("-1");
            }else{
                scope.add(userAccount.getOrgInfoUID());
            }

        }

        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,userAccount.getTenantUID()));
        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN,scope));
        searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ,false));
        searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ,true));
        searchFilters.add(new SearchFilter("borrowConfigId", SearchFilter.Operator.IsNull,null));
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
            for (AssetInfo assetInfo : selectAsset) {
                assetInfo.setBorrowConfigId("-1");
            }
            dao.save(selectAsset);
            for (AssetInfo assetInfo : selectAsset) {
                saveAssetHistory(assetInfo,"科室间借调/修改配置");
            }
            WebUtil.addErrorMessage("添加科室调剂设备成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void  changeStatus(AssetInfo assetInfo, Integer status){
        try {
            // TODO 校验是否有再借未完成工单
            assetInfo.setBorrowedStatus(status);
            dao.save(assetInfo);
            WebUtil.addSuccessMessage(WebUtil.getMessage("Updated"));
            saveAssetHistory(assetInfo,"科室间借调/借用状态变更");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void delete() {
        try {
            // TODO 校验是否有再借未完成工单
            selected.setBorrowConfigId(null);
            super.save();
            WebUtil.addSuccessMessage(WebUtil.getMessage("Deleted"));
            saveAssetHistory(selected,"科室间借调/解绑配置");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveAssetHistory(AssetInfo newAsset,String message) {
        MqContext mqContext = new MqContext();
        mqContext.setServiceName("hc-apm-web");
        mqContext.setClassName("SecondmentAssetSettingController");
        mqContext.setMethodName("changeStatus");
        mqContext.setDescribe(message);
        mqContext.setModifySource(ModifySourceEnum.DEPT_SECONDMENT.getCode());
        mqUtils.sendAssetToMQ(newAsset,userAccount,mqContext);
    }

    public List<SecondmentConfig> getCacheConfigList() {
        return cacheConfigList;
    }



    public String getBillingModel(AssetInfo assetInfo){
        String billingModel = "";
        List<SecondmentConfig> cacheConfigList = getCacheConfigList();
        String borrowConfigId = assetInfo.getBorrowConfigId();
        if(CollectionUtils.isEmpty(cacheConfigList) || StringUtils.isEmpty(borrowConfigId)) return billingModel;
        Optional<SecondmentConfig> first = cacheConfigList.stream().filter(item -> borrowConfigId.equals(item.getId())).findFirst();
        if (first.isPresent()) {
            SecondmentConfig secondmentConfig = first.get();
            Integer billingType = secondmentConfig.getBillingType();
            I18nMessage i18nMessage = cacheChargingType.get(billingType.toString());
            billingModel =secondmentConfig.getName() + "("+i18nMessage.getValue()+")";
        }
        return billingModel;
    }

    public SecondmentConfig getSecondmentConfig() {
        return secondmentConfig;
    }

    public void setSecondmentConfig(SecondmentConfig secondmentConfig) {
        this.secondmentConfig = secondmentConfig;
    }

    public String getCreatedBy(SecondmentConfig config){
        String createdBy = config.getCreatedBy();
        String name="";
        if(StringUtils.isEmpty(createdBy)) return name;
        for (UserAccount account : userAccountList) {
            if (account.getUid().equals(createdBy)) {
                name = account.getName();
                break;
            }
        }
        return name;
    }



    @Override
    public void prepareEdit() {
        super.prepareEdit();
        refreshSecondmentConfig();
    }


    public void refreshSecondmentConfig(){
        String borrowConfigId = selected.getBorrowConfigId();
        if(StringUtils.isEmpty(borrowConfigId)){
            secondmentConfig = new SecondmentConfig();
            return;
        }
        for (SecondmentConfig config : cacheConfigList) {
            if(config.getId().equals(borrowConfigId)){
                secondmentConfig = config;
                break;
            }
        }
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
