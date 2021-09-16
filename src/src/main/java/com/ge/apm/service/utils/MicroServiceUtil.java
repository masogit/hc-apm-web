/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * @author 212579464
 */
@Component
public class MicroServiceUtil extends MicroServiceInvoker {


    public InputStream downloadRepeatedReadFile(String fileId) {
        HttpHeaders headers = new HttpHeaders();
        String gatewayURL = getGatewayURL(GatewayURL.DOWNLOAD_FILE);
        ResponseEntity<byte[]> response = template.exchange(gatewayURL.concat("/").concat(fileId), HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
        HttpHeaders responseHeader = response.getHeaders();
        byte[] result = response.getBody();
        ByteArrayInputStream bios = new ByteArrayInputStream(result);
        bios.reset();
        return bios;
    }

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

    public String getAuthenticateByUserPassword(String userName, String password, String userAgent) {
        return doGetAuthenticateByUserPassword(userName, password, false, userAgent); //password is in plain text
    }

    public String getAuthenticateByUserPasswordEncrypted(String userName, String password, String userAgent) {
        return doGetAuthenticateByUserPassword(userName, password, true, userAgent); //password is already encrypted
    }

    public String doGetAuthenticateByUserPassword(String userName, String password, boolean isPasswordEncrypted, String userAgent) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("User-Agent", userAgent);
        headers.add("request-source", "legacy");
        Map<String, String> postParameters = new HashMap<String, String>();
        postParameters.put("loginName", userName);
        postParameters.put("password", password);

        HttpEntity<Map> requestEntity = new HttpEntity<Map>(
                postParameters, headers);

        String gatewayURL;
        if (isPasswordEncrypted)
            gatewayURL = getGatewayURL(GatewayURL.GET_AUTHENTICATE_BY_USER_PASSWORD_ENCRYPTED);
        else
            gatewayURL = getGatewayURL(GatewayURL.GET_AUTHENTICATE_BY_USER_PASSWORD);

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
        return downLoadURL.concat(GatewayURL.DOWNLOAD_FILE.toString());
    }

    public String getImgUrl() {
        return downLoadURL.concat(GatewayURL.SEARCH_FILE.toString());
    }

    public byte[] docxToPdf(byte[] content) {

        HttpHeaders headers = new HttpHeaders();

        MediaType type = MediaType.parseMediaType("multipart/form-data; charset=UTF-8");
        headers.setContentType(type);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource arrayResource = new ByteArrayResource(content) {
            @Override
            public String getFilename() throws IllegalStateException {
                return ".\\dummy.docx";
            }

        };
        body.add("files", arrayResource);

        String url = docxToPdfUrl + "/convert/office";
        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<byte[]> response = template.postForEntity(url, requestEntity, byte[].class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return response.getBody();
        }
        return null;
    }

    public void synchBatchTask(File file, String batchFileType) {
        logger.info("File uploaded {}, {}", batchFileType, file.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        List<MediaType> acceptList = new ArrayList<>();
        acceptList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(acceptList);

        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("file", new FileSystemResource(file));
        bodyMap.add("fileName", file.getName());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

        String uploadFileUrl = getGatewayURL(GatewayURL.BATCH_FILE_UPLOAD) + "/" + batchFileType;
        logger.info("upload file url: {}", uploadFileUrl);

        ResponseEntity<byte[]> response = template.postForEntity(uploadFileUrl, requestEntity, byte[].class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("Upload file failure");
        }
        logger.info("File uploaded");
    }

    /**
     * 发送短信验证码
     * @param telephone
     */
    public Boolean sendMsMCode(String telephone){
        String gatewayURL = getGatewayURL(GatewayURL.MSM_SEND_CODE);
        String url = gatewayURL.replace("{phone}",telephone);
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        logger.info("url for sendMsMCode: " + url);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, requestEntity, String.class);
        String body = response.getBody();
        logger.info("sendMsMCode,telephone={},response body ={}",telephone, body);
        JSONObject result = JSON.parseObject(body);
        return result.getBoolean("data");
    }
    /**
     * 发送短信验证码
     * @param telephone
     */
    public Map getMsMCode(String telephone){
        String gatewayURL = getGatewayURL(GatewayURL.MSM_GET_CODE);
        String url = gatewayURL.replace("{phone}",telephone);
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        logger.info("url for getMsMCode: " + url);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, requestEntity, String.class);
        String body = response.getBody();
        logger.info("getMsMCode,telephone={},response body ={}",telephone, body);
        JSONObject result = JSON.parseObject(body);
        String data = result.getString("data");
        if(StringUtils.isEmpty(data)){
            return null;
        }else {
            JSONObject cache = JSON.parseObject(data);
            logger.info("get sms code , cache:{}", cache.toJSONString());
            Date createdTime = cache.getDate("createdAt");
            String phone = cache.getString("phone");
            String code = cache.getString("code");

            Map<String,String> map = new HashMap<String,String>();
            map.put("phone", phone);
            map.put("code", code);
            long temp_sen = (System.currentTimeMillis() - createdTime.getTime())/1000;
            long temp_send =(10 * 60) - temp_sen;
            if(temp_send != 0){
                map.put("expire", String.valueOf(temp_send/60l));
            }else{
                map.put("expire", String.valueOf(1));
            }
            return map;
        }
    }
}
