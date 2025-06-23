package com.ge.apm.dao;


import com.ge.apm.domain.AssetScrap;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;
import java.util.Set;

public interface AssetScrapRepository extends GenericRepositoryUUID<AssetScrap> {


    @Query(value="select * from asset_scrap where asset_uid=?1 and current_status in ?2 limit 1",nativeQuery=true)
    AssetScrap getByAssetUidAndStatus(String assetUid, List<Integer> status);


    @Query(value="select * from asset_scrap where asset_uid=?1 order by created_date desc limit 1",nativeQuery=true)
    AssetScrap findByAssetUid(String item);


    @Query(value="select * from asset_scrap where asset_id=?1 order by created_date desc limit 1",nativeQuery=true)
    AssetScrap findByAssetId(Integer id);

    @Query(value="select al.* from asset_scrap al where  al.asset_uid in ?1 and al.current_status= 3 ", nativeQuery = true)
    List<AssetScrap> selectByAssetUids(Set<String> assetUids);


}
