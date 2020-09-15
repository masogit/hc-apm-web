package com.ge.apm.service.approval;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.MultiLevelApprovalConfigRepository;
import com.ge.apm.dao.MultiLevelApprovalRepository;
import com.ge.apm.dao.simpleparts.SimplePartsRequestRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.MultiLevelApproval;
import com.ge.apm.domain.MultiLevelApprovalConfig;
import com.ge.apm.domain.simpleparts.SimplePartsRequest;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.ApprovableTarget;
import com.ge.apm.domain.pure.ApprovalNode;
import com.ge.apm.service.common.WechatSendMsgService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.simpleparts.SimplePartsRequestService;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public MultiLevelApprovalConfig getFirstApprovalList(ApprovableTarget approvableTarget,String siteUid) {
        // 需要调用服务端提供的接口
        BigDecimal totalPrice = approvableTarget.getTotalPrice();
        MultiLevelApprovalConfig multiLevelApprovalConfig = this.getApproveConfig(siteUid,ApprovalConstant.APPROVAL_PARTS_STOCK, SimplePartsConstant.ALL_GROUP,totalPrice);
        return multiLevelApprovalConfig;
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
     * {
     * 1: {
     * "role": ["2", "4"]
     * },
     * 2: {
     * "user": ["dce025bf9f7a43fb88b3c986d5a922b7", "fa4a76d7259b459286e23699e320ee29"]
     * }
     * }
     */
    public MultiLevelApproval getMultiLevelApprovalPersonAndSubmit(ApprovableTarget approvableTarget, Integer level, boolean isApproval) {
        if (level == null) {
            level = 1;
        }
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        MultiLevelApprovalConfig multiLevelApprovalConfig = this.getFirstApprovalList(approvableTarget,userAccount.getSiteUID());
        if (multiLevelApprovalConfig != null) {
            String approvalItem = multiLevelApprovalConfig.getApprovalItem();
            if (null != approvalItem) {
                MultiLevelApproval multiLevelApproval = new MultiLevelApproval();
                List<String> weChatIdList = new ArrayList<>();
                List< ApprovalNode> approvalNodes = JSON.parseObject(approvalItem, new TypeReference<List<ApprovalNode>>() {});
                if (approvalNodes != null) {
                    ApprovalNode approvalNode = approvalNodes.get(0);
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
                        multiLevelApproval.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
                        multiLevelApproval.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING);
                        multiLevelApproval.setApprovalLevel(level);
                        multiLevelApproval.setApprovalNumber(autoGenerateFinacingNum());
                        multiLevelApproval.setApprovalIng(false);
                        multiLevelApproval.setApprovalLevelNow(level);
                        multiLevelApproval.setWorkflowConfigApproveUid(multiLevelApprovalConfig.getId());
                        multiLevelApproval.setInitiateApproverUid(userAccount.getUid());
                        multiLevelApproval.setCreatedDate(new Date());
                        multiLevelApproval.setCreatedBy(userAccount.getLoginName());
                        multiLevelApproval.setInstitutionUID(userAccount.getInstitutionUID());
                        multiLevelApproval.setHospitalUID(userAccount.getHospitalUID());
                        multiLevelApproval.setTenantUID(userAccount.getTenantUID());
                        multiLevelApproval.setSiteUID(userAccount.getSiteUID());
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
     * 供备件审批使用--审批同意
     * @return
     */
    public void getMultiLevelApprovalPersonAndSubmit(MultiLevelApproval multiLevelApprovalNew, String approvalOpinion) {
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
                        String businessTypee = this.getBusinessTypee(multiLevelApproval.getBusinessType());
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
                            String message = ApprovalConstant.WX_MESSAGE_AGREE_1 + businessTypee + ApprovalConstant.WX_MESSAGE_AGREE_2 + multiLevelApproval.getBusinessNumber() + ApprovalConstant.WX_MESSAGE_AGREE_3;
                            this.sendWxMsg(weChatIdList, message);
                        }
                        List<SimplePartsRequest> partsRequests = partsRequestService.findByRequestCode(multiLevelApproval.getBusinessNumber());
                        if (null != partsRequests) {
                            for (int i = 0; i < partsRequests.size(); i++) {
                                SimplePartsRequest partsRequest = partsRequests.get(i);
                                partsRequest.setProposerStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
                                partsRequestRepository.save(partsRequest);
                            }
                        }
                    }else {
                        ApprovalNode node = approvalNodes.get(approvalLevel);
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

    /**
     * 供备件审批使用--审批不同意
     * @param multiLevelApproval
     * @return
     */
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
        String initiateApproverUid=null;
        String businessTypee = this.getBusinessTypee(multiLevelApproval.getBusinessType());
        String businessNumber = multiLevelApproval.getBusinessNumber();
        if(null !=list){
            for (int i = 0; i < list.size(); i++) {
                MultiLevelApproval multi = list.get(i);
                if(null != multi.getInitiateApproverUid() && !"".equals(multi.getInitiateApproverUid())){
                    initiateApproverUid = multi.getInitiateApproverUid();
                }
                multi.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE);
                multi.setApprovalLevelNow(ApprovalConstant.APPROVAL_NOW_DISAGREE);
                multiLevelApprovalRepository.save(multi);
            }
        }
        List<SimplePartsRequest> partsRequests = partsRequestService.findByRequestCode(multiLevelApproval.getBusinessNumber());
        if(null != partsRequests){
            for (int i = 0; i < partsRequests.size(); i++) {
                SimplePartsRequest partsRequest = partsRequests.get(i);
                partsRequest.setProposerStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE);
                partsRequestRepository.save(partsRequest);
            }
        }
        //审批不同意时 给发起人发送微信
        List<String>weChatIdList = new ArrayList<>();
        UserAccount user = userAccountRepository.getByUid(initiateApproverUid);
        if (user != null && user.getWeChatId() !=null){
            weChatIdList.add(user.getWeChatId());
        }
        String message = ApprovalConstant.WX_MESSAGE_DISAGREE_1+businessTypee+ApprovalConstant.WX_MESSAGE_DISAGREE_2+businessNumber+ApprovalConstant.WX_MESSAGE_DISAGREE_3;
        this.sendWxMsg(weChatIdList,message);

    }
    public String getBusinessTypee(Integer businessType){
        if(null == businessType){
            return null;
        }
        switch (businessType){
            case 1:
                return ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_REQUEST;
            case 2:
                return ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_PURCHASE;
        }
        return null;
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
    /**
     *
     * @param busiType
     * @param assetType
     * @param amount
     * @return MultiLevelApprovalConfig
     * @date 2020/7/30 16:00
     * 根据金额查询审批配置信息
     */
    public MultiLevelApprovalConfig getApproveConfig(String siteUid,String busiType,String assetType, BigDecimal amount) {
        List<MultiLevelApprovalConfig> configList = multiApprovalConfigDao.getConfigByCondition(siteUid,busiType, assetType);
        if(null != configList &&configList.size()>0){
            for (MultiLevelApprovalConfig config : configList) {
                if(null != config.getConfigration()){
                    Map configMap = JSON.parseObject(config.getConfigration(), Map.class);
                    if((int)configMap.get("minAmount") <= amount.doubleValue() && amount.doubleValue() <= (int)configMap.get("maxAmount")){
                        return config;
                    }
                    if(null == configMap.get("minAmount") && null == configMap.get("maxAmount")){
                        return config;
                    }
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
}
