/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.pm;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BiomedGroupRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.ElectricSafetyDataRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.PmOrderRuleRepository;
import com.ge.apm.dao.RelRuleAssetRepository;
import com.ge.apm.dao.ServiceRequestMsgRepository;
import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.dao.UnstructuredDataCollectionRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.dao.WorkOrderCoworkerRepository;
import com.ge.apm.dao.WorkOrderDetailRepository;
import com.ge.apm.dao.WorkOrderStepRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.ElectricSafetyData;
import com.ge.apm.domain.PmOrderRule;
import com.ge.apm.domain.ServiceRequestMsg;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.UnstructuredDataCollection;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.V2_PmOrder;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_WorkOrderCoworker;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.domain.pure.TableSchema;
import com.ge.apm.service.common.UserCommonService;
/*import com.ge.apm.service.wo.PmOrderActionForm;
import com.ge.apm.service.wo.WorkOrderForm;*/
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.PmOrderActionForm;

import webapp.framework.web.WebUtil;
/**
 *
 * @author 212579464
 */
@Component
public class V2_PmOrderService extends MicroServiceInvoker {
	private static final Logger log = LoggerFactory.getLogger(V2_PmOrderService.class);
    @Autowired
    private ServiceRequestMsgRepository msgDao;
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
    private RelRuleAssetRepository relRuleAssetRepository;
    @Autowired
    private UserAccountRepository userDao;
    @Autowired
    private TableDefinitionRepository tableDao;
    @Autowired
    private PmOrderRuleRepository pmOrderRuleRepository;
    @Autowired
    private UnstructuredDataCollectionRepository reportDao;
    @Autowired
    private ElectricSafetyDataRepository electricSafetyDataRepository;

    public String updateWorkOrderDetailPrice(String token, V2_WorkOrder_Detail workOrderDetail) {
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



    public void updateAccessPrice(V2_WorkOrder_Detail workOrderDetail){
        String token = UserContextService.getAccessToken();
        String res = srApi.updateWorkOrderDetailPrice(token,workOrderDetail);
        if (res == null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", WebUtil.getMessage("Cancel")));
        }
    }


    public PmOrderRule acqPmOrderRule(V2_PmOrder v2PmOrder){
        PmOrderRule pmOrderRule=null;
        int level =  v2PmOrder.getRuleLevel();
        switch (level){
            case 3:{
                pmOrderRule= pmOrderRuleRepository.findPmOrderRuleLevel3(v2PmOrder.getAssetUid());break;
            }
            case 2:{
                pmOrderRule = pmOrderRuleRepository.findPmOrderRuleLevel2(v2PmOrder.getAssetUid());break;
            }
        }
        return pmOrderRule;
    }
    public List<TableDefinition> getTablesName(V2_PmOrder v2PmOrder){
        PmOrderRule pmOrderRule=acqPmOrderRule(v2PmOrder);
        if(pmOrderRule==null){
            return null;
        }
        List<TableDefinition> tableDefinitions = tableDao.findTableName(pmOrderRule.getUid());
        return tableDefinitions;
    }

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

    public List<UnstructuredDataCollection> getReportList(String boUid) {
        return reportDao.findByBoUuidNoPMCheckList(boUid);
    }
    
    public void saveReport(UnstructuredDataCollection report){
    	reportDao.save(report);
    }
    
    public void saveReport(List<UnstructuredDataCollection> listReport){
    	reportDao.save(listReport);
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
            WebUtil.addSuccessMessage("审批提交成功");
        }
    }

    public Map<String, Object> createPMrequestApi(String assetUID,PmOrderActionForm formData){
        String token = UserContextService.getAccessToken();
        formData.setAssetUid(assetUID);
        return srApi.createPmRequest(token,formData);
    }

