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

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "owner_config_type")
    private String ownerConfigType;

    @Column(name = "dept_uid")
    private String deptUID;

    @JoinColumn(insertable=false,updatable=false, name = "dept_uid", referencedColumnName = "uid")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private OrgInfo deptOrgInfo;

    @Column(name = "assetGroup_id")
    private Integer assetGroupID;

    @Column(name = "assetGroupSecondary_id")
    private Integer assetGroupSecondaryID;

    @Column(name = "woOwner_uid")
    private String woOwnerUID;

    @Column(name = "woOwner_uid2")
    private String woOwnerUID2;

    @Column(name = "woOwner_uid3")
    private String woOwnerUID3;

    @Column(name = "pmOwner_uid")
    private String pmOwnerUID;

    @JoinColumn(insertable=false,updatable=false, name = "woOwner_uid", referencedColumnName = "uid")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private UserAccount woOwner;

    @JoinColumn(insertable=false,updatable=false, name = "woOwner_uid2", referencedColumnName = "uid")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private UserAccount woOwner2;

    @JoinColumn(insertable=false,updatable=false, name = "woOwner_uid3", referencedColumnName = "uid")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private UserAccount woOwner3;

    @JoinColumn(insertable=false,updatable=false, name = "pmOwner_uid", referencedColumnName = "uid")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private UserAccount pmOwner;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @JoinColumn(insertable=false,updatable=false, name = "site_uid", referencedColumnName = "uid")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private OrgInfo siteOrgInfo;

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

    public String getDeptUID() {
        return deptUID;
    }

    public void setDeptUID(String deptUID) {
        this.deptUID = deptUID;
    }

    public OrgInfo getDeptOrgInfo() {
        return deptOrgInfo;
    }

    public void setDeptOrgInfo(OrgInfo deptOrgInfo) {
        this.deptOrgInfo = deptOrgInfo;
    }

    public Integer getAssetGroupID() {
        return assetGroupID;
    }

    public void setAssetGroupID(Integer assetGroupID) {
        this.assetGroupID = assetGroupID;
    }

    public Integer getAssetGroupSecondaryID() {
        return assetGroupSecondaryID;
    }

    public void setAssetGroupSecondaryID(Integer assetGroupSecondaryID) {
        this.assetGroupSecondaryID = assetGroupSecondaryID;
    }

    public String getWoOwnerUID() {
        return woOwnerUID;
    }

    public void setWoOwnerUID(String woOwnerUID) {
        this.woOwnerUID = woOwnerUID;
    }

    public String getWoOwnerUID2() {
        return woOwnerUID2;
    }

    public void setWoOwnerUID2(String woOwnerUID2) {
        this.woOwnerUID2 = woOwnerUID2;
    }

    public String getWoOwnerUID3() {
        return woOwnerUID3;
    }

    public void setWoOwnerUID3(String woOwnerUID3) {
        this.woOwnerUID3 = woOwnerUID3;
    }

    public String getPmOwnerUID() {
        return pmOwnerUID;
    }

    public void setPmOwnerUID(String pmOwnerUID) {
        this.pmOwnerUID = pmOwnerUID;
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

    public OrgInfo getSiteOrgInfo() {
        return siteOrgInfo;
    }

    public void setSiteOrgInfo(OrgInfo siteOrgInfo) {
        this.siteOrgInfo = siteOrgInfo;
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

    public UserAccount getWoOwner() {
        return woOwner;
    }

    public void setWoOwner(UserAccount woOwner) {
        this.woOwner = woOwner;
    }

    public UserAccount getWoOwner2() {
        return woOwner2;
    }

    public void setWoOwner2(UserAccount woOwner2) {
        this.woOwner2 = woOwner2;
    }

    public UserAccount getWoOwner3() {
        return woOwner3;
    }

    public void setWoOwner3(UserAccount woOwner3) {
        this.woOwner3 = woOwner3;
    }

    public UserAccount getPmOwner() {
        return pmOwner;
    }

    public void setPmOwner(UserAccount pmOwner) {
        this.pmOwner = pmOwner;
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
        if (!(object instanceof OwnerConfig)) {
            return false;
        }
        OwnerConfig other = (OwnerConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.MeasuringTypeConfig[ id=" + id + " ]";
    }
}
