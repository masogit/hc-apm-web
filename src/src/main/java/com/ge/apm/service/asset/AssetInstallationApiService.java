/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.service.common.GatewayURL;
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

    
    public String submitArrivalList(String token, String arrivalListId){
        
        HttpEntity<String> requestEntity = new HttpEntity<String>(arrivalListId, createHttpHeaders(token));

        String gatewayURL = getGatewayURL(GatewayURL.SUBMIT_ARRIVAL_LIST);
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

        String gatewayURL = getGatewayURL(GatewayURL.SUBMIT_ARRIVAL_LIST_ITEM);
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
        String gatewayURL = getGatewayURL(GatewayURL.REASSIGN_ARRIVAL_LIST_ITEM_OWNER);
        String url = gatewayURL.replace("{arrivalListItemId}", itemId);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(url, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            return response.getBody().toString();
        } else {
            return null;
        }
        
    }
}
