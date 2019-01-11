package com.ge.apm.domain.pure;

import java.util.Date;

/**
 * Created by 212605082 on 2018/11/20.
 */
public class WorkOrderExportPojo {

    //woId
    private String Id;
    //报修单号
    private String srNum;
    //设备编号
    private String assetDepartNum;
    //工单状态
    private Integer status;
    //工单状态名称
    private String statusName;
    //资产编号
    private String assetFinancingNum;
    //设备序列号
    private String assetSerialNum;
    //设备名称
    private String assetName;
    //院区
    private String hospitalName;
    //科室
    private String assetClinicalDeptName;
    //制造商
    private String assetManufacture;
    //品牌
    private String assetMakerShortName;
    //当前责任人
    private String currentPersonName;
    //当前步骤名称
    private String currentStepName;
    //当前步骤
    private Integer currentStepId;
    //报修人
    private String requestorName;
    //报修时间
    private Date requestTime;
    //故障描述
    private String requestReason;
    //派工人
    private String sendStepOwnerName;
    //派工时间
    private Date sendStepStartTime;
    //工单类型
    private Integer intExtType;
    //工单类型名称
    private String intExtTypeName;
    //上门维修/送修
    private Integer repairType;
    //上门维修/送修名称
    private String repairTypeName;
    //接单人
    private String acceptStepOwnerName;
    //接单时间
    private Date acceptStepStartTime;
    //维修完成时间
    private Date workStepEndTime;
    //签到时间
    private Date checkinTime;
    //维修工程师工时
    private Double manHours;
    //维修总工时
    private Double totalManHour;
    //维修备件
    private String parts;
    //备件总费用
    private Double partsTotalPrice;
    //其他费用
    private Double otherExpense;
    //外修费用
    private Double externalExpense;
    //维修总费用
    private Double totalPrice;
    //验收人
    private String evaluateStepOwnerName;
    //验收时间
    private Date evaluateStepEndTime;
    //关单人
    private String closeStepOwnerName;
    //关单时间
    private Date closeStepEndTime;
    //协助者
    private String coworker;
    //协助者工时
    private String coworkManHour;
    //最终评价
    private Integer feedbackRating;
    //故障类型
    private Integer caseType;
    //故障类型名称
    private String caseTypeName;
    //原因描述
    private String patProblems;
    //解决方案
    private String patActions;
    //交付前检测
    private String patTests;
    //备注
    private String comment;
    //二维码
    private String assetInfoQrCode;
    //设备分类
    private String assetGroup;
    //设备分类名称
    private String assetGroupName;
    //医疗器械分类
    private Integer assetFunctionGroup;
    //医疗器械分类名称
    private String assetFunctionGroupName;
    //医疗器械子分类
    private Integer assetCode68;
    //医疗器械子分类名称
    private String assetCode68Name;
    //设备型号
    private String assetFunctionType;
    //距上次报修天数
    private Integer nearestWorkDay;
    //停机时间
    private Date confirmedDownTime;
    //恢复时间
    private Date confirmedUpTime;
    //联系电话
    private String telephone;
    //当前位置
    private String locationName;
    //报修状态
    private Integer assetStatus;
    //报修状态名称
    private String assetStatusName;
    //
    private String signature;
    //
    private String ackSignature;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getSrNum() {
        return srNum;
    }

    public void setSrNum(String srNum) {
        this.srNum = srNum;
    }

    public String getAssetDepartNum() {
        return assetDepartNum;
    }

