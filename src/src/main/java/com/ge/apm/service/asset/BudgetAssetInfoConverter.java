package com.ge.apm.service.asset;

import com.ge.apm.dao.BudgetAssetInfoRepository;
import com.ge.apm.domain.budget.BudgetAssetInfo;
import webapp.framework.web.WebUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter("budgetAssetInfoConverter")
public class BudgetAssetInfoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                BudgetAssetInfoRepository dao = WebUtil.getBean(BudgetAssetInfoRepository.class);
                return dao.findById(value);
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
        if(object instanceof BudgetAssetInfo){
            BudgetAssetInfo budgetAssetInfo = (BudgetAssetInfo) object;
            return budgetAssetInfo.getId();
        }
        return null;
    }

}

