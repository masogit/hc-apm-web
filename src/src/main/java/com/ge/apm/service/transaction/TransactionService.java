package com.ge.apm.service.transaction;

import com.ge.apm.dao.*;

import com.ge.apm.domain.DeptTransactionLog;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.DepTranForTrans;
import com.ge.apm.web.TransactionForm;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.LinkedHashMap;


@Service
public class TransactionService extends MicroServiceInvoker {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    DeptTransactionLogRepository deptTransactionLogRepository;

public HashMap<String,Object> getSummary(String from,String to,String token){
    HttpEntity<String> requestEntity = new HttpEntity<String>(createHttpHeaders(token));
    String gatewayURL = getGatewayURL(GatewayURL.GET_DEPTRASACTION_LOG_SUMMARY);

    gatewayURL=gatewayURL.replace("{from}",from);
    gatewayURL=gatewayURL.replace("{to}",to);
    ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.GET, requestEntity, LinkedHashMap.class);
    if (isOkay(response)) {
        LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
        return responseMap;
    } else {
        return null;
    }
}
    public HashMap<String,Object> getTransactionListByPerson(DepTranForTrans dep, String token){
        HttpEntity<String> requestEntity = new HttpEntity<String>(createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.QUERY_DEPTTRANSACTION_LOG);
        gatewayURL=gatewayURL.replace("{staffId}",dep.getUserId().toString());
        gatewayURL=gatewayURL.replace("{from}",dep.getStartTime());
        gatewayURL=gatewayURL.replace("{to}", dep.getEndTime());
        gatewayURL=gatewayURL.replace("{page}", dep.getPageNumber().toString());
        gatewayURL=gatewayURL.replace("{pageSize}", dep.getPageSize().toString());
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return responseMap;
        } else {
            return null;
        }
    }
    public HashMap<String,Object> getDetailsByItemSelection(String from, String to,Integer authorId, String token){
        HttpEntity<String> requestEntity = new HttpEntity<String>(createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.QUERY_DEPTTRANSACTION_LOG);

        gatewayURL=gatewayURL.replace("{staffId}", authorId.toString());
        gatewayURL=gatewayURL.replace("{from}", from);
        gatewayURL=gatewayURL.replace("{to}", to);

        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return responseMap;
        } else {
            return null;
        }
    }

/*
    public void remoteAssignRequest(String requestId, String assigneeUID) {
        String url = getGatewayURL(GatewayURL.REMOTE_ASSIGN_REQUEST);
        url = url.replace("{requestId}", requestId);
        url = url.replace("{assigneeUID}", assigneeUID);
       // String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.PUT, requestEntity, LinkedHashMap.class);
        logger.info("Assignee request " + (isOkay(response) ? "successfully." : "failed."));
    }*/

    public LinkedHashMap<String, Object> createTmByLink(TransactionForm formData,String token){
        HttpEntity<TransactionForm> requestEntity = new HttpEntity<TransactionForm>(formData, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.CREATE_DEPTTRANSACTION_LOG);
        try {
            ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            if (isOkay(response)) {
                LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
                Object data = responseMap.get("data");
                if (data instanceof LinkedHashMap) {
                    return (LinkedHashMap<String, Object>) data;
                }
                return responseMap;
            } else {
                return null;
            }
        } catch (HttpServerErrorException e) {
            logger.error("微服务创建科务记录失败，状态码: {}，响应: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (RestClientException e) {
            logger.error("微服务调用失败: {}", e.getMessage());
            return null;
        }
    }

    public String updateTransaction(TransactionForm formData, String uid, String token){
        HttpEntity<TransactionForm> requestEntity = new HttpEntity<TransactionForm>(formData, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.UPDATE_DEPTTRANSACTION_LOG);
        gatewayURL = gatewayURL.replace("{uid}", uid);
        try {
            ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.PUT, requestEntity, LinkedHashMap.class);
            if (isOkay(response)) {
                LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
                return (String) responseMap.get("bizStatusCode");
            } else {
                return null;
            }
        } catch (HttpServerErrorException e) {
            logger.error("微服务更新科务记录失败，状态码: {}，响应: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (RestClientException e) {
            logger.error("微服务调用失败: {}", e.getMessage());
            return null;
        }
    }
    public void deleteTm(DeptTransactionLog tm)
    {
        deptTransactionLogRepository.delete(tm);
    }


    public void localUpdateTransaction(DeptTransactionLog dptlog){
        DeptTransactionLog deptlogTmp = new DeptTransactionLog();
        BeanUtils.copyProperties(dptlog,deptlogTmp);
        DeptTransactionLog save = deptTransactionLogRepository.save(deptlogTmp);
        System.out.println();
    }
   /* public void localUpdateTransactionByLink(DeptTransactionLog dptlog){
        HttpEntity<TransactionForm> requestEntity = new HttpEntity<TransactionForm>(formData, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.CREATE_DEPTTRANSACTION_LOG);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (String) responseMap.get("bizStatusCode");
        } else {
            return null;
        }
    }*/

}
