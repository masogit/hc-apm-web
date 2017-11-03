/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.pm;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.UserCommonService;
/*import com.ge.apm.service.wo.PmOrderActionForm;
import com.ge.apm.service.wo.WorkOrderForm;*/
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@Component
public class V2_PmOrderService {
    @Autowired
    protected RestTemplate template;
    @Autowired
    private BiomedGroupRepository groupDao;

    @Autowired
    private V2_PmOrderRepository woDao;

    @Autowired
    private AssetInfoRepository assetDao;

    @Autowired
    private ServiceRequestRepository srDao;

    @Autowired
    private WorkOrderStepRepository stepDao;


    @Autowired
    private WorkOrderDetailRepository detailDao;

    @Autowired
    private UserAccountRepository userDao;
    
    @Autowired
    private BlobObjectRepository blobDao;

    @Autowired
    private PmOrderRequestApiService srApi;
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


    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return workOrderCoworkerRepository.findByWoId(woId);
    }

    public void closePM(V2_PmOrder selectedPmOrder){
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setActionType("close");
        formData.setStepId(selectedPmOrder.getCurrentStepId());
      formData.setQualityChecking(selectedPmOrder.getQualityChecking());
        String res = srApi.invokePmOrderAction(token,formData,"close",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"提交");
        }
    }
    public void submitWorkOrder(V2_PmOrder selectedPmOrder,List<V2_WorkOrder_Detail> accessoryList,String approvaler ) {
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();

        formData.setActionType("approval");
        formData.setStepId(selectedPmOrder.getCurrentStepId());
formData.setAssignee(approvaler);

        formData.setDetails(accessoryList);
        String res = srApi.invokePmOrderAction(token,formData,"approval",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"提交");
        }
    }

    public void createPMrequest(String assetUID){
        String token = UserContextService.getAccessToken();
        srApi.createPmRequest(token,assetUID);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetails(String woId) {
        return detailDao.findByWoId(woId);
    }
    public void rateWorkOrder(V2_PmOrder selectedPmOrder, Integer rating, String comments) {
        V2_PmOrder tmpwo=woDao.findById(selectedPmOrder.getId());
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
       // formData.setStepId("rate");
        formData.setStepId(selectedPmOrder.getCurrentStepId());
        formData.setDesc(comments);
       // formData.setFeedbackRating(String.valueOf(rating));
        String res = srApi.invokePmOrderAction(token,formData,"rate",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("Ok")){
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("feedbackComment"));
        }
    }
    public void rejectWorkOrder(V2_PmOrder selectedPmOrder,String reason, Integer status, Date confirmedDowntime) {
        V2_PmOrder tmpwo=woDao.findById(selectedPmOrder.getId());
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        //formData.setStepId("Ack");
        formData.setStepId(selectedPmOrder.getCurrentStepId());
       // formData.setDesc(reason);
      /*  formData.setAssetStatus(StrPmOrderAction(token,formData,"ackFailed",selectedWorkOrder.getId());*/
        String res = srApi.invokePmOrderAction(token,formData,"ackFailed",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Acknowledge"));
        }


    }
    public void approveAction(V2_PmOrder selectedPmOrder, Boolean agree,String comments) {
        selectedPmOrder.setCurrentStepId(selectedPmOrder.getCurrentStepId());
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
       // formData.setStepId("approval");
        formData.setStepId(selectedPmOrder.getCurrentStepId());
        String res = srApi.invokePmOrderAction(token,formData,agree?"approvalPass":"approvalReject",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"提交");
        }
    }

    public void dispatchWorkOrder(V2_PmOrder selectedPmOrder) {
        V2_PmOrder tmpwo=woDao.findById(selectedPmOrder.getId());
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setActionType("assign");
        formData.setAssignee(selectedPmOrder.getCurrentPersonId());
        formData.setStepId(selectedPmOrder.getCurrentStepId());
      //   formData.setIntExtType(selectedPmOrder.getIntExtType().toString());
        String res = srApi.invokePmOrderAction(token,formData,"assign",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("dispatchWorkOrder"));
        }
        selectedPmOrder.setCurrentStepId("accept");
    }


   /* public List<V2_PmOrder> getWorkOrdersBySR(String serviceRequest) {
        return woDao.findBySrId(serviceRequest);
    }*/

   public void approvePMorder(V2_PmOrder selectedPmOrder,String passreject){
       String token = UserContextService.getAccessToken();
       PmOrderActionForm formData = new PmOrderActionForm();
       formData.setActionType(passreject);
       formData.setStepId(selectedPmOrder.getCurrentStepId());

       String res = srApi.invokePmOrderAction(token,formData,passreject,selectedPmOrder.getId());

       if (res==null || !res.equalsIgnoreCase("Ok")) {
           WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"完成");
       }
   }
    public void repairWorkOrder(V2_PmOrder selectedPmOrder,List<V2_WorkOrder_Detail> accDetails, List<V2_WorkOrderCoworker> coDetails,Date startTime,Date endTime) {

        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setActionType("repair");
        formData.setStepId(selectedPmOrder.getCurrentStepId());


        formData.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime));
        formData.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endTime));
        formData.setManHours(selectedPmOrder.getManHours());
        selectedPmOrder.setCurrentStepName("保养中");
        formData.setDetails(accDetails);
        formData.setCoworkers(coDetails);
     /*   formData.setManHours(selectedWorkOrder.getManHours());
        formData.setOtherExpense(selectedWorkOrder.getOtherExpense());*/
        String res = srApi.invokePmOrderAction(token,formData,"repair",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"完成");
        }
    }

    public List<V2_ServiceRequest> getServiceRequestByAssetId(Integer id) {
        return srDao.findByAssetId(id);
    }

    public void acceptWorkOrder(V2_PmOrder selectedPmOrder, Date estimatedCloseTime) {
        //V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());

        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setActionType("accept");
formData.setStepId(selectedPmOrder.getCurrentStepId());//Accept
        String res = srApi.invokePmOrderAction(token,formData,"accept",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"接单");
        }else{
            WebUtil.addSuccessMessage("接单成功");
        }
        selectedPmOrder.setCurrentStepId("repair");
    }


    public List<UserAccount> getWorkerList(Integer assetId) {
        List<UserAccount> workerList = ucService.getWorkerList(assetId);
        return workerList;
    }
    
    public AssetInfo getAssetInfo(Integer assetId){
        return assetDao.findById(assetId);
    }



