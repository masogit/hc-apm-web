package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
@Entity
@Table(name = "adjustment_transfer_application")
public class AdjustmentTransferApplication implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String uid;



    @Column(name = "application_number")
    private String applicationNumber;


    @Column(name = "stock_uid")
    private String stockUid;

    // ================= 第一部分：调入单位信息 =================

    /** 单位名称（盖章） */
    @Column(name = "unit_name")
    @NotNull(message = "单位名称不能为空")
    private String unitName;

    /** 医共体所属类别 (牵头医院/乡镇卫生院/社区卫生服务中心/社区卫生服务站) */
    @Column(name = "medical_community_type")
    private String medicalCommunityType;



    /** 联系人 */
    @Column(name = "contact_person")
    @NotNull(message = "联系人不能为空")
    private String contactPerson;

    /** 联系电话 */
    @Column(name = "contact_phone")
    private String contactPhone;

    /** 申报日期 */
    @Column(name = "application_date")
    private Date applicationDate;

    @Column(name = "application_uid")
    private String applicationUid;


    /** 借入时间 */
    @Column(name = "borrow_date")
    private Date borrowDate;

    /** 审批时间 */
    @Column(name = "approval_date")
    private Date approvalDate;

    // ================= 第二部分：需求设备基本要求 =================

    /** 设备类型 (诊断设备/治疗设备/辅助医疗设备/通用医疗设备/其他) */
    @Column(name = "device_type")
    private String deviceType;

    /** 其他设备类型注明 */
    @Column(name = "other_device_type_note")
    private String otherDeviceTypeNote;
    @Column(name = "description_of_use")
    private String descriptionOfUse;
    @Column(name = "other_description_of_use_note")
    private String otherDescriptionOfUseNote;
    /** 具体设备名称 */
    @Column(name = "specific_device_name")
    @NotNull(message = "具体设备名称不能为空")
    private String specificDeviceName;

    /** 核心技术参数 (必填) */
    @Column(name = "core_technical_params", columnDefinition = "TEXT")
