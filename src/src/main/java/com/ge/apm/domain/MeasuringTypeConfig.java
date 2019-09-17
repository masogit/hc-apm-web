package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 212605082 on 2017/10/10.
 */
@Entity
@Table(name = "measuring_type_config")
public class MeasuringTypeConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;
    @NotNull
    @Column(name = "measuring_item_category")
    private String measuringItemCategory;
    @Column(name = "measuring_item_category_name")
    private String measuringItemCategoryName;
    @Column(name = "measure_grade")
    private String measureGrade;
    @Column(name = "measure_cycle")
    private Integer measureCycle;

    //上次计量日期
    @Column(name = "last_metering_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date lastMeteringDate;

    //下次计量日期
    @Column(name = "next_metering_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextMeteringDate;

    //责任人UID
    @Column(name = "measure_owner_uid")
    private String measureOwnerUid;

    //责任人ID
    @Column(name = "measure_owner_id")
    private Integer measureOwnerId;

    @JoinColumn(insertable=false,updatable=false, name = "measure_owner_uid", referencedColumnName = "uid")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private UserAccount measureOwner;

    @Column(name = "site_id")
    private Integer siteId;
    @Column(name = "hospital_id")
    private Integer hospitalId;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeasuringItemCategory() {
        return measuringItemCategory;
    }

    public void setMeasuringItemCategory(String measuringItemCategory) {
        this.measuringItemCategory = measuringItemCategory;
    }

    public String getMeasureGrade() {
        return measureGrade;
    }

    public void setMeasureGrade(String measureGrade) {
        this.measureGrade = measureGrade;
    }

    public Integer getMeasureCycle() {
        return measureCycle;
    }

    public void setMeasureCycle(Integer measureCycle) {
        this.measureCycle = measureCycle;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastMeteringDate() {
        return lastMeteringDate;
    }

    public void setLastMeteringDate(Date lastMeteringDate) {
        this.lastMeteringDate = lastMeteringDate;
    }

    public Date getNextMeteringDate() {
        return nextMeteringDate;
    }

    public void setNextMeteringDate(Date nextMeteringDate) {
        this.nextMeteringDate = nextMeteringDate;
    }

    public String getMeasureOwnerUid() {
        return measureOwnerUid;
    }

    public void setMeasureOwnerUid(String measureOwnerUid) {
        this.measureOwnerUid = measureOwnerUid;
    }

    public Integer getMeasureOwnerId() {
        return measureOwnerId;
    }

    public void setMeasureOwnerId(Integer measureOwnerId) {
        this.measureOwnerId = measureOwnerId;
    }

    public UserAccount getMeasureOwner() {
        return measureOwner;
    }

    public void setMeasureOwner(UserAccount measureOwner) {
        this.measureOwner = measureOwner;
    }

    public String getMeasuringItemCategoryName() {
        return measuringItemCategoryName;
    }

    public void setMeasuringItemCategoryName(String measuringItemCategoryName) {
        this.measuringItemCategoryName = measuringItemCategoryName;
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
        if (!(object instanceof MeasuringTypeConfig)) {
            return false;
        }
        MeasuringTypeConfig other = (MeasuringTypeConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.MeasuringTypeConfig[ id=" + id + " ]";
    }
}
