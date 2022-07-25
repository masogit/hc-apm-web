package com.ge.apm.mq;

import com.ge.apm.callback.MqContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class MqUtils {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    public  void sendToMq(MqContext mqContext){
        log.info("send to mq exchange:{},routingKey:{},message:{}",mqContext.getExChange(),mqContext.getRoutingKey(),mqContext.getContext());
        try{
            rabbitTemplate.convertAndSend(mqContext.getExChange(),mqContext.getRoutingKey(),mqContext);
        }catch (Exception e){
            log.error("send to mq error exchange:{},routingKey:{},message:{}\",mqContext.getExChange(),mqContext.getRoutingKey(),mqContext.getContext()");
            throw new RuntimeException(e);
        }

    }
}
