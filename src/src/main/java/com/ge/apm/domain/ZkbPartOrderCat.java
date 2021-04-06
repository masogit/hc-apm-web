package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2020/7/16
 * @description :
 */
@Entity
@Table(name = "zkb_part_order_cat")
public class ZkbPartOrderCat implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * HighValue:高值
     * NormalValue:普通
     * SpecialValue:特殊
     */
    public enum ValueType {
        HighValue,NormalValue,SpecialValue
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "cat_no")
    private String catNo;

    @Column(name = "cat_model")
    private String catModel;

    @Column(name = "cat_type")
    private String catType;

    @Column(name = "value_type")
    private String valueType;

    @Column(name = "price")
    private String price;

    @Column(name = "is_vaild")
    private String isVaild = "Y"; // Y  N


    @Temporal(TemporalType.DATE)//生成yyyy-MM-dd类型的日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)//生成yyyy-MM-dd类型的日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_date")
    private Date endDate;

    @Transient
    private String catText;

    public String getCatText() {
        return this.catModel +"(" + this.catNo +")";
    }

    public void setCatText(String catText) {
        this.catText = catText;
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

    public String getIsVaild() {
        return isVaild;
    }

    public void setIsVaild(String isVaild) {
        this.isVaild = isVaild;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCatNo() {
        return catNo;
    }

    public void setCatNo(String catNo) {
        this.catNo = catNo;
    }

    public String getCatModel() {
        return catModel;
    }

    public void setCatModel(String catModel) {
        this.catModel = catModel;
    }

    public String getCatType() {
        return catType;
    }

    public void setCatType(String catType) {
        this.catType = catType;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
