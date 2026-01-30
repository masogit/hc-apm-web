package com.ge.apm.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Data
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
    private BigDecimal usageRate;  //使用率

    @Column(name = "check_number_people")
    private BigDecimal checkNumberPeople;  //检查人次

    @Column(name = "checkt_tme_length")
    private BigDecimal checkTimeLength ;  //检查时长

    @Column(name = "income")
    private BigDecimal income;  //收入

    @Column(name = "cost")
    private BigDecimal cost;  //成本

    @Column(name = "profits")
    private BigDecimal profits;  //利润

    @Column(name = "profits_rate")
    private BigDecimal profitsRate;  //利润率

    @Column(name = "created_by_uid")
    private String createdByUid;

    @CreatedDate
    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    @Column(name = "report_config", columnDefinition = "VARCHAR(1024)")
    private String reportConfig;

    @Column(name = "positive_rate")
    private BigDecimal positiveRate;

    @Column(name = "display_name")
    private String displayName;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_uid",referencedColumnName = "uid", insertable = false,updatable = false)
    private AssetInfo assetInfo;


    @Transient
    private String makerShortName;
    @Transient
    private String clinicalDeptName;//所属科室

    @Transient
    private Integer assetGroup;//设备分类

    @Transient
    private String assetGroupName;//设备分类名称
    @Transient
    private Integer assetId;//设备分类名称

    @Transient
    private String financingNum;

    @Transient
    private Date purchaseDate;

    public String getFinancingNum() {
        return financingNum;
    }

    public void setFinancingNum(String financingNum) {
        this.financingNum = financingNum;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

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


    public BigDecimal getUsageRate() {
        return usageRate;
    }

    public void setUsageRate(BigDecimal usageRate) {
        this.usageRate = usageRate;
    }

    public BigDecimal getCheckNumberPeople() {
        return checkNumberPeople;
    }

    public void setCheckNumberPeople(BigDecimal checkNumberPeople) {
        this.checkNumberPeople = checkNumberPeople;
    }

    public BigDecimal getCheckTimeLength() {
        return checkTimeLength;
    }

    public void setCheckTimeLength(BigDecimal checkTimeLength) {
        this.checkTimeLength = checkTimeLength;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getProfits() {
        return profits;
    }

    public void setProfits(BigDecimal profits) {
        this.profits = profits;
    }

    public BigDecimal getProfitsRate() {
        return profitsRate;
    }

    public void setProfitsRate(BigDecimal profitsRate) {
        this.profitsRate = profitsRate;
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

    public String getReportConfig() {
        return reportConfig;
    }

    public void setReportConfig(String reportConfig) {
        this.reportConfig = reportConfig;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public BigDecimal getPositiveRate() {
        return positiveRate;
    }

    public void setPositiveRate(BigDecimal positiveRate) {
        this.positiveRate = positiveRate;
    }

    public String getMakerShortName() {
        return makerShortName;
    }

    public void setMakerShortName(String makerShortName) {
        this.makerShortName = makerShortName;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
