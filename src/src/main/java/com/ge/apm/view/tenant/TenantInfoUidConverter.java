/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.tenant;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.TenantInfo;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author 212579464
 */
@FacesConverter("tenantInfoUidConverter")
public class TenantInfoUidConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            TenantInfoRepository dao = WebUtil.getBean(TenantInfoRepository.class);
            return dao.findByUid(value);
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof TenantInfo) {
            TenantInfo tenantInfo = (TenantInfo) object;

            return tenantInfo.getUid();
        }

        return null;
    }

}
