package com.ge.apm.service.uaa;

public class OrgInfoVO {

    private String name;

    private String uid;

    private String parentUID;
    private OrgInfoVO parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public OrgInfoVO getParent() {
        return parent;
    }

    public void setParent(OrgInfoVO parent) {
        this.parent = parent;
    }

    public String getParentUID() {
        return parentUID;
    }

    public void setParentUID(String parentUID) {
        this.parentUID = parentUID;
    }
}
