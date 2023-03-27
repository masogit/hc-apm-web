package com.ge.apm.listener;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.callback.DataDelVO;
import com.ge.apm.callback.MqContext;
import com.ge.apm.domain.AssetHistory;
import com.ge.apm.service.remote.ElasticService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;

public class PhysicalDataDelConsumer implements ChannelAwareMessageListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(PhysicalDataDelConsumer.class);

    @Autowired
    private ElasticService elasticService;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            MqContext mqContext = JSONObject.parseObject(new String(message.getBody()), MqContext.class);

            LOGGER.info("PhysicalDataDelConsumer mq 监听的消息 message :{}",mqContext);
            JSONObject jsonObject = mqContext.getJsonObject();
            String string = JSONObject.toJSONString(jsonObject.get("jsonData"));
            DataDelVO dataDelVO = JSONObject.parseObject(string, new TypeReference<DataDelVO>() {});
            Boolean aBoolean = elasticService.insertPhysicalDataDel(dataDelVO);
            if(aBoolean){
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }else{
                LOGGER.info("PhysicalDataDelConsumer insert es Failed");
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true );
            }
        }catch (Exception e){
            LOGGER.error("PhysicalDataDelConsumer insert es error e:",e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true );

        }


    }
}
