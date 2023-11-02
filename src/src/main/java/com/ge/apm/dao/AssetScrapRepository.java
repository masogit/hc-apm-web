package com.ge.apm.dao;


import com.ge.apm.domain.AssetScrap;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AssetScrapRepository extends GenericRepositoryUUID<AssetScrap> {


    @Query(value="select * from asset_scrap where asset_uid=?1 and current_status in ?2 limit 1",nativeQuery=true)
    AssetScrap getByAssetUidAndStatus(String assetUid, List<Integer> status);


    @Query(value="select * from asset_scrap where asset_uid=?1 order by created_date desc limit 1",nativeQuery=true)
    AssetScrap findByAssetUid(String item);


    @Query(value="select * from asset_scrap where asset_id=?1 order by created_date desc limit 1",nativeQuery=true)
    AssetScrap findByAssetId(Integer id);
}
