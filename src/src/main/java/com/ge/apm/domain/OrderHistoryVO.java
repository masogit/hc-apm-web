package com.ge.apm.domain;

/**
 * Created by 503202755 on 2020/9/1
 */
public class OrderHistoryVO extends ZkbOrderHistory {
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
        if(this.getDataType() == null
                &&this.getRegion() == null
                &&this.getAgent() == null
                &&this.getContractNo()==null
                &&this.getContractType()==null
                &&this.getContractBeginningTime()==null
                &&this.getContractEndingTime()==null
                &&this.getQuantityIb()==null
                &&this.getEventDate()==null
                &&this.getSystemNo()==null
                &&this.getFaultDescription()==null
                &&this.getHospitalName()==null
                &&this.getMachineType()==null
                &&this.getSparePartsNo()==null
                &&this.getSparePartsPrice()==null
                &&this.getDescription()==null
                &&this.getOrderNo()==null
                &&this.getRSESup()==null
                &&this.getQuantity()==null
                &&this.getSr()==null
                &&this.getAct()==null
                &&this.getSparePartsReturn()==null
                &&this.getComment()==null
                &&this.getInsuranceDescription()==null
                &&this.getCallNo()==null
                &&this.getRowner()==null
                &&this.getStatus()==null){
            flag = true;
        }
        return flag;
    }

    @Override
    public int hashCode() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer=stringBuffer.append(this.getDataType() != null ? this.getDataType().toString() : "");
        stringBuffer=stringBuffer.append(this.getRegion() != null ? this.getRegion().toString() : "");
        stringBuffer=stringBuffer.append(this.getAgent() != null ? this.getAgent().toString() : "");
        stringBuffer=stringBuffer.append(this.getContractNo() != null ? this.getContractNo().toString() : "");
        stringBuffer=stringBuffer.append(this.getContractType() != null ? this.getContractType().toString() : "");
        stringBuffer=stringBuffer.append(this.getContractBeginningTime() != null ? this.getContractBeginningTime().toString() : "");
        stringBuffer=stringBuffer.append(this.getContractEndingTime() != null ? this.getContractEndingTime().toString() : "");
        stringBuffer=stringBuffer.append(this.getQuantityIb() != null ? this.getQuantityIb().toString() : "");
        stringBuffer=stringBuffer.append(this.getEventDate() != null ? this.getEventDate().toString() : "");
        stringBuffer=stringBuffer.append(this.getSystemNo() != null ? this.getSystemNo().toString() : "");
        stringBuffer=stringBuffer.append(this.getFaultDescription() != null ? this.getFaultDescription().toString() : "");
        stringBuffer=stringBuffer.append(this.getHospitalName() != null ? this.getHospitalName().toString() : "");
        stringBuffer=stringBuffer.append(this.getMachineType() != null ? this.getMachineType().toString() : "");
        stringBuffer=stringBuffer.append(this.getSparePartsNo() != null ? this.getSparePartsNo().toString() : "");
        stringBuffer=stringBuffer.append(this.getSparePartsPrice() != null ? this.getSparePartsPrice().toString() : "");
        stringBuffer=stringBuffer.append(this.getDescription() != null ? this.getDescription().toString() : "");
        stringBuffer=stringBuffer.append(this.getOrderNo() != null ? this.getOrderNo().toString() : "");
        stringBuffer=stringBuffer.append(this.getRSESup() != null ? this.getRSESup().toString() : "");
        stringBuffer=stringBuffer.append(this.getQuantity() != null ? this.getQuantity().toString() : "");
        stringBuffer=stringBuffer.append(this.getSr() != null ? this.getSr().toString() : "");
        stringBuffer=stringBuffer.append(this.getAct() != null ? this.getAct().toString() : "");
        stringBuffer=stringBuffer.append(this.getSparePartsReturn() != null ? this.getSparePartsReturn().toString() : "");
        stringBuffer=stringBuffer.append(this.getComment() != null ? this.getComment().toString() : "");
        stringBuffer=stringBuffer.append(this.getInsuranceDescription() != null ? this.getInsuranceDescription().toString() : "");
        stringBuffer=stringBuffer.append(this.getCallNo() != null ? this.getCallNo().toString() : "");
        stringBuffer=stringBuffer.append(this.getRowner() != null ? this.getRowner().toString() : "");
        stringBuffer=stringBuffer.append(this.getStatus() != null ? this.getStatus().toString() : "");
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
