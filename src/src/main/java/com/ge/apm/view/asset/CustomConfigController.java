/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
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
    private DualListModel<String> assetsListPageFields;
    private List<String> sourceListFields;
    private LinkedList<String> targetListFields;
    private OrgInfo selectSite;
    private CustomConfigObject customConfigObject;
    
    @PostConstruct
    protected void init() {
        ccService = WebUtil.getBean(CustomConfigService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        selectSite = orgDao.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        loadConfigForSite(selectSite.getUid());
    }
    
    private void loadConfigForSite(String siteUid){
        customConfigObject = ccService.getCurrentCustomConfig(siteUid);
        targetListFields = customConfigObject.getAssetListPageFields();
        sourceListFields = ccService.getAllFields().keySet().stream().filter(item-> !targetListFields.contains(item)).collect(Collectors.toList());
        assetsListPageFields = new DualListModel<>(sourceListFields, targetListFields);
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
    
    public void onFieldsTransfer(TransferEvent event) {

        for(Object item : event.getItems()) {
            if(event.isAdd()){
                targetListFields.add((String)item);
            }else{
                targetListFields.remove((String)item);
            }
        }
    }
    
    public void onFieldsReorder(ReorderEvent event){
        
    }
    
    public void saveChange(){
        customConfigObject.setAssetListPageFields(targetListFields);
        ccService.saveConfig(selectSite,customConfigObject);
        WebUtil.addSuccessMessage("已保存");
    }
    
    public void deleteConfig(){
        ccService.removeConfig(selectSite);
        WebUtil.addSuccessMessage(WebUtil.getMessage("Deleted"));
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public DualListModel<String> getAssetsListPageFields() {
        return assetsListPageFields;
    }

    public void setAssetsListPageFields(DualListModel<String> assetsListPageFields) {
        this.assetsListPageFields = assetsListPageFields;
    }
    
}
