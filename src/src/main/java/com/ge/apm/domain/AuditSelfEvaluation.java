package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

/**
 *  自评
 */
@Entity
@Table(name = "audit_self_evaluation")
@Data
public class AuditSelfEvaluation {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;
    //批次
    @Column(name = "batch_version")
    private String batchVersion;
    // 日期
    @Column(name = "period")
    private Date period;
    // 评分标准（扣分、累加分、折算分...）
    @Column(name = "standard_type")
    private Integer standardType;
    // 标准分数
    @Column(name = "standard_score")
    private Double standardScore;
    // 自评分数
    @Column(name = "score")
    private  Double score;
    // 评分人uid
    @Column(name = "rater_uid")
    private String raterUid;
    // 评分人名称
    @Column(name = "rater_name")
    private String raterName;
    // 记录人Uid
    @Column(name = "record_uid")
    private String recordUid;
    // 记录人名称
    @Column(name = "record_name")
    private String recordName;
    // 现状
    @Column(name = "current_state")
    private String currentState;
    // 存在问题
    @Column(name = "exist_problems")
    private String existProblems;
    // 无法解决的问题
    @Column(name = "unsolvable_problem")
    private String unsolvableProblem;
    // 整改完成时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "correction_Complete_Time")
    private Date correctionCompleteTime;
    // 整改措施
    @Column(name = "corrective_measures")
    private String correctiveMeasures;

    @Column(name = "status")
    private Integer status;

    @Column(name = "case_uid")
    private String caseUid;

    @Column(name="entry_uid")
    private String entryUid;

    @Column(name = "system_entry_uid")
    private String systemEntryUid;
    // 创建人
    @Column(name = "creator")
    private String creator;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "modify_by")
    private String modifyBy;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_time")
    private Date modifyTime;
}
