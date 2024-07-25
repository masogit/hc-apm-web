/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.baomidou.mybatisplus.annotations.TableField;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import webapp.framework.util.TimeUtil;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "contract_info")
public class ContractInfo implements Serializable {

    public enum ContractType {
        Full, Artificial, Other, Equipment, Parts
    }

    public enum PriceType {
        LT1, BT1_5, BT5_50, GT50
    }

    public enum PurchaseType {
        Tenders, Negotiated, Continue
    }

    public enum AttachmentType {
        Contract, Tenders, Other
    }

    public enum ContractStatus {
        COMPLETE
    }

    public enum ExtensionStatus {
        DeliveryPending, AcceptancePending, AcceptanceComplete, ContractInvalid
    }

    public enum FundingSource {
        HospitalFunding, ScientificFunding, ProjectFunding, OtherFunding
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    // 合同名称
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;

    // 合同编号
    @Column(name = "contract_num")
    private String contractNum;

    // 外贸合同号
    @Column(name = "foreign_trade_num")
    private String foreignTradeNum;

    // 发票号
    @Column(name = "invoice_num")
    private String invoiceNum;


    //开始时间
    @Basic(optional = true)
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    //结束时间
    @Basic(optional = true)
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    //结束月数
    @Column(name = "end_months")
    private Integer endMonths;

    //合同总额
    @Basic(optional = true)
    @Column(name = "amount")
    private Double amount;

    //供应商
    @Column(name = "supplier_uid", columnDefinition = "CHAR(32)" )
    private String supplierUid;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_uid",referencedColumnName = "uid", insertable = false,updatable = false)
    private Supplier supplier;


    //合同类型
    @Column(name = "contract_type")
    private String contractType;

    //清单序号
    @Column(name = "list_num")
    private String listNum;

    //报告号
    @Column(name = "report_num")
    private String reportNum;

    //申请科室
    @Column(name = "apply_dept")
    private String applyDept;

    //项目申请名称
    @Column(name = "apply_project_name")
    private String applyProjectName;


    //论证会结果/时间
    @Basic(optional = true)
    @Column(name = "argument_end_date")
    @Temporal(TemporalType.DATE)
    private Date argumentEndDate;

    //技术参数送出时间
    @Basic(optional = true)
    @Column(name = "parameter_end_date")
    @Temporal(TemporalType.DATE)
    private Date parameterEndDate;

    //参数撰写人
    @Column(name = "parameter_person")
    private String parameterPerson;

    //开标时间
    @Basic(optional = true)
    @Column(name = "tender_open_date")
    @Temporal(TemporalType.DATE)
    private Date tenderOpenDate;

    //中标单位
    @Column(name = "bid_winner")
    private String bidWinner;

    //中标单位联系人
    @Column(name = "bid_winner_person")
    private String bidWinnerPerson;

    //中标单位联系电话
    @Column(name = "bid_winner_telphone")
    private String bidWinnerTelphone;

    //中标通知书时间
    @Basic(optional = true)
    @Column(name = "tender_notice_date")
    @Temporal(TemporalType.DATE)
    private Date tenderNoticeDate;

    //购入方式
    @Column(name = "tender_type")
    private String tenderType;

    //接招标文件材料
    @Basic(optional = true)
    @Column(name = "receive_file_date")
    @Temporal(TemporalType.DATE)
    private Date receiveFileDate;
    //拷样本、发《供货通知书》
    @Basic(optional = true)
    @Column(name = "copy_file_date")
    @Temporal(TemporalType.DATE)
    private Date copyFileDate;
    //接电子版合同#初审返修
    @Basic(optional = true)
    @Column(name = "first_check_date")
    @Temporal(TemporalType.DATE)
    private Date firstCheckDate;
    //送审#接审计修改意见
    @Basic(optional = true)
    @Column(name = "change_comment_date")
    @Temporal(TemporalType.DATE)
    private Date changeCommentDate;
    //律 师#律师意见及签字
    @Basic(optional = true)
    @Column(name = "lawer_sign_date")
    @Temporal(TemporalType.DATE)
    private Date lawerDignDate;
    //返回供货商修改意见
    @Basic(optional = true)
    @Column(name = "return_supplier_date")
    @Temporal(TemporalType.DATE)
    private Date returnSupplierDate;
    //收合同定稿【4份】
    @Basic(optional = true)
    @Column(name = "file_contract_date")
    @Temporal(TemporalType.DATE)
    private Date fileContractDate;
    //签字并盖合同专用章
    @Basic(optional = true)
    @Column(name = "sign_contract_date")
    @Temporal(TemporalType.DATE)
    private Date signContractDate;

