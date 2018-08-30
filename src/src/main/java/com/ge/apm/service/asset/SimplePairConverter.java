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

import com.ge.apm.domain.MaintainDataPair;

/**
 *
 * @author 212579464
 */
@FacesConverter("simplePairConverter")
public class SimplePairConverter implements Converter{
	private MaintainDataPair sp;
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                return sp;
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
        	sp=(MaintainDataPair)object;
            return String.valueOf(sp.getKey());
        }
        else {
            return null;
        }
    }
    
}
