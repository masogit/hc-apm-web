package com.ge.apm.view.global;

import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class CurrentCustomConfigController {

    private CustomConfigService customConfigService;

    private CustomConfigObject customConfig;

    @PostConstruct
    protected void init() {
        customConfigService = WebUtil.getBean(CustomConfigService.class);

        customConfig = customConfigService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
    }

    public boolean showImportantField(String fieldKey) {
        if (customConfig.getAssetsImportantFields().contains(fieldKey)) {
            return true;
        }
        return false;
    }

}