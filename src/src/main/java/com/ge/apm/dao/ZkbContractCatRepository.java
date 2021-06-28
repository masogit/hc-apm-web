package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContractCat;
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
}
