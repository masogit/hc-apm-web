package com.ge.apm.service;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class OCRTemplateService extends MicroServiceInvoker {

    Logger logger = LoggerFactory.getLogger(OCRTemplateService.class);

    public JSONObject readFileToObject(UploadedFile uploadedFile,String id){
        File file = null;
        try {
            file = getFile(uploadedFile);
            String token = UserContextService.getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("multipart/form-data; charset=UTF-8");
            headers.setContentType(type);
            headers.add("Authorization", token);
            String name = file.getName();
            headers.add("Content-disposition","attachment;filename="+ URLEncoder.encode(name));
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            FileSystemResource resource = new FileSystemResource(file);
            body.add("file", resource);
            body.add("templateId", id);
            HttpEntity<Map> requestEntity = new HttpEntity<Map>(body, headers);
            String url = getGatewayURL(GatewayURL.OCR_IMAGE);
            if(name.endsWith("pdf")){
                url = getGatewayURL(GatewayURL.OCR_DOCUMENT);
            }
            ResponseEntity<JSONObject> response = template.exchange(url,HttpMethod.POST, requestEntity, JSONObject.class);
            return response.getBody();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            if(file != null && file.exists()){
                file.delete();
            }
        }
    }

    public static File getFile(UploadedFile file) throws IOException {
        String path= System.getProperty("user.dir");
        File tempFile = new File(path + File.separator +file.getFileName());
        if(tempFile.exists()){
            tempFile.delete();
        }
        tempFile.createNewFile();
        byte[] bytes = file.getContents();
        try(FileOutputStream outputStream = new FileOutputStream(tempFile)){
            outputStream.write(bytes);
            outputStream.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
        return tempFile;
    }
}
