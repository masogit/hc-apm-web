package com.ge.apm.view.budget;

/**
 * @author Yangfan, Liu
 * @date 4/8/2022 17:21
 */
public class BudgetAssetSearchForm {
    // 设备分类
    private Integer assetGroup;
    // 设备二级分类
    private Integer assetGroupSecondary;
    // 设备品牌
    private String assetBrand;
    // 设备型号
    private String functionType;
    // 设备名称
    private String assetName;

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public Integer getAssetGroupSecondary() {
        return assetGroupSecondary;
    }

    public void setAssetGroupSecondary(Integer assetGroupSecondary) {
        this.assetGroupSecondary = assetGroupSecondary;
    }

    public String getAssetBrand() {
        return assetBrand;
    }

    public void setAssetBrand(String assetBrand) {
        this.assetBrand = assetBrand == null ? null : assetBrand.trim();
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType == null ? null : functionType.trim();
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName == null ? null : assetName.trim();
    }
}