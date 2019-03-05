package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 212605082 on 2019/2/25.
 */

@Entity
@Table(name = "owner_config")
public class OwnerConfig implements Serializable {

    public enum OwnerConfigType {
        Department, AssetGroup
    }

    public enum OwnerType {
        WO, PM
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "owner_config_type")
    private String ownerConfigType;

    @Column(name = "owner_type")
    private String ownerType;

    @Column(name = "type_uid")
    private String typeUID;

    @Column(name = "owner_uid")
    private String ownerUID;

    @JoinColumn(insertable=false,updatable=false, name = "owner_uid", referencedColumnName = "uid")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private UserAccount owner;

    @Column(name = "index")
    private Integer index;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "created_by", nullable = true, length = 50)
    private String createdBy;

    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    @Column(name = "last_modified_by", nullable = true, length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerConfigType() {
        return ownerConfigType;
    }

    public void setOwnerConfigType(String ownerConfigType) {
        this.ownerConfigType = ownerConfigType;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public String getTypeUID() {
        return typeUID;
    }

    public void setTypeUID(String typeUID) {
        this.typeUID = typeUID;
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

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

    public UserAccount getOwner() {
        return owner;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }
}
