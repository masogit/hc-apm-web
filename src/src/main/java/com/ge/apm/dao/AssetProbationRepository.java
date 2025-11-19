package com.ge.apm.dao;

import com.ge.apm.domain.AssetProbation;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

public interface AssetProbationRepository extends GenericRepositoryUUID<AssetProbation> {
    @Query(value = "select right(max(order_num), 4) from asset_probation where tenant_uid = ?1 and to_char(created_date, 'yyyy-MM-dd') = to_char(now(), 'yyyy-MM-dd')",nativeQuery = true)
    String queryOrderNum(String tenantUid);
}
