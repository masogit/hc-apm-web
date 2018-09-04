/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.ge.apm.domain.V2_PmOrder;
import com.ge.apm.service.pm.V2_PmOrderService;

import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@FacesConverter("v2PmOrderConverter")
public class V2PmOrderConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                V2_PmOrderService service = (V2_PmOrderService) WebUtil.getBean(V2_PmOrderService.class);
                return service.findById(value);
            } catch(NumberFormatException e) {
//                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            	return null;
            }
        }
        else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((V2_PmOrder) object).getId());
        }
        else {
            return null;
        }
    }
    
}
