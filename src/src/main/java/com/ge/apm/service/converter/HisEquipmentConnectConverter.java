/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.converter;

import com.ge.apm.dao.HisEquipmentConnectRepository;
import com.ge.apm.domain.his.HisEquipmentConnect;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author
 */
@FacesConverter("hisEquipmentConnect")
public class HisEquipmentConnectConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                HisEquipmentConnectRepository dao = WebUtil.getBean(HisEquipmentConnectRepository.class);
                return dao.findById(value);
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
            return String.valueOf(((HisEquipmentConnect) object).getId());
        }
        else {
            return null;
        }
    }
    
}
