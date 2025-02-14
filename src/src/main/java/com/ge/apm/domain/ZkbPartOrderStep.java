package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2020/6/23
 * @description :
 */
@Entity
@Table(name = "zkb_part_order_step")
public class ZkbPartOrderStep implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum PartOrderStep {
        TechnicalPending,ProductPending,ManagePending,OrderExtraPending, TsePending,OsscPending,OsscReturnPending,
        OrderPending,ReturnPending,ClosePending,Complete,DirectorPending,CompletePending,
        ProbeLetterSend,ProbeLetterCC,CommercialPending,Reviewer,FourHundredCancel,CompleteAuditPending,
        EngineerQualificationPending
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 外键
     */
    @Column(name = "part_order_id")
    private String partOrderId;

    /**
     * 步骤key
     */
    @Column(name = "step_key")
    private String stepKey;

    @Column(name = "step_name")
    private String stepName;

    /**
     * 操作人
     */
    @Column(name = "owner_id")
    private String ownerId;

    @Column(name = "owner_name")
    private String ownerName;

    /**
     * 操作开始时间
     */
    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 租户
     */
    @Column(name = "tenant_uid")
    private String tenantUID;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartOrderId() {
        return partOrderId;
    }

    public void setPartOrderId(String partOrderId) {
        this.partOrderId = partOrderId;
    }

    public String getStepKey() {
        return stepKey;
    }

    public void setStepKey(String stepKey) {
        this.stepKey = stepKey;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
        if (!(object instanceof ZkbPartOrderStep)) {
            return false;
        }
        ZkbPartOrderStep other = (ZkbPartOrderStep) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbPartOrderStatus[ id=" + id + " ]";
    }
}
