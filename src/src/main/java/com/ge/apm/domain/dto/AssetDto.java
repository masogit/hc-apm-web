package com.ge.apm.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AssetDto implements Serializable {
    private int id;
    private String userName;
    private String createdBy;

}
