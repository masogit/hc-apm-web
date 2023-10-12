package com.ge.apm.view.report;

import com.ge.apm.service.utils.MicroServiceUtil;
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
        dashboardId = WebUtil.getRequestParameter("dashboardId");
    }

    public String openNewWindowView(String configItemValue) {
        return "/portal/asset/info/DaVinciView.xhtml?faces-redirect=true&dashboardId=" + configItemValue;
    }

    public String view() {
        String daVinciShareLink = microServiceUtil.getDaVinciShareLink(UserContextService.getAccessToken(), dashboardId);
        logger.info("daVinciShareLink ================================== {}", daVinciShareLink);
        return daVinciShareLink;
    }
/*

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
//        return daVinciUrl + "&apmToken=Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMTExdGp6eDEiLCJhdXRoIjoiQXNzZXRIZWFkLEFzc2V0U3RhZmYsQXVkaXRTdGFmZixIb3NwaXRhbEhlYWQsSVRBZG1pbixNdWx0aUxldmVsQXVkaXRvcixTcGFyZVBhcnRzQWRtaW4sU3VwZXJBZG1pbiIsInVpZCI6Ijk3OGZmNTBhMzI2NzQyYjE5ZjM0NTZmNzYyNDAxMjE5IiwidXNlcklkIjo4MDA1LCJleHAiOjE2OTk1MTY0ODF9.ZpSzpvIcmNmmgTpL6OMT5lGpLZdDUMfX1tM3PsVlg91qIY5yQfDm4qSFm7Jm2zYTUlfecKD5P23UA5zX0kQFpg";
        return daVinciUrl + "&apmToken=" + UserContextService.getAccessToken();
    }
*/
}
