package com.ge.apm.service.budget;

import com.ge.apm.domain.budget.PurchaseBudgetConfig;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
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


    public void sendWxMsg(PurchaseBudgetConfig purchaseBudgetConfig){
        String token = UserContextService.getAccessToken();
        Map<String, Object> map = new HashMap<>();
        map.put("id", purchaseBudgetConfig.getId());
        HttpEntity<Map> requestEntity = new HttpEntity<>(map, createHttpHeaders(token));
        String url = getGatewayURL(GatewayURL.PURCHASE_BUDGET_CONFIG_SEND_WX_MESSAGE).concat("?id=").concat(purchaseBudgetConfig.getId());
        ResponseEntity<LinkedHashMap> response = template.postForEntity(url, requestEntity, LinkedHashMap.class);
        if (!isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            String str = responseMap.get("message").toString();
            throw new RuntimeException(str);
        }
    }
}
