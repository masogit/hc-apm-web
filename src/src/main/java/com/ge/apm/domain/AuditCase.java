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
 */
@Entity
@Table(name = "audit_case")
@Data
public class AuditCase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    /**
     * system_case_uid
     */
    @Column(name = "system_case_uid")
    private String systemCaseUid;

    @Column(name = "score")
    private Double score;
    /**
     * process进度
     */
    @Column(name = "process")
    private Double process;
    /**
     * site_uid
     */
    @Column(name = "site_uid")
    private String siteUid;
    /**
     * tenant_uid
     */
    @Column(name = "tenant_uid")
    private String tenantUid;
    /**
     * 状态 :1 active, 2 archieve
     */
    @Column(name = "status")
    private Integer status;

    @Column(name = "target")
    private Integer target;

    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

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

    /**
     * 更新人
     */
    @CreatedBy
    @Column(name = "updater", columnDefinition = "VARCHAR(60)")
    private String updater;
    /**
     * 更新日期
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    private Date updateTime;

    @OneToOne
    @JoinColumn(name = "system_case_uid", insertable = false, updatable = false)
    private AuditSystemCase auditSystemCase;

    @Column(name = "allow_download")
    private Boolean allowDownload;

    /**
     * 目标
     */
    @Transient
    private Integer targetId;

    /**
     * 目标分值
     */
    @Transient
    private Double targetScore;

}
