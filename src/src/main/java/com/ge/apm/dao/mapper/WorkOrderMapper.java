package com.ge.apm.dao.mapper;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.WorkOrderEventRecordDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface WorkOrderMapper {
	@Select("select count(wo.id) from asset_info ai left join work_order wo on ai.id= wo.asset_id "+
				  "where date(wo.request_time)=date(#{day}) and ai.id=#{assetId}")
	public Integer fetchWorkOrdersByAssetId(@Param("assetId") Integer assetId,@Param("day") Date day);

	@Select("select sum(total_price) from work_order wo where asset_id=#{assetId} and wo.status = 2 and "+
					"date(wo.request_time)=date(#{day}) group by date(#{day})")
	public Double fetchWorkOrderCost(@Param("assetId") Integer assetId,@Param("day") Date day);
	
	@Select("select  id,site_id,hospital_id,asset_id,request_time,requestor_id,asset_name,current_person_id,current_step_id,current_person_name,parent_wo_id,case_priority from work_order where status = 1 ")
	public List<WorkOrder> fetchUnFinishedWorkList();
	
	@Select("select  owner_id current_person_id,owner_name ,start_time,work_order_id,step_id current_step_id,step_name from work_order_step"+
				" where step_id <= #{currentStepId} and work_order_id = #{id} order by start_time desc ")
	public List<WorkFlow> fetchWorkFlowList(WorkOrder workOrder);

	@Select("select * from workflow_config where site_id =#{siteId} and hospital_id =#{hospitalId}")
	public WorkflowConfig fetchWorkflowConfig(@Param("siteId") Integer siteId,@Param("hospitalId") Integer hospitalId);
	
	@Select("select id ,request_time,current_person_id from work_order  where id = #{parentWoId}")
	public WorkOrder fetchParentWorkOrder(Integer parentWoId);

//	@Select("SELECT sr.case_priority casePriority, sr.request_time requestTime, sr.request_reason requestReason, sr.requestor_name requestorName, wo.dispatch_mode dispatchMode, wo.approval_date approvalDate, wo.current_step_id currentStepId, wo.current_step_name currentStepName, wo.current_person_name currentPersonName FROM v2_service_request sr, v2_work_order wo WHERE sr.id = wo.sr_id ${ew.sqlSegment}")
	@Select("SELECT wo.*, sr.case_priority casePriority, sr.request_time requestTime, sr.request_reason requestReason, sr.requestor_name requestorName FROM v2_service_request sr " +
			"LEFT JOIN ( SELECT wo.id, wo.asset_id, wo.asset_uid, wo.sr_id, wo.dispatch_mode dispatchMode, wo.approval_date approvalDate, wo.current_step_id currentStepId, wo.current_step_name currentStepName, wo.current_person_name currentPersonName, count(wod.id) detailCount " +
			"FROM v2_work_order wo LEFT JOIN v2_work_order_detail wod ON wo.id = wod.wo_id WHERE wo.asset_uid in (${assetUIds}) group by wo.id ) wo ON sr.id = wo.sr_id " +
			"WHERE 1=1 ${ew.sqlSegment}")
	List<WorkOrderEventRecordDto> getEventRecord(Pagination pagination, @Param("ew") Wrapper wrapper, @Param("assetUIds") String assetUIds);

}
