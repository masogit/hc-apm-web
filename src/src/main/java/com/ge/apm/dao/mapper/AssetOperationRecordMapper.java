package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.dao.mapper.provider.AssetOperationRecordProvider;
import com.ge.apm.dao.mapper.provider.AssetOptRecordProvider;
import com.ge.apm.domain.AssetOperationRecord;
import com.ge.apm.domain.AssetOptRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.Set;

public interface AssetOperationRecordMapper extends BaseMapper<AssetOperationRecord>{
	@SelectProvider(type = AssetOperationRecordProvider.class,method = "getAll")
	public List<AssetOperationRecord> getAll(@Param("filters") Set<SearchFilter> filters,@Param("sortField")String sortField,@Param("isAscending") boolean isAscending,@Param("limit")int limit,@Param("offset")int offset);

	@SelectProvider(type = AssetOperationRecordProvider.class,method = "getAllCount")
	public Integer getAllCount(@Param("filters") Set<SearchFilter> filters);
	
//	@Update("update v2_work_order_detail set mvs_price=#{mvsPrice},invoice_num=#{invoiceNum},purchase_time=#{purchaseTime},deliver_time=#{deliverTime},arrived_time=#{arrivedTime} where id=#{id}")
//	public Integer updateObj(AssetOptRecord AssetOptRecord);

}
