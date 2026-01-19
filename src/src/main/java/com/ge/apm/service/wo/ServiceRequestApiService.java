/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.wo;

import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.*;

import java.util.*;
import java.util.logging.Logger;

import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import webapp.framework.util.StringUtil;

import javax.persistence.Column;

/**
 *
 * @author 212579464
 */
@Component
public class ServiceRequestApiService extends MicroServiceInvoker {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    public String delAccess(String token,String accId){
        HttpEntity<String> requestEntity = new HttpEntity<String>(createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.ACCESSDEL);
        String url = gatewayURL.replace("{id}", accId.toString());
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.DELETE, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (String) responseMap.get("message");
        } else {
            return null;
        }
    }

    public String delAccessMeasuring(String token,String accId){
        HttpEntity<String> requestEntity = new HttpEntity<String>(createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.ACCESSDEL_MEASURING);
        String url = gatewayURL.replace("{id}", accId.toString());
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.DELETE, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (String) responseMap.get("message");
        } else {
            return null;
        }
    }

    public String addAccess(String token,String assetUid,V2_WorkOrder_Detail workOrderDetail){
        HttpEntity<V2_WorkOrder_Detail> requestEntity = new HttpEntity<V2_WorkOrder_Detail>(workOrderDetail, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.ACCESSADD);
        String url = gatewayURL.replace("{assetUid}",assetUid);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            HashMap data = (LinkedHashMap)responseMap.get("data");
            workOrderDetail.setId((String)data.get("id"));
            return (String) responseMap.get("message");
        } else {
            return null;
        }
    }
    public String addAccessMeasuring(String token, String assetUid, MeasuringOrderDetail measuringOrderDetail){
        HttpEntity<MeasuringOrderDetail> requestEntity = new HttpEntity<MeasuringOrderDetail>(measuringOrderDetail, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.ACCESSAD_MEASURING);
        String url = gatewayURL.replace("{assetUid}",assetUid);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            LinkedHashMap<String, Object>  data = (LinkedHashMap<String, Object> )responseMap.get("data");
            measuringOrderDetail.setId((String)data.get("id"));
            return (String) responseMap.get("message");
        } else {
            return null;
        }
    }

    public Map<String, Object> getReponserByAsset(String token, Integer assetId,String orgUid) {
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);

        String gatewayURL = getGatewayURL(GatewayURL.GET_RESPONSER_BY_ASSET);
        String url = gatewayURL.replace("{assetId}", assetId.toString());
        if(StringUtils.isNotEmpty(orgUid)){
            url = url + "?orgUid=" +orgUid;
        }
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (LinkedHashMap<String, Object>) responseMap;
        } else {
            return null;
        }
    }

    public Map<String, Object> getAcker(String token, String woOrderId,String orgUid) {
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);

        String gatewayURL = getGatewayURL(GatewayURL.GET_ACKER_BY_OrderId);
        String url = gatewayURL.replace("{workOrderId}", woOrderId);
        if(StringUtils.isNotEmpty(orgUid)){
            url = url + "?orgUid=" +orgUid;
        }
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (LinkedHashMap<String, Object>) responseMap;
        } else {
            return null;
        }
    }




    public ResponseEntity<LinkedHashMap> invokeWorkFlowConfig(String token, Integer assetId) {
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        String gatewayURL = getGatewayURL(GatewayURL.GET_WORKFLOW_CONFIG);
        String url = gatewayURL.replace("{assetId}", assetId.toString());
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if(isOkay(response)){
            return response;
        }
        else{
            return null;
        }

    }

    public String getSrNum(String numType) {
        String gatewayURL = null;
        if(numType.equals("pm")) {
            gatewayURL = getGatewayURL(GatewayURL.SERVICE_REQUEST_CREATE).concat("/getPmNum");
        } else if(numType.equals("work")) {
            gatewayURL = getGatewayURL(GatewayURL.SERVICE_REQUEST_CREATE).concat("/getSrNum");
        } else {
            return null;
        }
        HttpHeaders header = createHttpHeaders(UserContextService.getAccessToken());
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return String.valueOf(responseMap.get("data"));
        } else {
            return null;
        }
    }
    public  Map<String, Object> findSupplierFEByAssetUid( AssetInfo asset) {
        HttpHeaders header = createHttpHeaders(UserContextService.getAccessToken());
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        String gatewayURL = getGatewayURL(GatewayURL.SUPPLIER_FE_BY_ASSETUID);
        String url = gatewayURL.replace("{assetUid}",asset.getUid());
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (LinkedHashMap<String, Object>) responseMap;
        } else {
            return null;
        }
    }

    public Map<String, Object> createServiceRequest(String token, V2_ServiceRequest newServiceRequest, AssetInfo asset, List<V2_BlobObject> picList,Integer createType) {
        ServiceRequestForm formData = new ServiceRequestForm();
        formData.setAssetId(asset.getId());
        formData.setAssetUid(asset.getUid());
        formData.setAssetStatus(String.valueOf(asset.getStatus()));
        if (null != newServiceRequest.getConfirmedDownTime()) {
            formData.setConfirmedDownTime(TimeUtils.getStrDate(newServiceRequest.getConfirmedDownTime(), "yyyy-MM-dd HH:mm:ss"));
        }

        formData.setPriority(newServiceRequest.getCasePriority());
        formData.setRequestReason(newServiceRequest.getRequestReason());
        formData.setAttachments(picList);
        formData.setAssetLocation(newServiceRequest.getAssetLocation());
        formData.setCustomerName(newServiceRequest.getCustomerName());
        formData.setCustomerTel(newServiceRequest.getCustomerTel());
        formData.setCustomerPosition(newServiceRequest.getCustomerPosition());
        formData.setCreateType(createType!= null ?createType :1);
        formData.setSupplierUid(asset.getSupplierNewUID());
        formData.setSiebleNumber(newServiceRequest.getSiebleNumber());
        formData.setCallCenterNumber(newServiceRequest.getCallCenterNumber());
        formData.setOrderType(newServiceRequest.getOrderType());
        formData.setRepairReportSendPlace(newServiceRequest.getRepairReportSendPlace());
        formData.setRepairSource(newServiceRequest.getSrRepairSource());
        formData.setRequestorPhone(newServiceRequest.getRequestorPhone());
        formData.setIbStatus(newServiceRequest.getIbStatus());
        HttpEntity<ServiceRequestForm> requestEntity = new HttpEntity<>(formData, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.SERVICE_REQUEST_CREATE);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (LinkedHashMap<String, Object>) responseMap.get("data");
        } else {
            return null;
        }
    }

    public String invokeWorkOrderAction(String token, WorkOrderActionForm formData, String actionName, String woId) {
        HttpEntity<WorkOrderActionForm> requestEntity = new HttpEntity<WorkOrderActionForm>(formData, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.WORK_ORDER_ACTION);
        String url = gatewayURL.replace("{workOrderId}", woId).concat("?type=").concat(actionName);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(url, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            logger.info("Invoke WorkOrderAction " + actionName + " has finished " + responseMap.get("data"));
            return (String) responseMap.get("data");
        } else {
            return this.getResponseFailMessage(response);
        }
    }

    String createWorkOrder(String token, WorkOrderForm formData) {
        HttpEntity<WorkOrderForm> requestEntity = new HttpEntity<WorkOrderForm>(formData, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.CREATE_WORK_ORDER);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (String) responseMap.get("message");
        } else {
            return null;
        }
    }

    String updateWorkOrderDetailPrice(String token, V2_WorkOrder_Detail workOrderDetail) {
        HttpEntity<V2_WorkOrder_Detail> requestEntity = new HttpEntity<V2_WorkOrder_Detail>(workOrderDetail, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.UPDATE_WODETAIL_PRICE);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.PUT, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (String) responseMap.get("message");
        } else {
            return null;
        }
    }

    public List<Integer> getVisableMvsAssetSystemId(String token) {
        HttpEntity<V2_WorkOrder_Detail> requestEntity = new HttpEntity<V2_WorkOrder_Detail>(null, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.GET_MVSASSECT_UID);
        ResponseEntity<ArrayList> response = template.exchange(gatewayURL, HttpMethod.GET, requestEntity,ArrayList.class);
        return response.getBody();
    }

    public List<String> getVisableMvsAssetUidSystemId(String token) {
        HttpEntity<V2_WorkOrder_Detail> requestEntity = new HttpEntity<V2_WorkOrder_Detail>(null, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.GET_MVSASSECT_UID_STR);
        ResponseEntity<ArrayList> response = template.exchange(gatewayURL, HttpMethod.GET, requestEntity,ArrayList.class);
        return response.getBody();
    }

    public List<Integer> getVisableMvsAssetSystemIdBySite(String token,String siteUID) {
        HttpEntity<V2_WorkOrder_Detail> requestEntity = new HttpEntity<V2_WorkOrder_Detail>(null, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.GET_MVSASSECT_UID_BYSITE);
        ResponseEntity<ArrayList> response = template.exchange(gatewayURL.toString()+"?siteUid="+siteUID, HttpMethod.GET, requestEntity,ArrayList.class);
        return response.getBody();
    }

    public List<Integer> getVisableMvsAssetSystemIdByClinaicalDept(String token,String deptUid) {
        HttpEntity<V2_WorkOrder_Detail> requestEntity = new HttpEntity<V2_WorkOrder_Detail>(null, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.GET_MVSASSECT_UID_BYCLINICALDEPT);
        ResponseEntity<ArrayList> response = template.exchange(gatewayURL+"?deptUid="+deptUid, HttpMethod.GET, requestEntity,ArrayList.class);
        return response.getBody();
    }

    public List<Map<String, String>> getUserSignature() {
        HttpHeaders header = createHttpHeaders(UserContextService.getAccessToken());
        HttpEntity<String> requestEntity = new HttpEntity<>(header);
        String url = getGatewayURL(GatewayURL.USER_SIGNATURE);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            List<Map<String, String>> responseBody = (List<Map<String, String>>) response.getBody().get("data");
            return responseBody;
        } else {
            return null;
        }
    }

    public String addUserSignature(String signatureId) {
        HttpHeaders header = createHttpHeaders(UserContextService.getAccessToken());
        HttpEntity<String> requestEntity = new HttpEntity<>(signatureId, header);
        String url = getGatewayURL(GatewayURL.USER_SIGNATURE);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (String) responseMap.get("message");
        } else {
            return null;
        }
    }

    public LinkedHashMap<String, Object> getConfigByKeyAndSite(String key, String siteUid) {
        HttpHeaders header = createHttpHeaders(UserContextService.getAccessToken());
        HttpEntity<String> requestEntity = new HttpEntity<>(header);
        String url = getGatewayURL(GatewayURL.GET_CONFIG_BY_KEY_AND_SITE);
        ResponseEntity<LinkedHashMap> response = template.exchange(url + "?key=" + key + "&siteUid=" + siteUid,
                HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = response.getBody();
            return (LinkedHashMap<String, Object>) responseMap.get("data");
        } else {
            return new LinkedHashMap<>();
        }
    }

}

