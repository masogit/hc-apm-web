/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

import com.ge.apm.service.common.GatewayURL;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author 212579464
 */
@Component
public class MicroServiceUtil extends MicroServiceInvoker {

    public StreamedContent downloadSingleFile(String fileId) {
        HttpHeaders headers = new HttpHeaders();
        String gatewayURL = getGatewayURL(GatewayURL.DOWNLOAD_FILE);
        ResponseEntity<byte[]> response = template.exchange(gatewayURL.concat("/").concat(fileId), HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
        HttpHeaders responseHeader = response.getHeaders();
        byte[] result = response.getBody();
        InputStream ins = new ByteArrayInputStream(result);
        DefaultStreamedContent fileStream = new DefaultStreamedContent(ins);
        fileStream.setContentType(responseHeader.getContentType().toString());
        List<String> dispositions = responseHeader.get("content-disposition");
        String disposition = (String) dispositions.get(0);
        String fileName = disposition.substring(disposition.lastIndexOf("name=") + 5);
        fileStream.setName(fileName);
        return fileStream;
    }

    public Boolean deleteFileObject(String token, String objectId) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Authorization", token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

        String gatewayURL = getGatewayURL(GatewayURL.DELETE_FILE);

        try {
            ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL.concat("/").concat(objectId), HttpMethod.DELETE, requestEntity, LinkedHashMap.class);
            return isOkay(response);
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> uploadSingleFile(String token, File file) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("multipart/form-data; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Authorization", token);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        FileSystemResource resource = new FileSystemResource(file);
        body.add("file", resource);

        HttpEntity<Map> requestEntity = new HttpEntity<Map>(body, headers);

        String gatewayURL = getGatewayURL(GatewayURL.UPLOAD_SINGLE_FILE);

        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
            return data;
        }

        return null;
    }

    public Map<String, Object> uploadSingleFileByUrl(String token, String url, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Authorization", token);

        Map<String, String> postParameters = new HashMap<String, String>();
        postParameters.put("urlFile", url);
        postParameters.put("fileName", fileName);
        HttpEntity<Map> requestEntity = new HttpEntity<Map>(postParameters, headers);

        String gatewayURL = getGatewayURL(GatewayURL.UPLOAD_SINGLE_FILE_BY_URL);

        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
            return data;
        }
        return null;
    }

    public String getAuthenticateByOpenId(String code, String weChatId) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        Map<String, String> postParameters = new HashMap<String, String>();
        postParameters.put("code", code);
        postParameters.put("weChatId", weChatId);
        HttpEntity<Map> requestEntity = new HttpEntity<Map>(
                postParameters, headers);

        String gatewayURL = getGatewayURL(GatewayURL.GET_AUTHENTICATE_BY_OPEN_ID);

        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);

        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
            if (null != data && data.containsKey("jwtToken")) {
                LinkedHashMap<String, Object> jwtToken = (LinkedHashMap<String, Object>) data.get("jwtToken");
                if (null != jwtToken && jwtToken.containsKey("id_token")) {
                    return (String) jwtToken.get("id_token");
                }
            }
        }

        return null;

    }

    public String getAuthenticateByUserPassword(String userName, String password) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);

        Map<String, String> postParameters = new HashMap<String, String>();
        postParameters.put("loginName", userName);
        postParameters.put("password", password);

        HttpEntity<Map> requestEntity = new HttpEntity<Map>(
                postParameters, headers);

        String gatewayURL = getGatewayURL(GatewayURL.GET_AUTHENTICATE_BY_USER_PASSWORD);

        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);

        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
            if (null != data && data.containsKey("jwtToken")) {
                LinkedHashMap<String, Object> jwtToken = (LinkedHashMap<String, Object>) data.get("jwtToken");
                if (null != jwtToken && jwtToken.containsKey("id_token")) {
                    return (String) jwtToken.get("id_token");
                }
            }
        }

        return null;

    }

    public String getUrl_downloadFile() {
        return  getGatewayURL(GatewayURL.DOWNLOAD_FILE);
    }

}
