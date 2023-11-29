package com.ge.apm.service.approval;


import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.ApprovableTarget;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.core.WechatParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WxMessageService extends MicroServiceInvoker {


    private static final Logger logger = LoggerFactory.getLogger(WxMessageService.class);

    public void sendMessage(AssetInfo assetInfo, String approvalName, ApprovableTarget
        approvableTarget, List<UserAccount> userAccounts, String linkUrl, UserAccount user, String reason) {
        HashMap<String, Object> params = combineMsg(approvalName, approvableTarget, assetInfo,linkUrl,user,reason);
        List<WechatParam> paramList = new ArrayList<>();
        userAccounts= userAccounts.stream().distinct().collect(Collectors.toList());
        for (UserAccount userAccount : userAccounts) {
            WechatParam wechatParam = JSON.parseObject(JSON.toJSONString(params), WechatParam.class);
            wechatParam.setOpenId(userAccount.getWeChatId());
            wechatParam.setAppAlias(userAccount.getAppAlias());
            paramList.add(wechatParam);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("paramList", paramList);
        map.put("wxTemplateId", "new_workorder_change_template_id");
        String token = UserContextService.getAccessToken();
        send(map,token);

    }

    public void send(Map<String, Object> map,String token){

        HttpEntity<Map> requestEntity = new HttpEntity<>(map, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.SEND_WE_CHAT);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (!isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            String str = responseMap.get("message").toString();
            throw new RuntimeException(str);
        }
    }


    private HashMap<String, Object> combineMsg(String woSteps, ApprovableTarget approvableTarget, AssetInfo assetInfo,String linkUrl,UserAccount user,String reason) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword1", approvableTarget.getBusinessNumber() + "  业务类型：" + setBusinessTypeName(approvableTarget.getBusinessType()));
        params.put("keyword2", reason);
        params.put("keyword3", assetInfo.getName() + "  所属科室：" + assetInfo.getClinicalDeptName());
        if(woSteps.equals("审批通过") || woSteps.equals("审批失败")){
            params.put("keyword4", woSteps);
        }else{
            params.put("keyword4", "待" + woSteps + "审批");
        }
        String handler = user.getName();
        params.put("keyword5", handler);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(linkUrl)){
            params.put("linkUrl", linkUrl);
        }
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
        }
        return businessTypeName;
    }


    @Autowired
    private AssetCreateService acService;


    @Autowired
    private UserAccountRepository userAccountRepository;

    @Async
    public  void submitAssetApprovalSendMessage(AssetInfo assetInfo,String token) {
        List<UserAccount> userAccounts = acService.getAssetApprovalUser(assetInfo.getSiteUID());
        List<UserAccount> userAccounts2=userAccountRepository.findByClinicalDeptUID(assetInfo.getClinicalDeptUID());
        Set<Integer> uids = userAccounts.stream().map(UserAccount::getId).collect(Collectors.toSet());
        List<UserAccount> resultUser = userAccounts2.stream().filter(item -> uids.contains(item.getId())).distinct().collect(Collectors.toList());
        logger.info("submitApprovalSendMessage users:{}",JSON.toJSONString(resultUser));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = simpleDateFormat.format(assetInfo.getCreatedDate());
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword1","资产入库审核");
        params.put("keyword2", "待审核");
        params.put("keyword3", dateString);
        List<WechatParam> paramList = new ArrayList<>();
        resultUser= resultUser.stream().distinct().collect(Collectors.toList());
        for (UserAccount userAccount : resultUser) {
            WechatParam wechatParam = JSON.parseObject(JSON.toJSONString(params), WechatParam.class);
            wechatParam.setOpenId(userAccount.getWeChatId());
            wechatParam.setAppAlias(userAccount.getAppAlias());
            paramList.add(wechatParam);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("paramList", paramList);
        map.put("wxTemplateId", "partorder_template_id");
        send(map,token);
    }
}