    //中标时间
    @Column(name = "bid_date")
    @Temporal(TemporalType.DATE)
    private Date bidDate;
    //接电子版合同时间
    @Column(name = "receive_digital_contract_date")
    @Temporal(TemporalType.DATE)
    private Date receiveDigitalContractDate;
    //送审时间
    @Column(name = "send_contract_date")
    @Temporal(TemporalType.DATE)
    private Date sendContractDate;
    //送律师时间
    @Column(name = "send_lawer_date")
    @Temporal(TemporalType.DATE)
    private Date sendLawerDate;
    //中标单位领【1】份合同时间
    @Column(name = "receive_contract_date")
    @Temporal(TemporalType.DATE)
    private Date receiveContractDate;
    //合同转交存档时间
    @Column(name = "archive_date")
    @Temporal(TemporalType.DATE)
    private Date archiveDate;
    //应到货时间
    @Column(name = "expect_arrival_date")
    @Temporal(TemporalType.DATE)
    private Date expectArrivalDate;

    //备注
    @Column(name = "comments")
    @Size(min = 0, max = 256)
    private String comments;

    //经费来源
    @Column(name = "funding_source")
    private String fundingSource;
    //合同状态（扩展只对云南阜外医院）
    @Column(name = "extension_status")
    private String extensionStatus;

    //付款进度
    @Transient
    private String paymentProgress;

