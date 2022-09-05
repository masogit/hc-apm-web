package com.ge.apm.service.sharedasset.valuation.impl;

import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.SharedAssetType;
import com.ge.apm.service.sharedasset.valuation.UnattendedFee;

import java.math.BigDecimal;

// 不存在起步价
public class StartingPriceEmpty extends UnattendedFee {

    @Override
    protected void setTotalAmount(SharedAssetType sharedAssetType, SharedAssetReqDetail reqDetail) {
        super.setTotalAmount(sharedAssetType,reqDetail);
        BigDecimal totalAmount = reqDetail.getTotalAmount();
        reqDetail.setTotalAmount(totalAmount.compareTo(BigDecimal.ZERO)==1?totalAmount:BigDecimal.ZERO);
    }
}
