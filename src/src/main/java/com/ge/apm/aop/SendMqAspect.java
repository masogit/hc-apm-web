package com.ge.apm.aop;


import com.alibaba.fastjson.JSON;
import com.ge.apm.callback.MqContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SendMqAspect {

    private final static Logger LOGGER = LoggerFactory.getLogger(SendMqAspect.class);

    @Pointcut("@annotation(com.ge.apm.aop.SendMQLog)")
    public void sendMQLog() {
    }

    @Before("sendMQLog() && @annotation(sendMQLog)")
    public void doBefore(SendMQLog sendMQLog) {
        String className = sendMQLog.className();
        String serviceName = sendMQLog.serviceName();
        String methodName = sendMQLog.methodName();
        String describe = sendMQLog.describe();
        LOGGER.info("@SendMQLog before msg: :{},{},{},{}", serviceName,className,methodName,describe);
        MqContext mqContext=new MqContext();
        mqContext.setClassName(className);
        mqContext.setServiceName(serviceName);
        mqContext.setMethodName(methodName);
        mqContext.setDescribe(describe);
        SendMqThreadLocal.setMqContext(mqContext);
        //do something
         LOGGER.info("@SendMQLog before msg: :{},{},{},{}");
    }


    @After("sendMQLog()")
    public void after(JoinPoint joinPoint) {
        SendMqThreadLocal.remove();

    }

}
