package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContractAsset;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;
/**
 * @author : 503189362
 * @description :
 * @date : 2020/5/18 14:06
 */
public interface ZkbContractAssetRepository extends GenericRepositoryUUID<ZkbContractAsset> {

    public List<ZkbContractAsset> findByContractId(String contractId);

    ZkbContractAsset findFirstByContractIdAndAssetId(String contractId, String assetId);

}
