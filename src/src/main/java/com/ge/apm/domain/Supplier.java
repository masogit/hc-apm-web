/*
 */
package com.ge.apm.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ge.apm.service.utils.ChineseName;
import com.ge.apm.service.utils.PinyinUtil;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateTime;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author 212547631
 */
@Entity
@Table(name = "supplier")
public class Supplier implements Serializable, ChineseName {

    private static final long serialVersionUID = 1L;

    public enum Status {
        Enable, Disable
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Column(columnDefinition = "CHAR(32)")
    private String uid;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private int siteId;

    //hospital id is actually not needed, but micro service require it. so just sync
    @Basic(optional = false)
    @Column(name = "hospital_id")
    private Integer hospitalId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;
    @Column(name = "name_py_code")
    private String namePyCode;
    @Size(max = 32)
    @Column(name = "city")
    private String city;
    @Size(max = 128)
    @Column(name = "address")
    private String address;
    @Size(max = 16)
    @Column(name = "zipcode")
    private String zipcode;
    @Size(max = 16)
    @Column(name = "contactor")
    private String contactor;
    @Size(max = 30)
    @Column(name = "tel")
    private String tel;
    @Column(name = "email")
    private String email;
    @Column(name = "supplier_type")
    private String supplierType;
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "supplier_t")
    private Boolean supplierT = Boolean.FALSE;

    @Column(name = "maintainer_t")
    private Boolean maintainerT = Boolean.FALSE;

    @Column(name = "manufacturer_t")
    private Boolean manufacturerT = Boolean.FALSE;

    @CreatedBy
    @Column(name = "created_by", nullable = true, length = 50)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by", nullable = true, length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;

    @Size(max = 32)
    @Column(name = "source")
    private String source;

    @Column(name = "vender_company_id")
    private String venderCompanyId;

