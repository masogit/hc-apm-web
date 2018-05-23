package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 212605082 on 2018/3/7.
 */
@Entity
@Table(name = "small_asset_performance_report")
public class SmallAssetPerformanceReport implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	private String id;

	// 设备UID
	@Column(name = "asset_uid")
	private String assetUID;

	@JoinColumn(insertable = false, updatable = false, name = "asset_uid", referencedColumnName = "uid")
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JsonBackReference
	private AssetInfo assetInfo;

	// 科室
	@Column(name = "department")
	private String department;

	// 设备名称
	@Column(name = "assetName")
	private String assetName;

	// 费别
	@Column(name = "cost_type")
	private String costType;

	// 项目名称
	@Column(name = "projectName")
	private String projectName;

	// 一类标准
	@Column(name = "standard")
	private Double standard;

	// 数量
	@Column(name = "quantity")
	private Double quantity;

	// 金额
	@Column(name = "amount")
	private Double amount;

	// 日期
	@Column(name = "report_date")
	@Temporal(TemporalType.DATE)
	private Date reportDate;

	@Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
	@Size(max = 32)
	private String tenantUID;

	@Column(name = "institution_uid", columnDefinition = "CHAR(32)")
	private String institutionUID;

	@Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
	private String hospitalUID;

	@Column(name = "site_uid", columnDefinition = "CHAR(32)")
	private String siteUID;

	@Transient
	private String statusDisplay;
	
	@Column(name = "small_asset_performance_id", columnDefinition = "CHAR(32)")
	private String smallAssetPerformanceId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAssetUID() {
		return assetUID;
	}

	public void setAssetUID(String assetUID) {
		this.assetUID = assetUID;
	}

	public AssetInfo getAssetInfo() {
		return assetInfo;
	}

	public void setAssetInfo(AssetInfo assetInfo) {
		this.assetInfo = assetInfo;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getCostType() {
		return costType;
	}

	public void setCostType(String costType) {
		this.costType = costType;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Double getStandard() {
		return standard;
	}

	public void setStandard(Double standard) {
		this.standard = standard;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
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

	public String getStatusDisplay() {
		return statusDisplay;
	}

	public void setStatusDisplay(String statusDisplay) {
		this.statusDisplay = statusDisplay;
	}
	

	public String getSmallAssetPerformanceId() {
		return smallAssetPerformanceId;
	}

	public void setSmallAssetPerformanceId(String smallAssetPerformanceId) {
		this.smallAssetPerformanceId = smallAssetPerformanceId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof SmallAssetPerformanceReport)) {
			return false;
		}
		SmallAssetPerformanceReport other = (SmallAssetPerformanceReport) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.ge.apm.domain.SmallAssetPerformanceReport[ id=" + id + " ]";
	}
}
