/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.service.common.GatewayURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.annotation.PostConstruct;

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
    protected String baseURL;

    @Value("#{systemEnvironment['DOMAIN_URL'] ?: urlProperties.domainUrl}")
    protected String downLoadURL;

    @Value("#{systemEnvironment['DOCXTOPDF_URL'] ?: urlProperties.docxtopdf}")
    protected String docxToPdfUrl;

    @Value("#{wxProperties.web_context_url}")
    protected String webContextUrl;

    @Value("#{systemEnvironment['TOKEN_SECRETKEY'] ?: urlProperties.tokenSecretkey}")
    protected String tokenSecretkey;

    @Value("#{urlProperties.pdfPreViewGateway}")
    public String pdfPreViewUrl;

    @Autowired
    protected BlobObjectRepository blobObjectRepository;

    @PostConstruct
    public void initRestTemplate(){
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        List<HttpMessageConverter<?>> messageConverters = template.getMessageConverters();
//        Iterator<HttpMessageConverter<?>> iterator = messageConverters.iterator();
//        while(iterator.hasNext()){
//            HttpMessageConverter<?> messageConverter = iterator.next();
//            if(messageConverter instanceof StringHttpMessageConverter){
//                iterator.remove();
//            }
//
//        }
//        messageConverters.add(stringHttpMessageConverter);
        messageConverters.set(1,stringHttpMessageConverter);
        template.setMessageConverters(messageConverters);
    }

    public String getGatewayURL(GatewayURL url) {
        return baseURL + url.toString();
    }

    public String getGatewayURL(String url) {
        return baseURL + url;
    }

    public String getPdfPreViewUrl(){
        return pdfPreViewUrl;
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
