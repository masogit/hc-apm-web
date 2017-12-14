/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.pm;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.V2_ServiceRequest;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 212579464
 */
@Component
public class PmOrderRequestApiService extends MicroServiceInvoker {
	
	
    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    /*  @Value("#{urlProperties.url_serviceRequestCreate}")
	  private String url_serviceRequestCreate;*/
    @Value("#{urlProperties.url_PmOrderAction}")
    private String url_pmOrderAction;
    @Value("#{urlProperties.url_pmRequestCreate}")
    private String url_pmRequestCreate;

    @Value("#{urlProperties.url_getApproveByAsset}")
    private String url_getApproveByAsset;

    @Value("#{urlProperties.url_pmOrderSteps}")
    private String url_pmOrderSteps;
    
    @Value("#{urlProperties.url_createPmOrdersByBatch}")
    private String url_createPmOrdersByBatch;

    public Map<String, Object> getApproveByAsset(String token, String assetUid) {
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        String url = url_getApproveByAsset.replace("{assetUid}",assetUid);
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
        String url = url_pmOrderSteps.replace("{pmOrderId}",pmOrderId);
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
        String url = url_pmOrderAction.replace("{pmOrderId}", woId);

        ResponseEntity<LinkedHashMap> response = template.exchange(url,HttpMethod.PUT,requestEntity,LinkedHashMap.class);
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
        ResponseEntity<LinkedHashMap> response = template.postForEntity(url_pmRequestCreate, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (LinkedHashMap<String, Object>) responseMap.get("data");
        } else {
            return null;
        }
    }
    
    @Async
    public void createPmOrdersByBatch(String token,String requestBody){
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, createHttpHeaders(token));
        ResponseEntity<String> response = template.postForEntity(url_createPmOrdersByBatch, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
	        	String result = (String) response.getBody();
	        	logger.info("invoke createPmOrdersByBatch over,result is {}",result);
        }else {
        		logger.error("rmi failure,code is {},body is {}",response.getStatusCode(),response.getBody());
        }
        
    }
//http://apm-dev.hcdigital.com.cn:8090/hcapmworkflowservice/api/apm/pmOrders/ff8080815f525e84015f5764de610000/action?type=accept
}


