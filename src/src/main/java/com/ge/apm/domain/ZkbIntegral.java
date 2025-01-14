package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2021/2/2
 * @description :
 */
@Entity
@Table(name = "zkb_integral")
public class ZkbIntegral implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    @Size(max = 32)
    private String tenantUID;

    @Column(name = "integral_total")
    private Integer integralTotal;

    @Column(name = "coefficient")
    private Integer coefficient;

    @Column(name = "validity_date")
    private Date validityDate;

    @Column(name = "modify_reason")
    @Size(max = 50)
    private String modifyReason;

    @Column(name = "last_clear_date")
    private Date lastClearDate;

    public Date getLastClearDate() {
        return lastClearDate;
    }

    public void setLastClearDate(Date lastClearDate) {
        this.lastClearDate = lastClearDate;
    }

    public String getModifyReason() {
        return modifyReason;
    }

    public void setModifyReason(String modifyReason) {
        this.modifyReason = modifyReason;
    }

    public Integer getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Integer coefficient) {
        this.coefficient = coefficient;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public Integer getIntegralTotal() {
        return integralTotal;
    }

    public void setIntegralTotal(Integer integralTotal) {
        this.integralTotal = integralTotal;
    }

    public Date getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(Date validityDate) {
        this.validityDate = validityDate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ZkbIntegral)) {
            return false;
        }
        ZkbIntegral other = (ZkbIntegral) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
