/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author 212579464
 */
@FacesConverter("orgInfoColumnFilterConverter")
public class OrgInfoColumnFilterConverter implements Converter {
    OrgInfo emptyOrg = new OrgInfo();
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {

        if (value != null && value.trim().length() > 0) {
            try {
                if (Integer.parseInt(value) < 0) {
                    return emptyOrg;
                }
                OrgInfoRepository dao = WebUtil.getBean(OrgInfoRepository.class);
                return dao.findById(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                return emptyOrg;
            }
        } else {
            return emptyOrg;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object == null) {
            return null;
        }
        if(object instanceof OrgInfo){
            OrgInfo orgInfo = (OrgInfo) object;

            return String.valueOf(orgInfo.getId());
        }

        return null;
    }

}
