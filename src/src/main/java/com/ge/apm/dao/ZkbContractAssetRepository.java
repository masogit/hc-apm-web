package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContractAsset;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;
/**
 * @author : 503189362
 * @description :
 * @date : 2020/5/18 14:06
 */
public interface ZkbContractAssetRepository extends GenericRepositoryUUID<ZkbContractAsset> {

    List<ZkbContractAsset> findByContractId(String contractId);
    List<ZkbContractAsset> findByAssetId(String assetId);

    ZkbContractAsset findFirstByContractIdAndAssetId(String contractId, String assetId);

    long countByContractIdAndAssetType(String contractId,String assetType);

    @Query("select count(a.id) from  ZkbContractAsset a where a.contractId = ?1 and a.assetType = ?2 and a.applyNum > 0")
    long countByApplyNum(String contractId,String assetType);

    @Query(value = "select distinct a.contract_id from zkb_contract_asset a where a.asset_id=?1", nativeQuery = true)
    List<String> findDistinctContractId(String assetId);


    /**
     * 根据多个合同ID获取合同列表
     * @param contractIdList 合同ID集合
     * @return
     */
    List<ZkbContractAsset> findByContractIdIn(List<String> contractIdList);


    /**
     * 统计数量
     * @param tenantUID 租户tenantUID
     * @param kpiDate 生成KPI时间
     * @return
     */
    @Query(value = "SELECT zca.* from zkb_contract zkbc INNER JOIN zkb_contract_asset zca on zkbc.id = zca.contract_id where zkbc.tenant_uid = ?1 and NOT ((zkbc.end_date < ?2) OR (zkbc.start_date > ?3))", nativeQuery = true)
    List<ZkbContractAsset> findZkbContractAssetByTenantUID(String tenantUID,Date firstDate,Date kpiDate);

    /**
     * 统计数量
     * @param tenantUID 租户tenantUID
     * @param kpiDate 生成KPI时间
     * @return
     */
    @Query(value = "SELECT zca.* from zkb_contract zkbc INNER JOIN zkb_contract_asset zca on zkbc.id = zca.contract_id where zkbc.contract_type <> 'CCSB' and zkbc.tenant_uid = ?1 and NOT ((zkbc.end_date < ?2) OR (zkbc.start_date > ?3))", nativeQuery = true)
    List<ZkbContractAsset> findZkbContractAssetByTenantUIDOfCCSB(String tenantUID,Date firstDate,Date kpiDate);

    @Query(value = "SELECT zca.* from zkb_contract zkbc INNER JOIN zkb_contract_asset zca on zkbc.id = zca.contract_id where zkbc.tenant_uid = ?1 and zkbc.start_date<=?2 and zkbc.end_date>=?2", nativeQuery = true)
    List<ZkbContractAsset> findZkbContractAssetByTenantUID(String tenantUID, Date kpiDate);

    @Modifying
    @Transactional
    @Query(value = "UPDATE zkb_contract_asset SET apply_num=(select coalesce(apply_num,0)+1) WHERE contract_id=?1 and asset_id=?2 and asset_type=?3", nativeQuery = true)
    public void addContractAssetApplyNum(String contractId,String assetId,String assetType);

    @Modifying
    @Transactional
    @Query(value = "UPDATE zkb_contract_asset SET apply_num=(select coalesce(apply_num,0)-1) WHERE contract_id=?1 and asset_id=?2 and asset_type=?3", nativeQuery = true)
    public void subtractContractAssetApplyNum(String contractId,String assetId,String assetType);

    @Query(value = "SELECT zca.* from zkb_contract zkbc INNER JOIN zkb_contract_asset zca on zkbc.id = zca.contract_id where zkbc.tenant_uid = ?1 and zkbc.end_date =?2", nativeQuery = true)
    List<ZkbContractAsset> findByEndDateAsset(String tenantUID, Date endDate);

    @Query(value = "SELECT zca.* from zkb_contract zkbc INNER JOIN zkb_contract_asset zca on zkbc.id = zca.contract_id where zkbc.tenant_uid = ?1 and zkbc.start_date =?2", nativeQuery = true)
    List<ZkbContractAsset> findByStartDateAsset(String tenantUID, Date startDate);
}
