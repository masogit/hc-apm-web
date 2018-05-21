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
    Map<String,CustomConfigAssetField> assetFields;

    List<String> assetsListPageFields;
    List<String> assetsDetailpageBasicFields;
    List<String> assetsDetailpageDetailFields;

    public Map<String, CustomConfigAssetField> getAssetFields() {
        return assetFields;
    }

    public void setAssetFields(Map<String, CustomConfigAssetField> assetFields) {
        this.assetFields = assetFields;
    }

    public List<String> getAssetsListPageFields() {
        return assetsListPageFields;
    }

    public void setAssetsListPageFields(List<String> assetsListPageFields) {
        this.assetsListPageFields = assetsListPageFields;
    }

    public List<String> getAssetsDetailpageBasicFields() {
        return assetsDetailpageBasicFields;
    }

    public void setAssetsDetailpageBasicFields(List<String> assetsDetailpageBasicFields) {
        this.assetsDetailpageBasicFields = assetsDetailpageBasicFields;
    }

    public List<String> getAssetsDetailpageDetailFields() {
        return assetsDetailpageDetailFields;
    }

    public void setAssetsDetailpageDetailFields(List<String> assetsDetailpageDetailFields) {
        this.assetsDetailpageDetailFields = assetsDetailpageDetailFields;
    }
    
}
