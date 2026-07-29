package com.ge.apm.service.converter;

import com.ge.apm.dao.SurgeryAnesthesiaEquipmentConnectRepository;
import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaEquipmentConnect;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * 手麻系统设备Converter
 * 用于pickList组件中的对象转换
 */
@FacesConverter("surgeryAnesthesiaEquipmentConnectConverter")
public class SurgeryAnesthesiaEquipmentConnectConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                SurgeryAnesthesiaEquipmentConnectRepository dao = WebUtil.getBean(SurgeryAnesthesiaEquipmentConnectRepository.class);
                return dao.findById(value);
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null) {
            return String.valueOf(((SurgeryAnesthesiaEquipmentConnect) object).getId());
        } else {
            return null;
        }
    }

}