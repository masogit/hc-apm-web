package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "service_request_report")
public class ServiceRequestReport {

    public static final int REPORT_TYPE_SR = 1;
    public static final int REPORT_TYPE_PM = 2;
    public static final int REPORT_TYPE_OTHER = 3;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "alce_id")
    private String alceId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "storage_id")
    private String storageId;

    @Column(name = "type")
    private int type;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Date createdDate;

    public ServiceRequestReport() {
    }

    public String getDisplayName() {
        switch (type) {
            case REPORT_TYPE_SR:
                return "SR report";
            case REPORT_TYPE_PM:
                return "PM report";
            default:
                return "Other report";
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAlceId() {
        return alceId;
    }

    public void setAlceId(String alceId) {
        this.alceId = alceId;
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
}
