/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.wo;

import com.alibaba.fastjson.JSONArray;
import com.ge.apm.aop.SystemEventAuditAopAspect;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.exception.BusinessException;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.PmOrderActionForm;
import com.ge.healthcare.cn.common.cons.AuditBoTypeEnum;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author 212579464
 */
@Component
public class V2_WorkOrderService {
    static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(V2_WorkOrderService.class);

    @Autowired
    private BiomedGroupRepository groupDao;

    @Autowired
    private V2_WorkOrderRepository woDao;

    @Autowired
    private AssetInfoRepository assetDao;

    @Autowired
    private ServiceRequestRepository srDao;

    @Autowired
    private V2_WorkOrderStepRepository stepDao;

    @Autowired
    private WorkOrderDetailRepository detailDao;
    @Autowired
    private WorkOrderDetailReturnRepository detailReturnDao;
    @Autowired
    private WorkOrderDetailMeasuringRepository detailMeasuringDao;
    @Autowired
    private UserAccountRepository userDao;

    @Autowired
    private BlobObjectRepository blobDao;

    @Autowired
    private ServiceRequestApiService srApi;
    @Autowired
    OrgInfoRepository orgInfoRepository;
    @Autowired
    AssetInfoRepository assetInfoRepository;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    private WorkOrderCoworkerRepository workOrderCoworkerRepository;
    @Autowired
    private UserCommonService ucService;
    @Autowired
    private ServiceRequestMsgRepository msgDao;
    @Autowired
    private WorkOrderManHoursDetailRepository workOrderManHoursDetailRepository;
    @Autowired
    private BlobFileService blobService;
    @Autowired
    private SystemEventAuditAopAspect systemEventAuditAopAspect;


    public V2_WorkOrder getWorkOrderById(String woId) {
        return woDao.findById(woId);
    }

    public List<V2_WorkOrder> getWorkOrdersBySR(String serviceRequest) {
        return woDao.findBySrId(serviceRequest);
    }

    public List<V2_ServiceRequest> getServiceRequestByAssetId(Integer id) {
        return srDao.findByAssetId(id);
    }

