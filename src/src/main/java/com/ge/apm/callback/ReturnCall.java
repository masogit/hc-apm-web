package com.ge.apm.callback;

import com.ge.apm.web.cboard.CBoardController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
public class ReturnCall implements RabbitTemplate.ReturnCallback {

    private static final Logger logger = LoggerFactory.getLogger(CBoardController.class);

    /**
     *只有消息从Exchange路由到Queue失败才会回调这个方法
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
//        System.out.println("消息从Exchage路由到Queue失败 exchange:"+exchange+"routingKey:"+routingKey+"replyText");
        logger.info("消息从Exchage路由到Queue失败 exchange:{},routingKey:{},replyText:{},replyCode:{},message:{}",exchange,routingKey,replyText,replyCode,message);
    }
}
