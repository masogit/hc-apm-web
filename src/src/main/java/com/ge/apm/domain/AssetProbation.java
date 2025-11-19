package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "asset_probation")
public class AssetProbation implements Serializable {

    private static final long serialVersionUid = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;
    @Basic(optional = false)
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUid;//所属科室
    @Size(max = 64)
    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;
    @Column(name = "asset_dept_uid", columnDefinition = "CHAR(32)")
    private String assetDeptUid;//使用科室
    @Column(name = "asset_dept_name")
    private String assetDeptName;
    @Column(name = "dept_contact_uid")
    private String deptContactUid;//科室联系人
    @Column(name = "dept_contact_name")
    private String deptContactName;
    @Column(name = "dept_contact_tel")
    private String deptContactTel;//科室联系人电话
    @Column(name = "function_type")
    private String functionType;//规格型号
    @Column(name = "brand")
    private String brand;//品牌
    @Column(name = "company")
    private String company;//公司
    @Column(name = "company_contact_uid")
    private String companyContactUid;//公司联系人
    @Column(name = "company_contact_name")
    private String companyContactName;
    @Column(name = "company_contact_tel")
    private String companyContactTel;//公司联系人电话
    @Column(name = "status")
    private Integer status;//状态
    @Column(name = "approval_completed_date")
    @Temporal(TemporalType.DATE)
    private Date approvalCompletedDate;//审批完成日期
    @Column(name = "plan_start_date")
    @Temporal(TemporalType.DATE)
    private Date planStartDate;//计划开始日期
    @Column(name = "plan_end_date")
    @Temporal(TemporalType.DATE)
    private Date planEndDate;//计划结束日期
    @Column(name = "current_step")
    private String currentStep;//当前步骤
    @Column(name = "current_person_id")
    private String currentPersonId;//当前处理人
    @Size(max = 16)
    @Column(name = "current_person_name")
    private String currentPersonName;
    @Column(name = "has_certificate")
    private Boolean hasCertificate;//合格证
    @Size(max = 64)
    @Column(name = "specification")
    private String specification;//说明书
    @Size(max = 64)
    @Column(name = "information")
    private String information;//三证资料
    @Size(max = 64)
    @Column(name = "order_num")
    private String orderNum;//工单号
    @Column(name = "applicant_user_uid")
    private String applicantUserUid;//申请人
    @Column(name = "applicant_user_name")
    private String applicantUserName;
    @Column(name = "application_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applicationTime;//申请时间
    @Column(name = "detection_situation")
    private String detectionSituation;//检测情况
    @Column(name = "detection_user_uid")
    private String detectionUserUid;//检测人
    @Column(name = "detection_user_name")
    private String detectionUserName;
    @Column(name = "detection_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date detectionTime;//检测时间
    @Column(name = "probation_start_user_uid")
    private String probationStartUserUid;//试用开始人
    @Column(name = "probation_start_user_name")
    private String probationStartUserName;
    @Column(name = "probation_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date probationStartTime;//试用开始时间
    @Column(name = "probation_end_user_uid")
    private String probationEndUserUid;//试用结束人
    @Column(name = "probation_end_user_name")
    private String probationEndUserName;
    @Column(name = "probation_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date probationEndTime;//试用结束时间
    @Column(name = "evacuation_initiation_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date evacuationInitiationTime;//撤离发起时间
    @Column(name = "evacuation_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date evacuationEndTime;//撤离结束时间
    @Column(name = "evacuation_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date evacuationTime;//撤离时间
    @Column(name = "evacuation_asset_situation")
    private String evacuationAssetSituation;//撤离设备工况
    @Column(name = "evacuation_user_uid")
    private String evacuationUserUid;//撤离人
    @Column(name = "evacuation_user_name")
    private String evacuationUserName;
    @Column(name = "evacuation_remark")
    private String evacuationRemark;//撤离备注
    @Column(name = "close_user_uid")
    private String closeUserUid;//关单人
    @Column(name = "close_user_name")
    private String closeUserName;
    @Column(name = "close_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date closeTime;//关单时间
    @Column(name = "cancel_user_uid")
    private String cancelUserUid;//取消人
    @Column(name = "cancel_user_name")
    private String cancelUserName;
    @Column(name = "cancel_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cancelTime;//取消时间
    @Column(name = "cancel_reason")
    private String cancelReason;//取消原因

    @CreatedBy
    @Column(name = "created_by", nullable = true, length = 50, updatable = false)
    private String createdBy = "system";

    @CreatedDate
    @Column(name = "created_date", nullable = true)
    private Date createdDate = new Date();

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Date lastModifiedDate = new Date();

    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUid;

    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUid;

    @Column(name="site_uid",columnDefinition = "CHAR(32)")
    private String siteUid;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAssetDeptUid() {
        return assetDeptUid;
    }

    public void setAssetDeptUid(String assetDeptUid) {
        this.assetDeptUid = assetDeptUid;
    }

    public String getAssetDeptName() {
        return assetDeptName;
    }

    public void setAssetDeptName(String assetDeptName) {
        this.assetDeptName = assetDeptName;
    }

    public String getDeptContactUid() {
        return deptContactUid;
    }

    public void setDeptContactUid(String deptContactUid) {
        this.deptContactUid = deptContactUid;
    }

    public String getDeptContactName() {
        return deptContactName;
    }

    public void setDeptContactName(String deptContactName) {
        this.deptContactName = deptContactName;
    }

    public String getDeptContactTel() {
        return deptContactTel;
    }

    public void setDeptContactTel(String deptContactTel) {
        this.deptContactTel = deptContactTel;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyContactUid() {
        return companyContactUid;
    }

    public void setCompanyContactUid(String companyContactUid) {
        this.companyContactUid = companyContactUid;
    }

    public String getCompanyContactName() {
        return companyContactName;
    }

    public void setCompanyContactName(String companyContactName) {
        this.companyContactName = companyContactName;
    }

    public String getCompanyContactTel() {
        return companyContactTel;
    }

    public void setCompanyContactTel(String companyContactTel) {
        this.companyContactTel = companyContactTel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getApprovalCompletedDate() {
        return approvalCompletedDate;
    }

    public void setApprovalCompletedDate(Date approvalCompletedDate) {
        this.approvalCompletedDate = approvalCompletedDate;
    }

    public Date getPlanStartDate() {
        return planStartDate;
    }

    public void setPlanStartDate(Date planStartDate) {
        this.planStartDate = planStartDate;
    }

    public Date getPlanEndDate() {
        return planEndDate;
    }

    public void setPlanEndDate(Date planEndDate) {
        this.planEndDate = planEndDate;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public String getCurrentPersonId() {
        return currentPersonId;
    }

    public void setCurrentPersonId(String currentPersonId) {
        this.currentPersonId = currentPersonId;
    }

    public String getCurrentPersonName() {
        return currentPersonName;
    }

    public void setCurrentPersonName(String currentPersonName) {
        this.currentPersonName = currentPersonName;
    }

    public Boolean getHasCertificate() {
        return hasCertificate;
    }

    public void setHasCertificate(Boolean hasCertificate) {
        this.hasCertificate = hasCertificate;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getApplicantUserUid() {
        return applicantUserUid;
    }

    public void setApplicantUserUid(String applicantUserUid) {
        this.applicantUserUid = applicantUserUid;
    }

    public String getApplicantUserName() {
        return applicantUserName;
    }

    public void setApplicantUserName(String applicantUserName) {
        this.applicantUserName = applicantUserName;
    }

    public Date getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(Date applicationTime) {
        this.applicationTime = applicationTime;
    }

    public String getDetectionSituation() {
        return detectionSituation;
    }

    public void setDetectionSituation(String detectionSituation) {
        this.detectionSituation = detectionSituation;
    }

    public String getDetectionUserUid() {
        return detectionUserUid;
    }

    public void setDetectionUserUid(String detectionUserUid) {
        this.detectionUserUid = detectionUserUid;
    }

    public String getDetectionUserName() {
        return detectionUserName;
    }

    public void setDetectionUserName(String detectionUserName) {
        this.detectionUserName = detectionUserName;
    }

    public Date getDetectionTime() {
        return detectionTime;
    }

    public void setDetectionTime(Date detectionTime) {
        this.detectionTime = detectionTime;
    }

    public String getProbationStartUserUid() {
        return probationStartUserUid;
    }

    public void setProbationStartUserUid(String probationStartUserUid) {
        this.probationStartUserUid = probationStartUserUid;
    }

    public String getProbationStartUserName() {
        return probationStartUserName;
    }

    public void setProbationStartUserName(String probationStartUserName) {
        this.probationStartUserName = probationStartUserName;
    }

    public Date getProbationStartTime() {
        return probationStartTime;
    }

    public void setProbationStartTime(Date probationStartTime) {
        this.probationStartTime = probationStartTime;
    }

    public String getProbationEndUserUid() {
        return probationEndUserUid;
    }

    public void setProbationEndUserUid(String probationEndUserUid) {
        this.probationEndUserUid = probationEndUserUid;
    }

    public String getProbationEndUserName() {
        return probationEndUserName;
    }

    public void setProbationEndUserName(String probationEndUserName) {
        this.probationEndUserName = probationEndUserName;
    }

    public Date getProbationEndTime() {
        return probationEndTime;
    }

    public void setProbationEndTime(Date probationEndTime) {
        this.probationEndTime = probationEndTime;
    }

    public Date getEvacuationInitiationTime() {
        return evacuationInitiationTime;
    }

    public void setEvacuationInitiationTime(Date evacuationInitiationTime) {
        this.evacuationInitiationTime = evacuationInitiationTime;
    }

    public Date getEvacuationEndTime() {
        return evacuationEndTime;
    }

    public void setEvacuationEndTime(Date evacuationEndTime) {
        this.evacuationEndTime = evacuationEndTime;
    }

    public Date getEvacuationTime() {
        return evacuationTime;
    }

    public void setEvacuationTime(Date evacuationTime) {
        this.evacuationTime = evacuationTime;
    }

    public String getEvacuationAssetSituation() {
        return evacuationAssetSituation;
    }

    public void setEvacuationAssetSituation(String evacuationAssetSituation) {
        this.evacuationAssetSituation = evacuationAssetSituation;
    }

    public String getEvacuationUserUid() {
        return evacuationUserUid;
    }

    public void setEvacuationUserUid(String evacuationUserUid) {
        this.evacuationUserUid = evacuationUserUid;
    }

    public String getEvacuationUserName() {
        return evacuationUserName;
    }

    public void setEvacuationUserName(String evacuationUserName) {
        this.evacuationUserName = evacuationUserName;
    }

    public String getEvacuationRemark() {
        return evacuationRemark;
    }

    public void setEvacuationRemark(String evacuationRemark) {
        this.evacuationRemark = evacuationRemark;
    }

    public String getCloseUserUid() {
        return closeUserUid;
    }

    public void setCloseUserUid(String closeUserUid) {
        this.closeUserUid = closeUserUid;
    }

    public String getCloseUserName() {
        return closeUserName;
    }

    public void setCloseUserName(String closeUserName) {
        this.closeUserName = closeUserName;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public String getCancelUserUid() {
        return cancelUserUid;
    }

    public void setCancelUserUid(String cancelUserUid) {
        this.cancelUserUid = cancelUserUid;
    }

    public String getCancelUserName() {
        return cancelUserName;
    }

    public void setCancelUserName(String cancelUserName) {
        this.cancelUserName = cancelUserName;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AssetProbation)) {
            return false;
        }
        AssetProbation other = (AssetProbation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.AssetProbation[ id=" + id + " ]";
    }
    
}
