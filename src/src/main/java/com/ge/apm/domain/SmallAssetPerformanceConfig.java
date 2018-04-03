package com.ge.apm.domain;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by 212605082 on 2018/3/7.
 */
@Entity
@Table(name = "small_asset_performance_config")
public class SmallAssetPerformanceConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    //设备名称
    @Column(name = "assetName")
    private String assetName;

    //科室
    @Column(name = "department")
    private String department;

    //项目编号
    @Column(name = "projectNumber")
    private String projectNumber;

    //项目名称
    @Column(name = "projectName")
    private String projectName;

    //占比
    @Column(name = "rate")
    private Double rate;

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
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "status_code")
    private String statusCode;

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

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Double getRate() {
    	if(rate==null)
    		rate=0.00;
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
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
			String result="";
			if (this.status.contains(UploadFileErrorTypeEnum.FORMAT_ERROR + ",")) {
				result+="格式错误；";
			} else if (this.status.contains(UploadFileErrorTypeEnum.RATE_WRONG + ",")) {
				result+="占比总和错误；";
			} else if (this.status.contains(UploadFileErrorTypeEnum.ASSET_NOT_EXISTS + ",")) {
				result+="设备名称不存在；";
			}else if (this.status.contains(UploadFileErrorTypeEnum.OTHER_ERROR + ",")) {
				result+=this.otherErrorMessage;
			}
			else{
				result+=this.status;
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
		if(StringUtils.isEmpty(this.status)){
			this.statusCode="1";
		}
		else{
			this.statusCode="2";
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

	private void setOtherErrorMessage(String otherErrorMessage) {
		this.otherErrorMessage = otherErrorMessage;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SmallAssetPerformanceConfig)) {
            return false;
        }
        SmallAssetPerformanceConfig other = (SmallAssetPerformanceConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
		return "com.ge.apm.domain.SmallAssetPerformanceConfig[ id=" + id + " ]";
	}
}
