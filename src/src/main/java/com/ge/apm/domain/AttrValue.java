package com.ge.apm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "attr_value")
public class AttrValue implements Serializable {

    @Id
    @Column(name = "attr_value_uid", nullable = false)
    private String attrValueUid;

    @Column(name = "attr_desc_uid", nullable = false)
    private String attrDescUid;

    @Column(name = "attribute_uid", nullable = false)
    private String attributeUid;

    @Column(name = "value")
    private String value;

    public String getAttrValueUid() {
        return attrValueUid;
    }

    public void setAttrValueUid(String attrValueUid) {
        this.attrValueUid = attrValueUid;
    }

    public String getAttrDescUid() {
        return attrDescUid;
    }

    public void setAttrDescUid(String attrDescUid) {
        this.attrDescUid = attrDescUid;
    }

    public String getAttributeUid() {
        return attributeUid;
    }

    public void setAttributeUid(String attributeUid) {
        this.attributeUid = attributeUid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
