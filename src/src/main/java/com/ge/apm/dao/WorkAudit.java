/*
 */
package com.ge.apm.dao;

import com.ge.apm.domain.AuditGroupConfigurationDetail;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;

/**
 *中间表 work_order_detail/audit_group_configuration_detail
 */
@Entity
@Table(name = "work_audit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
public class WorkAudit implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "work_order_detail_id")
    private String workOrderDetailId;
    @Column(name = "audit_group_configuration_detail_id")
    private String auditGroupConfigurationDetailId;

    @JoinColumn(name = "work_order_detail_id", referencedColumnName = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @NotFound(action=NotFoundAction.IGNORE)
    @org.hibernate.annotations.ForeignKey(name = "none")
    private V2_WorkOrder_Detail v2WorkOrderDetail;
    @JoinColumn(name = "audit_group_configuration_detail_id", referencedColumnName = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @NotFound(action=NotFoundAction.IGNORE)
    @org.hibernate.annotations.ForeignKey(name = "none")
    private AuditGroupConfigurationDetail auditGroupConfigurationDetail;

    public WorkAudit() {
    }

    public WorkAudit(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkAudit)) {
            return false;
        }
        WorkAudit other = (WorkAudit) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    public String getWorkOrderDetailId() {
        return workOrderDetailId;
    }

    public void setWorkOrderDetailId(String workOrderDetailId) {
        this.workOrderDetailId = workOrderDetailId;
    }

    public String getAuditGroupConfigurationDetailId() {
        return auditGroupConfigurationDetailId;
    }

    public void setAuditGroupConfigurationDetailId(String auditGroupConfigurationDetailId) {
        this.auditGroupConfigurationDetailId = auditGroupConfigurationDetailId;
    }

    public V2_WorkOrder_Detail getV2WorkOrderDetail() {
        return v2WorkOrderDetail;
    }

    public void setV2WorkOrderDetail(V2_WorkOrder_Detail v2WorkOrderDetail) {
        this.v2WorkOrderDetail = v2WorkOrderDetail;
    }

    public AuditGroupConfigurationDetail getAuditGroupConfigurationDetail() {
        return auditGroupConfigurationDetail;
    }

    public void setAuditGroupConfigurationDetail(AuditGroupConfigurationDetail auditGroupConfigurationDetail) {
        this.auditGroupConfigurationDetail = auditGroupConfigurationDetail;
    }

}
