package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbPartOrderUserConfigRepository;
import com.ge.apm.domain.ZkbPartOrderUserConfig;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * @author : 503189362
 * @date : 2020/4/21
 * @description :
 */
@FacesConverter("zkbPartOrderUserConfigConverter")
public class ZkbPartOrderUserConfigConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                ZkbPartOrderUserConfigRepository dao = WebUtil.getBean(ZkbPartOrderUserConfigRepository.class);
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
            return ((ZkbPartOrderUserConfig) o).getId();
        }
        else {
            return null;
        }
    }
}
