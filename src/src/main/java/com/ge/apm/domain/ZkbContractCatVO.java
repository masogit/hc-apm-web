package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2021/6/2
 * @description :
 */
public class ZkbContractCatVO extends ZkbContractCat{

    private int lineNum;
    private String contractNo;
    private String enabledName;

    public String getEnabledName() {
        return enabledName;
    }

    public void setEnabledName(String enabledName) {
        this.enabledName = enabledName;
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

        str += (this.getSnNo() != null ? this.getSnNo().toString() : "");
        str += (this.getContractNo() != null ? this.getContractNo().toString() : "");
        return str.hashCode();
    }
}
