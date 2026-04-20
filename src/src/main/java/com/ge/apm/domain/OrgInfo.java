/*
 */
package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ge.apm.service.utils.ChineseName;
import com.ge.apm.service.utils.PinyinUtil;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import webapp.framework.web.WebUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

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

    @Column(name = "parent_id")
    private Integer parentID;
    
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

    @Column(name = "short_name")
    private String shortName;
    
    @Column(name = "short_name_py_code")
    private String shortNamePyCode;

    @JoinColumn(name = "parent_id", referencedColumnName = "id", insertable = false, updatable = false)
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

    @Column(name = "org_code")
    private String orgCode;
    @Column(name = "org_index")
    private Integer orgIndex = 0;

    @Transient
    private int lineNum;

    @PrePersist
    public void initializeUid() {
        if (uid == null)
            uid = UUID.randomUUID().toString().replace("-", "");
        
        if(this.parentOrg!=null) 
            this.parentID = parentOrg.getId();
    }

    @Column(name = "asset_owner_id")
    private Integer assetOwnerId;
    @Column(name = "asset_owner_id2")
    private Integer assetOwnerId2;

    @Column(name = "hospital_ucmid")
    private String hospitalUcmid;

    @Column(name = "status")
    private Integer status;

    @Transient
    private String orgUri;

    @Column(name = "address")
    private String address;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Transient
    private String assetType;

    @Transient
    private String orgName;

    @Column(name = "grade_nature")
    private String gradeNature;
    @Column(name = "region")
    private String region;

    @Column(name = "spare01")
    private String spare01;

    @Column(name = "spare02")
    private String spare02;

    @Column(name = "spare03")
    private String spare03;

    @Column(name = "spare04")
    private String spare04;

    @Column(name = "spare05")
    private String spare05;
    public String getAssetType() {
        return assetType == null ? "" : assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

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

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
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

    public Integer getParentID() {
        return parentID;
    }

    public void setParentID(Integer parentID) {
        this.parentID = parentID;
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

    public String getHospitalUcmid() {
        return hospitalUcmid;
    }

    public void setHospitalUcmid(String hospitalUcmid) {
        this.hospitalUcmid = hospitalUcmid;
    }

    public String getShortNamePyCode() {
        return shortNamePyCode;
    }

    public void setShortNamePyCode(String shortNamePyCode) {
        this.shortNamePyCode = shortNamePyCode;
    }

    public String getOrgUri() {
        return orgUri;
    }

    public void setOrgUri(String orgUri) {
        this.orgUri = orgUri;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getGradeNature() {
        return gradeNature;
    }

    public void setGradeNature(String gradeNature) {
        this.gradeNature = gradeNature;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSpare01() {
        return spare01;
    }

    public void setSpare01(String spare01) {
        this.spare01 = spare01;
    }

    public String getSpare02() {
        return spare02;
    }

    public void setSpare02(String spare02) {
        this.spare02 = spare02;
    }

    public String getSpare03() {
        return spare03;
    }

    public void setSpare03(String spare03) {
        this.spare03 = spare03;
    }

    public String getSpare04() {
        return spare04;
    }

    public void setSpare04(String spare04) {
        this.spare04 = spare04;
    }

    public String getSpare05() {
        return spare05;
    }

    public void setSpare05(String spare05) {
        this.spare05 = spare05;
    }

    public Integer getOrgIndex() {
        return orgIndex;
    }

    public void setOrgIndex(Integer orgIndex) {
        this.orgIndex = orgIndex;
    }

    public OrgInfo(Integer id) {
		super();
		this.id = id;
	}

	public OrgInfo(Integer id,String uid,String name,String namePyCode,String tenantUID,String institutionUID,String hospitalUID,String siteUID,String parentUID,Integer orgLevel,Integer orgType) {
		super();
		this.id = id;
		this.uid = uid;
		this.tenantUID = tenantUID;
		this.institutionUID = institutionUID;
		this.hospitalUID = hospitalUID;
		this.siteUID = siteUID;
		this.parentUID = parentUID;
		this.orgLevel = orgLevel;
		this.orgType = orgType;
		this.name = name;
		this.namePyCode = namePyCode;
	}
	
	public OrgInfo(Integer id,String uid,String name,String namePyCode,String shortName,String shortNamePyCode,String tenantUID,String institutionUID,String hospitalUID,String siteUID,String parentUID,Integer orgLevel,Integer orgType) {
		super();
		this.id = id;
		this.uid = uid;
		this.name = name;
		this.namePyCode = namePyCode;
		this.shortName=shortName;
		this.shortNamePyCode=shortNamePyCode;
		this.tenantUID = tenantUID;
		this.institutionUID = institutionUID;
		this.hospitalUID = hospitalUID;
		this.siteUID = siteUID;
		this.parentUID = parentUID;
		this.orgLevel = orgLevel;
		this.orgType = orgType;
	}
    
}
