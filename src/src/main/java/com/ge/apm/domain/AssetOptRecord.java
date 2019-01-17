package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Cacheable(false)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Table(name = "asset_opt_record")
public class AssetOptRecord implements Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(AssetOptRecord.class);
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	private String id;

	// 创建日期
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "create_date")
	private Date createDate;

	// 修改日期
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "modify_date")
	private Date modifyDate;

	// 开机时间
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "start_date")
	private Date startDate;

	// 关机时间
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "end_date")
	private Date endDate;

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

//	@Transient
	@JoinColumn(name = "create_by", referencedColumnName = "uid", insertable = false, updatable = false)
	@ManyToOne(fetch = FetchType.EAGER)
	private UserAccount createBy;

//	@Transient
	@JoinColumn(name = "modify_by", referencedColumnName = "uid", insertable = false, updatable = false)
	@ManyToOne(fetch = FetchType.EAGER)
	private UserAccount modifyBy;
	
	@Transient
	private String costTime;

	// 0：进行中，1：完成
	@Column(name = "status")
	private Integer status = 0;

//	@Column(name = "signature_id")
//	private String signatureId;
//
//	// NULL:no signature,0:from web(file text),1:from weixin(file stream)
//	@Column(name = "signature_source_type")
//	private Integer signatureSourceType;

	@Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
	private String clinicalDeptUID;// 设备使用科室

	@Column(name = "clinical_dept_name")
	private String clinicalDeptName;// 使用科室

	@Column(name = "asset_uid")
	private String assetUid;

	@Column(name = "comment")
	private String comment;

	@JoinColumn(name = "asset_uid", referencedColumnName = "uid", insertable = false, updatable = false)
	@ManyToOne(fetch = FetchType.EAGER)
//	@Transient
	private AssetInfo assetInfo;
	
	@Transient
	private OrgInfo siteOrgInfo;

	@Transient
	private boolean needGetItem = true;

	public AssetOptRecord() {
		super();
	}

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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

//	public String getSignatureId() {
//		return signatureId;
//	}
//
//	public void setSignatureId(String signatureId) {
//		if (StringUtils.isNotEmpty(signatureId)) {
//			this.signatureSourceType = 1;
//		}
//		this.signatureId = signatureId;
//	}

	public String getClinicalDeptUID() {
		return clinicalDeptUID;
	}

	public void setClinicalDeptUID(String clinicalDeptUID) {
		this.clinicalDeptUID = clinicalDeptUID;
	}

	public String getClinicalDeptName() {
		return clinicalDeptName;
	}

	public void setClinicalDeptName(String clinicalDeptName) {
		this.clinicalDeptName = clinicalDeptName;
	}

//	public Integer getSignatureSourceType() {
//		return signatureSourceType;
//	}
//
//	public void setSignatureSourceType(Integer signatureSourceType) {
//		this.signatureSourceType = signatureSourceType;
//	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getAssetUid() {
		return assetUid;
	}

	public void setAssetUid(String assetUid) {
		this.assetUid = assetUid;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isNeedGetItem() {
		return needGetItem;
	}

	public void setNeedGetItem(boolean needGetItem) {
		this.needGetItem = needGetItem;
	}

	public AssetInfo getAssetInfo() {
		return assetInfo;
	}

	public void setAssetInfo(AssetInfo assetInfo) {
		this.assetInfo = assetInfo;
	}

	public OrgInfo getSiteOrgInfo() {
		return siteOrgInfo;
	}

	public void setSiteOrgInfo(OrgInfo siteOrgInfo) {
		this.siteOrgInfo = siteOrgInfo;
	}

	public String getCostTime() {
		return costTime;
	}

	public void setCostTime(String costTime) {
		this.costTime = costTime;
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
		if (!(object instanceof AssetOptRecord)) {
			return false;
		}
		AssetOptRecord other = (AssetOptRecord) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "AssetOptRecord [ id=" + id + " ]";
	}
}
