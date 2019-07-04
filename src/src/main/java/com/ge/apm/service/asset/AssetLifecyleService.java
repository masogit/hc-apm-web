package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetLifecycleRepository;
import com.ge.apm.dao.PmOrderPreCreateRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetLifecycle;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.constant.AssetConstant;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AssetLifecyleService {

    @Autowired
    private AssetLifecycleRepository assetLifecycleRepository;

    @Autowired
    private PmOrderPreCreateRepository pmOrderPreCreateRepository;

    @Async
    public void saveLifecycle(int originStatus, AssetInfo assetInfo, UserAccount user){
        int currentStatus = assetInfo.getStatus();
        boolean isDeleted = assetInfo.getIsDeleted();
        boolean isValid = assetInfo.getIsValid();

        //delete exists associated prePmOrders
        if(Lists.newArrayList(AssetConstant.ASSET_STATUS_TO_TERMINATED,AssetConstant.ASSET_STATUS_STOP).contains(currentStatus) || isDeleted || !isValid){
            pmOrderPreCreateRepository.deleteByAssetUid(assetInfo.getUid());
        }
        AssetLifecycle assetLifecycle = new AssetLifecycle();
        assetLifecycle.setAssetId(assetInfo.getId());
        assetLifecycle.setAssetUid(assetInfo.getUid());
        assetLifecycle.setCreateTime(new Date());
        assetLifecycle.setLastStatus(originStatus);
        if(isDeleted){
            assetLifecycle.setCurrentStatus(AssetConstant.ASSET_STATUS_DELETED);//已删除的
        }else if(!isValid){
            assetLifecycle.setCurrentStatus(AssetConstant.ASSET_STATUS_INVALID);//报废
        }else{
            assetLifecycle.setCurrentStatus(currentStatus);
        }
        assetLifecycle.setModifyBy(user.getLastModifiedBy());
        assetLifecycle.setModifyByUserId(user.getId());
        assetLifecycle.setModifyTime(new Date());
        assetLifecycle.setTenantUID(user.getTenantUID());
        assetLifecycle.setHospitalUID(user.getHospitalUID());
        assetLifecycle.setInstitutionUID(user.getInstitutionUID());
        assetLifecycle.setSiteUID(user.getSiteUID());

        assetLifecycleRepository.save(assetLifecycle);
    }
}
