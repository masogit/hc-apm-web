package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;

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

import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by 212605082 on 2018/3/7.
 */
@Entity
@Table(name = "nurse_site_maintain_config")
public class NurseSiteMaintainConfig implements Serializable,Cloneable {
	private static final Logger LOGGER = LoggerFactory.getLogger(NurseSiteMaintainConfig.class);
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
	
	@Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;//设备使用科室

    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;//使用科室
	
	//1:day,2:week,3:month,4:year
	@Column(name="type")
	private Integer type;
	
	@Column(name="type_value")
	private String typeValue;
	
	@Column(name="name")
	private String name;
	
	@Column(name="apply_directly")
	private Boolean applyDirectly=false;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCheckItemsDetail() {
		return checkItemsDetail;
	}

	public void setCheckItemsDetail(String checkItemsDetail) {
		this.checkItemsDetail = checkItemsDetail;
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

	public Boolean getApplyDirectly() {
		if(applyDirectly==null){
			applyDirectly=false;
		}
		return applyDirectly;
	}

	public void setApplyDirectly(Boolean applyDirectly) {
		if(applyDirectly==null){
			applyDirectly=false;
		}
		this.applyDirectly = applyDirectly;
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
		if (!(object instanceof NurseSiteMaintainConfig)) {
			return false;
		}
		NurseSiteMaintainConfig other = (NurseSiteMaintainConfig) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NurseSiteMaintainConfig [ id=" + id + " ]";
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
	public NurseSiteMaintainConfig cloneObj(){
		NurseSiteMaintainConfig temp=null;
		try {
			temp=(NurseSiteMaintainConfig)this.clone();
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage());
		}
		return temp;
	}
}
