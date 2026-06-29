package com.ge.apm.domain.pure;

import java.util.Date;

public class AssetRuleModel {
	private String uid;
	private String assetName;
	private Integer level;
	private Integer period;
	private String periodUnit;//保养周期单位 /y 、m 、w、d  /年 月 周 日
	private Date planTime;
	private Integer pmType;
	private Integer measuringType;
	private String pmRuleUid;
	private String pmRuleName;
	private String uniqueUid;
    private String ruleScore;

    public String getRuleScore() {
        return ruleScore;
    }

    public void setRuleScore(String ruleScore) {
        this.ruleScore = ruleScore;
    }

    public Integer getMeasuringType() {
		return measuringType;
	}

	public void setMeasuringType(Integer measuringType) {
		this.measuringType = measuringType;
	}

	public Integer getPmType() {
		return pmType;
	}

	public void setPmType(Integer pmType) {
		this.pmType = pmType;
	}

	public Date getPlanTime() {
		return planTime;
	}
	public void setPlanTime(Date planTime) {
		this.planTime = planTime;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}

	public String getPeriodUnit() {
		return periodUnit;
	}

	public void setPeriodUnit(String periodUnit) {
		this.periodUnit = periodUnit;
	}

	public String getPmRuleUid() {
		return pmRuleUid;
	}

	public void setPmRuleUid(String pmRuleUid) {
		this.pmRuleUid = pmRuleUid;
	}

	public String getPmRuleName() {
		return pmRuleName;
	}

	public void setPmRuleName(String pmRuleName) {
		this.pmRuleName = pmRuleName;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getUniqueUid() {
		return uid+pmRuleUid;
	}

	public void setUniqueUid(String uniqueUid) {
		this.uniqueUid = uniqueUid;
	}
}
