package com.ge.apm.domain.budget;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ge.apm.domain.MultiLevelApproval;
import com.ge.apm.domain.MultiLevelApprovalConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.ApprovableTarget;
import com.ge.apm.service.constant.ApprovalConstant;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * @author lyj
 */
@Entity
@Table(name = "purchase_order")
public class PurchaseOrderInfo implements ApprovableTarget,Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    private String id;

    // 采购编号
    @Column(name = "purchase_num")
    private String purchaseNum;

    /**
     * 申请编号索引
     */
    @Column(name = "purchase_num_index")
    private Integer purchaseNumIndex;


    // 采购数量
    @Column(name = "purchase_quantity")
    private Integer purchaseQuantity;

    // 采购总价
    @Column(name = "total_price", scale = 2)
    private BigDecimal totalPrice;

    // 删除标识 0: 未删除；1：已删除
    @Column(name = "is_deleted")
    private Integer isDeleted = 0;

    /**
     * *设备名称
     */
    @Column(name = "application_asset_name")
    private String applicationAssetName;

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

    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    @Column(name = "contract_end_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date contractEndDate;

    @Column(name = "contract_start_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date contractStartDate;


    /**
     * 申请步骤APPLICATIONSTEP
     * 信息已确认 过会已完成  完成
     */
    public enum PURCHASESTEPSTATUS {
        Complete, ConfirmedInfoMeet,ConfirmedInfo
    }
    /**
     * 待信息确认  待过会 待招标 完成
     *
     *
     */
    public enum PURCHASESTEP {
        ToConfirmedInfo, ToConfirmedInfoMeet,ToConfirmedInfoTendered,Complete
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public Date getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(Date contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    @Column(name = "order_step")
    private String orderStep;

    @Column(name = "step_status")
    private String stepStatus;

    @Column(name = "status")
    private String status;

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


    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_uid", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @OrderBy("approval_level ASC")
    private Set<MultiLevelApproval> purchaseApprovals;

    // 审批流id
    @Column(name = "approval_config_id")
    private String approvalConfigId;

    // 审批流
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "approval_config_id",
            referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action = NotFoundAction.IGNORE)
    private MultiLevelApprovalConfig approvalConfig;

    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedDate;

    // 申请人id
    @Column(name = "operator_id")
    private Integer operatorId;

    // 申请人
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "operator_id",
            referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserAccount operator;

    /*****过会信息****/

    //
    @Column(name = "meeting_engineer_opinion")
    private String meetingEngineerOpinion;

    @Column(name = "meeting_existing_brands")
    private String meetingExistingBrands;

    @Column(name = "meeting_fee_items")
    private String meetingFeeItems;

    @Column(name = "meeting_consumable_description")
    private String meetingConsumableDescription;

    @Column(name = "meeting_remarks")
    private String meetingRemarks;

    @Column(name = "meeting_equipment_dept_opin")
    private String meetingEquipmentDeptOpin;

    @Column(name = "meeting_maximum_price")
    private String meetingMaximumPrice;

    @Column(name = "meeting_purchasing_imports")
    private String meetingPurchasingImports;

    @Column(name = "meeting_approve_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date meetingApproveDate;

    @Column(name = "meeting_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date meetingDate;


    /*****招标****/
    @Column(name = "purchase_type")
    private String purchaseType;

    @Column(name = "vender_name")
    private String venderName;

    @Column(name = "vender_id")
    private String venderId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "tender_no")
    private String tenderNo;

    @Column(name = "asset_purchase_count")
    private Integer assetPurchaseCount;

    @Column(name = "contract_total", scale = 2)
    private BigDecimal contractTotal;

    public String getTenderNo() {
        return tenderNo;
    }

    public void setTenderNo(String tenderNo) {
        this.tenderNo = tenderNo;
    }

    public Integer getAssetPurchaseCount() {
        return assetPurchaseCount;
    }

    public void setAssetPurchaseCount(Integer assetPurchaseCount) {
        this.assetPurchaseCount = assetPurchaseCount;
    }

    public BigDecimal getContractTotal() {
        return contractTotal;
    }

    public void setContractTotal(BigDecimal contractTotal) {
        this.contractTotal = contractTotal;
    }

    public Date getMeetingApproveDate() {
        return meetingApproveDate;
    }

    public void setMeetingApproveDate(Date meetingApproveDate) {
        this.meetingApproveDate = meetingApproveDate;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public Date getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(Date meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getMeetingEngineerOpinion() {
        return meetingEngineerOpinion;
    }

    public void setMeetingEngineerOpinion(String meetingEngineerOpinion) {
        this.meetingEngineerOpinion = meetingEngineerOpinion;
    }

    public String getMeetingExistingBrands() {
        return meetingExistingBrands;
    }

    public void setMeetingExistingBrands(String meetingExistingBrands) {
        this.meetingExistingBrands = meetingExistingBrands;
    }

    public String getMeetingFeeItems() {
        return meetingFeeItems;
    }

    public void setMeetingFeeItems(String meetingFeeItems) {
        this.meetingFeeItems = meetingFeeItems;
    }

    public String getMeetingConsumableDescription() {
        return meetingConsumableDescription;
    }

    public void setMeetingConsumableDescription(String meetingConsumableDescription) {
        this.meetingConsumableDescription = meetingConsumableDescription;
    }

    public String getMeetingRemarks() {
        return meetingRemarks;
    }

    public void setMeetingRemarks(String meetingRemarks) {
        this.meetingRemarks = meetingRemarks;
    }

    public String getMeetingEquipmentDeptOpin() {
        return meetingEquipmentDeptOpin;
    }

    public void setMeetingEquipmentDeptOpin(String meetingEquipmentDeptOpin) {
        this.meetingEquipmentDeptOpin = meetingEquipmentDeptOpin;
    }

    public String getMeetingMaximumPrice() {
        return meetingMaximumPrice;
    }

    public void setMeetingMaximumPrice(String meetingMaximumPrice) {
        this.meetingMaximumPrice = meetingMaximumPrice;
    }

    public String getMeetingPurchasingImports() {
        return meetingPurchasingImports;
    }

    public void setMeetingPurchasingImports(String meetingPurchasingImports) {
        this.meetingPurchasingImports = meetingPurchasingImports;
    }

    /*****过会信息****/

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

    public Set<MultiLevelApproval> getPurchaseApprovals() {
        return purchaseApprovals;
    }

    public void setPurchaseApprovals(Set<MultiLevelApproval> purchaseApprovals) {
        this.purchaseApprovals = purchaseApprovals;
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

    public String getOrderStep() {
        return orderStep;
    }

    public void setOrderStep(String orderStep) {
        this.orderStep = orderStep;
    }

    public String getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(String stepStatus) {
        this.stepStatus = stepStatus;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return StringUtils.isNotBlank(status)?status:"NotEnabled";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplicationAssetName() {
        return applicationAssetName;
    }

    public void setApplicationAssetName(String applicationAssetName) {
        this.applicationAssetName = applicationAssetName;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public UserAccount getOperator() {
        return operator;
    }

    public void setOperator(UserAccount operator) {
        this.operator = operator;
    }

    public Integer getPurchaseNumIndex() {
        return purchaseNumIndex;
    }

    public void setPurchaseNumIndex(Integer purchaseNumIndex) {
        this.purchaseNumIndex = purchaseNumIndex;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PurchaseOrderInfo)) {
            return false;
        }
        PurchaseOrderInfo other = (PurchaseOrderInfo) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }



    public String getPurchaseNum() {
        return purchaseNum;
    }

    public void setPurchaseNum(String purchaseNum) {
        this.purchaseNum = purchaseNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(Integer purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
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
        return new BigDecimal(0);
    }

    @Override
    public String getBusinessNumber() {
        return purchaseNum;
    }

    @Override
    public String getBusinessUid() {
        return id;
    }

    @Override
    public int getBusinessType() {
        return ApprovalConstant.BUSINESS_TYPE_PURCHASE_MANAGEMENT;
    }

}
