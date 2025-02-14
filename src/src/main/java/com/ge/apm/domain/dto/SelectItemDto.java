package com.ge.apm.domain.dto;

import lombok.Data;

@Data
public class SelectItemDto {
    private String label;
    private String value;

    public SelectItemDto(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