    public List<V2_WorkOrder_Step> getWorkOrderSteps(String woId) {
        return stepDao.findByWorkOrder(woId);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetails(String woId) {
        return detailDao.findByWoId(woId);
    }

    public List<V2_WorkOrder_Detail_Return> getWorkOrderDetailReturn(String woId) {
        return detailReturnDao.findByWoIdSort(woId);
    }

    public List<MeasuringOrderDetail> getWorkOrderMeasuringDetails(String woId) {
        return detailMeasuringDao.findByOrderId(woId);
    }

    public List<V2_WorkOrder_Step> getWorkOrderDetailResponsor(String woId, Integer step){
       return  stepDao.findResponsor(woId,step);
    }

    public List<V2_WorkOrder_Step> getWorkOrderRepaireName(String woId, Integer step){
        return  stepDao.findRepaireName(woId,step);
    }

    public List<V2_WorkOrder_Step> getMaxStartTimeWorkOrderStepByWoIdAndStepId(String woId, Integer step) {
        return stepDao.findMaxStartTimeWorkOrderStepByWoIdAndStepId(woId, step);
    }

    public List<V2_WorkOrder_Step> getWorkIds(String owerName){
        return  stepDao.findWorkIds(owerName);
    }

    public List<String> findOwerNames(String owerName){
        return  stepDao.findOwerNames(owerName);
    }

    public List<V2_WorkOrder_Step> findWorkSteps(String owerName){
        return  stepDao.findWorkSteps(owerName);
    }

    public List<V2_WorkOrder_Step> getWorkStepsByStepId(Integer stepId, String tenantUid){
        return  stepDao.findWorkStepsByStepIdAndTenantUid(stepId, tenantUid);
    }

    public List<V2_WorkOrder_Step> getWorkOrderDetailByownerName(String ownerName){
       return  stepDao.getWorkOrderDetailByownerName(ownerName);
    }

    public List<String> getSRListByStatus(List<Integer> status) {
        List<SearchFilter> srSearchFilters = new ArrayList();
        srSearchFilters.add(new SearchFilter("status", SearchFilter.Operator.IN, status));
        srSearchFilters.add(new SearchFilter("requestorId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*2+"));
        srSearchFilters.add(new SearchFilter("fromDeptUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getOrgInfoUID(), "1*2+"));
        return srDao.findBySearchFilter(srSearchFilters).stream().map(item -> item.getId()).collect(Collectors.toList());
    }
    public void updateAccessPrice(V2_WorkOrder_Detail workOrderDetail){
//        detailDao.save(workOrderDetail);
        String token = UserContextService.getAccessToken();
        String res = srApi.updateWorkOrderDetailPrice(token,workOrderDetail);
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", WebUtil.getMessage("Cancel")));
        }
    }


    public void createServiceRequest(V2_ServiceRequest newServiceRequest, AssetInfo asset, List<V2_BlobObject> picList,Integer createType) {
        String token = UserContextService.getAccessToken();
        Map<String, Object> data = srApi.createServiceRequest(token, newServiceRequest, asset, picList,createType);
    }
    public  Map<String, Object> findSupplierFEByAssetUid( AssetInfo asset) {
       return srApi.findSupplierFEByAssetUid(asset);
    }

    public List<V2_BlobObject> getAttachByBoSubType(String srUid,String bosubType) {
        List<SearchFilter> blobSearchFilters = new ArrayList();
        blobSearchFilters.add(new SearchFilter("boUuid", SearchFilter.Operator.EQ, srUid));
/*
        blobSearchFilters.add(new SearchFilter("boType", SearchFilter.Operator.EQ, "WorkOrder"));
*/
        if(bosubType!=null) blobSearchFilters.add(new SearchFilter("boSubType", SearchFilter.Operator.EQ, bosubType));
        return blobDao.findBySearchFilter(blobSearchFilters);
    }

    public List<V2_BlobObject> getPicListBySrUid(String srUid,Integer type, String boSubType) {
        List<SearchFilter> blobSearchFilters = new ArrayList<>();
        blobSearchFilters.add(new SearchFilter("boUuid", SearchFilter.Operator.EQ, srUid));
        if (type ==1 && Objects.equals(boSubType, V2_BlobObject.BO_SUB_TYPE_DOCS)) {
            blobSearchFilters.add(new SearchFilter("boSubType", SearchFilter.Operator.EQ, V2_BlobObject.BO_SUB_TYPE_DOCS ));
        }
        if (type ==1 && Objects.equals(boSubType, V2_BlobObject.BO_SUB_TYPE_IMG)) {
            blobSearchFilters.add(new SearchFilter("objectType", SearchFilter.Operator.LIKE, "image/%"));
            blobSearchFilters.add(new SearchFilter("boSubType", SearchFilter.Operator.EQ, "Image", "1*2+" ));
            blobSearchFilters.add(new SearchFilter("boSubType", SearchFilter.Operator.IsNull,null,"1*2+" ));
        }
        if (type == 2) {
            blobSearchFilters.add(new SearchFilter("objectType", SearchFilter.Operator.LIKE, "audio/%"));
        }
        if (type == 3) {
            blobSearchFilters.add(new SearchFilter("objectType", SearchFilter.Operator.LIKE, "video/%"));
        }
        return blobDao.findBySearchFilter(blobSearchFilters);
    }


    public void cancelWorkOrder(String workOrderId, String reason, String assetStatus) {
        V2_WorkOrder wo = woDao.findById(workOrderId);
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setDesc(reason);
        formData.setAssetStatus(assetStatus);
        formData.setCurrentStepId(wo.getCurrentStepId());
        String res = srApi.invokeWorkOrderAction(token, formData, "cancel", workOrderId);
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", WebUtil.getMessage("Cancel")));
        }
    }

    public List<UserAccount> getWorkerList(Integer assetId) {
        return ucService.getWorkerList(assetId,"");
    }

    public List<UserAccount> getAckerList(String woId) {
        return ucService.getAckerList(woId,"");
    }

    public List<UserAccount> getApproverListByAsset(String assetUid,String name) {
        return ucService.getApproveList(assetUid,name);
    }

    public AssetInfo getAssetInfo(Integer assetId) {
        return assetDao.findById(assetId);
    }

    public void dispatchWorkOrder(V2_WorkOrder selectedWorkOrder) {
        V2_WorkOrder tmpwo = woDao.findById(selectedWorkOrder.getId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setAssigneeId(selectedWorkOrder.getCurrentPersonId().toString());
        formData.setIntExtType(selectedWorkOrder.getIntExtType().toString());
        formData.setCurrentStepId(tmpwo.getCurrentStepId());
        String res = srApi.invokeWorkOrderAction(token, formData, "assign", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail",WebUtil.getMessage("dispatchWorkOrder")));
        }
    }

    public void ackWorkOrder(V2_WorkOrder selectedWorkOrder) {
        V2_WorkOrder tmpwo = woDao.findById(selectedWorkOrder.getId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(tmpwo.getCurrentStepId());
        String res = srApi.invokeWorkOrderAction(token, formData, "ack", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", WebUtil.getMessage("Acknowledge")));
        }
    }

    public void updateDownTime(V2_WorkOrder wo, Date confirmedDowntime) {
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(wo.getCurrentStepId());
        formData.setAssetStatus("2");
        formData.setConfirmedDownTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(confirmedDowntime));
        String res = srApi.invokeWorkOrderAction(token, formData, "updateTime", wo.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", WebUtil.getMessage("Acknowledge")));
        }
    }

    public void rejectWorkOrder(V2_WorkOrder selectedWorkOrder, String reason, Integer status, Date confirmedDowntime) {
        V2_WorkOrder tmpwo = woDao.findById(selectedWorkOrder.getId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(tmpwo.getCurrentStepId());
        formData.setDesc(reason);
        formData.setAssetStatus(String.valueOf(status));
        formData.setRepairType(tmpwo.getRepairType());
        String res = srApi.invokeWorkOrderAction(token, formData, "ackFailed", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", WebUtil.getMessage("Acknowledge")));
        }
    }

    public void rateWorkOrder(V2_WorkOrder selectedWorkOrder, Integer rating, String comments) {
        V2_WorkOrder tmpwo = woDao.findById(selectedWorkOrder.getId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(tmpwo.getCurrentStepId());
        formData.setDesc(comments);
        formData.setFeedbackRating(String.valueOf(rating));
        String res = srApi.invokeWorkOrderAction(token, formData, "rate", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", WebUtil.getMessage("feedbackComment")));
        }
    }

    public void checkIn(V2_WorkOrder selectedWorkOrder) {
        V2_WorkOrder tmpwo = woDao.findById(selectedWorkOrder.getId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(tmpwo.getCurrentStepId());
        String res = srApi.invokeWorkOrderAction(token, formData, "checkIn", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", WebUtil.getMessage("feedbackComment")));
        }
    }

    public void takeEquip(V2_WorkOrder selectedWorkOrder, String taker, String takeTime) {
        V2_WorkOrder tmpwo = woDao.findById(selectedWorkOrder.getId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(tmpwo.getCurrentStepId());
        formData.setEquipmentTaker(taker);
        formData.setTakeTime(takeTime);
        String res = srApi.invokeWorkOrderAction(token, formData, "take", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", WebUtil.getMessage("feedbackComment")));
        }
    }

    public void acceptWorkOrder(V2_WorkOrder selectedWorkOrder, Date estimatedCloseTime, Integer extType, String comments) {
        //V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        //   selectedWorkOrder.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        formData.setEstimatedCloseTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(estimatedCloseTime));
        formData.setRepairType(extType);
        formData.setDesc(comments);
        String res = srApi.invokeWorkOrderAction(token, formData, "accept", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "接单"));
        }
        selectedWorkOrder.setCurrentStepId(4);
    }
    public void confirmSend(V2_WorkOrder selectedWorkOrder) {
        //V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        //   selectedWorkOrder.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        String res = srApi.invokeWorkOrderAction(token, formData, "confirmSend", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "送修"));
        }
        selectedWorkOrder.setCurrentStepId(4);
    }
    public void confirmReturn(V2_WorkOrder selectedWorkOrder) {
        //V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        //   selectedWorkOrder.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        String res = srApi.invokeWorkOrderAction(token, formData, "confirmReturn", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "返还"));
        }
        selectedWorkOrder.setCurrentStepId(4);
    }

    public void takeWorkOrder(V2_WorkOrder selectedWorkOrder, Date takeTime, String taker) {
        //V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        selectedWorkOrder.setCurrentStepId(3);
        selectedWorkOrder.setCurrentStepName("待接单");
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        formData.setTakeTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(takeTime));
        formData.setEquipmentTaker(taker);
        String res = srApi.invokeWorkOrderAction(token, formData, "take", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "接单"));
        }
    }

    public void submitWorkOrder(V2_WorkOrder selectedWorkOrder, List<V2_WorkOrder_Detail> accessoryList, String comment,String approverUid) {
        selectedWorkOrder.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setDesc(comment);
        formData.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        formData.setStepDetail(accessoryList);
        formData.setAssigneeId(approverUid);
        String res = srApi.invokeWorkOrderAction(token, formData, "approval", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "提交"));
        } else {
            WebUtil.addSuccessMessage("已提交");
        }
    }

    public void approveAction(V2_WorkOrder selectedWorkOrder, List<V2_WorkOrder_Detail> approvedDetails, String comments) {
        selectedWorkOrder.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        approvedDetails.stream().forEach(item -> item.setCreatedDate(null));
        formData.setStepDetail(approvedDetails);
        formData.setDesc(comments);
        formData.setSignature(selectedWorkOrder.getPartApproveSignature());
        // 标记用于微服务端避免重复生成备件申领申购单
        formData.setGeneratePartsRequest(false);

        String approvalType = "approvalReject";
        if (approvedDetails.stream().anyMatch(item -> item.getApproved())) {
            if (approvedDetails.stream().anyMatch(item -> !item.getApproved())) {
                approvalType = "approvalPartial";
            } else {
                approvalType = "approvalPass";
            }
        }

        String res = srApi.invokeWorkOrderAction(token, formData, approvalType, selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "提交"));
        }
    }

    public void reassignWorkOrder(V2_WorkOrder selectedWorkOrder, Integer assigneeId, String comments) {
        selectedWorkOrder.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        formData.setAssigneeId(String.valueOf(assigneeId));
        formData.setDesc(comments == null ? "" : comments);
        String res = srApi.invokeWorkOrderAction(token, formData, "reassign", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "转单"));
        }
    }

    public void saveRepairWorkOrder(V2_WorkOrder selectedWorkOrder,String assigneeId, Date confirmedUpTime, Integer assetStatus, List<V2_WorkOrder_Detail> accDetails, List<V2_WorkOrderCoworker> coDetails, String comments, List<V2_BlobObject> attachments) {
        selectedWorkOrder.setCurrentStepId(4);
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(4);
        formData.setAssigneeId(assigneeId);
        selectedWorkOrder.setCurrentStepName("维修中");
        if(selectedWorkOrder.getStatusCloseTime()!=null) {
            formData.setStatusCloseTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(selectedWorkOrder.getStatusCloseTime()));
        }
        if(selectedWorkOrder.getStatusStartTime()!=null) {
            formData.setStatusStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(selectedWorkOrder.getStatusStartTime()));
        }
        formData.setSubStatus(selectedWorkOrder.getSubStatus());
        formData.setDesc(comments);
        formData.setAssetStatus(assetStatus.toString());
        formData.setRepairResult(selectedWorkOrder.getRepairResult());
        formData.setIntExtType(selectedWorkOrder.getIntExtType().toString());
        formData.setPatActions(selectedWorkOrder.getPatActions());
        formData.setPatProblems(selectedWorkOrder.getPatProblems());
        formData.setPatTests(selectedWorkOrder.getPatTests());
        formData.setEntryType(selectedWorkOrder.getEntryType());
        formData.setCoworkerEntryType(selectedWorkOrder.getCoworkerEntryType());
        //【最新进展】
        formData.setComment(selectedWorkOrder.getComment());

        if(selectedWorkOrder.getEntryType() == 1 || selectedWorkOrder.getEntryType() == 3 ){
            formData.setWorkHoursList(selectedWorkOrder.getWorkHoursList());
        }
        if (null != confirmedUpTime) {
            formData.setConfirmedUpTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(confirmedUpTime));
        }
        if (null != selectedWorkOrder.getCaseType()) {
            formData.setCaseType(selectedWorkOrder.getCaseType()==null?null:selectedWorkOrder.getCaseType().toString());
        }
        if(null!=assetStatus){
            formData.setAssetStatus(assetStatus.toString());
        }

        formData.setStepDetail(accDetails);
        formData.setStepCoworker(coDetails);
        formData.setManHours(selectedWorkOrder.getManHours());
        formData.setOtherExpense(selectedWorkOrder.getOtherExpense());
        formData.setExternalExpense(selectedWorkOrder.getExternalExpense());
        formData.setAttachments(attachments);
        formData.setWorkOrderType(selectedWorkOrder.getWorkOrderType());
        ExternalTempUser externalTempUser = null;
        if((externalTempUser = selectedWorkOrder.getExternalTempUser())!= null && ObjectUtil.isNotEmpty(externalTempUser.getWoId())){
            formData.setUserName(externalTempUser.getUserName());
            formData.setComName(externalTempUser.getComName());
            formData.setTel(externalTempUser.getTel());
        }
        formData.setTravelTime(selectedWorkOrder.getTravelTime());
        formData.setDiagnosticProcess(selectedWorkOrder.getDiagnosticProcess());
        formData.setTotalInspectionCount(selectedWorkOrder.getTotalInspectionCount());
        formData.setTotalExposureCount(selectedWorkOrder.getTotalExposureCount());
        formData.setTubeInstallDate(selectedWorkOrder.getTubeInstallDate());
        normalizeEntryType(formData);
        String res = srApi.invokeWorkOrderAction(token, formData, "saveRepair", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "保存"));
        }
    }
    private void normalizeEntryType(WorkOrderActionForm formData) {
        if (formData.getCoworkerEntryType() != null) {
            int type = formData.getCoworkerEntryType();
            if (type == 2 || type == 3) {
                formData.setCoworkerEntryType(type - 2);
            }
        }
        if (formData.getEntryType() != null) {
            int type = formData.getEntryType();
            if (type == 2 || type == 3) {
                formData.setEntryType(type - 2);
            }
        }
    }


    public void repairWorkOrder(V2_WorkOrder selectedWorkOrder,String assignId, Date confirmedUpTime, Integer assetStatus, List<V2_WorkOrder_Detail> accDetails, List<V2_WorkOrderCoworker> coDetails, String comments, List<V2_BlobObject> attachments) {
        selectedWorkOrder.setCurrentStepId(4);
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(4);
        selectedWorkOrder.setCurrentStepName("维修中");
        formData.setDesc(comments);
        formData.setRepairResult(selectedWorkOrder.getRepairResult());
        formData.setRepairType(selectedWorkOrder.getRepairType());
        if(selectedWorkOrder.getStatusCloseTime()!=null) {
            formData.setStatusCloseTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(selectedWorkOrder.getStatusCloseTime()));
        }
        if(selectedWorkOrder.getStatusStartTime()!=null) {
            formData.setStatusStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(selectedWorkOrder.getStatusStartTime()));
        }
        formData.setSubStatus(selectedWorkOrder.getSubStatus());

        formData.setIntExtType(selectedWorkOrder.getIntExtType().toString());
        formData.setPatActions(selectedWorkOrder.getPatActions());
        formData.setPatProblems(selectedWorkOrder.getPatProblems());
        formData.setPatTests(selectedWorkOrder.getPatTests());
        if(confirmedUpTime != null){
            formData.setConfirmedUpTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(confirmedUpTime));
        }
        formData.setCaseType(selectedWorkOrder.getCaseType()==null?null:selectedWorkOrder.getCaseType().toString());
        formData.setAssetStatus(assetStatus.toString());
        formData.setStepDetail(accDetails);
        formData.setStepCoworker(coDetails);
        formData.setManHours(selectedWorkOrder.getManHours());
        formData.setOtherExpense(selectedWorkOrder.getOtherExpense());
        formData.setExternalExpense(selectedWorkOrder.getExternalExpense());
        formData.setAttachments(attachments);
        formData.setAssigneeId(assignId);
        formData.setEntryType(selectedWorkOrder.getEntryType());
        formData.setCoworkerEntryType(selectedWorkOrder.getCoworkerEntryType());
        formData.setWorkOrderType(selectedWorkOrder.getWorkOrderType());
        if(selectedWorkOrder.getEntryType() == 1 || selectedWorkOrder.getEntryType() == 3){
            formData.setWorkHoursList(selectedWorkOrder.getWorkHoursList());
        }
        //【最新进展】
        formData.setComment(selectedWorkOrder.getComment());
        ExternalTempUser externalTempUser = null;
        if((externalTempUser = selectedWorkOrder.getExternalTempUser())!= null && ObjectUtil.isNotEmpty(externalTempUser.getWoId())){
            formData.setUserName(externalTempUser.getUserName());
            formData.setComName(externalTempUser.getComName());
            formData.setTel(externalTempUser.getTel());
        }
        formData.setTravelTime(selectedWorkOrder.getTravelTime());
        formData.setTotalInspectionCount(selectedWorkOrder.getTotalInspectionCount());
        formData.setTotalExposureCount(selectedWorkOrder.getTotalExposureCount());
        formData.setTubeInstallDate(selectedWorkOrder.getTubeInstallDate());
        normalizeEntryType(formData);
        String res = srApi.invokeWorkOrderAction(token, formData, "repair", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "完成"));
        }
    }

    public void closeWorkOrder(V2_WorkOrder selectedWorkOrder, String comments, List<V2_BlobObject> picList) {
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        selectedWorkOrder.setCurrentStepId(6);
        selectedWorkOrder.setCurrentStepName("待关单");
        formData.setCurrentStepId(6);
        formData.setDesc(comments);
        formData.setFeedbackRating(String.valueOf(selectedWorkOrder.getFeedbackRating()));
        formData.setIntExtType(selectedWorkOrder.getIntExtType().toString());
        formData.setPatActions(selectedWorkOrder.getPatActions());
        formData.setPatProblems(selectedWorkOrder.getPatProblems());
        formData.setPatTests(selectedWorkOrder.getPatTests());
        formData.setCaseType(selectedWorkOrder.getCaseType()==null?null:selectedWorkOrder.getCaseType().toString());
        formData.setAttachments(picList);
        //待关单增加【最新进展】
        formData.setComment(selectedWorkOrder.getComment());

        String res = srApi.invokeWorkOrderAction(token, formData, "close", selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            if(!Strings.isNullOrEmpty(res)){
                WebUtil.addErrorMessage(res);
            }else{
                WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "关单"));
            }
        }
    }

    public void reCreateWorkOrder(V2_WorkOrder selectedWorkOrder, String reason, Integer extType, List<V2_BlobObject> picList) {
        String token = UserContextService.getAccessToken();
        WorkOrderForm formData = new WorkOrderForm();
        formData.setServiceRequestId(selectedWorkOrder.getSrId());
        formData.setAssigneeId(selectedWorkOrder.getCurrentPersonId().toString());
        formData.setAssetId(selectedWorkOrder.getAssetId());
        formData.setAssetStatus("2");
        formData.setRequestReason(reason);
        formData.setIntExtType(extType);
        formData.setAttachments(picList);
        formData.setSiebleNumber(selectedWorkOrder.getSiebleNumber());
        formData.setCallCenterNumber(selectedWorkOrder.getCallCenterNumber());
        String res = srApi.createWorkOrder(token, formData);
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "创建"));
        }
    }



