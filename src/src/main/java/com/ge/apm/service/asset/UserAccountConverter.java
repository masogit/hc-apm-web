/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.google.common.base.Strings;
import java.util.LinkedHashMap;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@FacesConverter("userAccountConverter")
public class UserAccountConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                if (Integer.parseInt(value) < 0) {
                    return null;
                }
                UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
                return userDao.findById(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                return null;
//                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid Integer."));
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
        if(object instanceof UserAccount){
            UserAccount user = (UserAccount) object;
            if (Strings.isNullOrEmpty(user.getUid())) {
                return null;
            }
            return String.valueOf(user.getId());
        }
        if (object instanceof LinkedHashMap) {
            if (Strings.isNullOrEmpty(((LinkedHashMap) object).get("uid").toString())) {
                return null;
            }
            return String.valueOf(((LinkedHashMap) object).get("id"));
        } else {
            return null;
        }
    }

}
