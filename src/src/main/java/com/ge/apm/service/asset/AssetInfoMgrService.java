/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
    private ConfigCacheService configCacheService;
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
    @Autowired
    private InventoryOrderDetailAssetInfoRepository  detailAssetInfoRepository;
    @Autowired
    private V2_WorkOrderRepository v2_workOrderRepository;
    @Autowired
    private V2_WorkOrderStepRepository v2_workOrder_step;
    @Autowired
    private V2_PmOrderRepository v2_pmOrderRepository;
    @Autowired
    private AssetInfoHistoryRepository assetInfoHistory;
    @Autowired
    private InventoryIssueRepository inventoryIssueRepository;
    @Autowired
    private ContractInfoItemRepository contractInfoItemRepository;
    private static final Logger logger = LoggerFactory.getLogger(AssetInfoMgrService.class);

    public void deleteAsset(AssetInfo asset) {
        //List<V2_ServiceRequest> srlist = srDao.findByAssetId(asset.getId());
        List<SearchFilter> searchFilters=new ArrayList<>();
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
        searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, asset.getId()));
        List<V2_WorkOrder> srlist = v2_workOrderRepository.findBySearchFilter(searchFilters);
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
        asset.setLastModifiedBy(UserContextService.getCurrentUserAccount().getLoginName());
        asset.setLastModifiedDate(new Date());
        clearAssetLinkUid(asset);
        assetDao.save(asset);
        updateNurseSiteMaintain(asset.getUid());
        WebUtil.addSuccessMessage("设备成功删除");
        logger.info("delete asset info successful. assetName: {}", asset.getName());
    }

    /**
     *  YHAAO-22813
     * @param asset
     */
    private void clearAssetLinkUid(AssetInfo asset) {
        String tenantUID = asset.getTenantUID();
        asset.setInstitutionUID(tenantUID);
        asset.setHospitalUID(tenantUID);
        asset.setSiteUID(tenantUID);
        asset.setClinicalDeptUID(reverse(asset.getClinicalDeptUID()));
        asset.setAssetDeptUID(reverse(asset.getAssetDeptUID()));
        asset.setPatrolInspectionOwnerUID(reverse(asset.getPatrolInspectionOwnerUID()));
    }
    public static String reverse(String source){
        if(StringUtils.isEmpty(source)) return source;
        StringBuilder builder = new StringBuilder(source);
        builder.reverse();
        return builder.toString();
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
        assetFilters.add(new SearchFilter("parentAssetId", SearchFilter.Operator.IsNull,null));

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
    public List<Supplier> getOnlySuplierListBySiteUid(String tenantUid) {
        return supplierDao.findSupplierByTenantUID(tenantUid);
    }


    public List<Supplier> getOnlySuplierListBySiteUid(String tenantUid,String name,int size) {
        if(StringUtils.isNotBlank(name)){
            return supplierDao.findSupplierByTenantUIDAndName(tenantUid,name,size);

        }else{
            return supplierDao.findSupplierByTenantUID(tenantUid,size);

        }
    }
    public List<Supplier> getOnlyMaintainListBySiteUid(String tenantUid) {
        return supplierDao.findMaintainByTenantUID(tenantUid);
    }


    public List<Supplier> getOnlyMaintainListBySiteUid(String tenantUid,String name,int size) {
        if(StringUtils.isNotBlank(name)){
            return supplierDao.findMaintainByTenantUID(tenantUid,name,size);

        }else{
            return supplierDao.findMaintainByTenantUID(tenantUid,size);

        }
    }
    public List<Supplier> getOnlyManuListBySiteUid(String tenantUid) {
        return supplierDao.findManuByTenantUID(tenantUid);
    }


    public List<Supplier> getOnlyManuListBySiteUid(String tenantUid,String name,int size) {

        if(StringUtils.isNotBlank(name)){
            return supplierDao.findManuByTenantUID(tenantUid,name,size);

        }else{
            return supplierDao.findManuByTenantUID(tenantUid,size);

        }
    }

    public AssetInfo getAssetInfoByUid(String assetUid) {
        return assetDao.findByUid(assetUid);
    }

    public String getMaintanceType(AssetInfo selected) {
        
        TenantInfo tenant = configCacheService.getTenantByUid(selected.getTenantUID());
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
    @Transactional
    public void deleteAssetForever(AssetInfo asset) throws Exception {
        try {
            AssetInfoHistory history=new AssetInfoHistory();
            Integer assetId = asset.getId();
            String uid = asset.getUid();
            String name = asset.getName();
            asset= assetDao.getByUid(uid);
            if(asset == null){
                throw new RuntimeException("未查询到 \""+name+"\" 设备信息");
            }
            history.setAssetInfo(JSONArray.toJSONString(asset, SerializerFeature.WRITE_MAP_NULL_FEATURES));
            List<SearchFilter> searchFilters=new ArrayList<>();

            searchFilters.add(new SearchFilter("assetInfo", SearchFilter.Operator.EQ,asset));
            List<InventoryOrderDetail> bySearchFilter = detailAssetInfoRepository.findBySearchFilter(searchFilters);
            if(!CollectionUtils.isEmpty(bySearchFilter)){
                String jsonString = JSONArray.toJSONString(bySearchFilter,SerializerFeature.WRITE_MAP_NULL_FEATURES);
                history.setInventoryOrderDetail(jsonString);
                List<String> collect = bySearchFilter.stream().map(InventoryOrderDetail::getId).collect(Collectors.toList());
                Integer aLong = detailAssetInfoRepository.deleteById(collect);
                logger.info("detailAssetInfoRepository:{}",aLong);
            }
            searchFilters.clear();
            searchFilters.add(new SearchFilter("assetUid", SearchFilter.Operator.EQ,asset.getUid()));
            List<ContractInfoItem> contractInfoItemList = contractInfoItemRepository.findBySearchFilter(searchFilters);
            if(!CollectionUtils.isEmpty(contractInfoItemList)){
                String jsonString = JSONArray.toJSONString(contractInfoItemList,SerializerFeature.WRITE_MAP_NULL_FEATURES);
                history.setContractInfoItem(jsonString);
                List<String> collect = contractInfoItemList.stream().map(ContractInfoItem::getId).collect(Collectors.toList());
                Integer aLong = contractInfoItemRepository.deleteById(collect);
                logger.info("contractInfoItemRepository:{}",aLong);
            }
            searchFilters.clear();
            searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.EQ,assetId));
            List<AssetLifecycleEvent> assetLifecycleEventByAssetId = eventDao.findBySearchFilter(searchFilters);
            if(!CollectionUtils.isEmpty(assetLifecycleEventByAssetId)){
                String jsonString = JSONArray.toJSONString(assetLifecycleEventByAssetId,SerializerFeature.WRITE_MAP_NULL_FEATURES);
                history.setAssetLifecycleEvent(jsonString);
                for (AssetLifecycleEvent assetLifecycleEvent : assetLifecycleEventByAssetId) {
                    eventDao.delete(assetLifecycleEvent.getId());
                }

            }
            searchFilters.clear();
            searchFilters.add(new SearchFilter("assetInfo", SearchFilter.Operator.EQ,asset));
            List<InventoryIssue> inventoryIssueList = inventoryIssueRepository.findBySearchFilter(searchFilters);
            //setAttribute(history,"inventoryIssue",inventoryIssueList);
            if(!CollectionUtils.isEmpty(inventoryIssueList)){
                String jsonString = JSONArray.toJSONString(inventoryIssueList,SerializerFeature.WRITE_MAP_NULL_FEATURES);
                history.setInventoryIssue(jsonString);
                List<String> collect = inventoryIssueList.stream().map(InventoryIssue::getId).collect(Collectors.toList());
                Integer aLong = inventoryIssueRepository.deleteById(collect);
                logger.info("inventoryIssueRepository:{}",aLong);
            }
            List<V2_WorkOrder> workOrder = v2_workOrderRepository.findByAssetId(assetId);
            //setAttribute(history,"workOrder",workOrder);
            if(!CollectionUtils.isEmpty(workOrder)){
                String jsonString = JSONArray.toJSONString(workOrder,SerializerFeature.WRITE_MAP_NULL_FEATURES);
                history.setWorkOrder(jsonString);
                List<String> woIds = workOrder.stream().map(V2_WorkOrder::getId).collect(Collectors.toList());
                    searchFilters.clear();
                    searchFilters.add(new SearchFilter("woId", SearchFilter.Operator.IN,woIds));
                    List<V2_WorkOrder_Step>  workOrderSteps = v2_workOrder_step.findBySearchFilter(searchFilters);
                    if(!CollectionUtils.isEmpty(workOrderSteps)){
                        jsonString = JSONArray.toJSONString(workOrderSteps,SerializerFeature.WRITE_MAP_NULL_FEATURES);
                        history.setWorkOrderStep(jsonString);
                        List<String> collect = workOrderSteps.stream().map(V2_WorkOrder_Step::getId).collect(Collectors.toList());
                        Integer aLong = v2_workOrder_step.deleteById(collect);
                        logger.info("v2_workOrder_step:{}",aLong);
                    }
                Integer aLong=v2_workOrderRepository.deleteById(woIds);
                logger.info("v2_workOrderRepository:{}",aLong);
            }


            searchFilters.clear();
            searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.EQ,assetId));
            List<V2_PmOrder> pmOrder = v2_pmOrderRepository.findBySearchFilter(searchFilters);
            if(!CollectionUtils.isEmpty(pmOrder)){
                String  jsonString = JSONArray.toJSONString(pmOrder,SerializerFeature.WRITE_MAP_NULL_FEATURES);
                history.setPmOrder(jsonString);
                List<String> collect = pmOrder.stream().map(V2_PmOrder::getId).collect(Collectors.toList());
                Integer aLong = v2_pmOrderRepository.deleteById(collect);
                logger.info("v2_pmOrderRepository:{}",aLong);
            }

            assetDao.delete(asset);
            String userUid = UserContextService.getCurrentUserAccount().getUid();
            history.setAssetUid(asset.getUid());
            history.setSiteUID(asset.getSiteUID());
            history.setName(asset.getName());
            history.setCreateBy(userUid);
            history.setCreateDate(new Date());
            assetInfoHistory.save(history);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
