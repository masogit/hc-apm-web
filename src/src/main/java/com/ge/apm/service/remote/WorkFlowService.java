package com.ge.apm.service.remote;

import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;

@Service
public class WorkFlowService extends MicroServiceInvoker {


    public String getOrderNumber(String key) {
        String gatewayURL = getGatewayURL(GatewayURL.ORDERNUMBER_NO).concat("?key=");
        if(StringUtils.hasText(key)){
            gatewayURL.concat(key);
        }
        HttpHeaders header = createHttpHeaders(UserContextService.getAccessToken());
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return String.valueOf(responseMap.get("data"));
        } else {
            return null;
        }
    }
}
