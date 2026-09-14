package com.ge.apm.view.secondment.dept;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.SecondmentOrderDto;
import com.ge.apm.domain.secondment.SecondmentConfig;
import com.ge.apm.domain.secondment.SecondmentOrder;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.sharedasset.SharedAssetService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.cons.ModifySourceEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SecondmentOrderAssetController extends GenericCRUDController<AssetInfo> {

    private UserAccountRepository userAccountRepository;
    private SecondmentConfigRepository secondmentConfigRepository;
    private SecondmentOrderRepository secondmentOrderRepository;
    private OrgInfoRepository orgInfoRepository;
    private AssetInfoRepository dao;
    private MqUtils mqUtils;

    private UserAccount userAccount;
    private List<String> visibleScope;
    private List<SecondmentConfig> cacheConfigList;
    private Map<String, I18nMessage> cacheChargingType;
    private List<UserAccount> userAccountList;
    private SecondmentConfig secondmentConfig = new SecondmentConfig();
    private SecondmentOrder secondmentOrder;
    private boolean chooseDept = Boolean.FALSE;
    private List<OrgInfo> cacheOrgList;
    private ServerEventInterface callingController;
    private String updateViewIDs;
    private SharedAssetService sharedAssetService;
    private List<OrgInfo> orgInfoList;
    private List<AssetInfo> selectAssetInfos;
    private V2_WorkOrderRepository v2_workOrderRepository;
    private WorkflowConfigRepository workflowConfigRepository;
    @Override
    protected void init() {
        dao= WebUtil.getBean(AssetInfoRepository.class);
        mqUtils= WebUtil.getBean(MqUtils.class);
        secondmentConfigRepository= WebUtil.getBean(SecondmentConfigRepository.class);
        secondmentOrderRepository= WebUtil.getBean(SecondmentOrderRepository.class);
        userAccountRepository= WebUtil.getBean(UserAccountRepository.class);
        orgInfoRepository= WebUtil.getBean(OrgInfoRepository.class);
        sharedAssetService= WebUtil.getBean(SharedAssetService.class);
        v2_workOrderRepository= WebUtil.getBean(V2_WorkOrderRepository.class);
        workflowConfigRepository= WebUtil.getBean(WorkflowConfigRepository.class);
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        initialization();

    }

    private void initialization(){
        userAccount = UserContextService.getCurrentUserAccount();
        visibleScope = orgInfoRepository.getUserDeptScope(userAccount.getUid());
        String siteUID = userAccount.getSiteUID();
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, siteUID,"1*2+"));
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ,siteUID,"1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ,siteUID,"1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ,siteUID,"1*2+"));
        cacheConfigList = secondmentConfigRepository.findBySearchFilter(searchFilters);
        searchFilters.clear();
        List<I18nMessage> chargingType = FieldValueMessageController.getFieldValueList("chargingType");
        cacheChargingType = chargingType.stream().collect(Collectors.toMap(I18nMessage::getMsgKey, item -> item));
        userAccountList = userAccountRepository.getByTenantUID(userAccount.getTenantUID());
        cacheOrgList = orgInfoRepository.getHospitalChildrenByUids(Arrays.asList(userAccount.getSiteUID()));
        if(CollectionUtils.isNotEmpty(cacheOrgList)){
            filterAdjustmentCenter();
        }

        initChooseDept();
    }
    private  void initChooseDept(){
        String orgInfoUID = userAccount.getOrgInfoUID();
        String site = userAccount.getSiteUID();
        chooseDept =orgInfoUID.equals(site);
    }
    private void filterAdjustmentCenter(){
        String tenantUID = userAccount.getTenantUID();
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,tenantUID));
        searchFilters.add(new SearchFilter("deptForSharedAsset", SearchFilter.Operator.IsNotNull,tenantUID));
        List<WorkflowConfig> workflowConfigs = workflowConfigRepository.findBySearchFilter(searchFilters);
        if(CollectionUtils.isNotEmpty(workflowConfigs)){
            List<String> deptForSharedAsset = workflowConfigs.stream().map(WorkflowConfig::getDeptForSharedAsset).collect(Collectors.toList());
            cacheOrgList = cacheOrgList.stream().filter(item -> !deptForSharedAsset.contains(item.getUid())).collect(Collectors.toList());
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
        generateVirtualSerialNumber(assetInfos);
        return assetInfos;
    }
    private void generateVirtualSerialNumber(Page<AssetInfo> assetInfos){
        int number = assetInfos.getNumber();
        int size = assetInfos.getSize();
        List<AssetInfo> content = assetInfos.getContent();
        if(CollectionUtils.isEmpty(content)) return;
        int num =number * size;
        for (int i = 0; i < content.size(); i++) {
            AssetInfo assetInfo = content.get(i);
            assetInfo.setSharedAssetSwapIndexId(String.valueOf((num + i + 1)));
        }
    }
    private void addFilter(){
        String orgInfoUID = userAccount.getOrgInfoUID();
        List<String> scope = cacheOrgList.stream().filter(item -> !item.getUid().equals(orgInfoUID) && item.getOrgType() == 4).map(item -> item.getUid()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(scope)){
            scope.add("-1");
        }
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,userAccount.getTenantUID()));
        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN,scope));
        searchFilters.add(new SearchFilter("borrowConfigId", SearchFilter.Operator.IsNotNull,null));
        searchFilters.add(new SearchFilter("borrowedStatus", SearchFilter.Operator.EQ,0));
        searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ,false));
        searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ,true));

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



    public String getUserName(String uid){
        String name="";
        if(StringUtils.isEmpty(uid)) return name;
        for (UserAccount account : userAccountList) {
            if (account.getUid().equals(uid)) {
                name = account.getName();
                break;
            }
        }
        return name;
    }

    @Override
    public void save(){
        try {
            List<SearchFilter> searchFilters=new ArrayList<>();
            searchFilters.add(new SearchFilter("assetUid", SearchFilter.Operator.EQ,selected.getUid()));
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.GTE,1));
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.LT,3));
            List<SecondmentOrder> secondmentOrders = secondmentOrderRepository.findBySearchFilter(searchFilters);
            if(CollectionUtils.isNotEmpty(secondmentOrders)){
                WebUtil.addErrorMessage("设备："+selected.getName() +"，占用中！");
                return ;
            }
            String clinicalDeptUID = selected.getClinicalDeptUID();
            String assetDeptUID = selected.getAssetDeptUID();
            if(StringUtils.hasText(assetDeptUID) && !assetDeptUID.equals(clinicalDeptUID)){

                WebUtil.addErrorMessage("设备："+selected.getName() +"，正在调剂中！");
                return ;
            }
            List<V2_WorkOrder> workOrders = searchRepairAgain(selected.getUid());
            if(CollectionUtils.isNotEmpty(workOrders)){
                WebUtil.addErrorMessage("设备："+selected.getName() +" 正在维修中,暂停借出！");
                return ;
            }
            SecondmentOrder secondmentOrder = this.secondmentOrder;
            if(secondmentOrder!= null){
                 generateProcessRecords(secondmentOrder,userAccount.getUid(), userAccount.getName(), "申请",secondmentOrder.getApprovalSuggestions(),secondmentOrder.getApprovalRemarks());
                SecondmentOrder order = secondmentOrderRepository.save(secondmentOrder);
                sharedAssetService.asynRemoteCall(GatewayURL.SECONDMENT_PUSH_WECHAT_MSG,HttpMethod.POST,new HashMap<>(),(url)->url.replace("{orderId}",order.getId()).replace("{type}",order.getStatus().toString()));
                WebUtil.addSuccessMessage("调剂设备："+selected.getName() +" 申请成功");
            }
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

    public static void generateProcessRecords(SecondmentOrder order,String uid,String name,String nodeName,String approvalSuggestions,String approvalRemarks){
        if(order == null) return;
        List<SecondmentOrder.ProcessRecords> processRecordsList;
        String processRecords = order.getProcessRecords();
        if(StringUtils.hasText(processRecords)){
            processRecordsList = JSON.parseObject(processRecords, new TypeReference<List<SecondmentOrder.ProcessRecords>>() {
            });
        }else{
            processRecordsList =new ArrayList<>();
        }
        processRecordsList.add(new SecondmentOrder.ProcessRecords(uid,name,order.getStatus(),nodeName,new Date(),approvalSuggestions,approvalRemarks));
        order.setProcessRecords(JSON.toJSONString(processRecordsList));
    }
    public void applyBorrowBefore(){
        crudActionName = "Create";
        refreshSecondmentConfig();
        initSecondmentOrder();
        getOrgInfo();
    }
    private void initSecondmentOrder(){
        Date now = new Date();
        AssetInfo assetInfo=this.selected;
        SecondmentConfig config = this.secondmentConfig;
        UserAccount userAccount = this.userAccount;
        secondmentOrder= new SecondmentOrder();
        String secondmentNo = generateSecondmentNo(null);
        secondmentOrder.setSecondmentNo(secondmentNo);
        secondmentOrder.setAssetUid(assetInfo.getUid());
        secondmentOrder.setConfigId(config.getId());
        secondmentOrder.setApplyTime(now);
        secondmentOrder.setApplicantUid(userAccount.getUid());
        secondmentOrder.setApplicantName(userAccount.getName());
        secondmentOrder.setApplicantTel(userAccount.getTelephone());
        secondmentOrder.setLendDeptUid(assetInfo.getClinicalDeptUID());
        secondmentOrder.setBorrowDeptUid(chooseDept?null:userAccount.getOrgInfoUID());
        secondmentOrder.setBillingType(config.getBillingType());
        secondmentOrder.setUnitPrice(config.getUnitPrice());
        secondmentOrder.setNeedSpareParts(Boolean.FALSE);
        secondmentOrder.setCreatedBy(userAccount.getUid());
        secondmentOrder.setCreatedDate(now);
        secondmentOrder.setTenantUID(assetInfo.getTenantUID());
        secondmentOrder.setInstitutionUID(assetInfo.getInstitutionUID());
        secondmentOrder.setHospitalUID(assetInfo.getHospitalUID());
        secondmentOrder.setSiteUID(assetInfo.getSiteUID());
        secondmentOrder.setStatus(0);  // -2已拒绝 -1已取消  0 待同意 ，1 待交付， 2待归还， 3已归还
        String billingStandards = config.getUnitPrice() + "/" + (cacheChargingType.get(config.getBillingType().toString()).getValue());
        secondmentOrder.setBillingStandards(billingStandards);
        String configJSON = JSON.toJSONString(config);
        secondmentOrder.setConfigSnapshot(configJSON);
    }
    private String generateSecondmentNo(String key){
        try {
            return sharedAssetService.getSrNum(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void getOrgInfo(){
        String siteUid = selected.getSiteUID();
        String filterDeptUid = selected.getClinicalDeptUID();
        orgInfoList = cacheOrgList.stream().filter(item->{
            String uid = item.getUid();
            String siteUID = item.getSiteUID();
            Integer orgType = item.getOrgType();
            return orgType != null && orgType ==4 && !uid.equals(filterDeptUid) && siteUID.equals(siteUid);
        }).collect(Collectors.toList());
    }
    public String getBorrowDeptName(String uid){
        String name ="";
        Optional<OrgInfo> first = cacheOrgList.stream().filter(item -> item.getUid().equals(uid)).findFirst();
        if(first.isPresent()){
            OrgInfo orgInfo = first.get();
            name = orgInfo.getName();
        }
        return name;
    }

    public void refreshSecondmentConfig(){
        for (SecondmentConfig config : cacheConfigList) {
            if(config.getId().equals(selected.getBorrowConfigId())){
                secondmentConfig = config;
                break;
            }
        }
    }
    public SecondmentConfig getSecondmentConfig() {
        return secondmentConfig;
    }

    public void setSecondmentConfig(SecondmentConfig secondmentConfig) {
        this.secondmentConfig = secondmentConfig;
    }

    public SecondmentOrder getSecondmentOrder() {
        return secondmentOrder;
    }

    public void setSecondmentOrder(SecondmentOrder secondmentOrder) {
        this.secondmentOrder = secondmentOrder;
    }

    public boolean getChooseDept() {
        return chooseDept;
    }

    public void setChooseDept(boolean chooseDept) {
        this.chooseDept = chooseDept;
    }



    public void prepareDialogCallback(ServerEventInterface callingController, String updateViewIDs) {
        this.selected = null;

        this.callingController = callingController;
        this.updateViewIDs = updateViewIDs;

    }

    public void confirmSelection(){
        if (callingController != null) {
            applyBorrowBefore();
            SecondmentOrderDto dto=new SecondmentOrderDto();
            BeanUtils.copyProperties(this.secondmentOrder,dto);
            dto.setAssetName(this.selected.getName());
            dto.setFunctionType(this.selected.getFunctionType());
            dto.setAssetGroup(this.selected.getAssetGroup());
            dto.setDepartNum(this.selected.getDepartNum());
            dto.setQrCode(this.selected.getQrCode());
            callingController.onServerEvent("onAssetSelected", dto);
        }
    }

    public String getUpdateViewIDs() {
        return updateViewIDs;
    }

    public void setUpdateViewIDs(String updateViewIDs) {
        this.updateViewIDs = updateViewIDs;
    }

    public List<OrgInfo> getOrgInfoList() {
        return orgInfoList;
    }

    public void setOrgInfoList(List<OrgInfo> orgInfoList) {
        this.orgInfoList = orgInfoList;
    }

    public List<AssetInfo> getSelectAssetInfos() {
        return selectAssetInfos;
    }

    public void setSelectAssetInfos(List<AssetInfo> selectAssetInfos) {
        this.selectAssetInfos = selectAssetInfos;
    }

    public static String conversionTime(Long billingTime){
        StringBuilder result=new StringBuilder();
        if(billingTime != null){
            long day = 0;
            long hour = 0;
            long minute = 0;
            if((day=billingTime / (24 * 60)) > 0){
                result.append(day).append("天");
            }
            if((hour = billingTime % (24 * 60) / 60) > 0){
                result.append(hour).append("小时");
            }
            if((minute = billingTime % 60) > 0){
                result.append(minute).append("分钟");
            }
        }else{
            result.append("");
        }
        return result.toString();
    }
}
