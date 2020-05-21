package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContactPerson;
import com.ge.apm.domain.ZkbContract;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author 212605082 on 2019/12/16.
 */
public interface ZkbContractRepository extends GenericRepositoryUUID<ZkbContract> {

    ZkbContract findFirstByContractNo(String contractNo);
}
