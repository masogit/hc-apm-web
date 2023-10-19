package com.ge.apm.dao;

import com.ge.apm.domain.UserAssetGroup;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface UserAssetGroupRepository extends GenericRepository<UserAssetGroup> {

    @Query(value="select uag.user_uid from user_asset_group uag where uag.asset_group_uid in ?1",nativeQuery=true)
    public List<String> getRelevantUserUids(List<String> uids);

    @Transactional
    @Modifying
    @Query(value = "delete from user_asset_group where user_uid = ?1",nativeQuery = true)
    void deleteByUserUid(String uid);

    @Transactional
    @Modifying
    @Query(value = "delete from user_asset_group where asset_group_uid = ?1",nativeQuery = true)
    void deleteByAssetGroupUid(String uid);


    @Query(value="select atg.encoded from user_asset_group uag left join asset_type_group  atg on uag.asset_group_uid=atg.uid        where uag.user_uid = ?1",nativeQuery=true)
    public List<String> findByUserUid(String uid);
}
