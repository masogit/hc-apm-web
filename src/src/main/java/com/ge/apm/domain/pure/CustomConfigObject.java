/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain.pure;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.service.common.CustomConfigConstant;
import com.ge.apm.view.asset.AssetInfoListController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
public class CustomConfigObject {
    private static final Logger logger = LoggerFactory.getLogger(CustomConfigObject.class);

    Map<String, CustomConfigAssetField> assetFields;

    List<String> assetsListPageFields;
    List<String> assetsDetailpageBasicFields;
    List<String> assetsDetailpageDetailFields;

    List<String> assetsEditpageBasicFields;
    List<String> assetsEditpageCodesFields;
    List<String> assetsEditpageStatusFields;
    List<String> assetsEditpageCatalogFields;
    List<String> assetsEditpageDateFields;
    List<String> assetsEditpageFinicialFields;

    List<String> assetsMobilepageHeaderFields;
    List<String> assetsMobilepageBasicFields;
    List<String> assetsMobilepageDetailFields;
    List<String> assetsWebMaintainDetailFields;
    
    JSONObject mobilePage;


    JSONObject mobileMaintainPage;

    public Map<String, CustomConfigAssetField> getAssetFields() {

        if (null == assetFields) {
            assetFields = CustomConfigConstant.getAllFields().entrySet().stream().filter(item -> !CustomConfigConstant.getExtFields().contains(item.getKey()))
                    .collect(Collectors.toMap(
                            item -> item.getKey(),
                            item -> new CustomConfigAssetField(item.getKey(),CustomConfigConstant.getConfigRolesName(), CustomConfigConstant.getConfigEditRolesName()),
                            (assetField1, assetField2) -> {
                                logger.error("duplicate key found!, {}", assetField1.getKey());
                                return assetField1;
                            }
                            )
                    );
            CustomConfigConstant.getSystemFields().forEach(item -> {
                assetFields.get(item).setRequired(true);
                assetFields.get(item).setArrivalRequired(true);
            });
            CustomConfigConstant.getInstallRequiredFields().stream()
                    .filter(item -> assetFields.containsKey(item))
                    .forEach(item -> {
                        assetFields.get(item).setArrivalRequired(true);
                    });
        }
        return assetFields;
    }

    public void setAssetFields(Map<String, CustomConfigAssetField> assetFields) {
        this.assetFields = assetFields;
    }

    public List<String> getAssetsListPageFields() {
        if (assetsListPageFields == null) {
            assetsListPageFields = CustomConfigConstant.getAssetsListPageFields();
        }
        return assetsListPageFields;
    }

    public void setAssetsListPageFields(List<String> assetsListPageFields) {
        this.assetsListPageFields = assetsListPageFields;
    }

    public List<String> getAssetsDetailpageBasicFields() {
        if (assetsDetailpageBasicFields == null) {
            assetsDetailpageBasicFields = CustomConfigConstant.getAssetsDetailpageBasicFields();
        }
        return assetsDetailpageBasicFields;
    }

    public void setAssetsDetailpageBasicFields(List<String> assetsDetailpageBasicFields) {
        this.assetsDetailpageBasicFields = assetsDetailpageBasicFields;
    }

    public List<String> getAssetsDetailpageDetailFields() {
        if (assetsDetailpageDetailFields == null) {
            assetsDetailpageDetailFields = CustomConfigConstant.getAssetsDetailpageDetailFields();
        }
        return assetsDetailpageDetailFields;
    }

    public void setAssetsDetailpageDetailFields(List<String> assetsDetailpageDetailFields) {
        this.assetsDetailpageDetailFields = assetsDetailpageDetailFields;
    }

    public List<String> getAssetsEditpageBasicFields() {
        if (assetsEditpageBasicFields == null) {
            assetsEditpageBasicFields = CustomConfigConstant.getAssetsEditpageBasicFields();
        }
        return assetsEditpageBasicFields;
    }

    public void setAssetsEditpageBasicFields(List<String> assetsEditpageBasicFields) {
        this.assetsEditpageBasicFields = assetsEditpageBasicFields;
    }

    public List<String> getAssetsEditpageCodesFields() {
        if (assetsEditpageCodesFields == null) {
            assetsEditpageCodesFields = CustomConfigConstant.getAssetsEditpageCodesFields();
        }
        return assetsEditpageCodesFields;
    }

