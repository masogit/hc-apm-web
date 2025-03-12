package com.ge.apm.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Date;
@Data
public class HoursContextDTO {
    @JsonProperty("id")
    private String recordId;

    @JsonProperty("srId")
    private String serviceRequestId;

    @JsonProperty("srNum")
    private String serviceNumber;

    @JsonProperty("woId")
    private String workOrderId;

    // 修复日期格式：保持与原始字段一致的日期格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty("startTime")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty("endTime")
    private Date endTime;

    @JsonProperty("workingHours")
    private Double durationHours;

    @JsonProperty("type")
    private String workType;

    @JsonProperty("createdBy")
    private String creatorId;

    // 修复字段名：createdDate -> creationDate 改为 createdDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty("createdDate")
    private Date createdDate;  // 字段名修正为 createdDate

    // 补充缺失字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty("lastModifiedDate")
    private Date lastModifiedDate;

    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;

    @JsonProperty("tag")
    private String workTag;
}