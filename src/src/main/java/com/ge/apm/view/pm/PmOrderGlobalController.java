package com.ge.apm.view.pm;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_PmOrder;
import com.ge.apm.service.common.UserCommonService;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class PmOrderGlobalController {

    UserCommonService userCommonService;

    @PostConstruct
    public void init() {
        userCommonService = WebUtil.getBean(UserCommonService.class);
    }

    public List<UserAccount> getAckerList(String pmOrderId) {
        return userCommonService.getPmAckerList(pmOrderId);
    }

    public List<UserAccount> getAckerListForBatchComplete() {
        return userCommonService.getPmAckerListForBatchComplete();
    }

    public boolean isAutoAck(V2_PmOrder pmOrder) {
        return null == pmOrder.getAutoAckMaintenance() ? true : pmOrder.getAutoAckMaintenance();
    }

}
