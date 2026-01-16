package com.ge.apm.service.asset;

import com.ge.apm.domain.dto.SelectItemDto;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.Converter;

@FacesConverter(value = "labelValueConverter")
public class LabelValueConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) return null;
        SelectItemDto selectItemDto = new SelectItemDto(value, null);
        return selectItemDto;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) return "";
        if (value instanceof SelectItemDto) {
            return ((SelectItemDto) value).getLabel();
        }
        return value.toString();
    }


}