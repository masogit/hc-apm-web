package com.ge.apm.domain.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ge.apm.domain.UserAccount;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 212605082 on 2017/11/10.
 */

public class UserScheduleDto implements Serializable {
    public UserScheduleDto() {
    }

    public UserScheduleDto(String id, String userUID, Date scheduleDate, String type, String scheduleDateStage, String auditStatus, Integer manHours, String tenantUID, String institutionUID, String hospitalUID, String siteUID, String createdBy, Date createdDate, String lastModifiedBy, Date lastModifiedDate) {
        this.id = id;
        this.userUID = userUID;
        this.scheduleDate = scheduleDate;
        this.type = type;
        this.scheduleDateStage = scheduleDateStage;
        this.auditStatus = auditStatus;
        this.manHours = manHours;
        this.tenantUID = tenantUID;
        this.institutionUID = institutionUID;
        this.hospitalUID = hospitalUID;
        this.siteUID = siteUID;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
    }

    private String id;

    private String userUID;

    private Date scheduleDate;

    private String type;

    private String scheduleDateStage;

    private String auditStatus;

    private Integer manHours;

    private String tenantUID;

    private String institutionUID;

    private String hospitalUID;

    private String siteUID;

    private String createdBy;


    private Date createdDate;


    private String lastModifiedBy;


    private Date lastModifiedDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScheduleDateStage() {
        return scheduleDateStage;
    }

    public void setScheduleDateStage(String scheduleDateStage) {
        this.scheduleDateStage = scheduleDateStage;
    }


    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
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

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Integer getManHours() {
        return manHours;
    }

    public void setManHours(Integer manHours) {
        this.manHours = manHours;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
