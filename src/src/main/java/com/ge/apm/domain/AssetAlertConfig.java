package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 212688574
 */
@Entity
@Table(name = "asset_alert_config")
public class AssetAlertConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "create_by")
    private String createByUid;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "asset_uid")
    private String assetUid;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "asset_uid", referencedColumnName = "uid", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
    private AssetInfo assetInfo;

    @Column(name = "device_type")
    private Integer deviceType; //1. MR, 2. CT, 3. IGS, 4. US, 5. PETCT, 6. MR, 11. EE

    @Column(name = "config_type")
    private Integer configType;

    @Column(name = "lower_limit")
    private Double lowerLimit;

    @Column(name = "upper_limit")
    private Double upperLimit;

    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    @Column(name = "valid_values")
    private String validValues;

    @Column(name = "check_type")
    private Integer checkType; //1: limits, 2: valid values

    @Column(name = "label")
    private String label;

    @Column(name = "pad_message", columnDefinition = "TEXT")
    private String padMessage;

    @Column(name = "wechat_message", columnDefinition = "TEXT")
    private String wechatMessage;

    @Column(name = "mute_period")
    private Integer mutePeriod;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "is_notify_sales")
    private Boolean isNotifySales;

    @Column(name = "is_notify_fe")
    private Boolean isNotifyFe;

    @Column(name = "is_notify_ole")
    private Boolean isNotifyOle;

    @Column(name = "is_notify_customer")
    private Boolean isNotifyCustomer;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateByUid() {
        return createByUid;
    }

    public void setCreateByUid(String createByUid) {
        this.createByUid = createByUid;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getConfigType() {
        return configType;
    }

    public void setConfigType(Integer configType) {
        this.configType = configType;
    }

    public Double getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public Double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getValidValues() {
        return validValues;
    }

    public void setValidValues(String validValues) {
        this.validValues = validValues;
    }

    public Integer getCheckType() {
        return checkType;
    }

    public void setCheckType(Integer checkType) {
        this.checkType = checkType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPadMessage() {
        return padMessage;
    }

    public void setPadMessage(String padMessage) {
        this.padMessage = padMessage;
    }

    public String getWechatMessage() {
        return wechatMessage;
    }

    public void setWechatMessage(String wechatMessage) {
        this.wechatMessage = wechatMessage;
    }

    public Integer getMutePeriod() {
        return mutePeriod;
    }

    public void setMutePeriod(Integer mutePeriod) {
        this.mutePeriod = mutePeriod;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Boolean getNotifySales() {
        return isNotifySales;
    }

    public void setNotifySales(Boolean notifySales) {
        isNotifySales = notifySales;
    }

    public Boolean getNotifyFe() {
        return isNotifyFe;
    }

    public void setNotifyFe(Boolean notifyFe) {
        isNotifyFe = notifyFe;
    }

    public Boolean getNotifyOle() {
        return isNotifyOle;
    }

    public void setNotifyOle(Boolean notifyOle) {
        isNotifyOle = notifyOle;
    }

    public Boolean getNotifyCustomer() {
        return isNotifyCustomer;
    }

    public void setNotifyCustomer(Boolean notifyCustomer) {
        isNotifyCustomer = notifyCustomer;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }
}
