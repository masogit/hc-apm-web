package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2021/6/8
 * @description :
 */
@Entity
@Table(name = "zkb_contract_quota_log")
public class ZkbContractQuotaLog implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum OpTypeEnum{
        ContractAdd,ContractUpdate,ZkbPartOrderDeduct,ZkbPartOrderReturn,ZkbFePartOrderDeduct,ZkbFePartOrderReturn,AutoAdd
    }

    public enum OrderTypeEnum{
        Spare,SpareArtificial,ProbeAny,ProbeNormal,ProbeSpecial,Artificial,Probe,PartsHigh,CssbMaintenance,Consumable
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * zkb ge contract
     */
    @Column(name = "business_type")
    private String businessType;

    @Column(name = "order_type")
    private String orderType;

    @Column(name = "quota_total")
    private Integer quotaTotal;

    @Column(name = "quota")
    private Integer quota;

    @Column(name = "quota_apply")
    private Integer quotaApply;

    @Column(name = "quota_use")
    private Integer quotaUse;

    @Column(name = "op_type")
    private String opType;

    @Column(name = "contract_id")
    private String contractId;

    @Column(name = "contract_no")
    private String contractNo;

    @Column(name = "zkb_part_order_id")
    private String zkbPartOrderId;

    @Column(name = "zkb_part_order_no")
    private String zkbPartOrderNo;

    @Column(name = "created_user_uid")
    private String createdUserUid;

    @Column(name = "created_user_name")
    private String createdUserName;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "step")
    private String step;

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getZkbPartOrderId() {
        return zkbPartOrderId;
    }

    public void setZkbPartOrderId(String zkbPartOrderId) {
        this.zkbPartOrderId = zkbPartOrderId;
    }

    public String getZkbPartOrderNo() {
        return zkbPartOrderNo;
    }

    public void setZkbPartOrderNo(String zkbPartOrderNo) {
        this.zkbPartOrderNo = zkbPartOrderNo;
    }

    public String getCreatedUserUid() {
        return createdUserUid;
    }

    public void setCreatedUserUid(String createdUserUid) {
        this.createdUserUid = createdUserUid;
    }

    public String getCreatedUserName() {
        return createdUserName;
    }

    public void setCreatedUserName(String createdUserName) {
        this.createdUserName = createdUserName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Integer getQuotaTotal() {
        return quotaTotal;
    }

    public void setQuotaTotal(Integer quotaTotal) {
        this.quotaTotal = quotaTotal;
    }

    public Integer getQuota() {
        return quota;
    }

    public void setQuota(Integer quota) {
        this.quota = quota;
    }

    public Integer getQuotaApply() {
        return quotaApply;
    }

    public void setQuotaApply(Integer quotaApply) {
        this.quotaApply = quotaApply;
    }

    public Integer getQuotaUse() {
        return quotaUse;
    }

    public void setQuotaUse(Integer quotaUse) {
        this.quotaUse = quotaUse;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ZkbContractQuotaLog)) {
            return false;
        }
        ZkbContractQuotaLog other = (ZkbContractQuotaLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
