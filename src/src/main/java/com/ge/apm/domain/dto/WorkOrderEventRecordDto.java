package com.ge.apm.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class WorkOrderEventRecordDto {
    private String id;
    private Integer casePriority;
    private Date requestTime;
    private String requestReason;
    private String requestorName;
    private Integer currentStepId;
    private Integer dispatchMode;
    private String currentStepName;
    private String currentPersonName;
    private Date approvalDate;
    private Integer detailCount;
    private Integer assetId;
}
