/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "pm_schedule_data")
public class PmScheduleData {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    //Asset Number
    @Column(name = "asset_num")
    private String assetNumber;

    //AssetUID
    @Column(name = "asset_uid")
    private String assetUid;

    //System Id
    @Column(name = "system_id")
    String systemId;

    //Serial #
    @Column(name = "serial")
    String serial;

    //SR #
    @Column(name = "sr_num")
    String srNum;

    //Schedule Date
    @Column(name = "schedule_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    Date scheduleDate;

    //SR Type
    @Column(name = "sr_type")
    String srType;

    //Description
    @Column(name = "description")
    String description;

    //Service Region
    @Column(name = "service_region")
    String serviceRegion;

    //Alternate Customer Name
    @Column(name = "alternate_customer_name")
    String alternateCustomerName;

    //Status
    @Column(name = "status")
    String status;

    //Owner
    @Column(name = "owner")
    String owner;

    //Owner Last Name
    @Column(name = "owner_last_name")
    String ownerLastName;

    //Product Desc
    @Column(name = "product_desc")
    String productDesc;

    //Modality
    @Column(name = "modality")
    String modality;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetNumber() {
        return assetNumber;
    }

    public void setAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSrNum() {
        return srNum;
    }

    public void setSrNum(String srNum) {
        this.srNum = srNum;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getSrType() {
        return srType;
    }

    public void setSrType(String srType) {
        this.srType = srType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceRegion() {
        return serviceRegion;
    }

    public void setServiceRegion(String serviceRegion) {
        this.serviceRegion = serviceRegion;
    }

    public String getAlternateCustomerName() {
        return alternateCustomerName;
    }

    public void setAlternateCustomerName(String alternateCustomerName) {
        this.alternateCustomerName = alternateCustomerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("[id:").append(id).append(",assetNumber:").append(assetNumber)
                .append(",systemId:").append(systemId).append(",scheduleDate").append(scheduleDate).append("]").toString();
    }
}
