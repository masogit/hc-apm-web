package com.ge.apm.service.transaction;

import com.ge.apm.dao.*;

import com.ge.apm.domain.DeptTransactionLog;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.web.TransactionForm;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;


@Service
public class TransactionService extends MicroServiceInvoker {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    DeptTransactionLogRepository deptTransactionLogRepository;

    @Transactional
    public void createTm(DeptTransactionLog tm){
        deptTransactionLogRepository.save(tm);
    }

    public String createTmByLink(TransactionForm formData,String token){

        HttpEntity<TransactionForm> requestEntity = new HttpEntity<TransactionForm>(formData, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.CREATE_DEPTTRANSACTION_LOG);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (String) responseMap.get("bizStatusCode");
        } else {
            return null;
        }
    }
    @Transactional
    public void deleteTm(DeptTransactionLog tm)
    {
        deptTransactionLogRepository.delete(tm);
    }

}
