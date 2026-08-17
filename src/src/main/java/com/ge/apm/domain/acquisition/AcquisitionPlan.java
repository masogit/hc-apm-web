package com.ge.apm.domain.acquisition;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.ge.apm.domain.budget.BudgetApplication;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "acquisition_plan")
public class AcquisitionPlan {


    public enum ApplicationStatusEnum {
        TempSave, DrugCenterPending, WorkGroupPending, BureauPartyPending, Approved, Rejected,Cancel
    }

    public enum NodeStatusEnum{
        Pending, Approved, Rejected
    }

    public enum ResultEnum{
        SelfReview, Approved ,Rejected
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "application_no")
    private String applicationNo;

    /**
     * 申请编号索引
     */
    @Column(name = "application_no_index")
    private Integer applicationNoIndex;

    //0:暂存 1：待药管中心审批 2：待调研工作组审批 3：待局党委会审批
    @Column(name = "application_step")
    private Integer applicationStep;

    //
    @Column(name = "application_status")
    private String applicationStatus;

    @Column(name = "application_year")
    private String applicationYear;

    @Column(name = "application_dept_uid")
    private String applicationDeptUid;

    @Column(name = "application_dept_name")
    private String applicationDeptName;
    @Column(name = "application_hospital_uid")
    private String applicationHospitalUid;
    @Column(name = "application_hospital_name")
    private String applicationHospitalName;
    @Column(name = "application_user_uid")
    private String applicationUserUid;
    @Column(name = "application_user_name")
    private String applicationUserName;

