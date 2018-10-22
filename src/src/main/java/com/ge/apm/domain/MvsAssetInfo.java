package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author 212688574
 */
@Entity
@Table(name = "mvs_asset_info")
public class MvsAssetInfo {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Size(max = 64)
    @Column(name = "name")
    private String name;//设备名称

    @Size(max = 64)
    @Column(name = "alias_name")
    private String aliasName;//设备别名

//    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
//    private String siteUID;//所属院区uuid

    @Size(max = 64)
    @Column(name = "site_name")
    private String siteName;//所属院区

//    @Column(name = "asset_dept_uid", columnDefinition = "CHAR(32)") // 使用科室UID
//    private String assetDeptUID;//使用科室uuid

    @Size(max = 64)
    @Column(name = "asset_dept_name")
    private String assetDeptName;//使用科室

    @Size(max = 64)
    @Column(name = "manufacture")
    private String manufacture;//制造商

    @Size(max = 64)
    @Column(name = "make_short_name")
    private String makerShortName;//品牌/制造商简称

    @Column(name = "asset_group")
    private Integer assetGroup;//设备分类

    @Column(name = "repair_form")
    private Integer repairForm; //1和null是纯内修。 2，纯外修。3，先内修再外修

    @Column(name = "function_type")
    private String functionType;//设备型号

    @Size(max = 64)
    @Column(name = "serial_num")
    private String serialNum;//制造商序列号

    @Column(name = "system_id")
    private String systemId;//SystemID/厂商报修ID

    @Size(max = 64)
    @Column(name = "financing_num")
    private String financingNum;//资产编号

    @Column(name = "factory_warranty_date")
    @Temporal(TemporalType.DATE)
    private Date factoryWarrantyDate;//质保日期

    @Column(name = "warranty_date")
    @Temporal(TemporalType.DATE)
    private Date warrantyDate;//维保截止日期

    @Size(max = 256)
    @Column(name = "qr_code")
    private String qrCode;//二维码

    @Column(name = "comments")
    @Size(min = 0, max = 256)
    private String comments;//备注

    @Column(name = "device_type")
    private String deviceType; //GE 设备大类分类， DI/ULS/Other

    @Column(name = "mvs_type")
    private String mvsType; //MVS 22 类分类

    @Column(name = "asset_uid")
    private String assetUid;//资产UUID

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_uid", referencedColumnName = "uid", insertable = false, updatable = false, unique = true)
    @JsonBackReference
    private AssetInfo assetInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getMvsType() {
        return mvsType;
    }

    public void setMvsType(String mvsType) {
        this.mvsType = mvsType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

//    public String getSiteUID() {
//        return siteUID;
//    }
//
//    public void setSiteUID(String siteUID) {
//        this.siteUID = siteUID;
//    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

//    public String getAssetDeptUID() {
//        return assetDeptUID;
//    }
//
//    public void setAssetDeptUID(String assetDeptUID) {
//        this.assetDeptUID = assetDeptUID;
//    }

    public String getAssetDeptName() {
        return assetDeptName;
    }

    public void setAssetDeptName(String assetDeptName) {
        this.assetDeptName = assetDeptName;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getMakerShortName() {
        return makerShortName;
    }

    public void setMakerShortName(String makerShortName) {
        this.makerShortName = makerShortName;
    }

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public Integer getRepairForm() {
        return repairForm;
    }

    public void setRepairForm(Integer repairForm) {
        this.repairForm = repairForm;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getFinancingNum() {
        return financingNum;
    }

    public void setFinancingNum(String financingNum) {
        this.financingNum = financingNum;
    }

    public Date getFactoryWarrantyDate() {
        return factoryWarrantyDate;
    }

    public void setFactoryWarrantyDate(Date factoryWarrantyDate) {
        this.factoryWarrantyDate = factoryWarrantyDate;
    }

    public Date getWarrantyDate() {
        return warrantyDate;
    }

    public void setWarrantyDate(Date warrantyDate) {
        this.warrantyDate = warrantyDate;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
