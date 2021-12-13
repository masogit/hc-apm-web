package com.ge.apm.domain;

/**
 * Created by 503202755 on 2021/8/31
 */
public class TenantInfoVO extends TenantInfo {
    /**
     * excel行数
     */
    private int lineNum;

    /**
     * 年分
     */
    private String year;

    /**
     * 月份
     */
    private String month;


    public boolean isNull() {
        boolean flag = false;
        if(this.getId() == null
                &&this.getName() == null
                &&this.getUid() == null
                &&this.getMonth()==null
                &&this.getYear()==null){
            flag = true;
        }
        return flag;
    }

    @Override
    public int hashCode() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer=stringBuffer.append(this.getUid() != null ? this.getUid() : "");
        stringBuffer=stringBuffer.append(this.getName() != null ? this.getName() : "");
        stringBuffer=stringBuffer.append(this.getYear() != null ? this.getYear() : "");
        stringBuffer=stringBuffer.append(this.getMonth() != null ? this.getMonth() : "");
        return stringBuffer.toString().hashCode();
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
