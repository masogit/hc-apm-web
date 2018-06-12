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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "contract_info")
public class ContractInfo implements Serializable {

    public enum ContractType {
        Full, Artificial, Other, Equipment
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

    //合同金额
    @Basic(optional = true)
    @Column(name = "amount")
    private double amount;

    //供应商
    @Column(name = "supplier_uid", columnDefinition = "CHAR(32)")
    private String supplierUid;

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

    //技术撰写人
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
    
    
    //备注
    @Column(name = "comments")
    @Size(min = 0, max = 256)
    private String comments;
    
    

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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

}
