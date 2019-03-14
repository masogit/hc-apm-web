package com.ge.apm.domain;

import java.util.Date;

public class OpsSrReport {
    private String id;
    private String assetName;
    private String srNumber;
    private Date updateTime;
    private String systemId;
    private String modality;
    private String srType;
    private String srOpenDate;
    private String srScheduleDate;
    private String feSso;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSrOpenDate() {
        return srOpenDate;
    }

    public void setSrOpenDate(String srOpenDate) {
        this.srOpenDate = srOpenDate;
    }

    public String getSrScheduleDate() {
        return srScheduleDate;
    }

    public void setSrScheduleDate(String srScheduleDate) {
        this.srScheduleDate = srScheduleDate;
    }

    public String getFeSso() {
        return feSso;
    }

    public void setFeSso(String feSso) {
        this.feSso = feSso;
    }
}
