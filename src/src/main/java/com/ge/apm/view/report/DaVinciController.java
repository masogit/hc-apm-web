package com.ge.apm.view.report;

import com.ge.apm.service.utils.MicroServiceInvoker;
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
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
public class DaVinciController {
    Logger logger = LoggerFactory.getLogger(DaVinciController.class);

    private String daVinciUrl;

    @PostConstruct
    protected void init() {
        MicroServiceUtil microServiceUtil = WebUtil.getBean(MicroServiceUtil.class);
        String baseUrl = microServiceUtil.getBaseUrl();
        String webContextUrl = microServiceUtil.getWebContextUrl();
        String encodeStr = WebUtil.getRequestParameter("str");
        daVinciUrl = UrlEncryptController.getValueFromMap(encodeStr, "daVinciUrl") + "?dashboardId=" + UrlEncryptController.getValueFromMap(encodeStr, "dashboardId");
        logger.info("baseUrl ========================= {}", baseUrl);
        logger.info("webContextUrl ========================= {}", webContextUrl);
        logger.info("daVinciUrl ========================= {}", daVinciUrl);
    }

    public String openNewWindowView(String configItemValue) {
        List<String> urlArr = Arrays.asList(configItemValue.split("\\?"));
        String url = "daVinciUrl=" + urlArr.get(0) + "&" + urlArr.get(1);
        return "/portal/asset/info/DaVinciView.xhtml?faces-redirect=true&str=" + UrlParamUtil.encodeUrlQueryString(url);
    }

    public String view() {
        return daVinciUrl + "&apmToken=" + UserContextService.getAccessToken();
    }
}