    @Column(name = "application_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applicationDate;

    @Column(name = "aset_name")
    private String assetName;

    @Column(name = "specifications")
    private String specifications;// 型号

    @Column(name = "specifications_id")
    private String specificationsId;

    @Column(name = "application_type")
    private String applicationType;

    @Column(name = "updated_asset_uid")
    @Size(max = 4000)
    private String updatedAssetUid;//被更新设备

    @Column(name = "updated_asset_name")
    @Size(max = 4000)
    private String updatedAssetName;

    @Column(name = "application_plan_count")
    private Integer applicationPlanCount;

    @Column(name = "drug_center_uid")
    private String drugCenterUid;
    @Column(name = "work_group_uid")
    private String workGroupUid;
    @Column(name = "bureau_party_uid")
    private String bureauPartyUid;

    //预算单价（万元）(必填, 最多保留四位小数)
    @Column(name = "budget_unit_price")
    private BigDecimal budgetUnitPrice;

    /**
     * 预算总价（万元）(必填, 单价*数量计算)
     */
    @Column(name = "budget_total_amount")
    private BigDecimal budgetTotalAmount ;

    /**
     * 预计使用年限（年）(必填, 下拉选项1-10)
     */
    @Column(name = "estimated_service_years")
    private Integer estimatedServiceYears;

    /**
     * 同类设备数量（台/套）(必填, 整数)
     */
    @Column(name = "similar_equipment_count")
    private Integer similarEquipmentCount;

    /**
     * 拟更新设备已使用年限（年）(仅申请类别为更新类时显示并必填, 最多保留两位小数)
     */
    @Column(name = "current_equipment_service_years")
    private BigDecimal currentEquipmentServiceYears;

    /**
     * 资金来源 (必填): 财政/自有/其他
     */
    @Column(name = "funding_source")
    private String fundingSource;

    /**
     * 资金落实（是/否）(必填)
     */
    @Column(name = "funding_implemented")
    private String fundingImplemented;

    /**
     * 甲乙类大型医疗设备及配置许可证: 甲类/乙类/无
     */
    @Column(name = "medical_equipment_license")
    private String medicalEquipmentLicense;

    /**
     * 场地改造成本（万元）(最多保留两位小数)
     */
    @Column(name = "site_renovation_cost")
    private BigDecimal siteRenovationCost;

    /**
     * 采购原因 (不超过100字, 采用输入框控件)
     */
    @Column(name = "purchase_reason")
    private String purchaseReason;

    /**
     * 新增人员配备（人）(仅申请类别为必需类和常规类时显示且必填, 整数)
     */
    @Column(name = "new_staff_allocation")
    private Integer newStaffAllocation;

    /**
     * 折旧成本（万元）(自动计算, 最多保留两位小数)
     */
    @Column(name = "depreciation_cost")
    private BigDecimal depreciationCost;

    /**
     * 维保费用（万元）(填报数字, 最多保留两位小数)
     */
    @Column(name = "maintenance_cost")
    private BigDecimal maintenanceCost;

    /**
     * 配套不可单独收费耗材收费占比及采购成本（万元）
     */
    @Column(name = "consumables_cost")
    private BigDecimal consumablesCost;

    /**
     * 年均服务量（人/次）(填报整数)
     */
    @Column(name = "annual_service_volume")
    private Integer annualServiceVolume;

    /**
     * 年服务收入总金额（万元）(填报数字, 最多保留两位小数)
     */
    @Column(name = "annual_service_income")
    private BigDecimal annualServiceIncome;

    /**
     * 年度运营效益权重（%）(自动计算, 最多保留两位小数)
     */
    @Column(name = "annual_operation_efficiency_weight")
    private BigDecimal annualOperationEfficiencyWeight;

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

    @Column(name = "purchase_reason")
    @Size(max = 2000)
    private String fileComments;

    @Transient
    private List<AcquisitionPlan> list= new ArrayList<>();

    public String getFileComments() {
        return fileComments;
    }

    public void setFileComments(String fileComments) {
        this.fileComments = fileComments;
    }

    public List<AcquisitionPlan> getList() {
        return list;
    }

    public void setList(List<AcquisitionPlan> list) {
        this.list = list;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getApplicationStep() {
        return applicationStep;
    }

    public void setApplicationStep(Integer applicationStep) {
        this.applicationStep = applicationStep;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getApplicationDeptUid() {
        return applicationDeptUid;
    }

    public void setApplicationDeptUid(String applicationDeptUid) {
        this.applicationDeptUid = applicationDeptUid;
    }

    public String getApplicationDeptName() {
        return applicationDeptName;
    }

    public void setApplicationDeptName(String applicationDeptName) {
        this.applicationDeptName = applicationDeptName;
    }

    public String getApplicationHospitalUid() {
        return applicationHospitalUid;
    }

    public void setApplicationHospitalUid(String applicationHospitalUid) {
        this.applicationHospitalUid = applicationHospitalUid;
    }

    public String getApplicationHospitalName() {
        return applicationHospitalName;
    }

    public void setApplicationHospitalName(String applicationHospitalName) {
        this.applicationHospitalName = applicationHospitalName;
    }

    public String getApplicationUserUid() {
        return applicationUserUid;
    }

    public void setApplicationUserUid(String applicationUserUid) {
        this.applicationUserUid = applicationUserUid;
    }

    public String getApplicationUserName() {
        return applicationUserName;
    }

    public void setApplicationUserName(String applicationUserName) {
        this.applicationUserName = applicationUserName;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getUpdatedAssetUid() {
        return updatedAssetUid;
    }

    public void setUpdatedAssetUid(String updatedAssetUid) {
        this.updatedAssetUid = updatedAssetUid;
    }

    public Integer getApplicationPlanCount() {
        return applicationPlanCount;
    }

    public void setApplicationPlanCount(Integer applicationPlanCount) {
        this.applicationPlanCount = applicationPlanCount;
    }

    public BigDecimal getBudgetUnitPrice() {
        return budgetUnitPrice;
    }

    public void setBudgetUnitPrice(BigDecimal budgetUnitPrice) {
        this.budgetUnitPrice = budgetUnitPrice;
    }

    public BigDecimal getBudgetTotalAmount() {
        return budgetTotalAmount;
    }

    public void setBudgetTotalAmount(BigDecimal budgetTotalAmount) {
        this.budgetTotalAmount = budgetTotalAmount;
    }

    public Integer getEstimatedServiceYears() {
        return estimatedServiceYears;
    }

    public void setEstimatedServiceYears(Integer estimatedServiceYears) {
        this.estimatedServiceYears = estimatedServiceYears;
    }

    public Integer getSimilarEquipmentCount() {
        return similarEquipmentCount;
    }

    public void setSimilarEquipmentCount(Integer similarEquipmentCount) {
        this.similarEquipmentCount = similarEquipmentCount;
    }

    public BigDecimal getCurrentEquipmentServiceYears() {
        return currentEquipmentServiceYears;
    }

    public void setCurrentEquipmentServiceYears(BigDecimal currentEquipmentServiceYears) {
        this.currentEquipmentServiceYears = currentEquipmentServiceYears;
    }

    public String getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource;
    }

    public String getFundingImplemented() {
        return fundingImplemented;
    }

    public void setFundingImplemented(String fundingImplemented) {
        this.fundingImplemented = fundingImplemented;
    }

    public String getMedicalEquipmentLicense() {
        return medicalEquipmentLicense;
    }

    public void setMedicalEquipmentLicense(String medicalEquipmentLicense) {
        this.medicalEquipmentLicense = medicalEquipmentLicense;
    }

    public BigDecimal getSiteRenovationCost() {
        return siteRenovationCost;
    }

    public void setSiteRenovationCost(BigDecimal siteRenovationCost) {
        this.siteRenovationCost = siteRenovationCost;
    }

    public String getPurchaseReason() {
        return purchaseReason;
    }

    public void setPurchaseReason(String purchaseReason) {
        this.purchaseReason = purchaseReason;
    }

    public Integer getNewStaffAllocation() {
        return newStaffAllocation;
    }

    public void setNewStaffAllocation(Integer newStaffAllocation) {
        this.newStaffAllocation = newStaffAllocation;
    }

    public BigDecimal getDepreciationCost() {
        return depreciationCost;
    }

    public void setDepreciationCost(BigDecimal depreciationCost) {
        this.depreciationCost = depreciationCost;
    }

    public BigDecimal getMaintenanceCost() {
        return maintenanceCost;
    }

    public void setMaintenanceCost(BigDecimal maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }

    public BigDecimal getConsumablesCost() {
        return consumablesCost;
    }

    public void setConsumablesCost(BigDecimal consumablesCost) {
        this.consumablesCost = consumablesCost;
    }

    public Integer getAnnualServiceVolume() {
        return annualServiceVolume;
    }

    public void setAnnualServiceVolume(Integer annualServiceVolume) {
        this.annualServiceVolume = annualServiceVolume;
    }

    public BigDecimal getAnnualServiceIncome() {
        return annualServiceIncome;
    }

    public void setAnnualServiceIncome(BigDecimal annualServiceIncome) {
        this.annualServiceIncome = annualServiceIncome;
    }

    public BigDecimal getAnnualOperationEfficiencyWeight() {
        return annualOperationEfficiencyWeight;
    }

    public void setAnnualOperationEfficiencyWeight(BigDecimal annualOperationEfficiencyWeight) {
        this.annualOperationEfficiencyWeight = annualOperationEfficiencyWeight;
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

    public String getApplicationYear() {
        return applicationYear;
    }

    public void setApplicationYear(String applicationYear) {
        this.applicationYear = applicationYear;
    }

    public String getUpdatedAssetName() {
        return updatedAssetName;
    }

    public void setUpdatedAssetName(String updatedAssetName) {
        this.updatedAssetName = updatedAssetName;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public Integer getApplicationNoIndex() {
        return applicationNoIndex;
    }

    public void setApplicationNoIndex(Integer applicationNoIndex) {
        this.applicationNoIndex = applicationNoIndex;
    }

    public String getDrugCenterUid() {
        return drugCenterUid;
    }

    public void setDrugCenterUid(String drugCenterUid) {
        this.drugCenterUid = drugCenterUid;
    }

    public String getWorkGroupUid() {
        return workGroupUid;
    }

    public void setWorkGroupUid(String workGroupUid) {
        this.workGroupUid = workGroupUid;
    }

    public String getBureauPartyUid() {
        return bureauPartyUid;
    }

    public void setBureauPartyUid(String bureauPartyUid) {
        this.bureauPartyUid = bureauPartyUid;
    }

    public String getSpecificationsId() {
        return specificationsId;
    }

    public void setSpecificationsId(String specificationsId) {
        this.specificationsId = specificationsId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AcquisitionPlan)) {
            return false;
        }
        AcquisitionPlan other = (AcquisitionPlan) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
}
