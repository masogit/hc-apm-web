package com.ge.apm.service.remote;

import com.ge.apm.domain.AssetClassificationModel;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MasterDataService  extends MicroServiceInvoker {

    private Logger logger = LoggerFactory.getLogger(getClass());



    public Map<String, Object> saveAssetClassificationModel(AssetClassificationModel asset) {
        HttpHeaders header = createHttpHeaders(UserContextService.getAccessToken());
        HttpEntity<AssetClassificationModel> requestEntity = new HttpEntity<>(asset,header);
        String gatewayURL =null;
        if(asset.getId()==null){
             gatewayURL = getGatewayURL(GatewayURL.ASSET_CLASSIFICATION_MODEL_SAVE);
        }else{
            gatewayURL = getGatewayURL(GatewayURL.ASSET_CLASSIFICATION_MODEL_EDIT);
        }
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return responseMap;
        } else {
            return null;
        }
    }


    public Map<String, Object> deleteAssetClassificationModel(AssetClassificationModel asset) {
        HttpHeaders header = createHttpHeaders(UserContextService.getAccessToken());
        HttpEntity<AssetClassificationModel> requestEntity = new HttpEntity<>(asset,header);
        String gatewayURL = getGatewayURL(GatewayURL.ASSET_CLASSIFICATION_MODEL_DELETE);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return responseMap;
        } else {
            return null;
        }
    }


    public Map<String, Object> getResMapByAsset(AssetClassificationModel asset) {
        HttpHeaders header = createHttpHeaders(UserContextService.getAccessToken());
        HttpEntity<AssetClassificationModel> requestEntity = new HttpEntity<>(asset,header);
        String gatewayURL = getGatewayURL(GatewayURL.ASSET_CLASSIFICATION_MODEL_GETRESMAPBYASSET);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody().get("data");
            return responseMap;
        } else {
            return null;
        }
    }


}
