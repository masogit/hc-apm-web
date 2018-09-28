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
import java.util.Optional;

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
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.PUT, requestEntity, LinkedHashMap.class);
        logger.info("Assignee request " + (isOkay(response) ? "successfully." : "failed."));
    }

    public ResponseEntity<LinkedHashMap> borrowByShortcut(String clinicalDeptUID, String assetInfoUID, String chargingBeginTime, String chargingEndTime, String reqType) {
        String url = getGatewayURL(GatewayURL.RENT_SHORTCUT);
        url = url.replace("{clinicalDeptUID}", clinicalDeptUID);
        url = url.replace("{assetInfoUID}", assetInfoUID);
        url = url.replace("{chargingBeginTime}", chargingBeginTime);
        url = url.replace("{chargingEndTime}", chargingEndTime);
        url = url.replace("{reqType}", reqType);
        String token = UserContextService.getAccessToken();
        token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI0MTExdGp6eDEiLCJhdXRoIjoiQXNzZXRIZWFkLEFzc2V0U3RhZmYsSVRBZG1pbixTaGFyZWRBc3NldE1nbXQiLCJwcm9maWxlIjp7InVzZXJBY2NvdW50Ijp7InRlbmFudFVJRCI6IjYzOTU0ZDQwOTRlNTRjNDFiYWUyMDBjMTMyN2JmOGY3IiwiaW5zdGl0dXRpb25VSUQiOiIzOTNjODFmNjMzNWI0MWU4YWVhZDQ3ZDU2YTM4YmFiZSIsImhvc3BpdGFsVUlEIjoiYWJmNjY3YTBmZWQ1NDU0YmI1NzFiMzRiN2UxZmM5ODAiLCJzaXRlVUlEIjoiNjY0NGJmNTE5YWZhNGM3NDg3NjcwOTg5Nzg1MTgzYTMiLCJpZCI6MzA0NSwidWlkIjoiMmE2YTA5ZGY3M2Y5NDM3Yzk1YzMyYWU2YWJjYjlhZDEiLCJsb2dpbk5hbWUiOiI0MTExdGp6eDEiLCJuYW1lIjoiNDExMXRqengxIiwibmFtZVB5Q29kZSI6IjQxMTF0anp4MSIsImVtYWlsIjoiIiwidGVsZXBob25lIjoiNDExMXRqengx55S16K-dIiwiaXNTdXBlckFkbWluIjpmYWxzZSwiaXNTaXRlQWRtaW4iOmZhbHNlLCJpc0xvY2FsQWRtaW4iOmZhbHNlLCJpc0FjdGl2ZSI6dHJ1ZSwib3JnTGV2ZWwiOjMsImlzT25saW5lIjpmYWxzZSwic2l0ZUlkIjoxNjMsImhvc3BpdGFsSWQiOjI1MzQsImxhc3RMb2dpblRpbWUiOjE1MzgxMTUxNTQyMjksIndlQ2hhdElkIjpudWxsLCJsZWFkZXJVc2VySWQiOm51bGwsInBhc3N3b3JkVXBkYXRlRGF0ZSI6bnVsbCwicGFzc3dvcmRFcnJvckNvdW50IjowLCJpc0xvY2tlZCI6ZmFsc2UsIndlY2hhdE5pY2tuYW1lIjoi5YyX5Yal5pyJ6bG8IiwiYWRtaW5Nb2RlIjpudWxsLCJsb2dpbkFkbWluIjpudWxsLCJwbGFpblBhc3N3b3JkIjpudWxsLCJpc1VuYnVuZGluZyI6ZmFsc2UsIm9yZ0luZm9JZCI6MjU5NCwib3JnVWlkIjoiMDUzYmE1NjY0ZDk4NDhkMTgyMmU4NGE3OTI5YzY2ZWMiLCJwYXJlbnRVaWQiOm51bGwsInJlZ2lvbiI6bnVsbCwidXNlclJvbGVOYW1lcyI6bnVsbCwiZ2VXZWNoYXQiOm51bGwsImdlT3AiOm51bGx9LCJ1c2VyUm9sZUxpc3QiOlt7ImlkIjoyLCJuYW1lIjoiQXNzZXRIZWFkIiwicm9sZURlc2MiOiLljLvlrablt6XnqIvlpITkuLvku7siLCJob21lUGFnZSI6Ii9ob21lQXNzZXRIZWFkLnhodG1sIn0seyJpZCI6NSwibmFtZSI6IklUQWRtaW4iLCJyb2xlRGVzYyI6IuWQjuWPsOi_kOiQpeeuoeeQhiIsImhvbWVQYWdlIjoiL3BvcnRhbC91YWEvdXNlckFjY291bnQvTGlzdC54aHRtbCJ9LHsiaWQiOjMsIm5hbWUiOiJBc3NldFN0YWZmIiwicm9sZURlc2MiOiLlt6XnqIvluIgiLCJob21lUGFnZSI6Ii9ob21lQXNzZXRTdGFmZi54aHRtbCJ9LHsiaWQiOjEwLCJuYW1lIjoiU2hhcmVkQXNzZXRNZ210Iiwicm9sZURlc2MiOiLorr7lpIfosIPliYLnrqHnkIbkurrlkZgiLCJob21lUGFnZSI6Ii8ifV0sInRlbmFudExpc3QiOltdLCJtdWx0aVRlbmFudCI6ZmFsc2V9LCJleHAiOjE1NDA3MTA0ODF9.RJ4uhIl_xlq_ntvMD252SKvbuaxEacxyomnlX29XFWdT8qrmf6KwcBSv8sugNu7vGw59e9vkDf3FwVQDQOwrOA";
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        logger.info("Request URL: " + url);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        logger.info("Create request " + (isOkay(response) ? "successfully." : "failed."));
        logger.info(JSON.toJSONString(response));
        return response;
    }

    public ResponseEntity<LinkedHashMap> returnByShortcut(String clinicalDeptUID, String assetInfoUID, String chargingBeginTime, String chargingEndTime, String reqType,
                                                          String isIntact, String compensationAmount, String reqComment) {
        String url = getGatewayURL(GatewayURL.RETURN_SHORTCUT);
        url = url.replace("{clinicalDeptUID}", clinicalDeptUID);
        url = url.replace("{assetInfoUID}", assetInfoUID);
        url = url.replace("{chargingBeginTime}", chargingBeginTime);
        url = url.replace("{chargingEndTime}", chargingEndTime);
        url = url.replace("{reqType}", reqType);
        url = url.replace("{isIntact}", isIntact);
        url = url.replace("{compensationAmount}", compensationAmount);
        url = url.replace("{reqComment}", reqComment);
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        logger.info("Request URL: " + url);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        logger.info("Create request " + (isOkay(response) ? "successfully." : "failed."));
        logger.info(JSON.toJSONString(response));
        return response;
    }
    
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
}


