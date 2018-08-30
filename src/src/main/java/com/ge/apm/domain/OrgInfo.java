/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ge.apm.service.utils.ChineseName;
import com.ge.apm.service.utils.PinyinUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import webapp.framework.web.WebUtil;

/**
 * @author 212547631
 */
@Entity
@Table(name = "org_info")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
public class OrgInfo implements Serializable, ChineseName {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(columnDefinition = "CHAR(32)")
    private String uid;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    @Size(max = 32)
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "parent_uid", columnDefinition = "CHAR(32)")
    private String parentUID;

    @Column(name = "org_level")
    private Integer orgLevel;

    @Column(name = "org_type")
    private Integer orgType;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Size(max = 64)
    @Column(name = "name_en")
    private String nameEn;
    @Column(name = "name_py_code")
    private String namePyCode;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "short_name")
    private String shortName;
    @Column(name = "short_name_py_code")
    private String shortNamePyCode;

    @JoinColumn(name = "parent_uid", referencedColumnName = "uid", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private OrgInfo parentOrg;

    @Column(name = "hospital_id")
    private Integer hospitalId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private Integer siteId;

    @Size(max = 32)
    @Column(name = "building_num")
    private String buildingNum;

    @Size(max = 32)
    @Column(name = "floor_num")
    private String floorNum;

    @Size(max = 32)
    @Column(name = "other")
    private String other;

    @Transient
    private int lineNum;

    @PrePersist
    public void initializeUid() {
        if (uid == null)
            uid = UUID.randomUUID().toString().replace("-", "");
    }

    @Column(name = "asset_owner_id")
    private Integer assetOwnerId;
    @Column(name = "asset_owner_id2")
    private Integer assetOwnerId2;

    public OrgInfo() {
    }

    public OrgInfo(String name) {
        this.name = name;
    }

    public OrgInfo(String uid, String name, Integer orgType, String parentUID) {
        this.uid = uid;
        this.name = name;
        this.orgType = orgType;
        this.parentUID = parentUID;
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

    public String getParentUID() {
        return parentUID;
    }

    public void setParentUID(String parentUID) {
        this.parentUID = parentUID;
    }

    public Integer getOrgLevel() {
        return orgLevel;
    }

    public void setOrgLevel(Integer orgLevel) {
        this.orgLevel = orgLevel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.namePyCode = name == null ? null : PinyinUtil.getShortPinyin(name).toLowerCase().replaceAll("[^0-9a-zA-Z]", "");
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public OrgInfo getParentOrg() {
        return parentOrg;
    }

    public void setParentOrg(OrgInfo parentOrg) {
        this.parentOrg = parentOrg;
    }

    @JsonIgnore
    public OrgInfo getHospital() {
        OrgInfo org = this;
        while (org != null) {
            if (org.getParentOrg() != null)
                org = org.getParentOrg();
            else
                break;
        }

        return org;
    }

    public Integer getHospitalId() {
        if (hospitalId == null)
            return id;
        else
            return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }

    public String getNamePyCode() {
        return namePyCode;
    }

    public void setNamePyCode(String namePyCode) {
        this.namePyCode = namePyCode;
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
        if (!(object instanceof OrgInfo)) {
            return false;
        }
        OrgInfo other = (OrgInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getOrgTypeName() {
        if (this.orgType == null) return "";

        if (this.orgType.equals(1)) return WebUtil.getMessage("Institution");
        else if (this.orgType.equals(2)) return WebUtil.getMessage("Hospital");
        else if (this.orgType.equals(3)) return WebUtil.getMessage("Site");
        else if (this.orgType.equals(4)) return WebUtil.getMessage("Department");
        else return "";
    }

    public Integer getAssetOwnerId() {
        return assetOwnerId;
    }

    public void setAssetOwnerId(Integer assetOwnerId) {
        this.assetOwnerId = assetOwnerId;
    }

    public Integer getAssetOwnerId2() {
        return assetOwnerId2;
    }

    public void setAssetOwnerId2(Integer assetOwnerId2) {
        this.assetOwnerId2 = assetOwnerId2;
    }

    public String getOrgFullName() {
        String name = this.name;

        OrgInfo org = this;
        while (org.getParentOrg() != null) {
            String oName = org.getName();
            org = org.getParentOrg();
            String pName = org.getName();

            if (!(pName.contains(oName) || oName.contains(pName) || name.contains(pName)))
                name = org.getName() + " " + name;
        }

        return name;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public String getPinYin() {
        return namePyCode;
    }

    public String getBuildingNum() {
        return buildingNum;
    }

    public void setBuildingNum(String buildingNum) {
        this.buildingNum = buildingNum;
    }

    public String getFloorNum() {
        return floorNum;
    }

    public void setFloorNum(String floorNum) {
        this.floorNum = floorNum;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
        this.shortNamePyCode = shortName == null ? null : PinyinUtil.getShortPinyin(shortName).toLowerCase().replaceAll("[^0-9a-zA-Z]", "");

    }
}
