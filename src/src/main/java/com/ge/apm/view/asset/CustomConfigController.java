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
import com.ge.apm.domain.pure.CustomConfigAssetField;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.pure.CustomConfigAssetField;
import com.ge.apm.service.asset.CustomConfigService;
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
    
    @PostConstruct
    protected void init() {
        ccService = WebUtil.getBean(CustomConfigService.class);
        userService = WebUtil.getBean(UserCommonService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        selectSite = orgDao.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        allFields = ccService.getAllFields();
        configRoles = ccService.getConfigRoles().stream().collect(Collectors.toMap(SysRole::getName,SysRole::getRoleDesc));
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
        if (customConfigObject.getAssetFields().containsKey(key)) {
            customConfigObject.getAssetFields().remove(key);
        }
        if (customConfigObject.getAssetsListPageFields().contains(key)) {
            customConfigObject.getAssetsListPageFields().remove(key);
        }
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
    }
    
    public void deleteConfig() {
        ccService.removeConfig(selectSite);
        customConfig = ccService.getCustomConfigBySite(selectSite.getSiteUID());
        WebUtil.addSuccessMessage(WebUtil.getMessage("Deleted"));
    }
    
    public void removeListPageField(String key) {
        if (customConfigObject.getAssetsListPageFields().contains(key)) {
            customConfigObject.getAssetsListPageFields().remove(key);
        }
    }
    
    public void prepareEditAssetField(String fieldKey) {
        currentEditField = customConfigObject.getAssetFields().get(fieldKey);
    }
    
    //将要添加的字段
    public List<String> getAssetFieldAddList() {
        return allFields.keySet().stream().filter(item-> !customConfigObject.getAssetFields().containsKey(item)).collect(Collectors.toList());
    }
    
    public void addAssetFields() {
//        customConfigObject.getAssetsListPageFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
//                && !customConfigObject.getAssetsListPageFields().contains(item.getKey())).map(Entry::getKey).collect(Collectors.toList()));

        customConfigObject.getAssetFields().putAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                && !customConfigObject.getAssetFields().containsKey(item.getKey()))
                .collect(Collectors.toMap(itemField-> itemField.getKey(),itemField->new CustomConfigAssetField(itemField.getKey(),new ArrayList<String>(configRoles.keySet())))));
        selectedAddFields.clear();
    }

    public List<String> getRolesSelectItems(){
        return new ArrayList(configRoles.keySet());
    }
    
    public List<String> getListPageFieldsAddList(){
        return customConfigObject.getAssetFields().keySet().stream().filter(item-> !customConfigObject.getAssetsListPageFields().contains(item)).collect(Collectors.toList());
    }
    
    public void addListPageFields(){
        customConfigObject.getAssetsListPageFields().addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue()
                && !customConfigObject.getAssetsListPageFields().contains(item.getKey())).map(item-> item.getKey()).collect(Collectors.toList()));
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
    
}
