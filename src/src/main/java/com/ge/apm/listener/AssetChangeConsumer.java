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
import com.ge.apm.view.sysutil.UserContextService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AssetChangeConsumer implements ChannelAwareMessageListener {
    @Autowired
    private AssetInfoRepository dao;
    @Autowired
    private AssetHistoryService assetHistoryService;
    @Autowired
    private AssetHistoryRepository assetHistoryRepository;

    @Override
    public void onMessage(Message message, Channel channel ) throws Exception {
        try {
            MqContext mqContext = JSONObject.parseObject(new String(message.getBody()), MqContext.class);
            System.out.println("mq 监听的消息 message :"+mqContext);

            JSONObject jsonObject = mqContext.getJsonObject();
            String assetInfoString = JSONObject.toJSONString(jsonObject.get("assetInfo"));
            AssetInfo newAsset = JSONObject.parseObject(assetInfoString, new TypeReference<AssetInfo>() {});

            AssetInfo oldAsset = null;
            String currentUserAccountString = JSONObject.toJSONString(jsonObject.get("currentUserAccount"));
            UserAccount currentUserAccount = JSONObject.parseObject(currentUserAccountString, new TypeReference<UserAccount>() {});
            List<AssetHistory.AssetHistoryInfo> resultList =new ArrayList<>();
            if(newAsset.getId()!=null){
                AssetHistory assetHistory=assetHistoryRepository.getAssetHistoryByAssetUidOne(newAsset.getUid());
                String assetJson = assetHistory.getAssetJson();
                AssetInfo assetInfo = JSONObject.parseObject(assetJson, AssetInfo.class);
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
            String assetJson = JSONObject.toJSONString(newAsset);
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
            assetHistoryService.save(assetHistory);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            try {
//                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true );
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

//
//    public void listener(Object context, Channel channel, Message message){
//        try {
//            System.out.println("mq 监听的消息 message :"+context);
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//        }catch (Exception e){
//            try {
//                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true );
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        }
//
//    }
}
