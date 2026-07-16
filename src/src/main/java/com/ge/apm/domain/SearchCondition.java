package com.ge.apm.domain;
import java.util.Date;

public class SearchCondition {
    private String fieldName = "";
    private String operator = "LIKE";
    private String value = "";
    private String value2 = "";
    private Date dateValue;
    private Date dateValue2;

    public SearchCondition() {}

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getValue2() { return value2; }
    public void setValue2(String value2) { this.value2 = value2; }

    public Date getDateValue() { return dateValue; }
    public void setDateValue(Date dateValue) { this.dateValue = dateValue; }

    public Date getDateValue2() { return dateValue2; }
    public void setDateValue2(Date dateValue2) { this.dateValue2 = dateValue2; }
}
