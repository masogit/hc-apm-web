package com.ge.apm.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2021/6/28
 * @description :
 */
public class SvcEngineLogisticsLineVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String lineID;

    private String order;

    private String systemId;

    private String feName;

    private String feSSO;

    private String item;

    private Integer qty;

    private String hospital;

    private String carrier;

    private String carrierChinese;

    private String description;

    private String descriptionChinese;

    private String job;

    private List<SvcEngineLogistics> svcEngineLogisticsList = new ArrayList<>();

    private List<SvcEngineLogisticsOrderDetailVO> svcEngineLogisticsOrderDetails = new ArrayList<>();

    private String localStatusCode;

    private String localStatusName;

    private String messageTips;

    public List<SvcEngineLogisticsOrderDetailVO> getSvcEngineLogisticsOrderDetails() {
        return svcEngineLogisticsOrderDetails;
    }

    public void setSvcEngineLogisticsOrderDetails(List<SvcEngineLogisticsOrderDetailVO> svcEngineLogisticsOrderDetails) {
        this.svcEngineLogisticsOrderDetails = svcEngineLogisticsOrderDetails;
    }

    public String getMessageTips() {
        return messageTips;
    }

    public void setMessageTips(String messageTips) {
        this.messageTips = messageTips;
    }

    public String getLocalStatusCode() {
        return localStatusCode;
    }

    public void setLocalStatusCode(String localStatusCode) {
        this.localStatusCode = localStatusCode;
    }

    public String getLocalStatusName() {
        return localStatusName;
    }

    public void setLocalStatusName(String localStatusName) {
        this.localStatusName = localStatusName;
    }

    public String getLineID() {
        return lineID;
    }

    public void setLineID(String lineID) {
        this.lineID = lineID;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getFeName() {
        return feName;
    }

    public void setFeName(String feName) {
        this.feName = feName;
    }

    public String getFeSSO() {
        return feSSO;
    }

    public void setFeSSO(String feSSO) {
        this.feSSO = feSSO;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getCarrierChinese() {
        return carrierChinese;
    }

    public void setCarrierChinese(String carrierChinese) {
        this.carrierChinese = carrierChinese;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionChinese() {
        return descriptionChinese;
    }

    public void setDescriptionChinese(String descriptionChinese) {
        this.descriptionChinese = descriptionChinese;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public List<SvcEngineLogistics> getSvcEngineLogisticsList() {
        return svcEngineLogisticsList;
    }

    public void setSvcEngineLogisticsList(List<SvcEngineLogistics> svcEngineLogisticsList) {
        this.svcEngineLogisticsList = svcEngineLogisticsList;
    }
}
