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

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.I18nMessage;

import webapp.framework.web.WebUtil;

/**
 *
 * @author 503035627
 */
@FacesConverter("i18nMessageConverter")
public class I18nMessageConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                I18nMessageRepository dao = WebUtil.getBean(I18nMessageRepository.class);
                return dao.findById(Integer.parseInt(value));
            } catch(NumberFormatException e) {
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
            return String.valueOf(((I18nMessage) object).getId());
        }
        else {
            return null;
        }
    }
    
}
