package com.ge.apm.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

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

    // 医疗器械分类
    private AutoCompleteVo medicalClass;

    // 医疗器械分类来自udid
    private Boolean medicalClassFromUdid;

    // 知识库是否检验通过
    private Boolean checked;

    // 知识库校验信息
    private String message;

    public void fillValue(String registryNum, AutoCompleteVo assetName, String functionType, Supplier manufacture, AutoCompleteVo medicalClass) {
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

    /**
     * 提取器械分类代码（首个空白字符前的部分）
     * <p>按空白字符（空格、NBSP等）分割 medicalClass，返回第一段。</p>
     * <pre>
     *   "01 X 射线高压发生器" -> "01"
     *   "01.1\u00A0伽玛射束"   -> "01.1"
     *   "6825"               -> "6825"
     *   null / ""            -> 原值
     * </pre>
     *
     * @return 首个空白字符前的内容；输入为blank时返回原值
     */
    public String getMedicalClassCode() {
        if (Objects.isNull(medicalClass) || StringUtils.isBlank(medicalClass.getValue())) {
            return "";
        }
        String[] parts = medicalClass.getValue().split("[\\s\\u00A0]+", 2);
        return parts[0];
    }
}
