package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbContactPersonRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbContactPerson;
import com.ge.apm.service.zkb.ZkbContactPersonService;
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
import java.util.Date;
import java.util.List;

/**
 * @author 212605082 on 2019/12/16.
 */
@ManagedBean
@ViewScoped
public class ZkbContactPersonController extends GenericCRUDUUIDController<ZkbContactPerson> {

    private ZkbContactPersonRepository dao;
    private ZkbContactPersonService zkbContactPersonService;
    private UserContextService userContextService;

    private UserAccount currentUser;
    private List<TenantInfo> tenantInfoList;
    private String selectionTenantInfo;
    private String actionName;

    @Override
    protected void init() {
        super.init();

        dao = WebUtil.getBean(ZkbContactPersonRepository.class);
        zkbContactPersonService = WebUtil.getBean(ZkbContactPersonService.class);
        userContextService = WebUtil.getBean(UserContextService.class);

        currentUser = UserContextService.getCurrentUserAccount();

        tenantInfoList = zkbContactPersonService.getSourceTenantInfoList();

        if(userContextService.isZkbUser()) {
            this.selectionTenantInfo = currentUser.getTenantUID();
        }
    }

    @Override
    protected GenericRepositoryUUID<ZkbContactPerson> getDAO() {
        return dao;
    }

    @Override
    protected Page<ZkbContactPerson> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        if(this.selectionTenantInfo != null){
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectionTenantInfo));
        }

        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();

        this.actionName = "Create";
        this.selected.setTenantUID(this.selectionTenantInfo);
        this.selected.setCreatedBy(this.currentUser.getLoginName());
        this.selected.setCreatedDate(new Date());
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.actionName = "Edit";

        this.selected.setLastModifiedBy(this.currentUser.getLoginName());
        this.selected.setLastModifiedDate(new Date());
    }

    @Override
    public void save() {
        super.save();
        this.cancel();
    }

    public void tenantInfoSelect(){
        this.cancel();
    }

    public void cancel() {
        this.selected = null;
    }

    public TenantInfo getTenantInfo(String tenantInfoUID){
        return zkbContactPersonService.getTenantInfo(tenantInfoUID);
    }

    public boolean isHaveMaintenanceManager(){
        if(StringUtils.isNotEmpty(this.selectionTenantInfo)){
           if(zkbContactPersonService.isHaveContactPosition(ZkbContactPerson.Position.MaintenanceManager.name(), this.selectionTenantInfo)){
               return Boolean.TRUE;
           }
        }else {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public boolean isHaveZKBAdmin(){
        if(StringUtils.isNotEmpty(this.selectionTenantInfo)){
            if(zkbContactPersonService.isHaveContactPosition(ZkbContactPerson.Position.ZKBAdmin.name(), this.selectionTenantInfo)){
                return Boolean.TRUE;
            }
        }else {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public String getSelectionTenantInfo() {
        return selectionTenantInfo;
    }

    public void setSelectionTenantInfo(String selectionTenantInfo) {
        this.selectionTenantInfo = selectionTenantInfo;
    }

    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}
