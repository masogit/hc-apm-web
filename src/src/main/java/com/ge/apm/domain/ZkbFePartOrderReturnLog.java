package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : 503202755
 * @date : 2020/1/6
 * @description:GEFE维修申请归还
 */
@Entity
@Table(name = "zkb_fe_part_order_return_log")
public class ZkbFePartOrderReturnLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * FEGE维修流程ID
     */
    @Column(name = "part_order_id")
    private String partOrderId;

    /**
     * 备件申请ID
     */
    @Column(name = "asset_info_id")
    private String assetInfoId;

    /**
     * 备件号
     */
    @Column(name = "asset_no")
    private String assetNo;

    /**
     * Order#
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 归还数量
     */
    @Column(name = "qty")
    private Integer qty;

    /**
     * 归还类型
     */
    @Column(name = "return_type")
    private String returnType;

    @Column(name = "part_serial_number")
    private String partSerialNumber;


    @Column(name = "part_quality_issue")
    private String partQualityIssue;


    /**
     * 归还日期
     */
    @Column(name = "return_date")
    private Date returnDate;

    @Column(name = "comment")
    private String comment;

    @Transient
    private int index;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "create_user_id")
    private String createUserId;

    @Column(name = "create_user_name")
    private String createUserName;

    /**
     * 运单号
     */
    @Column(name = "waybill_no")
    private String waybillNo;

    @Column(name = "new_cat_sn")
    private String newCatSn;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ZkbFePartOrderReturnLog)) {
            return false;
        }
        ZkbFePartOrderReturnLog other = (ZkbFePartOrderReturnLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartOrderId() {
        return partOrderId;
    }

    public void setPartOrderId(String partOrderId) {
        this.partOrderId = partOrderId;
    }

    public String getAssetInfoId() {
        return assetInfoId;
    }

    public void setAssetInfoId(String assetInfoId) {
        this.assetInfoId = assetInfoId;
    }

    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public String getNewCatSn() {
        return newCatSn;
    }

    public void setNewCatSn(String newCatSn) {
        this.newCatSn = newCatSn;
    }
}
