package com.ge.apm.domain;

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
    private Integer  currentStatus;   //当前状态 1申请中  2 审核中  3完成  4申请拒绝  5审核拒绝


    private Integer level;   //当前审级别
    @Column(name = "asset_name")
    private String  assetName;

    @Column(name = "created_by", nullable = true, length = 50, updatable = false)
    @JsonIgnore
    private String createdBy;

    @Column(name = "created_date", nullable = true)
    @CreatedDate
    @JsonIgnore
    private Date createdDate;

    @Column(name = "request_user_uid")
    private String requestUserUid;


    @Column(name = "request_type")
    private Integer requestType; //请求类型   1 临床申请  2工程师申请

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







    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public Integer  getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
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



    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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
}
