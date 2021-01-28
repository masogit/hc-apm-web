package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2020/12/3
 * @description :
 */
@Entity
@Table(name = "zkb_fe_part_order_step")
public class ZkbFePartOrderStep implements Serializable {

    private static final long serialVersionUID = 1L;

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

    /**
     * 步骤名称
     */
    @Column(name = "step_name")
    private String stepName;

    /**
     * 操作人
     */
    @Column(name = "owner_id")
    private String ownerId;

    /**
     * 操作人名称
     */
    @Column(name = "owner_name")
    private String ownerName;

    /**
     * 操作开始时间
     */
    @Column(name = "start_time")
    private Date startTime;

    /**
     * 结束时间
     */
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

    /**
     * 数据创建时间
     */
    @Column(name = "create_date")
    private Date createDate;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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
        if (!(object instanceof ZkbFePartOrderStep)) {
            return false;
        }
        ZkbFePartOrderStep other = (ZkbFePartOrderStep) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbFePartOrderStep[ id=" + id + " ]";
    }
}
