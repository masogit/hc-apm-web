package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.*;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
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
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name="id", columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "name", columnDefinition = "VARCHAR(64)", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "VARCHAR(255)")
    private String description;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)", nullable = false)
    private String tenantUid;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)", nullable = false)
    private String institutionUid;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)", nullable = false)
    private String hospitalUid;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)", nullable = false)
    private String siteUid;

    @CreatedBy
    @Column(name = "create_by", columnDefinition = "CHAR(32)", nullable = false)
    private String createBy;

    @CreatedDate
    @Column(name = "create_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @LastModifiedBy
    @Column(name = "last_modified_by", columnDefinition = "CHAR(32)", nullable = false)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public String getInstitutionUid() {
        return institutionUid;
    }

    public void setInstitutionUid(String institutionUid) {
        this.institutionUid = institutionUid;
    }

    public String getHospitalUid() {
        return hospitalUid;
    }

    public void setHospitalUid(String hospitalUid) {
        this.hospitalUid = hospitalUid;
    }

    public String getSiteUid() {
        return siteUid;
    }

    public void setSiteUid(String siteUid) {
        this.siteUid = siteUid;
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

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
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
