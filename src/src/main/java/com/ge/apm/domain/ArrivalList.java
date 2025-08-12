/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "arrival_list")
public class ArrivalList implements Serializable {

    public enum ProcessType {
        Normal, Easy
    }
    public enum SupplierStatus {
        Saved, PendingInvoice
    }
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    //到货单名称
    @Basic(optional = true)
    @Column(name = "name")
    private String name;

    //到货单类型  [1:医院购买,2:赠送,3:投放,4;试用,5:借用]
    @Column(name = "order_type")
    private Integer orderType;

    //设备名称
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "asset_group")
    private Integer assetGroup;//设备分类

    //规格型号
    @Column(name = "function_type")
    private String functionType;

    //数量
    @Column(name = "quantity")
    private Integer quantity;

    //生产厂家
    @Size(max = 64)
    @Column(name = "manufacture")
    private String manufacture;

    @Size(max = 64)
    @Column(name = "manufacture_id")
    private String manufactureId;//制造商Id

    //品牌/制造商简称
    @Size(max = 64)
    @Column(name = "make_short_name")
    private String makerShortName;

    //品牌
    @Size(max = 64)
    @Column(name = "brand")
    private String brand;

    //产地
    @Size(max = 64)
    @Column(name = "origin")
    private String origin;

    //发票号
    @Size(max = 64)
    @Column(name = "invoice_num")
    private String invoiceNum;

    //发票日期
    @Column(name = "invoice_date")
    @Temporal(TemporalType.DATE)
    private Date invoiceDate;

    //合同价格
    @Column(name = "contract_price")
    private Double contractPrice;

    //关联合同订购项
    @Column(name = "contract_item_id", columnDefinition = "CHAR(32)")
    private String contractItemId;

    //合同号
    @Size(max = 64)
    @Column(name = "contract_num")
    private String contractNum;

    //供应商
    @Size(max = 64)
    @Column(name = "vendor")
    private String vendor;

    //供应商Id
    @Column(name = "supplier_id")
    private Integer supplierId;

    //供应商负责人
    @Column(name = "supplier_user_id")
    private Integer supplierUserId;

    //经办人
    @Size(max = 64)
    @Column(name = "agent")
    private String agent;

    //设备分类编码
    @Size(max = 64)
    @Column(name = "class_code")
    private String classCode;

    //填表人
    @Column(name = "creator_uid", columnDefinition = "CHAR(32)")
    private String creatorUID;

    //到货日期
    @Column(name = "arrival_date")
    @Temporal(TemporalType.DATE)
    private Date arrivalDate;

    @Column(name = "process_type")
    private String processType;

    // 1:new 2:partial finished 3.finished 4.canceled
    @Basic(optional = false)
    @NotNull
    @Column(name = "status")
    private int status;

    //供应商接口信息
    //合同签订日期
    @Column(name = "contract_date")
    @Temporal(TemporalType.DATE)
    private Date contractDate;
    //外贸协议
    @Column(name = "foreign_trade_agreements")
    private String foreignTradeAgreements;
    //开箱安装日期
    @Column(name = "box_installation_date")
    @Temporal(TemporalType.DATE)
    private Date boxInstallationDate;
    //使用日期
    @Column(name = "using_date")
    @Temporal(TemporalType.DATE)
    private Date usingDate;
    //使用人
    @Column(name = "using_person")
    private String usingPerson;
    //使用人电话
    @Column(name = "using_person_tel")
    private String usingPersonTel;
    //联系人
    @Column(name = "contact_person")
    private String contactPerson;
    //联系人电话
    @Column(name = "contact_person_tel")
    private String contactPersonTel;
    //维修联系人
    @Column(name = "repairer")
    private String repairer;
    //维修联系人电话
    @Column(name = "repairer_tel")
    private String repairerTel;
    //培训资料
    @Column(name = "training_info")
    private String trainingInfo;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @JoinColumn(name = "creator_uid", referencedColumnName = "uid", insertable = false, updatable = false,nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private UserAccount creator;

    @Transient
    private boolean cannotDeleted;

    // 入库单号
    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "supplier_status", columnDefinition = "VARCHAR(60)")
    private String supplierStatus;

    // 批文号（=合同表批文号）
    @Column(name = "approval_no")
    private String approvalNo;

    @Column(name = "is_edit")
    private Boolean isEdit;


    @Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;//设备所属科室

    @Size(max = 64)
    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;//所属科室

    @Column(name = "enable_generate_qrcode")
    private Boolean enableGenerateQrcode;

    @Column(name = "vender_company_id")
    private String venderCompanyId;

    @Column(name = "file_no")
    private String fileNo;

    // 采购日期
    @Column(name = "purchase_date")
    private Date purchaseDate;

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }

    public String getVenderCompanyId() {
        return venderCompanyId;
    }

    public void setVenderCompanyId(String venderCompanyId) {
        this.venderCompanyId = venderCompanyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Double getContractPrice() {
        return contractPrice;
    }

    public void setContractPrice(Double contractPrice) {
        this.contractPrice = contractPrice;
    }

    public String getContractNum() {
        return contractNum;
    }

    public void setContractNum(String contractNum) {
        this.contractNum = contractNum;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getCreatorUID() {
        return creatorUID;
    }

    public void setCreatorUID(String creatorUID) {
        this.creatorUID = creatorUID;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public UserAccount getCreator() {
        return creator;
    }

    public void setCreator(UserAccount creator) {
        this.creator = creator;
    }

    public String getContractItemId() {
        return contractItemId;
    }

    public void setContractItemId(String contractItemId) {
        this.contractItemId = contractItemId;
    }

    public boolean isCannotDeleted() {
        return cannotDeleted;
    }

    public void setCannotDeleted(boolean cannotDeleted) {
        this.cannotDeleted = cannotDeleted;
    }

    public String getManufactureId() {
        return manufactureId;
    }

    public void setManufactureId(String manufactureId) {
        this.manufactureId = manufactureId;
    }

    public Integer getSupplierUserId() {
        return supplierUserId;
    }

    public void setSupplierUserId(Integer supplierUserId) {
        this.supplierUserId = supplierUserId;
    }

    public String getForeignTradeAgreements() {
        return foreignTradeAgreements;
    }

    public void setForeignTradeAgreements(String foreignTradeAgreements) {
        this.foreignTradeAgreements = foreignTradeAgreements;
    }

    public Date getBoxInstallationDate() {
        return boxInstallationDate;
    }

    public void setBoxInstallationDate(Date boxInstallationDate) {
        this.boxInstallationDate = boxInstallationDate;
    }

    public Date getUsingDate() {
        return usingDate;
    }

    public void setUsingDate(Date usingDate) {
        this.usingDate = usingDate;
    }

    public String getUsingPerson() {
        return usingPerson;
    }

    public void setUsingPerson(String usingPerson) {
        this.usingPerson = usingPerson;
    }

    public String getUsingPersonTel() {
        return usingPersonTel;
    }

    public void setUsingPersonTel(String usingPersonTel) {
        this.usingPersonTel = usingPersonTel;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPersonTel() {
        return contactPersonTel;
    }

    public void setContactPersonTel(String contactPersonTel) {
        this.contactPersonTel = contactPersonTel;
    }

    public String getRepairer() {
        return repairer;
    }

    public void setRepairer(String repairer) {
        this.repairer = repairer;
    }

    public String getRepairerTel() {
        return repairerTel;
    }

    public void setRepairerTel(String repairerTel) {
        this.repairerTel = repairerTel;
    }

    public String getTrainingInfo() {
        return trainingInfo;
    }

    public void setTrainingInfo(String trainingInfo) {
        this.trainingInfo = trainingInfo;
    }

    public Date getContractDate() {
        return contractDate;
    }

    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getSupplierStatus() {
        return supplierStatus;
    }

    public String getApprovalNo() {
        return approvalNo;
    }

    public void setApprovalNo(String approvalNo) {
        this.approvalNo = approvalNo;
    }

    public void setSupplierStatus(String supplierStatus) {
        this.supplierStatus = supplierStatus;
    }

    public Boolean getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(Boolean isEdit) {
        this.isEdit = isEdit;
    }

    public String getMakerShortName() {
        return makerShortName;
    }

    public void setMakerShortName(String makerShortName) {
        this.makerShortName = makerShortName;
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ArrivalList)) {
            return false;
        }
        ArrivalList other = (ArrivalList) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }


    public String getClinicalDeptUID() {
        return clinicalDeptUID;
    }

    public void setClinicalDeptUID(String clinicalDeptUID) {
        this.clinicalDeptUID = clinicalDeptUID;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }

    public Boolean getEnableGenerateQrcode() {
        return enableGenerateQrcode;
    }

    public void setEnableGenerateQrcode(Boolean enableGenerateQrcode) {
        this.enableGenerateQrcode = enableGenerateQrcode;
    }
}
