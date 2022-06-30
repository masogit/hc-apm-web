package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.constant.ReceiveBorrowConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssetInfoBulkEditCheckService {

    @Autowired
    private SharedAssetRequestRepository sharedAssetRequestRepository;

    @Autowired
    private SharedAssetReqDetailRepository sharedAssetReqDetailRepository;

    @Autowired
    private InventoryOrderDetailAssetInfoRepository inventoryOrderDetailAssetInfoRepository;

    @Autowired
    private NurseSiteMaintainConfigItemRepository nurseSiteMaintainConfigItemRepository;

    @Autowired
    private AssetTagRuleRepository  assetTagRuleRepository;

    @Autowired
    private NurseSiteMaintainItemRepository nurseSiteMaintainItemRepository;

    @Autowired
    private NurseSiteMaintainDataRepository nurseSiteMaintainDataRepository;

    @Autowired
    private V2_PmOrderRepository v2_PmOrderRepository;

    @Autowired
    private WorkflowConfigRepository workflowConfigRepository;
    @Autowired
    private AssetInfoRepository assetInfoRepository;

    //验证调剂中心
    public boolean verify(List<AssetInfo> assets) {
        if(assets != null && !assets.isEmpty()){
            for (AssetInfo assetInfo:assets) {
                String assetDeptUID = assetInfo.getAssetDeptUID();
                String clinicalDeptUID = assetInfo.getClinicalDeptUID();
                if(assetDeptUID!=null && clinicalDeptUID !=null &&!assetDeptUID.equals(clinicalDeptUID)){
                    List<WorkflowConfig> workflowConfigs = workflowConfigRepository.findByDeptForSharedAsset(clinicalDeptUID);
                    if(workflowConfigs!=null && !workflowConfigs.isEmpty()){
                        return false;
                    }
                }
            }
           List<String> assetUids = assets.stream().map(p -> p.getUid()).collect(Collectors.toList());
            List<Integer> status = new ArrayList<>();
            status.add(ReceiveBorrowConstant.STATE_RENT_ACCEPTED);
            status.add(ReceiveBorrowConstant.STATE_RENT_DELIVERED);
            status.add(ReceiveBorrowConstant.STATE_RENT_SUSPENDED);
            status.add(ReceiveBorrowConstant.STATE_RENT_CONFIRMED);
            status.add(ReceiveBorrowConstant.STATE_RETURN_ACCEPTED);
            status.add(ReceiveBorrowConstant.STATE_RETURN_DELIVERED);
            status.add(ReceiveBorrowConstant.STATE_RETURN_SUSPENDED);
            List<SharedAssetReqDetail> sharedAssetReqDetails = sharedAssetReqDetailRepository.findCountByAssetInfoIdsAndState(assetUids,status);
            if(sharedAssetReqDetails.size() > 0){
                List<String> assetUidList = new ArrayList<>();
                for (int i = 0; i < sharedAssetReqDetails.size(); i++) {
                    SharedAssetReqDetail sharedAssetReqDetail = sharedAssetReqDetails.get(i);
                    String assetInfoUid = sharedAssetReqDetail.getAssetInfoId();
                    if(!assetUidList.contains(assetInfoUid)){
                        assetUidList.add(assetInfoUid);
                        String returnId = sharedAssetReqDetail.getReturnId();
                        if(!"".equals(returnId) && returnId !=null){
                            SharedAssetRequest sharedAssetRequest = sharedAssetRequestRepository.findById(returnId);
                            int state = sharedAssetRequest.getState();
                            if (state==ReceiveBorrowConstant.STATE_RETURN_CONFIRMED){
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        }
        return true;
    }
    public List<String> verifyByBatchEdit(List<AssetInfo> assets) {
        if(assets != null && !assets.isEmpty()){
           List<String> assetUids = assets.stream().map(p -> p.getUid()).collect(Collectors.toList());
            List<Integer> status = new ArrayList<>();
            status.add(ReceiveBorrowConstant.STATE_RENT_ACCEPTED);
            status.add(ReceiveBorrowConstant.STATE_RENT_DELIVERED);
            status.add(ReceiveBorrowConstant.STATE_RENT_SUSPENDED);
            status.add(ReceiveBorrowConstant.STATE_RENT_CONFIRMED);
            status.add(ReceiveBorrowConstant.STATE_RETURN_ACCEPTED);
            status.add(ReceiveBorrowConstant.STATE_RETURN_DELIVERED);
            status.add(ReceiveBorrowConstant.STATE_RETURN_SUSPENDED);
            return sharedAssetReqDetailRepository.findCountByAssetInfoIdsAndStateByBatchEdit(assetUids,status);
        }
        return null;
    }
    public boolean verifyForMaintain(List<AssetInfo> assets) {
        if(assets != null && !assets.isEmpty()){
            Set<String> assetUids = assets.stream().map(p -> p.getUid()).collect(Collectors.toSet());
            List<String> status = new ArrayList<>();
            status.add("assign");
            status.add("accept");
            status.add("reAssign");
            status.add("tempRepair");
            status.add("approval");
            status.add("pass");
            status.add("Maintain");
            status.add("repair");
            status.add("acceptNot");
            status.add("simplify");
            status.add("reject");
            status.add("partial");
            status.add("AcceptTance");

            Set<V2_PmOrder> v2_pmOrders = v2_PmOrderRepository.findV2PmOrderByAssetUids(assetUids);
            if(v2_pmOrders !=null && !v2_pmOrders.isEmpty()){
                for(V2_PmOrder order:v2_pmOrders){
                    List list = status.stream().filter(u -> u.equalsIgnoreCase(order.getCurrentStepId())).collect(Collectors.toList());
                    if (list !=null && !list.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public List<String> verifyForMaintainByBatchEdit(List<AssetInfo> assets) {
        if(assets != null && !assets.isEmpty()){
            Set<String> assetUids = assets.stream().map(p -> p.getUid()).collect(Collectors.toSet());
            List<String> status = new ArrayList<>();
            status.add("assign");
            status.add("accept");
            status.add("reAssign");
            status.add("tempRepair");
            status.add("approval");
            status.add("pass");
            status.add("Maintain");
            status.add("repair");
            status.add("acceptNot");
            status.add("simplify");
            status.add("reject");
            status.add("partial");
            status.add("AcceptTance");
            List<V2_PmOrder> v2_pmOrderList = new ArrayList<>();
            return v2_PmOrderRepository.findV2PmOrderByAssetUidByBatchEdit(assetUids);
        }
        return null;
    }

    //验证是否有正在盘点的设备
    public boolean verifyForInventory(List<AssetInfo> assets) {
        if(assets != null && !assets.isEmpty()){
            List<Integer> assetIds = assets.stream().map(assetInfo -> assetInfo.getId()).collect(Collectors.toList());
            int inventoryingAssetCount = inventoryOrderDetailAssetInfoRepository.getInventoryingAssetCount(assetIds);
            if(inventoryingAssetCount > 0)
                return false;
        }
        return true;
    }
    //验证是否有正在盘点的设备
    public List<Integer> verifyForInventoryByBatchEdit(List<AssetInfo> assets) {
        if(assets != null && !assets.isEmpty()){
            List<Integer> assetIds = assets.stream().map(assetInfo -> assetInfo.getId()).collect(Collectors.toList());
            return inventoryOrderDetailAssetInfoRepository.getInventoryingAssetCountByBatchEdit(assetIds);
        }
        return null;
    }


    public Optional<String> verifyForNurseSite(List<AssetInfo> assets) {
        // asset in repaire  status
        for(AssetInfo as: assets){
            //临床助手检查进行中
            List<NurseSiteMaintainItem> nurseSiteMaintainItemList = nurseSiteMaintainItemRepository.findByAssetUid(as.getUid());
            if(!StringUtils.isEmpty(nurseSiteMaintainItemList) && nurseSiteMaintainItemList.size()>0){
                Set<String> setMaintainIds = new HashSet<String>();
                for (int i = 0; i < nurseSiteMaintainItemList.size(); i++) {
                    setMaintainIds.add(nurseSiteMaintainItemList.get(i).getId());
                }
                Set<NurseSiteMaintainData> maintainDataByParentIds = nurseSiteMaintainDataRepository.findMaintainDataByParentIds(setMaintainIds);
                for (NurseSiteMaintainData nurseSiteMaintainData : maintainDataByParentIds) {
                    Boolean checkValue = nurseSiteMaintainData.getCheckValue() != null ? true :false;
                    if(!checkValue){
                        return Optional.of("设备("+as.getName()+")当前有未完成的日常检查工单无法迁移 请先关闭工单！");
                    }
                }
            }
        }

        return Optional.empty();
    }
    public boolean verifySiteToSite(List<AssetInfo> assets){
        boolean flag=true;
        if(CollectionUtils.isEmpty(assets)) return flag;
        for (AssetInfo asset : assets) {
            if(assetInfoRepository.secondment(asset.getUid()) > 0){
                flag=false;
                break;
            }
        }
        return flag;
    }
    /**
     * 批量导入资产时
     * @param assets
     * @return
     */
    public Optional<String> verifyForNurseSiteByBatchImport(List<AssetInfo> assets) {
        // asset in repaire  status
        for(AssetInfo as: assets){
            //临床助手检查进行中
            List<NurseSiteMaintainItem> nurseSiteMaintainItemList = nurseSiteMaintainItemRepository.findByAssetUid(as.getUid());
            if(!StringUtils.isEmpty(nurseSiteMaintainItemList) && nurseSiteMaintainItemList.size()>0){
                Set<String> setMaintainIds = new HashSet<String>();
                for (int i = 0; i < nurseSiteMaintainItemList.size(); i++) {
                    setMaintainIds.add(nurseSiteMaintainItemList.get(i).getId());
                }
                Set<NurseSiteMaintainData> maintainDataByParentIds = nurseSiteMaintainDataRepository.findMaintainDataByParentIds(setMaintainIds);
                for (NurseSiteMaintainData nurseSiteMaintainData : maintainDataByParentIds) {
                    Boolean checkValue = nurseSiteMaintainData.getCheckValue() != null ? true :false;
                    if(!checkValue){
                        return Optional.of("设备("+as.getName()+")当前有未完成的日常检查工单无法迁移 请先关闭工单！");
                    }
                }
            }
        }

        return Optional.empty();
    }

    public List<AssetInfo>verifyForNurseSiteByBatchEdit(List<AssetInfo> assets) {
        List<AssetInfo> list = new ArrayList<>();
        for(AssetInfo as: assets){
            //临床助手检查进行中
            List<NurseSiteMaintainItem> nurseSiteMaintainItemList = nurseSiteMaintainItemRepository.findByAssetUid(as.getUid());
            if(!StringUtils.isEmpty(nurseSiteMaintainItemList) && nurseSiteMaintainItemList.size()>0){
                Set<String> setMaintainIds = new HashSet<String>();
                for (int i = 0; i < nurseSiteMaintainItemList.size(); i++) {
                    setMaintainIds.add(nurseSiteMaintainItemList.get(i).getId());
                }
                Set<NurseSiteMaintainData> maintainDataByParentIds = nurseSiteMaintainDataRepository.findMaintainDataByParentIds(setMaintainIds);
                for (NurseSiteMaintainData nurseSiteMaintainData : maintainDataByParentIds) {
                    Boolean checkValue = nurseSiteMaintainData.getCheckValue() != null ? nurseSiteMaintainData.getCheckValue() :false;
                    if(!checkValue){
                        list.add(as);
                    }
                }
                return list;
            }
        }

        return null;
    }

}
