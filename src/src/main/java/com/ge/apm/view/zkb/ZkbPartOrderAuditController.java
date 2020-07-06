package com.ge.apm.view.zkb;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.domain.ZkbPartOrder;
import com.ge.apm.service.zkb.ZkbPartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.HashMap;

/**
 * @author : 503189362
 * @date : 2020/7/6
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbPartOrderAuditController extends GenericCRUDUUIDController<ZkbPartOrder> {

    private ZkbPartOrderService zkbPartOrderService;
    private UserContextService userContextService;
    private UserAccount currentUser;
    private String actionName;
    private V2_WorkOrder selectedWorkFlow;
    private ZkbContract zkbContract;
    private String orderType;

    @Override
    protected void init() {
        super.init();
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        currentUser = UserContextService.getCurrentUserAccount();

        String encodeStr = WebUtil.getRequestParameter("str");
        actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        if(StringUtils.isNotEmpty(selectedId)){
            HashMap m = zkbPartOrderService.queryEditInfo(selectedId);
            this.selected = m.get("zkbPartOrder") ==null ? new ZkbPartOrder() : (ZkbPartOrder)m.get("zkbPartOrder");
            if(StringUtils.isNotEmpty(this.selected.getOrderType())){
                orderType = this.selected.getOrderType();
            }
            zkbContract = m.get("zkbContract") ==null ? null : (ZkbContract)m.get("zkbContract");
            selectedWorkFlow = m.get("V2_WorkOrder") == null ? null : (V2_WorkOrder)m.get("V2_WorkOrder");

        }
    }

    public V2_WorkOrder getSelectedWorkFlow() {
        return selectedWorkFlow;
    }

    public void setSelectedWorkFlow(V2_WorkOrder s) {
        this.selectedWorkFlow = s;
    }

    public ZkbContract getZkbContract() {
        return zkbContract;
    }

    public void setZkbContract(ZkbContract zkbContract) {
        this.zkbContract = zkbContract;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }



    @Override
    protected GenericRepositoryUUID<ZkbPartOrder> getDAO() {
        return zkbPartOrderService.getZkbPartOrderRepository();
    }
}
