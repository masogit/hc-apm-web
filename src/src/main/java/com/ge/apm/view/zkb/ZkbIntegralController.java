package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbIntegralRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbContactPerson;
import com.ge.apm.domain.ZkbIntegral;
import com.ge.apm.service.zkb.ZkbIntegralService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2021/2/2
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbIntegralController extends GenericCRUDUUIDController<ZkbIntegral> {

    private ZkbIntegralRepository zkbIntegralRepository;
    private ZkbIntegralService zkbIntegralService;
    private String selectionTenantInfo;
    private String actionName;
    private List<TenantInfo> tenantInfoList;
    private UserAccount currentUser;

    @Override
    protected void init() {
        super.init();
        zkbIntegralRepository = WebUtil.getBean(ZkbIntegralRepository.class);
        zkbIntegralService = WebUtil.getBean(ZkbIntegralService.class);
        tenantInfoList = zkbIntegralService.getSourceTenantInfoList();
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected GenericRepositoryUUID<ZkbIntegral> getDAO() {
        return zkbIntegralRepository;
    }

    @Override
    protected Page<ZkbIntegral> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        return zkbIntegralRepository.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.actionName = "Create";
        selected = new ZkbIntegral();
        selected.setCoefficient(ZkbIntegralService.initCoefficient);
        selected.setIntegralTotal(ZkbIntegralService.initialScore);
    }

    @Override
    public void prepareEdit(){
        super.prepareEdit();
        this.actionName = "Edit";
    }

    @Override
    public void save(){
        if(StringUtils.isEmpty(selected.getId())){
            ZkbIntegral exist = zkbIntegralRepository.findFirstByTenantUID(selected.getTenantUID());
            if(exist != null){
                WebUtil.addErrorMessage("租户已存在积分数据","");
                return;
            }
        }
        zkbIntegralService.save(selected,currentUser);
        this.selected = null;
        this.actionName = "";
    }

    public void cancel(){
        this.selected = null;
        this.actionName = "";
    }

    public TenantInfo getTenantInfo(String tenantInfoUID){
        return zkbIntegralService.getTenantInfo(tenantInfoUID);
    }

    public String getSelectionTenantInfo() {
        return selectionTenantInfo;
    }

    public void setSelectionTenantInfo(String selectionTenantInfo) {
        this.selectionTenantInfo = selectionTenantInfo;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }
}
