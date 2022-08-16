package com.ge.apm.view;

import com.ge.apm.dao.ConfigItemRepository;
import com.ge.apm.dao.ConfigItemValueRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.ConfigItem;
import com.ge.apm.domain.ConfigItemValue;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 503266962
 */
@ManagedBean
@ViewScoped
public class SuperAdminConfigItemController extends GenericCRUDUUIDController<ConfigItem> {

    private ConfigItemRepository dao;
    private ConfigItemValueRepository configItemValueRepository;
    private OrgInfoRepository orgInfoRepository;

    private OrgInfo selectSite;
    private UserAccount user;
    private Boolean superAdminEdit = true;

    private Map<Integer, String> categoryMap;

    @Override
    protected void init() {
        dao = WebUtil.getBean(ConfigItemRepository.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        user = UserContextService.getCurrentUserAccount();

        selectSite = orgInfoRepository.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        categoryMap = new HashMap<>();
        categoryMap.put(1,"设备档案");
        categoryMap.put(2,"设备调剂");
        categoryMap.put(3,"到货安装");
        categoryMap.put(4,"维修流程");
        categoryMap.put(5,"预防维护");
        categoryMap.put(6,"预算管理");
        categoryMap.put(7,"计量管理");
        categoryMap.put(8,"临床助手");
        categoryMap.put(9,"知识文档");
        categoryMap.put(10,"绩效分析");
        categoryMap.put(11,"质控评审");
    }

    @Override
    protected GenericRepositoryUUID<ConfigItem> getDAO() {
        return dao;
    }

    @Override
    protected Page<ConfigItem> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if(searchFilters == null){
            searchFilters = new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("superAdminEdit", SearchFilter.Operator.EQ, superAdminEdit));
        Page<ConfigItem> page = dao.findBySearchFilter(searchFilters, pageRequest);

        List<ConfigItem> list = page.getContent();

        if(CollectionUtils.isNotEmpty(list)){
            for (ConfigItem configItem : list) {
                // 使用上级配置
                ConfigItemValue value;
                if(null != configItem.getUserParentConfig() && configItem.getUserParentConfig()){
                    value = null;
                    value = configItemValueRepository.findFirstByKeyAndSiteUid(configItem.getKey(), selectSite.getSiteUID());
                    if(value == null){
                        value = configItemValueRepository.findFirstByKeyAndSiteUid(configItem.getKey(), selectSite.getHospitalUID());
                        if(value == null){
                            value = configItemValueRepository.findFirstByKeyAndSiteUid(configItem.getKey(), selectSite.getInstitutionUID());
                            if(value == null){
                                value = configItemValueRepository.findFirstByKeyAndSiteUid(configItem.getKey(), selectSite.getTenantUID());
                            }
                        }
                    }
                }else{
                    value = configItemValueRepository.findFirstByKeyAndSiteUid(configItem.getKey(), selectSite.getSiteUID());
                }
                if(value != null){
                    configItem.setConfigItemValue(value);
                    configItem.setShowValue(value.getValue());
                }else{
                    configItem.setShowValue(configItem.getDefaultValue());
                }
            }
        }

        return page;
    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        if (null != event) {
            if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
                OrgInfo site = (OrgInfo) event.getTreeNode().getData();
                if(StringUtils.isNotEmpty(site.getUid())){
                    this.selectSite = site;
                }
            } else {
                this.selectSite = orgInfoRepository.getByUid(user.getSiteUID());
            }
        }
    }

    public void saveConfig(){
        if(this.selectSite != null){

            ConfigItemValue configItemValue;
            // 如果不为空，并且siteUid相等，说明已经有配置，直接更新
            // 如果siteUid不相等，说明使用的是上级配置，需要新增数据
            if(this.selected.getConfigItemValue() != null
                    && this.selected.getConfigItemValue().getSiteUid().equals(this.selectSite.getSiteUID())){
                configItemValue = this.selected.getConfigItemValue();
            }else{
                configItemValue = new ConfigItemValue();
                configItemValue.setTenantUid(selectSite.getTenantUID());
                configItemValue.setInstitutionUid(selectSite.getInstitutionUID());
                configItemValue.setHospitalUid(selectSite.getHospitalUID());
                configItemValue.setSiteUid(selectSite.getSiteUID());
                configItemValue.setKey(this.selected.getKey());
            }
            configItemValue.setValue(this.selected.getShowValue());
            configItemValueRepository.save(configItemValue);
        }
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }
}
