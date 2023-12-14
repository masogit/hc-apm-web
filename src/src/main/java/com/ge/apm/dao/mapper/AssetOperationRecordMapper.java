package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.dao.mapper.provider.AssetOperationRecordProvider;
import com.ge.apm.domain.AssetOperationRecord;
import org.apache.ibatis.annotations.Param;
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
                                             @Param("limit") int limit, @Param("offset") int offset, @Param("assetFlag") boolean assetFlag);

    @SelectProvider(type = AssetOperationRecordProvider.class, method = "getAllCount")
    Integer getAllCount(@Param("filters") Set<SearchFilter> filters, @Param("assetFlag") boolean assetFlag);

}
