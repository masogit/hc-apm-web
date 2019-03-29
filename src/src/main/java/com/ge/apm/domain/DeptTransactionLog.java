package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "dept_transaction_log")
public class DeptTransactionLog implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name="id")
    private String id;

    @Column(name = "authorName")
    private String authorName;

   /* @Basic(optional = false)
    @NotNull
    @Column(name = "name")
    private String name;*/

    @Basic(optional = false)
    @NotNull
    @Column(name = "startTime")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    @Basic(optional = false)
    @NotNull
    @Column(name = "endTime")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;

    @Size(min = 1, max = 64)
    @Column(name="transactionUID")
    private String transactionUID;

    @Size(min = 1, max = 256)
    @Column(name="remark")
    private String remark;

    @Column(name="tenantUID",columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name="institutionUID",columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name="hospitalUID",columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name="siteUID",columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name="category")
    private Integer category;


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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NotNull
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(@NotNull Date startTime) {
        this.startTime = startTime;
    }

    @NotNull
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(@NotNull Date endTime) {
        this.endTime = endTime;
    }

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

   /* @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }
  */
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
}
