package com.ge.apm.domain.pure;

import java.util.Date;

public class AssetRuleModel {
	private String uid;
	private Integer level;
	private Integer period;
	private Date planTime;
	private Integer pmType;

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
	
}
