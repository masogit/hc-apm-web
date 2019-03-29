package com.ge.apm.web;

import com.ge.apm.domain.V2_BlobObject;

import java.util.List;

/**
 * Created by lsg on 11/12/2017.
 */
public class TransactionForm {

    String startTime;
    String endTime;
    String authorUID;
    Integer category;

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

    public String getAuthorUID() {
        return authorUID;
    }

    public void setAuthorUID(String authorUID) {
        this.authorUID = authorUID;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }
}
