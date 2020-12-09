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
        New,Old
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
     * new 可以修改，删改
     * old 不能操作，
     */
    @Column(name = "data_satus")
    private String dataStatus= "New";

    @Column(name = "order_no")
    private String orderNo;

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
