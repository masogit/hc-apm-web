package com.ge.apm.service.asset;

import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import webapp.framework.web.WebUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AssetScrapService  extends MicroServiceInvoker {

    protected static final   Logger logger = LoggerFactory.getLogger(MicroServiceInvoker.class);

    public void apply(Integer assetId, String applyReason, String approvalReason, String equipmentDisposalMethod, String customDisposalMethod,
                      String signatureId, List<V2_BlobObject> attachments,boolean checkOrderWhenScrap) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("assetId",String.valueOf(assetId));
        map.put("applyReason",applyReason);
        map.put("reviewReason",approvalReason);
        map.put("equipmentDisposalMethod",equipmentDisposalMethod);
        map.put("customDisposalMethod",customDisposalMethod);
        map.put("auditSignature",signatureId);
        map.put("attachments",attachments);
        map.put("checkOrderWhenScrap",checkOrderWhenScrap);
        String token = UserContextService.getAccessToken();
        HttpEntity<HashMap> requestEntity = new HttpEntity<HashMap>(map, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.ASSET_SCRAP_APPLY);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
        if (isOkay(response)) {
            WebUtil.addSuccessMessage("申请成功");
            logger.info("AssetScrapService apply assetId:{},result:{}", assetId , responseMap.get("data"));
        }else {
            WebUtil.addErrorMessage((String)responseMap.get("message"));
            logger.error("AssetScrapService apply error  assetId:{},result:{}", assetId , responseMap.get("data"));
        }
    }

    public void autoResolve(Integer assetId, Integer resolverId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("assetId", String.valueOf(assetId));
        map.put("resolverId", resolverId);
        String token = UserContextService.getAccessToken();
        HttpEntity<HashMap> requestEntity = new HttpEntity<>(map, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.ASSET_SCRAP_AUTO_RESOLVE);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
        if (isOkay(response)) {
            logger.info("AssetScrapService auto resolve assetId:{},result:{}", assetId, responseMap.get("data"));
        } else {
            WebUtil.addErrorMessage((String) responseMap.get("message"));
            logger.error("AssetScrapService auto resolve error  assetId:{},result:{}", assetId, responseMap.get("data"));
        }
    }

    public String addAccess(String token, String assetUid, V2_WorkOrder_Detail workOrderDetail){
        HttpEntity<V2_WorkOrder_Detail> requestEntity = new HttpEntity<V2_WorkOrder_Detail>(workOrderDetail, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.ACCESSADD);
        String url = gatewayURL.replace("{assetUid}",assetUid);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            HashMap data = (LinkedHashMap)responseMap.get("data");
            workOrderDetail.setId((String)data.get("id"));
            return (String) responseMap.get("message");
        } else {
            return null;
        }
    }

    public boolean processUnfinishedNurseSiteMaintain(String token, List<String> assetUidList, String operationType){
        Map<String, Object> param = MapUtil.newHashMap();
        param.put("operationType", operationType);
        param.put("assetUidList", assetUidList);
        HttpEntity requestEntity = new HttpEntity<>(param, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.PROCESS_UN_FINISHED_NURSE_SITE_MAINTAIN);
        String url = gatewayURL.replace("{assetId}", "0");
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        return isOkay(response);
    }
}
