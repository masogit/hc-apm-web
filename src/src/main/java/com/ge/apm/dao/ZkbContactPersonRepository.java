package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContactPerson;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author 212605082 on 2019/12/16.
 */
public interface ZkbContactPersonRepository extends GenericRepositoryUUID<ZkbContactPerson> {
    List<ZkbContactPerson> findByPositionAndTenantUID(String position, String tenantUID);
}
