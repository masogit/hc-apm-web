/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.dto.FileInfoResult;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.image.ImageConstant;
import com.ge.healthcare.cn.common.utils.token.TokenBuilder;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author 212579464
 */
@Component
public class MicroServiceUtil extends MicroServiceInvoker {

    public String getBaseUrl() {
        return super.baseURL;
    }

    public String getDownLoadUrl() {
        return super.downLoadURL;
    }

    public String getWebContextUrl() {
        return super.webContextUrl;
    }

    public InputStream downloadRepeatedReadFile(String fileId) {
        HttpHeaders headers = createHttpHeaders(UserContextService.getAccessToken());
        String gatewayURL = getGatewayURL(GatewayURL.AUTH_DOWNLOAD_FILE);
        ResponseEntity<byte[]> response = template.exchange(gatewayURL.concat("/").concat(fileId), HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
        HttpHeaders responseHeader = response.getHeaders();
        byte[] result = response.getBody();
        ByteArrayInputStream bios = new ByteArrayInputStream(result);
        bios.reset();
        return bios;
    }

    public List<FileInfoResult> getFiles(Collection<String> fileIds) {
        HttpHeaders headers = createHttpHeaders(UserContextService.getAccessToken());
        String gatewayURL = getGatewayURL(GatewayURL.GET_FILES);
        HttpEntity<Collection<String>> httpEntity = new HttpEntity<>(fileIds, headers);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.POST, httpEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return JSON.parseArray(JSON.toJSONString(responseMap.get("data")), FileInfoResult.class);
        }
        return ListUtil.empty();
    }

