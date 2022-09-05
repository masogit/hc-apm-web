package com.ge.apm.service.sharedasset.valuation;

import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.SharedAssetType;
import com.ge.apm.service.sharedasset.valuation.impl.StartingPrice;
import com.ge.apm.service.sharedasset.valuation.impl.StartingPriceEmpty;

import java.util.HashMap;
import java.util.Map;

public class ValuationFactory {
    private static final Map<String,UnattendedFee> unattendedFeeMap=new HashMap<>();
    private static final String  STARTING_PRICE="startingPrice";
    private static final String  STARTING_PRICE_EMPTY="startingPriceEmpty";
    static {
        unattendedFeeMap.put(STARTING_PRICE,new StartingPrice());
        unattendedFeeMap.put(STARTING_PRICE_EMPTY,new StartingPriceEmpty());
    }

    private ValuationFactory(){}

    public static void valuation(SharedAssetType sharedAssetType, SharedAssetReqDetail reqDetail){
        if(sharedAssetType==null || reqDetail==null){
            return;
        }
        UnattendedFee unattendedFee =null;
        if(reqDetail.getStartingDuration() == null || reqDetail.getStartingPrice() == null){
             unattendedFee = unattendedFeeMap.get(STARTING_PRICE_EMPTY);
        }else{
            unattendedFee = unattendedFeeMap.get(STARTING_PRICE);
        }
        unattendedFee.valuation(sharedAssetType,reqDetail);
    }

}
