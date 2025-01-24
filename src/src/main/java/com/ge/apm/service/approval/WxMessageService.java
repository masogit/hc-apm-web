package com.ge.apm.service.approval;


import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.ConfigItemValueRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.ApprovableTarget;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.healthcare.cn.common.core.message.MSGEvent;
import com.ge.healthcare.cn.common.core.message.WechatMsgEvent;
import com.ge.healthcare.cn.common.mq.RabbitConfig;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WxMessageService extends MicroServiceInvoker {

    @Autowired
    private MqUtils mqUtils;

    private static final Logger logger = LoggerFactory.getLogger(WxMessageService.class);

    public void sendMessage(List<AssetInfo> assetInfos, String approvalName, ApprovableTarget
        approvableTarget, List<UserAccount> userAccounts, String linkUrl, UserAccount user, String reason,MSGEvent msgEvent) {
        HashMap<String, String> params = combineMsg(approvalName, approvableTarget, assetInfos,user,reason);
        List<String> sendUids= userAccounts.stream().filter(item-> StringUtils.isNotBlank(item.getUid())).map(UserAccount::getUid).collect(Collectors.toList());
        sendWxMsgToMq(params,linkUrl,msgEvent,sendUids,null);
    }

    private HashMap<String, String> combineMsg(String woSteps, ApprovableTarget approvableTarget, List<AssetInfo> assetInfos ,UserAccount user,String reason) {
        AssetInfo assetInfo = assetInfos.get(0);
        String status="status";
        HashMap<String, String> params = new HashMap<>();
        String handler = user.getName();
        params.put("orderCode", approvableTarget.getBusinessNumber());
        params.put("assetSum", String.valueOf(assetInfos.size()));
        params.put("dept",assetInfo.getClinicalDeptName());
        params.put("content", reason);
        params.put("assetName", assetInfo.getName());
        if(woSteps.equals("审批通过") || woSteps.equals("审批失败")){
            params.put(status, woSteps);
        }else{
            params.put(status, "待" + woSteps + "审批");
        }
        params.put("assetStaff", handler);
        params.put("businessType",  setBusinessTypeName(approvableTarget.getBusinessType()));
        return params;
    }

    private String setBusinessTypeName(Integer businessType) {
        String businessTypeName=null;
        switch (businessType) {
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_REQUEST_INT:
                businessTypeName=ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_REQUEST;
                break;
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_PURCHASE_INT:
                businessTypeName=ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_PURCHASE;
                break;
            case ApprovalConstant.BUSINESS_TYPE_FILE_INFORMATION_MODIFICATION_INT:
                businessTypeName=(ApprovalConstant.BUSINESS_TYPE_FILE_INFORMATION_MODIFICATION);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_OVERHAUL_INT:
                businessTypeName=(ApprovalConstant.APPROVAL_ASSET_BIG_REPAIR);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_BORROW:
                businessTypeName=(ApprovalConstant.APPROVAL_ASSET_BORROW_CHI);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_RECEIVE:
                businessTypeName=(ApprovalConstant.APPROVAL_ASSET_RECEIVE_CHI);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT:
                businessTypeName=(ApprovalConstant.APPROVAL_ASSET_REPAIR_CHi);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_MAINTAIN_INT:
                businessTypeName=(ApprovalConstant.APPROVAL_ASSET_MAINTAIN_CHi);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_BORROW_RETURN:
                businessTypeName=(ApprovalConstant.APPROVAL_ASSET_BORROW_RETURN_CHI);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_RECEIVE_RETURN:
                businessTypeName=(ApprovalConstant.APPROVAL_ASSET_RECEIVE_RETURN_CHI);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_TRANSFER:
                businessTypeName=(ApprovalConstant.APPROVAL_ASSET_TRANSFER_CHI);
                break;
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_RETURN_INT:
                businessTypeName=(ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_RETURN);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ADVERSE_EVENTS:
                businessTypeName=(ApprovalConstant.APPROVAL_ADVERSE_EVENTS);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP:
                businessTypeName=(ApprovalConstant.APPROVAL_ASSET_SCRAP_CHI);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT:
                businessTypeName=(ApprovalConstant.APPROVAL_ASSET_DEPARTMENT_CONVERT_CHI);
                break;
            case ApprovalConstant.BUSINESS_TYPE_BATH_ASSET_DEPARTMENT_CONVERT:
                businessTypeName=(ApprovalConstant.APPROVAL_BATH_ASSET_DEPARTMENT_CONVERT_CHI);
                break;
            default:
                break;
        }
        return businessTypeName;
    }


    @Autowired
    private AssetCreateService acService;


    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private ConfigItemValueRepository configItemValueRepository;

    public  void submitAssetApprovalSendMessage(AssetInfo assetInfo,String status) {
        try {
            String value = configItemValueRepository.findByKeyAndSiteForUseParent(assetInfo.getSiteUID(),assetInfo.getHospitalUID(),assetInfo.getInstitutionUID(),assetInfo.getTenantUID(),"assetReviewMessage");
            if(Boolean.FALSE.toString().equalsIgnoreCase(value)){
                return;
            }
            List<UserAccount> userAccounts = acService.getAssetApprovalUser(assetInfo.getSiteUID());
            List<UserAccount> userAccounts2=userAccountRepository.findByClinicalDeptUID(assetInfo.getClinicalDeptUID());
            Set<Integer> uids = userAccounts.stream().map(UserAccount::getId).collect(Collectors.toSet());
            List<UserAccount> resultUser = userAccounts2.stream().filter(item -> uids.contains(item.getId())).distinct().collect(Collectors.toList());
            logger.info("submitApprovalSendMessage users:{}",JSON.toJSONString(resultUser));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = simpleDateFormat.format(assetInfo.getCreatedDate());
            HashMap<String, String> params = new HashMap<>();
            params.put("businessType","资产入库审核");
            params.put("status", status);
            params.put("date", dateString);
            params.put("assetName", assetInfo.getName());
            params.put("dept", assetInfo.getClinicalDeptName());
            params.put("financingNum", assetInfo.getFinancingNum());
            params.put("departNum", assetInfo.getDepartNum());
            params.put("functionType", assetInfo.getFunctionType());
            List<String> sendUids = resultUser.stream().distinct().map(UserAccount::getUid).collect(Collectors.toList());
            sendWxMsgToMq(params,null,MSGEvent.ASSET_AUDIT,sendUids,null);
        }catch (Exception e){
            logger.error("submitAssetApprovalSendMessage error e:",e);
        }

    }

    public void sendWxMsgToMq(Map<String, String> eventParams , String link, MSGEvent event, List<String> userUIds, List<Integer> userIds) {
        WechatMsgEvent msgEvent=new WechatMsgEvent(event,eventParams);
        msgEvent.setLink(link);
        if(CollectionUtils.isEmpty(userUIds) && CollectionUtils.isNotEmpty(userIds) ){
            Set<Integer> setIds =userIds.stream().collect(Collectors.toSet());
            List<UserAccount> accounts = userAccountRepository.getByIds(setIds);
            userUIds = accounts.stream().distinct().filter(user -> ObjectUtil.isNotEmpty(user.getUid())).map(UserAccount::getUid).collect(Collectors.toList());
        }
        msgEvent.setUserIds(userUIds);
        mqUtils.sendWxMsgToMq(msgEvent, RabbitConfig.EVENT_NOTIFICATION_CHANGE,RabbitConfig.WX_MSG_ROUTING_KEY);
    }
}
