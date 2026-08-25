package com.ge.apm.domain;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "asset_annual_inspection_report")
public class AssetAnnualInspectionReport implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String uid;
    @Column(name="special_asset_uid")
    private String  specialAssetUid;
    @Column(name="name")
    private String name;
    @Column(name="report_type")
    private String reportType;
    @Column(name="testing_unit")
    private String testingUnit;
    @Column(name="test_results")
    private String testResults;
    @Column(name="certificate_issuing_unit")
    private String certificateIssuingUnit;
    @Column(name="effective_time")
    private Date effectiveTime;
    @Column(name="expiry_date")
    private Date expiryDate;
    @Column(name="created_by_uid")
    private String createdByUid;

    @Column(name="created_by_name")
    private String createdByName;


    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "bo_uuid",referencedColumnName = "uid", insertable = false,updatable = false)
    private Set<V2_BlobObject> blobObjects;


    @Transient
    private Integer nodeId;

    @Transient
    private String assetName;

    @Transient
    private String assetSerialNum;

    @Transient
    private String assetQrCode;

    @Transient
    private String assetClinicalDeptName;

    @Transient
    private String importStatus;

    @Transient
    private String errorInfo;

    @Transient
    private Integer excelIdex;
    @Transient
    private String assetDeptName;

    @Transient
    private Boolean isUpdate;

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetSerialNum() {
        return assetSerialNum;
    }

    public void setAssetSerialNum(String assetSerialNum) {
        this.assetSerialNum = assetSerialNum;
    }

    public String getAssetQrCode() {
        return assetQrCode;
    }

    public void setAssetQrCode(String assetQrCode) {
        this.assetQrCode = assetQrCode;
    }

    public String getAssetClinicalDeptName() {
        return assetClinicalDeptName;
    }

    public void setAssetClinicalDeptName(String assetClinicalDeptName) {
        this.assetClinicalDeptName = assetClinicalDeptName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSpecialAssetUid() {
        return specialAssetUid;
    }

    public void setSpecialAssetUid(String specialAssetUid) {
        this.specialAssetUid = specialAssetUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getTestingUnit() {
        return testingUnit;
    }

    public void setTestingUnit(String testingUnit) {
        this.testingUnit = testingUnit;
    }

    public String getTestResults() {
        return testResults;
    }

    public void setTestResults(String testResults) {
        this.testResults = testResults;
    }

    public String getCertificateIssuingUnit() {
        return certificateIssuingUnit;
    }

    public void setCertificateIssuingUnit(String certificateIssuingUnit) {
        this.certificateIssuingUnit = certificateIssuingUnit;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCreatedByUid() {
        return createdByUid;
    }

    public void setCreatedByUid(String createdByUid) {
        this.createdByUid = createdByUid;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
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

    public Set<V2_BlobObject> getBlobObjects() {
        return blobObjects;
    }

    public void setBlobObjects(Set<V2_BlobObject> blobObjects) {
        this.blobObjects = blobObjects;
    }

    public String getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(String importStatus) {
        this.importStatus = importStatus;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public Integer getExcelIdex() {
        return excelIdex;
    }

    public void setExcelIdex(Integer excelIdex) {
        this.excelIdex = excelIdex;
    }

    public String getAssetDeptName() {
        return assetDeptName;
    }

    public void setAssetDeptName(String assetDeptName) {
        this.assetDeptName = assetDeptName;
    }

    public Boolean getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AssetAnnualInspectionReport)) {
            return false;
        }
        AssetAnnualInspectionReport other = (AssetAnnualInspectionReport) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.AssetAnnualInspectionReport[ uid=" + uid + " ]";
    }
}
