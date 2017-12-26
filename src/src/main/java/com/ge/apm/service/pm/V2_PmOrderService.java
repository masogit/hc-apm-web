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
import com.ge.apm.web.PmOrderActionForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import webapp.framework.web.WebUtil;

import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    private PmOrderRequestApiService pmApi;

    @Autowired
    private UserAccountRepository userDao;


    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return workOrderCoworkerRepository.findByWoId(woId);
    }

    public void closePM(V2_PmOrder selectedPmOrder,List<V2_BlobObject> picList ){
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setAttachments(picList);
        formData.setActionType("close");
        formData.setStepId(selectedPmOrder.getCurrentStepId());
      formData.setQualityChecking(selectedPmOrder.getQualityChecking());
        String res = srApi.invokePmOrderAction(token,formData,"close",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"提交");
        }else{
            WebUtil.addSuccessMessage("关单成功");
        }
    }
    public void submitWorkOrder(V2_PmOrder selectedPmOrder,List<V2_WorkOrder_Detail> accessoryList,String approvaler ,String desc) {
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setActionType("approval");
        formData.setDesc(desc);
        formData.setStepId(selectedPmOrder.getCurrentStepId());
formData.setAssignee(approvaler);
        formData.setDetails(accessoryList);
        String res = srApi.invokePmOrderAction(token,formData,"approval",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"提交");
        }else{
            WebUtil.addSuccessMessage("审批成功");
        }
    }
    public void createPMrequestApi(String assetUID,PmOrderActionForm formData){
        String token = UserContextService.getAccessToken();
        formData.setAssetUid(assetUID);
        srApi.createPmRequest(token,formData);
    }
    
    public void createPMrequest(String assetUID, Date planTime) {
        PmOrderActionForm formData = new PmOrderActionForm();
        if (null != planTime) {
            formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(planTime));
        }
        createPMrequestApi(assetUID, formData);
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
        }else{
            WebUtil.addSuccessMessage("操作成功");
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
        }else{
            WebUtil.addSuccessMessage("操作成功");
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
        }else{
            WebUtil.addSuccessMessage("审批成功");
        }
    }

    public void dispatchWorkOrder(V2_PmOrder selectedPmOrder,Date planTime) {
        V2_PmOrder tmpwo=woDao.findById(selectedPmOrder.getId());
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setActionType("assign");
        formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(planTime));
        formData.setAssignee(selectedPmOrder.getCurrentPersonId());
        formData.setStepId(selectedPmOrder.getCurrentStepId());
      //   formData.setIntExtType(selectedPmOrder.getIntExtType().toString());
        String res = srApi.invokePmOrderAction(token,formData,"assign",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("dispatchWorkOrder"));
        }else{
            WebUtil.addSuccessMessage("派工成功");
        }
        selectedPmOrder.setCurrentStepId("accept");

    }

public List<PmOrderSteps> getPmOrderStep(String pmOrderId) {
   // String token = UserContextService.getAccessToken();
    //PmOrderActionForm formData = new PmOrderActionForm();
    List<PmOrderSteps> pmOrderStepsList = ucService.getPmOrderStepsList(pmOrderId);
    return pmOrderStepsList;

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
       }else{
           WebUtil.addSuccessMessage("审批成功");
       }
   }
    public void repairWorkOrder(V2_PmOrder selectedPmOrder,List<V2_WorkOrder_Detail> accDetails, List<V2_WorkOrderCoworker> coDetails,Date endTime,Date planTime,List<V2_BlobObject> attachment) {


        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setActionType("repair");
        formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(planTime));
        formData.setStepId(selectedPmOrder.getCurrentStepId());
formData.setAttachments(attachment);
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
        }else{
            WebUtil.addSuccessMessage("操作成功");
        }
    }


    public void reassignWorkOrder(V2_PmOrder selectedPmOrder, String assigneeId,String comments) {
        String token = UserContextService.getAccessToken();


        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setActionType("reAssign");
      //  UserAccount userAccount = userDao.getById(Integer.parseInt(assigneeId));
        formData.setStepId(selectedPmOrder.getCurrentStepId());
        formData.setAssignee(assigneeId);
        formData.setDesc(comments);
        String res = srApi.invokePmOrderAction(token,formData,"reAssign",selectedPmOrder.getId());
        if (res==null || !res.contains("ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"转单");
        }
    }



    public void stagingPmrder(V2_PmOrder selectedPmOrder,List<V2_WorkOrder_Detail> accDetails, List<V2_WorkOrderCoworker> coDetails,Date endTime,Date planTime) {
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setActionType("tempRepair");
        formData.setStepId(selectedPmOrder.getCurrentStepId());
        if(endTime !=null)
         formData.setEndTime(new SimpleDateFormat("yyyy-MM-dd").format(endTime));
        if(planTime !=null)
            formData.setEndTime(new SimpleDateFormat("yyyy-MM-dd").format(planTime));

        formData.setManHours(selectedPmOrder.getManHours());
        selectedPmOrder.setCurrentStepName("暂时保存");
        formData.setDetails(accDetails);
        formData.setCoworkers(coDetails);
        String res = srApi.invokePmOrderAction(token,formData,"tempRepair",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"完成");
        }else{
            WebUtil.addSuccessMessage("暂存成功");
        }
    }
    public List<V2_ServiceRequest> getServiceRequestByAssetId(Integer id) {
        return srDao.findByAssetId(id);
    }

    public void acceptWorkOrder(V2_PmOrder selectedPmOrder, Date estimatedCloseTime,Date planTime) {
        //V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());

        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setActionType("accept");
        formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd").format(planTime));
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

    public List<UserAccount> getApproveList(String assetUid) {
        List<UserAccount> workerList = ucService.getApproveList(assetUid);
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

    public UserAccountRepository getUserDao() {
        return userDao;
    }

    public void setUserDao(UserAccountRepository userDao) {
        this.userDao = userDao;
    }
}


