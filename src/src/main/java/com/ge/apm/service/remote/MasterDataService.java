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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class MasterDataService  extends MicroServiceInvoker {

    private Logger logger = LoggerFactory.getLogger(MasterDataService.class);

    public List<Map<String, String>> getUserSignature() {
        HttpHeaders header = createHttpHeaders(UserContextService.getAccessToken());
        HttpEntity<String> requestEntity = new HttpEntity<>(header);
        String url = getGatewayURL(GatewayURL.USER_SIGNATURE);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            List<Map<String, String>> responseBody = (List<Map<String, String>>) response.getBody().get("data");
            return responseBody;
        } else {
            return null;
        }
    }

    public String addUserSignature(String signatureId) {
        HttpHeaders header = createHttpHeaders(UserContextService.getAccessToken());
        HttpEntity<String> requestEntity = new HttpEntity<>(signatureId, header);
        String url = getGatewayURL(GatewayURL.USER_SIGNATURE);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (String) responseMap.get("message");
        } else {
            return null;
        }
    }

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

    /** 单批设备数，与 ONNX 的 ONNX_BATCH_SIZE(100) 对齐，每批只触发一次 ONNX 调用 */
    private static final int WEB_BATCH_SIZE = 100;

    /** 并行线程数，控制对 ONNX 的并发压力 */
    private static final int WEB_PARALLELISM = 4;

    private static final ExecutorService webBatchExecutor = Executors.newFixedThreadPool(WEB_PARALLELISM, r -> {
        Thread t = new Thread(r, "web-batch");
        t.setDaemon(true);
        return t;
    });

    /**
     * 批量获取设备分类推理结果，按 WEB_BATCH_SIZE 分批并行调用 masterData。
     * 每批 100 台，masterData 直接透传给 ONNX（无需二次拆分），4 线程并行。
     * 返回结果列表与输入列表索引一一对应。
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> batchGetResMapByAsset(List<AssetClassificationModel> assets) {
        int total = assets.size();
        List<Map<String, Object>> allResults = Collections.synchronizedList(new ArrayList<>(total));
        for (int i = 0; i < total; i++) {
            allResults.add(null);
        }

        // 主线程捕获 token，供子线程使用（RequestContextHolder 是线程局部的）
        String token = UserContextService.getAccessToken();
        String gatewayURL = getGatewayURL(GatewayURL.ASSET_CLASSIFICATION_MODEL_BATCH_GETRESMAPBYASSET);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int start = 0; start < total; start += WEB_BATCH_SIZE) {
            int end = Math.min(start + WEB_BATCH_SIZE, total);
            int offset = start;
            List<AssetClassificationModel> chunk = assets.subList(start, end);

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    HttpHeaders header = createHttpHeaders(token);
                    HttpEntity<List<AssetClassificationModel>> requestEntity = new HttpEntity<>(chunk, header);
                    ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
                    if (isOkay(response)) {
                        List<Map<String, Object>> chunkResults = (List<Map<String, Object>>) response.getBody().get("data");
                        if (chunkResults != null) {
                            for (int j = 0; j < chunkResults.size(); j++) {
                                allResults.set(offset + j, chunkResults.get(j));
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("批量分类推理分批失败 (offset={}, size={}): {}", offset, chunk.size(), e.getMessage(), e);
                }
            }, webBatchExecutor);
            futures.add(future);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(5, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.warn("部分批量分类推理超时或失败", e);
        }

        logger.info("批量分类推理完成，总数: {}, 成功: {}", total,
                allResults.stream().filter(r -> r != null).count());
        return allResults;
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