//Type：0-我的/1-同一group的/2-同院区的/3-其它院区的
    public Page<V2_WorkOrder> getWorkOrderListToPickup(int type, PageRequest pr, UserAccount ua) {
        Page<V2_WorkOrder> pages = null;

        if (type == 0 || type == 1) {
            switch (type) {
                case 0:
                    pages = woDao.fetchAvailableWorkOrderByUser(ua.getId(), pr);
                    break;
                case 1:
                    pages = woDao.fetchAvailableSameGroupWorkOrderByUser(ua.getId(), pr);
            }
            return pages;
        }
        Integer orgLevel = ua.getOrgLevel();
        if (orgLevel == null || orgLevel == 0) {
            orgLevel = 3;
        }
        List<SearchFilter> searchFilters = new ArrayList<>();
        switch (orgLevel) {
            case 1:
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, ua.getInstitutionUID()));
                break;
            case 2:
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, ua.getHospitalUID()));
                break;
            case 3:
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, ua.getSiteUID()));
        }

        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
        searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 3));

        return woDao.findBySearchFilter(searchFilters, pr);
    }

    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return workOrderCoworkerRepository.findByWoId(woId);
    }

    public List<UserAccount> findMaintenanceStaff(int assetId) {
        List<UserAccount> userListTag = null;
        AssetInfo assetInfo = assetInfoRepository.getById(assetId);

        //获取关注过该资产标签的用户, 设备标签<->(设备+工程师组[员1，2..])
        userListTag = userAccountRepository.getResponserByAssetId(assetId);

        //获取和资产绑定的owner
        List<UserAccount> assetOwnerList = userAccountRepository.getResponserWithAsset(assetId);
        List<UserAccount> defaultUserList = new ArrayList<UserAccount>();

        // Get responser with deeper down agaisnt on siteuid ->hospitalUid -> InstitutionUID
        if (CollectionUtils.isEmpty(userListTag) & CollectionUtils.isEmpty(assetOwnerList)) {
            OrgInfo orgInfo = orgInfoRepository.getByUid(assetInfo.getClinicalDeptUID());
            if (orgInfo != null) {
                if (orgInfo.getAssetOwnerId() != null) {
                    UserAccount user1 = userAccountRepository.findById(orgInfo.getAssetOwnerId());
                    defaultUserList.add(user1);
                }
                if (orgInfo.getAssetOwnerId2() != null) {
                    UserAccount user2 = userAccountRepository.findById(orgInfo.getAssetOwnerId2());
                    defaultUserList.add(user2);
                }
            }
            return defaultUserList;

        }
        if (CollectionUtils.isNotEmpty(userListTag) & CollectionUtils.isNotEmpty(assetOwnerList)) {
            userListTag.addAll(assetOwnerList);
            userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(userListTag));
            userListTag = userListTag.stream().filter(id -> id != null).collect(Collectors.toList());
            return userListTag;
        } else if (CollectionUtils.isEmpty(assetOwnerList)) {
            userListTag = userListTag.stream().filter(id -> id != null).collect(Collectors.toList());
            userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(userListTag));
            return userListTag;
        } else if (CollectionUtils.isEmpty(userListTag)) {
            userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(assetOwnerList));
            defaultUserList = userAccountRepository.getDefaultUsers(assetId);
            userListTag.addAll(defaultUserList);
            userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(userListTag));
            userListTag = userListTag.stream().filter(id -> id != null).collect(Collectors.toList());
            return userListTag;
        }

        return userListTag;
    }

    @Transactional
    public void arrivedParts(String detailId) {
        V2_WorkOrder_Detail detail = detailDao.findById(detailId);
        detail.setArrived(Boolean.TRUE);
        detailDao.save(detail);
    }

    public List<ServiceRequestMsg> getServiceRequestMessageList(String id) {
        return msgDao.findBySrIdOrderBySendTime(id);
    }

    public void saveServiceRequestMessage(String msg, UserAccount userAccount, String srId){
        ServiceRequestMsg serviceRequestMsg = new ServiceRequestMsg();
        serviceRequestMsg.setSrId(srId);
        serviceRequestMsg.setMessage(msg);
        serviceRequestMsg.setSendTime(new Date());
        serviceRequestMsg.setSenderUserId(userAccount.getId());
        serviceRequestMsg.setSenderUserName(userAccount.getName());
        msgDao.save(serviceRequestMsg);
    }

    /**
     * @param
     * @return void
     * @date 2020/6/29 11:00
     * 设置总单据信息
     */
    public void approveHighLevelAction(V2_WorkOrder selectedWorkOrder, List<V2_WorkOrder_Detail> approvedDetails, String comments,String approverUid) {
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        formData.setStepDetail(approvedDetails);
        formData.setDesc(comments);
        formData.setAssigneeId(approverUid);
        formData.setSignature(selectedWorkOrder.getPartApproveSignature());
        String approvalType = "approval";
        String res = srApi.invokeWorkOrderAction(token, formData, approvalType, selectedWorkOrder.getId());
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", "提交"));
        } else {
            WebUtil.addSuccessMessage("已提交");
        }
    }

    public List<V2_WorkOrderManHoursDetail> getWorkOrderManHoursDetail(String woId) {
        return workOrderManHoursDetailRepository.findByWoId(woId);
    }

    public List<Map<String, String>> getUserSignature() {
        return srApi.getUserSignature();
    }
    public String addUserSignature(String signatureId) {
        return srApi.addUserSignature(signatureId);
    }

    public List<V2_WorkOrder> findBySrNum(String tenantUid, String srNum) {
        V2_ServiceRequest request = srDao.findByTenantUIDAndSrNum(tenantUid, srNum);
        return woDao.findBySrId(request.getId());
    }

    public DefaultStreamedContent generate(Map<String, Boolean> woOrders,UserAccount userAccount ) throws IOException {
        if (!woOrders.values().contains(true)) {
            throw new BusinessException("未选中工单记录");
        }
        String reportName = DateFormatUtils.format(new Date(), "yyyy-MM-dd") + "工单报告.zip";
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ZipOutputStream zout = new ZipOutputStream(bout)) {
            woOrders.forEach((id, selected) -> {
                if (selected) {
                    try {
                        V2_WorkOrder workOrder = woDao.findById(id);
                        List<V2_BlobObject> byBoUuid = blobDao.findByBoUuid(workOrder.getServiceRequest().getId());
//                        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(byBoUuid)){
//                            byBoUuid = byBoUuid.stream()
//                                    .filter(item -> item.getObjectName().contains("-工单报告.pdf"))
//                                    .collect(Collectors.toList());
//                        }
                        for (V2_BlobObject v2BlobObject : byBoUuid) {
                            String vname = v2BlobObject.getObjectName();
                            zout.putNextEntry(new ZipEntry(vname));
                            byte[] bytess  = IOUtils.toByteArray(blobService.getBlobObjectByStorageId(v2BlobObject.getObjectStorageId()).getStream());
                            IOUtils.write(bytess, zout);
                        }
                        zout.closeEntry();
                    } catch (Exception e) {
                        logger.error("报告下载失败 e :",e);
                    }
                }
            });
            zout.close();
            return new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()), "application/zip", reportName);
        }
    }

    public String generateMessageTip(Map<String, Boolean> woOrders) {
        StringBuilder messageTipBuilder = new StringBuilder();
        for (Map.Entry<String, Boolean> entry : woOrders.entrySet()) {
            if (!entry.getValue()) {
                continue; // 跳过未选中的工单
            }
            String id = entry.getKey();
            V2_WorkOrder workOrder = woDao.findById(id);
            if (workOrder == null || workOrder.getServiceRequest() == null || workOrder.getServiceRequest().getSrNum() == null) {
                continue; // 跳过无效工单或服务请求
            }
            List<V2_BlobObject> blobObjects = blobDao.findByBoUuid(workOrder.getId());
            // 过滤掉包含 "-工单报告.pdf" 的对象
            List<V2_BlobObject> filteredBlobObjects = blobObjects != null
                    ? blobObjects.stream()
                    .filter(item -> item.getObjectName().contains("-工单报告.pdf")).collect(Collectors.toList())
                    : Collections.emptyList();
            // 如果过滤后列表为空，则添加 SR 编号到提示信息中
            if (CollectionUtils.isEmpty(filteredBlobObjects)) {
                messageTipBuilder.append(workOrder.getServiceRequest().getSrNum()).append(",");
            }
        }
        // 去掉最后多余的逗号并补充提示信息
        if (messageTipBuilder.length() > 0) {
            messageTipBuilder.deleteCharAt(messageTipBuilder.length() - 1); // 删除最后一个逗号
            messageTipBuilder.append("不存在工单报告！");
        }
        messageTipBuilder.append("请确认是否需要下载？");
        return messageTipBuilder.toString();
    }


    public void postProcessorExcel(){
        try{
            SystemEventAuditEnum eventAuditEnum = SystemEventAuditEnum.TABLE_DOWNLOAD_WORK_ORDER;
            SystemEventAudit systemEventAudit = systemEventAuditAopAspect.buildEventAudit(UserContextService.getCurrentUserAccount(), eventAuditEnum, null, Boolean.TRUE, "", AuditBoTypeEnum.TABLE_DOWNLOAD.getBoType(), "页面表格");
            systemEventAuditAopAspect.sendSystemAuditMessage(systemEventAudit);
        }catch (Exception e){
            logger.error("postProcessorExcel error:",e);
        }

    }

}
