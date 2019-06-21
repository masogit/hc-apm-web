/*
 */
package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;


@Entity
@Data
@Table(name = "measuring_order")
public class MeasuringOrder implements Serializable {

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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

    @Column(name = "plan_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date planTime;  //计划时间

    @Column(name = "origin_plan_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date originPlanTime;  //原计划时间

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "bo_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "bo_type = 'PmOrder'")
    private Set<V2_BlobObject> attachments = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "wo_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Set<MeasuringOrderDetail> details = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "wo_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Set<MeasuringOrderCoworker> coworkers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="asset_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    private AssetInfo assetInfo;

    @Column(name = "nearest_sr_days")
    private Integer nearestSrDays;//此次保养后，最近的报修距此保养天数
    @Column(name = "nearest_sr_id")
    private String nearestSrId;//此次保养后，最近的报修SR ID

    @Column(name = "measuring_num")
    private String measuringNum;

    @Column(name="inst_id")
    private String instId;
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
    private String qualityChecking;

    @Column(name="approval_status")
    private String approvalStatus;

    @Column(name="finish_on_time")
    private String finishOnTime;

    @Column(name="simplify")
    private Boolean simplify;

    @Column(name="measuring_type")
    private Integer measuringType;

    @Column(name="int_ext_type")
    private Integer intExtType;

    @Column(name = "signature")
    private String signature;

    @Column(name = "cancel_person")
    private String cancelPerson;

    @Column(name = "cancel_reason")
    private String cancelReason;

    public MeasuringOrder() {
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

    public Set<V2_BlobObject> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<V2_BlobObject> attachments) {
        this.attachments = attachments;
    }

    public MeasuringOrder(String id, String creatorId, String creatorName, Date createTime, boolean isFinished) {
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

    public String getMeasuringNum() {
        return measuringNum;
    }

    public void setMeasuringNum(String measuringNum) {
        this.measuringNum = measuringNum;
    }

    public Integer getMeasuringType() {
        return measuringType;
    }

    public void setMeasuringType(Integer measuringType) {
        this.measuringType = measuringType;
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

    public Set<MeasuringOrderDetail> getDetails() {
        return details;
    }

    public void setDetails(Set<MeasuringOrderDetail> details) {
        this.details = details;
    }

    public Set<MeasuringOrderCoworker> getCoworkers() {
        return coworkers;
    }

    public void setCoworkers(Set<MeasuringOrderCoworker> coworkers) {
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

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public Integer getRuleLevel() {
        return ruleLevel;
    }

    public void setRuleLevel(Integer ruleLevel) {
        this.ruleLevel = ruleLevel;
    }

    public Date getOriginPlanTime() {
        return originPlanTime;
    }

    public void setOriginPlanTime(Date originPlanTime) {
        this.originPlanTime = originPlanTime;
    }

    public Boolean getSimplify() {
        return simplify;
    }

    public void setSimplify(Boolean simplify) {
        this.simplify = simplify;
    }

    public Integer getIntExtType() {
        return intExtType;
    }

    public void setIntExtType(Integer intExtType) {
        this.intExtType = intExtType;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getCancelPerson() {
        return cancelPerson;
    }

    public void setCancelPerson(String cancelPerson) {
        this.cancelPerson = cancelPerson;
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
        if (!(object instanceof MeasuringOrder)) {
            return false;
        }
        MeasuringOrder other = (MeasuringOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
