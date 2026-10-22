package com.ge.apm.domain;

import lombok.Data;
import org.primefaces.model.TreeNode;

@Data
public class UdidInfoForm {

    // 注册证号
    private String registryNum;

    // 注册证号来自udid
    private Boolean registryNumFromUdid;

    // 资产名称
    private AutoCompleteVo assetName;

    // 资产名称来自udid
    private Boolean assetNameFromUdid;

    // 设备型号
    private String functionType;

    // 设备型号来自udid
    private Boolean functionTypeFromUdid;

    // 制造商
    private Supplier manufacture;

    // 制造商来自udid
    private Boolean manufactureFromUdid;

    // 医疗器械分类树
    private TreeNode medicalClassTree;

    // 医疗器械分类
    private SysAuthGroup medicalClass;

    // 医疗器械分类来自udid
    private Boolean medicalClassFromUdid;

    // 知识库是否检验通过
    private Boolean checked;

    // 知识库校验信息
    private String message;

    public void fillValue(String registryNum, AutoCompleteVo assetName, String functionType, Supplier manufacture, SysAuthGroup medicalClass) {
        this.registryNum = registryNum;
        this.assetName = assetName;
        this.functionType = functionType;
        this.manufacture = manufacture;
        this.medicalClass = medicalClass;
    }

    public void clearValue() {
        this.registryNum = null;
        this.assetName = null;
        this.functionType = null;
        this.manufacture = null;
        this.medicalClass = null;
    }

    public void clearFlag() {
        this.registryNumFromUdid = null;
        this.assetNameFromUdid = null;
        this.functionTypeFromUdid = null;
        this.manufactureFromUdid = null;
        this.medicalClassFromUdid = null;
    }

    public void clearMessage() {
        this.message = null;
    }

    public String getMedicalClassLabel() {
        if (null == medicalClass) {
            return "";
        } else {
            return medicalClass.getClassificationCode() + "  " + medicalClass.getName().replaceAll("\\d+", "");
        }
    }
}
