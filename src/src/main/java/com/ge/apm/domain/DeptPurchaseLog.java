package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.primefaces.model.LazyDataModel;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

//@Entity
@Data
@AllArgsConstructor
@Builder
//@Table(name = "dept_purchase_log")
public class DeptPurchaseLog implements Serializable {

//    protected LazyDataModel lazyModel;

    private static final long serialVersionUID = 1L;
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Basic(optional = false)
//    @Column(name = "id")
    private Integer id;

//    @Column(name = "uid", columnDefinition = "CHAR(32)")
//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String uid;

//    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;

//    @Column(name = "clinical_dept_uid")
    private String clinicalDeptUid;

//    @Column(name = "year_purchase")
    private String yearPurchase;

//    @Column(name = "amount")
    private BigDecimal amount;

//    @Column(name = "RecordDate")
//    @Temporal(TemporalType.DATE)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String recordDate;

    @Size(min = 1, max = 64)
//    @Column(name="transaction_uid")
    private String transactionUID;

    @Size(min = 1, max = 256)
//    @Column(name="remark")
    private String remark;

    private String purchaseType;


//    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
//    private String tenantUID;
//
//    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
//    private String institutionUID;
//
//    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
//    private String hospitalUID;
//
//    @Column(name="clinical_dept_uid",columnDefinition = "CHAR(32)")
//    private String clinicalDeptUID;
//
//    @Column(name="site_uid",columnDefinition = "CHAR(32)")
//    private String siteUID;
//
//    @PrePersist
//    public void initialize() {
//        recordDate = new Date();
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DeptPurchaseLog)) {
            return false;
        }
        DeptPurchaseLog other = (DeptPurchaseLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
