package com.ge.apm.service.asset;

import com.ge.apm.dao.InventoryOrderDetailAssetInfoRepository;
import com.ge.apm.dao.AssetTagRuleRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigItemRepository;
import com.ge.apm.dao.SharedAssetReqDetailRepository;
import com.ge.apm.dao.SharedAssetRequestRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.constant.ReceiveBorrowConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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


}