class ServiceRequestForm {

    Integer assetId;

    String assetUid;
    String requestReason;
    String voiceId;
    List<V2_BlobObject> attachments;
    Integer priority;
    String assetStatus;
    String confirmedDownTime;
    String userName;
    String telephone;
    String wechatId;
    String nickName;
    String assetLocation;
    String customerName;
    String customerTel;
    String customerPosition;
    /**
     * 创建方式 1:医院创建  2:供应商创建
     */
    Integer createType;
    String supplierUid;
    String siebleNumber;
    String callCenterNumber;
    /**县域 开单类型： 1主动开单、2被动开单**/
    String orderType;

    String repairReportSendPlace;
    String ibStatus;
    String repairSource;
    private String requestorPhone;

    public String getIbStatus() {
        return ibStatus;
    }

    public void setIbStatus(String ibStatus) {
        this.ibStatus = ibStatus;
    }

    public String getRepairReportSendPlace() {
        return repairReportSendPlace;
    }

    public void setRepairReportSendPlace(String repairReportSendPlace) {
        this.repairReportSendPlace = repairReportSendPlace;
    }
    public String getRepairSource() {
        return repairSource;
    }

    public void setRepairSource(String repairSource) {
        this.repairSource = repairSource;
    }
    public String getSupplierUid() {
        return supplierUid;
    }

