package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.mapper.provider.AssetOperationRecordProvider;
import com.ge.apm.domain.AssetOperationRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.Set;

/**
 * @author Liu
 */
public interface AssetOperationRecordMapper extends BaseMapper<AssetOperationRecord> {
    @SelectProvider(type = AssetOperationRecordProvider.class, method = "getAll")
    List<AssetOperationRecord> getAll(@Param("filters") Set<SearchFilter> filters, @Param("sortField") String sortField, @Param("isAscending") boolean isAscending,
                                             @Param("limit") int limit, @Param("offset") int offset, @Param("assetFlag") boolean assetFlag, @Param("scrapStatus") boolean scrapStatus);

    @SelectProvider(type = AssetOperationRecordProvider.class, method = "getAllCount")
    Integer getAllCount(@Param("filters") Set<SearchFilter> filters, @Param("assetFlag") boolean assetFlag,@Param("scrapStatus") boolean scrapStatus);

    @Select("SELECT hospital_number hospitalNumber, operation_name operationName, operation_by_name operationByName, create_date createDate FROM asset_operation_record WHERE 1=1 ${ew.sqlSegment} ")
    List<AssetOperationRecord> getEventRecord(Pagination pagination, @Param("ew") Wrapper wrapper);

}
