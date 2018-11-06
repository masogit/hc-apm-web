package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MVSAssetInfoOverview implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String woId;

	private String parts;

	private Integer partsQuantity;

	private Double partsPrice;

	private String tenantUID;

	private String institutionUID;

	private String hospitalUID;

	private String siteUID;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate;

	private String serialNum;

	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date warrantyDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	private Date applyTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	private Date estimatedArriveTime;

	// 收货时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	private Date arrivedTime;

	private Double closedPrice;

	private Boolean arrived;

	private String partsType;

	private Boolean approved;

	private String invoiceNum;

	private Boolean closed;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	private Date closedTime;
	private String supplier;

	private String comment;

	private String contractNum;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	private Date invoiceTime;

	// 是否完好
	private Boolean arrivedGood;

	// 好还/坏还
	private Boolean arrivedGoodReturn;

	// 采购时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	private Date purchaseTime;

	// 发货时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	private Date deliverTime;

	// 普通/加急,0普通，1加急
	private String priority;
	private Double mvsPrice;
	private Date v2WorkOrderServiceRequestRequestTime;
	private Integer v2WorkOrderMvsType;
	private String v2WorkOrderFEOwnerName;
	private String v2WorkOrderFEOwnerNamePyCode;
	private String v2WorkOrderAssetInfoDepartNum;
	private String v2WorkOrderAssetInfoName;
	private String v2WorkOrderAssetInfoNamePyCode;
	private String v2WorkOrderAssetInfoMvsEquip;
	private String v2WorkOrderAssetInfoSiteUID;
	private String v2WorkOrderAssetInfoHospitalUID;
	private String v2WorkOrderAssetInfoInstitutionUID;
	private String v2WorkOrderAssetInfoTenantUID;
	private String hospitalOrgName;
	private String siteOrgName;
	private String tenantOrgUid;
	private String institutionOrgUid;
	private String hospitalOrgUid;
	private String siteOrgUid;

	public String getInvoiceNum() {
		return invoiceNum;
	}

	public void setInvoiceNum(String invoiceNum) {
		this.invoiceNum = invoiceNum;
	}

	public String getId() {
		return id;
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

	public void setId(String id) {
		this.id = id;
	}

	public String getWoId() {
		return woId;
	}

	public void setWoId(String woId) {
		this.woId = woId;
	}

	public String getParts() {
		return parts;
	}

	public void setParts(String parts) {
		this.parts = parts;
	}

	public Integer getPartsQuantity() {
		return partsQuantity;
	}

	public void setPartsQuantity(Integer partsQuantity) {
		this.partsQuantity = partsQuantity;
	}

	public String getTenantOrgUid() {
		return tenantOrgUid;
	}

	public void setTenantOrgUid(String tenantOrgUid) {
		this.tenantOrgUid = tenantOrgUid;
	}

	public String getInstitutionOrgUid() {
		return institutionOrgUid;
	}

	public void setInstitutionOrgUid(String institutionOrgUid) {
		this.institutionOrgUid = institutionOrgUid;
	}

	public String getHospitalOrgUid() {
		return hospitalOrgUid;
	}

	public void setHospitalOrgUid(String hospitalOrgUid) {
		this.hospitalOrgUid = hospitalOrgUid;
	}

	public Double getPartsPrice() {
		return partsPrice;
	}

	public void setPartsPrice(Double partsPrice) {
		this.partsPrice = partsPrice;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public Date getWarrantyDate() {
		return warrantyDate;
	}

	public void setWarrantyDate(Date warrantyDate) {
		this.warrantyDate = warrantyDate;
	}

	public Date getEstimatedArriveTime() {
		return estimatedArriveTime;
	}

	public void setEstimatedArriveTime(Date estimatedArriveTime) {
		this.estimatedArriveTime = estimatedArriveTime;
	}

	public Boolean getArrived() {
		return arrived;
	}

	public void setArrived(Boolean arrived) {
		this.arrived = arrived;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public Date getArrivedTime() {
		return arrivedTime;
	}

	public void setArrivedTime(Date arrivedTime) {
		this.arrivedTime = arrivedTime;
	}

	public Double getClosedPrice() {
		return closedPrice;
	}

	public void setClosedPrice(Double closedPrice) {
		this.closedPrice = closedPrice;
	}

	public Boolean getClosed() {
		return closed;
	}

	public void setClosed(Boolean closed) {
		this.closed = closed;
	}

	public Date getClosedTime() {
		return closedTime;
	}

	public void setClosedTime(Date closedTime) {
		this.closedTime = closedTime;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getContractNum() {
		return contractNum;
	}

	public void setContractNum(String contractNum) {
		this.contractNum = contractNum;
	}

	public Date getInvoiceTime() {
		return invoiceTime;
	}

	public void setInvoiceTime(Date invoiceTime) {
		this.invoiceTime = invoiceTime;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Double getMvsPrice() {
		return mvsPrice;
	}

	public void setMvsPrice(Double mvsPrice) {
		this.mvsPrice = mvsPrice;
	}

	public String getPartsType() {
		return partsType;
	}

	public void setPartsType(String partsType) {
		this.partsType = partsType;
	}

	public Boolean getArrivedGood() {
		return arrivedGood;
	}

	public void setArrivedGood(Boolean arrivedGood) {
		this.arrivedGood = arrivedGood;
	}

	public Boolean getArrivedGoodReturn() {
		return arrivedGoodReturn;
	}

	public void setArrivedGoodReturn(Boolean arrivedGoodReturn) {
		this.arrivedGoodReturn = arrivedGoodReturn;
	}

	public Date getPurchaseTime() {
		return purchaseTime;
	}

	public void setPurchaseTime(Date purchaseTime) {
		this.purchaseTime = purchaseTime;
	}

	public Date getDeliverTime() {
		return deliverTime;
	}

	public void setDeliverTime(Date deliverTime) {
		this.deliverTime = deliverTime;
	}

	public Date getV2WorkOrderServiceRequestRequestTime() {
		return v2WorkOrderServiceRequestRequestTime;
	}

	public void setV2WorkOrderServiceRequestRequestTime(Date v2WorkOrderServiceRequestRequestTime) {
		this.v2WorkOrderServiceRequestRequestTime = v2WorkOrderServiceRequestRequestTime;
	}

	public Integer getV2WorkOrderMvsType() {
		return v2WorkOrderMvsType;
	}

	public void setV2WorkOrderMvsType(Integer v2WorkOrderMvsType) {
		this.v2WorkOrderMvsType = v2WorkOrderMvsType;
	}

	public String getV2WorkOrderFEOwnerName() {
		return v2WorkOrderFEOwnerName;
	}

	public void setV2WorkOrderFEOwnerName(String v2WorkOrderFEOwnerName) {
		this.v2WorkOrderFEOwnerName = v2WorkOrderFEOwnerName;
	}

	public String getV2WorkOrderAssetInfoDepartNum() {
		return v2WorkOrderAssetInfoDepartNum;
	}

	public void setV2WorkOrderAssetInfoDepartNum(String v2WorkOrderAssetInfoDepartNum) {
		this.v2WorkOrderAssetInfoDepartNum = v2WorkOrderAssetInfoDepartNum;
	}

	public String getV2WorkOrderAssetInfoName() {
		return v2WorkOrderAssetInfoName;
	}

	public void setV2WorkOrderAssetInfoName(String v2WorkOrderAssetInfoName) {
		this.v2WorkOrderAssetInfoName = v2WorkOrderAssetInfoName;
	}

	public String getHospitalOrgName() {
		return hospitalOrgName;
	}

	public void setHospitalOrgName(String hospitalOrgName) {
		this.hospitalOrgName = hospitalOrgName;
	}

	public String getSiteOrgName() {
		return siteOrgName;
	}

	public void setSiteOrgName(String siteOrgName) {
		this.siteOrgName = siteOrgName;
	}

	public String getSiteOrgUid() {
		return siteOrgUid;
	}

	public void setSiteOrgUid(String siteOrgUid) {
		this.siteOrgUid = siteOrgUid;
	}

	public String getV2WorkOrderFEOwnerNamePyCode() {
		return v2WorkOrderFEOwnerNamePyCode;
	}

	public void setV2WorkOrderFEOwnerNamePyCode(String v2WorkOrderFEOwnerNamePyCode) {
		this.v2WorkOrderFEOwnerNamePyCode = v2WorkOrderFEOwnerNamePyCode;
	}

	public String getV2WorkOrderAssetInfoNamePyCode() {
		return v2WorkOrderAssetInfoNamePyCode;
	}

	public void setV2WorkOrderAssetInfoNamePyCode(String v2WorkOrderAssetInfoNamePyCode) {
		this.v2WorkOrderAssetInfoNamePyCode = v2WorkOrderAssetInfoNamePyCode;
	}

	public String getV2WorkOrderAssetInfoMvsEquip() {
		return v2WorkOrderAssetInfoMvsEquip;
	}

	public void setV2WorkOrderAssetInfoMvsEquip(String v2WorkOrderAssetInfoMvsEquip) {
		this.v2WorkOrderAssetInfoMvsEquip = v2WorkOrderAssetInfoMvsEquip;
	}

	public String getV2WorkOrderAssetInfoSiteUID() {
		return v2WorkOrderAssetInfoSiteUID;
	}

	public void setV2WorkOrderAssetInfoSiteUID(String v2WorkOrderAssetInfoSiteUID) {
		this.v2WorkOrderAssetInfoSiteUID = v2WorkOrderAssetInfoSiteUID;
	}

	public String getV2WorkOrderAssetInfoHospitalUID() {
		return v2WorkOrderAssetInfoHospitalUID;
	}

	public void setV2WorkOrderAssetInfoHospitalUID(String v2WorkOrderAssetInfoHospitalUID) {
		this.v2WorkOrderAssetInfoHospitalUID = v2WorkOrderAssetInfoHospitalUID;
	}

	public String getV2WorkOrderAssetInfoInstitutionUID() {
		return v2WorkOrderAssetInfoInstitutionUID;
	}

	public void setV2WorkOrderAssetInfoInstitutionUID(String v2WorkOrderAssetInfoInstitutionUID) {
		this.v2WorkOrderAssetInfoInstitutionUID = v2WorkOrderAssetInfoInstitutionUID;
	}

	public String getV2WorkOrderAssetInfoTenantUID() {
		return v2WorkOrderAssetInfoTenantUID;
	}

	public void setV2WorkOrderAssetInfoTenantUID(String v2WorkOrderAssetInfoTenantUID) {
		this.v2WorkOrderAssetInfoTenantUID = v2WorkOrderAssetInfoTenantUID;
	}

	@Override
	public int hashCode() {
		Integer hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof MVSAssetInfoOverview)) {
			return false;
		}
		MVSAssetInfoOverview other = (MVSAssetInfoOverview) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return id+" "+parts;
	}
}
