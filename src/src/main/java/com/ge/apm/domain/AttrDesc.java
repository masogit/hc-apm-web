package com.ge.apm.domain;

import com.ge.apm.service.utils.ChineseName;
import com.ge.apm.service.utils.PinyinUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "attr_desc")
public class AttrDesc implements ChineseName,Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name="id", columnDefinition = "CHAR(32)")
    private String id;

    //mappedto attribute.id
    @Column(name = "attribute_id", columnDefinition = "CHAR(32)", nullable = false)
    private String attributeId;

    @Column(name = "name", columnDefinition = "VARCHAR(64)", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "VARCHAR(255)")
    private String description;

    @Column(name = "required", nullable = false)
    private Boolean required;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
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
