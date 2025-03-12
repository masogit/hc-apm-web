package com.ge.apm.domain.dto;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class WorkOrderDTO {
    @JsonProperty("id")
     private String workOrderId;

    @JsonProperty("woNum")
     private String workOrderNumber;

    @JsonProperty("srId")
    private String serviceRequestId;

    @JsonProperty("srNum")
    private String serviceRequestNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("closeTime")
    private Date closeTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("createdDate")
    private Date creationTime;

    @JsonProperty("createdBy")
    private String creatorId;

    private String detectionResult;

    private String engineer;

    @JsonProperty("engineerId")
    private String engineerId;


    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("lastModifiedDate")
    private Date lastModifiedDate;


    @JsonProperty("lastModifiedBy")
    private String lastModifierId;

    @JsonProperty("mainSub")
    private String mainSubType;

    @JsonProperty("ownerId")
    private String ownerId;

    @JsonProperty("ownerName")
    private String ownerName;

    @JsonProperty("parentTypeKey")
    private String parentTypeCode;

    @JsonProperty("problemDesc")
    private String problemDescription;


    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("reportTime")
    private Date reportTime;

    @JsonProperty("serviceDesc")
    private String serviceDescription;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("signInTime")
    private Date signInTime;

    private String status;

    @JsonProperty("typeKey")
    private String typeCode;

    private String vendor;

    @JsonProperty("vendorRepairName")
    private String vendorContactName;

    @JsonProperty("vendorRepairTel")
     private String vendorContactPhone;


    // 取消相关字段
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("cancelTime")
    private Date cancelTime;

    @JsonProperty("cancelBy")
    private String cancelBy;

    @JsonProperty("cancelReason")
    private String cancelReason;

    // 设备归还时间
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("returnTime")
    private Date returnTime;

    // 备份时间
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("backupStartTime")
    private Date backupStartTime;

    // 费用与工时
    @JsonProperty("workHour")
    private Double workHour;

    @JsonProperty("expense")
    private Double expense;

    @JsonProperty("otherExpense")
    private Double otherExpense;



}