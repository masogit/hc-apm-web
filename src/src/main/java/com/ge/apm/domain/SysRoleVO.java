package com.ge.apm.domain;

/**
 * Created by 212605082 on 2018/5/16.
 */
public class SysRoleVO {
    private Integer id;
    private Integer index;
    private String name;
    private String roleDesc;
    private Boolean isDisable = false;
    private Integer roleType;// 0 means sys role 1 means custom role
    private String  roleDescEn;

    public String getRoleDescEn() {
        return roleDescEn;
    }

    public void setRoleDescEn(String roleDescEn) {
        this.roleDescEn = roleDescEn;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
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
