package com.ge.apm.view.budget;

/**
 * @author Yangfan, Liu
 * @date 4/8/2022 17:21
 */
public class BudgetAssetSearchForm {
    // 设备类型
    private String assetType;
    // 设备品牌
    private String assetBrand;
    // 设备型号
    private String functionType;
    // 设备二级分类
    private Integer asset2ndClassify;
    // 设备名称
    private String assetName;

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType == null ? null : assetType.trim();
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

    public Integer getAsset2ndClassify() {
        return asset2ndClassify;
    }

    public void setAsset2ndClassify(Integer asset2ndClassify) {
        this.asset2ndClassify = asset2ndClassify;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName == null ? null : assetName.trim();
    }
}