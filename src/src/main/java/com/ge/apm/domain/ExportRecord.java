package com.ge.apm.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 503310146
 */
@Entity
@Table(name = "export_record")
@Data
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
public class ExportRecord implements Serializable {

    private static final long serialVersionUID = 4344213478040355610L;

    public enum Status {
        /**
         * 状态颜色
         */
        NEW("#8c8c8c"), AUDIT("#1890ff"), RUN("#2f54eb"), FINISHED("#52c41a"), REFUSE("#fa8c16"), FAIL("#f5222d");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public static String getStatusValue(String status) {
            for (Status type : Status.values()) {
                if (type.name().equals(status)) {
                    return type.value;
                }
            }
            return "#141414";
        }
    }

    public enum AuditStatus {
        /**
         * 审核状态
         */
        PASS, REFUSE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "status")
    private String status;

    @Column(name = "tenant_uid")
    private String tenantUid;

    @Column(name = "remark")
    private String remark;

    @Column(name = "password")
    private String password;

    @Column(name = "apply_by")
    private String applyBy;

    @Column(name = "apply_description")
    private String applyDescription;

    @Column(name = "apply_date")
    private Date applyDate;

    @Column(name = "audit_by")
    private String auditBy;

    @Column(name = "audit_description")
    private String auditDescription;

    @Column(name = "audit_date")
    private Date auditDate;

    @Column(name = "audit_status")
    private String auditStatus;

    @CreatedDate
    @Column(name = "create_date")
    private Date createDate;

    @LastModifiedDate
    @Column(name = "update_date")
    private Date updateDate;

}
