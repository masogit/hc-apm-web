package com.ge.apm.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.callback.DataDelVO;
import com.ge.apm.callback.MqContext;
import com.ge.apm.callback.BusinessErrorLog;
import com.ge.apm.config.MQConfig;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.remote.ElasticService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class PhysicalDataDelConsumer implements ChannelAwareMessageListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(PhysicalDataDelConsumer.class);

    @Autowired
    private ElasticService elasticService;

    @Autowired
    private MqUtils mqUtils;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MqContext mqContext = null;

        try {
            mqContext = JSONObject.parseObject(new String(message.getBody()), MqContext.class);

            LOGGER.info("PhysicalDataDelConsumer mq 监听的消息 message :{}",JSONObject.toJSONString(mqContext));
            JSONObject jsonObject = mqContext.getJsonObject();
            String string = JSONObject.toJSONString(jsonObject.get("jsonData"));
            DataDelVO dataDelVO = JSONObject.parseObject(string, new TypeReference<DataDelVO>() {});
            Boolean aBoolean = elasticService.insertPhysicalDataDel(dataDelVO);
            if(aBoolean){
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }else{
//                LOGGER.info("PhysicalDataDelConsumer insert es Failed");
//                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true );

                int retryCount = mqContext.getRetryCount();
                if(retryCount>5){
                    LOGGER.info("PhysicalDataDelConsumer mq 消费次数 retryCount:{},message:{}   ",retryCount, JSON.toJSONString(mqContext));
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    return;
                }
                retryCount=retryCount+1;
                mqContext.setRetryCount(retryCount);
                mqUtils.sendToMq(mqContext);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        }catch (Exception e){
            int retryCount = mqContext.getRetryCount();
            if(retryCount>5){
                LOGGER.info("PhysicalDataDelConsumer mq 消费次数 retryCount:{},message:{} ,error:  ",retryCount, JSON.toJSONString(mqContext),e);
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
        mqContext.setRetryCount(0);
        mqContext.setType("mqErrorLog");
        mqContext.setExChange(MQConfig.PHYSICAL_DATA_DEL_CHANGE);
        mqContext.setRoutingKey("");
        mqContext.setTime(new Date());
        BusinessErrorLog businessErrorLog = new BusinessErrorLog();
        businessErrorLog.setCreatedDate(new Date());
        businessErrorLog.setData(JSONObject.toJSONString(mqContext));
        businessErrorLog.setErrorQueue(MQConfig.PHYSICAL_DATA_DEL_QUEUE);
        businessErrorLog.setErrorLevel("ERROR");
        businessErrorLog.setException(e.toString());
        businessErrorLog.setClassName(mqContext.getClassName());
        businessErrorLog.setServiceName(mqContext.getServiceName());
        businessErrorLog.setMethodName(mqContext.getMethodName());
        mqUtils.sendToMq(mqContext);
    }


}
