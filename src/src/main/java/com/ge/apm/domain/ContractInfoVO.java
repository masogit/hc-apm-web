package com.ge.apm.domain;

/**
 * Created by 212605082 on 2018/9/25.
 */
public class ContractInfoVO extends ContractInfo {

    private String supplierName;

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public boolean isNull() {
        boolean flag = true;

        flag = this.getName() == null;
        flag = this.getAmount() == null;
        flag = this.getContractNum() == null;
        flag = this.getStartDate() == null;
        flag = this.getEndDate() == null;
        flag = this.getEndMonths() == null;
        flag = this.getSupplierName() == null;

        return flag;
    }

    @Override
    public int hashCode() {
        String str = "";

        str += (this.getName() != null ? this.getName().toString() : "");
        str += (this.getAmount() != null ? this.getAmount().toString() : "");
        str += (this.getContractNum() != null ? this.getContractNum().toString() : "");
        str += (this.getStartDate() != null ? this.getStartDate().toString() : "");
        str += (this.getEndDate() != null ? this.getEndDate().toString() : "");
        str += (this.getEndMonths() != null ? this.getEndMonths().toString() : "");
        str += (this.getSupplierName() != null ? this.getSupplierName().toString() : "");

        return str.hashCode();
    }

}
