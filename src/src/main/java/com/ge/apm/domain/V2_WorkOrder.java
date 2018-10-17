package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "v2_work_order")
/*@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)*/
public class V2_WorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "sr_id")
    private String srId;

    @JoinColumn(name = "sr_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private V2_ServiceRequest serviceRequest;
    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private Integer siteId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "hospital_id")
    private Integer hospitalId;

    @Column(name = "asset_id")
    @Basic(optional = false)
    @NotNull
    private Integer assetId;

    @JoinColumn(name = "asset_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private AssetInfo assetInfo;

    @Basic(optional = false)
    @NotNull
    @Column(name = "current_person_id")
    private Integer currentPersonId;

    @Size(max = 16)
    @Column(name = "current_person_name")
    private String currentPersonName;

    @Basic(optional = false)
    @NotNull
    @Column(name = "current_step_id")
    private Integer currentStepId;

    @Column(name = "case_type")
    private Integer caseType;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "current_step_name")
    private String currentStepName;

    @Column(name = "total_man_hour")
    private Double totalManHour;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "status")
    private Integer status;

    @Column(name = "int_ext_type")
    private Integer intExtType;

    @Column(name = "parent_wo_id")
    private Integer parentWoId;

    @Column(name = "feedback_rating")
    private Integer feedbackRating;

    @Column(name = "feedback_comment")
    private String feedbackComment;

    @Column(name = "pat_problems")
    private String patProblems;

    @Column(name = "pat_actions")
    private String patActions;

    @Column(name = "pat_tests")
    private String patTests;

    @Column(name = "close_time")
    private Date closeTime;

    @Column(name = "checkin")
    private Integer checkin;

    //	    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,targetEntity=V2_BlobObject.class) 
