package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2021/10/21
 * @description :
 */
@Entity
@Table(name = "zkb_part_order_modify_log")
public class ZkbPartOrderModifyLog {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "createUser_uid")
    private String createUserUid;

    @Column(name = "create_user_name")
    private String createUserName;

    @Column(name = "create_user_date")
    private Date createUserDate;

    @Column(name = "sr_no")
    private String srNo;

    /**
     *
     */
    @Column(name = "activity_no")
    private String activityNo;

    /**
     *
     */
    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "price")
    private String price;

    @Column(name = "part_order_id")
    private String partOrderId;

    @Column(name = "part_order_item_id")
    private String partOrderItemId;

    @Column(name = "actual_part_no")
    private String actualPartNo;

    @Column(name = "part_name")
    private String partName;

    private String needLetterFlag;

    public String getNeedLetterFlag() {
        return needLetterFlag;
    }

    public void setNeedLetterFlag(String needLetterFlag) {
        this.needLetterFlag = needLetterFlag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateUserUid() {
        return createUserUid;
    }

    public void setCreateUserUid(String createUserUid) {
        this.createUserUid = createUserUid;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Date getCreateUserDate() {
        return createUserDate;
    }

    public void setCreateUserDate(Date createUserDate) {
        this.createUserDate = createUserDate;
    }

    public String getSrNo() {
        return srNo;
    }

    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    public String getActivityNo() {
        return activityNo;
    }

    public void setActivityNo(String activityNo) {
        this.activityNo = activityNo;
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

    public String getPartOrderId() {
        return partOrderId;
    }

    public void setPartOrderId(String partOrderId) {
        this.partOrderId = partOrderId;
    }

    public String getPartOrderItemId() {
        return partOrderItemId;
    }

    public void setPartOrderItemId(String partOrderItemId) {
        this.partOrderItemId = partOrderItemId;
    }

    public String getActualPartNo() {
        return actualPartNo;
    }

    public void setActualPartNo(String actualPartNo) {
        this.actualPartNo = actualPartNo;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }
}
