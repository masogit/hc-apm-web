/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain.pure;

import java.util.LinkedList;

/**
 *
 * @author 212579464
 */
public class CustomConfigObject {
    LinkedList<String> AssetListPageFields;
    LinkedList<String> AssetDetailPageFields;

    public LinkedList<String> getAssetListPageFields() {
        return AssetListPageFields;
    }

    public void setAssetListPageFields(LinkedList<String> AssetListPageFields) {
        this.AssetListPageFields = AssetListPageFields;
    }

    public LinkedList<String> getAssetDetailPageFields() {
        return AssetDetailPageFields;
    }

    public void setAssetDetailPageFields(LinkedList<String> AssetDetailPageFields) {
        this.AssetDetailPageFields = AssetDetailPageFields;
    }
    
}
