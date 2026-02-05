package com.ge.apm.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.domain.OCRTemplate;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.FileScanService;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.ocr.DataItem;
import com.ge.healthcare.cn.common.ocr.OCRDocResponse;
import com.ge.healthcare.cn.common.ocr.OCRResponse;
import com.ge.healthcare.cn.common.ocr.Rectangle;
import com.ge.healthcare.cn.common.ocr.support.MatcherType;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class OCRTemplateService extends MicroServiceInvoker {

    Logger logger = LoggerFactory.getLogger(OCRTemplateService.class);

    @Autowired
    private FileScanService fileScanService;
    @Autowired
    private BlobFileService blobService;

    public JSONObject generateTemplateConfiguration(Object obj,OCRTemplate thisTemplate){
        if(obj instanceof OCRResponse){
            OCRResponse  result= (OCRResponse)obj;
            logger.info("解析图片成功:{}",JSONObject.toJSONString(result));
            return generateTemplate(result,thisTemplate);
        }else if(obj instanceof OCRDocResponse){
            OCRDocResponse  result= (OCRDocResponse)obj;
            logger.info("解析文档成功:{}",JSONObject.toJSONString(result));
            return generateTemplate(result,thisTemplate);
        }else{}
        return null;
    }
    private JSONObject  generateTemplate(OCRResponse  response,OCRTemplate thisTemplate){
        JSONObject result = new JSONObject();
        Double width = response.getWidth();
        result.put("Width", width);
        Double height = response.getHeight();
        result.put("Height", height);
        List<DataItem> data = response.getData();
        thisTemplate.setWidth(width.intValue());
        thisTemplate.setHeight(height.intValue());
        JSONArray elements = generateElements(data);
        result.put("Elements",elements);
        logger.info("image 图片生成配置:{}",JSONObject.toJSONString(result));
        return result;
    }

    private JSONObject  generateTemplate(OCRDocResponse  response,OCRTemplate thisTemplate){
        JSONObject result = new JSONObject();
        List<Double[]> size = response.getSize();
        List<Rectangle> data = response.getData();
        Rectangle rectangle = data.get(0);
        Double width = size.get(0)[0];
        result.put("Width", width);
        Double height = size.get(0)[1];
        result.put("Height", height);
        thisTemplate.setWidth(width.intValue());
        thisTemplate.setHeight(height.intValue());
        JSONArray elements = generateElements(rectangle.getData());
        result.put("Elements",elements);
        logger.info("pdf 文档生成配置:{}",JSONObject.toJSONString(result));
        return result;
    }

    private JSONArray generateElements(List<DataItem> dataItems){
        JSONArray elements = new JSONArray();
        for (int i=0;i<dataItems.size();i++) {
            DataItem item = dataItems.get(i);
            String text = item.getText();
            if(StringUtils.isEmpty(text)){
                continue;
            }
            List<Double[]> box = item.getBox();
            Double[] topLeft = box.get(0);
            Double[] lowerRight = box.get(2);
            JSONObject element = new JSONObject();
            element.put("TextValue", text);
            element.put("SerialNumber",i);
            element.put("FieldKey","");
            element.put("Box",box);
            element.put("PositionX",Math.ceil(topLeft[0]));
            element.put("PositionY",Math.ceil(topLeft[1]));
            element.put("Width",Math.abs(Math.ceil(lowerRight[0]) - Math.ceil(topLeft[0])));
            element.put("Height",Math.abs(Math.ceil(lowerRight[1]) - Math.ceil(topLeft[1])));
            element.put("Pattern", MatcherType.POSITION.getCode());
            element.put("Format", "");
            elements.add(element);
        }
        return elements;
    }
    private String clearSpecialChar(String text){
        return StringUtils.isEmpty(text) ? "" : text.replaceAll("[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t","");
    }

    public Object uploadTemplate(UploadedFile uploadedFile){
        String fileName = uploadedFile.getFileName();
        JSONObject jsonObject = null;
        if(fileName.toLowerCase().endsWith("pdf")){
            jsonObject = upload(uploadedFile, GatewayURL.OCR_RECOGNITION_DOC);
            logger.info("模板文件解析结果：{}",jsonObject.toJSONString());
            JSONObject data = jsonObject.getJSONObject("data");
            String id = data.getString("data");
            JSONArray size = data.getJSONArray("size");
            JSONObject result = getResultTask(id);
            result.put("size",size); // [[width,height]]
            return result.toJavaObject(OCRDocResponse.class);
        }else{
            JSONObject response = upload(uploadedFile, GatewayURL.OCR_RECOGNITION_IMAGE);
            logger.info("模板文件解析结果：{}",response.toJSONString());
            JSONObject data = response.getJSONObject("data");
            return data.toJavaObject(OCRResponse.class);
        }
    }

    public JSONObject upload(UploadedFile uploadedFile,GatewayURL gatewayURL){
        return upload(uploadedFile,gatewayURL,new HashMap<>());
    }

    public JSONObject getResult(String id,String token){
        String url = getGatewayURL(GatewayURL.OCR_DOC_RESULT);
        url = url.replace("{id}",id);
        JSONObject result = new JSONObject();
        result.put("is_done",false);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<Map> requestEntity = new HttpEntity<Map>(headers);
        JSONObject remoteRequest = getRemoteRequest(url, HttpMethod.GET, requestEntity, JSONObject.class);
        result = remoteRequest.getJSONObject("data");

        return result;
    }
    private JSONObject getResultTask(String id){
        final JSONObject[] result;
        Timer timer = null;
        TimerTask task = null;
        try {
            String accessToken = UserContextService.getAccessToken();
            CountDownLatch countdownLatch = new CountDownLatch(1);
            result = new JSONObject[]{new JSONObject()};
            timer = new Timer();
            task = new TimerTask() {
                public void run() {
                    try {
                        JSONObject dict = getResult(id,accessToken);
                        if(dict.getBoolean("is_done")){
                            result[0] = dict;
                            countdownLatch.countDown();
                        }
                    } catch (Exception e) {
                        countdownLatch.countDown();
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            };
            timer.scheduleAtFixedRate(task,0,200);
            countdownLatch.await(60, TimeUnit.SECONDS);
            task.cancel();
            timer.cancel();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return result[0];
    }
    public JSONObject upload(UploadedFile uploadedFile,GatewayURL gatewayURL,Map<String,Object> param){

        return upload(uploadedFile,gatewayURL,param,null);
    }

    public JSONObject upload(UploadedFile uploadedFile,GatewayURL gatewayURL,Map<String,Object> param,Function<String,String> function){
        File file = null;
        try {
            file = getFile(uploadedFile);
            return getJsonObject(gatewayURL, param, file,function);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            if(file != null && file.exists()){
                file.delete();
            }
        }
    }
    public JSONObject getJsonObject(GatewayURL gatewayURL, Map<String, Object> param, File file) {

        return getJsonObject(gatewayURL,param,file,null);
    }
    public JSONObject getJsonObject(GatewayURL gatewayURL, Map<String, Object> param, File file, Function<String,String> function) {
        String token = UserContextService.getAccessToken();
        return getJsonObject(gatewayURL,param,file,function,token);
    }

    public JSONObject getJsonObject(GatewayURL gatewayURL, Map<String, Object> param, File file, Function<String,String> function,String token) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("multipart/form-data;charset=UTF-8");
        headers.setContentType(type);
        headers.add("Authorization", token);
        String name = file.getName();
        headers.add("Content-disposition","attachment;filename="+ URLEncoder.encode(name));
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        FileSystemResource resource = new FileSystemResource(file);
        body.add("file", resource);
        body.setAll(param);
        HttpEntity<Map> requestEntity = new HttpEntity<Map>(body, headers);
        String url = getGatewayURL(gatewayURL);
        if(function != null){
            url = function.apply(url);
        }
        return getRemoteRequest(url, HttpMethod.POST, requestEntity, JSONObject.class);
    }

    public <T> T getRemoteRequest(String url,HttpMethod method,HttpEntity<?> requestEntity,Class<T> responseType){
        T body = null;
        ResponseEntity<T> exchange = template.exchange(url, method, requestEntity, responseType);
        body = exchange.getBody();
        return body;
    }

    public  File getFile(UploadedFile file) throws Exception {
        String fileName = fileScanService.saveUploadedFile(file);
        if(StringUtils.isEmpty(fileName)) return  null;
        File tempFile = new File(fileName);
        return tempFile;
    }

    public V2_BlobObject saveTemplateFile(byte[] contents,String fileName,String contentType,String boType,String uid){
        if(contents == null) return null;
        List<V2_BlobObject> blobObjectsByBoUid = findBlobObjectsByBoUid(uid);
        V2_BlobObject v2BlobObject = savFile(contents, fileName, contentType, boType, uid);
        if(!CollectionUtils.isEmpty(blobObjectsByBoUid)){
            deleteTemplateFile(blobObjectsByBoUid);
        }
        return v2BlobObject;
    }

    public V2_BlobObject savFile(byte[] contents,String fileName,String contentType,String boType,String uid){
        if(contents == null) return null;
        ByteArrayInputStream bais = new ByteArrayInputStream(contents);
        V2_BlobObject v2BlobObject = blobService.uploadFile(bais,fileName);
        v2BlobObject.setBoUuid(uid);
        v2BlobObject.setObjectType(contentType);
        v2BlobObject.setBoType(boType);
        blobObjectRepository.save(v2BlobObject);
        return v2BlobObject;
    }

    public List<V2_BlobObject>  saveBlobObject(List<V2_BlobObject> v2BlobObject,String uid){
        List<V2_BlobObject> resultObject= new ArrayList<>();
        for (V2_BlobObject item : v2BlobObject) {
            V2_BlobObject blobObject = new V2_BlobObject();
            BeanUtils.copyProperties(item,blobObject);
            blobObject.setId(null);
            blobObject.setBoUuid(uid);
            blobObjectRepository.save(blobObject);
            resultObject.add(blobObject);
        }
        return resultObject;
    }

    public void deleteTemplateFile(List<V2_BlobObject> listBlobObject){
        blobService.deleteBlobFile(listBlobObject);
    }

    public List<V2_BlobObject> findBlobObjectsByBoUid(String boUid){
        return blobService.findBlobObjectsByBoUid(boUid);
    }
}
