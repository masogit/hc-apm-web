/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_PmOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Component
public class AssetInfoMgrService {
    
    @Autowired
    private AssetInfoRepository assetDao;
    @Autowired
    private ServiceRequestRepository srDao;
    @Autowired
    private V2_PmOrderRepository pmDao;
    @Autowired
    private QrCodeService qrCodeService;
    
    public void deleteAsset(AssetInfo asset) {
        List<V2_ServiceRequest> srlist = srDao.findByAssetId(asset.getId());
        if (srlist != null && !srlist.isEmpty()) {
            WebUtil.addErrorMessage("该设备有工单存在,不能删除");
            return;
        }
        List<SearchFilter> pmFilter = Arrays.asList(new SearchFilter("assetUid", SearchFilter.Operator.EQ, asset.getUid()),
                new SearchFilter("isFinished", SearchFilter.Operator.EQ, false));
        List<V2_PmOrder> pmList = pmDao.findBySearchFilter(pmFilter);
        if (pmList != null && !pmList.isEmpty()) {
            WebUtil.addErrorMessage("该设备有保养工单存在,不能删除");
            return;
        }

        if (asset.getQrCode() != null) {
            qrCodeService.updateQrCodeStatus(asset.getQrCode(), asset.getSiteUID(), 1);
        }
        asset.setIsDeleted(true);
        asset.setIsValid(false);
        asset.setQrCode(null);
        assetDao.save(asset);
    }
    
    
    public List<AssetInfo> getRelationAssets(AssetInfo asset){
        List<SearchFilter> assetFilters = new ArrayList();
        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        assetFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, asset.getSiteId()));
        if (asset.getClinicalDeptUID() != null) {
            assetFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, asset.getClinicalDeptUID()));
        }
        if (asset.getSiteUID() != null) {
            assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, asset.getSiteUID()));
        }
        if (asset.getUid() != null) {
            assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.NE, asset.getUid()));
        }
        return assetDao.findBySearchFilter(assetFilters);
    }
     
    
    public TreeNode getParentAssetTree(AssetInfo asset){
        
        List<AssetInfo> assetList = getRelationAssets(asset);
        assetList.add(asset);
        Map<Integer, TreeNode> treeMap = assetList.stream().collect(Collectors.toMap(AssetInfo::getId, item -> new DefaultTreeNode("asset", item, null)));
        assetList.forEach(item ->{
            if (treeMap.containsKey(item.getParentAssetId())) {
                treeMap.get(item.getParentAssetId()).getChildren().add(treeMap.get(item.getId()));
            } 
        });
        
        TreeNode res = treeMap.get(asset.getId());
        while(res.getParent()!=null){
            res = res.getParent();
        }
        TreeNode rootNode = new DefaultTreeNode("root", null, null);
        rootNode.getChildren().add(res);
        return rootNode;
    }
    
}
