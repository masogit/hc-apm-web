package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbFileDirectoryRepository;
import com.ge.apm.domain.ZkbFileDirectory;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


/**
 * @author 503202755 2020/7/30
 */
@FacesConverter("zkbDirectoryConverter")
public class ZkbDirectoryConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                ZkbFileDirectoryRepository dao = WebUtil.getBean(ZkbFileDirectoryRepository.class);
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
            return ((ZkbFileDirectory) o).getId();
        }
        else {
            return null;
        }
    }
}
