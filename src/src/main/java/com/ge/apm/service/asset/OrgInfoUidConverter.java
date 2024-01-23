/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.service.common.ConfigCacheService;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author 212579464
 */
@FacesConverter("orgInfoUidConverter")
public class OrgInfoUidConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            ConfigCacheService cacheService = WebUtil.getBean(ConfigCacheService.class);
            return cacheService.findOrgByUid(value);
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof OrgInfo) {
            OrgInfo orgInfo = (OrgInfo) object;

            return orgInfo.getUid();
        }

        return null;
    }

}
