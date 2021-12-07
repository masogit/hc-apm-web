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
@Table(name = "audit_group_configuration")
@Data
public class AuditGroupConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    /**
     * 分组名称
     */
    @Column(name = "group_name")
    private String groupName;

    /**
     * 组员名称
     */
    @Column(name = "group_member_item", columnDefinition = "VARCHAR(1024)")
    private String groupMemberItem;

    /**
     * 创建日期
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    /**
     * 创建人
     */
    @CreatedBy
    @Column(name = "created_by", columnDefinition = "VARCHAR(64)")
    private String createdBy;

    /**
     * 组织id
     */
    @Column(name = "hier_org_uid", columnDefinition = "CHAR(32)")
    private String hierOrgUid;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AuditGroupConfiguration)) {
            return false;
        }
        AuditGroupConfiguration other = (AuditGroupConfiguration) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
