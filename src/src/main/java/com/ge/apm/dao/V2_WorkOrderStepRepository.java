package com.ge.apm.dao;

import com.ge.apm.domain.V2_WorkOrder_Step;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface V2_WorkOrderStepRepository extends GenericRepositoryUUID<V2_WorkOrder_Step> {

    @Query("select s from V2_WorkOrder_Step s where s.woId=?1 order by s.startTime desc")
    public List<V2_WorkOrder_Step> findByWorkOrder(String woId);

    public V2_WorkOrder_Step findFirstByWoIdAndStepIdOrderByStartTimeDesc(String woId, Integer stepId);

    public V2_WorkOrder_Step findFirstByWoIdAndStepIdOrderByStartTimeAsc(String woId, Integer stepId);

    @Query("select s from V2_WorkOrder_Step s where s.woId=?1 and s.stepId=?2")
    public List<V2_WorkOrder_Step> findResponsor(String woId,Integer stepId);

    @Query("select s from V2_WorkOrder_Step s where s.woId=?1 and s.stepId=?2")
    public List<V2_WorkOrder_Step> findRepaireName(String woId,Integer stepId);

    @Query("select s from V2_WorkOrder_Step s where s.ownerName like  concat('%',?1,'%') and s.stepId = 4")
//    @Query("select s.woId from V2_WorkOrder_Step s where s.ownerName like  concat('%',?1,'%')")
    public List<V2_WorkOrder_Step> findWorkIds(String owerName);

    @Query("select s.ownerName from V2_WorkOrder_Step s where s.ownerName like  concat('%',?1,'%') and s.stepId = 4")
    public List<String> findOwerNames(String owerName);

    @Query("select s from V2_WorkOrder_Step s where s.stepId = 4 and s.ownerName like concat('%',?1,'%')")
    List<V2_WorkOrder_Step> findWorkSteps(String ownerName);

    @Query("select s from V2_WorkOrder_Step s where s.ownerName like  concat('%',?1,'%')")
    public List<V2_WorkOrder_Step> getWorkOrderDetailByownerName(String ownerName);
    @Modifying
    @Transactional
    @Query(value = "delete from v2_work_order_step where id in ?1",nativeQuery = true)
    Integer deleteById(List<String> collect);

//    public List<V2_WorkOrder_Step> findByWoIdAndStepIdOrderByCreatedDateDesc(String woId, int stepId);
}