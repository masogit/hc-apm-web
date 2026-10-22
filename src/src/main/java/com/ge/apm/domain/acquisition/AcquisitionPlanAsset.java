package com.ge.apm.domain.acquisition;

import com.ge.apm.domain.AssetInfo;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 购置计划与设备的关联表
 */
@Entity
@Table(name = "acquisition_plan_asset")
public class AcquisitionPlanAsset {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "acquisition_plan_id", columnDefinition = "CHAR(32)")
    private String acquisitionPlanId;

    @Column(name = "asset_uid", columnDefinition = "CHAR(32)")
    private String assetUid;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "acquisition_plan_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private AcquisitionPlan acquisitionPlan;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "asset_uid", referencedColumnName = "uid",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private AssetInfo assetInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAcquisitionPlanId() {
        return acquisitionPlanId;
    }

    public void setAcquisitionPlanId(String acquisitionPlanId) {
        this.acquisitionPlanId = acquisitionPlanId;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
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

    public AcquisitionPlan getAcquisitionPlan() {
        return acquisitionPlan;
    }

    public void setAcquisitionPlan(AcquisitionPlan acquisitionPlan) {
        this.acquisitionPlan = acquisitionPlan;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AcquisitionPlanAsset)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        AcquisitionPlanAsset other = (AcquisitionPlanAsset) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
