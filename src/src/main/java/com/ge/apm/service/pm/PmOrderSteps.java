package com.ge.apm.service.pm;

/**
 * Created by lsg on 17/11/2017.
 */
public class PmOrderSteps {

	String setName;
	String comment;
	String ownerNam;
	String startTime;
	String endTime;
	String stepId;
	String ownerId;

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOwnerNam() {
		return ownerNam;
	}

	public void setOwnerNam(String ownerNam) {
		this.ownerNam = ownerNam;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStepId() {
		return stepId;
	}

	public void setStepId(String stepId) {
		this.stepId = stepId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
}
