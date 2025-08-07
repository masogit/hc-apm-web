package com.ge.apm.domain;

import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;


public class ZkbTrainCourseQuotaImportVO extends ZkbTrainCourseQuota {

    private int lineNum;

    private String tenantName;

    private String courseName;

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
        if(this.getCourseName() == null){
            flag = true;
        }
        if(StrUtil.isEmpty(this.getTenantName())){
            flag = true;
        }

        return flag;
    }

    @Override
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
