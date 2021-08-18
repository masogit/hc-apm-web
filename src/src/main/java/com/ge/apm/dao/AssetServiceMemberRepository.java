package com.ge.apm.dao;

import com.ge.apm.domain.AssetServiceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AssetServiceMemberRepository extends JpaRepository<AssetServiceMember, String> {

    @Modifying
    @Query(value = "delete from asset_service_member where user_uid = ?1 and (import_file_type = ?2 or import_file_type is null)  ", nativeQuery = true)
    @Transactional
    void deleteByUserUid(String userUid, String fileType);

}
