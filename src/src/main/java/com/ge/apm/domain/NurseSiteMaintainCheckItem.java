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
 * Created by 503035627 on 2018/3/7.
 */
@Entity
@Table(name = "nurse_site_maintain_check_item")
public class NurseSiteMaintainCheckItem implements Serializable,Cloneable {
	private static final Logger LOGGER = LoggerFactory.getLogger(NurseSiteMaintainCheckItem.class);
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
	
	@Column(name="name")
	private String name;
	
	@Column(name="description")
	private String description;
	
	@Column(name="key")
	private Long key;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
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
		if (!(object instanceof NurseSiteMaintainCheckItem)) {
			return false;
		}
		NurseSiteMaintainCheckItem other = (NurseSiteMaintainCheckItem) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NurseSiteMaintainConfig [ id=" + id + ",name=" + name + ",siteUID=" + siteUID + ",createByUid=" + createByUid+ " ]";
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
	public NurseSiteMaintainCheckItem cloneObj(){
		NurseSiteMaintainCheckItem temp=null;
		try {
			temp=(NurseSiteMaintainCheckItem)this.clone();
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage());
		}
		return temp;
	}
}
