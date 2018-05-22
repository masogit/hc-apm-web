/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.domain.remote.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
/**
 *
 * @author 212579464
 */
@Component
public class AssetInstallationApiService extends MicroServiceInvoker{

    @Value("#{systemEnvironment['GATEWAY_URL'] ?: urlProperties.defaultGateway}")
    private String baseURL;
    
    public String submitArrivalList(String token, String arrivalListId){
        
        HttpEntity<String> requestEntity = new HttpEntity<String>(arrivalListId, createHttpHeaders(token));

        String gatewayURL = GatewayURL.SUBMIT_ARRIVAL_LIST.toGatewayURL(baseURL);
        String url = gatewayURL.replace("{arrivalListId}", arrivalListId);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(url, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            return response.getBody().toString();
        } else {
            return null;
        }
    }

    public String submitArrivalListItem(String token, String itemId,Boolean isFinish) {
        HttpHeaders header = createHttpHeaders(token);
        header.add("type", isFinish?"install":"reject");
        HttpEntity<String> requestEntity = new HttpEntity<String>(itemId, header);

        String gatewayURL = GatewayURL.SUBMIT_ARRIVAL_LIST_ITEM.toGatewayURL(baseURL);
        String url = gatewayURL.replace("{arrivalListItemId}", itemId);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(url, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            return response.getBody().toString();
        } else {
            return null;
        }
    }

    public String reAssignOrderOwner(String token, String itemId, String currentPersonUID) {
        HttpHeaders header = createHttpHeaders(token);
        header.add("assignee", currentPersonUID);
        HttpEntity<String> requestEntity = new HttpEntity<String>(itemId, header);
        String gatewayURL = GatewayURL.REASSIGN_ARRIVAL_LIST_ITEM_OWNER.toGatewayURL(baseURL);
        String url = gatewayURL.replace("{arrivalListItemId}", itemId);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(url, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            return response.getBody().toString();
        } else {
            return null;
        }
        
    }
}
