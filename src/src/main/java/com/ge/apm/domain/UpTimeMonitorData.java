/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "uptime_monitor_data")
public class UpTimeMonitorData implements Serializable{

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;
    
    @Column(name = "asset_uid")
    private String assetUid;
    
    //Requestor's Email/SSO ID (*)
    @Column(name = "requestor_sso")
    String requestorSSO;
    
    //Customer Number
    @Column(name = "customer_number")
    String customerNumber;
    
    //Chinese Name
    @Column(name = "chinese_name")
    String chineseName;
    
    //System ID
    @Column(name = "system_Id")
    String systemId;
    
    //Product Description
    @Column(name = "product_Desc")
    String productDescription;
    
    //Up-time1
    @Column(name = "uptime1")
    Double uptime1;
    //Up-time2
    @Column(name = "uptime2")
    Double uptime2;
    //Up-time3
    @Column(name = "uptime3")
    Double uptime3;
    
    //period
    @Column(name = "period")
    String period;

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

    public String getRequestorSSO() {
        return requestorSSO;
    }

    public void setRequestorSSO(String requestorSSO) {
        this.requestorSSO = requestorSSO;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Double getUptime1() {
        return uptime1;
    }

    public void setUptime1(Double uptime1) {
        this.uptime1 = uptime1;
    }

    public Double getUptime2() {
        return uptime2;
    }

    public void setUptime2(Double uptime2) {
        this.uptime2 = uptime2;
    }

    public Double getUptime3() {
        return uptime3;
    }

    public void setUptime3(Double uptime3) {
        this.uptime3 = uptime3;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
    
    @Override
    public String toString() {
        return new StringBuilder().append("[id:").append(id).append(",assetUid:").append(assetUid)
                .append(",systemID:").append(systemId).append("]").toString();
    }
    
}
