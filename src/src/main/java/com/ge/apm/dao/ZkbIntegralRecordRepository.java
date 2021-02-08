package com.ge.apm.dao;

import com.ge.apm.domain.ZkbIntegralRecord;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author : 503189362
 * @date : 2021/2/2
 * @description :
 */
public interface ZkbIntegralRecordRepository extends GenericRepositoryUUID<ZkbIntegralRecord> {

    ZkbIntegralRecord findFirstByZkbIntegralIdAndRecordTypeAndKpiMonthOrderByCreatedDateDesc(String zkbIntegralId,String recordType,String kpiMonth);

    List<ZkbIntegralRecord> findByZkbIntegralIdOrderByCreatedDateDesc(String zkbIntegralId);
}
