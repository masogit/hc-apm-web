/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

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
    
    
    @Value("#{urlProperties.url_submitArrivalList}")
    private String submitArrivalListUrl;
    
    @Value("#{urlProperties.url_submitArrivalListItem}")
    private String submitArrivalListItemUrl;
    
    @Value("#{urlProperties.url_reAssignArrivalListItemOwner}")
    private String reAssignArrivalListItemOwnerUrl;
    
    
    public String submitArrivalList(String token, String arrivalListId){
        
        HttpEntity<String> requestEntity = new HttpEntity<String>(arrivalListId, createHttpHeaders(token));
        String url = submitArrivalListUrl.replace("{arrivalListId}", arrivalListId);
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
        String url = submitArrivalListItemUrl.replace("{arrivalListItemId}", itemId);
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
        String url = reAssignArrivalListItemOwnerUrl.replace("{arrivalListItemId}", itemId);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(url, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            return response.getBody().toString();
        } else {
            return null;
        }
        
    }
}
