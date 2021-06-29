package com.ge.apm.service.approval;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.dao.simpleparts.SimplePartsRequestRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.simpleparts.SimplePartsRequest;
import com.ge.apm.domain.pure.ApprovableTarget;
import com.ge.apm.service.common.WechatSendMsgService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.constant.UsageStatusConstant;
import com.ge.apm.service.constant.WorkOrderConstant;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.simpleparts.SimplePartsRequestService;
import com.ge.apm.service.simpleparts.SimplePartsRequestWorkOrderService;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MultiLevelApprovalService {

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
    private V2_WorkOrderDetailRepository v2_WorkOrderDetailRepository;
    @Autowired
    private SimplePartsRequestWorkOrderService simplePartsRequestWorkOrderService;
    @Autowired
    private V2_PmOrderService pmOrderService;

    public MultiLevelApprovalConfig getFirstApprovalList(ApprovableTarget approvableTarget) {
        // 需要调用服务端提供的接口
        BigDecimal totalPrice = approvableTarget.getTotalPriceToBigDecimal();
        MultiLevelApprovalConfig multiLevelApprovalConfig =
                this.getApproveConfig(switchBusyTypeToString(approvableTarget.getBusinessType()), totalPrice);
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
        MultiLevelApprovalConfig multiLevelApprovalConfig = this.getFirstApprovalList(approvableTarget);
        if (multiLevelApprovalConfig != null) {
            String approvalItem = multiLevelApprovalConfig.getApprovalItem();
            if (null != approvalItem) {
                MultiLevelApproval multiLevelApproval = new MultiLevelApproval();
                List<String> weChatIdList = new ArrayList<>();
                List<com.ge.apm.domain.ApprovalNode> approvalNodes = JSON.parseObject(approvalItem, new TypeReference<List<com.ge.apm.domain.ApprovalNode>>() {});
                if (approvalNodes != null) {
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
                            for (int k = 0; k < weChatIdList.size(); k++) {
                                String str = weChatIdList.get(k);
                                if (str != null) {
                                    Map<String, Object> m = new HashMap<>();
                                    m.put("_openId", str);
                                    m.put("_templateId", "no_template");
                                    m.put("message", ApprovalConstant.WX_MESSAGE_APPROVAL + TimeUtils.getStrDate(new Date(), "yyyy-MM-dd HH:mm:ss") + message);
                                    wechatSendMsgService.sendWxMsg(m);
                                }
                            }
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
                //审批不通过,修改工单状态
                generateSparePartsBuyList(v2WorkOrder.getId(), SimplePartsConstant.WORK_ORDER, ApprovalConstant.WORK_DETAIL_STATUS_DISAGREE);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_MAINTAIN_INT:
                V2_PmOrder v2PmOrder = v2_PmOrderRepository.findById(multiLevelApproval.getBusinessUid());
                v2PmOrder.setApprovalStatus(ApprovalConstant.PM_APPROVAL_STATUS_DISAGREE);
                v2_PmOrderRepository.save(v2PmOrder);
                //审批不通过,修改工单状态
                generateSparePartsBuyList(v2PmOrder.getId(), SimplePartsConstant.PM_ORDER, ApprovalConstant.WORK_DETAIL_STATUS_DISAGREE);
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
        this.sendWxMsg(weChatIdList,message);
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
    public MultiLevelApprovalConfig getApproveConfig(String busiType, BigDecimal amount) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = new ArrayList<>(Arrays.asList(user.getSiteUID(),user.getHospitalUID(),user.getInstitutionUID(),user.getTenantUID()));
        for (String orgUid: orgUidList) {
            MultiLevelApprovalConfig approveConfig = getApproveConfigByOrgUid(orgUid, busiType, amount);
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
            if(ApprovalConstant.APPROVAL_PARTS_STOCK.equals(busiType)){
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
                if((int)configMap.get("minAmount") < amount.doubleValue() && amount.doubleValue() <= (int)configMap.get("maxAmount")){
                    return config;
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


    public MultiLevelApproval updateByBusinessNumber(Integer approvalStatus,String requestCode) {
        return multiLevelApprovalRepository.updateByBusinessNumber(approvalStatus,requestCode);
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
                            this.sendWxMsg(weChatIdList, message);
                        }
                        switch (multiLevelApproval.getBusinessType()){
                            case ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT://维修
                                V2_WorkOrder v2WorkOrder = v2_WorkOrderRepository.findById(multiLevelApproval.getBusinessUid());
                                v2WorkOrder.setApprovalStatus(WorkOrderConstant.APPROVAL_STATUS_AGREE);//审批同意
                                v2_WorkOrderRepository.save(v2WorkOrder);
                                //审批校验,生成申领申购单
                                generateSparePartsBuyList(v2WorkOrder.getId(), SimplePartsConstant.WORK_ORDER, ApprovalConstant.WORK_DETAIL_STATUS_AGREE);
                                break;
                            case ApprovalConstant.BUSINESS_TYPE_ASSET_MAINTAIN_INT://保养
                                V2_PmOrder v2PmOrder = v2_PmOrderRepository.findById(multiLevelApproval.getBusinessUid());
                                v2PmOrder.setApprovalStatus(ApprovalConstant.PM_APPROVAL_STATUS_AGREE);
                                v2_PmOrderRepository.save(v2PmOrder);
                                //审批校验,生成申领申购单
                                generateSparePartsBuyList(v2PmOrder.getId(), SimplePartsConstant.PM_ORDER, ApprovalConstant.WORK_DETAIL_STATUS_AGREE);
                                break;
                            default:
                                throw new RuntimeException("New Business type not handled yet");
//                                this.editParts(multiLevelApproval,ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
                        }
                    }else {
                        com.ge.apm.domain.ApprovalNode node = approvalNodes.get(approvalLevel);
                        if(node != null){
                            boolean usingRole = node.getUsingRole();
                            if (usingRole) {//角色审批
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
                            //审批同意时 给审批人发送微信
                            String message = ApprovalConstant.WX_MESSAGE_APPROVAL_1+ApprovalConstant.WX_MESSAGE_APPROVAL_2+date.getBusinessNumber()+ApprovalConstant.WX_MESSAGE_APPROVAL_3;
                            this.sendWxMsg(weChatIdList,message);
                        }
                    }
                }
            }
        }
    }

    public void sendWxMsg(List<String>weChatIdList,String message){
        if(weChatIdList == null) return;
        for (int i = 0; i < weChatIdList.size(); i++) {
            String str = weChatIdList.get(i);
            if(str != null){
                Map<String,Object> map = new HashMap<>();
                map.put("_openId",str);
                map.put("_templateId","no_template");
                map.put("message", ApprovalConstant.WX_MESSAGE_APPROVAL+TimeUtils.getStrDate(new Date(), "yyyy-MM-dd HH:mm:ss") + message);
                wechatSendMsgService.sendWxMsg(map);
            }
        }
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetail(String woId) {
        return pmOrderService.getWorkOrderDetails(woId);
    }

    /**
     * @param woId
     * @param type 0 维修工单, 1 保养工单
     * @param isAgreeType 0 不同意, 1 同意
     */
    private void generateSparePartsBuyList(String woId, int type, int isAgreeType) {
        List<V2_WorkOrder_Detail> approvedDetailsNew = getWorkOrderDetail(woId);
        if(approvedDetailsNew.isEmpty()){
            return;
        }
        if (isAgreeType == ApprovalConstant.WORK_DETAIL_STATUS_AGREE) {
            //最后一个审批通过的时候 生成相应单子
            List<V2_WorkOrder_Detail> accessorySparePartsBuyList = new ArrayList<>();//申购
            List<V2_WorkOrder_Detail> accessorySparePartsRequestList = new ArrayList<>();//申领
            for (int i = 0; i < approvedDetailsNew.size(); i++) {
                V2_WorkOrder_Detail v2_workOrder_detail = approvedDetailsNew.get(i);
                v2_workOrder_detail.setApproved(Boolean.TRUE);
                v2_WorkOrderDetailRepository.save(v2_workOrder_detail);
                if (ApmConstant.TREASURY_USE.equals(v2_workOrder_detail.getPartsType())) {//库房领用
                    accessorySparePartsRequestList.add(v2_workOrder_detail);
                } else if (ApmConstant.MEDICAL_WORKERS_PURCHASING.equals(v2_workOrder_detail.getPartsType())) {//医工采购
                    accessorySparePartsBuyList.add(v2_workOrder_detail);
                }
            }
            if (!accessorySparePartsRequestList.isEmpty()) {
                simplePartsRequestWorkOrderService.savePartsRequest(accessorySparePartsRequestList, type);//生成申领单
            }
            if (!accessorySparePartsBuyList.isEmpty()) {
                simplePartsRequestWorkOrderService.savePartsPurchase(accessorySparePartsBuyList, type);//生成申购单
            }
        }else{
            for (int i = 0; i < approvedDetailsNew.size(); i++) {
                V2_WorkOrder_Detail v2_workOrder_detail = approvedDetailsNew.get(i);
                if(v2_workOrder_detail.getApproved() != null && v2_workOrder_detail.getApproved()){
                    continue;
                }
                v2_workOrder_detail.setApproved(Boolean.FALSE);
                v2_WorkOrderDetailRepository.save(v2_workOrder_detail);
            }
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
        }
        return null;
    }

    /*
     * 判断有没有配置多级审核
     */
    public MultiLevelApprovalConfig checkMultiLevelApprovalPersonAndSubmit(ApprovableTarget approvableTarget) {
        MultiLevelApprovalConfig multiLevelApprovalConfig = this.getFirstApprovalList(approvableTarget);
        return multiLevelApprovalConfig;
    }
}
