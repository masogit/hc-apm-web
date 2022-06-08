package com.ge.apm.domain.simpleparts;

import java.util.Date;

/**
 * @autor by lyj 503277717
 * @date 2022/5/17
*/

public class SimplePartsReportVo {

    private String id;

    private String partsCode;//备件号

    private String partsName;//备件名称

    private String partsDescribe;//备件描述

    private String partsPrice;//备件价格

    private String partQuantity;//备件数量

    private String workOrderNo;//APM工单号

    private String workOrderStatus;//工单状态

    private String outboundWarehouse;//出库仓库

    private String outboundPosition;//出库仓位

    private Date outboundDate;//出库时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartsCode() {
        return partsCode;
    }

    public void setPartsCode(String partsCode) {
        this.partsCode = partsCode;
    }

    public String getPartsName() {
        return partsName;
    }

    public void setPartsName(String partsName) {
        this.partsName = partsName;
    }

    public String getPartsDescribe() {
        return partsDescribe;
    }

    public void setPartsDescribe(String partsDescribe) {
        this.partsDescribe = partsDescribe;
    }

    public String getPartsPrice() {
        return partsPrice;
    }

    public void setPartsPrice(String partsPrice) {
        this.partsPrice = partsPrice;
    }

    public String getPartQuantity() {
        return partQuantity;
    }

    public void setPartQuantity(String partQuantity) {
        this.partQuantity = partQuantity;
    }

    public String getWorkOrderNo() {
        return workOrderNo;
    }

    public void setWorkOrderNo(String workOrderNo) {
        this.workOrderNo = workOrderNo;
    }

    public String getWorkOrderStatus() {
        if("1".equals(this.workOrderStatus)){
            return "在修";
        }else if ("2".equals(this.workOrderStatus)){
            return "完成";
        }else if ("3".equals(this.workOrderStatus)){
            return "取消";
        }
        return workOrderStatus;
    }

    public void setWorkOrderStatus(String workOrderStatus) {
        this.workOrderStatus = workOrderStatus;
    }

    public String getOutboundWarehouse() {
        return outboundWarehouse;
    }

    public void setOutboundWarehouse(String outboundWarehouse) {
        this.outboundWarehouse = outboundWarehouse;
    }

    public String getOutboundPosition() {
        return outboundPosition;
    }

    public void setOutboundPosition(String outboundPosition) {
        this.outboundPosition = outboundPosition;
    }

    public Date getOutboundDate() {
        return outboundDate;
    }

    public void setOutboundDate(Date outboundDate) {
        this.outboundDate = outboundDate;
    }
}
