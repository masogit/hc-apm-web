/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.PmOrderRuleRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.PmOrderRule;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("pmOrderRuleConverter")
public class PmOrderRuleConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            PmOrderRuleRepository pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
            return pmOrderRuleRepository.findByUid(value);
        }
        else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return ((PmOrderRule) object).getUid();
        }else {
            return null;
        }
    }
    
}
