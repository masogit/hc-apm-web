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
    private String manufacture;

    // 制造商来自udid
    private Boolean manufactureFromUdid;

    // 医疗器械分类
    private String medicalApparatusClassification;

    // 医疗器械分类来自udid
    private Boolean medicalApparatusClassificationFromUdid;

    // 知识库校验信息
    private String message;
}
