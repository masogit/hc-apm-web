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
    public static final String IMAGE_COMPRESS_ROUTING_KEY = "image.compress";

    public static final String ASSET_UPDATE_ES = "assetUpdateEsQueue";

    public static final String UPDATE_ES_ROUTING_KEY = "assetes.update";

    //物理数据删除
    public static final String PHYSICAL_DATA_DEL_CHANGE = "physicalDataDelChange";
    public static final String PHYSICAL_DATA_DEL_QUEUE = "physicalDataDelQueue";
    public static final String PHYSICALDATADELETE = "physicalDataDelete";

    //mqErrorLog
    public static final String BUSINESS_ERROR_LOG_QUEUE = "businessErrorLogQueue";
    public static final String BUSINESS_ERROR_LOG_CHANGE = "businessErrorLogChange";

    // 图片生成缩略图
    public static final String IMAGE_COMPRESS_CHANGE = "imageCompressChange";

    // 用户信息
    public static final String USER_CHANGE = "userChange";
}
