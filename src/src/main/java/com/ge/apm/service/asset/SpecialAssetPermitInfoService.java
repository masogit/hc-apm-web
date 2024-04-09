package com.ge.apm.service.asset;


import com.ge.apm.dao.PermitInfoRepository;
import com.ge.apm.dao.SpecialAssetPermitInfoRepository;
import com.ge.apm.domain.PermitInfo;
import com.ge.apm.domain.SpecialAssetPermitInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;

@Service
public class SpecialAssetPermitInfoService {

    @Autowired
    private  PermitInfoRepository repository;

    @Autowired
    private  SpecialAssetPermitInfoRepository specialAssetPermitInfoRepository;


    public void savePermitInfo(Set<PermitInfo> permitInfos, String id) {
        if(id!=null){
            specialAssetPermitInfoRepository.deleteBySpecialAssetId(id);

        }
        repository.save(permitInfos);

    }

    public void saveSpecialAssetPermitInfo(Set<PermitInfo> permitInfos, String id) {
        ArrayList<SpecialAssetPermitInfo> specialAssetPermitInfos = new ArrayList<>(permitInfos.size());
        for (PermitInfo permitInfo : permitInfos) {
            SpecialAssetPermitInfo specialAssetPermitInfo = new SpecialAssetPermitInfo();
            specialAssetPermitInfo.setSpecialAssetId(id);
            specialAssetPermitInfo.setPermitInfoUid(permitInfo.getUid());
            specialAssetPermitInfos.add(specialAssetPermitInfo);
        }
        specialAssetPermitInfoRepository.save(specialAssetPermitInfos);
    }
}
