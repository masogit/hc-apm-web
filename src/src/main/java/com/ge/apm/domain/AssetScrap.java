package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ge.apm.domain.pure.ApprovableTarget;
import com.ge.apm.service.constant.ApprovalConstant;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "asset_scrap")
public class AssetScrap implements Serializable , ApprovableTarget {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "asset_uid")
    private String assetUid;
    @Column(name = "asset_id")
    private Integer assetId;
    @Column(name = "current_status")
    private Integer  currentStatus;   //当前状态 1待审核(临床申请待审核)  2 审核中(多级审核中)  3完成(报废流程结束多级审批完成)  4申请拒绝(临床申请拒绝)  5审核拒绝(多级审批拒绝)


    private Integer level;   //当前审级别
    @Column(name = "asset_name")
    private String  assetName;


    @Column(name = "clinical_dept_id")
    private Integer clinicalDeptId;//所属科室

    @Column(name = "clinical_dept_uid")
    private String clinicalDeptUid;//设备所属科室

    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;//所属科室

    @Column(name="asset_dept_num")
    private String assetDeptNum; //设备编号

    @Column(name = "financing_num")
    private String financingNum;//资产编号



    @Column(name = "created_by", nullable = true, length = 50, updatable = false)
    private String createdBy;

    @Column(name = "created_date", nullable = true)
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;



    @Column(name = "approval_uid", nullable = true, length = 50, updatable = false)
    private String approvalUid; //工程师审核人
    @Column(name = "approval_by", nullable = true, length = 50, updatable = false)
    private String approvalBy; //工程师审核人

    @Column(name = "approval_date", nullable = true)
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approvalDate;//工程师审核时间


    @Column(name = "request_user_uid")
    private String requestUserUid;


    @Column(name = "request_type")
    private Integer requestType; //请求类型   1 临床申请  2工程师申请

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedDate = new Date();
    @Column(name = "apply_reason")
    private String applyReason;//申请理由
    @Column(name = "approval_reason")
    private String approvalReason;//审核内容
    @Column(name = "business_number")
    private String businessNumber;   //业务单号


    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUid;

    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUid;

    @Column(name="site_uid",columnDefinition = "CHAR(32)")
    private String siteUid;

    @Column(name="apply_audit_signature")
    private String applyAuditSignature;  //申请签名

    @Column(name="approval_audit_signature")
    private String approvalAuditSignature;  //申请审核签名



    @Column(name = "audit_uid", columnDefinition = "TEXT")
    private String auditUid;   //待审核人uid

    @Column(name = "asset_status")
    private Integer assetStatus; //报废之前asset_info status

    @Column(name = "equipment_disposal_method")
    private String equipmentDisposalMethod;

    @Column(name = "custom_disposal_method")
    private String customDisposalMethod;

    @Transient
    @JsonProperty
    private List<V2_BlobObject> attachments;

    // 报废申请审批通过时是否处理工单（维修，保养，日常检查，盘点）
    @Column(name = "resolve_order")
    private Boolean resolveOrder = false;

    // 报废申请提交人的ID（维修，保养，日常检查，盘点）
    @Column(name = "resolver_id")
    private Integer resolverId;

    public AssetScrap() {
    }

    public Double getTotalPrice() {
        return  0.00;
    }


    @Override
    public BigDecimal getTotalPriceToBigDecimal() {
        return new BigDecimal(0);
    }

    @Override
    public String getBusinessNumber() {
        return businessNumber;
    }

    @Override
    public String getBusinessUid() {
        return getId();
    }

    @Override
    public int getBusinessType() {
        return ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP;
    }


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

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getRequestUserUid() {
        return requestUserUid;
    }

    public void setRequestUserUid(String requestUserUid) {
        this.requestUserUid = requestUserUid;
    }

    public Integer getRequestType() {
        return requestType;
    }

    public void setRequestType(Integer requestType) {
        this.requestType = requestType;
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

    public void setBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public String getInstitutionUid() {
        return institutionUid;
    }

    public void setInstitutionUid(String institutionUid) {
        this.institutionUid = institutionUid;
    }

    public String getHospitalUid() {
        return hospitalUid;
    }

    public void setHospitalUid(String hospitalUid) {
        this.hospitalUid = hospitalUid;
    }

    public String getSiteUid() {
        return siteUid;
    }

    public void setSiteUid(String siteUid) {
        this.siteUid = siteUid;
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

    public Integer getClinicalDeptId() {
        return clinicalDeptId;
    }

    public void setClinicalDeptId(Integer clinicalDeptId) {
        this.clinicalDeptId = clinicalDeptId;
    }

    public String getClinicalDeptUid() {
        return clinicalDeptUid;
    }

    public void setClinicalDeptUid(String clinicalDeptUid) {
        this.clinicalDeptUid = clinicalDeptUid;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }

    public String getAssetDeptNum() {
        return assetDeptNum;
    }

    public void setAssetDeptNum(String assetDeptNum) {
        this.assetDeptNum = assetDeptNum;
    }

    public String getFinancingNum() {
        return financingNum;
    }

    public void setFinancingNum(String financingNum) {
        this.financingNum = financingNum;
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


    public String getApprovalUid() {
        return approvalUid;
    }

    public void setApprovalUid(String approvalUid) {
        this.approvalUid = approvalUid;
    }

    public String getAuditUid() {
        return auditUid;
    }

    public void setAuditUid(String auditUid) {
        this.auditUid = auditUid;
    }

    public Integer getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(Integer assetStatus) {
        this.assetStatus = assetStatus;
    }

    public String getEquipmentDisposalMethod() {
        return equipmentDisposalMethod;
    }

    public void setEquipmentDisposalMethod(String equipmentDisposalMethod) {
        this.equipmentDisposalMethod = equipmentDisposalMethod;
    }

    public String getCustomDisposalMethod() {
        return customDisposalMethod;
    }

    public void setCustomDisposalMethod(String customDisposalMethod) {
        this.customDisposalMethod = customDisposalMethod;
    }

    public List<V2_BlobObject> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<V2_BlobObject> attachments) {
        this.attachments = attachments;
    }

    public Boolean getResolveOrder() {
        return resolveOrder;
    }

    public void setResolveOrder(Boolean resolveOrder) {
        this.resolveOrder = resolveOrder;
    }

    public Integer getResolverId() {
        return resolverId;
    }

    public void setResolverId(Integer resolverId) {
        this.resolverId = resolverId;
    }
}
