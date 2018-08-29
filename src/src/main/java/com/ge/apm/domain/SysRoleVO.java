package com.ge.apm.domain;

/**
 * Created by 212605082 on 2018/5/16.
 */
public class SysRoleVO {
    private Integer id;
    private String name;
    private String roleDesc;
    private Boolean isDisable = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public Boolean getDisable() {
        return isDisable;
    }

    public void setDisable(Boolean disable) {
        isDisable = disable;
    }
}
