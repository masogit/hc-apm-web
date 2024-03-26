package com.ge.apm.domain.secondment;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *  科室借调配置
 */
@Entity
@Table(name = "secondment_config")
@Data
public class SecondmentConfig implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "billing_type")
    private Integer billingType;

    @Column(name = "accessories",columnDefinition = "text")
    private String accessories;//附件信息

    @Column(name="remark",columnDefinition = "text")
    private String remark;

    @Column(name = "created_by", columnDefinition = "VARCHAR(32)")
    private String createdBy;  // 创建人uid

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "modify_by", columnDefinition = "VARCHAR(32)")
    private String  modifyBy;// 修改人uid

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_Date")
    private Date modifyDate;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SecondmentConfig)) {
            return false;
        }
        SecondmentConfig other = (SecondmentConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
