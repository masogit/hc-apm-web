package com.ge.apm.service.nurseSite.export;

import com.ge.apm.domain.NurseSiteMaintainData;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class NurseSiteMaintainExport implements Serializable {

    public NurseSiteMaintainExport() {
    }


    private Date scheduleDate;

    private Date checkDate;

    private Date updateDate;

    private String principal;

    private String checker;

    private String comment;

    private String signatureId;

    private String status;
    private List<NurseSiteMaintainData> checkData;

    private String configId;
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getChecker() {
        return checker;
    }

    public void setChecker(String checker) {
        this.checker = checker;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(String signatureId) {
        this.signatureId = signatureId;
    }

    public List<NurseSiteMaintainData> getCheckData() {
        return checkData;
    }

    public void setCheckData(List<NurseSiteMaintainData> checkData) {
        this.checkData = checkData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    @Override
    public String toString() {
        return "NurseSiteMaintainExport{" +
                "scheduleDate=" + scheduleDate +
                ", checkDate=" + checkDate +
                ", principal='" + principal + '\'' +
                ", checker='" + checker + '\'' +
                ", comment='" + comment + '\'' +
                ", signatureId='" + signatureId + '\'' +
                ", status=" + status +
                ", checkData=" + checkData +
                '}';
    }
    public int CountCheckData(){
        return CollectionUtils.isEmpty(this.checkData)?0:this.checkData.size();
    }
}
