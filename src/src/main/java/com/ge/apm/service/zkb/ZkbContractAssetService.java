package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbContractAssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : 503189362
 * @date : 2021/9/26
 * @description :
 */
@Service
public class ZkbContractAssetService {

    @Autowired
    ZkbContractAssetRepository zkbContractAssetRepository;


    public Boolean checkHalfAsset(String contractId,String assetId){
        long total = 0;
        long highCount = 0;

        total = zkbContractAssetRepository.countByContractIdAndAssetType(contractId,"highMachine");
        highCount = zkbContractAssetRepository.countByApplyNum(contractId,"highMachine");

        if(total == 0){
            return null;
        }
        double d = total * 0.5 ;
        if((int)d != d){
            d= (int) d;
            d++;
        }
        if(highCount < d){
            return true;
        }else{
            return false;
        }
    }

    public void updateContractAssetApplyNum(String contractId,String assetId){
        zkbContractAssetRepository.updateContractAssetApplyNum(contractId,assetId,"highMachine");
    }

}
