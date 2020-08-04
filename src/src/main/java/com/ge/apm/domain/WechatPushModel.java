package com.ge.apm.domain;

import java.io.Serializable;

/**
 * @author : 503189362
 * @date : 2020/7/15
 * @description :
 */

public class WechatPushModel implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1L;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
