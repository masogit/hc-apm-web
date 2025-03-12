package com.ge.apm.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
public class MaintenanceOrderDTO implements Serializable {
    // 时间戳字段统一使用Date类型（毫秒级时间戳）
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("acceptanceCheckTime")
    private Date acceptanceCheckTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("acceptanceTime")
    private Date acceptanceTime;

    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("accountName")
    private String accountName;

    @JsonProperty("assetId")
    private String assetId;

    // 资产状态（建议根据业务文档确认数值含义）
    @JsonProperty("assetStatus")
    private String assetStatus;

    @JsonProperty("contractNo")
    private String contractNo;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("createdDate")
    private Date createdDate;

    @JsonProperty("customerName")
    private String customerName;

    @JsonProperty("customerTel")
    private String customerTel;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("deviceReturnTime")
    private Date deviceReturnTime;

    @JsonProperty("faultDesc")
    private String faultDesc;

    @JsonProperty("ibAcct")
    private String ibAcct;

    @JsonProperty("id")
    private String id;

    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("lastModifiedDate")
    private Date lastModifiedDate;

    @JsonProperty("opportunityId")
    private String opportunityId;

    @JsonProperty("parentKey")
    private String parentKey;

    // 优先级（建议根据业务文档确认数值含义）
    @JsonProperty("priority")
    private String priority;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("requestTime")
    private Date requestTime;

    @JsonProperty("srNum")
    private String srNum;

    @JsonProperty("typeKey")
    private String typeKey;

    @JsonProperty("ucmId")
    private String ucmId;

    // 已有字段保持不变，补充缺失字段如下：

    @JsonProperty("crcWf")
    private String crcWf;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("shutdownTime")
    private Date shutdownTime;

    @JsonProperty("cancelReason")
    private String cancelReason;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("cancelTime")
    private Date cancelTime;

    @JsonProperty("reportTypeAcct")
    private String reportTypeAcct;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("consultingArrivingTime")
    private Date consultingArrivingTime;

    @JsonProperty("consultingPerson")
    private String consultingPerson;

    @JsonProperty("consultingPersonPhoneNum")
    private String consultingPersonPhoneNum;

    @JsonProperty("supplementManufacture")
    private String supplementManufacture;

    @JsonProperty("supplementSerialNum")
    private String supplementSerialNum;

    @JsonProperty("supplementAssetName")
    private String supplementAssetName;

    @JsonProperty("supplementAssetType")
    private String supplementAssetType;

    @JsonProperty("supplementClinicalDeptName")
    private String supplementClinicalDeptName;

    @JsonProperty("salesName")
    private String salesName;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("shutdownEndTime")
    private Date shutdownEndTime;

    @JsonProperty("faultType")
    private String faultType;
}