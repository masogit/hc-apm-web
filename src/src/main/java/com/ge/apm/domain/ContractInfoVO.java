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
        boolean flag = false;

        if(this.getName() == null && this.getName() == null && this.getAmount() == null && this.getContractNum() == null && this.getStartDate() == null
                && this.getEndDate() == null && this.getEndMonths() == null && this.getSupplierName() == null && this.getForeignTradeNum() == null && this.getInvoiceNum() == null){
            flag = true;
        }

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
        str += (this.getForeignTradeNum() != null ? this.getForeignTradeNum().toString() : "");
        str += (this.getInvoiceNum() != null ? this.getInvoiceNum().toString() : "");

        return str.hashCode();
    }

}
