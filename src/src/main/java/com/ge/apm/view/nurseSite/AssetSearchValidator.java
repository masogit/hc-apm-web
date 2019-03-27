package com.ge.apm.view.nurseSite;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * @author nick.liu
 * @create 2019/3/27 13:18
 */
@FacesValidator("assetSearchValidator")
public class AssetSearchValidator implements Validator {

    private static final Logger logger = LoggerFactory.getLogger(AssetSearchValidator.class);

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {
        logger.debug("enter AssetSearchValidator");

        UIInput departNumComponent = (UIInput) uiComponent.getAttributes().get("departNumComponent");
        String inputDepartNum = (String)departNumComponent.getValue();
        String departOrg = null;
        if(value != null) {
            departOrg = (String)value;
        }

        if(StringUtils.isBlank(departOrg) && StringUtils.isBlank(inputDepartNum)) {
            departNumComponent.setValid(false);
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "请选择科室或输入设备编号", null
            ));
        }
        logger.debug("exit AssetSearchValidator");
    }
}
