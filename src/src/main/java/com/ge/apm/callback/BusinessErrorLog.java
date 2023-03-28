package com.ge.apm.callback;


import lombok.Data;

import java.util.Date;

@Data
public class BusinessErrorLog {


    private Date createdDate;
    private String data;
    private String exception;
    private String serviceName;
    private String className;
    private String methodName;
    //场景描述
    private String describe;
    //错误等级
    private String errorLevel;
    private String errorQueue;

}