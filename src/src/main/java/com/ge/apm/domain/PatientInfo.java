package com.ge.apm.domain;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class PatientInfo {

    private Integer index;

    private String patientNum;

    private String patientBedNum;

    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
