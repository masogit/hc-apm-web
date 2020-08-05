package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbPartOrderCatRepository;
import com.ge.apm.domain.ZkbCourse;
import com.ge.apm.domain.ZkbPartOrderCat;
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
@FacesConverter("zkbPartOrderCatConverter")
public class ZkbPartOrderCatConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                ZkbPartOrderCatRepository dao = WebUtil.getBean(ZkbPartOrderCatRepository.class);
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
            return ((ZkbPartOrderCat) o).getId();
        }
        else {
            return null;
        }
    }
}
