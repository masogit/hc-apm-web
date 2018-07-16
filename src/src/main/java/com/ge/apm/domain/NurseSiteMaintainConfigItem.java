package com.ge.apm.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by 212605082 on 2018/3/7.
 */
@Entity
@Table(name = "nurse_site_maintain_config_item")
public class NurseSiteMaintainConfigItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	private String id;
	
	// 创建日期
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH", timezone = "GMT+8")
	@Column(name = "create_date")
	private Date createDate;

	// 修改日期
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH", timezone = "GMT+8")
	@Column(name = "modify_date")
	private Date modifyDate;

	@Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
	private String tenantUID;

	@Column(name = "institution_uid", columnDefinition = "CHAR(32)")
	private String institutionUID;

	@Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
	private String hospitalUID;

	@Column(name = "site_uid", columnDefinition = "CHAR(32)")
	private String siteUID;
	
	@Column(name = "create_by")
	private String createByUid;

	@Column(name = "modify_by")
	private String modifyByUid;

	@JoinColumn(insertable = false, updatable = false, name = "create_by", referencedColumnName = "uid",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@org.hibernate.annotations.ForeignKey(name = "none")
	@JsonBackReference
	private UserAccount createBy;

	@JoinColumn(insertable = false, updatable = false, name = "modify_by", referencedColumnName = "uid",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@org.hibernate.annotations.ForeignKey(name = "none")
	@JsonBackReference
	private UserAccount modifyBy;
	
	@Column(name="check_items")
	private String checkItems;
	
	@Column(name="check_items_detail",columnDefinition="text")
	private String checkItemsDetail;
	
	//1:day,2:week,3:month,4:year
	@Column(name="type")
	private Integer type;
	
	@Column(name="type_value")
	private String typeValue;
	
	@Column(name="parent_id")
	private String parentId;
	
	@Column(name="asset_uid")
	private String assetUid;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getCreateByUid() {
		return createByUid;
	}

	public void setCreateByUid(String createByUid) {
		this.createByUid = createByUid;
	}

	public String getModifyByUid() {
		return modifyByUid;
	}

	public void setModifyByUid(String modifyByUid) {
		this.modifyByUid = modifyByUid;
	}

	public UserAccount getCreateBy() {
		return createBy;
	}

	public void setCreateBy(UserAccount createBy) {
		this.createBy = createBy;
	}

	public UserAccount getModifyBy() {
		return modifyBy;
	}

	public void setModifyBy(UserAccount modifyBy) {
		this.modifyBy = modifyBy;
	}

	public String getCheckItems() {
		return checkItems;
	}

	public void setCheckItems(String checkItems) {
		this.checkItems = checkItems;
	}


	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getTypeValue() {
		return typeValue;
	}

	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}

	public String getCheckItemsDetail() {
		return checkItemsDetail;
	}

	public void setCheckItemsDetail(String checkItemsDetail) {
		this.checkItemsDetail = checkItemsDetail;
	}
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getAssetUid() {
		return assetUid;
	}

	public void setAssetUid(String assetUid) {
		this.assetUid = assetUid;
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
		if (!(object instanceof NurseSiteMaintainConfigItem)) {
			return false;
		}
		NurseSiteMaintainConfigItem other = (NurseSiteMaintainConfigItem) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NurseSiteMaintainConfigItem [ id=" + id + " ]";
	}
}
