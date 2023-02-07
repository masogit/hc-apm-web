package com.ge.apm.dao;

import com.ge.apm.domain.ZkbIntegralRecord;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author : 503189362
 * @date : 2021/2/2
 * @description :
 */
public interface ZkbIntegralRecordRepository extends GenericRepositoryUUID<ZkbIntegralRecord> {

    @Query(value = "select * from zkb_integral_record r where  r.zkb_integral_id = ?1 and r.record_type in ?2 and r.kpi_month = ?3 order by r.created_date desc limit 1  ", nativeQuery = true)
    ZkbIntegralRecord findFirstByRecord(String zkbIntegralId,List<String> recordType,String kpiMonth);

    List<ZkbIntegralRecord> findByZkbIntegralIdAndRecordTypeNotOrderByCreatedDateDesc(String zkbIntegralId,String recordType);

    List<ZkbIntegralRecord> findByZkbIntegralIdOrderByCreatedDateDesc(String zkbIntegralId);

}
