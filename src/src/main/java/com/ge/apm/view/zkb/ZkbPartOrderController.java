package com.ge.apm.view.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.zkb.*;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
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
 * @date : 2020/6/22
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbPartOrderController  extends GenericCRUDUUIDController<ZkbPartOrder> {

    private ZkbPartOrderRepository dao;
    private UserContextService userContextService;
    private UserAccount currentUser;
    private List<TenantInfo> tenantInfoList;
    private String actionName;
    private String selectionTenantInfo;
    private Page<ZkbPartOrder> zkbContractPage;
    private ZkbPartOrderService zkbPartOrderService;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbPartOrderRepository.class);
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        if(userContextService.isZkbUser()) {
            this.selectionTenantInfo = currentUser.getTenantUID();
        }

        tenantInfoList = zkbPartOrderService.getSourceTenantInfoList();

    }


    @Override
    protected GenericRepositoryUUID<ZkbPartOrder> getDAO() {
        return dao;
    }

    @Override
    protected Page<ZkbPartOrder> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        if(this.selectionTenantInfo != null){
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectionTenantInfo));
        }

        zkbContractPage = dao.findBySearchFilter(searchFilters, pageRequest);
        return zkbContractPage;
    }

    public TenantInfo getTenantInfo(String tenantInfoUID){
        return zkbPartOrderService.getTenantInfo(tenantInfoUID);
    }

    public String getOrderTypeText(String value){
        switch (value){
            case "Parts" :
                return "备件申请";
            case "Probe" :
                return "探头申请";
            case "Artificial" :
                return "人工申请";
            default:
                return "";
        }
    }

    /**
     * @author : 503189362
     * @description : 租户选择变化时，那么重新进行编辑页面
     * @date : 2020/4/21 15:06
     */
    public void tenantInfoSelect(){
        this.cancel();
    }

    public void cancel() {
        this.selected = null;
    }

    @Override
    public void delete() {
        if(ZkbPartOrder.PartOrderStatus.Requested.toString().equals(selected.getStatus())){
            zkbPartOrderService.delete(this.selected);
            this.selected= null;
        }else {
            WebUtil.addErrorMessage("数据已审批不能删除！","");
            return;
        }
    }

    public String createAction() {
        return UrlEncryptController.encodeUrlParam("Edit?action=Create&selectionTenantInfo=".concat(selectionTenantInfo));
    }
    public String editAction() {
        return UrlEncryptController.encodeUrlParam("AuditStep?action=Edit&selectedid=".concat(selected.getId()));
}


    public UserAccount getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserAccount currentUser) {
        this.currentUser = currentUser;
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

    public String getSelectionTenantInfo() {
        return selectionTenantInfo;
    }

    public void setSelectionTenantInfo(String selectionTenantInfo) {
        this.selectionTenantInfo = selectionTenantInfo;
    }

    public Page<ZkbPartOrder> getZkbContractPage() {
        return zkbContractPage;
    }

    public void setZkbContractPage(Page<ZkbPartOrder> zkbContractPage) {
        this.zkbContractPage = zkbContractPage;
    }
}
