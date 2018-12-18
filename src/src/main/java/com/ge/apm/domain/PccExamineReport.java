package com.ge.apm.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * Created by 212605082 on 2018/3/7.
 */
@Entity
@Table(name = "pcc_examine_report")
public class PccExamineReport implements Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(PccExamineReport.class);
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

	// 检测日期
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH", timezone = "GMT+8")
	@Column(name = "report_date")
	private Date reportDate;

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
	
	@Column(name="name")
	private String name;
	
	@Column(name="report_file_id")
	private String reportFileId;

	@Transient
	private UserAccount createBy;

	@Transient
	private UserAccount modifyBy;
	
	
	@Column(name = "status")
	private Integer status;
    
    @Column(name = "asset_uid")
    private String assetUid;
    
    @JoinColumn(name = "asset_uid", referencedColumnName = "uid", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private AssetInfo assetInfo;
    
    @Column(name = "function_type")
    private Integer functionType;
    
    @Column(name = "num")
    private String num;
    
    @Column(name = "manufact_date")
    @Temporal(TemporalType.DATE)
    private Date manufactDate;
    
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
	private boolean needGetItem=true;
	
	public PccExamineReport() {
		super();
		this.status=0;
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

	public AssetInfo getAssetInfo() {
		return assetInfo;
	}

	public void setAssetInfo(AssetInfo assetInfo) {
		this.assetInfo = assetInfo;
	}

	public String getReportFileId() {
		return reportFileId;
	}

	public void setReportFileId(String reportFileId) {
		this.reportFileId = reportFileId;
	}

	public Integer getFunctionType() {
		return functionType;
	}

	public void setFunctionType(Integer functionType) {
		this.functionType = functionType;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public Date getManufactDate() {
		return manufactDate;
	}

	public void setManufactDate(Date manufactDate) {
		this.manufactDate = manufactDate;
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
