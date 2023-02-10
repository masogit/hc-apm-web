package com.ge.apm.listener;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.callback.MqContext;
import com.ge.apm.domain.AssetHistory;
import com.ge.apm.service.remote.ElasticService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;


public class AssetChangeEsConsumer implements ChannelAwareMessageListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(AssetChangeEsConsumer.class);

    @Autowired
    private ElasticService elasticService;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try{
            MqContext mqContext = JSONObject.parseObject(new String(message.getBody()), MqContext.class);

            LOGGER.info("AssetChangeEsConsumer mq 监听的消息 message :{}",mqContext);
            JSONObject jsonObject = mqContext.getJsonObject();
            String string = JSONObject.toJSONString(jsonObject.get("assetHistory"));

            AssetHistory assetHistory = JSONObject.parseObject(string, new TypeReference<AssetHistory>() {});


            Boolean aBoolean = elasticService.insertAssetHistory(assetHistory);
            if(!aBoolean){
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true );
                return;
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            LOGGER.error("AssetChangeEsConsumer 消息处理异常！"+e.getMessage(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true );
        }

    }
}
