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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javaslang.Tuple2;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.NodeSelectEvent;
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
    private List<String> targetListFields;
    private OrgInfo selectSite;
    private Map<String, String> allFields;
    
    @PostConstruct
    protected void init() {
        ccService = WebUtil.getBean(CustomConfigService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        selectSite = orgDao.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        allFields = ccService.getAllFields();
        CustomConfigObject customConfig = ccService.getCustomConfigBySite(selectSite.getSiteUID());
        sourceListFields = customConfig.getAssetListPageFields();
        targetListFields = new ArrayList();
        assetsListPageFields = new DualListModel<>(sourceListFields, targetListFields);
    }
    
    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        if (null != event) {
            if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
                selectSite = (OrgInfo) event.getTreeNode().getData();
            } else {
                selectSite = orgDao.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());
            }
        }

    }
    
    public void onAssetsTransfer(TransferEvent event) {

        for(Object item : event.getItems()) {
            if(event.isAdd()){
                targetFields.add((String)item);
            }else{
                targetFields.remove((String)item);
            }
        }
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }
    
    
    
}
