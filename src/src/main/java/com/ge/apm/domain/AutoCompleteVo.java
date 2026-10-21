package com.ge.apm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoCompleteVo {

    private String label = "";
    private String value = "";
    private Boolean apmExists = false;
    private String group = "";

    public AutoCompleteVo(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public AutoCompleteVo(String label, String value, String group) {
        this.label = label;
        this.value = value;
        this.group = group;
    }
}
