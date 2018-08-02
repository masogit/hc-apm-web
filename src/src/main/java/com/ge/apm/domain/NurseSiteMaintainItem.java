package com.ge.apm.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
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

import org.apache.commons.lang3.StringUtils;
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
@Table(name = "nurse_site_maintain_item")
public class NurseSiteMaintainItem implements Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(NurseSiteMaintainItem.class);
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

	@Column(name="parent_id", insertable = false, updatable = false)
	private String parentId;
	
	@Column(name="asset_uid")
	private String assetUid;
	
	@JoinColumn(name = "parent_id", referencedColumnName = "id",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
	@org.hibernate.annotations.ForeignKey(name = "none")
	@JsonIgnore
    private NurseSiteMaintain nurseSiteMaintain;
	
	@JoinColumn(insertable = false, updatable = false, name = "asset_uid", referencedColumnName = "uid",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@org.hibernate.annotations.ForeignKey(name = "none")
	@JsonBackReference
	private AssetInfo assetInfo;
	
	@JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item", fetch = FetchType.EAGER)
    private Set<NurseSiteMaintainData> listData;
	
	//0：未检查,1:已检查，2:通过，3有问题
	@Column(name = "status")
	private Integer status;
	
	@Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;//设备使用科室

    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;//使用科室
    
    @Column(name="name")
	private String name;
    
    @Column(name="signature_id")
	private String signatureId;
	
	//NULL:no signature,0:from web(file text),1:from weixin(file stream)
	@Column(name="signature_source_type")
	private Integer signatureSourceType=0;
	
	@Column(name="sequence")
	private Integer sequence;
	
	@Transient
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH");
	
	public NurseSiteMaintainItem() {
		super();
		this.status=0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	public NurseSiteMaintain getNurseSiteMaintain() {
		return nurseSiteMaintain;
	}

	public void setNurseSiteMaintain(NurseSiteMaintain nurseSiteMaintain) {
		this.nurseSiteMaintain = nurseSiteMaintain;
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

	public AssetInfo getAssetInfo() {
		return assetInfo;
	}

	public void setAssetInfo(AssetInfo assetInfo) {
		this.assetInfo = assetInfo;
	}

	public Date getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public Set<NurseSiteMaintainData> getListData() {
		return listData;
	}

	public void setListData(Set<NurseSiteMaintainData> listData) {
		this.listData = listData;
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
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getSignatureId() {
		return signatureId;
	}

	public void setSignatureId(String signatureId) {
		this.signatureSourceType=0;
		this.signatureId = signatureId;
	}
	
	public Integer getSignatureSourceType() {
		return signatureSourceType;
	}

	public void setSignatureSourceType(Integer signatureSourceType) {
		this.signatureSourceType = signatureSourceType;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public NurseSiteMaintainData getDataValue(String label){
		if(listData!=null && StringUtils.isNotEmpty(label)){
			Iterator<NurseSiteMaintainData> it=listData.iterator();
			while(it.hasNext()){
				NurseSiteMaintainData tempData=it.next();
				if(tempData.getCheckItem().equals(label)){
					return tempData;
				}
			}
		}
		NurseSiteMaintainData tempData=new NurseSiteMaintainData();
		tempData.setItem(this);
		tempData.setCheckItem(label);
		this.listData.add(tempData);
		return tempData;
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
		if (!(object instanceof NurseSiteMaintainItem)) {
			return false;
		}
		NurseSiteMaintainItem other = (NurseSiteMaintainItem) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))|| (this.id==null && other.id==null)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NurseSiteMaintainItem [ id=" + id + " ]";
	}
}
