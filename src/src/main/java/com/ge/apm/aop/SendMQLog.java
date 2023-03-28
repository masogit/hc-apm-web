package com.ge.apm.aop;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD})
public @interface SendMQLog {

    //发送消息位置
     String serviceName() default "";
     String className() default "";
     String methodName() default "";
    //场景描述
     String describe() default "";
}
