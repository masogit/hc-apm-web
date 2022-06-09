package com.ge.apm.dao;



import com.ge.apm.domain.SupplierFEAssetJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SupplierFEAssetJoinRepository extends JpaRepository<SupplierFEAssetJoin, Integer> {

    @Query(value = "select asset_info_uid  from supplier_fe_asset_join  where user_account_uid = ?1  ",nativeQuery = true)
    List<String> findAssetUidsByUserUid(String assetInfoUid);
}
