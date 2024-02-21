package com.ge.apm.service.budget;

import com.ge.apm.domain.budget.PurchaseBudgetConfig;
import com.ge.apm.domain.budget.PurchaseOrderInfo;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author : 503189362
 * @date : 2023/10/11
 * @description :
 */
@Service
public class PurchaseBudgetConfigService extends MicroServiceInvoker {


    @Async
    public void sendWxMsg(PurchaseBudgetConfig purchaseBudgetConfig, String token) throws InterruptedException {
        //String token = UserContextService.getAccessToken();
        Map<String, Object> map = new HashMap<>();
        map.put("id", purchaseBudgetConfig.getId());
        HttpEntity<Map> requestEntity = new HttpEntity<>(map, createHttpHeaders(token));
        String url = getGatewayURL(GatewayURL.PURCHASE_BUDGET_CONFIG_SEND_WX_MESSAGE).concat("?id=").concat(purchaseBudgetConfig.getId());
        Thread.sleep(1000);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(url, requestEntity, LinkedHashMap.class);
        if (!isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            String str = responseMap.get("message").toString();
            throw new RuntimeException(str);
        }
    }

    @Async
    public void sendWxMsgPurchase(PurchaseOrderInfo purchaseOrderInfo, String token,String msgType) throws InterruptedException {
        Map<String, Object> map = new HashMap<>();
        map.put("id", purchaseOrderInfo.getId());
        HttpEntity<Map> requestEntity = new HttpEntity<>(map, createHttpHeaders(token));
        String url = new String();
        if("RESULT".equals(msgType)){
            url = getGatewayURL(GatewayURL.RESULT_PURCHASE_SEND_WX_MESSAGE).concat("?id=").concat(purchaseOrderInfo.getId());
        }else {
            url = getGatewayURL(GatewayURL.PURCHASE_SEND_WX_MESSAGE).concat("?id=").concat(purchaseOrderInfo.getId());
        }
        Thread.sleep(1000);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(url, requestEntity, LinkedHashMap.class);
        if (!isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            String str = responseMap.get("message").toString();
            throw new RuntimeException(str);
        }
    }
}
