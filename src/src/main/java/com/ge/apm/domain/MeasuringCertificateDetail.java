package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 212605082 on 2017/11/6.
 */
@Entity
@Table(name = "measuring_certificate_detail")
public class MeasuringCertificateDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    //设备UID
    @Column(name = "asset_uid")
    private String assetUID;

    @JoinColumn(insertable=false,updatable=false, name = "asset_uid", referencedColumnName = "uid")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private AssetInfo assetInfo;

    //证书编号
    @Column(name = "certificate_no")
    private String certificateNo;

    //器具名称
    @Column(name = "asset_name")
    private String assetName;

    //制造厂
    @Column(name = "asset_manufacturer")
    private String assetManufacturer;

    //型号
    @Column(name = "asset_model")
    private String assetModel;

    //设备序列号
    @Column(name = "asset_sn")
    private String assetSN;

    //财产编号
    @Column(name = "finance_no")
    private String financeNo;

    //器具编号
    @Column(name = "asset_no")
    private String assetNo;

    //付款否
    @Column(name = "is_pay")
    private Boolean isPay;

    //检定结论
    @NotNull
    @Column(name = "status")
    private Integer status;

    //检定日期
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "check_date")
    private Date checkDate;

    //有效期至
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "valid_date_to")
    private Date validDateTo;

    //检测单位
    @Column(name = "check_company")
    private String checkCompany;

    //检定人员
    @Column(name = "check_person")
    private String checkPerson;

    //项别
    @Column(name = "check_item")
    private String checkItem;

    //种别
    @Column(name = "type")
    private String type;

    //送检单位
    @Column(name = "department")
    private String department;

    //备注
    @Column(name = "comments")
    private String comments;

    //过期否
    @Column(name = "is_expired")
    private Boolean isExpired;

    //存档记录
    @Column(name = "file_no")
    private String fileNo;

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
    private String statusDisplay;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetManufacturer() {
        return assetManufacturer;
    }

    public void setAssetManufacturer(String assetManufacturer) {
        this.assetManufacturer = assetManufacturer;
    }

    public String getAssetModel() {
        return assetModel;
    }

    public void setAssetModel(String assetModel) {
        this.assetModel = assetModel;
    }

    public String getFinanceNo() {
        return financeNo;
    }

    public void setFinanceNo(String financeNo) {
        this.financeNo = financeNo;
    }

    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public Boolean getPay() {
        return isPay;
    }

    public void setPay(Boolean pay) {
        isPay = pay;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public Date getValidDateTo() {
        return validDateTo;
    }

    public void setValidDateTo(Date validDateTo) {
        this.validDateTo = validDateTo;
    }

    public String getCheckCompany() {
        return checkCompany;
    }

    public void setCheckCompany(String checkCompany) {
        this.checkCompany = checkCompany;
    }

    public String getCheckPerson() {
        return checkPerson;
    }

    public void setCheckPerson(String checkPerson) {
        this.checkPerson = checkPerson;
    }

    public String getCheckItem() {
        return checkItem;
    }

    public void setCheckItem(String checkItem) {
        this.checkItem = checkItem;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Boolean getExpired() {
        return isExpired;
    }

    public void setExpired(Boolean expired) {
        isExpired = expired;
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
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    public String getAssetUID() {
        return assetUID;
    }

    public void setAssetUID(String assetUID) {
        this.assetUID = assetUID;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getAssetSN() {
        return assetSN;
    }

    public void setAssetSN(String assetSN) {
        this.assetSN = assetSN;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
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
        if (!(object instanceof MeasuringCertificateDetail)) {
            return false;
        }
        MeasuringCertificateDetail other = (MeasuringCertificateDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.MeasuringCertificateDetail[ id=" + id + " ]";
    }
}
