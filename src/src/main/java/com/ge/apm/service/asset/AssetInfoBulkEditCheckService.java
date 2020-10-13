package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.constant.ReceiveBorrowConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    //验证调剂中心
    public boolean verify(List<AssetInfo> assets) {
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
            List<SharedAssetRequest> listByState = sharedAssetRequestRepository.findListByState(status);
            if(listByState !=null && !listByState.isEmpty()){
                List<String> sharedIds = listByState.stream().map(p -> p.getId()).collect(Collectors.toList());
                List<SharedAssetReqDetail> details = sharedAssetReqDetailRepository.findListBySharedIds(sharedIds);
                if(details!=null && !details.isEmpty()){
                    for(SharedAssetReqDetail detail:details){
                        if(assetUids.contains(detail.getAssetInfoId())){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
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


    public Optional<String> verifyForNurseSite(List<AssetInfo> assets) {
        // asset in repaire  status
        for(AssetInfo as: assets){

            //临床助手检查进行中
            List<NurseSiteMaintainItem> nurseSiteMaintainItemList = nurseSiteMaintainItemRepository.findByAssetUid(as.getUid());
            if(!StringUtils.isEmpty(nurseSiteMaintainItemList)){
                Set<String> setMaintainIds = null;
                for (int i = 0; i < nurseSiteMaintainItemList.size(); i++) {
                    setMaintainIds = new HashSet<String>();
                    setMaintainIds.add(nurseSiteMaintainItemList.get(i).getId());
                }
                if(!StringUtils.isEmpty(setMaintainIds)){
                    Set<NurseSiteMaintainData> maintainDataByParentIds = nurseSiteMaintainDataRepository.findMaintainDataByParentIds(setMaintainIds);
                    for (NurseSiteMaintainData nurseSiteMaintainData : maintainDataByParentIds) {
                        Boolean checkValue = nurseSiteMaintainData.getCheckValue() != null ? nurseSiteMaintainData.getCheckValue() :false;
                        if(!checkValue){
                            return Optional.of("设备("+as.getName()+")当前有未完成的日常检查工单无法迁移 请先关闭工单！");
                        }
                    }
                }

            }
        }

        return Optional.empty();
    }

}
