package com.ge.apm.domain;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class  ApprovalNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(serialize = false)
    private String id;

    @JSONField(serialize = false)
    private Integer nodeId; //节点序号

    private String nodeName;  //节点名称

    @JSONField(serialize = false)
    private List<SysRole> approveRoles; //审核角色

    @JSONField(serialize = false)
    private List<UserAccount> approveUsers; //审核人

    private List<Integer> roleUids; //审核角色

    private List<String> userUids; //审核人

    private boolean usingRole; //是否启用角色

    private boolean selectByOrg; //是否通过机构筛选user

    private boolean clinicalDepartmentDirector; //是否启用临床科室主任

    private List<RoleApproveCondition> roleApproveConditions;

    private String approvalStrategy;
//
//    @JSONField(serialize = false)
//    private List<UserAccount> multiLevelApprovalUsers; //多级审核人
//    private List<String> multiLevelApprovalUids; //审核人
//    @JSONField(serialize = false)
//    private List<UserAccount> clinicalDepartmentDirectorUsers; //临床科室主任

    public boolean getSelectByOrg() {
        return selectByOrg;
    }

    public void setSelectByOrg(boolean selectByOrg) {
        this.selectByOrg = selectByOrg;
    }

    public boolean getUsingRole() {
        return usingRole;
    }

    public void setUsingRole(boolean usingRole) {
        this.usingRole = usingRole;
    }


    private RoleApproveCondition roleApproveCondition;


    public boolean isClinicalDepartmentDirector() {
        return clinicalDepartmentDirector;
    }

    public void setClinicalDepartmentDirector(boolean clinicalDepartmentDirector) {
        this.clinicalDepartmentDirector = clinicalDepartmentDirector;
    }

    public List<RoleApproveCondition> getRoleApproveConditions() {
        return roleApproveConditions;
    }

    public void setRoleApproveConditions(List<RoleApproveCondition> roleApproveConditions) {
        this.roleApproveConditions = roleApproveConditions;
    }

    public RoleApproveCondition getRoleApproveCondition() {
        return roleApproveCondition;
    }

    public void setRoleApproveCondition(RoleApproveCondition roleApproveCondition) {
        this.roleApproveCondition = roleApproveCondition;
    }

    public String getApprovalStrategy() {
        return approvalStrategy;
    }

    public void setApprovalStrategy(String approvalStrategy) {
        this.approvalStrategy = approvalStrategy;
    }
}
