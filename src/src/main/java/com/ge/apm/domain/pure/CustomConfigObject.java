/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain.pure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 212579464
 */
public class CustomConfigObject {
    Map<String,CustomConfigAssetField> assetFields;

    List<String> assetsListPageFields = Collections.EMPTY_LIST;
    List<String> assetsDetailpageBasicFields = Collections.EMPTY_LIST;
    List<String> assetsDetailpageDetailFields = Collections.EMPTY_LIST;
    
    List<String> assetsEditpageBasicFields = Collections.EMPTY_LIST;
    List<String> assetsEditpageCodesFields = Collections.EMPTY_LIST;
    List<String> assetsEditpageStatusFields = Collections.EMPTY_LIST;
    List<String> assetsEditpageCatalogFields = Collections.EMPTY_LIST;
    List<String> assetsEditpageDateFields = Collections.EMPTY_LIST;
    List<String> assetsEditpageFinicialFields = Collections.EMPTY_LIST;
    

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

    public List<String> getAssetsEditpageBasicFields() {
        return assetsEditpageBasicFields;
    }

    public void setAssetsEditpageBasicFields(List<String> assetsEditpageBasicFields) {
        this.assetsEditpageBasicFields = assetsEditpageBasicFields;
    }

    public List<String> getAssetsEditpageCodesFields() {
        return assetsEditpageCodesFields;
    }

    public void setAssetsEditpageCodesFields(List<String> assetsEditpageCodesFields) {
        this.assetsEditpageCodesFields = assetsEditpageCodesFields;
    }

    public List<String> getAssetsEditpageStatusFields() {
        return assetsEditpageStatusFields;
    }

    public void setAssetsEditpageStatusFields(List<String> assetsEditpageStatusFields) {
        this.assetsEditpageStatusFields = assetsEditpageStatusFields;
    }

    public List<String> getAssetsEditpageCatalogFields() {
        return assetsEditpageCatalogFields;
    }

    public void setAssetsEditpageCatalogFields(List<String> assetsEditpageCatalogFields) {
        this.assetsEditpageCatalogFields = assetsEditpageCatalogFields;
    }

    public List<String> getAssetsEditpageFinicialFields() {
        return assetsEditpageFinicialFields;
    }

    public void setAssetsEditpageFinicialFields(List<String> assetsEditpageFinicialFields) {
        this.assetsEditpageFinicialFields = assetsEditpageFinicialFields;
    }

    public List<String> getAssetsEditpageDateFields() {
        return assetsEditpageDateFields;
    }

    public void setAssetsEditpageDateFields(List<String> assetsEditpageDateFields) {
        this.assetsEditpageDateFields = assetsEditpageDateFields;
    }
    
}
