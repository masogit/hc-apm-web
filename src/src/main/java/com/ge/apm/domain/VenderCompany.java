package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @author lyj
 */
@Entity
@Table(name = "vender_company")
public class VenderCompany implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedDate;

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
    @Size(max = 16)
    @Column(name = "tel")
    private String tel;
    @Column(name = "email")
    private String email;

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
    //营业执照截止日期
    @Column(name = "license_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date licenseEndDate;
    //营业许可证编码
    @Column(name = "permit_code")
    private String permitCode;
    //营业许可证截止日期
    @Column(name = "permit_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date permitEndDate;
    //生产许可证编码
    @Column(name = "manufacturing_permit_code")
    private String manufacturingPermitCode;
    //生产许可证截止日期
    @Column(name = "manufacturing_permit_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date manufacturingPermitEndDate;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamePyCode() {
        return namePyCode;
    }

    public void setNamePyCode(String namePyCode) {
        this.namePyCode = namePyCode;
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

    public Date getLicenseEndDate() {
        return licenseEndDate;
    }

    public void setLicenseEndDate(Date licenseEndDate) {
        this.licenseEndDate = licenseEndDate;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof VenderCompany)) {
            return false;
        }
        VenderCompany other = (VenderCompany) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
}
