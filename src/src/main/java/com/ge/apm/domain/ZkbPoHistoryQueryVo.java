package com.ge.apm.domain;

/**
 * @author : 503189362
 * @date : 2020/11/17
 * @description :
 */
public class ZkbPoHistoryQueryVo {

    private String id;
    private String systemId;
    private String srNum;
    private String partOrderNo;
    private String status;
    private String currentStep;
    private String partNo;
    private String partQty;
    private String partRemark;
    private String partName;

    private String unrealStatus;
    private String reason;

    public String getUnrealStatus() {
        return unrealStatus;
    }

    public void setUnrealStatus(String unrealStatus) {
        this.unrealStatus = unrealStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartRemark() {
        return partRemark;
    }

    public void setPartRemark(String partRemark) {
        this.partRemark = partRemark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSrNum() {
        return srNum;
    }

    public void setSrNum(String srNum) {
        this.srNum = srNum;
    }

    public String getPartOrderNo() {
        return partOrderNo;
    }

    public void setPartOrderNo(String partOrderNo) {
        this.partOrderNo = partOrderNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public String getPartQty() {
        return partQty;
    }

    public void setPartQty(String partQty) {
        this.partQty = partQty;
    }
}
