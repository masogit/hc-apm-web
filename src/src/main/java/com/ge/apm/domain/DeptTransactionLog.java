package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.primefaces.model.LazyDataModel;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

@Entity
@Data
@Table(name = "dept_transaction_log")
public class DeptTransactionLog implements Serializable {

    protected LazyDataModel lazyModel;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "uid", columnDefinition = "CHAR(32)")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String uid;
    @Column(name = "author_uid", columnDefinition = "CHAR(32)")
    private String authorUID;
    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;
    @Column(name = "author_name")
    private String authorName;
    @Column
    private Integer clinicalDeptId;
    @Column
    private String authorLogin;
   /* @Basic(optional = false)
    @NotNull
    @Column(name = "name")
    private String name;*/
   @CreatedBy
   @Column(name = "created_by", nullable = true, length = 50, updatable = false)
   @JsonIgnore
   private String createdBy = "system";

    @CreatedDate
    @Column(name = "created_date", nullable = true)
    @JsonIgnore
    private Date createdDate;
    @Column(name = "hours")
    private Double hours;
//    @Basic(optional = false)
//    @Column(name = "start_time")
//    @Temporal(TemporalType.TIMESTAMP)
//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
//    private Date startTime;
//
//    @Basic(optional = false)
//    @Column(name = "end_time")
//    @Temporal(TemporalType.TIMESTAMP)
//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
//    private Date endTime;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;
    @Size(min = 1, max = 64)
    @Column(name="transaction_uid")
    private String transactionUID;

    @Size(min = 1, max = 256)
    @Column(name="remark")
    private String remark;

    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name="clinical_dept_uid",columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;
    
    @Column(name="site_uid",columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name="category")
    private Integer category;
    @Column(name="author_id")
    private Integer authorId;

    @Column(name="times")
    private Integer times;

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

//    public Date getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(Date startTime) {
//        this.startTime = startTime;
//    }
//
//    public Date getEndTime() {
//        return endTime;
//    }
//
//    public void setEndTime(Date endTime) {
//        this.endTime = endTime;
//    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getInstitutionUID() {
        return institutionUID;
    }

    public void setInstitutionUID(String institutionUID) {
        this.institutionUID = institutionUID;
    }

    public String getHospitalUID() {
        return hospitalUID;
    }

    public void setHospitalUID(String hospitalUID) {
        this.hospitalUID = hospitalUID;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
    }

    public String getTransactionUID() {
        return transactionUID;
    }

    public void setTransactionUID(String transactionUID) {
        this.transactionUID = transactionUID;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getClinicalDeptUID() {
        return clinicalDeptUID;
    }

    public void setClinicalDeptUID(String clinicalDeptUID) {
        this.clinicalDeptUID = clinicalDeptUID;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DeptTransactionLog)) {
            return false;
        }
        DeptTransactionLog other = (DeptTransactionLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.DeptTransactionLog[ id=" + id + " ]";
    }
}
