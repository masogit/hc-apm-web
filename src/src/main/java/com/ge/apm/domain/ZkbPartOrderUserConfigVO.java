package com.ge.apm.domain;

/**
 * Created by 503202755 on 2020/11/3
 */
public class ZkbPartOrderUserConfigVO extends ZkbPartOrderUserConfig {
    /**
     * excel行数
     */
    private int lineNum;

    @Override
    public int hashCode() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer=stringBuffer.append(this.getName() != null ? this.getName().toString() : "");
        stringBuffer=stringBuffer.append(this.getEmail() != null ? this.getEmail().toString() : "");
        stringBuffer=stringBuffer.append(this.getPhone() != null ? this.getPhone().toString() : "");
        stringBuffer=stringBuffer.append(this.getSso() != null ? this.getSso().toString() : "");
        stringBuffer=stringBuffer.append(this.getRegion() != null ? this.getRegion().toString() : "");
        stringBuffer=stringBuffer.append(this.getConfigType() != null ? this.getConfigType().toString() : "");
        stringBuffer=stringBuffer.append(this.getAssetType() != null ? this.getAssetType().toString() : "");
        stringBuffer=stringBuffer.append(this.getSubConfigType() != null ? this.getSubConfigType().toString() : "");
        stringBuffer=stringBuffer.append(this.getRemark() != null ? this.getRemark().toString() : "");
        stringBuffer=stringBuffer.append(this.getTenantUID() != null ? this.getTenantUID().toString() : "");
        stringBuffer=stringBuffer.append(this.getOrderType() != null ? this.getOrderType().toString() : "");
        stringBuffer=stringBuffer.append(this.getIsVacation() != null ? this.getIsVacation().toString() : "");
        return stringBuffer.toString().hashCode();
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

}
