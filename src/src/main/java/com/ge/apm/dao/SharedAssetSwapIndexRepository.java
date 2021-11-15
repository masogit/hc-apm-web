package com.ge.apm.dao;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SharedAssetReqType;
import com.ge.apm.domain.SharedAssetSwapIndex;
import com.ge.apm.domain.UserOrgInfo;
import org.apache.ibatis.annotations.Insert;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface SharedAssetSwapIndexRepository extends GenericRepositoryUUID<SharedAssetSwapIndex> {
    SharedAssetSwapIndex findByAssetUid(String assetUid);

    List<SharedAssetSwapIndex> findBySwapIndexLike(String swapIndex);

    @Query(value="select * from shared_asset_swap_index sasi join asset_info ai on ai.uid = sasi.asset_uid where swap_index = ?1 and clinical_dept_uid = ?2",nativeQuery=true)
    SharedAssetSwapIndex findBySharedAssetTypeaAndSwapIndexAnd(String swapIndex , String clinicalDeptUid);

    @Query(value="select * from shared_asset_swap_index sasi join asset_info ai on ai.uid = sasi.asset_uid where swap_index = ?1 and asset_uid != ?2 and clinical_dept_uid = ?3",nativeQuery=true)//shared_asset_type = ?1 and
    SharedAssetSwapIndex findNotData (String swapIndex , String id , String clinicalDeptUid);


}
