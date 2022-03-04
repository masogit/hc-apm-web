package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 503189829
 * @create 2022/2/23
 * @since 1.0.0
 */
@Entity
@Table(name = "audit_entry")
@Data
public class AuditEntry implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    /**
     * case_uid
     */
    @Column(name = "case_uid")
    private String caseUid;
    /**
     * system_entry_uid
     */
    @Column(name = "system_entry_uid")
    private String systemEntryUid;
    /**
     * owner
     */
    @Column(name = "owner")
    private String owner;
    /**
     * comments
     */
    @Column(name = "comments")
    private String comments;
    /**
     * 分数
     */
    @Column(name = "score")
    private Double score;
    /**
     * 进度
     */
    @Column(name = "process")
    private Double process;

    /**
     * 创建人
     */
    @CreatedBy
    @Column(name = "creator", columnDefinition = "VARCHAR(60)")
    private String creator;
    /**
     * 创建日期
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createTime;

}
