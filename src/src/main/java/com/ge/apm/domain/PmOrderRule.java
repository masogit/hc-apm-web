package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "pm_order_rule")
public class PmOrderRule implements Serializable{
	private static final long serialVersionUID = -1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
	private Integer id;
    
    @Column(name = "name")
	private String name;
    
    @Column(name = "asset_group")
	private Integer assetGroup;//设备类型
    
    @Column(name = "manufacture")
	private String manufacture;//品牌
    
    @Column(name = "function_type")
	private String functionType;//设备型号
    
    @Column(name = "approve_user_id")
	private Integer approveUserId;//审核人
    
    @Column(name = "owner_id")
	private Integer ownerId;//制定者
    
    @Column(name = "created_date")
	private Date createdDate;//制定日期
    
    @Column(name = "period")
	private Integer period;//保养周期,单位‘月’
    
    @Column(name = "check_list_uid")
	private String checkListUID;//检查项uid
    
    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;

	@Column(name="institution_uid",columnDefinition = "CHAR(32)")
	private String institutionUID;

	@Column(name="hospital_uid",columnDefinition = "CHAR(32)")
	private String hospitalUID;

	@Column(name="site_uid",columnDefinition = "CHAR(32)")
	private String siteUID;
    
    @Column(name = "status")
    private Integer status;//0、待审核 1、已审核
    
    @Column(name="uid",columnDefinition = "CHAR(32)")
    private String uid;
    
    @Column(name = "safety_report")
	private String safetyReport;//安全报告
    
    @Column(name = "performance_report")
	private String performanceReport;//性能报告

	@Column(name = "report_storage_id")
	private String reportStorageId; // 报告模版

	@Column(name = "report_template_name")
	private String reportTemplateName; // 报告模版名称
    
    @Column(name = "remark")
    private String remark;//备注
    
    @Column(name = "asset_name")
    private String assetName;//设备名称
    
    @Column(name = "level")
    private Integer level;//规则级别
    
    public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	@PrePersist
    public void initializeUid() {
        if(uid==null)
            uid = UUID.randomUUID().toString().replace("-", "");
    }
    
	public String getSafetyReport() {
		return safetyReport;
	}

	public void setSafetyReport(String safetyReport) {
		this.safetyReport = safetyReport;
	}

	public String getPerformanceReport() {
		return performanceReport;
	}

	public void setPerformanceReport(String performanceReport) {
		this.performanceReport = performanceReport;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUid() {
		return uid;
	}
    
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getTenantUID() {
		return tenantUID;
	}
	public void setTenantUID(String tenantUID) {
		this.tenantUID = tenantUID;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAssetGroup() {
		return assetGroup;
	}
	public void setAssetGroup(Integer assetGroup) {
		this.assetGroup = assetGroup;
	}
	public String getManufacture() {
		return manufacture;
	}
	public void setManufacture(String manufacture) {
		this.manufacture = manufacture;
	}

	public String getFunctionType() {
		return functionType;
	}
	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}
	public Integer getApproveUserId() {
		return approveUserId;
	}
	public void setApproveUserId(Integer approveUserId) {
		this.approveUserId = approveUserId;
	}
	public Integer getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	public String getCheckListUID() {
		return checkListUID;
	}
	public void setCheckListUID(String checkListUID) {
		this.checkListUID = checkListUID;
	}
	public String getReportStorageId() {
		return reportStorageId;
	}
	public void setReportStorageId(String reportStorageId) {
		this.reportStorageId = reportStorageId;
	}
	public String getReportTemplateName() {
		return reportTemplateName;
	}
	public void setReportTemplateName(String reportTemplateName) {
		this.reportTemplateName = reportTemplateName;
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
        if (!(object instanceof PmOrderRule)) {
            return false;
        }
        PmOrderRule other = (PmOrderRule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }


}
