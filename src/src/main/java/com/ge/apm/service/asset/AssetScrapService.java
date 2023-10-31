package com.ge.apm.service.asset;

import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;

@Service
public class AssetScrapService  extends MicroServiceInvoker {

    protected static final   Logger logger = LoggerFactory.getLogger(MicroServiceInvoker.class);



    public void apply(Integer assetId,String applyReason,String approvalReason) {
        HashMap<String, String> map = new HashMap<>();
        map.put("assetId",String.valueOf(assetId));
        map.put("applyReason",applyReason);
        map.put("approvalReason",approvalReason);
        String token = UserContextService.getAccessToken();
        HttpEntity<HashMap> requestEntity = new HttpEntity<HashMap>(map, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.ASSET_SCRAP_APPLY);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
        if (isOkay(response)) {
            logger.info("AssetScrapService apply assetId:{},result:{}", assetId , responseMap.get("data"));
        }else {
            logger.error("AssetScrapService apply error  assetId:{},result:{}", assetId , responseMap.get("data"));
        }
    }
}
