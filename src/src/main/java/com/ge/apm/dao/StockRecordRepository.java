package com.ge.apm.dao;

import com.ge.apm.domain.StockRecord;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

public interface StockRecordRepository extends GenericRepositoryUUID<StockRecord>{

    @Query(value = "select * from table_stock_record where spare_part_id = ?1 and record_type = ?2 limit 1", nativeQuery = true)
    StockRecord findInBySparePartId(String sparePartId, Integer recordType);

}
