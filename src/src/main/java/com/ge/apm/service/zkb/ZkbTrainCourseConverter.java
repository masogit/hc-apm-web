package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbTrainCourseRepository;
import com.ge.apm.domain.ZkbTrainCourse;
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
@FacesConverter("zkbTrainCourseConverter")
public class ZkbTrainCourseConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                ZkbTrainCourseRepository dao = WebUtil.getBean(ZkbTrainCourseRepository.class);
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
            return ((ZkbTrainCourse) o).getId();
        }
        else {
            return null;
        }
    }
}
