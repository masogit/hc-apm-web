package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by 212605082 on 2018/3/7.
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

	@Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
	@Size(max = 32)
	private String tenantUID;

	@Column(name = "institution_uid", columnDefinition = "CHAR(32)")
	private String institutionUID;

	@Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
	private String hospitalUID;

	@Column(name = "site_uid", columnDefinition = "CHAR(32)")
	private String siteUID;

	@Transient
	private Integer excelIdex;
	
	@Transient
	private String status;

	@Transient
	private String statusCode;
	
	@Transient
	private Integer ibStatus;
	
	@Column(name = "ge_device_type")
	private Integer geDeviceType;

	@Transient
	private String statusDisplay;

	@Transient
	private String otherErrorMessage;
	

	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date startDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date endDate;
	
	public static final int imageLength2="</img>".length();
	public static final int pLength1="<p>".length();
	public static final int pLength2="</p>".length();
	public static final String beginImage="<img";
	public static final String endImage="</img>";
	public static final String beginP="<p>";
	public static final String endP="</p>";
	public static final String pageLimit="<!--page-->";

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

	public Integer getExcelIdex() {
		return excelIdex;
	}

	public void setExcelIdex(Integer excelIdex) {
		this.excelIdex = excelIdex;
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
