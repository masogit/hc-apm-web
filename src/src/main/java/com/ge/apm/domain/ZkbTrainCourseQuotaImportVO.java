package com.ge.apm.domain;

import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;


public class ZkbTrainCourseQuotaImportVO {
    private String  id;

    private int lineNum;

    private String tenantName;

    private String courseName;

    private String modality;

    private String contractNo;

    private Date startDate;

    private Date endDate;

    private String uniqueID;

    private Integer quota = 0;

    private Integer totalQuota = 0;

    private String tenantUid;

    private String courseId;

    private String catNo;

    public String getCatNo() {
        return catNo;
    }

    public void setCatNo(String catNo) {
        this.catNo = catNo;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModality() {
        return modality;
    }
    
    public void setModality(String modality) {
        this.modality = modality;
    }
    
    public String getContractNo() {
        return contractNo;
    }
    
    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
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

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }


    public Integer getQuota() {
        return quota;
    }


    public void setQuota(Integer quota) {
        this.quota = quota;
    }


    public Integer getTotalQuota() {
        return totalQuota;
    }


    public void setTotalQuota(Integer totalQuota) {
        this.totalQuota = totalQuota;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public boolean isNull() {
        boolean flag = false;

        if(this.getContractNo() == null){
            flag = true;
        }
        if(this.getCatNo() == null){
            flag = true;
        }
        if(StrUtil.isEmpty(this.getTenantName())){
            flag = true;
        }

        return flag;
    }


    public int hashCode() {
        String str = "";

        str += (this.getContractNo() != null ? this.getContractNo().toString() : "");
        str += (this.getTenantName() != null ? this.getTenantName().toString() : "");
        str += (this.getCourseName() != null ? this.getCourseName().toString() : "");
        str += (this.getQuota() != null ? this.getQuota().toString() : "");
        str += (this.getTotalQuota() != null ? this.getTotalQuota().toString() : "");
        return str.hashCode();
    }

}
