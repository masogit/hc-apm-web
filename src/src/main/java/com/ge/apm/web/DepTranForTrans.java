package com.ge.apm.web;

import lombok.Getter;
import lombok.Setter;

public class DepTranForTrans {

    @Getter
    @Setter
    Integer userId;
    @Getter @Setter
    String startTime;
    @Getter @Setter
    String endTime;

    @Getter @Setter
    Integer pageNumber;
    @Getter @Setter
    Integer pageSize;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
