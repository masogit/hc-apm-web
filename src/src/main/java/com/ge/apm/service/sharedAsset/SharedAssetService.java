/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.sharedAsset;

import com.alibaba.fastjson.JSON;
import com.ge.apm.domain.SharedAssetRequest;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.PmOrderActionForm;
import com.google.common.base.Strings;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

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
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        logger.info("Message send " + (isOkay(response) ? "successfully." : "failed."));
    }

    public void remoteAcceptRent(String requestId) {
        String url = getGatewayURL(GatewayURL.REMOTE_ACCEPT_RENT);
        url = url.replace("{requestId}", requestId);
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        logger.info("Remote confirm rent request: " + JSON.toJSONString(requestEntity));
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        logger.info("Remote confirm rent " + (isOkay(response) ? "successfully." : "failed."));
    }

    public ResponseEntity<LinkedHashMap> getAssigneeList(String requestId) {
        String url = getGatewayURL(GatewayURL.ASSIGNEE_LIST);
        url = url.replace("{requestId}", requestId);
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
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
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.PUT, requestEntity, LinkedHashMap.class);
        logger.info("Assignee request " + (isOkay(response) ? "successfully." : "failed."));
    }
}


