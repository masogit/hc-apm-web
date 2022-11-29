package com.ge.apm.domain.reminder;

import lombok.Data;

/**
 * @author 503310146
 */
@Data
public class SmsParam {

    String telephone;

    public SmsParam(String telephone) {
        this.telephone = telephone;
    }

    public SmsParam() {
    }

}
