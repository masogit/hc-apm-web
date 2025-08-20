package com.ge.apm.domain.dto;

import lombok.Data;

@Data
public class FieldColumnDto {
    // 是否显示
    private Boolean rendered;
    // 标签名称
    private String label;
    // 是否必填
    private Boolean required;
    // 必填提示
    private String requiredMessage;
    // 日期格式
    private String datePattern;
    // 字段类型
    private String type;
    private Integer entryType;
    private Double hours;
}
