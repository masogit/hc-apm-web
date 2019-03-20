/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ge.apm.service.utils.ChineseName;
import com.ge.apm.service.utils.PinyinUtil;

/**
 * @author 503035627
 */
@Entity
@Table(name = "non_asset_info")
public class NonAssetInfo implements ChineseName, Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	private String id;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "name")
	private String name;

	@Column(name = "name_py_code")
	private String namePyCode;

	@Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
	private String clinicalDeptUID;// 设备所属科室

	@Size(max = 64)
	@Column(name = "clinical_dept_name")
	private String clinicalDeptName;// 所属科室

	@Column(name = "clinical_dept_name_py_code")
	private String clinicalDeptNamePyCode;

	@Basic(optional = false)
	@Column(name = "is_valid")
	private boolean isValid;// 在用？

	// 1:正常,2:停机,3:限制功能使用,4:待报废,5:停用
	@Basic(optional = false)
	@Column(name = "status")
	private Integer status;// 当前状态

	@Column(name = "purchase_date")
	@Temporal(TemporalType.DATE)
	private Date purchaseDate;// 采购日期

	@Column(name = "arrive_date")
	@Temporal(TemporalType.DATE)
	private Date arriveDate;// 到货日期

	@Column(name = "install_date")
	@Temporal(TemporalType.DATE)
	private Date installDate;// 启用日期

	@Column(name = "terminate_date")
	@Temporal(TemporalType.DATE)
	private Date terminateDate;// 报废日期

	@Column(name = "is_deleted")
	private Boolean isDeleted;// 是否删除

	@Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
	private String tenantUID;

	@Column(name = "institution_uid", columnDefinition = "CHAR(32)")
	private String institutionUID;

	@Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
	private String hospitalUID;

	@Column(name = "site_uid", columnDefinition = "CHAR(32)")
	private String siteUID;

	@CreatedBy
	@Column(name = "created_by", nullable = true, length = 50)
	private String createdBy;

	@CreatedDate
	@Column(name = "created_date", nullable = true)
	private Date createdDate;

	@LastModifiedBy
	@Column(name = "last_modified_by", nullable = true, length = 50)
	private String lastModifiedBy;

	@LastModifiedDate
	@Column(name = "last_modified_date", nullable = true)
	private Date lastModifiedDate;

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

	public NonAssetInfo() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		if (!StringUtils.isEmpty(name)) {
			this.namePyCode = PinyinUtil.getShortPinyin(name).toLowerCase().replaceAll("[^0-9a-zA-Z]", "");
		}

	}

	public String getClinicalDeptName() {
		return clinicalDeptName;
	}

	public void setClinicalDeptName(String clinicalDeptName) {
		this.clinicalDeptName = clinicalDeptName;
		if (!StringUtils.isEmpty(clinicalDeptName)) {
			this.clinicalDeptNamePyCode = PinyinUtil.getShortPinyin(clinicalDeptName).toLowerCase();
		}
	}

	public boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(boolean isValid) {
		this.isValid = isValid;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public Date getArriveDate() {
		return arriveDate;
	}

	public void setArriveDate(Date arriveDate) {
		this.arriveDate = arriveDate;
	}

	public Date getInstallDate() {
		return installDate;
	}

	public void setInstallDate(Date installDate) {
		this.installDate = installDate;
	}

	public Date getTerminateDate() {
		return terminateDate;
	}

	public void setTerminateDate(Date terminateDate) {
		this.terminateDate = terminateDate;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getClinicalDeptUID() {
		return clinicalDeptUID;
	}

	public void setClinicalDeptUID(String clinicalDeptUID) {
		this.clinicalDeptUID = clinicalDeptUID;
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

	public String getNamePyCode() {
		return namePyCode;
	}

	public void setNamePyCode(String namePyCode) {
		this.namePyCode = namePyCode;
	}

	public String getClinicalDeptNamePyCode() {
		return clinicalDeptNamePyCode;
	}

	public void setClinicalDeptNamePyCode(String clinicalDeptNamePyCode) {
		this.clinicalDeptNamePyCode = clinicalDeptNamePyCode;
	}

	public String getPinYin() {
		return getNamePyCode();
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public String getId() {
		return id;
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
		if (!(object instanceof NonAssetInfo)) {
			return false;
		}
		NonAssetInfo other = (NonAssetInfo) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

}
