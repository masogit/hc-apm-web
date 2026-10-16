package com.ge.apm.service.asset;

import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.domain.Supplier;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("udidSupplierConverter")
public class UdisSupplierConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (StringUtils.isNotBlank(value)) {
            int siteId = UserContextService.getCurrentUserAccount().getSiteId();

            SupplierRepository supplierDao = WebUtil.getBean(SupplierRepository.class);
            Supplier supplier = supplierDao.getBySiteIdAndName(siteId, value);
            if (supplier == null) {
                // 来自NMPA的虚拟制造商，id为负数
                supplier = new Supplier();
                supplier.setName(value);
                supplier.setId(-1);
            }
            return supplier;
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null) {
            return String.valueOf(((Supplier) object).getName());
        } else {
            return null;
        }
    }
}
