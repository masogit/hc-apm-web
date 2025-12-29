package com.ge.apm.service.remote;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.callback.BusinessErrorLog;
import com.ge.apm.callback.DataDelVO;
import com.ge.apm.dao.AssetHistoryRepository;
import com.ge.apm.domain.AssetHistory;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
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
        map.put("sortField","created_date");
        map.put("sortValue","DESC");
        map.put("esIndex","asset_history_v2");

        HttpEntity<HashMap> requestEntity = new HttpEntity<HashMap>(map, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.ELASTICSEARCH_QUERY);
        try {
            ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            if (isOkay(response)) {
                LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
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
            }
        } catch (Exception e) {
            logger.info("searchAssetHistory Failed : {}", e.getMessage(), e);
        }
        PageRequest  pageRequest=  new PageRequest(pageNum, pageSize, null);
        Page<AssetHistory> jsonPage = new PageImpl<>(new ArrayList<>(), pageRequest, 0);
        return jsonPage;
    }

    public Page<Object> searchHistory(List<SearchFilter> searchFilters, String token, int pageNum, int pageSize, String esIndex){
        try {
            Map<String, Object> map = MapUtil.newHashMap(4);
            map.put("list", searchFilters);
            map.put("pageNum", pageNum);
            map.put("pageSize", pageSize);
            map.put("esIndex", esIndex);
            map.put("sortField","created_date");
            map.put("sortValue","DESC");
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(map, createHttpHeaders(token));
            String gatewayURL = getGatewayURL(GatewayURL.ELASTICSEARCH_QUERY);
            ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            if (isOkay(response)) {
                LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
                LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
                String content = JSON.toJSONString(data.get("content"));
                List<Object> histories = JSON.parseObject(content, new TypeReference<List<Object>>() {});
                if(histories == null){
                    histories = new ArrayList<>();
                }
                int totalElements = (int) data.get("totalElements");
                PageRequest  pageRequest = new PageRequest(pageNum, pageSize, null);
                return new PageImpl<>(histories, pageRequest, totalElements);
            } else {
                logger.info("searchHistory Failed ");
                PageRequest  pageRequest=  new PageRequest(pageNum, pageSize, null);
                return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            logger.error("elastic service is not available");
            return new PageImpl<>(new ArrayList<>(), null, 0);
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
    public Boolean insertPhysicalDataDel(DataDelVO dataDelVO) throws Exception {
        HttpEntity<DataDelVO> requestEntity = new HttpEntity<>(dataDelVO);
        String gatewayURL = getGatewayURL(GatewayURL.INSERT_PHYSICAL_DATA_DEL);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        return isOkay(response);
    }


    public Boolean insertBusinessErrorLog(BusinessErrorLog mqErrorLog) {
        HttpEntity<BusinessErrorLog> requestEntity = new HttpEntity<>(mqErrorLog);
        String gatewayURL = getGatewayURL(GatewayURL.INSERT_BUSINESS_ERROR_LOG);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        return isOkay(response);
    }
}
