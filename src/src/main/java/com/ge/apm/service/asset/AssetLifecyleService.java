package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetLifecycleRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetLifecycle;
import com.ge.apm.domain.UserAccount;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AssetLifecyleService {

    @Autowired
    private AssetLifecycleRepository assetLifecycleRepository;

    @Async
    public void saveLifecycle(int originStatus, AssetInfo assetInfo, UserAccount user){
        int currentStatus = assetInfo.getStatus();
        AssetLifecycle assetLifecycle = new AssetLifecycle();
        assetLifecycle.setAssetId(assetInfo.getId());
        assetLifecycle.setAssetUid(assetInfo.getUid());
        assetLifecycle.setCreateTime(new Date());
        assetLifecycle.setLastStatus(originStatus);
        if(assetInfo.getIsDeleted()){
            assetLifecycle.setCurrentStatus(-1);//已删除的资产
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
