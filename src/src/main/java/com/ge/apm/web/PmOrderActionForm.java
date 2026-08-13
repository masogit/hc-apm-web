package com.ge.apm.web;

import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.V2_PmOrderManHoursDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsg on 11/12/2017.
 */
public class PmOrderActionForm {

    String assetUid;
    String actionType;
    String assignee;
    List<V2_BlobObject> attachments;
    String endTime;
    Double manHours;
    String startTime;
    String stepId;
    String qualityChecking;
    Object details;
    Object coworkers;
    String planTime;
    Integer ruleLevel;
    String desc;
    String comments;
    String pmOrderId;
    Integer pmType;
    String applyTime;
    String pmRuleUid;

    @Getter@Setter
    String remarks;

    Integer entryType;

    //附件
    private List<V2_BlobObject> attachmentsTwo = new ArrayList<>();
    //保养前照片
    private List<V2_BlobObject> prePmAttachments = new ArrayList<>();
    // 巡检照片
    private List<V2_BlobObject> inspectionPhotos = new ArrayList<>();
    // 其他照片
    private List<V2_BlobObject> otherPhotos = new ArrayList<>();

    private List<V2_PmOrderManHoursDetail> pmOrderManHoursDetails;

    /**
     * 创建方式 1:医院创建  2:供应商创建
     */
    private Integer createType = 1;
//    String currentPersonId;//验收人

    /**县域需求：顾客姓名 来源于设备所属院区的ContactPerson字段*/
    private String customerName;
    /**县域需求：顾客电话 来源于设备所属院区的ContactPhone字段*/
    private String customerTel;
    /**县域需求：院区名称 来源于设备所属院区的name字段*/
    private String siteName;

    private String signature;

    Integer coworkerEntryType;
    Double travelTime;
    public Integer getCoworkerEntryType() {
        return coworkerEntryType;
    }

    public void setCoworkerEntryType(Integer coworkerEntryType) {
        this.coworkerEntryType = coworkerEntryType;
    }

    public List<V2_PmOrderManHoursDetail> getPmOrderManHoursDetails() {
        return pmOrderManHoursDetails;
    }

    public void setPmOrderManHoursDetails(List<V2_PmOrderManHoursDetail> pmOrderManHoursDetails) {
        this.pmOrderManHoursDetails = pmOrderManHoursDetails;
    }

    //V2_WorkOrder_Coworker[] coworkes //optional
    public String getPmOrderId() {
        return pmOrderId;
    }

    public void setPmOrderId(String pmOrderId) {
        this.pmOrderId = pmOrderId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getQualityChecking() {
        return qualityChecking;
    }

    public void setQualityChecking(String qualityChecking) {
        this.qualityChecking = qualityChecking;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Double getManHours() {
        return manHours;
    }

    public void setManHours(Double manHours) {
        this.manHours = manHours;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<V2_BlobObject> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<V2_BlobObject> attachments) {
        this.attachments = attachments;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    public Object getCoworkers() {
        return coworkers;
    }

    public void setCoworkers(Object coworkers) {
        this.coworkers = coworkers;
    }

    public String getPlanTime() {
        return planTime;
    }

    public void setPlanTime(String planTime) {
        this.planTime = planTime;
    }

    public Integer getRuleLevel() {
        return ruleLevel;
    }

    public void setRuleLevel(Integer ruleLevel) {
        this.ruleLevel = ruleLevel;
    }

    public Integer getPmType() {
        return pmType;
    }

    public void setPmType(Integer pmType) {
        this.pmType = pmType;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public Integer getCreateType() {
        return createType;
    }

    public void setCreateType(Integer createType) {
        this.createType = createType;
    }

    //    public String getCurrentPersonId() {
//        return currentPersonId;
//    }
//
//    public void setCurrentPersonId(String currentPersonId) {
//        this.currentPersonId = currentPersonId;
//    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerTel() {
        return customerTel;
    }

    public void setCustomerTel(String customerTel) {
        this.customerTel = customerTel;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPmRuleUid() {
        return pmRuleUid;
    }

    public void setPmRuleUid(String pmRuleUid) {
        this.pmRuleUid = pmRuleUid;
    }

    public List<V2_BlobObject> getAttachmentsTwo() {
        return attachmentsTwo;
    }

    public void setAttachmentsTwo(List<V2_BlobObject> attachmentsTwo) {
        this.attachmentsTwo = attachmentsTwo;
    }

    public List<V2_BlobObject> getPrePmAttachments() {
        return prePmAttachments;
    }

    public void setPrePmAttachments(List<V2_BlobObject> prePmAttachments) {
        this.prePmAttachments = prePmAttachments;
    }

    public List<V2_BlobObject> getOtherPhotos() {
        return otherPhotos;
    }

    public void setOtherPhotos(List<V2_BlobObject> otherPhotos) {
        this.otherPhotos = otherPhotos;
    }

    public List<V2_BlobObject> getInspectionPhotos() {
        return inspectionPhotos;
    }

    public void setInspectionPhotos(List<V2_BlobObject> inspectionPhotos) {
        this.inspectionPhotos = inspectionPhotos;
    }

    public Integer getEntryType() {
        return entryType;
    }

    public void setEntryType(Integer entryType) {
        this.entryType = entryType;
    }

    public Double getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Double travelTime) {
        this.travelTime = travelTime;
    }
}
