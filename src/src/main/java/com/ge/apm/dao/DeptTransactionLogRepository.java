package com.ge.apm.dao;

import com.ge.apm.domain.DataTableConfig;
import com.ge.apm.domain.DeptTransactionLog;
import com.ge.apm.view.transaction.TransactionStatis;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.mvc.JpaCRUDController;

import java.util.List;

public interface DeptTransactionLogRepository extends GenericRepository<DeptTransactionLog> {

    public List<DeptTransactionLog> getByTenantUID(String tenantUid);

    @Query(value="select dtl.* from dept_transaction_log dtl where  author_id =?1 and to_char(start_time,'yyyy-mm') =?2 ",nativeQuery = true)
    public List<DeptTransactionLog> getDefByAuthorandMonth(Integer id,String month);

    //科务的时间差
   /* @Query(value="select dtl.author_name,dtl.date,sum(round(date_part('epoch', end_time - start_time))) as dtl.total from dept_transaction_log dtl group by author_name,date having to_char(date,'yyyy-mm') ='2019-03' order by author_name,total desc ",nativeQuery = true)*/
    /*@Query(value="select new com.ge.apm.view.transaction.TransactionStatis(dtl.authorId,dtl.authorName,to_char(dtl.date,'yyyy-mm') as date,sum(dtl.hours) as hours) from DeptTransactionLog dtl group by dtl.authorId,dtl.authorName,to_char(date,'yyyy-mm') having to_char(date,'yyyy-mm') ='2019-03' order by dtl.authorName desc")
    public List<TransactionStatis> getDefByMonth();*/
  //  聚合出来的新表 List中应该是什么



    /*select author_name, cast(to_char(dtl.date,'yyyy')as integer) as year,cast(to_char(dtl.date,'mm')as integer) as month from dept_transaction_log dtl group by  author_name,to_char(dtl.date,'yyyy'), to_char(dtl.date,'mm') having cast(to_char(dtl.date,'mm')as integer) >=2 and cast(to_char(dtl.date,'mm')as integer) <=4*/
    @Query(value="select new com.ge.apm.view.transaction.TransactionStatis(dtl.clinicalDeptUID,dtl.authorId,dtl.authorName,cast(to_char(dtl.date,'yyyy')as integer) as year, cast(to_char(dtl.date,'mm')as integer) as month,sum(dtl.hours) as hours) from DeptTransactionLog dtl group by dtl.clinicalDeptUID,dtl.authorId,dtl.authorName,to_char(dtl.date,'yyyy'), to_char(dtl.date,'mm') having dtl.clinicalDeptUID in (?5) and cast(to_char(dtl.date,'yyyy')as integer) >=?1 and cast(to_char(dtl.date,'yyyy')as integer) <=?2 and cast(to_char(dtl.date,'mm')as integer) >=?3 and cast(to_char(dtl.date,'mm')as integer) <=?4  order by dtl.authorName desc")
    public List<TransactionStatis> getDefByMonthFromTo(Integer startYear,Integer endYear,Integer startMonth,Integer endMonth,List<String> scope);

    /*@Query("select new com.ge.apm.domain.TubeData(max(td.id) as id,"
            + "td.assetUid,td.seriaNum,max(td.patientExam),max(td.tubeMAS),max(td.tubeScan),max(td.inputDate) as inputDate) "
            + "from TubeData td where td.tenantUID = ?1 and td.assetUid in ?2 and seriaNum like concat('%',?3,'%') "
            + "group by td.assetUid,td.seriaNum order by inputDate desc")
    Page<TubeData> findByConditionWithUidsAndSeriaNum(String tenantUid, List<String> queryUids, String queryCondition,Pageable pageable);*/

}
