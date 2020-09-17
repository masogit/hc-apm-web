package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsItemRepository;
import com.ge.apm.domain.simpleparts.SimplePartsItem;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * @author: csh
 * @date: 2020/7/17 13:27
 * @version: 1.0
 */
@FacesConverter(value = "simplePartsItemConverter")
public class SimplePartsItemConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            SimplePartsItemRepository bean = WebUtil.getBean(SimplePartsItemRepository.class);
            return bean.findOne(value);
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof SimplePartsItem) {
            SimplePartsItem partsItem = (SimplePartsItem) value;

            return partsItem.getId();
        }
        return null;
    }
}
