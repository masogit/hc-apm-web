package com.ge.apm.dao;


import com.ge.apm.domain.ZkbHighPartInfo;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author 212605082 on 2019/12/16.
 */
public interface ZkbHighPartInfoRepository extends GenericRepositoryUUID<ZkbHighPartInfo> {



    List<ZkbHighPartInfo> findByPartNo(String partNo);
    ZkbHighPartInfo findFirstByPartNo(String partNo);


    @Query("select o from ZkbHighPartInfo o where o.partNo = ?1 and o.partType = 'bulbTube'")
    List<ZkbHighPartInfo> findBulbTube(String partNo);

    @Query("select o from ZkbHighPartInfo o where o.partNo = ?1 and o.partType = 'consumable'")
    List<ZkbHighPartInfo> findConsumable(String partNo);
}
