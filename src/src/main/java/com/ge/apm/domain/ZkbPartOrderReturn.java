package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2020/7/30
 * @description :
 */
@Entity
@Table(name = "zkb_part_order_return")
public class ZkbPartOrderReturn implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum DataStatusEnum{
        Pending,Accept,Reject
    }

    public enum ModifyFlagEnum{
        Y,N
    }

    public enum ReturnTypeNoQuotaEnum{
        badReturn,useNoReturn
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "part_order_id")
    private String partOrderId;


    /**
     * 配件号
     */
    @Column(name = "part_no")
    private String partNo;

    /**
     * 归还类型
     */
    @Column(name = "return_type")
    private String returnType;


    /**
     * 数量
     */
    @Column(name = "qty")
    private int qty=0;

    @Column(name = "part_serial_number")
    private String partSerialNumber;


    @Column(name = "part_quality_issue")
    private String partQualityIssue;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 运单号
     */
    @Column(name = "waybill_no")
    private String waybillNo;

    /**
     * 归还日期
     */
    @Column(name = "return_date")
    private Date returnDate;

    /**
     * Pending 待审核
     * Accept 审核通过
     * Reject 审核不通过
     */
    @Column(name = "data_satus")
    private String dataStatus= "Pending";

    @Column(name = "modify_flag")
    private String modifyFlag= "Y";

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "closed_remark")
    private String closedRemark;

    @Column(name = "closed_date")
    private Date closedDate;

    @Column(name = "closed_auditer")
    private String closedAuditer;

    @Column(name = "closed_auditer_uid")
    private String closedAuditerUid;

    @Column(name = "seven_returned_flag")
    private String sevenReturnedFlag;

    @Column(name = "udi")
    private String udi;

    @Column(name = "internal_packaging")
    private String internalPackaging;

    @Column(name = "quality_seal_type")
    private String qualitySealType;

    @Column(name = "quality_seal_status")
    private String qualitySealStatus;

    @Column(name = "part_quality")
    private String partQuality;

    public String getUdi() {
        return udi;
    }

    public void setUdi(String udi) {
        this.udi = udi;
    }

    public String getInternalPackaging() {
        return internalPackaging;
    }

    public void setInternalPackaging(String internalPackaging) {
        this.internalPackaging = internalPackaging;
    }

    public String getQualitySealType() {
        return qualitySealType;
    }

    public void setQualitySealType(String qualitySealType) {
        this.qualitySealType = qualitySealType;
    }

    public String getQualitySealStatus() {
        return qualitySealStatus;
    }

    public void setQualitySealStatus(String qualitySealStatus) {
        this.qualitySealStatus = qualitySealStatus;
    }

    public String getPartQuality() {
        return partQuality;
    }

    public void setPartQuality(String partQuality) {
        this.partQuality = partQuality;
    }

    public String getSevenReturnedFlag() {
        return sevenReturnedFlag;
    }

    public void setSevenReturnedFlag(String sevenReturnedFlag) {
        this.sevenReturnedFlag = sevenReturnedFlag;
    }

    public String getClosedRemark() {
        return closedRemark;
    }

    public void setClosedRemark(String closedRemark) {
        this.closedRemark = closedRemark;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    public String getClosedAuditer() {
        return closedAuditer;
    }

    public void setClosedAuditer(String closedAuditer) {
        this.closedAuditer = closedAuditer;
    }

    public String getClosedAuditerUid() {
        return closedAuditerUid;
    }

    public void setClosedAuditerUid(String closedAuditerUid) {
        this.closedAuditerUid = closedAuditerUid;
    }

    public String getModifyFlag() {
        return modifyFlag;
    }

    public void setModifyFlag(String modifyFlag) {
        this.modifyFlag = modifyFlag;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(String dataStatus) {
        this.dataStatus = dataStatus;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getPartSerialNumber() {
        return partSerialNumber;
    }

    public void setPartSerialNumber(String partSerialNumber) {
        this.partSerialNumber = partSerialNumber;
    }


    public String getPartQualityIssue() {
        return partQualityIssue;
    }

    public void setPartQualityIssue(String partQualityIssue) {
        this.partQualityIssue = partQualityIssue;
    }

    public String getPartOrderId() {
        return partOrderId;
    }

    public void setPartOrderId(String partOrderId) {
        this.partOrderId = partOrderId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        if (!(object instanceof ZkbPartOrderReturn)) {
            return false;
        }
        ZkbPartOrderReturn other = (ZkbPartOrderReturn) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
