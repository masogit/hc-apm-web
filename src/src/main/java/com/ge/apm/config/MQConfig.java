package com.ge.apm.config;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

//    @Bean
//    public TopicExchange createChange(){
//        TopicExchange assetChange = new TopicExchange("asset_change", false, false);
//        return assetChange;
//    }
//
//    @Bean
//    public Queue createQueue(){
//        Queue assetQueue = new Queue("asset_queue", true, false, false);
//        return assetQueue;
//
//    }


}
