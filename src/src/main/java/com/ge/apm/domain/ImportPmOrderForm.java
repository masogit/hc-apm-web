package com.ge.apm.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ImportPmOrderForm {
    private String assetName;//设备名称
    private String financingNum;   //资产编号
    private String departNum; //卡片号  设备编号（院方）
    private String pmNum;    //保养单号
    private String pmType; //保养类型    1 周期性  2 一次性 3 重测
    private String ruleLevel; //保养级别 2 2级规则  3 3级规则
    private String manhours; //工时
    private Date requestTime;  //创建时间
    private Date closeTime; //关单时间
}
