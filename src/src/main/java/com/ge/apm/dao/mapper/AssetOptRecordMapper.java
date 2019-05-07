package com.ge.apm.dao.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.dao.mapper.provider.AssetOptRecordProvider;
import com.ge.apm.domain.AssetOptRecord;

import webapp.framework.dao.SearchFilter;

public interface AssetOptRecordMapper extends BaseMapper<AssetOptRecord>{
	@SelectProvider(type = AssetOptRecordProvider.class,method = "getAll")
	public List<AssetOptRecord> getAll(@Param("filters") Set<SearchFilter> filters,@Param("sortField")String sortField,@Param("isAscending") boolean isAscending,@Param("limit")int limit,@Param("offset")int offset);

	@SelectProvider(type = AssetOptRecordProvider.class,method = "getAllCount")
	public Integer getAllCount(@Param("filters") Set<SearchFilter> filters);
	
//	@Update("update v2_work_order_detail set mvs_price=#{mvsPrice},invoice_num=#{invoiceNum},purchase_time=#{purchaseTime},deliver_time=#{deliverTime},arrived_time=#{arrivedTime} where id=#{id}")
//	public Integer updateObj(AssetOptRecord AssetOptRecord);

}
