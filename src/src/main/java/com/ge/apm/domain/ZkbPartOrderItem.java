package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author : 503189362
 * @date : 2020/6/22
 * @description :
 */
@Entity
@Table(name = "zkb_part_order_item")
public class ZkbPartOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 配件名称
     */
    @Column(name = "part_name")
    private String partName;

    /**
     * 配件数量
     */
    @Column(name = "part_qty")
    private int partQty;

    @Column(name = "pre_part_qty")
    private Integer prePartQty;

    /**
     * 配件SN(序列号)
     */
    @Column(name = "serial_number")
    private String serialNumber;

    /**
     * 备注（说明是否DOA或者Short life）
     */
    @Column(name = "remark")
    private String remark;

    @Column(name = "price")
    private String price;

    @Column(name = "order_no")
    private String orderNo;

    public Integer getPrePartQty() {
        return prePartQty;
    }

    public void setPrePartQty(Integer prePartQty) {
        this.prePartQty = prePartQty;
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

    /**
     * 用于判断list中选中是哪个对象
     */
    @Transient
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public int getPartQty() {
        return partQty;
    }

    public void setPartQty(int partQty) {
        this.partQty = partQty;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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
        if (!(object instanceof ZkbPartOrderItem)) {
            return false;
        }
        ZkbPartOrderItem other = (ZkbPartOrderItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
