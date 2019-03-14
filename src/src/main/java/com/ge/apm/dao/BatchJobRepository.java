/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.BatchJob;

import webapp.framework.dao.GenericRepositoryUUID;

/**
 *
 * @author 212579464
 */
public interface BatchJobRepository extends GenericRepositoryUUID<BatchJob> {

    @Query(value="select o.* from batch_job o where o.status=0 order by o.create_time limit 1",nativeQuery=true)
    List<BatchJob> findNewBatchJob();
    
    @Query(value="select o.* from batch_job o where o.status=0 and o.name=?1 order by o.create_time limit 1",nativeQuery=true)
    List<BatchJob> findNewBatchJobWithName(String name);
    
    @Query(value="select o.* from batch_job o where o.status=0 and o.sub_name=?1 and o.content=?2 and o.site_uid=?3 order by o.create_time limit 1",nativeQuery=true)
    List<BatchJob> findNewBatchJobWithContentSite(String action,String content,String siteUid);
    
    @Query(value="select o.* from batch_job o where o.status=0 and o.site_uid=?1 order by o.create_time limit 1",nativeQuery=true)
    List<BatchJob> findNewBatchJobWithSite(String siteUid);

    BatchJob findById(String id);

    @Query(value = "select * from batch_job o where o.create_time < ?1 limit 100", nativeQuery = true)
    List<BatchJob> findOldBatchJob(Date beforeDate);
    
    @Modifying
    @Query(value = "update batch_job set logs=logs||?2 where id=?1", nativeQuery = true)
    void appendLog(String id,String log);

}
