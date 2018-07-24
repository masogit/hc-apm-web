package com.ge.apm.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ge.apm.service.utils.StringComparator;

/**
 * Created by 212605082 on 2018/3/7.
 */
@Entity
@Table(name = "nurse_site_maintain")
public class NurseSiteMaintain implements Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(NurseSiteMaintain.class);
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
	@Column(name = "check_date")
	private Date checkDate;

	// scheduleDate
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH", timezone = "GMT+8")
	@Column(name = "schedule_date")
	private Date scheduleDate;

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

	@JoinColumn(insertable = false, updatable = false, name = "create_by", referencedColumnName = "uid",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@org.hibernate.annotations.ForeignKey(name = "none")
	@JsonBackReference
	private UserAccount createBy;

	@JoinColumn(insertable = false, updatable = false, name = "modify_by", referencedColumnName = "uid",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@org.hibernate.annotations.ForeignKey(name = "none")
	@JsonBackReference
	private UserAccount modifyBy;
	
	@JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nurseSiteMaintain", fetch = FetchType.LAZY)
    private Set<NurseSiteMaintainItem> listNurseSiteMaintainItem;
	
	//0：未检查,1:已检查，2:通过，3有问题
	@Column(name = "status")
	private Integer status;
	
	@Column(name="config_id")
	private String configId;
	
	@Column(name="signature_id")
	private String signatureId;
	
	@Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;//设备使用科室

    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;//使用科室
	
	@Transient
	private List<String> showLables;
	
	@Transient
	private boolean needGetItem=true;
	
	public NurseSiteMaintain() {
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

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	public Date getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate=scheduleDate;
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

	public Set<NurseSiteMaintainItem> getListNurseSiteMaintainItem() {
		return listNurseSiteMaintainItem;
	}

	public void setListNurseSiteMaintainItem(Set<NurseSiteMaintainItem> listNurseSiteMaintainItem) {
		this.listNurseSiteMaintainItem = listNurseSiteMaintainItem;
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public String getSignatureId() {
		return signatureId;
	}

	public void setSignatureId(String signatureId) {
		this.signatureId = signatureId;
	}

	public List<String> getShowLables() {
		return showLables;
	}

	public void setShowLables(List<String> showLables) {
		this.showLables = showLables;
	}
	
	public boolean isNeedGetItem() {
		return needGetItem;
	}

	public void setNeedGetItem(boolean needGetItem) {
		this.needGetItem = needGetItem;
	}

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
		if (!(object instanceof NurseSiteMaintain)) {
			return false;
		}
		NurseSiteMaintain other = (NurseSiteMaintain) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NurseSiteMaintain [ id=" + id + " ]";
	}
}
