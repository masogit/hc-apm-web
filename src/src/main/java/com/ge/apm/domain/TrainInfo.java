package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "train_info")
public class TrainInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "status")
    private String status;

    //课程号
    @Column(name = "course_no")
    private String courseNo;

    @Column(name = "train_type")
    private String trainType;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "plan_train_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date plannTrainDate;

    @Column(name = "actual_train_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date actualTrainDate;

    @Column(name = "train_mode")
    private String trainMode;

    @Column(name = "course_content")
    @Size(max = 40000)
    private String courseContent;

    @Column(name = "course_objective")
    private String courseObjective;

    @Column(name = "train_qty")
    private Integer trainQty;

    //培训科室
    @Column(name = "training_dept_uid")
    @Size(max = 4000)
    private String trainingDeptUid;

    //培训科室数量
    @Column(name = "training_dept_count")
    private Integer trainingDeptCount;

    //培训角色
    @Column(name = "training_role_uid")
    @Size(max = 2000)
    private String trainingRoleUid;

    //培训人数
    @Column(name = "training_people_count")
    private Integer trainingPeopleCount;

    @Column(name = "contract_no")
    private String contractNo;

    @Column(name = "asset_group")
    private String assetGroup;

    @Column(name = "contract_content")
    private String contractContent;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name="org_uid",columnDefinition = "CHAR(32)")
    private String orgUid;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_uid")
    private String createdUid;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedDate;

    @Column(name = "comment")
    private String comment;


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TrainInfo)) {
            return false;
        }
        TrainInfo other = (TrainInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        if ((this.courseNo == null && other.courseNo != null) || (this.courseNo != null && !this.courseNo.equals(other.courseNo))) {
            return false;
        }
        if ((this.trainType == null && other.trainType != null) || (this.trainType != null && !this.trainType.equals(other.trainType))) {
            return false;
        }
        if ((this.courseName == null && other.courseName != null) || (this.courseName != null && !this.courseName.equals(other.courseName))) {
            return false;
        }
        if ((this.trainMode == null && other.trainMode != null) || (this.trainMode != null && !this.trainMode.equals(other.trainMode))) {
            return false;
        }
        if ((this.courseContent == null && other.courseContent != null) || (this.courseContent != null && !this.courseContent.equals(other.courseContent))) {
            return false;
        }
        if ((this.courseObjective == null && other.courseObjective != null) || (this.courseObjective != null && !this.courseObjective.equals(other.courseObjective))) {
            return false;
        }
        if ((this.trainingDeptUid == null && other.trainingDeptUid != null) || (this.trainingDeptUid != null && !this.trainingDeptUid.equals(other.trainingDeptUid))) {
            return false;
        }
        if ((this.trainingRoleUid == null && other.trainingRoleUid != null) || (this.trainingRoleUid != null && !this.trainingRoleUid.equals(other.trainingRoleUid))) {
            return false;
        }
        if ((this.trainingPeopleCount == null && other.trainingPeopleCount != null) || (this.trainingPeopleCount != null && !this.trainingPeopleCount.equals(other.trainingPeopleCount))) {
            return false;
        }
        if ((this.contractNo == null && other.contractNo != null) || (this.contractNo != null && !this.contractNo.equals(other.contractNo))) {
            return false;
        }
        if ((this.assetGroup == null && other.assetGroup != null) || (this.assetGroup != null && !this.assetGroup.equals(other.assetGroup))) {
            return false;
        }
        if ((this.contractContent == null && other.contractContent != null) || (this.contractContent != null && !this.contractContent.equals(other.contractContent))) {
            return false;
        }
        return true;
    }
}
