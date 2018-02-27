/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author nqy
 */
@Entity
@Table(name = "asset_purchase_list")
public class AssetPurchaseList implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "uid",columnDefinition = "CHAR(32)")
    private String id;
    @Column(name = "asset_type")
    private Integer assetType;
    @Column(name = "asset_type_id")
    private String assetTypeId;
    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;
    @Column(name = "clinical_dept_uid")
    private String clinicalDeptUid;
    @Column(name = "purchase_qty")
    private Integer purchaseQty;
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Column(name = "reach_qty")
    private Integer reachQty;
    @Column(name = "reach_state")
    private Integer reachState;
    @Column(name = "reach_detail_state")
    private String reachDetailState;
    @Column(name = "create_date")
    @Temporal(TemporalType.DATE)
    private Date createDate;
    @Column(name = "reach_detail_date")
    @Temporal(TemporalType.DATE)
    private Date reachDetailDate;
    @Column(name = "field_id")
    private Integer fieldId;
    @Column(name = "lin_man")
    private String linkMan;
    @Column(name = "telephone")
    private String telephone;
    @Column(name = "estimated_amount")
    private Double estimatedAmount;
    @Column(name = "is_need_consumables")
    private String isNeedConsumables;
    @Column(name = "apply_type")
    private String applyType;
    @Column(name = "capital_source")
    private String capitalSource;
    @Column(name = "apply_reason")
    private String applyReason;
    @Column(name = "apply_reason_content")
    private String applyReasonContent;
    @Column(name = "special_request")
    private String specialRequest;
    @Column(name = "remark")
    private String remark;
    @Column(name = "recommend_info1")
    private String recommendInfo1;
    @Column(name = "recommend_info2")
    private String recommendInfo2;
    @Column(name = "recommend_info3")
    private String recommendInfo3;
    @Column(name = "recommend_phone1")
    private String recommendPhone1;
    @Column(name = "recommend_phone2")
    private String recommendPhone2;
    @Column(name = "recommend_phone3")
    private String recommendPhone3;
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;
    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;
    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;
    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;
    @Column(name = "object_id")
    private String objectId;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        if (!(object instanceof AssetPurchaseList)) {
            return false;
        }
        AssetPurchaseList other = (AssetPurchaseList) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.AssetPurchaseList[ id=" + id + " ]";
    }

    /**
     * @return the assetType
     */
    public Integer getAssetType() {
        return assetType;
    }

    /**
     * @param assetType the assetType to set
     */
    public void setAssetType(Integer assetType) {
        this.assetType = assetType;
    }

    /**
     * @return the assetTypeId
     */
    public String getAssetTypeId() {
        return assetTypeId;
    }

    /**
     * @param assetTypeId the assetTypeId to set
     */
    public void setAssetTypeId(String assetTypeId) {
        this.assetTypeId = assetTypeId;
    }

    /**
     * @return the clinicalDeptName
     */
    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    /**
     * @param clinicalDeptName the clinicalDeptName to set
     */
    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }

    /**
     * @return the clinicalDeptUid
     */
    public String getClinicalDeptUid() {
        return clinicalDeptUid;
    }

    /**
     * @param clinicalDeptUid the clinicalDeptUid to set
     */
    public void setClinicalDeptUid(String clinicalDeptUid) {
        this.clinicalDeptUid = clinicalDeptUid;
    }

    /**
     * @return the purchaseQty
     */
    public Integer getPurchaseQty() {
        return purchaseQty;
    }

    /**
     * @param purchaseQty the purchaseQty to set
     */
    public void setPurchaseQty(Integer purchaseQty) {
        this.purchaseQty = purchaseQty;
    }

    /**
     * @return the reachQty
     */
    public Integer getReachQty() {
        return reachQty;
    }

    /**
     * @param reachQty the reachQty to set
     */
    public void setReachQty(Integer reachQty) {
        this.reachQty = reachQty;
    }

    /**
     * @return the reachState
     */
    public Integer getReachState() {
        return reachState;
    }

    /**
     * @param reachState the reachState to set
     */
    public void setReachState(Integer reachState) {
        this.reachState = reachState;
    }

    /**
     * @return the reachDetailState
     */
    public String getReachDetailState() {
        return reachDetailState;
    }

    /**
     * @param reachDetailState the reachDetailState to set
     */
    public void setReachDetailState(String reachDetailState) {
        this.reachDetailState = reachDetailState;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the reachDetailDate
     */
    public Date getReachDetailDate() {
        return reachDetailDate;
    }

    /**
     * @param reachDetailDate the reachDetailDate to set
     */
    public void setReachDetailDate(Date reachDetailDate) {
        this.reachDetailDate = reachDetailDate;
    }

    /**
     * @return the fieldId
     */
    public Integer getFieldId() {
        return fieldId;
    }

    /**
     * @param fieldId the fieldId to set
     */
    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    /**
     * @return the linkMan
     */
    public String getLinkMan() {
        return linkMan;
    }

    /**
     * @param linkMan the linkMan to set
     */
    public void setLinkMan(String linkMan) {
        this.linkMan = linkMan;
    }

    /**
     * @return the telephone
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * @param telephone the telephone to set
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * @return the estimatedAmount
     */
    public Double getEstimatedAmount() {
        return estimatedAmount;
    }

    /**
     * @param estimatedAmount the estimatedAmount to set
     */
    public void setEstimatedAmount(Double estimatedAmount) {
        this.estimatedAmount = estimatedAmount;
    }

    /**
     * @return the isNeedConsumables
     */
    public String getIsNeedConsumables() {
        return isNeedConsumables;
    }

    /**
     * @param isNeedConsumables the isNeedConsumables to set
     */
    public void setIsNeedConsumables(String isNeedConsumables) {
        this.isNeedConsumables = isNeedConsumables;
    }

    /**
     * @return the applyType
     */
    public String getApplyType() {
        return applyType;
    }

    /**
     * @param applyType the applyType to set
     */
    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    /**
     * @return the capitalSource
     */
    public String getCapitalSource() {
        return capitalSource;
    }

    /**
     * @param capitalSource the capitalSource to set
     */
    public void setCapitalSource(String capitalSource) {
        this.capitalSource = capitalSource;
    }

    /**
     * @return the applyReason
     */
    public String getApplyReason() {
        return applyReason;
    }

    /**
     * @param applyReason the applyReason to set
     */
    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason;
    }

    /**
     * @return the applyReasonContent
     */
    public String getApplyReasonContent() {
        return applyReasonContent;
    }

    /**
     * @param applyReasonContent the applyReasonContent to set
     */
    public void setApplyReasonContent(String applyReasonContent) {
        this.applyReasonContent = applyReasonContent;
    }

    /**
     * @return the specialRequest
     */
    public String getSpecialRequest() {
        return specialRequest;
    }

    /**
     * @param specialRequest the specialRequest to set
     */
    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the recommendInfo1
     */
    public String getRecommendInfo1() {
        return recommendInfo1;
    }

    /**
     * @param recommendInfo1 the recommendInfo1 to set
     */
    public void setRecommendInfo1(String recommendInfo1) {
        this.recommendInfo1 = recommendInfo1;
    }

    /**
     * @return the recommendInfo2
     */
    public String getRecommendInfo2() {
        return recommendInfo2;
    }

    /**
     * @param recommendInfo2 the recommendInfo2 to set
     */
    public void setRecommendInfo2(String recommendInfo2) {
        this.recommendInfo2 = recommendInfo2;
    }

    /**
     * @return the recommendInfo3
     */
    public String getRecommendInfo3() {
        return recommendInfo3;
    }

    /**
     * @param recommendInfo3 the recommendInfo3 to set
     */
    public void setRecommendInfo3(String recommendInfo3) {
        this.recommendInfo3 = recommendInfo3;
    }

    /**
     * @return the recommendPhone1
     */
    public String getRecommendPhone1() {
        return recommendPhone1;
    }

    /**
     * @param recommendPhone1 the recommendPhone1 to set
     */
    public void setRecommendPhone1(String recommendPhone1) {
        this.recommendPhone1 = recommendPhone1;
    }

    /**
     * @return the recommendPhone2
     */
    public String getRecommendPhone2() {
        return recommendPhone2;
    }

    /**
     * @param recommendPhone2 the recommendPhone2 to set
     */
    public void setRecommendPhone2(String recommendPhone2) {
        this.recommendPhone2 = recommendPhone2;
    }

    /**
     * @return the recommendPhone3
     */
    public String getRecommendPhone3() {
        return recommendPhone3;
    }

    /**
     * @param recommendPhone3 the recommendPhone3 to set
     */
    public void setRecommendPhone3(String recommendPhone3) {
        this.recommendPhone3 = recommendPhone3;
    }

    /**
     * @return the tenantUID
     */
    public String getTenantUID() {
        return tenantUID;
    }

    /**
     * @param tenantUID the tenantUID to set
     */
    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    /**
     * @return the institutionUID
     */
    public String getInstitutionUID() {
        return institutionUID;
    }

    /**
     * @param institutionUID the institutionUID to set
     */
    public void setInstitutionUID(String institutionUID) {
        this.institutionUID = institutionUID;
    }

    /**
     * @return the hospitalUID
     */
    public String getHospitalUID() {
        return hospitalUID;
    }

    /**
     * @param hospitalUID the hospitalUID to set
     */
    public void setHospitalUID(String hospitalUID) {
        this.hospitalUID = hospitalUID;
    }

    /**
     * @return the siteUID
     */
    public String getSiteUID() {
        return siteUID;
    }

    /**
     * @param siteUID the siteUID to set
     */
    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
    }

    /**
     * @return the objectId
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * @param objectId the objectId to set
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    @PrePersist
    public void initializeUid() {
        if (getId() == null) {
            setId(UUID.randomUUID().toString().replace("-", ""));
        }
    }
}
