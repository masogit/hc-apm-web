package com.ge.apm.edgeserver.dataupload.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@XmlRootElement(name = "Table")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "asset_performance_record_delta")
public class AssetPerformanceRecordDelta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @XmlTransient
    @Basic(optional = false)
    @NotNull
    @Column(name = "create_time")
    @Temporal(TemporalType.DATE)
    private Date createTime;

    @XmlTransient
    @Column(name = "upload_status")
    private String uploadStatus;

    // 科室
    @XmlElement
    @Column(name = "department")
    private String department;

    // 费别=费用类别
    @XmlElement
    @Column(name = "cost_type")
    private String costType;

    // 编码
    @XmlElement
    @Column(name = "code")
    private String code;

    // 项目名称
    @XmlElement
    @Column(name = "projectName")
    private String projectName;

    // 项目编号
    @XmlElement
    @Column(name = "projectNumber")
    private String projectNumber;

    // 一类标准=单价
    @XmlElement
    @Column(name = "standard")
    private Double standard;

    // 数量
    @XmlElement
    @Column(name = "quantity")
    private Double quantity;

    // 金额
    @XmlElement
    @Column(name = "amount")
    private Double amount;

    // 日期
    @XmlElement
    @Column(name = "happen_date")
    @Temporal(TemporalType.DATE)
    private Date happenDate;

    @XmlElement
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    @Size(max = 32)
    private String tenantUID;

    @XmlElement
    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @XmlElement
    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @XmlElement
    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @XmlElement(name = "GroupName")
    @Column(name = "group_name")
    private String groupName;

    @XmlElement(name = "GroupCode")
    @Column(name = "group_code")
    private String groupCode;

    @XmlElement(name = "OpenDepartName")
    @Column(name = "pre_exam_department")
    private String preExamDepartment;

    @XmlElement(name = "OpenDepartCode")
    @Column(name = "pre_exam_department_code")
    private String preExamDepartmentCode;

    @XmlElement(name = "InPeople")
    @Column(name = "inpatient_count")
    private Double inpatientCount;

    @XmlElement(name = "InIncome")
    @Column(name = "inpatient_revenue")
    private Double inpatientRevenue;

    @XmlElement(name = "DayPeople")
    @Column(name = "outpatient_count")
    private Double outpatientCount;

    @XmlElement(name = "DayIncome")
    @Column(name = "outpatient_revenue")
    private Double outpatientRevenue;

    @XmlElement(name = "SPPeople")
    @Column(name = "emergency_count")
    private Double emergencyCount;

    @XmlElement(name = "SPIncome")
    @Column(name = "emergency_revenue")
    private Double emergencyRevenue;


    public AssetPerformanceRecordDelta() {
        uploadStatus = "N";
        createTime = Calendar.getInstance().getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public Double getStandard() {
        return standard;
    }

    public void setStandard(Double standard) {
        this.standard = standard;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getAmount() {
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getPreExamDepartment() {
        return preExamDepartment;
    }

    public void setPreExamDepartment(String preExamDepartment) {
        this.preExamDepartment = preExamDepartment;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getPreExamDepartmentCode() {
        return preExamDepartmentCode;
    }

    public void setPreExamDepartmentCode(String preExamDepartmentCode) {
        this.preExamDepartmentCode = preExamDepartmentCode;
    }

    public Double getInpatientCount() {
        return inpatientCount;
    }

    public void setInpatientCount(Double inpatientCount) {
        this.inpatientCount = inpatientCount;
    }

    public Double getInpatientRevenue() {
        return inpatientRevenue;
    }

    public void setInpatientRevenue(Double inpatientRevenue) {
        this.inpatientRevenue = inpatientRevenue;
    }

    public Double getOutpatientCount() {
        return outpatientCount;
    }

    public void setOutpatientCount(Double outpatientCount) {
        this.outpatientCount = outpatientCount;
    }

    public Double getOutpatientRevenue() {
        return outpatientRevenue;
    }

    public void setOutpatientRevenue(Double outpatientRevenue) {
        this.outpatientRevenue = outpatientRevenue;
    }

    public Double getEmergencyCount() {
        return emergencyCount;
    }

    public void setEmergencyCount(Double emergencyCount) {
        this.emergencyCount = emergencyCount;
    }

    public Double getEmergencyRevenue() {
        return emergencyRevenue;
    }

    public void setEmergencyRevenue(Double emergencyRevenue) {
        this.emergencyRevenue = emergencyRevenue;
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
        if (!(object instanceof AssetPerformanceRecordDelta)) {
            return false;
        }
        AssetPerformanceRecordDelta other = (AssetPerformanceRecordDelta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AssetPerformanceRecordDelta{" +
                "id='" + id + '\'' +
                ", createTime=" + createTime +
                ", uploadStatus='" + uploadStatus + '\'' +
                ", department='" + department + '\'' +
                ", costType='" + costType + '\'' +
                ", code='" + code + '\'' +
                ", projectName='" + projectName + '\'' +
                ", projectNumber='" + projectNumber + '\'' +
                ", standard=" + standard +
                ", quantity=" + quantity +
                ", amount=" + amount +
                ", happenDate=" + happenDate +
                ", tenantUID='" + tenantUID + '\'' +
                ", institutionUID='" + institutionUID + '\'' +
                ", hospitalUID='" + hospitalUID + '\'' +
                ", siteUID='" + siteUID + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupCode='" + groupCode + '\'' +
                ", preExamDepartment='" + preExamDepartment + '\'' +
                '}';
    }
}
