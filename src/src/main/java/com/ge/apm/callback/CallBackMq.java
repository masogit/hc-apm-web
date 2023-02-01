package com.ge.apm.callback;

import com.ge.apm.web.cboard.CBoardController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.rabbit.support.CorrelationData;

public class CallBackMq implements RabbitTemplate.ConfirmCallback {
    private static final Logger logger = LoggerFactory.getLogger(CallBackMq.class);



    @Override   //消息投递到exchange是否成功
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        if (b) {
            //设置消息投递成功
            logger.info("消息投递exchange成功 message:{},correlationData:{}" ,s ,correlationData);
        } else {
            //消息投递失败
            logger.info("消息投递exchange失败 message:{},correlationData:{}",s,correlationData);
        }
    }

}
