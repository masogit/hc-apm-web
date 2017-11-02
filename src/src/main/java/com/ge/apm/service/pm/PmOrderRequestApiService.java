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
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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
    /*@Value("#{urlProperties.url_createPmOrder}")
    private String url_createPmOrder;*/


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


//http://apm-dev.hcdigital.com.cn:8090/hcapmworkflowservice/api/apm/pmOrders/ff8080815f525e84015f5764de610000/action?type=accept
}


