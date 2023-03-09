package com.ge.apm.config;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    public static final String ASSET_UPDATE = "assetUpdateQueue";
    public static final String ASSET_DELETE = "assetDeleteQueue";
    public static final String ASSET_CHANGE = "assetChange";
    public static final String UPDATE_ROUTING_KEY = "asset.update";
    public static final String DELETE_ROUTING_KEY = "asset.delete";


    public static final String ASSET_UPDATE_ES = "assetUpdateEsQueue";

    public static final String UPDATE_ES_ROUTING_KEY = "assetes.update";



    public static final String PHYSICAL_DATA_DELETE_KEY = "physical.data.delete";
    public static final String PHYSICAL_DATA_DEL_CHANGE = "physicalDataDelChange";
    public static final String PHYSICAL_DATA_DEL_QUEUE = "physicalDataDelQueue";
    public static final String MESSAGE_TYPE = "physical.data.delete";





}
