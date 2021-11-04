package com.ge.apm.domain;

/**
 * Created by 212605082 on 2018/9/25.
 */
public class ZkbContractVO extends ZkbContract {

    //excel行数
    private int lineNum;

    private String supplierName;

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

        return flag;
    }

    @Override
    public int hashCode() {
        String str = "";

        str += (this.getContractNo() != null ? this.getContractNo().toString() : "");
        str += (this.getRegion() != null ? this.getRegion().toString() : "");
        str += (this.getAgentName() != null ? this.getAgentName().toString() : "");
        str += (this.getStartDate() != null ? this.getStartDate().toString() : "");
        str += (this.getEndDate() != null ? this.getEndDate().toString() : "");
        str += (this.getContractType() != null ? this.getContractType().toString() : "");
        str += (this.getSupplierName() != null ? this.getSupplierName().toString() : "");
        str += (this.getContractState() != null ? this.getContractState().toString() : "");


        return str.hashCode();
    }

}
