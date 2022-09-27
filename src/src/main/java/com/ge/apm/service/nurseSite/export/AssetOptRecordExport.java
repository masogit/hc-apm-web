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

        if(startDate == null){
            return "";
        }
        LocalDateTime localDateTimeStart = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime localDateTimeEnd =null;
        if(endDate == null){
            localDateTimeEnd=LocalDateTime.now();
        }else{
            localDateTimeEnd = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        long year= ChronoUnit.YEARS.between(localDateTimeStart,localDateTimeEnd);
        long day=ChronoUnit.DAYS.between(localDateTimeStart,localDateTimeEnd)%356;
        long hour=ChronoUnit.HOURS.between(localDateTimeStart,localDateTimeEnd);
        long minute=ChronoUnit.MINUTES.between(localDateTimeStart,localDateTimeEnd);
        StringBuilder sb=new StringBuilder();
        if(year>0){
            sb.append(year+"年");
        }
        if(day>0){
            sb.append(day+"天");
        }
        if(hour>0){
            sb.append(hour+"时");
        }
        if(minute>0){
            sb.append(minute+"分");
        }
        return sb.toString();
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
