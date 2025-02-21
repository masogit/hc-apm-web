package com.ge.apm.view;

import com.ge.apm.dao.LargeScreenConfigRepository;
import com.ge.apm.dao.LargeScreenItemRepository;
import com.ge.apm.domain.LargeScreenConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.LargeScreen.LargeScreenConfigService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.data.FilterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class LargeScreenConfigController extends GenericCRUDUUIDController<LargeScreenConfig> implements Serializable, ServerEventInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(LargeScreenConfigController.class);
    private UserAccount currentUser;

    private LargeScreenConfigRepository dao;
    private LargeScreenConfigService largeScreenConfigService;

    private LargeScreenItemRepository largeScreenItemRepository;

    @Override
    protected void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(LargeScreenConfigRepository.class);
        largeScreenConfigService = WebUtil.getBean(LargeScreenConfigService.class);
        this.searchFilters = new ArrayList<>();
    }

    @Override
    protected GenericRepositoryUUID<LargeScreenConfig> getDAO() {
        return dao;
    }


    @Override
    protected Page<LargeScreenConfig> doLoadData(PageRequest pageRequest) {
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, Arrays.asList(currentUser.getTenantUID(),"-1")));
        Page<LargeScreenConfig> largeScreenConfigs = super.doLoadData(pageRequest);
        this.searchFilters.clear();
        return largeScreenConfigs;

    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
    }


    @Override
    public void save(){
        LargeScreenConfig selected = this.selected;
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ,selected.getName()));
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,selected.getTenantUID()));
        List<LargeScreenConfig> configs = dao.findBySearchFilter(searchFilters);
        if(!CollectionUtils.isEmpty(configs)){
            WebUtil.addErrorMessage("配置-"+selected.getName()+",版本名称重复");
            return;
        }
        selected.setLastModifiedBy(currentUser.getUid());
        selected.setLastModifiedDate(new Date());
        dao.save(selected);
        WebUtil.addSuccessMessage("配置-"+selected.getName()+" 已更新");
        this.selected = null;
    }
    @Override
    public void delete(){
        try {
            largeScreenConfigService.deleteLargeScreenConfig(this.selected, currentUser);
            WebUtil.addSuccessMessage("配置-"+this.selected.getName()+"已删除");
            this.selected = null;
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.addErrorMessage("配置-"+this.selected.getName()+"删除失败!"+e.getMessage());
        }
    }

    public void cloneLargeScreenConfig(LargeScreenConfig config){
        try {
            LargeScreenConfig largeScreenConfig = largeScreenConfigService.cloneLargeScreenConfig(config, currentUser);
            WebUtil.addSuccessMessage("配置-"+config.getName()+"复制成功,"+largeScreenConfig.getName());
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.addErrorMessage("配置-"+config.getName()+"复制失败");
        }
    }
    public void  enable(LargeScreenConfig config){
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,config.getTenantUID()));
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ,1));
        List<LargeScreenConfig> configs = dao.findBySearchFilter(searchFilters);
        configs.forEach(item->item.setStatus(0));
        config.setStatus(1);
        config.setUsingDate(new Date());
        configs.add(config);
        dao.save(configs);
        WebUtil.addSuccessMessage("配置-"+config.getName()+" 已启用");
    }
    public void  disabled(LargeScreenConfig config){
        config.setStatus(0);
        dao.save(config);
        WebUtil.addSuccessMessage("配置-"+config.getName()+" 已停用");
    }
}
