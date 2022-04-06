package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 503189829
 * @create 2022/2/23
 * @since 1.0.0
 */
@Entity
@Table(name = "audit_system_entry")
@Data
public class AuditSystemEntry implements Serializable {

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
     * name
     */
    @Column(name = "name")
    private String name;
    /**
     * 层级 12345
     */
    @Column(name = "level")
    private Integer level;
    /**
     * type 1条 2款 3指标
     */
    @Column(name = "type")
    private Integer type;
    /**
     * mark
     */
    @Column(name = "mark")
    private String mark;
    /**
     * 权重
     */
    @Column(name = "weight")
    private Double weight;
    /**
     * order
     */
    @Column(name = "order_by")
    private Integer orderBy;
    /**
     * parent_uid
     */
    @Column(name = "parent_uid")
    private String parentUid;
    /**
     * 计分方式 （枚举 3种)
     */
    @Column(name = "score_type")
    private Integer scoreType;
    /**
     * 标准得分
     */
    @Column(name = "score_standard")
    private Double scoreStandard;
    /**
     * 原文
     */
    @Column(name = "source_text")
    private String sourceText;
    /**
     * 评审标准
     */
    @Column(name = "evaluation_criteria")
    private String evaluationCriteria;
    /**
     * 初始得分 (默认0)
     */
    @Column(name = "score_init")
    private Double scoreInit;
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
    @Transient
    private List<AuditSystemEntry> children = new ArrayList<>();

    public List<AuditSystemEntry> getChildren() {
        return children;
    }
    public void setChildren(List<AuditSystemEntry> children) {
        this.children = children;
    }

}
