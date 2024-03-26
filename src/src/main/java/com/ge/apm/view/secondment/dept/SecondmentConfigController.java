package com.ge.apm.view.secondment.dept;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.SecondmentConfigRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.secondment.SecondmentConfig;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.mq.MqContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class SecondmentConfigController extends GenericCRUDUUIDController<SecondmentConfig> {

    private SecondmentConfigRepository dao;

    private UserAccountRepository userAccountRepository;

    private UserAccount userAccount;

    private List<UserAccount> userAccountList;

    private AssetInfoRepository assetInfoRepository;

    private MqUtils mqUtils;

    @Override
    protected void init() {
        dao= WebUtil.getBean(SecondmentConfigRepository.class);
        userAccountRepository= WebUtil.getBean(UserAccountRepository.class);
        assetInfoRepository= WebUtil.getBean(AssetInfoRepository.class);
        mqUtils= WebUtil.getBean(MqUtils.class);
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
        return super.loadData(pageRequest);
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
        super.delete();
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,userAccount.getTenantUID()));
        searchFilters.add(new SearchFilter("borrowConfigId", SearchFilter.Operator.EQ,selected.getId()));
        List<AssetInfo> assetInfos = assetInfoRepository.findBySearchFilter(searchFilters);
        searchFilters.clear();
        for (AssetInfo assetInfo : assetInfos) {
            assetInfo.setBorrowConfigId(null);
        }
        assetInfoRepository.save(assetInfos);
        for (AssetInfo assetInfo : assetInfos) {
            saveAssetHistory(assetInfo,"设备解绑科室间借调配置");
        }


    }

    public void saveAssetHistory(AssetInfo newAsset,String message) {
        MqContext mqContext = new MqContext();
        mqContext.setServiceName("hc-apm-web");
        mqContext.setClassName("SecondmentConfigController");
        mqContext.setMethodName("delete");
        mqContext.setDescribe(message);
        mqUtils.sendAssetToMQ(newAsset,userAccount,mqContext);
    }
}
