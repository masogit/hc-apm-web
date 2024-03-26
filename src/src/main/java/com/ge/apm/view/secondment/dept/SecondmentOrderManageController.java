package com.ge.apm.view.secondment.dept;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.dao.*;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.dto.SecondmentOrderDto;
import com.ge.apm.domain.secondment.SecondmentConfig;
import com.ge.apm.domain.secondment.SecondmentOrder;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.secondment.SecondmentOrderManageService;
import com.ge.apm.service.sharedasset.SharedAssetService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
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
        searchFilters.clear();
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

    }

    private void serializationConfiguration() {
        secondmentConfig = new SecondmentConfig();
        String configSnapshot = selected.getConfigSnapshot();
        if(selected.getStatus() == 2){
            calculateTimeCharges(selected);
        }
        if(StringUtils.hasText(configSnapshot)){
            secondmentConfig = JSON.parseObject(configSnapshot, SecondmentConfig.class);
        }
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
                 if(assetInfo.getBorrowedStatus() == 0){
                     assetInfo.setAssetDeptUID(borrowDeptUid);
                     assetInfo.setBorrowedStatus(1);
                     dto.setBillingTime(null);
                     dto.setCost(null);
                     BeanUtils.copyProperties(dto,order);
                     secondmentOrderRepository.save(order);
                     assetInfoRepository.save(assetInfo);
                 }else{
                     WebUtil.addSuccessMessage("设备："+assetInfo.getName() +",已被占用或未配置调剂规则,请刷新后重试");
                 }
                 break;
             case 3:
                     dto.setBillingEndTime(new Date());
                     calculateTimeCharges(dto);
                     assetInfo.setAssetDeptUID(null);
                     assetInfo.setBorrowedStatus(0);
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
                         assetInfo.setBillingCost(billingCost + billingCost+dto.getCost());
                     }
                     BeanUtils.copyProperties(dto,order);
                     secondmentOrderRepository.save(order);
                     assetInfoRepository.save(assetInfo);
                 break;
             case 5:
                 // 提醒归还
                 sharedAssetService.asynRemoteCall(GatewayURL.SECONDMENT_REMINDER_TO_RETURN, HttpMethod.POST,new HashMap<>(),(url)-> url.replace("{orderId}",dto.getId()));
                 WebUtil.addSuccessMessage("设备："+assetInfo.getName() +"，已发送归还通知！");
                 break;
             case 4:
             case 2:
                dto.setBillingBeginTime(new Date());
             case -2: // 已拒绝
             case -1: //已取消
             default:
                 dto.setBillingTime(null);
                 dto.setCost(null);
                 BeanUtils.copyProperties(dto,order);
                 secondmentOrderRepository.save(order);
                 break;
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

    private void calculateTimeCharges(SecondmentOrderDto order){
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
                Double halfHour = minutes / 30.0;
                order.setCost(halfHour * unitPrice);
                break;
            case 3:  //天
                long days = ChronoUnit.DAYS.between(beginLocalDateTime, endLocalDateTime);
                order.setCost(days * unitPrice);
                break;
            case 4:  // 自然时间
                order.setCost(minutes * unitPrice);
                break;
            case 5:   //小时(整点)
                long hours = ChronoUnit.HOURS.between(beginLocalDateTime, endLocalDateTime);
                order.setCost(hours * unitPrice);
                break;
            default:
                break;
        }

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
        if(!secondmentOrderDtoTemp.getAssetUid().equals(selectAssetOrder.getAssetUid())){
            selectAssetOrder.setId(this.selected.getId());
            selectAssetOrder.setSecondmentNo(this.selected.getSecondmentNo());
            selectAssetOrder.setStatus(this.selected.getStatus());
            this.selected = selectAssetOrder;
            serializationConfiguration();
        }else{
            this.selected = this.secondmentOrderDtoTemp;
            WebUtil.addErrorMessage("设备："+selected.getAssetName() +"，申请已拒绝！");
        }
    }

    public boolean resubmit() {
        return secondmentOrderDtoTemp.getSecondmentNo().equals(selected.getSecondmentNo()) && !secondmentOrderDtoTemp.getAssetUid().equals(selected.getAssetUid());
    }
}