    public List<FileInfoResult> getNotByteFiles(Collection<String> fileIds) {
        HttpHeaders headers = createHttpHeaders(UserContextService.getAccessToken());
        String gatewayURL = getGatewayURL(GatewayURL.GET_NOT_BYTE_FILES);
        HttpEntity<Collection<String>> httpEntity = new HttpEntity<>(fileIds, headers);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.POST, httpEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return JSON.parseArray(JSON.toJSONString(responseMap.get("data")), FileInfoResult.class);
        }
        return ListUtil.empty();
    }

    public String getImgBase64(String fileId) {
        String res = "data:image/jpeg;base64,";
        try {
            List<V2_BlobObject> allByObjectStorageId = blobObjectRepository.findAllByObjectStorageId(fileId);
            if (ObjectUtil.isNotEmpty(allByObjectStorageId)) {
                V2_BlobObject blobObject = allByObjectStorageId.get(0);
                if (!blobObject.isImageType()) {
                    String objectName = blobObject.getObjectName();
                    String type = objectName.substring(objectName.lastIndexOf(".") + 1).toLowerCase();
                    return getDefaultImage(type);
                }
            }
            HttpHeaders headers = createHttpHeaders(UserContextService.getAccessToken());
            String gatewayURL = getGatewayURL(GatewayURL.AUTH_DOWNLOAD_FILE);
            ResponseEntity<byte[]> response = template.exchange(gatewayURL.concat("/").concat(fileId), HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
            byte[] result = response.getBody();
            res += Base64Utils.encodeToString(result);
        } catch (Exception e) {
            logger.error("encode image base64 error:{}",e);
            e.printStackTrace();
        }
        return res;
    }

    public String getImgBase64(String fileId, V2_BlobObject blobObject) {
        String res = "data:image/jpeg;base64,";
        try {
            if (blobObject != null) {
                if (!blobObject.isImageType()) {
                    String objectName = blobObject.getObjectName();
                    String type = objectName.substring(objectName.lastIndexOf(".") + 1).toLowerCase();
                    return getDefaultImage(type);
                }
            }
            HttpHeaders headers = createHttpHeaders(UserContextService.getAccessToken());
            String gatewayURL = getGatewayURL(GatewayURL.AUTH_DOWNLOAD_FILE);
            ResponseEntity<byte[]> response = template.exchange(gatewayURL.concat("/").concat(fileId), HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
            byte[] result = response.getBody();
            res += Base64Utils.encodeToString(result);
        } catch (Exception e) {
            logger.error("encode image base64 error:{}",e);
            e.printStackTrace();
        }
        return res;
    }

    List<String> imageTypeList = ListUtil.newArrayList("png", "jpg", "jpeg");

    public String getImgBase64ByStream(TempFileUtil tempFileUtil) {
        String fileName = tempFileUtil.getFileName();
        String type = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (imageTypeList.contains(type.toLowerCase())) {
            return "data:image/jpeg;base64," + Base64Utils.encodeToString(tempFileUtil.getContents());
        }
        return getDefaultImage(type);
    }

    public static String getDefaultImage(String type) {
        switch (type) {
            case "xlsx":
            case "xls":
                return ImageConstant.EXCEL_DEFAULT_BASE64;
            case "docx":
            case "doc":
                return ImageConstant.WORD_DEFAULT_BASE64;
            case "pptx":
            case "ppt":
                return ImageConstant.PPT_DEFAULT_BASE64;
            case "zip":
            case "rar":
                return ImageConstant.ZIP_DEFAULT_BASE64;
            case "pdf":
                return ImageConstant.PDF_DEFAULT_BASE64;
            case "txt":
                return ImageConstant.TXT_DEFAULT_BASE64;
            case "mp4":
                return ImageConstant.MP4_DEFAULT_BASE64;
            default:
                break;
        }
        return "";
    }

    public StreamedContent downloadSingleFile(String fileId) {
        HttpHeaders headers = createHttpHeaders(UserContextService.getAccessToken());
        String gatewayURL = getGatewayURL(GatewayURL.AUTH_DOWNLOAD_FILE);
        ResponseEntity<byte[]> response = template.exchange(gatewayURL.concat("/").concat(fileId), HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
        HttpHeaders responseHeader = response.getHeaders();
        byte[] result = response.getBody();
        InputStream ins = new ByteArrayInputStream(result);
        DefaultStreamedContent fileStream = new DefaultStreamedContent(ins);
        fileStream.setContentType(responseHeader.getContentType().toString());
        List<String> dispositions = responseHeader.get("content-disposition");
        String disposition = dispositions.get(0);
        String fileName = disposition.substring(disposition.lastIndexOf("name=") + 5);
        List<V2_BlobObject> allByObjectStorageId = blobObjectRepository.findAllByObjectStorageId(fileId);
        if(!CollectionUtils.isEmpty(allByObjectStorageId)){
            try {
                fileStream.setName(new String(allByObjectStorageId.get(0).getObjectName().getBytes("UTF-8"), "ISO-8859-1"));
            } catch (UnsupportedEncodingException e) {
                logger.error("download file with  transforming chinese filename chart set error:{}",e);
            }
        }else{
            fileStream.setName(fileName);
        }
        return fileStream;
    }

    public StreamedContent downloadSingleFileForEhome(String fileId) {
        HttpHeaders headers = createHttpHeaders(UserContextService.getAccessToken());
        String gatewayURL = getGatewayURL(GatewayURL.AUTH_DOWNLOAD_FILE);
        ResponseEntity<byte[]> response = template.exchange(gatewayURL.concat("/").concat(fileId), HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
        HttpHeaders responseHeader = response.getHeaders();
        byte[] result = response.getBody();
        InputStream ins = new ByteArrayInputStream(result);
        DefaultStreamedContent fileStream = new DefaultStreamedContent(ins);
        fileStream.setContentType(responseHeader.getContentType().toString());
        List<String> dispositions = responseHeader.get("content-disposition");
        String disposition = dispositions.get(0);
        String fileName = disposition.substring(disposition.lastIndexOf("name=") + 5);
        /*List<V2_BlobObject> allByObjectStorageId = blobObjectRepository.findAllByObjectStorageId(fileId);
        if(!CollectionUtils.isEmpty(allByObjectStorageId)){
            try {
                fileStream.setName(new String(allByObjectStorageId.get(0).getObjectName().getBytes("UTF-8"), "ISO-8859-1"));
            } catch (UnsupportedEncodingException e) {
                logger.error("download file with  transforming chinese filename chart set error:{}",e);
            }
        }else{
            fileStream.setName(fileName);
        }*/
        fileStream.setName(fileName);
        return fileStream;
    }

    public byte[] downloadSingleFileByte(String fileId) {
        HttpHeaders headers = createHttpHeaders(UserContextService.getAccessToken());
        String gatewayURL = getGatewayURL(GatewayURL.AUTH_DOWNLOAD_FILE);
        ResponseEntity<byte[]> response = template.exchange(gatewayURL.concat("/").concat(fileId), HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
        byte[] result = response.getBody();
        return result;
    }

    public Boolean deleteFileObject(String token, String objectId) {
        HttpHeaders headers = createHttpHeaders(UserContextService.getAccessToken());
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
        HttpHeaders headers = createHttpHeaders(UserContextService.getAccessToken());
        MediaType type = MediaType.parseMediaType("multipart/form-data; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Authorization", token);
        String name = file.getName();
        logger.info("file name :{}",name);
        try {
            headers.add("Content-disposition","attachment;filename="+ URLEncoder.encode(name));
        }catch (Exception e){
            throw new RuntimeException("文件名加密异常");
        }

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


    public Map<String, Object> compressByObjectId(String objectId){
        HttpEntity<String> requestEntity = new HttpEntity<>(objectId);
        String gatewayURL = getGatewayURL(GatewayURL.COMPRESS_IMAGE);
        String url = gatewayURL.replace("{objectId}", objectId);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
            return data;
        } else {
            return null;
        }
    }


    public Map<String, Object> uploadSingleFileByUrl(String token, String url, String fileName) {
        HttpHeaders headers = createHttpHeaders(UserContextService.getAccessToken());
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
        if(StringUtils.isEmpty(token)){
            token = TokenBuilder.defaultToken(tokenSecretkey);
        }
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        logger.info("url for sendMsMCode: " + url);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, requestEntity, String.class);
        logger.info("sendMsMCode,telphone={},response={}",telephone, JSON.toJSONString(response));

        String body = response.getBody();
        logger.info("getMsMCode,telephone={},response body ={}",telephone, body);
        JSONObject result = JSON.parseObject(body);
        Boolean isSuccess = result.getBoolean("data");
        return isSuccess;
    }
    /**
     * 发送短信验证码
     * @param telephone
     */
    public Map getMsMCode(String telephone){

        String gatewayURL = getGatewayURL(GatewayURL.MSM_GET_CODE);
        String url = gatewayURL.replace("{phone}",telephone);
        String token = UserContextService.getAccessToken();
        if(StringUtils.isEmpty(token)){
            token = TokenBuilder.defaultToken(tokenSecretkey);
        }
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);
        logger.info("url for getMsMCode: " + url);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, requestEntity, String.class);
        logger.info("getMsMCode,telephone={},response={}",telephone, JSON.toJSONString(response));

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

    public Map<String, Object> uploadSingleCompressFile(String token, File file) {
        HttpHeaders headers = createHttpHeaders(UserContextService.getAccessToken());
        MediaType type = MediaType.parseMediaType("multipart/form-data; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Authorization", token);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        FileSystemResource resource = new FileSystemResource(file);
        body.add("file", resource);

        HttpEntity<Map> requestEntity = new HttpEntity<Map>(body, headers);

        String gatewayURL = getGatewayURL(GatewayURL.UPLOAD_SINGLE_COMPRESS_FILE);

        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
            return data;
        }

        return null;
    }

    public String getDaVinciShareLink(String token, String dashboardId) {
        String webContextUrl = getWebContextUrl();
        String daVinciShareLink = GatewayURL.DAVINCI_EXTERNAL.toString();
        String url = webContextUrl + daVinciShareLink + "?dashboardId=" + dashboardId + "&token=" + token;
        logger.info("getDaVinciShareLink url ================================== {}", url);
        ResponseEntity<String> response = template.getForEntity(url, String.class);
        if (HttpStatus.OK.equals(response.getStatusCode())) {
            String result = response.getBody();
            return webContextUrl + result.substring(1, result.length() - 1);
        }
        return "";
    }

    public Map getDavinciToken(String token) {
        HashMap<String, Object> map = new HashMap<>();

        try {
            String webContextUrl = getWebContextUrl();
            String davinciGetTokenString = GatewayURL.DAVINCI_GET_TOKEN.toString();
            String url = webContextUrl + davinciGetTokenString;
            logger.info("get davinci token url  ================================== {}", url);

            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            headers.add("Authorization", token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, entity, LinkedHashMap.class);

            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                return null;
            }
            LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) response.getBody();
            if (body.containsKey("header")) {
                LinkedHashMap<String, Object> header = (LinkedHashMap<String, Object>) body.get("header");
                if (header.containsKey("code")) {
                    int code = (int) header.get("code");
                    if (code == 200) {
//                        return (String) header.get("token");
                        map.put("davinciToken", header.get("token"));

                    }

                }
            }
            if (body.containsKey("payload")) {
                String jsonString = JSON.toJSONString(body.get("payload"));
                map.put("loginUser", jsonString);
            }
            return map;
        } catch (Exception e) {
            logger.error("getDavinciToken error:", e);
        }
        return map;
    }

    public JSONObject ocrCreated(String url, String token, File file) {
        HttpHeaders headers = createHttpHeaders(UserContextService.getAccessToken());
        MediaType type = MediaType.parseMediaType("multipart/form-data; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Authorization", token);
        String name = file.getName();
        logger.info("file name :{}",name);
        try {
            headers.add("Content-disposition","attachment;filename="+ URLEncoder.encode(name));
        } catch (Exception e) {
            throw new RuntimeException("文件名加密异常");
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        FileSystemResource resource = new FileSystemResource(file);
        body.add("file", resource);

        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);

        String gatewayURL = getGatewayURL(url);

        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            JSONObject data = new JSONObject((LinkedHashMap<String, Object>) response.getBody().get("data"));
            return data;
        }
        return null;
    }

}
