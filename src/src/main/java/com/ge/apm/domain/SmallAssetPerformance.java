package com.ge.apm.domain;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 212605082 on 2018/3/7.
 */
@Entity
@Table(name = "small_asset_performance")
public class SmallAssetPerformance implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	private String id;

	// 科室
	@Column(name = "department")
	private String department;

	// 费别
	@Column(name = "cost_type")
	private String costType;

	// 编码
	@Column(name = "code")
	private String code;

	// 项目名称
	@Column(name = "projectName")
	private String projectName;

	// 项目编号
	@Column(name = "projectNumber")
	private String projectNumber;

	// 一类标准
	@Column(name = "standard")
	private Double standard;

	// 数量
	@Column(name = "quantity")
	private Double quantity;

	// 金额
	@Column(name = "amount")
	private Double amount;

	// 日期
	@Column(name = "happen_date")
	@Temporal(TemporalType.DATE)
	private Date happenDate;

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
	private String statusDisplay;
	
	@Transient
	private String otherErrorMessage;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDepartment() {
		if (department == null)
			department = "";
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getCostType() {
		if (costType == null)
			costType = "";
		return costType;
	}

	public void setCostType(String costType) {
		this.costType = costType;
	}

	public String getCode() {
		if (code == null)
			code = "";
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getProjectName() {
		if (projectName == null)
			projectName = "";
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectNumber() {
		if (projectNumber == null)
			projectNumber = "";
		return projectNumber;
	}

	public void setProjectNumber(String projectNumber) {
		this.projectNumber = projectNumber;
	}

	public Double getStandard() {
		if (standard == null)
			standard = 0.00;
		return standard;
	}

	public void setStandard(Double standard) {
		this.standard = standard;
	}

	public Double getQuantity() {
		if (quantity == null)
			quantity = 0.00;
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getAmount() {
		if (amount == null)
			amount = 0.00;
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getHappenDate() {
		return happenDate;
	}

	public void setHappenDate(Date happenDate) {
		this.happenDate = happenDate;
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
				result+="格式错误；";
			}
			else if (this.status.contains(UploadFileErrorTypeEnum.CONFIG_NOT_EXISTS + ",")) {
				result += "项目未配置；";
			}
			else if (this.status.contains(UploadFileErrorTypeEnum.CONFIG_WRONG + ",")) {
				result += "配置文件错误；";
			}else if (this.status.contains(UploadFileErrorTypeEnum.OTHER_ERROR + ",")) {
				result+=this.otherErrorMessage;
			}
			else{
				result += this.status;
			}
			return result;
		}
		return statusDisplay;
	}

	public void setStatusDisplay(UploadFileErrorTypeEnum type, boolean pass) {
		if(pass){
			if(this.status!=null){
				this.status=this.status.replaceAll(type+",", "");
			}
		}
		else{
			if (this.status == null) {
				this.status = type + ",";
			} else if(this.status.indexOf(type+",")<0){
				this.status += type + ",";
			}
		}
	}
	
	public void setStatusDisplay(UploadFileErrorTypeEnum type,String otherErrorMessage) {
		if (this.status == null) {
			this.status = type + ",";
		} else if(this.status.indexOf(type+",")<0){
			this.status += type + ",";
		}
		this.otherErrorMessage=otherErrorMessage;
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

	public void setOtherErrorMessage(String otherErrorMessage) {
		this.otherErrorMessage = otherErrorMessage;
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
		if (!(object instanceof SmallAssetPerformance)) {
			return false;
		}
		SmallAssetPerformance other = (SmallAssetPerformance) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.ge.apm.domain.SmallAssetPerformance[ id=" + id + " ]";
	}
}
