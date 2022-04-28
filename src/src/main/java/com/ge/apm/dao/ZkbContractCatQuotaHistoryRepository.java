package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContractCatQuotaHistory;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @autor by lyj 503277717
 * @date 2022/4/6
*/

public interface ZkbContractCatQuotaHistoryRepository extends GenericRepositoryUUID<ZkbContractCatQuotaHistory> {

    //TODO  TODO liyonjian ZKB - 2022年新探头逻辑   CHECK
    @Query(value = "SELECT * FROM zkb_contract_cat_quota_history where contract_id =?1 and status =?2 ", nativeQuery = true)
    List<ZkbContractCatQuotaHistory> queryZkbContractCatQuotaHistory(String contractId, String status);


    List<ZkbContractCatQuotaHistory> queryByPartOrderId(String partOrderId);

    @Query("select h from ZkbContractCatQuotaHistory h where h.status = 'Y' and h.contractId = ?1 and h.probeType = ?2 and h.assetType = ?3")
    List<ZkbContractCatQuotaHistory> queryByCondition(String contractId, String probeType, String assetType);

}
