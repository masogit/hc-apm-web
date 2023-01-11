package com.ge.apm.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.callback.MqContext;
import com.ge.apm.dao.AssetHistoryRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetHistory;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetHistoryService;
import com.ge.apm.service.remote.ElasticService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AssetChangeConsumer implements ChannelAwareMessageListener {
    public static final Logger LOGGER = LoggerFactory.getLogger(AssetChangeConsumer.class);

    @Autowired
    private AssetInfoRepository dao;
    @Autowired
    private AssetHistoryService assetHistoryService;
    @Autowired
    private AssetHistoryRepository assetHistoryRepository;

    @Autowired
    private ElasticService elasticService;


    @Override
    public void onMessage(Message message, Channel channel ) throws Exception {
        MqContext mqContext = JSONObject.parseObject(new String(message.getBody()), MqContext.class);

        try {
            LOGGER.info("mq 监听的消息 message :{}",mqContext);

            JSONObject jsonObject = mqContext.getJsonObject();
            String assetInfoString = (String) jsonObject.get("assetInfo");
//            AssetInfo newAsset = JSONObject.parseObject(assetInfoString, new TypeReference<AssetInfo>() {});
            Gson g = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

            AssetInfo newAsset = g.fromJson(assetInfoString, AssetInfo.class);

            AssetInfo oldAsset = null;
            String currentUserAccountString = JSONObject.toJSONString(jsonObject.get("currentUserAccount"));
            UserAccount currentUserAccount = JSONObject.parseObject(currentUserAccountString, new TypeReference<UserAccount>() {});
            List<AssetHistory.AssetHistoryInfo> resultList =new ArrayList<>();
            if(newAsset.getId()!=null){
                AssetHistory assetHistory=assetHistoryRepository.getAssetHistoryByAssetUidOne(newAsset.getUid());

                if(assetHistory==null){
                    LOGGER.info("update asset assetHistory is null assetUid:{}",newAsset.getUid());
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    return;
                }
                String assetJson = assetHistory.getAssetJson();
                if(StringUtils.isEmpty(assetJson)){
                    LOGGER.info("update  asset  assetJson is null assetUid:{},historyUid:{}",newAsset.getUid(),assetHistory.getUid());
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    return;
                }
                AssetInfo assetInfo = g.fromJson(assetJson, AssetInfo.class);
//                AssetInfo assetInfo = JSONObject.parseObject(assetJson, AssetInfo.class);
                oldAsset=assetInfo;
                UserAccountThreadLocal.setUserAccount(currentUserAccount);
                resultList = assetHistoryService.getAssetsHistoryModifiedFields(oldAsset, newAsset);
                UserAccountThreadLocal.remove();
            }

            //no change, just return
            if (resultList.isEmpty() && newAsset.getId()!=null) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            String jsonData = JSONArray.toJSONString(resultList);
            newAsset.setHospital(null);
            newAsset.setQrCodeLib(null);
            newAsset.setAssetTags(null);
            newAsset.setBrand(null);
            newAsset.setTags(null);
            //gson序列化只与属性(字段)有关，与get开头的方法无关
            String assetJson = g.toJson(newAsset);
//            String assetJson = JSONObject.toJSONString(newAsset);
            AssetHistory assetHistory = new AssetHistory();
            assetHistory.setAssetUid(newAsset.getUid());
            assetHistory.setAssetName(newAsset.getName());
            assetHistory.setFinancingNum(newAsset.getFinancingNum());
            assetHistory.setJsonData(jsonData);
            assetHistory.setCreatedDate(new Date());
            assetHistory.setCreatedBy(currentUserAccount.getName());
            assetHistory.setSiteUID(currentUserAccount.getSiteUID());
            assetHistory.setHospitalUID(currentUserAccount.getHospitalUID());
            assetHistory.setTenantUID(currentUserAccount.getTenantUID());
            assetHistory.setInstitutionUID(currentUserAccount.getInstitutionUID());
            assetHistory.setClinicalDeptUid(newAsset.getClinicalDeptUID());
            assetHistory.setAssetJson(assetJson);
            elasticService.insertAssetHistory(assetHistory);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            LOGGER.info("mq 消息 error message:{}, e :",mqContext,e);
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true );
        }

    }
}
