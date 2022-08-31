package com.ge.apm.callback;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

@Data
public class MqContext implements Serializable {
    //发送消息的时间
    private Date time;
    //消息类型
    private String type;
//    private HashMap context;
    //消息路由键
    private String routingKey;
    //消息发送的交换机
    private String exChange;
    //消息内容
    private JSONObject jsonObject;
}
