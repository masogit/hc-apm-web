package com.ge.apm.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ge.apm.domain.pure.ApprovableTarget;
import com.ge.apm.service.constant.ApprovalConstant;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "asset_department_convert")
public class AssetDepartmentConvert implements Serializable , ApprovableTarget {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column
    private String id;
    @Column(name="asset_uid")
    private String assetUid;
    @Column(name="asset_name")
    private String assetName;//设备名称
    @Column(name="asset_dept_num")
    private String assetDeptNum; //设备编号

    @Column(name = "financing_num")
    private String financingNum;//资产编号

    @Column(name="asset_serial_num")
    private String assetSerialNum; //序列号
    @Column(name = "function_type")
    private String functionType;//设备型号
    @Column(name="asset_qr_code")
    private String assetQrCode; //二维码
    @Column(name="out_dept_uid")
    private String outDeptUid;
    @Column(name="out_dept_name")
    private String outDeptName; //转出科室
    @Column(name="in_dept_uid")
    private String inDeptUid;
    @Column(name="in_dept_name")
    private String inDeptName; //转入科室

    @Column(name="transfer_out_uid")
    private String transferOutUid;
    @Column(name="transfer_out_name")
    private String transferOutName; //转出人
    @Column(name="transfer_in_uid")
    private String transferInUid;
    @Column(name="transfer_in_name")
    private String transferInName; //转入人

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name="created_date")
    private Date createdDate;


    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name="transfer_out_at")
    private Date transferOutAt; //转出时间

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name="transfer_in_at")
    private Date transferInAt; //转入时间


    @Column(name = "current_status")
    private Integer  currentStatus;   //当前状态 1申请中  2 审核中  3完成   4审核拒绝


    @Column(name="apply_audit_signature")
    private String applyAuditSignature;  //申请签名

    @Column(name="approval_audit_signature")
    private String approvalAuditSignature;  //申请审核签名

    @Column(name="business_number")
    private String businessNumber;   //业务单号

    @Column(name = "last_modified_by", length = 50)
    @JsonIgnore
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    @LastModifiedDate
    @JsonIgnore
    private Date lastModifiedDate = new Date();

    @Column(name = "apply_reason")
    private String applyReason;//申请理由
    @Column(name = "approval_reason")
    private String approvalReason;//审核内容

    @Column(name = "approval_uid", nullable = true)
    private String approvalUid; //审核人
    @Column(name = "approval_by", nullable = true)
    private String approvalBy; //审核人

    @Column(name = "approval_date", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approvalDate;//审核时间


    @Column(name="out_site_uid")
    private String outSiteUid;
    @Column(name="out_site_name")
    private String outSiteName;
    @Column(name="in_site_uid")
    private String inSiteUid;
    @Column(name="in_site_name")
    private String inSiteName;






    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetDeptNum() {
        return assetDeptNum;
    }

    public void setAssetDeptNum(String assetDeptNum) {
        this.assetDeptNum = assetDeptNum;
    }

    public String getAssetSerialNum() {
        return assetSerialNum;
    }

    public void setAssetSerialNum(String assetSerialNum) {
        this.assetSerialNum = assetSerialNum;
    }

    public String getAssetQrCode() {
        return assetQrCode;
    }

    public void setAssetQrCode(String assetQrCode) {
        this.assetQrCode = assetQrCode;
    }

    public String getOutDeptUid() {
        return outDeptUid;
    }

    public void setOutDeptUid(String outDeptUid) {
        this.outDeptUid = outDeptUid;
    }

    public String getOutDeptName() {
        return outDeptName;
    }

    public void setOutDeptName(String outDeptName) {
        this.outDeptName = outDeptName;
    }

    public String getInDeptUid() {
        return inDeptUid;
    }

    public void setInDeptUid(String inDeptUid) {
        this.inDeptUid = inDeptUid;
    }

    public String getInDeptName() {
        return inDeptName;
    }

    public void setInDeptName(String inDeptName) {
        this.inDeptName = inDeptName;
    }

    public String getTransferOutUid() {
        return transferOutUid;
    }

    public void setTransferOutUid(String transferOutUid) {
        this.transferOutUid = transferOutUid;
    }

    public String getTransferOutName() {
        return transferOutName;
    }

    public void setTransferOutName(String transferOutName) {
        this.transferOutName = transferOutName;
    }

    public String getTransferInUid() {
        return transferInUid;
    }

    public void setTransferInUid(String transferInUid) {
        this.transferInUid = transferInUid;
    }

    public String getTransferInName() {
        return transferInName;
    }

    public void setTransferInName(String transferInName) {
        this.transferInName = transferInName;
    }



    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getTransferOutAt() {
        return transferOutAt;
    }

    public void setTransferOutAt(Date transferOutAt) {
        this.transferOutAt = transferOutAt;
    }

    public Date getTransferInAt() {
        return transferInAt;
    }

    public void setTransferInAt(Date transferInAt) {
        this.transferInAt = transferInAt;
    }


    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getApplyAuditSignature() {
        return applyAuditSignature;
    }

    public void setApplyAuditSignature(String applyAuditSignature) {
        this.applyAuditSignature = applyAuditSignature;
    }

    public String getApprovalAuditSignature() {
        return approvalAuditSignature;
    }

    public void setApprovalAuditSignature(String approvalAuditSignature) {
        this.approvalAuditSignature = approvalAuditSignature;
    }

    public Double getTotalPrice() {
        return  0.00;
    }

    @Override
    public BigDecimal getTotalPriceToBigDecimal() {
        return new BigDecimal(0);
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    @Override
    public String getBusinessUid() {
        return id;
    }


    @Override
    public int getBusinessType() {
        return ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT;
    }

    public void setBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
    }


    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getApplyReason() {
        return applyReason;
    }

    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }


    public String getFinancingNum() {
        return financingNum;
    }

    public void setFinancingNum(String financingNum) {
        this.financingNum = financingNum;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }


    public String getOutSiteUid() {
        return outSiteUid;
    }

    public void setOutSiteUid(String outSiteUid) {
        this.outSiteUid = outSiteUid;
    }

    public String getOutSiteName() {
        return outSiteName;
    }

    public void setOutSiteName(String outSiteName) {
        this.outSiteName = outSiteName;
    }

    public String getInSiteUid() {
        return inSiteUid;
    }

    public void setInSiteUid(String inSiteUid) {
        this.inSiteUid = inSiteUid;
    }

    public String getInSiteName() {
        return inSiteName;
    }

    public void setInSiteName(String inSiteName) {
        this.inSiteName = inSiteName;
    }


    public String getApprovalUid() {
        return approvalUid;
    }

    public void setApprovalUid(String approvalUid) {
        this.approvalUid = approvalUid;
    }

    public String getApprovalBy() {
        return approvalBy;
    }

    public void setApprovalBy(String approvalBy) {
        this.approvalBy = approvalBy;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }
}
