/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import com.ge.apm.service.common.GatewayURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 *
 * @author 212579464
 */
@Component
public class MicroServiceInvoker {

    Logger logger = LoggerFactory.getLogger(MicroServiceInvoker.class);

    @Autowired
    protected RestTemplate template;

    @Value("#{systemEnvironment['GATEWAY_URL'] ?: urlProperties.defaultGateway}")
    private String baseURL;

    @Value("#{systemEnvironment['DOMAIN_URL'] ?: urlProperties.domainUrl}")
    protected String downLoadURL;

    public String getGatewayURL(GatewayURL url) {
        return baseURL + url.toString();
    }

    protected Boolean isOkay(ResponseEntity res) {

        if (!res.getStatusCode().equals(HttpStatus.OK)) {
            return false;
        }
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) res.getBody();

        return body.containsKey("bizStatusCode") && body.get("bizStatusCode").equals("OK");
    }

    protected String getResponseFailMessage(ResponseEntity res) {
        if (!res.getStatusCode().equals(HttpStatus.OK)) {
            return null;
        }
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) res.getBody();

        return body.containsKey("message")? String.valueOf(body.get("message")):"";
    }

    protected HttpHeaders createHttpHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        if (null != token) {
            headers.add("Authorization", token);
        } else {
            logger.error("token doesn't exists!");
        }
        return headers;
    }
}
