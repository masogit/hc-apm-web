package com.ge.apm.view.budget.form;

import com.ge.apm.domain.OrgInfo;

/**
 * @author Yangfan, Liu
 * @date 4/8/2022 17:21
 */
public class BudgetAssetSearchForm {
    // 科室
    private OrgInfo dept;
    // 设备分类
    private Integer assetGroup;
    // 设备二级分类
    private Integer assetGroupSecondary;
    // 设备品牌
    private String assetBrand;
    // 设备型号
    private String assetModel;
    // 设备名称
    private String assetName;

    public OrgInfo getDept() {
        return dept;
    }

    public void setDept(OrgInfo dept) {
        this.dept = dept;
    }

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

    public String getAssetModel() {
        return assetModel;
    }

    public void setAssetModel(String functionType) {
        this.assetModel = functionType == null ? null : functionType.trim();
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName == null ? null : assetName.trim();
    }
}