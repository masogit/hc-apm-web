package com.ge.apm.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ImportWorkOrderForm {
    private String assetName;//设备名称
    private String financingNum;   //资产编号
    private String departNum; //卡片号  设备编号（院方）
    private String srNum;    //报修单号
    private String casePriority; //紧急程度
    private String requestReason; //故障现象
    private String requestorLoginName; //报修人
    private Date requestTime;  //报修时间
    private String acceptorLoginName; //接单人
    private String repairType;    //维修类型
    private String assignee;    //验收人
    private Date confirmedUpDate; //恢复时间
    private String assetStatus;  //当前状态
    private String repairResult; //维修结果
    private String caseType;  //故障类别
    private String manhours; //工时
    private Date closeTime; //关单时间
    private String closer;  //关单人
}
