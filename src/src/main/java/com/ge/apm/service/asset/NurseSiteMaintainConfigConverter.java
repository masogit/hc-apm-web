/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.google.common.base.Strings;
import java.util.LinkedHashMap;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@FacesConverter("nurseSiteMaintainConfigConverter")
public class NurseSiteMaintainConfigConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {
				NurseSiteMaintainService nurseSiteMaintainService = (NurseSiteMaintainService) WebUtil.getBean(NurseSiteMaintainService.class);
				return nurseSiteMaintainService.getMaintainConfigById(value);
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
			return "";
		}
		if (object instanceof NurseSiteMaintainConfig) {
			NurseSiteMaintainConfig config = (NurseSiteMaintainConfig) object;
			if (Strings.isNullOrEmpty(config.getId())) {
				return "";
			}
			return String.valueOf(config.getId());
		}
		if (object instanceof LinkedHashMap) {
			if (Strings.isNullOrEmpty(((LinkedHashMap) object).get("id").toString())) {
				return "";
			}
			return String.valueOf(((LinkedHashMap) object).get("id"));
		} else {
			return "";
		}
	}

}
