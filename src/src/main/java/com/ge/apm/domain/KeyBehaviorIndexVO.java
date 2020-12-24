package com.ge.apm.domain;

/**
 * Created by 503202755 on 2020/10/22
 */
public class KeyBehaviorIndexVO extends ZkbKeyBehaviorIndex {
    /**
     * excel行数
     */
    private int lineNum;

    /**
     * 是否删除
     */
    private String delete;


    public boolean isNull() {
        boolean flag = false;
        if(this.getTenantUID() == null
                &&this.getKbiDate() == null
                &&this.getPartReturnFullScore() == null
                &&this.getPartReturnScore()==null
//                &&this.getIntactPartReturnRateFullScore()==null
//                &&this.getIntactPartReturnRateScore()==null
                &&this.getPasteQuickResponseRateFullScore()==null
                &&this.getPasteQuickResponseRateScore()==null
                &&this.getApmBillingRateFullScore()==null
                &&this.getApmBillingRateScore()==null
                &&this.getSatisfactoryDegreeFullScore()==null
                &&this.getSatisfactoryDegreeScore()==null
                &&this.getComplianceFullScore()==null
                &&this.getComplianceScore()==null
                &&this.getEngineerFullScore()==null
                &&this.getEngineerScore()==null){
            flag = true;
        }
        return flag;
    }

    @Override
    public int hashCode() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer=stringBuffer.append(this.getTenantUID() != null ? this.getTenantUID().toString() : "");
        stringBuffer=stringBuffer.append(this.getKbiDate() != null ? this.getKbiDate().toString() : "");
        stringBuffer=stringBuffer.append(this.getPartReturnFullScore() != null ? this.getPartReturnFullScore().toString() : "");
        stringBuffer=stringBuffer.append(this.getPartReturnScore() != null ? this.getPartReturnScore().toString() : "");
//        stringBuffer=stringBuffer.append(this.getIntactPartReturnRateFullScore() != null ? this.getIntactPartReturnRateFullScore().toString() : "");
//        stringBuffer=stringBuffer.append(this.getIntactPartReturnRateScore() != null ? this.getIntactPartReturnRateScore().toString() : "");
        stringBuffer=stringBuffer.append(this.getPasteQuickResponseRateFullScore() != null ? this.getPasteQuickResponseRateFullScore().toString() : "");
        stringBuffer=stringBuffer.append(this.getPasteQuickResponseRateScore() != null ? this.getPasteQuickResponseRateScore().toString() : "");
        stringBuffer=stringBuffer.append(this.getApmBillingRateFullScore() != null ? this.getApmBillingRateFullScore().toString() : "");
        stringBuffer=stringBuffer.append(this.getApmBillingRateScore() != null ? this.getApmBillingRateScore().toString() : "");
        stringBuffer=stringBuffer.append(this.getSatisfactoryDegreeFullScore() != null ? this.getSatisfactoryDegreeFullScore().toString() : "");
        stringBuffer=stringBuffer.append(this.getSatisfactoryDegreeScore() != null ? this.getSatisfactoryDegreeScore().toString() : "");
        stringBuffer=stringBuffer.append(this.getComplianceFullScore() != null ? this.getComplianceFullScore().toString() : "");
        stringBuffer=stringBuffer.append(this.getComplianceScore() != null ? this.getComplianceScore().toString() : "");
        stringBuffer=stringBuffer.append(this.getEngineerFullScore() != null ? this.getEngineerFullScore().toString() : "");
        return stringBuffer.toString().hashCode();
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }
}
