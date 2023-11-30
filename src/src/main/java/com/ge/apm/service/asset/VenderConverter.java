package com.ge.apm.service.asset;

import com.ge.apm.dao.VenderInfoRepository;
import com.ge.apm.domain.VenderInfo;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("venderConverter")
public class VenderConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                VenderInfoRepository dao = WebUtil.getBean(VenderInfoRepository.class);
                return dao.findById(value);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object == null) {
            return null;
        }
        if(object instanceof VenderInfo){
            VenderInfo venderInfo = (VenderInfo) object;
            return venderInfo.getId();
        }
        return null;
    }

}
