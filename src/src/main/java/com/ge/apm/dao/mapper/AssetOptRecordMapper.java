package com.ge.apm.dao.mapper;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.dao.mapper.provider.AssetOptRecordProvider;
import com.ge.apm.domain.AssetOptRecord;

import webapp.framework.dao.SearchFilter;

public interface AssetOptRecordMapper extends BaseMapper<AssetOptRecord>{
	@SelectProvider(type = AssetOptRecordProvider.class,method = "getAll")
	List<AssetOptRecord> getAll(@Param("filters") Set<SearchFilter> filters,@Param("sortField")String sortField,@Param("isAscending") boolean isAscending,@Param("limit")int limit,@Param("offset")int offset);

	@SelectProvider(type = AssetOptRecordProvider.class,method = "getAllCount")
	Integer getAllCount(@Param("filters") Set<SearchFilter> filters);

	@Select("SELECT aor.create_date createDate, aor.end_date endDate, ua1.name createByName, ua2.name modifyByName FROM asset_opt_record aor LEFT JOIN user_account ua1 ON aor.create_by = ua1.uid LEFT JOIN user_account ua2 ON aor.modify_by = ua2.uid WHERE 1=1 ${ew.sqlSegment} ")
	List<AssetOptRecord> getEventRecord(Pagination pagination, @Param("ew") Wrapper wrapper);

}
