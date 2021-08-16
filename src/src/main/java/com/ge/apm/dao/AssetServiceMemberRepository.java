package com.ge.apm.dao;

import com.ge.apm.domain.AssetServiceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AssetServiceMemberRepository extends JpaRepository<AssetServiceMember, String> {

    @Modifying
    @Query(value = "delete from AssetServiceMember a where a.userUid = ?1")
    @Transactional
    void deleteByUserUid(String userUid);

}
