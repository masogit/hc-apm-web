package com.ge.apm.domain.budget;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.ApprovableTarget;
import com.ge.apm.service.constant.ApprovalConstant;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Yangfan, Liu
 * @date 4/8/2022 14:45
 */
@Entity
@Table(name = "budget_application", indexes = {@Index(unique = true, columnList = "tenant_uid,application_num"),
        @Index(columnList = "application_status")})
public class BudgetApplication implements ApprovableTarget, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    private String id;

    // 申请编号
    @Column(name = "application_num")
    private String applicationNum;

    //1暂存 2 通过  3 不通过
    @Column(name = "application_status")
    private Integer applicationStatus;
    /**
     * 申请步骤APPLICATIONSTEP
     *待提交 待审核  已完成     已完成
     */
    public enum APPLICATIONSTEP {
        ToSubmitted, PendingApproval,PendingComplete,Complete
    }
    /**
     * 采购状态
     *已采购未采购
     */
    public enum PROCURESTATUS {
        Unpurchased, Purchased
    }
    /**
     * 申请步骤
     */
    @Column(name = "application_step")
    private String applicationStep;

    /**
     * 采购状态
     */
    @Column(name = "procure_Status")
    private String procureStatus;

    /**
     * 采购单id
     */
    @Column(name = "purchase_id")
    private String purchaseId;

    // 申请日期
    @Column(name = "application_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applicationDate;

    // 申请人id
    @Column(name = "applicant_id")
    private Integer applicantId;

    // 申请人
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "applicant_id",
            referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserAccount applicant;

    // 申请科室id
    @Column(name = "applicant_dept_id")
    private Integer applicationDeptId;

    // 申请科室
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "applicant_dept_id",
            referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action = NotFoundAction.IGNORE)
    private OrgInfo applicationDept;

    // 审批进度
    @Column(name = "approval_progress")
    private Integer approvalProgress;

    // 审批日期
    @Column(name = "approval_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approvalDate;

    // 审批人id
    @Column(name = "approver_id")
    private Integer approverId;

    // 审批人
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "approver_id",
            referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserAccount approver;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @OrderBy("created_date DESC")
    private Set<BudgetApplicationAsset> budgetAssets;

    // 总金额
    @Formula("(select sum(a.application_amount) from budget_application_asset a where a.application_id = id)")
    private BigDecimal totalAmount;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_uid", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @OrderBy("approval_level ASC")
    private Set<MultiLevelApproval> budgetApprovals;

    // 审批流id
    @Column(name = "approval_config_id")
    private String approvalConfigId;

    // 病区代码
    @Column(name = "ward_code")
    private String wardCode;

    // 院区
    @Column(name = "courtyard_area")
    private String courtyardArea;

    // 审批流
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "approval_config_id",
            referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action = NotFoundAction.IGNORE)
    private MultiLevelApprovalConfig approvalConfig;

    // 删除标识 0: 未删除；1：已删除
    @Column(name = "is_deleted")
    private Integer isDeleted = 0;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedDate;

    @Transient
    private String approveName ;    //审批人

    @Transient
    private String applicantName ;//申请人

    public String getProcureStatus() {
        return StringUtils.isBlank(procureStatus)?BudgetApplication.PROCURESTATUS.Unpurchased.toString(): procureStatus;
    }

    public void setProcureStatus(String procureStatus) {
        this.procureStatus = procureStatus;
    }

    public String getCourtyardArea() {
        return courtyardArea;
    }

    public void setCourtyardArea(String courtyardArea) {
        this.courtyardArea = courtyardArea;
    }

    public String getWardCode() {
        return wardCode;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }

    /**
     * *设备名称
     */
    @Transient
    private String applicationAssetName;

    // 预算单价
    @Transient
    private BigDecimal applicationPrice;

    // 预算总价
    @Transient
    private BigDecimal applicationAmount;

    // 申请数量
    @Transient
    private Integer applicationQuantity;

    // 申请科室
    @Transient
    private String applicationDeptName;

    public String getApplicationDeptName() {
        return applicationDeptName;
    }

    public void setApplicationDeptName(String applicationDeptName) {
        this.applicationDeptName = applicationDeptName;
    }

    public Integer getApplicationQuantity() {
        return applicationQuantity;
    }

    public void setApplicationQuantity(Integer applicationQuantity) {
        this.applicationQuantity = applicationQuantity;
    }

    public String getApplicationAssetName() {
        return applicationAssetName;
    }

    public void setApplicationAssetName(String applicationAssetName) {
        this.applicationAssetName = applicationAssetName;
    }

    public BigDecimal getApplicationPrice() {
        return applicationPrice;
    }

    public void setApplicationPrice(BigDecimal applicationPrice) {
        this.applicationPrice = applicationPrice;
    }

    public BigDecimal getApplicationAmount() {
        return applicationAmount;
    }

    public void setApplicationAmount(BigDecimal applicationAmount) {
        this.applicationAmount = applicationAmount;
    }

    public String getApproveName() {
        return approveName;
    }

    public void setApproveName(String approveName) {
        this.approveName = approveName;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    /**
     * 订单类型
     * BAY临时采购  BAP年度采购
     */
    public enum OrderType {
        BAY, BAP
    }
    /**
     * 订单类型
     * BAY临时采购  BAP年度采购
     */
    @Column(name = "order_type")
    private String orderType;

    public String getApplicationStep() {
        return applicationStep;
    }

    public void setApplicationStep(String applicationStep) {
        this.applicationStep = applicationStep;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    /**
     * 申请编号索引
     */
    @Column(name = "application_num_index")
    private Integer applicationNumIndex;

    public Integer getApplicationNumIndex() {
        return applicationNumIndex;
    }

    public void setApplicationNumIndex(Integer applicationNumIndex) {
        this.applicationNumIndex = applicationNumIndex;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BudgetApplication)) {
            return false;
        }
        BudgetApplication other = (BudgetApplication) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationNum() {
        return applicationNum;
    }

    public void setApplicationNum(String applicationNum) {
        this.applicationNum = applicationNum;
    }

    public Integer getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(Integer applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Integer getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Integer applicantId) {
        this.applicantId = applicantId;
    }

    public UserAccount getApplicant() {
        return applicant;
    }

    public void setApplicant(UserAccount applicant) {
        this.applicant = applicant;
    }

    public Integer getApplicationDeptId() {
        return applicationDeptId;
    }

    public void setApplicationDeptId(Integer applicationDeptId) {
        this.applicationDeptId = applicationDeptId;
    }

    public OrgInfo getApplicationDept() {
        return applicationDept;
    }

    public void setApplicationDept(OrgInfo applicationDept) {
        this.applicationDept = applicationDept;
    }

    public Integer getApprovalProgress() {
        return approvalProgress;
    }

    public void setApprovalProgress(Integer approvalProgress) {
        this.approvalProgress = approvalProgress;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Integer getApproverId() {
        return approverId;
    }

    public void setApproverId(Integer approverId) {
        this.approverId = approverId;
    }

    public UserAccount getApprover() {
        return approver;
    }

    public void setApprover(UserAccount approver) {
        this.approver = approver;
    }

    public Set<BudgetApplicationAsset> getBudgetAssets() {
        return budgetAssets;
    }

    public void setBudgetAssets(Set<BudgetApplicationAsset> budgetAssets) {
        this.budgetAssets = budgetAssets;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Set<MultiLevelApproval> getBudgetApprovals() {
        return budgetApprovals;
    }

    public void setBudgetApprovals(Set<MultiLevelApproval> budgetApprovals) {
        this.budgetApprovals = budgetApprovals;
    }

    public String getApprovalConfigId() {
        return approvalConfigId;
    }

    public void setApprovalConfigId(String approvalConfigId) {
        this.approvalConfigId = approvalConfigId;
    }

    public MultiLevelApprovalConfig getApprovalConfig() {
        return approvalConfig;
    }

    public void setApprovalConfig(MultiLevelApprovalConfig approvalConfig) {
        this.approvalConfig = approvalConfig;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getInstitutionUID() {
        return institutionUID;
    }

    public void setInstitutionUID(String institutionUID) {
        this.institutionUID = institutionUID;
    }

    public String getHospitalUID() {
        return hospitalUID;
    }

    public void setHospitalUID(String hospitalUID) {
        this.hospitalUID = hospitalUID;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
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

    @Override
    public BigDecimal getTotalPriceToBigDecimal() {
        return totalAmount;
    }

    @Override
    public String getBusinessNumber() {
        return applicationNum;
    }

    @Override
    public String getBusinessUid() {
        return id;
    }

    @Override
    public int getBusinessType() {
        return ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }
}
