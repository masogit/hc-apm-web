package com.ge.apm.view.secondment.dept;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.SecondmentConfigRepository;
import com.ge.apm.dao.SecondmentOrderRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.secondment.SecondmentConfig;
import com.ge.apm.domain.secondment.SecondmentOrder;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.cons.ModifySourceEnum;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.FilterEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SecondmentConfigController extends GenericCRUDUUIDController<SecondmentConfig> {

    private SecondmentConfigRepository dao;

    private UserAccountRepository userAccountRepository;

    private UserAccount userAccount;

    private List<UserAccount> userAccountList;

    private AssetInfoRepository assetInfoRepository;

    private SecondmentOrderRepository secondmentOrderRepository;

    private MqUtils mqUtils;


    @Override
    protected void init() {
        dao= WebUtil.getBean(SecondmentConfigRepository.class);
        userAccountRepository= WebUtil.getBean(UserAccountRepository.class);
        assetInfoRepository= WebUtil.getBean(AssetInfoRepository.class);
        mqUtils= WebUtil.getBean(MqUtils.class);
        secondmentOrderRepository= WebUtil.getBean(SecondmentOrderRepository.class);
        userAccount = UserContextService.getCurrentUserAccount();
        userAccountList = userAccountRepository.getByTenantUID(userAccount.getTenantUID());
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
    }

    @Override
    public void onBeforeNewObject(SecondmentConfig object) {
        object.setCreatedBy(userAccount.getUid());
        object.setCreatedDate(new Date());
        object.setTenantUID(userAccount.getTenantUID());
        object.setInstitutionUID(userAccount.getInstitutionUID());
        object.setHospitalUID(userAccount.getHospitalUID());
        object.setSiteUID(userAccount.getSiteUID());
    }

    @Override
    protected SecondmentConfigRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<SecondmentConfig> loadData(PageRequest pageRequest) {
        addFilter();
        Page<SecondmentConfig> secondmentConfigs = super.loadData(pageRequest);
        return secondmentConfigs;
    }
    private void addFilter(){
        String siteUID = userAccount.getSiteUID();
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,siteUID,"1*2+"));
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ,siteUID,"1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ,siteUID,"1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ,siteUID,"1*2+"));
    }
    public String getCurrentUserAccount(){
       return userAccount.getName();
    }

    public String getCreatedBy(SecondmentConfig config){
        String name="";
        for (UserAccount account : userAccountList) {
            if (account.getUid().equals(config.getCreatedBy())) {
                name = account.getName();
                break;
            }
        }
        return name;
    }

    @Override
    public void delete() {
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,userAccount.getTenantUID()));
        searchFilters.add(new SearchFilter("borrowConfigId", SearchFilter.Operator.EQ,selected.getId()));
        List<AssetInfo> assetInfos = assetInfoRepository.findBySearchFilter(searchFilters);
        searchFilters.clear();
        if(CollectionUtils.isNotEmpty(assetInfos)){
            List<String> collect = assetInfos.stream().map(AssetInfo::getUid).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetUid", SearchFilter.Operator.IN,collect));
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.IN, Arrays.asList(0,1,2,4)));
            List<SecondmentOrder> secondmentOrders = secondmentOrderRepository.findBySearchFilter(searchFilters);
            searchFilters.clear();
            if(CollectionUtils.isNotEmpty(secondmentOrders)){
                WebUtil.addErrorMessage("删除失败,关联设备有未完成工单！");
                return;
            }
            if(CollectionUtils.isNotEmpty(assetInfos)){
                for (AssetInfo assetInfo : assetInfos) {
                    assetInfo.setBorrowConfigId("-1");
                    assetInfo.setBorrowedStatus(-1);
                }
                assetInfoRepository.save(assetInfos);
                for (AssetInfo assetInfo : assetInfos) {
                    saveAssetHistory(assetInfo,"设备解绑科室间借调配置");
                }
            }
            dao.delete(selected);
        }else{
            dao.delete(selected);
        }

    }

    @Override
    public void save() {
        if (validateData()) {
            return;
        }
        dao.save(selected);
        WebUtil.addSuccessMessage("保存成功");
    }

    private boolean validateData() {
        boolean flag=false;
        String name = selected.getName();
        if(name == null){
            flag=true;
            WebUtil.addErrorMessage("名称 输入不可为空！");
        }
        Double unitPrice = selected.getUnitPrice();
        if(unitPrice == null){
            flag=true;
            WebUtil.addErrorMessage("单价 输入不可为空！");
        }
        Integer billingType = selected.getBillingType();
        if(billingType == null){
            flag=true;
            WebUtil.addErrorMessage("计价方式 输入不可为空！");
        }
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", flag);
        return flag;
    }

    public void saveAssetHistory(AssetInfo newAsset, String message) {
        MqContext mqContext = new MqContext();
        mqContext.setServiceName("hc-apm-web");
        mqContext.setClassName("SecondmentConfigController");
        mqContext.setMethodName("delete");
        mqContext.setDescribe(message);
        mqContext.setModifySource(ModifySourceEnum.DEPT_SECONDMENT.getCode());
        mqUtils.sendAssetToMQ(newAsset,userAccount,mqContext);
    }

    @Override
    public void onFilter(FilterEvent event) {
        Map<String, Object> filters = event.getFilters();
        Set<String> collect = new HashSet<>();
        if (filters.containsKey("createdBy")) {
            Set<Map.Entry<String, Object>> entries = filters.entrySet();
            for (Map.Entry<String, Object> filter: entries) {
                String key = filter.getKey();
                if("createdBy".equals(key)){
                    String value = (String)filter.getValue();
                     collect = userAccountList.stream().filter(item -> item.getName().contains(value)).map(UserAccount::getUid).collect(Collectors.toSet());
                    if(CollectionUtils.isEmpty(collect)){
                        collect.add("-1");
                    }
                    break;
                }
            }
        }
        super.onFilter(event);
        if(CollectionUtils.isNotEmpty(collect)){
            searchFilters.add(new SearchFilter("createdBy", SearchFilter.Operator.IN, collect));
        }
    }

    @Override
    protected void processTimeFiled(String filterVal, String fieldName) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            Date parse = dateFormat.parse(filterVal);
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.LTE, parse));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
