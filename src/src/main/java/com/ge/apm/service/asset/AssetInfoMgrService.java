/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetLifecycleEventRepository;
import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@Component
public class AssetInfoMgrService {

    @Autowired
    private AssetInfoRepository assetDao;
    @Autowired
    private AssetLifecycleEventRepository eventDao;
    @Autowired
    private TenantInfoRepository tenantDao;
    @Autowired
    private ServiceRequestRepository srDao;
    @Autowired
    private V2_PmOrderRepository pmDao;
    @Autowired
    private QrCodeService qrCodeService;
    @Autowired
    private SupplierRepository supplierDao;
    @Autowired
    private ConfigService configService;
    @Autowired
    private NurseSiteMaintainService nurseSiteMaintainService;

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

        if (isInDispenceCenter(asset)) {
            WebUtil.addErrorMessage("操作失败，该设备为在借中的调剂设备，请先归还！");
            return;
        }

        if (asset.getQrCode() != null) {
            qrCodeService.releaseQrCode(asset.getQrCode(), asset.getSiteUID());
        }
        asset.setIsDeleted(true);
        asset.setIsValid(false);
        asset.setFinancingNum(null);
        asset.setDepartNum(null);
        asset.setQrCode(null);
        assetDao.save(asset);
        updateNurseSiteMaintain(asset.getUid());
    }
    
    public void updateNurseSiteMaintain(String assetUid){
    	nurseSiteMaintainService.deleteAssetUid(assetUid);
    }

    public boolean isInDispenceCenter(AssetInfo assetInfo) {
        WorkflowConfig workflowConfig = configService.findConfigBySite(assetInfo.getSiteUID());

        String deptUid = null;
        if (workflowConfig != null) {
            deptUid = workflowConfig.getDeptForSharedAsset();
        }
        if (StringUtils.isEmpty(deptUid)) {
            return false;
        }

        if (deptUid.equals(assetInfo.getClinicalDeptUID()) && (assetInfo.getAssetDeptUID() != null && !deptUid.equals(assetInfo.getAssetDeptUID()))) {
            return true;
        }

        return false;
    }

    public List<AssetInfo> getParentsAssetsList(AssetInfo asset) {
        return getParentsAssetsList(asset, null, null);
    }

    public List<AssetInfo> getParentsAssetsList(AssetInfo asset, String clinicalDeptUID, String siteUID) {
        List<SearchFilter> assetFilters = new ArrayList();
        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        assetFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        assetFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, 4));  //不能待报废

        if (asset == null ? clinicalDeptUID != null : asset.getClinicalDeptUID() != null) {
            assetFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, asset == null ? clinicalDeptUID : asset.getClinicalDeptUID()));
        }
        if (asset == null ? siteUID != null : asset.getSiteUID() != null) {
            String usingSiteUid = asset == null ? siteUID : asset.getSiteUID();
            assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, usingSiteUid));
            //配置项根据主设备生成编号
            WorkflowConfig config = configService.findConfigBySite(usingSiteUid);
            if (config != null && config.getGenerateMethod() != null && config.getGenerateMethod() == 2) {
                assetFilters.add(new SearchFilter("parentAssetId", SearchFilter.Operator.IsNull, ""));
            }
        } else {
            return Collections.EMPTY_LIST;
        }
        if (asset != null && !Strings.isNullOrEmpty(asset.getUid())) {
            assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.NE, asset.getUid()));
            List<AssetInfo> children = getChildrenAssets(asset);
            if (!children.isEmpty()) {  //去除子设备
                assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.NotIN, children.stream().map(AssetInfo::getUid).collect(Collectors.toList())));
            }
        }
        return assetDao.findBySearchFilter(assetFilters);
    }

    public List<AssetInfo> getChildrenAssets(AssetInfo asset) {
        if (Strings.isNullOrEmpty(asset.getUid())) {
            return Arrays.asList(asset);
        }
        List<SearchFilter> assetFilters = new ArrayList();
        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        assetFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, asset.getSiteId()));
        if (asset.getClinicalDeptUID() != null) {
            assetFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, asset.getClinicalDeptUID()));
        }
        if (asset.getSiteUID() != null) {
            assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, asset.getSiteUID()));
        }
        assetFilters.add(new SearchFilter("parentAssetId", SearchFilter.Operator.IsNotNull, null));
        List<AssetInfo> rowList = assetDao.findBySearchFilter(assetFilters);
        return getChildrenAssetsFromList(rowList, asset);
    }

    private List<AssetInfo> getChildrenAssetsFromList(List<AssetInfo> allAssets, AssetInfo asset) {
        List<AssetInfo> subAssets = allAssets.stream().filter(item -> Objects.equals(item.getParentAssetId(), asset.getId())).collect(Collectors.toList());
        if (!subAssets.isEmpty()) {
            List<AssetInfo> temp = subAssets.stream().flatMap(item -> getChildrenAssetsFromList(allAssets, item).stream()).collect(Collectors.toList());
            subAssets.addAll(temp);
        }
        return subAssets;
    }

    public TreeNode getParentAssetTree(AssetInfo asset) {

        AssetInfo rootAsset = asset;
        while (rootAsset.getParentAssetId() != null) {
            rootAsset = assetDao.findById(rootAsset.getParentAssetId());
        }

        List<AssetInfo> assetList = getChildrenAssets(rootAsset);
        assetList.add(rootAsset);

        Map<Integer, TreeNode> treeMap = assetList.stream().collect(Collectors.toMap(AssetInfo::getId, item -> new DefaultTreeNode("asset", item, null)));
        assetList.forEach(item -> {
            if (treeMap.containsKey(item.getParentAssetId())) {
                treeMap.get(item.getParentAssetId()).getChildren().add(treeMap.get(item.getId()));
            }
        });

        TreeNode res = treeMap.get(rootAsset.getId());
        TreeNode rootNode = new DefaultTreeNode("root", null, null);
        if (null != res) {
            rootNode.getChildren().add(res);
        }
        return rootNode;
    }

    public List<Supplier> getSuplierListBySiteUid(String tenantUid) {
        return supplierDao.getByTenantUID(tenantUid);
    }

    public AssetInfo getAssetInfoByUid(String assetUid) {
        return assetDao.findByUid(assetUid);
    }

    public String getMaintanceType(AssetInfo selected) {
        
        TenantInfo tenant = tenantDao.findByuid(selected.getTenantUID());
        if(tenant.getId() == 46){
            return "external";
        }
        JSONObject tenantConfig = JSONObject.parseObject(tenant.getTenantConfig());
        
        //超声科室
        if(Objects.equals(tenantConfig.getString("edition"), "ultrasound")){
            return "external";
        }
        //代理商租户
        if(Objects.equals(1, tenant.getType())){
            return "both";
        }
        return "internal";
    }

    public List<AssetLifecycleEvent> getAssetEventsList(AssetInfo selected) {
        return eventDao.findByAssetUid(selected.getUid());
    }

}
