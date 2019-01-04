package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "role_authority")
public class RoleAuthority implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "sys_auth_id")
    private Integer sysAuthId;

    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name="site_uid",columnDefinition = "CHAR(32)")
    private String siteUID;

    @CreatedBy
    @Column(name = "created_by", nullable = true, length = 50, updatable = false)
    @JsonIgnore
    private String createdBy = "system";

    @CreatedDate
    @Column(name = "created_date", nullable = true)
    @Temporal(TemporalType.DATE)
    @JsonIgnore
    private Date createdDate = new Date();

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    @JsonIgnore
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    @Temporal(TemporalType.DATE)
    @JsonIgnore
    private Date lastModifiedDate = new Date();

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getInstitutionUID() {
        return institutionUID;
    }

    public void setInstitutionUID(String institutionUID) {
        this.institutionUID = institutionUID;
    }

    public String getHospitalUID() {
        return hospitalUID;
    }

    public void setHospitalUID(String hospitalUID) {
        this.hospitalUID = hospitalUID;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSysAuthId() {
        return sysAuthId;
    }

    public void setSysAuthId(Integer sysAuthId) {
        this.sysAuthId = sysAuthId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoleAuthority)) {
            return false;
        }
        RoleAuthority other = (RoleAuthority) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
}
