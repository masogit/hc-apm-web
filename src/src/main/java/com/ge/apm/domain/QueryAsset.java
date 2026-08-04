package com.ge.apm.domain;

import java.util.Date;

public class QueryAsset {
	private Date installDateStart;
	private Date installDateEnd;
	private Date startInstallDate;
	private Date endInstallDate;

	private Date purchaseDateStart;
	private Date purchaseDateEnd;
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
	private String queryLevel;//查询规则级别
    private Double purchasePriceStart;//采购价格(元)Start
    private Double purchasePriceEnd;//采购价格(元)end
	private boolean generate;
	private Integer riskGrade;
 	private String mainDirectory;
 	private String secondDirectory;
	private Integer emergencyEquipment;
	private Date warrantyDateStart;
	private Date warrantyDateEnd;


	public String getMainDirectory() {
		return mainDirectory;
	}

	public void setMainDirectory(String mainDirectory) {
		this.mainDirectory = mainDirectory;
	}

	public String getSecondDirectory() {
		return secondDirectory;
	}

	public void setSecondDirectory(String secondDirectory) {
		this.secondDirectory = secondDirectory;
	}

	public Integer getRiskGrade() { return riskGrade; }

	public void setRiskGrade(Integer riskGrade) { this.riskGrade = riskGrade; }

	public Double getPurchasePriceStart() {
        return purchasePriceStart;
    }

    public void setPurchasePriceStart(Double purchasePriceStart) {
        this.purchasePriceStart = purchasePriceStart;
    }

    public Double getPurchasePriceEnd() {
        return purchasePriceEnd;
    }

    public void setPurchasePriceEnd(Double purchasePriceEnd) {
        this.purchasePriceEnd = purchasePriceEnd;
    }

	public String getQueryLevel() {
		return queryLevel;
	}

	public void setQueryLevel(String queryLevel) {
		this.queryLevel = queryLevel;
	}

	public String getClinicalDeptUid() {
        return clinicalDeptUid;
    }

	public Date getInstallDateStart() {
		return installDateStart;
	}

	public void setInstallDateStart(Date installDateStart) {
		this.installDateStart = installDateStart;
	}

	public Date getInstallDateEnd() {
		return installDateEnd;
	}

	public void setInstallDateEnd(Date installDateEnd) {
		this.installDateEnd = installDateEnd;
	}

	public Date getPurchaseDateStart() {
		return purchaseDateStart;
	}

	public void setPurchaseDateStart(Date purchaseDateStart) {
		this.purchaseDateStart = purchaseDateStart;
	}

	public Date getPurchaseDateEnd() {
		return purchaseDateEnd;
	}

	public void setPurchaseDateEnd(Date purchaseDateEnd) {
		this.purchaseDateEnd = purchaseDateEnd;
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

	public Date getStartInstallDate() {
		return startInstallDate;
	}

	public void setStartInstallDate(Date startInstallDate) {
		this.startInstallDate = startInstallDate;
	}

	public Date getEndInstallDate() {
		return endInstallDate;
	}

	public void setEndInstallDate(Date endInstallDate) {
		this.endInstallDate = endInstallDate;
	}

	public boolean getGenerate() {
		return generate;
	}

	public void setGenerate(boolean generate) {
		this.generate = generate;
	}


	public Integer getEmergencyEquipment() {
		return emergencyEquipment;
	}

	public void setEmergencyEquipment(Integer emergencyEquipment) {
		this.emergencyEquipment = emergencyEquipment;
	}


	public Date getWarrantyDateStart() {
		return warrantyDateStart;
	}

	public void setWarrantyDateStart(Date warrantyDateStart) {
		this.warrantyDateStart = warrantyDateStart;
	}

	public Date getWarrantyDateEnd() {
		return warrantyDateEnd;
	}

	public void setWarrantyDateEnd(Date warrantyDateEnd) {
		this.warrantyDateEnd = warrantyDateEnd;
	}
}
