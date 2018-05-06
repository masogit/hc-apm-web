/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain.pure;

import java.util.List;

/**
 *
 * @author 212579464
 */
public class CustomConfigObject {
    List<CustomConfigField> AssetListPageFields;
    List<CustomConfigField> AssetDetailPageFields;

    public List<CustomConfigField> getAssetListPageFields() {
        return AssetListPageFields;
    }

    public void setAssetListPageFields(List<CustomConfigField> AssetListPageFields) {
        this.AssetListPageFields = AssetListPageFields;
    }

    public List<CustomConfigField> getAssetDetailPageFields() {
        return AssetDetailPageFields;
    }

    public void setAssetDetailPageFields(List<CustomConfigField> AssetDetailPageFields) {
        this.AssetDetailPageFields = AssetDetailPageFields;
    }
    
}
