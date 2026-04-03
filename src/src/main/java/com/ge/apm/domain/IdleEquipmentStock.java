package com.ge.apm.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "idle_equipment_stock")
public class IdleEquipmentStock implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String uid;

    @Column(name = "application_number")
    private String applicationNumber;//申请编号

    @Column(name = "application_uid")
    private String applicationUid; //申请表uid

    @Column(name = "asset_uid")
    private String assetUid;
    @Column(name = "stock_hospital_uid")
    private String stockHospitalUid; //入库医院
    @Column(name = "stock_site_uid")
    private String stockSiteUid; //入库院区
    @Column(name = "stock_dept_uid")
    private String stockDeptUid;//入库科室

    @Column(name = "stock_dept_name")
    private String stockDeptName;//入库科室

    @Column(name = "borrow_hospital_uid")
    private String borrowHospitalUid; //借入医院
    @Column(name = "borrow_site_uid")
    private String borrowSiteUid; //借入院区
    @Column(name = "borrow_dept_uid")
    private String borrowDeptUid;//借入科室


    @Column(name = "status")
    private Integer status;// 调剂状态   1可借用   2 申请中  3已借用 4不可借


    @LastModifiedBy
    @Column(name = "last_modified_by", nullable = true, length = 50)
    private String lastModifiedBy;

    @Column(name = "created_by", nullable = true, length = 50)
    private String  createdBy;

    @LastModifiedDate
    @Column(name = "created_date", nullable = true)
    private Date createdDate;
    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    @JoinColumn(insertable = false, updatable = false, name = "asset_uid", referencedColumnName = "uid", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    @NotFound(action = NotFoundAction.IGNORE)
    private AssetInfo assetInfo;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getApplicationUid() {
        return applicationUid;
    }

    public void setApplicationUid(String applicationUid) {
        this.applicationUid = applicationUid;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getStockSiteUid() {
        return stockSiteUid;
    }

    public void setStockSiteUid(String stockSiteUid) {
        this.stockSiteUid = stockSiteUid;
    }

    public String getStockDeptUid() {
        return stockDeptUid;
    }

    public void setStockDeptUid(String stockDeptUid) {
        this.stockDeptUid = stockDeptUid;
    }

    public String getStockDeptName() {
        return stockDeptName;
    }

    public void setStockDeptName(String stockDeptName) {
        this.stockDeptName = stockDeptName;
    }

    public String getBorrowSiteUid() {
        return borrowSiteUid;
    }

    public void setBorrowSiteUid(String borrowSiteUid) {
        this.borrowSiteUid = borrowSiteUid;
    }

    public String getBorrowDeptUid() {
        return borrowDeptUid;
    }

    public void setBorrowDeptUid(String borrowDeptUid) {
        this.borrowDeptUid = borrowDeptUid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getStockHospitalUid() {
        return stockHospitalUid;
    }

    public void setStockHospitalUid(String stockHospitalUid) {
        this.stockHospitalUid = stockHospitalUid;
    }

    public String getBorrowHospitalUid() {
        return borrowHospitalUid;
    }

    public void setBorrowHospitalUid(String borrowHospitalUid) {
        this.borrowHospitalUid = borrowHospitalUid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IdleEquipmentStock)) {
            return false;
        }
        IdleEquipmentStock other = (IdleEquipmentStock) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }

}
