package com.ge.apm.service.schedule;

import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 212605082 on 2018/1/24.
 */
@Component
public class UserScheduleRequestApiService extends MicroServiceInvoker {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());


    public List<LinkedHashMap<String, Object>> createUserVacationRequest(String token, UserScheduleForm formData ){
        HttpEntity<UserScheduleForm> requestEntity = new HttpEntity<>(formData, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.USER_VACATION_CREATE);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (List<LinkedHashMap<String, Object>>) responseMap.get("data");
        } else {
            return null;
        }
    }


}
