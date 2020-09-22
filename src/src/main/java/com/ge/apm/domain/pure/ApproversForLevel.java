package com.ge.apm.domain.pure;

import com.alibaba.fastjson.annotation.JSONField;
import com.ge.apm.domain.UserAccount;

import java.io.Serializable;
import java.util.List;

public class ApproversForLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer level; //级别

    private boolean usingRole; //是否启用角色

    @JSONField(serialize = false)
    List<UserAccount> approverAccounts; //级别对应审批人

    List<String> approvers;

    List<String> roles;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<String> getApprovers() {
        return approvers;
    }

    public void setApprovers(List<String> approvers) {
        this.approvers = approvers;
    }

    public List<UserAccount> getApproverAccounts() {
        return approverAccounts;
    }

    public void setApproverAccounts(List<UserAccount> approverAccounts) {
        this.approverAccounts = approverAccounts;
    }

    public boolean getUsingRole() {
        return usingRole;
    }

    public void setUsingRole(boolean usingRole) {
        this.usingRole = usingRole;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
