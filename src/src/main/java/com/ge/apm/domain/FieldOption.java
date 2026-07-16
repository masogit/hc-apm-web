package com.ge.apm.domain;

public class FieldOption {
    private String value;
    private String label;
    private String fieldType;

    public FieldOption(String value, String label, String fieldType) {
        this.value = value;
        this.label = label;
        this.fieldType = fieldType;
    }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
}
