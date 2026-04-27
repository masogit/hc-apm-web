package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "order_snatching_person", indexes = {
        @Index(name = "order_snatching_order_id_idx", columnList = "order_id")
})
public class OrderSnatchingPerson implements Serializable {


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String uid;


    @Column(name = "order_id")
    private String orderId;

    @Column(name = "order_type")
    private Integer orderType; // 1维修工单  2 保养工单
    @Column(name = "asset_uid")
    private String assetUid;
    @Column(name = "user_uid")
    private String userUid;

    @Column(name = "created_by", nullable = true, length = 50, updatable = false)
    private String createdBy;

    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof V2_WorkOrder_Step)) {
            return false;
        }
        OrderSnatchingPerson other = (OrderSnatchingPerson) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }
}