//	    @Where(clause="bo_uuid=id")
//	    private List<V2_BlobObject> attachments ;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "wo_id")
    private List<V2_WorkOrder_Step> v2_workOrder_step_list;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "wo_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    private List<V2_WorkOrder_Detail> v2_workOrder_detail_list;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "wo_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Fetch(FetchMode.SUBSELECT)
    @org.hibernate.annotations.ForeignKey(name = "none")
    private List<V2_WorkOrderCoworker> v2_workOrder_coworker_list;

    @Column(name = "comment")
    private String comment;

    @Column(name = "repair_type")
    private Integer repairType;

    @Column(name = "repair_result", length = 2)
    private String repairResult;

    @Column(name = "checkin_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date checkinTime;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    @Column(name = "other_expense")
    private Double otherExpense;

    @Column(name = "man_hours")
    private Double manHours;

    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "external_expense")
    private Double externalExpense;

    /**
     * 1: auto dispatch 2: grab and manual 3: grab 4: manual
     */
    @Column(name = "dispatch_mode")
    private Integer dispatchMode;

    @Column(name = "approver_id")
    private Integer approverId;
    @Column(name = "approver_name")
    private String approverName;

    @Column(name = "signature")
    private String signature;

    @Column(name = "ack_signature")
    private String ackSignature;
    @Column(name="approval_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date approvalDate;
    //维修价值
    @Column(name = "maintenance_value")
    private Double maintenanceValue;
    
    //MVS工单类型
    @Column(name="mvs_type")
    private Integer mvsType;
    
    @Transient
    private UserAccount FEOwner;
    
    public Integer getDispatchMode() {
        return dispatchMode;
    }

    public void setDispatchMode(Integer dispatchMode) {
        this.dispatchMode = dispatchMode;
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

    public Date getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(Date checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRepairType() {
        return repairType;
    }

    public void setRepairType(Integer repairType) {
        this.repairType = repairType;
    }

    public List<V2_WorkOrder_Step> getV2_workOrder_step_list() {
        return v2_workOrder_step_list;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setV2_workOrder_step_list(List<V2_WorkOrder_Step> v2_workOrder_step_list) {
        this.v2_workOrder_step_list = v2_workOrder_step_list;
    }

    public List<V2_WorkOrderCoworker> getV2_workOrder_coworker_list() {
        return v2_workOrder_coworker_list;
    }

    public void setV2_workOrder_coworker_list(List<V2_WorkOrderCoworker> v2_workOrder_coworker_list) {
        this.v2_workOrder_coworker_list = v2_workOrder_coworker_list;
    }

    public List<V2_WorkOrder_Detail> getV2_workOrder_detail_list() {
        return v2_workOrder_detail_list;
    }

    public void setV2_workOrder_detail_list(List<V2_WorkOrder_Detail> v2_workOrder_detail_list) {
        this.v2_workOrder_detail_list = v2_workOrder_detail_list;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getSrId() {
        return srId;
    }

    public void setSrId(String srId) {
        this.srId = srId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public Integer getCurrentPersonId() {
        return currentPersonId;
    }

    public void setCurrentPersonId(Integer currentPersonId) {
        this.currentPersonId = currentPersonId;
    }

    public String getCurrentPersonName() {
        return currentPersonName;
    }

    public void setCurrentPersonName(String currentPersonName) {
        this.currentPersonName = currentPersonName;
    }

    public Integer getCurrentStepId() {
        return currentStepId;
    }

    public void setCurrentStepId(Integer currentStepId) {
        this.currentStepId = currentStepId;
    }

    public String getCurrentStepName() {
        return currentStepName;
    }

    public void setCurrentStepName(String currentStepName) {
        this.currentStepName = currentStepName;
    }

    public Double getTotalManHour() {
        return totalManHour;
    }

    public void setTotalManHour(Double totalManHour) {
        this.totalManHour = totalManHour;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIntExtType() {
        return intExtType;
    }

    public void setIntExtType(Integer intExtType) {
        this.intExtType = intExtType;
    }

    public Integer getParentWoId() {
        return parentWoId;
    }

    public void setParentWoId(Integer parentWoId) {
        this.parentWoId = parentWoId;
    }

    public Integer getFeedbackRating() {
        return feedbackRating;
    }

    public void setFeedbackRating(Integer feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public String getFeedbackComment() {
        return feedbackComment;
    }

    public void setFeedbackComment(String feedbackComment) {
        this.feedbackComment = feedbackComment;
    }

    public String getPatProblems() {
        return patProblems;
    }

    public void setPatProblems(String patProblems) {
        this.patProblems = patProblems;
    }

    public String getPatActions() {
        return patActions;
    }

    public void setPatActions(String patActions) {
        this.patActions = patActions;
    }

    public String getPatTests() {
        return patTests;
    }

    public void setPatTests(String patTests) {
        this.patTests = patTests;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public Integer getCaseType() {
        return caseType;
    }

    public void setCaseType(Integer caseType) {
        this.caseType = caseType;
    }

    public Integer getCheckin() {
        return checkin;
    }

    public void setCheckin(Integer checkin) {
        this.checkin = checkin;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Double getOtherExpense() {
        return otherExpense;
    }

    public void setOtherExpense(Double otherExpense) {
        this.otherExpense = otherExpense;
    }

    public Double getManHours() {
        return manHours;
    }

    public void setManHours(Double manHours) {
        this.manHours = manHours;
    }

    public V2_ServiceRequest getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(V2_ServiceRequest serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Double getExternalExpense() {
        return externalExpense;
    }

    public void setExternalExpense(Double externalExpense) {
        this.externalExpense = externalExpense;
    }

    public String getRepairResult() {
        return repairResult;
    }

    public void setRepairResult(String repairResult) {
        this.repairResult = repairResult;
    }

    public Integer getApproverId() {
        return approverId;
    }

    public void setApproverId(Integer approverId) {
        this.approverId = approverId;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAckSignature() {
        return ackSignature;
    }

    public void setAckSignature(String ackSignature) {
        this.ackSignature = ackSignature;
    }

    public Double getMaintenanceValue() {
        return maintenanceValue;
    }

    public void setMaintenanceValue(Double maintenanceValue) {
        this.maintenanceValue = maintenanceValue;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

	public Integer getMvsType() {
		return mvsType;
	}

	public void setMvsType(Integer mvsType) {
		this.mvsType = mvsType;
	}

	public UserAccount getFEOwner() {
		return FEOwner;
	}

	public void setFEOwner(UserAccount fEOwner) {
		FEOwner = fEOwner;
	}

	@Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof V2_WorkOrder)) {
            return false;
        }
        V2_WorkOrder other = (V2_WorkOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
