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
    List<String> AssetListPageFields;
    List<String> AssetDetailPageFields;

    public List<String> getAssetListPageFields() {
        return AssetListPageFields;
    }

    public void setAssetListPageFields(List<String> AssetListPageFields) {
        this.AssetListPageFields = AssetListPageFields;
    }

    public List<String> getAssetDetailPageFields() {
        return AssetDetailPageFields;
    }

    public void setAssetDetailPageFields(List<String> AssetDetailPageFields) {
        this.AssetDetailPageFields = AssetDetailPageFields;
    }
    
}
