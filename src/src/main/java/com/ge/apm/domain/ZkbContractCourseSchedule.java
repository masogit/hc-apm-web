package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 课程计划表
 */
@Entity
@Table(name = "zkb_contract_course_schedule")
public class ZkbContractCourseSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 课程ID
     */
    @Column(name = "cid")
    private String cid;

    /**
     * 课程名称
     */
    @Column(name = "cname")
    private String cname;

    /**
     * 课程管理主键
     */
    @Column(name = "zkb_course_id")
    private String zkbCourseId;

    /**
     * modality
     */
    @Column(name = "modality")
    private String modality;

    /**
     * 开始时间
     */
    @Column(name = "start_date")
    private Date startDate;

    /**
     * 结束时间
     */
    @Column(name = "end_date")
    private Date endDate;

    /**
     * 培训天数
     */
    @Column(name = "training_days")
    private Integer trainingDays;

    /**
     * 主讲老师
     */
    @Column(name = "instructor")
    private String instructor;

    /**
     * 观众
     */
    @Column(name = "audience")
    private String audience;

    /**
     * 位置
     */
    @Column(name = "location")
    private String location;

    /**
     * 个数
     */
    @Column(name = "student_class")
    private Integer studentClass;

    /**
     * 批次号
     */
    @Column(name = "batch_no")
    private String batchNo;

    /**
     * 删除状态
     */
    @Column(name = "deleted")
    private String deleted;

    /**
     * 生效状态
     */
    @Column(name = "valid")
    private String valid;

    /**
     * 扣除个数
     */
    @Column(name = "deduct_count")
    private Double deductCount;

    /**
     * 培训类型
     */
    @Column(name = "type")
    private String type;

    @Transient
    private String zkbContractCourseId;

    @Transient
    private Double prePersonCount;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ZkbContractCourse)) {
            return false;
        }
        if(this==object){
            return true;
        }
        ZkbContractCourseSchedule other = (ZkbContractCourseSchedule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbContractCourseSchedule[ id=" + id + " ]";
    }

    public Double getPrePersonCount() {
        return prePersonCount;
    }

    public void setPrePersonCount(Double prePersonCount) {
        this.prePersonCount = prePersonCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getZkbCourseId() {
        return zkbCourseId;
    }

    public void setZkbCourseId(String zkbCourseId) {
        this.zkbCourseId = zkbCourseId;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
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

    public Integer getTrainingDays() {
        return trainingDays;
    }

    public void setTrainingDays(Integer trainingDays) {
        this.trainingDays = trainingDays;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(Integer studentClass) {
        this.studentClass = studentClass;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public Double getDeductCount() {
        return deductCount;
    }

    public void setDeductCount(Double deductCount) {
        this.deductCount = deductCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getZkbContractCourseId() {
        return zkbContractCourseId;
    }

    public void setZkbContractCourseId(String zkbContractCourseId) {
        this.zkbContractCourseId = zkbContractCourseId;
    }
}
