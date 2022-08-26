package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @autor by lyj 503277717
 * @date 2022/8/24
*/

@Entity
@Table(name = "zkb_sales_Tenant_Associate")
public class ZkbSalesTenantAssociate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "tenant_uid")
    private String tenantUid;

    //创建人
    @Column(name = "created_by")
    private String createdBy;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @JoinColumn(name = "sales_person_id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ZkbSalesPersonInfo zkbSalesPersonInfo;

    //销售人员信息
    @Column(name = "sales_person_id")
    private String salesPersonId;

    public String getSalesPersonId() {
        return salesPersonId;
    }

    public void setSalesPersonId(String salesPersonId) {
        this.salesPersonId = salesPersonId;
    }

    public ZkbSalesPersonInfo getZkbSalesPersonInfo() {
        return zkbSalesPersonInfo;
    }

    public void setZkbSalesPersonInfo(ZkbSalesPersonInfo zkbSalesPersonInfo) {
        this.zkbSalesPersonInfo = zkbSalesPersonInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }


    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
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
        if (!(object instanceof ZkbSalesTenantAssociate)) {
            return false;
        }
        ZkbSalesTenantAssociate other = (ZkbSalesTenantAssociate) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbSalesTenantAssociate[ id=" + id + " ]";
    }

}
