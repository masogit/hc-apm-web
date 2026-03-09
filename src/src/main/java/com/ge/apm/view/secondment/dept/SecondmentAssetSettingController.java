package com.ge.apm.view.secondment.dept;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.secondment.SecondmentConfig;
import com.ge.apm.domain.secondment.SecondmentOrder;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.mq.MqContext;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SecondmentAssetSettingController extends GenericCRUDController<AssetInfo>   {

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
    private boolean chooseDept = Boolean.FALSE;
    private SecondmentConfig secondmentConfig = new SecondmentConfig();
    private WorkflowConfigRepository workflowConfigRepository;
    private V2_WorkOrderRepository v2_workOrderRepository;
    private SecondmentOrderRepository secondmentOrderRepository;
    @Override
    protected void init() {
        dao= WebUtil.getBean(AssetInfoRepository.class);
        mqUtils= WebUtil.getBean(MqUtils.class);
        secondmentConfigRepository= WebUtil.getBean(SecondmentConfigRepository.class);
        userAccountRepository= WebUtil.getBean(UserAccountRepository.class);
        workflowConfigRepository= WebUtil.getBean(WorkflowConfigRepository.class);
        orgInfoRepository= WebUtil.getBean(OrgInfoRepository.class);
        v2_workOrderRepository= WebUtil.getBean(V2_WorkOrderRepository.class);
        secondmentOrderRepository= WebUtil.getBean(SecondmentOrderRepository.class);
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        initialization();
        initChooseDept();
    }
    private  void initChooseDept(){
        String orgInfoUID = userAccount.getOrgInfoUID();
        String site = userAccount.getSiteUID();
        chooseDept =orgInfoUID.equals(site);
    }
    private void initialization(){
        userAccount = UserContextService.getCurrentUserAccount();
        visibleScope = orgInfoRepository.getUserDeptScope(userAccount.getUid());
        if(CollectionUtils.isNotEmpty(visibleScope)){
            filterAdjustmentCenter();
        }
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,userAccount.getSiteUID(),"1*2+"));
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ,userAccount.getSiteUID(),"1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ,userAccount.getSiteUID(),"1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ,userAccount.getSiteUID(),"1*2+"));
        cacheConfigList = secondmentConfigRepository.findBySearchFilter(searchFilters);
        searchFilters.clear();
        List<I18nMessage> chargingType = FieldValueMessageController.getFieldValueList("chargingType");
        cacheChargingType = chargingType.stream().collect(Collectors.toMap(I18nMessage::getMsgKey, item -> item));
        userAccountList = userAccountRepository.getByTenantUID(userAccount.getTenantUID());
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
        }
    }
    @Override
    public void onBeforeNewObject(AssetInfo object) {

    }

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        addFilter();
        Page<AssetInfo> assetInfos = super.loadData(pageRequest);
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
            scope.add(userAccount.getOrgInfoUID());
        }
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,userAccount.getTenantUID()));
        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN,scope));
        searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ,false));
        searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ,true));
        searchFilters.add(new SearchFilter("borrowConfigId", SearchFilter.Operator.IsNotNull,null));


    }

    @Override
    public void save() {
        try {
            // TODO 校验是否有维修工单
            String borrowConfigId = selected.getBorrowConfigId();
            if(StringUtils.isEmpty(borrowConfigId)){
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessage("计费配置不允许为空");
                return;
            }
            List<V2_WorkOrder> workOrders = searchRepairAgain(selected.getUid());
            if(CollectionUtils.isNotEmpty(workOrders)){
                selected.setBorrowedStatus(-1);
            }else{
                selected.setBorrowedStatus(0);
            }
            super.save();
            saveAssetHistory(selected,"科室间借调/修改配置");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private List<V2_WorkOrder> searchRepairAgain(String uid){
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("assetUid", SearchFilter.Operator.EQ, uid));
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
        List<V2_WorkOrder> workOrders = v2_workOrderRepository.findBySearchFilter(searchFilters);
        return workOrders;
    }
    public void  changeStatus(AssetInfo assetInfo, Integer status){
        try {
            Integer tempStatus = null;
            String uid = assetInfo.getUid();
            String name = assetInfo.getName();
            List<V2_WorkOrder> workOrders = searchRepairAgain(uid);
            if(CollectionUtils.isNotEmpty(workOrders)){
                tempStatus = -1; // 暂停借出
                WebUtil.addSuccessMessage("设备： "+ name + "有未完成维修工单,调剂状态更改为暂停借出!");
            }else{
                List<SearchFilter> searchFilters = new ArrayList<>();
                searchFilters.add(new SearchFilter("assetUid", SearchFilter.Operator.EQ,uid));
                List<SecondmentOrder> secondmentOrders = secondmentOrderRepository.findBySearchFilter(searchFilters);
                if(CollectionUtils.isNotEmpty(secondmentOrders)){
                    tempStatus = 1;       // 占用
                    WebUtil.addSuccessMessage("设备： "+ name + "有未完成调剂工单,调剂状态更改为占用!");
                }else{
                    tempStatus = status;  //  0 or -1
                    WebUtil.addSuccessMessage(WebUtil.getMessage("Updated"));
                }
            }
            assetInfo.setBorrowedStatus(tempStatus);
            dao.save(assetInfo);
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
            selected.setBorrowedStatus(null);
            dao.save(selected);
            WebUtil.addSuccessMessage("调剂设备移除成功");
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
                return;
            }
        }
        secondmentConfig = new SecondmentConfig();
    }
}
