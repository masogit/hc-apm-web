package com.ge.apm.service.approval;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.dao.simpleparts.SimplePartsRequestRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.domain.pure.ApprovableTarget;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.common.WechatSendMsgService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.constant.PmConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.constant.WorkOrderConstant;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.simpleparts.SimplePartsConfigService;
import com.ge.apm.service.simpleparts.SimplePartsRequestService;
import com.ge.apm.service.simpleparts.SimplePartsRequestWorkOrderService;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.budget.BudgetConstant;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MultiLevelApprovalService extends MicroServiceInvoker {

    protected static final Logger logger = Logger.getLogger(MultiLevelApprovalService.class);

    @Autowired
    private MultiLevelApprovalRepository multiLevelApprovalRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private WechatSendMsgService wechatSendMsgService;
    @Autowired
    private SimplePartsRequestService partsRequestService;
    @Autowired
    private SimplePartsRequestRepository partsRequestRepository;
    @Autowired
    private MultiLevelApprovalConfigRepository multiApprovalConfigDao;
    @Autowired
    private V2_WorkOrderRepository v2_WorkOrderRepository;
    @Autowired
    private V2_PmOrderRepository v2_PmOrderRepository;
    @Autowired
    private BudgetApplicationRepository budgetApplicationRepository;
    @Autowired
    private V2_WorkOrderDetailRepository v2_WorkOrderDetailRepository;
    @Autowired
    private SimplePartsRequestWorkOrderService simplePartsRequestWorkOrderService;
    @Autowired
    private V2_PmOrderService pmOrderService;
    @Autowired
    private SimplePartsConfigService partsConfigService;

    public MultiLevelApprovalConfig getFirstApprovalList(ApprovableTarget approvableTarget,boolean approvalFlag) {
        // 需要调用服务端提供的接口
        logger.info("approvableTarget"+approvableTarget);
        BigDecimal totalPrice = approvableTarget.getTotalPriceToBigDecimal();
        MultiLevelApprovalConfig multiLevelApprovalConfig =
                this.getApproveConfig(switchBusyTypeToString(approvableTarget.getBusinessType()), totalPrice,approvalFlag);
        return multiLevelApprovalConfig;
    }

    /**
     * 转换BusinessType
     * @return
     */
    String switchBusyTypeToString(int type){
        switch (type) {
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_REQUEST_INT:
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_RETURN_INT:
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_PURCHASE_INT:
                return ApprovalConstant.APPROVAL_PARTS_STOCK;
            case ApprovalConstant.BUSINESS_TYPE_FILE_INFORMATION_MODIFICATION_INT:
                return ApprovalConstant.APPROVAL_ASSET_INFO_MODIFY;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_OVERHAUL_INT:
                return ApprovalConstant.APPROVAL_ASSET_OVERHAUL;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_BORROW:
                return ApprovalConstant.APPROVAL_ASSET_BORROW;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_RECEIVE:
                return ApprovalConstant.APPROVAL_ASSET_RECEIVE;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT:
                return ApprovalConstant.APPROVAL_ASSET_REPAIR;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_MAINTAIN_INT:
                return ApprovalConstant.APPROVAL_ASSET_MAINTAIN;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_BORROW_RETURN:
                return ApprovalConstant.APPROVAL_ASSET_BORROW_RETURN;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_RECEIVE_RETURN:
                return ApprovalConstant.APPROVAL_ASSET_RECEIVE_RETURN;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_TRANSFER:
                return ApprovalConstant.APPROVAL_ASSET_TRANSFER;
            case ApprovalConstant.BUSINESS_TYPE_ADVERSE_EVENTS:
                return ApprovalConstant.APPROVAL_ADVERSE_EVENTS;
            case ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT:
                return ApprovalConstant.APPROVAL_BUDGET_MANAGEMENT;
            default: return null;
        }
    }

    /**
     * 获取指定审核人列表
     *
     * @return
     */
    public MultiLevelApprovalConfig getApproveWorkflowConfig(String approvalUid) {
        MultiLevelApprovalConfig approveWorkflowConfig = multiApprovalConfigDao.findById(approvalUid);
        return approveWorkflowConfig;
    }

    /**
     * 保存并提交到审批人
     */
    @Transactional(rollbackFor = Exception.class)
    public MultiLevelApproval getMultiLevelApprovalPersonAndSubmit(ApprovableTarget approvableTarget, Integer level, boolean isApproval) {
        if (level == null) {
            level = 1;
        }
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        MultiLevelApprovalConfig multiLevelApprovalConfig = this.getFirstApprovalList(approvableTarget,false);
        if (multiLevelApprovalConfig != null) {
            String approvalItem = multiLevelApprovalConfig.getApprovalItem();
            if (null != approvalItem) {
                MultiLevelApproval multiLevelApproval = new MultiLevelApproval();
                List<String> weChatIdList = new ArrayList<>();
                List<com.ge.apm.domain.ApprovalNode> approvalNodes = JSON.parseObject(approvalItem, new TypeReference<List<com.ge.apm.domain.ApprovalNode>>() {});
                if (approvalNodes != null && !approvalNodes.isEmpty()) {
                    com.ge.apm.domain.ApprovalNode approvalNode = approvalNodes.get(0);
                    if (approvalNode != null) {
                        boolean usingRole = approvalNode.getUsingRole();
                        if (usingRole) {//角色审批
                            List<Integer> nodeRoleUids = approvalNode.getRoleUids();
                            if (nodeRoleUids != null) {
                                List<UserAccount> userAccounts = userAccountRepository.getApprovalPersonByRoleId(nodeRoleUids);
                                if (userAccounts != null) {
                                    for (int j = 0; j < userAccounts.size(); j++) {
                                        UserAccount user = userAccounts.get(j);
                                        if (user != null && user.getWeChatId() != null) {
                                            weChatIdList.add(user.getWeChatId());
                                        }
                                    }
                                }
                                //保存角色到审批流中
                                multiLevelApproval.setApprovalType(ApprovalConstant.ROLE_APPROVAL_TYPE);
                                Object[] objects = nodeRoleUids.toArray();
                                String str = StringUtils.join(objects, ",");
                                multiLevelApproval.setApprovalUid(str);
                            }
                        } else {//用户审批
                            List<String> userUids = approvalNode.getUserUids();
                            List<UserAccount> userAccounts = userAccountRepository.findByUidIn(userUids);
                            if (userAccounts != null) {
                                for (int j = 0; j < userAccounts.size(); j++) {
                                    UserAccount user = userAccounts.get(j);
                                    if (user != null && user.getWeChatId() !=null) {
                                        weChatIdList.add(user.getWeChatId());
                                    }
                                }
                            }
                            multiLevelApproval.setApprovalType(ApprovalConstant.USER_APPROVAL_TYPE);
                            Object[] objects = userUids.toArray();
                            String str = StringUtils.join(objects, ",");
                            multiLevelApproval.setApprovalUid(str);
                        }

                        multiLevelApproval.setBusinessNumber(approvableTarget.getBusinessNumber());
                        multiLevelApproval.setBusinessType(approvableTarget.getBusinessType());
                        multiLevelApproval.setBusinessUid(approvableTarget.getBusinessUid());
                        multiLevelApproval.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
                        multiLevelApproval.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING);
                        multiLevelApproval.setApprovalLevel(level);
                        multiLevelApproval.setApprovalNumber(autoGenerateFinacingNum());
                        multiLevelApproval.setApprovalIng(false);
                        multiLevelApproval.setApprovalLevelNow(level);
                        multiLevelApproval.setWorkflowConfigApproveUid(multiLevelApprovalConfig.getId());
                        multiLevelApproval.setInitiateApproverUid(userAccount.getUid());
                        multiLevelApproval.setCreatedDate(TimeUtils.localDateTimeConverterToDate(LocalDateTime.now()));
                        multiLevelApproval.setCreatedBy(userAccount.getLoginName());

                        multiLevelApproval.setTenantUID(userAccount.getTenantUID());
                        multiLevelApproval.setHierOrgUid(userAccount.getSiteUID());

                        MultiLevelApproval multiLevel = multiLevelApprovalRepository.save(multiLevelApproval);
                        if (multiLevel != null) {
                            // 发送短信
                            String message = ApprovalConstant.WX_MESSAGE_APPROVAL_1 + ApprovalConstant.WX_MESSAGE_APPROVAL_2 + multiLevelApproval.getBusinessNumber() + ApprovalConstant.WX_MESSAGE_APPROVAL_3;
                            this.sendWxMsg(weChatIdList,message,"待审批");
                        }
                        return multiLevel;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 供备件审批使用--审批不同意
     * @param multiLevelApproval
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void getMultiLevelApprovalPersonAndSubmitByDisagree(MultiLevelApproval multiLevelApproval, String approvalOpinion) {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        multiLevelApproval.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE);
        multiLevelApproval.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE);
        multiLevelApproval.setActualApproverName(userAccount.getName());
        multiLevelApproval.setActualApproverDate(new Date());
        multiLevelApproval.setActualApproverUid(userAccount.getUid());
        multiLevelApproval.setApprovalOpinion(approvalOpinion);
        multiLevelApproval.setApprovalIng(true);
        //更新审批数据
        multiLevelApprovalRepository.save(multiLevelApproval);
        //更新审批状态为不通过 业务状态为审批不通过
        List<MultiLevelApproval> list = this.getMultiLevelApproval(multiLevelApproval);
        //发起人UID
        String initiateApproverUid = null;
        String businessType = this.getBusinessType(multiLevelApproval.getBusinessType());
        String businessNumber = multiLevelApproval.getBusinessNumber();
        if (null != list) {
            for (int i = 0; i < list.size(); i++) {
                MultiLevelApproval multi = list.get(i);
                if (null != multi.getInitiateApproverUid() && !"".equals(multi.getInitiateApproverUid())) {
                    initiateApproverUid = multi.getInitiateApproverUid();
                }
                multi.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE);
                multi.setApprovalLevelNow(ApprovalConstant.APPROVAL_NOW_DISAGREE);
                multiLevelApprovalRepository.save(multi);
            }
        }
        switch (multiLevelApproval.getBusinessType()) {
            case ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT:
                V2_WorkOrder v2WorkOrder = v2_WorkOrderRepository.findById(multiLevelApproval.getBusinessUid());
                v2WorkOrder.setApprovalStatus(WorkOrderConstant.APPROVAL_STATUS_DISAGREE);//审批不同意
                v2_WorkOrderRepository.save(v2WorkOrder);
                this.doWorkDetailByIsAgreeType(v2WorkOrder.getId(), ApprovalConstant.WORK_DETAIL_STATUS_DISAGREE, null, null);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_MAINTAIN_INT:
                V2_PmOrder v2PmOrder = v2_PmOrderRepository.findById(multiLevelApproval.getBusinessUid());
                v2PmOrder.setApprovalStatus(PmConstant.PM_APPROVAL_STATUS_DISAGREE);
                v2_PmOrderRepository.save(v2PmOrder);
                this.doWorkDetailByIsAgreeType(v2PmOrder.getId(), ApprovalConstant.WORK_DETAIL_STATUS_DISAGREE, null, null);
                break;
            case ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT:
                // 预算管理
                updateBudget(multiLevelApproval.getBusinessUid(), BudgetConstant.BUDGET_APPLICATION_STATUS_NOT_APPROVED);
                break;
            //备件审批
            default:
                throw new RuntimeException("New Business type not handled yet");
//                this.editParts(multiLevelApproval,ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE);
        }

        //审批不同意时 给发起人发送微信
        List<String>weChatIdList = new ArrayList<>();
        UserAccount user = userAccountRepository.getByUid(initiateApproverUid);
        if (user != null && user.getWeChatId() !=null){
            weChatIdList.add(user.getWeChatId());
        }
        String message = ApprovalConstant.WX_MESSAGE_DISAGREE_1+businessType+ApprovalConstant.WX_MESSAGE_DISAGREE_2+businessNumber+ApprovalConstant.WX_MESSAGE_DISAGREE_3;
        this.sendWxMsg(weChatIdList,message,"审批失败");
    }

    public void doWorkDetailByIsAgreeType(String id,int isAgreeType, List<V2_WorkOrder_Detail> accessorySparePartsBuyList, List<V2_WorkOrder_Detail> accessorySparePartsRequestList) {
        List<V2_WorkOrder_Detail> approvedDetailsNew = getWorkOrderDetail(id);
        if(approvedDetailsNew.isEmpty()){
            return;
        }
        if (isAgreeType == ApprovalConstant.WORK_DETAIL_STATUS_AGREE) {
            //最后一个审批通过的时候 生成相应单子
            for (int i = 0; i < approvedDetailsNew.size(); i++) {
                V2_WorkOrder_Detail v2_workOrder_detail = approvedDetailsNew.get(i);
                if (v2_workOrder_detail.getApproved() != null && v2_workOrder_detail.getApproved()) {
                    continue;
                }
                v2_workOrder_detail.setApproved(Boolean.TRUE);
                v2_WorkOrderDetailRepository.save(v2_workOrder_detail);
                if (ApmConstant.TREASURY_USE.equals(v2_workOrder_detail.getPartsType())) {//库房领用
                    accessorySparePartsRequestList.add(v2_workOrder_detail);
                } else if (ApmConstant.MEDICAL_WORKERS_PURCHASING.equals(v2_workOrder_detail.getPartsType())) {//医工采购
                    accessorySparePartsBuyList.add(v2_workOrder_detail);
                }
            }
        }else{
            for (int i = 0; i < approvedDetailsNew.size(); i++) {
                V2_WorkOrder_Detail v2_workOrder_detail = approvedDetailsNew.get(i);
                if (v2_workOrder_detail.getApproved() != null && v2_workOrder_detail.getApproved()) {
                    continue;
                }
                v2_workOrder_detail.setApproved(Boolean.FALSE);
                v2_WorkOrderDetailRepository.save(v2_workOrder_detail);
            }
        }
    }

    /**
     *
     * @param busiType
     * @param assetType
     * @param amount
     * @return MultiLevelApprovalConfig
     * @date 2020/7/30 16:00
     * 根据金额查询审批配置信息
     */
    public MultiLevelApprovalConfig getApproveConfig(String busiType, BigDecimal amount,boolean approvalFlag) {
        logger.info("-----校验开启多级审核请求参数 busiType : "+" , amount : "+amount);
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = new ArrayList<>(Arrays.asList(user.getSiteUID()));
        for (String orgUid: orgUidList) {
            MultiLevelApprovalConfig approveConfig= null;
            if(approvalFlag){
                approveConfig = getApproveConfigByOrgUid(orgUid, busiType, amount);
            }else {
                approveConfig = getApproveConfigByOrgUidApprovalFlag(orgUid, busiType, amount);
            }
            if(null != approveConfig){
                return approveConfig;
            }
        }
        return null;
    }

    MultiLevelApprovalConfig getApproveConfigByOrgUid(String orgUid, String busiType, BigDecimal amount){
        if (StringUtils.isEmpty(busiType)){
            return null;
        }
        List<MultiLevelApprovalConfig> configList = multiApprovalConfigDao.getConfigByCondition(orgUid, busiType);
        if(null != configList &&configList.size()>0){
            logger.info("多级审核  configList : "+configList.toString());
            if(ApprovalConstant.APPROVAL_PARTS_STOCK.equals(busiType) || ApprovalConstant.APPROVAL_BUDGET_MANAGEMENT.equals(busiType)){
                return getPartsSteam(configList,amount);
            }else{
                return configList.get(0);
            }
        }
        return null;
    }

    MultiLevelApprovalConfig getApproveConfigByOrgUidApprovalFlag(String orgUid, String busiType, BigDecimal amount){
        if (StringUtils.isEmpty(busiType)){
            return null;
        }
        List<MultiLevelApprovalConfig> configList = multiApprovalConfigDao.getConfigByCondition(orgUid, busiType);
        if(null != configList &&configList.size()>0){
            if(ApprovalConstant.APPROVAL_PARTS_STOCK.equals(busiType) || ApprovalConstant.APPROVAL_ASSET_REPAIR.equals(busiType)
                    || ApprovalConstant.APPROVAL_ASSET_MAINTAIN.equals(busiType) || ApprovalConstant.APPROVAL_BUDGET_MANAGEMENT.equals(busiType)){
                return getPartsSteam(configList,amount);
            }else{
                return configList.get(0);
            }
        }
        return null;
    }

    //获取备件库存审批流
    MultiLevelApprovalConfig getPartsSteam(List<MultiLevelApprovalConfig> configList,BigDecimal amount){
        //先遍历所有 有配置金额信息的
        for (MultiLevelApprovalConfig config : configList) {
            if(null != config.getConfigration()){
                Map configMap = JSON.parseObject(config.getConfigration(), Map.class);
                Integer minAmount = ((Integer) configMap.get("minAmount"));
                Integer maxAmount = ((Integer) configMap.get("maxAmount"));
                if(minAmount < amount.doubleValue() && amount.doubleValue() <= maxAmount){
                    return config;
                }
                if(minAmount > 0 && maxAmount == 0){
                    if(minAmount< amount.doubleValue()){
                        return config;
                    }
                }
            }
        }
        //若审批流配置金额条件都不满足，则取无金额配置信息的
        for (MultiLevelApprovalConfig config : configList) {
            if(null != config.getConfigration()){
                Map configMap = JSON.parseObject(config.getConfigration(), Map.class);
                if(0 == (int)configMap.get("minAmount") && 0 == (int)configMap.get("maxAmount")){
                    return config;
                }
            }
        }
        return null;
    }

    public List<MultiLevelApproval> getByInitiateApproverUid(String userUid) {
        List<MultiLevelApproval> multiLevelApprovals = multiLevelApprovalRepository.getByInitiateApproverUid(userUid);
        return multiLevelApprovals;
    }

    public List<MultiLevelApproval> getMultiLevelApprovalList(String actualApproverUid, Integer approvalNodeStatus) {
        List<MultiLevelApproval> multiLevelApprovals = multiLevelApprovalRepository.getMultiLevelApprovalList(actualApproverUid, approvalNodeStatus);
        return multiLevelApprovals;
    }

    public List<MultiLevelApproval> getMultiLevelApproval(MultiLevelApproval multiLevelApproval) {
        String approvalNumber = multiLevelApproval.getApprovalNumber();
        List<MultiLevelApproval> multiLevelApprovals = multiLevelApprovalRepository.getByApprovalNumberList(approvalNumber);
        return multiLevelApprovals;
    }

    //生成审批单号
    public String autoGenerateFinacingNum(){
        DateTime now = new DateTime();
        String prefix = ApprovalConstant.APPROVAL_NUM;
        String year = now.toString("yyyyMM");
        int max= 999999;

        String maxFinacingNum = null;//当前最大值的str
        synchronized (this){
            maxFinacingNum = multiLevelApprovalRepository.selectMaxApprovalNum(prefix+year);
            if(maxFinacingNum == null){
                return  prefix+year + "000001";
            }
            int finacingNum = 0;//当前最大数值
            if(finacingNum > max){
                return "error:无法生成资产编号，已达到上限。";
            }
            finacingNum = Integer.parseInt(maxFinacingNum.substring(8));
            String num = String.format("%06d", ++finacingNum);
            return prefix+year+num;
        }
    }

    /**
     * 供备件审批使用--审批同意
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void getMultiLevelApprovalPersonByAgree(MultiLevelApproval multiLevelApprovalNew, String approvalOpinion) throws Exception{
        MultiLevelApproval multiLevelApproval = multiLevelApprovalNew;
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        MultiLevelApprovalConfig multiLevelApprovalConfig = this.getApproveWorkflowConfig(multiLevelApproval.getWorkflowConfigApproveUid());
        if (multiLevelApprovalConfig != null) {
            multiLevelApproval.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
            multiLevelApproval.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
            multiLevelApproval.setActualApproverName(userAccount.getName());
            multiLevelApproval.setActualApproverDate(new Date());
            multiLevelApproval.setActualApproverUid(userAccount.getUid());
            multiLevelApproval.setApprovalOpinion(approvalOpinion);
            multiLevelApproval.setApprovalIng(true);
            int approvalLevel = multiLevelApproval.getApprovalLevel();
            int level = approvalLevel + 1;
            multiLevelApproval.setApprovalLevelNow(level);
            //更新审批数据
            multiLevelApprovalRepository.save(multiLevelApproval);
            //更新审批进度
            if(level > 2){
                List<MultiLevelApproval> list = this.getMultiLevelApproval(multiLevelApproval);
                if (null != list) {
                    for (int i = 0; i < list.size(); i++) {
                        MultiLevelApproval multi = list.get(i);
                        if (multi != null && multi.getApprovalLevelNow() != level){
                            multi.setApprovalLevelNow(level);
                            multiLevelApprovalRepository.save(multi);
                        }
                    }
                }
            }
            String approvalItem = multiLevelApprovalConfig.getApprovalItem();
            if (null != approvalItem) {
                List<String> weChatIdList = new ArrayList<>();
                List<ApprovalNode> approvalNodes = JSON.parseObject(approvalItem, new TypeReference<List<ApprovalNode>>() {});
                if (approvalNodes != null) {
                    MultiLevelApproval date = multiLevelApprovalNew;
                    int maxNodeId = approvalNodes.size();
                    if (level > maxNodeId) {//最后一个人审批通过 更新审批状态为通过 业务状态为审批通过
                        List<MultiLevelApproval> list = this.getMultiLevelApproval(multiLevelApproval);
                        //发起人UID
                        String initiateApproverUid = null;
                        String businessType = this.getBusinessType(multiLevelApproval.getBusinessType());
                        if (null != list) {

                            for (int i = 0; i < list.size(); i++) {
                                MultiLevelApproval multi = list.get(i);
                                if (null != multi.getInitiateApproverUid() && !"".equals(multi.getInitiateApproverUid())) {
                                    initiateApproverUid = multi.getInitiateApproverUid();
                                }
                                multi.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
                                multi.setApprovalLevelNow(ApprovalConstant.APPROVAL_NOW_AGREE);
                                multiLevelApprovalRepository.save(multi);
                            }
                            //微信通知发起人审批通过
                            UserAccount user = userAccountRepository.getByUid(initiateApproverUid);
                            if (user != null && user.getWeChatId() !=null) {
                                weChatIdList.add(user.getWeChatId());
                            }
                            String message = ApprovalConstant.WX_MESSAGE_AGREE_1 + businessType + ApprovalConstant.WX_MESSAGE_AGREE_2 + multiLevelApproval.getBusinessNumber() + ApprovalConstant.WX_MESSAGE_AGREE_3;
                            this.sendWxMsg(weChatIdList, message,"审批通过");
                        }
                        switch (multiLevelApproval.getBusinessType()){
                            case ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT://维修
                                V2_WorkOrder v2WorkOrder = v2_WorkOrderRepository.findById(multiLevelApproval.getBusinessUid());
                                v2WorkOrder.setApprovalStatus(WorkOrderConstant.APPROVAL_STATUS_AGREE);//审批同意
                                v2_WorkOrderRepository.save(v2WorkOrder);
                                List<V2_WorkOrder_Detail> accessorySparePartsBuyList = new ArrayList<>();//申购
                                List<V2_WorkOrder_Detail> accessorySparePartsRequestList = new ArrayList<>();//申领
                                this.doWorkDetailByIsAgreeType(v2WorkOrder.getId(), ApprovalConstant.WORK_DETAIL_STATUS_AGREE, accessorySparePartsBuyList, accessorySparePartsRequestList);
                                //审批校验,生成申领申购单
                                if (partsConfigService.isEnableIntegrationWithWorkOrder()) {
                                    generateSparePartsBuyList(SimplePartsConstant.WORK_ORDER, accessorySparePartsBuyList, accessorySparePartsRequestList, userAccountRepository.findById(v2WorkOrder.getCurrentPersonId()));
                                }
                                break;
                            case ApprovalConstant.BUSINESS_TYPE_ASSET_MAINTAIN_INT://保养
                                V2_PmOrder v2PmOrder = v2_PmOrderRepository.findById(multiLevelApproval.getBusinessUid());
                                v2PmOrder.setApprovalStatus(PmConstant.PM_APPROVAL_STATUS_AGREE);
                                v2_PmOrderRepository.save(v2PmOrder);
                                List<V2_WorkOrder_Detail> pmAccessorySparePartsBuyList = new ArrayList<>();//申购
                                List<V2_WorkOrder_Detail> pmAccessorySparePartsRequestList = new ArrayList<>();//申领
                                this.doWorkDetailByIsAgreeType(v2PmOrder.getId(), ApprovalConstant.WORK_DETAIL_STATUS_AGREE, pmAccessorySparePartsBuyList, pmAccessorySparePartsRequestList);
                                //审批校验,生成申领申购单
                                if (partsConfigService.isEnableIntegrationWithWorkOrder()) {
                                    generateSparePartsBuyList(SimplePartsConstant.PM_ORDER, pmAccessorySparePartsBuyList, pmAccessorySparePartsRequestList, userAccountRepository.getByUid(v2PmOrder.getCurrentPersonId()));
                                }
                                break;
                            case ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT:
                                // 预算管理
                                updateBudget(multiLevelApproval.getBusinessUid(), BudgetConstant.BUDGET_APPLICATION_STATUS_APPROVED);
                                break;
                            default:
                                throw new RuntimeException("New Business type not handled yet");
//                                this.editParts(multiLevelApproval,ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
                        }
                    }else {
                        com.ge.apm.domain.ApprovalNode node = approvalNodes.get(approvalLevel);
                        if(node != null){
                            boolean usingRole = node.getUsingRole();
                            if (usingRole) {//角色审批，根据角色拉取用户进行审批
                                List<Integer> nodeRoleUids = node.getRoleUids();
                                if (nodeRoleUids != null) {
                                    List<UserAccount> userAccounts = userAccountRepository.getApprovalPersonByRoleId(nodeRoleUids);
                                    if (userAccounts != null) {
                                        for (int j = 0; j < userAccounts.size(); j++) {
                                            UserAccount user = userAccounts.get(j);
                                            if (user != null && user.getWeChatId() != null) {
                                                weChatIdList.add(user.getWeChatId());
                                            }
                                        }
                                    }
                                    //保存角色到审批流中
                                    date.setApprovalType(ApprovalConstant.ROLE_APPROVAL_TYPE);
                                    Object[] objects = nodeRoleUids.toArray();
                                    String str = StringUtils.join(objects, ",");
                                    date.setApprovalUid(str);
                                }
                            }else {//用户审批
                                List<String> userUids = node.getUserUids();
                                List<UserAccount> userAccounts = userAccountRepository.findByUidIn(userUids);
                                if (userAccounts != null) {
                                    for (int j = 0; j < userAccounts.size(); j++) {
                                        UserAccount user = userAccounts.get(j);
                                        if (user != null && user.getWeChatId() !=null) {
                                            weChatIdList.add(user.getWeChatId());
                                        }
                                    }
                                }
                                date.setApprovalType(ApprovalConstant.USER_APPROVAL_TYPE);
                                Object[] objects = userUids.toArray();
                                String str = StringUtils.join(objects, ",");
                                date.setApprovalUid(str);
                            }
                            date.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
                            date.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING);
                            date.setApprovalLevel(level);
                            date.setCreatedBy(userAccount.getLoginName());
                            date.setApprovalIng(false);
                            date.setApprovalLevelNow(level);
                            date.setInitiateApproverUid(null);
                            date.setCreatedDate(new Date());
                            date.setId(null);
                            date.setActualApproverName(null);
                            date.setActualApproverDate(null);
                            date.setApprovalOpinion(null);
                            //生成下条待审批数据
                            multiLevelApprovalRepository.save(date);
                            if (multiLevelApproval.getBusinessType() == ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT) {
                                updateBudget(multiLevelApproval.getBusinessUid(), BudgetConstant.BUDGET_APPLICATION_STATUS_IN_PROGRESS);
                            }
                            //审批同意时 给审批人发送微信
                            String message = ApprovalConstant.WX_MESSAGE_APPROVAL_1+ApprovalConstant.WX_MESSAGE_APPROVAL_2+date.getBusinessNumber()+ApprovalConstant.WX_MESSAGE_APPROVAL_3;
                            this.sendWxMsg(weChatIdList,message,"待审批");
                        }
                    }
                }
            }
        }
    }

    public void sendWxMsg(List<String>weChatIdList,String message,String remark){
        if(weChatIdList == null||weChatIdList.size()==0) return;
        String token = UserContextService.getAccessToken();
        Map<String,Object> map = new HashMap<>();
        map.put("weChatIdList",weChatIdList);
        map.put("message", ApprovalConstant.WX_MESSAGE_APPROVAL+TimeUtils.getStrDate(new Date(), "yyyy-MM-dd HH:mm:ss") + message);
        map.put("remark",remark);
        HttpEntity<Map> requestEntity = new HttpEntity<>(map,createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.APPROVAL_TO_SEND_WE_CHAT);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (!isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            String str = responseMap.get("message").toString();
            throw new RuntimeException(str);
        }
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetail(String woId) {
        return pmOrderService.getWorkOrderDetails(woId);
    }

    /**
     * @param type 0 维修工单, 1 保养工单
     * @param isAgreeType 0 不同意, 1 同意
     */
    private void generateSparePartsBuyList(int type, List<V2_WorkOrder_Detail> accessorySparePartsBuyList, List<V2_WorkOrder_Detail> accessorySparePartsRequestList, UserAccount userAccount) {
        if (accessorySparePartsRequestList != null && !accessorySparePartsRequestList.isEmpty()) {
            simplePartsRequestWorkOrderService.savePartsRequest(accessorySparePartsRequestList, type, userAccount);//生成申领单
        }
        if (accessorySparePartsBuyList != null && !accessorySparePartsBuyList.isEmpty()) {
            simplePartsRequestWorkOrderService.savePartsPurchase(accessorySparePartsBuyList, type, userAccount);//生成申购单
        }
    }

    public String getBusinessType(Integer businessType){
        if(null == businessType){
            return null;
        }
        switch (businessType){
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_REQUEST_INT:
                return ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_REQUEST;
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_PURCHASE_INT:
                return ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_PURCHASE;
            case ApprovalConstant.BUSINESS_TYPE_FILE_INFORMATION_MODIFICATION_INT:
                return ApprovalConstant.BUSINESS_TYPE_FILE_INFORMATION_MODIFICATION;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT:
                return ApprovalConstant.APPROVAL_ASSET_REPAIR_CHi;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_MAINTAIN_INT:
                return ApprovalConstant.APPROVAL_ASSET_MAINTAIN_CHi;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_OVERHAUL_INT:
                return ApprovalConstant.APPROVAL_ASSET_BIG_REPAIR;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_BORROW:
                return ApprovalConstant.APPROVAL_ASSET_BORROW_CHI;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_RECEIVE:
                return ApprovalConstant.APPROVAL_ASSET_RECEIVE_CHI;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_BORROW_RETURN:
                return ApprovalConstant.APPROVAL_ASSET_BORROW_RETURN_CHI;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_RECEIVE_RETURN:
                return ApprovalConstant.APPROVAL_ASSET_RECEIVE_RETURN_CHI;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_TRANSFER:
                return ApprovalConstant.APPROVAL_ASSET_TRANSFER_CHI;
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_RETURN_INT:
                return ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_RETURN;
            case ApprovalConstant.BUSINESS_TYPE_ADVERSE_EVENTS:
                return ApprovalConstant.BUSINESS_TYPE_ADVERSE_EVENTS_NAME;
            case ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT:
                return ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT_NAME;
        }
        return null;
    }

    /*
     * 判断有没有配置多级审核
     */
    public MultiLevelApprovalConfig checkMultiLevelApprovalPersonAndSubmit(ApprovableTarget approvableTarget) {
        MultiLevelApprovalConfig multiLevelApprovalConfig = this.getFirstApprovalList(approvableTarget,true);
        return multiLevelApprovalConfig;
    }

    public AdverseEvents getAdverseEvents(String token, String uid){
        HttpEntity<String > requestEntity = new HttpEntity<>(uid, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.GET_ADVERSE_EVENTS_INFO);
        String url = gatewayURL.replace("{uid}",uid);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            String result = response.getBody();
            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            AdverseEvents adverseEvents = JSONObject.toJavaObject(data, AdverseEvents.class);
            return adverseEvents;
        }
        return null;
    }

    public void approveAdverseEvents(String token, AdverseEvents adverseEvents){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        HttpEntity<AdverseEvents> requestEntity = new HttpEntity<>(adverseEvents, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.APPROVE_ADVERSE_EVENTS);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (!isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            String message = responseMap.get("message").toString();
            throw new RuntimeException(message);
        }
    }

    // 预算管理
    private void updateBudget(String businessUid, Integer approvalStatus) {
        BudgetApplication budget = budgetApplicationRepository.findById(businessUid);
        budget.setApplicationStatus(approvalStatus);
        budget.setApprovalProgress(budget.getApprovalProgress() + 1);
        budget.setApproverId(UserContextService.getCurrentUserAccount().getId());
        budget.setApprovalDate(new Date());
        budgetApplicationRepository.save(budget);
    }
}
