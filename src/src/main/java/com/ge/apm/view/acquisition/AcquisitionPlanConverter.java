package com.ge.apm.view.acquisition;

import com.ge.apm.domain.acquisition.AcquisitionPlan;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.List;

/**
 * JSF Converter for AcquisitionPlan selectOneMenu.
 * Replaces omnifaces.SelectItemsConverter by converting between
 * AcquisitionPlan objects and their id strings.
 */
@FacesConverter("acquisitionPlanConverter")
public class AcquisitionPlanConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        List<AcquisitionPlan> planList = getPlanList(context);
        if (planList != null) {
            for (AcquisitionPlan plan : planList) {
                if (value.equals(plan.getId())) {
                    return plan;
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof AcquisitionPlan) {
            return ((AcquisitionPlan) value).getId();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private List<AcquisitionPlan> getPlanList(FacesContext context) {
        return (List<AcquisitionPlan>) context.getApplication().evaluateExpressionGet(
                context, "#{acquisitionPlanAssetSelectController.planList}", List.class);
    }
}
