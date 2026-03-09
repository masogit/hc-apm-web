package com.ge.apm.service.asset;

import com.ge.apm.dao.OwnerConfigRepository;
import com.ge.apm.domain.OwnerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 212605082 on 2019/2/27.
 */
@Component
public class OwnerConfigService {

    @Autowired
    private OwnerConfigRepository ownerConfigDao;

    public List<OwnerConfig> loadDepartmentOwnerConfig(String clinicalDeptUID, String siteUID){
        return ownerConfigDao.findByDeptUIDAndSiteUID(clinicalDeptUID, siteUID);
    }

    public List<OwnerConfig> loadAssetGroupOwnerConfig(Integer assetGroupID, Integer assetGroupSecondaryID, String siteUID){
        return ownerConfigDao.findByAssetGroupIDAndAssetGroupSecondaryIDAndSiteUID(assetGroupID, assetGroupSecondaryID, siteUID);
    }

    @Transactional
    public void saveDepartmentOwner(List<OwnerConfig> ownerConfigList) {

        for (OwnerConfig ownerConfig : ownerConfigList) {
            ownerConfigDao.save(ownerConfig);
        }

    }

    public List<OwnerConfig> loadAssetTypeOwnerConfig(String clinicalDeptUID, String siteUID, String assetType) {
        return ownerConfigDao.findByDeptUIDAndSiteUIDAndAssetType(clinicalDeptUID, siteUID, assetType);
    }
}
