package com.ge.apm.domain;

import lombok.Data;

@Data
public class UdidInfoForm {

    // 注册证号
    private String registrationNo;

    // 注册证号来自udid
    private Boolean registrationNoFromUdid;

    // 资产名称
    private String name;

    // 资产名称来自udid
    private Boolean nameFromUdid;

    // 设备型号
    private String functionType;

    // 设备型号来自udid
    private Boolean functionTypeFromUdid;

    // 制造商
    private Supplier manufacture;

    // 制造商来自udid
    private Boolean manufactureFromUdid;

    // 医疗器械分类
    private String medicalApparatusClassification;

    // 医疗器械分类来自udid
    private Boolean medicalApparatusClassificationFromUdid;

    // 知识库校验信息
    private String message;

    public void fillValue(String registrationNo, String name, String functionType, Supplier manufacture, String medicalApparatusClassification) {
        this.registrationNo = registrationNo;
        this.name = name;
        this.functionType = functionType;
        this.manufacture = manufacture;
        this.medicalApparatusClassification = medicalApparatusClassification;
    }

    public void clearValue() {
        this.name = null;
        this.functionType = null;
        this.manufacture = null;
        this.medicalApparatusClassification = null;
    }

    public void clearFlag() {
        this.registrationNoFromUdid = null;
        this.nameFromUdid = null;
        this.functionTypeFromUdid = null;
        this.manufactureFromUdid = null;
        this.medicalApparatusClassificationFromUdid = null;
    }

    public void clearMessage() {
        this.message = null;
    }
}
