package com.ge.apm.service.asset;

import com.ge.apm.dao.ZkbEngineerRepository;
import com.ge.apm.dao.ZkbPartOrderCatRepository;
import com.ge.apm.domain.ZkbEngineer;
import com.ge.apm.domain.ZkbPartOrderCat;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("zkbEngineerConverter")
public class ZkbEngineerConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                ZkbEngineerRepository dao = WebUtil.getBean(ZkbEngineerRepository.class);
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
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        if(o != null) {
            return ((ZkbEngineer) o).getId();
        }
        else {
            return null;
        }
    }
}
