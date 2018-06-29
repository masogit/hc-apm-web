package com.ge.apm.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "rel_rule_asset")
public class RelRuleAsset {
	
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
	private String id;
	
	@Column(name = "rule_uid")
	private String ruleUID;
	
	@Column(name = "rule_uid2")
	private String ruleUID2;
	
	@Column(name = "rule_uid1")
	private String ruleUID1;
	
	 @Column(name = "asset_uid")
	private String assetUID;
	 
    @Column(name = "created_date")
	private Date createdDate = new Date();//生成时间
    
    @Column(name = "last_modified_date")
	private Date lastModifiedDate;//修改时间
    
    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;

	@Column(name="institution_uid",columnDefinition = "CHAR(32)")
	private String institutionUID;

	@Column(name="hospital_uid",columnDefinition = "CHAR(32)")
	private String hospitalUID;

	@Column(name="site_uid",columnDefinition = "CHAR(32)")
	private String siteUID;
    
	public String getTenantUID() {
		return tenantUID;
	}

	public String getRuleUID2() {
		return ruleUID2;
	}

	public void setRuleUID2(String ruleUID2) {
		this.ruleUID2 = ruleUID2;
	}

	public String getRuleUID1() {
		return ruleUID1;
	}

	public void setRuleUID1(String ruleUID1) {
		this.ruleUID1 = ruleUID1;
	}

	public void setTenantUID(String tenantUID) {
		this.tenantUID = tenantUID;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getRuleUID() {
		return ruleUID;
	}

	public void setRuleUID(String ruleUID) {
		this.ruleUID = ruleUID;
	}

	public String getAssetUID() {
		return assetUID;
	}

	public void setAssetUID(String assetUID) {
		this.assetUID = assetUID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof RelRuleAsset)) {
			return false;
		}
		RelRuleAsset other = (RelRuleAsset) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}
}
