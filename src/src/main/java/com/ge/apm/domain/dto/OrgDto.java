package com.ge.apm.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrgDto implements Serializable {
    private Integer id;
    private String uid;
    private Integer parentId;
    private String orgName;
    private Boolean selected;
    private String assetType;

    public String getAssetType() {
        return assetType == null ? "" : assetType;
    }

}
