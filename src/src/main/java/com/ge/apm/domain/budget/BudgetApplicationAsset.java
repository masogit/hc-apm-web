package com.ge.apm.domain.budget;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * @author Yangfan, Liu
 * @date 4/9/2022 18:20
 */
@Entity
@Table(name = "budget_application_asset", indexes = {@Index(columnList = "application_id"),
        @Index(columnList = "stock_type")})
public class BudgetApplicationAsset implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    private String id;

    // 申请id
    @Column(name = "application_id", columnDefinition = "CHAR(32)")
    private String applicationId;

    // 申购类型
    @Column(name = "stock_type")
    private Integer stockType;

    // 申请设备id
    @Column(name = "application_asset_id", columnDefinition = "CHAR(32)")
    private String applicationAssetId;

    @Formula("(select a.name from budget_asset_info a where a.id = application_asset_id)")
    private String assetName;

    @Formula("(select a.asset_group from budget_asset_info a where a.id = application_asset_id)")
    private Integer assetGroup;

    @Formula("(select a.asset_group_secondary from budget_asset_info a where a.id = application_asset_id)")
    private Integer assetGroupSecondary;

    // 申请设备
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "application_asset_id",
            referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action = NotFoundAction.IGNORE)
    private BudgetAssetInfo asset;

    // 申请数量
    @Column(name = "application_quantity")
    private Integer applicationQuantity;

    // 申请价格
    @Column(name = "application_price", scale = 2)
    private BigDecimal applicationPrice;

    // 申请总金额
    @Column(name = "application_amount", scale = 2)
    private BigDecimal applicationAmount;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedDate;

    @Transient
    private boolean isNew = false;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BudgetApplicationAsset)) {
            return false;
        }
        BudgetApplicationAsset other = (BudgetApplicationAsset) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getStockType() {
        return stockType;
    }

    public void setStockType(Integer stockType) {
        this.stockType = stockType;
    }

    public String getApplicationAssetId() {
        return applicationAssetId;
    }

    public void setApplicationAssetId(String applicationAssetId) {
        this.applicationAssetId = applicationAssetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public Integer getAssetGroupSecondary() {
        return assetGroupSecondary;
    }

    public void setAssetGroupSecondary(Integer assetGroupSecondary) {
        this.assetGroupSecondary = assetGroupSecondary;
    }

    public BudgetAssetInfo getAsset() {
        return asset;
    }

    public void setAsset(BudgetAssetInfo asset) {
        this.asset = asset;
    }

    public Integer getApplicationQuantity() {
        return applicationQuantity;
    }

    public void setApplicationQuantity(Integer applicationQuantity) {
        this.applicationQuantity = applicationQuantity;
    }

    public BigDecimal getApplicationPrice() {
        return applicationPrice;
    }

    public void setApplicationPrice(BigDecimal applicationPrice) {
        this.applicationPrice = applicationPrice;
    }

    public BigDecimal getApplicationAmount() {
        return applicationAmount == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : applicationAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public void setApplicationAmount(BigDecimal applicationAmount) {
        this.applicationAmount = applicationAmount;
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

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
