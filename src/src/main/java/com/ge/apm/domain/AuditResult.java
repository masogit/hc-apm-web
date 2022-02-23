package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author 212579464
 */
@Entity
@Data
@Table(name = "audit_result")
public class AuditResult implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    /**
     * 用户标准uid
     */
    @Column(name = "case_uid")
    private String caseUid;

    /**
     * 评审时间
     */
    @Column(name = "audit_time")
    private Date auditTime;

    /**
     * 评审分数
     */
    @Column(name = "audit_score")
    private Double auditScore;

    /**
     * 备注
     */
    @Column(name = "comments")
    private String comments;
}
