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
@Table(name = "cold_head_monitor_data")
public class ColdHeaderMonitorData {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    //"Cryogen System Id" 
    @Column(name = "cryogen_system_id")
    String cryogenSystemId;
    
    //AssetUID
    @Column(name = "asset_uid")
    private String assetUid;

    //"Coldhead RuO ! 20k Shield Service"
     @Column(name = "cold_head_ruo")
    Double coldheadRuo;

    //"Coldhead RuO ! 20k Shield Service - Read Time"
    @Column(name = "cold_head_ruo_Time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date coldheadRuoTime;

    //"HePressure"
    @Column(name = "he_pressure")
    Double hePressure;
    //"HePressure - Read Time"
    @Column(name = "he_pressure_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date hePressureTime;

    //"HeLevel"
    @Column(name = "he_level")
    Double heLevel;
    //"HeLevel - Read Time"
    @Column(name = "he_level_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date heLevelTime;

    //"Compressor Status"
    @Column(name = "compressor_status")
    Integer compressorStatus;
    //"Compressor Status - Read Time"
    @Column(name = "compressor_status_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date compressorStatusTime;

    //"Water Flow"
    @Column(name = "water_flow")
    Double waterFlow;
    //"Water Flow - Read Time"
    @Column(name = "water_flow_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date waterFlowTime;

    //"Water Temp"
    @Column(name = "water_tempe")
    Double waterTemp;
    //"Water Temp - Read Time"
    @Column(name = "water_temp_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date waterTempTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCryogenSystemId() {
        return cryogenSystemId;
    }

    public void setCryogenSystemId(String cryogenSystemId) {
        this.cryogenSystemId = cryogenSystemId;
    }

    public Double getColdheadRuo() {
        return coldheadRuo;
    }

    public void setColdheadRuo(Double coldheadRuo) {
        this.coldheadRuo = coldheadRuo;
    }

    public Date getColdheadRuoTime() {
        return coldheadRuoTime;
    }

    public void setColdheadRuoTime(Date coldheadRuoTime) {
        this.coldheadRuoTime = coldheadRuoTime;
    }

    public Double getHePressure() {
        return hePressure;
    }

    public void setHePressure(Double hePressure) {
        this.hePressure = hePressure;
    }

    public Date getHePressureTime() {
        return hePressureTime;
    }

    public void setHePressureTime(Date hePressureTime) {
        this.hePressureTime = hePressureTime;
    }

    public Double getHeLevel() {
        return heLevel;
    }

    public void setHeLevel(Double heLevel) {
        this.heLevel = heLevel;
    }

    public Date getHeLevelTime() {
        return heLevelTime;
    }

    public void setHeLevelTime(Date heLevelTime) {
        this.heLevelTime = heLevelTime;
    }

    public Integer getCompressorStatus() {
        return compressorStatus;
    }

    public void setCompressorStatus(Integer compressorStatus) {
        this.compressorStatus = compressorStatus;
    }

    public Date getCompressorStatusTime() {
        return compressorStatusTime;
    }

    public void setCompressorStatusTime(Date compressorStatusTime) {
        this.compressorStatusTime = compressorStatusTime;
    }

    public Double getWaterFlow() {
        return waterFlow;
    }

    public void setWaterFlow(Double waterFlow) {
        this.waterFlow = waterFlow;
    }

    public Date getWaterFlowTime() {
        return waterFlowTime;
    }

    public void setWaterFlowTime(Date waterFlowTime) {
        this.waterFlowTime = waterFlowTime;
    }

    public Double getWaterTemp() {
        return waterTemp;
    }

    public void setWaterTemp(Double waterTemp) {
        this.waterTemp = waterTemp;
    }

    public Date getWaterTempTime() {
        return waterTempTime;
    }

    public void setWaterTempTime(Date waterTempTime) {
        this.waterTempTime = waterTempTime;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }
    

}
