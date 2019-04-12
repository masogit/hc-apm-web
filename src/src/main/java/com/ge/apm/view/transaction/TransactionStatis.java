package com.ge.apm.view.transaction;

import org.apache.poi.ss.formula.functions.T;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SelectableDataModel;

import javax.faces.model.DataModel;
import java.io.Serializable;
import java.util.Date;

public class TransactionStatis  {


    protected LazyDataModel lazyModel;
    String clinicalDeptUID;
    String clinicalDepName;
    Integer startYear;
    Integer startMonth;
    Integer endYear;
    Integer endMonth;
    Integer authorId;
    String authorName;
//    String date;
    Double hours;

    public String getClinicalDepName() {
        return clinicalDepName;
    }

    public Integer getEndYear() {
        return endYear;
    }

    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    public Integer getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(Integer endMonth) {
        this.endMonth = endMonth;
    }

    public void setClinicalDepName(String clinicalDepName) {
        this.clinicalDepName = clinicalDepName;
    }

    public TransactionStatis() {
        super();
    }

    public TransactionStatis(String clinicalDeptUID,Integer authorId,String authorName,Integer year, Integer month,Double hours) {
        super();
        this.clinicalDeptUID=clinicalDeptUID;
        this.authorId=authorId;
        this.authorName=authorName;
//        this.date=date;
        this.hours=hours;
        this.startYear=year;
        this.startMonth=month;
    }


    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }


    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public Integer getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }
    public String getClinicalDeptUID() {
        return clinicalDeptUID;
    }

    public void setClinicalDeptUID(String clinicalDeptUID) {
        this.clinicalDeptUID = clinicalDeptUID;
    }

}
