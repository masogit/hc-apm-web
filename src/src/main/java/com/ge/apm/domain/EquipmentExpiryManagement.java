package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "equipment_expiry_information" ,indexes = {@Index(columnList = "registerNo"), })
public class EquipmentExpiryManagement {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid", columnDefinition = "CHAR(32)")
    private String uid;

    @Column(name = "manufacture")
    private String manufacture;

    @Column(name = "manufacture_name")
    private String manufactureName;

    @Column(name = "function_type")
    private String functionType;

    @Column(name = "register_no")
    private String registerNo;

    @Column(name = "lifecycle")
    private Integer lifecycle;

    @Column(name = "manufact_date")
    private String manufactDate;

    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "version_number")
    private Integer versionNumber;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "version_time")
    private Date versionTime;

    @Column(name = "version_status")
    private Integer versionStatus;

    @Column(name = "keyword")
    private String keyword;

    @Column(name = "keyword1")
    private String keyword1;

    @Column(name = "keyword2")
    private String keyword2;

    @Column(name = "match_factor")
    private Double matchFactor;

    public Double getMatchFactor() {
        return matchFactor;
    }

    public void setMatchFactor(Double matchFactor) {
        this.matchFactor = matchFactor;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getRegisterNo() {
        return registerNo;
    }

    public void setRegisterNo(String registerNo) {
        this.registerNo = registerNo;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getManufactDate() {
        return manufactDate;
    }

    public void setManufactDate(String manufactDate) {
        this.manufactDate = manufactDate;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Date getVersionTime() {
        return versionTime;
    }

    public void setVersionTime(Date versionTime) {
        this.versionTime = versionTime;
    }

    public Integer getVersionStatus() {
        return versionStatus;
    }

    public void setVersionStatus(Integer versionStatus) {
        this.versionStatus = versionStatus;
    }

    public String getManufactureName() {
        return manufactureName;
    }

    public void setManufactureName(String manufactureName) {
        this.manufactureName = manufactureName;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword1() {
        return keyword1;
    }

    public void setKeyword1(String keyword1) {
        this.keyword1 = keyword1;
    }

    public String getKeyword2() {
        return keyword2;
    }

    public void setKeyword2(String keyword2) {
        this.keyword2 = keyword2;
    }
}
