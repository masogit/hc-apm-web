/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.measuring.measuring;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.MeasureOrderActionForm;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
public class V2_MeasureService extends MicroServiceInvoker {
	private static final Logger log = LoggerFactory.getLogger(V2_MeasureService.class);
    @Autowired
    private ServiceRequestMsgRepository msgDao;
    @Autowired
    protected RestTemplate template;
    @Autowired
    private BiomedGroupRepository groupDao;
    @Autowired
    private V2_MeasureRepository woDao;
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
    private MeasureRequestApiService srApi;
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
    private RelRuleAssetRepository relRuleAssetRepository;
    @Autowired
    private UserAccountRepository userDao;
    @Autowired
    private TableDefinitionRepository tableDao;
    @Autowired
    private MeasureOrderRuleRepository meaOrderRuleRepository;
    @Autowired
    private UnstructuredDataCollectionRepository reportDao;
    @Autowired
    private ElectricSafetyDataRepository electricSafetyDataRepository;
    @Autowired
    private UnstructuredDataRepository unstructuredDataDao;
    @Autowired
    private MeasureOrderRuleCheckListRepository pmckLstDao;

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


    public MeasureOrderRule acqMeasureOrderRule(V2_MeasureOrder v2MeasureOrder){
        MeasureOrderRule measureOrderRule= meaOrderRuleRepository.findMeasureOrderRuleLevel3(v2MeasureOrder.getAssetUid());
        /* int level =  v2MeasureOrder.getRuleLevel();
        switch (level){
            case 3:{
                pmOrderRule= meaOrderRuleRepository.findMeasureOrderRuleLevel3(v2MeasureOrder.getAssetUid());break;
            }
            case 2:{
                pmOrderRule = meaOrderRuleRepository.findMeasureOrderRuleLevel2(v2MeasureOrder.getAssetUid());break;
            }
        }*/
        return measureOrderRule;
    }
    public List<TableDefinition> getTablesName(V2_MeasureOrder v2MeasureOrder){
        MeasureOrderRule measureOrderRule=acqMeasureOrderRule(v2MeasureOrder);
        if(measureOrderRule==null){
            return null;
        }
        List<TableDefinition> tableDefinitions = tableDao.findTableName(measureOrderRule.getUid());
        return tableDefinitions;
    }

    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return workOrderCoworkerRepository.findByWoId(woId);
    }
    public void closePM(V2_MeasureOrder selectedPmOrder,List<V2_BlobObject> picList ){
        String token = UserContextService.getAccessToken();
        MeasureOrderActionForm formData = new MeasureOrderActionForm();
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


    public void invokePmOerCancel(String desc, V2_MeasureOrder selectedPmOrder,String cancelInt){
        String token = UserContextService.getAccessToken();
        MeasureOrderActionForm formData = new MeasureOrderActionForm();
        //如果是工单是周期性的
        /*qualityChecking (string, optional):  1-继续生成保养, 2-不生成保养 ,*/
        if(selectedPmOrder.getMeasureType()==1){ //周期性
             if(cancelInt.equalsIgnoreCase("1")){
                 formData.setQualityChecking("1");
             }else{
                 formData.setQualityChecking("2");
             }
        }else{//一次性或重测
            formData.setQualityChecking("2");
        }

        formData.setDesc(desc);

        String res = srApi.invokeMeasureOrderCancel(token,formData,"cancel",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"提交");
        }else{
            WebUtil.addSuccessMessage("取消提交成功");
        }

    }

    public void saveReport(List<UnstructuredDataCollection> listReport){
    	reportDao.save(listReport);
    }
    
    public void submitWorkOrder(V2_MeasureOrder selectedPmOrder,List<V2_WorkOrder_Detail> accessoryList,String approvaler ,String desc) {
        String token = UserContextService.getAccessToken();
        MeasureOrderActionForm formData = new MeasureOrderActionForm();
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

    public Map<String, Object> createMeasureRequestApi(String assetUID,MeasureOrderActionForm formData){
        String token = UserContextService.getAccessToken();
        formData.setAssetUid(assetUID);
        return srApi.createMeasureRequest(token,formData);
    }

    //创建一次性保养
    public void createPMrequest(String assetUID, Date planTime) {
        MeasureOrderActionForm formData = new MeasureOrderActionForm();
        if (null != planTime) {
            formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(planTime));
        }
        //一次性
        formData.setMeasureType(2);
        createMeasureRequestApi(assetUID, formData);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetails(String woId) {
        return detailDao.findByWoId(woId);
    }
    public void rateWorkOrder(V2_MeasureOrder selectedPmOrder, Integer rating, String comments) {
        V2_MeasureOrder tmpwo=woDao.findById(selectedPmOrder.getId());
        String token = UserContextService.getAccessToken();
        MeasureOrderActionForm formData = new MeasureOrderActionForm();
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
    public void rejectWorkOrder(V2_MeasureOrder selectedPmOrder,String reason, Integer status, Date confirmedDowntime) {
        V2_MeasureOrder tmpwo=woDao.findById(selectedPmOrder.getId());
        String token = UserContextService.getAccessToken();
        MeasureOrderActionForm formData = new MeasureOrderActionForm();
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
    public void dispatchWorkOrderBatch(List<V2_MeasureOrder> pmList,Map<String,Boolean> hmPmOrder,String currentPersonIdBatch,Date planTimeBatch){
        String token = UserContextService.getAccessToken();
        List<V2_MeasureOrder> pmListTmp = new ArrayList<V2_MeasureOrder>();
        ArrayList<MeasureOrderActionForm> formDataList = new ArrayList<MeasureOrderActionForm>();

       //批量派工时 来自不同page的选择的  参考 acceptWorkOrderBatch 可以替换上面两端代码
        hmPmOrder.entrySet().stream().filter(item->item.getValue()).forEach(item->{
            MeasureOrderActionForm formData = new MeasureOrderActionForm();
            formData.setActionType("assign");
            formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd").format(planTimeBatch));
            formData.setStepId("Assign");
            formData.setAssignee(currentPersonIdBatch);
            formData.setMeasureOrderId(item.getKey());
            formDataList.add(formData);
            item.setValue(false);//将选中的true 设置为false 还原
        });

        String res = srApi.invokeMeasureOrderActionBatch(token,formDataList,"assign");
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"派单");
        }else{
            WebUtil.addSuccessMessage("批量派单成功");
        }
    }
    public void closePMBatch(List<V2_MeasureOrder> pmList,Map<String,Boolean> hmPmOrder,List<V2_BlobObject> picList ){
        String token = UserContextService.getAccessToken();
        List<V2_MeasureOrder> pmListTmp = new ArrayList<V2_MeasureOrder>();
        pmList.stream().forEach(item->{
            if(hmPmOrder.containsKey(item.getId()) && hmPmOrder.get(item.getId()) == true){
                //确保批量接单后，批量接单按钮是灰色
                hmPmOrder.put(item.getId(),false);
                //pmListTmp保存了所要提交的批量pmOrder
                pmListTmp.add(item);
            }
        });
        ArrayList<MeasureOrderActionForm> formDataList = new ArrayList<MeasureOrderActionForm>();
        pmListTmp.stream().forEach(item->{
            MeasureOrderActionForm formData = new MeasureOrderActionForm();
            formData.setAttachments(picList);
            formData.setMeasureOrderId(item.getId());
            formData.setActionType("close");
            formData.setStepId(item.getCurrentStepId());
            //批量关单的都是合格pm，所以设置为1
            formData.setQualityChecking("1");
            formDataList.add(formData);
        });
        String res = srApi.invokeMeasureOrderActionBatch(token,formDataList,"close");
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"提交");
        }else{
            WebUtil.addSuccessMessage("批量关单成功");
        }
    }
    public void dispatchWorkOrder(V2_MeasureOrder selectedPmOrder,Date planTime) {
        String token = UserContextService.getAccessToken();
        MeasureOrderActionForm formData = new MeasureOrderActionForm();
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
    public void reassignWorkOrderBatch(List<V2_MeasureOrder> pmList,Map<String,Boolean> hmPmOrder,String currentPersonIdBatch,String comments){
        String token = UserContextService.getAccessToken();
        List<V2_MeasureOrder> pmListTmp = new ArrayList<V2_MeasureOrder>();
        ArrayList<MeasureOrderActionForm> formDataList = new ArrayList<MeasureOrderActionForm>();
       /* pmList.stream().forEach(item->{
            if(hmPmOrder.containsKey(item.getId()) && hmPmOrder.get(item.getId()) == true){
                //确保批量接单后，批量接单按钮是灰色
                hmPmOrder.put(item.getId(),false);
                //pmListTmp保存了所要提交的批量pmOrder
                pmListTmp.add(item);
            }
        });

        pmListTmp.stream().forEach(item->{
            MeasureOrderActionForm formData = new MeasureOrderActionForm();
            formData.setActionType("reAssign");
            formData.setStepId(item.getCurrentStepId());
            formData.setAssignee(currentPersonIdBatch);
            formData.setPmOrderId(item.getId());
            formData.setDesc(comments);
            formDataList.add(formData);
        });*/

          //批量转单时 来自不同page的选择的  参考 acceptWorkOrderBatch ，可以替换上面两端代码
            hmPmOrder.entrySet().stream().filter(item->item.getValue()).forEach(item->{
            MeasureOrderActionForm formData = new MeasureOrderActionForm();
            formData.setActionType("reAssign");
            formData.setStepId("Maintain");
            formData.setAssignee(currentPersonIdBatch);
            formData.setMeasureOrderId(item.getKey());
            formDataList.add(formData);
            item.setValue(false);//将选中的true 设置为false 还原
        });

        String res = srApi.invokeMeasureOrderActionBatch(token,formDataList,"reAssign");
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"转单");
        }else{
            WebUtil.addSuccessMessage("批量转单成功");
        }
    }

    public List<MeasureOrderSteps> getMeasureOrderStep(String meaOrderId) {
        List<MeasureOrderSteps> pmOrderSteps = ucService.getMeasureOrderStepsList(meaOrderId);
        return pmOrderSteps == null ? new ArrayList<>() : pmOrderSteps;
    }

    public void approvePMorder(V2_MeasureOrder selectedPmOrder,List<V2_WorkOrder_Detail> accessList,String comments){
        String token = UserContextService.getAccessToken();
        MeasureOrderActionForm formData = new MeasureOrderActionForm();
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
    public void repairWorkOrder(V2_MeasureOrder selectedPmOrder,List<V2_WorkOrder_Detail> accDetails, List<V2_WorkOrderCoworker> coDetails,Date endTime,Date planTime,List<V2_BlobObject> attachment) {
        String token = UserContextService.getAccessToken();
        MeasureOrderActionForm formData = new MeasureOrderActionForm();
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
    public void repairWorkOrderBatch(Date endTime,Double totalManHours,List<V2_MeasureOrder> pmList,Map<String, Boolean> hmPmOrder) {
        String token = UserContextService.getAccessToken();
        List<V2_MeasureOrder> pmListTmp = new ArrayList<V2_MeasureOrder>();
        List<String> uidList = new ArrayList<String>();
        hmPmOrder.entrySet().stream().filter(item->item.getValue()).forEach(item->uidList.add(item.getKey()));
        List<V2_MeasureOrder> batchPmOrderList = woDao.getBatchPmOrderList(uidList);
        batchPmOrderList.stream().forEach(item->{
            if(hmPmOrder.containsKey(item.getId()) && hmPmOrder.get(item.getId()) == true){
                hmPmOrder.put(item.getId(),false);
            }
        });

        Double averageHours = totalManHours/batchPmOrderList.size();
        ArrayList<MeasureOrderActionForm> formDataList = new ArrayList<MeasureOrderActionForm>();
        batchPmOrderList.stream().forEach(item->{
            MeasureOrderActionForm formData = new MeasureOrderActionForm();
            formData.setMeasureOrderId(item.getId());
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

        String res= srApi.invokeMeasureOrderActionBatch(token,formDataList,"repair");
        if (res==null || !res.contains("OK")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"批量完成");
        }else{
            WebUtil.addSuccessMessage("批量完成成功");
        }
    }
    public void reassignWorkOrder(V2_MeasureOrder selectedPmOrder, String assigneeId,String comments) {
        String token = UserContextService.getAccessToken();
        MeasureOrderActionForm formData = new MeasureOrderActionForm();
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

    public void stagingPmrder(V2_MeasureOrder selectedPmOrder,List<V2_WorkOrder_Detail> accDetails, List<V2_WorkOrderCoworker> coDetails,Date endTime,Date planTime,List<V2_BlobObject> attachment) {
        String token = UserContextService.getAccessToken();
        MeasureOrderActionForm formData = new MeasureOrderActionForm();
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
    public void acceptWorkOrder(V2_MeasureOrder selectedPmOrder,Date planTime) {
        //V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        String token = UserContextService.getAccessToken();
        MeasureOrderActionForm formData = new MeasureOrderActionForm();
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
    public void acceptWorkOrderBatch(List<V2_MeasureOrder> pmList,Map<String,Boolean> hmPmOrder,Date planTimeBatch,int type) {
        String token = UserContextService.getAccessToken();
        List<V2_MeasureOrder> pmListTmp = new ArrayList<V2_MeasureOrder>();
        ArrayList<MeasureOrderActionForm> formDataList = new ArrayList<MeasureOrderActionForm>();
        hmPmOrder.entrySet().stream().filter(item->item.getValue()).forEach(item->{
            V2_MeasureOrder v2 = woDao.findById(item.getKey());

            MeasureOrderActionForm formData = new MeasureOrderActionForm();
            formData.setActionType("accept");
            formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd").format(planTimeBatch));
            formData.setStepId(v2.getCurrentStepId());
            formData.setMeasureOrderId(item.getKey());
            formDataList.add(formData);
            item.setValue(false);//将选中的true 设置为false 还原
        });
        String res=srApi.invokeMeasureOrderActionBatch(token,formDataList,"accept");
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

            for(V2_MeasureOrder item:pmListTmp){
                item.setCurrentStepId("repair");
            }
        }
    }
    public List<UserAccount> getWorkerList(Integer assetId) {
        return ucService.getWorkerList(assetId,"");
    }
    public List<UserAccount> getApproveList(String assetUid) {
        return ucService.getApproveList(assetUid);
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
    
    public V2_MeasureOrder findById(String id){
    	return woDao.findById(id);
    }

    private List<MeasureOrderRuleCheckList> getPmRuleCLClosed(V2_MeasureOrder v2MeasureOrder, String ruleUid){
        /*   unsDC:bo_uuid(工单)-->unsDC:id====>unsDC:id/unsD:collecitoId-->key
        * key就是检查项uid*/
        List<MeasureOrderRuleCheckList> pmcl =new ArrayList<MeasureOrderRuleCheckList>();
        UnstructuredDataCollection unsD = reportDao.findCKByUId(v2MeasureOrder.getId());
        if(unsD==null){
            return pmcl;
        }
        List<UnstructuredData> byCollectionId = unstructuredDataDao.findByCollectionId(unsD.getId());
        List<String> strList= new ArrayList<>();
        byCollectionId.forEach(item->strList.add(item.getKey()));
        if(strList.isEmpty()){
            strList.add("");
        }
        pmcl = pmckLstDao.findPMCL(strList,ruleUid);
        return pmcl;
    }

    public List<MeasureOrderRuleCheckList> getPmOrderRuleCheckLists(V2_MeasureOrder v2MeasureOrder, MeasureOrderRule pmOrderRule) {
        if (v2MeasureOrder.isFinished()) {
            return getPmRuleCLClosed(v2MeasureOrder, pmOrderRule.getUid());
        } else {
            return pmckLstDao.findAllByRuleUID(pmOrderRule.getUid());
        }
    }

    public Map<String, UnstructuredData> fetchHmCheck(List<MeasureOrderRuleCheckList> pmCheckItemList, V2_MeasureOrder v2MeasureOrder) {
        Map<String, UnstructuredData> hmCheck = new HashMap<>();
        if (pmCheckItemList != null && !pmCheckItemList.isEmpty()) {
            List<UnstructuredDataCollection> unDataCollection = reportDao.findCKByBoUuid(v2MeasureOrder.getId(), "pm-checklist-report");
            if (unDataCollection != null && !unDataCollection.isEmpty()) {
                UnstructuredDataCollection unCollection = unDataCollection.get(0);
                if (unCollection != null) {
                    List<UnstructuredData> dataList = unCollection.getData();
                    for (MeasureOrderRuleCheckList aPmCheckItemList : pmCheckItemList) {
                        for (UnstructuredData unsData : dataList) {
                            if (StringUtils.equalsIgnoreCase(unsData.getKey(), aPmCheckItemList.getUid())) {
                                hmCheck.put(aPmCheckItemList.getUid(), unsData);
                            }
                        }
                    }
                }
            }
        }
        return hmCheck;
    }
}

