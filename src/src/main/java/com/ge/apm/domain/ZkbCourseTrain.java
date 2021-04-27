package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2021/4/8
 * @description :
 */
@Entity
@Table(name = "zkb_course_train")
public class ZkbCourseTrain implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum ParticipateStatusEnum{
        Y,N
    }

    public enum FlowStatusEnum{
        ScheduleJoin,ScheduleNoJoin,LetterSubmit,LetterCancelJoin,ApplySubmit,ApplyCancelJoin,LetterCancelAccepted,LetterCancelRejected,ApplyCancelAccepted,ApplyCancelRejected
    }

    public enum FlowStepEnum{
        LetterPending,LetterCancelAuditPending,ApplyPending,ApplyCancelAuditPending,Completed
    }

    public enum TrainTypeEnum{
        Online, Offline
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;


    /**
     * 租户
     */
    @Column(name = "tenant_uid")
    private String tenantUID;


    /**
     * 课表批次号
     */
    @Column(name = "batch_no")
    private String batchNo;

    /**
     * 课表id
     */
    @Column(name = "course_schedule_id")
    private String courseScheduleId;

    /**
     * 课程名称
     */
    @Column(name = "course_name")
    private String courseName;

    /**
     * 课程id
     */
    @Column(name = "course_id")
    private String courseId;

    /**
     * 培训天数
     */
    @Column(name = "training_days")
    private Integer trainingDays;

    /**
     * 参加状态
     */
    @Column(name = "participate_status")
    private String participateStatus;

    /**
     * 流程状态
     */
    @Column(name = "current_status")
    private String currentStatus;

    @Column(name = "current_step")
    private String currentStep;

    /**
     * 合同课程id
     */
    @Column(name = "contract_course_id")
    private String contractCourseId;

    /**
     * 课程培训号
     */
    @Column(name = "train_no")
    private String trainNo;

    /**
     * 唯一编号自增索引
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "train_no_index")
    private Integer trainNoIndex;

    /**
     * 培训状态
     * 线上 online
     * 线下 offline
     */
    @Column(name = "train_type")
    private String trainType;

    /**
     * 报名时间
     */
    @Column(name = "sign_date")
    private Date signDate;

    /**
     * 主讲老师
     */
    @Column(name = "instructor")
    private String instructor;

    /**
     * 位置
     */
    @Column(name = "location")
    private String location;

    /**
     * 预扣除数量
     */
    @Column(name = "pre_deduct_count")
    private Double preDeductCount;

    /**
     * 实际扣除数量
     */
    @Column(name = "actual_deduct_count")
    private Double actualDeductCount;

    @Column(name = "remark")
    @Size(max = 2000)
    private String remark;

    @Column(name = "reason")
    @Size(max = 2000)
    private String reason;

    @Column(name = "reason_auditer")
    private String reasonAuditer;

    @Column(name = "reason_auditer_uid")
    private String reasonAuditerUid;

    @Column(name = "reason_audit_date")
    private Date reasonAuditDate;

    @Column(name = "letter_auditer")
    private String letterAuditer;

    @Column(name = "letter_auditer_uid")
    private String letterAuditerUid;

    @Column(name = "letter_audit_date")
    private Date letterAuditDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    public Integer getTrainNoIndex() {
        return trainNoIndex;
    }

    public void setTrainNoIndex(Integer trainNoIndex) {
        this.trainNoIndex = trainNoIndex;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getCourseScheduleId() {
        return courseScheduleId;
    }

    public void setCourseScheduleId(String courseScheduleId) {
        this.courseScheduleId = courseScheduleId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public Integer getTrainingDays() {
        return trainingDays;
    }

    public void setTrainingDays(Integer trainingDays) {
        this.trainingDays = trainingDays;
    }

    public String getParticipateStatus() {
        return participateStatus;
    }

    public void setParticipateStatus(String participateStatus) {
        this.participateStatus = participateStatus;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getContractCourseId() {
        return contractCourseId;
    }

    public void setContractCourseId(String contractCourseId) {
        this.contractCourseId = contractCourseId;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getPreDeductCount() {
        return preDeductCount;
    }

    public void setPreDeductCount(Double preDeductCount) {
        this.preDeductCount = preDeductCount;
    }

    public Double getActualDeductCount() {
        return actualDeductCount;
    }

    public void setActualDeductCount(Double actualDeductCount) {
        this.actualDeductCount = actualDeductCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReasonAuditer() {
        return reasonAuditer;
    }

    public void setReasonAuditer(String reasonAuditer) {
        this.reasonAuditer = reasonAuditer;
    }

    public String getReasonAuditerUid() {
        return reasonAuditerUid;
    }

    public void setReasonAuditerUid(String reasonAuditerUid) {
        this.reasonAuditerUid = reasonAuditerUid;
    }

    public Date getReasonAuditDate() {
        return reasonAuditDate;
    }

    public void setReasonAuditDate(Date reasonAuditDate) {
        this.reasonAuditDate = reasonAuditDate;
    }

    public String getLetterAuditer() {
        return letterAuditer;
    }

    public void setLetterAuditer(String letterAuditer) {
        this.letterAuditer = letterAuditer;
    }

    public String getLetterAuditerUid() {
        return letterAuditerUid;
    }

    public void setLetterAuditerUid(String letterAuditerUid) {
        this.letterAuditerUid = letterAuditerUid;
    }

    public Date getLetterAuditDate() {
        return letterAuditDate;
    }

    public void setLetterAuditDate(Date letterAuditDate) {
        this.letterAuditDate = letterAuditDate;
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
        if (!(object instanceof ZkbCourseTrain)) {
            return false;
        }
        ZkbCourseTrain other = (ZkbCourseTrain) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
