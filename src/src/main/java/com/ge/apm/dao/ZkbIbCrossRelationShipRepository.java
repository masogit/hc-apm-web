package com.ge.apm.dao;


import com.ge.apm.domain.ZkbIbCrossRelationShip;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author 212605082 on 2019/12/16.
 */
public interface ZkbIbCrossRelationShipRepository extends GenericRepositoryUUID<ZkbIbCrossRelationShip> {



    @Query("delete from ZkbIbCrossRelationShip")
    void deleteAllBatch();


}
