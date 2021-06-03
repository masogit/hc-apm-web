package com.ge.apm.domain;

/**
 * Created by 503202755 on 2021/4/28
 */
public class ZkbFePartOrderVO extends ZkbFePartOrder{
    /**
     * excel行数
     */
    private int lineNum;

    /**
     * 是否删除
     */
    private String delete;

    /**
     * sheet 页名称
     */
    private String sheetName;


    public boolean isProbeNull() {
        boolean flag = false;
        if(this.getPartOrderNo() == null
                &&this.getReturnType() == null
                &&this.getPartSerialNumber() == null
                &&this.getPartQualityIssue() == null
                &&this.getWaybillNo()==null
                &&this.getReturnDate()==null
                &&this.getNewCatSn()==null
                &&this.getGeAdminComment()==null
                ){
            flag = true;
        }
        return flag;
    }

    public boolean isSparePartNull()
    {
        boolean flag = false;
        if(this.getReturnType()==null
                &&this.getGeAdminComment()==null
                &&this.getQty()==null
                &&this.getPartOrderNo()==null
                &&this.getAssetNo()==null
                &&this.getOrderNo()==null)
        {
            flag = true;
        }
        return flag;
    }

    @Override
    public int hashCode() {
        StringBuffer stringBuffer = new StringBuffer();
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

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
