package com.ge.apm.validator.sharedasset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author Nick Liu
 */
@FacesValidator("chargingTimeValidator")
public class ChargingTimeValidator implements Validator {

    private static final Logger logger = LoggerFactory.getLogger(ChargingTimeValidator.class.getName());

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if(value == null) {
            logger.warn("计费结束时间为空");
            return;
        }

        UIInput chargingBeginTimeComponent = (UIInput) component.getAttributes().get("chargingBeginTimeComponent");

        Date chargingBeginTime = (Date) chargingBeginTimeComponent.getValue();
        Date chargingEndTime = (Date) value;

        if(chargingEndTime.after(chargingBeginTime) && chargingEndTime.before(Date.from(Instant.now().plus(2, ChronoUnit.HOURS)))) {
            if(logger.isDebugEnabled()) {
                logger.debug("pass time validation");
            }
        } else {
            chargingBeginTimeComponent.setValid(false);
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "计费结束时间必须在计费开始时间之后，并在未来两小时内",null
            ));
        }
    }
}
