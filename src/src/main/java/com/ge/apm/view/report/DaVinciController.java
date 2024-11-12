package com.ge.apm.view.report;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.AccessRecordsFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;

@ManagedBean
@ViewScoped
public class DaVinciController {
    Logger logger = LoggerFactory.getLogger(DaVinciController.class);

    private String dashboardId;
    private MicroServiceUtil microServiceUtil;
    private UserAccount currentUser;

    private MqUtils mqUtils;
    @PostConstruct
    protected void init() {
        microServiceUtil = WebUtil.getBean(MicroServiceUtil.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        this.dashboardId = (String) UrlEncryptController.getValueFromMap(encodeStr, "dashboardId");
        currentUser = UserContextService.getCurrentUserAccount();
        mqUtils = WebUtil.getBean(MqUtils.class);
    }

    public String openNewWindowView(String configItemValue) {
        String url = "dashboardId=" + configItemValue;
        return "/portal/asset/info/DaVinciView.xhtml?faces-redirect=true&amp;&str=" + UrlParamUtil.encodeUrlQueryString(url);
    }

    public String view() {
        mqUtils.sendUserAccessRecords(currentUser,"view", AccessRecordsFeature.ASSET_DEVICE_OVERDUE.getFeatureCode());
        String daVinciShareLink = microServiceUtil.getDaVinciShareLink(UserContextService.getAccessToken(), dashboardId);
        logger.info("daVinciShareLink ================================== {}", daVinciShareLink);
        return daVinciShareLink;
    }

    public Map davinciAccessToken(){
        String apmToken= UserContextService.getAccessToken();
        return   microServiceUtil.getDavinciToken(apmToken);

    }

}
