package com.ge.apm.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;



/**
 * Created by 503035627
 */
@Entity
@Table(name = "capture_data_messages")
public class CaptureDataMessages implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	private String id;
	
	@CreatedBy
    @Column(name = "created_by", nullable = true, length = 50, updatable = false)
    private String createdBy = "system";

    @CreatedDate
    @Column(name = "created_date", nullable = true)
    private Date createdDate = new Date();

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Date lastModifiedDate = new Date();
    
    
    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;
    
    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUID;
    
    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUID;
    
    @Column(name="site_uid",columnDefinition = "CHAR(32)")
    private String siteUID;

	@Column(name = "system_id")
	private String systemId;
	
	@Column(name = "asset_uid")
	private String assetUID;
	
	//0:单个设备,1:geDeviceType
	@Column(name = "type")
	private Integer type;

	@Column(name = "comment")
	private String comment;
	
	@Column(name="index")
	private Integer index;

	@Column(name = "content",columnDefinition="text")
	private String content;
	
	@Column(name = "title")
	private String title;

	@Column(name = "icon")
	private String icon;
	
	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date startDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date endDate;

	@Transient
	private Integer excelIdex;
	
	@Transient
	private String status;

	@Transient
	private String statusCode;
	
	@Transient
	private Integer ibStatus;
	
	@Column(name = "region")
	private String region;
	
	@Column(name = "ge_device_type")
	private Integer geDeviceType;

	@Transient
	private String statusDisplay;

	@Transient
	private String otherErrorMessage;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Integer getExcelIdex() {
		return excelIdex;
	}

	public void setExcelIdex(Integer excelIdex) {
		this.excelIdex = excelIdex;
	}

	public void setStatusDisplay(String statusDisplay) {
		this.statusDisplay = statusDisplay;
	}

	public void setStatusDisplay(UploadFileErrorTypeEnum type, boolean pass) {
		if (pass) {
			if (this.status != null) {
				this.status = this.status.replaceAll(type + ",", "");
			}
		} else {
			if (this.status == null) {
				this.status = type + ",";
			} else if (this.status.indexOf(type + ",") < 0) {
				this.status += type + ",";
			}
		}
		if (StringUtils.isEmpty(this.status)) {
			this.statusCode = "1";
		} else {
			this.statusCode = "2";
		}
	}

	public void setStatusDisplay(UploadFileErrorTypeEnum type, String otherErrorMessage) {
		if (this.status == null) {
			this.status = type + ",";
		} else if (this.status.indexOf(type + ",") < 0) {
			this.status += type + ",";
		}
		this.otherErrorMessage = otherErrorMessage;
	}

	public String getStatus() {
		return this.status;
	}

	private void setStatus(String status) {
		this.status = status;
	}

	public String getOtherErrorMessage() {
		return otherErrorMessage;
	}

	private void setOtherErrorMessage(String otherErrorMessage) {
		this.otherErrorMessage = otherErrorMessage;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

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

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getAssetUID() {
		return assetUID;
	}

	public void setAssetUID(String assetUID) {
		this.assetUID = assetUID;
	}

	public Integer getGeDeviceType() {
		return geDeviceType;
	}

	public void setGeDeviceType(Integer geDeviceType) {
		this.geDeviceType = geDeviceType;
	}

	public Integer getType() {
		if(type==null)
			type=0;
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getIbStatus() {
		if(ibStatus==null){
			ibStatus=0;
		}
		return this.ibStatus;
	}

	public void setIbStatus(Integer ibStatus) {
		this.ibStatus = ibStatus;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getStatusDisplay() {
		if (this.status != null) {
			String result = "";
			if (this.status.contains(UploadFileErrorTypeEnum.FORMAT_ERROR + ",")) {
				result += "格式错误；";
			} else if (this.status.contains(UploadFileErrorTypeEnum.RATE_WRONG + ",")) {
				result += "占比总和错误；";
			} else if (this.status.contains(UploadFileErrorTypeEnum.ASSET_NOT_EXISTS + ",")) {
				result += "设备名称不存在；";
			} else if (this.status.contains(UploadFileErrorTypeEnum.OTHER_ERROR + ",")) {
				result += this.otherErrorMessage;
			} else {
				result += this.status;
			}
			return result;
		}
		return statusDisplay;
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

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
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
		if (!(object instanceof CaptureDataMessages)) {
			return false;
		}
		CaptureDataMessages other = (CaptureDataMessages) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.ge.apm.domain.CaptureDataMessages[ id=" + id + " ]";
	}
}
