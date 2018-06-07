/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.CustomConfig;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.pure.CustomConfigAssetField;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.CustomConfigConstant;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.httpclient.util.DateUtil;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.ReorderEvent;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class CustomConfigController {

    private CustomConfigService ccService;
    private OrgInfoRepository orgDao;
    private UserCommonService userService;

    private OrgInfo selectSite;
    private CustomConfigObject customConfigObject;
    private Map<String, Boolean> selectedAddFields;
    private CustomConfigAssetField currentEditField;
    private CustomConfig customConfig;
    private LinkedHashMap<String, String> allFields;
    private Map<String, String> configRoles;
    private List<String> addFieldsList;
    private String addFieldsType;

    private Map<String, String> configInfo = new HashMap();

    @PostConstruct
    protected void init() {
        ccService = WebUtil.getBean(CustomConfigService.class);
        userService = WebUtil.getBean(UserCommonService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        selectSite = orgDao.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        allFields = CustomConfigConstant.getAllFields();
        configRoles = ccService.getConfigRoles().stream().collect(Collectors.toMap(SysRole::getName, SysRole::getRoleDesc));
        selectedAddFields = new HashMap();
        loadConfigForSite(selectSite.getUid());
    }

    private void loadConfigForSite(String siteUid) {
        customConfig = ccService.getCustomConfigBySite(siteUid);
        if (null == customConfig) {
            customConfigObject = ccService.getCurrentCustomConfig(siteUid);
        } else {
            customConfigObject = JSONObject.parseObject(customConfig.getConfig(), CustomConfigObject.class);
        }
        updateConfigInfo();
    }

    public void prepareAddAssetField(String type) {
        addFieldsType = type;
        if (null == addFieldsList) {
            addFieldsList = new ArrayList();
        } else {
            addFieldsList.clear();
        }
        switch (type) {
            case "BasicFields":
                addFieldsList = customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !customConfigObject.getAssetsDetailpageBasicFields().contains(item)).collect(Collectors.toList());
                break;
            case "DetailFields":
                addFieldsList = customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !customConfigObject.getAssetsDetailpageDetailFields().contains(item)).collect(Collectors.toList());
                break;
            case "ListPageFields":
                addFieldsList = customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !customConfigObject.getAssetsListPageFields().contains(item)).collect(Collectors.toList());
                break;
            case "UsingFields":
                addFieldsList = allFields.keySet().stream().filter(item -> !customConfigObject.getAssetFields().containsKey(item)).collect(Collectors.toList());
                break;
            case "RemoveBasicFields":
                addFieldsList.addAll(customConfigObject.getAssetsDetailpageBasicFields());
                break;
            case "RemoveDetailFields":
                addFieldsList.addAll(customConfigObject.getAssetsDetailpageDetailFields());
                break;
            case "EditpageBasicFields":
            case "EditpageCodesFields":
            case "EditpageStatusFields":
            case "EditpageCatalogFields":
            case "EditpageDateFields":
            case "EditpageFinicialFields":
                addFieldsList = customConfigObject.getAssetFields().keySet().stream()
                        .filter(item -> !customConfigObject.getAssetsEditpageBasicFields().contains(item)
                        && !customConfigObject.getAssetsEditpageCodesFields().contains(item)
                        && !customConfigObject.getAssetsEditpageStatusFields().contains(item)
                        && !customConfigObject.getAssetsEditpageCatalogFields().contains(item)
                        && !customConfigObject.getAssetsEditpageDateFields().contains(item)
                        && !customConfigObject.getAssetsEditpageFinicialFields().contains(item)).collect(Collectors.toList());
                break;
            case "RemoveEditpageBasicFields":
                addFieldsList.addAll(customConfigObject.getAssetsEditpageBasicFields());
                break;
            case "RemoveEditpageCodesFields":
                addFieldsList.addAll(customConfigObject.getAssetsEditpageCodesFields());
                break;
            case "RemoveEditpageStatusFields":
                addFieldsList.addAll(customConfigObject.getAssetsEditpageStatusFields());
                break;
            case "RemoveEditpageCatalogFields":
                addFieldsList.addAll(customConfigObject.getAssetsEditpageCatalogFields());
                break;
            case "RemoveEditpageDateFields":
                addFieldsList.addAll(customConfigObject.getAssetsEditpageDateFields());
                break;
            case "RemoveEditpageFinicialFields":
                addFieldsList.addAll(customConfigObject.getAssetsEditpageFinicialFields());
                break;
        }
    }

    public String getAddFieldsUpdateString() {
        switch (addFieldsType) {
            case "BasicFields":
            case "RemoveBasicFields":
                return ":customConfigForm:tabView:BasicFieldsList";
            case "DetailFields":
            case "RemoveDetailFields":
                return ":customConfigForm:tabView:DetailFieldsList";
            case "ListPageFields":
                return ":customConfigForm:tabView:assetListPageTable";
            case "UsingFields":
                return ":customConfigForm:tabView:assetFieldsTable";
            case "EditpageBasicFields":
            case "RemoveEditpageBasicFields":
                return "customConfigForm:tabView:EditpageBasicFields";
            case "EditpageCodesFields":
            case "RemoveEditpageCodesFields":
                return "customConfigForm:tabView:EditpageCodesFields";
            case "EditpageStatusFields":
            case "RemoveEditpageStatusFields":
                return "customConfigForm:tabView:EditpageStatusFields";
            case "EditpageCatalogFields":
            case "RemoveEditpageCatalogFields":
                return "customConfigForm:tabView:EditpageCatalogFields";
            case "EditpageDateFields":
            case "RemoveEditpageDateFields":
                return "customConfigForm:tabView:EditpageDateFields";
            case "EditpageFinicialFields":
            case "RemoveEditpageFinicialFields":
                return "customConfigForm:tabView:EditpageFinicialFields";
            default:
                return "";
        }
    }

    public void addAssetFields() {
        switch (addFieldsType) {
            case "BasicFields":
                customConfigObject.getAssetsDetailpageBasicFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsDetailpageBasicFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "DetailFields":
                customConfigObject.getAssetsDetailpageDetailFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsDetailpageDetailFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "ListPageFields":
                customConfigObject.getAssetsListPageFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsListPageFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "UsingFields":
                customConfigObject.getAssetFields().putAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetFields().containsKey(item.getKey()))
                        .collect(Collectors.toMap(itemField -> itemField.getKey(), itemField -> new CustomConfigAssetField(itemField.getKey(), new ArrayList<String>(configRoles.keySet())))));
                break;
            case "RemoveBasicFields":
                List<String> removeList = selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsDetailpageBasicFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList());
                customConfigObject.getAssetsDetailpageBasicFields().removeAll(removeList);
                break;
            case "RemoveDetailFields":
                customConfigObject.getAssetsDetailpageDetailFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsDetailpageDetailFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "EditpageBasicFields":
                customConfigObject.getAssetsEditpageBasicFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsEditpageBasicFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveEditpageBasicFields":
                customConfigObject.getAssetsEditpageBasicFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsEditpageBasicFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "EditpageCodesFields":
                customConfigObject.getAssetsEditpageCodesFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsEditpageCodesFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveEditpageCodesFields":
                customConfigObject.getAssetsEditpageCodesFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsEditpageCodesFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "EditpageStatusFields":
                customConfigObject.getAssetsEditpageStatusFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsEditpageStatusFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveEditpageStatusFields":
                customConfigObject.getAssetsEditpageStatusFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsEditpageStatusFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "EditpageCatalogFields":
                customConfigObject.getAssetsEditpageCatalogFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsEditpageCatalogFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveEditpageCatalogFields":
                customConfigObject.getAssetsEditpageCatalogFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsEditpageCatalogFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "EditpageDateFields":
                customConfigObject.getAssetsEditpageDateFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsEditpageDateFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveEditpageDateFields":
                customConfigObject.getAssetsEditpageDateFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsEditpageDateFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "EditpageFinicialFields":
                customConfigObject.getAssetsEditpageFinicialFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && !customConfigObject.getAssetsEditpageFinicialFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
            case "RemoveEditpageFinicialFields":
                customConfigObject.getAssetsEditpageFinicialFields().removeAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                        && customConfigObject.getAssetsEditpageFinicialFields().contains(item.getKey())).map(item -> item.getKey()).collect(Collectors.toList()));
                break;
        }
        cancelAddFields();
    }

    public void cancelAddFields() {
        addFieldsType = "";
        addFieldsList.clear();
        selectedAddFields.clear();
    }

    //使用的字段
    public List<CustomConfigAssetField> getAssetFieldsList() {
        return customConfigObject.getAssetFields().entrySet().stream().map(Entry::getValue).collect(Collectors.toList());
    }

    //角色名称
    public String getAssetFieldsRoles(List<String> roles) {
        StringBuilder res = new StringBuilder();
        roles.stream().forEach(item -> res.append(configRoles.get(item)).append(" "));
        return res.toString();
    }

    public void removeAssetField(String key) {
        ccService.removeAssetField(customConfigObject, key);
    }

    public String getCustomConfigInfomation() {
        if (customConfig == null) {
            return "当前院区无配置,保存后创建";
        }
        StringBuilder res = new StringBuilder();
        if (customConfig.getCreatedBy() != null) {
            res.append("创建人:").append(userService.getUserByUid(customConfig.getCreatedBy()).getName());
            res.append(" 创建时间:").append(DateUtil.formatDate(customConfig.getCreatedDate(), "yyyy-MM-dd"));
        }
        if (customConfig.getLastModifiedBy() != null) {
            res.append("; 修改人:").append(userService.getUserByUid(customConfig.getLastModifiedBy()).getName());
            res.append(" 修改时间:").append(DateUtil.formatDate(customConfig.getLastModifiedDate(), "yyyy-MM-dd"));
        }
        return res.toString();
    }

    private void updateConfigInfo() {
        if (null != customConfig) {
            configInfo.put("CreatedBy", userService.getUserByUid(customConfig.getCreatedBy()).getName());
            configInfo.put("CreatedDate",customConfig.getCreatedDate()==null?"": DateUtil.formatDate(customConfig.getCreatedDate(), "yyyy-MM-dd"));
            configInfo.put("LastModifiedBy", userService.getUserByUid(customConfig.getLastModifiedBy()).getName());
            configInfo.put("LastModifiedDate",customConfig.getLastModifiedDate()==null?"": DateUtil.formatDate(customConfig.getLastModifiedDate(), "yyyy-MM-dd"));
        }
    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        if (null != event) {
            if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
                selectSite = (OrgInfo) event.getTreeNode().getData();
            } else {
                selectSite = orgDao.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());
            }
            loadConfigForSite(selectSite.getUid());
        }
    }

    public void onListPageFieldsReorder(ReorderEvent event) {
    }

    public void saveChange() {
        ccService.saveConfig(selectSite, customConfigObject);
        customConfig = ccService.getCustomConfigBySite(selectSite.getSiteUID());
        WebUtil.addSuccessMessage("已保存");
        updateConfigInfo();
    }

    public void deleteConfig() {
        ccService.removeConfig(selectSite);
        loadConfigForSite(selectSite.getUid());
        WebUtil.addSuccessMessage(WebUtil.getMessage("Deleted"));
    }

    public boolean isSystemFields(String key) {
        return CustomConfigConstant.getSysTemFields().contains(key);
    }

    public void removeListPageField(String key) {
        if (customConfigObject.getAssetsListPageFields().contains(key)) {
            customConfigObject.getAssetsListPageFields().remove(key);
        }
    }

    public void prepareEditAssetField(String fieldKey) {
        currentEditField = customConfigObject.getAssetFields().get(fieldKey);
    }

    public List<String> getRolesSelectItems() {
        return new ArrayList(configRoles.keySet());
    }

    //getter and setter
    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public CustomConfigAssetField getCurrentEditField() {
        return currentEditField;
    }

    public void setCurrentEditField(CustomConfigAssetField currentEditField) {
        this.currentEditField = currentEditField;
    }

    public Map<String, Boolean> getSelectedAddFields() {
        return selectedAddFields;
    }

    public void setSelectedAddFields(Map<String, Boolean> selectedAddFields) {
        this.selectedAddFields = selectedAddFields;
    }

    public CustomConfigObject getCustomConfigObject() {
        return customConfigObject;
    }

    public void setCustomConfigObject(CustomConfigObject customConfigObject) {
        this.customConfigObject = customConfigObject;
    }

    public LinkedHashMap<String, String> getAllFields() {
        return allFields;
    }

    public Map<String, String> getConfigRoles() {
        return configRoles;
    }

    public List<String> getAddFieldsList() {
        return addFieldsList;
    }

    public String getAddFieldsType() {
        return addFieldsType;
    }

    public Map<String, String> getConfigInfo() {
        return configInfo;
    }

    public CustomConfig getCustomConfig() {
        return customConfig;
    }
    
}
