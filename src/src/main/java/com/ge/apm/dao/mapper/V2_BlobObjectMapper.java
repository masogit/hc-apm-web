package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.mapper.provider.V2_BlobObjectProvider;
import com.ge.apm.domain.V2_BlobObject;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import webapp.framework.dao.SearchFilter;

import java.util.List;

/**
 * Created by 503202755 on 2020/9/2
 */
public interface V2_BlobObjectMapper {

    @SelectProvider(type = V2_BlobObjectProvider.class,method="loadV2_BlobObject")
    List<V2_BlobObject> loadV2_BlobObject(@Param("filters") List<SearchFilter> filters,@Param("tenantUid") String tenantUid,@Param("boType") String boType);

    List<V2_BlobObject> selectAssetBlob(Pagination pagination, @Param("ew") Wrapper<V2_BlobObject> wrapper);
}
