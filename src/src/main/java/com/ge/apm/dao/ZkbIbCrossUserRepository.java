package com.ge.apm.dao;


import com.ge.apm.domain.ZkbIbCrossUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author 212605082 on 2019/12/16.
 */
public interface ZkbIbCrossUserRepository extends GenericRepositoryUUID<ZkbIbCrossUser> {


    @Transactional
    @Modifying
    @Query(value = "delete from zkb_ib_cross_user", nativeQuery = true)
    void deleteAllBatch();



}
