package com.ge.apm.dao;

import com.ge.apm.domain.AssetHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author csh
 * @version 1.0
 * @date 2020/5/15
 * @descript AssetHistory历史留痕操作
 * @package com.ge.apm.dao
 */
public interface AssetHistoryRepository extends GenericRepositoryUUID<AssetHistory>, JpaRepository<AssetHistory, String> {

    /**
     * @return void
     * @throws
     * @params [assetUid]
     * @author csh
     * @createTime 2020/5/15 15:14
     * @description 根据uid查询数据
     */
    @Query(value = "select * from asset_history where asset_uid = ?1 order by created_date DESC limit 5", nativeQuery = true)
    List<AssetHistory> findByUidOrderByCreatedDateDescAtMostFive(String assetUid);

    @Query(value = "select * from asset_history where asset_uid = ?1 order by created_date DESC limit 1", nativeQuery = true)
    AssetHistory getAssetHistoryByAssetUidOne(String uid);
}
