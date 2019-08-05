/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.pm;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.PmOrderActionForm;
import com.google.common.base.Strings;
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
import java.util.function.Function;

/**
 *
 * @author 212579464
 */
@Component
public class PmOrderRequestApiService extends MicroServiceInvoker {
	
	
    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());


    public Map<String, Object> getApproveByAsset(String token, String assetUid,String name) {
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        String gatewayURL = getGatewayURL(GatewayURL.GET_APPROVE_BY_ASSET);
        String url = gatewayURL.replace("{assetUid}",assetUid);
        if(!Strings.isNullOrEmpty(name)){
            url = url.concat("?name=").concat(name);
        }
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class,name);
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


    String updateWorkOrderDetailPrice(String token, V2_WorkOrder_Detail workOrderDetail) {
        HttpEntity<V2_WorkOrder_Detail> requestEntity = new HttpEntity<V2_WorkOrder_Detail>(workOrderDetail, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.UPDATE_WODETAIL_PRICE);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.PUT, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (String) responseMap.get("message");
        } else {
            return null;
        }
    }


    //CANCEL_PM_ORDER
public String invokePmOrderCancel(String token, PmOrderActionForm formData, String actionName, String pmOrderId){
    HttpEntity<PmOrderActionForm> requestEntity = new HttpEntity<PmOrderActionForm>(formData, createHttpHeaders(token));
    String gatewayURL = getGatewayURL(GatewayURL.CANCEL_PM_ORDER);
    String url = gatewayURL.replace("{pmOrderId}", pmOrderId);

    ResponseEntity<LinkedHashMap> response = template.exchange(url,HttpMethod.DELETE,requestEntity,LinkedHashMap.class);
    if (isOkay(response)) {
        LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
        logger.info("Invoke PmOrderCancel "+actionName+" has finished "+responseMap.get("data"));
        return (String) responseMap.get("bizStatusCode");
    } else {
        return null;
    }
}

    //CANCEL_PM_ORDER_BATCH
    public String invokeBatchPmOrderCancel(String token, ArrayList<PmOrderActionForm> formDataList, String actionName){
        HttpEntity<ArrayList<PmOrderActionForm>> requestEntity = new HttpEntity<ArrayList<PmOrderActionForm>>(formDataList, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.PM_BATCH_CANCEL);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL,HttpMethod.DELETE,requestEntity,LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            logger.info("Invoke PmOrderCancel "+actionName+" has finished "+responseMap.get("data"));
            return (String) responseMap.get("bizStatusCode");
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

    @Async
    public void createMeasureOrdersByBatch(String token,String requestBody){
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.CREATE_MEASURE_ORDER_BY_BATCH);
        ResponseEntity<String> response = template.postForEntity(gatewayURL, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            String result = (String) response.getBody();
            logger.info("invoke createMeasureOrdersByBatch over,result is {}",result);
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


/* 使用方法参数化的方法可以将公共的代码抽象出: 公共的方法抽象*/
    /*public Map<String, Object> commonMethod(String token,Function<String,String> f, String urltmp){
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        String url= f.apply(urltmp);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (LinkedHashMap<String, Object>) responseMap;
        } else {
            return null;
        }
    }

    public String getApproveByAsset(String uid){
        String gatewayURL = getGatewayURL(GatewayURL.GET_APPROVE_BY_ASSET);
        return gatewayURL.replace("{assetUid}",uid);
    }

    public String getPmOrderSteps(String uid){
        String gatewayURL = getGatewayURL(GatewayURL.PM_ORDER_STEPS);
        return gatewayURL.replace("{pmOrderId}",uid);
    }*/