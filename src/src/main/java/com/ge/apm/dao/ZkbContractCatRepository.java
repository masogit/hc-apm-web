package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContractCat;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author : 503189362
 * @date : 2021/6/2
 * @description :
 */
public interface ZkbContractCatRepository extends GenericRepositoryUUID<ZkbContractCat> {

    List<ZkbContractCat> findByContractId(String contractId);
    List<ZkbContractCat> findByContractIdAndIsEnabled(String contractId,boolean enabled);

    @Query("select c from ZkbContractCat c where c.contractId =?1 and c.isEnabled = true order by c.createdDate desc")
    List<ZkbContractCat> queryByContractId(String contractId);

    @Query("select c from ZkbContractCat c where c.contractId =?1 order by c.createdDate desc")
    List<ZkbContractCat> queryAllByContractId(String contractId);
}
