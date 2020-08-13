package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContract;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

/**
 * @author 212605082 on 2019/12/16.
 */
public interface ZkbContractRepository extends GenericRepositoryUUID<ZkbContract> {

    ZkbContract findFirstByContractNo(String contractNo);

    /**
     *
     *
     */
    List<ZkbContract> findByTenantUIDAndContractNo(String tenantUID, String contractNo);

    @Query("select c from ZkbContract c where c.tenantUID =?1 and c.startDate <= ?2 and " +
            "c.endDate >= ?2 and c.id in (select a.contractId from ZkbContractAsset a where a.assetId = ?3) " +
            " and c.spareQuota > 1 ")
    List<ZkbContract> findByAsset(String tenantUID, Date date, String assetId);
}
