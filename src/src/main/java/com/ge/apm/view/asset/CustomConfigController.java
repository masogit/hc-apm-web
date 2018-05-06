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
import com.ge.apm.domain.pure.CustomConfigField;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private List<CustomConfigField> assetsListPageFields;
    private OrgInfo selectSite;
    private CustomConfigObject customConfigObject;
    private LinkedHashMap<String, String> allFields;
    private Map<String, Boolean> selectedAddFields;
    private CustomConfigField currentEditField;
    private CustomConfig customConfig;
    private UserCommonService userService;

    @PostConstruct
    protected void init() {
        ccService = WebUtil.getBean(CustomConfigService.class);
        userService = WebUtil.getBean(UserCommonService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        selectSite = orgDao.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        allFields = ccService.getAllFields();
        selectedAddFields = new HashMap();
        loadConfigForSite(selectSite.getUid());
    }

    private void loadConfigForSite(String siteUid) {
        customConfig = ccService.getCustomConfigBySite(siteUid);
        if(null == customConfig){
            customConfigObject = ccService.getCurrentCustomConfig(siteUid);
        }else{
            customConfigObject = JSONObject.parseObject(customConfig.getConfig(), CustomConfigObject.class);
        }
        assetsListPageFields = customConfigObject.getAssetListPageFields();
    }
    
    public String getCustomConfigInfomation(){
        if(customConfig == null){
            return "当前院区无配置,保存后创建";
        }
        StringBuilder res = new StringBuilder();
        if(customConfig.getCreatedBy()!=null){
            res.append("创建人:").append(userService.getUserByUid(customConfig.getCreatedBy()).getName());
            res.append(" 创建时间:").append(DateUtil.formatDate(customConfig.getCreatedDate(),"yyyy-MM-dd"));
        }
        if(customConfig.getLastModifiedBy()!=null){
            res.append("; 修改人:").append(userService.getUserByUid(customConfig.getLastModifiedBy()).getName());
            res.append(" 修改时间:").append(DateUtil.formatDate(customConfig.getLastModifiedDate(),"yyyy-MM-dd"));
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
        customConfigObject.setAssetListPageFields(assetsListPageFields);
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
        assetsListPageFields.removeAll(assetsListPageFields.stream().filter(item -> key.equalsIgnoreCase(item.getKey())).collect(Collectors.toList()));
    }

    public void prepareEditListField(String fieldKey) {
        currentEditField = assetsListPageFields.stream().filter(item -> fieldKey.equalsIgnoreCase(item.getKey())).findAny().orElse(null);
    }

    public List<String> getListPageAddFieldList() {
        return allFields.keySet().stream().filter(item -> !assetsListPageFields.stream().anyMatch(selectedItem -> selectedItem.getKey().equals(item))).collect(Collectors.toList());
    }

    public void addFieldsForListPage() {
        assetsListPageFields.addAll(selectedAddFields.entrySet().stream().filter(item -> item.getValue() != null && item.getValue())
                .filter(item -> !assetsListPageFields.stream().anyMatch(selectedItem -> selectedItem.getKey().equals(item.getKey())))
                .map(item -> new CustomConfigField(item.getKey())).collect(Collectors.toList()));
    }

    //getter and setter
    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public List<CustomConfigField> getAssetsListPageFields() {
        return assetsListPageFields;
    }

    public void setAssetsListPageFields(List<CustomConfigField> assetsListPageFields) {
        this.assetsListPageFields = assetsListPageFields;
    }

    public LinkedHashMap<String, String> getAllFields() {
        return allFields;
    }

    public CustomConfigField getCurrentEditField() {
        return currentEditField;
    }

    public void setCurrentEditField(CustomConfigField currentEditField) {
        this.currentEditField = currentEditField;
    }

    public Map<String, Boolean> getSelectedAddFields() {
        return selectedAddFields;
    }

    public void setSelectedAddFields(Map<String, Boolean> selectedAddFields) {
        this.selectedAddFields = selectedAddFields;
    }

}
