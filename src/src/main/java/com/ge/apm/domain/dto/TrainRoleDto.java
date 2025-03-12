package com.ge.apm.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TrainRoleDto implements Serializable {

    private String id;
    private String uid;
    private String roleName;
    private Boolean selected;


}
