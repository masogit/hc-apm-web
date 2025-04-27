package com.ge.apm.domain.dto;

import lombok.Data;

@Data
public class FieldColumnDto {
    private Boolean rendered;
    private String label;
    private Boolean required;
    private String requiredMessage;
    private String datePattern;
    private String type;
}
