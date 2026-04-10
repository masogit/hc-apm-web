package com.ge.apm.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "idle_equipment_application")
public class IdleEquipmentApplication implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String uid;

    @Column(name = "application_number")

    private String applicationNumber;//申请编号
    @Column(name = "asset_uid")

    private String assetUid;
    @Column(name = "asset_group")

    private String assetGroup;
    @Column(name = "asset_name")

    private String assetName;

    @Column(name = "stock_hospital_uid")
    private String stockHospitalUid; //入库医院
    @Column(name = "stock_site_uid")

    private String stockSiteUid; //入库院区
    @Column(name = "stock_dept_uid")
    private String stockDeptUid;//入库科室

    @Column(name = "stock_dept_name")
    private String stockDeptName;//入库科室
    @Column(name = "status")
    private Integer status;//状态    1 暂存中， 2待审批，3已通过，4已退回，5已取消
    @Column(name = "application_user_uid")
    private String applicationUserUid;

    @CreatedDate
    @Column(name = "created_date")
    private Date  createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by", nullable = true, length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;
    @Column(name = "company_name")
    private String companyName;  //单位名称

    @Column(name = "medical_consortium_type")

    private Integer medicalConsortiumType;//医共体类别
    @Column(name = "contact_person")

    private String contactPerson;  //联系人
    @Column(name = "contact_number")
    private String contactNumber;   //联系电话
    @Column(name = "application_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date  applicationDate;   //申报日期


    @Column(name = "function_type")
    private String functionType;//设备型号

    @Size(max = 64)
    @Column(name = "manufacture")
    private String manufacturer; // 生产厂家

    @Column(name = "purchase_date")
    private Date purchaseDate; // 购置日期

    @Column(name = "purchase_price")
    private Double purchasePrice;//采购价格(元)

    @Column(name = "used_years")
    private Integer usedYears; // 累计使用年限 - 年



    @Column(name = "depreciation_period_years")
    private Integer depreciationPeriodYears; // 折旧期限（规定）- 年

    @Column(name = "is_over_depreciation")
    private Boolean isOverDepreciation; // 是否超折旧期限

    @Column(name = "over_depreciation_reason")
    private String overDepreciationReason; // 超期需说明仍具备使用价值的依据

    @Column(name = "device_status")
    private String deviceStatus; // 设备现状：性能完好 / 可修复后正常使用 / 超期仍具备使用价值

    @Column(name = "maintenance_summary")
    private String maintenanceSummary; // 维修记录摘要（近2年）

    @Column(name = "purchase_voucher_no")
    private String purchaseVoucherNo; // 购置凭证编号

    @Column(name = "property_nature")
    private String propertyNature; // 设备产权性质：国有资产（产权清晰） / 其他

    @Column(name = "property_nature_desc")

    private String propertyNatureDesc;//设备产权性质说明
    @Column(name = "has_rights_defect")
    private Boolean hasRightsDefect; // 是否存在权利瑕疵

    @Column(name = "rights_defect_description")
    private String rightsDefectDescription; // 权利瑕疵说明


    @Column(name = "idle_reasons")
    private String idleReasons; // 闲置原因   JSON数组，如 ["业务调整", "更新换代"]

    @Column(name = "other_reason_description")
    private String otherReasonDescription; // “其他”原因的详细说明

    @Column(name = "expected_idle_years")
    private Integer expectedIdleYears; // 预计闲置年限

    @Column(name = "expected_idle_months")
    private Integer expectedIdleMonths; // 预计闲置月数

    @Column(name = "audit_result")
    private Boolean auditResult;//审核结果

    @Column(name = "audit_result_reason")
    private String auditResultReason; // 审核结果理由
    @Column(name = "registration_number")

    private String registrationNumber;//登记编号
    @Column(name = "audit_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditDate;//审核时间
    @Column(name = "audit_signature")
    private String  auditSignature;

    @Column(name = "audit_user_uid")
    private String  auditUserUid;
    @Column(name = "attachments")
    private String attachments;

    @Column(name = "attachments_other_description")
    private String attachmentsOtherDescription;
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;
    @JoinColumn(insertable = false, updatable = false, name = "asset_uid", referencedColumnName = "uid", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    @NotFound(action = NotFoundAction.IGNORE)
    private AssetInfo assetInfo;

    @JoinColumn(insertable = false, updatable = false, name = "application_user_uid", referencedColumnName = "uid", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    @NotFound(action = NotFoundAction.IGNORE)
    private UserAccount userAccount;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getStockSiteUid() {
        return stockSiteUid;
    }

    public void setStockSiteUid(String stockSiteUid) {
        this.stockSiteUid = stockSiteUid;
    }

    public String getStockDeptUid() {
        return stockDeptUid;
    }

    public void setStockDeptUid(String stockDeptUid) {
        this.stockDeptUid = stockDeptUid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getApplicationUserUid() {
        return applicationUserUid;
    }

    public void setApplicationUserUid(String applicationUserUid) {
        this.applicationUserUid = applicationUserUid;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
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

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getMedicalConsortiumType() {
        return medicalConsortiumType;
    }

    public void setMedicalConsortiumType(Integer medicalConsortiumType) {
        this.medicalConsortiumType = medicalConsortiumType;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Integer getUsedYears() {
        return usedYears;
    }

    public void setUsedYears(Integer usedYears) {
        this.usedYears = usedYears;
    }

    public Integer getDepreciationPeriodYears() {
        return depreciationPeriodYears;
    }

    public void setDepreciationPeriodYears(Integer depreciationPeriodYears) {
        this.depreciationPeriodYears = depreciationPeriodYears;
    }

    public Boolean getOverDepreciation() {
        return isOverDepreciation;
    }

    public void setOverDepreciation(Boolean overDepreciation) {
        isOverDepreciation = overDepreciation;
    }

    public String getOverDepreciationReason() {
        return overDepreciationReason;
    }

    public void setOverDepreciationReason(String overDepreciationReason) {
        this.overDepreciationReason = overDepreciationReason;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getMaintenanceSummary() {
        return maintenanceSummary;
    }

    public void setMaintenanceSummary(String maintenanceSummary) {
        this.maintenanceSummary = maintenanceSummary;
    }

    public String getPurchaseVoucherNo() {
        return purchaseVoucherNo;
    }

    public void setPurchaseVoucherNo(String purchaseVoucherNo) {
        this.purchaseVoucherNo = purchaseVoucherNo;
    }

    public String getPropertyNature() {
        return propertyNature;
    }

    public void setPropertyNature(String propertyNature) {
        this.propertyNature = propertyNature;
    }

    public Boolean getHasRightsDefect() {
        return hasRightsDefect;
    }

    public void setHasRightsDefect(Boolean hasRightsDefect) {
        this.hasRightsDefect = hasRightsDefect;
    }

    public String getRightsDefectDescription() {
        return rightsDefectDescription;
    }

    public void setRightsDefectDescription(String rightsDefectDescription) {
        this.rightsDefectDescription = rightsDefectDescription;
    }

    public String getIdleReasons() {
        return idleReasons;
    }

    public void setIdleReasons(String idleReasons) {
        this.idleReasons = idleReasons;
    }

    public String getOtherReasonDescription() {
        return otherReasonDescription;
    }

    public void setOtherReasonDescription(String otherReasonDescription) {
        this.otherReasonDescription = otherReasonDescription;
    }

    public Integer getExpectedIdleYears() {
        return expectedIdleYears;
    }

    public void setExpectedIdleYears(Integer expectedIdleYears) {
        this.expectedIdleYears = expectedIdleYears;
    }

    public Integer getExpectedIdleMonths() {
        return expectedIdleMonths;
    }

    public void setExpectedIdleMonths(Integer expectedIdleMonths) {
        this.expectedIdleMonths = expectedIdleMonths;
    }

    public Boolean getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(Boolean auditResult) {
        this.auditResult = auditResult;
    }

    public String getAuditResultReason() {
        return auditResultReason;
    }

    public void setAuditResultReason(String auditResultReason) {
        this.auditResultReason = auditResultReason;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditSignature() {
        return auditSignature;
    }

    public void setAuditSignature(String auditSignature) {
        this.auditSignature = auditSignature;
    }


    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }


    public String getPropertyNatureDesc() {
        return propertyNatureDesc;
    }

    public void setPropertyNatureDesc(String propertyNatureDesc) {
        this.propertyNatureDesc = propertyNatureDesc;
    }

    public String getStockDeptName() {
        return stockDeptName;
    }

    public void setStockDeptName(String stockDeptName) {
        this.stockDeptName = stockDeptName;
    }

    public String getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(String assetGroup) {
        this.assetGroup = assetGroup;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }


    public String getAuditUserUid() {
        return auditUserUid;
    }

    public void setAuditUserUid(String auditUserUid) {
        this.auditUserUid = auditUserUid;
    }

    public String getStockHospitalUid() {
        return stockHospitalUid;
    }

    public void setStockHospitalUid(String stockHospitalUid) {
        this.stockHospitalUid = stockHospitalUid;
    }


    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getAttachmentsOtherDescription() {
        return attachmentsOtherDescription;
    }

    public void setAttachmentsOtherDescription(String attachmentsOtherDescription) {
        this.attachmentsOtherDescription = attachmentsOtherDescription;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IdleEquipmentApplication)) {
            return false;
        }
        IdleEquipmentApplication other = (IdleEquipmentApplication) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }


}
