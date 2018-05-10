/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain.pure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 212579464
 */
public class CustomConfigObject {
    Map<String,CustomConfigAssetField> AssetFields;

    List<String> assetsListPageFields;

    public Map<String, CustomConfigAssetField> getAssetFields() {
        return AssetFields;
    }

    public void setAssetFields(Map<String, CustomConfigAssetField> AssetFields) {
        this.AssetFields = AssetFields;
    }

    public List<String> getAssetsListPageFields() {
        return assetsListPageFields;
    }

    public void setAssetsListPageFields(List<String> assetsListPageFields) {
        this.assetsListPageFields = assetsListPageFields;
    }

    
}
