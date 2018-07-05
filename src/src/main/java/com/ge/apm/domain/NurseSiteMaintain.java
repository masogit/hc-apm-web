package com.ge.apm.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
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

	@JoinColumn(insertable = false, updatable = false, name = "create_by", referencedColumnName = "uid")
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JsonBackReference
	private UserAccount createBy;

	@JoinColumn(insertable = false, updatable = false, name = "modify_by", referencedColumnName = "uid")
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JsonBackReference
	private UserAccount modifyBy;
	
	@JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nurseSiteMaintain", fetch = FetchType.EAGER)
    private Set<NurseSiteMaintainItem> listNurseSiteMaintainItem;
	
	//0：未检查,1:已检查，2:通过，3有问题
	@Column(name = "status")
	private Integer status;
	
	@Transient
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH");
	
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
		try {
			this.scheduleDate = sdf.parse(sdf.format(scheduleDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage());
		}
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
	
	public void addItem(NurseSiteMaintainItem item){
		if(this.listNurseSiteMaintainItem==null){
			this.listNurseSiteMaintainItem=new HashSet<NurseSiteMaintainItem>();
		}
		if(!listNurseSiteMaintainItem.contains(item)){
			item.setNurseSiteMaintain(this);
			listNurseSiteMaintainItem.add(item);
		}
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
