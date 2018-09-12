/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author 212547631
 */
@Entity
@Table(name = "v2_pm_order")
public class V2_PmOrder  implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name="id")
    private String id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creator_id")
    private String creatorId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "creator_name")
    private String creatorName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;
    @Column(name = "owner_id")
    private String ownerId;
    @Size(max = 64)
    @Column(name = "owner_name")
    private String ownerName;
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;
    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_finished")
    private boolean isFinished;
    @Size(max = 256)
    @Column(name = "comments")
    private String comments;
    @Column(name="pm_type")
    private Integer pmType;
    @Column(name = "asset_id")
    @Basic(optional = false)
    @NotNull
    private Integer assetId;

    @Column(name = "asset_uid")
    @Basic(optional = false)
    @NotNull
    private String assetUid;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "total_man_hour")
    private Double totalManHour;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "man_hours")
    private Double manHours;

    @Column(name="simplify")
    private Boolean simplify;

    @Column(name="signature")
    private String signature;

    @Column(name="int_ext_type")
    private Integer intExtType;

    @Column(name = "plan_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date planTime;  //计划时间

    @JoinColumn(name = "asset_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private AssetInfo assetInfo;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bo_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "bo_type = 'PmOrder'")
    @org.hibernate.annotations.ForeignKey(name = "none")
    private List<V2_BlobObject> attachments = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "wo_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    private List<V2_WorkOrder_Detail> details;

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "wo_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    private List<V2_WorkOrderCoworker> coworkers;

    @Column(name = "nearest_sr_days")
    private Integer nearestSrDays;//此次保养后，最近的报修距此保养天数
    @Column(name = "nearest_sr_id")
    private String nearestSrId;//此次保养后，最近的报修SR ID

    @Column(name="pm_num")
    private String pmNum;

    @Column(name="inst_id")
    private String instId;
    //    @Column(name = "feedback_rating")
//    private Integer feedbackRating;
//
//    @Column(name = "feedback_comment")
//    private String feedbackComment;
    @Column(name = "current_step_id")
    private String currentStepId;
    @Column(name = "current_step_name")
    private String currentStepName;
    @Column(name = "current_person_id")
    private String currentPersonId;
    @Size(max = 64)
    @Column(name = "current_person_name")
    private String currentPersonName;
    @Column(name="rule_level")
    private Integer ruleLevel;
    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name="site_uid",columnDefinition = "CHAR(32)")
    private String siteUID;
    /**
     * 1: auto dispatch 2: grab and manual 3: grab 4: manual
     */
    @Column(name="dispatch_mode")
    private Integer dispatchMode;
    @Column(name="clinical_dept_id")
    private String clinicalDeptId;
    @Column(name="quality_checking")
    private String qualityChecking; //默认是合格
    @Column(name="approval_status")
    private String approvalStatus;
    @Transient
    @JsonIgnore
    private String strPlanTime;
    
    @Transient
    @JsonIgnore
    private String strFinishTime;

    public V2_PmOrder() {
    }

    public Integer getNearestSrDays() {
        return nearestSrDays;
    }

    public void setNearestSrDays(Integer nearestSrDays) {
        this.nearestSrDays = nearestSrDays;
    }

    public String getNearestSrId() {
        return nearestSrId;
    }

    public void setNearestSrId(String nearestSrId) {
        this.nearestSrId = nearestSrId;
    }

    public List<V2_BlobObject> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<V2_BlobObject> attachments) {
        this.attachments = attachments;
    }

    public V2_PmOrder(String id, String creatorId, String creatorName, Date createTime, boolean isFinished) {
        this.id = id;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.createTime = createTime;
        this.isFinished = isFinished;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
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

    public Double getManHours() {
        return manHours;
    }

    public void setManHours(Double manHours) {
        this.manHours = manHours;
    }

    public Date getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Date planTime) {
        this.planTime = planTime;
    }

    public String getPmNum() {
        return pmNum;
    }

    public void setPmNum(String pmNum) {
        this.pmNum = pmNum;
    }

    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

//    public Integer getFeedbackRating() {
//        return feedbackRating;
//    }

//    public void setFeedbackRating(Integer feedbackRating) {
//        this.feedbackRating = feedbackRating;
//    }

//    public String getFeedbackComment() {
//        return feedbackComment;
//    }

//    public void setFeedbackComment(String feedbackComment) {
//        this.feedbackComment = feedbackComment;
//    }

    public String getStrPlanTime() {
    	if(this.planTime!=null){
    		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    		strPlanTime=sdf.format(this.planTime);
    	}
		return strPlanTime;
	}

	public void setStrPlanTime(String strPlanTime) {
		this.strPlanTime = strPlanTime;
	}

	public String getStrFinishTime() {
		if(this.endTime!=null){
    		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    		strFinishTime=sdf.format(this.endTime);
    	}
		return strFinishTime;
	}

	public void setStrFinishTime(String strFinishTime) {
		this.strFinishTime = strFinishTime;
	}

	public String getCurrentStepId() {
        return currentStepId;
    }

    public void setCurrentStepId(String currentStepId) {
        this.currentStepId = currentStepId;
    }

    public String getCurrentStepName() {
        return currentStepName;
    }

    public void setCurrentStepName(String currentStepName) {
        this.currentStepName = currentStepName;
    }

    public List<V2_WorkOrder_Detail> getDetails() {
        return details;
    }

    public void setDetails(List<V2_WorkOrder_Detail> details) {
        this.details = details;
    }

    public List<V2_WorkOrderCoworker> getCoworkers() {
        return coworkers;
    }

    public void setCoworkers(List<V2_WorkOrderCoworker> coworkers) {
        this.coworkers = coworkers;
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

    public Integer getDispatchMode() {
        return dispatchMode;
    }

    public void setDispatchMode(Integer dispatchMode) {
        this.dispatchMode = dispatchMode;
    }

    public String getClinicalDeptId() {
        return clinicalDeptId;
    }

    public void setClinicalDeptId(String clinicalDeptId) {
        this.clinicalDeptId = clinicalDeptId;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getQualityChecking() {
        return qualityChecking;
    }

    public void setQualityChecking(String qualityChecking) {
        this.qualityChecking = qualityChecking;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
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

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof V2_PmOrder)) {
            return false;
        }
        V2_PmOrder other = (V2_PmOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Integer getRuleLevel() {
        return ruleLevel;
    }

    public void setRuleLevel(Integer ruleLevel) {
        this.ruleLevel = ruleLevel;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public Boolean getSimplify() {
        return simplify;
    }

    public void setSimplify(Boolean simplify) {
        this.simplify = simplify;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Integer getPmType() {
        return pmType;
    }

    public void setPmType(Integer pmType) {
        this.pmType = pmType;
    }

    public Integer getIntExtType() {
        return intExtType;
    }

    public void setIntExtType(Integer intExtType) {
        this.intExtType = intExtType;
    }
}
