package com.ge.apm.job;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetHistoryRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetHistory;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.BatchJob;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
//@PropertySource(value = "classpath:mq.properties")
@Service
@EnableScheduling
public class AssetHistoryJob {
    public static final Logger LOGGER = LoggerFactory.getLogger(AssetHistoryJob.class);

//    @Value("${history.job.switch}")
//    private boolean flag;

    @Autowired
    private AssetHistoryRepository assetHistoryRepository;

    @Autowired
    private AssetInfoRepository assetInfoRepository;

//    @Scheduled(cron = "*/5 * * * * ?")
    @PostConstruct
    public void execute() {
        LOGGER.info("AssetHistoryJob start .........");
        ResourceBundle resourceBundle = ResourceBundle.getBundle("mq");
        String flag = resourceBundle.getString("history.job.switch");

        try {
            if("true".equals(flag)){
                doJob();
            }

        } catch (Exception e) {
            LOGGER.error("update asset history error e :",e);
        }
        LOGGER.info("AssetHistoryJob end .........");

    }

    private void doJob() {
        int pageNum=0;
        int pageSize=1000;
        int querySize=0;
        while (true){
            List<AssetHistory> assetHistorys = assetHistoryRepository.getAssetHistory(pageNum, pageSize);
            if(CollectionUtils.isEmpty(assetHistorys)){
                break;
            }
            querySize=querySize+assetHistorys.size();
            Set<String> uids = assetHistorys.stream().map(AssetHistory::getAssetUid).collect(Collectors.toSet());

            List<AssetInfo> assetInfos = assetInfoRepository.fetchListByUid(uids);
            Map<String, AssetInfo> collect = assetInfos.stream().collect(Collectors.toMap(AssetInfo::getUid, AssetInfo -> AssetInfo));
                for (AssetHistory  assetHistory:assetHistorys ){
                    AssetInfo assetInfo = collect.get(assetHistory.getAssetUid());
                    if(assetInfo!=null){
//                        LOGGER.info("assetInfo:{},uid:{}",assetInfo.toString(),assetInfo.getUid());
                        assetInfo.setHospital(null);
                        assetInfo.setQrCodeLib(null);
                        assetInfo.setAssetTags(null);
                        assetInfo.setBrand(null);
                        //gson序列化只与属性(字段)有关，与get开头的方法无关
                        Gson g = new GsonBuilder().create();
                        String str = g.toJson(assetInfo);
                        assetHistory.setAssetJson(str);
                    }
                }
            assetHistoryRepository.save(assetHistorys);
            pageNum=pageNum+pageSize;
        }

        LOGGER.info("查询assetHistory size:{}",querySize);
    }

}
