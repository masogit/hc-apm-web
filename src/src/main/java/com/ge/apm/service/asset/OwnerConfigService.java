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
        return  ownerConfigDao.findByOwnerConfigTypeAndTypeUIDAndSiteUID(OwnerConfig.OwnerConfigType.Department.name(), clinicalDeptUID, siteUID);
    }

    public List<OwnerConfig> loadAssetGroupOwnerConfig(String assetGroupID, String siteUID){
        return  ownerConfigDao.findByOwnerConfigTypeAndTypeUIDAndSiteUID(OwnerConfig.OwnerConfigType.AssetGroup.name(), assetGroupID, siteUID);
    }

    @Transactional
    public void saveDepartmentOwner(List<OwnerConfig> ownerConfigList) {

        for (OwnerConfig ownerConfig : ownerConfigList) {
            if(ownerConfig.getOwnerUID() != null){
                ownerConfigDao.save(ownerConfig);
            }else{
                if(ownerConfig.getId() != null){
                    ownerConfigDao.delete(ownerConfig.getId());
                }
            }
        }

    }

}