    public void setAssetDepartNum(String assetDepartNum) {
        this.assetDepartNum = assetDepartNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAssetFinancingNum() {
        return assetFinancingNum;
    }

    public void setAssetFinancingNum(String assetFinancingNum) {
        this.assetFinancingNum = assetFinancingNum;
    }

    public String getAssetSerialNum() {
        return assetSerialNum;
    }

    public void setAssetSerialNum(String assetSerialNum) {
        this.assetSerialNum = assetSerialNum;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getAssetClinicalDeptName() {
        return assetClinicalDeptName;
    }

    public void setAssetClinicalDeptName(String assetClinicalDeptName) {
        this.assetClinicalDeptName = assetClinicalDeptName;
    }

    public String getAssetManufacture() {
        return assetManufacture;
    }

    public void setAssetManufacture(String assetManufacture) {
        this.assetManufacture = assetManufacture;
    }

    public String getAssetMakerShortName() {
        return assetMakerShortName;
    }

    public void setAssetMakerShortName(String assetMakerShortName) {
        this.assetMakerShortName = assetMakerShortName;
    }

    public String getCurrentPersonName() {
        return currentPersonName;
    }

    public void setCurrentPersonName(String currentPersonName) {
        this.currentPersonName = currentPersonName;
    }

    public Integer getCurrentStepId() {
        return currentStepId;
    }

    public void setCurrentStepId(Integer currentStepId) {
        this.currentStepId = currentStepId;
    }

    public String getRequestorName() {
        return requestorName;
    }

    public void setRequestorName(String requestorName) {
        this.requestorName = requestorName;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public String getSendStepOwnerName() {
        return sendStepOwnerName;
    }

    public void setSendStepOwnerName(String sendStepOwnerName) {
        this.sendStepOwnerName = sendStepOwnerName;
    }

    public Date getSendStepStartTime() {
        return sendStepStartTime;
    }

    public void setSendStepStartTime(Date sendStepStartTime) {
        this.sendStepStartTime = sendStepStartTime;
    }

    public Integer getIntExtType() {
        return intExtType;
    }

    public void setIntExtType(Integer intExtType) {
        this.intExtType = intExtType;
    }

    public Integer getRepairType() {
        return repairType;
    }

    public void setRepairType(Integer repairType) {
        this.repairType = repairType;
    }

    public String getAcceptStepOwnerName() {
        return acceptStepOwnerName;
    }

    public void setAcceptStepOwnerName(String acceptStepOwnerName) {
        this.acceptStepOwnerName = acceptStepOwnerName;
    }

    public Date getAcceptStepStartTime() {
        return acceptStepStartTime;
    }

    public void setAcceptStepStartTime(Date acceptStepStartTime) {
        this.acceptStepStartTime = acceptStepStartTime;
    }

    public Date getWorkStepEndTime() {
        return workStepEndTime;
    }

    public void setWorkStepEndTime(Date workStepEndTime) {
        this.workStepEndTime = workStepEndTime;
    }

    public Date getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(Date checkinTime) {
        this.checkinTime = checkinTime;
    }

    public Double getManHours() {
        return manHours;
    }

    public void setManHours(Double manHours) {
        this.manHours = manHours;
    }

    public Double getTotalManHour() {
        return totalManHour;
    }

    public void setTotalManHour(Double totalManHour) {
        this.totalManHour = totalManHour;
    }

    public String getParts() {
        return parts;
    }

    public void setParts(String parts) {
        this.parts = parts;
    }

    public Double getPartsTotalPrice() {
        return partsTotalPrice;
    }

    public void setPartsTotalPrice(Double partsTotalPrice) {
        this.partsTotalPrice = partsTotalPrice;
    }

    public Double getOtherExpense() {
        return otherExpense;
    }

    public void setOtherExpense(Double otherExpense) {
        this.otherExpense = otherExpense;
    }

    public Double getExternalExpense() {
        return externalExpense;
    }

    public void setExternalExpense(Double externalExpense) {
        this.externalExpense = externalExpense;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getEvaluateStepOwnerName() {
        return evaluateStepOwnerName;
    }

    public void setEvaluateStepOwnerName(String evaluateStepOwnerName) {
        this.evaluateStepOwnerName = evaluateStepOwnerName;
    }

    public Date getEvaluateStepEndTime() {
        return evaluateStepEndTime;
    }

    public void setEvaluateStepEndTime(Date evaluateStepEndTime) {
        this.evaluateStepEndTime = evaluateStepEndTime;
    }

    public String getCloseStepOwnerName() {
        return closeStepOwnerName;
    }

    public void setCloseStepOwnerName(String closeStepOwnerName) {
        this.closeStepOwnerName = closeStepOwnerName;
    }

    public Date getCloseStepEndTime() {
        return closeStepEndTime;
    }

    public void setCloseStepEndTime(Date closeStepEndTime) {
        this.closeStepEndTime = closeStepEndTime;
    }

    public String getCoworker() {
        return coworker;
    }

    public void setCoworker(String coworker) {
        this.coworker = coworker;
    }

    public String getCoworkManHour() {
        return coworkManHour;
    }

    public void setCoworkManHour(String coworkManHour) {
        this.coworkManHour = coworkManHour;
    }

    public Integer getFeedbackRating() {
        return feedbackRating;
    }

    public void setFeedbackRating(Integer feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public Integer getCaseType() {
        return caseType;
    }

    public void setCaseType(Integer caseType) {
        this.caseType = caseType;
    }

    public String getPatProblems() {
        return patProblems;
    }

    public void setPatProblems(String patProblems) {
        this.patProblems = patProblems;
    }

    public String getPatActions() {
        return patActions;
    }

    public void setPatActions(String patActions) {
        this.patActions = patActions;
    }

    public String getPatTests() {
        return patTests;
    }

    public void setPatTests(String patTests) {
        this.patTests = patTests;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAssetInfoQrCode() {
        return assetInfoQrCode;
    }

    public void setAssetInfoQrCode(String assetInfoQrCode) {
        this.assetInfoQrCode = assetInfoQrCode;
    }

    public String getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(String assetGroup) {
        this.assetGroup = assetGroup;
    }

    public Integer getAssetFunctionGroup() {
        return assetFunctionGroup;
    }

    public void setAssetFunctionGroup(Integer assetFunctionGroup) {
        this.assetFunctionGroup = assetFunctionGroup;
    }

    public Integer getAssetCode68() {
        return assetCode68;
    }

    public void setAssetCode68(Integer assetCode68) {
        this.assetCode68 = assetCode68;
    }

    public String getAssetFunctionType() {
        return assetFunctionType;
    }

    public void setAssetFunctionType(String assetFunctionType) {
        this.assetFunctionType = assetFunctionType;
    }

    public Integer getNearestWorkDay() {
        return nearestWorkDay;
    }

    public void setNearestWorkDay(Integer nearestWorkDay) {
        this.nearestWorkDay = nearestWorkDay;
    }

    public Date getConfirmedDownTime() {
        return confirmedDownTime;
    }

    public void setConfirmedDownTime(Date confirmedDownTime) {
        this.confirmedDownTime = confirmedDownTime;
    }

    public Date getConfirmedUpTime() {
        return confirmedUpTime;
    }

    public void setConfirmedUpTime(Date confirmedUpTime) {
        this.confirmedUpTime = confirmedUpTime;
    }

    public String getCurrentStepName() {
        return currentStepName;
    }

    public void setCurrentStepName(String currentStepName) {
        this.currentStepName = currentStepName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    public String getIntExtTypeName() {
        return intExtTypeName;
    }

    public void setIntExtTypeName(String intExtTypeName) {
        this.intExtTypeName = intExtTypeName;
    }

    public String getRepairTypeName() {
        return repairTypeName;
    }

    public void setRepairTypeName(String repairTypeName) {
        this.repairTypeName = repairTypeName;
    }

    public String getCaseTypeName() {
        return caseTypeName;
    }

    public void setCaseTypeName(String caseTypeName) {
        this.caseTypeName = caseTypeName;
    }

    public String getAssetFunctionGroupName() {
        return assetFunctionGroupName;
    }

    public void setAssetFunctionGroupName(String assetFunctionGroupName) {
        this.assetFunctionGroupName = assetFunctionGroupName;
    }

    public String getAssetCode68Name() {
        return assetCode68Name;
    }

    public void setAssetCode68Name(String assetCode68Name) {
        this.assetCode68Name = assetCode68Name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Integer getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(Integer assetStatus) {
        this.assetStatus = assetStatus;
    }

    public String getAssetStatusName() {
        return assetStatusName;
    }

    public void setAssetStatusName(String assetStatusName) {
        this.assetStatusName = assetStatusName;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAckSignature() {
        return ackSignature;
    }

    public void setAckSignature(String ackSignature) {
        this.ackSignature = ackSignature;
    }
}
