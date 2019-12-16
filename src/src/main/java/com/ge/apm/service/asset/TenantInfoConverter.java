package com.ge.apm.service.asset;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.TenantInfo;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * @author 212605082 on 2019/12/4.
 */
@FacesConverter("tenantInfoConverter")
public class TenantInfoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                TenantInfoRepository dao = WebUtil.getBean(TenantInfoRepository.class);
                return dao.findById(Integer.parseInt(value));
            } catch(NumberFormatException e) {
                return null;
            }
        }
        else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object == null) {
            return null;
        }
        if(object instanceof TenantInfo){
            TenantInfo tenantInfo = (TenantInfo) object;

            return String.valueOf(tenantInfo.getId());
        }

        return null;
    }

}
