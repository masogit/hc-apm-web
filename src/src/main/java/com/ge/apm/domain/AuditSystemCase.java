package com.ge.apm.domain;

import lombok.Data;
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
@Table(name = "audit_system_case")
@Data
public class AuditSystemCase implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(columnDefinition = "CHAR(32)")
    private String uid;
    /**
     * 年份
     */
    @Column(name = "version")
    private String version;
    /**
     * 适用地区
     */
    @Column(name = "zone")
    private String zone;
    /**
     * 枚举已知算法
     */
    @Column(name = "judge_logic")
    private String judgeLogic;
    /**
     * 状态 :1 草稿draft, 2文件file, 3 过期expired
     */
    @Column(name = "status")
    private Integer status;
    /**
     * 类型 :1 评审/ 2质控
     */
    @Column(name = "type")
    private Integer type;
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
