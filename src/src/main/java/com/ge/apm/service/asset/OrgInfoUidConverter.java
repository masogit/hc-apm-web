/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.google.common.base.Strings;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.LinkedHashMap;

/**
 *
 * @author 212579464
 */
@FacesConverter("orgInfoUidConverter")
public class OrgInfoUidConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            OrgInfoRepository dao = WebUtil.getBean(OrgInfoRepository.class);
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
        if (object instanceof OrgInfo) {
            OrgInfo orgInfo = (OrgInfo) object;

            return orgInfo.getUid();
        }

        return null;
    }

}
