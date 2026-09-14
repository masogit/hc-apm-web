package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.ConstraintMode.NO_CONSTRAINT;

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

    // 基本信息
    @Column(name = "name")
    private String name;
    // 企业法人
    @Column(name = "corporate")
    private String corporate;
    // 法人联系方式
    @Column(name = "corporate_tel")
    private String corporateTel;
    // 统一社会信用编码
    @Column(name = "credit_code")
    private String creditCode;
    // 供应商组织机构编码
    @Column(name = "supplier_code")
    private String supplierCode;
    // 供应商名称缩写
    @Column(name = "supplier_abbreviation")
    private String supplierAbbreviation;
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
    // 项目对接人姓名
    @Column(name = "project_docker_name")
    private String projectDockerName;
    // 项目对接人电话
    @Column(name = "project_docker_tel")
    private String projectDockerTel;
    // 项目对接人职位
    @Column(name = "project_docker_position")
    private String projectDockerPosition;
    @Column(name = "maintainer_t")
    private Boolean maintainerT  = Boolean.FALSE;
    @Column(name = "manufacturer_t")
    private Boolean manufacturerT  = Boolean.FALSE;
    @Column(name = "supplier_t")
    private Boolean supplierT  = Boolean.FALSE;

    // 营业执照编码
    @Column(name = "license_code")
    private String licenseCode;
    // 营业执照开始日期
    @Column(name = "license_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date licenseStartDate;
    // 营业执照结束日期
    @Column(name = "license_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date licenseEndDate;
    // 营业执照登记机关批准日期
    @Column(name = "license_approval_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date licenseApprovalDate;
    // 业务范围
    @Column(name = "license_scope")
    private String licenseScope;
    // 营业执照附件
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bo_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    @Where(clause = "bo_type = 'VenderCompany' and bo_sub_type = 'license'")
    private List<V2_BlobObject> licenseAttachments = new ArrayList<>();
    // 维修资质附件
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bo_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    @Where(clause = "bo_type = 'VenderCompany' and bo_sub_type = 'maintenanceQualification'")
    private List<V2_BlobObject> maintenanceQualificationAttachments = new ArrayList<>();

    // 医疗器械经营许可证编码
    @Column(name = "permit_code")
    private String permitCode;
    // 医疗器械经营许可证开始日期
    @Column(name = "permit_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date permitStartDate;
    // 医疗器械经营许可证结束日期
    @Column(name = "permit_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date permitEndDate;
    // 经营发证日期
    @Column(name = "business_issuing_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date businessIssuingDate;
    // 医疗器械经营许可证附件
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bo_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    @Where(clause = "bo_type = 'VenderCompany' and bo_sub_type = 'permit'")
    private List<V2_BlobObject> permitAttachments = new ArrayList<>();
    // 医疗器械经营许可证售后服务承诺附件
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bo_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    @Where(clause = "bo_type = 'VenderCompany' and bo_sub_type = 'afterSaleService'")
    private List<V2_BlobObject> afterSaleServiceAttachments = new ArrayList<>();
    // 医疗器械经营许可证授权书附件
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bo_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    @Where(clause = "bo_type = 'VenderCompany' and bo_sub_type = 'authorization'")
    private List<V2_BlobObject> authorizationAttachments = new ArrayList<>();

    // 医疗器械经营备案凭证编码
    @Column(name = "business_filing_code")
    private String businessFilingCode;
    // 医疗器械经营备案凭证开始日期
    @Column(name = "business_filing_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date businessFilingStartDate;
    // 医疗器械经营备案凭证结束日期
    @Column(name = "business_filing_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date businessFilingEndDate;
    // 医疗器械经营备案凭证附件
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bo_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    @Where(clause = "bo_type = 'VenderCompany' and bo_sub_type = 'businessFiling'")
    private List<V2_BlobObject> businessFilingAttachments = new ArrayList<>();
    // 医疗器械经营备案凭证售后服务承诺附件
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bo_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    @Where(clause = "bo_type = 'VenderCompany' and bo_sub_type = 'businessFilingAfterSaleService'")
    private List<V2_BlobObject> businessFilingAfterSaleServiceAttachments = new ArrayList<>();
    // 医疗器械经营备案凭证授权书附件
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bo_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    @Where(clause = "bo_type = 'VenderCompany' and bo_sub_type = 'businessFilingAuthorization'")
    private List<V2_BlobObject> businessFilingAuthorizationAttachments = new ArrayList<>();

    // 医疗器械生产许可证编码
    @Column(name = "manufacturing_permit_code")
    private String manufacturingPermitCode;
    // 医疗器械生产许可证开始日期
    @Column(name = "manufacturing_permit_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date manufacturingPermitStartDate;
    // 医疗器械生产许可证结束日期
    @Column(name = "manufacturing_permit_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date manufacturingPermitEndDate;
    // 生产发证日期
    @Column(name = "production_issuing_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date productionIssuingDate;
    // 医疗器械生产许可证附件
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bo_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    @Where(clause = "bo_type = 'VenderCompany' and bo_sub_type = 'manufacturingPermit'")
    private List<V2_BlobObject> manufacturingPermitAttachments = new ArrayList<>();

    // 医疗器械生产备案凭证编码
    @Column(name = "manufacturing_filing_code")
    private String manufacturingFilingCode;
    // 医疗器械生产备案凭证开始日期
    @Column(name = "manufacturing_filing_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date manufacturingFilingStartDate;
    // 医疗器械生产备案凭证结束日期
    @Column(name = "manufacturing_filing_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date manufacturingFilingEndDate;
    // 医疗器械生产备案凭证附件
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bo_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    @Where(clause = "bo_type = 'VenderCompany' and bo_sub_type = 'manufacturingFiling'")
    private List<V2_BlobObject> manufacturingFilingAttachments = new ArrayList<>();

    // 产品注册证编号
    @Column(name = "product_registration_code")
    private String productRegistrationCode;
    // 产品注册证开始日期
    @Column(name = "product_registration_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date productRegistrationStartDate;
    // 产品注册证结束日期
    @Column(name = "product_registration_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date productRegistrationEndDate;
    // 产品注册证附件
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bo_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    @Where(clause = "bo_type = 'VenderCompany' and bo_sub_type = 'productRegistration'")
    private List<V2_BlobObject> productRegistrationAttachments = new ArrayList<>();

    // 提交人
    @Column(name = "author")
    private String author;

    //提交时间
    @Column(name = "submit_time")
    @Temporal(TemporalType.DATE)
    private Date submitTime;

    // 审核人
    @Column(name = "auditor")
    private String auditor;

    //审批时间
    @Column(name = "approval_time")
    @Temporal(TemporalType.DATE)
    private Date approvalTime;

    // 审核结果
    @Column(name = "approval_result")
    private String approvalResult;

    @Transient
    private Boolean relation;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "vender_company_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private List<Supplier> supplierList = new ArrayList<>();

    public Date getBusinessIssuingDate() {
        return businessIssuingDate;
    }

    public void setBusinessIssuingDate(Date businessIssuingDate) {
        this.businessIssuingDate = businessIssuingDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public Date getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Date approvalTime) {
        this.approvalTime = approvalTime;
    }

    public String getApprovalResult() {
        return approvalResult;
    }

    public void setApprovalResult(String approvalResult) {
        this.approvalResult = approvalResult;
    }

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

    public String getCorporate() {
        return corporate;
    }

    public void setCorporate(String corporate) {
        this.corporate = corporate;
    }

    public String getCorporateTel() {
        return corporateTel;
    }

    public void setCorporateTel(String corporateTel) {
        this.corporateTel = corporateTel;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
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

    public String getProjectDockerName() {
        return projectDockerName;
    }

    public void setProjectDockerName(String projectDockerName) {
        this.projectDockerName = projectDockerName;
    }

    public String getProjectDockerTel() {
        return projectDockerTel;
    }

    public void setProjectDockerTel(String projectDockerTel) {
        this.projectDockerTel = projectDockerTel;
    }

    public String getProjectDockerPosition() {
        return projectDockerPosition;
    }

    public void setProjectDockerPosition(String projectDockerPosition) {
        this.projectDockerPosition = projectDockerPosition;
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

    public Boolean getSupplierT() {
        return supplierT;
    }

    public void setSupplierT(Boolean supplierT) {
        this.supplierT = supplierT;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        this.licenseCode = licenseCode;
    }

    public Date getLicenseStartDate() {
        return licenseStartDate;
    }

    public void setLicenseStartDate(Date licenseStartDate) {
        this.licenseStartDate = licenseStartDate;
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

    public String getLicenseScope() {
        return licenseScope;
    }

    public void setLicenseScope(String licenseScope) {
        this.licenseScope = licenseScope;
    }

    public String getPermitCode() {
        return permitCode;
    }

    public void setPermitCode(String permitCode) {
        this.permitCode = permitCode;
    }

    public Date getPermitStartDate() {
        return permitStartDate;
    }

    public void setPermitStartDate(Date permitStartDate) {
        this.permitStartDate = permitStartDate;
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

    public Date getManufacturingPermitStartDate() {
        return manufacturingPermitStartDate;
    }

    public void setManufacturingPermitStartDate(Date manufacturingPermitStartDate) {
        this.manufacturingPermitStartDate = manufacturingPermitStartDate;
    }

    public Date getManufacturingPermitEndDate() {
        return manufacturingPermitEndDate;
    }

    public void setManufacturingPermitEndDate(Date manufacturingPermitEndDate) {
        this.manufacturingPermitEndDate = manufacturingPermitEndDate;
    }

    public Date getProductionIssuingDate() {
        return productionIssuingDate;
    }

    public void setProductionIssuingDate(Date productionIssuingDate) {
        this.productionIssuingDate = productionIssuingDate;
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

    public List<V2_BlobObject> getLicenseAttachments() {
        return licenseAttachments;
    }

    public void setLicenseAttachments(List<V2_BlobObject> licenseAttachments) {
        this.licenseAttachments = licenseAttachments;
    }

    public List<V2_BlobObject> getPermitAttachments() {
        return permitAttachments;
    }

    public void setPermitAttachments(List<V2_BlobObject> permitAttachments) {
        this.permitAttachments = permitAttachments;
    }

    public List<V2_BlobObject> getAfterSaleServiceAttachments() {
        return afterSaleServiceAttachments;
    }

    public void setAfterSaleServiceAttachments(List<V2_BlobObject> afterSaleServiceAttachments) {
        this.afterSaleServiceAttachments = afterSaleServiceAttachments;
    }

    public List<V2_BlobObject> getAuthorizationAttachments() {
        return authorizationAttachments;
    }

    public void setAuthorizationAttachments(List<V2_BlobObject> authorizationAttachments) {
        this.authorizationAttachments = authorizationAttachments;
    }

    public List<V2_BlobObject> getManufacturingPermitAttachments() {
        return manufacturingPermitAttachments;
    }

    public void setManufacturingPermitAttachments(List<V2_BlobObject> manufacturingPermitAttachments) {
        this.manufacturingPermitAttachments = manufacturingPermitAttachments;
    }

    public List<V2_BlobObject> getProductRegistrationAttachments() {
        return productRegistrationAttachments;
    }

    public void setProductRegistrationAttachments(List<V2_BlobObject> productRegistrationAttachments) {
        this.productRegistrationAttachments = productRegistrationAttachments;
    }


    public List<V2_BlobObject> getMaintenanceQualificationAttachments() {
        return maintenanceQualificationAttachments;
    }

    public void setMaintenanceQualificationAttachments(List<V2_BlobObject> maintenanceQualificationAttachments) {
        this.maintenanceQualificationAttachments = maintenanceQualificationAttachments;
    }

    public String getBusinessFilingCode() {
        return businessFilingCode;
    }

    public void setBusinessFilingCode(String businessFilingCode) {
        this.businessFilingCode = businessFilingCode;
    }

    public Date getBusinessFilingStartDate() {
        return businessFilingStartDate;
    }

    public void setBusinessFilingStartDate(Date businessFilingStartDate) {
        this.businessFilingStartDate = businessFilingStartDate;
    }

    public Date getBusinessFilingEndDate() {
        return businessFilingEndDate;
    }

    public void setBusinessFilingEndDate(Date businessFilingEndDate) {
        this.businessFilingEndDate = businessFilingEndDate;
    }

    public List<V2_BlobObject> getBusinessFilingAttachments() {
        return businessFilingAttachments;
    }

    public void setBusinessFilingAttachments(List<V2_BlobObject> businessFilingAttachments) {
        this.businessFilingAttachments = businessFilingAttachments;
    }

    public List<V2_BlobObject> getBusinessFilingAfterSaleServiceAttachments() {
        return businessFilingAfterSaleServiceAttachments;
    }

    public void setBusinessFilingAfterSaleServiceAttachments(List<V2_BlobObject> businessFilingAfterSaleServiceAttachments) {
        this.businessFilingAfterSaleServiceAttachments = businessFilingAfterSaleServiceAttachments;
    }

    public List<V2_BlobObject> getBusinessFilingAuthorizationAttachments() {
        return businessFilingAuthorizationAttachments;
    }

    public void setBusinessFilingAuthorizationAttachments(List<V2_BlobObject> businessFilingAuthorizationAttachments) {
        this.businessFilingAuthorizationAttachments = businessFilingAuthorizationAttachments;
    }

    public String getManufacturingFilingCode() {
        return manufacturingFilingCode;
    }

    public void setManufacturingFilingCode(String manufacturingFilingCode) {
        this.manufacturingFilingCode = manufacturingFilingCode;
    }

    public Date getManufacturingFilingStartDate() {
        return manufacturingFilingStartDate;
    }

    public void setManufacturingFilingStartDate(Date manufacturingFilingStartDate) {
        this.manufacturingFilingStartDate = manufacturingFilingStartDate;
    }

    public Date getManufacturingFilingEndDate() {
        return manufacturingFilingEndDate;
    }

    public void setManufacturingFilingEndDate(Date manufacturingFilingEndDate) {
        this.manufacturingFilingEndDate = manufacturingFilingEndDate;
    }

    public List<V2_BlobObject> getManufacturingFilingAttachments() {
        return manufacturingFilingAttachments;
    }

    public void setManufacturingFilingAttachments(List<V2_BlobObject> manufacturingFilingAttachments) {
        this.manufacturingFilingAttachments = manufacturingFilingAttachments;
    }

    public Boolean getRelation() {
        return relation;
    }

    public void setRelation(Boolean relation) {
        this.relation = relation;
    }

    public List<Supplier> getSupplierList() {
        return supplierList;
    }

    public void setSupplierList(List<Supplier> supplierList) {
        this.supplierList = supplierList;
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
