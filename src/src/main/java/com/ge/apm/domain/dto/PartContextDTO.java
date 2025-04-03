package com.ge.apm.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Date;

@Data
public class PartContextDTO {
    @JsonProperty("createdBy")
    private String creatorId;          // 创建人唯一标识

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("createdDate")
    private Date createdAt;

    @JsonProperty("description")
    private String partDescription;    // 零件描述

    @JsonProperty("id")
    private String recordId;           // 记录唯一标识

    @JsonProperty("num")
    private Integer quantity;          // 零件数量

    @JsonProperty("orderNo")
    private String purchaseOrderNo;    // 采购订单号

    @JsonProperty("partNo")
    private String partNumber;         // 零件编号

    @JsonProperty("srId")
    private String serviceRequestId;   // 服务请求ID

    @JsonProperty("srNum")
    private String serviceRequestNo;   // 服务请求编号

    @JsonProperty("woId")
    private String workOrderId;        // 工单ID

    // 以下是新增字段
    @JsonProperty("partName")
    private String partName;           // 零件名称

    @JsonProperty("partSerialNum")
    private String partSerialNum;      // 零件序列号

    @JsonProperty("stepName")
    private String stepName;           // 步骤名称

    @JsonProperty("pendingUserSso")
    private String pendingUserSso;     // 待处理用户SSO

    @JsonProperty("stepStatus")
    private String stepStatus;         // 步骤状态

    @JsonProperty("operatorName")
    private String operatorName;       // 操作员姓名

    @JsonProperty("comments")
    private String comments;           // 备注


}