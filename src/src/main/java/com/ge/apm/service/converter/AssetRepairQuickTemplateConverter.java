package com.ge.apm.service.converter;

import com.ge.apm.dao.AssetRepairQuickTemplateRepository;
import com.ge.apm.domain.AssetRepairQuickTemplate;
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
@FacesConverter(value = "assetRepairQuickTemplateConverter")
public class AssetRepairQuickTemplateConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            AssetRepairQuickTemplateRepository bean = WebUtil.getBean(AssetRepairQuickTemplateRepository.class);
            return bean.findById(value);
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof AssetRepairQuickTemplate) {
            AssetRepairQuickTemplate assetRepairQuickTemplate = (AssetRepairQuickTemplate) value;

            return assetRepairQuickTemplate.getId();
        }
        return null;
    }
}