    //创建一次性保养
    public void createPMrequest(String assetUID, Date planTime) {
        PmOrderActionForm formData = new PmOrderActionForm();
        if (null != planTime) {
            formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(planTime));
        }
        //一次性
        formData.setPmType(2);
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
    public void dispatchWorkOrderBatch(List<V2_PmOrder> pmList,Map<String,Boolean> hmPmOrder,String currentPersonIdBatch,Date planTimeBatch){
        String token = UserContextService.getAccessToken();
        List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
        pmList.stream().forEach(item->{
            if(hmPmOrder.containsKey(item.getId()) && hmPmOrder.get(item.getId()) == true){
                //确保批量接单后，批量接单按钮是灰色
                hmPmOrder.put(item.getId(),false);
                //pmListTmp保存了所要提交的批量pmOrder
                pmListTmp.add(item);
            }
        });
        ArrayList<PmOrderActionForm> formDataList = new ArrayList<PmOrderActionForm>();
        pmListTmp.stream().forEach(item->{
            PmOrderActionForm formData = new PmOrderActionForm();
            formData.setActionType("assign");
            formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(planTimeBatch));
            formData.setAssignee(currentPersonIdBatch);
            formData.setStepId(item.getCurrentStepId());
            formData.setPmOrderId(item.getId());
            formDataList.add(formData);
        });
        String res = srApi.invokePmOrderActionBatch(token,formDataList,"assign");
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"派单");
        }else{
            WebUtil.addSuccessMessage("批量派单成功");
        }
    }
    public void closePMBatch(List<V2_PmOrder> pmList,Map<String,Boolean> hmPmOrder,List<V2_BlobObject> picList ){
        String token = UserContextService.getAccessToken();
        List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
        pmList.stream().forEach(item->{
            if(hmPmOrder.containsKey(item.getId()) && hmPmOrder.get(item.getId()) == true){
                //确保批量接单后，批量接单按钮是灰色
                hmPmOrder.put(item.getId(),false);
                //pmListTmp保存了所要提交的批量pmOrder
                pmListTmp.add(item);
            }
        });
        ArrayList<PmOrderActionForm> formDataList = new ArrayList<PmOrderActionForm>();
        pmListTmp.stream().forEach(item->{
            PmOrderActionForm formData = new PmOrderActionForm();
            formData.setAttachments(picList);
            formData.setPmOrderId(item.getId());
            formData.setActionType("close");
            formData.setStepId(item.getCurrentStepId());
            formData.setPmOrderId(item.getId());
            //批量关单的都是合格pm，所以设置为1
            formData.setQualityChecking("1");
            formDataList.add(formData);
        });
        String res = srApi.invokePmOrderActionBatch(token,formDataList,"close");
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"提交");
        }else{
            WebUtil.addSuccessMessage("批量关单成功");
        }
    }
    public void dispatchWorkOrder(V2_PmOrder selectedPmOrder,Date planTime) {
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
    public void reassignWorkOrderBatch(List<V2_PmOrder> pmList,Map<String,Boolean> hmPmOrder,String currentPersonIdBatch,String comments){
        String token = UserContextService.getAccessToken();
        List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
        pmList.stream().forEach(item->{
            if(hmPmOrder.containsKey(item.getId()) && hmPmOrder.get(item.getId()) == true){
                //确保批量接单后，批量接单按钮是灰色
                hmPmOrder.put(item.getId(),false);
                //pmListTmp保存了所要提交的批量pmOrder
                pmListTmp.add(item);
            }
        });
        ArrayList<PmOrderActionForm> formDataList = new ArrayList<PmOrderActionForm>();
        pmListTmp.stream().forEach(item->{
            PmOrderActionForm formData = new PmOrderActionForm();
            formData.setActionType("reAssign");
            formData.setStepId(item.getCurrentStepId());
            formData.setAssignee(currentPersonIdBatch);
            formData.setPmOrderId(item.getId());
            formData.setDesc(comments);
            formDataList.add(formData);
        });
        String res = srApi.invokePmOrderActionBatch(token,formDataList,"reAssign");
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"转单");
        }else{
            WebUtil.addSuccessMessage("批量转单成功");
        }
    }
    public List<PmOrderSteps> getPmOrderStep(String pmOrderId) {
        // String token = UserContextService.getAccessToken();
        //PmOrderActionForm formData = new PmOrderActionForm();
        List<PmOrderSteps> pmOrderStepsList = ucService.getPmOrderStepsList(pmOrderId);
        return pmOrderStepsList;
    }

    public void approvePMorder(V2_PmOrder selectedPmOrder,List<V2_WorkOrder_Detail> accessList,String comments){
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        String actionType =null;
        if(accessList.stream().allMatch(item->item.getApproved().toString().equalsIgnoreCase("true")))
            actionType="pass";
        else if(accessList.stream().allMatch(item->item.getApproved().toString().equalsIgnoreCase("false")))
            actionType="reject";
        else
            actionType="partial";
        formData.setActionType(actionType);
        formData.setStepId(selectedPmOrder.getCurrentStepId());
        formData.setDesc(comments);
        formData.setDetails(accessList);
        String res = srApi.invokePmOrderAction(token,formData,"审批",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"完成");
        }else{
            WebUtil.addSuccessMessage("审批成功");
        }
    }
    public void repairWorkOrder(V2_PmOrder selectedPmOrder,List<V2_WorkOrder_Detail> accDetails, List<V2_WorkOrderCoworker> coDetails,Date endTime,Date planTime,List<V2_BlobObject> attachment) {
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        if(selectedPmOrder.getSimplify()!=null && selectedPmOrder.getSimplify()==true){
            formData.setActionType("simplify");
        }else{
            formData.setActionType("repair");
        }
        formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(planTime));
        formData.setStepId(selectedPmOrder.getCurrentStepId());
        formData.setAttachments(attachment);
        if(endTime!=null)
            formData.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endTime));
        formData.setManHours(selectedPmOrder.getManHours());
        selectedPmOrder.setCurrentStepName("保养中");

        accDetails.stream().forEach(item->{
            if(item.getArrivedTime()==null){
                item.setArrivedTime((new Date()));
            }

        });

        formData.setDetails(accDetails);
        formData.setCoworkers(coDetails);
        formData.setQualityChecking(selectedPmOrder.getQualityChecking());
        String res = srApi.invokePmOrderAction(token,formData,"保养",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"完成");
        }else{
            WebUtil.addSuccessMessage("操作成功");
        }
    }
    public void repairWorkOrderBatch(Date endTime,Double totalManHours,List<V2_PmOrder> pmList,Map<String, Boolean> hmPmOrder) {
        String token = UserContextService.getAccessToken();
        List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
        pmList.stream().forEach(item->{
            if(hmPmOrder.containsKey(item.getId()) && hmPmOrder.get(item.getId()) == true){
                hmPmOrder.put(item.getId(),false);
                pmListTmp.add(item);
            }
        });
        Double averageHours = totalManHours/pmListTmp.size();
        ArrayList<PmOrderActionForm> formDataList = new ArrayList<PmOrderActionForm>();
        pmListTmp.stream().forEach(item->{
            PmOrderActionForm formData = new PmOrderActionForm();
            formData.setPmOrderId(item.getId());
            if(item.getSimplify()!=null && item.getSimplify()==true){
                formData.setActionType("simplify");
            }else{
                formData.setActionType("repair");
            }
            formData.setQualityChecking("1");
            formData.setStepId(item.getCurrentStepId());
            formData.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endTime));
            //小数点后保留两位小数
            formData.setManHours(Math.round(averageHours*100.0)/100.0);
            formData.setPlanTime(item.getPlanTime().toString());
            item.setCurrentStepName("保养中");
            formDataList.add(formData);
        });
        String res= srApi.invokePmOrderActionBatch(token,formDataList,"repair");
        if (res==null || !res.contains("OK")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"批量完成");
        }else{
            WebUtil.addSuccessMessage("批量完成成功");
        }
    }
    public void reassignWorkOrder(V2_PmOrder selectedPmOrder, String assigneeId,String comments) {
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setActionType("reAssign");
        formData.setStepId(selectedPmOrder.getCurrentStepId());
        formData.setAssignee(assigneeId);
        formData.setDesc(comments);
        String res = srApi.invokePmOrderAction(token,formData,"转单",selectedPmOrder.getId());
        if (res==null || !res.contains("OK")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"转单");
        }else{
            WebUtil.addSuccessMessage("转单成功");
        }
    }

    public void stagingPmrder(V2_PmOrder selectedPmOrder,List<V2_WorkOrder_Detail> accDetails, List<V2_WorkOrderCoworker> coDetails,Date endTime,Date planTime,List<V2_BlobObject> attachment) {
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setAttachments(attachment);
        formData.setActionType("tempRepair");
        formData.setStepId(selectedPmOrder.getCurrentStepId());
        if(endTime !=null)
            formData.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endTime));
        if(planTime !=null)
            formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd").format(planTime));
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
    public void acceptWorkOrder(V2_PmOrder selectedPmOrder,Date planTime) {
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
    public void acceptWorkOrderBatch(List<V2_PmOrder> pmList,Map<String,Boolean> hmPmOrder,Date planTimeBatch,int type) {
        String token = UserContextService.getAccessToken();
        List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
        pmList.stream().forEach(item->{
            if(hmPmOrder.containsKey(item.getId()) && hmPmOrder.get(item.getId()) == true){
                //确保批量接单后，批量接单按钮是灰色
                hmPmOrder.put(item.getId(),false);
                //pmListTmp保存了所要提交的批量pmOrder
                pmListTmp.add(item);
            }
        });
        ArrayList<PmOrderActionForm> formDataList = new ArrayList<PmOrderActionForm>();
        pmListTmp.stream().forEach(item->{
            PmOrderActionForm formData = new PmOrderActionForm();
            formData.setActionType("accept");
            formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd").format(planTimeBatch));
            formData.setStepId(item.getCurrentStepId());//Accept
            formData.setPmOrderId(item.getId());
            formDataList.add(formData);
        });
        String res = srApi.invokePmOrderActionBatch(token,formDataList,"accept");
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            if(type==1)
                WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"接单");
            else
                WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"抢单");

        }else{
            if(type==1)
                WebUtil.addSuccessMessage("批量接单成功");
            else
                WebUtil.addSuccessMessage("批量抢单成功");

            for(V2_PmOrder item:pmListTmp){
                item.setCurrentStepId("repair");
            }
        }
    }
    public List<UserAccount> getWorkerList(Integer assetId) {
        List<UserAccount> workerList = ucService.getWorkerList(assetId,"");
        return workerList;
    }
    public List<UserAccount> getApproveList(String assetUid) {
        List<UserAccount> workerList = ucService.getApproveList(assetUid);
        return workerList;
    }
    public AssetInfo getAssetInfo(Integer assetId){
        return assetDao.findById(assetId);
    }
    public UserAccountRepository getUserDao() {
        return userDao;
    }
    public void setUserDao(UserAccountRepository userDao) {
        this.userDao = userDao;
    }
    public List<ServiceRequestMsg> getServiceRequestMessageList(String id) {
        return msgDao.findBySrIdOrderBySendTime(id);
    }
    
    public V2_PmOrder findById(String id){
    	return woDao.findById(id);
    }
}

