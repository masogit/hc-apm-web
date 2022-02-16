package com.ge.apm.dao;


import com.ge.apm.domain.ZkbIbCrossUser;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author 212605082 on 2019/12/16.
 */
public interface ZkbIbCrossUserRepository extends GenericRepositoryUUID<ZkbIbCrossUser> {


    @Query("delete from ZkbIbCrossUser")
    void deleteAllBatch();



}
