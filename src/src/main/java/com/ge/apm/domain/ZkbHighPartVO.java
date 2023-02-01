package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2022/12/20
 * @description :
 */

public class ZkbHighPartVO extends ZkbHighPartInfo{

    private int lineNum;

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }


    @Override
    public boolean isNull() {
        boolean flag = false;

        if(this.getPartNo() == null){
            flag = true;
        }

        return flag;
    }

    @Override
    public int hashCode() {
        String str = "";
        str += (this.getPartNo() != null ? this.getPartNo().toString() : "");
        str += (this.getPartType() != null ? this.getPartType().toString() : "");
        return str.hashCode();
    }
}
