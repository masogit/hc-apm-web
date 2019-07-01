/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.measuring.measuring;

import com.ge.apm.domain.MeasuringOrderDetail;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.web.MeasureOrderActionForm;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author 212579464
 */
@Component
public class MeasureRequestApiService extends MicroServiceInvoker {
	
	
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
    String updateWorkOrderMeasuringDetailPrice(String token, MeasuringOrderDetail workOrderDetail) {
        HttpEntity<MeasuringOrderDetail> requestEntity = new HttpEntity<MeasuringOrderDetail>(workOrderDetail, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.UPDATE_WODETAIL_PRICE);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.PUT, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (String) responseMap.get("message");
        } else {
            return null;
        }
    }
    public Map<String, Object> getMeasuareOrderSteps(String token, String measureOrderId) {
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        String gatewayURL = getGatewayURL(GatewayURL.MEASURE_ORDER_STEPS);
        String url = gatewayURL.replace("{OrderId}",measureOrderId);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (LinkedHashMap<String, Object>) responseMap;
        } else {
            return null;
        }
    }

    //CANCEL_PM_ORDER
public String invokeMeasureOrderCancel(String token, MeasureOrderActionForm formData, String actionName, String measureOrderId){
    HttpEntity<MeasureOrderActionForm> requestEntity = new HttpEntity<MeasureOrderActionForm>(formData, createHttpHeaders(token));
    String gatewayURL = getGatewayURL(GatewayURL.MEASURE_CANCEL_MEASURE_ORDER);
    String url = gatewayURL.replace("{OrderId}", measureOrderId);

    ResponseEntity<LinkedHashMap> response = template.exchange(url,HttpMethod.DELETE,requestEntity,LinkedHashMap.class);
    if (isOkay(response)) {
        LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
        logger.info("Invoke PmOrderCancel "+actionName+" has finished "+responseMap.get("data"));
        return (String) responseMap.get("bizStatusCode");
    } else {
        return null;
    }
}

    public String invokePmOrderAction(String token, MeasureOrderActionForm formData, String actionName, String woId) {
        HttpEntity<MeasureOrderActionForm> requestEntity = new HttpEntity<MeasureOrderActionForm>(formData, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.MEASURE_ORDER_ACTION);
        String url = gatewayURL.replace("{orderId}", woId);

        ResponseEntity<LinkedHashMap> response = template.exchange(url,HttpMethod.PUT,requestEntity,LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            logger.info("Invoke MeasureOrderAction "+actionName+" has finished "+responseMap.get("data"));
            return (String) responseMap.get("bizStatusCode");
        } else {
            return null;
        }
    }

    public String invokeMeasureOrderActionBatch(String token, ArrayList<MeasureOrderActionForm> formDataList, String actionName) {
        HttpEntity<ArrayList<MeasureOrderActionForm>> requestEntity = new HttpEntity<ArrayList<MeasureOrderActionForm>>(formDataList, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.MEASURE_BATCH_TAKE_ORDER);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL,HttpMethod.PUT,requestEntity,LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            logger.info("Invoke MeasureOrderAction "+actionName+" has finished "+responseMap.get("data"));
            return (String) responseMap.get("bizStatusCode");
        } else {
            return null;
        }
    }

    public Map<String, Object> createMeasureRequest(String token,MeasureOrderActionForm formData ){
        HttpEntity<MeasureOrderActionForm> requestEntity = new HttpEntity<>(formData, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.MEASURE_REQUEST_CREATE);
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
        String gatewayURL = getGatewayURL(GatewayURL.MEASURE_CREATE_MEASURE_ORDER_BY_BATCH);
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


