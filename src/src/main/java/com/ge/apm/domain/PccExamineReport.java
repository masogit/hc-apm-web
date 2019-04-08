package com.ge.apm.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by 503035627 on 2018/3/7.
 */
@Entity
@Table(name = "pcc_examine_report")
public class PccExamineReport implements Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(PccExamineReport.class);
	private static final long serialVersionUID = 1L;

	@Id
	@Column(columnDefinition = "CHAR(32)")
	private String id;

	// 创建日期
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "MM/dd/yyyy HH", timezone = "GMT+8")
	@Column(name = "create_date")
	private Date createDate;

	// 修改日期
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "MM/dd/yyyy HH", timezone = "GMT+8")
	@Column(name = "modify_date")
	private Date modifyDate;

	// 生成报告日期
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "MM/dd/yyyy HH", timezone = "GMT+8")
	@Column(name = "report_date")
	private Date reportDate;

	// 检测日期
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "MM/dd/yyyy HH", timezone = "GMT+8")
	@Column(name = "check_date")
	private Date checkDate;

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

	@Column(name = "name")
	private String name;

	@Column(name = "report_storage_id")
	private String reportStorageId;

	@Transient
	private UserAccount createBy;

	@Transient
	private UserAccount modifyBy;

	// 1:save,2:submit,
	@Column(name = "status")
	private Integer status;

	@Column(name = "asset_uid")
	private String assetUid;

	// @JoinColumn(name = "asset_uid", referencedColumnName = "uid", insertable
	// = false, updatable = false)
	// @ManyToOne(fetch = FetchType.EAGER)
	@Transient
	AssetInfo assetInfo;

	@Transient
	private String modifyByName;

	@Transient
	private String createByName;
	@Transient
	private String assetInfohospitalName;
	@Transient
	private String assetInfoname;
	@Transient
	private String assetInfonamePyCode;
	@Transient
	private Integer assetInfofunctionGroup;
	@Transient
	private String assetInfofunctionType;
	@Transient
	private String assetInfoserialNum;
	@Transient
	private String assetInfodepartNum;
	@Transient
	private String assetInfofinancingNum;
	@Transient
	private String assetInfoclinicalDeptUID;
	@Transient
	private String assetInfoclinicalDeptName;
	@Transient
	private Date assetInfomanufactDate;
	@Transient
	private String assetInfosystemId;
	@Transient
	private Date assetInfoarriveDate;
	@Transient
	private Date assetInfoinstallDate;
	@Transient
	private Date assetInfowarrantyDate;

	@Column(name = "line_status")
	private String lineStatus;
	@Column(name = "mirror_status")
	private String mirrorStatus;
	@Column(name = "shell_status")
	private String shellStatus;
	@Column(name = "oil_status")
	private String oilStatus;
	@Column(name = "linker_status")
	private String linkerStatus;
	@Column(name = "tip_status")
	private String tipStatus;
	@Column(name = "electric_leakage_status")
	private String electricLeakageStatus;
	@Column(name = "first_seen_status")
	private String firstSeenStatus;
	@Column(name = "first_tip_status")
	private String firstTipStatus;
	@Column(name = "result_status")
	private String resultStatus;
	@Column(name = "recommand_status")
	private String recommandStatus;
	@Column(name = "comment")
	private String comment;
	@Transient
	private boolean blnHasId=true;

	@Transient
	private boolean needGetItem = true;

	public PccExamineReport() {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNeedGetItem() {
		return needGetItem;
	}

	public void setNeedGetItem(boolean needGetItem) {
		this.needGetItem = needGetItem;
	}

	public String getAssetUid() {
		return assetUid;
	}

	public void setAssetUid(String assetUid) {
		this.assetUid = assetUid;
	}

	public String getAssetInfoname() {
		return assetInfoname;
	}

	public void setAssetInfoname(String assetInfoname) {
		this.assetInfoname = assetInfoname;
	}

	public String getAssetInfonamePyCode() {
		return assetInfonamePyCode;
	}

	public void setAssetInfonamePyCode(String assetInfonamePyCode) {
		this.assetInfonamePyCode = assetInfonamePyCode;
	}

	public String getAssetInfofunctionType() {
		return assetInfofunctionType;
	}

	public void setAssetInfofunctionType(String assetInfofunctionType) {
		this.assetInfofunctionType = assetInfofunctionType;
	}

	public String getAssetInfoserialNum() {
		return assetInfoserialNum;
	}

	public void setAssetInfoserialNum(String assetInfoserialNum) {
		this.assetInfoserialNum = assetInfoserialNum;
	}

	public String getAssetInfodepartNum() {
		return assetInfodepartNum;
	}

	public void setAssetInfodepartNum(String assetInfodepartNum) {
		this.assetInfodepartNum = assetInfodepartNum;
	}

	public String getAssetInfofinancingNum() {
		return assetInfofinancingNum;
	}

	public void setAssetInfofinancingNum(String assetInfofinancingNum) {
		this.assetInfofinancingNum = assetInfofinancingNum;
	}

	public String getAssetInfoclinicalDeptUID() {
		return assetInfoclinicalDeptUID;
	}

	public void setAssetInfoclinicalDeptUID(String assetInfoclinicalDeptUID) {
		this.assetInfoclinicalDeptUID = assetInfoclinicalDeptUID;
	}

	public String getAssetInfoclinicalDeptName() {
		return assetInfoclinicalDeptName;
	}

	public void setAssetInfoclinicalDeptName(String assetInfoclinicalDeptName) {
		this.assetInfoclinicalDeptName = assetInfoclinicalDeptName;
	}

	public Integer getAssetInfofunctionGroup() {
		return assetInfofunctionGroup;
	}

	public void setAssetInfofunctionGroup(Integer assetInfofunctionGroup) {
		this.assetInfofunctionGroup = assetInfofunctionGroup;
	}

	public Date getAssetInfomanufactDate() {
		return assetInfomanufactDate;
	}

	public void setAssetInfomanufactDate(Date assetInfomanufactDate) {
		this.assetInfomanufactDate = assetInfomanufactDate;
	}

	public String getLineStatus() {
		return lineStatus;
	}

	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
	}

	public String getMirrorStatus() {
		return mirrorStatus;
	}

	public void setMirrorStatus(String mirrorStatus) {
		this.mirrorStatus = mirrorStatus;
	}

	public String getShellStatus() {
		return shellStatus;
	}

	public void setShellStatus(String shellStatus) {
		this.shellStatus = shellStatus;
	}

	public String getLinkerStatus() {
		return linkerStatus;
	}

	public void setLinkerStatus(String linkerStatus) {
		this.linkerStatus = linkerStatus;
	}

	public String getTipStatus() {
		return tipStatus;
	}

	public void setTipStatus(String tipStatus) {
		this.tipStatus = tipStatus;
	}

	public String getElectricLeakageStatus() {
		return electricLeakageStatus;
	}

	public void setElectricLeakageStatus(String electricLeakageStatus) {
		this.electricLeakageStatus = electricLeakageStatus;
	}

	public String getFirstSeenStatus() {
		return firstSeenStatus;
	}

	public void setFirstSeenStatus(String firstSeenStatus) {
		this.firstSeenStatus = firstSeenStatus;
	}

	public String getFirstTipStatus() {
		return firstTipStatus;
	}

	public void setFirstTipStatus(String firstTipStatus) {
		this.firstTipStatus = firstTipStatus;
	}

	public String getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getRecommandStatus() {
		return recommandStatus;
	}

	public void setRecommandStatus(String recommandStatus) {
		this.recommandStatus = recommandStatus;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOilStatus() {
		return oilStatus;
	}

	public void setOilStatus(String oilStatus) {
		this.oilStatus = oilStatus;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public String getReportStorageId() {
		return reportStorageId;
	}

	public void setReportStorageId(String reportStorageId) {
		this.reportStorageId = reportStorageId;
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	public AssetInfo getAssetInfo() {
		return assetInfo;
	}

	public void setAssetInfo(AssetInfo assetInfo) {
		this.assetInfo = assetInfo;
	}

	public String getAssetInfosystemId() {
		return assetInfosystemId;
	}

	public void setAssetInfosystemId(String assetInfosystemId) {
		this.assetInfosystemId = assetInfosystemId;
	}

	public Date getAssetInfoarriveDate() {
		return assetInfoarriveDate;
	}

	public void setAssetInfoarriveDate(Date assetInfoarriveDate) {
		this.assetInfoarriveDate = assetInfoarriveDate;
	}

	public Date getAssetInfoinstallDate() {
		return assetInfoinstallDate;
	}

	public void setAssetInfoinstallDate(Date assetInfoinstallDate) {
		this.assetInfoinstallDate = assetInfoinstallDate;
	}

	public Date getAssetInfowarrantyDate() {
		return assetInfowarrantyDate;
	}

	public void setAssetInfowarrantyDate(Date assetInfowarrantyDate) {
		this.assetInfowarrantyDate = assetInfowarrantyDate;
	}

	public String getModifyByName() {
		return modifyByName;
	}

	public void setModifyByName(String modifyByName) {
		this.modifyByName = modifyByName;
	}

	public String getCreateByName() {
		return createByName;
	}

	public void setCreateByName(String createByName) {
		this.createByName = createByName;
	}

	public String getAssetInfohospitalName() {
		return assetInfohospitalName;
	}

	public void setAssetInfohospitalName(String assetInfohospitalName) {
		this.assetInfohospitalName = assetInfohospitalName;
	}
	public boolean isBlnHasId() {
		return blnHasId;
	}

	public void setBlnHasId(boolean blnHasId) {
		this.blnHasId = blnHasId;
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
		if (!(object instanceof PccExamineReport)) {
			return false;
		}
		PccExamineReport other = (PccExamineReport) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PccExamineReport [ id=" + id + " ]";
	}
}
