package com.ge.apm.service.sharedasset.valuation.impl;

import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.SharedAssetType;
import com.ge.apm.service.sharedasset.valuation.UnattendedFee;

import java.math.BigDecimal;

public class StartingPrice extends UnattendedFee {

    @Override
    protected void setTotalAmount(SharedAssetType sharedAssetType, SharedAssetReqDetail reqDetail) {
        super.setTotalAmount(sharedAssetType,reqDetail);
        BigDecimal totalAmount = reqDetail.getTotalAmount();
        BigDecimal startingPrice = BigDecimal.valueOf(reqDetail.getStartingPrice());
        reqDetail.setTotalAmount(totalAmount.compareTo(startingPrice)==1?totalAmount:startingPrice);
    }
}
