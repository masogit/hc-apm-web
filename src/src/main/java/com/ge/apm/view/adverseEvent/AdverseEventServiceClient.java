package com.ge.apm.view.adverseEvent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.domain.AdverseEvents;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.service.utils.TempFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class AdverseEventServiceClient extends MicroServiceInvoker {
    @Autowired
    private BlobFileService blobService;

    public AdverseEvents tempSaveAdverseEvents(String token, AdverseEvents adverseEvents) throws RuntimeException{
        HttpEntity<AdverseEvents> requestEntity = new HttpEntity<>(adverseEvents, createHttpHeaders(token));
        String gatewayURL = "";
        switch (adverseEvents.getStatus()) {
            case 1:
            case 2:
                gatewayURL = getGatewayURL(GatewayURL.ADD_ADVERSE_EVENTS);
                break;
            case 3:
                gatewayURL = getGatewayURL(GatewayURL.APPROVE_TEMP_SAVE_ADVERSE_EVENTS);
                break;
            case 5:
                gatewayURL = getGatewayURL(GatewayURL.ADVERSE_EVENTS_INVALID);
                break;
            case 4:
            case 6:
                gatewayURL = getGatewayURL(GatewayURL.APPROVE_ADVERSE_EVENTS);
                break;
        }

        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (!isOkay(response)) {
            //如果失败则清除附件
            if (!ObjectUtils.isEmpty(adverseEvents.getDeviceFaultPerformFile())) {
                adverseEvents.getDeviceFaultPerformFile().removeIf(v -> {
                    if (StringUtils.isEmpty(v.getId())) {
                        return blobService.deleteBlobFilesByStorageId(v.getObjectId());
                    }
                    return false;
                });
            }
            if (!ObjectUtils.isEmpty(adverseEvents.getDeviceFaultPerformFile())) {
                adverseEvents.getHarmPerformFile().removeIf(v -> {
                    if (StringUtils.isEmpty(v.getId())) {
                        return blobService.deleteBlobFilesByStorageId(v.getObjectId());
                    }
                    return false;
                });
            }
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            String message = responseMap.get("message").toString();
            throw new RuntimeException(message);
        }
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) response.getBody();
        if (ObjectUtils.isEmpty(body.get("data"))) {
            return adverseEvents;
        }
        return JSON.parseObject(JSON.toJSONString(body.get("data")), AdverseEvents.class);
    }

    public Page<AdverseEvents> getAdverseEventsById(String token, PageRequest pageRequest, String status, String queryStr, String findType) {
        HttpEntity<String> requestEntity = new HttpEntity<>(createHttpHeaders(token));
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("page", (pageRequest.getPageNumber() == 0?1:pageRequest.getPageNumber()+1) + "");
        paramMap.add("pageSize", pageRequest.getPageSize() + "");
        paramMap.add("status", status);
        paramMap.add("queryStr", queryStr);
        String gatewayURL = getGatewayURL(findType.equals("report")?GatewayURL.ADVERSE_EVENTS_LIST:GatewayURL.APPROVE_ADVERSE_EVENTS_LIST);
        UriComponentsBuilder builderUrl = UriComponentsBuilder.fromHttpUrl(gatewayURL).queryParams(paramMap);
        ResponseEntity<String> response = template.exchange(builderUrl.build(false).toUriString(), HttpMethod.GET, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            String result = response.getBody();
            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONArray data = jsonObject.getJSONArray("data");
            if (null != data) {
                return new PageImpl<>(data.toJavaList(AdverseEvents.class), pageRequest, jsonObject.getInteger("rowCount"));
            }
        }
        return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
    }

    public AdverseEvents getAdverseEventsById(String token, String id) {
        HttpEntity<String> requestEntity = new HttpEntity<>(id, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.GET_ADVERSE_EVENTS_INFO_BY_ID);
        String url = gatewayURL.replace("{id}", id);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            String result = response.getBody();
            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            AdverseEvents adverseEvents = JSONObject.toJavaObject(data, AdverseEvents.class);
            return adverseEvents;
        }
        return null;
    }

    public void approveAdverseEvents(String token, AdverseEvents adverseEvents) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        HttpEntity<AdverseEvents> requestEntity = new HttpEntity<>(adverseEvents, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.APPROVE_ADVERSE_EVENTS);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (!isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            String message = responseMap.get("message").toString();
            throw new RuntimeException(message);
        }
    }

    public void sendReportMail(String token, String[] mails, String[] workOrderNums) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mailReceiverAddress", mails);
        jsonObject.put("workOrderNums", workOrderNums);
        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(jsonObject, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.ADVERSE_EVENTS_SENDMAIL);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (!isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            String message = responseMap.get("message").toString();
            throw new RuntimeException(message);
        }
    }

    public TempFileUtil downLoadPdf(String token, String[] workOrderNums) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("workOrderNums", workOrderNums);
        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(jsonObject, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.ADVERSE_EVENTS_EXPORT_PDF);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (!isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            String message = responseMap.get("message").toString();
            throw new RuntimeException(message);
        }
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) response.getBody();
        if (ObjectUtils.isEmpty(body.get("data"))) {
            return new TempFileUtil();
        }
        return JSON.parseObject(JSON.toJSONString(body.get("data")), TempFileUtil.class);
    }
}
