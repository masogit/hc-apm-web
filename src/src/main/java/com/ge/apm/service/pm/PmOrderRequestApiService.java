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
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    public Map<String, Object> createPmRequest(String token, String assetUid,Date planTime){
        PmOrderActionForm formData = new PmOrderActionForm();
        formData.setAssetUid(assetUid);
        if(null!=planTime){
            formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(planTime));
        }
        HttpEntity<PmOrderActionForm> requestEntity = new HttpEntity<>(formData, createHttpHeaders(token));
        ResponseEntity<LinkedHashMap> response = template.postForEntity(url_pmRequestCreate, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (LinkedHashMap<String, Object>) responseMap.get("data");
        } else {
            return null;
        }


    }

//http://apm-dev.hcdigital.com.cn:8090/hcapmworkflowservice/api/apm/pmOrders/ff8080815f525e84015f5764de610000/action?type=accept
}


