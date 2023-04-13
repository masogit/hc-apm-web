package com.ge.apm.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.callback.MqContext;
import com.ge.apm.callback.BusinessErrorLog;
import com.ge.apm.config.MQConfig;
import com.ge.apm.dao.AssetHistoryRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetHistory;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.asset.AssetHistoryService;
import com.ge.apm.service.remote.ElasticService;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import webapp.framework.context.LocaleHolder;

import javax.persistence.Column;
import java.util.*;

@Deprecated
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

    @Autowired
    private MqUtils mqUtils;


    @Override
    public void onMessage(Message message, Channel channel ) throws Exception {
        MqContext mqContext = null;

        try {
            mqContext = JSONObject.parseObject(new String(message.getBody()), MqContext.class);
            LOGGER.info("mq 监听的消息 message :{}",JSON.toJSONString(mqContext));
            JSONObject jsonObject = mqContext.getJsonObject();
            String assetInfoString = (String) jsonObject.get("assetInfo");
            Gson g = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            AssetInfo newAsset = g.fromJson(assetInfoString, AssetInfo.class);

            String currentUserAccountString = JSONObject.toJSONString(jsonObject.get("currentUserAccount"));
            UserAccount currentUserAccount = JSONObject.parseObject(currentUserAccountString, new TypeReference<UserAccount>() {});
            List<AssetHistory.AssetHistoryInfo> resultList =new ArrayList<>();
            if(newAsset.getId()!=null){
//                AssetHistory assetHistory=assetHistoryRepository.getAssetHistoryByAssetUidOne(newAsset.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("pageNum",0);
                map.put("pageSize",1);
                map.put("assetUid",newAsset.getUid());
                map.put("sortField","created_date");
                map.put("sortValue","DESC");
                List<AssetHistory> list = elasticService.searchAssetHistoryList(map);
                if(CollectionUtils.isEmpty(list)){
                    LOGGER.info("update asset assetHistory is null assetUid:{}",newAsset.getUid());
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    return;
                }
                AssetHistory assetHistory = list.get(0);
                String assetJson = assetHistory.getAssetJson();
                if(StringUtils.isEmpty(assetJson)){
                    LOGGER.info("update  asset  assetJson is null assetUid:{},historyUid:{}",newAsset.getUid(),assetHistory.getUid());
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    return;
                }
                AssetInfo assetInfo = g.fromJson(assetJson, AssetInfo.class);
                UserAccountThreadLocal.setUserAccount(currentUserAccount);
                String localeString = mqContext.getLocale();
                if(StringUtils.isBlank(localeString)  ||  localeString.equalsIgnoreCase("zh")){
                    Locale locale = new Locale("zh", "CN");
                    LocaleHolder.setLocale(locale);
                }
                resultList = assetHistoryService.getAssetsHistoryModifiedFields(assetInfo, newAsset);
                LocaleHolder.resetLocaleContext();
                UserAccountThreadLocal.remove();
            }

            //no change, just return
            if (resultList.isEmpty() && newAsset.getId()!=null) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            String jsonData = JSONArray.toJSONString(resultList);
            //gson序列化只与属性(字段)有关，与get开头的方法无关

            ExclusionStrategy strategy = new ExclusionStrategy() {
                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
                @Override
                public boolean shouldSkipField(FieldAttributes field) {
                    return field.getAnnotation(Column.class) == null;
                }
            };
            //gson序列化只与属性(字段)有关，与get开头的方法无关
            Gson gson = new GsonBuilder().setExclusionStrategies(strategy)
                    .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            String assetJson = gson.toJson(newAsset);
            AssetHistory assetHistory = buildAssetHistory(newAsset, currentUserAccount, jsonData, assetJson);
//            elasticService.insertAssetHistory(assetHistory);
            saveAssetHistory(assetHistory);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            int retryCount = mqContext.getRetryCount();
            if(retryCount>5){
                LOGGER.info("AssetChangeConsumer mq 消费次数 retryCount:{},message:{} ,error:  ",retryCount,JSON.toJSONString(mqContext),e);
                sendMqErrorLog(mqContext,e);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
            retryCount=retryCount+1;
            mqContext.setRetryCount(retryCount);
            mqUtils.sendToMq(mqContext);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true );
        }

    }

    private void sendMqErrorLog(MqContext mqContext,Exception e) {
        MqContext newmqContext = new MqContext();
        BeanUtils.copyProperties(mqContext,newmqContext);
        newmqContext.setRetryCount(0);
        newmqContext.setType("mqErrorLog");
        newmqContext.setExChange(MQConfig.BUSINESS_ERROR_LOG_CHANGE);
        newmqContext.setRoutingKey("");
        newmqContext.setTime(new Date());
        BusinessErrorLog businessErrorLog = new BusinessErrorLog();
        businessErrorLog.setCreatedDate(new Date());
        businessErrorLog.setData(JSONObject.toJSONString(mqContext));
        businessErrorLog.setErrorQueue(MQConfig.ASSET_UPDATE);
        businessErrorLog.setErrorLevel("ERROR");
        businessErrorLog.setException(e.getLocalizedMessage()+JSON.toJSONString(e));
        businessErrorLog.setClassName(mqContext.getClassName());
        businessErrorLog.setServiceName(mqContext.getServiceName());
        businessErrorLog.setMethodName(mqContext.getMethodName());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mqerrorlog", businessErrorLog);
        newmqContext.setJsonObject(jsonObject);
        mqUtils.sendToMq(newmqContext);
    }

    private void saveAssetHistory(AssetHistory assetHistory) {
        AssetHistory assetHistoryResult = assetHistoryRepository.save(assetHistory);

        MqContext mqContext = new MqContext();
        mqContext.setTime(new Date());
        mqContext.setType("asset");
        mqContext.setRoutingKey(MQConfig.UPDATE_ES_ROUTING_KEY);
        mqContext.setExChange(MQConfig.ASSET_CHANGE);
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("assetHistory",assetHistoryResult);

        mqContext.setJsonObject(jsonObject);
        mqUtils.sendToMq(mqContext);

    }

    private AssetHistory buildAssetHistory(AssetInfo newAsset, UserAccount currentUserAccount, String jsonData, String assetJson) {
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
        return assetHistory;
    }
}
