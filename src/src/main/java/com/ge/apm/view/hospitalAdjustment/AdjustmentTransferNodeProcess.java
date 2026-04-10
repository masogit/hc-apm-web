package com.ge.apm.view.hospitalAdjustment;

import java.io.Serializable;
import java.util.Date;

public class AdjustmentTransferNodeProcess implements Serializable {

    private String nodeName;

    private String operator;

    private Date operatorTime;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(Date operatorTime) {
        this.operatorTime = operatorTime;
    }
}
