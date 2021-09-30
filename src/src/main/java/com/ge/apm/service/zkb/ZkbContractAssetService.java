package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbContractAssetRepository;
import com.ge.apm.domain.ZkbContractAsset;
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
        Boolean checkHalfAsset = null;
        ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(contractId,assetId);
        if(zkbContractAsset == null){
            return checkHalfAsset;
        }
        if(zkbContractAsset.getApplyNum() == null || zkbContractAsset.getApplyNum() == 0){
            long total = 0;
            long highCount = 0;

            total = zkbContractAssetRepository.countByContractIdAndAssetType(contractId,"highMachine");
            highCount = zkbContractAssetRepository.countByApplyNum(contractId,"highMachine");

            if(total == 0){
                return checkHalfAsset;
            }
            double d = total * 0.5 ;
            if((int)d != d){
                d= (int) d;
            }
            if(highCount < d){
                checkHalfAsset = true;
            }else{
                checkHalfAsset = false;
            }
        }else if(zkbContractAsset.getApplyNum() > 0){
            checkHalfAsset = true;
        }
        return checkHalfAsset;
    }

    public void addContractAssetApplyNum(String contractId,String assetId){
        zkbContractAssetRepository.addContractAssetApplyNum(contractId,assetId,"highMachine");
    }

    public void subtractContractAssetApplyNum(String contractId,String assetId){
        zkbContractAssetRepository.subtractContractAssetApplyNum(contractId,assetId,"highMachine");
    }
}
