package com.ge.apm.mq;

import com.ge.apm.callback.MqContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MqUtils {

    public static final Logger log = LoggerFactory.getLogger(MqUtils.class);



    @Autowired
    private  RabbitTemplate rabbitTemplate;

    public   void sendToMq(MqContext mqContext){
//        log.info("send to mq exchange:{},routingKey:{},message:{}",mqContext.getExChange(),mqContext.getRoutingKey(),mqContext.getJsonObject());
        try{
            rabbitTemplate.convertAndSend(mqContext.getExChange(),mqContext.getRoutingKey(),mqContext);
        }catch (Exception e){
            log.error("send to mq error exchange:{},routingKey:{},message:{},e :",mqContext.getExChange(),mqContext.getRoutingKey(),mqContext.getJsonObject(),e);
            throw  e;
        }

    }




    public   void sendToMq(String routingKey,String exChange,Object object){
        rabbitTemplate.convertAndSend(exChange,routingKey,object);
    }


}
