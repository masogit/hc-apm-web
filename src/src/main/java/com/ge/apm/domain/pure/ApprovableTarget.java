package com.ge.apm.domain.pure;

import com.ge.apm.domain.AssetInfo;

import java.math.BigDecimal;

public interface ApprovableTarget {

    BigDecimal getTotalPriceToBigDecimal();

    String getBusinessNumber();

    String getBusinessUid();

    int getBusinessType();

    default AssetInfo getApprovalAssetInfo(){
        return null;
    }

    default String getAssetType(){
        return null;
    }

    default String getAssetGroup(){
        return null;
    }

    default String getClinicalDeptUid(){
        return null;
    }


    default String getBusinessDigest() {
        return null;
    }

    default boolean getFlag(){
        return false;
    }

    //多及审批开启需要找设备的院区的配置
    default String getAssetSiteUid(){
        return null;
    }

}
