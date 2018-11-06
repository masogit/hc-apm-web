package com.ge.apm.dao.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.mapper.provider.V2WorkOrderDetailProvider;
import com.ge.apm.domain.MVSAssetInfoOverview;

import webapp.framework.dao.SearchFilter;

public interface V2WorkOrderDetailMapper extends BaseMapper<MVSAssetInfoOverview>{
	@SelectProvider(type = V2WorkOrderDetailProvider.class,method = "getAll")
	public List<MVSAssetInfoOverview> getAll(@Param("filters") Set<SearchFilter> filters,@Param("sortField")String sortField,@Param("isAscending") boolean isAscending,@Param("limit")int limit,@Param("offset")int offset);

	@SelectProvider(type = V2WorkOrderDetailProvider.class,method = "getAllCount")
	public Integer getAllCount(@Param("filters") Set<SearchFilter> filters);

}
