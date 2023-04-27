package com.ge.apm.domain.hisory;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 503310146
 */
@Data
public class HistoryInfo implements Serializable {
    private static final long serialVersionUID = -6811948019302265355L;

    private String fieldName;

    private Object oldValue;

    private Object newValue;

    public HistoryInfo() {
    }

    public HistoryInfo(String fieldName, Object oldValue, Object newValue) {
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
