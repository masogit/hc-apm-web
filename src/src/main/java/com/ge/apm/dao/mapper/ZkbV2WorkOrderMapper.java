package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.dao.mapper.provider.V2WorkOrderProvider;
import com.ge.apm.domain.V2_WorkOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

public interface ZkbV2WorkOrderMapper extends BaseMapper<V2_WorkOrder>{


	/**
	 * 根据时间统计
	 * @param date 时间
	 * @param dateFormat 时间格式
	 * @return
	 */
	@SelectProvider(type = V2WorkOrderProvider.class,method = "statisticsOrderCount")
	List<V2_WorkOrder> statisticsOrderCount(@Param("date") String date,@Param("dateFormat") String dateFormat);



}
