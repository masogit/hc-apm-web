package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.ge.apm.service.utils.ChineseName;
import com.ge.apm.service.utils.PinyinUtil;

@Entity
@Table(name = "attribute")
public class Attribute implements ChineseName,Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "attribute_uid", nullable = false)
    private String attributeUid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @CreatedBy
    @Column(name = "create_by", nullable = false)
    private String createBy;

    @CreatedDate
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @LastModifiedBy
    @Column(name = "update_by", nullable = false)
    private String updateBy;

    @LastModifiedDate
    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="attribute_uid")
    private Set<AttrDesc> attrDescSet;

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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Set<AttrDesc> getAttrDescSet() {
        return attrDescSet;
    }

    public void setAttrDescSet(Set<AttrDesc> attrDescSet) {
        this.attrDescSet = attrDescSet;
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
