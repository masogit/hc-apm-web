package com.ge.apm.service.converter;

import com.ge.apm.domain.AutoCompleteVo;
import org.apache.commons.lang3.StringUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.Objects;

@FacesConverter("autoCompleteVoConverter")
public class AutoCompleteVoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (StringUtils.isNotBlank(value) && !"null".equalsIgnoreCase(value)) {
            return new AutoCompleteVo(value, value);
        } else {
            return new AutoCompleteVo();
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (Objects.isNull(object)) {
            return "";
        }
        return String.valueOf(((AutoCompleteVo) object).getLabel());
    }
}
