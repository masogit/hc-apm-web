package com.ge.apm.view.secondment.dept;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.SecondmentOrderDto;
import com.ge.apm.domain.secondment.SecondmentConfig;
import com.ge.apm.domain.secondment.SecondmentOrder;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.secondment.SecondmentOrderManageService;
import com.ge.apm.service.sharedasset.SharedAssetService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.cons.ModifySourceEnum;
import org.apache.commons.beanutils.ConvertUtils;
import org.primefaces.context.RequestContext;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SecondmentOrderManageController extends GenericCRUDUUIDController<SecondmentOrderDto> {

    private UserAccountRepository userAccountRepository;
    private OrgInfoRepository orgInfoRepository;
    private SecondmentOrderManageService service;
    private AssetInfoRepository assetInfoRepository;
    private SecondmentConfigRepository secondmentConfigRepository;
    private SecondmentOrderRepository secondmentOrderRepository;
    private Map<String, I18nMessage> cacheChargingType;

    private List<OrgInfo> cacheOrgList;
    private UserAccount userAccount;
    private List<UserAccount> userAccountList;
    private List<SecondmentConfig> cacheConfigList;
    private SecondmentConfig secondmentConfig;
    private Boolean borrowFlag=Boolean.TRUE;
    private boolean chooseDept = Boolean.FALSE;
    private List<String> visibleScope;
    private List<Field> fields = new ArrayList<>();
    private SecondmentOrderDto secondmentOrderDtoTemp;
    private SharedAssetService sharedAssetService;
    private MqUtils mqUtils;
    private List<SecondmentOrder.ProcessRecords> processRecords;
    private AssetInfo assetInfo;
    private V2_WorkOrderRepository v2_workOrderRepository;
    private List<UserAccount> emergencyContactList = new ArrayList<>();

    @Override
    protected GenericRepositoryUUID<SecondmentOrderDto> getDAO() {
        return null;
    }

    @Override
    protected void init() {
        service = WebUtil.getBean(SecondmentOrderManageService.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        secondmentConfigRepository = WebUtil.getBean(SecondmentConfigRepository.class);
        secondmentOrderRepository = WebUtil.getBean(SecondmentOrderRepository.class);
        sharedAssetService = WebUtil.getBean(SharedAssetService.class);
        v2_workOrderRepository = WebUtil.getBean(V2_WorkOrderRepository.class);
        mqUtils = WebUtil.getBean(MqUtils.class);

        List<I18nMessage> chargingType = FieldValueMessageController.getFieldValueList("chargingType");
        cacheChargingType = chargingType.stream().collect(Collectors.toMap(I18nMessage::getMsgKey, item -> item));
        if(searchFilters == null){
            searchFilters = new ArrayList<>();
        }
        Class<SecondmentOrderDto> secondmentOrderDtoClass = SecondmentOrderDto.class;
        Field[] fieldArr = secondmentOrderDtoClass.getDeclaredFields();
        Field[] superFields = secondmentOrderDtoClass.getSuperclass().getDeclaredFields();
        fields.addAll(Arrays.asList(fieldArr));
        fields.addAll(Arrays.asList(superFields));
        initialization();
    }
    private void initialization(){
        userAccount = UserContextService.getCurrentUserAccount();
        visibleScope = orgInfoRepository.getUserDeptScope(userAccount.getUid());
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ,userAccount.getSiteUID()));
        cacheConfigList = secondmentConfigRepository.findBySearchFilter(searchFilters);
        searchFilters.clear();
        List<I18nMessage> chargingType = FieldValueMessageController.getFieldValueList("chargingType");
        cacheChargingType = chargingType.stream().collect(Collectors.toMap(I18nMessage::getMsgKey, item -> item));
        userAccountList = userAccountRepository.getByTenantUID(userAccount.getTenantUID());
        cacheOrgList = orgInfoRepository.getHospitalChildrenByUids(Arrays.asList(userAccount.getSiteUID()));
        initChooseDept();
    }
    private  void initChooseDept(){
        String orgInfoUID = userAccount.getOrgInfoUID();
        String site = userAccount.getSiteUID();
        chooseDept =orgInfoUID.equals(site);
    }
    @Override
    protected Page<SecondmentOrderDto> loadData(PageRequest pageRequest) {
        initDeptScope();
        Wrapper<SecondmentOrderDto> entityWrapper = buildDefaultFilters(searchFilters);
        return service.loadSecondmentOrderList(pageRequest, entityWrapper);
    }
    private void initDeptScope(){
        List<String> scope = new ArrayList<>();
        if(chooseDept){
            scope = visibleScope;
        }else{
            scope.add(userAccount.getOrgInfoUID());
        }
        if(borrowFlag){
            searchFilters.add(new SearchFilter("lend_dept_uid", SearchFilter.Operator.IN,scope));
        }else{
            searchFilters.add(new SearchFilter("borrow_dept_uid", SearchFilter.Operator.IN,scope));
        }
    }


    @Override
    public void prepareEdit() {
        super.prepareEdit();
        secondmentOrderDtoTemp = this.selected;
        serializationConfiguration();
        serializationProcessRecords();
        serializationAssetInfo();
    }
    public void serializationAssetInfo(){
        assetInfo = assetInfoRepository.findByUid(selected.getAssetUid());
    }
    private void serializationProcessRecords(){
        processRecords = new ArrayList<>();
        String processRecordsStr = selected.getProcessRecords();
        if(StringUtils.hasText(processRecordsStr)){
            processRecords = JSON.parseObject(processRecordsStr,new TypeReference<List<SecondmentOrder.ProcessRecords>>(){});
        }
    }
    private void serializationConfiguration() {
        secondmentConfig = new SecondmentConfig();
        String configSnapshot = selected.getConfigSnapshot();
        if(Objects.equals(selected.getStatus(), 2)){
            calculateTimeCharges(selected);
        }
        if(StringUtils.hasText(configSnapshot)){
            secondmentConfig = JSON.parseObject(configSnapshot, SecondmentConfig.class);
        }
        emergencyContactList = userAccountList.stream().filter(item-> item.getOrgInfoUID().equals(selected.getLendDeptUid())).distinct().collect(Collectors.toList());
    }

    public void statusUpdate(SecondmentOrderDto dto,Integer status){
        if(dto == null){
            dto = selected;
        }
        doUpdate(dto,status);
    }
    private void doUpdate(SecondmentOrderDto dto,Integer status){
        // -2已拒绝 -1已取消  0 待同意 ，1 待交付， 2待归还， 3已归还 4、维修中
        SecondmentOrder order=new SecondmentOrder();
        dto.setStatus(status);
        dto.setModifyBy(userAccount.getUid());
        dto.setModifyDate(new Date());
        String assetUid = dto.getAssetUid();
        String borrowDeptUid = dto.getBorrowDeptUid();
        AssetInfo assetInfo = assetInfoRepository.findByUid(assetUid);
         switch (status){
             case 1:
                 Integer borrowedStatus = assetInfo.getBorrowedStatus();
                 assetInfo.setAssetDeptUID(borrowDeptUid);
                 assetInfo.setBorrowedStatus(1);
                 dto.setBillingTime(null);
                 dto.setCost(null);
                 BeanUtils.copyProperties(dto,order);
                 SecondmentOrderAssetController.generateProcessRecords(order,userAccount.getUid(),userAccount.getName(),"同意",order.getApprovalSuggestions(),order.getApprovalRemarks());
                 secondmentOrderRepository.save(order);
                 assetInfoRepository.save(assetInfo);
                 saveAssetHistory(assetInfo,"科室调剂更新设备使用科室");
                 sharedAssetService.asynRemoteCall(GatewayURL.SECONDMENT_PUSH_WECHAT_MSG,HttpMethod.POST,new HashMap<>(),(url)->url.replace("{orderId}",order.getId()).replace("{type}",order.getStatus().toString()));
                 rejectSameDeviceWorkOrder(order);
                 break;
             case 3:
                     dto.setBillingEndTime(new Date());
                     calculateTimeCharges(dto);
                     assetInfo.setAssetDeptUID(null);
                     List<V2_WorkOrder> workOrders = searchRepairAgain(assetUid);
                     if(CollectionUtils.isEmpty(workOrders)){
                         assetInfo.setBorrowedStatus(0);
                     }else{
                         assetInfo.setBorrowedStatus(-1);
                     }
                     Long billingTime = assetInfo.getBillingTime();
                     if(billingTime == null){
                         assetInfo.setBillingTime(dto.getBillingTime());
                     }else{
                         assetInfo.setBillingTime(billingTime + dto.getBillingTime());
                     }
                     Double billingCost = assetInfo.getBillingCost();
                     if(billingCost == null){
                         assetInfo.setBillingCost(dto.getCost());
                     }else{
                         assetInfo.setBillingCost(billingCost+dto.getCost());
                     }
                     BeanUtils.copyProperties(dto,order);
                     SecondmentOrderAssetController.generateProcessRecords(order,userAccount.getUid(),userAccount.getName(),"归还",null,null);
                     secondmentOrderRepository.save(order);
                     sharedAssetService.asynRemoteCall(GatewayURL.SECONDMENT_PUSH_WECHAT_MSG,HttpMethod.POST,new HashMap<>(),(url)->url.replace("{orderId}",order.getId()).replace("{type}",order.getStatus().toString()));
                     assetInfoRepository.save(assetInfo);
                     saveAssetHistory(assetInfo,"科室调剂更新设备使用科室");
                 break;
             case 5:
                 // 提醒归还
                 dto.setBillingTime(null);
                 dto.setCost(null);
                 sharedAssetService.asynRemoteCall(GatewayURL.SECONDMENT_REMINDER_TO_RETURN, HttpMethod.POST,new HashMap<>(),(url)-> url.replace("{orderId}",dto.getId()));
                 WebUtil.addSuccessMessage("设备："+assetInfo.getName() +"，已发送归还通知！");
                 break;
             case 4:
                 dto.setBillingTime(null);
                 dto.setCost(null);
                 dto.setIsRepair(true);
                 BeanUtils.copyProperties(dto,order);
                 SecondmentOrderAssetController.generateProcessRecords(order,userAccount.getUid(),userAccount.getName(),"维修中",null,null);
                 secondmentOrderRepository.save(order);
                 break;
             case 2:
                 dto.setBillingBeginTime(new Date());
                 dto.setBillingTime(null);
                 dto.setCost(null);
                 BeanUtils.copyProperties(dto,order);
                 SecondmentOrderAssetController.generateProcessRecords(order,userAccount.getUid(),userAccount.getName(),"领用",null,null);
                 secondmentOrderRepository.save(order);
                 sharedAssetService.asynRemoteCall(GatewayURL.SECONDMENT_PUSH_WECHAT_MSG,HttpMethod.POST,new HashMap<>(),(url)->url.replace("{orderId}",order.getId()).replace("{type}",order.getStatus().toString()));
                 break;
             case -2: // 已拒绝
                 dto.setBillingTime(null);
                 dto.setCost(null);
                 BeanUtils.copyProperties(dto,order);
                 SecondmentOrderAssetController.generateProcessRecords(order,userAccount.getUid(),userAccount.getName(),"拒绝",order.getDenialReason(),order.getApprovalRemarks());
                 secondmentOrderRepository.save(order);
                 sharedAssetService.asynRemoteCall(GatewayURL.SECONDMENT_PUSH_WECHAT_MSG,HttpMethod.POST,new HashMap<>(),(url)->url.replace("{orderId}",order.getId()).replace("{type}",order.getStatus().toString()));
                 break;
             case -1: //已取消
                 BeanUtils.copyProperties(dto,order);
                 SecondmentOrderAssetController.generateProcessRecords(order,userAccount.getUid(),userAccount.getName(),"取消",order.getDenialReason(),order.getApprovalRemarks());
                 secondmentOrderRepository.save(order);
                 updateDeviceStatus(assetInfo);
                 break;
             case 0: //申请
                 BeanUtils.copyProperties(dto,order);
                 SecondmentOrderAssetController.generateProcessRecords(order,userAccount.getUid(),userAccount.getName(),"申请",null,null);
                 secondmentOrderRepository.save(order);
                 sharedAssetService.asynRemoteCall(GatewayURL.SECONDMENT_PUSH_WECHAT_MSG,HttpMethod.POST,new HashMap<>(),(url)->url.replace("{orderId}",order.getId()).replace("{type}",order.getStatus().toString()));
                 break;
             default:
                 break;
         }

    }
    private List<V2_WorkOrder> searchRepairAgain(String uid){
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("assetUid", SearchFilter.Operator.EQ, uid));
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
        List<V2_WorkOrder> workOrders = v2_workOrderRepository.findBySearchFilter(searchFilters);
        return workOrders;
    }
    private void updateDeviceStatus(AssetInfo assetInfo){
        boolean flag=false;
        String clinicalDeptUID = assetInfo.getClinicalDeptUID();
        String assetDeptUID = assetInfo.getAssetDeptUID();
        if(StringUtils.hasText(assetDeptUID) && !clinicalDeptUID.equals(assetDeptUID)){
            assetInfo.setAssetDeptUID(clinicalDeptUID);
            flag=true;
        }
        Integer borrowedStatusCurrent = assetInfo.getBorrowedStatus();
        if(borrowedStatusCurrent != null && Objects.equals(borrowedStatusCurrent, 1)){
            assetInfo.setBorrowedStatus(0);
            flag=true;
        }
        if(flag){
            assetInfoRepository.save(assetInfo);
            saveAssetHistory(assetInfo,"科室调剂取消借用更新设备使用科室");
        }

    }
    public void rejectSameDeviceWorkOrder(SecondmentOrder order){
        String assetUid = order.getAssetUid();
        List<SearchFilter> searchFilters=new ArrayList<>();
        searchFilters.add(new SearchFilter("assetUid", SearchFilter.Operator.EQ,assetUid));
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ,0));
        List<SecondmentOrder> rejectWorkOrder = secondmentOrderRepository.findBySearchFilter(searchFilters);
        if(CollectionUtils.isEmpty(rejectWorkOrder)) return;
        for (SecondmentOrder secondmentOrder : rejectWorkOrder) {
             secondmentOrder.setDenialReason("该设备其他科室已占用"); // 拒绝原因
             SecondmentOrderAssetController.generateProcessRecords(secondmentOrder,userAccount.getUid(),userAccount.getName(),"拒绝",secondmentOrder.getDenialReason(),null);
             secondmentOrder.setModifyBy(userAccount.getUid());
             secondmentOrder.setModifyDate(new Date());
             secondmentOrder.setStatus(-2);
            secondmentOrderRepository.save(rejectWorkOrder);
            sharedAssetService.asynRemoteCall(GatewayURL.SECONDMENT_PUSH_WECHAT_MSG,HttpMethod.POST,new HashMap<>(),(url)->url.replace("{orderId}",order.getId()).replace("{type}",order.getStatus().toString()));
        }
    }
    public SecondmentConfig getSecondmentConfig() {
        return secondmentConfig;
    }

    public void setSecondmentConfig(SecondmentConfig secondmentConfig) {
        this.secondmentConfig = secondmentConfig;
    }

    public List<SecondmentConfig> getCacheConfigList() {
        return cacheConfigList;
    }

    public void setCacheConfigList(List<SecondmentConfig> cacheConfigList) {
        this.cacheConfigList = cacheConfigList;
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

    public SecondmentOrderDto calculateTimeCharges(SecondmentOrderDto order){
        LocalDateTime beginLocalDateTime = order.getBillingBeginTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Date billingEndTime = order.getBillingEndTime();
        if(billingEndTime == null){
            billingEndTime = new Date();
        }
        LocalDateTime endLocalDateTime = billingEndTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Integer billingType = order.getBillingType();
        Double unitPrice = order.getUnitPrice();
        long minutes = ChronoUnit.MINUTES.between(beginLocalDateTime, endLocalDateTime);
        order.setBillingTime(minutes);
        switch (billingType){
            case 1:  // 次
                order.setCost(unitPrice);
                break;
            case 2:  //小时(半点)
                long halfHour = minutes / 30;
                double cost = halfHour * (unitPrice / 2);
                if((minutes % 30) > 0){
                    cost += unitPrice / 2;
                }
                order.setCost(cost);
                break;
            case 3:  //天
                long days = minutes / 1440;
                if((minutes % 1440) > 0){
                    days+=1;
                }
                order.setCost(days * unitPrice);
                break;
            case 4:  // 自然时间
                long hour = minutes / 60;
                long remainder = minutes % 60;
                order.setCost(hour * unitPrice + remainder * (unitPrice / 60));
                break;
            case 5:   //小时(整点)
                long hours = minutes / 60;
                if((minutes % 60) > 0){
                    hours+=1;
                }
                order.setCost(hours * unitPrice);
                break;
            default:
                break;
        }
        return order;
    }
    public SecondmentOrderDto adDocCalculation(SecondmentOrderDto dto){
        if(dto.getBillingBeginTime() != null && dto.getBillingTime() == null){
            return calculateTimeCharges(dto);
        }
        return dto;
    }

    public Wrapper<SecondmentOrderDto> buildDefaultFilters(List<SearchFilter> searchFilters) {
        Wrapper<SecondmentOrderDto> ew = new EntityWrapper<SecondmentOrderDto>();
        for (SearchFilter filter : searchFilters) {
            switch(filter.operator){
                case EQ:
                    ew.eq(filter.fieldName, getFieldValue(filter));
                    break;
                case LIKE:
                    if(filter.value!=null){
                        ew.like("lower("+filter.fieldName+")", filter.value.toString().toLowerCase());
                    }
                    break;
                case LT :
                    ew.lt(filter.fieldName, getFieldValue(filter));
                    break;
                case LTE:
                    ew.le(filter.fieldName, getFieldValue(filter));
                    break;
                case GT:
                    ew.gt(filter.fieldName, getFieldValue(filter));
                    break;
                case GTE:
                    ew.ge(filter.fieldName, getFieldValue(filter));
                    break;
                case IsNull:
                    ew.isNull(filter.fieldName);
                    break;
                case IsNotNull:
                    ew.isNotNull(filter.fieldName);
                    break;
                case IN:
                    ew.in(filter.fieldName, (Collection) filter.value);
                default:
                    break;
            }
        }
        return ew;
    }
    public void switchQuery(boolean borrowFlag){
        setBorrowFlag(borrowFlag);
        this.selected = null;
        searchFilters = new ArrayList<>();
    }
    public Boolean getBorrowFlag() {
        return borrowFlag;
    }

    public void setBorrowFlag(Boolean borrowFlag) {
        this.borrowFlag = borrowFlag;
    }

    private Object getFieldValue(SearchFilter filter){
        String fieldName = filter.fieldName;
        Object value = filter.value;
        if(fieldName.contains(".")){
            fieldName = fieldName.substring(fieldName.lastIndexOf(".")+1);
        }
        fieldName= fieldName.replaceAll("_", "").toLowerCase();
        for (Field field : fields) {
            String name = field.getName().toLowerCase();
            if (name.equals(fieldName)) {
                value = ConvertUtils.convert(value, field.getType());
                break;
            }
        }
        return value;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        SecondmentOrderDto  selectAssetOrder= (SecondmentOrderDto) eventObject;
        selectAssetOrder.setBorrowDeptUid(secondmentOrderDtoTemp.getBorrowDeptUid());
        this.selected = selectAssetOrder;
//        serializationConfiguration();
//        if(!secondmentOrderDtoTemp.getAssetUid().equals(selectAssetOrder.getAssetUid())){
//            this.selected = selectAssetOrder;
//            serializationConfiguration();
//        }else{
//            this.selected = this.secondmentOrderDtoTemp;
//            WebUtil.addErrorMessage("设备："+selected.getAssetName() +"，申请已拒绝！");
//        }
    }

    public boolean resubmit() {
        return secondmentOrderDtoTemp.getSecondmentNo().equals(selected.getSecondmentNo()) && !secondmentOrderDtoTemp.getAssetUid().equals(selected.getAssetUid());
    }

    public void saveAssetHistory(AssetInfo newAsset,String message) {
        MqContext mqContext = new MqContext();
        mqContext.setServiceName("hc-apm-web");
        mqContext.setClassName("SecondmentAssetSettingController");
        mqContext.setMethodName("statusUpdate");
        mqContext.setDescribe(message);
        mqContext.setModifySource(ModifySourceEnum.DEPT_SECONDMENT.getCode());
        mqUtils.sendAssetToMQ(newAsset,userAccount,mqContext);
    }

    public SecondmentOrderDto getSecondmentOrderDtoTemp() {
        return secondmentOrderDtoTemp;
    }

    public void setSecondmentOrderDtoTemp(SecondmentOrderDto secondmentOrderDtoTemp) {
        this.secondmentOrderDtoTemp = secondmentOrderDtoTemp;
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

    public List<SecondmentOrder.ProcessRecords> getProcessRecords() {
        return processRecords;
    }

    public void setProcessRecords(List<SecondmentOrder.ProcessRecords> processRecords) {
        this.processRecords = processRecords;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public List<UserAccount> getEmergencyContactList() {
        return emergencyContactList;
    }

    public void setEmergencyContactList(List<UserAccount> emergencyContactList) {
        this.emergencyContactList = emergencyContactList;
    }

    public void updateEmergencyContact(){
        String emergencyContactUid = selected.getEmergencyContactUid();
        if(StringUtils.hasText(emergencyContactUid)){
            Optional<UserAccount> first = emergencyContactList.stream().filter(item -> emergencyContactUid.equals(item.getUid())).findFirst();
            if (first.isPresent()) {
                UserAccount userAccount = first.get();
                selected.setEmergencyContactName(userAccount.getName());
                selected.setEmergencyContactTel(userAccount.getTelephone());
            }
        }else{
            selected.setEmergencyContactUid(null);
            selected.setEmergencyContactName(null);
            selected.setEmergencyContactTel(null);
        }
    }

    public void checkData(){
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
        SecondmentOrderDto selected = this.selected;
        String emergencyContactUid = selected.getEmergencyContactUid();
        String emergencyContactTel= selected.getEmergencyContactTel();
        if(StringUtils.isEmpty(emergencyContactUid)){
            WebUtil.addErrorMessage("设备紧急联系人，输入不可为空");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
        }
        if(StringUtils.isEmpty(emergencyContactTel)){
            WebUtil.addErrorMessage("紧急联系人电话，输入不可为空");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
        }
    }
}
