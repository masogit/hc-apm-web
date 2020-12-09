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

    /**
     * 根据租户UID获取所有合同列表
     * @param tenantUID 租户UID
     * @return 合同列表
     */
    List<ZkbContract> findByTenantUID(String tenantUID);

    /**
     *根据多个租户UID获取合同列表信息
     * @param tenantUIDList 多个租户UID
     * @return 合同列表
     */
    List<ZkbContract> findByTenantUIDIn(List<String> tenantUIDList);

    /**
     * 统计数量
     * @param contractIdList 合同ID
     * @return
     */
    @Query(value = "SELECT sum(ib_number)  from zkb_contract  where id in ?1", nativeQuery = true)
    Integer fetchIbNumber(List<String> contractIdList);
}
