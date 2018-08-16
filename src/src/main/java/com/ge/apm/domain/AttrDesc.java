package com.ge.apm.domain;

import com.ge.apm.service.utils.ChineseName;
import com.ge.apm.service.utils.PinyinUtil;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "attr_desc")
public class AttrDesc implements ChineseName,Serializable {

    @Id
    @Column(name = "attr_desc_uid", nullable = false)
    private String attrDescUid;

    //mappedto attribute.attribute_uid
    @Column(name = "attribute_uid", nullable = false)
    private String attributeUid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "required", nullable = false)
    private Boolean required;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Override
    public String getPinYin() {
        String pinYin = null;
        if(StringUtils.isNotEmpty(this.name)) {
            pinYin = PinyinUtil.getShortPinyin(this.name).toLowerCase().replaceAll("[^0-9a-zA-Z]", "");
        }
        return pinYin;
    }
}
