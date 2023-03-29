package com.ge.apm.aop;

import com.ge.apm.callback.MqContext;

public class SendMqThreadLocal {
    static ThreadLocal<MqContext> mqContextThreadLocal=new ThreadLocal<>();



    public static void setMqContext(MqContext context){
        mqContextThreadLocal.set(context);
    }
    public  static MqContext getMqContext() {
        MqContext mqContext = mqContextThreadLocal.get();
        return mqContext;
    }
    public static void remove(){
        mqContextThreadLocal.remove();
    }



}
