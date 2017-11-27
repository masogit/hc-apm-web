package com.ge.apm.domain.pure;

import java.io.Serializable;

public class AssetRule implements Serializable{

	private static final long serialVersionUID = 1L;
	private String assetUID;
	private String assetName;
	private String ruleUID;
	private String ruleName;
	
	
	public AssetRule(String assetUID, String assetName, String ruleUID, String ruleName) {
		super();
		this.assetUID = assetUID;
		this.assetName = assetName;
		this.ruleUID = ruleUID;
		this.ruleName = ruleName;
	}
	public String getAssetUID() {
		return assetUID;
	}
	public void setAssetUID(String assetUID) {
		this.assetUID = assetUID;
	}
	public String getAssetName() {
		return assetName;
	}
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}
	public String getRuleUID() {
		return ruleUID;
	}
	public void setRuleUID(String ruleUID) {
		this.ruleUID = ruleUID;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	
}
