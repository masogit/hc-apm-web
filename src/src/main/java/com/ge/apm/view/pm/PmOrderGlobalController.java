package com.ge.apm.view.pm;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_PmOrder;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class PmOrderGlobalController {

    UserCommonService userCommonService;
    ConfigService configService;

    @PostConstruct
    public void init() {
        userCommonService = WebUtil.getBean(UserCommonService.class);
        configService = WebUtil.getBean(ConfigService.class);
    }

    public List<UserAccount> getAckerList(String pmOrderId) {
        return userCommonService.getPmAckerList(pmOrderId);
    }

    public List<UserAccount> getAckerListForBatchComplete() {
        return userCommonService.getPmAckerListForBatchComplete();
    }

    public List<UserAccount> getAckerForMultiplePmOrders() {
        V2PmOrderController pmOrderController = WebUtil.getBean(V2PmOrderController.class);
        return userCommonService.getAckerForMultiplePmOrders(pmOrderController.pmIdList);
    }

    public boolean isAutoAck(V2_PmOrder pmOrder) {
        return null == pmOrder.getAutoAckMaintenance() ? true : pmOrder.getAutoAckMaintenance();
    }

    public boolean isAutoAck() {
        UserAccount user = UserContextService.getCurrentUserAccount();

        WorkflowConfig wfc = configService.findConfigBySite(user.getSiteUID());

        return null == wfc || null == wfc.getAutoAckMaintenance() ? true : wfc.getAutoAckMaintenance();
    }

    public List<String> getStepsInTableFilter() {
        List<String> steps = new ArrayList<>();

        steps.add("待派工");
        steps.add("待接单");
        steps.add("已计划");
        steps.add("待验收");
        steps.add("待关单");

        return steps;
    }

}
