package com.ge.apm.domain;

import java.util.Date;

public class OpsSrReport {
    private String srId;
    private String assetName;
    private String srNumber;
    private Date updateTime;
    private String systemId;
    private String modality;
    private String srType;
    private Date srOpenDate;
    private Date srScheduleDate;
    private String feSso;

    public String getSrId() {
        return srId;
    }

    public void setSrId(String srId) {
        this.srId = srId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getSrNumber() {
        return srNumber;
    }

    public void setSrNumber(String srNumber) {
        this.srNumber = srNumber;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getSrType() {
        return srType;
    }

    public void setSrType(String srType) {
        this.srType = srType;
    }

    public Date getSrOpenDate() {
        return srOpenDate;
    }

    public void setSrOpenDate(Date srOpenDate) {
        this.srOpenDate = srOpenDate;
    }

    public Date getSrScheduleDate() {
        return srScheduleDate;
    }

    public void setSrScheduleDate(Date srScheduleDate) {
        this.srScheduleDate = srScheduleDate;
    }

    public String getFeSso() {
        return feSso;
    }

    public void setFeSso(String feSso) {
        this.feSso = feSso;
    }
}
