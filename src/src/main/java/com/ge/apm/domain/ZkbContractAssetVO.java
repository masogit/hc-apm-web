package com.ge.apm.domain;

/**
 * @author : 503189362
 * @date : 2020/5/20
 * @description :
 */
public class ZkbContractAssetVO extends ZkbContractAsset {

    /**
     * excel行数
     */
    private int lineNum;

    /**
     * 二维码
     */
    private String qrCode;

    private String contractNo;

    /**
     * 数据标记 标记Y则为删除，为空则为新增
     * N是补充的，如果新增，但是数据库已存在合同设备信息，那么则不进行数据操作
     */
    private String flag;

    private String contractId;
    private String assetId;

    private String zkbContractAssetId;
    private String repairForm;

    public String getRepairForm() {
        return repairForm;
    }

    public void setRepairForm(String repairForm) {
        this.repairForm = repairForm;
    }

    @Override
    public String getContractId() {
        return contractId;
    }

    @Override
    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    @Override
    public String getAssetId() {
        return assetId;
    }

    @Override
    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getZkbContractAssetId() {
        return zkbContractAssetId;
    }

    public void setZkbContractAssetId(String zkbContractAssetId) {
        this.zkbContractAssetId = zkbContractAssetId;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public boolean isNull() {
        boolean flag = false;

        if(this.getContractNo() == null && this.getQrCode() == null){
            flag = true;
        }

        return flag;
    }

    @Override
    public int hashCode() {
        String str = "";

        str += (this.getContractNo() != null ? this.getContractNo().toString() : "");
        str += (this.getQrCode() != null ? this.getQrCode().toString() : "");

        return str.hashCode();
    }

}
