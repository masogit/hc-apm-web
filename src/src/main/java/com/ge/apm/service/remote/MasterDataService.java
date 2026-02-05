package com.ge.apm.service.remote;

import com.ge.apm.domain.AssetClassificationModel;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.dto.SelectItemDto;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.AssetClassification32Enum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MasterDataService  extends MicroServiceInvoker {

    private Logger logger = LoggerFactory.getLogger(MasterDataService.class);



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


    public List<SelectItemDto>   autoAssetClassification32(AssetClassificationModel assetClassificationModel){
        ArrayList<SelectItemDto> selectItemDtos = new ArrayList<>();
        Map<String, Object> resMapByAsset = getResMapByAsset(assetClassificationModel);
        List<String> cats = new ArrayList<>();
        if(resMapByAsset!=null){
            String cat1 = (String) resMapByAsset.get("category");
            String cat2 = (String) resMapByAsset.get("category2");
            String cat3 = (String) resMapByAsset.get("category3");
            if (org.apache.commons.lang.StringUtils.isNotBlank(cat1)) {
                selectItemDtos.add(new SelectItemDto(cat1,toPercentageString(resMapByAsset.get("Prob"))));
                cats.add(cat1);
            }
            if (org.apache.commons.lang.StringUtils.isNotBlank(cat2)) {
                selectItemDtos.add(new SelectItemDto(cat2,toPercentageString(resMapByAsset.get("Prob2"))));
                cats.add(cat2);
            }
            if (org.apache.commons.lang.StringUtils.isNotBlank(cat3)) {
                selectItemDtos.add(new SelectItemDto(cat3,toPercentageString(resMapByAsset.get("Prob3"))));
                cats.add(cat3);
            }
        }
        List<String> allDisplayNameList = AssetClassification32Enum.getAllDisplayNameList();
        for (String s : allDisplayNameList) {
            boolean b = cats.stream().anyMatch(ite -> ite.equals(s));
            if(!b){
                selectItemDtos.add(new SelectItemDto(s,null));
            }

        }
        return selectItemDtos;
    }

    private String toPercentageString(Object value) {
        if (value == null) {
            return "0%";
        }
        try {
            double d;
            if (value instanceof Number) {
                d = ((Number) value).doubleValue();
            } else {
                d = Double.parseDouble(value.toString());
            }
            int percent = (int) Math.round(d * 100);
            if(percent==0){
                percent=1;
            }
            return percent + "%";
        } catch (Exception e) {
            logger.error("设备32分类匹配值转换error: ",e);
            return "0%";
        }
    }
}
