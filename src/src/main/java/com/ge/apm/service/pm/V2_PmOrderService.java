/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.pm;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.WorkOrderStatus;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.PmOrderActionForm;
import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@Component
@Lazy(false)
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
    @Autowired
    private UnstructuredDataRepository unstructuredDataDao;
    @Autowired
    private PmOrderRuleCheckListRepository pmckLstDao;
    @Autowired
    private V2_PmOrderStepRepository pmOrderStepRepository;

    @PostConstruct
    public void initialize(){
        setPyNameCode();
    }

    private void setPyNameCode(){
        CompletableFuture.runAsync(()->{
            long o = System.currentTimeMillis();
            log.info("pmOrderRules start:{}", o);
            List<SearchFilter> searchFilters = new ArrayList<>();
            long count = pmOrderRuleRepository.count();
            log.info("pmOrderRules size:{}",count);
            if(count > 0){
                searchFilters.add(new SearchFilter("namePyCode", SearchFilter.Operator.IsNull,null));
                searchFilters.add(new SearchFilter("name", SearchFilter.Operator.IsNotNull,null));
                Page<PmOrderRule> page = null;
                while ((page = pmOrderRuleRepository.findBySearchFilter(searchFilters, new PageRequest(0, 500))).getTotalElements() > 0){
                    for (PmOrderRule pmOrderRule : page) {
                         pmOrderRule.setName(pmOrderRule.getName());
                    }
                    pmOrderRuleRepository.save(page);

                }
            }
            log.info("pmOrderRules end:{}",System.currentTimeMillis()-o);
        });
    }
    /*
    *1 查看工单时候否pm_performance_report报告 , 再查看该报告的属性值有没有被填写即对应的unstrucData中有没有数据
    * 2 查看工单时候否pm_safty_report报告 , 再查看该报告的属性值有没有被填写即对应的unstrucData中有没有数据
    * */
    public HashMap<String,String> fillInform(V2_PmOrder v2PmOrder){
        HashMap<String, String> hm = new HashMap<String, String>();
          //默认false表示不用提示用户填写报告信息
           hm.put("pmReportNeedFill","");
           hm.put("saftyReportNeedFill","");
        UnstructuredDataCollection reporter;
        reporter=reportDao.findPerformanceReport(v2PmOrder.getId());
        if(null != reporter) {
            //unstructuredDataCollection如果在unstructuredData没有对应值 说明工单的性能或安全报告没有填写信息
            if(CollectionUtils.isEmpty(reporter.getData())){
                //获取保养报告的名字
                TableDefinition byBaseId = tableDao.findById(reporter.getSchemaId());
                if(byBaseId == null){
                    WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", WebUtil.getMessage("MaintainReportNotExist")));
                } else {
                    hm.put("pmReportNeedFill",byBaseId.getName());
                }
            }
        }
        reporter =reportDao.findSaftyReport(v2PmOrder.getId());
        if(null != reporter) {
            TableDefinition byBaseId = tableDao.findById(reporter.getSchemaId());
            if(byBaseId == null){
                WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail", WebUtil.getMessage("MaintainReportNotExist")));
            } else {
                if(CollectionUtils.isEmpty(reporter.getData())){
                    hm.put("saftyReportNeedFill",byBaseId.getName());
                }
            }
        }
        return hm;
    }

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

    public List<Integer> getMvsAssectUids(String token) {
        String gatewayURL = getGatewayURL(GatewayURL.GET_MVSASSECT_UID);
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        ResponseEntity<ArrayList> response = template.exchange(gatewayURL, HttpMethod.GET, requestEntity, ArrayList.class);
        if (response.getBody() != null && response.getStatusCode().equals(HttpStatus.OK)) {
            ArrayList<Integer> mvsAssetUidsByVisibleScope = (ArrayList<Integer>) response.getBody();
            if(mvsAssetUidsByVisibleScope.isEmpty()){
                return new ArrayList<Integer>();
            }
            return mvsAssetUidsByVisibleScope;
        } else {
            return new ArrayList<Integer>();
        }
    }

    public List<String> getMvsAssectUidsStr(String token) {
        String gatewayURL = getGatewayURL(GatewayURL.GET_MVSASSECT_UID_STR);
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        ResponseEntity<ArrayList> response = template.exchange(gatewayURL, HttpMethod.GET, requestEntity, ArrayList.class);
        if (response.getBody() != null && response.getStatusCode().equals(HttpStatus.OK)) {
            ArrayList<String> mvsAssetUidsByVisibleScope = (ArrayList<String>) response.getBody();
            if(mvsAssetUidsByVisibleScope.isEmpty()){
                return new ArrayList<String>();
            }
            return mvsAssetUidsByVisibleScope;
        } else {
            return new ArrayList<String>();
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
            case 1:{
                pmOrderRule= pmOrderRuleRepository.findPmOrderRuleLevel1(v2PmOrder.getAssetUid());break;
            }
            case 4:{
                pmOrderRule = pmOrderRuleRepository.findPmOrderRuleLevel4(v2PmOrder.getAssetUid());
                //rule_uid4 存在多个规则,此时v2_pm_order表中会保存本工单实际绑定的保养规则(可增加流程配置项中是否启用质控绑定多个规则限定条件。但是若中途再修改流程配置项会影响正常流程，此处逻辑需产品经理确认？)
                if(pmOrderRule == null){
                     pmOrderRule = pmOrderRuleRepository.findByUid(v2PmOrder.getPmRuleUid());
                }
                break;
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
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Submit"));
        }else{
            WebUtil.addSuccessMessage(WebUtil.getMessage("CloseSuccess"));
        }
    }

    public List<UnstructuredDataCollection> getReportList(String boUid) {
        return reportDao.findByBoUuidNoPMCheckList(boUid);
    }
    
    public void saveReport(UnstructuredDataCollection report){
    	reportDao.save(report);
    }

    /*  一次性单子 qualityChecking就是2，如果是周期性的 再根据cancelInt来设置qualityChecking */
    public void cancelPmBatch(Map<String, Boolean> hmPmOrder,String cancelInt){
        String token = UserContextService.getAccessToken();
        List<String> uidList = new ArrayList<String>();
        ArrayList<PmOrderActionForm> formDataList = new ArrayList<PmOrderActionForm>();
        hmPmOrder.entrySet().stream().filter(item->item.getValue()).forEach(item->uidList.add(item.getKey()));
        List<V2_PmOrder> batchPmOrderList = woDao.getBatchPmOrderList(uidList);
        batchPmOrderList.stream().forEach(item->{
            if(hmPmOrder.containsKey(item.getId()) && hmPmOrder.get(item.getId()) == true){
                hmPmOrder.put(item.getId(),false);
            }
        });

        batchPmOrderList.stream().forEach(item->{
            PmOrderActionForm formData = new PmOrderActionForm();
            formData.setPmOrderId(item.getId());

            formData.setQualityChecking("1");
            if(item.getPmType()==1){
                if(cancelInt.equalsIgnoreCase("1")){
                    formData.setQualityChecking("1");
                }else{
                    formData.setQualityChecking("2");
                }
            }else{
                formData.setQualityChecking("2");
            }


            formData.setStepId(item.getCurrentStepId());
            formDataList.add(formData);
        });

        String res= srApi.invokePmOrderActionBatch(token,formDataList,"repair");
        if (res==null || !res.contains("OK")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("BatchCancel"));
        }else{
            WebUtil.addSuccessMessage(WebUtil.getMessage("BatchCancelSuccess"));
        }
    }

    public void invokePmOerCancel(String desc, V2_PmOrder selectedPmOrder,String cancelInt){
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        //如果是工单是周期性的
        /*qualityChecking (string, optional):  1-继续生成保养, 2-不生成保养 ,*/
        if(selectedPmOrder.getPmType()==1){ //周期性
             if(cancelInt.equalsIgnoreCase("1")){
                 formData.setQualityChecking("1");
             }else{
                 formData.setQualityChecking("2");
             }
        }else{//一次性或重测
            formData.setQualityChecking("2");
        }

        formData.setDesc(desc);

        String res = srApi.invokePmOrderCancel(token,formData,"cancel",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Submit"));
        }else{
            WebUtil.addSuccessMessage(WebUtil.getMessage("SubmissionSuccessfullyCanceled"));
        }

    }

    //cancelInt: 1 取消当前工单或者 2 取消取消周期性工单
    public void invokeBatchPmOrderCancel(String desc, Map<String, Boolean> hmPmOrder,String cancelInt){
        String token = UserContextService.getAccessToken();
        //PmOrderActionForm formData = new PmOrderActionForm();
        List<String> uidList = new ArrayList<String>();
        ArrayList<PmOrderActionForm> formDataList = new ArrayList<PmOrderActionForm>();

        //如果是工单是周期性的
        /*qualityChecking (string, optional):  1-继续生成保养, 2-不生成保养 ,*/
        hmPmOrder.entrySet().stream().filter(item->item.getValue()).forEach(item->uidList.add(item.getKey()));
        List<V2_PmOrder> batchPmOrderList = woDao.getBatchPmOrderList(uidList);

        batchPmOrderList.stream().forEach(item->{
            if(hmPmOrder.containsKey(item.getId()) && hmPmOrder.get(item.getId()) == true){
                hmPmOrder.put(item.getId(),false);
            }
        });


        batchPmOrderList.stream().forEach(item->{
            PmOrderActionForm formData = new PmOrderActionForm();
            formData.setPmOrderId(item.getId());


            if(item.getPmType()==1){ //周期性
                if(cancelInt.equalsIgnoreCase("1")){
                    formData.setQualityChecking("1");
                }else{
                    formData.setQualityChecking("2");
                }
            }else{//一次性或重测
                formData.setQualityChecking("2");
            }
            formData.setStepId(item.getCurrentStepId());
            formDataList.add(formData);
            formData.setDesc(desc);
        });

        String res = srApi.invokeBatchPmOrderCancel(token,formDataList,"cancel");
        if (res==null || !res.equalsIgnoreCase("ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Submit"));
        }else{
            WebUtil.addSuccessMessage(WebUtil.getMessage("SubmissionSuccessfullyCanceled"));
        }

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
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Submit"));
        }else{
            WebUtil.addSuccessMessage(WebUtil.getMessage("SubmissionForApprovalSuccess"));
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
            WebUtil.addSuccessMessage(WebUtil.getMessage("OperationSuccess"));
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
            WebUtil.addSuccessMessage(WebUtil.getMessage("OperationSuccess"));
        }

    }
    public void dispatchWorkOrderBatch(List<V2_PmOrder> pmList,Map<String,Boolean> hmPmOrder,String currentPersonIdBatch,Date planTimeBatch){
        String token = UserContextService.getAccessToken();
        List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
        ArrayList<PmOrderActionForm> formDataList = new ArrayList<PmOrderActionForm>();
       /* pmList.stream().forEach(item->{
            if(hmPmOrder.containsKey(item.getId()) && hmPmOrder.get(item.getId()) == true){
                //确保批量接单后，批量接单按钮是灰色
                hmPmOrder.put(item.getId(),false);
                //pmListTmp保存了所要提交的批量pmOrder
                pmListTmp.add(item);
            }
        });

        pmListTmp.stream().forEach(item->{
            PmOrderActionForm formData = new PmOrderActionForm();
            formData.setActionType("assign");
            formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(planTimeBatch));
            formData.setAssignee(currentPersonIdBatch);
            formData.setStepId(item.getCurrentStepId());
            formData.setPmOrderId(item.getId());
            formDataList.add(formData);
        });*/
       //批量派工时 来自不同page的选择的  参考 acceptWorkOrderBatch 可以替换上面两端代码
        hmPmOrder.entrySet().stream().filter(item->item.getValue()).forEach(item->{
            PmOrderActionForm formData = new PmOrderActionForm();
            formData.setActionType("assign");
            formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd").format(planTimeBatch));
            formData.setStepId("Assign");
            formData.setAssignee(currentPersonIdBatch);
            formData.setPmOrderId(item.getKey());
            formDataList.add(formData);
            item.setValue(false);//将选中的true 设置为false 还原
        });

        String res = srApi.invokePmOrderActionBatch(token,formDataList,"assign");
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Distribute"));
        }else{
            WebUtil.addSuccessMessage(WebUtil.getMessage("BatchDistributeSuccess"));
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
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Submit"));
        }else{
            WebUtil.addSuccessMessage(WebUtil.getMessage("BatchCloseSuccess"));
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
            WebUtil.addSuccessMessage(WebUtil.getMessage("DisptachSuccess"));
        }
        selectedPmOrder.setCurrentStepId("accept");
    }
    public void reassignWorkOrderBatch(List<V2_PmOrder> pmList,Map<String,Boolean> hmPmOrder,String currentPersonIdBatch,String comments){
        String token = UserContextService.getAccessToken();
        List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
        ArrayList<PmOrderActionForm> formDataList = new ArrayList<PmOrderActionForm>();
       /* pmList.stream().forEach(item->{
            if(hmPmOrder.containsKey(item.getId()) && hmPmOrder.get(item.getId()) == true){
                //确保批量接单后，批量接单按钮是灰色
                hmPmOrder.put(item.getId(),false);
                //pmListTmp保存了所要提交的批量pmOrder
                pmListTmp.add(item);
            }
        });

        pmListTmp.stream().forEach(item->{
            PmOrderActionForm formData = new PmOrderActionForm();
            formData.setActionType("reAssign");
            formData.setStepId(item.getCurrentStepId());
            formData.setAssignee(currentPersonIdBatch);
            formData.setPmOrderId(item.getId());
            formData.setDesc(comments);
            formDataList.add(formData);
        });*/

          //批量转单时 来自不同page的选择的  参考 acceptWorkOrderBatch ，可以替换上面两端代码
            hmPmOrder.entrySet().stream().filter(item->item.getValue()).forEach(item->{
            PmOrderActionForm formData = new PmOrderActionForm();
            formData.setActionType("reAssign");
            formData.setStepId("Maintain");
            formData.setAssignee(currentPersonIdBatch);
            formData.setPmOrderId(item.getKey());
            formDataList.add(formData);
            item.setValue(false);//将选中的true 设置为false 还原
        });

        String res = srApi.invokePmOrderActionBatch(token,formDataList,"reAssign");
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Reassign"));
        }else{
            WebUtil.addSuccessMessage(WebUtil.getMessage("BatchReassignSuccess"));
        }
    }

    public List<PmOrderSteps> getPmOrderStep(String pmOrderId) {
        List<PmOrderSteps> pmOrderSteps = ucService.getPmOrderStepsList(pmOrderId);
        return pmOrderSteps == null ? new ArrayList<>() : pmOrderSteps;
    }

    public List<PmOrderSteps> getPmOrderStep0(String pmOrderId){
       DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        List<V2_PmOrderStep> pmOrderSteps = pmOrderStepRepository.findByPmIdOrderByStartTimeAsc(pmOrderId);
        List<PmOrderSteps> collect = pmOrderSteps.stream().map(item -> {
            PmOrderSteps step = new PmOrderSteps();
            String ownerName = item.getOwnerName();
            String ownerUid = item.getOwnerUid();
            Date startTime = item.getStartTime();
            Date endTime = item.getEndTime();
            String stepName = item.getStepName();
            if (StringUtils.isEmpty(ownerName)) {
                if (StringUtils.isNotEmpty(ownerUid)) {
                    UserAccount owner = userAccountRepository.findByUid(ownerUid);
                    if (owner != null) {
                        ownerName = owner.getName();
                    }
                }
            }
            if(StringUtils.isEmpty(stepName)){
                stepName = "Create".equals(item.getStepId()) ? "新建" : WorkOrderStatus.getStepName(item.getStepId());
            }
            step.setOwnerName(ownerName);
            step.setStepName(stepName);
            step.setStartTime(startTime == null?"":dtf.print(startTime.getTime()));
            step.setEndTime(endTime == null?"":dtf.print(endTime.getTime()));
            step.setStepId(item.getStepId());
            step.setOwnerId(item.getOwnerUid());
            step.setComments(item.getComment());
            return step;
        }).collect(Collectors.toList());

        return collect;
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
        formData.setSignature(selectedPmOrder.getPartApproveSignature());
        String res = srApi.invokePmOrderAction(token,formData,"审批",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Finish"));
        }else{
            WebUtil.addSuccessMessage(WebUtil.getMessage("ApprovalSuccess"));
        }
    }
    public void repairWorkOrder(V2_PmOrder selectedPmOrder,List<V2_WorkOrder_Detail> accDetails, List<V2_WorkOrderCoworker> coDetails,Date endTime,Date planTime,List<V2_BlobObject> attachment,
                                String currentPersonIdBatch,List<V2_BlobObject> picBeforeList,List<V2_BlobObject> picAfterList, List<V2_PmOrderManHoursDetail> pmHoursList) {
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        if(selectedPmOrder.getSimplify()!=null && selectedPmOrder.getSimplify()==true){
            formData.setActionType("simplify");
        }else{
            formData.setActionType("repair");
        }
        formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(planTime));
        formData.setStepId(selectedPmOrder.getCurrentStepId());
        //formData.setAttachments(attachment);
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
        formData.setAssignee(currentPersonIdBatch); // （修改记录验收人）
        formData.setAttachmentsTwo(attachment);
        formData.setAttachments(picAfterList);
        formData.setPrePmAttachments(picBeforeList);
        formData.setComments(selectedPmOrder.getComments());//工作备注
        formData.setEntryType(selectedPmOrder.getEntryType());
        if(selectedPmOrder.getEntryType() == 1){
            formData.setPmOrderManHoursDetails(pmHoursList);
        }
        String res = srApi.invokePmOrderAction(token,formData,"保养",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Finish"));
        }else{
            WebUtil.addSuccessMessage(WebUtil.getMessage("OperationSuccess"));
        }
    }
    public void repairWorkOrderBatch(Date endTime,Double totalManHours,Map<String, Boolean> hmPmOrder,String currentPersonIdBatch, String comments) {
        String token = UserContextService.getAccessToken();
        List<String> uidList = new ArrayList<String>();
        hmPmOrder.entrySet().stream().filter(item->item.getValue()).forEach(item->uidList.add(item.getKey()));
        List<V2_PmOrder> batchPmOrderList = woDao.getBatchPmOrderList(uidList);
        batchPmOrderList.stream().forEach(item->{
            if(hmPmOrder.containsKey(item.getId()) && hmPmOrder.get(item.getId()) == true){
                hmPmOrder.put(item.getId(),false);
            }
        });

        Double averageHours = totalManHours/batchPmOrderList.size();
        ArrayList<PmOrderActionForm> formDataList = new ArrayList<PmOrderActionForm>();
        batchPmOrderList.stream().forEach(item->{
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
            formData.setAssignee(currentPersonIdBatch); // 记录验收人
            formData.setComments(comments);
            item.setCurrentStepName("保养中");
            formDataList.add(formData);
        });
        log.info(formDataList.toString());
        String res= srApi.invokePmOrderActionBatch(token,formDataList,"repair");
        log.info(res);
        if (res==null || !res.contains("OK")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("BatchComplete"));
        }else{
            WebUtil.addSuccessMessage(WebUtil.getMessage("BatchCompleteSuccess"));
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
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Reassign"));
        }else{
            WebUtil.addSuccessMessage(WebUtil.getMessage("ReassignSuccess"));
        }
    }

    public void stagingPmrder(V2_PmOrder selectedPmOrder,List<V2_WorkOrder_Detail> accDetails, List<V2_WorkOrderCoworker> coDetails,Date endTime,Date planTime,List<V2_BlobObject> attachment,List<V2_BlobObject> picBeforeList,List<V2_BlobObject> picAfterList, List<V2_PmOrderManHoursDetail> pmHoursList) {
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        //formData.setAttachments(attachment);
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
        formData.setAttachmentsTwo(attachment);
        formData.setAttachments(picAfterList);
        formData.setPrePmAttachments(picBeforeList);
        formData.setComments(selectedPmOrder.getComments());
        formData.setEntryType(selectedPmOrder.getEntryType());
        if(selectedPmOrder.getEntryType() == 1){
            formData.setPmOrderManHoursDetails(pmHoursList);
        }
        String res = srApi.invokePmOrderAction(token,formData,"tempRepair",selectedPmOrder.getId());
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Finish"));
        }else{
            WebUtil.addSuccessMessage(WebUtil.getMessage("TemporaryStorageSuccess"));
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
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("TakeOrder"));
        }else{
            WebUtil.addSuccessMessage(WebUtil.getMessage("TakeOrderSuccess"));
        }
        selectedPmOrder.setCurrentStepId("repair");
    }
    public void acceptWorkOrderBatch(List<V2_PmOrder> pmList,Map<String,Boolean> hmPmOrder,Date planTimeBatch,int type) {
        String token = UserContextService.getAccessToken();
        List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
        ArrayList<PmOrderActionForm> formDataList = new ArrayList<PmOrderActionForm>();
        hmPmOrder.entrySet().stream().filter(item->item.getValue()).forEach(item->{
            V2_PmOrder v2 = woDao.findById(item.getKey());

            PmOrderActionForm formData = new PmOrderActionForm();
            formData.setActionType("accept");
            formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd").format(planTimeBatch));
            formData.setStepId(v2.getCurrentStepId());
       /*     if(type==1)
                formData.setStepId("Accept");//抢单Assign和接单都是Accept
            if(type==2)
                formData.setStepId("Assign");//抢单Assign和接单都是Accept*/
            formData.setPmOrderId(item.getKey());
            formDataList.add(formData);
            item.setValue(false);//将选中的true 设置为false 还原
        });
        String res=srApi.invokePmOrderActionBatch(token,formDataList,"accept");
        if (res==null || !res.equalsIgnoreCase("Ok")) {
            if(type==1)
                WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("TakeOrder"));
            else
                WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Compete"));

        }else{
            if(type==1)
                WebUtil.addSuccessMessage(WebUtil.getMessage("BatchTakeOrderSuccess"));
            else
                WebUtil.addSuccessMessage(WebUtil.getMessage("BatchCompeteSuccess"));

            for(V2_PmOrder item:pmListTmp){
                item.setCurrentStepId("repair");
            }
        }
    }
    public List<UserAccount> getWorkerList(Integer assetId) {
        return ucService.getWorkerList(assetId,"");
    }
    public List<UserAccount> getApproveList(String assetUid,String name) {
        return ucService.getApproveList(assetUid,name);
    }
    public AssetInfo getAssetInfo(Integer assetId){
        return assetDao.findById(assetId);
    }
    public AssetInfo getAssetInfoByUid(String assetUid){
        return assetDao.findByUid(assetUid);
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

    private List<PmOrderRuleCheckList> getPmRuleCLClosed(V2_PmOrder v2PmOrder, String ruleUid){
        /*   unsDC:bo_uuid(工单)-->unsDC:id====>unsDC:id/unsD:collecitoId-->key
        * key就是检查项uid*/
        List<PmOrderRuleCheckList> pmcl =new ArrayList<PmOrderRuleCheckList>();
        UnstructuredDataCollection unsD = reportDao.findCKByUId(v2PmOrder.getId());
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

    public List<PmOrderRuleCheckList> getPmOrderRuleCheckLists(V2_PmOrder v2PmOrder, PmOrderRule pmOrderRule) {
        if (v2PmOrder.isFinished()) {
            return getPmRuleCLClosed(v2PmOrder, pmOrderRule.getUid());
        } else {
            return pmckLstDao.findAllByRuleUID(pmOrderRule.getUid());
        }
    }

    public Map<String, UnstructuredData> fetchHmCheck(List<PmOrderRuleCheckList> pmCheckItemList, V2_PmOrder v2PmOrder) {
        Map<String, UnstructuredData> hmCheck = new HashMap<>();
        if (pmCheckItemList != null && !pmCheckItemList.isEmpty()) {
            List<UnstructuredDataCollection> unDataCollection = reportDao.findCKByBoUuid(v2PmOrder.getId(), "pm-checklist-report");
            if (unDataCollection != null && !unDataCollection.isEmpty()) {
                UnstructuredDataCollection unCollection = unDataCollection.get(0);
                if (unCollection != null) {
                    List<UnstructuredData> dataList = unCollection.getData();
                    for (PmOrderRuleCheckList aPmCheckItemList : pmCheckItemList) {
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

    public void invokeAcceptance(V2_PmOrder selectedPmOrder, String remarks, boolean pass){
        String token = UserContextService.getAccessToken();
        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setStepId(selectedPmOrder.getCurrentStepId());
        formData.setRemarks(remarks);
        String res = null;
        if(pass) {
            formData.setActionType("acceptancePass");
            res = srApi.invokePmOrderAction(token, formData, "acceptancePass", selectedPmOrder.getId());

            if (res==null || !res.equalsIgnoreCase("Ok")) {
                WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"));
            }else{
                WebUtil.addSuccessMessage(WebUtil.getMessage("AckPass"));
            }
        }else {
            formData.setActionType("acceptanceReject");
            res = srApi.invokePmOrderAction(token, formData, "acceptanceReject", selectedPmOrder.getId());
            if (res==null || !res.equalsIgnoreCase("Ok")) {
                WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"));
            }else{
                WebUtil.addSuccessMessage(WebUtil.getMessage("OperationSuccess"));
            }
        }
    }

    public void invokeAcceptanceInBatch(Map<String,Boolean> hmPmOrder, String remarks, boolean pass){
        String token = UserContextService.getAccessToken();
        List<String> uidList = new ArrayList<String>();
        hmPmOrder.entrySet().stream().filter(item -> item.getValue()).forEach(item -> uidList.add(item.getKey()));
        List<V2_PmOrder> batchPmOrderList = woDao.getBatchPmOrderList(uidList);
        batchPmOrderList.stream().forEach(item -> {
            if (hmPmOrder.containsKey(item.getId()) && hmPmOrder.get(item.getId()) == true) {
                hmPmOrder.put(item.getId(), false);
            }
        });

        ArrayList<PmOrderActionForm> formDataList = new ArrayList<PmOrderActionForm>();
        batchPmOrderList.stream().forEach(item -> {
            PmOrderActionForm formData = new PmOrderActionForm();
            if(pass) {
                formData.setActionType("acceptancePass");

            }else {
                formData.setActionType("acceptanceReject");
            }
            formData.setComments(remarks);
            formData.setPmOrderId(item.getId());
            formData.setStepId(item.getCurrentStepId());
            formDataList.add(formData);
        });

        String res = null;
        if(pass){
            res = srApi.invokePmOrderActionBatch(token, formDataList, "acceptancePass");
            if (res==null || !res.equalsIgnoreCase("Ok")) {
                WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"));
            }else{
                WebUtil.addSuccessMessage(WebUtil.getMessage("AckPass"));
            }
        }else {
            res = srApi.invokePmOrderActionBatch(token, formDataList, "acceptanceReject");
            if (res==null || !res.equalsIgnoreCase("Ok")) {
                WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"));
            }else{
                WebUtil.addSuccessMessage(WebUtil.getMessage("OperationSuccess"));
            }
        }

    }

}

