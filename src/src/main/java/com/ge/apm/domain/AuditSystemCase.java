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
@Table(name = "audit_system_case")
@Data
public class AuditSystemCase implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
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
     * 评审要求
     */
    @Column(name = "rating_requirements")
    private String ratingRequirements;
    /**
     * 枚举已知算法
     */
    @Column(name = "judge_logic")
    private String judgeLogic;
    /**
     * 状态 :1 草稿 2 发布 3 过期
     */
    @Column(name = "status")
    private Integer status;
    /**
     * 类型 :1 评审/ 2质控
     */
    @Column(name = "type")
    private Integer type;

    @Transient
    private Integer stripNum;//条总数

    @Transient
    private Integer paymentNum;//款总数

    @Transient
    private Integer targetNum;//指标总数
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AuditSystemCase)) {
            return false;
        }
        AuditSystemCase other = (AuditSystemCase) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }
}
