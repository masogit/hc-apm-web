package com.ge.apm.service.remote;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.AssetHistoryRepository;
import com.ge.apm.domain.AssetHistory;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.util.*;

@Service
public class ElasticService extends MicroServiceInvoker {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AssetHistoryRepository assetHistoryRepository;


    public Page<AssetHistory> searchAssetHistory(List<SearchFilter> searchFilters, String token,int pageNum,int pageSize){

        HashMap<String, Object> map = new HashMap<>();
        map.put("list",searchFilters);
        map.put("pageNum",pageNum);
        map.put("pageSize",pageSize);
        HttpEntity<HashMap> requestEntity = new HttpEntity<HashMap>(map, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.SEARCH_ASSET_HISTORY);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
//            String dat = JSON.toJSONString(responseMap.get("data"));
//            Page<AssetHistory> assetHistoryPage = JSON.parseObject(dat, new TypeReference<Page<AssetHistory>>() {});
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
            String content = JSON.toJSONString(data.get("content"));
            List<AssetHistory> assetHistories = JSON.parseObject(content, new TypeReference<List<AssetHistory>>() {});
            if(assetHistories==null){
                assetHistories=new ArrayList<>();
            }
            int totalElements = (int)data.get("totalElements");
            PageRequest  pageRequest=  new PageRequest(pageNum, pageSize, null);
            Page<AssetHistory> jsonPage = new PageImpl<>(assetHistories, pageRequest, totalElements);
            return jsonPage;
        } else {
            return null;
        }
    }

    public List<AssetHistory> searchAssetHistoryList(HashMap<String, Object> map){


        HttpEntity<HashMap> requestEntity = new HttpEntity<HashMap>(map);
        String gatewayURL = getGatewayURL(GatewayURL.SEARCH_ASSET_HISTORY_LIST);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            String dat = JSON.toJSONString(responseMap.get("data"));
            List<AssetHistory> list = JSON.parseObject(dat, new TypeReference<List<AssetHistory>>() {});
            return list;
        } else {
            return null;
        }
    }


    public Boolean insertAssetHistory(AssetHistory assetHistory) throws Exception {
        HttpEntity<AssetHistory> requestEntity = new HttpEntity<AssetHistory>(assetHistory);
        String gatewayURL = getGatewayURL(GatewayURL.INSERT_ASSET_HISTORY);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        return isOkay(response);
    }

}
