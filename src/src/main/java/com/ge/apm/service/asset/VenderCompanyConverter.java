/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.VenderCompanyRepository;
import com.ge.apm.domain.VenderCompany;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author 212579464
 */
@FacesConverter("venderCompanyConverter")
public class VenderCompanyConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && !value.trim().isEmpty()) {
            try {
                if (Integer.parseInt(value) < 0) {
                    return null;
                }
                VenderCompanyRepository dao = WebUtil.getBean(VenderCompanyRepository.class);
                return dao.findById(value);
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object == null) {
            return null;
        }
        if(object instanceof VenderCompany){
            VenderCompany venderCompany = (VenderCompany) object;

            return String.valueOf(venderCompany.getId());
        }

        return null;
    }

}