    //供应商资质
    //供应商编码
    @Column(name = "supplier_code")
    private String supplierCode;
    //供应商名称缩写
    @Column(name = "supplier_abbreviation")
    private String supplierAbbreviation;
    //企业法人
    @Column(name = "corporate")
    private String corporate;
    //统一社会信用编码
    @Column(name = "credit_code")
    private String creditCode;
    //营业执照编码
    @Column(name = "license_code")
    private String licenseCode;
    // 业务范围
    @Column(name = "license_scope")
    private String licenseScope;
    //营业执照截止日期
    @Column(name = "license_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date licenseEndDate;
    //营业执照登记机关批准日期
    @Column(name = "license_approval_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date licenseApprovalDate;
    //营业许可证编码
    @Column(name = "permit_code")
    private String permitCode;
    //营业许可证截止日期
    @Column(name = "permit_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date permitEndDate;
    //经营发证日期
    @Column(name = "business_issuing_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date businessIssuingDate;
    //生产许可证编码
    @Column(name = "manufacturing_permit_code")
    private String manufacturingPermitCode;
    //生产许可证截止日期
    @Column(name = "manufacturing_permit_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date manufacturingPermitEndDate;

    //生产发证日期
    @Column(name = "production_issuing_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date productionIssuingDate;
    //产品注册证编号
    @Column(name = "product_registration_code")
    private String productRegistrationCode;
    //产品注册证开始日期
    @Column(name = "product_registration_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date productRegistrationStartDate;
    //产品注册证结束日期
    @Column(name = "product_registration_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date productRegistrationEndDate;

    //生产许可证到期日期
    @Column(name = "production_license_expire_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date productionLicenseExpireDate;

    //注册许可证到期日期
    @Column(name = "business_license_expire_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date businessLicenseExpireDate;

    //注册证到期日期
    @Column(name = "registration_certificate_expire_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationCertificateExpireDate;

    //授权书到期时间
    @Column(name = "power_of_attorney_expire_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date powerOfAttorneyExpireDate;

    //供应商状态
    private String status = "Enable";

    @Transient
    private String taxpayerIdentityNumber;

    @Transient
    private Integer excelIndex;

    @Transient
    private String errorInfo;

    @Transient
    @TableField(exist = false)
    private String supplierAlertStatus = null;

    @Transient
    List<Date> licensesDateList = null;

    // 制造商存在于APM
    @Getter
    @Setter
    @Transient
    @TableField(exist = false)
    private boolean fromUdid = false;

    @PrePersist
    public void initializeUid() {
        if (uid == null) {
            uid = UUID.randomUUID().toString().replace("-", "");
        }
    }
    public Date getProductionIssuingDate() {
        return productionIssuingDate;
    }

    public void setProductionIssuingDate(Date productionIssuingDate) {
        this.productionIssuingDate = productionIssuingDate;
    }

    public Date getBusinessIssuingDate() {
        return businessIssuingDate;
    }

    public void setBusinessIssuingDate(Date businessIssuingDate) {
        this.businessIssuingDate = businessIssuingDate;
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

    public Supplier() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.namePyCode = name == null ? null : PinyinUtil.getShortPinyin(name).toLowerCase().replaceAll("[^0-9a-zA-Z]", "");
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getContactor() {
        return contactor;
    }

    public void setContactor(String contactor) {
        this.contactor = contactor;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getNamePyCode() {
        return namePyCode;
    }

    public void setNamePyCode(String namePyCode) {
        this.namePyCode = namePyCode;
    }

    public String getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getSupplierT() {
        return supplierT;
    }

    public void setSupplierT(Boolean supplierT) {
        this.supplierT = supplierT;
    }

    public Boolean getMaintainerT() {
        return maintainerT;
    }

    public void setMaintainerT(Boolean maintainerT) {
        this.maintainerT = maintainerT;
    }

    public Boolean getManufacturerT() {
        return manufacturerT;
    }

    public void setManufacturerT(Boolean manufacturerT) {
        this.manufacturerT = manufacturerT;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getVenderCompanyId() {
        return venderCompanyId;
    }

    public void setVenderCompanyId(String venderCompanyId) {
        this.venderCompanyId = venderCompanyId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierAbbreviation() {
        return supplierAbbreviation;
    }

    public void setSupplierAbbreviation(String supplierAbbreviation) {
        this.supplierAbbreviation = supplierAbbreviation;
    }

    public String getCorporate() {
        return corporate;
    }

    public void setCorporate(String corporate) {
        this.corporate = corporate;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        this.licenseCode = licenseCode;
    }

    public String getLicenseScope() {
        return licenseScope;
    }

    public void setLicenseScope(String licenseScope) {
        this.licenseScope = licenseScope;
    }

    public Date getLicenseEndDate() {
        return licenseEndDate;
    }

    public void setLicenseEndDate(Date licenseEndDate) {
        this.licenseEndDate = licenseEndDate;
    }

    public Date getLicenseApprovalDate() {
        return licenseApprovalDate;
    }

    public void setLicenseApprovalDate(Date licenseApprovalDate) {
        this.licenseApprovalDate = licenseApprovalDate;
    }

    public String getPermitCode() {
        return permitCode;
    }

    public void setPermitCode(String permitCode) {
        this.permitCode = permitCode;
    }

    public Date getPermitEndDate() {
        return permitEndDate;
    }

    public void setPermitEndDate(Date permitEndDate) {
        this.permitEndDate = permitEndDate;
    }


    public String getManufacturingPermitCode() {
        return manufacturingPermitCode;
    }

    public void setManufacturingPermitCode(String manufacturingPermitCode) {
        this.manufacturingPermitCode = manufacturingPermitCode;
    }

    public Date getManufacturingPermitEndDate() {
        return manufacturingPermitEndDate;
    }

    public void setManufacturingPermitEndDate(Date manufacturingPermitEndDate) {
        this.manufacturingPermitEndDate = manufacturingPermitEndDate;
    }


    public String getProductRegistrationCode() {
        return productRegistrationCode;
    }

    public void setProductRegistrationCode(String productRegistrationCode) {
        this.productRegistrationCode = productRegistrationCode;
    }

    public Date getProductRegistrationStartDate() {
        return productRegistrationStartDate;
    }

    public void setProductRegistrationStartDate(Date productRegistrationStartDate) {
        this.productRegistrationStartDate = productRegistrationStartDate;
    }

    public Date getProductRegistrationEndDate() {
        return productRegistrationEndDate;
    }

    public void setProductRegistrationEndDate(Date productRegistrationEndDate) {
        this.productRegistrationEndDate = productRegistrationEndDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getProductionLicenseExpireDate() {
        return productionLicenseExpireDate;
    }

    public void setProductionLicenseExpireDate(Date productionLicenseExpireDate) {
        this.productionLicenseExpireDate = productionLicenseExpireDate;
    }

    public Date getBusinessLicenseExpireDate() {
        return businessLicenseExpireDate;
    }

    public void setBusinessLicenseExpireDate(Date businessLicenseExpireDate) {
        this.businessLicenseExpireDate = businessLicenseExpireDate;
    }

    public Date getRegistrationCertificateExpireDate() {
        return registrationCertificateExpireDate;
    }

    public void setRegistrationCertificateExpireDate(Date registrationCertificateExpireDate) {
        this.registrationCertificateExpireDate = registrationCertificateExpireDate;
    }

    public Date getPowerOfAttorneyExpireDate() {
        return powerOfAttorneyExpireDate;
    }

    public void setPowerOfAttorneyExpireDate(Date powerOfAttorneyExpireDate) {
        this.powerOfAttorneyExpireDate = powerOfAttorneyExpireDate;
    }

    public String getTaxpayerIdentityNumber() {
        return taxpayerIdentityNumber;
    }

    public void setTaxpayerIdentityNumber(String taxpayerIdentityNumber) {
        this.taxpayerIdentityNumber = taxpayerIdentityNumber;
    }

    public Integer getExcelIndex() {
        return excelIndex;
    }

    public void setExcelIndex(Integer excelIndex) {
        this.excelIndex = excelIndex;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getSupplierAlertStatus() {
        if (ObjectUtil.isEmpty(licensesDateList) && ObjectUtil.isEmpty(supplierAlertStatus)) {
            licensesDateList = ListUtil.newArrayList(licenseEndDate, permitEndDate, manufacturingPermitEndDate, productRegistrationEndDate, productionLicenseExpireDate, businessLicenseExpireDate, registrationCertificateExpireDate, powerOfAttorneyExpireDate)
                    .stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
            Date now = DateUtil.now();
            DateTime nextMonth = DateUtil.nextMonth();
            for (Date date : licensesDateList) {
                if (date.before(now)) {
                    supplierAlertStatus = "supplierExpired";
                    break;
                } else if (date.after(now) && date.before(nextMonth)) {
                    supplierAlertStatus = "supplierExpiredSoon";
                }
            }
        }
        return supplierAlertStatus;
    }

    public void setSupplierAlertStatus(String supplierAlertStatus) {
        this.supplierAlertStatus = supplierAlertStatus;
    }

    @Override
    public String getPinYin() {
        return namePyCode;
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
        if (!(object instanceof Supplier)) {
            return false;
        }
        Supplier other = (Supplier) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.Supplier[ id=" + id + " ]";
    }

}
