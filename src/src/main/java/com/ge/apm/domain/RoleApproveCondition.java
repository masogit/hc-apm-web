package com.ge.apm.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoleApproveCondition implements Serializable {
    private String approveRole; //审核角色  DeptHead   ClinicalStaff
    private Integer condition;//  1按照工单设备所属科室


}

