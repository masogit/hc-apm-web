package com.ge.apm.dao;

import com.ge.apm.domain.StockRecord;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface StockRecordRepository extends GenericRepositoryUUID<StockRecord>{

    @Query(value = "select * from table_stock_record where spare_part_id = ?1 and record_type = ?2 limit 1", nativeQuery = true)
    StockRecord findInBySparePartId(String sparePartId, Integer recordType);

    @Query(value = "select * from table_stock_record where inbound_number = ?1 and record_type = 3", nativeQuery = true)
    List<StockRecord> findListByInboundNumber(String inboundNumber);

    @Query(value = "select * from table_stock_record where outbound_number = ?1 and record_type = 4", nativeQuery = true)
    List<StockRecord> findListByOutboundNumber(String outboundNumber);

    @Query(value = "select * from table_stock_record where record_type = ?1 ", nativeQuery = true)
    List<StockRecord> findByType(Integer recordType);
}
