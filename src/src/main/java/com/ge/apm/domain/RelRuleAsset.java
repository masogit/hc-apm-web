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
	
	 @Column(name = "asset_uid")
	private String assetUID;
	 
    @Column(name = "created_date")
	private Date createdDate = new Date();//生成时间
    
    @Column(name = "last_modified_date")
	private Date lastModifiedDate;//修改时间
    
    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;
	
/*    @JoinColumn(name = "asset_uid",referencedColumnName = "uid", insertable = false, updatable = false)
    @ManyToMany(fetch = FetchType.EAGER)
    private AssetInfo assetInfo;
    
    @JoinColumn(name = "rule_uid",referencedColumnName = "uid", insertable = false, updatable = false)
    @ManyToMany(fetch = FetchType.EAGER)
    private PmOrderRule pmOrderRule;*/
	 
	public String getTenantUID() {
		return tenantUID;
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
	
    
}
