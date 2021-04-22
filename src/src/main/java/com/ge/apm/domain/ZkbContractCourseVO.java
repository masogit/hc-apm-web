package com.ge.apm.domain;

import org.apache.poi.ss.formula.functions.T;

import javax.persistence.Column;

/**
 * @author : 503189362
 * @date : 2020/5/11
 * @description :
 */
public class ZkbContractCourseVO extends ZkbContractCourse {

    //excel行数
    private int lineNum;

    private String contractNo;
    private String cid;
    private String cname;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }


    @Override
    public int hashCode() {
        String str = "";

        str += (this.getContractNo() != null ? this.getContractNo().toString() : "");
        str += (this.getCid() != null ? this.getCid().toString() : "");
        str += (this.getCname() != null ? this.getCname() : "");
        str += (this.getMainCourseCount() != null ? this.getMainCourseCount().toString() : "");

        return str.hashCode();
    }

}
