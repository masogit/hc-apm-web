package com.ge.apm.service.nurseSite.export;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class AssetOptRecordExport {

    public AssetOptRecordExport() {
    }

    public AssetOptRecordExport(Date startDate, Date endDate, String createByUid, String modifyByUid, String comment) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.createByUid = createByUid;
        this.modifyByUid = modifyByUid;
        this.comment = comment;
    }

    // 开机时间
    @JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    // 关机时间
    @JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss", timezone = "GMT+8")
    private Date endDate;


    private String createByUid;

    private String modifyByUid;

    private String comment;
    private String costTime;

    public String getCostTime(){

      return costTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCreateByUid() {
        return createByUid;
    }

    public void setCreateByUid(String createByUid) {
        this.createByUid = createByUid;
    }

    public String getModifyByUid() {
        return modifyByUid;
    }

    public void setModifyByUid(String modifyByUid) {
        this.modifyByUid = modifyByUid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }



    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }
}
