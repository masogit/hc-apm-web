package com.ge.apm.domain;

/**
 * Created by 503202755 on 2020/8/11
 */
public class AssetInfoVO extends AssetInfo {
    /**
     * excel行数
     */
    private int lineNum;


    public boolean isNull() {
        boolean flag = false;

        if(this.getSystemId() == null && this.getMainDirectory() == null && this.getSecondDirectory() == null ){
            flag = true;
        }

        return flag;
    }

    @Override
    public int hashCode() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer=stringBuffer.append(this.getSystemId() != null ? this.getSystemId().toString() : "");
        stringBuffer=stringBuffer.append(this.getMainDirectory() != null ? this.getMainDirectory().toString() : "");
        stringBuffer=stringBuffer.append(this.getSecondDirectory() != null ? this.getSecondDirectory().toString(): "");
        return stringBuffer.toString().hashCode();
    }

    @Override
    public int getLineNum() {
        return lineNum;
    }

    @Override
    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }
}
