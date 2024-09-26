/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.HisAssetGroupAssetMatchRepository;
import com.ge.apm.dao.PmOrderRuleRepository;
import com.ge.apm.domain.PmOrderRule;
import com.ge.apm.domain.his.HisAssetGroupAssetMatch;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("hisAssetGroupConverter")
public class HisAssetGroupConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            HisAssetGroupAssetMatchRepository hisAssetGroupAssetMatchRepository = WebUtil.getBean(HisAssetGroupAssetMatchRepository.class);
            return hisAssetGroupAssetMatchRepository.findById(value);
        }
        else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return ((HisAssetGroupAssetMatch) object).getUuid();
        }else {
            return null;
        }
    }
    
}
