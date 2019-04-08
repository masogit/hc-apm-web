package com.ge.apm.domain.pure;

/**
 * Created by 212605082 on 2019/1/10.
 */
public class AssetWorkOrder {

    //设备ID
    private Integer id;
    //设备UID
    private String uid;
    //设备名称
    private String name;
    //设备序列号
    private String serialNum;
    //资产编号
    private String financingNum;
    //设备型号
    private String functionType;
    //设备分类
    private Integer assetGroup;
    //设备分类名称
    private String assetGroupName;
    //所属科室
    private String clinicalDeptUID;
    //所属科室名称
    private String clinicalDeptName;
    //二维码
    private String qrCode;
    //工单数量
    private Integer workOrderCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getFinancingNum() {
        return financingNum;
    }

    public void setFinancingNum(String financingNum) {
        this.financingNum = financingNum;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public String getClinicalDeptUID() {
        return clinicalDeptUID;
    }

    public void setClinicalDeptUID(String clinicalDeptUID) {
        this.clinicalDeptUID = clinicalDeptUID;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Integer getWorkOrderCount() {
        return workOrderCount;
    }

    public void setWorkOrderCount(Integer workOrderCount) {
        this.workOrderCount = workOrderCount;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }
}