//    @NotNull(message = "核心技术参数不能为空")
    private String coreTechnicalParams;

    /** 次要技术参数 (选填) */
    @Column(name = "secondary_technical_params", columnDefinition = "TEXT")
    private String secondaryTechnicalParams;

    /** 预计使用时限 (年) */
    @Column(name = "estimated_usage_years")
    private Integer estimatedUsageYears;

    // ================= 第三部分：配套条件说明 =================

    /** 场地条件状态 (已具备/需改造) */
    @Column(name = "site_condition_status")
    private String siteConditionStatus;

    /** 场地改造计划说明 */
    @Column(name = "site_renovation_plan", length = 500)
    private String siteRenovationPlan;

    /** 电力保障状态 (已满足/需增容) */
    @Column(name = "power_supply_status")
    private String powerSupplyStatus;

    /** 电力增容需求说明 */
    @Column(name = "power_capacity_demand", length = 500)
    private String powerCapacityDemand;

    /** 操作人员状态 (已配备/需培训) */
    @Column(name = "operator_status")
    private String operatorStatus;

    /** 操作人员资质注明 */
    @Column(name = "operator_qualification_note")
    private String operatorQualificationNote;

    /** 操作人员培训计划说明 */
    @Column(name = "operator_training_plan", length = 500)
    private String operatorTrainingPlan;

    /** 维护能力 (自主维护/需医共体技术支持) */
    @Column(name = "maintenance_capability")
    private String maintenanceCapability;

    // ================= 审计字段 (可选，建议加上) =================

    /** 创建时间 */
    @Column(name = "create_time", updatable = false)
    private Date createTime;

    /** 更新时间 */
    @Column(name = "update_time")
    private Date updateTime;

    /** 创建人ID */
    @Column(name = "created_by")
    private String  createdBy;

    /** 更新人ID */
    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "status")
    private Integer status;   //1待审批  2  审批未通过 3 已取消  4 待交付  5  已交付 6 已完成  7退回待审批    8 已退回 9退回审批不通过


    @Column(name = "stock_hospital_uid")
    private String stockHospitalUid; //入库医院
    @Column(name = "stock_site_uid")
    private String stockSiteUid; //入库院区
    @Column(name = "stock_dept_uid")
    private String stockDeptUid;//入库科室

    @Column(name = "stock_dept_name")
    private String stockDeptName;//入库科室

    @Column(name = "borrow_hospital_uid")
    private String borrowHospitalUid; //借入医院
    @Column(name = "borrow_site_uid")
    private String borrowSiteUid; //借入院区
    @Column(name = "borrow_dept_uid")
    private String borrowDeptUid;//借入科室


    // --- 1. 初步筛选结果 ---
    // 对应：□已匹配闲置设备 □无匹配设备（说明原因）
    @Column(name = "initial_screening_result")
    private String initialScreeningResult; // 存储选中的值，如 "matched" 或 "unmatched"
    @Column(name = "screening_reason")
    private String screeningReason;        // 存储“无匹配设备”时的说明原因

    // --- 2. 技术评估安排 ---
    // 对应：□无需现场评估 □需组织专家评估（评估时间：）
    @Column(name = "technical_assessment")
    private String technicalAssessment;    // 存储选中的值，如 "none" 或 "needed"
    @Column(name = "assessment_time")
    private Date assessmentTime;           // 存储评估时间

    // --- 3. 经办人签字 ---

    @Column(name = "handler_signature")
    private String handlerSignature;       // 存储签字人的姓名或ID





    @Column(name = "application_pending_approval_uid")
    private String applicationPendingApprovalUid; //申请待审批处理人
    @Column(name = "application_pending_approval_date")
    private Date applicationPendingApprovalDate;//申请待审批处理时间



    @Column(name = "pending_delivery_uid")
    private String pendingDeliveryUid; //待交付处理人
    @Column(name = "pending_delivery_date")
    private Date pendingDeliveryDate;//待交付处理时间



    @Column(name = "return_uid")
    private String returnUid; //退回处理人
    @Column(name = "return_date")
    private Date returnDate;//退回时间

    @Column(name = "return_reason")
    private String returnReason;//退回原因


    @Column(name = "return_approval_uid")
    private String returnApprovalUid; //退回处理人
    @Column(name = "return_approval_date")
    private Date returnApprovalDate;//退回时间

    @Column(name = "return_approval_result")
    private Integer returnApprovalResult;//退回原因 1 审批通过  2 审批拒绝


    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;



    @JoinColumn(insertable = false, updatable = false, name = "stock_uid", referencedColumnName = "uid", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    @NotFound(action = NotFoundAction.IGNORE)
    private IdleEquipmentStock idleEquipmentStock;


    @JoinColumn(insertable = false, updatable = false, name = "application_uid", referencedColumnName = "uid", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    @NotFound(action = NotFoundAction.IGNORE)
    private UserAccount userAccount;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStockUid() {
        return stockUid;
    }

    public void setStockUid(String stockUid) {
        this.stockUid = stockUid;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getMedicalCommunityType() {
        return medicalCommunityType;
    }

    public void setMedicalCommunityType(String medicalCommunityType) {
        this.medicalCommunityType = medicalCommunityType;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOtherDeviceTypeNote() {
        return otherDeviceTypeNote;
    }

    public void setOtherDeviceTypeNote(String otherDeviceTypeNote) {
        this.otherDeviceTypeNote = otherDeviceTypeNote;
    }

    public String getSpecificDeviceName() {
        return specificDeviceName;
    }

    public void setSpecificDeviceName(String specificDeviceName) {
        this.specificDeviceName = specificDeviceName;
    }

    public String getCoreTechnicalParams() {
        return coreTechnicalParams;
    }

    public void setCoreTechnicalParams(String coreTechnicalParams) {
        this.coreTechnicalParams = coreTechnicalParams;
    }

    public String getSecondaryTechnicalParams() {
        return secondaryTechnicalParams;
    }

    public void setSecondaryTechnicalParams(String secondaryTechnicalParams) {
        this.secondaryTechnicalParams = secondaryTechnicalParams;
    }

    public Integer getEstimatedUsageYears() {
        return estimatedUsageYears;
    }

    public void setEstimatedUsageYears(Integer estimatedUsageYears) {
        this.estimatedUsageYears = estimatedUsageYears;
    }

    public String getSiteConditionStatus() {
        return siteConditionStatus;
    }

    public void setSiteConditionStatus(String siteConditionStatus) {
        this.siteConditionStatus = siteConditionStatus;
    }

    public String getSiteRenovationPlan() {
        return siteRenovationPlan;
    }

    public void setSiteRenovationPlan(String siteRenovationPlan) {
        this.siteRenovationPlan = siteRenovationPlan;
    }

    public String getPowerSupplyStatus() {
        return powerSupplyStatus;
    }

    public void setPowerSupplyStatus(String powerSupplyStatus) {
        this.powerSupplyStatus = powerSupplyStatus;
    }

    public String getPowerCapacityDemand() {
        return powerCapacityDemand;
    }

    public void setPowerCapacityDemand(String powerCapacityDemand) {
        this.powerCapacityDemand = powerCapacityDemand;
    }

    public String getOperatorStatus() {
        return operatorStatus;
    }

    public void setOperatorStatus(String operatorStatus) {
        this.operatorStatus = operatorStatus;
    }

    public String getOperatorQualificationNote() {
        return operatorQualificationNote;
    }

    public void setOperatorQualificationNote(String operatorQualificationNote) {
        this.operatorQualificationNote = operatorQualificationNote;
    }

    public String getOperatorTrainingPlan() {
        return operatorTrainingPlan;
    }

    public void setOperatorTrainingPlan(String operatorTrainingPlan) {
        this.operatorTrainingPlan = operatorTrainingPlan;
    }

    public String getMaintenanceCapability() {
        return maintenanceCapability;
    }

    public void setMaintenanceCapability(String maintenanceCapability) {
        this.maintenanceCapability = maintenanceCapability;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getApplicationUid() {
        return applicationUid;
    }

    public void setApplicationUid(String applicationUid) {
        this.applicationUid = applicationUid;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public String getStockSiteUid() {
        return stockSiteUid;
    }

    public void setStockSiteUid(String stockSiteUid) {
        this.stockSiteUid = stockSiteUid;
    }

    public String getStockDeptUid() {
        return stockDeptUid;
    }

    public void setStockDeptUid(String stockDeptUid) {
        this.stockDeptUid = stockDeptUid;
    }

    public String getStockDeptName() {
        return stockDeptName;
    }

    public void setStockDeptName(String stockDeptName) {
        this.stockDeptName = stockDeptName;
    }

    public String getBorrowSiteUid() {
        return borrowSiteUid;
    }

    public void setBorrowSiteUid(String borrowSiteUid) {
        this.borrowSiteUid = borrowSiteUid;
    }

    public String getBorrowDeptUid() {
        return borrowDeptUid;
    }

    public void setBorrowDeptUid(String borrowDeptUid) {
        this.borrowDeptUid = borrowDeptUid;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public IdleEquipmentStock getIdleEquipmentStock() {
        return idleEquipmentStock;
    }

    public void setIdleEquipmentStock(IdleEquipmentStock idleEquipmentStock) {
        this.idleEquipmentStock = idleEquipmentStock;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }


    public String getApplicationPendingApprovalUid() {
        return applicationPendingApprovalUid;
    }

    public void setApplicationPendingApprovalUid(String applicationPendingApprovalUid) {
        this.applicationPendingApprovalUid = applicationPendingApprovalUid;
    }

    public Date getApplicationPendingApprovalDate() {
        return applicationPendingApprovalDate;
    }

    public void setApplicationPendingApprovalDate(Date applicationPendingApprovalDate) {
        this.applicationPendingApprovalDate = applicationPendingApprovalDate;
    }

    public String getPendingDeliveryUid() {
        return pendingDeliveryUid;
    }

    public void setPendingDeliveryUid(String pendingDeliveryUid) {
        this.pendingDeliveryUid = pendingDeliveryUid;
    }

    public Date getPendingDeliveryDate() {
        return pendingDeliveryDate;
    }

    public void setPendingDeliveryDate(Date pendingDeliveryDate) {
        this.pendingDeliveryDate = pendingDeliveryDate;
    }

    public String getReturnUid() {
        return returnUid;
    }

    public void setReturnUid(String returnUid) {
        this.returnUid = returnUid;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }


    public String getInitialScreeningResult() {
        return initialScreeningResult;
    }

    public void setInitialScreeningResult(String initialScreeningResult) {
        this.initialScreeningResult = initialScreeningResult;
    }

    public String getScreeningReason() {
        return screeningReason;
    }

    public void setScreeningReason(String screeningReason) {
        this.screeningReason = screeningReason;
    }

    public String getTechnicalAssessment() {
        return technicalAssessment;
    }

    public void setTechnicalAssessment(String technicalAssessment) {
        this.technicalAssessment = technicalAssessment;
    }

    public Date getAssessmentTime() {
        return assessmentTime;
    }

    public void setAssessmentTime(Date assessmentTime) {
        this.assessmentTime = assessmentTime;
    }

    public String getHandlerSignature() {
        return handlerSignature;
    }

    public void setHandlerSignature(String handlerSignature) {
        this.handlerSignature = handlerSignature;
    }


    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public String getReturnApprovalUid() {
        return returnApprovalUid;
    }

    public void setReturnApprovalUid(String returnApprovalUid) {
        this.returnApprovalUid = returnApprovalUid;
    }

    public Date getReturnApprovalDate() {
        return returnApprovalDate;
    }

    public void setReturnApprovalDate(Date returnApprovalDate) {
        this.returnApprovalDate = returnApprovalDate;
    }

    public Integer getReturnApprovalResult() {
        return returnApprovalResult;
    }

    public void setReturnApprovalResult(Integer returnApprovalResult) {
        this.returnApprovalResult = returnApprovalResult;
    }


    public String getStockHospitalUid() {
        return stockHospitalUid;
    }

    public void setStockHospitalUid(String stockHospitalUid) {
        this.stockHospitalUid = stockHospitalUid;
    }

    public String getBorrowHospitalUid() {
        return borrowHospitalUid;
    }

    public void setBorrowHospitalUid(String borrowHospitalUid) {
        this.borrowHospitalUid = borrowHospitalUid;
    }


    public String getDescriptionOfUse() {
        return descriptionOfUse;
    }

    public void setDescriptionOfUse(String descriptionOfUse) {
        this.descriptionOfUse = descriptionOfUse;
    }

    public String getOtherDescriptionOfUseNote() {
        return otherDescriptionOfUseNote;
    }

    public void setOtherDescriptionOfUseNote(String otherDescriptionOfUseNote) {
        this.otherDescriptionOfUseNote = otherDescriptionOfUseNote;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AdjustmentTransferApplication)) {
            return false;
        }
        AdjustmentTransferApplication other = (AdjustmentTransferApplication) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }
}
