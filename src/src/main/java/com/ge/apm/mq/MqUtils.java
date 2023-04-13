package com.ge.apm.mq;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.callback.DataDelVO;
import com.ge.apm.callback.MqContext;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


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


    /**
     * 物理数据删除发送接口哦
     * @param object  数据对象
     * @param objectType  对象信息 表类型
     * @param userAccount  操作用户
     */
    public void sendPhysicalDataDel(Object object, String objectType, UserAccount userAccount,MqContext mqContex) {

        DataDelVO dataDelVO = new DataDelVO();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jsonData",dataDelVO);
        mqContex.setJsonObject(jsonObject);
        if(userAccount==null){
            userAccount = UserContextService.getCurrentUserAccount();
        }
        dataDelVO.setOperator(userAccount.getLoginName());
        dataDelVO.setCreatedDate(new Date());
        dataDelVO.setObjectType(objectType);
        dataDelVO.setObjectValue(JSONObject.toJSONString(object));
        dataDelVO.setHospitalUID(userAccount.getHospitalUID());
        dataDelVO.setInstitutionUID(userAccount.getInstitutionUID());
        dataDelVO.setSiteUID(userAccount.getSiteUID());
        dataDelVO.setTenantUID(userAccount.getTenantUID());

        dataDelVO.setServiceName(mqContex.getServiceName());
        dataDelVO.setClassName(mqContex.getClassName());
        dataDelVO.setMethodName(mqContex.getMethodName());
        dataDelVO.setDescribe(mqContex.getDescribe());

        sendToMq(mqContex);
    }



}
