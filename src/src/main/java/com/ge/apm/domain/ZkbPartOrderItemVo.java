package com.ge.apm.domain;

import java.io.Serializable;

/**
 * @author : 503189362
 * @date : 2020/10/12
 * @description :
 */
public class ZkbPartOrderItemVo implements Serializable {

    private String partNo;
    private String partName;
    private int partQty;
    private String serialNumber;
    private String orderNo;
    private String price;
    private Integer prePartQty;
    private String remark;

    private String partOrderNo;
    private String currentStep;
    private String status;
    private String srNum;

    public ZkbPartOrderItemVo(String partNo, String partName, int partQty, String serialNumber, String orderNo, String price, String remark, String partOrderNo, String currentStep, String status, String srNum,Integer prePartQty) {
        this.partNo = partNo;
        this.partName = partName;
        this.partQty = partQty;
        this.serialNumber = serialNumber;
        this.orderNo = orderNo;
        this.price = price;
        this.remark = remark;
        this.partOrderNo = partOrderNo;
        this.currentStep = currentStep;
        this.status = status;
        this.srNum = srNum;
        this.prePartQty = prePartQty;
    }

    public Integer getPrePartQty() {
        return prePartQty;
    }

    public void setPrePartQty(Integer prePartQty) {
        this.prePartQty = prePartQty;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSrNum() {
        return srNum;
    }

    public void setSrNum(String srNum) {
        this.srNum = srNum;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public int getPartQty() {
        return partQty;
    }

    public void setPartQty(int partQty) {
        this.partQty = partQty;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPartOrderNo() {
        return partOrderNo;
    }

    public void setPartOrderNo(String partOrderNo) {
        this.partOrderNo = partOrderNo;
    }
}
