package com.ge.apm.view.report;

import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class DaVinciController {
    Logger logger = LoggerFactory.getLogger(DaVinciController.class);

    private String dashboardId;
    private MicroServiceUtil microServiceUtil;

    @PostConstruct
    protected void init() {
        microServiceUtil = WebUtil.getBean(MicroServiceUtil.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        this.dashboardId = (String) UrlEncryptController.getValueFromMap(encodeStr, "dashboardId");
    }

    public String openNewWindowView(String configItemValue) {
        String url = "dashboardId=" + configItemValue;
        return "/portal/asset/info/DaVinciView.xhtml?faces-redirect=true&amp;&str=" + UrlParamUtil.encodeUrlQueryString(url);
    }

    public String view() {
        String daVinciShareLink = microServiceUtil.getDaVinciShareLink(UserContextService.getAccessToken(), dashboardId);
        logger.info("daVinciShareLink ================================== {}", daVinciShareLink);
        return daVinciShareLink;
    }

}
