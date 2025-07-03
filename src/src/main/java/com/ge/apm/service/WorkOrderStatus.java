package com.ge.apm.service;

public enum WorkOrderStatus {
	CREATE(1,"报修", "Create"),DISPATER(2,"待派工", "Assign"),ACCEPT(3,"待接单", "Accept"),REPAIR(4," 维修中", "Repair"),
	CONFIRM(5,"待验收", "Ack"),CLOSE(6,"待关单", "Close"),EVALUATE(7,"待评价", "Rate"),APPROVAL(8, "待审核", "Approval"), MAINTAIN(0, "已计划", "Maintain"),
	ACCEPTANCE(11,"待验收","Acceptance");

	public static final int SPECIAL_DISPATCHER = 1;// 专人派工 work_order_dispather角色
	public static final int GRAB_DISPATCHER = 2;// 抢单 asset_staff 角色
	public static final int AUTO_DISPATCHER = 3;// 资产owner 1 2
	
	private Integer stepId;
	private String stepName;
	private String stepEnName;
	
	private WorkOrderStatus(Integer stepId, String stepName) {
		this.stepId = stepId;
		this.stepName = stepName;
	}
	private WorkOrderStatus(Integer stepId, String stepName, String stepEnName) {
		this.stepId = stepId;
		this.stepName = stepName;
		this.stepEnName = stepEnName;
	}
	public Integer getStepId() {
		return stepId;
	}
	public void setStepId(Integer stepId) {
		this.stepId = stepId;
	}
	public String getStepName() {
		return stepName;
	}
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getStepEnName() {
		return stepEnName;
	}

	public void setStepEnName(String stepEnName) {
		this.stepEnName = stepEnName;
	}

	public static String getStepName(Integer stepId){
		if(stepId == null){
			return "";
		}
		for(WorkOrderStatus wos:WorkOrderStatus.values()){
			if(wos.getStepId().equals(stepId)){
				return wos.getStepName();
			}
		}
		return "";
	}

	public static String getStepName(String stepEnName){
		if(stepEnName == null){
			return "";
		}
		for(WorkOrderStatus wos:WorkOrderStatus.values()){
			if(wos.getStepEnName().equals(stepEnName)){
				return wos.getStepName();
			}
		}
		return "";
	}
	
}
