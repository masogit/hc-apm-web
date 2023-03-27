package com.ge.apm.callback;


import lombok.Data;

import java.util.Date;

@Data
public class DataDelVO {

    //操作人
    private String operator;
    //日期
    private Date date;
    //对象类型 表
    private String objectType;
    //对象值
    private String objectValue;

    private String tenantUID;

    private String institutionUID;

    private String hospitalUID;

    private String siteUID;
}
