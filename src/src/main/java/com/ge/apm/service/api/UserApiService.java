package com.ge.apm.service.api;

import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author Yangfan, Liu
 * @date 4/7/2022 15:29
 */
@Service
public class UserApiService extends MicroServiceInvoker {
    private static Logger logger = LoggerFactory.getLogger(UserApiService.class);

    public Boolean resetUser(String loginName) {
        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(createHttpHeaders(UserContextService.getAccessToken()));
            String url = getGatewayURL(GatewayURL.RESET_USER_TO_REDIS).replace("{loginName}", loginName);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            return isOkay(response);
        } catch (Exception e) {
            logger.error("failed to access micro-service", e);
        }
        return false;
    }

    public Boolean removeUser(String loginName) {
        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(createHttpHeaders(UserContextService.getAccessToken()));
            String url = getGatewayURL(GatewayURL.REMOVE_USER_FROM_REDIS).replace("{loginName}", loginName);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            return isOkay(response);
        } catch (Exception e) {
            logger.error("failed to access micro-service", e);
        }
        return false;
    }
}
