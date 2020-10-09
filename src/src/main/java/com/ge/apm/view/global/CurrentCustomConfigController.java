package com.ge.apm.view.global;

import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class CurrentCustomConfigController {

    private CustomConfigService customConfigService;

    @Getter
    private CustomConfigObject customConfig;

    private List<String> readableFieldsList;
    private List<String> editableFieldsList;

    @PostConstruct
    protected void init() {
        customConfigService = WebUtil.getBean(CustomConfigService.class);

        customConfig = customConfigService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        readableFieldsList = customConfigService.getReadAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        editableFieldsList = customConfigService.getEditAssetFieldsByRoles(customConfig, UserContextService.getRoles());
    }

    public boolean showImportantField(String fieldKey) {
        if (customConfig.getAssetsImportantFields().contains(fieldKey)) {
            return true;
        }
        return false;
    }

    public boolean needShow(String itemKey, String fieldKey) {
        return itemKey.equals(fieldKey) && readableFieldsList.contains(fieldKey);
    }

}