    //excel行数
    @Transient
    private int lineNum;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "created_by", nullable = true, length = 50)
    private String createdBy;

    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    @Column(name = "last_modified_by", nullable = true, length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;

    @Column(name = "contract_status")
    private String contractStatus;

    //预算申请时间
    @Column(name = "budget_request_time")
    @Temporal(TemporalType.DATE)
    private Date budgetRequestTime;

    //论证通过时间
    @Column(name = "argument_through_time")
    @Temporal(TemporalType.DATE)
    private Date argumentThroughTime;

    //上报管理部门时间
    @Column(name = "report_to_management_time")
    @Temporal(TemporalType.DATE)
    private Date reportToManagementTime;

    //立项时间
    @Column(name = "project_establishment_time")
    @Temporal(TemporalType.DATE)
    private Date projectEstablishmentTime;

    //到货时间
    @Column(name = "arrival_time")
    @Temporal(TemporalType.DATE)
    private Date arrivalTime;

    // 状态 1：新建 2：审核 3：退回 4：归档
    @Column(name = "status")
    private Integer status;

    // 采购单号
    @Column(name = "purchase_num")
    private String purchaseNum;

    // 标的名称
    @Column(name = "bid_name")
    private String bidName;

    // 招标编码
    @Column(name = "bid_num")
    private String bidNum;

    // 维保开始日期
    @Column(name = "warranty_start_date")
    private Date warrantyStartDate;

    // 维保结束日期
    @Column(name = "warranty_end_date")
    private Date warrantyEndDate;

    // 补充协议信息
    @Column(name = "supplementary")
    @Type(type = "text")
    private String supplementary;

    // 补充协议合同金额
    @Column(name = "supplementary_amount")
    private Double supplementaryAmount;

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
    @Column(name = "approval_status")
    private Boolean approvalStatus;

    // 审核结果
    @Column(name = "approval_result")
    private String approvalResult;

    @Column(name = "vender_company_id")
    private String venderCompanyId;

    public String getVenderCompanyId() {
        return venderCompanyId;
    }

    public void setVenderCompanyId(String venderCompanyId) {
        this.venderCompanyId = venderCompanyId;
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

    public Boolean getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Boolean approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovalResult() {
        return approvalResult;
    }

    public void setApprovalResult(String approvalResult) {
        this.approvalResult = approvalResult;
    }

    public Date getBudgetRequestTime() {
        return budgetRequestTime;
    }

    public void setBudgetRequestTime(Date budgetRequestTime) {
        this.budgetRequestTime = budgetRequestTime;
    }

    public Date getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Date approvalTime) {
        this.approvalTime = approvalTime;
    }

    public Date getArgumentThroughTime() {
        return argumentThroughTime;
    }

    public void setArgumentThroughTime(Date argumentThroughTime) {
        this.argumentThroughTime = argumentThroughTime;
    }

    public Date getReportToManagementTime() {
        return reportToManagementTime;
    }

    public void setReportToManagementTime(Date reportToManagementTime) {
        this.reportToManagementTime = reportToManagementTime;
    }

    public Date getProjectEstablishmentTime() {
        return projectEstablishmentTime;
    }

    public void setProjectEstablishmentTime(Date projectEstablishmentTime) {
        this.projectEstablishmentTime = projectEstablishmentTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getTenderType() {
        return tenderType;
    }

    public void setTenderType(String tenderType) {
        this.tenderType = tenderType;
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

    public String getContractNum() {
        return contractNum;
    }

    public void setContractNum(String contractNum) {
        this.contractNum = contractNum;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getSupplierUid() {
        return supplierUid;
    }

    public void setSupplierUid(String supplierUid) {
        this.supplierUid = supplierUid;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getListNum() {
        return listNum;
    }

    public void setListNum(String listNum) {
        this.listNum = listNum;
    }

    public String getReportNum() {
        return reportNum;
    }

    public void setReportNum(String reportNum) {
        this.reportNum = reportNum;
    }

    public String getApplyDept() {
        return applyDept;
    }

    public void setApplyDept(String applyDept) {
        this.applyDept = applyDept;
    }

    public String getApplyProjectName() {
        return applyProjectName;
    }

    public void setApplyProjectName(String applyProjectName) {
        this.applyProjectName = applyProjectName;
    }

    public Date getArgumentEndDate() {
        return argumentEndDate;
    }

    public void setArgumentEndDate(Date argumentEndDate) {
        this.argumentEndDate = argumentEndDate;
    }

    public Date getParameterEndDate() {
        return parameterEndDate;
    }

    public void setParameterEndDate(Date parameterEndDate) {
        this.parameterEndDate = parameterEndDate;
    }

    public String getParameterPerson() {
        return parameterPerson;
    }

    public void setParameterPerson(String parameterPerson) {
        this.parameterPerson = parameterPerson;
    }

    public Date getTenderOpenDate() {
        return tenderOpenDate;
    }

    public void setTenderOpenDate(Date tenderOpenDate) {
        this.tenderOpenDate = tenderOpenDate;
    }

    public String getBidWinner() {
        return bidWinner;
    }

    public void setBidWinner(String bidWinner) {
        this.bidWinner = bidWinner;
    }

    public String getBidWinnerPerson() {
        return bidWinnerPerson;
    }

    public void setBidWinnerPerson(String bidWinnerPerson) {
        this.bidWinnerPerson = bidWinnerPerson;
    }

    public String getBidWinnerTelphone() {
        return bidWinnerTelphone;
    }

    public void setBidWinnerTelphone(String bidWinnerTelphone) {
        this.bidWinnerTelphone = bidWinnerTelphone;
    }

    public Date getTenderNoticeDate() {
        return tenderNoticeDate;
    }

    public void setTenderNoticeDate(Date tenderNoticeDate) {
        this.tenderNoticeDate = tenderNoticeDate;
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    public Date getReceiveFileDate() {
        return receiveFileDate;
    }

    public void setReceiveFileDate(Date receiveFileDate) {
        this.receiveFileDate = receiveFileDate;
    }

    public Date getCopyFileDate() {
        return copyFileDate;
    }

    public void setCopyFileDate(Date copyFileDate) {
        this.copyFileDate = copyFileDate;
    }

    public Date getFirstCheckDate() {
        return firstCheckDate;
    }

    public void setFirstCheckDate(Date firstCheckDate) {
        this.firstCheckDate = firstCheckDate;
    }

    public Date getChangeCommentDate() {
        return changeCommentDate;
    }

    public void setChangeCommentDate(Date changeCommentDate) {
        this.changeCommentDate = changeCommentDate;
    }

    public Date getLawerDignDate() {
        return lawerDignDate;
    }

    public void setLawerDignDate(Date lawerDignDate) {
        this.lawerDignDate = lawerDignDate;
    }

    public Date getReturnSupplierDate() {
        return returnSupplierDate;
    }

    public void setReturnSupplierDate(Date returnSupplierDate) {
        this.returnSupplierDate = returnSupplierDate;
    }

    public Date getFileContractDate() {
        return fileContractDate;
    }

    public void setFileContractDate(Date fileContractDate) {
        this.fileContractDate = fileContractDate;
    }

    public Date getSignContractDate() {
        return signContractDate;
    }

    public void setSignContractDate(Date signContractDate) {
        this.signContractDate = signContractDate;
    }

    public Integer getEndMonths() {
        return endMonths;
    }

    public void setEndMonths(Integer endMonths) {
        this.endMonths = endMonths;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public String getForeignTradeNum() {
        return foreignTradeNum;
    }

    public void setForeignTradeNum(String foreignTradeNum) {
        this.foreignTradeNum = foreignTradeNum;
    }

    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public Date getBidDate() {
        return bidDate;
    }

    public void setBidDate(Date bidDate) {
        this.bidDate = bidDate;
    }

    public Date getReceiveDigitalContractDate() {
        return receiveDigitalContractDate;
    }

    public void setReceiveDigitalContractDate(Date receiveDigitalContractDate) {
        this.receiveDigitalContractDate = receiveDigitalContractDate;
    }

    public Date getSendContractDate() {
        return sendContractDate;
    }

    public void setSendContractDate(Date sendContractDate) {
        this.sendContractDate = sendContractDate;
    }

    public Date getSendLawerDate() {
        return sendLawerDate;
    }

    public void setSendLawerDate(Date sendLawerDate) {
        this.sendLawerDate = sendLawerDate;
    }

    public Date getReceiveContractDate() {
        return receiveContractDate;
    }

    public void setReceiveContractDate(Date receiveContractDate) {
        this.receiveContractDate = receiveContractDate;
    }

    public Date getArchiveDate() {
        return archiveDate;
    }

    public void setArchiveDate(Date archiveDate) {
        this.archiveDate = archiveDate;
    }

    public Date getExpectArrivalDate() {
        return expectArrivalDate;
    }

    public void setExpectArrivalDate(Date expectArrivalDate) {
        this.expectArrivalDate = expectArrivalDate;
    }

    public String getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource;
    }

    public String getExtensionStatus() {
        return extensionStatus;
    }

    public void setExtensionStatus(String extensionStatus) {
        this.extensionStatus = extensionStatus;
    }

    public String getPaymentProgress() {
        return paymentProgress;
    }

    public void setPaymentProgress(String paymentProgress) {
        this.paymentProgress = paymentProgress;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPurchaseNum() {
        return purchaseNum;
    }

    public void setPurchaseNum(String purchaseNum) {
        this.purchaseNum = purchaseNum;
    }

    public String getBidName() {
        return bidName;
    }

    public void setBidName(String bidName) {
        this.bidName = bidName;
    }

    public String getBidNum() {
        return bidNum;
    }

    public void setBidNum(String bidNum) {
        this.bidNum = bidNum;
    }

    public Date getWarrantyStartDate() {
        return warrantyStartDate;
    }

    public void setWarrantyStartDate(Date warrantyStartDate) {
        this.warrantyStartDate = warrantyStartDate;
    }

    public Date getWarrantyEndDate() {
        return warrantyEndDate;
    }

    public void setWarrantyEndDate(Date warrantyEndDate) {
        this.warrantyEndDate = warrantyEndDate;
    }

    public String getSupplementary() {
        return supplementary;
    }

    public void setSupplementary(String supplementary) {
        this.supplementary = supplementary;
    }

    public Double getSupplementaryAmount() {
        return supplementaryAmount;
    }

    public void setSupplementaryAmount(Double supplementaryAmount) {
        this.supplementaryAmount = supplementaryAmount;
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
        if (!(object instanceof ContractInfo)) {
            return false;
        }
        ContractInfo other = (ContractInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public boolean isContractComplete() {
        boolean isComplete = false;
        if(ContractStatus.COMPLETE.toString().equals(this.getContractStatus())) {
            isComplete = true;
        }
        return isComplete;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @Transient
    @TableField(exist = false)
    private String completion;

    @Transient
    @TableField(exist = false)
    private String contractAlertStatus = null;

    @Transient
    @TableField(exist = false)
    @Getter @Setter
    private String assetUid = null;

    public String getCompletion() {
        return completion;
    }

    public void setCompletion(String completion) {
        this.completion = completion;
    }

    public String getContractAlertStatus() {
        return contractAlertStatus;
    }

    public void setContractAlertStatus(Integer contractAlertDays) {
        if(ObjectUtil.isAllNotEmpty(completion, expectArrivalDate) && !completion.contains("到货完成")) {
            DateTime dt = TimeUtil.toJodaDate(expectArrivalDate);
            if(dt.isBeforeNow()) {
                contractAlertStatus = "contractExpired";
            } else if(dt.minusDays(contractAlertDays).isBeforeNow()) {
                contractAlertStatus = "contractExpiredSoon";
            }
        }
    }


}