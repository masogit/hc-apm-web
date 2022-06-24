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
 * @create 2021/12/1
 * @since 1.0.0
 */
@Entity
@Table(name = "audit_group_configuration_detail")
@Data
public class AuditGroupConfigurationDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    /**
     * 业务UID
     */
    @Column(name = "business_uid")
    private String businessUid;
    /**
     *审批是否通过
     */
    @Column(name = "audit_status")
    private Boolean auditStatus;
    /**
     * 0 初始状态 , 1 审核通过 2 审核未通过
     */
    @Column(name = "audit_node_status")
    private Integer auditNodeStatus;

    /**
     * 0 在用 1失效
     */
    @Column(name = "delete_flag")
    private Integer deleteFlag;
    /**
     * 审核批次
     */
    @Column(name = "approve_num")
    private Integer approveNum;
    /**
     * 审批人UID
     */
    @Column(name = "actual_audit_uid")
    private String actualAuditUid;
    /**
     * 审批人时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "actual_audit_date")
    private Date actualAuditDate;
    /**
     * 审批签名(核心组)
     */
    @Column(name = "approver_signature")
    private String approverSignature;

    /**
     * 创建日期
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    /**
     * 保修工单号
     */
    @Transient
    private String srNum;

    /**
     * 创建人
     */
    @CreatedBy
    @Column(name = "created_by", columnDefinition = "VARCHAR(60)")
    private String createdBy;
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AuditGroupConfigurationDetail)) {
            return false;
        }
        AuditGroupConfigurationDetail other = (AuditGroupConfigurationDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
