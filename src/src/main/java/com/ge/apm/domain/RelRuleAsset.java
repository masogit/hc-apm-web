package com.ge.apm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
