package com;

import com.ge.apm.callback.MqContext;
import com.ge.apm.mq.MqUtils;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:config/springmvc-parent.xml")
public class Test {
    @Autowired
    private MqUtils mqUtils;

    @org.junit.Test
    public void to(){

        HashMap<Object, Object> map = new HashMap<>();
        map.put("id",1);
        map.put("name","张三");
        map.put("password","123456");
        MqContext mqContext = new MqContext();
        mqContext.setTime(new Date());
        mqContext.setType("asset");
        mqContext.setContext(map);
        mqContext.setRoutingKey("asset.delete");
        mqContext.setExChange("assetChange");
        mqUtils.sendToMq(mqContext);
    }

}
