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
import javax.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "contract_info_item")
public class ContractInfoItem implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "contract_id", columnDefinition = "CHAR(32)")
    private String contractId;
    
    @JoinColumn(name = "contract_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private ContractInfo contractInfo;

    @Column(name = "asset_uid", columnDefinition = "CHAR(32)")
    private String assetUid;

    //制造商
    @Column(name = "supplier_uid", columnDefinition = "CHAR(32)")
    private String supplierUid;

    //型号
    @Column(name = "pattern")
    private String pattern;

    //交货期
    @Basic(optional = true)
    @Column(name = "delivered_date")
    @Temporal(TemporalType.DATE)
    private Date deliveredDate;

    //通知供货时间
    @Basic(optional = true)
    @Column(name = "notice_date")
    @Temporal(TemporalType.DATE)
    private Date noticeDate;

    //应到货时间
    @Basic(optional = true)
    @Column(name = "planned_arrive_date")
    @Temporal(TemporalType.DATE)
    private Date plannedArriveDate;

    //实际到货时间
    @Basic(optional = true)
    @Column(name = "arrive_date")
    @Temporal(TemporalType.DATE)
    private Date arriveDate;

    //是否在规定时间内到货
    @Column(name = "in_time_arrived")
    private Boolean inTimeArrived;

    //验收时间
    @Basic(optional = true)
    @Column(name = "ack_date")
    @Temporal(TemporalType.DATE)
    private Date ackDate;

    //出保日期
    @Basic(optional = true)
    @Column(name = "warranty_end_date")
    @Temporal(TemporalType.DATE)
    private Date warrantyEndDate;

    //质保期(保修时长)
    @Column(name = "warranty_year")
    private Double warrantyYear;
    
    //设备SN
    @Column(name = "serial_num")
    private String serialNum;
    
  
    //备注
    @Column(name = "comments")
    @Size(min = 0, max = 256)
    private String comments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public ContractInfo getContractInfo() {
        return contractInfo;
    }

    public void setContractInfo(ContractInfo contractInfo) {
        this.contractInfo = contractInfo;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getSupplierUid() {
        return supplierUid;
    }

    public void setSupplierUid(String supplierUid) {
        this.supplierUid = supplierUid;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Date getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Date deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public Date getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(Date noticeDate) {
        this.noticeDate = noticeDate;
    }

    public Date getPlannedArriveDate() {
        return plannedArriveDate;
    }

    public void setPlannedArriveDate(Date plannedArriveDate) {
        this.plannedArriveDate = plannedArriveDate;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public Boolean getInTimeArrived() {
        return inTimeArrived;
    }

    public void setInTimeArrived(Boolean inTimeArrived) {
        this.inTimeArrived = inTimeArrived;
    }

    public Date getAckDate() {
        return ackDate;
    }

    public void setAckDate(Date ackDate) {
        this.ackDate = ackDate;
    }

    public Date getWarrantyEndDate() {
        return warrantyEndDate;
    }

    public void setWarrantyEndDate(Date warrantyEndDate) {
        this.warrantyEndDate = warrantyEndDate;
    }

    public Double getWarrantyYear() {
        return warrantyYear;
    }

    public void setWarrantyYear(Double warrantyYear) {
        this.warrantyYear = warrantyYear;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
        if (!(object instanceof ContractInfoItem)) {
            return false;
        }
        ContractInfoItem other = (ContractInfoItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
