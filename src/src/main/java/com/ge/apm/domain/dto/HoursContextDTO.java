package com.ge.apm.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Date;
@Data
public class HoursContextDTO {
    @JsonProperty("id")
    private String id;                  // 直接使用 id（原 recordId）

    @JsonProperty("srId")
    private String serviceRequestId;

    @JsonProperty("srNum")
    private String serviceRequestNo;    // 字段名改为 serviceRequestNo（原 serviceNumber）

    @JsonProperty("woId")
    private String workOrderId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty("startTime")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty("endTime")
    private Date endTime;

    @JsonProperty("workingHours")
    private Double workingHours;        // 字段名改为 workingHours（原 durationHours）

    @JsonProperty("type")
    private String type;                // 直接使用 type（原 workType）

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty("createdDate")
    private Date createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty("lastModifiedDate")
    private Date lastModifiedDate;

    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;

    @JsonProperty("tag")
    private String tag;
}