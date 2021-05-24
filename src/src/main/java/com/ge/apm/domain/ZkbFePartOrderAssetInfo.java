package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author : 503202755
 * @date : 2020/12/30
 * @description:备件申请信息
 */
@Entity
@Table(name = "zkb_fe_part_order_asset_info")
public class ZkbFePartOrderAssetInfo {


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 备件号
     */
    @Column(name = "part_order_no")
    private String assetNo;


    /**
     * 备件描述
     */
    @Column(name = "asset_description")
    private String assetDescription;

    /**
     * 备件数量
     */
    @Column(name = "asset_qty")
    private Integer assetQty;

    /**
     * 主表ID
     */
    @Column(name = "fe_part_order_id")
    private String fePartOrderId;

    /**
     * Order#
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 价格
     */
    @Column(name = "price")
    private String price;

    /**
     * 备件（说明是否DOA或者short life
     */
    private String sparePart;

    /**
     * 创建时间
     */
    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "comment")
    private String comment;

    /**
     * 修改备注
     */
    @Column(name = "update_comment")
    private String updateComment;

    @Column(name = "before_Adjust_Asset_Qty")
    private Integer beforeAdjustAssetQty;

    /**
     * 用于判断list中选中是哪个对象
     */
    @Transient
    private int index;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public String getAssetDescription() {
        return assetDescription;
    }

    public void setAssetDescription(String assetDescription) {
        this.assetDescription = assetDescription;
    }

    public Integer getAssetQty() {
        return assetQty;
    }

    public void setAssetQty(Integer assetQty) {
        this.assetQty = assetQty;
    }

    public String getFePartOrderId() {
        return fePartOrderId;
    }

    public void setFePartOrderId(String fePartOrderId) {
        this.fePartOrderId = fePartOrderId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSparePart() {
        return sparePart;
    }

    public void setSparePart(String sparePart) {
        this.sparePart = sparePart;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUpdateComment() {
        return updateComment;
    }

    public void setUpdateComment(String updateComment) {
        this.updateComment = updateComment;
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
        if (!(object instanceof ZkbFePartOrderAssetInfo)) {
            return false;
        }
        ZkbFePartOrderAssetInfo other = (ZkbFePartOrderAssetInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbFePartOrderAssetInfo[ id=" + id + " ]";
    }

    public Integer getBeforeAdjustAssetQty() {
        return beforeAdjustAssetQty;
    }

    public void setBeforeAdjustAssetQty(Integer beforeAdjustAssetQty) {
        this.beforeAdjustAssetQty = beforeAdjustAssetQty;
    }
}
