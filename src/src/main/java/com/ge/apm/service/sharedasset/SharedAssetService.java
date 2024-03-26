/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.sharedasset;

import com.alibaba.fastjson.JSON;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author 212579464
 */
@Component
public class SharedAssetService extends MicroServiceInvoker {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    public void pushWxMsg(String requestId) {
        String url = getGatewayURL(GatewayURL.SHARED_ASSET_PUSH_MESSAGE);
        url = url.replace("{requestId}", requestId);
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        logger.info("pushWxMsg request: " + JSON.toJSONString(requestEntity));
        runAsync(url, HttpMethod.POST,requestEntity);
    }

    public void remoteAcceptRent(String requestId) {
        String url = getGatewayURL(GatewayURL.REMOTE_ACCEPT_RENT);
        url = url.replace("{requestId}", requestId);
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        logger.info("Remote confirm rent request: " + JSON.toJSONString(requestEntity));
        runAsync(url,HttpMethod.POST,requestEntity);
    }

    public ResponseEntity<LinkedHashMap> getAssigneeList(String requestId) {
    	logger.info("RequestId: " + requestId);
        String url = getGatewayURL(GatewayURL.ASSIGNEE_LIST);
        url = url.replace("{requestId}", requestId);
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        logger.info("url for getAssigneeList: " + url);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        logger.info("Get assignee list " + (isOkay(response) ? "successfully." : "failed."));
        return response;
    }

    public void remoteAssignRequest(String requestId, String assigneeUID) {
        String url = getGatewayURL(GatewayURL.REMOTE_ASSIGN_REQUEST);
        url = url.replace("{requestId}", requestId);
        url = url.replace("{assigneeUID}", assigneeUID);
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        runAsync(url,HttpMethod.PUT,requestEntity);
    }

    @Deprecated
    public boolean onDuty() {
        String url = getGatewayURL(GatewayURL.ALLOCATION_ON_DUTY);
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<>(header);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        logger.info(JSON.toJSONString(response));
        return Optional.ofNullable(response)
                .map(ResponseEntity::getBody)
                .map(body -> (Map) body.get("data"))
                .map(data -> (Boolean) data.get("onDuty"))
                .orElse(true);
    }


    public ResponseEntity<LinkedHashMap> leaseAssets(Map<String, Object> requestData) {
        logger.info("Sending data : {} to LEASE_SHORTCUT", JSON.toJSONString(requestData));
        String url = getGatewayURL(GatewayURL.LEASE_SHORTCUT);
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<Map> requestEntity = new HttpEntity<>(requestData, header);
        ResponseEntity<LinkedHashMap> responseEntity = template.postForEntity(url, requestEntity, LinkedHashMap.class);
        logger.info("Create request " + (isOkay(responseEntity) ? "successfully." : "failed."));
        logger.info("Response: {}", JSON.toJSONString(responseEntity));
        return responseEntity;
    }

    public ResponseEntity<LinkedHashMap> returnAssets(Map<String, Object> requestData) {
        logger.info("Sending data : {} to RETURN_SHORTCUT", JSON.toJSONString(requestData));
        String url = getGatewayURL(GatewayURL.RETURN_SHORTCUT);
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<Map> requestEntity = new HttpEntity<>(requestData, header);
        ResponseEntity<LinkedHashMap> responseEntity = template.postForEntity(url, requestEntity, LinkedHashMap.class);
        logger.info("Create request " + (isOkay(responseEntity) ? "successfully." : "failed."));
        logger.info("Response: {}" ,JSON.toJSONString(responseEntity));
        return responseEntity;
    }

    public ResponseEntity<LinkedHashMap> rejectApply(GatewayURL gatewayUrl,Map<String, Object> requestData) {
        logger.info("rejectApply data : {} ", JSON.toJSONString(requestData));
        String url = getGatewayURL(gatewayUrl);
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<Map> requestEntity = new HttpEntity<>(requestData, header);
        ResponseEntity<LinkedHashMap> responseEntity = template.postForEntity(url, requestEntity, LinkedHashMap.class);
        logger.info("Create request " + (isOkay(responseEntity) ? "successfully." : "failed."));
        logger.info("Response: {}" ,JSON.toJSONString(responseEntity));
        return responseEntity;
    }

    private void runAsync(String url,HttpMethod method, HttpEntity<String> requestEntity) {
        CompletableFuture.runAsync(()->{
            ResponseEntity<LinkedHashMap> response = template.exchange(url,method, requestEntity, LinkedHashMap.class);
            logger.info("Message send " + (isOkay(response) ? "successfully." : "failed."));
        });
    }

    public void asynRemoteCall(GatewayURL gatewayUrl, HttpMethod method, Map<String, Object> requestData, Function<String,String> function){
        String url = getGatewayURL(gatewayUrl);
         url =function.apply(url);
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity requestEntity = new HttpEntity<>(requestData, header);
        runAsync(url,method,requestEntity);
    }

}


