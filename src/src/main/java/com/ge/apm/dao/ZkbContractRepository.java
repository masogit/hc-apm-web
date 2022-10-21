package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContract;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
     * @param tenantUID 租户tenantUID
     * @param firstDate 第一天
     * @param kpiDate 生成KPI时间
     * @return
     */
    @Query(value = "SELECT sum(ib_number)  from zkb_contract  where tenant_uid = ?1 and NOT ((end_date < ?2) OR (start_date > ?3)) ", nativeQuery = true)
    Integer fetchIbNumber(String tenantUID,Date firstDate,Date kpiDate);

    /**
     * 统计数量
     * @param tenantUID 租户tenantUID
     * @param kpiDate 生成KPI时间
     * @return
     */
    @Query(value = "SELECT coalesce(count(distinct ai.system_id),0) from zkb_contract zkbc INNER JOIN zkb_contract_asset zca on zkbc.id = zca.contract_id INNER JOIN asset_info ai on zca.asset_id = ai.uid where zkbc.tenant_uid = ?1 and NOT ((zkbc.end_date < ?2) OR (zkbc.start_date > ?3))", nativeQuery = true)
    Integer fetchIbNumberNew(String tenantUID, Date firstDate, Date kpiDate);




    /**
     * 统计数量
     * @param tenantUID 租户tenantUID
     * @param firstDate 第一天
     * @param kpiDate 生成KPI时间
     * @return
     */
    @Query(value = "SELECT coalesce(count(distinct ai.system_id),0)  from zkb_contract zkbc INNER JOIN zkb_contract_asset zca on zkbc.id = zca.contract_id INNER JOIN asset_info ai on zca.asset_id = ai.uid where zkbc.contract_type <> 'CCSB' and zkbc.tenant_uid = ?1 and NOT ((end_date < ?2) OR (start_date > ?3)) ", nativeQuery = true)
    Integer fetchIbNumberOfNewCCSB(String tenantUID,Date firstDate,Date kpiDate);
    /**
     * 统计高端机数量
     * @param tenantUID 租户tenantUID
     * @param kpiDate 生成KPI时间
     * @return
     */
    @Query(value = "SELECT count(ac.id)  from zkb_contract ac  INNER JOIN zkb_contract_asset zca on zca.contract_id = ac.ID and zca.asset_type='highMachine' where tenant_uid = ?1 and  NOT ((end_date < ?2) OR (start_date > ?3))", nativeQuery = true)
    Integer fetchHighMachineCount(String tenantUID,Date firstDate,Date kpiDate);

    /**
     * 统计高端机数量
     * @param tenantUID 租户tenantUID
     * @param kpiDate 生成KPI时间
     * @return
     */
    @Query(value = "SELECT coalesce(count(ac.id),0)  from zkb_contract ac  INNER JOIN zkb_contract_asset zca on zca.contract_id = ac.ID and zca.asset_type='highMachine' where ac.contract_type <> 'CCSB' and tenant_uid = ?1 and  NOT ((end_date < ?2) OR (start_date > ?3))", nativeQuery = true)
    Integer fetchHighMachineCountOfCCSB(String tenantUID,Date firstDate,Date kpiDate);
    /**
     * 根据ID批量获取
     * @param contractIdSet 主键集合
     * @return
     */
    List<ZkbContract> findByIdIn(Set<String> contractIdSet);

    @Query(value = "select distinct c.tenant_uid from zkb_contract c where c.start_date <?2 and c.end_date >= ?1 and c.last_modified_date >= ?1 and c.last_modified_date < ?2", nativeQuery = true)
    List<String> queryByLastModifiedDate(Date d1,Date d2);

    @Query(value = "select distinct c.tenant_uid from zkb_contract c where c.end_date = ?1 ", nativeQuery = true)
    List<String> queryByEndDate(Date d1);

    @Query(value = "select distinct c.tenant_uid from zkb_contract c where c.start_date = ?1 ", nativeQuery = true)
    List<String> queryByStartDate(Date d1);
}