//Type：0-我的/1-同一group的/2-同院区的/3-其它院区的
/*public Page<V2_PmOrder> getWorkOrderListToPickup(int type,PageRequest pr, UserAccount ua) {
    Page<V2_PmOrder> pages = null;

    if (type == 0 || type == 1) {
        switch(type) {
            case 0: pages = woDao.fetchAvailableWorkOrderByUser(ua.getId(), pr);break;
            case 1: pages = woDao.fetchAvailableSameGroupWorkOrderByUser(ua.getId(),pr);
        }
        return pages;
    }
    Integer orgLevel = ua.getOrgLevel();
    if (orgLevel == null || orgLevel == 0)
        orgLevel = 3;
    List<SearchFilter> searchFilters = new ArrayList<>();
    switch (orgLevel) {
        case 1: searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, ua.getInstitutionUID()));break;
        case 2: searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, ua.getHospitalUID()));break;
        case 3: searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, ua.getSiteUID()));
    }

    searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
    searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 3));

    return woDao.findBySearchFilter(searchFilters, pr);
}*/
  /*  public List<V2_PmOrderCoworker> getWorkOrderCoworker(String woId) {
        return workOrderCoworkerRepository.findByWoId(woId);
    }*/



}

class PmOrderCreateForm{
    String assetUid;

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }
}

class PmOrderActionForm{
String actionType;
String assignee;
    List<V2_BlobObject> attachments;

    String desc;
String endTime;

Double manHours;
String startTime;
String stepId;
String qualityChecking;
    Object details;
    Object coworkers;



//V2_WorkOrder_Coworker[] coworkes //optional

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
}

