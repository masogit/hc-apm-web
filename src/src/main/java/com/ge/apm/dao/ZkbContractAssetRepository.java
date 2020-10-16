package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContractAsset;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;
/**
 * @author : 503189362
 * @description :
 * @date : 2020/5/18 14:06
 */
public interface ZkbContractAssetRepository extends GenericRepositoryUUID<ZkbContractAsset> {

    List<ZkbContractAsset> findByContractId(String contractId);
    List<ZkbContractAsset> findByAssetId(String assetId);

    ZkbContractAsset findFirstByContractIdAndAssetId(String contractId, String assetId);

    @Query(value = "select distinct a.contract_id from zkb_contract_asset a where a.asset_id=?1", nativeQuery = true)
    List<String> findDistinctContractId(String assetId);


}
