package com.ge.apm.domain.pure;

import java.util.Date;

/**
 * Created by 212605082 on 2019/3/19.
 */
public class WorkOrderDetailPojo {

    //ID
    private String id;
    //科室
    private String assetClinicalDeptName;
    //设备编号
    private String assetDepartNum;
    //资产编号
    private String assetFinancingNum;
    //制造商序列号
    private String serialNum;
    //设备名称
    private String assetName;
    //供货单位名称
    private String supplierName;
    //配件名称
    private String partsName;
    //配件数量
    private Integer partsQuantity;
    //发生费用
    private Double closedPrice;
    //发票号
    private String invoiceNum;
    //开票日期
    private Date invoiceTime;
    //审批人
    private String approverName;
    //合同号
    private String contractNum;
    //备注
    private String comment;
    //结单状态
    private Boolean statementStatus;
    //备件类别
    private String partsType;
    //审核状态
    private Boolean approved;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetClinicalDeptName() {
        return assetClinicalDeptName;
    }

    public void setAssetClinicalDeptName(String assetClinicalDeptName) {
        this.assetClinicalDeptName = assetClinicalDeptName;
    }

    public String getAssetFinancingNum() {
        return assetFinancingNum;
    }

    public void setAssetFinancingNum(String assetFinancingNum) {
        this.assetFinancingNum = assetFinancingNum;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPartsName() {
        return partsName;
    }

    public void setPartsName(String partsName) {
        this.partsName = partsName;
    }

    public Integer getPartsQuantity() {
        return partsQuantity;
    }

    public void setPartsQuantity(Integer partsQuantity) {
        this.partsQuantity = partsQuantity;
    }

    public Double getClosedPrice() {
        return closedPrice;
    }

    public void setClosedPrice(Double closedPrice) {
        this.closedPrice = closedPrice;
    }

    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public Date getInvoiceTime() {
        return invoiceTime;
    }

    public void setInvoiceTime(Date invoiceTime) {
        this.invoiceTime = invoiceTime;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getContractNum() {
        return contractNum;
    }

    public void setContractNum(String contractNum) {
        this.contractNum = contractNum;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getStatementStatus() {
        return statementStatus;
    }

    public void setStatementStatus(Boolean statementStatus) {
        this.statementStatus = statementStatus;
    }

    public String getAssetDepartNum() {
        return assetDepartNum;
    }

    public void setAssetDepartNum(String assetDepartNum) {
        this.assetDepartNum = assetDepartNum;
    }

    public String getPartsType() {
        return partsType;
    }

    public void setPartsType(String partsType) {
        this.partsType = partsType;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }
}
