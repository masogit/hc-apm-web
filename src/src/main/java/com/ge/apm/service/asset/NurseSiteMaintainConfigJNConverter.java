package com.ge.apm.service.asset;

import com.ge.apm.dao.NurseSiteMaintainConfigJNRepository;
import com.ge.apm.domain.NurseSiteMaintainConfigJN;
import com.google.common.base.Strings;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.LinkedHashMap;

@FacesConverter("nurseSiteMaintainConfigJNConverter")
public class NurseSiteMaintainConfigJNConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                NurseSiteMaintainConfigJNRepository nurseSiteMaintainConfigJNRepository = WebUtil.getBean(NurseSiteMaintainConfigJNRepository.class);
                return nurseSiteMaintainConfigJNRepository.findById(value);
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
        if (object instanceof NurseSiteMaintainConfigJN) {
            NurseSiteMaintainConfigJN config = (NurseSiteMaintainConfigJN) object;
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