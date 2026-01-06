package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.domain.dto.SelectItemDto;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.Converter;
@FacesConverter(forClass = String.class, value = "stringIdentityConverter")
public class StringIdentityConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(value!=null){
            return JSONObject.parseObject(value, SelectItemDto.class);
        }
        return value; // 从 Entry 转为 String（由 itemValue 决定）
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value!=null){
            return JSONObject.toJSONString(value);
        }
        return null;
    }
}