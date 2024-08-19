package com.ge.apm.domain;


import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "efficiency_equipment_info")
public class EfficiencyEquipmentInfo  implements  Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    @Column(name="asset_uid")
    private String assetUid;
    @Basic(optional = false)
    @Size(min = 1, max = 128)
    @Column(name = "assetName")
    private String assetName;

    @Size(max = 64)
    @Column(name = "serial_num")
    private String serialNum;//制造商序列号

    @Size(max = 64)
    @Column(name = "qr_code")
    private String qrCode;//二维码

    @Column(name = "purchase_price")
    private Double purchasePrice;//采购价格(元)

    @Column(name = "salvage_value")
    private Double salvageValue;//最终残值(元)

    @Column(name = "function_type")
    private String functionType;//设备型号

    @Column(name = "usage_rate")
    private Double usageRate;  //使用率

    @Column(name = "check_number_people")
    private Double checkNumberPeople;  //检查人次

    @Column(name = "checkt_tme_length")
    private Double checkTimeLength ;  //检查时长

    @Column(name = "income")
    private Double income;  //收入

    @Column(name = "cost")
    private Double cost;  //成本

    @Column(name = "profits")
    private Double profits;  //利润

    @Column(name = "profits_rate")
    private Double profitsRate;  //利润率

    @Column(name = "created_by_uid")
    private String createdByUid;

    @CreatedDate
    @Column(name = "created_date", nullable = true)
    private Date createdDate;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_uid",referencedColumnName = "uid", insertable = false,updatable = false)
    private AssetInfo assetInfo;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Double getSalvageValue() {
        return salvageValue;
    }

    public void setSalvageValue(Double salvageValue) {
        this.salvageValue = salvageValue;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public Double getUsageRate() {
        return usageRate;
    }

    public void setUsageRate(Double usageRate) {
        this.usageRate = usageRate;
    }

    public Double getCheckNumberPeople() {
        return checkNumberPeople;
    }

    public void setCheckNumberPeople(Double checkNumberPeople) {
        this.checkNumberPeople = checkNumberPeople;
    }

    public Double getCheckTimeLength() {
        return checkTimeLength;
    }

    public void setCheckTimeLength(Double checkTimeLength) {
        this.checkTimeLength = checkTimeLength;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getProfits() {
        return profits;
    }

    public void setProfits(Double profits) {
        this.profits = profits;
    }

    public Double getProfitsRate() {
        return profitsRate;
    }

    public void setProfitsRate(Double profitsRate) {
        this.profitsRate = profitsRate;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getCreatedByUid() {
        return createdByUid;
    }

    public void setCreatedByUid(String createdByUid) {
        this.createdByUid = createdByUid;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EfficiencyEquipmentInfo)) {
            return false;
        }
        EfficiencyEquipmentInfo other = (EfficiencyEquipmentInfo) object;
        return (this.uid != null || other.uid == null) && (this.uid == null || this.uid.equals(other.uid));
    }

}
