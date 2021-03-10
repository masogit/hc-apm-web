package com.ge.apm.dao;

import com.ge.apm.domain.SqlExportQueue;
import com.ge.apm.domain.Supplier;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

/**
 * @author zhaokai
 * @create 2021-02-02-17:10
 */
public interface SqlExportQueueRepostitory extends GenericRepository<SqlExportQueue> {

    @Query(value= "select count(*) from sql_export_queue where execute_statement = ?1" ,nativeQuery = true)
    int findSqlExportQueueByExecuteStatement(String executeStatement);
}
