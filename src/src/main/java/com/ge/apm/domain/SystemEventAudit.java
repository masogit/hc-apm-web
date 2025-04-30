package com.ge.apm.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "system_event_audit")
public class SystemEventAudit {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;


    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "created_date")
    private Date createdDate;


    @Column(name = "bo_type")
    private String boType;

    @Column(name = "bo_uid")
    private String boUid;

    @Column(name = "system_event_key")
    private String systemEventKey;

    @Column(name="event_data",columnDefinition = "text")
    private String eventData;



    @Column(name = "operator_uid")
    private String operatorUid;



    @Column(name = "result")
    private Boolean result;

    @Column(name = "error_log",columnDefinition = "text")
    private String errorLog;

    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUid;

    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUid;

    @Column(name="site_uid",columnDefinition = "CHAR(32)")
    private String siteUid;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getBoType() {
        return boType;
    }

    public void setBoType(String boType) {
        this.boType = boType;
    }



    public String getOperatorUid() {
        return operatorUid;
    }

    public void setOperatorUid(String operatorUid) {
        this.operatorUid = operatorUid;
    }



    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public String getInstitutionUid() {
        return institutionUid;
    }

    public void setInstitutionUid(String institutionUid) {
        this.institutionUid = institutionUid;
    }

    public String getHospitalUid() {
        return hospitalUid;
    }

    public void setHospitalUid(String hospitalUid) {
        this.hospitalUid = hospitalUid;
    }

    public String getSiteUid() {
        return siteUid;
    }

    public void setSiteUid(String siteUid) {
        this.siteUid = siteUid;
    }


    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }

    public String getBoUid() {
        return boUid;
    }

    public void setBoUid(String boUid) {
        this.boUid = boUid;
    }

    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public String getSystemEventKey() {
        return systemEventKey;
    }

    public void setSystemEventKey(String systemEventKey) {
        this.systemEventKey = systemEventKey;
    }
}
