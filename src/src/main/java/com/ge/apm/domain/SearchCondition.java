package com.ge.apm.domain;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchCondition {
    private String fieldName = "";
    private String operator = "LIKE";
    private String value = "";
    private String value2 = "";
    private Date dateValue;
    private Date dateValue2;
    private List<String> enumValues;       // 枚举多选值
    private String selectedRangeLabel;     // 选中的区间标签

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

    public List<String> getEnumValues() {
        if (enumValues == null) {
            enumValues = new ArrayList<>();
        }
        return enumValues;
    }
    public void setEnumValues(List<String> enumValues) { this.enumValues = enumValues; }

    public String getSelectedRangeLabel() { return selectedRangeLabel; }
    public void setSelectedRangeLabel(String selectedRangeLabel) { this.selectedRangeLabel = selectedRangeLabel; }
}
