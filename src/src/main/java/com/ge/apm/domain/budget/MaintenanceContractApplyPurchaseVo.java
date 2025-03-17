package com.ge.apm.domain.budget;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;


public class MaintenanceContractApplyPurchaseVo {

    private String techAnalysisDiscussion;

    private String meetAuditComment;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date meetAuditDate;

    private String meetReasoningResult;

    private String recordPersonName;

    private String meetAuditRemark;

    public String getTechAnalysisDiscussion() {
        return techAnalysisDiscussion;
    }

    public void setTechAnalysisDiscussion(String techAnalysisDiscussion) {
        this.techAnalysisDiscussion = techAnalysisDiscussion;
    }

    public String getMeetAuditComment() {
        return meetAuditComment;
    }

    public void setMeetAuditComment(String meetAuditComment) {
        this.meetAuditComment = meetAuditComment;
    }

    public Date getMeetAuditDate() {
        return meetAuditDate;
    }

    public void setMeetAuditDate(Date meetAuditDate) {
        this.meetAuditDate = meetAuditDate;
    }

    public String getMeetReasoningResult() {
        return meetReasoningResult;
    }

    public void setMeetReasoningResult(String meetReasoningResult) {
        this.meetReasoningResult = meetReasoningResult;
    }

    public String getRecordPersonName() {
        return recordPersonName;
    }

    public void setRecordPersonName(String recordPersonName) {
        this.recordPersonName = recordPersonName;
    }

    public String getMeetAuditRemark() {
        return meetAuditRemark;
    }

    public void setMeetAuditRemark(String meetAuditRemark) {
        this.meetAuditRemark = meetAuditRemark;
    }
}