    public void setAssetsEditpageCodesFields(List<String> assetsEditpageCodesFields) {
        this.assetsEditpageCodesFields = assetsEditpageCodesFields;
    }

    public List<String> getAssetsEditpageStatusFields() {
        if (assetsEditpageStatusFields == null) {
            assetsEditpageStatusFields = CustomConfigConstant.getAssetsEditpageStatusFields();
        }
        return assetsEditpageStatusFields;
    }

    public void setAssetsEditpageStatusFields(List<String> assetsEditpageStatusFields) {
        this.assetsEditpageStatusFields = assetsEditpageStatusFields;
    }

    public List<String> getAssetsEditpageCatalogFields() {
        if (assetsEditpageCatalogFields == null) {
            assetsEditpageCatalogFields = CustomConfigConstant.getAssetsEditpageCatalogFields();
        }
        return assetsEditpageCatalogFields;
    }

    public void setAssetsEditpageCatalogFields(List<String> assetsEditpageCatalogFields) {
        this.assetsEditpageCatalogFields = assetsEditpageCatalogFields;
    }

    public List<String> getAssetsEditpageFinicialFields() {
        if (assetsEditpageFinicialFields == null) {
            assetsEditpageFinicialFields = CustomConfigConstant.getAssetsEditpageFinicialFields();
        }
        return assetsEditpageFinicialFields;
    }

    public void setAssetsEditpageFinicialFields(List<String> assetsEditpageFinicialFields) {
        this.assetsEditpageFinicialFields = assetsEditpageFinicialFields;
    }

    public List<String> getAssetsEditpageDateFields() {
        if (assetsEditpageDateFields == null) {
            assetsEditpageDateFields = CustomConfigConstant.getAssetsEditpageDateFields();
        }
        return assetsEditpageDateFields;
    }

    public void setAssetsEditpageDateFields(List<String> assetsEditpageDateFields) {
        this.assetsEditpageDateFields = assetsEditpageDateFields;
    }

    public List<String> getAssetsMobilepageBasicFields() {
        if (assetsMobilepageBasicFields == null) {
            assetsMobilepageBasicFields = CustomConfigConstant.getAssetsMobilepageBasicFields();
        }
        return assetsMobilepageBasicFields;
    }

    public void setAssetsMobilepageBasicFields(List<String> assetsMobilepageBasicFields) {
        this.assetsMobilepageBasicFields = assetsMobilepageBasicFields;
    }

    public List<String> getAssetsMobilepageDetailFields() {
        if (assetsMobilepageDetailFields == null) {
            assetsMobilepageDetailFields = CustomConfigConstant.getAssetsMobilepageDetailFields();
        }
        return assetsMobilepageDetailFields;
    }

    public List<String> getAssetsWebMaintainDetailFields() {
        return assetsWebMaintainDetailFields;
    }

    public void setAssetsWebMaintainDetailFields(List<String> assetsWebMaintainDetailFields) {
        this.assetsWebMaintainDetailFields = assetsWebMaintainDetailFields;
    }

    public void setAssetsMobilepageDetailFields(List<String> assetsMobilepageDetailFields) {
        this.assetsMobilepageDetailFields = assetsMobilepageDetailFields;
    }

    public List<String> getAssetsMobilepageHeaderFields() {
        if (assetsMobilepageHeaderFields == null) {
            assetsMobilepageHeaderFields = CustomConfigConstant.getAssetsMobilepageHeaderFields();
        }
        return assetsMobilepageHeaderFields;
    }

    public void setAssetsMobilepageHeaderFields(List<String> assetsMobilepageHeaderFields) {
        this.assetsMobilepageHeaderFields = assetsMobilepageHeaderFields;
    }

    public JSONObject getMobilePage() {
        if (mobilePage == null) {
            mobilePage = JSONObject.parseObject(CustomConfigConstant.DEFAULT_MOBILE_PAGE_JSON);
        }
        return mobilePage;
    }

    public void setMobilePage(JSONObject mobilePage) {
        this.mobilePage = mobilePage;
    }

    public JSONObject getMobileMaintainPage() {
        if (mobileMaintainPage == null) {
            mobileMaintainPage = JSONObject.parseObject(CustomConfigConstant.DEFAULT_MOBILE_PAGE_JSON);
        }
        return mobileMaintainPage;
    }

    public void setMobileMaintainPage(JSONObject mobileMaintainPage) {
        this.mobileMaintainPage = mobileMaintainPage;
    }


}
