/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.pm;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.PmOrderActionForm;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author 212579464
 */
@Component
public class PmOrderRequestApiService extends MicroServiceInvoker {
	
	
    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());


    public Map<String, Object> getApproveByAsset(String token, String assetUid) {
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        String gatewayURL = getGatewayURL(GatewayURL.GET_APPROVE_BY_ASSET);
        String url = gatewayURL.replace("{assetUid}",assetUid);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (LinkedHashMap<String, Object>) responseMap;
        } else {
            return null;
        }
    }

    public Map<String, Object> getPmOrderSteps(String token, String pmOrderId) {
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        String gatewayURL = getGatewayURL(GatewayURL.PM_ORDER_STEPS);
        String url = gatewayURL.replace("{pmOrderId}",pmOrderId);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (LinkedHashMap<String, Object>) responseMap;
        } else {
            return null;
        }
    }

    public String invokePmOrderAction(String token, PmOrderActionForm formData, String actionName, String woId) {
        HttpEntity<PmOrderActionForm> requestEntity = new HttpEntity<PmOrderActionForm>(formData, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.PM_ORDER_ACTION);
        String url = gatewayURL.replace("{pmOrderId}", woId);

        ResponseEntity<LinkedHashMap> response = template.exchange(url,HttpMethod.PUT,requestEntity,LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            logger.info("Invoke PmOrderAction "+actionName+" has finished "+responseMap.get("data"));
            return (String) responseMap.get("bizStatusCode");
        } else {
            return null;
        }
    }

    public String invokePmOrderActionBatch(String token, ArrayList<PmOrderActionForm> formDataList, String actionName) {
        HttpEntity<ArrayList<PmOrderActionForm>> requestEntity = new HttpEntity<ArrayList<PmOrderActionForm>>(formDataList, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.BATCH_TAKE_ORDER);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL,HttpMethod.PUT,requestEntity,LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            logger.info("Invoke PmOrderAction "+actionName+" has finished "+responseMap.get("data"));
            return (String) responseMap.get("bizStatusCode");
        } else {
            return null;
        }
    }

    public Map<String, Object> createPmRequest(String token,PmOrderActionForm formData ){
        HttpEntity<PmOrderActionForm> requestEntity = new HttpEntity<>(formData, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.PM_REQUEST_CREATE);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
        if (isOkay(response)) {
            return (LinkedHashMap<String, Object>) responseMap.get("data");
        } else {
           if(responseMap.get("bizStatusCode").equals("INVALID_PARAM")){
               return responseMap;
           }
            return null;
        }
    }
    
    @Async
    public void createPmOrdersByBatch(String token,String requestBody){
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.CREATE_PM_ORDER_BY_BATCH);
        ResponseEntity<String> response = template.postForEntity(gatewayURL, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
	        	String result = (String) response.getBody();
	        	logger.info("invoke createPmOrdersByBatch over,result is {}",result);
        }else {
        		logger.error("rmi failure,code is {},body is {}",response.getStatusCode(),response.getBody());
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public String autoGenerateFinacingNum(LinkedHashMap<String,Object> requestBody){
        HttpEntity<LinkedHashMap> requestEntity = new HttpEntity<LinkedHashMap>(requestBody);
        String gatewayURL = getGatewayURL(GatewayURL.AUTO_GENERATE_FINANCING_NUM);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (response.getStatusCode() == HttpStatus.OK) {
        		LinkedHashMap<String, Object> responseMap =  response.getBody();
        		logger.info("invoke autoGenerateFinacingNum over,result is {}",responseMap.get("data"));
            return (String) responseMap.get("data");
        }else {
        		logger.error("rmi failure,code is {},body is {}",response.getStatusCode(),response.getBody());
        		return null;
        }
    } 
}