    public void setSupplierUid(String supplierUid) {
        this.supplierUid = supplierUid;
    }

    public Integer getCreateType() {
        return createType;
    }

    public void setCreateType(Integer createType) {
        this.createType = createType;
    }

    public String getAssetLocation() {
        return assetLocation;
    }

    public void setAssetLocation(String assetLocation) {
        this.assetLocation = assetLocation;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public String getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(String assetStatus) {
        this.assetStatus = assetStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getConfirmedDownTime() {
        return confirmedDownTime;
    }

    public void setConfirmedDownTime(String confirmedDownTime) {
        this.confirmedDownTime = confirmedDownTime;
    }

    public List<V2_BlobObject> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<V2_BlobObject> attachments) {
        this.attachments = attachments;
    }

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

    public String getCustomerPosition() {
        return customerPosition;
    }

    public void setCustomerPosition(String customerPosition) {
        this.customerPosition = customerPosition;
    }

    public String getSiebleNumber() {
        return siebleNumber;
    }

    public void setSiebleNumber(String siebleNumber) {
        this.siebleNumber = siebleNumber;
    }

    public String getCallCenterNumber() {
        return callCenterNumber;
    }

    public void setCallCenterNumber(String callCenterNumber) {
        this.callCenterNumber = callCenterNumber;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getRequestorPhone() {
        return requestorPhone;
    }

    public void setRequestorPhone(String requestorPhone) {
        this.requestorPhone = requestorPhone;
    }
}

class WorkOrderActionForm {

    String serviceRequestId;
    String  strDate;
    String desc;
    String estimatedCloseTime;
    String assigneeId;
    String feedbackRating;
    String patProblems;
    String patActions;
    String patTests;
    String caseType;
    String confirmedDownTime;
    String confirmedUpTime;
    String statusStartTime;
    String statusCloseTime;
    Integer subStatus;
    String assetStatus;
    String intExtType;
    Object stepDetail;
    List<V2_WorkOrderCoworker> stepCoworker;
    Double manHours;
    Double otherExpense;
    Integer currentStepId;
    String equipmentTaker;
    String takeTime;
    Integer repairType;
    String repairResult;
    Double externalExpense;
    String signature;

    List<V2_BlobObject> attachments;
    Integer entryType;
    List<V2_WorkOrderManHoursDetail> workHoursList;
    Boolean generatePartsRequest;

    //维修中【最新进展】
    String comment;

    String workOrderType;

    Integer coworkerEntryType;

    String userName;

    String tel;

    String comName;
    Double travelTime;
    public Integer getCoworkerEntryType() {
        return coworkerEntryType;
    }

    public void setCoworkerEntryType(Integer coworkerEntryType) {
        this.coworkerEntryType = coworkerEntryType;
    }

    public String getWorkOrderType() {
        return workOrderType;
    }

    public void setWorkOrderType(String workOrderType) {
        this.workOrderType = workOrderType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getGeneratePartsRequest() {
        return generatePartsRequest;
    }

    public void setGeneratePartsRequest(Boolean generatePartsRequest) {
        this.generatePartsRequest = generatePartsRequest;
    }

    public String getRepairResult() {
        return repairResult;
    }

    public void setRepairResult(String repairResult) {
        this.repairResult = repairResult;
    }

    public String getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(String serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEstimatedCloseTime() {
        return estimatedCloseTime;
    }

    public void setEstimatedCloseTime(String estimatedCloseTime) {
        this.estimatedCloseTime = estimatedCloseTime;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getFeedbackRating() {
        return feedbackRating;
    }

    public void setFeedbackRating(String feedbackRating) {
        this.feedbackRating = feedbackRating;
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

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getConfirmedDownTime() {
        return confirmedDownTime;
    }

    public void setConfirmedDownTime(String confirmedDownTime) {
        this.confirmedDownTime = confirmedDownTime;
    }

    public String getConfirmedUpTime() {
        return confirmedUpTime;
    }

    public void setConfirmedUpTime(String confirmedUpTime) {
        this.confirmedUpTime = confirmedUpTime;
    }

    public String getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(String assetStatus) {
        this.assetStatus = assetStatus;
    }

    public String getIntExtType() {
        return intExtType;
    }

    public void setIntExtType(String intExtType) {
        this.intExtType = intExtType;
    }

    public Object getStepDetail() {
        return stepDetail;
    }

    public void setStepDetail(Object stepDetail) {
        this.stepDetail = stepDetail;
    }

    public Integer getCurrentStepId() {
        return currentStepId;
    }

    public void setCurrentStepId(Integer currentStepId) {
        this.currentStepId = currentStepId;
    }

    public String getEquipmentTaker() {
        return equipmentTaker;
    }

    public void setEquipmentTaker(String equipmentTaker) {
        this.equipmentTaker = equipmentTaker;
    }

    public String getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }

    public List<V2_BlobObject> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<V2_BlobObject> attachments) {
        this.attachments = attachments;
    }

    public Integer getRepairType() {
        return repairType;
    }

    public void setRepairType(Integer repairType) {
        this.repairType = repairType;
    }

    public List<V2_WorkOrderCoworker> getStepCoworker() {
        return stepCoworker;
    }

    public void setStepCoworker(List<V2_WorkOrderCoworker> stepCoworker) {
        this.stepCoworker = stepCoworker;
    }

    public Double getManHours() {
        return manHours;
    }

    public void setManHours(Double manHours) {
        this.manHours = manHours;
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

    public String getStatusStartTime() {
        return statusStartTime;
    }

    public void setStatusStartTime(String statusStartTime) {
        this.statusStartTime = statusStartTime;
    }

    public String getStatusCloseTime() {
        return statusCloseTime;
    }

    public void setStatusCloseTime(String statusCloseTime) {
        this.statusCloseTime = statusCloseTime;
    }

    public Integer getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(Integer subStatus) {
        this.subStatus = subStatus;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Integer getEntryType() {
        return entryType;
    }

    public void setEntryType(Integer entryType) {
        this.entryType = entryType;
    }

    public List<V2_WorkOrderManHoursDetail> getWorkHoursList() {
        return workHoursList;
    }

    public void setWorkHoursList(List<V2_WorkOrderManHoursDetail> workHoursList) {
        this.workHoursList = workHoursList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public Double getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Double travelTime) {
        this.travelTime = travelTime;
    }
}

class WorkOrderForm {

    Integer assetId;
    String requestReason;
    String serviceRequestId;
    String assigneeId;
    Integer intExtType;
    List<V2_BlobObject> attachments;
    String assetStatus;

    private String siebleNumber;// 县域需求字段sieble单号
    private String callCenterNumber; // 县域需求call center单号
    public Integer getAssetId() {
        return assetId;
    }

    public String getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(String serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Integer getIntExtType() {
        return intExtType;
    }

    public void setIntExtType(Integer intExtType) {
        this.intExtType = intExtType;
    }

    public List<V2_BlobObject> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<V2_BlobObject> attachments) {
        this.attachments = attachments;
    }

    public String getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(String assetStatus) {
        this.assetStatus = assetStatus;
    }

    public String getSiebleNumber() {
        return siebleNumber;
    }

    public void setSiebleNumber(String siebleNumber) {
        this.siebleNumber = siebleNumber;
    }

    public String getCallCenterNumber() {
        return callCenterNumber;
    }

    public void setCallCenterNumber(String callCenterNumber) {
        this.callCenterNumber = callCenterNumber;
    }
}
