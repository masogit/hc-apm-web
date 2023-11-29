package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "rel_rule_asset")
public class RelRuleAsset {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "rule_uid")
    private String ruleUID;

    @Column(name = "rule_uid_inspection")
    private String ruleUIDInspection;   //巡检

    @Column(name = "rule_uid2")
    private String ruleUID2;   //二级保养规则

    @Column(name = "rule_uid1")  //一级保养规则
    private String ruleUID1;

    @Column(name = "rule_uid4")  //质量控制
    private String ruleUID4;

    @Column(name = "asset_uid")
    private String assetUID;

    @JoinColumn(insertable = false, updatable = false, name = "asset_uid", referencedColumnName = "uid")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private AssetInfo assetInfo;

    @Column(name = "created_date")
    private Date createdDate = new Date();//生成时间

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;//修改时间

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Transient
    private String assetName;//资产名称

    @Transient
    private String ruleName;//保养规则

    @Transient
    private String departNum;//设备编号

    @Transient
    private Integer assetGroup;//设备分类

    @Transient
    private String clinicalDeptName;//所属科室

    @Transient
    private String nationalStandardCode;//2010国家编码

    @Transient
    private String makerShortName;//品牌/制造商简称

    @Transient
    private String manufacture;//制造商

    @Transient
    private String qrCode;//二维码


    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDepartNum() {
        return departNum;
    }

    public void setDepartNum(String departNum) {
        this.departNum = departNum;
    }

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }

    public String getNationalStandardCode() {
        return nationalStandardCode;
    }

    public void setNationalStandardCode(String nationalStandardCode) {
        this.nationalStandardCode = nationalStandardCode;
    }

    public String getMakerShortName() {
        return makerShortName;
    }

    public void setMakerShortName(String makerShortName) {
        this.makerShortName = makerShortName;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getRuleUIDInspection() {
        return ruleUIDInspection;
    }

    public void setRuleUIDInspection(String ruleUIDInspection) {
        this.ruleUIDInspection = ruleUIDInspection;
    }

    public String getRuleUID4() {
        return ruleUID4;
    }

    public void setRuleUID4(String ruleUID4) {
        this.ruleUID4 = ruleUID4;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public String getRuleUID2() {
        return ruleUID2;
    }

    public void setRuleUID2(String ruleUID2) {
        this.ruleUID2 = ruleUID2;
    }

    public String getRuleUID1() {
        return ruleUID1;
    }

    public void setRuleUID1(String ruleUID1) {
        this.ruleUID1 = ruleUID1;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getRuleUID() {
        return ruleUID;
    }

    public void setRuleUID(String ruleUID) {
        this.ruleUID = ruleUID;
    }

    public String getAssetUID() {
        return assetUID;
    }

    public void setAssetUID(String assetUID) {
        this.assetUID = assetUID;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstitutionUID() {
        return institutionUID;
    }

    public void setInstitutionUID(String institutionUID) {
        this.institutionUID = institutionUID;
    }

    public String getHospitalUID() {
        return hospitalUID;
    }

    public void setHospitalUID(String hospitalUID) {
        this.hospitalUID = hospitalUID;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RelRuleAsset)) {
            return false;
        }
        RelRuleAsset other = (RelRuleAsset) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
