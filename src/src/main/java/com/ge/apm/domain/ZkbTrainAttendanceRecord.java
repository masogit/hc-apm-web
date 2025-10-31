package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 工程师培训记录实体类
 */
@Data
@Entity
@Table(name = "zkb_train_attendance_record")
public class ZkbTrainAttendanceRecord {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    // 状态（如：Attended, Absent 等）
    @Column(name = "status")
    private String status;

    // 原因
    @Column(name = "reason")
    private String reason;

    // 学员类型（如：ZKB 2025Q1）
    @Column(name = "trainee_type")
    private String traineeType;

    // 培训开始时间
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "train_start_date")
    private Date trainStartDate;

    // 培训结束时间
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "train_end_date")
    private Date trainEndDate;

    // 课程名称
    @Column(name = "course_name")
    private String courseName;

    // Modality（培训模式标识，如 WIX）
    @Column(name = "modality")
    private String modality;

    // 培训老师
    @Column(name = "trainer_name")
    private String trainerName;

    // 培训形式（如：Full classroom）
    @Column(name = "train_format")
    private String trainFormat;

    // 培训地点
    @Column(name = "location")
    private String location;

    // 公司名称
    @Column(name = "company_name")
    private String companyName;

    // 合同号
    @Column(name = "contract_no")
    private String contractNo;

    // 合同唯一号 / Unic ID
    @Column(name = "contract_unique_id")
    private String contractUniqueId;

    // 学员姓名
    @Column(name = "trainee_name")
    private String traineeName;

    // SSO 账号
    @Column(name = "sso")
    private String sso;

    // 代理商租户Id
    @Column(name = "tenant_uid")
    private String tenantUid;

    // 电话
    @Column(name = "phone")
    private String phone;

    // 邮箱
    @Column(name = "email")
    private String email;

    // 身份证号
    @Column(name = "id_card_no")
    private String idCardNo;

    // 商备邮箱
    @Column(name = "business_email")
    private String businessEmail;

    // Online 成绩
    @Column(name = "online_score")
    private String onlineScore;

    // Lab + 笔试成绩（如 "40+48"）
    @Column(name = "lab_written_score")
    private String labWrittenScore;

    // Final 成绩（数值型）
    @Column(name = "final_score")
    private Double finalScore;

    // 学位/评级（如：良好、优秀等）
    @Column(name = "degree")
    private String degree;

    // 是否通过（"通过" / "未通过"）
    @Column(name = "is_passed")
    private String isPassed;

    @Column(name = "created_by")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @Transient
    private Integer excelIdex;

    @Transient
    private String errorMsg = "";

    public void setErrorMsg(UploadFileErrorTypeEnum type,String errorMsg) {
        this.errorMsg += errorMsg;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ZkbTrainAttendanceRecord)) return false;
        ZkbTrainAttendanceRecord that = (ZkbTrainAttendanceRecord) obj;
        return id != null && id.equals(that.id);
    }
}