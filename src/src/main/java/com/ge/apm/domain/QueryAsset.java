package com.ge.apm.domain;

public class QueryAsset {
	private String tenantUID;
	private String name;
	private String functionType;//设备型号
	private Integer assetGroup;//设备类型
	private String manufacture;//品牌
	private Integer ruleStatus;//规则状态 0：全部 1、已配置 2、未配置
	private Integer level;
	//private Integer clinicalDeptId;//使用科室
	private String brand;//品牌
    private String clinicalDeptUid;//使用科室uid

    public String getClinicalDeptUid() {
        return clinicalDeptUid;
    }

    public void setClinicalDeptUid(String clinicalDeptUid) {
        this.clinicalDeptUid = clinicalDeptUid;
    }

    public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getRuleStatus() {
		return ruleStatus;
	}
	public void setRuleStatus(Integer ruleStatus) {
		this.ruleStatus = ruleStatus;
	}
	public String getTenantUID() {
		return tenantUID;
	}
	public void setTenantUID(String tenantUID) {
		this.tenantUID = tenantUID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFunctionType() {
		return functionType;
	}
	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}
	public Integer getAssetGroup() {
		return assetGroup;
	}
	public void setAssetGroup(Integer assetGroup) {
		this.assetGroup = assetGroup;
	}
	public String getManufacture() {
		return manufacture;
	}
	public void setManufacture(String manufacture) {
		this.manufacture = manufacture;
	}
	
	
}
