package com.ge.apm.domain.reminder;

import lombok.Data;

/**
 * @author 503310146
 */
@Data
public class EmailParam {

    String email;

    public EmailParam(String email) {
        this.email = email;
    }

    public EmailParam() {
    }

}
