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

import com.ge.apm.domain.ApmDropDownItem;

import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@FacesConverter("apmDropDownConverter")
public class ApmDropDownConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {
				char c=value.charAt(0);
				if(c>160){
					return null;
				}
				ApmDropDownItem tempItem = new ApmDropDownItem();
				tempItem.setKey(value);
				tempItem.setLabel(value);
				return tempItem;
			} catch (NumberFormatException e) {
				// throw new ConverterException(new
				// FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error",
				// "Not a valid theme."));
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if (object != null) {
			return ((ApmDropDownItem) object).getKey();
		} else {
			return null;
		}
	}

}
