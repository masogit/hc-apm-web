package com.ge.apm.callback;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

public class CallBackMq implements RabbitTemplate.ConfirmCallback {



    @Override   //消息投递到exchange是否成功
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        if (b) {
            //设置消息投递成功
            System.out.println("消息投递exchange成功 message:"+s+" correlationData:"+correlationData);
        } else {
            //消息投递失败
            System.out.println("消息投递exchange失败 message:"+s+" correlationData:"+correlationData);
        }
    }

}
